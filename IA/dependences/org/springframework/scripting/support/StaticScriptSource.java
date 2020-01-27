// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.support;

import org.springframework.util.Assert;
import org.springframework.scripting.ScriptSource;

public class StaticScriptSource implements ScriptSource
{
    private String script;
    private boolean modified;
    private String className;
    
    public StaticScriptSource(final String script) {
        this.setScript(script);
    }
    
    public StaticScriptSource(final String script, final String className) {
        this.setScript(script);
        this.className = className;
    }
    
    public synchronized void setScript(final String script) {
        Assert.hasText(script, "Script must not be empty");
        this.modified = !script.equals(this.script);
        this.script = script;
    }
    
    @Override
    public synchronized String getScriptAsString() {
        this.modified = false;
        return this.script;
    }
    
    @Override
    public synchronized boolean isModified() {
        return this.modified;
    }
    
    @Override
    public String suggestedClassName() {
        return this.className;
    }
    
    @Override
    public String toString() {
        return "static script" + ((this.className != null) ? (" [" + this.className + "]") : "");
    }
}
