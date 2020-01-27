// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.access;

import java.util.Arrays;
import org.springframework.core.CollectionFactory;
import java.lang.reflect.Array;
import javax.management.openmbean.TabularData;
import org.springframework.core.GenericCollectionTypeResolver;
import java.util.Collection;
import javax.management.openmbean.CompositeData;
import javax.management.Attribute;
import java.beans.PropertyDescriptor;
import javax.management.JMException;
import javax.management.OperationsException;
import org.springframework.util.ReflectionUtils;
import javax.management.RuntimeOperationsException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.MBeanException;
import org.springframework.core.MethodParameter;
import org.springframework.beans.BeanUtils;
import org.aopalliance.intercept.MethodInvocation;
import javax.management.MBeanInfo;
import java.io.IOException;
import javax.management.ReflectionException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.JMX;
import org.springframework.jmx.support.JmxUtils;
import javax.management.MalformedObjectNameException;
import org.springframework.jmx.support.ObjectNameManager;
import java.net.MalformedURLException;
import java.util.HashMap;
import org.springframework.util.ClassUtils;
import org.apache.commons.logging.LogFactory;
import java.lang.reflect.Method;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import java.util.Map;
import javax.management.remote.JMXServiceURL;
import javax.management.MBeanServerConnection;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.aopalliance.intercept.MethodInterceptor;

public class MBeanClientInterceptor implements MethodInterceptor, BeanClassLoaderAware, InitializingBean, DisposableBean
{
    protected final Log logger;
    private MBeanServerConnection server;
    private JMXServiceURL serviceUrl;
    private Map<String, ?> environment;
    private String agentId;
    private boolean connectOnStartup;
    private boolean refreshOnConnectFailure;
    private ObjectName objectName;
    private boolean useStrictCasing;
    private Class<?> managementInterface;
    private ClassLoader beanClassLoader;
    private final ConnectorDelegate connector;
    private MBeanServerConnection serverToUse;
    private MBeanServerInvocationHandler invocationHandler;
    private Map<String, MBeanAttributeInfo> allowedAttributes;
    private Map<MethodCacheKey, MBeanOperationInfo> allowedOperations;
    private final Map<Method, String[]> signatureCache;
    private final Object preparationMonitor;
    
    public MBeanClientInterceptor() {
        this.logger = LogFactory.getLog(this.getClass());
        this.connectOnStartup = true;
        this.refreshOnConnectFailure = false;
        this.useStrictCasing = true;
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        this.connector = new ConnectorDelegate();
        this.signatureCache = new HashMap<Method, String[]>();
        this.preparationMonitor = new Object();
    }
    
    public void setServer(final MBeanServerConnection server) {
        this.server = server;
    }
    
    public void setServiceUrl(final String url) throws MalformedURLException {
        this.serviceUrl = new JMXServiceURL(url);
    }
    
    public void setEnvironment(final Map<String, ?> environment) {
        this.environment = environment;
    }
    
    public Map<String, ?> getEnvironment() {
        return this.environment;
    }
    
    public void setAgentId(final String agentId) {
        this.agentId = agentId;
    }
    
    public void setConnectOnStartup(final boolean connectOnStartup) {
        this.connectOnStartup = connectOnStartup;
    }
    
    public void setRefreshOnConnectFailure(final boolean refreshOnConnectFailure) {
        this.refreshOnConnectFailure = refreshOnConnectFailure;
    }
    
    public void setObjectName(final Object objectName) throws MalformedObjectNameException {
        this.objectName = ObjectNameManager.getInstance(objectName);
    }
    
    public void setUseStrictCasing(final boolean useStrictCasing) {
        this.useStrictCasing = useStrictCasing;
    }
    
    public void setManagementInterface(final Class<?> managementInterface) {
        this.managementInterface = managementInterface;
    }
    
