// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

public interface Mergeable
{
    boolean isMergeEnabled();
    
    Object merge(final Object p0);
}
