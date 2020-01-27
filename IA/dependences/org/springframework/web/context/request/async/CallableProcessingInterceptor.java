// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import java.util.concurrent.Callable;
import org.springframework.web.context.request.NativeWebRequest;

public interface CallableProcessingInterceptor
{
    public static final Object RESULT_NONE = new Object();
    public static final Object RESPONSE_HANDLED = new Object();
    
     <T> void beforeConcurrentHandling(final NativeWebRequest p0, final Callable<T> p1) throws Exception;
    
     <T> void preProcess(final NativeWebRequest p0, final Callable<T> p1) throws Exception;
    
     <T> void postProcess(final NativeWebRequest p0, final Callable<T> p1, final Object p2) throws Exception;
    
     <T> Object handleTimeout(final NativeWebRequest p0, final Callable<T> p1) throws Exception;
    
     <T> void afterCompletion(final NativeWebRequest p0, final Callable<T> p1) throws Exception;
}
