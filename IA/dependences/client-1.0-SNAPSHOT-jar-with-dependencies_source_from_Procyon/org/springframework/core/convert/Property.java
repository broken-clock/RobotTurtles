// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert;

import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashMap;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.StringUtils;
import org.springframework.core.MethodParameter;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Map;

public final class Property
{
    private static Map<Property, Annotation[]> annotationCache;
    private final Class<?> objectType;
    private final Method readMethod;
    private final Method writeMethod;
    private final String name;
    private final MethodParameter methodParameter;
    private Annotation[] annotations;
    
    public Property(final Class<?> objectType, final Method readMethod, final Method writeMethod) {
        this(objectType, readMethod, writeMethod, null);
    }
    
    public Property(final Class<?> objectType, final Method readMethod, final Method writeMethod, final String name) {
        this.objectType = objectType;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
        this.methodParameter = this.resolveMethodParameter();
        this.name = ((name == null) ? this.resolveName() : name);
    }
    
    public Class<?> getObjectType() {
        return this.objectType;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class<?> getType() {
        return this.methodParameter.getParameterType();
    }
    
    public Method getReadMethod() {
        return this.readMethod;
    }
    
    public Method getWriteMethod() {
        return this.writeMethod;
    }
    
    MethodParameter getMethodParameter() {
        return this.methodParameter;
    }
    
    Annotation[] getAnnotations() {
        if (this.annotations == null) {
            this.annotations = this.resolveAnnotations();
        }
        return this.annotations;
    }
    
    private String resolveName() {
        if (this.readMethod != null) {
            int index = this.readMethod.getName().indexOf("get");
            if (index != -1) {
                index += 3;
            }
            else {
                index = this.readMethod.getName().indexOf("is");
                if (index == -1) {
                    throw new IllegalArgumentException("Not a getter method");
                }
                index += 2;
            }
            return StringUtils.uncapitalize(this.readMethod.getName().substring(index));
        }
        int index = this.writeMethod.getName().indexOf("set") + 3;
        if (index == -1) {
            throw new IllegalArgumentException("Not a setter method");
        }
        return StringUtils.uncapitalize(this.writeMethod.getName().substring(index));
    }
    
    private MethodParameter resolveMethodParameter() {
        final MethodParameter read = this.resolveReadMethodParameter();
        final MethodParameter write = this.resolveWriteMethodParameter();
        if (write != null) {
            if (read != null) {
                final Class<?> readType = read.getParameterType();
                final Class<?> writeType = write.getParameterType();
                if (!writeType.equals(readType) && writeType.isAssignableFrom(readType)) {
                    return read;
                }
            }
            return write;
        }
        if (read == null) {
            throw new IllegalStateException("Property is neither readable nor writeable");
        }
        return read;
    }
    
    private MethodParameter resolveReadMethodParameter() {
        if (this.getReadMethod() == null) {
            return null;
        }
        return this.resolveParameterType(new MethodParameter(this.getReadMethod(), -1));
    }
    
    private MethodParameter resolveWriteMethodParameter() {
        if (this.getWriteMethod() == null) {
            return null;
        }
        return this.resolveParameterType(new MethodParameter(this.getWriteMethod(), 0));
    }
    
    private MethodParameter resolveParameterType(final MethodParameter parameter) {
        GenericTypeResolver.resolveParameterType(parameter, this.getObjectType());
        return parameter;
    }
    
    private Annotation[] resolveAnnotations() {
        Annotation[] annotations = Property.annotationCache.get(this);
        if (annotations == null) {
            final Map<Class<? extends Annotation>, Annotation> annotationMap = new LinkedHashMap<Class<? extends Annotation>, Annotation>();
            this.addAnnotationsToMap(annotationMap, this.getReadMethod());
            this.addAnnotationsToMap(annotationMap, this.getWriteMethod());
            this.addAnnotationsToMap(annotationMap, this.getField());
            annotations = annotationMap.values().toArray(new Annotation[annotationMap.size()]);
            Property.annotationCache.put(this, annotations);
        }
        return annotations;
    }
    
    private void addAnnotationsToMap(final Map<Class<? extends Annotation>, Annotation> annotationMap, final AnnotatedElement object) {
        if (object != null) {
            for (final Annotation annotation : object.getAnnotations()) {
                annotationMap.put(annotation.annotationType(), annotation);
            }
        }
    }
    
    private Field getField() {
        final String name = this.getName();
        if (!StringUtils.hasLength(name)) {
            return null;
        }
        final Class<?> declaringClass = this.declaringClass();
        Field field = ReflectionUtils.findField(declaringClass, name);
        if (field == null) {
            field = ReflectionUtils.findField(declaringClass, name.substring(0, 1).toLowerCase() + name.substring(1));
            if (field == null) {
                field = ReflectionUtils.findField(declaringClass, name.substring(0, 1).toUpperCase() + name.substring(1));
            }
        }
        return field;
    }
    
    private Class<?> declaringClass() {
        if (this.getReadMethod() != null) {
            return this.getReadMethod().getDeclaringClass();
        }
        return this.getWriteMethod().getDeclaringClass();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.objectType);
        hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.readMethod);
        hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.writeMethod);
        hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.name);
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Property other = (Property)obj;
        boolean equals = true;
        equals &= ObjectUtils.nullSafeEquals(this.objectType, other.objectType);
        equals &= ObjectUtils.nullSafeEquals(this.readMethod, other.readMethod);
        equals &= ObjectUtils.nullSafeEquals(this.writeMethod, other.writeMethod);
        equals &= ObjectUtils.nullSafeEquals(this.name, other.name);
        return equals;
    }
    
    static {
        Property.annotationCache = new ConcurrentReferenceHashMap<Property, Annotation[]>();
    }
}
