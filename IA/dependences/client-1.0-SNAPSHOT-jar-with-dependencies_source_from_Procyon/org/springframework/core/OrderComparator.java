// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

public class OrderComparator implements Comparator<Object>
{
    public static final OrderComparator INSTANCE;
    
    @Override
    public int compare(final Object o1, final Object o2) {
        final boolean p1 = o1 instanceof PriorityOrdered;
        final boolean p2 = o2 instanceof PriorityOrdered;
        if (p1 && !p2) {
            return -1;
        }
        if (p2 && !p1) {
            return 1;
        }
        final int i1 = this.getOrder(o1);
        final int i2 = this.getOrder(o2);
        return (i1 < i2) ? -1 : ((i1 > i2) ? 1 : 0);
    }
    
    protected int getOrder(final Object obj) {
        return (obj instanceof Ordered) ? ((Ordered)obj).getOrder() : Integer.MAX_VALUE;
    }
    
    public static void sort(final List<?> list) {
        if (list.size() > 1) {
            Collections.sort(list, OrderComparator.INSTANCE);
        }
    }
    
    public static void sort(final Object[] array) {
        if (array.length > 1) {
            Arrays.sort(array, OrderComparator.INSTANCE);
        }
    }
    
    public static void sortIfNecessary(final Object value) {
        if (value instanceof Object[]) {
            sort((Object[])value);
        }
        else if (value instanceof List) {
            sort((List<?>)value);
        }
    }
    
    static {
        INSTANCE = new OrderComparator();
    }
}
