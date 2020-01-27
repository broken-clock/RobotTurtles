// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import java.util.concurrent.ThreadFactory;
import org.springframework.util.CustomizableThreadCreator;

public class CustomizableThreadFactory extends CustomizableThreadCreator implements ThreadFactory
{
    public CustomizableThreadFactory() {
    }
    
    public CustomizableThreadFactory(final String threadNamePrefix) {
        super(threadNamePrefix);
    }
    
    @Override
    public Thread newThread(final Runnable runnable) {
        return this.createThread(runnable);
    }
}
