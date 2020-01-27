// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.support;

import org.springframework.core.Conventions;
import org.aopalliance.aop.Advice;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.cglib.core.Signature;
import org.springframework.asm.Type;
import org.springframework.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.proxy.InterfaceMaker;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.ObjectUtils;
import org.springframework.scripting.ScriptFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import java.util.Iterator;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.beans.factory.BeanFactory;
import java.util.HashMap;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.ClassUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.scripting.ScriptSource;
import java.util.Map;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.apache.commons.logging.Log;
import org.springframework.core.Ordered;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

public class ScriptFactoryPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements BeanClassLoaderAware, BeanFactoryAware, ResourceLoaderAware, DisposableBean, Ordered
{
    public static final String INLINE_SCRIPT_PREFIX = "inline:";
    public static final String REFRESH_CHECK_DELAY_ATTRIBUTE;
    public static final String PROXY_TARGET_CLASS_ATTRIBUTE;
    public static final String LANGUAGE_ATTRIBUTE;
    private static final String SCRIPT_FACTORY_NAME_PREFIX = "scriptFactory.";
    private static final String SCRIPTED_OBJECT_NAME_PREFIX = "scriptedObject.";
    protected final Log logger;
    private long defaultRefreshCheckDelay;
    private boolean defaultProxyTargetClass;
    private ClassLoader beanClassLoader;
    private ConfigurableBeanFactory beanFactory;
    private ResourceLoader resourceLoader;
    final DefaultListableBeanFactory scriptBeanFactory;
    private final Map<String, ScriptSource> scriptSourceCache;
    
    public ScriptFactoryPostProcessor() {
        this.logger = LogFactory.getLog(this.getClass());
        this.defaultRefreshCheckDelay = -1L;
        this.defaultProxyTargetClass = false;
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        this.resourceLoader = new DefaultResourceLoader();
        this.scriptBeanFactory = new DefaultListableBeanFactory();
        this.scriptSourceCache = new HashMap<String, ScriptSource>();
    }
    
    public void setDefaultRefreshCheckDelay(final long defaultRefreshCheckDelay) {
        this.defaultRefreshCheckDelay = defaultRefreshCheckDelay;
    }
    
