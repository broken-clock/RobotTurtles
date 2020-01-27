// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.annotation;

import java.util.HashMap;
import java.util.Arrays;
import org.springframework.util.ObjectUtils;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.WeakHashMap;
import org.springframework.util.ReflectionUtils;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import org.springframework.util.Assert;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.BridgeMethodResolver;
import java.lang.reflect.Method;
import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;
import java.util.Map;

public abstract class AnnotationUtils
{
    static final String VALUE = "value";
    private static final Map<Class<?>, Boolean> annotatedInterfaceCache;
    
    public static <T extends Annotation> T getAnnotation(final Annotation ann, final Class<T> annotationType) {
        if (annotationType.isInstance(ann)) {
            return (T)ann;
        }
        return ann.annotationType().getAnnotation(annotationType);
    }
    
    public static <T extends Annotation> T getAnnotation(final AnnotatedElement ae, final Class<T> annotationType) {
        T ann = ae.getAnnotation(annotationType);
        if (ann == null) {
            for (final Annotation metaAnn : ae.getAnnotations()) {
                ann = metaAnn.annotationType().getAnnotation(annotationType);
                if (ann != null) {
                    break;
                }
            }
        }
        return ann;
    }
    
    public static Annotation[] getAnnotations(final Method method) {
        return BridgeMethodResolver.findBridgedMethod(method).getAnnotations();
    }
    
    public static <A extends Annotation> A getAnnotation(final Method method, final Class<A> annotationType) {
        final Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
        return getAnnotation((AnnotatedElement)resolvedMethod, annotationType);
    }
    
    public static <A extends Annotation> Set<A> getRepeatableAnnotation(final Method method, final Class<? extends Annotation> containerAnnotationType, final Class<A> annotationType) {
        final Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
        return getRepeatableAnnotation((AnnotatedElement)resolvedMethod, containerAnnotationType, annotationType);
    }
    
    public static <A extends Annotation> Set<A> getRepeatableAnnotation(final AnnotatedElement annotatedElement, final Class<? extends Annotation> containerAnnotationType, final Class<A> annotationType) {
        if (annotatedElement.getAnnotations().length == 0) {
            return Collections.emptySet();
        }
        return new AnnotationCollector<A>(containerAnnotationType, annotationType).getResult(annotatedElement);
    }
    
    public static <A extends Annotation> A findAnnotation(final Method method, final Class<A> annotationType) {
        A annotation = (A)getAnnotation(method, (Class<Annotation>)annotationType);
        Class<?> clazz = method.getDeclaringClass();
        if (annotation == null) {
            annotation = searchOnInterfaces(method, annotationType, clazz.getInterfaces());
        }
        while (annotation == null) {
            clazz = clazz.getSuperclass();
            if (clazz == null) {
                break;
            }
            if (clazz.equals(Object.class)) {
                break;
            }
            try {
                final Method equivalentMethod = clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
                annotation = (A)getAnnotation(equivalentMethod, (Class<Annotation>)annotationType);
            }
            catch (NoSuchMethodException ex) {}
            if (annotation != null) {
                continue;
            }
            annotation = searchOnInterfaces(method, annotationType, clazz.getInterfaces());
        }
        return annotation;
    }
    
    private static <A extends Annotation> A searchOnInterfaces(final Method method, final Class<A> annotationType, final Class<?>[] ifcs) {
        A annotation = null;
        for (final Class<?> iface : ifcs) {
            if (isInterfaceWithAnnotatedMethods(iface)) {
                try {
                    final Method equivalentMethod = iface.getMethod(method.getName(), method.getParameterTypes());
                    annotation = getAnnotation(equivalentMethod, annotationType);
                }
                catch (NoSuchMethodException ex) {}
                if (annotation != null) {
                    break;
                }
            }
        }
        return annotation;
    }
    
    private static boolean isInterfaceWithAnnotatedMethods(final Class<?> iface) {
        synchronized (AnnotationUtils.annotatedInterfaceCache) {
            final Boolean flag = AnnotationUtils.annotatedInterfaceCache.get(iface);
            if (flag != null) {
                return flag;
            }
            boolean found = false;
            for (final Method ifcMethod : iface.getMethods()) {
                if (ifcMethod.getAnnotations().length > 0) {
                    found = true;
                    break;
                }
            }
            AnnotationUtils.annotatedInterfaceCache.put(iface, found);
            return found;
        }
    }
    
