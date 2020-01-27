// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.TypeConverter;
import java.lang.reflect.Array;
import org.springframework.expression.common.ExpressionUtils;
import java.util.Iterator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ConstructorResolver;
import java.util.List;
import org.springframework.expression.AccessException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import java.lang.reflect.InvocationTargetException;
import org.springframework.core.convert.TypeDescriptor;
import java.util.ArrayList;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.ConstructorExecutor;

public class ConstructorReference extends SpelNodeImpl
{
    private boolean isArrayConstructor;
    private SpelNodeImpl[] dimensions;
    private volatile ConstructorExecutor cachedExecutor;
    
    public ConstructorReference(final int pos, final SpelNodeImpl... arguments) {
        super(pos, arguments);
        this.isArrayConstructor = false;
        this.isArrayConstructor = false;
    }
    
    public ConstructorReference(final int pos, final SpelNodeImpl[] dimensions, final SpelNodeImpl... arguments) {
        super(pos, arguments);
        this.isArrayConstructor = false;
        this.isArrayConstructor = true;
        this.dimensions = dimensions;
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        if (this.isArrayConstructor) {
            return this.createArray(state);
        }
        return this.createNewInstance(state);
    }
    
    private TypedValue createNewInstance(final ExpressionState state) throws EvaluationException {
        final Object[] arguments = new Object[this.getChildCount() - 1];
        final List<TypeDescriptor> argumentTypes = new ArrayList<TypeDescriptor>(this.getChildCount() - 1);
        for (int i = 0; i < arguments.length; ++i) {
            final TypedValue childValue = this.children[i + 1].getValueInternal(state);
            final Object value = childValue.getValue();
            arguments[i] = value;
            argumentTypes.add(TypeDescriptor.forObject(value));
        }
        ConstructorExecutor executorToUse = this.cachedExecutor;
        if (executorToUse != null) {
            try {
                return executorToUse.execute(state.getEvaluationContext(), arguments);
            }
            catch (AccessException ae) {
                if (ae.getCause() instanceof InvocationTargetException) {
                    final Throwable rootCause = ae.getCause().getCause();
                    if (rootCause instanceof RuntimeException) {
                        throw (RuntimeException)rootCause;
                    }
                    final String typename = (String)this.children[0].getValueInternal(state).getValue();
                    throw new SpelEvaluationException(this.getStartPosition(), rootCause, SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM, new Object[] { typename, FormatHelper.formatMethodForMessage("", argumentTypes) });
                }
                else {
                    this.cachedExecutor = null;
                }
            }
        }
        final String typename2 = (String)this.children[0].getValueInternal(state).getValue();
        executorToUse = this.findExecutorForConstructor(typename2, argumentTypes, state);
        try {
            this.cachedExecutor = executorToUse;
            return executorToUse.execute(state.getEvaluationContext(), arguments);
        }
        catch (AccessException ae2) {
            throw new SpelEvaluationException(this.getStartPosition(), ae2, SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM, new Object[] { typename2, FormatHelper.formatMethodForMessage("", argumentTypes) });
        }
    }
    
