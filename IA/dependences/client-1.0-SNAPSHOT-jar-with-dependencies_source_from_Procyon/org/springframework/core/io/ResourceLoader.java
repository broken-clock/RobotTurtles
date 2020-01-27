// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

public interface ResourceLoader
{
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    
    Resource getResource(final String p0);
    
    ClassLoader getClassLoader();
}
