// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.proxy;

import org.springframework.cglib.core.CodeEmitter;
import java.util.Iterator;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.core.MethodInfo;
import java.util.List;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.Signature;
import org.springframework.asm.Type;

class DispatcherGenerator implements CallbackGenerator
{
    public static final DispatcherGenerator INSTANCE;
    public static final DispatcherGenerator PROXY_REF_INSTANCE;
    private static final Type DISPATCHER;
    private static final Type PROXY_REF_DISPATCHER;
    private static final Signature LOAD_OBJECT;
    private static final Signature PROXY_REF_LOAD_OBJECT;
    private boolean proxyRef;
    
    private DispatcherGenerator(final boolean proxyRef) {
        this.proxyRef = proxyRef;
    }
    
    public void generate(final ClassEmitter ce, final Context context, final List methods) {
        for (final MethodInfo method : methods) {
            if (!TypeUtils.isProtected(method.getModifiers())) {
                final CodeEmitter e = context.beginMethod(ce, method);
                context.emitCallback(e, context.getIndex(method));
                if (this.proxyRef) {
                    e.load_this();
                    e.invoke_interface(DispatcherGenerator.PROXY_REF_DISPATCHER, DispatcherGenerator.PROXY_REF_LOAD_OBJECT);
                }
                else {
                    e.invoke_interface(DispatcherGenerator.DISPATCHER, DispatcherGenerator.LOAD_OBJECT);
                }
                e.checkcast(method.getClassInfo().getType());
                e.load_args();
                e.invoke(method);
                e.return_value();
                e.end_method();
            }
        }
    }
    
    public void generateStatic(final CodeEmitter e, final Context context, final List methods) {
    }
    
    static {
        INSTANCE = new DispatcherGenerator(false);
        PROXY_REF_INSTANCE = new DispatcherGenerator(true);
        DISPATCHER = TypeUtils.parseType("org.springframework.cglib.proxy.Dispatcher");
        PROXY_REF_DISPATCHER = TypeUtils.parseType("org.springframework.cglib.proxy.ProxyRefDispatcher");
        LOAD_OBJECT = TypeUtils.parseSignature("Object loadObject()");
        PROXY_REF_LOAD_OBJECT = TypeUtils.parseSignature("Object loadObject(Object)");
    }
}
