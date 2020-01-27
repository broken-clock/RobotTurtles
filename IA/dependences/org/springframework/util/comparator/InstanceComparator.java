// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.comparator;

import org.springframework.util.Assert;
import java.util.Comparator;

public class InstanceComparator<T> implements Comparator<T>
{
    private Class<?>[] instanceOrder;
    
    public InstanceComparator(final Class<?>... instanceOrder) {
        Assert.notNull(instanceOrder, "InstanceOrder must not be null");
        this.instanceOrder = instanceOrder;
    }
    
    @Override
    public int compare(final T o1, final T o2) {
        final int i1 = this.getOrder(o1);
        final int i2 = this.getOrder(o2);
        return (i1 < i2) ? -1 : ((i1 == i2) ? 0 : 1);
    }
    
    private int getOrder(final T object) {
        if (object != null) {
            for (int i = 0; i < this.instanceOrder.length; ++i) {
                if (this.instanceOrder[i].isInstance(object)) {
                    return i;
                }
            }
        }
        return this.instanceOrder.length;
    }
}
