// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.expression;

import org.springframework.expression.TypedValue;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.core.env.Environment;
import org.springframework.expression.PropertyAccessor;

public class EnvironmentAccessor implements PropertyAccessor
{
    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return (Class<?>[])new Class[] { Environment.class };
    }
    
    @Override
    public boolean canRead(final EvaluationContext context, final Object target, final String name) throws AccessException {
        return true;
    }
    
    @Override
    public TypedValue read(final EvaluationContext context, final Object target, final String name) throws AccessException {
        return new TypedValue(((Environment)target).getProperty(name));
    }
    
    @Override
    public boolean canWrite(final EvaluationContext context, final Object target, final String name) throws AccessException {
        return false;
    }
    
    @Override
    public void write(final EvaluationContext context, final Object target, final String name, final Object newValue) throws AccessException {
    }
}
