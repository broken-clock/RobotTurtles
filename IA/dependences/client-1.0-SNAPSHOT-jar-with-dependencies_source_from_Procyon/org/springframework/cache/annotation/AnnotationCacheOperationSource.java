// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.annotation;

import java.util.Iterator;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.AnnotatedElement;
import org.springframework.cache.interceptor.CacheOperation;
import java.util.Collection;
import java.util.Collections;
import org.springframework.util.Assert;
import java.util.LinkedHashSet;
import java.util.Set;
import java.io.Serializable;
import org.springframework.cache.interceptor.AbstractFallbackCacheOperationSource;

public class AnnotationCacheOperationSource extends AbstractFallbackCacheOperationSource implements Serializable
{
    private final boolean publicMethodsOnly;
    private final Set<CacheAnnotationParser> annotationParsers;
    
    public AnnotationCacheOperationSource() {
        this(true);
    }
    
    public AnnotationCacheOperationSource(final boolean publicMethodsOnly) {
        this.publicMethodsOnly = publicMethodsOnly;
        (this.annotationParsers = new LinkedHashSet<CacheAnnotationParser>(1)).add(new SpringCacheAnnotationParser());
    }
    
    public AnnotationCacheOperationSource(final CacheAnnotationParser annotationParser) {
        this.publicMethodsOnly = true;
        Assert.notNull(annotationParser, "CacheAnnotationParser must not be null");
        this.annotationParsers = Collections.singleton(annotationParser);
    }
    
    public AnnotationCacheOperationSource(final CacheAnnotationParser... annotationParsers) {
        this.publicMethodsOnly = true;
        Assert.notEmpty(annotationParsers, "At least one CacheAnnotationParser needs to be specified");
        final Set<CacheAnnotationParser> parsers = new LinkedHashSet<CacheAnnotationParser>(annotationParsers.length);
        Collections.addAll(parsers, annotationParsers);
        this.annotationParsers = parsers;
    }
    
    public AnnotationCacheOperationSource(final Set<CacheAnnotationParser> annotationParsers) {
        this.publicMethodsOnly = true;
        Assert.notEmpty(annotationParsers, "At least one CacheAnnotationParser needs to be specified");
        this.annotationParsers = annotationParsers;
    }
    
    @Override
    protected Collection<CacheOperation> findCacheOperations(final Class<?> clazz) {
        return this.determineCacheOperations(clazz);
    }
    
    @Override
    protected Collection<CacheOperation> findCacheOperations(final Method method) {
        return this.determineCacheOperations(method);
    }
    
    protected Collection<CacheOperation> determineCacheOperations(final AnnotatedElement ae) {
        Collection<CacheOperation> ops = null;
        for (final CacheAnnotationParser annotationParser : this.annotationParsers) {
            final Collection<CacheOperation> annOps = annotationParser.parseCacheAnnotations(ae);
            if (annOps != null) {
                if (ops == null) {
                    ops = new ArrayList<CacheOperation>();
                }
                ops.addAll(annOps);
            }
        }
        return ops;
    }
    
    @Override
    protected boolean allowPublicMethodsOnly() {
        return this.publicMethodsOnly;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationCacheOperationSource)) {
            return false;
        }
        final AnnotationCacheOperationSource otherCos = (AnnotationCacheOperationSource)other;
        return this.annotationParsers.equals(otherCos.annotationParsers) && this.publicMethodsOnly == otherCos.publicMethodsOnly;
    }
    
    @Override
    public int hashCode() {
        return this.annotationParsers.hashCode();
    }
}
