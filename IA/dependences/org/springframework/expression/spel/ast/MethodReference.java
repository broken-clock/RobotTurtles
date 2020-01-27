// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.ExpressionInvocationTargetException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.spel.support.ReflectiveMethodResolver;
import java.util.Collections;
import java.util.ArrayList;
import org.springframework.expression.MethodExecutor;
import java.util.List;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.AccessException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.ExpressionState;

public class MethodReference extends SpelNodeImpl
{
    private final String name;
    private final boolean nullSafe;
    private volatile CachedMethodExecutor cachedExecutor;
    
    public MethodReference(final boolean nullSafe, final String methodName, final int pos, final SpelNodeImpl... arguments) {
        super(pos, arguments);
        this.name = methodName;
        this.nullSafe = nullSafe;
    }
    
    public final String getName() {
        return this.name;
    }
    
    @Override
    protected ValueRef getValueRef(final ExpressionState state) throws EvaluationException {
        final Object[] arguments = this.getArguments(state);
        if (state.getActiveContextObject().getValue() == null) {
            this.throwIfNotNullSafe(this.getArgumentTypes(arguments));
            return ValueRef.NullValueRef.instance;
        }
        return new MethodValueRef(state);
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final EvaluationContext evaluationContext = state.getEvaluationContext();
        final Object value = state.getActiveContextObject().getValue();
        final TypeDescriptor targetType = state.getActiveContextObject().getTypeDescriptor();
        final Object[] arguments = this.getArguments(state);
        return this.getValueInternal(evaluationContext, value, targetType, arguments);
    }
    
    private TypedValue getValueInternal(final EvaluationContext evaluationContext, final Object value, final TypeDescriptor targetType, final Object[] arguments) {
        final List<TypeDescriptor> argumentTypes = this.getArgumentTypes(arguments);
        if (value == null) {
            this.throwIfNotNullSafe(argumentTypes);
            return TypedValue.NULL;
        }
        MethodExecutor executorToUse = this.getCachedExecutor(evaluationContext, value, targetType, argumentTypes);
        if (executorToUse != null) {
            try {
                return executorToUse.execute(evaluationContext, value, arguments);
            }
            catch (AccessException ae) {
                this.throwSimpleExceptionIfPossible(value, ae);
                this.cachedExecutor = null;
            }
        }
        executorToUse = this.findAccessorForMethod(this.name, argumentTypes, value, evaluationContext);
        this.cachedExecutor = new CachedMethodExecutor(executorToUse, (value instanceof Class) ? ((Class)value) : null, targetType, argumentTypes);
        try {
            return executorToUse.execute(evaluationContext, value, arguments);
        }
        catch (AccessException ex) {
            this.throwSimpleExceptionIfPossible(value, ex);
            throw new SpelEvaluationException(this.getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_METHOD_INVOCATION, new Object[] { this.name, value.getClass().getName(), ex.getMessage() });
        }
    }
    
