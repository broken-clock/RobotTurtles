// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.proxy;

import org.springframework.cglib.core.CodeEmitter;
import java.util.Iterator;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.core.MethodInfo;
import java.util.List;
import org.springframework.cglib.core.ClassEmitter;

class NoOpGenerator implements CallbackGenerator
{
    public static final NoOpGenerator INSTANCE;
    
    public void generate(final ClassEmitter ce, final Context context, final List methods) {
        for (final MethodInfo method : methods) {
            if (TypeUtils.isBridge(method.getModifiers()) || (TypeUtils.isProtected(context.getOriginalModifiers(method)) && TypeUtils.isPublic(method.getModifiers()))) {
                final CodeEmitter e = EmitUtils.begin_method(ce, method);
                e.load_this();
                e.load_args();
                context.emitInvoke(e, method);
                e.return_value();
                e.end_method();
            }
        }
    }
    
    public void generateStatic(final CodeEmitter e, final Context context, final List methods) {
    }
    
    static {
        INSTANCE = new NoOpGenerator();
    }
}
