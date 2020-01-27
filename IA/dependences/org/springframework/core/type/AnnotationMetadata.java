// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type;

import java.util.Set;

public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata
{
    Set<String> getAnnotationTypes();
    
    Set<String> getMetaAnnotationTypes(final String p0);
    
    boolean hasAnnotation(final String p0);
    
    boolean hasMetaAnnotation(final String p0);
    
    boolean hasAnnotatedMethods(final String p0);
    
    Set<MethodMetadata> getAnnotatedMethods(final String p0);
}
