// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.util.HashSet;
import java.lang.reflect.Method;
import org.springframework.util.Assert;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import java.lang.reflect.Member;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

public class RootBeanDefinition extends AbstractBeanDefinition
{
    boolean allowCaching;
    private BeanDefinitionHolder decoratedDefinition;
    private volatile Class<?> targetType;
    boolean isFactoryMethodUnique;
    final Object constructorArgumentLock;
    Object resolvedConstructorOrFactoryMethod;
    volatile Class<?> resolvedFactoryMethodReturnType;
    boolean constructorArgumentsResolved;
    Object[] resolvedConstructorArguments;
    Object[] preparedConstructorArguments;
    final Object postProcessingLock;
    boolean postProcessed;
    volatile Boolean beforeInstantiationResolved;
    private Set<Member> externallyManagedConfigMembers;
    private Set<String> externallyManagedInitMethods;
    private Set<String> externallyManagedDestroyMethods;
    
    public RootBeanDefinition() {
        this.allowCaching = true;
        this.isFactoryMethodUnique = false;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
    }
    
    public RootBeanDefinition(final Class<?> beanClass) {
        this.allowCaching = true;
        this.isFactoryMethodUnique = false;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        this.setBeanClass(beanClass);
    }
    
    public RootBeanDefinition(final Class<?> beanClass, final int autowireMode, final boolean dependencyCheck) {
        this.allowCaching = true;
        this.isFactoryMethodUnique = false;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        this.setBeanClass(beanClass);
        this.setAutowireMode(autowireMode);
        if (dependencyCheck && this.getResolvedAutowireMode() != 3) {
            this.setDependencyCheck(1);
        }
    }
    
    public RootBeanDefinition(final Class<?> beanClass, final ConstructorArgumentValues cargs, final MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.allowCaching = true;
        this.isFactoryMethodUnique = false;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        this.setBeanClass(beanClass);
    }
    
    public RootBeanDefinition(final String beanClassName) {
        this.allowCaching = true;
        this.isFactoryMethodUnique = false;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        this.setBeanClassName(beanClassName);
    }
    
    public RootBeanDefinition(final String beanClassName, final ConstructorArgumentValues cargs, final MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.allowCaching = true;
        this.isFactoryMethodUnique = false;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        this.setBeanClassName(beanClassName);
    }
    
    public RootBeanDefinition(final RootBeanDefinition original) {
        super(original);
        this.allowCaching = true;
        this.isFactoryMethodUnique = false;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
        this.allowCaching = original.allowCaching;
        this.decoratedDefinition = original.decoratedDefinition;
        this.targetType = original.targetType;
        this.isFactoryMethodUnique = original.isFactoryMethodUnique;
    }
    
    RootBeanDefinition(final BeanDefinition original) {
        super(original);
        this.allowCaching = true;
        this.isFactoryMethodUnique = false;
        this.constructorArgumentLock = new Object();
        this.constructorArgumentsResolved = false;
        this.postProcessingLock = new Object();
        this.postProcessed = false;
    }
    
    @Override
    public String getParentName() {
        return null;
    }
    
    @Override
    public void setParentName(final String parentName) {
        if (parentName != null) {
            throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
        }
    }
    
    public void setDecoratedDefinition(final BeanDefinitionHolder decoratedDefinition) {
        this.decoratedDefinition = decoratedDefinition;
    }
    
    public BeanDefinitionHolder getDecoratedDefinition() {
        return this.decoratedDefinition;
    }
    
    public void setTargetType(final Class<?> targetType) {
        this.targetType = targetType;
    }
    
    public Class<?> getTargetType() {
        return this.targetType;
    }
    
    public void setUniqueFactoryMethodName(final String name) {
        Assert.hasText(name, "Factory method name must not be empty");
        this.setFactoryMethodName(name);
        this.isFactoryMethodUnique = true;
    }
    
    public boolean isFactoryMethod(final Method candidate) {
        return candidate != null && candidate.getName().equals(this.getFactoryMethodName());
    }
    
    public Method getResolvedFactoryMethod() {
        synchronized (this.constructorArgumentLock) {
            final Object candidate = this.resolvedConstructorOrFactoryMethod;
            return (candidate instanceof Method) ? ((Method)candidate) : null;
        }
    }
    
    public void registerExternallyManagedConfigMember(final Member configMember) {
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedConfigMembers == null) {
                this.externallyManagedConfigMembers = new HashSet<Member>(1);
            }
            this.externallyManagedConfigMembers.add(configMember);
        }
    }
    
    public boolean isExternallyManagedConfigMember(final Member configMember) {
        synchronized (this.postProcessingLock) {
            return this.externallyManagedConfigMembers != null && this.externallyManagedConfigMembers.contains(configMember);
        }
    }
    
    public void registerExternallyManagedInitMethod(final String initMethod) {
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedInitMethods == null) {
                this.externallyManagedInitMethods = new HashSet<String>(1);
            }
            this.externallyManagedInitMethods.add(initMethod);
        }
    }
    
    public boolean isExternallyManagedInitMethod(final String initMethod) {
        synchronized (this.postProcessingLock) {
            return this.externallyManagedInitMethods != null && this.externallyManagedInitMethods.contains(initMethod);
        }
    }
    
    public void registerExternallyManagedDestroyMethod(final String destroyMethod) {
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedDestroyMethods == null) {
                this.externallyManagedDestroyMethods = new HashSet<String>(1);
            }
            this.externallyManagedDestroyMethods.add(destroyMethod);
        }
    }
    
    public boolean isExternallyManagedDestroyMethod(final String destroyMethod) {
        synchronized (this.postProcessingLock) {
            return this.externallyManagedDestroyMethods != null && this.externallyManagedDestroyMethods.contains(destroyMethod);
        }
    }
    
    @Override
    public RootBeanDefinition cloneBeanDefinition() {
        return new RootBeanDefinition(this);
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof RootBeanDefinition && super.equals(other));
    }
    
    @Override
    public String toString() {
        return "Root bean: " + super.toString();
    }
}
