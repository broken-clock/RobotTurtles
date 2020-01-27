// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading.jboss;

import java.lang.instrument.ClassFileTransformer;

interface JBossClassLoaderAdapter
{
    void addTransformer(final ClassFileTransformer p0);
    
    ClassLoader getInstrumentableClassLoader();
}
