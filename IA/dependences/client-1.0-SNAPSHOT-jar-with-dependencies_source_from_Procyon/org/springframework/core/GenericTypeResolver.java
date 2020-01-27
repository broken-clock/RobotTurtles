// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import org.springframework.util.ConcurrentReferenceHashMap;
import java.util.Collections;
import java.util.HashMap;
import java.lang.reflect.WildcardType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Method;
import org.springframework.util.Assert;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

public abstract class GenericTypeResolver
{
    private static final Map<Class<?>, Map<TypeVariable, Type>> typeVariableCache;
    
    @Deprecated
    public static Type getTargetType(final MethodParameter methodParam) {
        Assert.notNull(methodParam, "MethodParameter must not be null");
        return methodParam.getGenericParameterType();
    }
    
    public static Class<?> resolveParameterType(final MethodParameter methodParam, final Class<?> clazz) {
        Assert.notNull(methodParam, "MethodParameter must not be null");
        Assert.notNull(clazz, "Class must not be null");
        methodParam.setContainingClass(clazz);
        methodParam.setParameterType(ResolvableType.forMethodParameter(methodParam).resolve());
        return methodParam.getParameterType();
    }
    
    public static Class<?> resolveReturnType(final Method method, final Class<?> clazz) {
        Assert.notNull(method, "Method must not be null");
        Assert.notNull(clazz, "Class must not be null");
        return ResolvableType.forMethodReturnType(method, clazz).resolve(method.getReturnType());
    }
    
    public static Class<?> resolveReturnTypeForGenericMethod(final Method method, final Object[] args, final ClassLoader classLoader) {
        Assert.notNull(method, "Method must not be null");
        Assert.notNull(args, "Argument array must not be null");
        final TypeVariable<Method>[] declaredTypeVariables = method.getTypeParameters();
        final Type genericReturnType = method.getGenericReturnType();
        final Type[] methodArgumentTypes = method.getGenericParameterTypes();
        if (declaredTypeVariables.length == 0) {
            return method.getReturnType();
        }
        if (args.length < methodArgumentTypes.length) {
            return null;
        }
        boolean locallyDeclaredTypeVariableMatchesReturnType = false;
        for (final TypeVariable<Method> currentTypeVariable : declaredTypeVariables) {
            if (currentTypeVariable.equals(genericReturnType)) {
                locallyDeclaredTypeVariableMatchesReturnType = true;
                break;
            }
        }
        if (locallyDeclaredTypeVariableMatchesReturnType) {
            for (int i = 0; i < methodArgumentTypes.length; ++i) {
                final Type currentMethodArgumentType = methodArgumentTypes[i];
                if (currentMethodArgumentType.equals(genericReturnType)) {
                    return args[i].getClass();
                }
                if (currentMethodArgumentType instanceof ParameterizedType) {
                    final ParameterizedType parameterizedType = (ParameterizedType)currentMethodArgumentType;
                    final Type[] actualTypeArguments2;
                    final Type[] actualTypeArguments = actualTypeArguments2 = parameterizedType.getActualTypeArguments();
                    final int length2 = actualTypeArguments2.length;
                    int k = 0;
                    while (k < length2) {
                        final Type typeArg = actualTypeArguments2[k];
                        if (typeArg.equals(genericReturnType)) {
                            final Object arg = args[i];
                            if (arg instanceof Class) {
                                return (Class<?>)arg;
                            }
                            if (arg instanceof String && classLoader != null) {
                                try {
                                    return classLoader.loadClass((String)arg);
                                }
                                catch (ClassNotFoundException ex) {
                                    throw new IllegalStateException("Could not resolve specific class name argument [" + arg + "]", ex);
                                }
                            }
                            return method.getReturnType();
                        }
                        else {
                            ++k;
                        }
                    }
                }
            }
        }
        return method.getReturnType();
    }
    
