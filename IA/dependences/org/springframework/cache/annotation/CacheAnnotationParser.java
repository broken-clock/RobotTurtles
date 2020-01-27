// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.annotation;

import org.springframework.cache.interceptor.CacheOperation;
import java.util.Collection;
import java.lang.reflect.AnnotatedElement;

public interface CacheAnnotationParser
{
    Collection<CacheOperation> parseCacheAnnotations(final AnnotatedElement p0);
}
