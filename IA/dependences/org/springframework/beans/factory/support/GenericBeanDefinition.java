// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;

public class GenericBeanDefinition extends AbstractBeanDefinition
{
    private String parentName;
    
    public GenericBeanDefinition() {
    }
    
    public GenericBeanDefinition(final BeanDefinition original) {
        super(original);
    }
    
    @Override
    public void setParentName(final String parentName) {
        this.parentName = parentName;
    }
    
    @Override
    public String getParentName() {
        return this.parentName;
    }
    
    @Override
    public AbstractBeanDefinition cloneBeanDefinition() {
        return new GenericBeanDefinition(this);
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof GenericBeanDefinition && super.equals(other));
    }
    
    @Override
    public String toString() {
        return "Generic bean: " + super.toString();
    }
}
