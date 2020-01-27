// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.comparator;

import java.util.Comparator;

public class ComparableComparator<T extends Comparable<T>> implements Comparator<T>
{
    public static final ComparableComparator INSTANCE;
    
    @Override
    public int compare(final T o1, final T o2) {
        return o1.compareTo(o2);
    }
    
    static {
        INSTANCE = new ComparableComparator();
    }
}
