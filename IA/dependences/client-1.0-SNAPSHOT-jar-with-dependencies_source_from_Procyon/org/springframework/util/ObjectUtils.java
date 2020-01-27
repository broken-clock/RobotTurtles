// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.Arrays;
import java.lang.reflect.Array;

public abstract class ObjectUtils
{
    private static final int INITIAL_HASH = 7;
    private static final int MULTIPLIER = 31;
    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = "null";
    private static final String ARRAY_START = "{";
    private static final String ARRAY_END = "}";
    private static final String EMPTY_ARRAY = "{}";
    private static final String ARRAY_ELEMENT_SEPARATOR = ", ";
    
    public static boolean isCheckedException(final Throwable ex) {
        return !(ex instanceof RuntimeException) && !(ex instanceof Error);
    }
    
    public static boolean isCompatibleWithThrowsClause(final Throwable ex, final Class<?>... declaredExceptions) {
        if (!isCheckedException(ex)) {
            return true;
        }
        if (declaredExceptions != null) {
            for (final Class<?> declaredException : declaredExceptions) {
                if (declaredException.isInstance(ex)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isArray(final Object obj) {
        return obj != null && obj.getClass().isArray();
    }
    
    public static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }
    
    public static boolean containsElement(final Object[] array, final Object element) {
        if (array == null) {
            return false;
        }
        for (final Object arrayEle : array) {
            if (nullSafeEquals(arrayEle, element)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean containsConstant(final Enum<?>[] enumValues, final String constant) {
        return containsConstant(enumValues, constant, false);
    }
    
    public static boolean containsConstant(final Enum<?>[] enumValues, final String constant, final boolean caseSensitive) {
        for (final Enum<?> candidate : enumValues) {
            if (caseSensitive) {
                if (candidate.toString().equals(constant)) {
                    return true;
                }
            }
            else if (candidate.toString().equalsIgnoreCase(constant)) {
                return true;
            }
        }
        return false;
    }
    
    public static <E extends Enum<?>> E caseInsensitiveValueOf(final E[] enumValues, final String constant) {
        for (final E candidate : enumValues) {
            if (candidate.toString().equalsIgnoreCase(constant)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException(String.format("constant [%s] does not exist in enum type %s", constant, enumValues.getClass().getComponentType().getName()));
    }
    
    public static <A, O extends A> A[] addObjectToArray(final A[] array, final O obj) {
        Class<?> compType = Object.class;
        if (array != null) {
            compType = array.getClass().getComponentType();
        }
        else if (obj != null) {
            compType = obj.getClass();
        }
        final int newArrLength = (array != null) ? (array.length + 1) : 1;
        final A[] newArr = (A[])Array.newInstance(compType, newArrLength);
        if (array != null) {
            System.arraycopy(array, 0, newArr, 0, array.length);
        }
        newArr[newArr.length - 1] = obj;
        return newArr;
    }
    
    public static Object[] toObjectArray(final Object source) {
        if (source instanceof Object[]) {
            return (Object[])source;
        }
        if (source == null) {
            return new Object[0];
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        }
        final int length = Array.getLength(source);
        if (length == 0) {
            return new Object[0];
        }
        final Class<?> wrapperType = Array.get(source, 0).getClass();
        final Object[] newArray = (Object[])Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; ++i) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }
    
    public static boolean nullSafeEquals(final Object o1, final Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            if (o1 instanceof Object[] && o2 instanceof Object[]) {
                return Arrays.equals((Object[])o1, (Object[])o2);
            }
            if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
                return Arrays.equals((boolean[])o1, (boolean[])o2);
            }
            if (o1 instanceof byte[] && o2 instanceof byte[]) {
                return Arrays.equals((byte[])o1, (byte[])o2);
            }
            if (o1 instanceof char[] && o2 instanceof char[]) {
                return Arrays.equals((char[])o1, (char[])o2);
            }
            if (o1 instanceof double[] && o2 instanceof double[]) {
                return Arrays.equals((double[])o1, (double[])o2);
            }
            if (o1 instanceof float[] && o2 instanceof float[]) {
                return Arrays.equals((float[])o1, (float[])o2);
            }
            if (o1 instanceof int[] && o2 instanceof int[]) {
                return Arrays.equals((int[])o1, (int[])o2);
            }
            if (o1 instanceof long[] && o2 instanceof long[]) {
                return Arrays.equals((long[])o1, (long[])o2);
            }
            if (o1 instanceof short[] && o2 instanceof short[]) {
                return Arrays.equals((short[])o1, (short[])o2);
            }
        }
        return false;
    }
    
    public static int nullSafeHashCode(final Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj.getClass().isArray()) {
            if (obj instanceof Object[]) {
                return nullSafeHashCode((Object[])obj);
            }
            if (obj instanceof boolean[]) {
                return nullSafeHashCode((boolean[])obj);
            }
            if (obj instanceof byte[]) {
                return nullSafeHashCode((byte[])obj);
            }
            if (obj instanceof char[]) {
                return nullSafeHashCode((char[])obj);
            }
            if (obj instanceof double[]) {
                return nullSafeHashCode((double[])obj);
            }
            if (obj instanceof float[]) {
                return nullSafeHashCode((float[])obj);
            }
            if (obj instanceof int[]) {
                return nullSafeHashCode((int[])obj);
            }
            if (obj instanceof long[]) {
                return nullSafeHashCode((long[])obj);
            }
            if (obj instanceof short[]) {
                return nullSafeHashCode((short[])obj);
            }
        }
        return obj.hashCode();
    }
    
    public static int nullSafeHashCode(final Object[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        for (final Object element : array) {
            hash = 31 * hash + nullSafeHashCode(element);
        }
        return hash;
    }
    
    public static int nullSafeHashCode(final boolean[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        for (final boolean element : array) {
            hash = 31 * hash + hashCode(element);
        }
        return hash;
    }
    
    public static int nullSafeHashCode(final byte[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        for (final byte element : array) {
            hash = 31 * hash + element;
        }
        return hash;
    }
    
    public static int nullSafeHashCode(final char[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        for (final char element : array) {
            hash = 31 * hash + element;
        }
        return hash;
    }
    
    public static int nullSafeHashCode(final double[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        for (final double element : array) {
            hash = 31 * hash + hashCode(element);
        }
        return hash;
    }
    
    public static int nullSafeHashCode(final float[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        for (final float element : array) {
            hash = 31 * hash + hashCode(element);
        }
        return hash;
    }
    
    public static int nullSafeHashCode(final int[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        for (final int element : array) {
            hash = 31 * hash + element;
        }
        return hash;
    }
    
    public static int nullSafeHashCode(final long[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        for (final long element : array) {
            hash = 31 * hash + hashCode(element);
        }
        return hash;
    }
    
    public static int nullSafeHashCode(final short[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        for (final short element : array) {
            hash = 31 * hash + element;
        }
        return hash;
    }
    
    public static int hashCode(final boolean bool) {
        return bool ? 1231 : 1237;
    }
    
    public static int hashCode(final double dbl) {
        return hashCode(Double.doubleToLongBits(dbl));
    }
    
    public static int hashCode(final float flt) {
        return Float.floatToIntBits(flt);
    }
    
    public static int hashCode(final long lng) {
        return (int)(lng ^ lng >>> 32);
    }
    
    public static String identityToString(final Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.getClass().getName() + "@" + getIdentityHexString(obj);
    }
    
    public static String getIdentityHexString(final Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }
    
    public static String getDisplayString(final Object obj) {
        if (obj == null) {
            return "";
        }
        return nullSafeToString(obj);
    }
    
    public static String nullSafeClassName(final Object obj) {
        return (obj != null) ? obj.getClass().getName() : "null";
    }
    
    public static String nullSafeToString(final Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return (String)obj;
        }
        if (obj instanceof Object[]) {
            return nullSafeToString((Object[])obj);
        }
        if (obj instanceof boolean[]) {
            return nullSafeToString((boolean[])obj);
        }
        if (obj instanceof byte[]) {
            return nullSafeToString((byte[])obj);
        }
        if (obj instanceof char[]) {
            return nullSafeToString((char[])obj);
        }
        if (obj instanceof double[]) {
            return nullSafeToString((double[])obj);
        }
        if (obj instanceof float[]) {
            return nullSafeToString((float[])obj);
        }
        if (obj instanceof int[]) {
            return nullSafeToString((int[])obj);
        }
        if (obj instanceof long[]) {
            return nullSafeToString((long[])obj);
        }
        if (obj instanceof short[]) {
            return nullSafeToString((short[])obj);
        }
        final String str = obj.toString();
        return (str != null) ? str : "";
    }
    
    public static String nullSafeToString(final Object[] array) {
        if (array == null) {
            return "null";
        }
        final int length = array.length;
        if (length == 0) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            if (i == 0) {
                sb.append("{");
            }
            else {
                sb.append(", ");
            }
            sb.append(String.valueOf(array[i]));
        }
        sb.append("}");
        return sb.toString();
    }
    
    public static String nullSafeToString(final boolean[] array) {
        if (array == null) {
            return "null";
        }
        final int length = array.length;
        if (length == 0) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            if (i == 0) {
                sb.append("{");
            }
            else {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        sb.append("}");
        return sb.toString();
    }
    
    public static String nullSafeToString(final byte[] array) {
        if (array == null) {
            return "null";
        }
        final int length = array.length;
        if (length == 0) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            if (i == 0) {
                sb.append("{");
            }
            else {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        sb.append("}");
        return sb.toString();
    }
    
    public static String nullSafeToString(final char[] array) {
        if (array == null) {
            return "null";
        }
        final int length = array.length;
        if (length == 0) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            if (i == 0) {
                sb.append("{");
            }
            else {
                sb.append(", ");
            }
            sb.append("'").append(array[i]).append("'");
        }
        sb.append("}");
        return sb.toString();
    }
    
    public static String nullSafeToString(final double[] array) {
        if (array == null) {
            return "null";
        }
        final int length = array.length;
        if (length == 0) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            if (i == 0) {
                sb.append("{");
            }
            else {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        sb.append("}");
        return sb.toString();
    }
    
    public static String nullSafeToString(final float[] array) {
        if (array == null) {
            return "null";
        }
        final int length = array.length;
        if (length == 0) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            if (i == 0) {
                sb.append("{");
            }
            else {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        sb.append("}");
        return sb.toString();
    }
    
    public static String nullSafeToString(final int[] array) {
        if (array == null) {
            return "null";
        }
        final int length = array.length;
        if (length == 0) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            if (i == 0) {
                sb.append("{");
            }
            else {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        sb.append("}");
        return sb.toString();
    }
    
    public static String nullSafeToString(final long[] array) {
        if (array == null) {
            return "null";
        }
        final int length = array.length;
        if (length == 0) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            if (i == 0) {
                sb.append("{");
            }
            else {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        sb.append("}");
        return sb.toString();
    }
    
    public static String nullSafeToString(final short[] array) {
        if (array == null) {
            return "null";
        }
        final int length = array.length;
        if (length == 0) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            if (i == 0) {
                sb.append("{");
            }
            else {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        sb.append("}");
        return sb.toString();
    }
}
