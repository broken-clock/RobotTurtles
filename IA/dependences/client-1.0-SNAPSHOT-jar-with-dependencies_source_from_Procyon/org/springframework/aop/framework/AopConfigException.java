// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.springframework.core.NestedRuntimeException;

public class AopConfigException extends NestedRuntimeException
{
    public AopConfigException(final String msg) {
        super(msg);
    }
    
    public AopConfigException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
