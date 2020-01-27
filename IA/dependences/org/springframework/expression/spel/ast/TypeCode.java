// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

public enum TypeCode
{
    OBJECT((Class<?>)Object.class), 
    BOOLEAN((Class<?>)Boolean.TYPE), 
    BYTE((Class<?>)Byte.TYPE), 
    CHAR((Class<?>)Character.TYPE), 
    SHORT((Class<?>)Short.TYPE), 
    INT((Class<?>)Integer.TYPE), 
    LONG((Class<?>)Long.TYPE), 
    FLOAT((Class<?>)Float.TYPE), 
    DOUBLE((Class<?>)Double.TYPE);
    
    private Class<?> type;
    
    private TypeCode(final Class<?> type) {
        this.type = type;
    }
    
    public Class<?> getType() {
        return this.type;
    }
    
    public static TypeCode forName(final String name) {
        final String searchingFor = name.toUpperCase();
        final TypeCode[] tcs = values();
        for (int i = 1; i < tcs.length; ++i) {
            if (tcs[i].name().equals(searchingFor)) {
                return tcs[i];
            }
        }
        return TypeCode.OBJECT;
    }
    
    public static TypeCode forClass(final Class<?> c) {
        final TypeCode[] allValues = values();
        for (int i = 0; i < allValues.length; ++i) {
            final TypeCode typeCode = allValues[i];
            if (c == typeCode.getType()) {
                return typeCode;
            }
        }
        return TypeCode.OBJECT;
    }
}
