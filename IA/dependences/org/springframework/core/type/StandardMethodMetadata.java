// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type;

import org.springframework.util.MultiValueMap;
import java.util.Map;
import java.lang.reflect.AnnotatedElement;
import org.springframework.core.annotation.AnnotatedElementUtils;
import java.lang.reflect.Modifier;
import org.springframework.util.Assert;
import java.lang.reflect.Method;

public class StandardMethodMetadata implements MethodMetadata
{
    private final Method introspectedMethod;
    private final boolean nestedAnnotationsAsMap;
    
    public StandardMethodMetadata(final Method introspectedMethod) {
        this(introspectedMethod, false);
    }
    
    public StandardMethodMetadata(final Method introspectedMethod, final boolean nestedAnnotationsAsMap) {
        Assert.notNull(introspectedMethod, "Method must not be null");
        this.introspectedMethod = introspectedMethod;
        this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
    }
    
    public final Method getIntrospectedMethod() {
        return this.introspectedMethod;
    }
    
    @Override
    public String getMethodName() {
        return this.introspectedMethod.getName();
    }
    
    @Override
    public String getDeclaringClassName() {
        return this.introspectedMethod.getDeclaringClass().getName();
    }
    
    @Override
    public boolean isStatic() {
        return Modifier.isStatic(this.introspectedMethod.getModifiers());
    }
    
    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedMethod.getModifiers());
    }
    
    @Override
    public boolean isOverridable() {
        return !this.isStatic() && !this.isFinal() && !Modifier.isPrivate(this.introspectedMethod.getModifiers());
    }
    
    @Override
    public boolean isAnnotated(final String annotationType) {
        return AnnotatedElementUtils.isAnnotated(this.introspectedMethod, annotationType);
    }
    
    @Override
    public Map<String, Object> getAnnotationAttributes(final String annotationType) {
        return this.getAnnotationAttributes(annotationType, false);
    }
    
    @Override
    public Map<String, Object> getAnnotationAttributes(final String annotationType, final boolean classValuesAsString) {
        return AnnotatedElementUtils.getAnnotationAttributes(this.introspectedMethod, annotationType, classValuesAsString, this.nestedAnnotationsAsMap);
    }
    
    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(final String annotationType) {
        return this.getAllAnnotationAttributes(annotationType, false);
    }
    
    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(final String annotationType, final boolean classValuesAsString) {
        return AnnotatedElementUtils.getAllAnnotationAttributes(this.introspectedMethod, annotationType, classValuesAsString, this.nestedAnnotationsAsMap);
    }
}
