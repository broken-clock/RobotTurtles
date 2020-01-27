// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.io.InputStream;
import java.io.IOException;
import org.springframework.util.FileCopyUtils;

public class OverridingClassLoader extends DecoratingClassLoader
{
    public static final String[] DEFAULT_EXCLUDED_PACKAGES;
    private static final String CLASS_FILE_SUFFIX = ".class";
    
    public OverridingClassLoader(final ClassLoader parent) {
        super(parent);
        for (final String packageName : OverridingClassLoader.DEFAULT_EXCLUDED_PACKAGES) {
            this.excludePackage(packageName);
        }
    }
    
    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        Class<?> result = null;
        if (this.isEligibleForOverriding(name)) {
            result = this.loadClassForOverriding(name);
        }
        if (result != null) {
            if (resolve) {
                this.resolveClass(result);
            }
            return result;
        }
        return super.loadClass(name, resolve);
    }
    
    protected boolean isEligibleForOverriding(final String className) {
        return !this.isExcluded(className);
    }
    
    protected Class<?> loadClassForOverriding(final String name) throws ClassNotFoundException {
        Class<?> result = this.findLoadedClass(name);
        if (result == null) {
            final byte[] bytes = this.loadBytesForClass(name);
            if (bytes != null) {
                result = this.defineClass(name, bytes, 0, bytes.length);
            }
        }
        return result;
    }
    
    protected byte[] loadBytesForClass(final String name) throws ClassNotFoundException {
        final InputStream is = this.openStreamForClass(name);
        if (is == null) {
            return null;
        }
        try {
            final byte[] bytes = FileCopyUtils.copyToByteArray(is);
            return this.transformIfNecessary(name, bytes);
        }
        catch (IOException ex) {
            throw new ClassNotFoundException("Cannot load resource for class [" + name + "]", ex);
        }
    }
    
    protected InputStream openStreamForClass(final String name) {
        final String internalName = name.replace('.', '/') + ".class";
        return this.getParent().getResourceAsStream(internalName);
    }
    
    protected byte[] transformIfNecessary(final String name, final byte[] bytes) {
        return bytes;
    }
    
    static {
        DEFAULT_EXCLUDED_PACKAGES = new String[] { "java.", "javax.", "sun.", "oracle." };
    }
}
