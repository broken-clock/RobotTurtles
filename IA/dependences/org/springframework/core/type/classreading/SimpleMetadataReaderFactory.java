// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import org.springframework.core.io.Resource;
import java.io.IOException;
import org.springframework.util.ClassUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class SimpleMetadataReaderFactory implements MetadataReaderFactory
{
    private final ResourceLoader resourceLoader;
    
    public SimpleMetadataReaderFactory() {
        this.resourceLoader = new DefaultResourceLoader();
    }
    
    public SimpleMetadataReaderFactory(final ResourceLoader resourceLoader) {
        this.resourceLoader = ((resourceLoader != null) ? resourceLoader : new DefaultResourceLoader());
    }
    
    public SimpleMetadataReaderFactory(final ClassLoader classLoader) {
        this.resourceLoader = ((classLoader != null) ? new DefaultResourceLoader(classLoader) : new DefaultResourceLoader());
    }
    
    public final ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }
    
    @Override
    public MetadataReader getMetadataReader(final String className) throws IOException {
        final String resourcePath = "classpath:" + ClassUtils.convertClassNameToResourcePath(className) + ".class";
        return this.getMetadataReader(this.resourceLoader.getResource(resourcePath));
    }
    
    @Override
    public MetadataReader getMetadataReader(final Resource resource) throws IOException {
        return new SimpleMetadataReader(resource, this.resourceLoader.getClassLoader());
    }
}
