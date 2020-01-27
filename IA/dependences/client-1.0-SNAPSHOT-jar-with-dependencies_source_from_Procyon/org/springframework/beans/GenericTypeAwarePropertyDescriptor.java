// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import org.springframework.core.GenericTypeResolver;
import org.apache.commons.logging.LogFactory;
import java.beans.IntrospectionException;
import java.util.HashSet;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import java.util.Set;
import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;

class GenericTypeAwarePropertyDescriptor extends PropertyDescriptor
{
    private final Class<?> beanClass;
    private final Method readMethod;
    private final Method writeMethod;
    private final Class<?> propertyEditorClass;
    private volatile Set<Method> ambiguousWriteMethods;
    private Class<?> propertyType;
    private MethodParameter writeMethodParameter;
    
    public GenericTypeAwarePropertyDescriptor(final Class<?> beanClass, final String propertyName, final Method readMethod, final Method writeMethod, final Class<?> propertyEditorClass) throws IntrospectionException {
        super(propertyName, null, null);
        this.beanClass = beanClass;
        this.propertyEditorClass = propertyEditorClass;
        final Method readMethodToUse = BridgeMethodResolver.findBridgedMethod(readMethod);
        Method writeMethodToUse = BridgeMethodResolver.findBridgedMethod(writeMethod);
        if (writeMethodToUse == null && readMethodToUse != null) {
            final Method candidate = ClassUtils.getMethodIfAvailable(this.beanClass, "set" + StringUtils.capitalize(this.getName()), (Class<?>[])null);
            if (candidate != null && candidate.getParameterTypes().length == 1) {
                writeMethodToUse = candidate;
            }
        }
        this.readMethod = readMethodToUse;
        this.writeMethod = writeMethodToUse;
        if (this.writeMethod != null && this.readMethod == null) {
            final Set<Method> ambiguousCandidates = new HashSet<Method>();
            for (final Method method : beanClass.getMethods()) {
                if (method.getName().equals(writeMethodToUse.getName()) && !method.equals(writeMethodToUse) && !method.isBridge()) {
                    ambiguousCandidates.add(method);
                }
            }
            if (!ambiguousCandidates.isEmpty()) {
                this.ambiguousWriteMethods = ambiguousCandidates;
            }
        }
    }
    
    public Class<?> getBeanClass() {
        return this.beanClass;
    }
    
    @Override
    public Method getReadMethod() {
        return this.readMethod;
    }
    
    @Override
    public Method getWriteMethod() {
        return this.writeMethod;
    }
    
    public Method getWriteMethodForActualAccess() {
        final Set<Method> ambiguousCandidates = this.ambiguousWriteMethods;
        if (ambiguousCandidates != null) {
            this.ambiguousWriteMethods = null;
            LogFactory.getLog(GenericTypeAwarePropertyDescriptor.class).warn("Invalid JavaBean property '" + this.getName() + "' being accessed! Ambiguous write methods found next to actually used [" + this.writeMethod + "]: " + ambiguousCandidates);
        }
        return this.writeMethod;
    }
    
    @Override
    public Class<?> getPropertyEditorClass() {
        return this.propertyEditorClass;
    }
    
    @Override
    public synchronized Class<?> getPropertyType() {
        if (this.propertyType == null) {
            if (this.readMethod != null) {
                this.propertyType = GenericTypeResolver.resolveReturnType(this.readMethod, this.beanClass);
            }
            else {
                final MethodParameter writeMethodParam = this.getWriteMethodParameter();
                if (writeMethodParam != null) {
                    this.propertyType = writeMethodParam.getParameterType();
                }
                else {
                    this.propertyType = super.getPropertyType();
                }
            }
        }
        return this.propertyType;
    }
    
    public synchronized MethodParameter getWriteMethodParameter() {
        if (this.writeMethod == null) {
            return null;
        }
        if (this.writeMethodParameter == null) {
            GenericTypeResolver.resolveParameterType(this.writeMethodParameter = new MethodParameter(this.writeMethod, 0), this.beanClass);
        }
        return this.writeMethodParameter;
    }
}
