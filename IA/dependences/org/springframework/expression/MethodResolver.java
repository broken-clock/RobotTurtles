// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;
import java.util.List;

public interface MethodResolver
{
    MethodExecutor resolve(final EvaluationContext p0, final Object p1, final String p2, final List<TypeDescriptor> p3) throws AccessException;
}
