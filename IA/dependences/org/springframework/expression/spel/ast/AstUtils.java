// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.springframework.expression.PropertyAccessor;
import java.util.List;

public class AstUtils
{
    public static List<PropertyAccessor> getPropertyAccessorsToTry(final Class<?> targetType, final List<PropertyAccessor> propertyAccessors) {
        final List<PropertyAccessor> specificAccessors = new ArrayList<PropertyAccessor>();
        final List<PropertyAccessor> generalAccessors = new ArrayList<PropertyAccessor>();
        for (final PropertyAccessor resolver : propertyAccessors) {
            final Class<?>[] targets = resolver.getSpecificTargetClasses();
            if (targets == null) {
                generalAccessors.add(resolver);
            }
            else {
                if (targetType == null) {
                    continue;
                }
                int pos = 0;
                for (final Class<?> clazz : targets) {
                    if (clazz == targetType) {
                        specificAccessors.add(pos++, resolver);
                    }
                    else if (clazz.isAssignableFrom(targetType)) {
                        generalAccessors.add(resolver);
                    }
                }
            }
        }
        final List<PropertyAccessor> resolvers = new ArrayList<PropertyAccessor>();
        resolvers.addAll(specificAccessors);
        resolvers.addAll(generalAccessors);
        return resolvers;
    }
}
