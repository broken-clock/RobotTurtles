// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.bsh;

import java.io.IOException;
import bsh.EvalError;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.scripting.ScriptFactory;

public class BshScriptFactory implements ScriptFactory, BeanClassLoaderAware
{
    private final String scriptSourceLocator;
    private final Class<?>[] scriptInterfaces;
    private ClassLoader beanClassLoader;
    private Class<?> scriptClass;
    private final Object scriptClassMonitor;
    private boolean wasModifiedForTypeCheck;
    
    public BshScriptFactory(final String scriptSourceLocator) {
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        this.scriptClassMonitor = new Object();
        this.wasModifiedForTypeCheck = false;
        Assert.hasText(scriptSourceLocator, "'scriptSourceLocator' must not be empty");
        this.scriptSourceLocator = scriptSourceLocator;
        this.scriptInterfaces = null;
    }
    
    public BshScriptFactory(final String scriptSourceLocator, final Class<?>... scriptInterfaces) {
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        this.scriptClassMonitor = new Object();
        this.wasModifiedForTypeCheck = false;
        Assert.hasText(scriptSourceLocator, "'scriptSourceLocator' must not be empty");
        this.scriptSourceLocator = scriptSourceLocator;
        this.scriptInterfaces = scriptInterfaces;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    @Override
    public String getScriptSourceLocator() {
        return this.scriptSourceLocator;
    }
    
    @Override
    public Class<?>[] getScriptInterfaces() {
        return this.scriptInterfaces;
    }
    
    @Override
    public boolean requiresConfigInterface() {
        return true;
    }
    
    @Override
    public Object getScriptedObject(final ScriptSource scriptSource, final Class<?>... actualInterfaces) throws IOException, ScriptCompilationException {
        try {
            final Class<?> clazz;
            synchronized (this.scriptClassMonitor) {
                final boolean requiresScriptEvaluation = this.wasModifiedForTypeCheck && this.scriptClass == null;
                this.wasModifiedForTypeCheck = false;
                if (scriptSource.isModified() || requiresScriptEvaluation) {
                    final Object result = BshScriptUtils.evaluateBshScript(scriptSource.getScriptAsString(), actualInterfaces, this.beanClassLoader);
                    if (!(result instanceof Class)) {
                        return result;
                    }
                    this.scriptClass = (Class<?>)result;
                }
                clazz = this.scriptClass;
            }
            if (clazz != null) {
                try {
                    return clazz.newInstance();
                }
                catch (Throwable ex) {
                    throw new ScriptCompilationException(scriptSource, "Could not instantiate script class: " + clazz.getName(), ex);
                }
            }
            return BshScriptUtils.createBshObject(scriptSource.getScriptAsString(), actualInterfaces, this.beanClassLoader);
        }
        catch (EvalError ex2) {
            throw new ScriptCompilationException(scriptSource, (Throwable)ex2);
        }
    }
    
    @Override
    public Class<?> getScriptedObjectType(final ScriptSource scriptSource) throws IOException, ScriptCompilationException {
        try {
            synchronized (this.scriptClassMonitor) {
                if (scriptSource.isModified()) {
                    this.wasModifiedForTypeCheck = true;
                    this.scriptClass = BshScriptUtils.determineBshObjectType(scriptSource.getScriptAsString(), this.beanClassLoader);
                }
                return this.scriptClass;
            }
        }
        catch (EvalError ex) {
            throw new ScriptCompilationException(scriptSource, (Throwable)ex);
        }
    }
    
    @Override
    public boolean requiresScriptedObjectRefresh(final ScriptSource scriptSource) {
        synchronized (this.scriptClassMonitor) {
            return scriptSource.isModified() || this.wasModifiedForTypeCheck;
        }
    }
    
    @Override
    public String toString() {
        return "BshScriptFactory: script source locator [" + this.scriptSourceLocator + "]";
    }
}
