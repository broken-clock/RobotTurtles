// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.util.Assert;

public class BeanExpressionContext
{
    private final ConfigurableBeanFactory beanFactory;
    private final Scope scope;
    
    public BeanExpressionContext(final ConfigurableBeanFactory beanFactory, final Scope scope) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
        this.scope = scope;
    }
    
    public final ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }
    
    public final Scope getScope() {
        return this.scope;
    }
    
    public boolean containsObject(final String key) {
        return this.beanFactory.containsBean(key) || (this.scope != null && this.scope.resolveContextualObject(key) != null);
    }
    
    public Object getObject(final String key) {
        if (this.beanFactory.containsBean(key)) {
            return this.beanFactory.getBean(key);
        }
        if (this.scope != null) {
            return this.scope.resolveContextualObject(key);
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BeanExpressionContext)) {
            return false;
        }
        final BeanExpressionContext otherContext = (BeanExpressionContext)other;
        return this.beanFactory == otherContext.beanFactory && this.scope == otherContext.scope;
    }
    
    @Override
    public int hashCode() {
        return this.beanFactory.hashCode();
    }
}
