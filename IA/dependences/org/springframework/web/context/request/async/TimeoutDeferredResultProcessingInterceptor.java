// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.NativeWebRequest;

public class TimeoutDeferredResultProcessingInterceptor extends DeferredResultProcessingInterceptorAdapter
{
    @Override
    public <T> boolean handleTimeout(final NativeWebRequest request, final DeferredResult<T> deferredResult) throws Exception {
        final HttpServletResponse servletResponse = request.getNativeResponse(HttpServletResponse.class);
        if (!servletResponse.isCommitted()) {
            servletResponse.sendError(HttpStatus.SERVICE_UNAVAILABLE.value());
        }
        return false;
    }
}
