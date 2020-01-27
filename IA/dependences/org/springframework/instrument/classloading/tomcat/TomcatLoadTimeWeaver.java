// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading.tomcat;

import java.lang.reflect.InvocationTargetException;
import java.lang.instrument.ClassFileTransformer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import java.lang.reflect.Method;
import org.springframework.instrument.classloading.LoadTimeWeaver;

public class TomcatLoadTimeWeaver implements LoadTimeWeaver
{
    private static final String INSTRUMENTABLE_LOADER_CLASS_NAME = "org.apache.tomcat.InstrumentableClassLoader";
    private final ClassLoader classLoader;
    private final Method addTransformerMethod;
    private final Method copyMethod;
    
    public TomcatLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }
    
    public TomcatLoadTimeWeaver(final ClassLoader classLoader) {
        Assert.notNull(classLoader, "ClassLoader must not be null");
        this.classLoader = classLoader;
        Class<?> instrumentableLoaderClass;
        try {
            instrumentableLoaderClass = classLoader.loadClass("org.apache.tomcat.InstrumentableClassLoader");
            if (!instrumentableLoaderClass.isInstance(classLoader)) {
                instrumentableLoaderClass = classLoader.getClass();
            }
        }
        catch (ClassNotFoundException ex2) {
            instrumentableLoaderClass = classLoader.getClass();
        }
        try {
            this.addTransformerMethod = instrumentableLoaderClass.getMethod("addTransformer", ClassFileTransformer.class);
            Method copyMethod = ClassUtils.getMethodIfAvailable(instrumentableLoaderClass, "copyWithoutTransformers", (Class<?>[])new Class[0]);
            if (copyMethod == null) {
                copyMethod = instrumentableLoaderClass.getMethod("getThrowawayClassLoader", (Class<?>[])new Class[0]);
            }
            this.copyMethod = copyMethod;
        }
        catch (Exception ex) {
            throw new IllegalStateException("Could not initialize TomcatLoadTimeWeaver because Tomcat API classes are not available", ex);
        }
    }
    
    @Override
    public void addTransformer(final ClassFileTransformer transformer) {
        try {
            this.addTransformerMethod.invoke(this.classLoader, transformer);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("Tomcat addTransformer method threw exception", ex.getCause());
        }
        catch (Exception ex2) {
            throw new IllegalStateException("Could not invoke Tomcat addTransformer method", ex2);
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
            throw new IllegalStateException("Tomcat copy method threw exception", ex.getCause());
        }
        catch (Exception ex2) {
            throw new IllegalStateException("Could not invoke Tomcat copy method", ex2);
        }
    }
}
