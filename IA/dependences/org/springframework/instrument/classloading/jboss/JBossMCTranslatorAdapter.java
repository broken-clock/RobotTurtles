// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading.jboss;

import java.security.ProtectionDomain;
import java.lang.reflect.Method;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.InvocationHandler;

class JBossMCTranslatorAdapter implements InvocationHandler
{
    private final ClassFileTransformer transformer;
    
    public JBossMCTranslatorAdapter(final ClassFileTransformer transformer) {
        this.transformer = transformer;
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
        if ("transform".equals(name)) {
            return this.transform((ClassLoader)args[0], (String)args[1], (Class<?>)args[2], (ProtectionDomain)args[3], (byte[])args[4]);
        }
        if ("unregisterClassLoader".equals(name)) {
            this.unregisterClassLoader((ClassLoader)args[0]);
            return null;
        }
        throw new IllegalArgumentException("Unknown method: " + method);
    }
    
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws Exception {
        return this.transformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
    }
    
    public void unregisterClassLoader(final ClassLoader loader) {
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(this.getClass().getName());
        builder.append(" for transformer: ");
        builder.append(this.transformer);
        return builder.toString();
    }
}
