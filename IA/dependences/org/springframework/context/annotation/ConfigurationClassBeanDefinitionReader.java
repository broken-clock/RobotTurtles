// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.beans.factory.parsing.Location;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import java.lang.reflect.Method;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import java.util.HashMap;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import java.util.Map;
import java.util.List;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.MethodMetadata;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.util.StringUtils;
import java.util.Iterator;
import java.util.Set;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.apache.commons.logging.Log;

class ConfigurationClassBeanDefinitionReader
{
    private static final Log logger;
    private final BeanDefinitionRegistry registry;
    private final SourceExtractor sourceExtractor;
    private final ProblemReporter problemReporter;
    private final MetadataReaderFactory metadataReaderFactory;
    private final ResourceLoader resourceLoader;
    private final Environment environment;
    private final BeanNameGenerator importBeanNameGenerator;
    private final ConditionEvaluator conditionEvaluator;
    
    public ConfigurationClassBeanDefinitionReader(final BeanDefinitionRegistry registry, final SourceExtractor sourceExtractor, final ProblemReporter problemReporter, final MetadataReaderFactory metadataReaderFactory, final ResourceLoader resourceLoader, final Environment environment, final BeanNameGenerator importBeanNameGenerator) {
        this.registry = registry;
        this.sourceExtractor = sourceExtractor;
        this.problemReporter = problemReporter;
        this.metadataReaderFactory = metadataReaderFactory;
        this.resourceLoader = resourceLoader;
        this.environment = environment;
        this.importBeanNameGenerator = importBeanNameGenerator;
        this.conditionEvaluator = new ConditionEvaluator(registry, environment, resourceLoader);
    }
    
    public void loadBeanDefinitions(final Set<ConfigurationClass> configurationModel) {
        final TrackedConditionEvaluator trackedConditionEvaluator = new TrackedConditionEvaluator();
        for (final ConfigurationClass configClass : configurationModel) {
            this.loadBeanDefinitionsForConfigurationClass(configClass, trackedConditionEvaluator);
        }
    }
    
