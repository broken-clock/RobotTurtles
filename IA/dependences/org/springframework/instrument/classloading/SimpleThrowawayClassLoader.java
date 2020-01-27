// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading;

import org.springframework.core.OverridingClassLoader;

public class SimpleThrowawayClassLoader extends OverridingClassLoader
{
    public SimpleThrowawayClassLoader(final ClassLoader parent) {
        super(parent);
    }
}
