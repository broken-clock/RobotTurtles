// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading;

import java.util.Enumeration;
import java.util.Iterator;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import java.util.Collection;
import org.springframework.util.Assert;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.lang.instrument.ClassFileTransformer;
import java.util.List;
import org.springframework.core.DecoratingClassLoader;

public class ShadowingClassLoader extends DecoratingClassLoader
{
    public static final String[] DEFAULT_EXCLUDED_PACKAGES;
    private final ClassLoader enclosingClassLoader;
    private final List<ClassFileTransformer> classFileTransformers;
    private final Map<String, Class<?>> classCache;
    
    public ShadowingClassLoader(final ClassLoader enclosingClassLoader) {
        this.classFileTransformers = new LinkedList<ClassFileTransformer>();
        this.classCache = new HashMap<String, Class<?>>();
        Assert.notNull(enclosingClassLoader, "Enclosing ClassLoader must not be null");
        this.enclosingClassLoader = enclosingClassLoader;
        for (final String excludedPackage : ShadowingClassLoader.DEFAULT_EXCLUDED_PACKAGES) {
            this.excludePackage(excludedPackage);
        }
    }
    
    public void addTransformer(final ClassFileTransformer transformer) {
        Assert.notNull(transformer, "Transformer must not be null");
        this.classFileTransformers.add(transformer);
    }
    
    public void copyTransformers(final ShadowingClassLoader other) {
        Assert.notNull(other, "Other ClassLoader must not be null");
        this.classFileTransformers.addAll(other.classFileTransformers);
    }
    
    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        if (!this.shouldShadow(name)) {
            return this.enclosingClassLoader.loadClass(name);
        }
        final Class<?> cls = this.classCache.get(name);
        if (cls != null) {
            return cls;
        }
        return this.doLoadClass(name);
    }
    
    private boolean shouldShadow(final String className) {
        return !className.equals(this.getClass().getName()) && !className.endsWith("ShadowingClassLoader") && this.isEligibleForShadowing(className);
    }
    
    protected boolean isEligibleForShadowing(final String className) {
        return !this.isExcluded(className);
    }
    
    private Class<?> doLoadClass(final String name) throws ClassNotFoundException {
        final String internalName = StringUtils.replace(name, ".", "/") + ".class";
        final InputStream is = this.enclosingClassLoader.getResourceAsStream(internalName);
        if (is == null) {
            throw new ClassNotFoundException(name);
        }
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(is);
            bytes = this.applyTransformers(name, bytes);
            final Class<?> cls = this.defineClass(name, bytes, 0, bytes.length);
            if (cls.getPackage() == null) {
                final int packageSeparator = name.lastIndexOf(46);
                if (packageSeparator != -1) {
                    final String packageName = name.substring(0, packageSeparator);
                    this.definePackage(packageName, null, null, null, null, null, null, null);
                }
            }
            this.classCache.put(name, cls);
            return cls;
        }
        catch (IOException ex) {
            throw new ClassNotFoundException("Cannot load resource for class [" + name + "]", ex);
        }
    }
    
    private byte[] applyTransformers(final String name, byte[] bytes) {
        final String internalName = StringUtils.replace(name, ".", "/");
        try {
            for (final ClassFileTransformer transformer : this.classFileTransformers) {
                final byte[] transformed = transformer.transform(this, internalName, null, null, bytes);
                bytes = ((transformed != null) ? transformed : bytes);
            }
            return bytes;
        }
        catch (IllegalClassFormatException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    @Override
    public URL getResource(final String name) {
        return this.enclosingClassLoader.getResource(name);
    }
    
    @Override
    public InputStream getResourceAsStream(final String name) {
        return this.enclosingClassLoader.getResourceAsStream(name);
    }
    
    @Override
    public Enumeration<URL> getResources(final String name) throws IOException {
        return this.enclosingClassLoader.getResources(name);
    }
    
    static {
        DEFAULT_EXCLUDED_PACKAGES = new String[] { "java.", "javax.", "sun.", "oracle.", "com.sun.", "com.ibm.", "COM.ibm.", "org.w3c.", "org.xml.", "org.dom4j.", "org.eclipse", "org.aspectj.", "net.sf.cglib", "org.springframework.cglib", "org.apache.xerces.", "org.apache.commons.logging." };
    }
}
