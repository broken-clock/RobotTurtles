// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.comparator;

import org.springframework.util.Assert;
import java.io.Serializable;
import java.util.Comparator;

public class InvertibleComparator<T> implements Comparator<T>, Serializable
{
    private final Comparator<T> comparator;
    private boolean ascending;
    
    public InvertibleComparator(final Comparator<T> comparator) {
        this.ascending = true;
        Assert.notNull(comparator, "Comparator must not be null");
        this.comparator = comparator;
    }
    
    public InvertibleComparator(final Comparator<T> comparator, final boolean ascending) {
        this.ascending = true;
        Assert.notNull(comparator, "Comparator must not be null");
        this.comparator = comparator;
        this.setAscending(ascending);
    }
    
    public void setAscending(final boolean ascending) {
        this.ascending = ascending;
    }
    
    public boolean isAscending() {
        return this.ascending;
    }
    
    public void invertOrder() {
        this.ascending = !this.ascending;
    }
    
    @Override
    public int compare(final T o1, final T o2) {
        int result = this.comparator.compare(o1, o2);
        if (result != 0) {
            if (!this.ascending) {
                if (Integer.MIN_VALUE == result) {
                    result = Integer.MAX_VALUE;
                }
                else {
                    result *= -1;
                }
            }
            return result;
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InvertibleComparator)) {
            return false;
        }
        final InvertibleComparator<T> other = (InvertibleComparator<T>)obj;
        return this.comparator.equals(other.comparator) && this.ascending == other.ascending;
    }
    
    @Override
    public int hashCode() {
        return this.comparator.hashCode();
    }
    
    @Override
    public String toString() {
        return "InvertibleComparator: [" + this.comparator + "]; ascending=" + this.ascending;
    }
}
