// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.core.env.EnvironmentCapable;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

class ConditionEvaluator
{
    private final ConditionContextImpl context;
    
    public ConditionEvaluator(final BeanDefinitionRegistry registry, final Environment environment, final ResourceLoader resourceLoader) {
        this.context = new ConditionContextImpl(registry, environment, resourceLoader);
    }
    
    public boolean shouldSkip(final AnnotatedTypeMetadata metadata) {
        return this.shouldSkip(metadata, null);
    }
    
    public boolean shouldSkip(final AnnotatedTypeMetadata metadata, final ConfigurationCondition.ConfigurationPhase phase) {
        if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
            return false;
        }
        if (phase != null) {
            for (final String[] array : this.getConditionClasses(metadata)) {
                final String[] conditionClasses = array;
                for (final String conditionClass : array) {
                    final Condition condition = this.getCondition(conditionClass, this.context.getClassLoader());
                    ConfigurationCondition.ConfigurationPhase requiredPhase = null;
                    if (condition instanceof ConfigurationCondition) {
                        requiredPhase = ((ConfigurationCondition)condition).getConfigurationPhase();
                    }
                    if ((requiredPhase == null || requiredPhase == phase) && !condition.matches(this.context, metadata)) {
                        return true;
                    }
                }
            }
            return false;
        }
        if (metadata instanceof AnnotationMetadata && ConfigurationClassUtils.isConfigurationCandidate((AnnotationMetadata)metadata)) {
            return this.shouldSkip(metadata, ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }
        return this.shouldSkip(metadata, ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
    }
    
    private List<String[]> getConditionClasses(final AnnotatedTypeMetadata metadata) {
        final MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
        final Object values = (attributes != null) ? attributes.get("value") : null;
        return (List<String[]>)((values != null) ? values : Collections.emptyList());
    }
    
    private Condition getCondition(final String conditionClassName, final ClassLoader classloader) {
        final Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName, classloader);
        return BeanUtils.instantiateClass(conditionClass);
    }
    
    private static class ConditionContextImpl implements ConditionContext
    {
        private final BeanDefinitionRegistry registry;
        private final ConfigurableListableBeanFactory beanFactory;
        private final Environment environment;
        private final ResourceLoader resourceLoader;
        
        public ConditionContextImpl(final BeanDefinitionRegistry registry, final Environment environment, final ResourceLoader resourceLoader) {
            this.registry = registry;
            this.beanFactory = this.deduceBeanFactory(registry);
            this.environment = ((environment != null) ? environment : this.deduceEnvironment(registry));
            this.resourceLoader = ((resourceLoader != null) ? resourceLoader : this.deduceResourceLoader(registry));
        }
        
        private ConfigurableListableBeanFactory deduceBeanFactory(final BeanDefinitionRegistry source) {
            if (source instanceof ConfigurableListableBeanFactory) {
                return (ConfigurableListableBeanFactory)source;
            }
            if (source instanceof ConfigurableApplicationContext) {
                return ((ConfigurableApplicationContext)source).getBeanFactory();
            }
            return null;
        }
        
        private Environment deduceEnvironment(final BeanDefinitionRegistry source) {
            if (source instanceof EnvironmentCapable) {
                return ((EnvironmentCapable)source).getEnvironment();
            }
            return null;
        }
        
        private ResourceLoader deduceResourceLoader(final BeanDefinitionRegistry source) {
            if (source instanceof ResourceLoader) {
                return (ResourceLoader)source;
            }
            return null;
        }
        
        @Override
        public BeanDefinitionRegistry getRegistry() {
            return this.registry;
        }
        
        @Override
        public ConfigurableListableBeanFactory getBeanFactory() {
            return this.beanFactory;
        }
        
        @Override
        public Environment getEnvironment() {
            return this.environment;
        }
        
        @Override
        public ResourceLoader getResourceLoader() {
            return this.resourceLoader;
        }
        
        @Override
        public ClassLoader getClassLoader() {
            if (this.resourceLoader != null) {
                return this.resourceLoader.getClassLoader();
            }
            if (this.beanFactory != null) {
                return this.beanFactory.getBeanClassLoader();
            }
            return null;
        }
    }
}
