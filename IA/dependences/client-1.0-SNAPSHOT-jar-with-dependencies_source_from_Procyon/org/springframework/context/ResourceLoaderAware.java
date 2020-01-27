// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

import org.springframework.core.io.ResourceLoader;
import org.springframework.beans.factory.Aware;

public interface ResourceLoaderAware extends Aware
{
    void setResourceLoader(final ResourceLoader p0);
}
