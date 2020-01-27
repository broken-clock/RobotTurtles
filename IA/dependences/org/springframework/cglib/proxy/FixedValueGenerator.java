// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.proxy;

import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.core.CodeEmitter;
import java.util.Iterator;
import org.springframework.cglib.core.MethodInfo;
import java.util.List;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.Signature;
import org.springframework.asm.Type;

class FixedValueGenerator implements CallbackGenerator
{
    public static final FixedValueGenerator INSTANCE;
    private static final Type FIXED_VALUE;
    private static final Signature LOAD_OBJECT;
    
    public void generate(final ClassEmitter ce, final Context context, final List methods) {
        for (final MethodInfo method : methods) {
            final CodeEmitter e = context.beginMethod(ce, method);
            context.emitCallback(e, context.getIndex(method));
            e.invoke_interface(FixedValueGenerator.FIXED_VALUE, FixedValueGenerator.LOAD_OBJECT);
            e.unbox_or_zero(e.getReturnType());
            e.return_value();
            e.end_method();
        }
    }
    
    public void generateStatic(final CodeEmitter e, final Context context, final List methods) {
    }
    
    static {
        INSTANCE = new FixedValueGenerator();
        FIXED_VALUE = TypeUtils.parseType("org.springframework.cglib.proxy.FixedValue");
        LOAD_OBJECT = TypeUtils.parseSignature("Object loadObject()");
    }
}
