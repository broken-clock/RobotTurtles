// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.support;

import org.springframework.expression.AccessException;
import org.springframework.util.ReflectionUtils;
import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationContext;
import java.lang.reflect.Constructor;
import org.springframework.expression.ConstructorExecutor;

class ReflectiveConstructorExecutor implements ConstructorExecutor
{
    private final Constructor<?> ctor;
    private final Integer varargsPosition;
    
    public ReflectiveConstructorExecutor(final Constructor<?> ctor) {
        this.ctor = ctor;
        if (ctor.isVarArgs()) {
            final Class<?>[] paramTypes = ctor.getParameterTypes();
            this.varargsPosition = paramTypes.length - 1;
        }
        else {
            this.varargsPosition = null;
        }
    }
    
    @Override
    public TypedValue execute(final EvaluationContext context, Object... arguments) throws AccessException {
        try {
            if (arguments != null) {
                ReflectionHelper.convertArguments(context.getTypeConverter(), arguments, this.ctor, this.varargsPosition);
            }
            if (this.ctor.isVarArgs()) {
                arguments = ReflectionHelper.setupArgumentsForVarargsInvocation(this.ctor.getParameterTypes(), arguments);
            }
            ReflectionUtils.makeAccessible(this.ctor);
            return new TypedValue(this.ctor.newInstance(arguments));
        }
        catch (Exception ex) {
            throw new AccessException("Problem invoking constructor: " + this.ctor, ex);
        }
    }
}
