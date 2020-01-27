// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading.weblogic;

import java.lang.instrument.ClassFileTransformer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.instrument.classloading.LoadTimeWeaver;

public class WebLogicLoadTimeWeaver implements LoadTimeWeaver
{
    private final WebLogicClassLoaderAdapter classLoader;
    
    public WebLogicLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }
    
    public WebLogicLoadTimeWeaver(final ClassLoader classLoader) {
        Assert.notNull(classLoader, "ClassLoader must not be null");
        this.classLoader = new WebLogicClassLoaderAdapter(classLoader);
    }
    
    @Override
    public void addTransformer(final ClassFileTransformer transformer) {
        this.classLoader.addTransformer(transformer);
    }
    
    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader.getClassLoader();
    }
    
    @Override
    public ClassLoader getThrowawayClassLoader() {
        return this.classLoader.getThrowawayClassLoader();
    }
}
