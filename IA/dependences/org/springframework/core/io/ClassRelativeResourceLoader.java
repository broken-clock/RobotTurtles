// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

import org.springframework.util.StringUtils;
import org.springframework.util.Assert;

public class ClassRelativeResourceLoader extends DefaultResourceLoader
{
    private final Class<?> clazz;
    
    public ClassRelativeResourceLoader(final Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        this.clazz = clazz;
        this.setClassLoader(clazz.getClassLoader());
    }
    
    @Override
    protected Resource getResourceByPath(final String path) {
        return new ClassRelativeContextResource(path, this.clazz);
    }
    
    private static class ClassRelativeContextResource extends ClassPathResource implements ContextResource
    {
        private final Class<?> clazz;
        
        public ClassRelativeContextResource(final String path, final Class<?> clazz) {
            super(path, clazz);
            this.clazz = clazz;
        }
        
        @Override
        public String getPathWithinContext() {
            return this.getPath();
        }
        
        @Override
        public Resource createRelative(final String relativePath) {
            final String pathToUse = StringUtils.applyRelativePath(this.getPath(), relativePath);
            return new ClassRelativeContextResource(pathToUse, this.clazz);
        }
    }
}
