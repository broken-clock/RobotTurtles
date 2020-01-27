// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

public interface AopProxy
{
    Object getProxy();
    
    Object getProxy(final ClassLoader p0);
}
