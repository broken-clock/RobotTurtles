// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.springframework.core.GenericCollectionTypeResolver;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.Assert;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import org.springframework.core.MethodParameter;
import java.io.Serializable;

public class DependencyDescriptor implements Serializable
{
    private transient MethodParameter methodParameter;
    private transient Field field;
    private Class<?> declaringClass;
    private Class<?> containingClass;
    private String methodName;
    private Class<?>[] parameterTypes;
    private int parameterIndex;
    private String fieldName;
    private final boolean required;
    private final boolean eager;
    private int nestingLevel;
    private transient Annotation[] fieldAnnotations;
    
    public DependencyDescriptor(final MethodParameter methodParameter, final boolean required) {
        this(methodParameter, required, true);
    }
    
    public DependencyDescriptor(final MethodParameter methodParameter, final boolean required, final boolean eager) {
        this.nestingLevel = 1;
        Assert.notNull(methodParameter, "MethodParameter must not be null");
        this.methodParameter = methodParameter;
        this.declaringClass = methodParameter.getDeclaringClass();
        this.containingClass = methodParameter.getContainingClass();
        if (this.methodParameter.getMethod() != null) {
            this.methodName = methodParameter.getMethod().getName();
            this.parameterTypes = methodParameter.getMethod().getParameterTypes();
        }
        else {
            this.parameterTypes = methodParameter.getConstructor().getParameterTypes();
        }
        this.parameterIndex = methodParameter.getParameterIndex();
        this.required = required;
        this.eager = eager;
    }
    
    public DependencyDescriptor(final Field field, final boolean required) {
        this(field, required, true);
    }
    
    public DependencyDescriptor(final Field field, final boolean required, final boolean eager) {
        this.nestingLevel = 1;
        Assert.notNull(field, "Field must not be null");
        this.field = field;
        this.declaringClass = field.getDeclaringClass();
        this.fieldName = field.getName();
        this.required = required;
        this.eager = eager;
    }
    
    public DependencyDescriptor(final DependencyDescriptor original) {
        this.nestingLevel = 1;
        this.methodParameter = ((original.methodParameter != null) ? new MethodParameter(original.methodParameter) : null);
        this.field = original.field;
        this.declaringClass = original.declaringClass;
        this.containingClass = original.containingClass;
        this.methodName = original.methodName;
        this.parameterTypes = original.parameterTypes;
        this.parameterIndex = original.parameterIndex;
        this.fieldName = original.fieldName;
        this.required = original.required;
        this.eager = original.eager;
        this.nestingLevel = original.nestingLevel;
        this.fieldAnnotations = original.fieldAnnotations;
    }
    
    public MethodParameter getMethodParameter() {
        return this.methodParameter;
    }
    
    public Field getField() {
        return this.field;
    }
    
    public boolean isRequired() {
        return this.required;
    }
    
    public boolean isEager() {
        return this.eager;
    }
    
    public void increaseNestingLevel() {
        ++this.nestingLevel;
        if (this.methodParameter != null) {
            this.methodParameter.increaseNestingLevel();
        }
    }
    
    public void setContainingClass(final Class<?> containingClass) {
        this.containingClass = containingClass;
        if (this.methodParameter != null) {
            GenericTypeResolver.resolveParameterType(this.methodParameter, containingClass);
        }
    }
    
    public ResolvableType getResolvableType() {
        return (this.field != null) ? ResolvableType.forField(this.field, this.nestingLevel, this.containingClass) : ResolvableType.forMethodParameter(this.methodParameter);
    }
    
    public boolean fallbackMatchAllowed() {
        return false;
    }
    
    public DependencyDescriptor forFallbackMatch() {
        return new DependencyDescriptor(this) {
            @Override
            public boolean fallbackMatchAllowed() {
                return true;
            }
        };
    }
    
    public void initParameterNameDiscovery(final ParameterNameDiscoverer parameterNameDiscoverer) {
        if (this.methodParameter != null) {
            this.methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
        }
    }
    
    public String getDependencyName() {
        return (this.field != null) ? this.field.getName() : this.methodParameter.getParameterName();
    }
    
    public Class<?> getDependencyType() {
        if (this.field == null) {
            return this.methodParameter.getNestedParameterType();
        }
        if (this.nestingLevel > 1) {
            final Type type = this.field.getGenericType();
            if (type instanceof ParameterizedType) {
                final Type[] args = ((ParameterizedType)type).getActualTypeArguments();
                Type arg = args[args.length - 1];
                if (arg instanceof Class) {
                    return (Class<?>)arg;
                }
                if (arg instanceof ParameterizedType) {
                    arg = ((ParameterizedType)arg).getRawType();
                    if (arg instanceof Class) {
                        return (Class<?>)arg;
                    }
                }
            }
            return Object.class;
        }
        return this.field.getType();
    }
    
    public Class<?> getCollectionType() {
        return (this.field != null) ? GenericCollectionTypeResolver.getCollectionFieldType(this.field, this.nestingLevel) : GenericCollectionTypeResolver.getCollectionParameterType(this.methodParameter);
    }
    
    public Class<?> getMapKeyType() {
        return (this.field != null) ? GenericCollectionTypeResolver.getMapKeyFieldType(this.field, this.nestingLevel) : GenericCollectionTypeResolver.getMapKeyParameterType(this.methodParameter);
    }
    
    public Class<?> getMapValueType() {
        return (this.field != null) ? GenericCollectionTypeResolver.getMapValueFieldType(this.field, this.nestingLevel) : GenericCollectionTypeResolver.getMapValueParameterType(this.methodParameter);
    }
    
    public Annotation[] getAnnotations() {
        if (this.field != null) {
            if (this.fieldAnnotations == null) {
                this.fieldAnnotations = this.field.getAnnotations();
            }
            return this.fieldAnnotations;
        }
        return this.methodParameter.getParameterAnnotations();
    }
    
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        try {
            if (this.fieldName != null) {
                this.field = this.declaringClass.getDeclaredField(this.fieldName);
            }
            else {
                if (this.methodName != null) {
                    this.methodParameter = new MethodParameter(this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes), this.parameterIndex);
                }
                else {
                    this.methodParameter = new MethodParameter(this.declaringClass.getDeclaredConstructor(this.parameterTypes), this.parameterIndex);
                }
                for (int i = 1; i < this.nestingLevel; ++i) {
                    this.methodParameter.increaseNestingLevel();
                }
            }
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Could not find original class structure", ex);
        }
    }
}
