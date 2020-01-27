// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamResource extends AbstractResource
{
    private final InputStream inputStream;
    private final String description;
    private boolean read;
    
    public InputStreamResource(final InputStream inputStream) {
        this(inputStream, "resource loaded through InputStream");
    }
    
    public InputStreamResource(final InputStream inputStream, final String description) {
        this.read = false;
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream must not be null");
        }
        this.inputStream = inputStream;
        this.description = ((description != null) ? description : "");
    }
    
    @Override
    public boolean exists() {
        return true;
    }
    
    @Override
    public boolean isOpen() {
        return true;
    }
    
    @Override
    public InputStream getInputStream() throws IOException, IllegalStateException {
        if (this.read) {
            throw new IllegalStateException("InputStream has already been read - do not use InputStreamResource if a stream needs to be read multiple times");
        }
        this.read = true;
        return this.inputStream;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof InputStreamResource && ((InputStreamResource)obj).inputStream.equals(this.inputStream));
    }
    
    @Override
    public int hashCode() {
        return this.inputStream.hashCode();
    }
}
