// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.export;

import org.springframework.jmx.export.notification.NotificationPublisher;
import javax.management.modelmbean.ModelMBeanNotificationBroadcaster;
import org.springframework.jmx.export.notification.ModelMBeanNotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import java.util.LinkedHashSet;
import org.springframework.util.ObjectUtils;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.MBeanException;
import javax.management.modelmbean.RequiredModelMBean;
import javax.management.StandardMBean;
import javax.management.NotCompliantMBeanException;
import org.springframework.aop.support.AopUtils;
import javax.management.MalformedObjectNameException;
import org.springframework.jmx.export.naming.SelfNaming;
import org.springframework.aop.TargetSource;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.aop.framework.ProxyFactory;
import javax.management.DynamicMBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.jmx.export.assembler.AutodetectCapableMBeanInfoAssembler;
import java.util.HashMap;
import javax.management.modelmbean.ModelMBean;
import javax.management.JMException;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.beans.factory.BeanFactory;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.springframework.util.Assert;
import javax.management.NotificationListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.springframework.util.ClassUtils;
import org.springframework.jmx.export.naming.KeyNamingStrategy;
import org.springframework.jmx.export.assembler.SimpleReflectiveMBeanInfoAssembler;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.jmx.export.assembler.MBeanInfoAssembler;
import javax.management.ObjectName;
import java.util.Set;
import java.util.Map;
import org.springframework.core.Constants;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.jmx.support.MBeanRegistrationSupport;

public class MBeanExporter extends MBeanRegistrationSupport implements MBeanExportOperations, BeanClassLoaderAware, BeanFactoryAware, InitializingBean, DisposableBean
{
    public static final int AUTODETECT_NONE = 0;
    public static final int AUTODETECT_MBEAN = 1;
    public static final int AUTODETECT_ASSEMBLER = 2;
    public static final int AUTODETECT_ALL = 3;
    private static final String WILDCARD = "*";
    private static final String MR_TYPE_OBJECT_REFERENCE = "ObjectReference";
    private static final String CONSTANT_PREFIX_AUTODETECT = "AUTODETECT_";
    private static final Constants constants;
    private Map<String, Object> beans;
    private Integer autodetectMode;
    private boolean allowEagerInit;
    private boolean ensureUniqueRuntimeObjectNames;
    private boolean exposeManagedResourceClassLoader;
    private Set<String> excludedBeans;
    private MBeanExporterListener[] listeners;
    private NotificationListenerBean[] notificationListeners;
    private final Map<NotificationListenerBean, ObjectName[]> registeredNotificationListeners;
    private MBeanInfoAssembler assembler;
    private ObjectNamingStrategy namingStrategy;
    private ClassLoader beanClassLoader;
    private ListableBeanFactory beanFactory;
    
    public MBeanExporter() {
        this.allowEagerInit = false;
        this.ensureUniqueRuntimeObjectNames = true;
        this.exposeManagedResourceClassLoader = true;
        this.registeredNotificationListeners = new LinkedHashMap<NotificationListenerBean, ObjectName[]>();
        this.assembler = new SimpleReflectiveMBeanInfoAssembler();
        this.namingStrategy = new KeyNamingStrategy();
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
    }
    
    public void setBeans(final Map<String, Object> beans) {
        this.beans = beans;
    }
    
    public void setAutodetect(final boolean autodetect) {
        this.autodetectMode = (autodetect ? 3 : 0);
    }
    
    public void setAutodetectMode(final int autodetectMode) {
        if (!MBeanExporter.constants.getValues("AUTODETECT_").contains(autodetectMode)) {
            throw new IllegalArgumentException("Only values of autodetect constants allowed");
        }
        this.autodetectMode = autodetectMode;
    }
    
    public void setAutodetectModeName(final String constantName) {
        if (constantName == null || !constantName.startsWith("AUTODETECT_")) {
            throw new IllegalArgumentException("Only autodetect constants allowed");
        }
        this.autodetectMode = (Integer)MBeanExporter.constants.asNumber(constantName);
    }
    
