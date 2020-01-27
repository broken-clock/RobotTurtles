// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.util.List;
import java.util.Collection;
import org.springframework.util.StringUtils;
import java.util.Collections;
import java.util.ArrayList;
import java.beans.PropertyDescriptor;
import org.springframework.util.ObjectUtils;

final class PropertyMatches
{
    public static final int DEFAULT_MAX_DISTANCE = 2;
    private final String propertyName;
    private String[] possibleMatches;
    
    public static PropertyMatches forProperty(final String propertyName, final Class<?> beanClass) {
        return forProperty(propertyName, beanClass, 2);
    }
    
    public static PropertyMatches forProperty(final String propertyName, final Class<?> beanClass, final int maxDistance) {
        return new PropertyMatches(propertyName, beanClass, maxDistance);
    }
    
    private PropertyMatches(final String propertyName, final Class<?> beanClass, final int maxDistance) {
        this.propertyName = propertyName;
        this.possibleMatches = this.calculateMatches(BeanUtils.getPropertyDescriptors(beanClass), maxDistance);
    }
    
    public String[] getPossibleMatches() {
        return this.possibleMatches;
    }
    
    public String buildErrorMessage() {
        final StringBuilder msg = new StringBuilder();
        msg.append("Bean property '");
        msg.append(this.propertyName);
        msg.append("' is not writable or has an invalid setter method. ");
        if (ObjectUtils.isEmpty(this.possibleMatches)) {
            msg.append("Does the parameter type of the setter match the return type of the getter?");
        }
        else {
            msg.append("Did you mean ");
            for (int i = 0; i < this.possibleMatches.length; ++i) {
                msg.append('\'');
                msg.append(this.possibleMatches[i]);
                if (i < this.possibleMatches.length - 2) {
                    msg.append("', ");
                }
                else if (i == this.possibleMatches.length - 2) {
                    msg.append("', or ");
                }
            }
            msg.append("'?");
        }
        return msg.toString();
    }
    
    private String[] calculateMatches(final PropertyDescriptor[] propertyDescriptors, final int maxDistance) {
        final List<String> candidates = new ArrayList<String>();
        for (final PropertyDescriptor pd : propertyDescriptors) {
            if (pd.getWriteMethod() != null) {
                final String possibleAlternative = pd.getName();
                if (this.calculateStringDistance(this.propertyName, possibleAlternative) <= maxDistance) {
                    candidates.add(possibleAlternative);
                }
            }
        }
        Collections.sort(candidates);
        return StringUtils.toStringArray(candidates);
    }
    
    private int calculateStringDistance(final String s1, final String s2) {
        if (s1.length() == 0) {
            return s2.length();
        }
        if (s2.length() == 0) {
            return s1.length();
        }
        final int[][] d = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); ++i) {
            d[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); ++j) {
            d[0][j] = j;
        }
        for (int i = 1; i <= s1.length(); ++i) {
            final char s_i = s1.charAt(i - 1);
            for (int k = 1; k <= s2.length(); ++k) {
                final char t_j = s2.charAt(k - 1);
                int cost;
                if (s_i == t_j) {
                    cost = 0;
                }
                else {
                    cost = 1;
                }
                d[i][k] = Math.min(Math.min(d[i - 1][k] + 1, d[i][k - 1] + 1), d[i - 1][k - 1] + cost);
            }
        }
        return d[s1.length()][s2.length()];
    }
}
