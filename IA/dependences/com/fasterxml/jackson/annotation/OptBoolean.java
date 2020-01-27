// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.annotation;

public enum OptBoolean
{
    TRUE, 
    FALSE, 
    DEFAULT;
    
    public Boolean asBoolean() {
        if (this == OptBoolean.DEFAULT) {
            return null;
        }
        return (this == OptBoolean.TRUE) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public boolean asPrimitive() {
        return this == OptBoolean.TRUE;
    }
    
    public static OptBoolean fromBoolean(final Boolean b) {
        if (b == null) {
            return OptBoolean.DEFAULT;
        }
        return b ? OptBoolean.TRUE : OptBoolean.FALSE;
    }
    
    public static boolean equals(final Boolean b1, final Boolean b2) {
        if (b1 == null) {
            return b2 == null;
        }
        return b1.equals(b2);
    }
}
