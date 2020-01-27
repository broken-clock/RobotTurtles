// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io.support;

import org.springframework.util.ObjectUtils;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.springframework.util.Assert;
import java.nio.charset.Charset;
import org.springframework.core.io.Resource;

public class EncodedResource
{
    private final Resource resource;
    private String encoding;
    private Charset charset;
    
    public EncodedResource(final Resource resource) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
    }
    
    public EncodedResource(final Resource resource, final String encoding) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
        this.encoding = encoding;
    }
    
    public EncodedResource(final Resource resource, final Charset charset) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
        this.charset = charset;
    }
    
    public final Resource getResource() {
        return this.resource;
    }
    
    public final String getEncoding() {
        return this.encoding;
    }
    
    public final Charset getCharset() {
        return this.charset;
    }
    
    public boolean requiresReader() {
        return this.encoding != null || this.charset != null;
    }
    
    public Reader getReader() throws IOException {
        if (this.charset != null) {
            return new InputStreamReader(this.resource.getInputStream(), this.charset);
        }
        if (this.encoding != null) {
            return new InputStreamReader(this.resource.getInputStream(), this.encoding);
        }
        return new InputStreamReader(this.resource.getInputStream());
    }
    
    public InputStream getInputStream() throws IOException {
        return this.resource.getInputStream();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof EncodedResource) {
            final EncodedResource otherRes = (EncodedResource)obj;
            return this.resource.equals(otherRes.resource) && ObjectUtils.nullSafeEquals(this.encoding, otherRes.encoding);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.resource.hashCode();
    }
    
    @Override
    public String toString() {
        return this.resource.toString();
    }
}
