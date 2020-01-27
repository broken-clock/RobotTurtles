// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyValue;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.TreeSet;
import org.springframework.core.MethodParameter;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.core.PriorityOrdered;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.core.GenericTypeResolver;
import java.lang.reflect.Method;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import java.lang.reflect.Modifier;
import org.springframework.util.ReflectionUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.util.StringUtils;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import java.util.Iterator;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.PropertyValues;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Constructor;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ClassUtils;
import org.springframework.beans.BeansException;
import java.util.Collection;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import org.springframework.core.DefaultParameterNameDiscoverer;
import java.beans.PropertyDescriptor;
import org.springframework.beans.BeanWrapper;
import java.util.Map;
import java.util.Set;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory
{
    private InstantiationStrategy instantiationStrategy;
    private ParameterNameDiscoverer parameterNameDiscoverer;
    private boolean allowCircularReferences;
    private boolean allowRawInjectionDespiteWrapping;
    private final Set<Class<?>> ignoredDependencyTypes;
    private final Set<Class<?>> ignoredDependencyInterfaces;
    private final Map<String, BeanWrapper> factoryBeanInstanceCache;
    private final Map<Class<?>, PropertyDescriptor[]> filteredPropertyDescriptorsCache;
    
    public AbstractAutowireCapableBeanFactory() {
        this.instantiationStrategy = new CglibSubclassingInstantiationStrategy();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        this.allowCircularReferences = true;
        this.allowRawInjectionDespiteWrapping = false;
        this.ignoredDependencyTypes = new HashSet<Class<?>>();
        this.ignoredDependencyInterfaces = new HashSet<Class<?>>();
        this.factoryBeanInstanceCache = new ConcurrentHashMap<String, BeanWrapper>(16);
        this.filteredPropertyDescriptorsCache = new ConcurrentHashMap<Class<?>, PropertyDescriptor[]>(64);
        this.ignoreDependencyInterface(BeanNameAware.class);
        this.ignoreDependencyInterface(BeanFactoryAware.class);
        this.ignoreDependencyInterface(BeanClassLoaderAware.class);
    }
    
    public AbstractAutowireCapableBeanFactory(final BeanFactory parentBeanFactory) {
        this();
        this.setParentBeanFactory(parentBeanFactory);
    }
    
    public void setInstantiationStrategy(final InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }
    
    protected InstantiationStrategy getInstantiationStrategy() {
        return this.instantiationStrategy;
    }
    
    public void setParameterNameDiscoverer(final ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }
    
    protected ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }
    
    public void setAllowCircularReferences(final boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }
    
    public void setAllowRawInjectionDespiteWrapping(final boolean allowRawInjectionDespiteWrapping) {
        this.allowRawInjectionDespiteWrapping = allowRawInjectionDespiteWrapping;
    }
    
    public void ignoreDependencyType(final Class<?> type) {
        this.ignoredDependencyTypes.add(type);
    }
    
    public void ignoreDependencyInterface(final Class<?> ifc) {
        this.ignoredDependencyInterfaces.add(ifc);
    }
    
    @Override
    public void copyConfigurationFrom(final ConfigurableBeanFactory otherFactory) {
        super.copyConfigurationFrom(otherFactory);
        if (otherFactory instanceof AbstractAutowireCapableBeanFactory) {
            final AbstractAutowireCapableBeanFactory otherAutowireFactory = (AbstractAutowireCapableBeanFactory)otherFactory;
            this.instantiationStrategy = otherAutowireFactory.instantiationStrategy;
            this.allowCircularReferences = otherAutowireFactory.allowCircularReferences;
            this.ignoredDependencyTypes.addAll(otherAutowireFactory.ignoredDependencyTypes);
            this.ignoredDependencyInterfaces.addAll(otherAutowireFactory.ignoredDependencyInterfaces);
        }
    }
    
    @Override
    public <T> T createBean(final Class<T> beanClass) throws BeansException {
        final RootBeanDefinition bd = new RootBeanDefinition(beanClass);
        bd.setScope("prototype");
        bd.allowCaching = false;
        return (T)this.createBean(beanClass.getName(), bd, null);
    }
    
    @Override
    public void autowireBean(final Object existingBean) {
        final RootBeanDefinition bd = new RootBeanDefinition(ClassUtils.getUserClass(existingBean));
        bd.setScope("prototype");
        bd.allowCaching = false;
        final BeanWrapper bw = new BeanWrapperImpl(existingBean);
        this.initBeanWrapper(bw);
        this.populateBean(bd.getBeanClass().getName(), bd, bw);
    }
    
    @Override
    public Object configureBean(final Object existingBean, final String beanName) throws BeansException {
        this.markBeanAsCreated(beanName);
        final BeanDefinition mbd = this.getMergedBeanDefinition(beanName);
        RootBeanDefinition bd = null;
        if (mbd instanceof RootBeanDefinition) {
            final RootBeanDefinition rbd = (RootBeanDefinition)mbd;
            bd = (rbd.isPrototype() ? rbd : rbd.cloneBeanDefinition());
        }
        if (!mbd.isPrototype()) {
            if (bd == null) {
                bd = new RootBeanDefinition(mbd);
            }
            bd.setScope("prototype");
            bd.allowCaching = false;
        }
        final BeanWrapper bw = new BeanWrapperImpl(existingBean);
        this.initBeanWrapper(bw);
        this.populateBean(beanName, bd, bw);
        return this.initializeBean(beanName, existingBean, bd);
    }
    
    @Override
    public Object resolveDependency(final DependencyDescriptor descriptor, final String beanName) throws BeansException {
        return this.resolveDependency(descriptor, beanName, null, null);
    }
    
    @Override
    public Object createBean(final Class<?> beanClass, final int autowireMode, final boolean dependencyCheck) throws BeansException {
        final RootBeanDefinition bd = new RootBeanDefinition(beanClass, autowireMode, dependencyCheck);
        bd.setScope("prototype");
        return this.createBean(beanClass.getName(), bd, null);
    }
    
    @Override
    public Object autowire(final Class<?> beanClass, final int autowireMode, final boolean dependencyCheck) throws BeansException {
        final RootBeanDefinition bd = new RootBeanDefinition(beanClass, autowireMode, dependencyCheck);
        bd.setScope("prototype");
        if (bd.getResolvedAutowireMode() == 3) {
            return this.autowireConstructor(beanClass.getName(), bd, null, null).getWrappedInstance();
        }
        final BeanFactory parent = this;
        Object bean;
        if (System.getSecurityManager() != null) {
            bean = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    return AbstractAutowireCapableBeanFactory.this.getInstantiationStrategy().instantiate(bd, null, parent);
                }
            }, this.getAccessControlContext());
        }
        else {
            bean = this.getInstantiationStrategy().instantiate(bd, null, parent);
        }
        this.populateBean(beanClass.getName(), bd, new BeanWrapperImpl(bean));
        return bean;
    }
    
    @Override
    public void autowireBeanProperties(final Object existingBean, final int autowireMode, final boolean dependencyCheck) throws BeansException {
        if (autowireMode == 3) {
            throw new IllegalArgumentException("AUTOWIRE_CONSTRUCTOR not supported for existing bean instance");
        }
        final RootBeanDefinition bd = new RootBeanDefinition(ClassUtils.getUserClass(existingBean), autowireMode, dependencyCheck);
        bd.setScope("prototype");
        final BeanWrapper bw = new BeanWrapperImpl(existingBean);
        this.initBeanWrapper(bw);
        this.populateBean(bd.getBeanClass().getName(), bd, bw);
    }
    
    @Override
    public void applyBeanPropertyValues(final Object existingBean, final String beanName) throws BeansException {
        this.markBeanAsCreated(beanName);
        final BeanDefinition bd = this.getMergedBeanDefinition(beanName);
        final BeanWrapper bw = new BeanWrapperImpl(existingBean);
        this.initBeanWrapper(bw);
        this.applyPropertyValues(beanName, bd, bw, bd.getPropertyValues());
    }
    
    @Override
    public Object initializeBean(final Object existingBean, final String beanName) {
        return this.initializeBean(beanName, existingBean, null);
    }
    
    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(final Object existingBean, final String beanName) throws BeansException {
        Object result = existingBean;
        for (final BeanPostProcessor beanProcessor : this.getBeanPostProcessors()) {
            result = beanProcessor.postProcessBeforeInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }
    
    @Override
    public Object applyBeanPostProcessorsAfterInitialization(final Object existingBean, final String beanName) throws BeansException {
        Object result = existingBean;
        for (final BeanPostProcessor beanProcessor : this.getBeanPostProcessors()) {
            result = beanProcessor.postProcessAfterInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }
    
    @Override
    public void destroyBean(final Object existingBean) {
        new DisposableBeanAdapter(existingBean, this.getBeanPostProcessors(), this.getAccessControlContext()).destroy();
    }
    
    @Override
    protected Object createBean(final String beanName, final RootBeanDefinition mbd, final Object[] args) throws BeanCreationException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Creating instance of bean '" + beanName + "'");
        }
        this.resolveBeanClass(mbd, beanName, (Class<?>[])new Class[0]);
        try {
            mbd.prepareMethodOverrides();
        }
        catch (BeanDefinitionValidationException ex) {
            throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "Validation of method overrides failed", ex);
        }
        try {
            final Object bean = this.resolveBeforeInstantiation(beanName, mbd);
            if (bean != null) {
                return bean;
            }
        }
        catch (Throwable ex2) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "BeanPostProcessor before instantiation of bean failed", ex2);
        }
        final Object beanInstance = this.doCreateBean(beanName, mbd, args);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Finished creating instance of bean '" + beanName + "'");
        }
        return beanInstance;
    }
    
    protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final Object[] args) {
        BeanWrapper instanceWrapper = null;
        if (mbd.isSingleton()) {
            instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
        }
        if (instanceWrapper == null) {
            instanceWrapper = this.createBeanInstance(beanName, mbd, args);
        }
        final Object bean = (instanceWrapper != null) ? instanceWrapper.getWrappedInstance() : null;
        final Class<?> beanType = (instanceWrapper != null) ? instanceWrapper.getWrappedClass() : null;
        synchronized (mbd.postProcessingLock) {
            if (!mbd.postProcessed) {
                this.applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
                mbd.postProcessed = true;
            }
        }
        final boolean earlySingletonExposure = mbd.isSingleton() && this.allowCircularReferences && this.isSingletonCurrentlyInCreation(beanName);
        if (earlySingletonExposure) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Eagerly caching bean '" + beanName + "' to allow for resolving potential circular references");
            }
            this.addSingletonFactory(beanName, new ObjectFactory<Object>() {
                @Override
                public Object getObject() throws BeansException {
                    return AbstractAutowireCapableBeanFactory.this.getEarlyBeanReference(beanName, mbd, bean);
                }
            });
        }
        Object exposedObject = bean;
        try {
            this.populateBean(beanName, mbd, instanceWrapper);
            if (exposedObject != null) {
                exposedObject = this.initializeBean(beanName, exposedObject, mbd);
            }
        }
        catch (Throwable ex) {
            if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException)ex).getBeanName())) {
                throw (BeanCreationException)ex;
            }
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
        }
        if (earlySingletonExposure) {
            final Object earlySingletonReference = this.getSingleton(beanName, false);
            if (earlySingletonReference != null) {
                if (exposedObject == bean) {
                    exposedObject = earlySingletonReference;
                }
                else if (!this.allowRawInjectionDespiteWrapping && this.hasDependentBean(beanName)) {
                    final String[] dependentBeans = this.getDependentBeans(beanName);
                    final Set<String> actualDependentBeans = new LinkedHashSet<String>(dependentBeans.length);
                    for (final String dependentBean : dependentBeans) {
                        if (!this.removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
                            actualDependentBeans.add(dependentBean);
                        }
                    }
                    if (!actualDependentBeans.isEmpty()) {
                        throw new BeanCurrentlyInCreationException(beanName, "Bean with name '" + beanName + "' has been injected into other beans [" + StringUtils.collectionToCommaDelimitedString(actualDependentBeans) + "] in its raw version as part of a circular reference, but has eventually been " + "wrapped. This means that said other beans do not use the final version of the " + "bean. This is often the result of over-eager type matching - consider using " + "'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
                    }
                }
            }
        }
        try {
            this.registerDisposableBeanIfNecessary(beanName, bean, mbd);
        }
        catch (BeanDefinitionValidationException ex2) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex2);
        }
        return exposedObject;
    }
    
    @Override
    protected Class<?> predictBeanType(final String beanName, final RootBeanDefinition mbd, final Class<?>... typesToMatch) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType == null) {
            targetType = ((mbd.getFactoryMethodName() != null) ? this.getTypeForFactoryMethod(beanName, mbd, typesToMatch) : this.resolveBeanClass(mbd, beanName, typesToMatch));
            if (ObjectUtils.isEmpty(typesToMatch) || this.getTempClassLoader() == null) {
                mbd.setTargetType(targetType);
            }
        }
        if (targetType != null && !mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
            for (final BeanPostProcessor bp : this.getBeanPostProcessors()) {
                if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                    final SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor)bp;
                    final Class<?> predicted = ibp.predictBeanType(targetType, beanName);
                    if (predicted != null && (typesToMatch.length != 1 || !FactoryBean.class.equals(typesToMatch[0]) || FactoryBean.class.isAssignableFrom(predicted))) {
                        return predicted;
                    }
                    continue;
                }
            }
        }
        return targetType;
    }
    
    protected Class<?> getTypeForFactoryMethod(final String beanName, final RootBeanDefinition mbd, final Class<?>... typesToMatch) {
        final Class<?> preResolved = mbd.resolvedFactoryMethodReturnType;
        if (preResolved != null) {
            return preResolved;
        }
        boolean isStatic = true;
        final String factoryBeanName = mbd.getFactoryBeanName();
        Class<?> factoryClass;
        if (factoryBeanName != null) {
            if (factoryBeanName.equals(beanName)) {
                throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "factory-bean reference points back to the same bean definition");
            }
            factoryClass = this.getType(factoryBeanName);
            isStatic = false;
        }
        else {
            factoryClass = this.resolveBeanClass(mbd, beanName, typesToMatch);
        }
        if (factoryClass == null) {
            return null;
        }
        Class<?> commonType = null;
        boolean cache = false;
        final int minNrOfArgs = mbd.getConstructorArgumentValues().getArgumentCount();
        final Method[] uniqueDeclaredMethods;
        final Method[] candidates = uniqueDeclaredMethods = ReflectionUtils.getUniqueDeclaredMethods(factoryClass);
        for (final Method factoryMethod : uniqueDeclaredMethods) {
            if (Modifier.isStatic(factoryMethod.getModifiers()) == isStatic && factoryMethod.getName().equals(mbd.getFactoryMethodName()) && factoryMethod.getParameterTypes().length >= minNrOfArgs) {
                if (factoryMethod.getTypeParameters().length > 0) {
                    try {
                        final Class<?>[] paramTypes = factoryMethod.getParameterTypes();
                        String[] paramNames = null;
                        final ParameterNameDiscoverer pnd = this.getParameterNameDiscoverer();
                        if (pnd != null) {
                            paramNames = pnd.getParameterNames(factoryMethod);
                        }
                        final ConstructorArgumentValues cav = mbd.getConstructorArgumentValues();
                        final Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<ConstructorArgumentValues.ValueHolder>(paramTypes.length);
                        final Object[] args = new Object[paramTypes.length];
                        for (int i = 0; i < args.length; ++i) {
                            ConstructorArgumentValues.ValueHolder valueHolder = cav.getArgumentValue(i, paramTypes[i], (paramNames != null) ? paramNames[i] : null, usedValueHolders);
                            if (valueHolder == null) {
                                valueHolder = cav.getGenericArgumentValue(null, null, usedValueHolders);
                            }
                            if (valueHolder != null) {
                                args[i] = valueHolder.getValue();
                                usedValueHolders.add(valueHolder);
                            }
                        }
                        final Class<?> returnType = AutowireUtils.resolveReturnTypeForFactoryMethod(factoryMethod, args, this.getBeanClassLoader());
                        if (returnType != null) {
                            cache = true;
                            commonType = ClassUtils.determineCommonAncestor(returnType, commonType);
                        }
                    }
                    catch (Throwable ex) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Failed to resolve generic return type for factory method: " + ex);
                        }
                    }
                }
                else {
                    commonType = ClassUtils.determineCommonAncestor(factoryMethod.getReturnType(), commonType);
                }
            }
        }
        if (commonType != null) {
            if (cache) {
                mbd.resolvedFactoryMethodReturnType = commonType;
            }
            return commonType;
        }
        return null;
    }
    
    @Override
    protected Class<?> getTypeForFactoryBean(final String beanName, final RootBeanDefinition mbd) {
        class Holder
        {
            Class<?> value;
            
            Holder() {
                this.value = null;
            }
        }
        final Holder objectType = new Holder();
        final String factoryBeanName = mbd.getFactoryBeanName();
        final String factoryMethodName = mbd.getFactoryMethodName();
        if (factoryBeanName != null) {
            if (factoryMethodName != null) {
                final BeanDefinition fbDef = this.getBeanDefinition(factoryBeanName);
                if (fbDef instanceof AbstractBeanDefinition && ((AbstractBeanDefinition)fbDef).hasBeanClass()) {
                    final Class<?> fbClass = ClassUtils.getUserClass(((AbstractBeanDefinition)fbDef).getBeanClass());
                    ReflectionUtils.doWithMethods(fbClass, new ReflectionUtils.MethodCallback() {
                        @Override
                        public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
                            if (method.getName().equals(factoryMethodName) && FactoryBean.class.isAssignableFrom(method.getReturnType())) {
                                objectType.value = GenericTypeResolver.resolveReturnTypeArgument(method, FactoryBean.class);
                            }
                        }
                    });
                    if (objectType.value != null) {
                        return objectType.value;
                    }
                }
            }
            if (!this.isBeanEligibleForMetadataCaching(factoryBeanName)) {
                return null;
            }
        }
        final FactoryBean<?> fb = mbd.isSingleton() ? this.getSingletonFactoryBeanForTypeCheck(beanName, mbd) : this.getNonSingletonFactoryBeanForTypeCheck(beanName, mbd);
        if (fb != null) {
            objectType.value = this.getTypeForFactoryBean(fb);
            if (objectType.value != null) {
                return objectType.value;
            }
        }
        return super.getTypeForFactoryBean(beanName, mbd);
    }
    
    protected Object getEarlyBeanReference(final String beanName, final RootBeanDefinition mbd, final Object bean) {
        Object exposedObject = bean;
        if (bean != null && !mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
            for (final BeanPostProcessor bp : this.getBeanPostProcessors()) {
                if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                    final SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor)bp;
                    exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
                    if (exposedObject == null) {
                        return exposedObject;
                    }
                    continue;
                }
            }
        }
        return exposedObject;
    }
    
    private FactoryBean<?> getSingletonFactoryBeanForTypeCheck(final String beanName, final RootBeanDefinition mbd) {
        synchronized (this.getSingletonMutex()) {
            BeanWrapper bw = this.factoryBeanInstanceCache.get(beanName);
            if (bw != null) {
                return (FactoryBean<?>)bw.getWrappedInstance();
            }
            if (this.isSingletonCurrentlyInCreation(beanName)) {
                return null;
            }
            Object instance = null;
            try {
                this.beforeSingletonCreation(beanName);
                instance = this.resolveBeforeInstantiation(beanName, mbd);
                if (instance == null) {
                    bw = this.createBeanInstance(beanName, mbd, null);
                    instance = bw.getWrappedInstance();
                }
            }
            finally {
                this.afterSingletonCreation(beanName);
            }
            final FactoryBean<?> fb = this.getFactoryBean(beanName, instance);
            if (bw != null) {
                this.factoryBeanInstanceCache.put(beanName, bw);
            }
            return fb;
        }
    }
    
    private FactoryBean<?> getNonSingletonFactoryBeanForTypeCheck(final String beanName, final RootBeanDefinition mbd) {
        if (this.isPrototypeCurrentlyInCreation(beanName)) {
            return null;
        }
        Object instance = null;
        try {
            this.beforePrototypeCreation(beanName);
            instance = this.resolveBeforeInstantiation(beanName, mbd);
            if (instance == null) {
                final BeanWrapper bw = this.createBeanInstance(beanName, mbd, null);
                instance = bw.getWrappedInstance();
            }
        }
        finally {
            this.afterPrototypeCreation(beanName);
        }
        return this.getFactoryBean(beanName, instance);
    }
    
    protected void applyMergedBeanDefinitionPostProcessors(final RootBeanDefinition mbd, final Class<?> beanType, final String beanName) throws BeansException {
        try {
            for (final BeanPostProcessor bp : this.getBeanPostProcessors()) {
                if (bp instanceof MergedBeanDefinitionPostProcessor) {
                    final MergedBeanDefinitionPostProcessor bdp = (MergedBeanDefinitionPostProcessor)bp;
                    bdp.postProcessMergedBeanDefinition(mbd, beanType, beanName);
                }
            }
        }
        catch (Exception ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Post-processing failed of bean type [" + beanType + "] failed", ex);
        }
    }
    
    protected Object resolveBeforeInstantiation(final String beanName, final RootBeanDefinition mbd) {
        Object bean = null;
        if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
            if (mbd.hasBeanClass() && !mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
                bean = this.applyBeanPostProcessorsBeforeInstantiation(mbd.getBeanClass(), beanName);
                if (bean != null) {
                    bean = this.applyBeanPostProcessorsAfterInitialization(bean, beanName);
                }
            }
            mbd.beforeInstantiationResolved = (bean != null);
        }
        return bean;
    }
    
    protected Object applyBeanPostProcessorsBeforeInstantiation(final Class<?> beanClass, final String beanName) throws BeansException {
        for (final BeanPostProcessor bp : this.getBeanPostProcessors()) {
            if (bp instanceof InstantiationAwareBeanPostProcessor) {
                final InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor)bp;
                final Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
                if (result != null) {
                    return result;
                }
                continue;
            }
        }
        return null;
    }
    
    protected BeanWrapper createBeanInstance(final String beanName, final RootBeanDefinition mbd, final Object[] args) {
        final Class<?> beanClass = this.resolveBeanClass(mbd, beanName, (Class<?>[])new Class[0]);
        if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
        }
        if (mbd.getFactoryMethodName() != null) {
            return this.instantiateUsingFactoryMethod(beanName, mbd, args);
        }
        boolean resolved = false;
        boolean autowireNecessary = false;
        if (args == null) {
            synchronized (mbd.constructorArgumentLock) {
                if (mbd.resolvedConstructorOrFactoryMethod != null) {
                    resolved = true;
                    autowireNecessary = mbd.constructorArgumentsResolved;
                }
            }
        }
        if (resolved) {
            if (autowireNecessary) {
                return this.autowireConstructor(beanName, mbd, null, null);
            }
            return this.instantiateBean(beanName, mbd);
        }
        else {
            final Constructor<?>[] ctors = this.determineConstructorsFromBeanPostProcessors(beanClass, beanName);
            if (ctors != null || mbd.getResolvedAutowireMode() == 3 || mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
                return this.autowireConstructor(beanName, mbd, ctors, args);
            }
            return this.instantiateBean(beanName, mbd);
        }
    }
    
    protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(final Class<?> beanClass, final String beanName) throws BeansException {
        if (beanClass != null && this.hasInstantiationAwareBeanPostProcessors()) {
            for (final BeanPostProcessor bp : this.getBeanPostProcessors()) {
                if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                    final SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor)bp;
                    final Constructor<?>[] ctors = ibp.determineCandidateConstructors(beanClass, beanName);
                    if (ctors != null) {
                        return ctors;
                    }
                    continue;
                }
            }
        }
        return null;
    }
    
    protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
        try {
            final BeanFactory parent = this;
            Object beanInstance;
            if (System.getSecurityManager() != null) {
                beanInstance = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        return AbstractAutowireCapableBeanFactory.this.getInstantiationStrategy().instantiate(mbd, beanName, parent);
                    }
                }, this.getAccessControlContext());
            }
            else {
                beanInstance = this.getInstantiationStrategy().instantiate(mbd, beanName, parent);
            }
            final BeanWrapper bw = new BeanWrapperImpl(beanInstance);
            this.initBeanWrapper(bw);
            return bw;
        }
        catch (Throwable ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
        }
    }
    
    protected BeanWrapper instantiateUsingFactoryMethod(final String beanName, final RootBeanDefinition mbd, final Object[] explicitArgs) {
        return new ConstructorResolver(this).instantiateUsingFactoryMethod(beanName, mbd, explicitArgs);
    }
    
    protected BeanWrapper autowireConstructor(final String beanName, final RootBeanDefinition mbd, final Constructor<?>[] ctors, final Object[] explicitArgs) {
        return new ConstructorResolver(this).autowireConstructor(beanName, mbd, ctors, explicitArgs);
    }
    
    protected void populateBean(final String beanName, final RootBeanDefinition mbd, final BeanWrapper bw) {
        PropertyValues pvs = mbd.getPropertyValues();
        if (bw == null) {
            if (!pvs.isEmpty()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
            }
        }
        else {
            boolean continueWithPropertyPopulation = true;
            if (!mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
                for (final BeanPostProcessor bp : this.getBeanPostProcessors()) {
                    if (bp instanceof InstantiationAwareBeanPostProcessor) {
                        final InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor)bp;
                        if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
                            continueWithPropertyPopulation = false;
                            break;
                        }
                        continue;
                    }
                }
            }
            if (!continueWithPropertyPopulation) {
                return;
            }
            if (mbd.getResolvedAutowireMode() == 1 || mbd.getResolvedAutowireMode() == 2) {
                final MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
                if (mbd.getResolvedAutowireMode() == 1) {
                    this.autowireByName(beanName, mbd, bw, newPvs);
                }
                if (mbd.getResolvedAutowireMode() == 2) {
                    this.autowireByType(beanName, mbd, bw, newPvs);
                }
                pvs = newPvs;
            }
            final boolean hasInstAwareBpps = this.hasInstantiationAwareBeanPostProcessors();
            final boolean needsDepCheck = mbd.getDependencyCheck() != 0;
            if (hasInstAwareBpps || needsDepCheck) {
                final PropertyDescriptor[] filteredPds = this.filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
                if (hasInstAwareBpps) {
                    for (final BeanPostProcessor bp2 : this.getBeanPostProcessors()) {
                        if (bp2 instanceof InstantiationAwareBeanPostProcessor) {
                            final InstantiationAwareBeanPostProcessor ibp2 = (InstantiationAwareBeanPostProcessor)bp2;
                            pvs = ibp2.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
                            if (pvs == null) {
                                return;
                            }
                            continue;
                        }
                    }
                }
                if (needsDepCheck) {
                    this.checkDependencies(beanName, mbd, filteredPds, pvs);
                }
            }
            this.applyPropertyValues(beanName, mbd, bw, pvs);
        }
    }
    
    protected void autowireByName(final String beanName, final AbstractBeanDefinition mbd, final BeanWrapper bw, final MutablePropertyValues pvs) {
        final String[] unsatisfiedNonSimpleProperties;
        final String[] propertyNames = unsatisfiedNonSimpleProperties = this.unsatisfiedNonSimpleProperties(mbd, bw);
        for (final String propertyName : unsatisfiedNonSimpleProperties) {
            if (this.containsBean(propertyName)) {
                final Object bean = this.getBean(propertyName);
                pvs.add(propertyName, bean);
                this.registerDependentBean(propertyName, beanName);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Added autowiring by name from bean name '" + beanName + "' via property '" + propertyName + "' to bean named '" + propertyName + "'");
                }
            }
            else if (this.logger.isTraceEnabled()) {
                this.logger.trace("Not autowiring property '" + propertyName + "' of bean '" + beanName + "' by name: no matching bean found");
            }
        }
    }
    
    protected void autowireByType(final String beanName, final AbstractBeanDefinition mbd, final BeanWrapper bw, final MutablePropertyValues pvs) {
        TypeConverter converter = this.getCustomTypeConverter();
        if (converter == null) {
            converter = bw;
        }
        final Set<String> autowiredBeanNames = new LinkedHashSet<String>(4);
        final String[] unsatisfiedNonSimpleProperties;
        final String[] propertyNames = unsatisfiedNonSimpleProperties = this.unsatisfiedNonSimpleProperties(mbd, bw);
        for (final String propertyName : unsatisfiedNonSimpleProperties) {
            try {
                final PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
                if (!Object.class.equals(pd.getPropertyType())) {
                    final MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
                    final boolean eager = !PriorityOrdered.class.isAssignableFrom(bw.getWrappedClass());
                    final DependencyDescriptor desc = new AutowireByTypeDependencyDescriptor(methodParam, eager);
                    final Object autowiredArgument = this.resolveDependency(desc, beanName, autowiredBeanNames, converter);
                    if (autowiredArgument != null) {
                        pvs.add(propertyName, autowiredArgument);
                    }
                    for (final String autowiredBeanName : autowiredBeanNames) {
                        this.registerDependentBean(autowiredBeanName, beanName);
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Autowiring by type from bean name '" + beanName + "' via property '" + propertyName + "' to bean named '" + autowiredBeanName + "'");
                        }
                    }
                    autowiredBeanNames.clear();
                }
            }
            catch (BeansException ex) {
                throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, propertyName, ex);
            }
        }
    }
    
    protected String[] unsatisfiedNonSimpleProperties(final AbstractBeanDefinition mbd, final BeanWrapper bw) {
        final Set<String> result = new TreeSet<String>();
        final PropertyValues pvs = mbd.getPropertyValues();
        final PropertyDescriptor[] propertyDescriptors;
        final PropertyDescriptor[] pds = propertyDescriptors = bw.getPropertyDescriptors();
        for (final PropertyDescriptor pd : propertyDescriptors) {
            if (pd.getWriteMethod() != null && !this.isExcludedFromDependencyCheck(pd) && !pvs.contains(pd.getName()) && !BeanUtils.isSimpleProperty(pd.getPropertyType())) {
                result.add(pd.getName());
            }
        }
        return StringUtils.toStringArray(result);
    }
    
    protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(final BeanWrapper bw, final boolean cache) {
        PropertyDescriptor[] filtered = this.filteredPropertyDescriptorsCache.get(bw.getWrappedClass());
        if (filtered == null) {
            if (cache) {
                synchronized (this.filteredPropertyDescriptorsCache) {
                    filtered = this.filteredPropertyDescriptorsCache.get(bw.getWrappedClass());
                    if (filtered == null) {
                        filtered = this.filterPropertyDescriptorsForDependencyCheck(bw);
                        this.filteredPropertyDescriptorsCache.put(bw.getWrappedClass(), filtered);
                    }
                }
            }
            else {
                filtered = this.filterPropertyDescriptorsForDependencyCheck(bw);
            }
        }
        return filtered;
    }
    
    protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(final BeanWrapper bw) {
        final List<PropertyDescriptor> pds = new LinkedList<PropertyDescriptor>(Arrays.asList(bw.getPropertyDescriptors()));
        final Iterator<PropertyDescriptor> it = pds.iterator();
        while (it.hasNext()) {
            final PropertyDescriptor pd = it.next();
            if (this.isExcludedFromDependencyCheck(pd)) {
                it.remove();
            }
        }
        return pds.toArray(new PropertyDescriptor[pds.size()]);
    }
    
    protected boolean isExcludedFromDependencyCheck(final PropertyDescriptor pd) {
        return AutowireUtils.isExcludedFromDependencyCheck(pd) || this.ignoredDependencyTypes.contains(pd.getPropertyType()) || AutowireUtils.isSetterDefinedInInterface(pd, this.ignoredDependencyInterfaces);
    }
    
    protected void checkDependencies(final String beanName, final AbstractBeanDefinition mbd, final PropertyDescriptor[] pds, final PropertyValues pvs) throws UnsatisfiedDependencyException {
        final int dependencyCheck = mbd.getDependencyCheck();
        for (final PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null && !pvs.contains(pd.getName())) {
                final boolean isSimple = BeanUtils.isSimpleProperty(pd.getPropertyType());
                final boolean unsatisfied = dependencyCheck == 3 || (isSimple && dependencyCheck == 2) || (!isSimple && dependencyCheck == 1);
                if (unsatisfied) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, pd.getName(), "Set this property value or disable dependency checking for this bean.");
                }
            }
        }
    }
    
    protected void applyPropertyValues(final String beanName, final BeanDefinition mbd, final BeanWrapper bw, final PropertyValues pvs) {
        if (pvs == null || pvs.isEmpty()) {
            return;
        }
        MutablePropertyValues mpvs = null;
        if (System.getSecurityManager() != null && bw instanceof BeanWrapperImpl) {
            ((BeanWrapperImpl)bw).setSecurityContext(this.getAccessControlContext());
        }
        List<PropertyValue> original;
        if (pvs instanceof MutablePropertyValues) {
            mpvs = (MutablePropertyValues)pvs;
            if (mpvs.isConverted()) {
                try {
                    bw.setPropertyValues(mpvs);
                    return;
                }
                catch (BeansException ex) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Error setting property values", ex);
                }
            }
            original = mpvs.getPropertyValueList();
        }
        else {
            original = Arrays.asList(pvs.getPropertyValues());
        }
        TypeConverter converter = this.getCustomTypeConverter();
        if (converter == null) {
            converter = bw;
        }
        final BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this, beanName, mbd, converter);
        final List<PropertyValue> deepCopy = new ArrayList<PropertyValue>(original.size());
        boolean resolveNecessary = false;
        for (final PropertyValue pv : original) {
            if (pv.isConverted()) {
                deepCopy.add(pv);
            }
            else {
                final String propertyName = pv.getName();
                final Object originalValue = pv.getValue();
                Object convertedValue;
                final Object resolvedValue = convertedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);
                final boolean convertible = bw.isWritableProperty(propertyName) && !PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName);
                if (convertible) {
                    convertedValue = this.convertForProperty(resolvedValue, propertyName, bw, converter);
                }
                if (resolvedValue == originalValue) {
                    if (convertible) {
                        pv.setConvertedValue(convertedValue);
                    }
                    deepCopy.add(pv);
                }
                else if (convertible && originalValue instanceof TypedStringValue && !((TypedStringValue)originalValue).isDynamic() && !(convertedValue instanceof Collection) && !ObjectUtils.isArray(convertedValue)) {
                    pv.setConvertedValue(convertedValue);
                    deepCopy.add(pv);
                }
                else {
                    resolveNecessary = true;
                    deepCopy.add(new PropertyValue(pv, convertedValue));
                }
            }
        }
        if (mpvs != null && !resolveNecessary) {
            mpvs.setConverted();
        }
        try {
            bw.setPropertyValues(new MutablePropertyValues(deepCopy));
        }
        catch (BeansException ex2) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Error setting property values", ex2);
        }
    }
    
    private Object convertForProperty(final Object value, final String propertyName, final BeanWrapper bw, final TypeConverter converter) {
        if (converter instanceof BeanWrapperImpl) {
            return ((BeanWrapperImpl)converter).convertForProperty(value, propertyName);
        }
        final PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
        final MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
        return converter.convertIfNecessary(value, pd.getPropertyType(), methodParam);
    }
    
    protected Object initializeBean(final String beanName, final Object bean, final RootBeanDefinition mbd) {
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    AbstractAutowireCapableBeanFactory.this.invokeAwareMethods(beanName, bean);
                    return null;
                }
            }, this.getAccessControlContext());
        }
        else {
            this.invokeAwareMethods(beanName, bean);
        }
        Object wrappedBean = bean;
        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = this.applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
        }
        try {
            this.invokeInitMethods(beanName, wrappedBean, mbd);
        }
        catch (Throwable ex) {
            throw new BeanCreationException((mbd != null) ? mbd.getResourceDescription() : null, beanName, "Invocation of init method failed", ex);
        }
        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = this.applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        }
        return wrappedBean;
    }
    
    private void invokeAwareMethods(final String beanName, final Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware)bean).setBeanName(beanName);
            }
            if (bean instanceof BeanClassLoaderAware) {
                ((BeanClassLoaderAware)bean).setBeanClassLoader(this.getBeanClassLoader());
            }
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware)bean).setBeanFactory(this);
            }
        }
    }
    
    protected void invokeInitMethods(final String beanName, final Object bean, final RootBeanDefinition mbd) throws Throwable {
        final boolean isInitializingBean = bean instanceof InitializingBean;
        Label_0114: {
            if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
                }
                if (System.getSecurityManager() != null) {
                    try {
                        AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                            @Override
                            public Object run() throws Exception {
                                ((InitializingBean)bean).afterPropertiesSet();
                                return null;
                            }
                        }, this.getAccessControlContext());
                        break Label_0114;
                    }
                    catch (PrivilegedActionException pae) {
                        throw pae.getException();
                    }
                }
                ((InitializingBean)bean).afterPropertiesSet();
            }
        }
        if (mbd != null) {
            final String initMethodName = mbd.getInitMethodName();
            if (initMethodName != null && (!isInitializingBean || !"afterPropertiesSet".equals(initMethodName)) && !mbd.isExternallyManagedInitMethod(initMethodName)) {
                this.invokeCustomInitMethod(beanName, bean, mbd);
            }
        }
    }
    
    protected void invokeCustomInitMethod(final String beanName, final Object bean, final RootBeanDefinition mbd) throws Throwable {
        final String initMethodName = mbd.getInitMethodName();
        final Method initMethod = mbd.isNonPublicAccessAllowed() ? BeanUtils.findMethod(bean.getClass(), initMethodName, (Class<?>[])new Class[0]) : ClassUtils.getMethodIfAvailable(bean.getClass(), initMethodName, (Class<?>[])new Class[0]);
        if (initMethod != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Invoking init method  '" + initMethodName + "' on bean with name '" + beanName + "'");
            }
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                    @Override
                    public Object run() throws Exception {
                        ReflectionUtils.makeAccessible(initMethod);
                        return null;
                    }
                });
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                        @Override
                        public Object run() throws Exception {
                            initMethod.invoke(bean, new Object[0]);
                            return null;
                        }
                    }, this.getAccessControlContext());
                    return;
                }
                catch (PrivilegedActionException pae) {
                    final InvocationTargetException ex = (InvocationTargetException)pae.getException();
                    throw ex.getTargetException();
                }
            }
            try {
                ReflectionUtils.makeAccessible(initMethod);
                initMethod.invoke(bean, new Object[0]);
            }
            catch (InvocationTargetException ex2) {
                throw ex2.getTargetException();
            }
            return;
        }
        if (mbd.isEnforceInitMethod()) {
            throw new BeanDefinitionValidationException("Couldn't find an init method named '" + initMethodName + "' on bean with name '" + beanName + "'");
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("No default init method named '" + initMethodName + "' found on bean with name '" + beanName + "'");
        }
    }
    
    @Override
    protected Object postProcessObjectFromFactoryBean(final Object object, final String beanName) {
        return this.applyBeanPostProcessorsAfterInitialization(object, beanName);
    }
    
    @Override
    protected void removeSingleton(final String beanName) {
        super.removeSingleton(beanName);
        this.factoryBeanInstanceCache.remove(beanName);
    }
    
    private static class AutowireByTypeDependencyDescriptor extends DependencyDescriptor
    {
        public AutowireByTypeDependencyDescriptor(final MethodParameter methodParameter, final boolean eager) {
            super(methodParameter, false, eager);
        }
        
        @Override
        public String getDependencyName() {
            return null;
        }
    }
}
