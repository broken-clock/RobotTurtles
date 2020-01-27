// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import java.lang.annotation.Annotation;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class AnnotatedBeanDefinitionReader
{
    private final BeanDefinitionRegistry registry;
    private BeanNameGenerator beanNameGenerator;
    private ScopeMetadataResolver scopeMetadataResolver;
    private ConditionEvaluator conditionEvaluator;
    
    public AnnotatedBeanDefinitionReader(final BeanDefinitionRegistry registry) {
        this(registry, getOrCreateEnvironment(registry));
    }
    
    public AnnotatedBeanDefinitionReader(final BeanDefinitionRegistry registry, final Environment environment) {
        this.beanNameGenerator = new AnnotationBeanNameGenerator();
        this.scopeMetadataResolver = new AnnotationScopeMetadataResolver();
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        Assert.notNull(environment, "Environment must not be null");
        this.registry = registry;
        this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
    }
    
    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }
    
    public void setEnvironment(final Environment environment) {
        this.conditionEvaluator = new ConditionEvaluator(this.registry, environment, null);
    }
    
    public void setBeanNameGenerator(final BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = ((beanNameGenerator != null) ? beanNameGenerator : new AnnotationBeanNameGenerator());
    }
    
    public void setScopeMetadataResolver(final ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver = ((scopeMetadataResolver != null) ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
    }
    
    public void register(final Class<?>... annotatedClasses) {
        for (final Class<?> annotatedClass : annotatedClasses) {
            this.registerBean(annotatedClass);
        }
    }
    
    public void registerBean(final Class<?> annotatedClass) {
        this.registerBean(annotatedClass, (String)null, (Class<? extends Annotation>[])null);
    }
    
    public void registerBean(final Class<?> annotatedClass, final Class<? extends Annotation>... qualifiers) {
        this.registerBean(annotatedClass, (String)null, qualifiers);
    }
    
    public void registerBean(final Class<?> annotatedClass, final String name, final Class<? extends Annotation>... qualifiers) {
        final AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(annotatedClass);
        if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
            return;
        }
        final ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());
        final String beanName = (name != null) ? name : this.beanNameGenerator.generateBeanName(abd, this.registry);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
        if (qualifiers != null) {
            for (final Class<? extends Annotation> qualifier : qualifiers) {
                if (Primary.class.equals(qualifier)) {
                    abd.setPrimary(true);
                }
                else if (Lazy.class.equals(qualifier)) {
                    abd.setLazyInit(true);
                }
                else {
                    abd.addQualifier(new AutowireCandidateQualifier(qualifier));
                }
            }
        }
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
        definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
    }
    
    private static Environment getOrCreateEnvironment(final BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        if (registry instanceof EnvironmentCapable) {
            return ((EnvironmentCapable)registry).getEnvironment();
        }
        return new StandardEnvironment();
    }
}
