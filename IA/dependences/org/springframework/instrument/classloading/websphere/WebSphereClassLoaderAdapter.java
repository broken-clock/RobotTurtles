// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading.websphere;

import java.util.List;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.instrument.ClassFileTransformer;
import org.springframework.util.Assert;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class WebSphereClassLoaderAdapter
{
    private static final String COMPOUND_CLASS_LOADER_NAME = "com.ibm.ws.classloader.CompoundClassLoader";
    private static final String CLASS_PRE_PROCESSOR_NAME = "com.ibm.websphere.classloader.ClassLoaderInstancePreDefinePlugin";
    private static final String PLUGINS_FIELD = "preDefinePlugins";
    private ClassLoader classLoader;
    private Class<?> wsPreProcessorClass;
    private Method addPreDefinePlugin;
    private Constructor<? extends ClassLoader> cloneConstructor;
    private Field transformerList;
    
    public WebSphereClassLoaderAdapter(final ClassLoader classLoader) {
        Class<?> wsCompoundClassLoaderClass = null;
        try {
            wsCompoundClassLoaderClass = classLoader.loadClass("com.ibm.ws.classloader.CompoundClassLoader");
            (this.cloneConstructor = classLoader.getClass().getDeclaredConstructor(wsCompoundClassLoaderClass)).setAccessible(true);
            this.wsPreProcessorClass = classLoader.loadClass("com.ibm.websphere.classloader.ClassLoaderInstancePreDefinePlugin");
            this.addPreDefinePlugin = classLoader.getClass().getMethod("addPreDefinePlugin", this.wsPreProcessorClass);
            (this.transformerList = wsCompoundClassLoaderClass.getDeclaredField("preDefinePlugins")).setAccessible(true);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Could not initialize WebSphere LoadTimeWeaver because WebSphere 7 API classes are not available", ex);
        }
        Assert.isInstanceOf(wsCompoundClassLoaderClass, classLoader, "ClassLoader must be instance of [com.ibm.ws.classloader.CompoundClassLoader]");
        this.classLoader = classLoader;
    }
    
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    public void addTransformer(final ClassFileTransformer transformer) {
        Assert.notNull(transformer, "ClassFileTransformer must not be null");
        try {
            final InvocationHandler adapter = new WebSphereClassPreDefinePlugin(transformer);
            final Object adapterInstance = Proxy.newProxyInstance(this.wsPreProcessorClass.getClassLoader(), new Class[] { this.wsPreProcessorClass }, adapter);
            this.addPreDefinePlugin.invoke(this.classLoader, adapterInstance);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("WebSphere addPreDefinePlugin method threw exception", ex.getCause());
        }
        catch (Exception ex2) {
            throw new IllegalStateException("Could not invoke WebSphere addPreDefinePlugin method", ex2);
        }
    }
    
    public ClassLoader getThrowawayClassLoader() {
        try {
            final ClassLoader loader = (ClassLoader)this.cloneConstructor.newInstance(this.getClassLoader());
            final List<?> list = (List<?>)this.transformerList.get(loader);
            list.clear();
            return loader;
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("WebSphere CompoundClassLoader constructor failed", ex.getCause());
        }
        catch (Exception ex2) {
            throw new IllegalStateException("Could not construct WebSphere CompoundClassLoader", ex2);
        }
    }
}
