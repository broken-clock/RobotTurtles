// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel;

import java.util.HashMap;
import org.springframework.expression.PropertyAccessor;
import java.util.List;
import org.springframework.expression.OperatorOverloader;
import org.springframework.expression.Operation;
import java.util.Map;
import org.springframework.expression.TypeConverter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeComparator;
import org.springframework.util.Assert;
import java.util.Stack;
import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationContext;

public class ExpressionState
{
    private final EvaluationContext relatedContext;
    private final TypedValue rootObject;
    private final SpelParserConfiguration configuration;
    private Stack<VariableScope> variableScopes;
    private Stack<TypedValue> contextObjects;
    
    public ExpressionState(final EvaluationContext context) {
        this(context, context.getRootObject(), new SpelParserConfiguration(false, false));
    }
    
    public ExpressionState(final EvaluationContext context, final SpelParserConfiguration configuration) {
        this(context, context.getRootObject(), configuration);
    }
    
    public ExpressionState(final EvaluationContext context, final TypedValue rootObject) {
        this(context, rootObject, new SpelParserConfiguration(false, false));
    }
    
    public ExpressionState(final EvaluationContext context, final TypedValue rootObject, final SpelParserConfiguration configuration) {
        Assert.notNull(context, "EvaluationContext must not be null");
        Assert.notNull(configuration, "SpelParserConfiguration must not be null");
        this.relatedContext = context;
        this.rootObject = rootObject;
        this.configuration = configuration;
    }
    
    private void ensureVariableScopesInitialized() {
        if (this.variableScopes == null) {
            (this.variableScopes = new Stack<VariableScope>()).add(new VariableScope());
        }
    }
    
    public TypedValue getActiveContextObject() {
        if (this.contextObjects == null || this.contextObjects.isEmpty()) {
            return this.rootObject;
        }
        return this.contextObjects.peek();
    }
    
    public void pushActiveContextObject(final TypedValue obj) {
        if (this.contextObjects == null) {
            this.contextObjects = new Stack<TypedValue>();
        }
        this.contextObjects.push(obj);
    }
    
    public void popActiveContextObject() {
        if (this.contextObjects == null) {
            this.contextObjects = new Stack<TypedValue>();
        }
        this.contextObjects.pop();
    }
    
    public TypedValue getRootContextObject() {
        return this.rootObject;
    }
    
    public void setVariable(final String name, final Object value) {
        this.relatedContext.setVariable(name, value);
    }
    
    public TypedValue lookupVariable(final String name) {
        final Object value = this.relatedContext.lookupVariable(name);
        if (value == null) {
            return TypedValue.NULL;
        }
        return new TypedValue(value);
    }
    
    public TypeComparator getTypeComparator() {
        return this.relatedContext.getTypeComparator();
    }
    
    public Class<?> findType(final String type) throws EvaluationException {
        return this.relatedContext.getTypeLocator().findType(type);
    }
    
    public Object convertValue(final Object value, final TypeDescriptor targetTypeDescriptor) throws EvaluationException {
        return this.relatedContext.getTypeConverter().convertValue(value, TypeDescriptor.forObject(value), targetTypeDescriptor);
    }
    
    public TypeConverter getTypeConverter() {
        return this.relatedContext.getTypeConverter();
    }
    
    public Object convertValue(final TypedValue value, final TypeDescriptor targetTypeDescriptor) throws EvaluationException {
        final Object val = value.getValue();
        return this.relatedContext.getTypeConverter().convertValue(val, TypeDescriptor.forObject(val), targetTypeDescriptor);
    }
    
    public void enterScope(final Map<String, Object> argMap) {
        this.ensureVariableScopesInitialized();
        this.variableScopes.push(new VariableScope(argMap));
    }
    
    public void enterScope(final String name, final Object value) {
        this.ensureVariableScopesInitialized();
        this.variableScopes.push(new VariableScope(name, value));
    }
    
    public void exitScope() {
        this.ensureVariableScopesInitialized();
        this.variableScopes.pop();
    }
    
    public void setLocalVariable(final String name, final Object value) {
        this.ensureVariableScopesInitialized();
        this.variableScopes.peek().setVariable(name, value);
    }
    
    public Object lookupLocalVariable(final String name) {
        this.ensureVariableScopesInitialized();
        int i;
        for (int scopeNumber = i = this.variableScopes.size() - 1; i >= 0; --i) {
            if (this.variableScopes.get(i).definesVariable(name)) {
                return this.variableScopes.get(i).lookupVariable(name);
            }
        }
        return null;
    }
    
    public TypedValue operate(final Operation op, final Object left, final Object right) throws EvaluationException {
        final OperatorOverloader overloader = this.relatedContext.getOperatorOverloader();
        if (overloader.overridesOperation(op, left, right)) {
            final Object returnValue = overloader.operate(op, left, right);
            return new TypedValue(returnValue);
        }
        final String leftType = (left == null) ? "null" : left.getClass().getName();
        final String rightType = (right == null) ? "null" : right.getClass().getName();
        throw new SpelEvaluationException(SpelMessage.OPERATOR_NOT_SUPPORTED_BETWEEN_TYPES, new Object[] { op, leftType, rightType });
    }
    
    public List<PropertyAccessor> getPropertyAccessors() {
        return this.relatedContext.getPropertyAccessors();
    }
    
    public EvaluationContext getEvaluationContext() {
        return this.relatedContext;
    }
    
    public SpelParserConfiguration getConfiguration() {
        return this.configuration;
    }
    
    private static class VariableScope
    {
        private final Map<String, Object> vars;
        
        public VariableScope() {
            this.vars = new HashMap<String, Object>();
        }
        
        public VariableScope(final Map<String, Object> arguments) {
            this.vars = new HashMap<String, Object>();
            if (arguments != null) {
                this.vars.putAll(arguments);
            }
        }
        
        public VariableScope(final String name, final Object value) {
            (this.vars = new HashMap<String, Object>()).put(name, value);
        }
        
        public Object lookupVariable(final String name) {
            return this.vars.get(name);
        }
        
        public void setVariable(final String name, final Object value) {
            this.vars.put(name, value);
        }
        
        public boolean definesVariable(final String name) {
            return this.vars.containsKey(name);
        }
    }
}
