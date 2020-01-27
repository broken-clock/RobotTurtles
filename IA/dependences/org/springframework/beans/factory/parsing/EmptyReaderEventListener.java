// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

public class EmptyReaderEventListener implements ReaderEventListener
{
    @Override
    public void defaultsRegistered(final DefaultsDefinition defaultsDefinition) {
    }
    
    @Override
    public void componentRegistered(final ComponentDefinition componentDefinition) {
    }
    
    @Override
    public void aliasRegistered(final AliasDefinition aliasDefinition) {
    }
    
    @Override
    public void importProcessed(final ImportDefinition importDefinition) {
    }
}
