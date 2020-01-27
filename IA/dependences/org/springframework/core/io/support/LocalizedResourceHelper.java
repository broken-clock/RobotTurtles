// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io.support;

import org.springframework.core.io.Resource;
import java.util.Locale;
import org.springframework.util.Assert;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class LocalizedResourceHelper
{
    public static final String DEFAULT_SEPARATOR = "_";
    private final ResourceLoader resourceLoader;
    private String separator;
    
    public LocalizedResourceHelper() {
        this.separator = "_";
        this.resourceLoader = new DefaultResourceLoader();
    }
    
    public LocalizedResourceHelper(final ResourceLoader resourceLoader) {
        this.separator = "_";
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }
    
    public void setSeparator(final String separator) {
        this.separator = ((separator != null) ? separator : "_");
    }
    
    public Resource findLocalizedResource(final String name, final String extension, final Locale locale) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(extension, "Extension must not be null");
        Resource resource = null;
        if (locale != null) {
            final String lang = locale.getLanguage();
            final String country = locale.getCountry();
            final String variant = locale.getVariant();
            if (variant.length() > 0) {
                final String location = name + this.separator + lang + this.separator + country + this.separator + variant + extension;
                resource = this.resourceLoader.getResource(location);
            }
            if ((resource == null || !resource.exists()) && country.length() > 0) {
                final String location = name + this.separator + lang + this.separator + country + extension;
                resource = this.resourceLoader.getResource(location);
            }
            if ((resource == null || !resource.exists()) && lang.length() > 0) {
                final String location = name + this.separator + lang + extension;
                resource = this.resourceLoader.getResource(location);
            }
        }
        if (resource == null || !resource.exists()) {
            final String location2 = name + extension;
            resource = this.resourceLoader.getResource(location2);
        }
        return resource;
    }
}
