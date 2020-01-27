// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.io.Closeable;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;
import java.security.PrivilegedActionException;
import java.lang.reflect.InvocationTargetException;
import org.springframework.util.ReflectionUtils;
import org.springframework.beans.BeanUtils;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.ArrayList;
import org.springframework.util.Assert;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import java.util.List;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import org.apache.commons.logging.Log;
import java.io.Serializable;
import org.springframework.beans.factory.DisposableBean;

class DisposableBeanAdapter implements DisposableBean, Runnable, Serializable
{
    private static final String CLOSE_METHOD_NAME = "close";
    private static final String SHUTDOWN_METHOD_NAME = "shutdown";
    private static final Log logger;
    private static Class<?> closeableInterface;
    private final Object bean;
    private final String beanName;
    private final boolean invokeDisposableBean;
    private final boolean nonPublicAccessAllowed;
    private final AccessControlContext acc;
    private String destroyMethodName;
    private transient Method destroyMethod;
    private List<DestructionAwareBeanPostProcessor> beanPostProcessors;
    
    public DisposableBeanAdapter(final Object bean, final String beanName, final RootBeanDefinition beanDefinition, final List<BeanPostProcessor> postProcessors, final AccessControlContext acc) {
        Assert.notNull(bean, "Disposable bean must not be null");
        this.bean = bean;
        this.beanName = beanName;
        this.invokeDisposableBean = (this.bean instanceof DisposableBean && !beanDefinition.isExternallyManagedDestroyMethod("destroy"));
        this.nonPublicAccessAllowed = beanDefinition.isNonPublicAccessAllowed();
        this.acc = acc;
        final String destroyMethodName = this.inferDestroyMethodIfNecessary(bean, beanDefinition);
        if (destroyMethodName != null && (!this.invokeDisposableBean || !"destroy".equals(destroyMethodName)) && !beanDefinition.isExternallyManagedDestroyMethod(destroyMethodName)) {
            this.destroyMethodName = destroyMethodName;
            this.destroyMethod = this.determineDestroyMethod();
            if (this.destroyMethod == null) {
                if (beanDefinition.isEnforceDestroyMethod()) {
                    throw new BeanDefinitionValidationException("Couldn't find a destroy method named '" + destroyMethodName + "' on bean with name '" + beanName + "'");
                }
            }
            else {
                final Class<?>[] paramTypes = this.destroyMethod.getParameterTypes();
                if (paramTypes.length > 1) {
                    throw new BeanDefinitionValidationException("Method '" + destroyMethodName + "' of bean '" + beanName + "' has more than one parameter - not supported as destroy method");
                }
                if (paramTypes.length == 1 && !paramTypes[0].equals(Boolean.TYPE)) {
                    throw new BeanDefinitionValidationException("Method '" + destroyMethodName + "' of bean '" + beanName + "' has a non-boolean parameter - not supported as destroy method");
                }
            }
        }
        this.beanPostProcessors = this.filterPostProcessors(postProcessors);
    }
    
    public DisposableBeanAdapter(final Object bean, final List<BeanPostProcessor> postProcessors, final AccessControlContext acc) {
        Assert.notNull(bean, "Disposable bean must not be null");
        this.bean = bean;
        this.beanName = null;
        this.invokeDisposableBean = (this.bean instanceof DisposableBean);
        this.nonPublicAccessAllowed = true;
        this.acc = acc;
        this.beanPostProcessors = this.filterPostProcessors(postProcessors);
    }
    
    private DisposableBeanAdapter(final Object bean, final String beanName, final boolean invokeDisposableBean, final boolean nonPublicAccessAllowed, final String destroyMethodName, final List<DestructionAwareBeanPostProcessor> postProcessors) {
        this.bean = bean;
        this.beanName = beanName;
        this.invokeDisposableBean = invokeDisposableBean;
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
        this.acc = null;
        this.destroyMethodName = destroyMethodName;
        this.beanPostProcessors = postProcessors;
    }
    
    private String inferDestroyMethodIfNecessary(final Object bean, final RootBeanDefinition beanDefinition) {
        if ("(inferred)".equals(beanDefinition.getDestroyMethodName()) || (beanDefinition.getDestroyMethodName() == null && DisposableBeanAdapter.closeableInterface.isInstance(bean))) {
            if (!(bean instanceof DisposableBean)) {
                try {
                    return bean.getClass().getMethod("close", (Class<?>[])new Class[0]).getName();
                }
                catch (NoSuchMethodException ex) {
                    try {
                        return bean.getClass().getMethod("shutdown", (Class<?>[])new Class[0]).getName();
                    }
                    catch (NoSuchMethodException ex2) {}
                }
            }
            return null;
        }
        return beanDefinition.getDestroyMethodName();
    }
    
    private List<DestructionAwareBeanPostProcessor> filterPostProcessors(final List<BeanPostProcessor> postProcessors) {
        List<DestructionAwareBeanPostProcessor> filteredPostProcessors = null;
        if (postProcessors != null && !postProcessors.isEmpty()) {
            filteredPostProcessors = new ArrayList<DestructionAwareBeanPostProcessor>(postProcessors.size());
            for (final BeanPostProcessor postProcessor : postProcessors) {
                if (postProcessor instanceof DestructionAwareBeanPostProcessor) {
                    filteredPostProcessors.add((DestructionAwareBeanPostProcessor)postProcessor);
                }
            }
        }
        return filteredPostProcessors;
    }
    
    @Override
    public void run() {
        this.destroy();
    }
    
