// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type;

import org.springframework.util.MultiValueMap;
import java.util.Map;

public interface AnnotatedTypeMetadata
{
    boolean isAnnotated(final String p0);
    
    Map<String, Object> getAnnotationAttributes(final String p0);
    
    Map<String, Object> getAnnotationAttributes(final String p0, final boolean p1);
    
    MultiValueMap<String, Object> getAllAnnotationAttributes(final String p0);
    
    MultiValueMap<String, Object> getAllAnnotationAttributes(final String p0, final boolean p1);
}
