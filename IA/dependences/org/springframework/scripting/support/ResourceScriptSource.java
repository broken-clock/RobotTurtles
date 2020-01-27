// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.support;

import org.springframework.util.StringUtils;
import java.io.IOException;
import java.io.Reader;
import org.springframework.util.FileCopyUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.EncodedResource;
import org.apache.commons.logging.Log;
import org.springframework.scripting.ScriptSource;

public class ResourceScriptSource implements ScriptSource
{
    protected final Log logger;
    private EncodedResource resource;
    private long lastModified;
    private final Object lastModifiedMonitor;
    
    public ResourceScriptSource(final EncodedResource resource) {
        this.logger = LogFactory.getLog(this.getClass());
        this.lastModified = -1L;
        this.lastModifiedMonitor = new Object();
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
    }
    
    public ResourceScriptSource(final Resource resource) {
        this.logger = LogFactory.getLog(this.getClass());
        this.lastModified = -1L;
        this.lastModifiedMonitor = new Object();
        Assert.notNull(resource, "Resource must not be null");
        this.resource = new EncodedResource(resource, "UTF-8");
    }
    
    public final Resource getResource() {
        return this.resource.getResource();
    }
    
    public void setEncoding(final String encoding) {
        this.resource = new EncodedResource(this.resource.getResource(), encoding);
    }
    
    @Override
    public String getScriptAsString() throws IOException {
        synchronized (this.lastModifiedMonitor) {
            this.lastModified = this.retrieveLastModifiedTime();
        }
        final Reader reader = this.resource.getReader();
        return FileCopyUtils.copyToString(reader);
    }
    
    @Override
    public boolean isModified() {
        synchronized (this.lastModifiedMonitor) {
            return this.lastModified < 0L || this.retrieveLastModifiedTime() > this.lastModified;
        }
    }
    
    protected long retrieveLastModifiedTime() {
        try {
            return this.getResource().lastModified();
        }
        catch (IOException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(this.getResource() + " could not be resolved in the file system - " + "current timestamp not available for script modification check", ex);
            }
            return 0L;
        }
    }
    
    @Override
    public String suggestedClassName() {
        return StringUtils.stripFilenameExtension(this.getResource().getFilename());
    }
    
    @Override
    public String toString() {
        return this.resource.toString();
    }
}
