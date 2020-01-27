// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.comparator;

import java.util.Iterator;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Comparator;

public class CompoundComparator<T> implements Comparator<T>, Serializable
{
    private final List<InvertibleComparator> comparators;
    
    public CompoundComparator() {
        this.comparators = new ArrayList<InvertibleComparator>();
    }
    
    public CompoundComparator(final Comparator... comparators) {
        Assert.notNull(comparators, "Comparators must not be null");
        this.comparators = new ArrayList<InvertibleComparator>(comparators.length);
        for (final Comparator comparator : comparators) {
            this.addComparator(comparator);
        }
    }
    
    public void addComparator(final Comparator<? extends T> comparator) {
        if (comparator instanceof InvertibleComparator) {
            this.comparators.add((InvertibleComparator)comparator);
        }
        else {
            this.comparators.add(new InvertibleComparator(comparator));
        }
    }
    
    public void addComparator(final Comparator<? extends T> comparator, final boolean ascending) {
        this.comparators.add(new InvertibleComparator(comparator, ascending));
    }
    
    public void setComparator(final int index, final Comparator<? extends T> comparator) {
        if (comparator instanceof InvertibleComparator) {
            this.comparators.set(index, (InvertibleComparator)comparator);
        }
        else {
            this.comparators.set(index, new InvertibleComparator(comparator));
        }
    }
    
    public void setComparator(final int index, final Comparator<T> comparator, final boolean ascending) {
        this.comparators.set(index, new InvertibleComparator(comparator, ascending));
    }
    
    public void invertOrder() {
        for (final InvertibleComparator comparator : this.comparators) {
            comparator.invertOrder();
        }
    }
    
    public void invertOrder(final int index) {
        this.comparators.get(index).invertOrder();
    }
    
    public void setAscendingOrder(final int index) {
        this.comparators.get(index).setAscending(true);
    }
    
    public void setDescendingOrder(final int index) {
        this.comparators.get(index).setAscending(false);
    }
    
    public int getComparatorCount() {
        return this.comparators.size();
    }
    
    @Override
    public int compare(final T o1, final T o2) {
        Assert.state(this.comparators.size() > 0, "No sort definitions have been added to this CompoundComparator to compare");
        for (final InvertibleComparator comparator : this.comparators) {
            final int result = comparator.compare(o1, o2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CompoundComparator)) {
            return false;
        }
        final CompoundComparator<T> other = (CompoundComparator<T>)obj;
        return this.comparators.equals(other.comparators);
    }
    
    @Override
    public int hashCode() {
        return this.comparators.hashCode();
    }
    
    @Override
    public String toString() {
        return "CompoundComparator: " + this.comparators;
    }
}
