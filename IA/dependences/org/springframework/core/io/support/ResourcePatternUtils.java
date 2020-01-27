// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io.support;

import org.springframework.util.Assert;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

public abstract class ResourcePatternUtils
{
    public static boolean isUrl(final String resourceLocation) {
        return resourceLocation != null && (resourceLocation.startsWith("classpath*:") || ResourceUtils.isUrl(resourceLocation));
    }
    
    public static ResourcePatternResolver getResourcePatternResolver(final ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        if (resourceLoader instanceof ResourcePatternResolver) {
            return (ResourcePatternResolver)resourceLoader;
        }
        if (resourceLoader != null) {
            return new PathMatchingResourcePatternResolver(resourceLoader);
        }
        return new PathMatchingResourcePatternResolver();
    }
}
