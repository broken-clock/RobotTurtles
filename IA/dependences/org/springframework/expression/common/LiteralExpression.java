// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.common;

import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

public class LiteralExpression implements Expression
{
    private final String literalValue;
    
    public LiteralExpression(final String literalValue) {
        this.literalValue = literalValue;
    }
    
    @Override
    public final String getExpressionString() {
        return this.literalValue;
    }
    
    @Override
    public String getValue() {
        return this.literalValue;
    }
    
    @Override
    public String getValue(final EvaluationContext context) {
        return this.literalValue;
    }
    
    @Override
    public String getValue(final Object rootObject) {
        return this.literalValue;
    }
    
    @Override
    public Class<?> getValueType(final EvaluationContext context) {
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
        throw new EvaluationException(this.literalValue, "Cannot call setValue() on a LiteralExpression");
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
    
    @Override
    public Class<?> getValueType() {
        return String.class;
    }
    
    @Override
    public <T> T getValue(final Object rootObject, final Class<T> desiredResultType) throws EvaluationException {
        final Object value = this.getValue(rootObject);
        return ExpressionUtils.convertTypedValue(null, new TypedValue(value), desiredResultType);
    }
    
    @Override
    public String getValue(final EvaluationContext context, final Object rootObject) throws EvaluationException {
        return this.literalValue;
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
        throw new EvaluationException(this.literalValue, "Cannot call setValue() on a LiteralExpression");
    }
    
    @Override
    public boolean isWritable(final Object rootObject) throws EvaluationException {
        return false;
    }
    
    @Override
    public void setValue(final Object rootObject, final Object value) throws EvaluationException {
        throw new EvaluationException(this.literalValue, "Cannot call setValue() on a LiteralExpression");
    }
}
