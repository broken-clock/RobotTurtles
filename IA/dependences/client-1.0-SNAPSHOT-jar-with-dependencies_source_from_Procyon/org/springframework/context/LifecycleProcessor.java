// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

public interface LifecycleProcessor extends Lifecycle
{
    void onRefresh();
    
    void onClose();
}
