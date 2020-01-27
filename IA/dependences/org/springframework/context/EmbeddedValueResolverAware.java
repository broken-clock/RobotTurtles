// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

import org.springframework.util.StringValueResolver;
import org.springframework.beans.factory.Aware;

public interface EmbeddedValueResolverAware extends Aware
{
    void setEmbeddedValueResolver(final StringValueResolver p0);
}
