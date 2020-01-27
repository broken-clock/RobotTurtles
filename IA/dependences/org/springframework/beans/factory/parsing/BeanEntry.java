// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

public class BeanEntry implements ParseState.Entry
{
    private String beanDefinitionName;
    
    public BeanEntry(final String beanDefinitionName) {
        this.beanDefinitionName = beanDefinitionName;
    }
    
    @Override
    public String toString() {
        return "Bean '" + this.beanDefinitionName + "'";
    }
}
