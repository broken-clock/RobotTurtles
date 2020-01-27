// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.support;

import java.lang.reflect.Array;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import java.lang.reflect.Method;
import org.springframework.expression.EvaluationException;
import org.springframework.core.MethodParameter;
import org.springframework.util.ClassUtils;
import org.springframework.util.Assert;
import org.springframework.expression.TypeConverter;
import org.springframework.core.convert.TypeDescriptor;
import java.util.List;

public class ReflectionHelper
{
    static ArgumentsMatchInfo compareArguments(final List<TypeDescriptor> expectedArgTypes, final List<TypeDescriptor> suppliedArgTypes, final TypeConverter typeConverter) {
        Assert.isTrue(expectedArgTypes.size() == suppliedArgTypes.size(), "Expected argument types and supplied argument types should be arrays of same length");
        ArgumentsMatchKind match = ArgumentsMatchKind.EXACT;
        for (int i = 0; i < expectedArgTypes.size() && match != null; ++i) {
            final TypeDescriptor suppliedArg = suppliedArgTypes.get(i);
            final TypeDescriptor expectedArg = expectedArgTypes.get(i);
            if (!expectedArg.equals(suppliedArg)) {
                if (suppliedArg == null) {
                    if (expectedArg.isPrimitive()) {
                        match = null;
                    }
                }
                else if (suppliedArg.isAssignableTo(expectedArg)) {
                    if (match != ArgumentsMatchKind.REQUIRES_CONVERSION) {
                        match = ArgumentsMatchKind.CLOSE;
                    }
                }
                else if (typeConverter.canConvert(suppliedArg, expectedArg)) {
                    match = ArgumentsMatchKind.REQUIRES_CONVERSION;
                }
                else {
                    match = null;
                }
            }
        }
        return (match != null) ? new ArgumentsMatchInfo(match) : null;
    }
    
    public static int getTypeDifferenceWeight(final List<TypeDescriptor> paramTypes, final List<TypeDescriptor> argTypes) {
        int result = 0;
        for (int i = 0; i < paramTypes.size(); ++i) {
            final TypeDescriptor paramType = paramTypes.get(i);
            final TypeDescriptor argType = argTypes.get(i);
            if (argType == null) {
                if (paramType.isPrimitive()) {
                    return Integer.MAX_VALUE;
                }
            }
            else {
                Class<?> paramTypeClazz = paramType.getType();
                if (!ClassUtils.isAssignable(paramTypeClazz, argType.getType())) {
                    return Integer.MAX_VALUE;
                }
                if (paramTypeClazz.isPrimitive()) {
                    paramTypeClazz = Object.class;
                }
                Class<?> superClass = argType.getType().getSuperclass();
                while (superClass != null) {
                    if (paramTypeClazz.equals(superClass)) {
                        result += 2;
                        superClass = null;
                    }
                    else if (ClassUtils.isAssignable(paramTypeClazz, superClass)) {
                        result += 2;
                        superClass = superClass.getSuperclass();
                    }
                    else {
                        superClass = null;
                    }
                }
                if (paramTypeClazz.isInterface()) {
                    ++result;
                }
            }
        }
        return result;
    }
    
    static ArgumentsMatchInfo compareArgumentsVarargs(final List<TypeDescriptor> expectedArgTypes, final List<TypeDescriptor> suppliedArgTypes, final TypeConverter typeConverter) {
        Assert.isTrue(expectedArgTypes != null && expectedArgTypes.size() > 0, "Expected arguments must at least include one array (the vargargs parameter)");
        Assert.isTrue(expectedArgTypes.get(expectedArgTypes.size() - 1).isArray(), "Final expected argument should be array type (the varargs parameter)");
        ArgumentsMatchKind match = ArgumentsMatchKind.EXACT;
        for (int argCountUpToVarargs = expectedArgTypes.size() - 1, i = 0; i < argCountUpToVarargs && match != null; ++i) {
            final TypeDescriptor suppliedArg = suppliedArgTypes.get(i);
            final TypeDescriptor expectedArg = expectedArgTypes.get(i);
            if (suppliedArg == null) {
                if (expectedArg.isPrimitive()) {
                    match = null;
                }
            }
            else if (!expectedArg.equals(suppliedArg)) {
                if (suppliedArg.isAssignableTo(expectedArg)) {
                    if (match != ArgumentsMatchKind.REQUIRES_CONVERSION) {
                        match = ArgumentsMatchKind.CLOSE;
                    }
                }
                else if (typeConverter.canConvert(suppliedArg, expectedArg)) {
                    match = ArgumentsMatchKind.REQUIRES_CONVERSION;
                }
                else {
                    match = null;
                }
            }
        }
        if (match == null) {
            return null;
        }
        if (suppliedArgTypes.size() != expectedArgTypes.size() || !expectedArgTypes.get(expectedArgTypes.size() - 1).equals(suppliedArgTypes.get(suppliedArgTypes.size() - 1))) {
            final Class<?> varargsParamType = expectedArgTypes.get(expectedArgTypes.size() - 1).getElementTypeDescriptor().getType();
            for (int j = expectedArgTypes.size() - 1; j < suppliedArgTypes.size(); ++j) {
                final TypeDescriptor suppliedArg2 = suppliedArgTypes.get(j);
                if (suppliedArg2 == null) {
                    if (varargsParamType.isPrimitive()) {
                        match = null;
                    }
                }
                else if (varargsParamType != suppliedArg2.getType()) {
                    if (ClassUtils.isAssignable(varargsParamType, suppliedArg2.getType())) {
                        if (match != ArgumentsMatchKind.REQUIRES_CONVERSION) {
                            match = ArgumentsMatchKind.CLOSE;
                        }
                    }
                    else if (typeConverter.canConvert(suppliedArg2, TypeDescriptor.valueOf(varargsParamType))) {
                        match = ArgumentsMatchKind.REQUIRES_CONVERSION;
                    }
                    else {
                        match = null;
                    }
                }
            }
        }
        return (match != null) ? new ArgumentsMatchInfo(match) : null;
    }
    
