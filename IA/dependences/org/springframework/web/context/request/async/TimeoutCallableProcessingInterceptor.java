// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.Callable;
import org.springframework.web.context.request.NativeWebRequest;

public class TimeoutCallableProcessingInterceptor extends CallableProcessingInterceptorAdapter
{
    @Override
    public <T> Object handleTimeout(final NativeWebRequest request, final Callable<T> task) throws Exception {
        final HttpServletResponse servletResponse = request.getNativeResponse(HttpServletResponse.class);
        if (!servletResponse.isCommitted()) {
            servletResponse.sendError(HttpStatus.SERVICE_UNAVAILABLE.value());
        }
        return CallableProcessingInterceptor.RESPONSE_HANDLED;
    }
}
