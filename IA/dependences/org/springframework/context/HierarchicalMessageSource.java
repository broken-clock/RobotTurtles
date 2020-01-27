// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

public interface HierarchicalMessageSource extends MessageSource
{
    void setParentMessageSource(final MessageSource p0);
    
    MessageSource getParentMessageSource();
}
