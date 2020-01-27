// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading;

import java.util.Iterator;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.lang.instrument.ClassFileTransformer;
import java.util.List;

public class WeavingTransformer
{
    private final ClassLoader classLoader;
    private final List<ClassFileTransformer> transformers;
    
    public WeavingTransformer(final ClassLoader classLoader) {
        this.transformers = new ArrayList<ClassFileTransformer>();
        if (classLoader == null) {
            throw new IllegalArgumentException("ClassLoader must not be null");
        }
        this.classLoader = classLoader;
    }
    
    public void addTransformer(final ClassFileTransformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer must not be null");
        }
        this.transformers.add(transformer);
    }
    
    public byte[] transformIfNecessary(final String className, final byte[] bytes) {
        final String internalName = className.replace(".", "/");
        return this.transformIfNecessary(className, internalName, bytes, null);
    }
    
    public byte[] transformIfNecessary(final String className, final String internalName, final byte[] bytes, final ProtectionDomain pd) {
        byte[] result = bytes;
        for (final ClassFileTransformer cft : this.transformers) {
            try {
                final byte[] transformed = cft.transform(this.classLoader, internalName, null, pd, result);
                if (transformed == null) {
                    continue;
                }
                result = transformed;
            }
            catch (IllegalClassFormatException ex) {
                throw new IllegalStateException("Class file transformation failed", ex);
            }
        }
        return result;
    }
}
