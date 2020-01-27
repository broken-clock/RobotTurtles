// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.annotation;

import org.springframework.util.ObjectUtils;
import org.springframework.cache.interceptor.CachePutOperation;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheableOperation;
import java.util.ArrayList;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import org.springframework.cache.interceptor.CacheOperation;
import java.util.Collection;
import java.lang.reflect.AnnotatedElement;
import java.io.Serializable;

public class SpringCacheAnnotationParser implements CacheAnnotationParser, Serializable
{
    @Override
    public Collection<CacheOperation> parseCacheAnnotations(final AnnotatedElement ae) {
        Collection<CacheOperation> ops = null;
        final Collection<Cacheable> cacheables = this.getAnnotations(ae, Cacheable.class);
        if (cacheables != null) {
            ops = this.lazyInit(ops);
            for (final Cacheable cacheable : cacheables) {
                ops.add(this.parseCacheableAnnotation(ae, cacheable));
            }
        }
        final Collection<CacheEvict> evicts = this.getAnnotations(ae, CacheEvict.class);
        if (evicts != null) {
            ops = this.lazyInit(ops);
            for (final CacheEvict e : evicts) {
                ops.add(this.parseEvictAnnotation(ae, e));
            }
        }
        final Collection<CachePut> updates = this.getAnnotations(ae, CachePut.class);
        if (updates != null) {
            ops = this.lazyInit(ops);
            for (final CachePut p : updates) {
                ops.add(this.parseUpdateAnnotation(ae, p));
            }
        }
        final Collection<Caching> caching = this.getAnnotations(ae, Caching.class);
        if (caching != null) {
            ops = this.lazyInit(ops);
            for (final Caching c : caching) {
                ops.addAll(this.parseCachingAnnotation(ae, c));
            }
        }
        return ops;
    }
    
    private <T extends Annotation> Collection<CacheOperation> lazyInit(final Collection<CacheOperation> ops) {
        return (ops != null) ? ops : new ArrayList<CacheOperation>(1);
    }
    
    CacheableOperation parseCacheableAnnotation(final AnnotatedElement ae, final Cacheable caching) {
        final CacheableOperation cuo = new CacheableOperation();
        cuo.setCacheNames(caching.value());
        cuo.setCondition(caching.condition());
        cuo.setUnless(caching.unless());
        cuo.setKey(caching.key());
        cuo.setName(ae.toString());
        return cuo;
    }
    
    CacheEvictOperation parseEvictAnnotation(final AnnotatedElement ae, final CacheEvict caching) {
        final CacheEvictOperation ceo = new CacheEvictOperation();
        ceo.setCacheNames(caching.value());
        ceo.setCondition(caching.condition());
        ceo.setKey(caching.key());
        ceo.setCacheWide(caching.allEntries());
        ceo.setBeforeInvocation(caching.beforeInvocation());
        ceo.setName(ae.toString());
        return ceo;
    }
    
    CacheOperation parseUpdateAnnotation(final AnnotatedElement ae, final CachePut caching) {
        final CachePutOperation cuo = new CachePutOperation();
        cuo.setCacheNames(caching.value());
        cuo.setCondition(caching.condition());
        cuo.setUnless(caching.unless());
        cuo.setKey(caching.key());
        cuo.setName(ae.toString());
        return cuo;
    }
    
    Collection<CacheOperation> parseCachingAnnotation(final AnnotatedElement ae, final Caching caching) {
        Collection<CacheOperation> ops = null;
        final Cacheable[] cacheables = caching.cacheable();
        if (!ObjectUtils.isEmpty(cacheables)) {
            ops = this.lazyInit(ops);
            for (final Cacheable cacheable : cacheables) {
                ops.add(this.parseCacheableAnnotation(ae, cacheable));
            }
        }
        final CacheEvict[] evicts = caching.evict();
        if (!ObjectUtils.isEmpty(evicts)) {
            ops = this.lazyInit(ops);
            for (final CacheEvict evict : evicts) {
                ops.add(this.parseEvictAnnotation(ae, evict));
            }
        }
        final CachePut[] updates = caching.put();
        if (!ObjectUtils.isEmpty(updates)) {
            ops = this.lazyInit(ops);
            for (final CachePut update : updates) {
                ops.add(this.parseUpdateAnnotation(ae, update));
            }
        }
        return ops;
    }
    
    private <T extends Annotation> Collection<T> getAnnotations(final AnnotatedElement ae, final Class<T> annotationType) {
        final Collection<T> anns = new ArrayList<T>(2);
        T ann = ae.getAnnotation(annotationType);
        if (ann != null) {
            anns.add(ann);
        }
        for (final Annotation metaAnn : ae.getAnnotations()) {
            ann = metaAnn.annotationType().getAnnotation(annotationType);
            if (ann != null) {
                anns.add(ann);
            }
        }
        return anns.isEmpty() ? null : anns;
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof SpringCacheAnnotationParser;
    }
    
    @Override
    public int hashCode() {
        return SpringCacheAnnotationParser.class.hashCode();
    }
}
