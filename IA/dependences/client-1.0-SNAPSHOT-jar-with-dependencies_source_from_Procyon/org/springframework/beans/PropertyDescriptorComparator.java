// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.beans.PropertyDescriptor;
import java.util.Comparator;

class PropertyDescriptorComparator implements Comparator<PropertyDescriptor>
{
    @Override
    public int compare(final PropertyDescriptor desc1, final PropertyDescriptor desc2) {
        final String left = desc1.getName();
        final String right = desc2.getName();
        for (int i = 0; i < left.length(); ++i) {
            if (right.length() == i) {
                return 1;
            }
            final int result = left.getBytes()[i] - right.getBytes()[i];
            if (result != 0) {
                return result;
            }
        }
        return left.length() - right.length();
    }
}
