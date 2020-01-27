// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.jaxws;

import java.lang.reflect.Method;
import org.springframework.remoting.RemoteProxyFailureException;
import java.lang.reflect.InvocationTargetException;
import javax.xml.ws.WebServiceException;
import org.springframework.remoting.RemoteAccessException;
import javax.xml.ws.ProtocolException;
import org.springframework.remoting.RemoteConnectFailureException;
import javax.xml.ws.soap.SOAPFaultException;
import org.springframework.aop.support.AopUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.RemoteLookupFailureException;
import javax.xml.ws.BindingProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.util.StringUtils;
import javax.jws.WebService;
import java.util.HashMap;
import org.springframework.util.ClassUtils;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;
import java.util.Map;
import javax.xml.ws.Service;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.aopalliance.intercept.MethodInterceptor;

public class JaxWsPortClientInterceptor extends LocalJaxWsServiceFactory implements MethodInterceptor, BeanClassLoaderAware, InitializingBean
{
    private Service jaxWsService;
    private String portName;
    private String username;
    private String password;
    private String endpointAddress;
    private boolean maintainSession;
    private boolean useSoapAction;
    private String soapActionUri;
    private Map<String, Object> customProperties;
    private WebServiceFeature[] portFeatures;
    private Object[] webServiceFeatures;
    private Class<?> serviceInterface;
    private boolean lookupServiceOnStartup;
    private ClassLoader beanClassLoader;
    private QName portQName;
    private Object portStub;
    private final Object preparationMonitor;
    
    public JaxWsPortClientInterceptor() {
        this.lookupServiceOnStartup = true;
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        this.preparationMonitor = new Object();
    }
    
    public void setJaxWsService(final Service jaxWsService) {
        this.jaxWsService = jaxWsService;
    }
    
    public Service getJaxWsService() {
        return this.jaxWsService;
    }
    
    public void setPortName(final String portName) {
        this.portName = portName;
    }
    
    public String getPortName() {
        return this.portName;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setEndpointAddress(final String endpointAddress) {
        this.endpointAddress = endpointAddress;
    }
    
    public String getEndpointAddress() {
        return this.endpointAddress;
    }
    
    public void setMaintainSession(final boolean maintainSession) {
        this.maintainSession = maintainSession;
    }
    
    public boolean isMaintainSession() {
        return this.maintainSession;
    }
    
    public void setUseSoapAction(final boolean useSoapAction) {
        this.useSoapAction = useSoapAction;
    }
    
    public boolean isUseSoapAction() {
        return this.useSoapAction;
    }
    
    public void setSoapActionUri(final String soapActionUri) {
        this.soapActionUri = soapActionUri;
    }
    
    public String getSoapActionUri() {
        return this.soapActionUri;
    }
    
    public void setCustomProperties(final Map<String, Object> customProperties) {
        this.customProperties = customProperties;
    }
    
    public Map<String, Object> getCustomProperties() {
        if (this.customProperties == null) {
            this.customProperties = new HashMap<String, Object>();
        }
        return this.customProperties;
    }
    
    public void addCustomProperty(final String name, final Object value) {
        this.getCustomProperties().put(name, value);
    }
    
    public void setPortFeatures(final WebServiceFeature... features) {
        this.portFeatures = features;
    }
    
    @Deprecated
    public void setWebServiceFeatures(final Object[] webServiceFeatures) {
        this.webServiceFeatures = webServiceFeatures;
    }
    
    public void setServiceInterface(final Class<?> serviceInterface) {
        if (serviceInterface != null && !serviceInterface.isInterface()) {
            throw new IllegalArgumentException("'serviceInterface' must be an interface");
        }
        this.serviceInterface = serviceInterface;
    }
    
    public Class<?> getServiceInterface() {
        return this.serviceInterface;
    }
    
    public void setLookupServiceOnStartup(final boolean lookupServiceOnStartup) {
        this.lookupServiceOnStartup = lookupServiceOnStartup;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    protected ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.lookupServiceOnStartup) {
            this.prepare();
        }
    }
    
    public void prepare() {
        final Class<?> ifc = this.getServiceInterface();
        if (ifc == null) {
            throw new IllegalArgumentException("Property 'serviceInterface' is required");
        }
        final WebService ann = ifc.getAnnotation(WebService.class);
        if (ann != null) {
            this.applyDefaultsFromAnnotation(ann);
        }
        Service serviceToUse = this.getJaxWsService();
        if (serviceToUse == null) {
            serviceToUse = this.createJaxWsService();
        }
        this.portQName = this.getQName((this.getPortName() != null) ? this.getPortName() : this.getServiceInterface().getName());
        final Object stub = this.getPortStub(serviceToUse, (this.getPortName() != null) ? this.portQName : null);
        this.preparePortStub(stub);
        this.portStub = stub;
    }
    
    protected void applyDefaultsFromAnnotation(final WebService ann) {
        if (this.getWsdlDocumentUrl() == null) {
            final String wsdl = ann.wsdlLocation();
            if (StringUtils.hasText(wsdl)) {
                try {
                    this.setWsdlDocumentUrl(new URL(wsdl));
                }
                catch (MalformedURLException ex) {
                    throw new IllegalStateException("Encountered invalid @Service wsdlLocation value [" + wsdl + "]", ex);
                }
            }
        }
        if (this.getNamespaceUri() == null) {
            final String ns = ann.targetNamespace();
            if (StringUtils.hasText(ns)) {
                this.setNamespaceUri(ns);
            }
        }
        if (this.getServiceName() == null) {
            final String sn = ann.serviceName();
            if (StringUtils.hasText(sn)) {
                this.setServiceName(sn);
            }
        }
        if (this.getPortName() == null) {
            final String pn = ann.portName();
            if (StringUtils.hasText(pn)) {
                this.setPortName(pn);
            }
        }
    }
    
