// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading.weblogic;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Hashtable;
import java.lang.reflect.Method;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.InvocationHandler;

class WebLogicClassPreProcessorAdapter implements InvocationHandler
{
    private final ClassFileTransformer transformer;
    private final ClassLoader loader;
    
    public WebLogicClassPreProcessorAdapter(final ClassFileTransformer transformer, final ClassLoader loader) {
        this.transformer = transformer;
        this.loader = loader;
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
        if ("initialize".equals(name)) {
            this.initialize((Hashtable<?, ?>)args[0]);
            return null;
        }
        if ("preProcess".equals(name)) {
            return this.preProcess((String)args[0], (byte[])args[1]);
        }
        throw new IllegalArgumentException("Unknown method: " + method);
    }
    
    public void initialize(final Hashtable<?, ?> params) {
    }
    
    public byte[] preProcess(final String className, final byte[] classBytes) {
        try {
            final byte[] result = this.transformer.transform(this.loader, className, null, null, classBytes);
            return (result != null) ? result : classBytes;
        }
        catch (IllegalClassFormatException ex) {
            throw new IllegalStateException("Cannot transform due to illegal class format", ex);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(this.getClass().getName());
        builder.append(" for transformer: ");
        builder.append(this.transformer);
        return builder.toString();
    }
}
