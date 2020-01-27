// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading.jboss;

import org.springframework.instrument.classloading.SimpleThrowawayClassLoader;
import java.lang.instrument.ClassFileTransformer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.instrument.classloading.LoadTimeWeaver;

public class JBossLoadTimeWeaver implements LoadTimeWeaver
{
    private final JBossClassLoaderAdapter adapter;
    
    public JBossLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }
    
    public JBossLoadTimeWeaver(final ClassLoader classLoader) {
        Assert.notNull(classLoader, "ClassLoader must not be null");
        if (classLoader.getClass().getName().startsWith("org.jboss.modules")) {
            this.adapter = new JBossModulesAdapter(classLoader);
        }
        else {
            this.adapter = new JBossMCAdapter(classLoader);
        }
    }
    
    @Override
    public void addTransformer(final ClassFileTransformer transformer) {
        this.adapter.addTransformer(transformer);
    }
    
    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.adapter.getInstrumentableClassLoader();
    }
    
    @Override
    public ClassLoader getThrowawayClassLoader() {
        return new SimpleThrowawayClassLoader(this.getInstrumentableClassLoader());
    }
}
