// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io.support;

import org.springframework.util.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import java.io.IOException;
import org.springframework.core.env.PropertiesPropertySource;

public class ResourcePropertySource extends PropertiesPropertySource
{
    public ResourcePropertySource(final String name, final EncodedResource resource) throws IOException {
        super(name, PropertiesLoaderUtils.loadProperties(resource));
    }
    
    public ResourcePropertySource(final EncodedResource resource) throws IOException {
        this(getNameForResource(resource.getResource()), resource);
    }
    
    public ResourcePropertySource(final String name, final Resource resource) throws IOException {
        super(name, PropertiesLoaderUtils.loadProperties(new EncodedResource(resource)));
    }
    
    public ResourcePropertySource(final Resource resource) throws IOException {
        this(getNameForResource(resource), resource);
    }
    
    public ResourcePropertySource(final String name, final String location, final ClassLoader classLoader) throws IOException {
        this(name, new DefaultResourceLoader(classLoader).getResource(location));
    }
    
    public ResourcePropertySource(final String location, final ClassLoader classLoader) throws IOException {
        this(new DefaultResourceLoader(classLoader).getResource(location));
    }
    
    public ResourcePropertySource(final String name, final String location) throws IOException {
        this(name, new DefaultResourceLoader().getResource(location));
    }
    
    public ResourcePropertySource(final String location) throws IOException {
        this(new DefaultResourceLoader().getResource(location));
    }
    
    private static String getNameForResource(final Resource resource) {
        String name = resource.getDescription();
        if (!StringUtils.hasText(name)) {
            name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
        }
        return name;
    }
}
