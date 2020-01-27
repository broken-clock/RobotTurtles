// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

public class CacheEvictOperation extends CacheOperation
{
    private boolean cacheWide;
    private boolean beforeInvocation;
    
    public CacheEvictOperation() {
        this.cacheWide = false;
        this.beforeInvocation = false;
    }
    
    public void setCacheWide(final boolean cacheWide) {
        this.cacheWide = cacheWide;
    }
    
    public boolean isCacheWide() {
        return this.cacheWide;
    }
    
    public void setBeforeInvocation(final boolean beforeInvocation) {
        this.beforeInvocation = beforeInvocation;
    }
    
    public boolean isBeforeInvocation() {
        return this.beforeInvocation;
    }
    
    @Override
    protected StringBuilder getOperationDescription() {
        final StringBuilder sb = super.getOperationDescription();
        sb.append(",");
        sb.append(this.cacheWide);
        sb.append(",");
        sb.append(this.beforeInvocation);
        return sb;
    }
}
