// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.core.type.AnnotatedTypeMetadata;
import java.util.Iterator;
import org.springframework.util.ClassUtils;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.io.Resource;
import java.io.IOException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.config.BeanDefinition;
import java.util.Set;
import java.lang.annotation.Annotation;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.support.ResourcePatternUtils;
import java.util.LinkedList;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.filter.TypeFilter;
import java.util.List;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.env.Environment;
import org.apache.commons.logging.Log;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.EnvironmentCapable;

public class ClassPathScanningCandidateComponentProvider implements EnvironmentCapable, ResourceLoaderAware
{
    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    protected final Log logger;
    private Environment environment;
    private ResourcePatternResolver resourcePatternResolver;
    private MetadataReaderFactory metadataReaderFactory;
    private String resourcePattern;
    private final List<TypeFilter> includeFilters;
    private final List<TypeFilter> excludeFilters;
    private ConditionEvaluator conditionEvaluator;
    
    public ClassPathScanningCandidateComponentProvider(final boolean useDefaultFilters) {
        this(useDefaultFilters, new StandardEnvironment());
    }
    
    public ClassPathScanningCandidateComponentProvider(final boolean useDefaultFilters, final Environment environment) {
        this.logger = LogFactory.getLog(this.getClass());
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        this.metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
        this.resourcePattern = "**/*.class";
        this.includeFilters = new LinkedList<TypeFilter>();
        this.excludeFilters = new LinkedList<TypeFilter>();
        if (useDefaultFilters) {
            this.registerDefaultFilters();
        }
        this.environment = environment;
    }
    
    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }
    
    public final ResourceLoader getResourceLoader() {
        return this.resourcePatternResolver;
    }
    
    public void setMetadataReaderFactory(final MetadataReaderFactory metadataReaderFactory) {
        this.metadataReaderFactory = metadataReaderFactory;
    }
    
    public final MetadataReaderFactory getMetadataReaderFactory() {
        return this.metadataReaderFactory;
    }
    
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
        this.conditionEvaluator = null;
    }
    
    @Override
    public final Environment getEnvironment() {
        return this.environment;
    }
    
    protected BeanDefinitionRegistry getRegistry() {
        return null;
    }
    
    public void setResourcePattern(final String resourcePattern) {
        Assert.notNull(resourcePattern, "'resourcePattern' must not be null");
        this.resourcePattern = resourcePattern;
    }
    
    public void addIncludeFilter(final TypeFilter includeFilter) {
        this.includeFilters.add(includeFilter);
    }
    
    public void addExcludeFilter(final TypeFilter excludeFilter) {
        this.excludeFilters.add(0, excludeFilter);
    }
    
    public void resetFilters(final boolean useDefaultFilters) {
        this.includeFilters.clear();
        this.excludeFilters.clear();
        if (useDefaultFilters) {
            this.registerDefaultFilters();
        }
    }
    
    protected void registerDefaultFilters() {
        this.includeFilters.add(new AnnotationTypeFilter(Component.class));
        final ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
        try {
            this.includeFilters.add(new AnnotationTypeFilter((Class<? extends Annotation>)cl.loadClass("javax.annotation.ManagedBean"), false));
            this.logger.debug("JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
        }
        catch (ClassNotFoundException ex) {}
        try {
            this.includeFilters.add(new AnnotationTypeFilter((Class<? extends Annotation>)cl.loadClass("javax.inject.Named"), false));
            this.logger.debug("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
        }
        catch (ClassNotFoundException ex2) {}
    }
    
    public Set<BeanDefinition> findCandidateComponents(final String basePackage) {
        final Set<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
        try {
            final String packageSearchPath = "classpath*:" + this.resolveBasePackage(basePackage) + "/" + this.resourcePattern;
            final Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
            final boolean traceEnabled = this.logger.isTraceEnabled();
            final boolean debugEnabled = this.logger.isDebugEnabled();
            for (final Resource resource : resources) {
                if (traceEnabled) {
                    this.logger.trace("Scanning " + resource);
                }
                Label_0390: {
                    if (resource.isReadable()) {
                        try {
                            final MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                            if (this.isCandidateComponent(metadataReader)) {
                                final ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                                sbd.setResource(resource);
                                sbd.setSource(resource);
                                if (this.isCandidateComponent(sbd)) {
                                    if (debugEnabled) {
                                        this.logger.debug("Identified candidate component class: " + resource);
                                    }
                                    candidates.add(sbd);
                                }
                                else if (debugEnabled) {
                                    this.logger.debug("Ignored because not a concrete top-level class: " + resource);
                                }
                            }
                            else if (traceEnabled) {
                                this.logger.trace("Ignored because not matching any filter: " + resource);
                            }
                            break Label_0390;
                        }
                        catch (Throwable ex) {
                            throw new BeanDefinitionStoreException("Failed to read candidate component class: " + resource, ex);
                        }
                    }
                    if (traceEnabled) {
                        this.logger.trace("Ignored because not readable: " + resource);
                    }
                }
            }
        }
        catch (IOException ex2) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex2);
        }
        return candidates;
    }
    
    protected String resolveBasePackage(final String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(this.environment.resolveRequiredPlaceholders(basePackage));
    }
    
    protected boolean isCandidateComponent(final MetadataReader metadataReader) throws IOException {
        for (final TypeFilter tf : this.excludeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return false;
            }
        }
        for (final TypeFilter tf : this.includeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return this.isConditionMatch(metadataReader);
            }
        }
        return false;
    }
    
    private boolean isConditionMatch(final MetadataReader metadataReader) {
        if (this.conditionEvaluator == null) {
            this.conditionEvaluator = new ConditionEvaluator(this.getRegistry(), this.getEnvironment(), this.getResourceLoader());
        }
        return !this.conditionEvaluator.shouldSkip(metadataReader.getAnnotationMetadata());
    }
    
    protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isConcrete() && beanDefinition.getMetadata().isIndependent();
    }
    
    public void clearCache() {
        if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            ((CachingMetadataReaderFactory)this.metadataReaderFactory).clearCache();
        }
    }
}
