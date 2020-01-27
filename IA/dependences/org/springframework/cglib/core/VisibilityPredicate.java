// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

import java.lang.reflect.Modifier;
import java.lang.reflect.Member;
import org.springframework.asm.Type;

public class VisibilityPredicate implements Predicate
{
    private boolean protectedOk;
    private String pkg;
    
    public VisibilityPredicate(final Class source, final boolean protectedOk) {
        this.protectedOk = protectedOk;
        this.pkg = TypeUtils.getPackageName(Type.getType(source));
    }
    
    public boolean evaluate(final Object arg) {
        final int mod = (arg instanceof Member) ? ((Member)arg).getModifiers() : arg;
        if (Modifier.isPrivate(mod)) {
            return false;
        }
        if (Modifier.isPublic(mod)) {
            return true;
        }
        if (Modifier.isProtected(mod)) {
            return this.protectedOk;
        }
        return this.pkg.equals(TypeUtils.getPackageName(Type.getType(((Member)arg).getDeclaringClass())));
    }
}
