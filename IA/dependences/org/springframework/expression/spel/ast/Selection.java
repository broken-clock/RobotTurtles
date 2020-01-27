// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import java.util.List;
import java.util.Iterator;
import java.lang.reflect.Array;
import org.springframework.util.ClassUtils;
import java.util.Arrays;
import java.util.ArrayList;
import org.springframework.util.ObjectUtils;
import java.util.Collection;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import java.util.HashMap;
import java.util.Map;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.util.Assert;

public class Selection extends SpelNodeImpl
{
    public static final int ALL = 0;
    public static final int FIRST = 1;
    public static final int LAST = 2;
    private final int variant;
    private final boolean nullSafe;
    
    public Selection(final boolean nullSafe, final int variant, final int pos, final SpelNodeImpl expression) {
        super(pos, (expression != null) ? new SpelNodeImpl[] { expression } : new SpelNodeImpl[0]);
        Assert.notNull(expression, "Expression must not be null");
        this.nullSafe = nullSafe;
        this.variant = variant;
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        return this.getValueRef(state).getValue();
    }
    
    @Override
    protected ValueRef getValueRef(final ExpressionState state) throws EvaluationException {
        final TypedValue op = state.getActiveContextObject();
        final Object operand = op.getValue();
        final SpelNodeImpl selectionCriteria = this.children[0];
        if (operand instanceof Map) {
            final Map<?, ?> mapdata = (Map<?, ?>)operand;
            final Map<Object, Object> result = new HashMap<Object, Object>();
            Object lastKey = null;
            for (final Map.Entry<?, ?> entry : mapdata.entrySet()) {
                try {
                    final TypedValue kvpair = new TypedValue(entry);
                    state.pushActiveContextObject(kvpair);
                    final Object o = selectionCriteria.getValueInternal(state).getValue();
                    if (!(o instanceof Boolean)) {
                        throw new SpelEvaluationException(selectionCriteria.getStartPosition(), SpelMessage.RESULT_OF_SELECTION_CRITERIA_IS_NOT_BOOLEAN, new Object[0]);
                    }
                    if (!(boolean)o) {
                        continue;
                    }
                    if (this.variant == 1) {
                        result.put(entry.getKey(), entry.getValue());
                        return new ValueRef.TypedValueHolderValueRef(new TypedValue(result), this);
                    }
                    result.put(entry.getKey(), entry.getValue());
                    lastKey = entry.getKey();
                }
                finally {
                    state.popActiveContextObject();
                }
            }
            if ((this.variant == 1 || this.variant == 2) && result.size() == 0) {
                return new ValueRef.TypedValueHolderValueRef(new TypedValue(null), this);
            }
            if (this.variant == 2) {
                final Map<Object, Object> resultMap = new HashMap<Object, Object>();
                final Object lastValue = result.get(lastKey);
                resultMap.put(lastKey, lastValue);
                return new ValueRef.TypedValueHolderValueRef(new TypedValue(resultMap), this);
            }
            return new ValueRef.TypedValueHolderValueRef(new TypedValue(result), this);
        }
        else if (operand instanceof Collection || ObjectUtils.isArray(operand)) {
            final List<Object> data = new ArrayList<Object>();
            final Collection<?> c = (Collection<?>)((operand instanceof Collection) ? ((Collection)operand) : Arrays.asList(ObjectUtils.toObjectArray(operand)));
            data.addAll(c);
            final List<Object> result2 = new ArrayList<Object>();
            int idx = 0;
            for (final Object element : data) {
                try {
                    state.pushActiveContextObject(new TypedValue(element));
                    state.enterScope("index", idx);
                    final Object o = selectionCriteria.getValueInternal(state).getValue();
                    if (!(o instanceof Boolean)) {
                        throw new SpelEvaluationException(selectionCriteria.getStartPosition(), SpelMessage.RESULT_OF_SELECTION_CRITERIA_IS_NOT_BOOLEAN, new Object[0]);
                    }
                    if (o) {
                        if (this.variant == 1) {
                            return new ValueRef.TypedValueHolderValueRef(new TypedValue(element), this);
                        }
                        result2.add(element);
                    }
                    ++idx;
                }
                finally {
                    state.exitScope();
                    state.popActiveContextObject();
                }
            }
            if ((this.variant == 1 || this.variant == 2) && result2.size() == 0) {
                return ValueRef.NullValueRef.instance;
            }
            if (this.variant == 2) {
                return new ValueRef.TypedValueHolderValueRef(new TypedValue(result2.get(result2.size() - 1)), this);
            }
            if (operand instanceof Collection) {
                return new ValueRef.TypedValueHolderValueRef(new TypedValue(result2), this);
            }
            final Class<?> elementType = ClassUtils.resolvePrimitiveIfNecessary(op.getTypeDescriptor().getElementTypeDescriptor().getType());
            final Object resultArray = Array.newInstance(elementType, result2.size());
            System.arraycopy(result2.toArray(), 0, resultArray, 0, result2.size());
            return new ValueRef.TypedValueHolderValueRef(new TypedValue(resultArray), this);
        }
        else {
            if (operand != null) {
                throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.INVALID_TYPE_FOR_SELECTION, new Object[] { operand.getClass().getName() });
            }
            if (this.nullSafe) {
                return ValueRef.NullValueRef.instance;
            }
            throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.INVALID_TYPE_FOR_SELECTION, new Object[] { "null" });
        }
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder sb = new StringBuilder();
        switch (this.variant) {
            case 0: {
                sb.append("?[");
                break;
            }
            case 1: {
                sb.append("^[");
                break;
            }
            case 2: {
                sb.append("$[");
                break;
            }
        }
        return sb.append(this.getChild(0).toStringAST()).append("]").toString();
    }
}
