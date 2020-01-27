// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.util.HashSet;
import java.lang.reflect.Array;

public final class ArrayBuilders
{
    private BooleanBuilder _booleanBuilder;
    private ByteBuilder _byteBuilder;
    private ShortBuilder _shortBuilder;
    private IntBuilder _intBuilder;
    private LongBuilder _longBuilder;
    private FloatBuilder _floatBuilder;
    private DoubleBuilder _doubleBuilder;
    
    public ArrayBuilders() {
        this._booleanBuilder = null;
        this._byteBuilder = null;
        this._shortBuilder = null;
        this._intBuilder = null;
        this._longBuilder = null;
        this._floatBuilder = null;
        this._doubleBuilder = null;
    }
    
    public BooleanBuilder getBooleanBuilder() {
        if (this._booleanBuilder == null) {
            this._booleanBuilder = new BooleanBuilder();
        }
        return this._booleanBuilder;
    }
    
    public ByteBuilder getByteBuilder() {
        if (this._byteBuilder == null) {
            this._byteBuilder = new ByteBuilder();
        }
        return this._byteBuilder;
    }
    
    public ShortBuilder getShortBuilder() {
        if (this._shortBuilder == null) {
            this._shortBuilder = new ShortBuilder();
        }
        return this._shortBuilder;
    }
    
    public IntBuilder getIntBuilder() {
        if (this._intBuilder == null) {
            this._intBuilder = new IntBuilder();
        }
        return this._intBuilder;
    }
    
    public LongBuilder getLongBuilder() {
        if (this._longBuilder == null) {
            this._longBuilder = new LongBuilder();
        }
        return this._longBuilder;
    }
    
    public FloatBuilder getFloatBuilder() {
        if (this._floatBuilder == null) {
            this._floatBuilder = new FloatBuilder();
        }
        return this._floatBuilder;
    }
    
    public DoubleBuilder getDoubleBuilder() {
        if (this._doubleBuilder == null) {
            this._doubleBuilder = new DoubleBuilder();
        }
        return this._doubleBuilder;
    }
    
    public static Object getArrayComparator(final Object defaultValue) {
        final int length = Array.getLength(defaultValue);
        final Class<?> defaultValueType = defaultValue.getClass();
        return new Object() {
            @Override
            public boolean equals(final Object other) {
                if (other == this) {
                    return true;
                }
                if (!ClassUtil.hasClass(other, defaultValueType)) {
                    return false;
                }
                if (Array.getLength(other) != length) {
                    return false;
                }
                for (int i = 0; i < length; ++i) {
                    final Object value1 = Array.get(defaultValue, i);
                    final Object value2 = Array.get(other, i);
                    if (value1 != value2) {
                        if (value1 != null && !value1.equals(value2)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        };
    }
    
    public static <T> HashSet<T> arrayToSet(final T[] elements) {
        if (elements != null) {
            final int len = elements.length;
            final HashSet<T> result = new HashSet<T>(len);
            for (int i = 0; i < len; ++i) {
                result.add(elements[i]);
            }
            return result;
        }
        return new HashSet<T>();
    }
    
    public static <T> T[] insertInListNoDup(final T[] array, final T element) {
        final int len = array.length;
        int ix = 0;
        while (ix < len) {
            if (array[ix] == element) {
                if (ix == 0) {
                    return array;
                }
                final T[] result = (T[])Array.newInstance(array.getClass().getComponentType(), len);
                System.arraycopy(array, 0, result, 1, ix);
                result[0] = element;
                ++ix;
                final int left = len - ix;
                if (left > 0) {
                    System.arraycopy(array, ix, result, ix, left);
                }
                return result;
            }
            else {
                ++ix;
            }
        }
        final T[] result2 = (T[])Array.newInstance(array.getClass().getComponentType(), len + 1);
        if (len > 0) {
            System.arraycopy(array, 0, result2, 1, len);
        }
        result2[0] = element;
        return result2;
    }
    
    public static final class BooleanBuilder extends PrimitiveArrayBuilder<boolean[]>
    {
        public final boolean[] _constructArray(final int len) {
            return new boolean[len];
        }
    }
    
    public static final class ByteBuilder extends PrimitiveArrayBuilder<byte[]>
    {
        public final byte[] _constructArray(final int len) {
            return new byte[len];
        }
    }
    
    public static final class ShortBuilder extends PrimitiveArrayBuilder<short[]>
    {
        public final short[] _constructArray(final int len) {
            return new short[len];
        }
    }
    
    public static final class IntBuilder extends PrimitiveArrayBuilder<int[]>
    {
        public final int[] _constructArray(final int len) {
            return new int[len];
        }
    }
    
    public static final class LongBuilder extends PrimitiveArrayBuilder<long[]>
    {
        public final long[] _constructArray(final int len) {
            return new long[len];
        }
    }
    
    public static final class FloatBuilder extends PrimitiveArrayBuilder<float[]>
    {
        public final float[] _constructArray(final int len) {
            return new float[len];
        }
    }
    
    public static final class DoubleBuilder extends PrimitiveArrayBuilder<double[]>
    {
        public final double[] _constructArray(final int len) {
            return new double[len];
        }
    }
}
