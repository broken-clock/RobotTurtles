// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.bsh;

import java.util.Iterator;
import bsh.EvalError;
import java.io.IOException;
import org.springframework.scripting.ScriptCompilationException;
import java.io.Reader;
import java.io.StringReader;
import bsh.Interpreter;
import java.util.Map;
import org.springframework.scripting.ScriptSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.scripting.ScriptEvaluator;

public class BshScriptEvaluator implements ScriptEvaluator, BeanClassLoaderAware
{
    private ClassLoader classLoader;
    
    public BshScriptEvaluator() {
    }
    
    public BshScriptEvaluator(final ClassLoader classLoader) {
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
        try {
            final Interpreter interpreter = new Interpreter();
            interpreter.setClassLoader(this.classLoader);
            if (arguments != null) {
                for (final Map.Entry<String, Object> entry : arguments.entrySet()) {
                    interpreter.set((String)entry.getKey(), entry.getValue());
                }
            }
            return interpreter.eval((Reader)new StringReader(script.getScriptAsString()));
        }
        catch (IOException ex) {
            throw new ScriptCompilationException(script, "Cannot access script", ex);
        }
        catch (EvalError ex2) {
            throw new ScriptCompilationException(script, "Evaluation failure", (Throwable)ex2);
        }
    }
}
