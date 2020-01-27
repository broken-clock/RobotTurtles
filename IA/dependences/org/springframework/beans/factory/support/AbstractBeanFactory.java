// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.util.ObjectUtils;
import java.security.PrivilegedActionException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import java.security.PrivilegedExceptionAction;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.BeanWrapper;
import java.util.HashSet;
import java.security.AccessControlContext;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import java.util.Iterator;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import java.util.Collection;
import java.util.Arrays;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanDefinition;
import java.security.AccessController;
import org.springframework.beans.factory.SmartFactoryBean;
import java.security.PrivilegedAction;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.BeansException;
import org.springframework.core.NamedThreadLocal;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.StringValueResolver;
import java.util.List;
import java.beans.PropertyEditor;
import java.util.Map;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.PropertyEditorRegistrar;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory
{
    private BeanFactory parentBeanFactory;
    private ClassLoader beanClassLoader;
    private ClassLoader tempClassLoader;
    private boolean cacheBeanMetadata;
    private BeanExpressionResolver beanExpressionResolver;
    private ConversionService conversionService;
    private final Set<PropertyEditorRegistrar> propertyEditorRegistrars;
    private TypeConverter typeConverter;
    private final Map<Class<?>, Class<? extends PropertyEditor>> customEditors;
    private final List<StringValueResolver> embeddedValueResolvers;
    private final List<BeanPostProcessor> beanPostProcessors;
    private boolean hasInstantiationAwareBeanPostProcessors;
    private boolean hasDestructionAwareBeanPostProcessors;
    private final Map<String, Scope> scopes;
    private SecurityContextProvider securityContextProvider;
    private final Map<String, RootBeanDefinition> mergedBeanDefinitions;
    private final Set<String> alreadyCreated;
    private final ThreadLocal<Object> prototypesCurrentlyInCreation;
    
    public AbstractBeanFactory() {
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        this.cacheBeanMetadata = true;
        this.propertyEditorRegistrars = new LinkedHashSet<PropertyEditorRegistrar>(4);
        this.customEditors = new HashMap<Class<?>, Class<? extends PropertyEditor>>(4);
        this.embeddedValueResolvers = new LinkedList<StringValueResolver>();
        this.beanPostProcessors = new ArrayList<BeanPostProcessor>();
        this.scopes = new HashMap<String, Scope>(8);
        this.mergedBeanDefinitions = new ConcurrentHashMap<String, RootBeanDefinition>(64);
        this.alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(64));
        this.prototypesCurrentlyInCreation = new NamedThreadLocal<Object>("Prototype beans currently in creation");
    }
    
    public AbstractBeanFactory(final BeanFactory parentBeanFactory) {
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        this.cacheBeanMetadata = true;
        this.propertyEditorRegistrars = new LinkedHashSet<PropertyEditorRegistrar>(4);
        this.customEditors = new HashMap<Class<?>, Class<? extends PropertyEditor>>(4);
        this.embeddedValueResolvers = new LinkedList<StringValueResolver>();
        this.beanPostProcessors = new ArrayList<BeanPostProcessor>();
        this.scopes = new HashMap<String, Scope>(8);
        this.mergedBeanDefinitions = new ConcurrentHashMap<String, RootBeanDefinition>(64);
        this.alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(64));
        this.prototypesCurrentlyInCreation = new NamedThreadLocal<Object>("Prototype beans currently in creation");
        this.parentBeanFactory = parentBeanFactory;
    }
    
    @Override
    public Object getBean(final String name) throws BeansException {
        return this.doGetBean(name, (Class<Object>)null, null, false);
    }
    
    @Override
    public <T> T getBean(final String name, final Class<T> requiredType) throws BeansException {
        return this.doGetBean(name, requiredType, null, false);
    }
    
    @Override
    public Object getBean(final String name, final Object... args) throws BeansException {
        return this.doGetBean(name, (Class<Object>)null, args, false);
    }
    
    public <T> T getBean(final String name, final Class<T> requiredType, final Object... args) throws BeansException {
        return this.doGetBean(name, requiredType, args, false);
    }
    
    protected <T> T doGetBean(final String name, final Class<T> requiredType, final Object[] args, final boolean typeCheckOnly) throws BeansException {
        final String beanName = this.transformedBeanName(name);
        Object sharedInstance = this.getSingleton(beanName);
        Object bean;
        if (sharedInstance != null && args == null) {
            if (this.logger.isDebugEnabled()) {
                if (this.isSingletonCurrentlyInCreation(beanName)) {
                    this.logger.debug("Returning eagerly cached instance of singleton bean '" + beanName + "' that is not fully initialized yet - a consequence of a circular reference");
                }
                else {
                    this.logger.debug("Returning cached instance of singleton bean '" + beanName + "'");
                }
            }
            bean = this.getObjectForBeanInstance(sharedInstance, name, beanName, null);
        }
        else {
            if (this.isPrototypeCurrentlyInCreation(beanName)) {
                throw new BeanCurrentlyInCreationException(beanName);
            }
            final BeanFactory parentBeanFactory = this.getParentBeanFactory();
            if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
                final String nameToLookup = this.originalBeanName(name);
                if (args != null) {
                    return (T)parentBeanFactory.getBean(nameToLookup, args);
                }
                return parentBeanFactory.getBean(nameToLookup, requiredType);
            }
            else {
                if (!typeCheckOnly) {
                    this.markBeanAsCreated(beanName);
                }
                try {
                    final RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                    this.checkMergedBeanDefinition(mbd, beanName, args);
                    final String[] dependsOn = mbd.getDependsOn();
                    if (dependsOn != null) {
                        for (final String dependsOnBean : dependsOn) {
                            if (this.isDependent(beanName, dependsOnBean)) {
                                throw new BeanCreationException("Circular depends-on relationship between '" + beanName + "' and '" + dependsOnBean + "'");
                            }
                            this.registerDependentBean(dependsOnBean, beanName);
                            this.getBean(dependsOnBean);
                        }
                    }
                    if (mbd.isSingleton()) {
                        sharedInstance = this.getSingleton(beanName, new ObjectFactory<Object>() {
                            @Override
                            public Object getObject() throws BeansException {
                                try {
                                    return AbstractBeanFactory.this.createBean(beanName, mbd, args);
                                }
                                catch (BeansException ex) {
                                    AbstractBeanFactory.this.destroySingleton(beanName);
                                    throw ex;
                                }
                            }
                        });
                        bean = this.getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
                    }
                    else if (mbd.isPrototype()) {
                        Object prototypeInstance = null;
                        try {
                            this.beforePrototypeCreation(beanName);
                            prototypeInstance = this.createBean(beanName, mbd, args);
                        }
                        finally {
                            this.afterPrototypeCreation(beanName);
                        }
                        bean = this.getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
                    }
                    else {
                        final String scopeName = mbd.getScope();
                        final Scope scope = this.scopes.get(scopeName);
                        if (scope == null) {
                            throw new IllegalStateException("No Scope registered for scope '" + scopeName + "'");
                        }
                        try {
                            final Object scopedInstance = scope.get(beanName, new ObjectFactory<Object>() {
                                @Override
                                public Object getObject() throws BeansException {
                                    AbstractBeanFactory.this.beforePrototypeCreation(beanName);
                                    try {
                                        return AbstractBeanFactory.this.createBean(beanName, mbd, args);
                                    }
                                    finally {
                                        AbstractBeanFactory.this.afterPrototypeCreation(beanName);
                                    }
                                }
                            });
                            bean = this.getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
                        }
                        catch (IllegalStateException ex) {
                            throw new BeanCreationException(beanName, "Scope '" + scopeName + "' is not active for the current thread; " + "consider defining a scoped proxy for this bean if you intend to refer to it from a singleton", ex);
                        }
                    }
                }
                catch (BeansException ex2) {
                    this.cleanupAfterBeanCreationFailure(beanName);
                    throw ex2;
                }
            }
        }
        if (requiredType != null && bean != null && !requiredType.isAssignableFrom(bean.getClass())) {
            try {
                return this.getTypeConverter().convertIfNecessary(bean, requiredType);
            }
            catch (TypeMismatchException ex3) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Failed to convert bean '" + name + "' to required type [" + ClassUtils.getQualifiedName(requiredType) + "]", ex3);
                }
                throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
            }
        }
        return (T)bean;
    }
    
    @Override
    public boolean containsBean(final String name) {
        final String beanName = this.transformedBeanName(name);
        if (this.containsSingleton(beanName) || this.containsBeanDefinition(beanName)) {
            return !BeanFactoryUtils.isFactoryDereference(name) || this.isFactoryBean(name);
        }
        final BeanFactory parentBeanFactory = this.getParentBeanFactory();
        return parentBeanFactory != null && parentBeanFactory.containsBean(this.originalBeanName(name));
    }
    
    @Override
    public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
        final String beanName = this.transformedBeanName(name);
        final Object beanInstance = this.getSingleton(beanName, false);
        if (beanInstance != null) {
            if (beanInstance instanceof FactoryBean) {
                return BeanFactoryUtils.isFactoryDereference(name) || ((FactoryBean)beanInstance).isSingleton();
            }
            return !BeanFactoryUtils.isFactoryDereference(name);
        }
        else {
            if (this.containsSingleton(beanName)) {
                return true;
            }
            final BeanFactory parentBeanFactory = this.getParentBeanFactory();
            if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
                return parentBeanFactory.isSingleton(this.originalBeanName(name));
            }
            final RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
            if (!mbd.isSingleton()) {
                return false;
            }
            if (!this.isFactoryBean(beanName, mbd)) {
                return !BeanFactoryUtils.isFactoryDereference(name);
            }
            if (BeanFactoryUtils.isFactoryDereference(name)) {
                return true;
            }
            final FactoryBean<?> factoryBean = (FactoryBean<?>)this.getBean("&" + beanName);
            return factoryBean.isSingleton();
        }
    }
    
    @Override
    public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException {
        final String beanName = this.transformedBeanName(name);
        final BeanFactory parentBeanFactory = this.getParentBeanFactory();
        if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
            return parentBeanFactory.isPrototype(this.originalBeanName(name));
        }
        final RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
        if (mbd.isPrototype()) {
            return !BeanFactoryUtils.isFactoryDereference(name) || this.isFactoryBean(beanName, mbd);
        }
        if (BeanFactoryUtils.isFactoryDereference(name)) {
            return false;
        }
        if (!this.isFactoryBean(beanName, mbd)) {
            return false;
        }
        final FactoryBean<?> factoryBean = (FactoryBean<?>)this.getBean("&" + beanName);
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run() {
                    return (factoryBean instanceof SmartFactoryBean && ((SmartFactoryBean)factoryBean).isPrototype()) || !factoryBean.isSingleton();
                }
            }, this.getAccessControlContext());
        }
        return (factoryBean instanceof SmartFactoryBean && ((SmartFactoryBean)factoryBean).isPrototype()) || !factoryBean.isSingleton();
    }
    
    @Override
    public boolean isTypeMatch(final String name, final Class<?> targetType) throws NoSuchBeanDefinitionException {
        final String beanName = this.transformedBeanName(name);
        final Class<?> typeToMatch = (targetType != null) ? targetType : Object.class;
        final Object beanInstance = this.getSingleton(beanName, false);
        if (beanInstance != null) {
            if (!(beanInstance instanceof FactoryBean)) {
                return !BeanFactoryUtils.isFactoryDereference(name) && ClassUtils.isAssignableValue(typeToMatch, beanInstance);
            }
            if (!BeanFactoryUtils.isFactoryDereference(name)) {
                final Class<?> type = this.getTypeForFactoryBean((FactoryBean<?>)beanInstance);
                return type != null && ClassUtils.isAssignable(typeToMatch, type);
            }
            return ClassUtils.isAssignableValue(typeToMatch, beanInstance);
        }
        else {
            if (this.containsSingleton(beanName) && !this.containsBeanDefinition(beanName)) {
                return false;
            }
            final BeanFactory parentBeanFactory = this.getParentBeanFactory();
            if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
                return parentBeanFactory.isTypeMatch(this.originalBeanName(name), targetType);
            }
            final RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
            final Class<?>[] typesToMatch = (Class<?>[])(FactoryBean.class.equals(typeToMatch) ? new Class[] { typeToMatch } : new Class[] { FactoryBean.class, typeToMatch });
            final BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
            if (dbd != null && !BeanFactoryUtils.isFactoryDereference(name)) {
                final RootBeanDefinition tbd = this.getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
                final Class<?> targetClass = this.predictBeanType(dbd.getBeanName(), tbd, typesToMatch);
                if (targetClass != null && !FactoryBean.class.isAssignableFrom(targetClass)) {
                    return typeToMatch.isAssignableFrom(targetClass);
                }
            }
            Class<?> beanType = this.predictBeanType(beanName, mbd, typesToMatch);
            if (beanType == null) {
                return false;
            }
            if (FactoryBean.class.isAssignableFrom(beanType)) {
                if (!BeanFactoryUtils.isFactoryDereference(name)) {
                    beanType = this.getTypeForFactoryBean(beanName, mbd);
                    if (beanType == null) {
                        return false;
                    }
                }
            }
            else if (BeanFactoryUtils.isFactoryDereference(name)) {
                beanType = this.predictBeanType(beanName, mbd, FactoryBean.class);
                if (beanType == null || !FactoryBean.class.isAssignableFrom(beanType)) {
                    return false;
                }
            }
            return typeToMatch.isAssignableFrom(beanType);
        }
    }
    
    @Override
    public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
        final String beanName = this.transformedBeanName(name);
        final Object beanInstance = this.getSingleton(beanName, false);
        if (beanInstance != null) {
            if (beanInstance instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name)) {
                return this.getTypeForFactoryBean((FactoryBean<?>)beanInstance);
            }
            return beanInstance.getClass();
        }
        else {
            if (this.containsSingleton(beanName) && !this.containsBeanDefinition(beanName)) {
                return null;
            }
            final BeanFactory parentBeanFactory = this.getParentBeanFactory();
            if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
                return parentBeanFactory.getType(this.originalBeanName(name));
            }
            final RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
            final BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
            if (dbd != null && !BeanFactoryUtils.isFactoryDereference(name)) {
                final RootBeanDefinition tbd = this.getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
                final Class<?> targetClass = this.predictBeanType(dbd.getBeanName(), tbd, (Class<?>[])new Class[0]);
                if (targetClass != null && !FactoryBean.class.isAssignableFrom(targetClass)) {
                    return targetClass;
                }
            }
            final Class<?> beanClass = this.predictBeanType(beanName, mbd, (Class<?>[])new Class[0]);
            if (beanClass == null || !FactoryBean.class.isAssignableFrom(beanClass)) {
                return BeanFactoryUtils.isFactoryDereference(name) ? null : beanClass;
            }
            if (!BeanFactoryUtils.isFactoryDereference(name)) {
                return this.getTypeForFactoryBean(beanName, mbd);
            }
            return beanClass;
        }
    }
    
    @Override
    public String[] getAliases(final String name) {
        final String beanName = this.transformedBeanName(name);
        final List<String> aliases = new ArrayList<String>();
        final boolean factoryPrefix = name.startsWith("&");
        String fullBeanName = beanName;
        if (factoryPrefix) {
            fullBeanName = "&" + beanName;
        }
        if (!fullBeanName.equals(name)) {
            aliases.add(fullBeanName);
        }
        final String[] aliases2;
        final String[] retrievedAliases = aliases2 = super.getAliases(beanName);
        for (final String retrievedAlias : aliases2) {
            final String alias = (factoryPrefix ? "&" : "") + retrievedAlias;
            if (!alias.equals(name)) {
                aliases.add(alias);
            }
        }
        if (!this.containsSingleton(beanName) && !this.containsBeanDefinition(beanName)) {
            final BeanFactory parentBeanFactory = this.getParentBeanFactory();
            if (parentBeanFactory != null) {
                aliases.addAll(Arrays.asList(parentBeanFactory.getAliases(fullBeanName)));
            }
        }
        return StringUtils.toStringArray(aliases);
    }
    
    @Override
    public BeanFactory getParentBeanFactory() {
        return this.parentBeanFactory;
    }
    
    @Override
    public boolean containsLocalBean(final String name) {
        final String beanName = this.transformedBeanName(name);
        return (this.containsSingleton(beanName) || this.containsBeanDefinition(beanName)) && (!BeanFactoryUtils.isFactoryDereference(name) || this.isFactoryBean(beanName));
    }
    
    @Override
    public void setParentBeanFactory(final BeanFactory parentBeanFactory) {
        if (this.parentBeanFactory != null && this.parentBeanFactory != parentBeanFactory) {
            throw new IllegalStateException("Already associated with parent BeanFactory: " + this.parentBeanFactory);
        }
        this.parentBeanFactory = parentBeanFactory;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader beanClassLoader) {
        this.beanClassLoader = ((beanClassLoader != null) ? beanClassLoader : ClassUtils.getDefaultClassLoader());
    }
    
    @Override
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }
    
    @Override
    public void setTempClassLoader(final ClassLoader tempClassLoader) {
        this.tempClassLoader = tempClassLoader;
    }
    
    @Override
    public ClassLoader getTempClassLoader() {
        return this.tempClassLoader;
    }
    
    @Override
    public void setCacheBeanMetadata(final boolean cacheBeanMetadata) {
        this.cacheBeanMetadata = cacheBeanMetadata;
    }
    
    @Override
    public boolean isCacheBeanMetadata() {
        return this.cacheBeanMetadata;
    }
    
    @Override
    public void setBeanExpressionResolver(final BeanExpressionResolver resolver) {
        this.beanExpressionResolver = resolver;
    }
    
    @Override
    public BeanExpressionResolver getBeanExpressionResolver() {
        return this.beanExpressionResolver;
    }
    
    @Override
    public void setConversionService(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public ConversionService getConversionService() {
        return this.conversionService;
    }
    
    @Override
    public void addPropertyEditorRegistrar(final PropertyEditorRegistrar registrar) {
        Assert.notNull(registrar, "PropertyEditorRegistrar must not be null");
        this.propertyEditorRegistrars.add(registrar);
    }
    
    public Set<PropertyEditorRegistrar> getPropertyEditorRegistrars() {
        return this.propertyEditorRegistrars;
    }
    
    @Override
    public void registerCustomEditor(final Class<?> requiredType, final Class<? extends PropertyEditor> propertyEditorClass) {
        Assert.notNull(requiredType, "Required type must not be null");
        Assert.isAssignable(PropertyEditor.class, propertyEditorClass);
        this.customEditors.put(requiredType, propertyEditorClass);
    }
    
    @Override
    public void copyRegisteredEditorsTo(final PropertyEditorRegistry registry) {
        this.registerCustomEditors(registry);
    }
    
    public Map<Class<?>, Class<? extends PropertyEditor>> getCustomEditors() {
        return this.customEditors;
    }
    
    @Override
    public void setTypeConverter(final TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }
    
    protected TypeConverter getCustomTypeConverter() {
        return this.typeConverter;
    }
    
    @Override
    public TypeConverter getTypeConverter() {
        final TypeConverter customConverter = this.getCustomTypeConverter();
        if (customConverter != null) {
            return customConverter;
        }
        final SimpleTypeConverter typeConverter = new SimpleTypeConverter();
        typeConverter.setConversionService(this.getConversionService());
        this.registerCustomEditors(typeConverter);
        return typeConverter;
    }
    
    @Override
    public void addEmbeddedValueResolver(final StringValueResolver valueResolver) {
        Assert.notNull(valueResolver, "StringValueResolver must not be null");
        this.embeddedValueResolvers.add(valueResolver);
    }
    
    @Override
    public String resolveEmbeddedValue(final String value) {
        String result = value;
        for (final StringValueResolver resolver : this.embeddedValueResolvers) {
            if (result == null) {
                return null;
            }
            result = resolver.resolveStringValue(result);
        }
        return result;
    }
    
    @Override
    public void addBeanPostProcessor(final BeanPostProcessor beanPostProcessor) {
        Assert.notNull(beanPostProcessor, "BeanPostProcessor must not be null");
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
        if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
            this.hasInstantiationAwareBeanPostProcessors = true;
        }
        if (beanPostProcessor instanceof DestructionAwareBeanPostProcessor) {
            this.hasDestructionAwareBeanPostProcessors = true;
        }
    }
    
    @Override
    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }
    
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }
    
    protected boolean hasInstantiationAwareBeanPostProcessors() {
        return this.hasInstantiationAwareBeanPostProcessors;
    }
    
    protected boolean hasDestructionAwareBeanPostProcessors() {
        return this.hasDestructionAwareBeanPostProcessors;
    }
    
    @Override
    public void registerScope(final String scopeName, final Scope scope) {
        Assert.notNull(scopeName, "Scope identifier must not be null");
        Assert.notNull(scope, "Scope must not be null");
        if ("singleton".equals(scopeName) || "prototype".equals(scopeName)) {
            throw new IllegalArgumentException("Cannot replace existing scopes 'singleton' and 'prototype'");
        }
        this.scopes.put(scopeName, scope);
    }
    
    @Override
    public String[] getRegisteredScopeNames() {
        return StringUtils.toStringArray(this.scopes.keySet());
    }
    
    @Override
    public Scope getRegisteredScope(final String scopeName) {
        Assert.notNull(scopeName, "Scope identifier must not be null");
        return this.scopes.get(scopeName);
    }
    
    public void setSecurityContextProvider(final SecurityContextProvider securityProvider) {
        this.securityContextProvider = securityProvider;
    }
    
    @Override
    public AccessControlContext getAccessControlContext() {
        return (this.securityContextProvider != null) ? this.securityContextProvider.getAccessControlContext() : AccessController.getContext();
    }
    
    @Override
    public void copyConfigurationFrom(final ConfigurableBeanFactory otherFactory) {
        Assert.notNull(otherFactory, "BeanFactory must not be null");
        this.setBeanClassLoader(otherFactory.getBeanClassLoader());
        this.setCacheBeanMetadata(otherFactory.isCacheBeanMetadata());
        this.setBeanExpressionResolver(otherFactory.getBeanExpressionResolver());
        if (otherFactory instanceof AbstractBeanFactory) {
            final AbstractBeanFactory otherAbstractFactory = (AbstractBeanFactory)otherFactory;
            this.customEditors.putAll(otherAbstractFactory.customEditors);
            this.propertyEditorRegistrars.addAll(otherAbstractFactory.propertyEditorRegistrars);
            this.beanPostProcessors.addAll(otherAbstractFactory.beanPostProcessors);
            this.hasInstantiationAwareBeanPostProcessors = (this.hasInstantiationAwareBeanPostProcessors || otherAbstractFactory.hasInstantiationAwareBeanPostProcessors);
            this.hasDestructionAwareBeanPostProcessors = (this.hasDestructionAwareBeanPostProcessors || otherAbstractFactory.hasDestructionAwareBeanPostProcessors);
            this.scopes.putAll(otherAbstractFactory.scopes);
            this.securityContextProvider = otherAbstractFactory.securityContextProvider;
        }
        else {
            this.setTypeConverter(otherFactory.getTypeConverter());
        }
    }
    
    @Override
    public BeanDefinition getMergedBeanDefinition(final String name) throws BeansException {
        final String beanName = this.transformedBeanName(name);
        if (!this.containsBeanDefinition(beanName) && this.getParentBeanFactory() instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)this.getParentBeanFactory()).getMergedBeanDefinition(beanName);
        }
        return this.getMergedLocalBeanDefinition(beanName);
    }
    
    @Override
    public boolean isFactoryBean(final String name) throws NoSuchBeanDefinitionException {
        final String beanName = this.transformedBeanName(name);
        final Object beanInstance = this.getSingleton(beanName, false);
        if (beanInstance != null) {
            return beanInstance instanceof FactoryBean;
        }
        if (this.containsSingleton(beanName)) {
            return false;
        }
        if (!this.containsBeanDefinition(beanName) && this.getParentBeanFactory() instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)this.getParentBeanFactory()).isFactoryBean(name);
        }
        return this.isFactoryBean(beanName, this.getMergedLocalBeanDefinition(beanName));
    }
    
    public boolean isActuallyInCreation(final String beanName) {
        return this.isSingletonCurrentlyInCreation(beanName) || this.isPrototypeCurrentlyInCreation(beanName);
    }
    
    protected boolean isPrototypeCurrentlyInCreation(final String beanName) {
        final Object curVal = this.prototypesCurrentlyInCreation.get();
        return curVal != null && (curVal.equals(beanName) || (curVal instanceof Set && ((Set)curVal).contains(beanName)));
    }
    
    protected void beforePrototypeCreation(final String beanName) {
        final Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal == null) {
            this.prototypesCurrentlyInCreation.set(beanName);
        }
        else if (curVal instanceof String) {
            final Set<String> beanNameSet = new HashSet<String>(2);
            beanNameSet.add((String)curVal);
            beanNameSet.add(beanName);
            this.prototypesCurrentlyInCreation.set(beanNameSet);
        }
        else {
            final Set<String> beanNameSet = (Set<String>)curVal;
            beanNameSet.add(beanName);
        }
    }
    
    protected void afterPrototypeCreation(final String beanName) {
        final Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal instanceof String) {
            this.prototypesCurrentlyInCreation.remove();
        }
        else if (curVal instanceof Set) {
            final Set<String> beanNameSet = (Set<String>)curVal;
            beanNameSet.remove(beanName);
            if (beanNameSet.isEmpty()) {
                this.prototypesCurrentlyInCreation.remove();
            }
        }
    }
    
    @Override
    public void destroyBean(final String beanName, final Object beanInstance) {
        this.destroyBean(beanName, beanInstance, this.getMergedLocalBeanDefinition(beanName));
    }
    
    protected void destroyBean(final String beanName, final Object beanInstance, final RootBeanDefinition mbd) {
        new DisposableBeanAdapter(beanInstance, beanName, mbd, this.getBeanPostProcessors(), this.getAccessControlContext()).destroy();
    }
    
    @Override
    public void destroyScopedBean(final String beanName) {
        final RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
        if (mbd.isSingleton() || mbd.isPrototype()) {
            throw new IllegalArgumentException("Bean name '" + beanName + "' does not correspond to an object in a mutable scope");
        }
        final String scopeName = mbd.getScope();
        final Scope scope = this.scopes.get(scopeName);
        if (scope == null) {
            throw new IllegalStateException("No Scope SPI registered for scope '" + scopeName + "'");
        }
        final Object bean = scope.remove(beanName);
        if (bean != null) {
            this.destroyBean(beanName, bean, mbd);
        }
    }
    
    protected String transformedBeanName(final String name) {
        return this.canonicalName(BeanFactoryUtils.transformedBeanName(name));
    }
    
    protected String originalBeanName(final String name) {
        String beanName = this.transformedBeanName(name);
        if (name.startsWith("&")) {
            beanName = "&" + beanName;
        }
        return beanName;
    }
    
    protected void initBeanWrapper(final BeanWrapper bw) {
        bw.setConversionService(this.getConversionService());
        this.registerCustomEditors(bw);
    }
    
    protected void registerCustomEditors(final PropertyEditorRegistry registry) {
        final PropertyEditorRegistrySupport registrySupport = (registry instanceof PropertyEditorRegistrySupport) ? ((PropertyEditorRegistrySupport)registry) : null;
        if (registrySupport != null) {
            registrySupport.useConfigValueEditors();
        }
        if (!this.propertyEditorRegistrars.isEmpty()) {
            for (final PropertyEditorRegistrar registrar : this.propertyEditorRegistrars) {
                try {
                    registrar.registerCustomEditors(registry);
                }
                catch (BeanCreationException ex) {
                    final Throwable rootCause = ex.getMostSpecificCause();
                    if (rootCause instanceof BeanCurrentlyInCreationException) {
                        final BeanCreationException bce = (BeanCreationException)rootCause;
                        if (this.isCurrentlyInCreation(bce.getBeanName())) {
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("PropertyEditorRegistrar [" + registrar.getClass().getName() + "] failed because it tried to obtain currently created bean '" + ex.getBeanName() + "': " + ex.getMessage());
                            }
                            this.onSuppressedException(ex);
                            continue;
                        }
                    }
                    throw ex;
                }
            }
        }
        if (!this.customEditors.isEmpty()) {
            for (final Map.Entry<Class<?>, Class<? extends PropertyEditor>> entry : this.customEditors.entrySet()) {
                final Class<?> requiredType = entry.getKey();
                final Class<? extends PropertyEditor> editorClass = entry.getValue();
                registry.registerCustomEditor(requiredType, BeanUtils.instantiateClass(editorClass));
            }
        }
    }
    
    protected RootBeanDefinition getMergedLocalBeanDefinition(final String beanName) throws BeansException {
        final RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
        if (mbd != null) {
            return mbd;
        }
        return this.getMergedBeanDefinition(beanName, this.getBeanDefinition(beanName));
    }
    
    protected RootBeanDefinition getMergedBeanDefinition(final String beanName, final BeanDefinition bd) throws BeanDefinitionStoreException {
        return this.getMergedBeanDefinition(beanName, bd, null);
    }
    
    protected RootBeanDefinition getMergedBeanDefinition(final String beanName, final BeanDefinition bd, final BeanDefinition containingBd) throws BeanDefinitionStoreException {
        synchronized (this.mergedBeanDefinitions) {
            RootBeanDefinition mbd = null;
            if (containingBd == null) {
                mbd = this.mergedBeanDefinitions.get(beanName);
            }
            if (mbd == null) {
                if (bd.getParentName() == null) {
                    if (bd instanceof RootBeanDefinition) {
                        mbd = ((RootBeanDefinition)bd).cloneBeanDefinition();
                    }
                    else {
                        mbd = new RootBeanDefinition(bd);
                    }
                }
                else {
                    BeanDefinition pbd;
                    try {
                        final String parentBeanName = this.transformedBeanName(bd.getParentName());
                        if (!beanName.equals(parentBeanName)) {
                            pbd = this.getMergedBeanDefinition(parentBeanName);
                        }
                        else {
                            if (!(this.getParentBeanFactory() instanceof ConfigurableBeanFactory)) {
                                throw new NoSuchBeanDefinitionException(bd.getParentName(), "Parent name '" + bd.getParentName() + "' is equal to bean name '" + beanName + "': cannot be resolved without an AbstractBeanFactory parent");
                            }
                            pbd = ((ConfigurableBeanFactory)this.getParentBeanFactory()).getMergedBeanDefinition(parentBeanName);
                        }
                    }
                    catch (NoSuchBeanDefinitionException ex) {
                        throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanName, "Could not resolve parent bean definition '" + bd.getParentName() + "'", ex);
                    }
                    mbd = new RootBeanDefinition(pbd);
                    mbd.overrideFrom(bd);
                }
                if (!StringUtils.hasLength(mbd.getScope())) {
                    mbd.setScope("singleton");
                }
                if (containingBd != null && !containingBd.isSingleton() && mbd.isSingleton()) {
                    mbd.setScope(containingBd.getScope());
                }
                if (containingBd == null && this.isCacheBeanMetadata() && this.isBeanEligibleForMetadataCaching(beanName)) {
                    this.mergedBeanDefinitions.put(beanName, mbd);
                }
            }
            return mbd;
        }
    }
    
    protected void checkMergedBeanDefinition(final RootBeanDefinition mbd, final String beanName, final Object[] args) throws BeanDefinitionStoreException {
        if (mbd.isAbstract()) {
            throw new BeanIsAbstractException(beanName);
        }
        if (args != null && !mbd.isPrototype()) {
            throw new BeanDefinitionStoreException("Can only specify arguments for the getBean method when referring to a prototype bean definition");
        }
    }
    
    protected void clearMergedBeanDefinition(final String beanName) {
        this.mergedBeanDefinitions.remove(beanName);
    }
    
    protected Class<?> resolveBeanClass(final RootBeanDefinition mbd, final String beanName, final Class<?>... typesToMatch) throws CannotLoadBeanClassException {
        try {
            if (mbd.hasBeanClass()) {
                return mbd.getBeanClass();
            }
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged((PrivilegedExceptionAction<Class<?>>)new PrivilegedExceptionAction<Class<?>>() {
                    @Override
                    public Class<?> run() throws Exception {
                        return AbstractBeanFactory.this.doResolveBeanClass(mbd, (Class<?>[])typesToMatch);
                    }
                }, this.getAccessControlContext());
            }
            return this.doResolveBeanClass(mbd, typesToMatch);
        }
        catch (PrivilegedActionException pae) {
            final ClassNotFoundException ex = (ClassNotFoundException)pae.getException();
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
        }
        catch (ClassNotFoundException ex2) {
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex2);
        }
        catch (LinkageError err) {
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), err);
        }
    }
    
    private Class<?> doResolveBeanClass(final RootBeanDefinition mbd, final Class<?>... typesToMatch) throws ClassNotFoundException {
        if (!ObjectUtils.isEmpty(typesToMatch)) {
            final ClassLoader tempClassLoader = this.getTempClassLoader();
            if (tempClassLoader != null) {
                if (tempClassLoader instanceof DecoratingClassLoader) {
                    final DecoratingClassLoader dcl = (DecoratingClassLoader)tempClassLoader;
                    for (final Class<?> typeToMatch : typesToMatch) {
                        dcl.excludeClass(typeToMatch.getName());
                    }
                }
                final String className = mbd.getBeanClassName();
                return (className != null) ? ClassUtils.forName(className, tempClassLoader) : null;
            }
        }
        return mbd.resolveBeanClass(this.getBeanClassLoader());
    }
    
    protected Object evaluateBeanDefinitionString(final String value, final BeanDefinition beanDefinition) {
        if (this.beanExpressionResolver == null) {
            return value;
        }
        final Scope scope = (beanDefinition != null) ? this.getRegisteredScope(beanDefinition.getScope()) : null;
        return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
    }
    
    protected Class<?> predictBeanType(final String beanName, final RootBeanDefinition mbd, final Class<?>... typesToMatch) {
        if (mbd.getFactoryMethodName() != null) {
            return null;
        }
        return this.resolveBeanClass(mbd, beanName, typesToMatch);
    }
    
    protected boolean isFactoryBean(final String beanName, final RootBeanDefinition mbd) {
        final Class<?> beanType = this.predictBeanType(beanName, mbd, FactoryBean.class);
        return beanType != null && FactoryBean.class.isAssignableFrom(beanType);
    }
    
    protected Class<?> getTypeForFactoryBean(final String beanName, final RootBeanDefinition mbd) {
        if (!mbd.isSingleton()) {
            return null;
        }
        try {
            final FactoryBean<?> factoryBean = this.doGetBean("&" + beanName, (Class<FactoryBean<?>>)FactoryBean.class, null, true);
            return this.getTypeForFactoryBean(factoryBean);
        }
        catch (BeanCreationException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Ignoring bean creation exception on FactoryBean type check: " + ex);
            }
            this.onSuppressedException(ex);
            return null;
        }
    }
    
    protected void markBeanAsCreated(final String beanName) {
        this.alreadyCreated.add(beanName);
    }
    
    protected void cleanupAfterBeanCreationFailure(final String beanName) {
        this.alreadyCreated.remove(beanName);
    }
    
    protected boolean isBeanEligibleForMetadataCaching(final String beanName) {
        return this.alreadyCreated.contains(beanName);
    }
    
    protected boolean removeSingletonIfCreatedForTypeCheckOnly(final String beanName) {
        if (!this.alreadyCreated.contains(beanName)) {
            this.removeSingleton(beanName);
            return true;
        }
        return false;
    }
    
    protected Object getObjectForBeanInstance(final Object beanInstance, final String name, final String beanName, RootBeanDefinition mbd) {
        if (BeanFactoryUtils.isFactoryDereference(name) && !(beanInstance instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(this.transformedBeanName(name), beanInstance.getClass());
        }
        if (!(beanInstance instanceof FactoryBean) || BeanFactoryUtils.isFactoryDereference(name)) {
            return beanInstance;
        }
        Object object = null;
        if (mbd == null) {
            object = this.getCachedObjectForFactoryBean(beanName);
        }
        if (object == null) {
            final FactoryBean<?> factory = (FactoryBean<?>)beanInstance;
            if (mbd == null && this.containsBeanDefinition(beanName)) {
                mbd = this.getMergedLocalBeanDefinition(beanName);
            }
            final boolean synthetic = mbd != null && mbd.isSynthetic();
            object = this.getObjectFromFactoryBean(factory, beanName, !synthetic);
        }
        return object;
    }
    
    public boolean isBeanNameInUse(final String beanName) {
        return this.isAlias(beanName) || this.containsLocalBean(beanName) || this.hasDependentBean(beanName);
    }
    
    protected boolean requiresDestruction(final Object bean, final RootBeanDefinition mbd) {
        return bean != null && (DisposableBeanAdapter.hasDestroyMethod(bean, mbd) || this.hasDestructionAwareBeanPostProcessors());
    }
    
    protected void registerDisposableBeanIfNecessary(final String beanName, final Object bean, final RootBeanDefinition mbd) {
        final AccessControlContext acc = (System.getSecurityManager() != null) ? this.getAccessControlContext() : null;
        if (!mbd.isPrototype() && this.requiresDestruction(bean, mbd)) {
            if (mbd.isSingleton()) {
                this.registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, mbd, this.getBeanPostProcessors(), acc));
            }
            else {
                final Scope scope = this.scopes.get(mbd.getScope());
                if (scope == null) {
                    throw new IllegalStateException("No Scope registered for scope '" + mbd.getScope() + "'");
                }
                scope.registerDestructionCallback(beanName, new DisposableBeanAdapter(bean, beanName, mbd, this.getBeanPostProcessors(), acc));
            }
        }
    }
    
    protected abstract boolean containsBeanDefinition(final String p0);
    
    protected abstract BeanDefinition getBeanDefinition(final String p0) throws BeansException;
    
    protected abstract Object createBean(final String p0, final RootBeanDefinition p1, final Object[] p2) throws BeanCreationException;
}
