// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;
import org.springframework.core.OverridingClassLoader;

public class SimpleInstrumentableClassLoader extends OverridingClassLoader
{
    private final WeavingTransformer weavingTransformer;
    
    public SimpleInstrumentableClassLoader(final ClassLoader parent) {
        super(parent);
        this.weavingTransformer = new WeavingTransformer(parent);
    }
    
    public void addTransformer(final ClassFileTransformer transformer) {
        this.weavingTransformer.addTransformer(transformer);
    }
    
    @Override
    protected byte[] transformIfNecessary(final String name, final byte[] bytes) {
        return this.weavingTransformer.transformIfNecessary(name, bytes);
    }
}
