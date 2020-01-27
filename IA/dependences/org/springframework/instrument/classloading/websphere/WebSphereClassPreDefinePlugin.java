// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading.websphere;

import java.security.CodeSource;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import org.springframework.util.FileCopyUtils;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.InvocationHandler;

class WebSphereClassPreDefinePlugin implements InvocationHandler
{
    private final ClassFileTransformer transformer;
    
    public WebSphereClassPreDefinePlugin(final ClassFileTransformer transformer) {
        this.transformer = transformer;
        final ClassLoader classLoader = transformer.getClass().getClassLoader();
        try {
            final String dummyClass = Dummy.class.getName().replace('.', '/');
            final byte[] bytes = FileCopyUtils.copyToByteArray(classLoader.getResourceAsStream(dummyClass + ".class"));
            transformer.transform(classLoader, dummyClass, null, null, bytes);
        }
        catch (Throwable ex) {
            throw new IllegalArgumentException("Cannot load transformer", ex);
        }
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String name = method.getName();
        if ("equals".equals(name)) {
            return proxy == args[0];
        }
        if ("hashCode".equals(name)) {
            return this.hashCode();
        }
        if ("toString".equals(name)) {
            return this.toString();
        }
        if ("transformClass".equals(name)) {
            return this.transform((String)args[0], (byte[])args[1], (CodeSource)args[2], (ClassLoader)args[3]);
        }
        throw new IllegalArgumentException("Unknown method: " + method);
    }
    
    protected byte[] transform(final String className, final byte[] classfileBuffer, final CodeSource codeSource, final ClassLoader classLoader) throws Exception {
        final byte[] result = this.transformer.transform(classLoader, className.replace('.', '/'), null, null, classfileBuffer);
        return (result != null) ? result : classfileBuffer;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(this.getClass().getName());
        builder.append(" for transformer: ");
        builder.append(this.transformer);
        return builder.toString();
    }
    
    private static class Dummy
    {
    }
}
