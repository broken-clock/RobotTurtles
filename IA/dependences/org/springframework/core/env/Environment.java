// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

public interface Environment extends PropertyResolver
{
    String[] getActiveProfiles();
    
    String[] getDefaultProfiles();
    
    boolean acceptsProfiles(final String... p0);
}
