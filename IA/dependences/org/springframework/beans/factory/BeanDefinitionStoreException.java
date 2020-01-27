// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

public class BeanDefinitionStoreException extends FatalBeanException
{
    private String resourceDescription;
    private String beanName;
    
    public BeanDefinitionStoreException(final String msg) {
        super(msg);
    }
    
    public BeanDefinitionStoreException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public BeanDefinitionStoreException(final String resourceDescription, final String msg) {
        super(msg);
        this.resourceDescription = resourceDescription;
    }
    
    public BeanDefinitionStoreException(final String resourceDescription, final String msg, final Throwable cause) {
        super(msg, cause);
        this.resourceDescription = resourceDescription;
    }
    
    public BeanDefinitionStoreException(final String resourceDescription, final String beanName, final String msg) {
        this(resourceDescription, beanName, msg, null);
    }
    
    public BeanDefinitionStoreException(final String resourceDescription, final String beanName, final String msg, final Throwable cause) {
        super("Invalid bean definition with name '" + beanName + "' defined in " + resourceDescription + ": " + msg, cause);
        this.resourceDescription = resourceDescription;
        this.beanName = beanName;
    }
    
    public String getResourceDescription() {
        return this.resourceDescription;
    }
    
    public String getBeanName() {
        return this.beanName;
    }
}