    public static <A extends Annotation> A findAnnotation(final Class<?> clazz, final Class<A> annotationType) {
        Assert.notNull(clazz, "Class must not be null");
        A annotation = clazz.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        for (final Class<?> ifc : clazz.getInterfaces()) {
            annotation = (A)findAnnotation(ifc, (Class<Annotation>)annotationType);
            if (annotation != null) {
                return annotation;
            }
        }
        if (!Annotation.class.isAssignableFrom(clazz)) {
            for (final Annotation ann : clazz.getAnnotations()) {
                annotation = (A)findAnnotation(ann.annotationType(), (Class<Annotation>)annotationType);
                if (annotation != null) {
                    return annotation;
                }
            }
        }
        final Class<?> superClass = clazz.getSuperclass();
        if (superClass == null || superClass.equals(Object.class)) {
            return null;
        }
        return (A)findAnnotation(superClass, (Class<Annotation>)annotationType);
    }
    
    public static Class<?> findAnnotationDeclaringClass(final Class<? extends Annotation> annotationType, final Class<?> clazz) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        if (clazz == null || clazz.equals(Object.class)) {
            return null;
        }
        if (isAnnotationDeclaredLocally(annotationType, clazz)) {
            return clazz;
        }
        return findAnnotationDeclaringClass(annotationType, clazz.getSuperclass());
    }
    
    public static Class<?> findAnnotationDeclaringClassForTypes(final List<Class<? extends Annotation>> annotationTypes, final Class<?> clazz) {
        Assert.notEmpty(annotationTypes, "The list of annotation types must not be empty");
        if (clazz == null || clazz.equals(Object.class)) {
            return null;
        }
        for (final Class<? extends Annotation> annotationType : annotationTypes) {
            if (isAnnotationDeclaredLocally(annotationType, clazz)) {
                return clazz;
            }
        }
        return findAnnotationDeclaringClassForTypes(annotationTypes, clazz.getSuperclass());
    }
    
    public static boolean isAnnotationDeclaredLocally(final Class<? extends Annotation> annotationType, final Class<?> clazz) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        Assert.notNull(clazz, "Class must not be null");
        boolean declaredLocally = false;
        for (final Annotation annotation : clazz.getDeclaredAnnotations()) {
            if (annotation.annotationType().equals(annotationType)) {
                declaredLocally = true;
                break;
            }
        }
        return declaredLocally;
    }
    
    public static boolean isAnnotationInherited(final Class<? extends Annotation> annotationType, final Class<?> clazz) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isAnnotationPresent(annotationType) && !isAnnotationDeclaredLocally(annotationType, clazz);
    }
    
    public static Map<String, Object> getAnnotationAttributes(final Annotation annotation) {
        return getAnnotationAttributes(annotation, false, false);
    }
    
    public static Map<String, Object> getAnnotationAttributes(final Annotation annotation, final boolean classValuesAsString) {
        return getAnnotationAttributes(annotation, classValuesAsString, false);
    }
    
    public static AnnotationAttributes getAnnotationAttributes(final Annotation annotation, final boolean classValuesAsString, final boolean nestedAnnotationsAsMap) {
        final AnnotationAttributes attrs = new AnnotationAttributes();
        final Method[] declaredMethods;
        final Method[] methods = declaredMethods = annotation.annotationType().getDeclaredMethods();
        for (final Method method : declaredMethods) {
            if (method.getParameterTypes().length == 0 && method.getReturnType() != Void.TYPE) {
                try {
                    Object value = method.invoke(annotation, new Object[0]);
                    if (classValuesAsString) {
                        if (value instanceof Class) {
                            value = ((Class)value).getName();
                        }
                        else if (value instanceof Class[]) {
                            final Class<?>[] clazzArray = (Class<?>[])value;
                            final String[] newValue = new String[clazzArray.length];
                            for (int i = 0; i < clazzArray.length; ++i) {
                                newValue[i] = clazzArray[i].getName();
                            }
                            value = newValue;
                        }
                    }
                    if (nestedAnnotationsAsMap && value instanceof Annotation) {
                        ((HashMap<String, AnnotationAttributes>)attrs).put(method.getName(), getAnnotationAttributes((Annotation)value, classValuesAsString, nestedAnnotationsAsMap));
                    }
                    else if (nestedAnnotationsAsMap && value instanceof Annotation[]) {
                        final Annotation[] realAnnotations = (Annotation[])value;
                        final AnnotationAttributes[] mappedAnnotations = new AnnotationAttributes[realAnnotations.length];
                        for (int i = 0; i < realAnnotations.length; ++i) {
                            mappedAnnotations[i] = getAnnotationAttributes(realAnnotations[i], classValuesAsString, nestedAnnotationsAsMap);
                        }
                        ((HashMap<String, AnnotationAttributes[]>)attrs).put(method.getName(), mappedAnnotations);
                    }
                    else {
                        attrs.put(method.getName(), value);
                    }
                }
                catch (Exception ex) {
                    throw new IllegalStateException("Could not obtain annotation attribute values", ex);
                }
            }
        }
        return attrs;
    }
    
    public static Object getValue(final Annotation annotation) {
        return getValue(annotation, "value");
    }
    
    public static Object getValue(final Annotation annotation, final String attributeName) {
        try {
            final Method method = annotation.annotationType().getDeclaredMethod(attributeName, (Class<?>[])new Class[0]);
            ReflectionUtils.makeAccessible(method);
            return method.invoke(annotation, new Object[0]);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static Object getDefaultValue(final Annotation annotation) {
        return getDefaultValue(annotation, "value");
    }
    
    public static Object getDefaultValue(final Annotation annotation, final String attributeName) {
        return getDefaultValue(annotation.annotationType(), attributeName);
    }
    
    public static Object getDefaultValue(final Class<? extends Annotation> annotationType) {
        return getDefaultValue(annotationType, "value");
    }
    
    public static Object getDefaultValue(final Class<? extends Annotation> annotationType, final String attributeName) {
        try {
            final Method method = annotationType.getDeclaredMethod(attributeName, (Class<?>[])new Class[0]);
            return method.getDefaultValue();
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    static {
        annotatedInterfaceCache = new WeakHashMap<Class<?>, Boolean>();
    }
    
    private static class AnnotationCollector<A extends Annotation>
    {
        private final Class<? extends Annotation> containerAnnotationType;
        private final Class<A> annotationType;
        private final Set<AnnotatedElement> visited;
        private final Set<A> result;
        
        public AnnotationCollector(final Class<? extends Annotation> containerAnnotationType, final Class<A> annotationType) {
            this.visited = new HashSet<AnnotatedElement>();
            this.result = new LinkedHashSet<A>();
            this.containerAnnotationType = containerAnnotationType;
            this.annotationType = annotationType;
        }
        
        public Set<A> getResult(final AnnotatedElement element) {
            this.process(element);
            return Collections.unmodifiableSet((Set<? extends A>)this.result);
        }
        
        private void process(final AnnotatedElement annotatedElement) {
            if (this.visited.add(annotatedElement)) {
                for (final Annotation annotation : annotatedElement.getAnnotations()) {
                    if (ObjectUtils.nullSafeEquals(this.annotationType, annotation.annotationType())) {
                        this.result.add((A)annotation);
                    }
                    else if (ObjectUtils.nullSafeEquals(this.containerAnnotationType, annotation.annotationType())) {
                        this.result.addAll((Collection<? extends A>)Arrays.asList(this.getValue(annotation)));
                    }
                    else {
                        this.process(annotation.annotationType());
                    }
                }
            }
        }
        
        private A[] getValue(final Annotation annotation) {
            try {
                final Method method = annotation.annotationType().getDeclaredMethod("value", (Class<?>[])new Class[0]);
                ReflectionUtils.makeAccessible(method);
                return (A[])method.invoke(annotation, new Object[0]);
            }
            catch (Exception ex) {
                throw new IllegalStateException("Unable to read value from repeating annotation container " + this.containerAnnotationType.getName(), ex);
            }
        }
    }
}
