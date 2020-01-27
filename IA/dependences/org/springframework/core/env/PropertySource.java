// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import org.springframework.util.Assert;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public abstract class PropertySource<T>
{
    protected final Log logger;
    protected final String name;
    protected final T source;
    
    public PropertySource(final String name, final T source) {
        this.logger = LogFactory.getLog(this.getClass());
        Assert.hasText(name, "Property source name must contain at least one character");
        Assert.notNull(source, "Property source must not be null");
        this.name = name;
        this.source = source;
    }
    
    public PropertySource(final String name) {
        this(name, new Object());
    }
    
    public String getName() {
        return this.name;
    }
    
    public T getSource() {
        return this.source;
    }
    
    public boolean containsProperty(final String name) {
        return this.getProperty(name) != null;
    }
    
    public abstract Object getProperty(final String p0);
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PropertySource)) {
            return false;
        }
        final PropertySource<?> other = (PropertySource<?>)obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        if (this.logger.isDebugEnabled()) {
            return String.format("%s@%s [name='%s', properties=%s]", this.getClass().getSimpleName(), System.identityHashCode(this), this.name, this.source);
        }
        return String.format("%s [name='%s']", this.getClass().getSimpleName(), this.name);
    }
    
    public static PropertySource<?> named(final String name) {
        return new ComparisonPropertySource(name);
    }
    
    public static class StubPropertySource extends PropertySource<Object>
    {
        public StubPropertySource(final String name) {
            super(name, new Object());
        }
        
        @Override
        public String getProperty(final String name) {
            return null;
        }
    }
    
    static class ComparisonPropertySource extends StubPropertySource
    {
        private static final String USAGE_ERROR = "ComparisonPropertySource instances are for collection comparison use only";
        
        public ComparisonPropertySource(final String name) {
            super(name);
        }
        
        @Override
        public Object getSource() {
            throw new UnsupportedOperationException("ComparisonPropertySource instances are for collection comparison use only");
        }
        
        @Override
        public boolean containsProperty(final String name) {
            throw new UnsupportedOperationException("ComparisonPropertySource instances are for collection comparison use only");
        }
        
        @Override
        public String getProperty(final String name) {
            throw new UnsupportedOperationException("ComparisonPropertySource instances are for collection comparison use only");
        }
        
        @Override
        public String toString() {
            return String.format("%s [name='%s']", this.getClass().getSimpleName(), this.name);
        }
    }
}