    private void throwIfNotNullSafe(final List<TypeDescriptor> argumentTypes) {
        if (!this.nullSafe) {
            throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.METHOD_CALL_ON_NULL_OBJECT_NOT_ALLOWED, new Object[] { FormatHelper.formatMethodForMessage(this.name, argumentTypes) });
        }
    }
    
    private Object[] getArguments(final ExpressionState state) {
        final Object[] arguments = new Object[this.getChildCount()];
        for (int i = 0; i < arguments.length; ++i) {
            try {
                state.pushActiveContextObject(state.getRootContextObject());
                arguments[i] = this.children[i].getValueInternal(state).getValue();
            }
            finally {
                state.popActiveContextObject();
            }
        }
        return arguments;
    }
    
    private List<TypeDescriptor> getArgumentTypes(final Object... arguments) {
        final List<TypeDescriptor> descriptors = new ArrayList<TypeDescriptor>(arguments.length);
        for (final Object argument : arguments) {
            descriptors.add(TypeDescriptor.forObject(argument));
        }
        return Collections.unmodifiableList((List<? extends TypeDescriptor>)descriptors);
    }
    
    private MethodExecutor getCachedExecutor(final EvaluationContext evaluationContext, final Object value, final TypeDescriptor target, final List<TypeDescriptor> argumentTypes) {
        final List<MethodResolver> methodResolvers = evaluationContext.getMethodResolvers();
        if (methodResolvers == null || methodResolvers.size() != 1 || !(methodResolvers.get(0) instanceof ReflectiveMethodResolver)) {
            return null;
        }
        final CachedMethodExecutor executorToCheck = this.cachedExecutor;
        if (executorToCheck != null && executorToCheck.isSuitable(value, target, argumentTypes)) {
            return executorToCheck.get();
        }
        this.cachedExecutor = null;
        return null;
    }
    
    private MethodExecutor findAccessorForMethod(final String name, final List<TypeDescriptor> argumentTypes, final Object targetObject, final EvaluationContext evaluationContext) throws SpelEvaluationException {
        final List<MethodResolver> methodResolvers = evaluationContext.getMethodResolvers();
        if (methodResolvers != null) {
            for (final MethodResolver methodResolver : methodResolvers) {
                try {
                    final MethodExecutor methodExecutor = methodResolver.resolve(evaluationContext, targetObject, name, argumentTypes);
                    if (methodExecutor != null) {
                        return methodExecutor;
                    }
                    continue;
                }
                catch (AccessException ex) {
                    throw new SpelEvaluationException(this.getStartPosition(), ex, SpelMessage.PROBLEM_LOCATING_METHOD, new Object[] { name, targetObject.getClass() });
                }
            }
        }
        throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.METHOD_NOT_FOUND, new Object[] { FormatHelper.formatMethodForMessage(name, argumentTypes), FormatHelper.formatClassNameForMessage((targetObject instanceof Class) ? ((Class)targetObject) : targetObject.getClass()) });
    }
    
    private void throwSimpleExceptionIfPossible(final Object value, final AccessException ae) {
        if (!(ae.getCause() instanceof InvocationTargetException)) {
            return;
        }
        final Throwable rootCause = ae.getCause().getCause();
        if (rootCause instanceof RuntimeException) {
            throw (RuntimeException)rootCause;
        }
        throw new ExpressionInvocationTargetException(this.getStartPosition(), "A problem occurred when trying to execute method '" + this.name + "' on object of type [" + value.getClass().getName() + "]", rootCause);
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.name).append("(");
        for (int i = 0; i < this.getChildCount(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(this.getChild(i).toStringAST());
        }
        sb.append(")");
        return sb.toString();
    }
    
    private class MethodValueRef implements ValueRef
    {
        private final EvaluationContext evaluationContext;
        private final Object value;
        private final TypeDescriptor targetType;
        private final Object[] arguments;
        
        public MethodValueRef(final ExpressionState state) {
            this.evaluationContext = state.getEvaluationContext();
            this.value = state.getActiveContextObject().getValue();
            this.targetType = state.getActiveContextObject().getTypeDescriptor();
            this.arguments = MethodReference.this.getArguments(state);
        }
        
        @Override
        public TypedValue getValue() {
            return MethodReference.this.getValueInternal(this.evaluationContext, this.value, this.targetType, this.arguments);
        }
        
        @Override
        public void setValue(final Object newValue) {
            throw new IllegalAccessError();
        }
        
        @Override
        public boolean isWritable() {
            return false;
        }
    }
    
    private static class CachedMethodExecutor
    {
        private final MethodExecutor methodExecutor;
        private final Class<?> staticClass;
        private final TypeDescriptor target;
        private final List<TypeDescriptor> argumentTypes;
        
        public CachedMethodExecutor(final MethodExecutor methodExecutor, final Class<?> staticClass, final TypeDescriptor target, final List<TypeDescriptor> argumentTypes) {
            this.methodExecutor = methodExecutor;
            this.staticClass = staticClass;
            this.target = target;
            this.argumentTypes = argumentTypes;
        }
        
        public boolean isSuitable(final Object value, final TypeDescriptor target, final List<TypeDescriptor> argumentTypes) {
            return (this.staticClass == null || this.staticClass.equals(value)) && this.target.equals(target) && this.argumentTypes.equals(argumentTypes);
        }
        
        public MethodExecutor get() {
            return this.methodExecutor;
        }
    }
}
