// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

public class FileSystemResourceLoader extends DefaultResourceLoader
{
    @Override
    protected Resource getResourceByPath(String path) {
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        return new FileSystemContextResource(path);
    }
    
    private static class FileSystemContextResource extends FileSystemResource implements ContextResource
    {
        public FileSystemContextResource(final String path) {
            super(path);
        }
        
        @Override
        public String getPathWithinContext() {
            return this.getPath();
        }
    }
}
