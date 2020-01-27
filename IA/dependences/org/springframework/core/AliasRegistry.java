// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

public interface AliasRegistry
{
    void registerAlias(final String p0, final String p1);
    
    void removeAlias(final String p0);
    
    boolean isAlias(final String p0);
    
    String[] getAliases(final String p0);
}
