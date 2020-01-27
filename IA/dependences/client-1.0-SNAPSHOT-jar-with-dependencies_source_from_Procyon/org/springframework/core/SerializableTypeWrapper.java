// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.WildcardType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.io.Serializable;
import org.springframework.util.Assert;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import org.springframework.util.ConcurrentReferenceHashMap;
import java.lang.reflect.Method;

abstract class SerializableTypeWrapper
{
    private static final Class<?>[] SUPPORTED_SERIALIZABLE_TYPES;
    private static final Method EQUALS_METHOD;
    private static final Method GET_TYPE_PROVIDER_METHOD;
    private static final ConcurrentReferenceHashMap<Type, Type> cache;
    
    public static Type forField(final Field field) {
        Assert.notNull(field, "Field must not be null");
        return forTypeProvider(new FieldTypeProvider(field));
    }
    
    public static Type forMethodParameter(final MethodParameter methodParameter) {
        return forTypeProvider(new MethodParameterTypeProvider(methodParameter));
    }
    
    public static Type forGenericSuperclass(final Class<?> type) {
        return forTypeProvider(new DefaultTypeProvider() {
            @Override
            public Type getType() {
                return type.getGenericSuperclass();
            }
        });
    }
    
    public static Type[] forGenericInterfaces(final Class<?> type) {
        final Type[] result = new Type[type.getGenericInterfaces().length];
        for (int i = 0; i < result.length; ++i) {
            final int index = i;
            result[i] = forTypeProvider(new DefaultTypeProvider() {
                @Override
                public Type getType() {
                    return type.getGenericInterfaces()[index];
                }
            });
        }
        return result;
    }
    
    public static Type[] forTypeParameters(final Class<?> type) {
        final Type[] result = new Type[type.getTypeParameters().length];
        for (int i = 0; i < result.length; ++i) {
            final int index = i;
            result[i] = forTypeProvider(new DefaultTypeProvider() {
                @Override
                public Type getType() {
                    return type.getTypeParameters()[index];
                }
            });
        }
        return result;
    }
    
    public static <T extends Type> T unwrap(final T type) {
        Type unwrapped;
        for (unwrapped = type; unwrapped instanceof SerializableTypeProxy; unwrapped = ((SerializableTypeProxy)type).getTypeProvider().getType()) {}
        return (T)unwrapped;
    }
    
    static Type forTypeProvider(final TypeProvider provider) {
        Assert.notNull(provider, "Provider must not be null");
        if (provider.getType() instanceof Serializable || provider.getType() == null) {
            return provider.getType();
        }
        Type cached = SerializableTypeWrapper.cache.get(provider.getType());
        if (cached != null) {
            return cached;
        }
        for (final Class<?> type : SerializableTypeWrapper.SUPPORTED_SERIALIZABLE_TYPES) {
            if (type.isAssignableFrom(provider.getType().getClass())) {
                final ClassLoader classLoader = provider.getClass().getClassLoader();
                final Class<?>[] interfaces = (Class<?>[])new Class[] { type, SerializableTypeProxy.class, Serializable.class };
                final InvocationHandler handler = new TypeProxyInvocationHandler(provider);
                cached = (Type)Proxy.newProxyInstance(classLoader, interfaces, handler);
                SerializableTypeWrapper.cache.put(provider.getType(), cached);
                return cached;
            }
        }
        throw new IllegalArgumentException("Unsupported Type class " + provider.getType().getClass().getName());
    }
    
    static {
        SUPPORTED_SERIALIZABLE_TYPES = new Class[] { GenericArrayType.class, ParameterizedType.class, TypeVariable.class, WildcardType.class };
        EQUALS_METHOD = ReflectionUtils.findMethod(Object.class, "equals", Object.class);
        GET_TYPE_PROVIDER_METHOD = ReflectionUtils.findMethod(SerializableTypeProxy.class, "getTypeProvider");
        cache = new ConcurrentReferenceHashMap<Type, Type>(256);
    }
    
    private abstract static class DefaultTypeProvider implements TypeProvider
    {
        @Override
        public Object getSource() {
            return null;
        }
    }
    
    private static class TypeProxyInvocationHandler implements InvocationHandler, Serializable
    {
        private final TypeProvider provider;
        
