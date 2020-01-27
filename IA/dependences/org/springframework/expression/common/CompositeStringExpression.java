// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.common;

import org.springframework.expression.TypedValue;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;

public class CompositeStringExpression implements Expression
{
    private final String expressionString;
    private final Expression[] expressions;
    
    public CompositeStringExpression(final String expressionString, final Expression[] expressions) {
        this.expressionString = expressionString;
        this.expressions = expressions;
    }
    
    @Override
    public final String getExpressionString() {
        return this.expressionString;
    }
    
    @Override
    public String getValue() throws EvaluationException {
        final StringBuilder sb = new StringBuilder();
        for (final Expression expression : this.expressions) {
            final String value = expression.getValue(String.class);
            if (value != null) {
                sb.append(value);
            }
        }
        return sb.toString();
    }
    
    @Override
    public String getValue(final Object rootObject) throws EvaluationException {
        final StringBuilder sb = new StringBuilder();
        for (final Expression expression : this.expressions) {
            final String value = expression.getValue(rootObject, String.class);
            if (value != null) {
                sb.append(value);
            }
        }
        return sb.toString();
    }
    
    @Override
    public String getValue(final EvaluationContext context) throws EvaluationException {
        final StringBuilder sb = new StringBuilder();
        for (final Expression expression : this.expressions) {
            final String value = expression.getValue(context, String.class);
            if (value != null) {
                sb.append(value);
            }
        }
        return sb.toString();
    }
    
    @Override
    public String getValue(final EvaluationContext context, final Object rootObject) throws EvaluationException {
        final StringBuilder sb = new StringBuilder();
        for (final Expression expression : this.expressions) {
            final String value = expression.getValue(context, rootObject, String.class);
            if (value != null) {
                sb.append(value);
            }
        }
        return sb.toString();
    }
    
    @Override
    public Class<?> getValueType(final EvaluationContext context) {
        return String.class;
    }
    
    @Override
    public Class<?> getValueType() {
        return String.class;
    }
    
    @Override
    public TypeDescriptor getValueTypeDescriptor(final EvaluationContext context) {
        return TypeDescriptor.valueOf(String.class);
    }
    
    @Override
    public TypeDescriptor getValueTypeDescriptor() {
        return TypeDescriptor.valueOf(String.class);
    }
    
    @Override
    public void setValue(final EvaluationContext context, final Object value) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
    }
    
    @Override
    public <T> T getValue(final EvaluationContext context, final Class<T> expectedResultType) throws EvaluationException {
        final Object value = this.getValue(context);
        return ExpressionUtils.convertTypedValue(context, new TypedValue(value), expectedResultType);
    }
    
    @Override
    public <T> T getValue(final Class<T> expectedResultType) throws EvaluationException {
        final Object value = this.getValue();
        return ExpressionUtils.convertTypedValue(null, new TypedValue(value), expectedResultType);
    }
    
    @Override
    public boolean isWritable(final EvaluationContext context) {
        return false;
    }
    
    public Expression[] getExpressions() {
        return this.expressions;
    }
    
    @Override
    public <T> T getValue(final Object rootObject, final Class<T> desiredResultType) throws EvaluationException {
        final Object value = this.getValue(rootObject);
        return ExpressionUtils.convertTypedValue(null, new TypedValue(value), desiredResultType);
    }
    
    @Override
    public <T> T getValue(final EvaluationContext context, final Object rootObject, final Class<T> desiredResultType) throws EvaluationException {
        final Object value = this.getValue(context, rootObject);
        return ExpressionUtils.convertTypedValue(context, new TypedValue(value), desiredResultType);
    }
    
    @Override
    public Class<?> getValueType(final Object rootObject) throws EvaluationException {
        return String.class;
    }
    
    @Override
    public Class<?> getValueType(final EvaluationContext context, final Object rootObject) throws EvaluationException {
        return String.class;
    }
    
    @Override
    public TypeDescriptor getValueTypeDescriptor(final Object rootObject) throws EvaluationException {
        return TypeDescriptor.valueOf(String.class);
    }
    
    @Override
    public TypeDescriptor getValueTypeDescriptor(final EvaluationContext context, final Object rootObject) throws EvaluationException {
        return TypeDescriptor.valueOf(String.class);
    }
    
    @Override
    public boolean isWritable(final EvaluationContext context, final Object rootObject) throws EvaluationException {
        return false;
    }
    
    @Override
    public void setValue(final EvaluationContext context, final Object rootObject, final Object value) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
    }
    
    @Override
    public boolean isWritable(final Object rootObject) throws EvaluationException {
        return false;
    }
    
    @Override
    public void setValue(final Object rootObject, final Object value) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
    }
}
