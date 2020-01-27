// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading.jboss;

import java.lang.reflect.Field;
import java.lang.instrument.ClassFileTransformer;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;

class JBossModulesAdapter implements JBossClassLoaderAdapter
{
    private static final String DELEGATING_TRANSFORMER_CLASS_NAME = "org.jboss.as.server.deployment.module.DelegatingClassFileTransformer";
    private final ClassLoader classLoader;
    private final Method addTransformer;
    private final Object delegatingTransformer;
    
    public JBossModulesAdapter(final ClassLoader loader) {
        this.classLoader = loader;
        try {
            final Field transformer = ReflectionUtils.findField(loader.getClass(), "transformer");
            transformer.setAccessible(true);
            this.delegatingTransformer = transformer.get(loader);
            if (!this.delegatingTransformer.getClass().getName().equals("org.jboss.as.server.deployment.module.DelegatingClassFileTransformer")) {
                throw new IllegalStateException("Transformer not of the expected type DelegatingClassFileTransformer: " + this.delegatingTransformer.getClass().getName());
            }
            (this.addTransformer = ReflectionUtils.findMethod(this.delegatingTransformer.getClass(), "addTransformer", ClassFileTransformer.class)).setAccessible(true);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Could not initialize JBoss 7 LoadTimeWeaver", ex);
        }
    }
    
    @Override
    public void addTransformer(final ClassFileTransformer transformer) {
        try {
            this.addTransformer.invoke(this.delegatingTransformer, transformer);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Could not add transformer on JBoss 7 ClassLoader " + this.classLoader, ex);
        }
    }
    
    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader;
    }
}
