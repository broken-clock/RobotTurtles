// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.util.PatternMatchUtils;
import java.util.Iterator;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import java.util.Set;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider
{
    private final BeanDefinitionRegistry registry;
    private BeanDefinitionDefaults beanDefinitionDefaults;
    private String[] autowireCandidatePatterns;
    private BeanNameGenerator beanNameGenerator;
    private ScopeMetadataResolver scopeMetadataResolver;
    private boolean includeAnnotationConfig;
    
    public ClassPathBeanDefinitionScanner(final BeanDefinitionRegistry registry) {
        this(registry, true);
    }
    
    public ClassPathBeanDefinitionScanner(final BeanDefinitionRegistry registry, final boolean useDefaultFilters) {
        this(registry, useDefaultFilters, getOrCreateEnvironment(registry));
    }
    
    public ClassPathBeanDefinitionScanner(final BeanDefinitionRegistry registry, final boolean useDefaultFilters, final Environment environment) {
        super(useDefaultFilters, environment);
        this.beanDefinitionDefaults = new BeanDefinitionDefaults();
        this.beanNameGenerator = new AnnotationBeanNameGenerator();
        this.scopeMetadataResolver = new AnnotationScopeMetadataResolver();
        this.includeAnnotationConfig = true;
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        this.registry = registry;
        if (this.registry instanceof ResourceLoader) {
            this.setResourceLoader((ResourceLoader)this.registry);
        }
    }
    
    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }
    
    public void setBeanDefinitionDefaults(final BeanDefinitionDefaults beanDefinitionDefaults) {
        this.beanDefinitionDefaults = ((beanDefinitionDefaults != null) ? beanDefinitionDefaults : new BeanDefinitionDefaults());
    }
    
    public void setAutowireCandidatePatterns(final String[] autowireCandidatePatterns) {
        this.autowireCandidatePatterns = autowireCandidatePatterns;
    }
    
    public void setBeanNameGenerator(final BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = ((beanNameGenerator != null) ? beanNameGenerator : new AnnotationBeanNameGenerator());
    }
    
    public void setScopeMetadataResolver(final ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver = ((scopeMetadataResolver != null) ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
    }
    
    public void setScopedProxyMode(final ScopedProxyMode scopedProxyMode) {
        this.scopeMetadataResolver = new AnnotationScopeMetadataResolver(scopedProxyMode);
    }
    
    public void setIncludeAnnotationConfig(final boolean includeAnnotationConfig) {
        this.includeAnnotationConfig = includeAnnotationConfig;
    }
    
    public int scan(final String... basePackages) {
        final int beanCountAtScanStart = this.registry.getBeanDefinitionCount();
        this.doScan(basePackages);
        if (this.includeAnnotationConfig) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
        }
        return this.registry.getBeanDefinitionCount() - beanCountAtScanStart;
    }
    
    protected Set<BeanDefinitionHolder> doScan(final String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        final Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<BeanDefinitionHolder>();
        for (final String basePackage : basePackages) {
            final Set<BeanDefinition> candidates = this.findCandidateComponents(basePackage);
            for (final BeanDefinition candidate : candidates) {
                final ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
                candidate.setScope(scopeMetadata.getScopeName());
                final String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
                if (candidate instanceof AbstractBeanDefinition) {
                    this.postProcessBeanDefinition((AbstractBeanDefinition)candidate, beanName);
                }
                if (candidate instanceof AnnotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition)candidate);
                }
                if (this.checkCandidate(beanName, candidate)) {
                    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                    definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
                    beanDefinitions.add(definitionHolder);
                    this.registerBeanDefinition(definitionHolder, this.registry);
                }
            }
        }
        return beanDefinitions;
    }
    
    protected void postProcessBeanDefinition(final AbstractBeanDefinition beanDefinition, final String beanName) {
        beanDefinition.applyDefaults(this.beanDefinitionDefaults);
        if (this.autowireCandidatePatterns != null) {
            beanDefinition.setAutowireCandidate(PatternMatchUtils.simpleMatch(this.autowireCandidatePatterns, beanName));
        }
    }
    
    protected void registerBeanDefinition(final BeanDefinitionHolder definitionHolder, final BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }
    
    protected boolean checkCandidate(final String beanName, final BeanDefinition beanDefinition) throws IllegalStateException {
        if (!this.registry.containsBeanDefinition(beanName)) {
            return true;
        }
        BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);
        final BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
        if (originatingDef != null) {
            existingDef = originatingDef;
        }
        if (this.isCompatible(beanDefinition, existingDef)) {
            return false;
        }
        throw new ConflictingBeanDefinitionException("Annotation-specified bean name '" + beanName + "' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " + "non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
    }
    
    protected boolean isCompatible(final BeanDefinition newDefinition, final BeanDefinition existingDefinition) {
        return !(existingDefinition instanceof ScannedGenericBeanDefinition) || newDefinition.getSource().equals(existingDefinition.getSource()) || newDefinition.equals(existingDefinition);
    }
    
    private static Environment getOrCreateEnvironment(final BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        if (registry instanceof EnvironmentCapable) {
            return ((EnvironmentCapable)registry).getEnvironment();
        }
        return new StandardEnvironment();
    }
}
