// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import org.springframework.util.Assert;
import org.springframework.core.io.Resource;

public class Location
{
    private final Resource resource;
    private final Object source;
    
    public Location(final Resource resource) {
        this(resource, null);
    }
    
    public Location(final Resource resource, final Object source) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
        this.source = source;
    }
    
    public Resource getResource() {
        return this.resource;
    }
    
    public Object getSource() {
        return this.source;
    }
}
