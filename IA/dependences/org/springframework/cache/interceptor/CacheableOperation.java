// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

public class CacheableOperation extends CacheOperation
{
    private String unless;
    
    public String getUnless() {
        return this.unless;
    }
    
    public void setUnless(final String unless) {
        this.unless = unless;
    }
    
    @Override
    protected StringBuilder getOperationDescription() {
        final StringBuilder sb = super.getOperationDescription();
        sb.append(" | unless='");
        sb.append(this.unless);
        sb.append("'");
        return sb;
    }
}
