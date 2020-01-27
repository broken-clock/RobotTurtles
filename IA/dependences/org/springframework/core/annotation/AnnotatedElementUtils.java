// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.annotation;

import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Map;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Iterator;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;
import java.lang.reflect.AnnotatedElement;

public class AnnotatedElementUtils
{
    public static Set<String> getMetaAnnotationTypes(final AnnotatedElement element, final String annotationType) {
        final Set<String> types = new LinkedHashSet<String>();
        process(element, annotationType, (Processor<Object>)new Processor<Object>() {
            @Override
            public Object process(final Annotation annotation, final int depth) {
                if (depth > 0) {
                    types.add(annotation.annotationType().getName());
                }
                return null;
            }
            
            @Override
            public void postProcess(final Annotation annotation, final Object result) {
            }
        });
        return types.isEmpty() ? null : types;
    }
    
    public static boolean hasMetaAnnotationTypes(final AnnotatedElement element, final String annotationType) {
        return Boolean.TRUE.equals(process(element, annotationType, (Processor<Object>)new Processor<Boolean>() {
            @Override
            public Boolean process(final Annotation annotation, final int depth) {
                if (depth > 0) {
                    return true;
                }
                return null;
            }
            
            @Override
            public void postProcess(final Annotation annotation, final Boolean result) {
            }
        }));
    }
    
    public static boolean isAnnotated(final AnnotatedElement element, final String annotationType) {
        return Boolean.TRUE.equals(process(element, annotationType, (Processor<Object>)new Processor<Boolean>() {
            @Override
            public Boolean process(final Annotation annotation, final int depth) {
                return true;
            }
            
            @Override
            public void postProcess(final Annotation annotation, final Boolean result) {
            }
        }));
    }
    
    public static AnnotationAttributes getAnnotationAttributes(final AnnotatedElement element, final String annotationType) {
        return getAnnotationAttributes(element, annotationType, false, false);
    }
    
    public static AnnotationAttributes getAnnotationAttributes(final AnnotatedElement element, final String annotationType, final boolean classValuesAsString, final boolean nestedAnnotationsAsMap) {
        return process(element, annotationType, (Processor<AnnotationAttributes>)new Processor<AnnotationAttributes>() {
            @Override
            public AnnotationAttributes process(final Annotation annotation, final int depth) {
                return AnnotationUtils.getAnnotationAttributes(annotation, classValuesAsString, nestedAnnotationsAsMap);
            }
            
            @Override
            public void postProcess(final Annotation annotation, final AnnotationAttributes result) {
                for (final String key : ((LinkedHashMap<String, V>)result).keySet()) {
                    if (!"value".equals(key)) {
                        final Object value = AnnotationUtils.getValue(annotation, key);
                        if (value == null) {
                            continue;
                        }
                        result.put(key, value);
                    }
                }
            }
        });
    }
    
    public static MultiValueMap<String, Object> getAllAnnotationAttributes(final AnnotatedElement element, final String annotationType, final boolean classValuesAsString, final boolean nestedAnnotationsAsMap) {
        final MultiValueMap<String, Object> attributes = new LinkedMultiValueMap<String, Object>();
        process(element, annotationType, (Processor<Object>)new Processor<Void>() {
            @Override
            public Void process(final Annotation annotation, final int depth) {
                if (annotation.annotationType().getName().equals(annotationType)) {
                    for (final Map.Entry<String, Object> entry : AnnotationUtils.getAnnotationAttributes(annotation, classValuesAsString, nestedAnnotationsAsMap).entrySet()) {
                        attributes.add(entry.getKey(), entry.getValue());
                    }
                }
                return null;
            }
            
            @Override
            public void postProcess(final Annotation annotation, final Void result) {
                for (final String key : attributes.keySet()) {
                    if (!"value".equals(key)) {
                        final Object value = AnnotationUtils.getValue(annotation, key);
                        if (value == null) {
                            continue;
                        }
                        attributes.add(key, value);
                    }
                }
            }
        });
        return attributes.isEmpty() ? null : attributes;
    }
    
    private static <T> T process(final AnnotatedElement element, final String annotationType, final Processor<T> processor) {
        return doProcess(element, annotationType, processor, new HashSet<AnnotatedElement>(), 0);
    }
    
    private static <T> T doProcess(final AnnotatedElement element, final String annotationType, final Processor<T> processor, final Set<AnnotatedElement> visited, final int depth) {
        if (visited.add(element)) {
            for (final Annotation annotation : element.getAnnotations()) {
                if (annotation.annotationType().getName().equals(annotationType) || depth > 0) {
                    T result = processor.process(annotation, depth);
                    if (result != null) {
                        return result;
                    }
                    result = (T)doProcess(annotation.annotationType(), annotationType, (Processor<Object>)processor, visited, depth + 1);
                    if (result != null) {
                        processor.postProcess(annotation, result);
                        return result;
                    }
                }
            }
            for (final Annotation annotation : element.getAnnotations()) {
                final T result = (T)doProcess(annotation.annotationType(), annotationType, (Processor<Object>)processor, visited, depth);
                if (result != null) {
                    processor.postProcess(annotation, result);
                    return result;
                }
            }
        }
        return null;
    }
    
    private interface Processor<T>
    {
        T process(final Annotation p0, final int p1);
        
        void postProcess(final Annotation p0, final T p1);
    }
}