    private void loadBeanDefinitionsForConfigurationClass(final ConfigurationClass configClass, final TrackedConditionEvaluator trackedConditionEvaluator) {
        if (trackedConditionEvaluator.shouldSkip(configClass)) {
            this.removeBeanDefinition(configClass);
            return;
        }
        if (configClass.isImported()) {
            this.registerBeanDefinitionForImportedConfigurationClass(configClass);
        }
        for (final BeanMethod beanMethod : configClass.getBeanMethods()) {
            this.loadBeanDefinitionsForBeanMethod(beanMethod);
        }
        this.loadBeanDefinitionsFromImportedResources(configClass.getImportedResources());
        this.loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars());
    }
    
    private void removeBeanDefinition(final ConfigurationClass configClass) {
        final String beanName = configClass.getBeanName();
        if (StringUtils.hasLength(beanName) && this.registry.containsBeanDefinition(beanName)) {
            this.registry.removeBeanDefinition(beanName);
        }
    }
    
    private void registerBeanDefinitionForImportedConfigurationClass(final ConfigurationClass configClass) {
        final AnnotationMetadata metadata = configClass.getMetadata();
        final BeanDefinition configBeanDef = new AnnotatedGenericBeanDefinition(metadata);
        if (ConfigurationClassUtils.checkConfigurationClassCandidate(configBeanDef, this.metadataReaderFactory)) {
            final String configBeanName = this.importBeanNameGenerator.generateBeanName(configBeanDef, this.registry);
            this.registry.registerBeanDefinition(configBeanName, configBeanDef);
            configClass.setBeanName(configBeanName);
            if (ConfigurationClassBeanDefinitionReader.logger.isDebugEnabled()) {
                ConfigurationClassBeanDefinitionReader.logger.debug(String.format("Registered bean definition for imported @Configuration class %s", configBeanName));
            }
        }
        else {
            this.problemReporter.error(new InvalidConfigurationImportProblem(metadata.getClassName(), configClass.getResource(), metadata));
        }
    }
    
    private void loadBeanDefinitionsForBeanMethod(final BeanMethod beanMethod) {
        if (this.conditionEvaluator.shouldSkip(beanMethod.getMetadata(), ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN)) {
            return;
        }
        final ConfigurationClass configClass = beanMethod.getConfigurationClass();
        final MethodMetadata metadata = beanMethod.getMetadata();
        final ConfigurationClassBeanDefinition beanDef = new ConfigurationClassBeanDefinition(configClass);
        beanDef.setResource(configClass.getResource());
        beanDef.setSource(this.sourceExtractor.extractSource(metadata, configClass.getResource()));
        if (metadata.isStatic()) {
            beanDef.setBeanClassName(configClass.getMetadata().getClassName());
            beanDef.setFactoryMethodName(metadata.getMethodName());
        }
        else {
            beanDef.setFactoryBeanName(configClass.getBeanName());
            beanDef.setUniqueFactoryMethodName(metadata.getMethodName());
        }
        beanDef.setAutowireMode(3);
        beanDef.setAttribute(RequiredAnnotationBeanPostProcessor.SKIP_REQUIRED_CHECK_ATTRIBUTE, Boolean.TRUE);
        final AnnotationAttributes bean = AnnotationConfigUtils.attributesFor(metadata, Bean.class);
        final List<String> names = new ArrayList<String>(Arrays.asList(bean.getStringArray("name")));
        final String beanName = (names.size() > 0) ? names.remove(0) : beanMethod.getMetadata().getMethodName();
        for (final String alias : names) {
            this.registry.registerAlias(beanName, alias);
        }
        if (this.isOverriddenByExistingDefinition(beanMethod, beanName)) {
            return;
        }
        AnnotationConfigUtils.processCommonDefinitionAnnotations(beanDef, metadata);
        final Autowire autowire = bean.getEnum("autowire");
        if (autowire.isAutowire()) {
            beanDef.setAutowireMode(autowire.value());
        }
        final String initMethodName = bean.getString("initMethod");
        if (StringUtils.hasText(initMethodName)) {
            beanDef.setInitMethodName(initMethodName);
        }
        final String destroyMethodName = bean.getString("destroyMethod");
        if (StringUtils.hasText(destroyMethodName)) {
            beanDef.setDestroyMethodName(destroyMethodName);
        }
        ScopedProxyMode proxyMode = ScopedProxyMode.NO;
        final AnnotationAttributes scope = AnnotationConfigUtils.attributesFor(metadata, Scope.class);
        if (scope != null) {
            beanDef.setScope(scope.getString("value"));
            proxyMode = scope.getEnum("proxyMode");
            if (proxyMode == ScopedProxyMode.DEFAULT) {
                proxyMode = ScopedProxyMode.NO;
            }
        }
        BeanDefinition beanDefToRegister = beanDef;
        if (proxyMode != ScopedProxyMode.NO) {
            final BeanDefinitionHolder proxyDef = ScopedProxyCreator.createScopedProxy(new BeanDefinitionHolder(beanDef, beanName), this.registry, proxyMode == ScopedProxyMode.TARGET_CLASS);
            beanDefToRegister = new ConfigurationClassBeanDefinition((RootBeanDefinition)proxyDef.getBeanDefinition(), configClass);
        }
        if (ConfigurationClassBeanDefinitionReader.logger.isDebugEnabled()) {
            ConfigurationClassBeanDefinitionReader.logger.debug(String.format("Registering bean definition for @Bean method %s.%s()", configClass.getMetadata().getClassName(), beanName));
        }
        this.registry.registerBeanDefinition(beanName, beanDefToRegister);
    }
    
    protected boolean isOverriddenByExistingDefinition(final BeanMethod beanMethod, final String beanName) {
        if (!this.registry.containsBeanDefinition(beanName)) {
            return false;
        }
        final BeanDefinition existingBeanDef = this.registry.getBeanDefinition(beanName);
        if (existingBeanDef instanceof ConfigurationClassBeanDefinition) {
            final ConfigurationClassBeanDefinition ccbd = (ConfigurationClassBeanDefinition)existingBeanDef;
            return ccbd.getMetadata().getClassName().equals(beanMethod.getConfigurationClass().getMetadata().getClassName());
        }
        if (existingBeanDef.getRole() > 0) {
            return false;
        }
        if (ConfigurationClassBeanDefinitionReader.logger.isInfoEnabled()) {
            ConfigurationClassBeanDefinitionReader.logger.info(String.format("Skipping bean definition for %s: a definition for bean '%s' already exists. This top-level bean definition is considered as an override.", beanMethod, beanName));
        }
        return true;
    }
    
    private void loadBeanDefinitionsFromImportedResources(final Map<String, Class<? extends BeanDefinitionReader>> importedResources) {
        final Map<Class<?>, BeanDefinitionReader> readerInstanceCache = new HashMap<Class<?>, BeanDefinitionReader>();
        for (final Map.Entry<String, Class<? extends BeanDefinitionReader>> entry : importedResources.entrySet()) {
            final String resource = entry.getKey();
            final Class<? extends BeanDefinitionReader> readerClass = entry.getValue();
            if (!readerInstanceCache.containsKey(readerClass)) {
                try {
                    final BeanDefinitionReader readerInstance = (BeanDefinitionReader)readerClass.getConstructor(BeanDefinitionRegistry.class).newInstance(this.registry);
                    if (readerInstance instanceof AbstractBeanDefinitionReader) {
                        final AbstractBeanDefinitionReader abdr = (AbstractBeanDefinitionReader)readerInstance;
                        abdr.setResourceLoader(this.resourceLoader);
                        abdr.setEnvironment(this.environment);
                    }
                    readerInstanceCache.put(readerClass, readerInstance);
                }
                catch (Exception ex) {
                    throw new IllegalStateException("Could not instantiate BeanDefinitionReader class [" + readerClass.getName() + "]");
                }
            }
            final BeanDefinitionReader reader = readerInstanceCache.get(readerClass);
            reader.loadBeanDefinitions(resource);
        }
    }
    
    private void loadBeanDefinitionsFromRegistrars(final Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> registrars) {
        for (final Map.Entry<ImportBeanDefinitionRegistrar, AnnotationMetadata> entry : registrars.entrySet()) {
            entry.getKey().registerBeanDefinitions(entry.getValue(), this.registry);
        }
    }
    
    static {
        logger = LogFactory.getLog(ConfigurationClassBeanDefinitionReader.class);
    }
    
    private static class ConfigurationClassBeanDefinition extends RootBeanDefinition implements AnnotatedBeanDefinition
    {
        private final AnnotationMetadata annotationMetadata;
        
        public ConfigurationClassBeanDefinition(final ConfigurationClass configClass) {
            this.annotationMetadata = configClass.getMetadata();
            this.setLenientConstructorResolution(false);
        }
        
        public ConfigurationClassBeanDefinition(final RootBeanDefinition original, final ConfigurationClass configClass) {
            super(original);
            this.annotationMetadata = configClass.getMetadata();
        }
        
        private ConfigurationClassBeanDefinition(final ConfigurationClassBeanDefinition original) {
            super(original);
            this.annotationMetadata = original.annotationMetadata;
        }
        
        @Override
        public AnnotationMetadata getMetadata() {
            return this.annotationMetadata;
        }
        
        @Override
        public boolean isFactoryMethod(final Method candidate) {
            return super.isFactoryMethod(candidate) && BeanAnnotationHelper.isBeanAnnotated(candidate);
        }
        
        @Override
        public ConfigurationClassBeanDefinition cloneBeanDefinition() {
            return new ConfigurationClassBeanDefinition(this);
        }
    }
    
    private static class InvalidConfigurationImportProblem extends Problem
    {
        public InvalidConfigurationImportProblem(final String className, final Resource resource, final AnnotationMetadata metadata) {
            super(String.format("%s was @Import'ed but is not annotated with @Configuration nor does it declare any @Bean methods; it does not implement ImportSelector or extend ImportBeanDefinitionRegistrar. Update the class to meet one of these requirements or do not attempt to @Import it.", className), new Location(resource, metadata));
        }
    }
    
    private class TrackedConditionEvaluator
    {
        private final Map<ConfigurationClass, Boolean> skipped;
        
        private TrackedConditionEvaluator() {
            this.skipped = new HashMap<ConfigurationClass, Boolean>();
        }
        
        public boolean shouldSkip(final ConfigurationClass configClass) {
            Boolean skip = this.skipped.get(configClass);
            if (skip == null) {
                if (configClass.isImported() && this.shouldSkip(configClass.getImportedBy())) {
                    skip = true;
                }
                if (skip == null) {
                    skip = ConfigurationClassBeanDefinitionReader.this.conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
                }
                this.skipped.put(configClass, skip);
            }
            return skip;
        }
    }
}
