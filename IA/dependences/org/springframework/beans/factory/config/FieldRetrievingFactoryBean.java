// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.util.ReflectionUtils;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.ClassUtils;
import java.lang.reflect.Field;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;

public class FieldRetrievingFactoryBean implements FactoryBean<Object>, BeanNameAware, BeanClassLoaderAware, InitializingBean
{
    private Class<?> targetClass;
    private Object targetObject;
    private String targetField;
    private String staticField;
    private String beanName;
    private ClassLoader beanClassLoader;
    private Field fieldObject;
    
    public FieldRetrievingFactoryBean() {
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
    }
    
    public void setTargetClass(final Class<?> targetClass) {
        this.targetClass = targetClass;
    }
    
    public Class<?> getTargetClass() {
        return this.targetClass;
    }
    
    public void setTargetObject(final Object targetObject) {
        this.targetObject = targetObject;
    }
    
    public Object getTargetObject() {
        return this.targetObject;
    }
    
    public void setTargetField(final String targetField) {
        this.targetField = StringUtils.trimAllWhitespace(targetField);
    }
    
    public String getTargetField() {
        return this.targetField;
    }
    
    public void setStaticField(final String staticField) {
        this.staticField = StringUtils.trimAllWhitespace(staticField);
    }
    
    @Override
    public void setBeanName(final String beanName) {
        this.beanName = StringUtils.trimAllWhitespace(BeanFactoryUtils.originalBeanName(beanName));
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    @Override
    public void afterPropertiesSet() throws ClassNotFoundException, NoSuchFieldException {
        if (this.targetClass != null && this.targetObject != null) {
            throw new IllegalArgumentException("Specify either targetClass or targetObject, not both");
        }
        if (this.targetClass == null && this.targetObject == null) {
            if (this.targetField != null) {
                throw new IllegalArgumentException("Specify targetClass or targetObject in combination with targetField");
            }
            if (this.staticField == null) {
                this.staticField = this.beanName;
            }
            final int lastDotIndex = this.staticField.lastIndexOf(46);
            if (lastDotIndex == -1 || lastDotIndex == this.staticField.length()) {
                throw new IllegalArgumentException("staticField must be a fully qualified class plus static field name: e.g. 'example.MyExampleClass.MY_EXAMPLE_FIELD'");
            }
            final String className = this.staticField.substring(0, lastDotIndex);
            final String fieldName = this.staticField.substring(lastDotIndex + 1);
            this.targetClass = ClassUtils.forName(className, this.beanClassLoader);
            this.targetField = fieldName;
        }
        else if (this.targetField == null) {
            throw new IllegalArgumentException("targetField is required");
        }
        final Class<?> targetClass = (this.targetObject != null) ? this.targetObject.getClass() : this.targetClass;
        this.fieldObject = targetClass.getField(this.targetField);
    }
    
    @Override
    public Object getObject() throws IllegalAccessException {
        if (this.fieldObject == null) {
            throw new FactoryBeanNotInitializedException();
        }
        ReflectionUtils.makeAccessible(this.fieldObject);
        if (this.targetObject != null) {
            return this.fieldObject.get(this.targetObject);
        }
        return this.fieldObject.get(null);
    }
    
    @Override
    public Class<?> getObjectType() {
        return (this.fieldObject != null) ? this.fieldObject.getType() : null;
    }
    
    @Override
    public boolean isSingleton() {
        return false;
    }
}
