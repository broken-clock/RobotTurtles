// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.util.Assert;

public class RuntimeBeanNameReference implements BeanReference
{
    private final String beanName;
    private Object source;
    
    public RuntimeBeanNameReference(final String beanName) {
        Assert.hasText(beanName, "'beanName' must not be empty");
        this.beanName = beanName;
    }
    
    @Override
    public String getBeanName() {
        return this.beanName;
    }
    
    public void setSource(final Object source) {
        this.source = source;
    }
    
    @Override
    public Object getSource() {
        return this.source;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RuntimeBeanNameReference)) {
            return false;
        }
        final RuntimeBeanNameReference that = (RuntimeBeanNameReference)other;
        return this.beanName.equals(that.beanName);
    }
    
    @Override
    public int hashCode() {
        return this.beanName.hashCode();
    }
    
    @Override
    public String toString() {
        return '<' + this.getBeanName() + '>';
    }
}
