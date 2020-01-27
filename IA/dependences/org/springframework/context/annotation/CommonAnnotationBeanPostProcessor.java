// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.core.MethodParameter;
import javax.ejb.EJB;
import java.net.MalformedURLException;
import java.lang.reflect.Constructor;
import javax.xml.ws.WebServiceClient;
import javax.xml.namespace.QName;
import java.net.URL;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceRef;
import java.beans.Introspector;
import java.lang.reflect.AnnotatedElement;
import org.springframework.beans.factory.config.DependencyDescriptor;
import java.util.Iterator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import java.util.Collections;
import org.springframework.beans.TypeConverter;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Collection;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.core.BridgeMethodResolver;
import javax.annotation.Resource;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.BeanCreationException;
import java.beans.PropertyDescriptor;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.BeansException;
import org.springframework.util.Assert;
import javax.annotation.PreDestroy;
import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.jndi.support.SimpleJndiBeanFactory;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import java.util.Map;
import org.springframework.beans.factory.BeanFactory;
import java.util.Set;
import java.lang.annotation.Annotation;
import java.io.Serializable;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;

public class CommonAnnotationBeanPostProcessor extends InitDestroyAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware, Serializable
{
    private static Class<? extends Annotation> webServiceRefClass;
    private static Class<? extends Annotation> ejbRefClass;
    private final Set<String> ignoredResourceTypes;
    private boolean fallbackToDefaultTypeMatch;
    private boolean alwaysUseJndiLookup;
    private transient BeanFactory jndiFactory;
    private transient BeanFactory resourceFactory;
    private transient BeanFactory beanFactory;
    private final transient Map<String, InjectionMetadata> injectionMetadataCache;
    
    public CommonAnnotationBeanPostProcessor() {
        this.ignoredResourceTypes = new HashSet<String>(1);
        this.fallbackToDefaultTypeMatch = true;
        this.alwaysUseJndiLookup = false;
        this.jndiFactory = new SimpleJndiBeanFactory();
        this.injectionMetadataCache = new ConcurrentHashMap<String, InjectionMetadata>(64);
        this.setOrder(2147483644);
        this.setInitAnnotationType((Class<? extends Annotation>)PostConstruct.class);
        this.setDestroyAnnotationType((Class<? extends Annotation>)PreDestroy.class);
        this.ignoreResourceType("javax.xml.ws.WebServiceContext");
    }
    
    public void ignoreResourceType(final String resourceType) {
        Assert.notNull(resourceType, "Ignored resource type must not be null");
        this.ignoredResourceTypes.add(resourceType);
    }
    
    public void setFallbackToDefaultTypeMatch(final boolean fallbackToDefaultTypeMatch) {
        this.fallbackToDefaultTypeMatch = fallbackToDefaultTypeMatch;
    }
    
    public void setAlwaysUseJndiLookup(final boolean alwaysUseJndiLookup) {
        this.alwaysUseJndiLookup = alwaysUseJndiLookup;
    }
    
    public void setJndiFactory(final BeanFactory jndiFactory) {
        Assert.notNull(jndiFactory, "BeanFactory must not be null");
        this.jndiFactory = jndiFactory;
    }
    
    public void setResourceFactory(final BeanFactory resourceFactory) {
        Assert.notNull(resourceFactory, "BeanFactory must not be null");
        this.resourceFactory = resourceFactory;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
        if (this.resourceFactory == null) {
            this.resourceFactory = beanFactory;
        }
    }
    
    @Override
    public void postProcessMergedBeanDefinition(final RootBeanDefinition beanDefinition, final Class<?> beanType, final String beanName) {
        super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
        if (beanType != null) {
            final InjectionMetadata metadata = this.findResourceMetadata(beanName, beanType);
            metadata.checkConfigMembers(beanDefinition);
        }
    }
    
