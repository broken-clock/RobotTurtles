// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;
import java.util.List;

public interface ConstructorResolver
{
    ConstructorExecutor resolve(final EvaluationContext p0, final String p1, final List<TypeDescriptor> p2) throws AccessException;
}
