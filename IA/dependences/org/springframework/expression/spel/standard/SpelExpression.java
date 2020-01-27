// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.standard;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.SpelNode;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;
import org.springframework.expression.TypedValue;
import org.springframework.expression.common.ExpressionUtils;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.Expression;

public class SpelExpression implements Expression
{
    private final String expression;
    private final SpelNodeImpl ast;
    private final SpelParserConfiguration configuration;
    private EvaluationContext defaultContext;
    
    public SpelExpression(final String expression, final SpelNodeImpl ast, final SpelParserConfiguration configuration) {
        this.expression = expression;
        this.ast = ast;
        this.configuration = configuration;
    }
    
    @Override
    public Object getValue() throws EvaluationException {
        final ExpressionState expressionState = new ExpressionState(this.getEvaluationContext(), this.configuration);
        return this.ast.getValue(expressionState);
    }
    
    @Override
    public Object getValue(final Object rootObject) throws EvaluationException {
        final ExpressionState expressionState = new ExpressionState(this.getEvaluationContext(), this.toTypedValue(rootObject), this.configuration);
        return this.ast.getValue(expressionState);
    }
    
    @Override
    public <T> T getValue(final Class<T> expectedResultType) throws EvaluationException {
        final ExpressionState expressionState = new ExpressionState(this.getEvaluationContext(), this.configuration);
        final TypedValue typedResultValue = this.ast.getTypedValue(expressionState);
        return ExpressionUtils.convertTypedValue(expressionState.getEvaluationContext(), typedResultValue, expectedResultType);
    }
    
    @Override
    public <T> T getValue(final Object rootObject, final Class<T> expectedResultType) throws EvaluationException {
        final ExpressionState expressionState = new ExpressionState(this.getEvaluationContext(), this.toTypedValue(rootObject), this.configuration);
        final TypedValue typedResultValue = this.ast.getTypedValue(expressionState);
        return ExpressionUtils.convertTypedValue(expressionState.getEvaluationContext(), typedResultValue, expectedResultType);
    }
    
    @Override
    public Object getValue(final EvaluationContext context) throws EvaluationException {
        Assert.notNull(context, "The EvaluationContext is required");
        return this.ast.getValue(new ExpressionState(context, this.configuration));
    }
    
    @Override
    public Object getValue(final EvaluationContext context, final Object rootObject) throws EvaluationException {
        Assert.notNull(context, "The EvaluationContext is required");
        return this.ast.getValue(new ExpressionState(context, this.toTypedValue(rootObject), this.configuration));
    }
    
    @Override
    public <T> T getValue(final EvaluationContext context, final Class<T> expectedResultType) throws EvaluationException {
        final TypedValue typedResultValue = this.ast.getTypedValue(new ExpressionState(context, this.configuration));
        return ExpressionUtils.convertTypedValue(context, typedResultValue, expectedResultType);
    }
    
    @Override
    public <T> T getValue(final EvaluationContext context, final Object rootObject, final Class<T> expectedResultType) throws EvaluationException {
        final TypedValue typedResultValue = this.ast.getTypedValue(new ExpressionState(context, this.toTypedValue(rootObject), this.configuration));
        return ExpressionUtils.convertTypedValue(context, typedResultValue, expectedResultType);
    }
    
    @Override
    public Class<?> getValueType() throws EvaluationException {
        return this.getValueType(this.getEvaluationContext());
    }
    
    @Override
    public Class<?> getValueType(final Object rootObject) throws EvaluationException {
        return this.getValueType(this.getEvaluationContext(), rootObject);
    }
    
    @Override
    public Class<?> getValueType(final EvaluationContext context) throws EvaluationException {
        Assert.notNull(context, "The EvaluationContext is required");
        final ExpressionState eState = new ExpressionState(context, this.configuration);
        final TypeDescriptor typeDescriptor = this.ast.getValueInternal(eState).getTypeDescriptor();
        return (typeDescriptor != null) ? typeDescriptor.getType() : null;
    }
    
