// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.util.ClassUtils;
import java.util.Iterator;
import java.util.List;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.util.ObjectUtils;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

public class Projection extends SpelNodeImpl
{
    private final boolean nullSafe;
    
    public Projection(final boolean nullSafe, final int pos, final SpelNodeImpl expression) {
        super(pos, new SpelNodeImpl[] { expression });
        this.nullSafe = nullSafe;
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        return this.getValueRef(state).getValue();
    }
    
    @Override
    protected ValueRef getValueRef(final ExpressionState state) throws EvaluationException {
        final TypedValue op = state.getActiveContextObject();
        final Object operand = op.getValue();
        final boolean operandIsArray = ObjectUtils.isArray(operand);
        if (operand instanceof Map) {
            final Map<?, ?> mapData = (Map<?, ?>)operand;
            final List<Object> result = new ArrayList<Object>();
            for (final Map.Entry<?, ?> entry : mapData.entrySet()) {
                try {
                    state.pushActiveContextObject(new TypedValue(entry));
                    result.add(this.children[0].getValueInternal(state).getValue());
                }
                finally {
                    state.popActiveContextObject();
                }
            }
            return new ValueRef.TypedValueHolderValueRef(new TypedValue(result), this);
        }
        if (operand instanceof Collection || operandIsArray) {
            final Collection<?> data = (Collection<?>)((operand instanceof Collection) ? ((Collection)operand) : Arrays.asList(ObjectUtils.toObjectArray(operand)));
            final List<Object> result = new ArrayList<Object>();
            int idx = 0;
            Class<?> arrayElementType = null;
            for (final Object element : data) {
                try {
                    state.pushActiveContextObject(new TypedValue(element));
                    state.enterScope("index", idx);
                    final Object value = this.children[0].getValueInternal(state).getValue();
                    if (value != null && operandIsArray) {
                        arrayElementType = this.determineCommonType(arrayElementType, value.getClass());
                    }
                    result.add(value);
                }
                finally {
                    state.exitScope();
                    state.popActiveContextObject();
                }
                ++idx;
            }
            if (operandIsArray) {
                if (arrayElementType == null) {
                    arrayElementType = Object.class;
                }
                final Object resultArray = Array.newInstance(arrayElementType, result.size());
                System.arraycopy(result.toArray(), 0, resultArray, 0, result.size());
                return new ValueRef.TypedValueHolderValueRef(new TypedValue(resultArray), this);
            }
            return new ValueRef.TypedValueHolderValueRef(new TypedValue(result), this);
        }
        else {
            if (operand != null) {
                throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.PROJECTION_NOT_SUPPORTED_ON_TYPE, new Object[] { operand.getClass().getName() });
            }
            if (this.nullSafe) {
                return ValueRef.NullValueRef.instance;
            }
            throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.PROJECTION_NOT_SUPPORTED_ON_TYPE, new Object[] { "null" });
        }
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder sb = new StringBuilder();
        return sb.append("![").append(this.getChild(0).toStringAST()).append("]").toString();
    }
    
    private Class<?> determineCommonType(final Class<?> oldType, final Class<?> newType) {
        if (oldType == null) {
            return newType;
        }
        if (oldType.isAssignableFrom(newType)) {
            return oldType;
        }
        for (Class<?> nextType = newType; nextType != Object.class; nextType = nextType.getSuperclass()) {
            if (nextType.isAssignableFrom(oldType)) {
                return nextType;
            }
        }
        final Class<?>[] allInterfacesForClass;
        final Class<?>[] interfaces = allInterfacesForClass = ClassUtils.getAllInterfacesForClass(newType);
        for (final Class<?> nextInterface : allInterfacesForClass) {
            if (nextInterface.isAssignableFrom(oldType)) {
                return nextInterface;
            }
        }
        return Object.class;
    }
}
