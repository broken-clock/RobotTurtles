// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting;

import org.springframework.core.NestedRuntimeException;

public class ScriptCompilationException extends NestedRuntimeException
{
    private ScriptSource scriptSource;
    
    public ScriptCompilationException(final String msg) {
        super(msg);
    }
    
    public ScriptCompilationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public ScriptCompilationException(final ScriptSource scriptSource, final Throwable cause) {
        super("Could not compile script", cause);
        this.scriptSource = scriptSource;
    }
    
    public ScriptCompilationException(final ScriptSource scriptSource, final String msg, final Throwable cause) {
        super("Could not compile script [" + scriptSource + "]: " + msg, cause);
        this.scriptSource = scriptSource;
    }
    
    public ScriptSource getScriptSource() {
        return this.scriptSource;
    }
}
