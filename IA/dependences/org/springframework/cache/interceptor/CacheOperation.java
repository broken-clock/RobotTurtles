// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import java.util.LinkedHashSet;
import org.springframework.util.Assert;
import java.util.Collections;
import java.util.Set;

public abstract class CacheOperation
{
    private Set<String> cacheNames;
    private String condition;
    private String key;
    private String name;
    
    public CacheOperation() {
        this.cacheNames = Collections.emptySet();
        this.condition = "";
        this.key = "";
        this.name = "";
    }
    
    public Set<String> getCacheNames() {
        return this.cacheNames;
    }
    
    public String getCondition() {
        return this.condition;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setCacheName(final String cacheName) {
        Assert.hasText(cacheName);
        this.cacheNames = Collections.singleton(cacheName);
    }
    
    public void setCacheNames(final String[] cacheNames) {
        Assert.notEmpty(cacheNames);
        this.cacheNames = new LinkedHashSet<String>(cacheNames.length);
        for (final String string : cacheNames) {
            this.cacheNames.add(string);
        }
    }
    
    public void setCondition(final String condition) {
        Assert.notNull(condition);
        this.condition = condition;
    }
    
    public void setKey(final String key) {
        Assert.notNull(key);
        this.key = key;
    }
    
    public void setName(final String name) {
        Assert.hasText(name);
        this.name = name;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof CacheOperation && this.toString().equals(other.toString());
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override
    public String toString() {
        return this.getOperationDescription().toString();
    }
    
    protected StringBuilder getOperationDescription() {
        final StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName());
        result.append("[");
        result.append(this.name);
        result.append("] caches=");
        result.append(this.cacheNames);
        result.append(" | key='");
        result.append(this.key);
        result.append("' | condition='");
        result.append(this.condition);
        result.append("'");
        return result;
    }
}
