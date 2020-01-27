// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.util.ReflectionUtils;
import org.springframework.core.OverridingClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;
import org.springframework.core.SmartClassLoader;
import org.springframework.core.DecoratingClassLoader;

class ContextTypeMatchClassLoader extends DecoratingClassLoader implements SmartClassLoader
{
    private static Method findLoadedClassMethod;
    private final Map<String, byte[]> bytesCache;
    
    public ContextTypeMatchClassLoader(final ClassLoader parent) {
        super(parent);
        this.bytesCache = new HashMap<String, byte[]>();
    }
    
    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        return new ContextOverridingClassLoader(this.getParent()).loadClass(name);
    }
    
    @Override
    public boolean isClassReloadable(final Class<?> clazz) {
        return clazz.getClassLoader() instanceof ContextOverridingClassLoader;
    }
    
    static {
        try {
            ContextTypeMatchClassLoader.findLoadedClassMethod = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Invalid [java.lang.ClassLoader] class: no 'findLoadedClass' method defined!");
        }
    }
    
    private class ContextOverridingClassLoader extends OverridingClassLoader
    {
        public ContextOverridingClassLoader(final ClassLoader parent) {
            super(parent);
        }
        
        @Override
        protected boolean isEligibleForOverriding(final String className) {
            if (this.isExcluded(className) || DecoratingClassLoader.this.isExcluded(className)) {
                return false;
            }
            ReflectionUtils.makeAccessible(ContextTypeMatchClassLoader.findLoadedClassMethod);
            for (ClassLoader parent = this.getParent(); parent != null; parent = parent.getParent()) {
                if (ReflectionUtils.invokeMethod(ContextTypeMatchClassLoader.findLoadedClassMethod, parent, className) != null) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        protected Class<?> loadClassForOverriding(final String name) throws ClassNotFoundException {
            byte[] bytes = ContextTypeMatchClassLoader.this.bytesCache.get(name);
            if (bytes == null) {
                bytes = this.loadBytesForClass(name);
                if (bytes == null) {
                    return null;
                }
                ContextTypeMatchClassLoader.this.bytesCache.put(name, bytes);
            }
            return this.defineClass(name, bytes, 0, bytes.length);
        }
    }
}
