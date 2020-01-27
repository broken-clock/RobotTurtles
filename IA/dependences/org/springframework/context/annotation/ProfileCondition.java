// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import java.util.Iterator;
import org.springframework.util.MultiValueMap;
import java.util.List;
import org.springframework.core.type.AnnotatedTypeMetadata;

class ProfileCondition implements Condition
{
    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        if (context.getEnvironment() != null) {
            final MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
            if (attrs != null) {
                for (final Object value : attrs.get("value")) {
                    if (context.getEnvironment().acceptsProfiles((String[])value)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }
}
