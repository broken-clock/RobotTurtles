// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DescriptiveResource extends AbstractResource
{
    private final String description;
    
    public DescriptiveResource(final String description) {
        this.description = ((description != null) ? description : "");
    }
    
    @Override
    public boolean exists() {
        return false;
    }
    
    @Override
    public boolean isReadable() {
        return false;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException(this.getDescription() + " cannot be opened because it does not point to a readable resource");
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof DescriptiveResource && ((DescriptiveResource)obj).description.equals(this.description));
    }
    
    @Override
    public int hashCode() {
        return this.description.hashCode();
    }
}
