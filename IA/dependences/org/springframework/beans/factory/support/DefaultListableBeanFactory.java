// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import javax.inject.Provider;
import java.io.ObjectStreamException;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import java.util.Collections;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.TypeConverter;
import java.util.Set;
import java.util.Iterator;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.core.annotation.AnnotationUtils;
import java.lang.annotation.Annotation;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import java.util.Collection;
import org.springframework.util.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import java.util.Arrays;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;
import java.lang.ref.WeakReference;
import org.springframework.beans.factory.BeanFactory;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import java.util.Comparator;
import java.lang.ref.Reference;
import java.util.Map;
import java.io.Serializable;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable
{
    private static Class<?> javaxInjectProviderClass;
    private static final Map<String, Reference<DefaultListableBeanFactory>> serializableFactories;
    private String serializationId;
    private boolean allowBeanDefinitionOverriding;
    private boolean allowEagerClassLoading;
    private Comparator<Object> dependencyComparator;
    private AutowireCandidateResolver autowireCandidateResolver;
    private final Map<Class<?>, Object> resolvableDependencies;
    private final Map<String, BeanDefinition> beanDefinitionMap;
    private final Map<Class<?>, String[]> allBeanNamesByType;
    private final Map<Class<?>, String[]> singletonBeanNamesByType;
    private final List<String> beanDefinitionNames;
    private boolean configurationFrozen;
    private String[] frozenBeanDefinitionNames;
    
    public DefaultListableBeanFactory() {
        this.allowBeanDefinitionOverriding = true;
        this.allowEagerClassLoading = true;
        this.autowireCandidateResolver = new SimpleAutowireCandidateResolver();
        this.resolvableDependencies = new HashMap<Class<?>, Object>(16);
        this.beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(64);
        this.allBeanNamesByType = new ConcurrentHashMap<Class<?>, String[]>(64);
        this.singletonBeanNamesByType = new ConcurrentHashMap<Class<?>, String[]>(64);
        this.beanDefinitionNames = new ArrayList<String>();
        this.configurationFrozen = false;
    }
    
    public DefaultListableBeanFactory(final BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
        this.allowBeanDefinitionOverriding = true;
        this.allowEagerClassLoading = true;
        this.autowireCandidateResolver = new SimpleAutowireCandidateResolver();
        this.resolvableDependencies = new HashMap<Class<?>, Object>(16);
        this.beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(64);
        this.allBeanNamesByType = new ConcurrentHashMap<Class<?>, String[]>(64);
        this.singletonBeanNamesByType = new ConcurrentHashMap<Class<?>, String[]>(64);
        this.beanDefinitionNames = new ArrayList<String>();
        this.configurationFrozen = false;
    }
    
    public void setSerializationId(final String serializationId) {
        if (serializationId != null) {
            DefaultListableBeanFactory.serializableFactories.put(serializationId, new WeakReference<DefaultListableBeanFactory>(this));
        }
        else if (this.serializationId != null) {
            DefaultListableBeanFactory.serializableFactories.remove(this.serializationId);
        }
        this.serializationId = serializationId;
    }
    
    public void setAllowBeanDefinitionOverriding(final boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }
    
    public void setAllowEagerClassLoading(final boolean allowEagerClassLoading) {
        this.allowEagerClassLoading = allowEagerClassLoading;
    }
    
    public void setDependencyComparator(final Comparator<Object> dependencyComparator) {
        this.dependencyComparator = dependencyComparator;
    }
    
    public Comparator<Object> getDependencyComparator() {
        return this.dependencyComparator;
    }
    
    public void setAutowireCandidateResolver(final AutowireCandidateResolver autowireCandidateResolver) {
        Assert.notNull(autowireCandidateResolver, "AutowireCandidateResolver must not be null");
        if (autowireCandidateResolver instanceof BeanFactoryAware) {
            if (System.getSecurityManager() != null) {
                final BeanFactory target = this;
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        ((BeanFactoryAware)autowireCandidateResolver).setBeanFactory(target);
                        return null;
                    }
                }, this.getAccessControlContext());
            }
            else {
                ((BeanFactoryAware)autowireCandidateResolver).setBeanFactory(this);
            }
        }
        this.autowireCandidateResolver = autowireCandidateResolver;
    }
    
    public AutowireCandidateResolver getAutowireCandidateResolver() {
        return this.autowireCandidateResolver;
    }
    
    @Override
    public void copyConfigurationFrom(final ConfigurableBeanFactory otherFactory) {
        super.copyConfigurationFrom(otherFactory);
        if (otherFactory instanceof DefaultListableBeanFactory) {
            final DefaultListableBeanFactory otherListableFactory = (DefaultListableBeanFactory)otherFactory;
            this.allowBeanDefinitionOverriding = otherListableFactory.allowBeanDefinitionOverriding;
            this.allowEagerClassLoading = otherListableFactory.allowEagerClassLoading;
            this.autowireCandidateResolver = otherListableFactory.autowireCandidateResolver;
            this.resolvableDependencies.putAll(otherListableFactory.resolvableDependencies);
        }
    }
    
    @Override
    public <T> T getBean(final Class<T> requiredType) throws BeansException {
        Assert.notNull(requiredType, "Required type must not be null");
        String[] beanNames = this.getBeanNamesForType(requiredType);
        if (beanNames.length > 1) {
            final ArrayList<String> autowireCandidates = new ArrayList<String>();
            for (final String beanName : beanNames) {
                if (this.getBeanDefinition(beanName).isAutowireCandidate()) {
                    autowireCandidates.add(beanName);
                }
            }
            if (autowireCandidates.size() > 0) {
                beanNames = autowireCandidates.toArray(new String[autowireCandidates.size()]);
            }
        }
        if (beanNames.length == 1) {
            return this.getBean(beanNames[0], requiredType);
        }
        if (beanNames.length > 1) {
            T primaryBean = null;
            for (final String beanName : beanNames) {
                final T beanInstance = this.getBean(beanName, requiredType);
                if (this.isPrimary(beanName, beanInstance)) {
                    if (primaryBean != null) {
                        throw new NoUniqueBeanDefinitionException(requiredType, beanNames.length, "more than one 'primary' bean found of required type: " + Arrays.asList(beanNames));
                    }
                    primaryBean = beanInstance;
                }
            }
            if (primaryBean != null) {
                return primaryBean;
            }
            throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
        }
        else {
            if (this.getParentBeanFactory() != null) {
                return this.getParentBeanFactory().getBean(requiredType);
            }
            throw new NoSuchBeanDefinitionException(requiredType);
        }
    }
    
    @Override
    public boolean containsBeanDefinition(final String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        return this.beanDefinitionMap.containsKey(beanName);
    }
    
    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }
    
    @Override
    public String[] getBeanDefinitionNames() {
        synchronized (this.beanDefinitionMap) {
            if (this.frozenBeanDefinitionNames != null) {
                return this.frozenBeanDefinitionNames;
            }
            return StringUtils.toStringArray(this.beanDefinitionNames);
        }
    }
    
    @Override
    public String[] getBeanNamesForType(final Class<?> type) {
        return this.getBeanNamesForType(type, true, true);
    }
    
    @Override
    public String[] getBeanNamesForType(final Class<?> type, final boolean includeNonSingletons, final boolean allowEagerInit) {
        if (!this.isConfigurationFrozen() || type == null || !allowEagerInit) {
            return this.doGetBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        }
        final Map<Class<?>, String[]> cache = includeNonSingletons ? this.allBeanNamesByType : this.singletonBeanNamesByType;
        String[] resolvedBeanNames = cache.get(type);
        if (resolvedBeanNames != null) {
            return resolvedBeanNames;
        }
        resolvedBeanNames = this.doGetBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        cache.put(type, resolvedBeanNames);
        return resolvedBeanNames;
    }
    
    private String[] doGetBeanNamesForType(final Class<?> type, final boolean includeNonSingletons, final boolean allowEagerInit) {
        final List<String> result = new ArrayList<String>();
        final String[] beanDefinitionNames2;
        final String[] beanDefinitionNames = beanDefinitionNames2 = this.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames2) {
            if (!this.isAlias(beanName)) {
                try {
                    final RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                    if (!mbd.isAbstract() && (allowEagerInit || ((mbd.hasBeanClass() || !mbd.isLazyInit() || this.allowEagerClassLoading) && !this.requiresEagerInitForType(mbd.getFactoryBeanName())))) {
                        final boolean isFactoryBean = this.isFactoryBean(beanName, mbd);
                        boolean matchFound = (allowEagerInit || !isFactoryBean || this.containsSingleton(beanName)) && (includeNonSingletons || this.isSingleton(beanName)) && this.isTypeMatch(beanName, type);
                        if (!matchFound && isFactoryBean) {
                            beanName = "&" + beanName;
                            matchFound = ((includeNonSingletons || mbd.isSingleton()) && this.isTypeMatch(beanName, type));
                        }
                        if (matchFound) {
                            result.add(beanName);
                        }
                    }
                }
                catch (CannotLoadBeanClassException ex) {
                    if (allowEagerInit) {
                        throw ex;
                    }
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Ignoring bean class loading failure for bean '" + beanName + "'", ex);
                    }
                    this.onSuppressedException(ex);
                }
                catch (BeanDefinitionStoreException ex2) {
                    if (allowEagerInit) {
                        throw ex2;
                    }
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Ignoring unresolvable metadata in bean definition '" + beanName + "'", ex2);
                    }
                    this.onSuppressedException(ex2);
                }
            }
        }
        final String[] singletonNames2;
        final String[] singletonNames = singletonNames2 = this.getSingletonNames();
        for (String beanName2 : singletonNames2) {
            Label_0505: {
                if (!this.containsBeanDefinition(beanName2)) {
                    if (this.isFactoryBean(beanName2)) {
                        if ((includeNonSingletons || this.isSingleton(beanName2)) && this.isTypeMatch(beanName2, type)) {
                            result.add(beanName2);
                            break Label_0505;
                        }
                        beanName2 = "&" + beanName2;
                    }
                    if (this.isTypeMatch(beanName2, type)) {
                        result.add(beanName2);
                    }
                }
            }
        }
        return StringUtils.toStringArray(result);
    }
    
    private boolean requiresEagerInitForType(final String factoryBeanName) {
        return factoryBeanName != null && this.isFactoryBean(factoryBeanName) && !this.containsSingleton(factoryBeanName);
    }
    
    @Override
    public <T> Map<String, T> getBeansOfType(final Class<T> type) throws BeansException {
        return this.getBeansOfType(type, true, true);
    }
    
    @Override
    public <T> Map<String, T> getBeansOfType(final Class<T> type, final boolean includeNonSingletons, final boolean allowEagerInit) throws BeansException {
        final String[] beanNames = this.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        final Map<String, T> result = new LinkedHashMap<String, T>(beanNames.length);
        for (final String beanName : beanNames) {
            Label_0169: {
                try {
                    result.put(beanName, this.getBean(beanName, type));
                }
                catch (BeanCreationException ex) {
                    final Throwable rootCause = ex.getMostSpecificCause();
                    if (rootCause instanceof BeanCurrentlyInCreationException) {
                        final BeanCreationException bce = (BeanCreationException)rootCause;
                        if (this.isCurrentlyInCreation(bce.getBeanName())) {
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("Ignoring match to currently created bean '" + beanName + "': " + ex.getMessage());
                            }
                            this.onSuppressedException(ex);
                            break Label_0169;
                        }
                    }
                    throw ex;
                }
            }
        }
        return result;
    }
    
    @Override
    public String[] getBeanNamesForAnnotation(final Class<? extends Annotation> annotationType) {
        final List<String> results = new ArrayList<String>();
        for (final String beanName : this.getBeanDefinitionNames()) {
            final BeanDefinition beanDefinition = this.getBeanDefinition(beanName);
            if (!beanDefinition.isAbstract() && this.findAnnotationOnBean(beanName, annotationType) != null) {
                results.add(beanName);
            }
        }
        for (final String beanName : this.getSingletonNames()) {
            if (!results.contains(beanName) && this.findAnnotationOnBean(beanName, annotationType) != null) {
                results.add(beanName);
            }
        }
        return results.toArray(new String[results.size()]);
    }
    
    @Override
    public Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> annotationType) {
        final Map<String, Object> results = new LinkedHashMap<String, Object>();
        for (final String beanName : this.getBeanDefinitionNames()) {
            final BeanDefinition beanDefinition = this.getBeanDefinition(beanName);
            if (!beanDefinition.isAbstract() && this.findAnnotationOnBean(beanName, annotationType) != null) {
                results.put(beanName, this.getBean(beanName));
            }
        }
        for (final String beanName : this.getSingletonNames()) {
            if (!results.containsKey(beanName) && this.findAnnotationOnBean(beanName, annotationType) != null) {
                results.put(beanName, this.getBean(beanName));
            }
        }
        return results;
    }
    
    @Override
    public <A extends Annotation> A findAnnotationOnBean(final String beanName, final Class<A> annotationType) throws NoSuchBeanDefinitionException {
        A ann = null;
        final Class<?> beanType = this.getType(beanName);
        if (beanType != null) {
            ann = AnnotationUtils.findAnnotation(beanType, annotationType);
        }
        if (ann == null && this.containsBeanDefinition(beanName)) {
            final BeanDefinition bd = this.getMergedBeanDefinition(beanName);
            if (bd instanceof AbstractBeanDefinition) {
                final AbstractBeanDefinition abd = (AbstractBeanDefinition)bd;
                if (abd.hasBeanClass()) {
                    ann = AnnotationUtils.findAnnotation(abd.getBeanClass(), annotationType);
                }
            }
        }
        return ann;
    }
    
    @Override
    public void registerResolvableDependency(final Class<?> dependencyType, final Object autowiredValue) {
        Assert.notNull(dependencyType, "Type must not be null");
        if (autowiredValue != null) {
            Assert.isTrue(autowiredValue instanceof ObjectFactory || dependencyType.isInstance(autowiredValue), "Value [" + autowiredValue + "] does not implement specified type [" + dependencyType.getName() + "]");
            this.resolvableDependencies.put(dependencyType, autowiredValue);
        }
    }
    
    @Override
    public boolean isAutowireCandidate(final String beanName, final DependencyDescriptor descriptor) throws NoSuchBeanDefinitionException {
        return this.isAutowireCandidate(beanName, descriptor, this.getAutowireCandidateResolver());
    }
    
    protected boolean isAutowireCandidate(final String beanName, final DependencyDescriptor descriptor, final AutowireCandidateResolver resolver) throws NoSuchBeanDefinitionException {
        final String beanDefinitionName = BeanFactoryUtils.transformedBeanName(beanName);
        if (this.containsBeanDefinition(beanDefinitionName)) {
            return this.isAutowireCandidate(beanName, this.getMergedLocalBeanDefinition(beanDefinitionName), descriptor, resolver);
        }
        if (this.containsSingleton(beanName)) {
            return this.isAutowireCandidate(beanName, new RootBeanDefinition(this.getType(beanName)), descriptor, resolver);
        }
        if (this.getParentBeanFactory() instanceof DefaultListableBeanFactory) {
            return ((DefaultListableBeanFactory)this.getParentBeanFactory()).isAutowireCandidate(beanName, descriptor, resolver);
        }
        return !(this.getParentBeanFactory() instanceof ConfigurableListableBeanFactory) || ((ConfigurableListableBeanFactory)this.getParentBeanFactory()).isAutowireCandidate(beanName, descriptor);
    }
    
    protected boolean isAutowireCandidate(final String beanName, final RootBeanDefinition mbd, final DependencyDescriptor descriptor, final AutowireCandidateResolver resolver) {
        final String beanDefinitionName = BeanFactoryUtils.transformedBeanName(beanName);
        this.resolveBeanClass(mbd, beanDefinitionName, (Class<?>[])new Class[0]);
        if (mbd.isFactoryMethodUnique) {
            final boolean resolve;
            synchronized (mbd.constructorArgumentLock) {
                resolve = (mbd.resolvedConstructorOrFactoryMethod == null);
            }
            if (resolve) {
                new ConstructorResolver(this).resolveFactoryMethodIfPossible(mbd);
            }
        }
        return resolver.isAutowireCandidate(new BeanDefinitionHolder(mbd, beanName, this.getAliases(beanDefinitionName)), descriptor);
    }
    
    @Override
    public BeanDefinition getBeanDefinition(final String beanName) throws NoSuchBeanDefinitionException {
        final BeanDefinition bd = this.beanDefinitionMap.get(beanName);
        if (bd == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No bean named '" + beanName + "' found in " + this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        }
        return bd;
    }
    
    @Override
    public void freezeConfiguration() {
        this.configurationFrozen = true;
        synchronized (this.beanDefinitionMap) {
            this.frozenBeanDefinitionNames = StringUtils.toStringArray(this.beanDefinitionNames);
        }
    }
    
    @Override
    public boolean isConfigurationFrozen() {
        return this.configurationFrozen;
    }
    
    @Override
    protected boolean isBeanEligibleForMetadataCaching(final String beanName) {
        return this.configurationFrozen || super.isBeanEligibleForMetadataCaching(beanName);
    }
    
    @Override
    public void preInstantiateSingletons() throws BeansException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Pre-instantiating singletons in " + this);
        }
        final List<String> beanNames;
        synchronized (this.beanDefinitionMap) {
            beanNames = new ArrayList<String>(this.beanDefinitionNames);
        }
        for (final String beanName : beanNames) {
            final RootBeanDefinition bd = this.getMergedLocalBeanDefinition(beanName);
            if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
                if (this.isFactoryBean(beanName)) {
                    final FactoryBean<?> factory = (FactoryBean<?>)this.getBean("&" + beanName);
                    boolean isEagerInit;
                    if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                        isEagerInit = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
                            @Override
                            public Boolean run() {
                                return ((SmartFactoryBean)factory).isEagerInit();
                            }
                        }, this.getAccessControlContext());
                    }
                    else {
                        isEagerInit = (factory instanceof SmartFactoryBean && ((SmartFactoryBean)factory).isEagerInit());
                    }
                    if (!isEagerInit) {
                        continue;
                    }
                    this.getBean(beanName);
                }
                else {
                    this.getBean(beanName);
                }
            }
        }
    }
    
    @Override
    public void registerBeanDefinition(final String beanName, final BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        Assert.hasText(beanName, "Bean name must not be empty");
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        if (beanDefinition instanceof AbstractBeanDefinition) {
            try {
                ((AbstractBeanDefinition)beanDefinition).validate();
            }
            catch (BeanDefinitionValidationException ex) {
                throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName, "Validation of bean definition failed", ex);
            }
        }
        synchronized (this.beanDefinitionMap) {
            final BeanDefinition oldBeanDefinition = this.beanDefinitionMap.get(beanName);
            if (oldBeanDefinition != null) {
                if (!this.allowBeanDefinitionOverriding) {
                    throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName, "Cannot register bean definition [" + beanDefinition + "] for bean '" + beanName + "': There is already [" + oldBeanDefinition + "] bound.");
                }
                if (oldBeanDefinition.getRole() < beanDefinition.getRole()) {
                    if (this.logger.isWarnEnabled()) {
                        this.logger.warn("Overriding user-defined bean definition for bean '" + beanName + " with a framework-generated bean definition ': replacing [" + oldBeanDefinition + "] with [" + beanDefinition + "]");
                    }
                }
                else if (this.logger.isInfoEnabled()) {
                    this.logger.info("Overriding bean definition for bean '" + beanName + "': replacing [" + oldBeanDefinition + "] with [" + beanDefinition + "]");
                }
            }
            else {
                this.beanDefinitionNames.add(beanName);
                this.frozenBeanDefinitionNames = null;
            }
            this.beanDefinitionMap.put(beanName, beanDefinition);
        }
        this.resetBeanDefinition(beanName);
    }
    
    @Override
    public void removeBeanDefinition(final String beanName) throws NoSuchBeanDefinitionException {
        Assert.hasText(beanName, "'beanName' must not be empty");
        synchronized (this.beanDefinitionMap) {
            final BeanDefinition bd = this.beanDefinitionMap.remove(beanName);
            if (bd == null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("No bean named '" + beanName + "' found in " + this);
                }
                throw new NoSuchBeanDefinitionException(beanName);
            }
            this.beanDefinitionNames.remove(beanName);
            this.frozenBeanDefinitionNames = null;
        }
        this.resetBeanDefinition(beanName);
    }
    
    protected void resetBeanDefinition(final String beanName) {
        this.clearMergedBeanDefinition(beanName);
        this.destroySingleton(beanName);
        this.clearByTypeCache();
        for (final String bdName : this.beanDefinitionNames) {
            if (!beanName.equals(bdName)) {
                final BeanDefinition bd = this.beanDefinitionMap.get(bdName);
                if (!beanName.equals(bd.getParentName())) {
                    continue;
                }
                this.resetBeanDefinition(bdName);
            }
        }
    }
    
    @Override
    protected boolean allowAliasOverriding() {
        return this.allowBeanDefinitionOverriding;
    }
    
    @Override
    public void registerSingleton(final String beanName, final Object singletonObject) throws IllegalStateException {
        super.registerSingleton(beanName, singletonObject);
        this.clearByTypeCache();
    }
    
    @Override
    public void destroySingleton(final String beanName) {
        super.destroySingleton(beanName);
        this.clearByTypeCache();
    }
    
    private void clearByTypeCache() {
        this.allBeanNamesByType.clear();
        this.singletonBeanNamesByType.clear();
    }
    
    @Override
    public Object resolveDependency(final DependencyDescriptor descriptor, final String beanName, final Set<String> autowiredBeanNames, final TypeConverter typeConverter) throws BeansException {
        descriptor.initParameterNameDiscovery(this.getParameterNameDiscoverer());
        if (descriptor.getDependencyType().equals(ObjectFactory.class)) {
            return new DependencyObjectFactory(descriptor, beanName);
        }
        if (descriptor.getDependencyType().equals(DefaultListableBeanFactory.javaxInjectProviderClass)) {
            return new DependencyProviderFactory().createDependencyProvider(descriptor, beanName);
        }
        Object result = this.getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(descriptor, beanName);
        if (result == null) {
            result = this.doResolveDependency(descriptor, beanName, autowiredBeanNames, typeConverter);
        }
        return result;
    }
    
    public Object doResolveDependency(final DependencyDescriptor descriptor, final String beanName, final Set<String> autowiredBeanNames, final TypeConverter typeConverter) throws BeansException {
        final Class<?> type = descriptor.getDependencyType();
        Object value = this.getAutowireCandidateResolver().getSuggestedValue(descriptor);
        if (value != null) {
            if (value instanceof String) {
                final String strVal = this.resolveEmbeddedValue((String)value);
                final BeanDefinition bd = (beanName != null && this.containsBean(beanName)) ? this.getMergedBeanDefinition(beanName) : null;
                value = this.evaluateBeanDefinitionString(strVal, bd);
            }
            final TypeConverter converter = (typeConverter != null) ? typeConverter : this.getTypeConverter();
            return (descriptor.getField() != null) ? converter.convertIfNecessary(value, type, descriptor.getField()) : converter.convertIfNecessary(value, type, descriptor.getMethodParameter());
        }
        if (type.isArray()) {
            final Class<?> componentType = type.getComponentType();
            final DependencyDescriptor targetDesc = new DependencyDescriptor(descriptor);
            targetDesc.increaseNestingLevel();
            final Map<String, Object> matchingBeans = this.findAutowireCandidates(beanName, componentType, targetDesc);
            if (matchingBeans.isEmpty()) {
                if (descriptor.isRequired()) {
                    this.raiseNoSuchBeanDefinitionException(componentType, "array of " + componentType.getName(), descriptor);
                }
                return null;
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans.keySet());
            }
            final TypeConverter converter2 = (typeConverter != null) ? typeConverter : this.getTypeConverter();
            final Object result = converter2.convertIfNecessary(matchingBeans.values(), type);
            if (this.dependencyComparator != null && result instanceof Object[]) {
                Arrays.sort((Object[])result, this.dependencyComparator);
            }
            return result;
        }
        else if (Collection.class.isAssignableFrom(type) && type.isInterface()) {
            final Class<?> elementType = descriptor.getCollectionType();
            if (elementType == null) {
                if (descriptor.isRequired()) {
                    throw new FatalBeanException("No element type declared for collection [" + type.getName() + "]");
                }
                return null;
            }
            else {
                final DependencyDescriptor targetDesc = new DependencyDescriptor(descriptor);
                targetDesc.increaseNestingLevel();
                final Map<String, Object> matchingBeans = this.findAutowireCandidates(beanName, elementType, targetDesc);
                if (matchingBeans.isEmpty()) {
                    if (descriptor.isRequired()) {
                        this.raiseNoSuchBeanDefinitionException(elementType, "collection of " + elementType.getName(), descriptor);
                    }
                    return null;
                }
                if (autowiredBeanNames != null) {
                    autowiredBeanNames.addAll(matchingBeans.keySet());
                }
                final TypeConverter converter2 = (typeConverter != null) ? typeConverter : this.getTypeConverter();
                final Object result = converter2.convertIfNecessary(matchingBeans.values(), type);
                if (this.dependencyComparator != null && result instanceof List) {
                    Collections.sort((List<Object>)result, this.dependencyComparator);
                }
                return result;
            }
        }
        else if (Map.class.isAssignableFrom(type) && type.isInterface()) {
            final Class<?> keyType = descriptor.getMapKeyType();
            if (keyType == null || !String.class.isAssignableFrom(keyType)) {
                if (descriptor.isRequired()) {
                    throw new FatalBeanException("Key type [" + keyType + "] of map [" + type.getName() + "] must be assignable to [java.lang.String]");
                }
                return null;
            }
            else {
                final Class<?> valueType = descriptor.getMapValueType();
                if (valueType == null) {
                    if (descriptor.isRequired()) {
                        throw new FatalBeanException("No value type declared for map [" + type.getName() + "]");
                    }
                    return null;
                }
                else {
                    final DependencyDescriptor targetDesc2 = new DependencyDescriptor(descriptor);
                    targetDesc2.increaseNestingLevel();
                    final Map<String, Object> matchingBeans2 = this.findAutowireCandidates(beanName, valueType, targetDesc2);
                    if (matchingBeans2.isEmpty()) {
                        if (descriptor.isRequired()) {
                            this.raiseNoSuchBeanDefinitionException(valueType, "map with value type " + valueType.getName(), descriptor);
                        }
                        return null;
                    }
                    if (autowiredBeanNames != null) {
                        autowiredBeanNames.addAll(matchingBeans2.keySet());
                    }
                    return matchingBeans2;
                }
            }
        }
        else {
            final Map<String, Object> matchingBeans3 = this.findAutowireCandidates(beanName, type, descriptor);
            if (matchingBeans3.isEmpty()) {
                if (descriptor.isRequired()) {
                    this.raiseNoSuchBeanDefinitionException(type, "", descriptor);
                }
                return null;
            }
            if (matchingBeans3.size() <= 1) {
                final Map.Entry<String, Object> entry = matchingBeans3.entrySet().iterator().next();
                if (autowiredBeanNames != null) {
                    autowiredBeanNames.add(entry.getKey());
                }
                return entry.getValue();
            }
            final String primaryBeanName = this.determinePrimaryCandidate(matchingBeans3, descriptor);
            if (primaryBeanName == null) {
                throw new NoUniqueBeanDefinitionException(type, matchingBeans3.keySet());
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.add(primaryBeanName);
            }
            return matchingBeans3.get(primaryBeanName);
        }
    }
    
    protected Map<String, Object> findAutowireCandidates(final String beanName, final Class<?> requiredType, final DependencyDescriptor descriptor) {
        final String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this, requiredType, true, descriptor.isEager());
        final Map<String, Object> result = new LinkedHashMap<String, Object>(candidateNames.length);
        for (final Class<?> autowiringType : this.resolvableDependencies.keySet()) {
            if (autowiringType.isAssignableFrom(requiredType)) {
                Object autowiringValue = this.resolvableDependencies.get(autowiringType);
                autowiringValue = AutowireUtils.resolveAutowiringValue(autowiringValue, requiredType);
                if (requiredType.isInstance(autowiringValue)) {
                    result.put(ObjectUtils.identityToString(autowiringValue), autowiringValue);
                    break;
                }
                continue;
            }
        }
        for (final String candidateName : candidateNames) {
            if (!candidateName.equals(beanName) && this.isAutowireCandidate(candidateName, descriptor)) {
                result.put(candidateName, this.getBean(candidateName));
            }
        }
        if (result.isEmpty()) {
            final DependencyDescriptor fallbackDescriptor = descriptor.forFallbackMatch();
            for (final String candidateName2 : candidateNames) {
                if (!candidateName2.equals(beanName) && this.isAutowireCandidate(candidateName2, fallbackDescriptor)) {
                    result.put(candidateName2, this.getBean(candidateName2));
                }
            }
        }
        return result;
    }
    
    protected String determinePrimaryCandidate(final Map<String, Object> candidateBeans, final DependencyDescriptor descriptor) {
        String primaryBeanName = null;
        String fallbackBeanName = null;
        for (final Map.Entry<String, Object> entry : candidateBeans.entrySet()) {
            final String candidateBeanName = entry.getKey();
            final Object beanInstance = entry.getValue();
            if (this.isPrimary(candidateBeanName, beanInstance)) {
                if (primaryBeanName != null) {
                    final boolean candidateLocal = this.containsBeanDefinition(candidateBeanName);
                    final boolean primaryLocal = this.containsBeanDefinition(primaryBeanName);
                    if (candidateLocal == primaryLocal) {
                        throw new NoUniqueBeanDefinitionException(descriptor.getDependencyType(), candidateBeans.size(), "more than one 'primary' bean found among candidates: " + candidateBeans.keySet());
                    }
                    if (candidateLocal && !primaryLocal) {
                        primaryBeanName = candidateBeanName;
                    }
                }
                else {
                    primaryBeanName = candidateBeanName;
                }
            }
            if (primaryBeanName == null && (this.resolvableDependencies.values().contains(beanInstance) || this.matchesBeanName(candidateBeanName, descriptor.getDependencyName()))) {
                fallbackBeanName = candidateBeanName;
            }
        }
        return (primaryBeanName != null) ? primaryBeanName : fallbackBeanName;
    }
    
    protected boolean isPrimary(final String beanName, final Object beanInstance) {
        if (this.containsBeanDefinition(beanName)) {
            return this.getMergedLocalBeanDefinition(beanName).isPrimary();
        }
        final BeanFactory parentFactory = this.getParentBeanFactory();
        return parentFactory instanceof DefaultListableBeanFactory && ((DefaultListableBeanFactory)parentFactory).isPrimary(beanName, beanInstance);
    }
    
    protected boolean matchesBeanName(final String beanName, final String candidateName) {
        return candidateName != null && (candidateName.equals(beanName) || ObjectUtils.containsElement(this.getAliases(beanName), candidateName));
    }
    
    private void raiseNoSuchBeanDefinitionException(final Class<?> type, final String dependencyDescription, final DependencyDescriptor descriptor) throws NoSuchBeanDefinitionException {
        throw new NoSuchBeanDefinitionException(type, dependencyDescription, "expected at least 1 bean which qualifies as autowire candidate for this dependency. Dependency annotations: " + ObjectUtils.nullSafeToString(descriptor.getAnnotations()));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(ObjectUtils.identityToString(this));
        sb.append(": defining beans [");
        sb.append(StringUtils.arrayToCommaDelimitedString(this.getBeanDefinitionNames()));
        sb.append("]; ");
        final BeanFactory parent = this.getParentBeanFactory();
        if (parent == null) {
            sb.append("root of factory hierarchy");
        }
        else {
            sb.append("parent: ").append(ObjectUtils.identityToString(parent));
        }
        return sb.toString();
    }
    
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        throw new NotSerializableException("DefaultListableBeanFactory itself is not deserializable - just a SerializedBeanFactoryReference is");
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        if (this.serializationId != null) {
            return new SerializedBeanFactoryReference(this.serializationId);
        }
        throw new NotSerializableException("DefaultListableBeanFactory has no serialization id");
    }
    
    static {
        DefaultListableBeanFactory.javaxInjectProviderClass = null;
        final ClassLoader cl = DefaultListableBeanFactory.class.getClassLoader();
        try {
            DefaultListableBeanFactory.javaxInjectProviderClass = cl.loadClass("javax.inject.Provider");
        }
        catch (ClassNotFoundException ex) {}
        serializableFactories = new ConcurrentHashMap<String, Reference<DefaultListableBeanFactory>>(8);
    }
    
    private static class SerializedBeanFactoryReference implements Serializable
    {
        private final String id;
        
        public SerializedBeanFactoryReference(final String id) {
            this.id = id;
        }
        
        private Object readResolve() {
            final Reference<?> ref = DefaultListableBeanFactory.serializableFactories.get(this.id);
            if (ref == null) {
                throw new IllegalStateException("Cannot deserialize BeanFactory with id " + this.id + ": no factory registered for this id");
            }
            final Object result = ref.get();
            if (result == null) {
                throw new IllegalStateException("Cannot deserialize BeanFactory with id " + this.id + ": factory has been garbage-collected");
            }
            return result;
        }
    }
    
    private class DependencyObjectFactory implements ObjectFactory<Object>, Serializable
    {
        private final DependencyDescriptor descriptor;
        private final String beanName;
        
        public DependencyObjectFactory(final DependencyDescriptor descriptor, final String beanName) {
            (this.descriptor = new DependencyDescriptor(descriptor)).increaseNestingLevel();
            this.beanName = beanName;
        }
        
        @Override
        public Object getObject() throws BeansException {
            return DefaultListableBeanFactory.this.doResolveDependency(this.descriptor, this.beanName, null, null);
        }
    }
    
    private class DependencyProvider extends DependencyObjectFactory implements Provider<Object>
    {
        public DependencyProvider(final DependencyDescriptor descriptor, final String beanName) {
            super(descriptor, beanName);
        }
        
        public Object get() throws BeansException {
            return this.getObject();
        }
    }
    
    private class DependencyProviderFactory
    {
        public Object createDependencyProvider(final DependencyDescriptor descriptor, final String beanName) {
            return new DependencyProvider(descriptor, beanName);
        }
    }
}
