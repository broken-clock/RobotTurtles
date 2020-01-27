// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.jruby;

import java.io.IOException;
import org.jruby.RubyException;
import org.jruby.exceptions.JumpException;
import org.jruby.exceptions.RaiseException;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.scripting.ScriptFactory;

public class JRubyScriptFactory implements ScriptFactory, BeanClassLoaderAware
{
    private final String scriptSourceLocator;
    private final Class<?>[] scriptInterfaces;
    private ClassLoader beanClassLoader;
    
    public JRubyScriptFactory(final String scriptSourceLocator, final Class<?>... scriptInterfaces) {
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        Assert.hasText(scriptSourceLocator, "'scriptSourceLocator' must not be empty");
        Assert.notEmpty(scriptInterfaces, "'scriptInterfaces' must not be empty");
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
            return JRubyScriptUtils.createJRubyObject(scriptSource.getScriptAsString(), actualInterfaces, this.beanClassLoader);
        }
        catch (RaiseException ex) {
            final RubyException rubyEx = ex.getException();
            final String msg = (rubyEx != null && rubyEx.message != null) ? rubyEx.message.toString() : "Unexpected JRuby error";
            throw new ScriptCompilationException(scriptSource, msg, (Throwable)ex);
        }
        catch (JumpException ex2) {
            throw new ScriptCompilationException(scriptSource, (Throwable)ex2);
        }
    }
    
    @Override
    public Class<?> getScriptedObjectType(final ScriptSource scriptSource) throws IOException, ScriptCompilationException {
        return null;
    }
    
    @Override
    public boolean requiresScriptedObjectRefresh(final ScriptSource scriptSource) {
        return scriptSource.isModified();
    }
    
    @Override
    public String toString() {
        return "JRubyScriptFactory: script source locator [" + this.scriptSourceLocator + "]";
    }
}
