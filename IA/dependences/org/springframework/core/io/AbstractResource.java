// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

import org.springframework.util.Assert;
import java.io.File;
import java.net.URISyntaxException;
import org.springframework.core.NestedIOException;
import org.springframework.util.ResourceUtils;
import java.net.URI;
import java.io.FileNotFoundException;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;

public abstract class AbstractResource implements Resource
{
    @Override
    public boolean exists() {
        try {
            return this.getFile().exists();
        }
        catch (IOException ex) {
            try {
                final InputStream is = this.getInputStream();
                is.close();
                return true;
            }
            catch (Throwable isEx) {
                return false;
            }
        }
    }
    
    @Override
    public boolean isReadable() {
        return true;
    }
    
    @Override
    public boolean isOpen() {
        return false;
    }
    
    @Override
    public URL getURL() throws IOException {
        throw new FileNotFoundException(this.getDescription() + " cannot be resolved to URL");
    }
    
    @Override
    public URI getURI() throws IOException {
        final URL url = this.getURL();
        try {
            return ResourceUtils.toURI(url);
        }
        catch (URISyntaxException ex) {
            throw new NestedIOException("Invalid URI [" + url + "]", ex);
        }
    }
    
    @Override
    public File getFile() throws IOException {
        throw new FileNotFoundException(this.getDescription() + " cannot be resolved to absolute file path");
    }
    
    @Override
    public long contentLength() throws IOException {
        final InputStream is = this.getInputStream();
        Assert.state(is != null, "resource input stream must not be null");
        try {
            long size = 0L;
            final byte[] buf = new byte[255];
            int read;
            while ((read = is.read(buf)) != -1) {
                size += read;
            }
            return size;
        }
        finally {
            try {
                is.close();
            }
            catch (IOException ex) {}
        }
    }
    
    @Override
    public long lastModified() throws IOException {
        final long lastModified = this.getFileForLastModifiedCheck().lastModified();
        if (lastModified == 0L) {
            throw new FileNotFoundException(this.getDescription() + " cannot be resolved in the file system for resolving its last-modified timestamp");
        }
        return lastModified;
    }
    
    protected File getFileForLastModifiedCheck() throws IOException {
        return this.getFile();
    }
    
    @Override
    public Resource createRelative(final String relativePath) throws IOException {
        throw new FileNotFoundException("Cannot create a relative resource for " + this.getDescription());
    }
    
    @Override
    public String getFilename() {
        return null;
    }
    
    @Override
    public String toString() {
        return this.getDescription();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof Resource && ((Resource)obj).getDescription().equals(this.getDescription()));
    }
    
    @Override
    public int hashCode() {
        return this.getDescription().hashCode();
    }
}
