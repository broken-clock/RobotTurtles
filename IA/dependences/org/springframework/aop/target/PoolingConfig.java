// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

public interface PoolingConfig
{
    int getMaxSize();
    
    int getActiveCount() throws UnsupportedOperationException;
    
    int getIdleCount() throws UnsupportedOperationException;
}