    static void convertArguments(final TypeConverter converter, final Object[] arguments, final Object methodOrCtor, final Integer varargsPosition) throws EvaluationException {
        if (varargsPosition == null) {
            for (int i = 0; i < arguments.length; ++i) {
                final TypeDescriptor targetType = new TypeDescriptor(MethodParameter.forMethodOrConstructor(methodOrCtor, i));
                final Object argument = arguments[i];
                arguments[i] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
            }
        }
        else {
            for (int i = 0; i < varargsPosition; ++i) {
                final TypeDescriptor targetType = new TypeDescriptor(MethodParameter.forMethodOrConstructor(methodOrCtor, i));
                final Object argument = arguments[i];
                arguments[i] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
            }
            final MethodParameter methodParam = MethodParameter.forMethodOrConstructor(methodOrCtor, varargsPosition);
            if (varargsPosition == arguments.length - 1) {
                final TypeDescriptor targetType = new TypeDescriptor(methodParam);
                final Object argument = arguments[varargsPosition];
                arguments[varargsPosition] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
            }
            else {
                final TypeDescriptor targetType = TypeDescriptor.nested(methodParam, 1);
                for (int j = varargsPosition; j < arguments.length; ++j) {
                    final Object argument2 = arguments[j];
                    arguments[j] = converter.convertValue(argument2, TypeDescriptor.forObject(argument2), targetType);
                }
            }
        }
    }
    
    public static void convertAllArguments(final TypeConverter converter, final Object[] arguments, final Method method) throws SpelEvaluationException {
        Integer varargsPosition = null;
        if (method.isVarArgs()) {
            final Class<?>[] paramTypes = method.getParameterTypes();
            varargsPosition = paramTypes.length - 1;
        }
        for (int argPos = 0; argPos < arguments.length; ++argPos) {
            TypeDescriptor targetType;
            if (varargsPosition != null && argPos >= varargsPosition) {
                final MethodParameter methodParam = new MethodParameter(method, varargsPosition);
                targetType = TypeDescriptor.nested(methodParam, 1);
            }
            else {
                targetType = new TypeDescriptor(new MethodParameter(method, argPos));
            }
            try {
                final Object argument = arguments[argPos];
                if (argument != null && !targetType.getObjectType().isInstance(argument)) {
                    if (converter == null) {
                        throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, new Object[] { argument.getClass().getName(), targetType });
                    }
                    arguments[argPos] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
                }
            }
            catch (EvaluationException ex) {
                if (ex instanceof SpelEvaluationException) {
                    throw (SpelEvaluationException)ex;
                }
                throw new SpelEvaluationException(ex, SpelMessage.TYPE_CONVERSION_ERROR, new Object[] { arguments[argPos].getClass().getName(), targetType });
            }
        }
    }
    
    public static Object[] setupArgumentsForVarargsInvocation(final Class<?>[] requiredParameterTypes, final Object... args) {
        final int parameterCount = requiredParameterTypes.length;
        final int argumentCount = args.length;
        if (parameterCount != args.length || requiredParameterTypes[parameterCount - 1] != ((args[argumentCount - 1] != null) ? args[argumentCount - 1].getClass() : null)) {
            int arraySize = 0;
            if (argumentCount >= parameterCount) {
                arraySize = argumentCount - (parameterCount - 1);
            }
            final Object[] newArgs = new Object[parameterCount];
            System.arraycopy(args, 0, newArgs, 0, newArgs.length - 1);
            final Class<?> componentType = requiredParameterTypes[parameterCount - 1].getComponentType();
            final Object repackagedArgs = Array.newInstance(componentType, arraySize);
            for (int i = 0; i < arraySize; ++i) {
                Array.set(repackagedArgs, i, args[parameterCount - 1 + i]);
            }
            newArgs[newArgs.length - 1] = repackagedArgs;
            return newArgs;
        }
        return args;
    }
    
    enum ArgumentsMatchKind
    {
        EXACT, 
        CLOSE, 
        REQUIRES_CONVERSION;
    }
    
    static class ArgumentsMatchInfo
    {
        private final ArgumentsMatchKind kind;
        
        ArgumentsMatchInfo(final ArgumentsMatchKind kind) {
            this.kind = kind;
        }
        
        public boolean isExactMatch() {
            return this.kind == ArgumentsMatchKind.EXACT;
        }
        
        public boolean isCloseMatch() {
            return this.kind == ArgumentsMatchKind.CLOSE;
        }
        
        public boolean isMatchRequiringConversion() {
            return this.kind == ArgumentsMatchKind.REQUIRES_CONVERSION;
        }
        
        @Override
        public String toString() {
            return "ArgumentMatchInfo: " + this.kind;
        }
    }
}
