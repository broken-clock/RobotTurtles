// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting;

import java.io.IOException;

public interface ScriptFactory
{
    String getScriptSourceLocator();
    
    Class<?>[] getScriptInterfaces();
    
    boolean requiresConfigInterface();
    
    Object getScriptedObject(final ScriptSource p0, final Class<?>... p1) throws IOException, ScriptCompilationException;
    
    Class<?> getScriptedObjectType(final ScriptSource p0) throws IOException, ScriptCompilationException;
    
    boolean requiresScriptedObjectRefresh(final ScriptSource p0);
}
