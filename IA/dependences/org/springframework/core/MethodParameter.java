// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.util.HashMap;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import org.springframework.util.Assert;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class MethodParameter
{
    private final Method method;
    private final Constructor<?> constructor;
    private final int parameterIndex;
    private Class<?> containingClass;
    private Class<?> parameterType;
    private Type genericParameterType;
    private Annotation[] parameterAnnotations;
    private ParameterNameDiscoverer parameterNameDiscoverer;
    private String parameterName;
    private int nestingLevel;
    Map<Integer, Integer> typeIndexesPerLevel;
    
    public MethodParameter(final Method method, final int parameterIndex) {
        this(method, parameterIndex, 1);
    }
    
    public MethodParameter(final Method method, final int parameterIndex, final int nestingLevel) {
        this.nestingLevel = 1;
        Assert.notNull(method, "Method must not be null");
        this.method = method;
        this.parameterIndex = parameterIndex;
        this.nestingLevel = nestingLevel;
        this.constructor = null;
    }
    
    public MethodParameter(final Constructor<?> constructor, final int parameterIndex) {
        this(constructor, parameterIndex, 1);
    }
    
    public MethodParameter(final Constructor<?> constructor, final int parameterIndex, final int nestingLevel) {
        this.nestingLevel = 1;
        Assert.notNull(constructor, "Constructor must not be null");
        this.constructor = constructor;
        this.parameterIndex = parameterIndex;
        this.nestingLevel = nestingLevel;
        this.method = null;
    }
    
    public MethodParameter(final MethodParameter original) {
        this.nestingLevel = 1;
        Assert.notNull(original, "Original must not be null");
        this.method = original.method;
        this.constructor = original.constructor;
        this.parameterIndex = original.parameterIndex;
        this.containingClass = original.containingClass;
        this.parameterType = original.parameterType;
        this.genericParameterType = original.genericParameterType;
        this.parameterAnnotations = original.parameterAnnotations;
        this.parameterNameDiscoverer = original.parameterNameDiscoverer;
        this.parameterName = original.parameterName;
        this.nestingLevel = original.nestingLevel;
        this.typeIndexesPerLevel = original.typeIndexesPerLevel;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public Constructor<?> getConstructor() {
        return this.constructor;
    }
    
    public Member getMember() {
        if (this.method != null) {
            return this.method;
        }
        return this.constructor;
    }
    
    public AnnotatedElement getAnnotatedElement() {
        if (this.method != null) {
            return this.method;
        }
        return this.constructor;
    }
    
    public Class<?> getDeclaringClass() {
        return this.getMember().getDeclaringClass();
    }
    
    public int getParameterIndex() {
        return this.parameterIndex;
    }
    
    void setContainingClass(final Class<?> containingClass) {
        this.containingClass = containingClass;
    }
    
    public Class<?> getContainingClass() {
        return (this.containingClass != null) ? this.containingClass : this.getDeclaringClass();
    }
    
    void setParameterType(final Class<?> parameterType) {
        this.parameterType = parameterType;
    }
    
    public Class<?> getParameterType() {
        if (this.parameterType == null) {
            if (this.parameterIndex < 0) {
                this.parameterType = ((this.method != null) ? this.method.getReturnType() : null);
            }
            else {
                this.parameterType = ((this.method != null) ? this.method.getParameterTypes()[this.parameterIndex] : this.constructor.getParameterTypes()[this.parameterIndex]);
            }
        }
        return this.parameterType;
    }
    
    public Type getGenericParameterType() {
        if (this.genericParameterType == null) {
            if (this.parameterIndex < 0) {
                this.genericParameterType = ((this.method != null) ? this.method.getGenericReturnType() : null);
            }
            else {
                this.genericParameterType = ((this.method != null) ? this.method.getGenericParameterTypes()[this.parameterIndex] : this.constructor.getGenericParameterTypes()[this.parameterIndex]);
            }
        }
        return this.genericParameterType;
    }
    
    public Class<?> getNestedParameterType() {
        if (this.nestingLevel > 1) {
            final Type type = this.getGenericParameterType();
            if (type instanceof ParameterizedType) {
                final Integer index = this.getTypeIndexForCurrentLevel();
                final Type[] args = ((ParameterizedType)type).getActualTypeArguments();
                Type arg = args[(index != null) ? ((int)index) : (args.length - 1)];
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
        return this.getParameterType();
    }
    
    public Annotation[] getMethodAnnotations() {
        return this.getAnnotatedElement().getAnnotations();
    }
    
    public <T extends Annotation> T getMethodAnnotation(final Class<T> annotationType) {
        return this.getAnnotatedElement().getAnnotation(annotationType);
    }
    
    public Annotation[] getParameterAnnotations() {
        if (this.parameterAnnotations == null) {
            final Annotation[][] annotationArray = (this.method != null) ? this.method.getParameterAnnotations() : this.constructor.getParameterAnnotations();
            if (this.parameterIndex >= 0 && this.parameterIndex < annotationArray.length) {
                this.parameterAnnotations = annotationArray[this.parameterIndex];
            }
            else {
                this.parameterAnnotations = new Annotation[0];
            }
        }
        return this.parameterAnnotations;
    }
    
    public <T extends Annotation> T getParameterAnnotation(final Class<T> annotationType) {
        final Annotation[] parameterAnnotations;
        final Annotation[] anns = parameterAnnotations = this.getParameterAnnotations();
        for (final Annotation ann : parameterAnnotations) {
            if (annotationType.isInstance(ann)) {
                return (T)ann;
            }
        }
        return null;
    }
    
    public boolean hasParameterAnnotations() {
        return this.getParameterAnnotations().length != 0;
    }
    
    public <T extends Annotation> boolean hasParameterAnnotation(final Class<T> annotationType) {
        return this.getParameterAnnotation(annotationType) != null;
    }
    
    public void initParameterNameDiscovery(final ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }
    
    public String getParameterName() {
        if (this.parameterNameDiscoverer != null) {
            final String[] parameterNames = (this.method != null) ? this.parameterNameDiscoverer.getParameterNames(this.method) : this.parameterNameDiscoverer.getParameterNames(this.constructor);
            if (parameterNames != null) {
                this.parameterName = parameterNames[this.parameterIndex];
            }
            this.parameterNameDiscoverer = null;
        }
        return this.parameterName;
    }
    
    public void increaseNestingLevel() {
        ++this.nestingLevel;
    }
    
    public void decreaseNestingLevel() {
        this.getTypeIndexesPerLevel().remove(this.nestingLevel);
        --this.nestingLevel;
    }
    
    public int getNestingLevel() {
        return this.nestingLevel;
    }
    
    public void setTypeIndexForCurrentLevel(final int typeIndex) {
        this.getTypeIndexesPerLevel().put(this.nestingLevel, typeIndex);
    }
    
    public Integer getTypeIndexForCurrentLevel() {
        return this.getTypeIndexForLevel(this.nestingLevel);
    }
    
    public Integer getTypeIndexForLevel(final int nestingLevel) {
        return this.getTypeIndexesPerLevel().get(nestingLevel);
    }
    
    private Map<Integer, Integer> getTypeIndexesPerLevel() {
        if (this.typeIndexesPerLevel == null) {
            this.typeIndexesPerLevel = new HashMap<Integer, Integer>(4);
        }
        return this.typeIndexesPerLevel;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj instanceof MethodParameter) {
            final MethodParameter other = (MethodParameter)obj;
            return this.parameterIndex == other.parameterIndex && this.getMember().equals(other.getMember());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getMember().hashCode() * 31 + this.parameterIndex;
    }
    
    public static MethodParameter forMethodOrConstructor(final Object methodOrConstructor, final int parameterIndex) {
        if (methodOrConstructor instanceof Method) {
            return new MethodParameter((Method)methodOrConstructor, parameterIndex);
        }
        if (methodOrConstructor instanceof Constructor) {
            return new MethodParameter((Constructor<?>)methodOrConstructor, parameterIndex);
        }
        throw new IllegalArgumentException("Given object [" + methodOrConstructor + "] is neither a Method nor a Constructor");
    }
}
