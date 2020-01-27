// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.core.type.AnnotatedTypeMetadata;

public interface Condition
{
    boolean matches(final ConditionContext p0, final AnnotatedTypeMetadata p1);
}
