// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import java.util.EventListener;

public interface ReaderEventListener extends EventListener
{
    void defaultsRegistered(final DefaultsDefinition p0);
    
    void componentRegistered(final ComponentDefinition p0);
    
    void aliasRegistered(final AliasDefinition p0);
    
    void importProcessed(final ImportDefinition p0);
}