    @Override
    public void destroy() {
        if (this.beanPostProcessors != null && !this.beanPostProcessors.isEmpty()) {
            for (final DestructionAwareBeanPostProcessor processor : this.beanPostProcessors) {
                processor.postProcessBeforeDestruction(this.bean, this.beanName);
            }
        }
        if (this.invokeDisposableBean) {
            if (DisposableBeanAdapter.logger.isDebugEnabled()) {
                DisposableBeanAdapter.logger.debug("Invoking destroy() on bean with name '" + this.beanName + "'");
            }
            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                        @Override
                        public Object run() throws Exception {
                            ((DisposableBean)DisposableBeanAdapter.this.bean).destroy();
                            return null;
                        }
                    }, this.acc);
                }
                else {
                    ((DisposableBean)this.bean).destroy();
                }
            }
            catch (Throwable ex) {
                final String msg = "Invocation of destroy method failed on bean with name '" + this.beanName + "'";
                if (DisposableBeanAdapter.logger.isDebugEnabled()) {
                    DisposableBeanAdapter.logger.warn(msg, ex);
                }
                else {
                    DisposableBeanAdapter.logger.warn(msg + ": " + ex);
                }
            }
        }
        if (this.destroyMethod != null) {
            this.invokeCustomDestroyMethod(this.destroyMethod);
        }
        else if (this.destroyMethodName != null) {
            final Method methodToCall = this.determineDestroyMethod();
            if (methodToCall != null) {
                this.invokeCustomDestroyMethod(methodToCall);
            }
        }
    }
    
    private Method determineDestroyMethod() {
        try {
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged((PrivilegedAction<Method>)new PrivilegedAction<Method>() {
                    @Override
                    public Method run() {
                        return DisposableBeanAdapter.this.findDestroyMethod();
                    }
                });
            }
            return this.findDestroyMethod();
        }
        catch (IllegalArgumentException ex) {
            throw new BeanDefinitionValidationException("Couldn't find a unique destroy method on bean with name '" + this.beanName + ": " + ex.getMessage());
        }
    }
    
    private Method findDestroyMethod() {
        return this.nonPublicAccessAllowed ? BeanUtils.findMethodWithMinimalParameters(this.bean.getClass(), this.destroyMethodName) : BeanUtils.findMethodWithMinimalParameters(this.bean.getClass().getMethods(), this.destroyMethodName);
    }
    
    private void invokeCustomDestroyMethod(final Method destroyMethod) {
        final Class<?>[] paramTypes = destroyMethod.getParameterTypes();
        final Object[] args = new Object[paramTypes.length];
        if (paramTypes.length == 1) {
            args[0] = Boolean.TRUE;
        }
        if (DisposableBeanAdapter.logger.isDebugEnabled()) {
            DisposableBeanAdapter.logger.debug("Invoking destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "'");
        }
        try {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        ReflectionUtils.makeAccessible(destroyMethod);
                        return null;
                    }
                });
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                        @Override
                        public Object run() throws Exception {
                            destroyMethod.invoke(DisposableBeanAdapter.this.bean, args);
                            return null;
                        }
                    }, this.acc);
                    return;
                }
                catch (PrivilegedActionException pax) {
                    throw (InvocationTargetException)pax.getException();
                }
            }
            ReflectionUtils.makeAccessible(destroyMethod);
            destroyMethod.invoke(this.bean, args);
        }
        catch (InvocationTargetException ex) {
            final String msg = "Invocation of destroy method '" + this.destroyMethodName + "' failed on bean with name '" + this.beanName + "'";
            if (DisposableBeanAdapter.logger.isDebugEnabled()) {
                DisposableBeanAdapter.logger.warn(msg, ex.getTargetException());
            }
            else {
                DisposableBeanAdapter.logger.warn(msg + ": " + ex.getTargetException());
            }
        }
        catch (Throwable ex2) {
            DisposableBeanAdapter.logger.error("Couldn't invoke destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "'", ex2);
        }
    }
    
    protected Object writeReplace() {
        List<DestructionAwareBeanPostProcessor> serializablePostProcessors = null;
        if (this.beanPostProcessors != null) {
            serializablePostProcessors = new ArrayList<DestructionAwareBeanPostProcessor>();
            for (final DestructionAwareBeanPostProcessor postProcessor : this.beanPostProcessors) {
                if (postProcessor instanceof Serializable) {
                    serializablePostProcessors.add(postProcessor);
                }
            }
        }
        return new DisposableBeanAdapter(this.bean, this.beanName, this.invokeDisposableBean, this.nonPublicAccessAllowed, this.destroyMethodName, serializablePostProcessors);
    }
    
    public static boolean hasDestroyMethod(final Object bean, final RootBeanDefinition beanDefinition) {
        if (bean instanceof DisposableBean || DisposableBeanAdapter.closeableInterface.isInstance(bean)) {
            return true;
        }
        final String destroyMethodName = beanDefinition.getDestroyMethodName();
        if ("(inferred)".equals(destroyMethodName)) {
            return ClassUtils.hasMethod(bean.getClass(), "close", (Class<?>[])new Class[0]);
        }
        return destroyMethodName != null;
    }
    
    static {
        logger = LogFactory.getLog(DisposableBeanAdapter.class);
        try {
            DisposableBeanAdapter.closeableInterface = DisposableBeanAdapter.class.getClassLoader().loadClass("java.lang.AutoCloseable");
        }
        catch (ClassNotFoundException ex) {
            DisposableBeanAdapter.closeableInterface = Closeable.class;
        }
    }
}
