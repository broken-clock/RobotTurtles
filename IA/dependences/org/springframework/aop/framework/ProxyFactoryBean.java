// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.springframework.aop.framework.adapter.UnknownAdviceTypeException;
import org.springframework.aop.target.SingletonTargetSource;
import java.util.Iterator;
import java.util.Map;
import org.springframework.core.OrderComparator;
import java.util.HashMap;
import org.aopalliance.intercept.Interceptor;
import org.springframework.beans.factory.BeanFactoryUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.ListableBeanFactory;
import java.util.Arrays;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.util.ObjectUtils;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.BeansException;
import org.springframework.util.ClassUtils;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;

public class ProxyFactoryBean extends ProxyCreatorSupport implements FactoryBean<Object>, BeanClassLoaderAware, BeanFactoryAware
{
    public static final String GLOBAL_SUFFIX = "*";
    protected final Log logger;
    private String[] interceptorNames;
    private String targetName;
    private boolean autodetectInterfaces;
    private boolean singleton;
    private AdvisorAdapterRegistry advisorAdapterRegistry;
    private boolean freezeProxy;
    private transient ClassLoader proxyClassLoader;
    private transient boolean classLoaderConfigured;
    private transient BeanFactory beanFactory;
    private boolean advisorChainInitialized;
    private Object singletonInstance;
    
    public ProxyFactoryBean() {
        this.logger = LogFactory.getLog(this.getClass());
        this.autodetectInterfaces = true;
        this.singleton = true;
        this.advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
        this.freezeProxy = false;
        this.proxyClassLoader = ClassUtils.getDefaultClassLoader();
        this.classLoaderConfigured = false;
        this.advisorChainInitialized = false;
    }
    
    public void setProxyInterfaces(final Class<?>[] proxyInterfaces) throws ClassNotFoundException {
        this.setInterfaces(proxyInterfaces);
    }
    
    public void setInterceptorNames(final String[] interceptorNames) {
        this.interceptorNames = interceptorNames;
    }
    
    public void setTargetName(final String targetName) {
        this.targetName = targetName;
    }
    
    public void setAutodetectInterfaces(final boolean autodetectInterfaces) {
        this.autodetectInterfaces = autodetectInterfaces;
    }
    
    public void setSingleton(final boolean singleton) {
        this.singleton = singleton;
    }
    
    public void setAdvisorAdapterRegistry(final AdvisorAdapterRegistry advisorAdapterRegistry) {
        this.advisorAdapterRegistry = advisorAdapterRegistry;
    }
    
    @Override
    public void setFrozen(final boolean frozen) {
        this.freezeProxy = frozen;
    }
    
    public void setProxyClassLoader(final ClassLoader classLoader) {
        this.proxyClassLoader = classLoader;
        this.classLoaderConfigured = (classLoader != null);
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        if (!this.classLoaderConfigured) {
            this.proxyClassLoader = classLoader;
        }
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.checkInterceptorNames();
    }
    
    @Override
    public Object getObject() throws BeansException {
        this.initializeAdvisorChain();
        if (this.isSingleton()) {
            return this.getSingletonInstance();
        }
        if (this.targetName == null) {
            this.logger.warn("Using non-singleton proxies with singleton targets is often undesirable. Enable prototype proxies by setting the 'targetName' property.");
        }
        return this.newPrototypeInstance();
    }
    
    @Override
    public Class<?> getObjectType() {
        synchronized (this) {
            if (this.singletonInstance != null) {
                return this.singletonInstance.getClass();
            }
        }
        final Class<?>[] ifcs = this.getProxiedInterfaces();
        if (ifcs.length == 1) {
            return ifcs[0];
        }
        if (ifcs.length > 1) {
            return this.createCompositeInterface(ifcs);
        }
        if (this.targetName != null && this.beanFactory != null) {
            return this.beanFactory.getType(this.targetName);
        }
        return this.getTargetClass();
    }
    
    @Override
    public boolean isSingleton() {
        return this.singleton;
    }
    
    protected Class<?> createCompositeInterface(final Class<?>[] interfaces) {
        return ClassUtils.createCompositeInterface(interfaces, this.proxyClassLoader);
    }
    