    @Override
    public Object postProcessBeforeInstantiation(final Class<?> beanClass, final String beanName) throws BeansException {
        return null;
    }
    
    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
        return true;
    }
    
    @Override
    public PropertyValues postProcessPropertyValues(final PropertyValues pvs, final PropertyDescriptor[] pds, final Object bean, final String beanName) throws BeansException {
        final InjectionMetadata metadata = this.findResourceMetadata(beanName, bean.getClass());
        try {
            metadata.inject(bean, beanName, pvs);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of resource dependencies failed", ex);
        }
        return pvs;
    }
    
    private InjectionMetadata findResourceMetadata(final String beanName, final Class<?> clazz) {
        final String cacheKey = StringUtils.hasLength(beanName) ? beanName : clazz.getName();
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    final LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
                    Class<?> targetClass = clazz;
                    do {
                        final LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<InjectionMetadata.InjectedElement>();
                        for (final Field field : targetClass.getDeclaredFields()) {
                            if (CommonAnnotationBeanPostProcessor.webServiceRefClass != null && field.isAnnotationPresent(CommonAnnotationBeanPostProcessor.webServiceRefClass)) {
                                if (Modifier.isStatic(field.getModifiers())) {
                                    throw new IllegalStateException("@WebServiceRef annotation is not supported on static fields");
                                }
                                currElements.add(new WebServiceRefElement(field, null));
                            }
                            else if (CommonAnnotationBeanPostProcessor.ejbRefClass != null && field.isAnnotationPresent(CommonAnnotationBeanPostProcessor.ejbRefClass)) {
                                if (Modifier.isStatic(field.getModifiers())) {
                                    throw new IllegalStateException("@EJB annotation is not supported on static fields");
                                }
                                currElements.add(new EjbRefElement(field, null));
                            }
                            else if (field.isAnnotationPresent((Class<? extends Annotation>)Resource.class)) {
                                if (Modifier.isStatic(field.getModifiers())) {
                                    throw new IllegalStateException("@Resource annotation is not supported on static fields");
                                }
                                if (!this.ignoredResourceTypes.contains(field.getType().getName())) {
                                    currElements.add(new ResourceElement(field, null));
                                }
                            }
                        }
                        for (Method method : targetClass.getDeclaredMethods()) {
                            method = BridgeMethodResolver.findBridgedMethod(method);
                            final Method mostSpecificMethod = BridgeMethodResolver.findBridgedMethod(ClassUtils.getMostSpecificMethod(method, clazz));
                            if (method.equals(mostSpecificMethod)) {
                                if (CommonAnnotationBeanPostProcessor.webServiceRefClass != null && method.isAnnotationPresent(CommonAnnotationBeanPostProcessor.webServiceRefClass)) {
                                    if (Modifier.isStatic(method.getModifiers())) {
                                        throw new IllegalStateException("@WebServiceRef annotation is not supported on static methods");
                                    }
                                    if (method.getParameterTypes().length != 1) {
                                        throw new IllegalStateException("@WebServiceRef annotation requires a single-arg method: " + method);
                                    }
                                    final PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
                                    currElements.add(new WebServiceRefElement(method, pd));
                                }
                                else if (CommonAnnotationBeanPostProcessor.ejbRefClass != null && method.isAnnotationPresent(CommonAnnotationBeanPostProcessor.ejbRefClass)) {
                                    if (Modifier.isStatic(method.getModifiers())) {
                                        throw new IllegalStateException("@EJB annotation is not supported on static methods");
                                    }
                                    if (method.getParameterTypes().length != 1) {
                                        throw new IllegalStateException("@EJB annotation requires a single-arg method: " + method);
                                    }
                                    final PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
                                    currElements.add(new EjbRefElement(method, pd));
                                }
                                else if (method.isAnnotationPresent((Class<? extends Annotation>)Resource.class)) {
                                    if (Modifier.isStatic(method.getModifiers())) {
                                        throw new IllegalStateException("@Resource annotation is not supported on static methods");
                                    }
                                    final Class<?>[] paramTypes = method.getParameterTypes();
                                    if (paramTypes.length != 1) {
                                        throw new IllegalStateException("@Resource annotation requires a single-arg method: " + method);
                                    }
                                    if (!this.ignoredResourceTypes.contains(paramTypes[0].getName())) {
                                        final PropertyDescriptor pd2 = BeanUtils.findPropertyForMethod(method);
                                        currElements.add(new ResourceElement(method, pd2));
                                    }
                                }
                            }
                        }
                        elements.addAll(0, currElements);
                        targetClass = targetClass.getSuperclass();
                    } while (targetClass != null && targetClass != Object.class);
                    metadata = new InjectionMetadata(clazz, elements);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }
    
    protected Object getResource(final LookupElement element, final String requestingBeanName) throws BeansException {
        if (StringUtils.hasLength(element.mappedName)) {
            return this.jndiFactory.getBean(element.mappedName, element.lookupType);
        }
        if (this.alwaysUseJndiLookup) {
            return this.jndiFactory.getBean(element.name, element.lookupType);
        }
        if (this.resourceFactory == null) {
            throw new NoSuchBeanDefinitionException(element.lookupType, "No resource factory configured - specify the 'resourceFactory' property");
        }
        return this.autowireResource(this.resourceFactory, element, requestingBeanName);
    }
    
    protected Object autowireResource(final BeanFactory factory, final LookupElement element, final String requestingBeanName) throws BeansException {
        final String name = element.name;
        Set<String> autowiredBeanNames;
        Object resource;
        if (this.fallbackToDefaultTypeMatch && element.isDefaultName && factory instanceof AutowireCapableBeanFactory && !factory.containsBean(name)) {
            autowiredBeanNames = new LinkedHashSet<String>();
            resource = ((AutowireCapableBeanFactory)factory).resolveDependency(element.getDependencyDescriptor(), requestingBeanName, autowiredBeanNames, null);
        }
        else {
            resource = factory.getBean(name, element.lookupType);
            autowiredBeanNames = Collections.singleton(name);
        }
        if (factory instanceof ConfigurableBeanFactory) {
            final ConfigurableBeanFactory beanFactory = (ConfigurableBeanFactory)factory;
            for (final String autowiredBeanName : autowiredBeanNames) {
                if (beanFactory.containsBean(autowiredBeanName)) {
                    beanFactory.registerDependentBean(autowiredBeanName, requestingBeanName);
                }
            }
        }
        return resource;
    }
    
    static {
        CommonAnnotationBeanPostProcessor.webServiceRefClass = null;
        CommonAnnotationBeanPostProcessor.ejbRefClass = null;
        final ClassLoader cl = CommonAnnotationBeanPostProcessor.class.getClassLoader();
        try {
            final Class<? extends Annotation> clazz = CommonAnnotationBeanPostProcessor.webServiceRefClass = (Class<? extends Annotation>)cl.loadClass("javax.xml.ws.WebServiceRef");
        }
        catch (ClassNotFoundException ex) {
            CommonAnnotationBeanPostProcessor.webServiceRefClass = null;
        }
        try {
            final Class<? extends Annotation> clazz = CommonAnnotationBeanPostProcessor.ejbRefClass = (Class<? extends Annotation>)cl.loadClass("javax.ejb.EJB");
        }
        catch (ClassNotFoundException ex) {
            CommonAnnotationBeanPostProcessor.ejbRefClass = null;
        }
    }
    
    protected abstract class LookupElement extends InjectionMetadata.InjectedElement
    {
        protected String name;
        protected boolean isDefaultName;
        protected Class<?> lookupType;
        protected String mappedName;
        
        public LookupElement(final Member member, final PropertyDescriptor pd) {
            super(member, pd);
            this.isDefaultName = false;
        }
        
        public final String getName() {
            return this.name;
        }
        
        public final Class<?> getLookupType() {
            return this.lookupType;
        }
        
        public final DependencyDescriptor getDependencyDescriptor() {
            if (this.isField) {
                return new LookupDependencyDescriptor((Field)this.member, this.lookupType);
            }
            return new LookupDependencyDescriptor((Method)this.member, this.lookupType);
        }
    }
    
    private class ResourceElement extends LookupElement
    {
        public ResourceElement(final Member member, final PropertyDescriptor pd) {
            super(member, pd);
            final AnnotatedElement ae = (AnnotatedElement)member;
            final Resource resource = ae.getAnnotation(Resource.class);
            String resourceName = resource.name();
            Class<?> resourceType = (Class<?>)resource.type();
            this.isDefaultName = !StringUtils.hasLength(resourceName);
            if (this.isDefaultName) {
                resourceName = this.member.getName();
                if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
                    resourceName = Introspector.decapitalize(resourceName.substring(3));
                }
            }
            else if (CommonAnnotationBeanPostProcessor.this.beanFactory instanceof ConfigurableBeanFactory) {
                resourceName = ((ConfigurableBeanFactory)CommonAnnotationBeanPostProcessor.this.beanFactory).resolveEmbeddedValue(resourceName);
            }
            if (resourceType != null && !Object.class.equals(resourceType)) {
                this.checkResourceType(resourceType);
            }
            else {
                resourceType = this.getResourceType();
            }
            this.name = resourceName;
            this.lookupType = resourceType;
            this.mappedName = resource.mappedName();
        }
        
        @Override
        protected Object getResourceToInject(final Object target, final String requestingBeanName) {
            return CommonAnnotationBeanPostProcessor.this.getResource(this, requestingBeanName);
        }
    }
    
    private class WebServiceRefElement extends LookupElement
    {
        private final Class<?> elementType;
        private final String wsdlLocation;
        
        public WebServiceRefElement(final Member member, final PropertyDescriptor pd) {
            super(member, pd);
            final AnnotatedElement ae = (AnnotatedElement)member;
            final WebServiceRef resource = ae.getAnnotation(WebServiceRef.class);
            String resourceName = resource.name();
            Class<?> resourceType = (Class<?>)resource.type();
            this.isDefaultName = !StringUtils.hasLength(resourceName);
            if (this.isDefaultName) {
                resourceName = this.member.getName();
                if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
                    resourceName = Introspector.decapitalize(resourceName.substring(3));
                }
            }
            if (resourceType != null && !Object.class.equals(resourceType)) {
                this.checkResourceType(resourceType);
            }
            else {
                resourceType = this.getResourceType();
            }
            this.name = resourceName;
            this.elementType = resourceType;
            if (Service.class.isAssignableFrom(resourceType)) {
                this.lookupType = resourceType;
            }
            else {
                this.lookupType = (Object.class.equals(resource.value()) ? Service.class : resource.value());
            }
            this.mappedName = resource.mappedName();
            this.wsdlLocation = resource.wsdlLocation();
        }
        
        @Override
        protected Object getResourceToInject(final Object target, final String requestingBeanName) {
            Service service;
            try {
                service = (Service)CommonAnnotationBeanPostProcessor.this.getResource(this, requestingBeanName);
            }
            catch (NoSuchBeanDefinitionException notFound) {
                if (Service.class.equals(this.lookupType)) {
                    throw new IllegalStateException("No resource with name '" + this.name + "' found in context, " + "and no specific JAX-WS Service subclass specified. The typical solution is to either specify " + "a LocalJaxWsServiceFactoryBean with the given name or to specify the (generated) Service " + "subclass as @WebServiceRef(...) value.");
                }
                if (StringUtils.hasLength(this.wsdlLocation)) {
                    try {
                        final Constructor<?> ctor = this.lookupType.getConstructor(URL.class, QName.class);
                        final WebServiceClient clientAnn = this.lookupType.getAnnotation(WebServiceClient.class);
                        if (clientAnn == null) {
                            throw new IllegalStateException("JAX-WS Service class [" + this.lookupType.getName() + "] does not carry a WebServiceClient annotation");
                        }
                        service = BeanUtils.instantiateClass(ctor, new URL(this.wsdlLocation), new QName(clientAnn.targetNamespace(), clientAnn.name()));
                        return service.getPort((Class)this.elementType);
                    }
                    catch (NoSuchMethodException ex) {
                        throw new IllegalStateException("JAX-WS Service class [" + this.lookupType.getName() + "] does not have a (URL, QName) constructor. Cannot apply specified WSDL location [" + this.wsdlLocation + "].");
                    }
                    catch (MalformedURLException ex2) {
                        throw new IllegalArgumentException("Specified WSDL location [" + this.wsdlLocation + "] isn't a valid URL");
                    }
                }
                service = BeanUtils.instantiateClass(this.lookupType);
            }
            return service.getPort((Class)this.elementType);
        }
    }
    
    private class EjbRefElement extends LookupElement
    {
        private final String beanName;
        
        public EjbRefElement(final Member member, final PropertyDescriptor pd) {
            super(member, pd);
            final AnnotatedElement ae = (AnnotatedElement)member;
            final EJB resource = ae.getAnnotation(EJB.class);
            final String resourceBeanName = resource.beanName();
            String resourceName = resource.name();
            this.isDefaultName = !StringUtils.hasLength(resourceName);
            if (this.isDefaultName) {
                resourceName = this.member.getName();
                if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
                    resourceName = Introspector.decapitalize(resourceName.substring(3));
                }
            }
            Class<?> resourceType = (Class<?>)resource.beanInterface();
            if (resourceType != null && !Object.class.equals(resourceType)) {
                this.checkResourceType(resourceType);
            }
            else {
                resourceType = this.getResourceType();
            }
            this.beanName = resourceBeanName;
            this.name = resourceName;
            this.lookupType = resourceType;
            this.mappedName = resource.mappedName();
        }
        
        @Override
        protected Object getResourceToInject(final Object target, final String requestingBeanName) {
            if (StringUtils.hasLength(this.beanName)) {
                if (CommonAnnotationBeanPostProcessor.this.beanFactory != null && CommonAnnotationBeanPostProcessor.this.beanFactory.containsBean(this.beanName)) {
                    final Object bean = CommonAnnotationBeanPostProcessor.this.beanFactory.getBean(this.beanName, this.lookupType);
                    if (CommonAnnotationBeanPostProcessor.this.beanFactory instanceof ConfigurableBeanFactory) {
                        ((ConfigurableBeanFactory)CommonAnnotationBeanPostProcessor.this.beanFactory).registerDependentBean(this.beanName, requestingBeanName);
                    }
                    return bean;
                }
                if (this.isDefaultName && !StringUtils.hasLength(this.mappedName)) {
                    throw new NoSuchBeanDefinitionException(this.beanName, "Cannot resolve 'beanName' in local BeanFactory. Consider specifying a general 'name' value instead.");
                }
            }
            return CommonAnnotationBeanPostProcessor.this.getResource(this, requestingBeanName);
        }
    }
    
    private static class LookupDependencyDescriptor extends DependencyDescriptor
    {
        private final Class<?> lookupType;
        
        public LookupDependencyDescriptor(final Field field, final Class<?> lookupType) {
            super(field, true);
            this.lookupType = lookupType;
        }
        
        public LookupDependencyDescriptor(final Method method, final Class<?> lookupType) {
            super(new MethodParameter(method, 0), true);
            this.lookupType = lookupType;
        }
        
        @Override
        public Class<?> getDependencyType() {
            return this.lookupType;
        }
    }
}
