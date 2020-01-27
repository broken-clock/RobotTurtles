// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.support;

import org.springframework.beans.TypeMismatchException;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import org.springframework.beans.PropertyEditorRegistry;
import java.beans.PropertyEditor;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.util.MethodInvoker;

public class ArgumentConvertingMethodInvoker extends MethodInvoker
{
    private TypeConverter typeConverter;
    private boolean useDefaultConverter;
    
    public ArgumentConvertingMethodInvoker() {
        this.useDefaultConverter = true;
    }
    
    public void setTypeConverter(final TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
        this.useDefaultConverter = false;
    }
    
    public TypeConverter getTypeConverter() {
        if (this.typeConverter == null && this.useDefaultConverter) {
            this.typeConverter = this.getDefaultTypeConverter();
        }
        return this.typeConverter;
    }
    
    protected TypeConverter getDefaultTypeConverter() {
        return new SimpleTypeConverter();
    }
    
    public void registerCustomEditor(final Class<?> requiredType, final PropertyEditor propertyEditor) {
        final TypeConverter converter = this.getTypeConverter();
        if (!(converter instanceof PropertyEditorRegistry)) {
            throw new IllegalStateException("TypeConverter does not implement PropertyEditorRegistry interface: " + converter);
        }
        ((PropertyEditorRegistry)converter).registerCustomEditor(requiredType, propertyEditor);
    }
    
    @Override
    protected Method findMatchingMethod() {
        Method matchingMethod = super.findMatchingMethod();
        if (matchingMethod == null) {
            matchingMethod = this.doFindMatchingMethod(this.getArguments());
        }
        if (matchingMethod == null) {
            matchingMethod = this.doFindMatchingMethod(new Object[] { this.getArguments() });
        }
        return matchingMethod;
    }
    
    protected Method doFindMatchingMethod(final Object[] arguments) {
        final TypeConverter converter = this.getTypeConverter();
        if (converter != null) {
            final String targetMethod = this.getTargetMethod();
            Method matchingMethod = null;
            final int argCount = arguments.length;
            final Method[] candidates = ReflectionUtils.getAllDeclaredMethods(this.getTargetClass());
            int minTypeDiffWeight = Integer.MAX_VALUE;
            Object[] argumentsToUse = null;
            for (final Method candidate : candidates) {
                if (candidate.getName().equals(targetMethod)) {
                    final Class<?>[] paramTypes = candidate.getParameterTypes();
                    if (paramTypes.length == argCount) {
                        final Object[] convertedArguments = new Object[argCount];
                        boolean match = true;
                        for (int j = 0; j < argCount && match; ++j) {
                            try {
                                convertedArguments[j] = converter.convertIfNecessary(arguments[j], paramTypes[j]);
                            }
                            catch (TypeMismatchException ex) {
                                match = false;
                            }
                        }
                        if (match) {
                            final int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, convertedArguments);
                            if (typeDiffWeight < minTypeDiffWeight) {
                                minTypeDiffWeight = typeDiffWeight;
                                matchingMethod = candidate;
                                argumentsToUse = convertedArguments;
                            }
                        }
                    }
                }
            }
            if (matchingMethod != null) {
                this.setArguments(argumentsToUse);
                return matchingMethod;
            }
        }
        return null;
    }
}
