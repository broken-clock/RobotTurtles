// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import org.springframework.util.Assert;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public abstract class EnumerablePropertySource<T> extends PropertySource<T>
{
    protected static final String[] EMPTY_NAMES_ARRAY;
    protected final Log logger;
    
    public EnumerablePropertySource(final String name, final T source) {
        super(name, source);
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    public abstract String[] getPropertyNames();
    
    @Override
    public boolean containsProperty(final String name) {
        Assert.notNull(name, "property name must not be null");
        for (final String candidate : this.getPropertyNames()) {
            if (candidate.equals(name)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug(String.format("PropertySource [%s] contains '%s'", this.getName(), name));
                }
                return true;
            }
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(String.format("PropertySource [%s] does not contain '%s'", this.getName(), name));
        }
        return false;
    }
    
    static {
        EMPTY_NAMES_ARRAY = new String[0];
    }
}
