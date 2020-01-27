// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.annotation;

import java.util.concurrent.Executor;

public interface AsyncConfigurer
{
    Executor getAsyncExecutor();
}
