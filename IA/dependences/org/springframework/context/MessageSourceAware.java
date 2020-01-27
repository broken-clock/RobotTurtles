// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

import org.springframework.beans.factory.Aware;

public interface MessageSourceAware extends Aware
{
    void setMessageSource(final MessageSource p0);
}
