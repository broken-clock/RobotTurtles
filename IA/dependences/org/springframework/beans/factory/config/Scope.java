// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.factory.ObjectFactory;

public interface Scope
{
    Object get(final String p0, final ObjectFactory<?> p1);
    
    Object remove(final String p0);
    
    void registerDestructionCallback(final String p0, final Runnable p1);
    
    Object resolveContextualObject(final String p0);
    
    String getConversationId();
}
