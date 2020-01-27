// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.instrument.classloading;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.util.Assert;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.util.Enumeration;

public class ResourceOverridingShadowingClassLoader extends ShadowingClassLoader
{
    private static final Enumeration<URL> EMPTY_URL_ENUMERATION;
    private Map<String, String> overrides;
    
    public ResourceOverridingShadowingClassLoader(final ClassLoader enclosingClassLoader) {
        super(enclosingClassLoader);
        this.overrides = new HashMap<String, String>();
    }
    
    public void override(final String oldPath, final String newPath) {
        this.overrides.put(oldPath, newPath);
    }
    
    public void suppress(final String oldPath) {
        this.overrides.put(oldPath, null);
    }
    
    public void copyOverrides(final ResourceOverridingShadowingClassLoader other) {
        Assert.notNull(other, "Other ClassLoader must not be null");
        this.overrides.putAll(other.overrides);
    }
    
    @Override
    public URL getResource(final String requestedPath) {
        if (this.overrides.containsKey(requestedPath)) {
            final String overriddenPath = this.overrides.get(requestedPath);
            return (overriddenPath != null) ? super.getResource(overriddenPath) : null;
        }
        return super.getResource(requestedPath);
    }
    
    @Override
    public InputStream getResourceAsStream(final String requestedPath) {
        if (this.overrides.containsKey(requestedPath)) {
            final String overriddenPath = this.overrides.get(requestedPath);
            return (overriddenPath != null) ? super.getResourceAsStream(overriddenPath) : null;
        }
        return super.getResourceAsStream(requestedPath);
    }
    
    @Override
    public Enumeration<URL> getResources(final String requestedPath) throws IOException {
        if (this.overrides.containsKey(requestedPath)) {
            final String overriddenLocation = this.overrides.get(requestedPath);
            return (overriddenLocation != null) ? super.getResources(overriddenLocation) : ResourceOverridingShadowingClassLoader.EMPTY_URL_ENUMERATION;
        }
        return super.getResources(requestedPath);
    }
    
    static {
        EMPTY_URL_ENUMERATION = new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }
            
            @Override
            public URL nextElement() {
                throw new UnsupportedOperationException("Should not be called. I am empty.");
            }
        };
    }
}
