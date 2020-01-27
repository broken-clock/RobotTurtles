// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.core;

public interface FormatFeature
{
    boolean enabledByDefault();
    
    int getMask();
    
    boolean enabledIn(final int p0);
}