    public void setDefaultProxyTargetClass(final boolean defaultProxyTargetClass) {
        this.defaultProxyTargetClass = defaultProxyTargetClass;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("ScriptFactoryPostProcessor doesn't work with a BeanFactory which does not implement ConfigurableBeanFactory: " + beanFactory.getClass());
        }
        this.beanFactory = (ConfigurableBeanFactory)beanFactory;
        this.scriptBeanFactory.setParentBeanFactory(this.beanFactory);
        this.scriptBeanFactory.copyConfigurationFrom(this.beanFactory);
        final Iterator<BeanPostProcessor> it = this.scriptBeanFactory.getBeanPostProcessors().iterator();
        while (it.hasNext()) {
            if (it.next() instanceof AopInfrastructureBean) {
                it.remove();
            }
        }
    }
    
    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
    
    @Override
    public Class<?> predictBeanType(final Class<?> beanClass, final String beanName) {
        if (!ScriptFactory.class.isAssignableFrom(beanClass)) {
            return null;
        }
        final BeanDefinition bd = this.beanFactory.getMergedBeanDefinition(beanName);
        try {
            final String scriptFactoryBeanName = "scriptFactory." + beanName;
            final String scriptedObjectBeanName = "scriptedObject." + beanName;
            this.prepareScriptBeans(bd, scriptFactoryBeanName, scriptedObjectBeanName);
            final ScriptFactory scriptFactory = this.scriptBeanFactory.getBean(scriptFactoryBeanName, ScriptFactory.class);
            final ScriptSource scriptSource = this.getScriptSource(scriptFactoryBeanName, scriptFactory.getScriptSourceLocator());
            final Class<?>[] interfaces = scriptFactory.getScriptInterfaces();
            final Class<?> scriptedType = scriptFactory.getScriptedObjectType(scriptSource);
            if (scriptedType != null) {
                return scriptedType;
            }
            if (!ObjectUtils.isEmpty(interfaces)) {
                return (interfaces.length == 1) ? interfaces[0] : this.createCompositeInterface(interfaces);
            }
            if (bd.isSingleton()) {
                final Object bean = this.scriptBeanFactory.getBean(scriptedObjectBeanName);
                if (bean != null) {
                    return bean.getClass();
                }
            }
        }
        catch (Exception ex) {
            if (ex instanceof BeanCreationException && ((BeanCreationException)ex).getMostSpecificCause() instanceof BeanCurrentlyInCreationException) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Could not determine scripted object type for bean '" + beanName + "': " + ex.getMessage());
                }
            }
            else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not determine scripted object type for bean '" + beanName + "'", ex);
            }
        }
        return null;
    }
    
    @Override
    public Object postProcessBeforeInstantiation(final Class<?> beanClass, final String beanName) {
        if (!ScriptFactory.class.isAssignableFrom(beanClass)) {
            return null;
        }
        final BeanDefinition bd = this.beanFactory.getMergedBeanDefinition(beanName);
        final String scriptFactoryBeanName = "scriptFactory." + beanName;
        String scriptedObjectBeanName = "scriptedObject." + beanName;
        this.prepareScriptBeans(bd, scriptFactoryBeanName, scriptedObjectBeanName);
        final ScriptFactory scriptFactory = this.scriptBeanFactory.getBean(scriptFactoryBeanName, ScriptFactory.class);
        final ScriptSource scriptSource = this.getScriptSource(scriptFactoryBeanName, scriptFactory.getScriptSourceLocator());
        boolean isFactoryBean = false;
        try {
            final Class<?> scriptedObjectType = scriptFactory.getScriptedObjectType(scriptSource);
            if (scriptedObjectType != null) {
                isFactoryBean = FactoryBean.class.isAssignableFrom(scriptedObjectType);
            }
        }
        catch (Exception ex) {
            throw new BeanCreationException(beanName, "Could not determine scripted object type for " + scriptFactory, ex);
        }
        final long refreshCheckDelay = this.resolveRefreshCheckDelay(bd);
        if (refreshCheckDelay < 0L) {
            if (isFactoryBean) {
                scriptedObjectBeanName = "&" + scriptedObjectBeanName;
            }
            return this.scriptBeanFactory.getBean(scriptedObjectBeanName);
        }
        final Class<?>[] interfaces = scriptFactory.getScriptInterfaces();
        final RefreshableScriptTargetSource ts = new RefreshableScriptTargetSource(this.scriptBeanFactory, scriptedObjectBeanName, scriptFactory, scriptSource, isFactoryBean);
        final boolean proxyTargetClass = this.resolveProxyTargetClass(bd);
        final String language = (String)bd.getAttribute(ScriptFactoryPostProcessor.LANGUAGE_ATTRIBUTE);
        if (proxyTargetClass && (language == null || !language.equals("groovy"))) {
            throw new BeanDefinitionValidationException("Cannot use proxyTargetClass=true with script beans where language is not 'groovy': '" + language + "'");
        }
        ts.setRefreshCheckDelay(refreshCheckDelay);
        return this.createRefreshableProxy(ts, interfaces, proxyTargetClass);
    }
    
    protected void prepareScriptBeans(final BeanDefinition bd, final String scriptFactoryBeanName, final String scriptedObjectBeanName) {
        synchronized (this.scriptBeanFactory) {
            if (!this.scriptBeanFactory.containsBeanDefinition(scriptedObjectBeanName)) {
                this.scriptBeanFactory.registerBeanDefinition(scriptFactoryBeanName, this.createScriptFactoryBeanDefinition(bd));
                final ScriptFactory scriptFactory = this.scriptBeanFactory.getBean(scriptFactoryBeanName, ScriptFactory.class);
                final ScriptSource scriptSource = this.getScriptSource(scriptFactoryBeanName, scriptFactory.getScriptSourceLocator());
                Class<?>[] scriptedInterfaces;
                final Class<?>[] interfaces = scriptedInterfaces = scriptFactory.getScriptInterfaces();
                if (scriptFactory.requiresConfigInterface() && !bd.getPropertyValues().isEmpty()) {
                    final Class<?> configInterface = this.createConfigInterface(bd, interfaces);
                    scriptedInterfaces = ObjectUtils.addObjectToArray(interfaces, configInterface);
                }
                final BeanDefinition objectBd = this.createScriptedObjectBeanDefinition(bd, scriptFactoryBeanName, scriptSource, scriptedInterfaces);
                final long refreshCheckDelay = this.resolveRefreshCheckDelay(bd);
                if (refreshCheckDelay >= 0L) {
                    objectBd.setScope("prototype");
                }
                this.scriptBeanFactory.registerBeanDefinition(scriptedObjectBeanName, objectBd);
            }
        }
    }
    
    protected long resolveRefreshCheckDelay(final BeanDefinition beanDefinition) {
        long refreshCheckDelay = this.defaultRefreshCheckDelay;
        final Object attributeValue = beanDefinition.getAttribute(ScriptFactoryPostProcessor.REFRESH_CHECK_DELAY_ATTRIBUTE);
        if (attributeValue instanceof Number) {
            refreshCheckDelay = ((Number)attributeValue).longValue();
        }
        else if (attributeValue instanceof String) {
            refreshCheckDelay = Long.parseLong((String)attributeValue);
        }
        else if (attributeValue != null) {
            throw new BeanDefinitionStoreException("Invalid refresh check delay attribute [" + ScriptFactoryPostProcessor.REFRESH_CHECK_DELAY_ATTRIBUTE + "] with value '" + attributeValue + "': needs to be of type Number or String");
        }
        return refreshCheckDelay;
    }
    
    protected boolean resolveProxyTargetClass(final BeanDefinition beanDefinition) {
        boolean proxyTargetClass = this.defaultProxyTargetClass;
        final Object attributeValue = beanDefinition.getAttribute(ScriptFactoryPostProcessor.PROXY_TARGET_CLASS_ATTRIBUTE);
        if (attributeValue instanceof Boolean) {
            proxyTargetClass = (boolean)attributeValue;
        }
        else if (attributeValue instanceof String) {
            proxyTargetClass = Boolean.valueOf((String)attributeValue);
        }
        else if (attributeValue != null) {
            throw new BeanDefinitionStoreException("Invalid proxy target class attribute [" + ScriptFactoryPostProcessor.PROXY_TARGET_CLASS_ATTRIBUTE + "] with value '" + attributeValue + "': needs to be of type Boolean or String");
        }
        return proxyTargetClass;
    }
    
    protected BeanDefinition createScriptFactoryBeanDefinition(final BeanDefinition bd) {
        final GenericBeanDefinition scriptBd = new GenericBeanDefinition();
        scriptBd.setBeanClassName(bd.getBeanClassName());
        scriptBd.getConstructorArgumentValues().addArgumentValues(bd.getConstructorArgumentValues());
        return scriptBd;
    }
    
    protected ScriptSource getScriptSource(final String beanName, final String scriptSourceLocator) {
        synchronized (this.scriptSourceCache) {
            ScriptSource scriptSource = this.scriptSourceCache.get(beanName);
            if (scriptSource == null) {
                scriptSource = this.convertToScriptSource(beanName, scriptSourceLocator, this.resourceLoader);
                this.scriptSourceCache.put(beanName, scriptSource);
            }
            return scriptSource;
        }
    }
    
    protected ScriptSource convertToScriptSource(final String beanName, final String scriptSourceLocator, final ResourceLoader resourceLoader) {
        if (scriptSourceLocator.startsWith("inline:")) {
            return new StaticScriptSource(scriptSourceLocator.substring("inline:".length()), beanName);
        }
        return new ResourceScriptSource(resourceLoader.getResource(scriptSourceLocator));
    }
    
    protected Class<?> createConfigInterface(final BeanDefinition bd, final Class<?>[] interfaces) {
        final InterfaceMaker maker = new InterfaceMaker();
        final PropertyValue[] propertyValues;
        final PropertyValue[] pvs = propertyValues = bd.getPropertyValues().getPropertyValues();
        for (final PropertyValue pv : propertyValues) {
            final String propertyName = pv.getName();
            final Class<?> propertyType = BeanUtils.findPropertyType(propertyName, interfaces);
            final String setterName = "set" + StringUtils.capitalize(propertyName);
            final Signature signature = new Signature(setterName, Type.VOID_TYPE, new Type[] { Type.getType(propertyType) });
            maker.add(signature, new Type[0]);
        }
        if (bd instanceof AbstractBeanDefinition) {
            final AbstractBeanDefinition abd = (AbstractBeanDefinition)bd;
            if (abd.getInitMethodName() != null) {
                final Signature signature2 = new Signature(abd.getInitMethodName(), Type.VOID_TYPE, new Type[0]);
                maker.add(signature2, new Type[0]);
            }
            if (abd.getDestroyMethodName() != null) {
                final Signature signature2 = new Signature(abd.getDestroyMethodName(), Type.VOID_TYPE, new Type[0]);
                maker.add(signature2, new Type[0]);
            }
        }
        return (Class<?>)maker.create();
    }
    
    protected Class<?> createCompositeInterface(final Class<?>[] interfaces) {
        return ClassUtils.createCompositeInterface(interfaces, this.beanClassLoader);
    }
    
    protected BeanDefinition createScriptedObjectBeanDefinition(final BeanDefinition bd, final String scriptFactoryBeanName, final ScriptSource scriptSource, final Class<?>[] interfaces) {
        final GenericBeanDefinition objectBd = new GenericBeanDefinition(bd);
        objectBd.setFactoryBeanName(scriptFactoryBeanName);
        objectBd.setFactoryMethodName("getScriptedObject");
        objectBd.getConstructorArgumentValues().clear();
        objectBd.getConstructorArgumentValues().addIndexedArgumentValue(0, scriptSource);
        objectBd.getConstructorArgumentValues().addIndexedArgumentValue(1, interfaces);
        return objectBd;
    }
    
    protected Object createRefreshableProxy(final TargetSource ts, Class<?>[] interfaces, final boolean proxyTargetClass) {
        final ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource(ts);
        ClassLoader classLoader = this.beanClassLoader;
        if (interfaces == null) {
            interfaces = ClassUtils.getAllInterfacesForClass(ts.getTargetClass(), this.beanClassLoader);
        }
        proxyFactory.setInterfaces(interfaces);
        if (proxyTargetClass) {
            classLoader = null;
            proxyFactory.setProxyTargetClass(proxyTargetClass);
        }
        final DelegatingIntroductionInterceptor introduction = new DelegatingIntroductionInterceptor(ts);
        introduction.suppressInterface(TargetSource.class);
        proxyFactory.addAdvice(introduction);
        return proxyFactory.getProxy(classLoader);
    }
    
    @Override
    public void destroy() {
        this.scriptBeanFactory.destroySingletons();
    }
    
    static {
        REFRESH_CHECK_DELAY_ATTRIBUTE = Conventions.getQualifiedAttributeName(ScriptFactoryPostProcessor.class, "refreshCheckDelay");
        PROXY_TARGET_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(ScriptFactoryPostProcessor.class, "proxyTargetClass");
        LANGUAGE_ATTRIBUTE = Conventions.getQualifiedAttributeName(ScriptFactoryPostProcessor.class, "language");
    }
}
