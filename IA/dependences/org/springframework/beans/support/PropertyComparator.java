// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.support;

import java.util.Arrays;
import java.util.Collections;
import org.springframework.util.StringUtils;
import java.util.List;
import org.springframework.beans.BeansException;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.apache.commons.logging.Log;
import java.util.Comparator;

public class PropertyComparator<T> implements Comparator<T>
{
    protected final Log logger;
    private final SortDefinition sortDefinition;
    private final BeanWrapperImpl beanWrapper;
    
    public PropertyComparator(final SortDefinition sortDefinition) {
        this.logger = LogFactory.getLog(this.getClass());
        this.beanWrapper = new BeanWrapperImpl(false);
        this.sortDefinition = sortDefinition;
    }
    
    public PropertyComparator(final String property, final boolean ignoreCase, final boolean ascending) {
        this.logger = LogFactory.getLog(this.getClass());
        this.beanWrapper = new BeanWrapperImpl(false);
        this.sortDefinition = new MutableSortDefinition(property, ignoreCase, ascending);
    }
    
    public final SortDefinition getSortDefinition() {
        return this.sortDefinition;
    }
    
    @Override
    public int compare(final T o1, final T o2) {
        Object v1 = this.getPropertyValue(o1);
        Object v2 = this.getPropertyValue(o2);
        if (this.sortDefinition.isIgnoreCase() && v1 instanceof String && v2 instanceof String) {
            v1 = ((String)v1).toLowerCase();
            v2 = ((String)v2).toLowerCase();
        }
        int result;
        try {
            if (v1 != null) {
                result = ((v2 != null) ? ((Comparable)v1).compareTo(v2) : -1);
            }
            else {
                result = ((v2 != null) ? 1 : 0);
            }
        }
        catch (RuntimeException ex) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("Could not sort objects [" + o1 + "] and [" + o2 + "]", ex);
            }
            return 0;
        }
        return this.sortDefinition.isAscending() ? result : (-result);
    }
    
    private Object getPropertyValue(final Object obj) {
        try {
            this.beanWrapper.setWrappedInstance(obj);
            return this.beanWrapper.getPropertyValue(this.sortDefinition.getProperty());
        }
        catch (BeansException ex) {
            this.logger.info("PropertyComparator could not access property - treating as null for sorting", ex);
            return null;
        }
    }
    
    public static void sort(final List<?> source, final SortDefinition sortDefinition) throws BeansException {
        if (StringUtils.hasText(sortDefinition.getProperty())) {
            Collections.sort(source, new PropertyComparator<Object>(sortDefinition));
        }
    }
    
    public static void sort(final Object[] source, final SortDefinition sortDefinition) throws BeansException {
        if (StringUtils.hasText(sortDefinition.getProperty())) {
            Arrays.sort(source, new PropertyComparator<Object>(sortDefinition));
        }
    }
}