    protected boolean isPrepared() {
        synchronized (this.preparationMonitor) {
            return this.portStub != null;
        }
    }
    
    protected final QName getPortQName() {
        return this.portQName;
    }
    
    protected Object getPortStub(final Service service, final QName portQName) {
        if (this.portFeatures != null || this.webServiceFeatures != null) {
            WebServiceFeature[] portFeaturesToUse = this.portFeatures;
            if (portFeaturesToUse == null) {
                portFeaturesToUse = new WebServiceFeature[this.webServiceFeatures.length];
                for (int i = 0; i < this.webServiceFeatures.length; ++i) {
                    portFeaturesToUse[i] = this.convertWebServiceFeature(this.webServiceFeatures[i]);
                }
            }
            return (portQName != null) ? service.getPort(portQName, (Class)this.getServiceInterface(), portFeaturesToUse) : service.getPort((Class)this.getServiceInterface(), portFeaturesToUse);
        }
        return (portQName != null) ? service.getPort(portQName, (Class)this.getServiceInterface()) : service.getPort((Class)this.getServiceInterface());
    }
    
    private WebServiceFeature convertWebServiceFeature(final Object feature) {
        Assert.notNull(feature, "WebServiceFeature specification object must not be null");
        if (feature instanceof WebServiceFeature) {
            return (WebServiceFeature)feature;
        }
        if (feature instanceof Class) {
            return BeanUtils.instantiate((Class<WebServiceFeature>)feature);
        }
        if (feature instanceof String) {
            try {
                final Class<?> featureClass = this.getBeanClassLoader().loadClass((String)feature);
                return BeanUtils.instantiate(featureClass);
            }
            catch (ClassNotFoundException ex) {
                throw new IllegalArgumentException("Could not load WebServiceFeature class [" + feature + "]");
            }
        }
        throw new IllegalArgumentException("Unknown WebServiceFeature specification type: " + feature.getClass());
    }
    
    protected void preparePortStub(final Object stub) {
        final Map<String, Object> stubProperties = new HashMap<String, Object>();
        final String username = this.getUsername();
        if (username != null) {
            stubProperties.put("javax.xml.ws.security.auth.username", username);
        }
        final String password = this.getPassword();
        if (password != null) {
            stubProperties.put("javax.xml.ws.security.auth.password", password);
        }
        final String endpointAddress = this.getEndpointAddress();
        if (endpointAddress != null) {
            stubProperties.put("javax.xml.ws.service.endpoint.address", endpointAddress);
        }
        if (this.isMaintainSession()) {
            stubProperties.put("javax.xml.ws.session.maintain", Boolean.TRUE);
        }
        if (this.isUseSoapAction()) {
            stubProperties.put("javax.xml.ws.soap.http.soapaction.use", Boolean.TRUE);
        }
        final String soapActionUri = this.getSoapActionUri();
        if (soapActionUri != null) {
            stubProperties.put("javax.xml.ws.soap.http.soapaction.uri", soapActionUri);
        }
        stubProperties.putAll(this.getCustomProperties());
        if (!stubProperties.isEmpty()) {
            if (!(stub instanceof BindingProvider)) {
                throw new RemoteLookupFailureException("Port stub of class [" + stub.getClass().getName() + "] is not a customizable JAX-WS stub: it does not implement interface [javax.xml.ws.BindingProvider]");
            }
            ((BindingProvider)stub).getRequestContext().putAll(stubProperties);
        }
    }
    
    protected Object getPortStub() {
        return this.portStub;
    }
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        if (AopUtils.isToStringMethod(invocation.getMethod())) {
            return "JAX-WS proxy for port [" + this.getPortName() + "] of service [" + this.getServiceName() + "]";
        }
        synchronized (this.preparationMonitor) {
            if (!this.isPrepared()) {
                this.prepare();
            }
        }
        return this.doInvoke(invocation);
    }
    
    protected Object doInvoke(final MethodInvocation invocation) throws Throwable {
        try {
            return this.doInvoke(invocation, this.getPortStub());
        }
        catch (SOAPFaultException ex) {
            throw new JaxWsSoapFaultException(ex);
        }
        catch (ProtocolException ex2) {
            throw new RemoteConnectFailureException("Could not connect to remote service [" + this.getEndpointAddress() + "]", (Throwable)ex2);
        }
        catch (WebServiceException ex3) {
            throw new RemoteAccessException("Could not access remote service at [" + this.getEndpointAddress() + "]", (Throwable)ex3);
        }
    }
    
    protected Object doInvoke(final MethodInvocation invocation, final Object portStub) throws Throwable {
        final Method method = invocation.getMethod();
        try {
            return method.invoke(portStub, invocation.getArguments());
        }
        catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
        catch (Throwable ex2) {
            throw new RemoteProxyFailureException("Invocation of stub method failed: " + method, ex2);
        }
    }
}
