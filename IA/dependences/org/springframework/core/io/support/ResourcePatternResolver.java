// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io.support;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public interface ResourcePatternResolver extends ResourceLoader
{
    public static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
    
    Resource[] getResources(final String p0) throws IOException;
}
