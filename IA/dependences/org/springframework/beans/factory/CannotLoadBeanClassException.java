// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

public class CannotLoadBeanClassException extends FatalBeanException
{
    private String resourceDescription;
    private String beanName;
    private String beanClassName;
    
    public CannotLoadBeanClassException(final String resourceDescription, final String beanName, final String beanClassName, final ClassNotFoundException cause) {
        super("Cannot find class [" + beanClassName + "] for bean with name '" + beanName + "' defined in " + resourceDescription, cause);
        this.resourceDescription = resourceDescription;
        this.beanName = beanName;
        this.beanClassName = beanClassName;
    }
    
    public CannotLoadBeanClassException(final String resourceDescription, final String beanName, final String beanClassName, final LinkageError cause) {
        super("Error loading class [" + beanClassName + "] for bean with name '" + beanName + "' defined in " + resourceDescription + ": problem with class file or dependent class", cause);
        this.resourceDescription = resourceDescription;
        this.beanName = beanName;
        this.beanClassName = beanClassName;
    }
    
    public String getResourceDescription() {
        return this.resourceDescription;
    }
    
    public String getBeanName() {
        return this.beanName;
    }
    
    public String getBeanClassName() {
        return this.beanClassName;
    }
}
