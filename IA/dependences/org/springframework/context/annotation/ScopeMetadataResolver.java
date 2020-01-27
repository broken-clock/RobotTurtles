// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;

public interface ScopeMetadataResolver
{
    ScopeMetadata resolveScopeMetadata(final BeanDefinition p0);
}
