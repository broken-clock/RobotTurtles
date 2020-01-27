// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class SimpleLoadTimeWeaver implements LoadTimeWeaver
{
    private final SimpleInstrumentableClassLoader classLoader;
    
    public SimpleLoadTimeWeaver() {
        this.classLoader = new SimpleInstrumentableClassLoader(ClassUtils.getDefaultClassLoader());
    }
    
    public SimpleLoadTimeWeaver(final SimpleInstrumentableClassLoader classLoader) {
        Assert.notNull(classLoader, "ClassLoader must not be null");
        this.classLoader = classLoader;
    }
    
    @Override
    public void addTransformer(final ClassFileTransformer transformer) {
        this.classLoader.addTransformer(transformer);
    }
    
    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader;
    }
    
    @Override
    public ClassLoader getThrowawayClassLoader() {
        return new SimpleThrowawayClassLoader(this.getInstrumentableClassLoader());
    }
}
