// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.beans.factory.parsing.Location;
import java.util.Iterator;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.util.ClassUtils;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.util.Assert;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import java.util.Map;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;

final class ConfigurationClass
{
    private final AnnotationMetadata metadata;
    private final Resource resource;
    private String beanName;
    private final ConfigurationClass importedBy;
    private final Set<BeanMethod> beanMethods;
    private final Map<String, Class<? extends BeanDefinitionReader>> importedResources;
    private final Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> importBeanDefinitionRegistrars;
    
    public ConfigurationClass(final MetadataReader metadataReader, final String beanName) {
        this.beanMethods = new LinkedHashSet<BeanMethod>();
        this.importedResources = new LinkedHashMap<String, Class<? extends BeanDefinitionReader>>();
        this.importBeanDefinitionRegistrars = new LinkedHashMap<ImportBeanDefinitionRegistrar, AnnotationMetadata>();
        Assert.hasText(beanName, "bean name must not be null");
        this.metadata = metadataReader.getAnnotationMetadata();
        this.resource = metadataReader.getResource();
        this.beanName = beanName;
        this.importedBy = null;
    }
    
    public ConfigurationClass(final MetadataReader metadataReader, final ConfigurationClass importedBy) {
        this.beanMethods = new LinkedHashSet<BeanMethod>();
        this.importedResources = new LinkedHashMap<String, Class<? extends BeanDefinitionReader>>();
        this.importBeanDefinitionRegistrars = new LinkedHashMap<ImportBeanDefinitionRegistrar, AnnotationMetadata>();
        this.metadata = metadataReader.getAnnotationMetadata();
        this.resource = metadataReader.getResource();
        this.importedBy = importedBy;
    }
    
    public ConfigurationClass(final Class<?> clazz, final String beanName) {
        this.beanMethods = new LinkedHashSet<BeanMethod>();
        this.importedResources = new LinkedHashMap<String, Class<? extends BeanDefinitionReader>>();
        this.importBeanDefinitionRegistrars = new LinkedHashMap<ImportBeanDefinitionRegistrar, AnnotationMetadata>();
        Assert.hasText(beanName, "Bean name must not be null");
        this.metadata = new StandardAnnotationMetadata(clazz, true);
        this.resource = new DescriptiveResource(clazz.toString());
        this.beanName = beanName;
        this.importedBy = null;
    }
    
    public ConfigurationClass(final Class<?> clazz, final ConfigurationClass importedBy) {
        this.beanMethods = new LinkedHashSet<BeanMethod>();
        this.importedResources = new LinkedHashMap<String, Class<? extends BeanDefinitionReader>>();
        this.importBeanDefinitionRegistrars = new LinkedHashMap<ImportBeanDefinitionRegistrar, AnnotationMetadata>();
        this.metadata = new StandardAnnotationMetadata(clazz, true);
        this.resource = new DescriptiveResource(clazz.toString());
        this.importedBy = importedBy;
    }
    
    public AnnotationMetadata getMetadata() {
        return this.metadata;
    }
    
    public Resource getResource() {
        return this.resource;
    }
    
    public String getSimpleName() {
        return ClassUtils.getShortName(this.getMetadata().getClassName());
    }
    
    public void setBeanName(final String beanName) {
        this.beanName = beanName;
    }
    
    public String getBeanName() {
        return this.beanName;
    }
    
    public boolean isImported() {
        return this.importedBy != null;
    }
    
    public ConfigurationClass getImportedBy() {
        return this.importedBy;
    }
    
    public void addBeanMethod(final BeanMethod method) {
        this.beanMethods.add(method);
    }
    
    public Set<BeanMethod> getBeanMethods() {
        return this.beanMethods;
    }
    
    public void addImportedResource(final String importedResource, final Class<? extends BeanDefinitionReader> readerClass) {
        this.importedResources.put(importedResource, readerClass);
    }
    
    public void addImportBeanDefinitionRegistrar(final ImportBeanDefinitionRegistrar registrar, final AnnotationMetadata importingClassMetadata) {
        this.importBeanDefinitionRegistrars.put(registrar, importingClassMetadata);
    }
    
    public Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> getImportBeanDefinitionRegistrars() {
        return this.importBeanDefinitionRegistrars;
    }
    
    public Map<String, Class<? extends BeanDefinitionReader>> getImportedResources() {
        return this.importedResources;
    }
    
    public void validate(final ProblemReporter problemReporter) {
        if (this.getMetadata().isAnnotated(Configuration.class.getName()) && this.getMetadata().isFinal()) {
            problemReporter.error(new FinalConfigurationProblem());
        }
        for (final BeanMethod beanMethod : this.beanMethods) {
            beanMethod.validate(problemReporter);
        }
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof ConfigurationClass && this.getMetadata().getClassName().equals(((ConfigurationClass)other).getMetadata().getClassName()));
    }
    
    @Override
    public int hashCode() {
        return this.getMetadata().getClassName().hashCode();
    }
    
    @Override
    public String toString() {
        return "ConfigurationClass:beanName=" + this.beanName + ",resource=" + this.resource;
    }
    
    private class FinalConfigurationProblem extends Problem
    {
        public FinalConfigurationProblem() {
            super(String.format("@Configuration class '%s' may not be final. Remove the final modifier to continue.", ConfigurationClass.this.getSimpleName()), new Location(ConfigurationClass.this.getResource(), ConfigurationClass.this.getMetadata()));
        }
    }
}
