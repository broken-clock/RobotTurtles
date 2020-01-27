// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

public class VariableReference extends SpelNodeImpl
{
    private static final String THIS = "this";
    private static final String ROOT = "root";
    private final String name;
    
    public VariableReference(final String variableName, final int pos) {
        super(pos, new SpelNodeImpl[0]);
        this.name = variableName;
    }
    
    public ValueRef getValueRef(final ExpressionState state) throws SpelEvaluationException {
        if (this.name.equals("this")) {
            return new ValueRef.TypedValueHolderValueRef(state.getActiveContextObject(), this);
        }
        if (this.name.equals("root")) {
            return new ValueRef.TypedValueHolderValueRef(state.getRootContextObject(), this);
        }
        final TypedValue result = state.lookupVariable(this.name);
        return new VariableRef(this.name, result, state.getEvaluationContext());
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws SpelEvaluationException {
        if (this.name.equals("this")) {
            return state.getActiveContextObject();
        }
        if (this.name.equals("root")) {
            return state.getRootContextObject();
        }
        final TypedValue result = state.lookupVariable(this.name);
        return result;
    }
    
    @Override
    public void setValue(final ExpressionState state, final Object value) throws SpelEvaluationException {
        state.setVariable(this.name, value);
    }
    
    @Override
    public String toStringAST() {
        return "#" + this.name;
    }
    
    @Override
    public boolean isWritable(final ExpressionState expressionState) throws SpelEvaluationException {
        return !this.name.equals("this") && !this.name.equals("root");
    }
    
    class VariableRef implements ValueRef
    {
        private final String name;
        private final TypedValue value;
        private final EvaluationContext evaluationContext;
        
        public VariableRef(final String name, final TypedValue value, final EvaluationContext evaluationContext) {
            this.name = name;
            this.value = value;
            this.evaluationContext = evaluationContext;
        }
        
        @Override
        public TypedValue getValue() {
            return this.value;
        }
        
        @Override
        public void setValue(final Object newValue) {
            this.evaluationContext.setVariable(this.name, newValue);
        }
        
        @Override
        public boolean isWritable() {
            return true;
        }
    }
}
