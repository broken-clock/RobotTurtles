// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading.jboss;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.instrument.ClassFileTransformer;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;

class JBossMCAdapter implements JBossClassLoaderAdapter
{
    private static final String LOADER_NAME = "org.jboss.classloader.spi.base.BaseClassLoader";
    private static final String TRANSLATOR_NAME = "org.jboss.util.loading.Translator";
    private final ClassLoader classLoader;
    private final Object target;
    private final Class<?> translatorClass;
    private final Method addTranslator;
    
    public JBossMCAdapter(ClassLoader classLoader) {
        try {
            final Class<?> clazzLoaderType = classLoader.loadClass("org.jboss.classloader.spi.base.BaseClassLoader");
            ClassLoader clazzLoader = null;
            for (ClassLoader cl = classLoader; cl != null && clazzLoader == null; cl = cl.getParent()) {
                if (clazzLoaderType.isInstance(cl)) {
                    clazzLoader = cl;
                }
            }
            if (clazzLoader == null) {
                throw new IllegalArgumentException(classLoader + " and its parents are not suitable ClassLoaders: " + "A [" + "org.jboss.classloader.spi.base.BaseClassLoader" + "] implementation is required.");
            }
            this.classLoader = clazzLoader;
            classLoader = clazzLoader.getClass().getClassLoader();
            final Method method = clazzLoaderType.getDeclaredMethod("getPolicy", (Class<?>[])new Class[0]);
            ReflectionUtils.makeAccessible(method);
            this.target = method.invoke(this.classLoader, new Object[0]);
            this.translatorClass = classLoader.loadClass("org.jboss.util.loading.Translator");
            this.addTranslator = this.target.getClass().getMethod("addTranslator", this.translatorClass);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Could not initialize JBoss LoadTimeWeaver because the JBoss 6 API classes are not available", ex);
        }
    }
    
    @Override
    public void addTransformer(final ClassFileTransformer transformer) {
        final InvocationHandler adapter = new JBossMCTranslatorAdapter(transformer);
        final Object adapterInstance = Proxy.newProxyInstance(this.translatorClass.getClassLoader(), new Class[] { this.translatorClass }, adapter);
        try {
            this.addTranslator.invoke(this.target, adapterInstance);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Could not add transformer on JBoss 6 ClassLoader " + this.classLoader, ex);
        }
    }
    
    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader;
    }
}
