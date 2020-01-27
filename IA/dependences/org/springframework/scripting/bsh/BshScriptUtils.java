// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.bsh;

import org.springframework.core.NestedRuntimeException;
import bsh.Primitive;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import bsh.XThis;
import bsh.Interpreter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import bsh.EvalError;

public abstract class BshScriptUtils
{
    public static Object createBshObject(final String scriptSource) throws EvalError {
        return createBshObject(scriptSource, null, null);
    }
    
    public static Object createBshObject(final String scriptSource, final Class<?>... scriptInterfaces) throws EvalError {
        return createBshObject(scriptSource, scriptInterfaces, ClassUtils.getDefaultClassLoader());
    }
    
    public static Object createBshObject(final String scriptSource, final Class<?>[] scriptInterfaces, final ClassLoader classLoader) throws EvalError {
        final Object result = evaluateBshScript(scriptSource, scriptInterfaces, classLoader);
        if (result instanceof Class) {
            final Class<?> clazz = (Class<?>)result;
            try {
                return clazz.newInstance();
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Could not instantiate script class [" + clazz.getName() + "]. Root cause is " + ex);
            }
        }
        return result;
    }
    
    static Class<?> determineBshObjectType(final String scriptSource, final ClassLoader classLoader) throws EvalError {
        Assert.hasText(scriptSource, "Script source must not be empty");
        final Interpreter interpreter = new Interpreter();
        interpreter.setClassLoader(classLoader);
        final Object result = interpreter.eval(scriptSource);
        if (result instanceof Class) {
            return (Class<?>)result;
        }
        if (result != null) {
            return result.getClass();
        }
        return null;
    }
    
    static Object evaluateBshScript(final String scriptSource, final Class<?>[] scriptInterfaces, final ClassLoader classLoader) throws EvalError {
        Assert.hasText(scriptSource, "Script source must not be empty");
        final Interpreter interpreter = new Interpreter();
        interpreter.setClassLoader(classLoader);
        final Object result = interpreter.eval(scriptSource);
        if (result != null) {
            return result;
        }
        Assert.notEmpty(scriptInterfaces, "Given script requires a script proxy: At least one script interface is required.");
        final XThis xt = (XThis)interpreter.eval("return this");
        return Proxy.newProxyInstance(classLoader, scriptInterfaces, new BshObjectInvocationHandler(xt));
    }
    
    private static class BshObjectInvocationHandler implements InvocationHandler
    {
        private final XThis xt;
        
        public BshObjectInvocationHandler(final XThis xt) {
            this.xt = xt;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (ReflectionUtils.isEqualsMethod(method)) {
                return this.isProxyForSameBshObject(args[0]);
            }
            if (ReflectionUtils.isHashCodeMethod(method)) {
                return this.xt.hashCode();
            }
            if (ReflectionUtils.isToStringMethod(method)) {
                return "BeanShell object [" + this.xt + "]";
            }
            try {
                final Object result = this.xt.invokeMethod(method.getName(), args);
                if (result == Primitive.NULL || result == Primitive.VOID) {
                    return null;
                }
                if (result instanceof Primitive) {
                    return ((Primitive)result).getValue();
                }
                return result;
            }
            catch (EvalError ex) {
                throw new BshExecutionException(ex);
            }
        }
        
        private boolean isProxyForSameBshObject(final Object other) {
            if (!Proxy.isProxyClass(other.getClass())) {
                return false;
            }
            final InvocationHandler ih = Proxy.getInvocationHandler(other);
            return ih instanceof BshObjectInvocationHandler && this.xt.equals(((BshObjectInvocationHandler)ih).xt);
        }
    }
    
    public static class BshExecutionException extends NestedRuntimeException
    {
        private BshExecutionException(final EvalError ex) {
            super("BeanShell script execution failed", (Throwable)ex);
        }
    }
}
