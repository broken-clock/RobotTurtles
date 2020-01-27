// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

public final class BooleanComparator implements Comparator<Boolean>, Serializable
{
    public static final BooleanComparator TRUE_LOW;
    public static final BooleanComparator TRUE_HIGH;
    private final boolean trueLow;
    
    public BooleanComparator(final boolean trueLow) {
        this.trueLow = trueLow;
    }
    
    @Override
    public int compare(final Boolean v1, final Boolean v2) {
        return (v1 ^ v2) ? ((v1 ^ this.trueLow) ? 1 : -1) : 0;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof BooleanComparator && this.trueLow == ((BooleanComparator)obj).trueLow);
    }
    
    @Override
    public int hashCode() {
        return (this.trueLow ? -1 : 1) * this.getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "BooleanComparator: " + (this.trueLow ? "true low" : "true high");
    }
    
    static {
        TRUE_LOW = new BooleanComparator(true);
        TRUE_HIGH = new BooleanComparator(false);
    }
}
