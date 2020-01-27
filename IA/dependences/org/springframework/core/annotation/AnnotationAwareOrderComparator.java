// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.annotation;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.OrderComparator;

public class AnnotationAwareOrderComparator extends OrderComparator
{
    public static final AnnotationAwareOrderComparator INSTANCE;
    
    @Override
    protected int getOrder(final Object obj) {
        if (obj instanceof Ordered) {
            return ((Ordered)obj).getOrder();
        }
        if (obj != null) {
            final Class<?> clazz = (Class<?>)((obj instanceof Class) ? ((Class)obj) : obj.getClass());
            final Order order = AnnotationUtils.findAnnotation(clazz, Order.class);
            if (order != null) {
                return order.value();
            }
        }
        return Integer.MAX_VALUE;
    }
    
    public static void sort(final List<?> list) {
        if (list.size() > 1) {
            Collections.sort(list, AnnotationAwareOrderComparator.INSTANCE);
        }
    }
    
    public static void sort(final Object[] array) {
        if (array.length > 1) {
            Arrays.sort(array, AnnotationAwareOrderComparator.INSTANCE);
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
        INSTANCE = new AnnotationAwareOrderComparator();
    }
}