    private ConstructorExecutor findExecutorForConstructor(final String typename, final List<TypeDescriptor> argumentTypes, final ExpressionState state) throws SpelEvaluationException {
        final EvaluationContext eContext = state.getEvaluationContext();
        final List<ConstructorResolver> cResolvers = eContext.getConstructorResolvers();
        if (cResolvers != null) {
            for (final ConstructorResolver ctorResolver : cResolvers) {
                try {
                    final ConstructorExecutor cEx = ctorResolver.resolve(state.getEvaluationContext(), typename, argumentTypes);
                    if (cEx != null) {
                        return cEx;
                    }
                    continue;
                }
                catch (AccessException ex) {
                    throw new SpelEvaluationException(this.getStartPosition(), ex, SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM, new Object[] { typename, FormatHelper.formatMethodForMessage("", argumentTypes) });
                }
            }
        }
        throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.CONSTRUCTOR_NOT_FOUND, new Object[] { typename, FormatHelper.formatMethodForMessage("", argumentTypes) });
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder sb = new StringBuilder();
        sb.append("new ");
        int index = 0;
        sb.append(this.getChild(index++).toStringAST());
        sb.append("(");
        for (int i = index; i < this.getChildCount(); ++i) {
            if (i > index) {
                sb.append(",");
            }
            sb.append(this.getChild(i).toStringAST());
        }
        sb.append(")");
        return sb.toString();
    }
    
    private TypedValue createArray(final ExpressionState state) throws EvaluationException {
        final Object intendedArrayType = this.getChild(0).getValue(state);
        if (!(intendedArrayType instanceof String)) {
            throw new SpelEvaluationException(this.getChild(0).getStartPosition(), SpelMessage.TYPE_NAME_EXPECTED_FOR_ARRAY_CONSTRUCTION, new Object[] { FormatHelper.formatClassNameForMessage(intendedArrayType.getClass()) });
        }
        final String type = (String)intendedArrayType;
        final TypeCode arrayTypeCode = TypeCode.forName(type);
        Class<?> componentType;
        if (arrayTypeCode == TypeCode.OBJECT) {
            componentType = state.findType(type);
        }
        else {
            componentType = arrayTypeCode.getType();
        }
        Object newArray;
        if (!this.hasInitializer()) {
            for (final SpelNodeImpl dimension : this.dimensions) {
                if (dimension == null) {
                    throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.MISSING_ARRAY_DIMENSION, new Object[0]);
                }
            }
            final TypeConverter typeConverter = state.getEvaluationContext().getTypeConverter();
            if (this.dimensions.length == 1) {
                final TypedValue o = this.dimensions[0].getTypedValue(state);
                final int arraySize = ExpressionUtils.toInt(typeConverter, o);
                newArray = Array.newInstance(componentType, arraySize);
            }
            else {
                final int[] dims = new int[this.dimensions.length];
                for (int d = 0; d < this.dimensions.length; ++d) {
                    final TypedValue o2 = this.dimensions[d].getTypedValue(state);
                    dims[d] = ExpressionUtils.toInt(typeConverter, o2);
                }
                newArray = Array.newInstance(componentType, dims);
            }
        }
        else {
            if (this.dimensions.length > 1) {
                throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.MULTIDIM_ARRAY_INITIALIZER_NOT_SUPPORTED, new Object[0]);
            }
            final TypeConverter typeConverter = state.getEvaluationContext().getTypeConverter();
            final InlineList initializer = (InlineList)this.getChild(1);
            if (this.dimensions[0] != null) {
                final TypedValue dValue = this.dimensions[0].getTypedValue(state);
                final int i = ExpressionUtils.toInt(typeConverter, dValue);
                if (i != initializer.getChildCount()) {
                    throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.INITIALIZER_LENGTH_INCORRECT, new Object[0]);
                }
            }
            final int arraySize = initializer.getChildCount();
            newArray = Array.newInstance(componentType, arraySize);
            if (arrayTypeCode == TypeCode.OBJECT) {
                this.populateReferenceTypeArray(state, newArray, typeConverter, initializer, componentType);
            }
            else if (arrayTypeCode == TypeCode.INT) {
                this.populateIntArray(state, newArray, typeConverter, initializer);
            }
            else if (arrayTypeCode == TypeCode.BOOLEAN) {
                this.populateBooleanArray(state, newArray, typeConverter, initializer);
            }
            else if (arrayTypeCode == TypeCode.CHAR) {
                this.populateCharArray(state, newArray, typeConverter, initializer);
            }
            else if (arrayTypeCode == TypeCode.LONG) {
                this.populateLongArray(state, newArray, typeConverter, initializer);
            }
            else if (arrayTypeCode == TypeCode.SHORT) {
                this.populateShortArray(state, newArray, typeConverter, initializer);
            }
            else if (arrayTypeCode == TypeCode.DOUBLE) {
                this.populateDoubleArray(state, newArray, typeConverter, initializer);
            }
            else if (arrayTypeCode == TypeCode.FLOAT) {
                this.populateFloatArray(state, newArray, typeConverter, initializer);
            }
            else {
                if (arrayTypeCode != TypeCode.BYTE) {
                    throw new IllegalStateException(arrayTypeCode.name());
                }
                this.populateByteArray(state, newArray, typeConverter, initializer);
            }
        }
        return new TypedValue(newArray);
    }
    
    private void populateReferenceTypeArray(final ExpressionState state, final Object newArray, final TypeConverter typeConverter, final InlineList initializer, final Class<?> componentType) {
        final TypeDescriptor toTypeDescriptor = TypeDescriptor.valueOf(componentType);
        final Object[] newObjectArray = (Object[])newArray;
        for (int i = 0; i < newObjectArray.length; ++i) {
            final SpelNode elementNode = initializer.getChild(i);
            final Object arrayEntry = elementNode.getValue(state);
            newObjectArray[i] = typeConverter.convertValue(arrayEntry, TypeDescriptor.forObject(arrayEntry), toTypeDescriptor);
        }
    }
    
    private void populateByteArray(final ExpressionState state, final Object newArray, final TypeConverter typeConverter, final InlineList initializer) {
        final byte[] newByteArray = (byte[])newArray;
        for (int i = 0; i < newByteArray.length; ++i) {
            final TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
            newByteArray[i] = ExpressionUtils.toByte(typeConverter, typedValue);
        }
    }
    
    private void populateFloatArray(final ExpressionState state, final Object newArray, final TypeConverter typeConverter, final InlineList initializer) {
        final float[] newFloatArray = (float[])newArray;
        for (int i = 0; i < newFloatArray.length; ++i) {
            final TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
            newFloatArray[i] = ExpressionUtils.toFloat(typeConverter, typedValue);
        }
    }
    
    private void populateDoubleArray(final ExpressionState state, final Object newArray, final TypeConverter typeConverter, final InlineList initializer) {
        final double[] newDoubleArray = (double[])newArray;
        for (int i = 0; i < newDoubleArray.length; ++i) {
            final TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
            newDoubleArray[i] = ExpressionUtils.toDouble(typeConverter, typedValue);
        }
    }
    
    private void populateShortArray(final ExpressionState state, final Object newArray, final TypeConverter typeConverter, final InlineList initializer) {
        final short[] newShortArray = (short[])newArray;
        for (int i = 0; i < newShortArray.length; ++i) {
            final TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
            newShortArray[i] = ExpressionUtils.toShort(typeConverter, typedValue);
        }
    }
    
    private void populateLongArray(final ExpressionState state, final Object newArray, final TypeConverter typeConverter, final InlineList initializer) {
        final long[] newLongArray = (long[])newArray;
        for (int i = 0; i < newLongArray.length; ++i) {
            final TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
            newLongArray[i] = ExpressionUtils.toLong(typeConverter, typedValue);
        }
    }
    
    private void populateCharArray(final ExpressionState state, final Object newArray, final TypeConverter typeConverter, final InlineList initializer) {
        final char[] newCharArray = (char[])newArray;
        for (int i = 0; i < newCharArray.length; ++i) {
            final TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
            newCharArray[i] = ExpressionUtils.toChar(typeConverter, typedValue);
        }
    }
    
    private void populateBooleanArray(final ExpressionState state, final Object newArray, final TypeConverter typeConverter, final InlineList initializer) {
        final boolean[] newBooleanArray = (boolean[])newArray;
        for (int i = 0; i < newBooleanArray.length; ++i) {
            final TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
            newBooleanArray[i] = ExpressionUtils.toBoolean(typeConverter, typedValue);
        }
    }
    
    private void populateIntArray(final ExpressionState state, final Object newArray, final TypeConverter typeConverter, final InlineList initializer) {
        final int[] newIntArray = (int[])newArray;
        for (int i = 0; i < newIntArray.length; ++i) {
            final TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
            newIntArray[i] = ExpressionUtils.toInt(typeConverter, typedValue);
        }
    }
    
    private boolean hasInitializer() {
        return this.getChildCount() > 1;
    }
}
