// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.support;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.OperatorOverloader;

public class StandardOperatorOverloader implements OperatorOverloader
{
    @Override
    public boolean overridesOperation(final Operation operation, final Object leftOperand, final Object rightOperand) throws EvaluationException {
        return false;
    }
    
    @Override
    public Object operate(final Operation operation, final Object leftOperand, final Object rightOperand) throws EvaluationException {
        throw new EvaluationException("No operation overloaded by default");
    }
}
