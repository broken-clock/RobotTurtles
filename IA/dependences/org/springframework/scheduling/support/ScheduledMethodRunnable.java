// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.support;

import java.lang.reflect.UndeclaredThrowableException;
import java.lang.reflect.InvocationTargetException;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;

public class ScheduledMethodRunnable implements Runnable
{
    private final Object target;
    private final Method method;
    
    public ScheduledMethodRunnable(final Object target, final Method method) {
        this.target = target;
        this.method = method;
    }
    
    public ScheduledMethodRunnable(final Object target, final String methodName) throws NoSuchMethodException {
        this.target = target;
        this.method = target.getClass().getMethod(methodName, (Class<?>[])new Class[0]);
    }
    
    public Object getTarget() {
        return this.target;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    @Override
    public void run() {
        try {
            ReflectionUtils.makeAccessible(this.method);
            this.method.invoke(this.target, new Object[0]);
        }
        catch (InvocationTargetException ex) {
            ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
        }
        catch (IllegalAccessException ex2) {
            throw new UndeclaredThrowableException(ex2);
        }
    }
}