    private synchronized Object getSingletonInstance() {
        if (this.singletonInstance == null) {
            this.targetSource = this.freshTargetSource();
            if (this.autodetectInterfaces && this.getProxiedInterfaces().length == 0 && !this.isProxyTargetClass()) {
                final Class<?> targetClass = this.getTargetClass();
                if (targetClass == null) {
                    throw new FactoryBeanNotInitializedException("Cannot determine target class for proxy");
                }
                this.setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, this.proxyClassLoader));
            }
            super.setFrozen(this.freezeProxy);
            this.singletonInstance = this.getProxy(this.createAopProxy());
        }
        return this.singletonInstance;
    }
    
    private synchronized Object newPrototypeInstance() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Creating copy of prototype ProxyFactoryBean config: " + this);
        }
        final ProxyCreatorSupport copy = new ProxyCreatorSupport(this.getAopProxyFactory());
        final TargetSource targetSource = this.freshTargetSource();
        copy.copyConfigurationFrom(this, targetSource, this.freshAdvisorChain());
        if (this.autodetectInterfaces && this.getProxiedInterfaces().length == 0 && !this.isProxyTargetClass()) {
            copy.setInterfaces(ClassUtils.getAllInterfacesForClass(targetSource.getTargetClass(), this.proxyClassLoader));
        }
        copy.setFrozen(this.freezeProxy);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Using ProxyCreatorSupport copy: " + copy);
        }
        return this.getProxy(copy.createAopProxy());
    }
    
    protected Object getProxy(final AopProxy aopProxy) {
        return aopProxy.getProxy(this.proxyClassLoader);
    }
    
    private void checkInterceptorNames() {
        if (!ObjectUtils.isEmpty(this.interceptorNames)) {
            final String finalName = this.interceptorNames[this.interceptorNames.length - 1];
            if (this.targetName == null && this.targetSource == ProxyFactoryBean.EMPTY_TARGET_SOURCE && !finalName.endsWith("*") && !this.isNamedBeanAnAdvisorOrAdvice(finalName)) {
                this.targetName = finalName;
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Bean with name '" + finalName + "' concluding interceptor chain " + "is not an advisor class: treating it as a target or TargetSource");
                }
                final String[] newNames = new String[this.interceptorNames.length - 1];
                System.arraycopy(this.interceptorNames, 0, newNames, 0, newNames.length);
                this.interceptorNames = newNames;
            }
        }
    }
    
    private boolean isNamedBeanAnAdvisorOrAdvice(final String beanName) {
        final Class<?> namedBeanClass = this.beanFactory.getType(beanName);
        if (namedBeanClass != null) {
            return Advisor.class.isAssignableFrom(namedBeanClass) || Advice.class.isAssignableFrom(namedBeanClass);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Could not determine type of bean with name '" + beanName + "' - assuming it is neither an Advisor nor an Advice");
        }
        return false;
    }
    
    private synchronized void initializeAdvisorChain() throws AopConfigException, BeansException {
        if (this.advisorChainInitialized) {
            return;
        }
        if (!ObjectUtils.isEmpty(this.interceptorNames)) {
            if (this.beanFactory == null) {
                throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve interceptor names " + Arrays.asList(this.interceptorNames));
            }
            if (this.interceptorNames[this.interceptorNames.length - 1].endsWith("*") && this.targetName == null && this.targetSource == ProxyFactoryBean.EMPTY_TARGET_SOURCE) {
                throw new AopConfigException("Target required after globals");
            }
            for (final String name : this.interceptorNames) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Configuring advisor or advice '" + name + "'");
                }
                if (name.endsWith("*")) {
                    if (!(this.beanFactory instanceof ListableBeanFactory)) {
                        throw new AopConfigException("Can only use global advisors or interceptors with a ListableBeanFactory");
                    }
                    this.addGlobalAdvisor((ListableBeanFactory)this.beanFactory, name.substring(0, name.length() - "*".length()));
                }
                else {
                    Object advice;
                    if (this.singleton || this.beanFactory.isSingleton(name)) {
                        advice = this.beanFactory.getBean(name);
                    }
                    else {
                        advice = new PrototypePlaceholderAdvisor(name);
                    }
                    this.addAdvisorOnChainCreation(advice, name);
                }
            }
        }
        this.advisorChainInitialized = true;
    }
    
    private List<Advisor> freshAdvisorChain() {
        final Advisor[] advisors = this.getAdvisors();
        final List<Advisor> freshAdvisors = new ArrayList<Advisor>(advisors.length);
        for (final Advisor advisor : advisors) {
            if (advisor instanceof PrototypePlaceholderAdvisor) {
                final PrototypePlaceholderAdvisor pa = (PrototypePlaceholderAdvisor)advisor;
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Refreshing bean named '" + pa.getBeanName() + "'");
                }
                if (this.beanFactory == null) {
                    throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve prototype advisor '" + pa.getBeanName() + "'");
                }
                final Object bean = this.beanFactory.getBean(pa.getBeanName());
                final Advisor refreshedAdvisor = this.namedBeanToAdvisor(bean);
                freshAdvisors.add(refreshedAdvisor);
            }
            else {
                freshAdvisors.add(advisor);
            }
        }
        return freshAdvisors;
    }
    
    private void addGlobalAdvisor(final ListableBeanFactory beanFactory, final String prefix) {
        final String[] globalAdvisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, Advisor.class);
        final String[] globalInterceptorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, Interceptor.class);
        final List<Object> beans = new ArrayList<Object>(globalAdvisorNames.length + globalInterceptorNames.length);
        final Map<Object, String> names = new HashMap<Object, String>(beans.size());
        for (final String name : globalAdvisorNames) {
            final Object bean = beanFactory.getBean(name);
            beans.add(bean);
            names.put(bean, name);
        }
        for (final String name : globalInterceptorNames) {
            final Object bean = beanFactory.getBean(name);
            beans.add(bean);
            names.put(bean, name);
        }
        OrderComparator.sort(beans);
        for (final Object bean2 : beans) {
            final String name2 = names.get(bean2);
            if (name2.startsWith(prefix)) {
                this.addAdvisorOnChainCreation(bean2, name2);
            }
        }
    }
    
    private void addAdvisorOnChainCreation(final Object next, final String name) {
        final Advisor advisor = this.namedBeanToAdvisor(next);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Adding advisor with name '" + name + "'");
        }
        this.addAdvisor(advisor);
    }
    
    private TargetSource freshTargetSource() {
        if (this.targetName == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Not refreshing target: Bean name not specified in 'interceptorNames'.");
            }
            return this.targetSource;
        }
        if (this.beanFactory == null) {
            throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve target with name '" + this.targetName + "'");
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Refreshing target with name '" + this.targetName + "'");
        }
        final Object target = this.beanFactory.getBean(this.targetName);
        return (target instanceof TargetSource) ? ((TargetSource)target) : new SingletonTargetSource(target);
    }
    
    private Advisor namedBeanToAdvisor(final Object next) {
        try {
            return this.advisorAdapterRegistry.wrap(next);
        }
        catch (UnknownAdviceTypeException ex) {
            throw new AopConfigException("Unknown advisor type " + next.getClass() + "; Can only include Advisor or Advice type beans in interceptorNames chain except for last entry," + "which may also be target or TargetSource", ex);
        }
    }
    
    @Override
    protected void adviceChanged() {
        super.adviceChanged();
        if (this.singleton) {
            this.logger.debug("Advice has changed; recaching singleton instance");
            synchronized (this) {
                this.singletonInstance = null;
            }
        }
    }
    
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.proxyClassLoader = ClassUtils.getDefaultClassLoader();
    }
    
    private static class PrototypePlaceholderAdvisor implements Advisor, Serializable
    {
        private final String beanName;
        private final String message;
        
        public PrototypePlaceholderAdvisor(final String beanName) {
            this.beanName = beanName;
            this.message = "Placeholder for prototype Advisor/Advice with bean name '" + beanName + "'";
        }
        
        public String getBeanName() {
            return this.beanName;
        }
        
        @Override
        public Advice getAdvice() {
            throw new UnsupportedOperationException("Cannot invoke methods: " + this.message);
        }
        
        @Override
        public boolean isPerInstance() {
            throw new UnsupportedOperationException("Cannot invoke methods: " + this.message);
        }
        
        @Override
        public String toString() {
            return this.message;
        }
    }
}
