// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.comparator;

import org.springframework.util.Assert;
import java.util.Comparator;

public class NullSafeComparator<T> implements Comparator<T>
{
    public static final NullSafeComparator NULLS_LOW;
    public static final NullSafeComparator NULLS_HIGH;
    private final Comparator<T> nonNullComparator;
    private final boolean nullsLow;
    
    private NullSafeComparator(final boolean nullsLow) {
        this.nonNullComparator = new ComparableComparator<T>();
        this.nullsLow = nullsLow;
    }
    
    public NullSafeComparator(final Comparator<T> comparator, final boolean nullsLow) {
        Assert.notNull(comparator, "The non-null comparator is required");
        this.nonNullComparator = comparator;
        this.nullsLow = nullsLow;
    }
    
    @Override
    public int compare(final T o1, final T o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return this.nullsLow ? -1 : 1;
        }
        if (o2 == null) {
            return this.nullsLow ? 1 : -1;
        }
        return this.nonNullComparator.compare(o1, o2);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NullSafeComparator)) {
            return false;
        }
        final NullSafeComparator<T> other = (NullSafeComparator<T>)obj;
        return this.nonNullComparator.equals(other.nonNullComparator) && this.nullsLow == other.nullsLow;
    }
    
    @Override
    public int hashCode() {
        return (this.nullsLow ? -1 : 1) * this.nonNullComparator.hashCode();
    }
    
    @Override
    public String toString() {
        return "NullSafeComparator: non-null comparator [" + this.nonNullComparator + "]; " + (this.nullsLow ? "nulls low" : "nulls high");
    }
    
    static {
        NULLS_LOW = new NullSafeComparator(true);
        NULLS_HIGH = new NullSafeComparator(false);
    }
}
