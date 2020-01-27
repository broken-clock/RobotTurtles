// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;

public interface LoadTimeWeaver
{
    void addTransformer(final ClassFileTransformer p0);
    
    ClassLoader getInstrumentableClassLoader();
    
    ClassLoader getThrowawayClassLoader();
}
