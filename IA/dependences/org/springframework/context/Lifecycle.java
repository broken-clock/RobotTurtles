// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

public interface Lifecycle
{
    void start();
    
    void stop();
    
    boolean isRunning();
}
