// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypeConverter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.expression.spel.support.ReflectionHelper;
import java.lang.reflect.Modifier;
import org.springframework.expression.EvaluationException;
import java.lang.reflect.Method;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

public class FunctionReference extends SpelNodeImpl
{
    private final String name;
    
    public FunctionReference(final String functionName, final int pos, final SpelNodeImpl... arguments) {
        super(pos, arguments);
        this.name = functionName;
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final TypedValue o = state.lookupVariable(this.name);
        if (o == null) {
            throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.FUNCTION_NOT_DEFINED, new Object[] { this.name });
        }
        if (!(o.getValue() instanceof Method)) {
            throw new SpelEvaluationException(SpelMessage.FUNCTION_REFERENCE_CANNOT_BE_INVOKED, new Object[] { this.name, o.getClass() });
        }
        try {
            return this.executeFunctionJLRMethod(state, (Method)o.getValue());
        }
        catch (SpelEvaluationException se) {
            se.setPosition(this.getStartPosition());
            throw se;
        }
    }
    
    private TypedValue executeFunctionJLRMethod(final ExpressionState state, final Method method) throws EvaluationException {
        Object[] functionArgs = this.getArguments(state);
        if (!method.isVarArgs() && method.getParameterTypes().length != functionArgs.length) {
            throw new SpelEvaluationException(SpelMessage.INCORRECT_NUMBER_OF_ARGUMENTS_TO_FUNCTION, new Object[] { functionArgs.length, method.getParameterTypes().length });
        }
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.FUNCTION_MUST_BE_STATIC, new Object[] { method.getDeclaringClass().getName() + "." + method.getName(), this.name });
        }
        if (functionArgs != null) {
            final TypeConverter converter = state.getEvaluationContext().getTypeConverter();
            ReflectionHelper.convertAllArguments(converter, functionArgs, method);
        }
        if (method.isVarArgs()) {
            functionArgs = ReflectionHelper.setupArgumentsForVarargsInvocation(method.getParameterTypes(), functionArgs);
        }
        try {
            ReflectionUtils.makeAccessible(method);
            final Object result = method.invoke(method.getClass(), functionArgs);
            return new TypedValue(result, new TypeDescriptor(new MethodParameter(method, -1)).narrow(result));
        }
        catch (Exception ex) {
            throw new SpelEvaluationException(this.getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_FUNCTION_CALL, new Object[] { this.name, ex.getMessage() });
        }
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder sb = new StringBuilder("#").append(this.name);
        sb.append("(");
        for (int i = 0; i < this.getChildCount(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(this.getChild(i).toStringAST());
        }
        sb.append(")");
        return sb.toString();
    }
    
    private Object[] getArguments(final ExpressionState state) throws EvaluationException {
        final Object[] arguments = new Object[this.getChildCount()];
        for (int i = 0; i < arguments.length; ++i) {
            arguments[i] = this.children[i].getValueInternal(state).getValue();
        }
        return arguments;
    }
}
