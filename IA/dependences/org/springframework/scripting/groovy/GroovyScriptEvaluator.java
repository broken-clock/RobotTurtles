// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.groovy;

import org.codehaus.groovy.control.CompilationFailedException;
import java.io.IOException;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.support.ResourceScriptSource;
import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import java.util.Map;
import org.springframework.scripting.ScriptSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.scripting.ScriptEvaluator;

public class GroovyScriptEvaluator implements ScriptEvaluator, BeanClassLoaderAware
{
    private ClassLoader classLoader;
    
    public GroovyScriptEvaluator() {
    }
    
    public GroovyScriptEvaluator(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    @Override
    public Object evaluate(final ScriptSource script) {
        return this.evaluate(script, null);
    }
    
    @Override
    public Object evaluate(final ScriptSource script, final Map<String, Object> arguments) {
        final GroovyShell groovyShell = new GroovyShell(this.classLoader, new Binding((Map)arguments));
        try {
            final String filename = (script instanceof ResourceScriptSource) ? ((ResourceScriptSource)script).getResource().getFilename() : null;
            if (filename != null) {
                return groovyShell.evaluate(script.getScriptAsString(), filename);
            }
            return groovyShell.evaluate(script.getScriptAsString());
        }
        catch (IOException ex) {
            throw new ScriptCompilationException(script, "Cannot access script", ex);
        }
        catch (CompilationFailedException ex2) {
            throw new ScriptCompilationException(script, "Evaluation failure", (Throwable)ex2);
        }
    }
}
