// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.config;

import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;

public class AspectComponentDefinition extends CompositeComponentDefinition
{
    private final BeanDefinition[] beanDefinitions;
    private final BeanReference[] beanReferences;
    
    public AspectComponentDefinition(final String aspectName, final BeanDefinition[] beanDefinitions, final BeanReference[] beanReferences, final Object source) {
        super(aspectName, source);
        this.beanDefinitions = ((beanDefinitions != null) ? beanDefinitions : new BeanDefinition[0]);
        this.beanReferences = ((beanReferences != null) ? beanReferences : new BeanReference[0]);
    }
    
    @Override
    public BeanDefinition[] getBeanDefinitions() {
        return this.beanDefinitions;
    }
    
    @Override
    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }
}
