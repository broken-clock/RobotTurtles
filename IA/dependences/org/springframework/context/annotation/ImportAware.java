// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.Aware;

public interface ImportAware extends Aware
{
    void setImportMetadata(final AnnotationMetadata p0);
}
