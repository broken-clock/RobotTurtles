// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.expression;

import org.springframework.expression.TypedValue;
import org.springframework.expression.AccessException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;

public class BeanExpressionContextAccessor implements PropertyAccessor
{
    @Override
    public boolean canRead(final EvaluationContext context, final Object target, final String name) throws AccessException {
        return ((BeanExpressionContext)target).containsObject(name);
    }
    
    @Override
    public TypedValue read(final EvaluationContext context, final Object target, final String name) throws AccessException {
        return new TypedValue(((BeanExpressionContext)target).getObject(name));
    }
    
    @Override
    public boolean canWrite(final EvaluationContext context, final Object target, final String name) throws AccessException {
        return false;
    }
    
    @Override
    public void write(final EvaluationContext context, final Object target, final String name, final Object newValue) throws AccessException {
        throw new AccessException("Beans in a BeanFactory are read-only");
    }
    
    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return (Class<?>[])new Class[] { BeanExpressionContext.class };
    }
}
