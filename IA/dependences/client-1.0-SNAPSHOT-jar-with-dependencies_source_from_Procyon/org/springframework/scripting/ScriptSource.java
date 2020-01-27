// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting;

import java.io.IOException;

public interface ScriptSource
{
    String getScriptAsString() throws IOException;
    
    boolean isModified();
    
    String suggestedClassName();
}
