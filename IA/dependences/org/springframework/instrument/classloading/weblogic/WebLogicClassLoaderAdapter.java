// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading.weblogic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.instrument.ClassFileTransformer;
import org.springframework.util.Assert;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class WebLogicClassLoaderAdapter
{
    private static final String GENERIC_CLASS_LOADER_NAME = "weblogic.utils.classloaders.GenericClassLoader";
    private static final String CLASS_PRE_PROCESSOR_NAME = "weblogic.utils.classloaders.ClassPreProcessor";
    private final ClassLoader classLoader;
    private final Class<?> wlPreProcessorClass;
    private final Method addPreProcessorMethod;
    private final Method getClassFinderMethod;
    private final Method getParentMethod;
    private final Constructor<?> wlGenericClassLoaderConstructor;
    
    public WebLogicClassLoaderAdapter(final ClassLoader classLoader) {
        Class<?> wlGenericClassLoaderClass = null;
        try {
            wlGenericClassLoaderClass = classLoader.loadClass("weblogic.utils.classloaders.GenericClassLoader");
            this.wlPreProcessorClass = classLoader.loadClass("weblogic.utils.classloaders.ClassPreProcessor");
            this.addPreProcessorMethod = classLoader.getClass().getMethod("addInstanceClassPreProcessor", this.wlPreProcessorClass);
            this.getClassFinderMethod = classLoader.getClass().getMethod("getClassFinder", (Class<?>[])new Class[0]);
            this.getParentMethod = classLoader.getClass().getMethod("getParent", (Class<?>[])new Class[0]);
            this.wlGenericClassLoaderConstructor = wlGenericClassLoaderClass.getConstructor(this.getClassFinderMethod.getReturnType(), ClassLoader.class);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Could not initialize WebLogic LoadTimeWeaver because WebLogic 10 API classes are not available", ex);
        }
        Assert.isInstanceOf(wlGenericClassLoaderClass, classLoader, "ClassLoader must be instance of [" + wlGenericClassLoaderClass.getName() + "]");
        this.classLoader = classLoader;
    }
    
    public void addTransformer(final ClassFileTransformer transformer) {
        Assert.notNull(transformer, "ClassFileTransformer must not be null");
        try {
            final InvocationHandler adapter = new WebLogicClassPreProcessorAdapter(transformer, this.classLoader);
            final Object adapterInstance = Proxy.newProxyInstance(this.wlPreProcessorClass.getClassLoader(), new Class[] { this.wlPreProcessorClass }, adapter);
            this.addPreProcessorMethod.invoke(this.classLoader, adapterInstance);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("WebLogic addInstanceClassPreProcessor method threw exception", ex.getCause());
        }
        catch (Exception ex2) {
            throw new IllegalStateException("Could not invoke WebLogic addInstanceClassPreProcessor method", ex2);
        }
    }
    
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    public ClassLoader getThrowawayClassLoader() {
        try {
            final Object classFinder = this.getClassFinderMethod.invoke(this.classLoader, new Object[0]);
            final Object parent = this.getParentMethod.invoke(this.classLoader, new Object[0]);
            return (ClassLoader)this.wlGenericClassLoaderConstructor.newInstance(classFinder, parent);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("WebLogic GenericClassLoader constructor failed", ex.getCause());
        }
        catch (Exception ex2) {
            throw new IllegalStateException("Could not construct WebLogic GenericClassLoader", ex2);
        }
    }
}
