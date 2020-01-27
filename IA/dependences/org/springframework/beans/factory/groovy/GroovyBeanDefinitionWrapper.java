// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.groovy;

import java.util.ArrayList;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import java.util.Iterator;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import java.util.Collection;
import java.util.List;
import groovy.lang.GroovyObjectSupport;

class GroovyBeanDefinitionWrapper extends GroovyObjectSupport
{
    private static final String PARENT = "parent";
    private static final String AUTOWIRE = "autowire";
    private static final String CONSTRUCTOR_ARGS = "constructorArgs";
    private static final String FACTORY_BEAN = "factoryBean";
    private static final String FACTORY_METHOD = "factoryMethod";
    private static final String INIT_METHOD = "initMethod";
    private static final String DESTROY_METHOD = "destroyMethod";
    private static final String SINGLETON = "singleton";
    private static final List<String> dynamicProperties;
    private String beanName;
    private Class<?> clazz;
    private Collection<?> constructorArgs;
    private AbstractBeanDefinition definition;
    private BeanWrapper definitionWrapper;
    private String parentName;
    
    public GroovyBeanDefinitionWrapper(final String beanName) {
        this.beanName = beanName;
    }
    
    public GroovyBeanDefinitionWrapper(final String beanName, final Class<?> clazz) {
        this.beanName = beanName;
        this.clazz = clazz;
    }
    
    public GroovyBeanDefinitionWrapper(final String beanName, final Class<?> clazz, final Collection<?> constructorArgs) {
        this.beanName = beanName;
        this.clazz = clazz;
        this.constructorArgs = constructorArgs;
    }
    
    public String getBeanName() {
        return this.beanName;
    }
    
    public void setBeanDefinition(final AbstractBeanDefinition definition) {
        this.definition = definition;
    }
    
    public AbstractBeanDefinition getBeanDefinition() {
        if (this.definition == null) {
            this.definition = this.createBeanDefinition();
        }
        return this.definition;
    }
    
    protected AbstractBeanDefinition createBeanDefinition() {
        final AbstractBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(this.clazz);
        if (!CollectionUtils.isEmpty(this.constructorArgs)) {
            final ConstructorArgumentValues cav = new ConstructorArgumentValues();
            for (final Object constructorArg : this.constructorArgs) {
                cav.addGenericArgumentValue(constructorArg);
            }
            bd.setConstructorArgumentValues(cav);
        }
        if (this.parentName != null) {
            bd.setParentName(this.parentName);
        }
        this.definitionWrapper = new BeanWrapperImpl(bd);
        return bd;
    }
    
    public void setBeanDefinitionHolder(final BeanDefinitionHolder holder) {
        this.definition = (AbstractBeanDefinition)holder.getBeanDefinition();
        this.beanName = holder.getBeanName();
    }
    
    public BeanDefinitionHolder getBeanDefinitionHolder() {
        return new BeanDefinitionHolder(this.getBeanDefinition(), this.getBeanName());
    }
    
    public void setParent(final Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Parent bean cannot be set to a null runtime bean reference!");
        }
        if (obj instanceof String) {
            this.parentName = (String)obj;
        }
        else if (obj instanceof RuntimeBeanReference) {
            this.parentName = ((RuntimeBeanReference)obj).getBeanName();
        }
        else if (obj instanceof GroovyBeanDefinitionWrapper) {
            this.parentName = ((GroovyBeanDefinitionWrapper)obj).getBeanName();
        }
        this.getBeanDefinition().setParentName(this.parentName);
        this.getBeanDefinition().setAbstract(false);
    }
    
    public GroovyBeanDefinitionWrapper addProperty(final String propertyName, Object propertyValue) {
        if (propertyValue instanceof GroovyBeanDefinitionWrapper) {
            propertyValue = ((GroovyBeanDefinitionWrapper)propertyValue).getBeanDefinition();
        }
        this.getBeanDefinition().getPropertyValues().add(propertyName, propertyValue);
        return this;
    }
    
    public Object getProperty(final String property) {
        if (this.definitionWrapper.isReadableProperty(property)) {
            return this.definitionWrapper.getPropertyValue(property);
        }
        if (GroovyBeanDefinitionWrapper.dynamicProperties.contains(property)) {
            return null;
        }
        return super.getProperty(property);
    }
    
    public void setProperty(final String property, final Object newValue) {
        if ("parent".equals(property)) {
            this.setParent(newValue);
        }
        else {
            final AbstractBeanDefinition bd = this.getBeanDefinition();
            if ("autowire".equals(property)) {
                if ("byName".equals(newValue)) {
                    bd.setAutowireMode(1);
                }
                else if ("byType".equals(newValue)) {
                    bd.setAutowireMode(2);
                }
                else if ("constructor".equals(newValue)) {
                    bd.setAutowireMode(3);
                }
                else if (Boolean.TRUE.equals(newValue)) {
                    bd.setAutowireMode(1);
                }
            }
            else if ("constructorArgs".equals(property) && newValue instanceof List) {
                final ConstructorArgumentValues cav = new ConstructorArgumentValues();
                final List args = (List)newValue;
                for (final Object arg : args) {
                    cav.addGenericArgumentValue(arg);
                }
                bd.setConstructorArgumentValues(cav);
            }
            else if ("factoryBean".equals(property)) {
                if (newValue != null) {
                    bd.setFactoryBeanName(newValue.toString());
                }
            }
            else if ("factoryMethod".equals(property)) {
                if (newValue != null) {
                    bd.setFactoryMethodName(newValue.toString());
                }
            }
            else if ("initMethod".equals(property)) {
                if (newValue != null) {
                    bd.setInitMethodName(newValue.toString());
                }
            }
            else if ("destroyMethod".equals(property)) {
                if (newValue != null) {
                    bd.setDestroyMethodName(newValue.toString());
                }
            }
            else if ("singleton".equals(property)) {
                bd.setScope(Boolean.TRUE.equals(newValue) ? "singleton" : "prototype");
            }
            else if (this.definitionWrapper.isWritableProperty(property)) {
                this.definitionWrapper.setPropertyValue(property, newValue);
            }
            else {
                super.setProperty(property, newValue);
            }
        }
    }
    
    static {
        (dynamicProperties = new ArrayList<String>(8)).add("parent");
        GroovyBeanDefinitionWrapper.dynamicProperties.add("autowire");
        GroovyBeanDefinitionWrapper.dynamicProperties.add("constructorArgs");
        GroovyBeanDefinitionWrapper.dynamicProperties.add("factoryBean");
        GroovyBeanDefinitionWrapper.dynamicProperties.add("factoryMethod");
        GroovyBeanDefinitionWrapper.dynamicProperties.add("initMethod");
        GroovyBeanDefinitionWrapper.dynamicProperties.add("destroyMethod");
        GroovyBeanDefinitionWrapper.dynamicProperties.add("singleton");
    }
}
