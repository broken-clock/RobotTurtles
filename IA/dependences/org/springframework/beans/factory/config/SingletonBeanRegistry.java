// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

public interface SingletonBeanRegistry
{
    void registerSingleton(final String p0, final Object p1);
    
    Object getSingleton(final String p0);
    
    boolean containsSingleton(final String p0);
    
    String[] getSingletonNames();
    
    int getSingletonCount();
}