    @Override
    public Class<?> getValueType(final EvaluationContext context, final Object rootObject) throws EvaluationException {
        final ExpressionState eState = new ExpressionState(context, this.toTypedValue(rootObject), this.configuration);
        final TypeDescriptor typeDescriptor = this.ast.getValueInternal(eState).getTypeDescriptor();
        return (typeDescriptor != null) ? typeDescriptor.getType() : null;
    }
    
    @Override
    public TypeDescriptor getValueTypeDescriptor() throws EvaluationException {
        return this.getValueTypeDescriptor(this.getEvaluationContext());
    }
    
    @Override
    public TypeDescriptor getValueTypeDescriptor(final Object rootObject) throws EvaluationException {
        final ExpressionState eState = new ExpressionState(this.getEvaluationContext(), this.toTypedValue(rootObject), this.configuration);
        return this.ast.getValueInternal(eState).getTypeDescriptor();
    }
    
    @Override
    public TypeDescriptor getValueTypeDescriptor(final EvaluationContext context) throws EvaluationException {
        Assert.notNull(context, "The EvaluationContext is required");
        final ExpressionState eState = new ExpressionState(context, this.configuration);
        return this.ast.getValueInternal(eState).getTypeDescriptor();
    }
    
    @Override
    public TypeDescriptor getValueTypeDescriptor(final EvaluationContext context, final Object rootObject) throws EvaluationException {
        Assert.notNull(context, "The EvaluationContext is required");
        final ExpressionState eState = new ExpressionState(context, this.toTypedValue(rootObject), this.configuration);
        return this.ast.getValueInternal(eState).getTypeDescriptor();
    }
    
    @Override
    public String getExpressionString() {
        return this.expression;
    }
    
    @Override
    public boolean isWritable(final EvaluationContext context) throws EvaluationException {
        Assert.notNull(context, "The EvaluationContext is required");
        return this.ast.isWritable(new ExpressionState(context, this.configuration));
    }
    
    @Override
    public boolean isWritable(final Object rootObject) throws EvaluationException {
        return this.ast.isWritable(new ExpressionState(this.getEvaluationContext(), this.toTypedValue(rootObject), this.configuration));
    }
    
    @Override
    public boolean isWritable(final EvaluationContext context, final Object rootObject) throws EvaluationException {
        Assert.notNull(context, "The EvaluationContext is required");
        return this.ast.isWritable(new ExpressionState(context, this.toTypedValue(rootObject), this.configuration));
    }
    
    @Override
    public void setValue(final EvaluationContext context, final Object value) throws EvaluationException {
        Assert.notNull(context, "The EvaluationContext is required");
        this.ast.setValue(new ExpressionState(context, this.configuration), value);
    }
    
    @Override
    public void setValue(final Object rootObject, final Object value) throws EvaluationException {
        this.ast.setValue(new ExpressionState(this.getEvaluationContext(), this.toTypedValue(rootObject), this.configuration), value);
    }
    
    @Override
    public void setValue(final EvaluationContext context, final Object rootObject, final Object value) throws EvaluationException {
        Assert.notNull(context, "The EvaluationContext is required");
        this.ast.setValue(new ExpressionState(context, this.toTypedValue(rootObject), this.configuration), value);
    }
    
    public SpelNode getAST() {
        return this.ast;
    }
    
    public String toStringAST() {
        return this.ast.toStringAST();
    }
    
    public EvaluationContext getEvaluationContext() {
        if (this.defaultContext == null) {
            this.defaultContext = new StandardEvaluationContext();
        }
        return this.defaultContext;
    }
    
    public void setEvaluationContext(final EvaluationContext context) {
        this.defaultContext = context;
    }
    
    private TypedValue toTypedValue(final Object object) {
        if (object == null) {
            return TypedValue.NULL;
        }
        return new TypedValue(object);
    }
}
