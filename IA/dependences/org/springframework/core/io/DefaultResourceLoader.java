// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

import org.springframework.util.StringUtils;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class DefaultResourceLoader implements ResourceLoader
{
    private ClassLoader classLoader;
    
    public DefaultResourceLoader() {
        this.classLoader = ClassUtils.getDefaultClassLoader();
    }
    
    public DefaultResourceLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return (this.classLoader != null) ? this.classLoader : ClassUtils.getDefaultClassLoader();
    }
    
    @Override
    public Resource getResource(final String location) {
        Assert.notNull(location, "Location must not be null");
        if (location.startsWith("classpath:")) {
            return new ClassPathResource(location.substring("classpath:".length()), this.getClassLoader());
        }
        try {
            final URL url = new URL(location);
            return new UrlResource(url);
        }
        catch (MalformedURLException ex) {
            return this.getResourceByPath(location);
        }
    }
    
    protected Resource getResourceByPath(final String path) {
        return new ClassPathContextResource(path, this.getClassLoader());
    }
    
    protected static class ClassPathContextResource extends ClassPathResource implements ContextResource
    {
        public ClassPathContextResource(final String path, final ClassLoader classLoader) {
            super(path, classLoader);
        }
        
        @Override
        public String getPathWithinContext() {
            return this.getPath();
        }
        
        @Override
        public Resource createRelative(final String relativePath) {
            final String pathToUse = StringUtils.applyRelativePath(this.getPath(), relativePath);
            return new ClassPathContextResource(pathToUse, this.getClassLoader());
        }
    }
}
