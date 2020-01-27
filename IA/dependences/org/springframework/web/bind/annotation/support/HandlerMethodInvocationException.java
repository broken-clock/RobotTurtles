// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.bind.annotation.support;

import java.lang.reflect.Method;
import org.springframework.core.NestedRuntimeException;

public class HandlerMethodInvocationException extends NestedRuntimeException
{
    public HandlerMethodInvocationException(final Method handlerMethod, final Throwable cause) {
        super("Failed to invoke handler method [" + handlerMethod + "]", cause);
    }
}
