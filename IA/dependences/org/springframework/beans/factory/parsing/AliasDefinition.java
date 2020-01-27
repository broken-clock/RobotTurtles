// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import org.springframework.util.Assert;
import org.springframework.beans.BeanMetadataElement;

public class AliasDefinition implements BeanMetadataElement
{
    private final String beanName;
    private final String alias;
    private final Object source;
    
    public AliasDefinition(final String beanName, final String alias) {
        this(beanName, alias, null);
    }
    
    public AliasDefinition(final String beanName, final String alias, final Object source) {
        Assert.notNull(beanName, "Bean name must not be null");
        Assert.notNull(alias, "Alias must not be null");
        this.beanName = beanName;
        this.alias = alias;
        this.source = source;
    }
    
    public final String getBeanName() {
        return this.beanName;
    }
    
    public final String getAlias() {
        return this.alias;
    }
    
    @Override
    public final Object getSource() {
        return this.source;
    }
}
