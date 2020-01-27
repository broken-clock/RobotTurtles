// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.annotation;

import org.springframework.beans.BeanWrapper;
import java.lang.reflect.Method;
import java.util.Set;
import org.springframework.util.ReflectionUtils;
import org.springframework.beans.PropertyAccessorFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.springframework.util.StringValueResolver;
import java.lang.annotation.Annotation;

public abstract class AnnotationBeanUtils
{
    public static void copyPropertiesToBean(final Annotation ann, final Object bean, final String... excludedProperties) {
        copyPropertiesToBean(ann, bean, (StringValueResolver)null, excludedProperties);
    }
    
    public static void copyPropertiesToBean(final Annotation ann, final Object bean, final StringValueResolver valueResolver, final String... excludedProperties) {
        final Set<String> excluded = new HashSet<String>(Arrays.asList(excludedProperties));
        final Method[] annotationProperties = ann.annotationType().getDeclaredMethods();
        final BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        for (final Method annotationProperty : annotationProperties) {
            final String propertyName = annotationProperty.getName();
            if (!excluded.contains(propertyName) && bw.isWritableProperty(propertyName)) {
                Object value = ReflectionUtils.invokeMethod(annotationProperty, ann);
                if (valueResolver != null && value instanceof String) {
                    value = valueResolver.resolveStringValue((String)value);
                }
                bw.setPropertyValue(propertyName, value);
            }
        }
    }
}
