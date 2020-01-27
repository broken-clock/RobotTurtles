// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.common;

import org.springframework.expression.TypeConverter;
import org.springframework.util.ClassUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationContext;

public abstract class ExpressionUtils
{
    @Deprecated
    public static <T> T convert(final EvaluationContext context, final Object value, final Class<T> targetType) throws EvaluationException {
        return convertTypedValue(context, new TypedValue(value), targetType);
    }
    
    public static <T> T convertTypedValue(final EvaluationContext context, final TypedValue typedValue, final Class<T> targetType) {
        final Object value = typedValue.getValue();
        if (targetType == null) {
            return (T)value;
        }
        if (context != null) {
            return (T)context.getTypeConverter().convertValue(value, typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(targetType));
        }
        if (ClassUtils.isAssignableValue(targetType, value)) {
            return (T)value;
        }
        throw new EvaluationException("Cannot convert value '" + value + "' to type '" + targetType.getName() + "'");
    }
    
    public static int toInt(final TypeConverter typeConverter, final TypedValue typedValue) {
        return (int)typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Integer.class));
    }
    
    public static boolean toBoolean(final TypeConverter typeConverter, final TypedValue typedValue) {
        return (boolean)typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Boolean.class));
    }
    
    public static double toDouble(final TypeConverter typeConverter, final TypedValue typedValue) {
        return (double)typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Double.class));
    }
    
    public static long toLong(final TypeConverter typeConverter, final TypedValue typedValue) {
        return (long)typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Long.class));
    }
    
    public static char toChar(final TypeConverter typeConverter, final TypedValue typedValue) {
        return (char)typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Character.class));
    }
    
    public static short toShort(final TypeConverter typeConverter, final TypedValue typedValue) {
        return (short)typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Short.class));
    }
    
    public static float toFloat(final TypeConverter typeConverter, final TypedValue typedValue) {
        return (float)typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Float.class));
    }
    
    public static byte toByte(final TypeConverter typeConverter, final TypedValue typedValue) {
        return (byte)typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Byte.class));
    }
}
