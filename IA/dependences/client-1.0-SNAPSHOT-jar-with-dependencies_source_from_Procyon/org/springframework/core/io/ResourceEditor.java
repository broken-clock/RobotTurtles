// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

import java.io.IOException;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.StringUtils;
import org.springframework.util.Assert;
import org.springframework.core.env.PropertyResolver;
import java.beans.PropertyEditorSupport;

public class ResourceEditor extends PropertyEditorSupport
{
    private final ResourceLoader resourceLoader;
    private PropertyResolver propertyResolver;
    private final boolean ignoreUnresolvablePlaceholders;
    
    public ResourceEditor() {
        this(new DefaultResourceLoader(), null);
    }
    
    @Deprecated
    public ResourceEditor(final ResourceLoader resourceLoader) {
        this(resourceLoader, null, true);
    }
    
    @Deprecated
    public ResourceEditor(final ResourceLoader resourceLoader, final boolean ignoreUnresolvablePlaceholders) {
        this(resourceLoader, null, ignoreUnresolvablePlaceholders);
    }
    
    public ResourceEditor(final ResourceLoader resourceLoader, final PropertyResolver propertyResolver) {
        this(resourceLoader, propertyResolver, true);
    }
    
    public ResourceEditor(final ResourceLoader resourceLoader, final PropertyResolver propertyResolver, final boolean ignoreUnresolvablePlaceholders) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
        this.propertyResolver = propertyResolver;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }
    
    @Override
    public void setAsText(final String text) {
        if (StringUtils.hasText(text)) {
            final String locationToUse = this.resolvePath(text).trim();
            this.setValue(this.resourceLoader.getResource(locationToUse));
        }
        else {
            this.setValue(null);
        }
    }
    
    protected String resolvePath(final String path) {
        if (this.propertyResolver == null) {
            this.propertyResolver = new StandardEnvironment();
        }
        return this.ignoreUnresolvablePlaceholders ? this.propertyResolver.resolvePlaceholders(path) : this.propertyResolver.resolveRequiredPlaceholders(path);
    }
    
    @Override
    public String getAsText() {
        final Resource value = (Resource)this.getValue();
        try {
            return (value != null) ? value.getURL().toExternalForm() : "";
        }
        catch (IOException ex) {
            return null;
        }
    }
}
