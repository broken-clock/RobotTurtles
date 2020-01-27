// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import org.springframework.web.context.request.NativeWebRequest;

public interface DeferredResultProcessingInterceptor
{
     <T> void beforeConcurrentHandling(final NativeWebRequest p0, final DeferredResult<T> p1) throws Exception;
    
     <T> void preProcess(final NativeWebRequest p0, final DeferredResult<T> p1) throws Exception;
    
     <T> void postProcess(final NativeWebRequest p0, final DeferredResult<T> p1, final Object p2) throws Exception;
    
     <T> boolean handleTimeout(final NativeWebRequest p0, final DeferredResult<T> p1) throws Exception;
    
     <T> void afterCompletion(final NativeWebRequest p0, final DeferredResult<T> p1) throws Exception;
}
