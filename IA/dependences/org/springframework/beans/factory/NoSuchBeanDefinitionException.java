// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import org.springframework.util.StringUtils;
import org.springframework.beans.BeansException;

public class NoSuchBeanDefinitionException extends BeansException
{
    private String beanName;
    private Class<?> beanType;
    
    public NoSuchBeanDefinitionException(final String name) {
        super("No bean named '" + name + "' is defined");
        this.beanName = name;
    }
    
    public NoSuchBeanDefinitionException(final String name, final String message) {
        super("No bean named '" + name + "' is defined: " + message);
        this.beanName = name;
    }
    
    public NoSuchBeanDefinitionException(final Class<?> type) {
        super("No qualifying bean of type [" + type.getName() + "] is defined");
        this.beanType = type;
    }
    
    public NoSuchBeanDefinitionException(final Class<?> type, final String message) {
        super("No qualifying bean of type [" + type.getName() + "] is defined: " + message);
        this.beanType = type;
    }
    
    public NoSuchBeanDefinitionException(final Class<?> type, final String dependencyDescription, final String message) {
        super("No qualifying bean of type [" + type.getName() + "] found for dependency" + (StringUtils.hasLength(dependencyDescription) ? (" [" + dependencyDescription + "]") : "") + ": " + message);
        this.beanType = type;
    }
    
    public String getBeanName() {
        return this.beanName;
    }
    
    public Class<?> getBeanType() {
        return this.beanType;
    }
    
    public int getNumberOfBeansFound() {
        return 0;
    }
}
