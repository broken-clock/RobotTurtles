// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.proxy;

import org.springframework.asm.Type;

class CallbackInfo
{
    private Class cls;
    private CallbackGenerator generator;
    private Type type;
    private static final CallbackInfo[] CALLBACKS;
    
    public static Type[] determineTypes(final Class[] callbackTypes) {
        final Type[] types = new Type[callbackTypes.length];
        for (int i = 0; i < types.length; ++i) {
            types[i] = determineType(callbackTypes[i]);
        }
        return types;
    }
    
    public static Type[] determineTypes(final Callback[] callbacks) {
        final Type[] types = new Type[callbacks.length];
        for (int i = 0; i < types.length; ++i) {
            types[i] = determineType(callbacks[i]);
        }
        return types;
    }
    
    public static CallbackGenerator[] getGenerators(final Type[] callbackTypes) {
        final CallbackGenerator[] generators = new CallbackGenerator[callbackTypes.length];
        for (int i = 0; i < generators.length; ++i) {
            generators[i] = getGenerator(callbackTypes[i]);
        }
        return generators;
    }
    
    private CallbackInfo(final Class cls, final CallbackGenerator generator) {
        this.cls = cls;
        this.generator = generator;
        this.type = Type.getType(cls);
    }
    
    private static Type determineType(final Callback callback) {
        if (callback == null) {
            throw new IllegalStateException("Callback is null");
        }
        return determineType(callback.getClass());
    }
    
    private static Type determineType(final Class callbackType) {
        Class cur = null;
        for (int i = 0; i < CallbackInfo.CALLBACKS.length; ++i) {
            final CallbackInfo info = CallbackInfo.CALLBACKS[i];
            if (info.cls.isAssignableFrom(callbackType)) {
                if (cur != null) {
                    throw new IllegalStateException("Callback implements both " + cur + " and " + info.cls);
                }
                cur = info.cls;
            }
        }
        if (cur == null) {
            throw new IllegalStateException("Unknown callback type " + callbackType);
        }
        return Type.getType(cur);
    }
    
    private static CallbackGenerator getGenerator(final Type callbackType) {
        for (int i = 0; i < CallbackInfo.CALLBACKS.length; ++i) {
            final CallbackInfo info = CallbackInfo.CALLBACKS[i];
            if (info.type.equals(callbackType)) {
                return info.generator;
            }
        }
        throw new IllegalStateException("Unknown callback type " + callbackType);
    }
    
    static {
        CALLBACKS = new CallbackInfo[] { new CallbackInfo(NoOp.class, NoOpGenerator.INSTANCE), new CallbackInfo(MethodInterceptor.class, MethodInterceptorGenerator.INSTANCE), new CallbackInfo(InvocationHandler.class, InvocationHandlerGenerator.INSTANCE), new CallbackInfo(LazyLoader.class, LazyLoaderGenerator.INSTANCE), new CallbackInfo(Dispatcher.class, DispatcherGenerator.INSTANCE), new CallbackInfo(FixedValue.class, FixedValueGenerator.INSTANCE), new CallbackInfo(ProxyRefDispatcher.class, DispatcherGenerator.PROXY_REF_INSTANCE) };
    }
}