        public TypeProxyInvocationHandler(final TypeProvider provider) {
            this.provider = provider;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (SerializableTypeWrapper.GET_TYPE_PROVIDER_METHOD.equals(method)) {
                return this.provider;
            }
            if (SerializableTypeWrapper.EQUALS_METHOD.equals(method)) {
                Object other = args[0];
                if (other instanceof Type) {
                    other = SerializableTypeWrapper.unwrap(other);
                }
                return this.provider.getType().equals(other);
            }
            if (Type.class.equals(method.getReturnType()) && args == null) {
                return SerializableTypeWrapper.forTypeProvider(new MethodInvokeTypeProvider(this.provider, method, -1));
            }
            if (Type[].class.equals(method.getReturnType()) && args == null) {
                final Type[] result = new Type[((Type[])method.invoke(this.provider.getType(), args)).length];
                for (int i = 0; i < result.length; ++i) {
                    result[i] = SerializableTypeWrapper.forTypeProvider(new MethodInvokeTypeProvider(this.provider, method, i));
                }
                return result;
            }
            try {
                return method.invoke(this.provider.getType(), args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
    
    static class FieldTypeProvider implements TypeProvider
    {
        private final String fieldName;
        private final Class<?> declaringClass;
        private transient Field field;
        
        public FieldTypeProvider(final Field field) {
            this.fieldName = field.getName();
            this.declaringClass = field.getDeclaringClass();
            this.field = field;
        }
        
        @Override
        public Type getType() {
            return this.field.getGenericType();
        }
        
        @Override
        public Object getSource() {
            return this.field;
        }
        
        private void readObject(final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
            inputStream.defaultReadObject();
            try {
                this.field = this.declaringClass.getDeclaredField(this.fieldName);
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Could not find original class structure", ex);
            }
        }
    }
    
    static class MethodParameterTypeProvider implements TypeProvider
    {
        private final String methodName;
        private final Class<?>[] parameterTypes;
        private final Class<?> declaringClass;
        private final int parameterIndex;
        private transient MethodParameter methodParameter;
        
        public MethodParameterTypeProvider(final MethodParameter methodParameter) {
            if (methodParameter.getMethod() != null) {
                this.methodName = methodParameter.getMethod().getName();
                this.parameterTypes = methodParameter.getMethod().getParameterTypes();
            }
            else {
                this.methodName = null;
                this.parameterTypes = methodParameter.getConstructor().getParameterTypes();
            }
            this.declaringClass = methodParameter.getDeclaringClass();
            this.parameterIndex = methodParameter.getParameterIndex();
            this.methodParameter = methodParameter;
        }
        
        @Override
        public Type getType() {
            return this.methodParameter.getGenericParameterType();
        }
        
        @Override
        public Object getSource() {
            return this.methodParameter;
        }
        
        private void readObject(final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
            inputStream.defaultReadObject();
            try {
                if (this.methodName != null) {
                    this.methodParameter = new MethodParameter(this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes), this.parameterIndex);
                }
                else {
                    this.methodParameter = new MethodParameter(this.declaringClass.getDeclaredConstructor(this.parameterTypes), this.parameterIndex);
                }
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Could not find original class structure", ex);
            }
        }
    }
    
    static class MethodInvokeTypeProvider implements TypeProvider
    {
        private final TypeProvider provider;
        private final String methodName;
        private final int index;
        private transient Object result;
        
        public MethodInvokeTypeProvider(final TypeProvider provider, final Method method, final int index) {
            this.provider = provider;
            this.methodName = method.getName();
            this.index = index;
            this.result = ReflectionUtils.invokeMethod(method, provider.getType());
        }
        
        @Override
        public Type getType() {
            if (this.result instanceof Type || this.result == null) {
                return (Type)this.result;
            }
            return ((Type[])this.result)[this.index];
        }
        
        @Override
        public Object getSource() {
            return null;
        }
        
        private void readObject(final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
            inputStream.defaultReadObject();
            final Method method = ReflectionUtils.findMethod(this.provider.getType().getClass(), this.methodName);
            this.result = ReflectionUtils.invokeMethod(method, this.provider.getType());
        }
    }
    
    interface TypeProvider extends Serializable
    {
        Type getType();
        
        Object getSource();
    }
    
    interface SerializableTypeProxy
    {
        TypeProvider getTypeProvider();
    }
}
