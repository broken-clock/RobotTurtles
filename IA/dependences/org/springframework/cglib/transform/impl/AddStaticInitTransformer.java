// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform.impl;

import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.asm.Type;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.TypeUtils;
import java.lang.reflect.Member;
import org.springframework.cglib.core.ReflectUtils;
import java.lang.reflect.Method;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.transform.ClassEmitterTransformer;

public class AddStaticInitTransformer extends ClassEmitterTransformer
{
    private MethodInfo info;
    
    public AddStaticInitTransformer(final Method classInit) {
        this.info = ReflectUtils.getMethodInfo(classInit);
        if (!TypeUtils.isStatic(this.info.getModifiers())) {
            throw new IllegalArgumentException(classInit + " is not static");
        }
        final Type[] types = this.info.getSignature().getArgumentTypes();
        if (types.length != 1 || !types[0].equals(Constants.TYPE_CLASS) || !this.info.getSignature().getReturnType().equals(Type.VOID_TYPE)) {
            throw new IllegalArgumentException(classInit + " illegal signature");
        }
    }
    
    protected void init() {
        if (!TypeUtils.isInterface(this.getAccess())) {
            final CodeEmitter e = this.getStaticHook();
            EmitUtils.load_class_this(e);
            e.invoke(this.info);
        }
    }
}