    protected final Class<?> getManagementInterface() {
        return this.managementInterface;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.server != null && this.refreshOnConnectFailure) {
            throw new IllegalArgumentException("'refreshOnConnectFailure' does not work when setting a 'server' reference. Prefer 'serviceUrl' etc instead.");
        }
        if (this.connectOnStartup) {
            this.prepare();
        }
    }
    
    public void prepare() {
        synchronized (this.preparationMonitor) {
            if (this.server != null) {
                this.serverToUse = this.server;
            }
            else {
                this.serverToUse = null;
                this.serverToUse = this.connector.connect(this.serviceUrl, this.environment, this.agentId);
            }
            this.invocationHandler = null;
            if (this.useStrictCasing) {
                if (JmxUtils.isMXBeanSupportAvailable()) {
                    this.invocationHandler = new MBeanServerInvocationHandler(this.serverToUse, this.objectName, this.managementInterface != null && JMX.isMXBeanInterface(this.managementInterface));
                }
                else {
                    this.invocationHandler = new MBeanServerInvocationHandler(this.serverToUse, this.objectName);
                }
            }
            else {
                this.retrieveMBeanInfo();
            }
        }
    }
    
    private void retrieveMBeanInfo() throws MBeanInfoRetrievalException {
        try {
            final MBeanInfo info = this.serverToUse.getMBeanInfo(this.objectName);
            final MBeanAttributeInfo[] attributeInfo = info.getAttributes();
            this.allowedAttributes = new HashMap<String, MBeanAttributeInfo>(attributeInfo.length);
            for (final MBeanAttributeInfo infoEle : attributeInfo) {
                this.allowedAttributes.put(infoEle.getName(), infoEle);
            }
            final MBeanOperationInfo[] operationInfo = info.getOperations();
            this.allowedOperations = new HashMap<MethodCacheKey, MBeanOperationInfo>(operationInfo.length);
            for (final MBeanOperationInfo infoEle2 : operationInfo) {
                final Class<?>[] paramTypes = JmxUtils.parameterInfoToTypes(infoEle2.getSignature(), this.beanClassLoader);
                this.allowedOperations.put(new MethodCacheKey(infoEle2.getName(), paramTypes), infoEle2);
            }
        }
        catch (ClassNotFoundException ex) {
            throw new MBeanInfoRetrievalException("Unable to locate class specified in method signature", ex);
        }
        catch (IntrospectionException ex2) {
            throw new MBeanInfoRetrievalException("Unable to obtain MBean info for bean [" + this.objectName + "]", ex2);
        }
        catch (InstanceNotFoundException ex3) {
            throw new MBeanInfoRetrievalException("Unable to obtain MBean info for bean [" + this.objectName + "]: it is likely that this bean was unregistered during the proxy creation process", ex3);
        }
        catch (ReflectionException ex4) {
            throw new MBeanInfoRetrievalException("Unable to read MBean info for bean [ " + this.objectName + "]", ex4);
        }
        catch (IOException ex5) {
            throw new MBeanInfoRetrievalException("An IOException occurred when communicating with the MBeanServer. It is likely that you are communicating with a remote MBeanServer. Check the inner exception for exact details.", ex5);
        }
    }
    
    protected boolean isPrepared() {
        synchronized (this.preparationMonitor) {
            return this.serverToUse != null;
        }
    }
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        synchronized (this.preparationMonitor) {
            if (!this.isPrepared()) {
                this.prepare();
            }
        }
        try {
            return this.doInvoke(invocation);
        }
        catch (MBeanConnectFailureException ex) {
            return this.handleConnectFailure(invocation, ex);
        }
        catch (IOException ex2) {
            return this.handleConnectFailure(invocation, ex2);
        }
    }
    
    protected Object handleConnectFailure(final MethodInvocation invocation, final Exception ex) throws Throwable {
        if (this.refreshOnConnectFailure) {
            final String msg = "Could not connect to JMX server - retrying";
            if (this.logger.isDebugEnabled()) {
                this.logger.warn(msg, ex);
            }
            else if (this.logger.isWarnEnabled()) {
                this.logger.warn(msg);
            }
            this.prepare();
            return this.doInvoke(invocation);
        }
        throw ex;
    }
    
    protected Object doInvoke(final MethodInvocation invocation) throws Throwable {
        final Method method = invocation.getMethod();
        try {
            Object result = null;
            if (this.invocationHandler != null) {
                result = this.invocationHandler.invoke(invocation.getThis(), method, invocation.getArguments());
            }
            else {
                final PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
                if (pd != null) {
                    result = this.invokeAttribute(pd, invocation);
                }
                else {
                    result = this.invokeOperation(method, invocation.getArguments());
                }
            }
            return this.convertResultValueIfNecessary(result, new MethodParameter(method, -1));
        }
        catch (MBeanException ex) {
            throw ex.getTargetException();
        }
        catch (RuntimeMBeanException ex2) {
            throw ex2.getTargetException();
        }
        catch (RuntimeErrorException ex3) {
            throw ex3.getTargetError();
        }
        catch (RuntimeOperationsException ex4) {
            final RuntimeException rex = ex4.getTargetException();
            if (rex instanceof RuntimeMBeanException) {
                throw ((RuntimeMBeanException)rex).getTargetException();
            }
            if (rex instanceof RuntimeErrorException) {
                throw ((RuntimeErrorException)rex).getTargetError();
            }
            throw rex;
        }
        catch (OperationsException ex5) {
            if (ReflectionUtils.declaresException(method, ex5.getClass())) {
                throw ex5;
            }
            throw new InvalidInvocationException(ex5.getMessage());
        }
        catch (JMException ex6) {
            if (ReflectionUtils.declaresException(method, ex6.getClass())) {
                throw ex6;
            }
            throw new InvocationFailureException("JMX access failed", ex6);
        }
        catch (IOException ex7) {
            if (ReflectionUtils.declaresException(method, ex7.getClass())) {
                throw ex7;
            }
            throw new MBeanConnectFailureException("I/O failure during JMX access", ex7);
        }
    }
    
    private Object invokeAttribute(final PropertyDescriptor pd, final MethodInvocation invocation) throws JMException, IOException {
        final String attributeName = JmxUtils.getAttributeName(pd, this.useStrictCasing);
        final MBeanAttributeInfo inf = this.allowedAttributes.get(attributeName);
        if (inf == null) {
            throw new InvalidInvocationException("Attribute '" + pd.getName() + "' is not exposed on the management interface");
        }
        if (invocation.getMethod().equals(pd.getReadMethod())) {
            if (inf.isReadable()) {
                return this.serverToUse.getAttribute(this.objectName, attributeName);
            }
            throw new InvalidInvocationException("Attribute '" + attributeName + "' is not readable");
        }
        else {
            if (!invocation.getMethod().equals(pd.getWriteMethod())) {
                throw new IllegalStateException("Method [" + invocation.getMethod() + "] is neither a bean property getter nor a setter");
            }
            if (inf.isWritable()) {
                this.serverToUse.setAttribute(this.objectName, new Attribute(attributeName, invocation.getArguments()[0]));
                return null;
            }
            throw new InvalidInvocationException("Attribute '" + attributeName + "' is not writable");
        }
    }
    
    private Object invokeOperation(final Method method, final Object[] args) throws JMException, IOException {
        final MethodCacheKey key = new MethodCacheKey(method.getName(), method.getParameterTypes());
        final MBeanOperationInfo info = this.allowedOperations.get(key);
        if (info == null) {
            throw new InvalidInvocationException("Operation '" + method.getName() + "' is not exposed on the management interface");
        }
        String[] signature = null;
        synchronized (this.signatureCache) {
            signature = this.signatureCache.get(method);
            if (signature == null) {
                signature = JmxUtils.getMethodSignature(method);
                this.signatureCache.put(method, signature);
            }
        }
        return this.serverToUse.invoke(this.objectName, method.getName(), args, signature);
    }
    
    protected Object convertResultValueIfNecessary(final Object result, final MethodParameter parameter) {
        final Class<?> targetClass = parameter.getParameterType();
        try {
            if (result == null) {
                return null;
            }
            if (ClassUtils.isAssignableValue(targetClass, result)) {
                return result;
            }
            if (result instanceof CompositeData) {
                final Method fromMethod = targetClass.getMethod("from", CompositeData.class);
                return ReflectionUtils.invokeMethod(fromMethod, null, result);
            }
            if (result instanceof CompositeData[]) {
                final CompositeData[] array = (CompositeData[])result;
                if (targetClass.isArray()) {
                    return this.convertDataArrayToTargetArray(array, targetClass);
                }
                if (Collection.class.isAssignableFrom(targetClass)) {
                    final Class<?> elementType = GenericCollectionTypeResolver.getCollectionParameterType(parameter);
                    if (elementType != null) {
                        return this.convertDataArrayToTargetCollection(array, targetClass, elementType);
                    }
                }
            }
            else {
                if (result instanceof TabularData) {
                    final Method fromMethod = targetClass.getMethod("from", TabularData.class);
                    return ReflectionUtils.invokeMethod(fromMethod, null, result);
                }
                if (result instanceof TabularData[]) {
                    final TabularData[] array2 = (TabularData[])result;
                    if (targetClass.isArray()) {
                        return this.convertDataArrayToTargetArray(array2, targetClass);
                    }
                    if (Collection.class.isAssignableFrom(targetClass)) {
                        final Class<?> elementType = GenericCollectionTypeResolver.getCollectionParameterType(parameter);
                        if (elementType != null) {
                            return this.convertDataArrayToTargetCollection(array2, targetClass, elementType);
                        }
                    }
                }
            }
            throw new InvocationFailureException("Incompatible result value [" + result + "] for target type [" + targetClass.getName() + "]");
        }
        catch (NoSuchMethodException ex) {
            throw new InvocationFailureException("Could not obtain 'from(CompositeData)' / 'from(TabularData)' method on target type [" + targetClass.getName() + "] for conversion of MXBean data structure [" + result + "]");
        }
    }
    
    private Object convertDataArrayToTargetArray(final Object[] array, final Class<?> targetClass) throws NoSuchMethodException {
        final Class<?> targetType = targetClass.getComponentType();
        final Method fromMethod = targetType.getMethod("from", array.getClass().getComponentType());
        final Object resultArray = Array.newInstance(targetType, array.length);
        for (int i = 0; i < array.length; ++i) {
            Array.set(resultArray, i, ReflectionUtils.invokeMethod(fromMethod, null, array[i]));
        }
        return resultArray;
    }
    
    private Collection<?> convertDataArrayToTargetCollection(final Object[] array, final Class<?> collectionType, final Class<?> elementType) throws NoSuchMethodException {
        final Method fromMethod = elementType.getMethod("from", array.getClass().getComponentType());
        final Collection<Object> resultColl = CollectionFactory.createCollection(collectionType, Array.getLength(array));
        for (int i = 0; i < array.length; ++i) {
            resultColl.add(ReflectionUtils.invokeMethod(fromMethod, null, array[i]));
        }
        return resultColl;
    }
    
    @Override
    public void destroy() {
        this.connector.close();
    }
    
    private static class MethodCacheKey
    {
        private final String name;
        private final Class<?>[] parameterTypes;
        
        public MethodCacheKey(final String name, final Class<?>[] parameterTypes) {
            this.name = name;
            this.parameterTypes = ((parameterTypes != null) ? parameterTypes : new Class[0]);
        }
        
        @Override
        public boolean equals(final Object other) {
            if (other == this) {
                return true;
            }
            final MethodCacheKey otherKey = (MethodCacheKey)other;
            return this.name.equals(otherKey.name) && Arrays.equals(this.parameterTypes, otherKey.parameterTypes);
        }
        
        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }
}
