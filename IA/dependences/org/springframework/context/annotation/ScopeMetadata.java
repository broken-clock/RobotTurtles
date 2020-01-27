// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.util.Assert;

public class ScopeMetadata
{
    private String scopeName;
    private ScopedProxyMode scopedProxyMode;
    
    public ScopeMetadata() {
        this.scopeName = "singleton";
        this.scopedProxyMode = ScopedProxyMode.NO;
    }
    
    public void setScopeName(final String scopeName) {
        Assert.notNull(scopeName, "'scopeName' must not be null");
        this.scopeName = scopeName;
    }
    
    public String getScopeName() {
        return this.scopeName;
    }
    
    public void setScopedProxyMode(final ScopedProxyMode scopedProxyMode) {
        Assert.notNull(scopedProxyMode, "'scopedProxyMode' must not be null");
        this.scopedProxyMode = scopedProxyMode;
    }
    
    public ScopedProxyMode getScopedProxyMode() {
        return this.scopedProxyMode;
    }
}
