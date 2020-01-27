// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling;

public interface SchedulingAwareRunnable extends Runnable
{
    boolean isLongLived();
}
