// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.util.ObjectUtils;
import org.springframework.beans.factory.config.RuntimeBeanReference;

public class BeanDefinitionBuilder
{
    private AbstractBeanDefinition beanDefinition;
    private int constructorArgIndex;
    
    public static BeanDefinitionBuilder genericBeanDefinition() {
        final BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        builder.beanDefinition = new GenericBeanDefinition();
        return builder;
    }
    
    public static BeanDefinitionBuilder genericBeanDefinition(final Class<?> beanClass) {
        final BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        (builder.beanDefinition = new GenericBeanDefinition()).setBeanClass(beanClass);
        return builder;
    }
    
    public static BeanDefinitionBuilder genericBeanDefinition(final String beanClassName) {
        final BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        (builder.beanDefinition = new GenericBeanDefinition()).setBeanClassName(beanClassName);
        return builder;
    }
    
    public static BeanDefinitionBuilder rootBeanDefinition(final Class<?> beanClass) {
        return rootBeanDefinition(beanClass, null);
    }
    
    public static BeanDefinitionBuilder rootBeanDefinition(final Class<?> beanClass, final String factoryMethodName) {
        final BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        (builder.beanDefinition = new RootBeanDefinition()).setBeanClass(beanClass);
        builder.beanDefinition.setFactoryMethodName(factoryMethodName);
        return builder;
    }
    
    public static BeanDefinitionBuilder rootBeanDefinition(final String beanClassName) {
        return rootBeanDefinition(beanClassName, null);
    }
    
    public static BeanDefinitionBuilder rootBeanDefinition(final String beanClassName, final String factoryMethodName) {
        final BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        (builder.beanDefinition = new RootBeanDefinition()).setBeanClassName(beanClassName);
        builder.beanDefinition.setFactoryMethodName(factoryMethodName);
        return builder;
    }
    
    public static BeanDefinitionBuilder childBeanDefinition(final String parentName) {
        final BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        builder.beanDefinition = new ChildBeanDefinition(parentName);
        return builder;
    }
    
    private BeanDefinitionBuilder() {
    }
    
    public AbstractBeanDefinition getRawBeanDefinition() {
        return this.beanDefinition;
    }
    
    public AbstractBeanDefinition getBeanDefinition() {
        this.beanDefinition.validate();
        return this.beanDefinition;
    }
    
    public BeanDefinitionBuilder setParentName(final String parentName) {
        this.beanDefinition.setParentName(parentName);
        return this;
    }
    
    public BeanDefinitionBuilder setFactoryMethod(final String factoryMethod) {
        this.beanDefinition.setFactoryMethodName(factoryMethod);
        return this;
    }
    
    @Deprecated
    public BeanDefinitionBuilder addConstructorArg(final Object value) {
        return this.addConstructorArgValue(value);
    }
    
    public BeanDefinitionBuilder addConstructorArgValue(final Object value) {
        this.beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(this.constructorArgIndex++, value);
        return this;
    }
    
    public BeanDefinitionBuilder addConstructorArgReference(final String beanName) {
        this.beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(this.constructorArgIndex++, new RuntimeBeanReference(beanName));
        return this;
    }
    
    public BeanDefinitionBuilder addPropertyValue(final String name, final Object value) {
        this.beanDefinition.getPropertyValues().add(name, value);
        return this;
    }
    
    public BeanDefinitionBuilder addPropertyReference(final String name, final String beanName) {
        this.beanDefinition.getPropertyValues().add(name, new RuntimeBeanReference(beanName));
        return this;
    }
    
    public BeanDefinitionBuilder setInitMethodName(final String methodName) {
        this.beanDefinition.setInitMethodName(methodName);
        return this;
    }
    
    public BeanDefinitionBuilder setDestroyMethodName(final String methodName) {
        this.beanDefinition.setDestroyMethodName(methodName);
        return this;
    }
    
    public BeanDefinitionBuilder setScope(final String scope) {
        this.beanDefinition.setScope(scope);
        return this;
    }
    
    public BeanDefinitionBuilder setAbstract(final boolean flag) {
        this.beanDefinition.setAbstract(flag);
        return this;
    }
    
    public BeanDefinitionBuilder setLazyInit(final boolean lazy) {
        this.beanDefinition.setLazyInit(lazy);
        return this;
    }
    
    public BeanDefinitionBuilder setAutowireMode(final int autowireMode) {
        this.beanDefinition.setAutowireMode(autowireMode);
        return this;
    }
    
    public BeanDefinitionBuilder setDependencyCheck(final int dependencyCheck) {
        this.beanDefinition.setDependencyCheck(dependencyCheck);
        return this;
    }
    
    public BeanDefinitionBuilder addDependsOn(final String beanName) {
        if (this.beanDefinition.getDependsOn() == null) {
            this.beanDefinition.setDependsOn(new String[] { beanName });
        }
        else {
            final String[] added = ObjectUtils.addObjectToArray(this.beanDefinition.getDependsOn(), beanName);
            this.beanDefinition.setDependsOn(added);
        }
        return this;
    }
    
    public BeanDefinitionBuilder setRole(final int role) {
        this.beanDefinition.setRole(role);
        return this;
    }
}
