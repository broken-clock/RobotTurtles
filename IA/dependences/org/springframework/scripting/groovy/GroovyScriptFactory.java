// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.groovy;

import groovy.lang.GroovyObject;
import java.io.IOException;
import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.scripting.ScriptCompilationException;
import groovy.lang.Script;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.ClassUtils;
import org.springframework.beans.BeansException;
import groovy.lang.MetaClass;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;
import groovy.lang.GroovyClassLoader;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.scripting.ScriptFactory;

public class GroovyScriptFactory implements ScriptFactory, BeanFactoryAware, BeanClassLoaderAware
{
    private final String scriptSourceLocator;
    private final GroovyObjectCustomizer groovyObjectCustomizer;
    private GroovyClassLoader groovyClassLoader;
    private Class<?> scriptClass;
    private Class<?> scriptResultClass;
    private CachedResultHolder cachedResult;
    private final Object scriptClassMonitor;
    private boolean wasModifiedForTypeCheck;
    
    public GroovyScriptFactory(final String scriptSourceLocator) {
        this(scriptSourceLocator, null);
    }
    
    public GroovyScriptFactory(final String scriptSourceLocator, final GroovyObjectCustomizer groovyObjectCustomizer) {
        this.scriptClassMonitor = new Object();
        this.wasModifiedForTypeCheck = false;
        Assert.hasText(scriptSourceLocator, "'scriptSourceLocator' must not be empty");
        this.scriptSourceLocator = scriptSourceLocator;
        this.groovyObjectCustomizer = groovyObjectCustomizer;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ((ConfigurableListableBeanFactory)beanFactory).ignoreDependencyType(MetaClass.class);
        }
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.groovyClassLoader = new GroovyClassLoader(classLoader);
    }
    
    public GroovyClassLoader getGroovyClassLoader() {
        synchronized (this.scriptClassMonitor) {
            if (this.groovyClassLoader == null) {
                this.groovyClassLoader = new GroovyClassLoader(ClassUtils.getDefaultClassLoader());
            }
            return this.groovyClassLoader;
        }
    }
    
    @Override
    public String getScriptSourceLocator() {
        return this.scriptSourceLocator;
    }
    
    @Override
    public Class<?>[] getScriptInterfaces() {
        return null;
    }
    
    @Override
    public boolean requiresConfigInterface() {
        return false;
    }
    
    @Override
    public Object getScriptedObject(final ScriptSource scriptSource, final Class<?>... actualInterfaces) throws IOException, ScriptCompilationException {
        try {
            final Class<?> scriptClassToExecute;
            synchronized (this.scriptClassMonitor) {
                this.wasModifiedForTypeCheck = false;
                if (this.cachedResult != null) {
                    final Object result = this.cachedResult.object;
                    this.cachedResult = null;
                    return result;
                }
                if (this.scriptClass == null || scriptSource.isModified()) {
                    this.scriptClass = (Class<?>)this.getGroovyClassLoader().parseClass(scriptSource.getScriptAsString(), scriptSource.suggestedClassName());
                    if (Script.class.isAssignableFrom(this.scriptClass)) {
                        final Object result = this.executeScript(scriptSource, this.scriptClass);
                        this.scriptResultClass = ((result != null) ? result.getClass() : null);
                        return result;
                    }
                    this.scriptResultClass = this.scriptClass;
                }
                scriptClassToExecute = this.scriptClass;
            }
            return this.executeScript(scriptSource, scriptClassToExecute);
        }
        catch (CompilationFailedException ex) {
            throw new ScriptCompilationException(scriptSource, (Throwable)ex);
        }
    }
    
    @Override
    public Class<?> getScriptedObjectType(final ScriptSource scriptSource) throws IOException, ScriptCompilationException {
        try {
            synchronized (this.scriptClassMonitor) {
                if (this.scriptClass == null || scriptSource.isModified()) {
                    this.wasModifiedForTypeCheck = true;
                    this.scriptClass = (Class<?>)this.getGroovyClassLoader().parseClass(scriptSource.getScriptAsString(), scriptSource.suggestedClassName());
                    if (Script.class.isAssignableFrom(this.scriptClass)) {
                        final Object result = this.executeScript(scriptSource, this.scriptClass);
                        this.scriptResultClass = ((result != null) ? result.getClass() : null);
                        this.cachedResult = new CachedResultHolder(result);
                    }
                    else {
                        this.scriptResultClass = this.scriptClass;
                    }
                }
                return this.scriptResultClass;
            }
        }
        catch (CompilationFailedException ex) {
            throw new ScriptCompilationException(scriptSource, (Throwable)ex);
        }
    }
    
    @Override
    public boolean requiresScriptedObjectRefresh(final ScriptSource scriptSource) {
        synchronized (this.scriptClassMonitor) {
            return scriptSource.isModified() || this.wasModifiedForTypeCheck;
        }
    }
    
    protected Object executeScript(final ScriptSource scriptSource, final Class<?> scriptClass) throws ScriptCompilationException {
        try {
            final GroovyObject goo = (GroovyObject)scriptClass.newInstance();
            if (this.groovyObjectCustomizer != null) {
                this.groovyObjectCustomizer.customize(goo);
            }
            if (goo instanceof Script) {
                return ((Script)goo).run();
            }
            return goo;
        }
        catch (InstantiationException ex) {
            throw new ScriptCompilationException(scriptSource, "Could not instantiate Groovy script class: " + scriptClass.getName(), ex);
        }
        catch (IllegalAccessException ex2) {
            throw new ScriptCompilationException(scriptSource, "Could not access Groovy script constructor: " + scriptClass.getName(), ex2);
        }
    }
    
    @Override
    public String toString() {
        return "GroovyScriptFactory: script source locator [" + this.scriptSourceLocator + "]";
    }
    
    private static class CachedResultHolder
    {
        public final Object object;
        
        public CachedResultHolder(final Object object) {
            this.object = object;
        }
    }
}
