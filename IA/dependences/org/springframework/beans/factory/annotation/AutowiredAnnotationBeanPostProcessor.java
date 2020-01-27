// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.annotation;

import java.lang.reflect.InvocationTargetException;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Member;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import java.util.Iterator;
import java.lang.reflect.AnnotatedElement;
import org.springframework.core.annotation.AnnotatedElementUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.core.BridgeMethodResolver;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import org.springframework.util.StringUtils;
import java.beans.PropertyDescriptor;
import org.springframework.beans.PropertyValues;
import org.springframework.core.annotation.AnnotationAttributes;
import java.util.List;
import org.springframework.beans.factory.BeanCreationException;
import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import java.util.Collection;
import org.springframework.util.Assert;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedHashSet;
import org.apache.commons.logging.LogFactory;
import java.lang.reflect.Constructor;
import java.util.Map;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import java.lang.annotation.Annotation;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

public class AutowiredAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware
{
    protected final Log logger;
    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes;
    private String requiredParameterName;
    private boolean requiredParameterValue;
    private int order;
    private ConfigurableListableBeanFactory beanFactory;
    private final Map<Class<?>, Constructor<?>[]> candidateConstructorsCache;
    private final Map<String, InjectionMetadata> injectionMetadataCache;
    
    public AutowiredAnnotationBeanPostProcessor() {
        this.logger = LogFactory.getLog(this.getClass());
        this.autowiredAnnotationTypes = new LinkedHashSet<Class<? extends Annotation>>();
        this.requiredParameterName = "required";
        this.requiredParameterValue = true;
        this.order = 2147483645;
        this.candidateConstructorsCache = new ConcurrentHashMap<Class<?>, Constructor<?>[]>(64);
        this.injectionMetadataCache = new ConcurrentHashMap<String, InjectionMetadata>(64);
        this.autowiredAnnotationTypes.add(Autowired.class);
        this.autowiredAnnotationTypes.add(Value.class);
        final ClassLoader cl = AutowiredAnnotationBeanPostProcessor.class.getClassLoader();
        try {
            this.autowiredAnnotationTypes.add((Class<? extends Annotation>)cl.loadClass("javax.inject.Inject"));
            this.logger.info("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
        }
        catch (ClassNotFoundException ex) {}
    }
    
    public void setAutowiredAnnotationType(final Class<? extends Annotation> autowiredAnnotationType) {
        Assert.notNull(autowiredAnnotationType, "'autowiredAnnotationType' must not be null");
        this.autowiredAnnotationTypes.clear();
        this.autowiredAnnotationTypes.add(autowiredAnnotationType);
    }
    
    public void setAutowiredAnnotationTypes(final Set<Class<? extends Annotation>> autowiredAnnotationTypes) {
        Assert.notEmpty(autowiredAnnotationTypes, "'autowiredAnnotationTypes' must not be empty");
        this.autowiredAnnotationTypes.clear();
        this.autowiredAnnotationTypes.addAll(autowiredAnnotationTypes);
    }
    
    public void setRequiredParameterName(final String requiredParameterName) {
        this.requiredParameterName = requiredParameterName;
    }
    
    public void setRequiredParameterValue(final boolean requiredParameterValue) {
        this.requiredParameterValue = requiredParameterValue;
    }
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    @Override
    public int getOrder() {
        return this.order;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
    }
    
    @Override
    public void postProcessMergedBeanDefinition(final RootBeanDefinition beanDefinition, final Class<?> beanType, final String beanName) {
        if (beanType != null) {
            final InjectionMetadata metadata = this.findAutowiringMetadata(beanName, beanType);
            metadata.checkConfigMembers(beanDefinition);
        }
    }
    
    @Override
    public Constructor<?>[] determineCandidateConstructors(final Class<?> beanClass, final String beanName) throws BeansException {
        Constructor<?>[] candidateConstructors = this.candidateConstructorsCache.get(beanClass);
        if (candidateConstructors == null) {
            synchronized (this.candidateConstructorsCache) {
                candidateConstructors = this.candidateConstructorsCache.get(beanClass);
                if (candidateConstructors == null) {
                    final Constructor<?>[] rawCandidates = beanClass.getDeclaredConstructors();
                    final List<Constructor<?>> candidates = new ArrayList<Constructor<?>>(rawCandidates.length);
                    Constructor<?> requiredConstructor = null;
                    Constructor<?> defaultConstructor = null;
                    for (final Constructor<?> candidate : rawCandidates) {
                        final AnnotationAttributes annotation = this.findAutowiredAnnotation(candidate);
                        if (annotation != null) {
                            if (requiredConstructor != null) {
                                throw new BeanCreationException("Invalid autowire-marked constructor: " + candidate + ". Found another constructor with 'required' Autowired annotation: " + requiredConstructor);
                            }
                            if (candidate.getParameterTypes().length == 0) {
                                throw new IllegalStateException("Autowired annotation requires at least one argument: " + candidate);
                            }
                            final boolean required = this.determineRequiredStatus(annotation);
                            if (required) {
                                if (!candidates.isEmpty()) {
                                    throw new BeanCreationException("Invalid autowire-marked constructors: " + candidates + ". Found another constructor with 'required' Autowired annotation: " + requiredConstructor);
                                }
                                requiredConstructor = candidate;
                            }
                            candidates.add(candidate);
                        }
                        else if (candidate.getParameterTypes().length == 0) {
                            defaultConstructor = candidate;
                        }
                    }
                    if (!candidates.isEmpty()) {
                        if (requiredConstructor == null && defaultConstructor != null) {
                            candidates.add(defaultConstructor);
                        }
                        candidateConstructors = candidates.toArray(new Constructor[candidates.size()]);
                    }
                    else {
                        candidateConstructors = (Constructor<?>[])new Constructor[0];
                    }
                    this.candidateConstructorsCache.put(beanClass, candidateConstructors);
                }
            }
        }
        return (Constructor<?>[])((candidateConstructors.length > 0) ? candidateConstructors : null);
    }
    
    @Override
    public PropertyValues postProcessPropertyValues(final PropertyValues pvs, final PropertyDescriptor[] pds, final Object bean, final String beanName) throws BeansException {
        final InjectionMetadata metadata = this.findAutowiringMetadata(beanName, bean.getClass());
        try {
            metadata.inject(bean, beanName, pvs);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
        }
        return pvs;
    }
    
    public void processInjection(final Object bean) throws BeansException {
        final Class<?> clazz = bean.getClass();
        final InjectionMetadata metadata = this.findAutowiringMetadata(clazz.getName(), clazz);
        try {
            metadata.inject(bean, null, null);
        }
        catch (Throwable ex) {
            throw new BeanCreationException("Injection of autowired dependencies failed for class [" + clazz + "]", ex);
        }
    }
    
    private InjectionMetadata findAutowiringMetadata(final String beanName, final Class<?> clazz) {
        final String cacheKey = StringUtils.hasLength(beanName) ? beanName : clazz.getName();
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    metadata = this.buildAutowiringMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }
    
    private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
        final LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
        Class<?> targetClass = clazz;
        do {
            final LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<InjectionMetadata.InjectedElement>();
            for (final Field field : targetClass.getDeclaredFields()) {
                final AnnotationAttributes annotation = this.findAutowiredAnnotation(field);
                if (annotation != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        if (this.logger.isWarnEnabled()) {
                            this.logger.warn("Autowired annotation is not supported on static fields: " + field);
                        }
                    }
                    else {
                        final boolean required = this.determineRequiredStatus(annotation);
                        currElements.add(new AutowiredFieldElement(field, required));
                    }
                }
            }
            for (final Method method : targetClass.getDeclaredMethods()) {
                final Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                final AnnotationAttributes annotation2 = BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod) ? this.findAutowiredAnnotation(bridgedMethod) : this.findAutowiredAnnotation(method);
                if (annotation2 != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        if (this.logger.isWarnEnabled()) {
                            this.logger.warn("Autowired annotation is not supported on static methods: " + method);
                        }
                    }
                    else {
                        if (method.getParameterTypes().length == 0 && this.logger.isWarnEnabled()) {
                            this.logger.warn("Autowired annotation should be used on methods with actual parameters: " + method);
                        }
                        final boolean required2 = this.determineRequiredStatus(annotation2);
                        final PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
                        currElements.add(new AutowiredMethodElement(method, required2, pd));
                    }
                }
            }
            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
        return new InjectionMetadata(clazz, elements);
    }
    
    private AnnotationAttributes findAutowiredAnnotation(final AccessibleObject ao) {
        for (final Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
            final AnnotationAttributes annotation = AnnotatedElementUtils.getAnnotationAttributes(ao, type.getName());
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }
    
    protected boolean determineRequiredStatus(final AnnotationAttributes annotation) {
        return !annotation.containsKey(this.requiredParameterName) || this.requiredParameterValue == annotation.getBoolean(this.requiredParameterName);
    }
    
    protected <T> Map<String, T> findAutowireCandidates(final Class<T> type) throws BeansException {
        if (this.beanFactory == null) {
            throw new IllegalStateException("No BeanFactory configured - override the getBeanOfType method or specify the 'beanFactory' property");
        }
        return BeanFactoryUtils.beansOfTypeIncludingAncestors(this.beanFactory, type);
    }
    
    private void registerDependentBeans(final String beanName, final Set<String> autowiredBeanNames) {
        if (beanName != null) {
            for (final String autowiredBeanName : autowiredBeanNames) {
                if (this.beanFactory.containsBean(autowiredBeanName)) {
                    this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Autowiring by type from bean name '" + beanName + "' to bean named '" + autowiredBeanName + "'");
                }
            }
        }
    }
    
    private Object resolvedCachedArgument(final String beanName, final Object cachedArgument) {
        if (cachedArgument instanceof DependencyDescriptor) {
            final DependencyDescriptor descriptor = (DependencyDescriptor)cachedArgument;
            return this.beanFactory.resolveDependency(descriptor, beanName, null, null);
        }
        if (cachedArgument instanceof RuntimeBeanReference) {
            return this.beanFactory.getBean(((RuntimeBeanReference)cachedArgument).getBeanName());
        }
        return cachedArgument;
    }
    
    private class AutowiredFieldElement extends InjectionMetadata.InjectedElement
    {
        private final boolean required;
        private volatile boolean cached;
        private volatile Object cachedFieldValue;
        
        public AutowiredFieldElement(final Field field, final boolean required) {
            super(field, null);
            this.cached = false;
            this.required = required;
        }
        
        @Override
        protected void inject(final Object bean, final String beanName, final PropertyValues pvs) throws Throwable {
            final Field field = (Field)this.member;
            try {
                Object value;
                if (this.cached) {
                    value = AutowiredAnnotationBeanPostProcessor.this.resolvedCachedArgument(beanName, this.cachedFieldValue);
                }
                else {
                    final DependencyDescriptor desc = new DependencyDescriptor(field, this.required);
                    desc.setContainingClass(bean.getClass());
                    final Set<String> autowiredBeanNames = new LinkedHashSet<String>(1);
                    final TypeConverter typeConverter = AutowiredAnnotationBeanPostProcessor.this.beanFactory.getTypeConverter();
                    value = AutowiredAnnotationBeanPostProcessor.this.beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
                    synchronized (this) {
                        if (!this.cached) {
                            if (value != null || this.required) {
                                this.cachedFieldValue = desc;
                                AutowiredAnnotationBeanPostProcessor.this.registerDependentBeans(beanName, autowiredBeanNames);
                                if (autowiredBeanNames.size() == 1) {
                                    final String autowiredBeanName = autowiredBeanNames.iterator().next();
                                    if (AutowiredAnnotationBeanPostProcessor.this.beanFactory.containsBean(autowiredBeanName) && AutowiredAnnotationBeanPostProcessor.this.beanFactory.isTypeMatch(autowiredBeanName, field.getType())) {
                                        this.cachedFieldValue = new RuntimeBeanReference(autowiredBeanName);
                                    }
                                }
                            }
                            else {
                                this.cachedFieldValue = null;
                            }
                            this.cached = true;
                        }
                    }
                }
                if (value != null) {
                    ReflectionUtils.makeAccessible(field);
                    field.set(bean, value);
                }
            }
            catch (Throwable ex) {
                throw new BeanCreationException("Could not autowire field: " + field, ex);
            }
        }
    }
    
    private class AutowiredMethodElement extends InjectionMetadata.InjectedElement
    {
        private final boolean required;
        private volatile boolean cached;
        private volatile Object[] cachedMethodArguments;
        
        public AutowiredMethodElement(final Method method, final boolean required, final PropertyDescriptor pd) {
            super(method, pd);
            this.cached = false;
            this.required = required;
        }
        
        @Override
        protected void inject(final Object bean, final String beanName, final PropertyValues pvs) throws Throwable {
            if (this.checkPropertySkipping(pvs)) {
                return;
            }
            final Method method = (Method)this.member;
            try {
                Object[] arguments;
                if (this.cached) {
                    arguments = this.resolveCachedArguments(beanName);
                }
                else {
                    final Class<?>[] paramTypes = method.getParameterTypes();
                    arguments = new Object[paramTypes.length];
                    final DependencyDescriptor[] descriptors = new DependencyDescriptor[paramTypes.length];
                    final Set<String> autowiredBeanNames = new LinkedHashSet<String>(paramTypes.length);
                    final TypeConverter typeConverter = AutowiredAnnotationBeanPostProcessor.this.beanFactory.getTypeConverter();
                    for (int i = 0; i < arguments.length; ++i) {
                        final MethodParameter methodParam = new MethodParameter(method, i);
                        final DependencyDescriptor desc = new DependencyDescriptor(methodParam, this.required);
                        desc.setContainingClass(bean.getClass());
                        descriptors[i] = desc;
                        final Object arg = AutowiredAnnotationBeanPostProcessor.this.beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
                        if (arg == null && !this.required) {
                            arguments = null;
                            break;
                        }
                        arguments[i] = arg;
                    }
                    synchronized (this) {
                        if (!this.cached) {
                            if (arguments != null) {
                                this.cachedMethodArguments = new Object[arguments.length];
                                for (int j = 0; j < arguments.length; ++j) {
                                    this.cachedMethodArguments[j] = descriptors[j];
                                }
                                AutowiredAnnotationBeanPostProcessor.this.registerDependentBeans(beanName, autowiredBeanNames);
                                if (autowiredBeanNames.size() == paramTypes.length) {
                                    final Iterator<String> it = autowiredBeanNames.iterator();
                                    for (int k = 0; k < paramTypes.length; ++k) {
                                        final String autowiredBeanName = it.next();
                                        if (AutowiredAnnotationBeanPostProcessor.this.beanFactory.containsBean(autowiredBeanName) && AutowiredAnnotationBeanPostProcessor.this.beanFactory.isTypeMatch(autowiredBeanName, paramTypes[k])) {
                                            this.cachedMethodArguments[k] = new RuntimeBeanReference(autowiredBeanName);
                                        }
                                    }
                                }
                            }
                            else {
                                this.cachedMethodArguments = null;
                            }
                            this.cached = true;
                        }
                    }
                }
                if (arguments != null) {
                    ReflectionUtils.makeAccessible(method);
                    method.invoke(bean, arguments);
                }
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
            catch (Throwable ex2) {
                throw new BeanCreationException("Could not autowire method: " + method, ex2);
            }
        }
        
        private Object[] resolveCachedArguments(final String beanName) {
            if (this.cachedMethodArguments == null) {
                return null;
            }
            final Object[] arguments = new Object[this.cachedMethodArguments.length];
            for (int i = 0; i < arguments.length; ++i) {
                arguments[i] = AutowiredAnnotationBeanPostProcessor.this.resolvedCachedArgument(beanName, this.cachedMethodArguments[i]);
            }
            return arguments;
        }
    }
}