    public static Class<?> resolveReturnTypeArgument(final Method method, final Class<?> genericIfc) {
        Assert.notNull(method, "method must not be null");
        final ResolvableType resolvableType = ResolvableType.forMethodReturnType(method).as(genericIfc);
        if (!resolvableType.hasGenerics() || resolvableType.getType() instanceof WildcardType) {
            return null;
        }
        return getSingleGeneric(resolvableType);
    }
    
    public static Class<?> resolveTypeArgument(final Class<?> clazz, final Class<?> genericIfc) {
        final ResolvableType resolvableType = ResolvableType.forClass(clazz).as(genericIfc);
        if (!resolvableType.hasGenerics()) {
            return null;
        }
        return getSingleGeneric(resolvableType);
    }
    
    private static Class<?> getSingleGeneric(final ResolvableType resolvableType) {
        if (resolvableType.getGenerics().length > 1) {
            throw new IllegalArgumentException("Expected 1 type argument on generic interface [" + resolvableType + "] but found " + resolvableType.getGenerics().length);
        }
        return resolvableType.getGeneric(new int[0]).resolve();
    }
    
    public static Class<?>[] resolveTypeArguments(final Class<?> clazz, final Class<?> genericIfc) {
        final ResolvableType type = ResolvableType.forClass(clazz).as(genericIfc);
        if (!type.hasGenerics() || type.hasUnresolvableGenerics()) {
            return null;
        }
        return type.resolveGenerics();
    }
    
    @Deprecated
    public static Class<?> resolveType(final Type genericType, final Map<TypeVariable, Type> map) {
        return ResolvableType.forType(genericType, new TypeVariableMapVariableResolver(map)).resolve(Object.class);
    }
    
    @Deprecated
    public static Map<TypeVariable, Type> getTypeVariableMap(final Class<?> clazz) {
        Map<TypeVariable, Type> typeVariableMap = GenericTypeResolver.typeVariableCache.get(clazz);
        if (typeVariableMap == null) {
            typeVariableMap = new HashMap<TypeVariable, Type>();
            buildTypeVariableMap(ResolvableType.forClass(clazz), typeVariableMap);
            GenericTypeResolver.typeVariableCache.put(clazz, (Map<TypeVariable, Type>)Collections.unmodifiableMap((Map<? extends TypeVariable, ? extends Type>)typeVariableMap));
        }
        return typeVariableMap;
    }
    
    private static void buildTypeVariableMap(final ResolvableType type, final Map<TypeVariable, Type> typeVariableMap) {
        if (type != ResolvableType.NONE) {
            if (type.getType() instanceof ParameterizedType) {
                final TypeVariable<?>[] variables = type.resolve().getTypeParameters();
                for (int i = 0; i < variables.length; ++i) {
                    ResolvableType generic;
                    for (generic = type.getGeneric(i); generic.getType() instanceof TypeVariable; generic = generic.resolveType()) {}
                    if (generic != ResolvableType.NONE) {
                        typeVariableMap.put(variables[i], generic.getType());
                    }
                }
            }
            buildTypeVariableMap(type.getSuperType(), typeVariableMap);
            for (final ResolvableType interfaceType : type.getInterfaces()) {
                buildTypeVariableMap(interfaceType, typeVariableMap);
            }
            if (type.resolve().isMemberClass()) {
                buildTypeVariableMap(ResolvableType.forClass(type.resolve().getEnclosingClass()), typeVariableMap);
            }
        }
    }
    
    static {
        typeVariableCache = new ConcurrentReferenceHashMap<Class<?>, Map<TypeVariable, Type>>();
    }
    
    private static class TypeVariableMapVariableResolver implements ResolvableType.VariableResolver
    {
        private final Map<TypeVariable, Type> typeVariableMap;
        
        public TypeVariableMapVariableResolver(final Map<TypeVariable, Type> typeVariableMap) {
            this.typeVariableMap = typeVariableMap;
        }
        
        @Override
        public ResolvableType resolveVariable(final TypeVariable<?> variable) {
            final Type type = this.typeVariableMap.get(variable);
            return (type != null) ? ResolvableType.forType(type) : null;
        }
        
        @Override
        public Object getSource() {
            return this.typeVariableMap;
        }
    }
}
