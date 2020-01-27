// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.util.ClassUtils;
import java.util.Collections;
import java.util.Map;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import java.util.Comparator;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import java.util.Set;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class AnnotationConfigUtils
{
    public static final String CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalConfigurationAnnotationProcessor";
    public static final String CONFIGURATION_BEAN_NAME_GENERATOR = "org.springframework.context.annotation.internalConfigurationBeanNameGenerator";
    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalAutowiredAnnotationProcessor";
    public static final String REQUIRED_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalRequiredAnnotationProcessor";
    public static final String COMMON_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalCommonAnnotationProcessor";
    public static final String SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalScheduledAnnotationProcessor";
    public static final String ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalAsyncAnnotationProcessor";
    public static final String ASYNC_EXECUTION_ASPECT_BEAN_NAME = "org.springframework.scheduling.config.internalAsyncExecutionAspect";
    public static final String ASYNC_EXECUTION_ASPECT_CLASS_NAME = "org.springframework.scheduling.aspectj.AnnotationAsyncExecutionAspect";
    public static final String ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME = "org.springframework.scheduling.aspectj.AspectJAsyncConfiguration";
    public static final String CACHE_ADVISOR_BEAN_NAME = "org.springframework.cache.config.internalCacheAdvisor";
    public static final String CACHE_ASPECT_BEAN_NAME = "org.springframework.cache.config.internalCacheAspect";
    public static final String CACHE_ASPECT_CLASS_NAME = "org.springframework.cache.aspectj.AnnotationCacheAspect";
    public static final String CACHE_ASPECT_CONFIGURATION_CLASS_NAME = "org.springframework.cache.aspectj.AspectJCachingConfiguration";
    public static final String PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalPersistenceAnnotationProcessor";
    private static final String PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME = "org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor";
    private static final boolean jsr250Present;
    private static final boolean jpaPresent;
    
    public static void registerAnnotationConfigProcessors(final BeanDefinitionRegistry registry) {
        registerAnnotationConfigProcessors(registry, null);
    }
    
    public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(final BeanDefinitionRegistry registry, final Object source) {
        final DefaultListableBeanFactory beanFactory = unwrapDefaultListableBeanFactory(registry);
        if (beanFactory != null) {
            if (!(beanFactory.getDependencyComparator() instanceof AnnotationAwareOrderComparator)) {
                beanFactory.setDependencyComparator(AnnotationAwareOrderComparator.INSTANCE);
            }
            if (!(beanFactory.getAutowireCandidateResolver() instanceof ContextAnnotationAutowireCandidateResolver)) {
                beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
            }
        }
        final Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<BeanDefinitionHolder>(4);
        if (!registry.containsBeanDefinition("org.springframework.context.annotation.internalConfigurationAnnotationProcessor")) {
            final RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, "org.springframework.context.annotation.internalConfigurationAnnotationProcessor"));
        }
        if (!registry.containsBeanDefinition("org.springframework.context.annotation.internalAutowiredAnnotationProcessor")) {
            final RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, "org.springframework.context.annotation.internalAutowiredAnnotationProcessor"));
        }
        if (!registry.containsBeanDefinition("org.springframework.context.annotation.internalRequiredAnnotationProcessor")) {
            final RootBeanDefinition def = new RootBeanDefinition(RequiredAnnotationBeanPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, "org.springframework.context.annotation.internalRequiredAnnotationProcessor"));
        }
        if (AnnotationConfigUtils.jsr250Present && !registry.containsBeanDefinition("org.springframework.context.annotation.internalCommonAnnotationProcessor")) {
            final RootBeanDefinition def = new RootBeanDefinition(CommonAnnotationBeanPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, "org.springframework.context.annotation.internalCommonAnnotationProcessor"));
        }
        if (AnnotationConfigUtils.jpaPresent && !registry.containsBeanDefinition("org.springframework.context.annotation.internalPersistenceAnnotationProcessor")) {
            final RootBeanDefinition def = new RootBeanDefinition();
            try {
                final ClassLoader cl = AnnotationConfigUtils.class.getClassLoader();
                def.setBeanClass(cl.loadClass("org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor"));
            }
            catch (ClassNotFoundException ex) {
                throw new IllegalStateException("Cannot load optional framework class: org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor", ex);
            }
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, "org.springframework.context.annotation.internalPersistenceAnnotationProcessor"));
        }
        return beanDefs;
    }
    
    private static BeanDefinitionHolder registerPostProcessor(final BeanDefinitionRegistry registry, final RootBeanDefinition definition, final String beanName) {
        definition.setRole(2);
        registry.registerBeanDefinition(beanName, definition);
        return new BeanDefinitionHolder(definition, beanName);
    }
    
    private static DefaultListableBeanFactory unwrapDefaultListableBeanFactory(final BeanDefinitionRegistry registry) {
        if (registry instanceof DefaultListableBeanFactory) {
            return (DefaultListableBeanFactory)registry;
        }
        if (registry instanceof GenericApplicationContext) {
            return ((GenericApplicationContext)registry).getDefaultListableBeanFactory();
        }
        return null;
    }
    
    public static void processCommonDefinitionAnnotations(final AnnotatedBeanDefinition abd) {
        processCommonDefinitionAnnotations(abd, abd.getMetadata());
    }
    
    static void processCommonDefinitionAnnotations(final AnnotatedBeanDefinition abd, final AnnotatedTypeMetadata metadata) {
        if (metadata.isAnnotated(Lazy.class.getName())) {
            abd.setLazyInit(attributesFor(metadata, Lazy.class).getBoolean("value"));
        }
        else if (abd.getMetadata().isAnnotated(Lazy.class.getName())) {
            abd.setLazyInit(attributesFor(abd.getMetadata(), Lazy.class).getBoolean("value"));
        }
        if (metadata.isAnnotated(Primary.class.getName())) {
            abd.setPrimary(true);
        }
        if (metadata.isAnnotated(DependsOn.class.getName())) {
            abd.setDependsOn(attributesFor(metadata, DependsOn.class).getStringArray("value"));
        }
        if (abd instanceof AbstractBeanDefinition) {
            final AbstractBeanDefinition absBd = (AbstractBeanDefinition)abd;
            if (metadata.isAnnotated(Role.class.getName())) {
                absBd.setRole(attributesFor(metadata, Role.class).getNumber("value").intValue());
            }
            if (metadata.isAnnotated(Description.class.getName())) {
                absBd.setDescription(attributesFor(metadata, Description.class).getString("value"));
            }
        }
    }
    
    static BeanDefinitionHolder applyScopedProxyMode(final ScopeMetadata metadata, final BeanDefinitionHolder definition, final BeanDefinitionRegistry registry) {
        final ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
        if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
            return definition;
        }
        final boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
        return ScopedProxyCreator.createScopedProxy(definition, registry, proxyTargetClass);
    }
    
    static AnnotationAttributes attributesFor(final AnnotatedTypeMetadata metadata, final Class<?> annotationClass) {
        return attributesFor(metadata, annotationClass.getName());
    }
    
    static AnnotationAttributes attributesFor(final AnnotatedTypeMetadata metadata, final String annotationClassName) {
        return AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(annotationClassName, false));
    }
    
    static Set<AnnotationAttributes> attributesForRepeatable(final AnnotationMetadata metadata, final Class<?> containerClass, final Class<?> annotationClass) {
        return attributesForRepeatable(metadata, containerClass.getName(), annotationClass.getName());
    }
    
    static Set<AnnotationAttributes> attributesForRepeatable(final AnnotationMetadata metadata, final String containerClassName, final String annotationClassName) {
        final Set<AnnotationAttributes> result = new LinkedHashSet<AnnotationAttributes>();
        addAttributesIfNotNull(result, metadata.getAnnotationAttributes(annotationClassName, false));
        final Map<String, Object> container = metadata.getAnnotationAttributes(containerClassName, false);
        if (container != null && container.containsKey("value")) {
            for (final Map<String, Object> containedAttributes : container.get("value")) {
                addAttributesIfNotNull(result, containedAttributes);
            }
        }
        return Collections.unmodifiableSet((Set<? extends AnnotationAttributes>)result);
    }
    
    private static void addAttributesIfNotNull(final Set<AnnotationAttributes> result, final Map<String, Object> attributes) {
        if (attributes != null) {
            result.add(AnnotationAttributes.fromMap(attributes));
        }
    }
    
    static {
        jsr250Present = ClassUtils.isPresent("javax.annotation.Resource", AnnotationConfigUtils.class.getClassLoader());
        jpaPresent = (ClassUtils.isPresent("javax.persistence.EntityManagerFactory", AnnotationConfigUtils.class.getClassLoader()) && ClassUtils.isPresent("org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor", AnnotationConfigUtils.class.getClassLoader()));
    }
}
