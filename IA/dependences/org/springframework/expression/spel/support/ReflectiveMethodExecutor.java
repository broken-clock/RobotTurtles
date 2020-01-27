// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.support;

import org.springframework.expression.AccessException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationContext;
import java.lang.reflect.Method;
import org.springframework.expression.MethodExecutor;

class ReflectiveMethodExecutor implements MethodExecutor
{
    private final Method method;
    private final Integer varargsPosition;
    
    public ReflectiveMethodExecutor(final Method method) {
        this.method = method;
        if (method.isVarArgs()) {
            final Class<?>[] paramTypes = method.getParameterTypes();
            this.varargsPosition = paramTypes.length - 1;
        }
        else {
            this.varargsPosition = null;
        }
    }
    
    @Override
    public TypedValue execute(final EvaluationContext context, final Object target, Object... arguments) throws AccessException {
        try {
            if (arguments != null) {
                ReflectionHelper.convertArguments(context.getTypeConverter(), arguments, this.method, this.varargsPosition);
            }
            if (this.method.isVarArgs()) {
                arguments = ReflectionHelper.setupArgumentsForVarargsInvocation(this.method.getParameterTypes(), arguments);
            }
            ReflectionUtils.makeAccessible(this.method);
            final Object value = this.method.invoke(target, arguments);
            return new TypedValue(value, new TypeDescriptor(new MethodParameter(this.method, -1)).narrow(value));
        }
        catch (Exception ex) {
            throw new AccessException("Problem invoking method: " + this.method, ex);
        }
    }
}
