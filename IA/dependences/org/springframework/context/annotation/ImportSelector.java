// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;

public interface ImportSelector
{
    String[] selectImports(final AnnotationMetadata p0);
}
