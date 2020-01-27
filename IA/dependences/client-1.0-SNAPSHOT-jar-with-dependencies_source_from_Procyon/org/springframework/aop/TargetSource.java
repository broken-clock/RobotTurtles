// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

public interface TargetSource extends TargetClassAware
{
    Class<?> getTargetClass();
    
    boolean isStatic();
    
    Object getTarget() throws Exception;
    
    void releaseTarget(final Object p0) throws Exception;
}
