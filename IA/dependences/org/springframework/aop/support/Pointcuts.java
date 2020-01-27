// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.aop.MethodMatcher;
import org.springframework.util.Assert;
import java.lang.reflect.Method;
import org.springframework.aop.Pointcut;

public abstract class Pointcuts
{
    public static final Pointcut SETTERS;
    public static final Pointcut GETTERS;
    
    public static Pointcut union(final Pointcut pc1, final Pointcut pc2) {
        return new ComposablePointcut(pc1).union(pc2);
    }
    
    public static Pointcut intersection(final Pointcut pc1, final Pointcut pc2) {
        return new ComposablePointcut(pc1).intersection(pc2);
    }
    
    public static boolean matches(final Pointcut pointcut, final Method method, final Class<?> targetClass, final Object[] args) {
        Assert.notNull(pointcut, "Pointcut must not be null");
        if (pointcut == Pointcut.TRUE) {
            return true;
        }
        if (pointcut.getClassFilter().matches(targetClass)) {
            final MethodMatcher mm = pointcut.getMethodMatcher();
            if (mm.matches(method, targetClass)) {
                return !mm.isRuntime() || mm.matches(method, targetClass, args);
            }
        }
        return false;
    }
    
    static {
        SETTERS = SetterPointcut.INSTANCE;
        GETTERS = GetterPointcut.INSTANCE;
    }
    
    private static class SetterPointcut extends StaticMethodMatcherPointcut implements Serializable
    {
        public static SetterPointcut INSTANCE;
        
        @Override
        public boolean matches(final Method method, final Class<?> targetClass) {
            return method.getName().startsWith("set") && method.getParameterTypes().length == 1 && method.getReturnType() == Void.TYPE;
        }
        
        private Object readResolve() {
            return SetterPointcut.INSTANCE;
        }
        
        static {
            SetterPointcut.INSTANCE = new SetterPointcut();
        }
    }
    
    private static class GetterPointcut extends StaticMethodMatcherPointcut implements Serializable
    {
        public static GetterPointcut INSTANCE;
        
        @Override
        public boolean matches(final Method method, final Class<?> targetClass) {
            return method.getName().startsWith("get") && method.getParameterTypes().length == 0;
        }
        
        private Object readResolve() {
            return GetterPointcut.INSTANCE;
        }
        
        static {
            GetterPointcut.INSTANCE = new GetterPointcut();
        }
    }
}
