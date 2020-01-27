// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.util.ClassUtils;
import java.beans.PropertyEditorSupport;

public class URIEditor extends PropertyEditorSupport
{
    private final ClassLoader classLoader;
    private final boolean encode;
    
    public URIEditor() {
        this.classLoader = null;
        this.encode = true;
    }
    
    public URIEditor(final boolean encode) {
        this.classLoader = null;
        this.encode = encode;
    }
    
    public URIEditor(final ClassLoader classLoader) {
        this.classLoader = ((classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader());
        this.encode = true;
    }
    
    public URIEditor(final ClassLoader classLoader, final boolean encode) {
        this.classLoader = ((classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader());
        this.encode = encode;
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            final String uri = text.trim();
            if (this.classLoader != null && uri.startsWith("classpath:")) {
                final ClassPathResource resource = new ClassPathResource(uri.substring("classpath:".length()), this.classLoader);
                try {
                    final String url = resource.getURL().toString();
                    this.setValue(this.createURI(url));
                }
                catch (IOException ex) {
                    throw new IllegalArgumentException("Could not retrieve URI for " + resource + ": " + ex.getMessage());
                }
                catch (URISyntaxException ex2) {
                    throw new IllegalArgumentException("Invalid URI syntax: " + ex2);
                }
            }
            else {
                try {
                    this.setValue(this.createURI(uri));
                }
                catch (URISyntaxException ex3) {
                    throw new IllegalArgumentException("Invalid URI syntax: " + ex3);
                }
            }
        }
        else {
            this.setValue(null);
        }
    }
    
    protected URI createURI(final String value) throws URISyntaxException {
        final int colonIndex = value.indexOf(58);
        if (this.encode && colonIndex != -1) {
            final int fragmentIndex = value.indexOf(35, colonIndex + 1);
            final String scheme = value.substring(0, colonIndex);
            final String ssp = value.substring(colonIndex + 1, (fragmentIndex > 0) ? fragmentIndex : value.length());
            final String fragment = (fragmentIndex > 0) ? value.substring(fragmentIndex + 1) : null;
            return new URI(scheme, ssp, fragment);
        }
        return new URI(value);
    }
    
    @Override
    public String getAsText() {
        final URI value = (URI)this.getValue();
        return (value != null) ? value.toString() : "";
    }
}
