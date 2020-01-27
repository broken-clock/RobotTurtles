// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.lang.reflect.Proxy;
import java.io.NotSerializableException;
import org.springframework.util.ClassUtils;
import java.io.ObjectStreamClass;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ConfigurableObjectInputStream extends ObjectInputStream
{
    private final ClassLoader classLoader;
    private final boolean acceptProxyClasses;
    
    public ConfigurableObjectInputStream(final InputStream in, final ClassLoader classLoader) throws IOException {
        this(in, classLoader, true);
    }
    
    public ConfigurableObjectInputStream(final InputStream in, final ClassLoader classLoader, final boolean acceptProxyClasses) throws IOException {
        super(in);
        this.classLoader = classLoader;
        this.acceptProxyClasses = acceptProxyClasses;
    }
    
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
        try {
            if (this.classLoader != null) {
                return ClassUtils.forName(classDesc.getName(), this.classLoader);
            }
            return super.resolveClass(classDesc);
        }
        catch (ClassNotFoundException ex) {
            return this.resolveFallbackIfPossible(classDesc.getName(), ex);
        }
    }
    
    @Override
    protected Class<?> resolveProxyClass(final String[] interfaces) throws IOException, ClassNotFoundException {
        if (!this.acceptProxyClasses) {
            throw new NotSerializableException("Not allowed to accept serialized proxy classes");
        }
        if (this.classLoader != null) {
            final Class<?>[] resolvedInterfaces = (Class<?>[])new Class[interfaces.length];
            for (int i = 0; i < interfaces.length; ++i) {
                try {
                    resolvedInterfaces[i] = ClassUtils.forName(interfaces[i], this.classLoader);
                }
                catch (ClassNotFoundException ex) {
                    resolvedInterfaces[i] = this.resolveFallbackIfPossible(interfaces[i], ex);
                }
            }
            try {
                return Proxy.getProxyClass(this.classLoader, resolvedInterfaces);
            }
            catch (IllegalArgumentException ex2) {
                throw new ClassNotFoundException(null, ex2);
            }
        }
        try {
            return super.resolveProxyClass(interfaces);
        }
        catch (ClassNotFoundException ex3) {
            final Class<?>[] resolvedInterfaces2 = (Class<?>[])new Class[interfaces.length];
            for (int j = 0; j < interfaces.length; ++j) {
                resolvedInterfaces2[j] = this.resolveFallbackIfPossible(interfaces[j], ex3);
            }
            return Proxy.getProxyClass(this.getFallbackClassLoader(), resolvedInterfaces2);
        }
    }
    
    protected Class<?> resolveFallbackIfPossible(final String className, final ClassNotFoundException ex) throws IOException, ClassNotFoundException {
        throw ex;
    }
    
    protected ClassLoader getFallbackClassLoader() throws IOException {
        return null;
    }
}
