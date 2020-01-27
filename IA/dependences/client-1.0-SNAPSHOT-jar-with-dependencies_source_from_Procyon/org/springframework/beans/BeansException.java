// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import org.springframework.util.ObjectUtils;
import org.springframework.core.NestedRuntimeException;

public abstract class BeansException extends NestedRuntimeException
{
    public BeansException(final String msg) {
        super(msg);
    }
    
    public BeansException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BeansException)) {
            return false;
        }
        final BeansException otherBe = (BeansException)other;
        return this.getMessage().equals(otherBe.getMessage()) && ObjectUtils.nullSafeEquals(this.getCause(), otherBe.getCause());
    }
    
    @Override
    public int hashCode() {
        return this.getMessage().hashCode();
    }
}
