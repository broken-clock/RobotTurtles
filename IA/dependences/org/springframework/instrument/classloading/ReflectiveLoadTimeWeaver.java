// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading;

import org.apache.commons.logging.LogFactory;
import org.springframework.util.ReflectionUtils;
import java.lang.instrument.ClassFileTransformer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;

public class ReflectiveLoadTimeWeaver implements LoadTimeWeaver
{
    private static final String ADD_TRANSFORMER_METHOD_NAME = "addTransformer";
    private static final String GET_THROWAWAY_CLASS_LOADER_METHOD_NAME = "getThrowawayClassLoader";
    private static final Log logger;
    private final ClassLoader classLoader;
    private final Method addTransformerMethod;
    private final Method getThrowawayClassLoaderMethod;
    
    public ReflectiveLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }
    
    public ReflectiveLoadTimeWeaver(final ClassLoader classLoader) {
        Assert.notNull(classLoader, "ClassLoader must not be null");
        this.classLoader = classLoader;
        this.addTransformerMethod = ClassUtils.getMethodIfAvailable(this.classLoader.getClass(), "addTransformer", ClassFileTransformer.class);
        if (this.addTransformerMethod == null) {
            throw new IllegalStateException("ClassLoader [" + classLoader.getClass().getName() + "] does NOT provide an " + "'addTransformer(ClassFileTransformer)' method.");
        }
        this.getThrowawayClassLoaderMethod = ClassUtils.getMethodIfAvailable(this.classLoader.getClass(), "getThrowawayClassLoader", (Class<?>[])new Class[0]);
        if (this.getThrowawayClassLoaderMethod == null && ReflectiveLoadTimeWeaver.logger.isInfoEnabled()) {
            ReflectiveLoadTimeWeaver.logger.info("The ClassLoader [" + classLoader.getClass().getName() + "] does NOT provide a " + "'getThrowawayClassLoader()' method; SimpleThrowawayClassLoader will be used instead.");
        }
    }
    
    @Override
    public void addTransformer(final ClassFileTransformer transformer) {
        Assert.notNull(transformer, "Transformer must not be null");
        ReflectionUtils.invokeMethod(this.addTransformerMethod, this.classLoader, transformer);
    }
    
    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader;
    }
    
    @Override
    public ClassLoader getThrowawayClassLoader() {
        if (this.getThrowawayClassLoaderMethod != null) {
            return (ClassLoader)ReflectionUtils.invokeMethod(this.getThrowawayClassLoaderMethod, this.classLoader);
        }
        return new SimpleThrowawayClassLoader(this.classLoader);
    }
    
    static {
        logger = LogFactory.getLog(ReflectiveLoadTimeWeaver.class);
    }
}
