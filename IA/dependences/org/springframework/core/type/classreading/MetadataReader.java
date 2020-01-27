// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.io.Resource;

public interface MetadataReader
{
    Resource getResource();
    
    ClassMetadata getClassMetadata();
    
    AnnotationMetadata getAnnotationMetadata();
}
