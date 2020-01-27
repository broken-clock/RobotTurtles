// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.ClassUtils;

public class BeanDefinitionReaderUtils
{
    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";
    
    public static AbstractBeanDefinition createBeanDefinition(final String parentName, final String className, final ClassLoader classLoader) throws ClassNotFoundException {
        final GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setParentName(parentName);
        if (className != null) {
            if (classLoader != null) {
                bd.setBeanClass(ClassUtils.forName(className, classLoader));
            }
            else {
                bd.setBeanClassName(className);
            }
        }
        return bd;
    }
    
    public static String generateBeanName(final BeanDefinition definition, final BeanDefinitionRegistry registry, final boolean isInnerBean) throws BeanDefinitionStoreException {
        String generatedBeanName = definition.getBeanClassName();
        if (generatedBeanName == null) {
            if (definition.getParentName() != null) {
                generatedBeanName = definition.getParentName() + "$child";
            }
            else if (definition.getFactoryBeanName() != null) {
                generatedBeanName = definition.getFactoryBeanName() + "$created";
            }
        }
        if (!StringUtils.hasText(generatedBeanName)) {
            throw new BeanDefinitionStoreException("Unnamed bean definition specifies neither 'class' nor 'parent' nor 'factory-bean' - can't generate bean name");
        }
        String id = generatedBeanName;
        if (isInnerBean) {
            id = generatedBeanName + "#" + ObjectUtils.getIdentityHexString(definition);
        }
        else {
            for (int counter = -1; counter == -1 || registry.containsBeanDefinition(id); ++counter, id = generatedBeanName + "#" + counter) {}
        }
        return id;
    }
    
    public static String generateBeanName(final BeanDefinition beanDefinition, final BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
        return generateBeanName(beanDefinition, registry, false);
    }
    
    public static void registerBeanDefinition(final BeanDefinitionHolder definitionHolder, final BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
        final String beanName = definitionHolder.getBeanName();
        registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());
        final String[] aliases = definitionHolder.getAliases();
        if (aliases != null) {
            for (final String aliase : aliases) {
                registry.registerAlias(beanName, aliase);
            }
        }
    }
    
    public static String registerWithGeneratedName(final AbstractBeanDefinition definition, final BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
        final String generatedName = generateBeanName(definition, registry, false);
        registry.registerBeanDefinition(generatedName, definition);
        return generatedName;
    }
}
