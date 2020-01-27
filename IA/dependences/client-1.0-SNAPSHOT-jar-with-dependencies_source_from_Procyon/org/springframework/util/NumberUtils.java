// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.text.ParseException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class NumberUtils
{
    public static <T extends Number> T convertNumberToTargetClass(final Number number, final Class<T> targetClass) throws IllegalArgumentException {
        Assert.notNull(number, "Number must not be null");
        Assert.notNull(targetClass, "Target class must not be null");
        if (targetClass.isInstance(number)) {
            return (T)number;
        }
        if (targetClass.equals(Byte.class)) {
            final long value = number.longValue();
            if (value < -128L || value > 127L) {
                raiseOverflowException(number, targetClass);
            }
            return (T)new Byte(number.byteValue());
        }
        if (targetClass.equals(Short.class)) {
            final long value = number.longValue();
            if (value < -32768L || value > 32767L) {
                raiseOverflowException(number, targetClass);
            }
            return (T)new Short(number.shortValue());
        }
        if (targetClass.equals(Integer.class)) {
            final long value = number.longValue();
            if (value < -2147483648L || value > 2147483647L) {
                raiseOverflowException(number, targetClass);
            }
            return (T)new Integer(number.intValue());
        }
        if (targetClass.equals(Long.class)) {
            return (T)new Long(number.longValue());
        }
        if (targetClass.equals(BigInteger.class)) {
            if (number instanceof BigDecimal) {
                return (T)((BigDecimal)number).toBigInteger();
            }
            return (T)BigInteger.valueOf(number.longValue());
        }
        else {
            if (targetClass.equals(Float.class)) {
                return (T)new Float(number.floatValue());
            }
            if (targetClass.equals(Double.class)) {
                return (T)new Double(number.doubleValue());
            }
            if (targetClass.equals(BigDecimal.class)) {
                return (T)new BigDecimal(number.toString());
            }
            throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to unknown target class [" + targetClass.getName() + "]");
        }
    }
    
    private static void raiseOverflowException(final Number number, final Class<?> targetClass) {
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
    }
    
    public static <T extends Number> T parseNumber(final String text, final Class<T> targetClass) {
        Assert.notNull(text, "Text must not be null");
        Assert.notNull(targetClass, "Target class must not be null");
        final String trimmed = StringUtils.trimAllWhitespace(text);
        if (targetClass.equals(Byte.class)) {
            return (T)(isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
        }
        if (targetClass.equals(Short.class)) {
            return (T)(isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
        }
        if (targetClass.equals(Integer.class)) {
            return (T)(isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));
        }
        if (targetClass.equals(Long.class)) {
            return (T)(isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed));
        }
        if (targetClass.equals(BigInteger.class)) {
            return (T)(isHexNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed));
        }
        if (targetClass.equals(Float.class)) {
            return (T)Float.valueOf(trimmed);
        }
        if (targetClass.equals(Double.class)) {
            return (T)Double.valueOf(trimmed);
        }
        if (targetClass.equals(BigDecimal.class) || targetClass.equals(Number.class)) {
            return (T)new BigDecimal(trimmed);
        }
        throw new IllegalArgumentException("Cannot convert String [" + text + "] to target class [" + targetClass.getName() + "]");
    }
    
    public static <T extends Number> T parseNumber(final String text, final Class<T> targetClass, final NumberFormat numberFormat) {
        if (numberFormat != null) {
            Assert.notNull(text, "Text must not be null");
            Assert.notNull(targetClass, "Target class must not be null");
            DecimalFormat decimalFormat = null;
            boolean resetBigDecimal = false;
            if (numberFormat instanceof DecimalFormat) {
                decimalFormat = (DecimalFormat)numberFormat;
                if (BigDecimal.class.equals(targetClass) && !decimalFormat.isParseBigDecimal()) {
                    decimalFormat.setParseBigDecimal(true);
                    resetBigDecimal = true;
                }
            }
            try {
                final Number number = numberFormat.parse(StringUtils.trimAllWhitespace(text));
                return convertNumberToTargetClass(number, targetClass);
            }
            catch (ParseException ex) {
                throw new IllegalArgumentException("Could not parse number: " + ex.getMessage());
            }
            finally {
                if (resetBigDecimal) {
                    decimalFormat.setParseBigDecimal(false);
                }
            }
        }
        return parseNumber(text, targetClass);
    }
    
    private static boolean isHexNumber(final String value) {
        final int index = value.startsWith("-") ? 1 : 0;
        return value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index);
    }
    
    private static BigInteger decodeBigInteger(final String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        if (value.startsWith("-")) {
            negative = true;
            ++index;
        }
        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        }
        else if (value.startsWith("#", index)) {
            ++index;
            radix = 16;
        }
        else if (value.startsWith("0", index) && value.length() > 1 + index) {
            ++index;
            radix = 8;
        }
        final BigInteger result = new BigInteger(value.substring(index), radix);
        return negative ? result.negate() : result;
    }
}