    public void setAllowEagerInit(final boolean allowEagerInit) {
        this.allowEagerInit = allowEagerInit;
    }
    
    public void setAssembler(final MBeanInfoAssembler assembler) {
        this.assembler = assembler;
    }
    
    public void setNamingStrategy(final ObjectNamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }
    
    public void setListeners(final MBeanExporterListener[] listeners) {
        this.listeners = listeners;
    }
    
    public void setExcludedBeans(final String[] excludedBeans) {
        this.excludedBeans = ((excludedBeans != null) ? new HashSet<String>(Arrays.asList(excludedBeans)) : null);
    }
    
    public void setEnsureUniqueRuntimeObjectNames(final boolean ensureUniqueRuntimeObjectNames) {
        this.ensureUniqueRuntimeObjectNames = ensureUniqueRuntimeObjectNames;
    }
    
    public void setExposeManagedResourceClassLoader(final boolean exposeManagedResourceClassLoader) {
        this.exposeManagedResourceClassLoader = exposeManagedResourceClassLoader;
    }
    
    public void setNotificationListeners(final NotificationListenerBean[] notificationListeners) {
        this.notificationListeners = notificationListeners;
    }
    
    public void setNotificationListenerMappings(final Map<?, ? extends NotificationListener> listeners) {
        Assert.notNull(listeners, "'listeners' must not be null");
        final List<NotificationListenerBean> notificationListeners = new ArrayList<NotificationListenerBean>(listeners.size());
        for (final Map.Entry<?, ? extends NotificationListener> entry : listeners.entrySet()) {
            final NotificationListenerBean bean = new NotificationListenerBean((NotificationListener)entry.getValue());
            final Object key = entry.getKey();
            if (key != null && !"*".equals(key)) {
                bean.setMappedObjectName(entry.getKey());
            }
            notificationListeners.add(bean);
        }
        this.notificationListeners = notificationListeners.toArray(new NotificationListenerBean[notificationListeners.size()]);
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            this.beanFactory = (ListableBeanFactory)beanFactory;
        }
        else {
            this.logger.info("MBeanExporter not running in a ListableBeanFactory: autodetection of MBeans not available.");
        }
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.server == null) {
            this.server = JmxUtils.locateMBeanServer();
        }
        try {
            this.logger.info("Registering beans for JMX exposure on startup");
            this.registerBeans();
            this.registerNotificationListeners();
        }
        catch (RuntimeException ex) {
            this.unregisterNotificationListeners();
            this.unregisterBeans();
            throw ex;
        }
    }
    
    @Override
    public void destroy() {
        this.logger.info("Unregistering JMX-exposed beans on shutdown");
        this.unregisterNotificationListeners();
        this.unregisterBeans();
    }
    
    @Override
    public ObjectName registerManagedResource(final Object managedResource) throws MBeanExportException {
        Assert.notNull(managedResource, "Managed resource must not be null");
        ObjectName objectName;
        try {
            objectName = this.getObjectName(managedResource, null);
            if (this.ensureUniqueRuntimeObjectNames) {
                objectName = JmxUtils.appendIdentityToObjectName(objectName, managedResource);
            }
        }
        catch (Exception ex) {
            throw new MBeanExportException("Unable to generate ObjectName for MBean [" + managedResource + "]", ex);
        }
        this.registerManagedResource(managedResource, objectName);
        return objectName;
    }
    
    @Override
    public void registerManagedResource(final Object managedResource, final ObjectName objectName) throws MBeanExportException {
        Assert.notNull(managedResource, "Managed resource must not be null");
        Assert.notNull(objectName, "ObjectName must not be null");
        try {
            if (this.isMBean(managedResource.getClass())) {
                this.doRegister(managedResource, objectName);
            }
            else {
                final ModelMBean mbean = this.createAndConfigureMBean(managedResource, managedResource.getClass().getName());
                this.doRegister(mbean, objectName);
                this.injectNotificationPublisherIfNecessary(managedResource, mbean, objectName);
            }
        }
        catch (JMException ex) {
            throw new UnableToRegisterMBeanException("Unable to register MBean [" + managedResource + "] with object name [" + objectName + "]", ex);
        }
    }
    
    @Override
    public void unregisterManagedResource(final ObjectName objectName) {
        Assert.notNull(objectName, "ObjectName must not be null");
        this.doUnregister(objectName);
    }
    
    protected void registerBeans() {
        if (this.beans == null) {
            this.beans = new HashMap<String, Object>();
            if (this.autodetectMode == null) {
                this.autodetectMode = 3;
            }
        }
        final int mode = (this.autodetectMode != null) ? this.autodetectMode : 0;
        if (mode != 0) {
            if (this.beanFactory == null) {
                throw new MBeanExportException("Cannot autodetect MBeans if not running in a BeanFactory");
            }
            if (mode == 1 || mode == 3) {
                this.logger.debug("Autodetecting user-defined JMX MBeans");
                this.autodetectMBeans();
            }
            if ((mode == 2 || mode == 3) && this.assembler instanceof AutodetectCapableMBeanInfoAssembler) {
                this.autodetectBeans((AutodetectCapableMBeanInfoAssembler)this.assembler);
            }
        }
        if (!this.beans.isEmpty()) {
            for (final Map.Entry<String, Object> entry : this.beans.entrySet()) {
                this.registerBeanNameOrInstance(entry.getValue(), entry.getKey());
            }
        }
    }
    
    protected boolean isBeanDefinitionLazyInit(final ListableBeanFactory beanFactory, final String beanName) {
        return beanFactory instanceof ConfigurableListableBeanFactory && beanFactory.containsBeanDefinition(beanName) && ((ConfigurableListableBeanFactory)beanFactory).getBeanDefinition(beanName).isLazyInit();
    }
    
    protected ObjectName registerBeanNameOrInstance(final Object mapValue, final String beanKey) throws MBeanExportException {
        try {
            if (!(mapValue instanceof String)) {
                if (this.beanFactory != null) {
                    final Map<String, ?> beansOfSameType = this.beanFactory.getBeansOfType(mapValue.getClass(), false, this.allowEagerInit);
                    for (final Map.Entry<String, ?> entry : beansOfSameType.entrySet()) {
                        if (entry.getValue() == mapValue) {
                            final String beanName = entry.getKey();
                            final ObjectName objectName = this.registerBeanInstance(mapValue, beanKey);
                            this.replaceNotificationListenerBeanNameKeysIfNecessary(beanName, objectName);
                            return objectName;
                        }
                    }
                }
                return this.registerBeanInstance(mapValue, beanKey);
            }
            if (this.beanFactory == null) {
                throw new MBeanExportException("Cannot resolve bean names if not running in a BeanFactory");
            }
            final String beanName2 = (String)mapValue;
            if (this.isBeanDefinitionLazyInit(this.beanFactory, beanName2)) {
                final ObjectName objectName2 = this.registerLazyInit(beanName2, beanKey);
                this.replaceNotificationListenerBeanNameKeysIfNecessary(beanName2, objectName2);
                return objectName2;
            }
            final Object bean = this.beanFactory.getBean(beanName2);
            final ObjectName objectName3 = this.registerBeanInstance(bean, beanKey);
            this.replaceNotificationListenerBeanNameKeysIfNecessary(beanName2, objectName3);
            return objectName3;
        }
        catch (Exception ex) {
            throw new UnableToRegisterMBeanException("Unable to register MBean [" + mapValue + "] with key '" + beanKey + "'", ex);
        }
    }
    
    private void replaceNotificationListenerBeanNameKeysIfNecessary(final String beanName, final ObjectName objectName) {
        if (this.notificationListeners != null) {
            for (final NotificationListenerBean notificationListener : this.notificationListeners) {
                notificationListener.replaceObjectName(beanName, objectName);
            }
        }
    }
    
    private ObjectName registerBeanInstance(final Object bean, final String beanKey) throws JMException {
        final ObjectName objectName = this.getObjectName(bean, beanKey);
        Object mbeanToExpose = null;
        if (this.isMBean(bean.getClass())) {
            mbeanToExpose = bean;
        }
        else {
            final DynamicMBean adaptedBean = this.adaptMBeanIfPossible(bean);
            if (adaptedBean != null) {
                mbeanToExpose = adaptedBean;
            }
        }
        if (mbeanToExpose != null) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Located MBean '" + beanKey + "': registering with JMX server as MBean [" + objectName + "]");
            }
            this.doRegister(mbeanToExpose, objectName);
        }
        else {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Located managed bean '" + beanKey + "': registering with JMX server as MBean [" + objectName + "]");
            }
            final ModelMBean mbean = this.createAndConfigureMBean(bean, beanKey);
            this.doRegister(mbean, objectName);
            this.injectNotificationPublisherIfNecessary(bean, mbean, objectName);
        }
        return objectName;
    }
    
    private ObjectName registerLazyInit(final String beanName, final String beanKey) throws JMException {
        final ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.setFrozen(true);
        if (this.isMBean(this.beanFactory.getType(beanName))) {
            final LazyInitTargetSource targetSource = new LazyInitTargetSource();
            targetSource.setTargetBeanName(beanName);
            targetSource.setBeanFactory(this.beanFactory);
            proxyFactory.setTargetSource(targetSource);
            final Object proxy = proxyFactory.getProxy(this.beanClassLoader);
            final ObjectName objectName = this.getObjectName(proxy, beanKey);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Located MBean '" + beanKey + "': registering with JMX server as lazy-init MBean [" + objectName + "]");
            }
            this.doRegister(proxy, objectName);
            return objectName;
        }
        final NotificationPublisherAwareLazyTargetSource targetSource2 = new NotificationPublisherAwareLazyTargetSource();
        targetSource2.setTargetBeanName(beanName);
        targetSource2.setBeanFactory(this.beanFactory);
        proxyFactory.setTargetSource(targetSource2);
        final Object proxy = proxyFactory.getProxy(this.beanClassLoader);
        final ObjectName objectName = this.getObjectName(proxy, beanKey);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Located simple bean '" + beanKey + "': registering with JMX server as lazy-init MBean [" + objectName + "]");
        }
        final ModelMBean mbean = this.createAndConfigureMBean(proxy, beanKey);
        targetSource2.setModelMBean(mbean);
        targetSource2.setObjectName(objectName);
        this.doRegister(mbean, objectName);
        return objectName;
    }
    
    protected ObjectName getObjectName(final Object bean, final String beanKey) throws MalformedObjectNameException {
        if (bean instanceof SelfNaming) {
            return ((SelfNaming)bean).getObjectName();
        }
        return this.namingStrategy.getObjectName(bean, beanKey);
    }
    
    protected boolean isMBean(final Class<?> beanClass) {
        return JmxUtils.isMBean(beanClass);
    }
    
    protected DynamicMBean adaptMBeanIfPossible(final Object bean) throws JMException {
        final Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (targetClass != bean.getClass()) {
            Class<Object> ifc = (Class<Object>)JmxUtils.getMXBeanInterface(targetClass);
            if (ifc != null) {
                if (!ifc.isInstance(bean)) {
                    throw new NotCompliantMBeanException("Managed bean [" + bean + "] has a target class with an MXBean interface but does not expose it in the proxy");
                }
                return new StandardMBean((T)bean, (Class<T>)ifc, true);
            }
            else {
                ifc = (Class<Object>)JmxUtils.getMBeanInterface(targetClass);
                if (ifc != null) {
                    if (!ifc.isInstance(bean)) {
                        throw new NotCompliantMBeanException("Managed bean [" + bean + "] has a target class with an MBean interface but does not expose it in the proxy");
                    }
                    return new StandardMBean((T)bean, (Class<T>)ifc);
                }
            }
        }
        return null;
    }
    
    protected ModelMBean createAndConfigureMBean(final Object managedResource, final String beanKey) throws MBeanExportException {
        try {
            final ModelMBean mbean = this.createModelMBean();
            mbean.setModelMBeanInfo(this.getMBeanInfo(managedResource, beanKey));
            mbean.setManagedResource(managedResource, "ObjectReference");
            return mbean;
        }
        catch (Exception ex) {
            throw new MBeanExportException("Could not create ModelMBean for managed resource [" + managedResource + "] with key '" + beanKey + "'", ex);
        }
    }
    
    protected ModelMBean createModelMBean() throws MBeanException {
        return this.exposeManagedResourceClassLoader ? new SpringModelMBean() : new RequiredModelMBean();
    }
    
    private ModelMBeanInfo getMBeanInfo(final Object managedBean, final String beanKey) throws JMException {
        final ModelMBeanInfo info = this.assembler.getMBeanInfo(managedBean, beanKey);
        if (this.logger.isWarnEnabled() && ObjectUtils.isEmpty(info.getAttributes()) && ObjectUtils.isEmpty(info.getOperations())) {
            this.logger.warn("Bean with key '" + beanKey + "' has been registered as an MBean but has no exposed attributes or operations");
        }
        return info;
    }
    
    private void autodetectBeans(final AutodetectCapableMBeanInfoAssembler assembler) {
        this.autodetect(new AutodetectCallback() {
            @Override
            public boolean include(final Class<?> beanClass, final String beanName) {
                return assembler.includeBean(beanClass, beanName);
            }
        });
    }
    
    private void autodetectMBeans() {
        this.autodetect(new AutodetectCallback() {
            @Override
            public boolean include(final Class<?> beanClass, final String beanName) {
                return MBeanExporter.this.isMBean(beanClass);
            }
        });
    }
    
    private void autodetect(final AutodetectCallback callback) {
        final Set<String> beanNames = new LinkedHashSet<String>(this.beanFactory.getBeanDefinitionCount());
        beanNames.addAll(Arrays.asList(this.beanFactory.getBeanDefinitionNames()));
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            beanNames.addAll(Arrays.asList(((ConfigurableBeanFactory)this.beanFactory).getSingletonNames()));
        }
        for (final String beanName : beanNames) {
            if (!this.isExcluded(beanName) && !this.isBeanDefinitionAbstract(this.beanFactory, beanName)) {
                try {
                    final Class<?> beanClass = this.beanFactory.getType(beanName);
                    if (beanClass == null || !callback.include(beanClass, beanName)) {
                        continue;
                    }
                    final boolean lazyInit = this.isBeanDefinitionLazyInit(this.beanFactory, beanName);
                    final Object beanInstance = lazyInit ? null : this.beanFactory.getBean(beanName);
                    if (!this.beans.containsValue(beanName) && (beanInstance == null || !CollectionUtils.containsInstance(this.beans.values(), beanInstance))) {
                        this.beans.put(beanName, (beanInstance != null) ? beanInstance : beanName);
                        if (!this.logger.isInfoEnabled()) {
                            continue;
                        }
                        this.logger.info("Bean with name '" + beanName + "' has been autodetected for JMX exposure");
                    }
                    else {
                        if (!this.logger.isDebugEnabled()) {
                            continue;
                        }
                        this.logger.debug("Bean with name '" + beanName + "' is already registered for JMX exposure");
                    }
                }
                catch (CannotLoadBeanClassException ex) {
                    if (this.allowEagerInit) {
                        throw ex;
                    }
                    continue;
                }
            }
        }
    }
    
    private boolean isExcluded(final String beanName) {
        return this.excludedBeans != null && (this.excludedBeans.contains(beanName) || (beanName.startsWith("&") && this.excludedBeans.contains(beanName.substring("&".length()))));
    }
    
    private boolean isBeanDefinitionAbstract(final ListableBeanFactory beanFactory, final String beanName) {
        return beanFactory instanceof ConfigurableListableBeanFactory && beanFactory.containsBeanDefinition(beanName) && ((ConfigurableListableBeanFactory)beanFactory).getBeanDefinition(beanName).isAbstract();
    }
    
    private void injectNotificationPublisherIfNecessary(final Object managedResource, final ModelMBean modelMBean, final ObjectName objectName) {
        if (managedResource instanceof NotificationPublisherAware) {
            ((NotificationPublisherAware)managedResource).setNotificationPublisher(new ModelMBeanNotificationPublisher(modelMBean, objectName, managedResource));
        }
    }
    
    private void registerNotificationListeners() throws MBeanExportException {
        if (this.notificationListeners != null) {
            for (final NotificationListenerBean bean : this.notificationListeners) {
                try {
                    ObjectName[] mappedObjectNames = bean.getResolvedObjectNames();
                    if (mappedObjectNames == null) {
                        mappedObjectNames = this.getRegisteredObjectNames();
                    }
                    if (this.registeredNotificationListeners.put(bean, mappedObjectNames) == null) {
                        for (final ObjectName mappedObjectName : mappedObjectNames) {
                            this.server.addNotificationListener(mappedObjectName, bean.getNotificationListener(), bean.getNotificationFilter(), bean.getHandback());
                        }
                    }
                }
                catch (Exception ex) {
                    throw new MBeanExportException("Unable to register NotificationListener", ex);
                }
            }
        }
    }
    
    private void unregisterNotificationListeners() {
        for (final Map.Entry<NotificationListenerBean, ObjectName[]> entry : this.registeredNotificationListeners.entrySet()) {
            final NotificationListenerBean bean = entry.getKey();
            final ObjectName[] array;
            final ObjectName[] mappedObjectNames = array = entry.getValue();
            for (final ObjectName mappedObjectName : array) {
                try {
                    this.server.removeNotificationListener(mappedObjectName, bean.getNotificationListener(), bean.getNotificationFilter(), bean.getHandback());
                }
                catch (Exception ex) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Unable to unregister NotificationListener", ex);
                    }
                }
            }
        }
        this.registeredNotificationListeners.clear();
    }
    
    @Override
    protected void onRegister(final ObjectName objectName) {
        this.notifyListenersOfRegistration(objectName);
    }
    
    @Override
    protected void onUnregister(final ObjectName objectName) {
        this.notifyListenersOfUnregistration(objectName);
    }
    
    private void notifyListenersOfRegistration(final ObjectName objectName) {
        if (this.listeners != null) {
            for (final MBeanExporterListener listener : this.listeners) {
                listener.mbeanRegistered(objectName);
            }
        }
    }
    
    private void notifyListenersOfUnregistration(final ObjectName objectName) {
        if (this.listeners != null) {
            for (final MBeanExporterListener listener : this.listeners) {
                listener.mbeanUnregistered(objectName);
            }
        }
    }
    
    static {
        constants = new Constants(MBeanExporter.class);
    }
    
    private class NotificationPublisherAwareLazyTargetSource extends LazyInitTargetSource
    {
        private ModelMBean modelMBean;
        private ObjectName objectName;
        
        public void setModelMBean(final ModelMBean modelMBean) {
            this.modelMBean = modelMBean;
        }
        
        public void setObjectName(final ObjectName objectName) {
            this.objectName = objectName;
        }
        
        @Override
        protected void postProcessTargetObject(final Object targetObject) {
            MBeanExporter.this.injectNotificationPublisherIfNecessary(targetObject, this.modelMBean, this.objectName);
        }
    }
    
    private interface AutodetectCallback
    {
        boolean include(final Class<?> p0, final String p1);
    }
}
