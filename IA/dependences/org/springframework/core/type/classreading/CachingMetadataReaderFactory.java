// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import java.io.IOException;
import org.springframework.core.io.ResourceLoader;
import java.util.LinkedHashMap;
import org.springframework.core.io.Resource;
import java.util.Map;

public class CachingMetadataReaderFactory extends SimpleMetadataReaderFactory
{
    public static final int DEFAULT_CACHE_LIMIT = 256;
    private volatile int cacheLimit;
    private final Map<Resource, MetadataReader> metadataReaderCache;
    
    public CachingMetadataReaderFactory() {
        this.cacheLimit = 256;
        this.metadataReaderCache = new LinkedHashMap<Resource, MetadataReader>(256, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<Resource, MetadataReader> eldest) {
                return this.size() > CachingMetadataReaderFactory.this.getCacheLimit();
            }
        };
    }
    
    public CachingMetadataReaderFactory(final ResourceLoader resourceLoader) {
        super(resourceLoader);
        this.cacheLimit = 256;
        this.metadataReaderCache = new LinkedHashMap<Resource, MetadataReader>(256, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<Resource, MetadataReader> eldest) {
                return this.size() > CachingMetadataReaderFactory.this.getCacheLimit();
            }
        };
    }
    
    public CachingMetadataReaderFactory(final ClassLoader classLoader) {
        super(classLoader);
        this.cacheLimit = 256;
        this.metadataReaderCache = new LinkedHashMap<Resource, MetadataReader>(256, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<Resource, MetadataReader> eldest) {
                return this.size() > CachingMetadataReaderFactory.this.getCacheLimit();
            }
        };
    }
    
    public void setCacheLimit(final int cacheLimit) {
        this.cacheLimit = cacheLimit;
    }
    
    public int getCacheLimit() {
        return this.cacheLimit;
    }
    
    @Override
    public MetadataReader getMetadataReader(final Resource resource) throws IOException {
        if (this.getCacheLimit() <= 0) {
            return super.getMetadataReader(resource);
        }
        synchronized (this.metadataReaderCache) {
            MetadataReader metadataReader = this.metadataReaderCache.get(resource);
            if (metadataReader == null) {
                metadataReader = super.getMetadataReader(resource);
                this.metadataReaderCache.put(resource, metadataReader);
            }
            return metadataReader;
        }
    }
    
    public void clearCache() {
        synchronized (this.metadataReaderCache) {
            this.metadataReaderCache.clear();
        }
    }
}
