// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.asm.ClassVisitor;
import org.springframework.core.NestedIOException;
import org.springframework.asm.ClassReader;
import java.io.BufferedInputStream;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.io.Resource;

final class SimpleMetadataReader implements MetadataReader
{
    private final Resource resource;
    private final ClassMetadata classMetadata;
    private final AnnotationMetadata annotationMetadata;
    
    SimpleMetadataReader(final Resource resource, final ClassLoader classLoader) throws IOException {
        final InputStream is = new BufferedInputStream(resource.getInputStream());
        ClassReader classReader;
        try {
            classReader = new ClassReader(is);
        }
        catch (IllegalArgumentException ex) {
            throw new NestedIOException("ASM ClassReader failed to parse class file - probably due to a new Java class file version that isn't supported yet: " + resource, ex);
        }
        finally {
            is.close();
        }
        final AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor(classLoader);
        classReader.accept(visitor, 2);
        this.annotationMetadata = visitor;
        this.classMetadata = visitor;
        this.resource = resource;
    }
    
    @Override
    public Resource getResource() {
        return this.resource;
    }
    
    @Override
    public ClassMetadata getClassMetadata() {
        return this.classMetadata;
    }
    
    @Override
    public AnnotationMetadata getAnnotationMetadata() {
        return this.annotationMetadata;
    }
}
