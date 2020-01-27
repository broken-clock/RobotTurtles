// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.support;

import org.springframework.expression.TypedValue;

public class BooleanTypedValue extends TypedValue
{
    public static final BooleanTypedValue TRUE;
    public static final BooleanTypedValue FALSE;
    
    private BooleanTypedValue(final boolean b) {
        super(b);
    }
    
    public static BooleanTypedValue forValue(final boolean b) {
        return b ? BooleanTypedValue.TRUE : BooleanTypedValue.FALSE;
    }
    
    static {
        TRUE = new BooleanTypedValue(true);
        FALSE = new BooleanTypedValue(false);
    }
}
