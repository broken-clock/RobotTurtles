// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.apache.commons.logging.LogFactory;
import org.springframework.core.Conventions;
import org.springframework.stereotype.Component;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.AnnotationMetadata;
import java.io.IOException;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.apache.commons.logging.Log;

abstract class ConfigurationClassUtils
{
    private static final String CONFIGURATION_CLASS_FULL = "full";
    private static final String CONFIGURATION_CLASS_LITE = "lite";
    private static final String CONFIGURATION_CLASS_ATTRIBUTE;
    private static final Log logger;
    
    public static boolean checkConfigurationClassCandidate(final BeanDefinition beanDef, final MetadataReaderFactory metadataReaderFactory) {
        AnnotationMetadata metadata = null;
        if (beanDef instanceof AbstractBeanDefinition && ((AbstractBeanDefinition)beanDef).hasBeanClass()) {
            final Class<?> beanClass = ((AbstractBeanDefinition)beanDef).getBeanClass();
            metadata = new StandardAnnotationMetadata(beanClass, true);
        }
        else {
            final String className = beanDef.getBeanClassName();
            if (className != null) {
                try {
                    final MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(className);
                    metadata = metadataReader.getAnnotationMetadata();
                }
                catch (IOException ex) {
                    if (ConfigurationClassUtils.logger.isDebugEnabled()) {
                        ConfigurationClassUtils.logger.debug("Could not find class file for introspecting factory methods: " + className, ex);
                    }
                    return false;
                }
            }
        }
        if (metadata != null) {
            if (isFullConfigurationCandidate(metadata)) {
                beanDef.setAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE, "full");
                return true;
            }
            if (isLiteConfigurationCandidate(metadata)) {
                beanDef.setAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE, "lite");
                return true;
            }
        }
        return false;
    }
    
    public static boolean isConfigurationCandidate(final AnnotationMetadata metadata) {
        return isFullConfigurationCandidate(metadata) || isLiteConfigurationCandidate(metadata);
    }
    
    public static boolean isFullConfigurationCandidate(final AnnotationMetadata metadata) {
        return metadata.isAnnotated(Configuration.class.getName());
    }
    
    public static boolean isLiteConfigurationCandidate(final AnnotationMetadata metadata) {
        return !metadata.isInterface() && (metadata.isAnnotated(Component.class.getName()) || metadata.isAnnotated(Import.class.getName()) || metadata.hasAnnotatedMethods(Bean.class.getName()));
    }
    
    public static boolean isFullConfigurationClass(final BeanDefinition beanDef) {
        return "full".equals(beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE));
    }
    
    static {
        CONFIGURATION_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "configurationClass");
        logger = LogFactory.getLog(ConfigurationClassUtils.class);
    }
}
