// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting;

import java.util.Map;

public interface ScriptEvaluator
{
    Object evaluate(final ScriptSource p0) throws ScriptCompilationException;
    
    Object evaluate(final ScriptSource p0, final Map<String, Object> p1) throws ScriptCompilationException;
}
