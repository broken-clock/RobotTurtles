// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import java.beans.PropertyDescriptor;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import java.util.Map;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import java.util.LinkedHashMap;
import java.util.Iterator;
import org.springframework.core.env.MutablePropertySources;
import java.util.List;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.Assert;
import org.springframework.beans.factory.config.BeanDefinition;
import java.util.HashSet;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.BeanNameGenerator;
import java.util.Set;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.apache.commons.logging.Log;
import org.springframework.context.EnvironmentAware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware
{
    private static final String IMPORT_AWARE_PROCESSOR_BEAN_NAME;
    private static final String IMPORT_REGISTRY_BEAN_NAME;
    private static final String ENHANCED_CONFIGURATION_PROCESSOR_BEAN_NAME;
    private final Log logger;
    private SourceExtractor sourceExtractor;
    private ProblemReporter problemReporter;
    private Environment environment;
    private ResourceLoader resourceLoader;
    private ClassLoader beanClassLoader;
    private MetadataReaderFactory metadataReaderFactory;
    private boolean setMetadataReaderFactoryCalled;
    private final Set<Integer> registriesPostProcessed;
    private final Set<Integer> factoriesPostProcessed;
    private ConfigurationClassBeanDefinitionReader reader;
    private boolean localBeanNameGeneratorSet;
    private BeanNameGenerator componentScanBeanNameGenerator;
    private BeanNameGenerator importBeanNameGenerator;
    
    public ConfigurationClassPostProcessor() {
        this.logger = LogFactory.getLog(this.getClass());
        this.sourceExtractor = new PassThroughSourceExtractor();
        this.problemReporter = new FailFastProblemReporter();
        this.resourceLoader = new DefaultResourceLoader();
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        this.metadataReaderFactory = new CachingMetadataReaderFactory();
        this.setMetadataReaderFactoryCalled = false;
        this.registriesPostProcessed = new HashSet<Integer>();
        this.factoriesPostProcessed = new HashSet<Integer>();
        this.localBeanNameGeneratorSet = false;
        this.componentScanBeanNameGenerator = new AnnotationBeanNameGenerator();
        this.importBeanNameGenerator = new AnnotationBeanNameGenerator() {
            @Override
            protected String buildDefaultBeanName(final BeanDefinition definition) {
                return definition.getBeanClassName();
            }
        };
    }
    
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
    
    public void setSourceExtractor(final SourceExtractor sourceExtractor) {
        this.sourceExtractor = ((sourceExtractor != null) ? sourceExtractor : new PassThroughSourceExtractor());
    }
    
    public void setProblemReporter(final ProblemReporter problemReporter) {
        this.problemReporter = ((problemReporter != null) ? problemReporter : new FailFastProblemReporter());
    }
    
    public void setMetadataReaderFactory(final MetadataReaderFactory metadataReaderFactory) {
        Assert.notNull(metadataReaderFactory, "MetadataReaderFactory must not be null");
        this.metadataReaderFactory = metadataReaderFactory;
        this.setMetadataReaderFactoryCalled = true;
    }
    
    public void setBeanNameGenerator(final BeanNameGenerator beanNameGenerator) {
        Assert.notNull(beanNameGenerator, "BeanNameGenerator must not be null");
        this.localBeanNameGeneratorSet = true;
        this.componentScanBeanNameGenerator = beanNameGenerator;
        this.importBeanNameGenerator = beanNameGenerator;
    }
    
    @Override
    public void setEnvironment(final Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
    }
    
    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
        if (!this.setMetadataReaderFactoryCalled) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory(beanClassLoader);
        }
    }
    
    @Override
    public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) {
        final RootBeanDefinition iabpp = new RootBeanDefinition(ImportAwareBeanPostProcessor.class);
        iabpp.setRole(2);
        registry.registerBeanDefinition(ConfigurationClassPostProcessor.IMPORT_AWARE_PROCESSOR_BEAN_NAME, iabpp);
        final RootBeanDefinition ecbpp = new RootBeanDefinition(EnhancedConfigurationBeanPostProcessor.class);
        ecbpp.setRole(2);
        registry.registerBeanDefinition(ConfigurationClassPostProcessor.ENHANCED_CONFIGURATION_PROCESSOR_BEAN_NAME, ecbpp);
        final int registryId = System.identityHashCode(registry);
        if (this.registriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException("postProcessBeanDefinitionRegistry already called for this post-processor against " + registry);
        }
        if (this.factoriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException("postProcessBeanFactory already called for this post-processor against " + registry);
        }
        this.registriesPostProcessed.add(registryId);
        this.processConfigBeanDefinitions(registry);
    }
    
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) {
        final int factoryId = System.identityHashCode(beanFactory);
        if (this.factoriesPostProcessed.contains(factoryId)) {
            throw new IllegalStateException("postProcessBeanFactory already called for this post-processor against " + beanFactory);
        }
        this.factoriesPostProcessed.add(factoryId);
        if (!this.registriesPostProcessed.contains(factoryId)) {
            this.processConfigBeanDefinitions((BeanDefinitionRegistry)beanFactory);
        }
        this.enhanceConfigurationClasses(beanFactory);
    }
    
    public void processConfigBeanDefinitions(final BeanDefinitionRegistry registry) {
        final Set<BeanDefinitionHolder> configCandidates = new LinkedHashSet<BeanDefinitionHolder>();
        for (final String beanName : registry.getBeanDefinitionNames()) {
            final BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
                configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
            }
        }
        if (configCandidates.isEmpty()) {
            return;
        }
        SingletonBeanRegistry singletonRegistry = null;
        if (registry instanceof SingletonBeanRegistry) {
            singletonRegistry = (SingletonBeanRegistry)registry;
            if (!this.localBeanNameGeneratorSet && singletonRegistry.containsSingleton("org.springframework.context.annotation.internalConfigurationBeanNameGenerator")) {
                final BeanNameGenerator generator = (BeanNameGenerator)singletonRegistry.getSingleton("org.springframework.context.annotation.internalConfigurationBeanNameGenerator");
                this.componentScanBeanNameGenerator = generator;
                this.importBeanNameGenerator = generator;
            }
        }
        final ConfigurationClassParser parser = new ConfigurationClassParser(this.metadataReaderFactory, this.problemReporter, this.environment, this.resourceLoader, this.componentScanBeanNameGenerator, registry);
        parser.parse(configCandidates);
        parser.validate();
        final List<PropertySource<?>> parsedPropertySources = parser.getPropertySources();
        if (!parsedPropertySources.isEmpty()) {
            if (!(this.environment instanceof ConfigurableEnvironment)) {
                this.logger.warn("Ignoring @PropertySource annotations. Reason: Environment must implement ConfigurableEnvironment");
            }
            else {
                final MutablePropertySources envPropertySources = ((ConfigurableEnvironment)this.environment).getPropertySources();
                for (final PropertySource<?> propertySource : parsedPropertySources) {
                    envPropertySources.addLast(propertySource);
                }
            }
        }
        if (this.reader == null) {
            this.reader = new ConfigurationClassBeanDefinitionReader(registry, this.sourceExtractor, this.problemReporter, this.metadataReaderFactory, this.resourceLoader, this.environment, this.importBeanNameGenerator);
        }
        this.reader.loadBeanDefinitions(parser.getConfigurationClasses());
        if (singletonRegistry != null && !singletonRegistry.containsSingleton(ConfigurationClassPostProcessor.IMPORT_REGISTRY_BEAN_NAME)) {
            singletonRegistry.registerSingleton(ConfigurationClassPostProcessor.IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
        }
        if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            ((CachingMetadataReaderFactory)this.metadataReaderFactory).clearCache();
        }
    }
    
    public void enhanceConfigurationClasses(final ConfigurableListableBeanFactory beanFactory) {
        final Map<String, AbstractBeanDefinition> configBeanDefs = new LinkedHashMap<String, AbstractBeanDefinition>();
        for (final String beanName : beanFactory.getBeanDefinitionNames()) {
            final BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
            if (ConfigurationClassUtils.isFullConfigurationClass(beanDef)) {
                if (!(beanDef instanceof AbstractBeanDefinition)) {
                    throw new BeanDefinitionStoreException("Cannot enhance @Configuration bean definition '" + beanName + "' since it is not stored in an AbstractBeanDefinition subclass");
                }
                configBeanDefs.put(beanName, (AbstractBeanDefinition)beanDef);
            }
        }
        if (configBeanDefs.isEmpty()) {
            return;
        }
        final ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
        for (final Map.Entry<String, AbstractBeanDefinition> entry : configBeanDefs.entrySet()) {
            final AbstractBeanDefinition beanDef2 = entry.getValue();
            beanDef2.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
            try {
                final Class<?> configClass = beanDef2.resolveBeanClass(this.beanClassLoader);
                final Class<?> enhancedClass = enhancer.enhance(configClass);
                if (configClass == enhancedClass) {
                    continue;
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug(String.format("Replacing bean definition '%s' existing class name '%s' with enhanced class name '%s'", entry.getKey(), configClass.getName(), enhancedClass.getName()));
                }
                beanDef2.setBeanClass(enhancedClass);
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Cannot load configuration class: " + beanDef2.getBeanClassName(), ex);
            }
        }
    }
    
    static {
        IMPORT_AWARE_PROCESSOR_BEAN_NAME = ConfigurationClassPostProcessor.class.getName() + ".importAwareProcessor";
        IMPORT_REGISTRY_BEAN_NAME = ConfigurationClassPostProcessor.class.getName() + ".importRegistry";
        ENHANCED_CONFIGURATION_PROCESSOR_BEAN_NAME = ConfigurationClassPostProcessor.class.getName() + ".enhancedConfigurationProcessor";
    }
    
    private static class ImportAwareBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware, PriorityOrdered
    {
        private BeanFactory beanFactory;
        
        @Override
        public void setBeanFactory(final BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }
        
        @Override
        public int getOrder() {
            return Integer.MIN_VALUE;
        }
        
        @Override
        public Object postProcessBeforeInitialization(final Object bean, final String beanName) {
            if (bean instanceof ImportAware) {
                final ConfigurationClassParser.ImportRegistry importRegistry = this.beanFactory.getBean(ConfigurationClassPostProcessor.IMPORT_REGISTRY_BEAN_NAME, ConfigurationClassParser.ImportRegistry.class);
                final AnnotationMetadata importingClass = importRegistry.getImportingClassFor(bean.getClass().getSuperclass().getName());
                if (importingClass != null) {
                    ((ImportAware)bean).setImportMetadata(importingClass);
                }
            }
            return bean;
        }
        
        @Override
        public Object postProcessAfterInitialization(final Object bean, final String beanName) {
            return bean;
        }
    }
    
    private static class EnhancedConfigurationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements PriorityOrdered, BeanFactoryAware
    {
        private BeanFactory beanFactory;
        
        @Override
        public int getOrder() {
            return Integer.MIN_VALUE;
        }
        
        @Override
        public void setBeanFactory(final BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }
        
        @Override
        public PropertyValues postProcessPropertyValues(final PropertyValues pvs, final PropertyDescriptor[] pds, final Object bean, final String beanName) {
            if (bean instanceof ConfigurationClassEnhancer.EnhancedConfiguration) {
                ((ConfigurationClassEnhancer.EnhancedConfiguration)bean).setBeanFactory(this.beanFactory);
            }
            return pvs;
        }
    }
}
