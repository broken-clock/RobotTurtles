// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.springframework.util.ObjectUtils;
import java.io.Serializable;
import org.springframework.util.Assert;
import org.springframework.aop.ClassFilter;

public abstract class ClassFilters
{
    public static ClassFilter union(final ClassFilter cf1, final ClassFilter cf2) {
        Assert.notNull(cf1, "First ClassFilter must not be null");
        Assert.notNull(cf2, "Second ClassFilter must not be null");
        return new UnionClassFilter(new ClassFilter[] { cf1, cf2 });
    }
    
    public static ClassFilter union(final ClassFilter[] classFilters) {
        Assert.notEmpty(classFilters, "ClassFilter array must not be empty");
        return new UnionClassFilter(classFilters);
    }
    
    public static ClassFilter intersection(final ClassFilter cf1, final ClassFilter cf2) {
        Assert.notNull(cf1, "First ClassFilter must not be null");
        Assert.notNull(cf2, "Second ClassFilter must not be null");
        return new IntersectionClassFilter(new ClassFilter[] { cf1, cf2 });
    }
    
    public static ClassFilter intersection(final ClassFilter[] classFilters) {
        Assert.notEmpty(classFilters, "ClassFilter array must not be empty");
        return new IntersectionClassFilter(classFilters);
    }
    
    private static class UnionClassFilter implements ClassFilter, Serializable
    {
        private ClassFilter[] filters;
        
        public UnionClassFilter(final ClassFilter[] filters) {
            this.filters = filters;
        }
        
        @Override
        public boolean matches(final Class<?> clazz) {
            for (final ClassFilter filter : this.filters) {
                if (filter.matches(clazz)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || (other instanceof UnionClassFilter && ObjectUtils.nullSafeEquals(this.filters, ((UnionClassFilter)other).filters));
        }
        
        @Override
        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.filters);
        }
    }
    
    private static class IntersectionClassFilter implements ClassFilter, Serializable
    {
        private ClassFilter[] filters;
        
        public IntersectionClassFilter(final ClassFilter[] filters) {
            this.filters = filters;
        }
        
        @Override
        public boolean matches(final Class<?> clazz) {
            for (final ClassFilter filter : this.filters) {
                if (!filter.matches(clazz)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || (other instanceof IntersectionClassFilter && ObjectUtils.nullSafeEquals(this.filters, ((IntersectionClassFilter)other).filters));
        }
        
        @Override
        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.filters);
        }
    }
}
