// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.support;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import org.springframework.scripting.ScriptCompilationException;
import javax.script.SimpleBindings;
import org.springframework.util.CollectionUtils;
import java.util.Map;
import org.springframework.scripting.ScriptSource;
import javax.script.ScriptEngineManager;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.scripting.ScriptEvaluator;

public class StandardScriptEvaluator implements ScriptEvaluator, BeanClassLoaderAware
{
    private volatile ScriptEngineManager scriptEngineManager;
    private String language;
    
    public StandardScriptEvaluator() {
    }
    
    public StandardScriptEvaluator(final ClassLoader classLoader) {
        this.scriptEngineManager = new ScriptEngineManager(classLoader);
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.scriptEngineManager = new ScriptEngineManager(classLoader);
    }
    
    public void setLanguage(final String language) {
        this.language = language;
    }
    
    @Override
    public Object evaluate(final ScriptSource script) {
        return this.evaluate(script, null);
    }
    
    @Override
    public Object evaluate(final ScriptSource script, final Map<String, Object> arguments) {
        final ScriptEngine engine = this.getScriptEngine(script);
        final Bindings bindings = CollectionUtils.isEmpty(arguments) ? null : new SimpleBindings(arguments);
        try {
            return (bindings != null) ? engine.eval(script.getScriptAsString(), bindings) : engine.eval(script.getScriptAsString());
        }
        catch (IOException ex) {
            throw new ScriptCompilationException(script, "Cannot access script", ex);
        }
        catch (ScriptException ex2) {
            throw new ScriptCompilationException(script, "Evaluation failure", ex2);
        }
    }
    
    protected ScriptEngine getScriptEngine(final ScriptSource script) {
        if (this.scriptEngineManager == null) {
            this.scriptEngineManager = new ScriptEngineManager();
        }
        if (StringUtils.hasText(this.language)) {
            final ScriptEngine engine = this.scriptEngineManager.getEngineByName(this.language);
            if (engine == null) {
                throw new IllegalStateException("No matching engine found for language '" + this.language + "'");
            }
            return engine;
        }
        else {
            if (!(script instanceof ResourceScriptSource)) {
                throw new IllegalStateException("No script language defined, and no resource associated with script: " + script);
            }
            final Resource resource = ((ResourceScriptSource)script).getResource();
            final String extension = StringUtils.getFilenameExtension(resource.getFilename());
            if (extension == null) {
                throw new IllegalStateException("No script language defined, and no file extension defined for resource: " + resource);
            }
            final ScriptEngine engine2 = this.scriptEngineManager.getEngineByExtension(extension);
            if (engine2 == null) {
                throw new IllegalStateException("No matching engine found for file extension '" + extension + "'");
            }
            return engine2;
        }
    }
}
