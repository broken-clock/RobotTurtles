// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading.glassfish;

import java.lang.reflect.InvocationTargetException;
import java.lang.instrument.ClassFileTransformer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import java.lang.reflect.Method;
import org.springframework.instrument.classloading.LoadTimeWeaver;

public class GlassFishLoadTimeWeaver implements LoadTimeWeaver
{
    private static final String INSTRUMENTABLE_LOADER_CLASS_NAME = "org.glassfish.api.deployment.InstrumentableClassLoader";
    private final ClassLoader classLoader;
    private final Method addTransformerMethod;
    private final Method copyMethod;
    
    public GlassFishLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }
    
    public GlassFishLoadTimeWeaver(final ClassLoader classLoader) {
        Assert.notNull(classLoader, "ClassLoader must not be null");
        Class<?> instrumentableLoaderClass;
        try {
            instrumentableLoaderClass = classLoader.loadClass("org.glassfish.api.deployment.InstrumentableClassLoader");
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Could not initialize GlassFishLoadTimeWeaver because GlassFish API classes are not available", ex);
        }
        try {
            this.addTransformerMethod = instrumentableLoaderClass.getMethod("addTransformer", ClassFileTransformer.class);
            this.copyMethod = instrumentableLoaderClass.getMethod("copy", (Class<?>[])new Class[0]);
        }
        catch (Exception ex2) {
            throw new IllegalStateException("Could not initialize GlassFishLoadTimeWeaver because GlassFish API classes are not available", ex2);
        }
        ClassLoader clazzLoader = null;
        for (ClassLoader cl = classLoader; cl != null && clazzLoader == null; cl = cl.getParent()) {
            if (instrumentableLoaderClass.isInstance(cl)) {
                clazzLoader = cl;
            }
        }
        if (clazzLoader == null) {
            throw new IllegalArgumentException(classLoader + " and its parents are not suitable ClassLoaders: A [" + instrumentableLoaderClass.getName() + "] implementation is required.");
        }
        this.classLoader = clazzLoader;
    }
    
    @Override
    public void addTransformer(final ClassFileTransformer transformer) {
        try {
            this.addTransformerMethod.invoke(this.classLoader, transformer);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("GlassFish addTransformer method threw exception", ex.getCause());
        }
        catch (Exception ex2) {
            throw new IllegalStateException("Could not invoke GlassFish addTransformer method", ex2);
        }
    }
    
    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader;
    }
    
    @Override
    public ClassLoader getThrowawayClassLoader() {
        try {
            return (ClassLoader)this.copyMethod.invoke(this.classLoader, new Object[0]);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("GlassFish copy method threw exception", ex.getCause());
        }
        catch (Exception ex2) {
            throw new IllegalStateException("Could not invoke GlassFish copy method", ex2);
        }
    }
}
