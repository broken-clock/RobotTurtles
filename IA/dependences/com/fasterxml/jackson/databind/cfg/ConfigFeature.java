// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.cfg;

public interface ConfigFeature
{
    boolean enabledByDefault();
    
    int getMask();
    
    boolean enabledIn(final int p0);
}
