// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.springframework.util.ObjectUtils;
import org.springframework.aop.PointcutAdvisor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.AopUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.Dispatcher;
import org.springframework.aop.TargetSource;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.NoOp;
import java.util.WeakHashMap;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.RawTargetAccess;
import java.util.List;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.core.GeneratorStrategy;
import org.springframework.cglib.transform.impl.UndeclaredThrowableStrategy;
import java.lang.reflect.UndeclaredThrowableException;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.core.SmartClassLoader;
import org.springframework.util.ClassUtils;
import org.springframework.util.Assert;
import java.util.Map;
import org.apache.commons.logging.Log;
import java.io.Serializable;

class CglibAopProxy implements AopProxy, Serializable
{
    private static final int AOP_PROXY = 0;
    private static final int INVOKE_TARGET = 1;
    private static final int NO_OVERRIDE = 2;
    private static final int DISPATCH_TARGET = 3;
    private static final int DISPATCH_ADVISED = 4;
    private static final int INVOKE_EQUALS = 5;
    private static final int INVOKE_HASHCODE = 6;
    protected static final Log logger;
    private static final Map<Class<?>, Boolean> validatedClasses;
    protected final AdvisedSupport advised;
    private Object[] constructorArgs;
    private Class<?>[] constructorArgTypes;
    private final transient AdvisedDispatcher advisedDispatcher;
    private transient Map<String, Integer> fixedInterceptorMap;
    private transient int fixedInterceptorOffset;
    
    public CglibAopProxy(final AdvisedSupport config) throws AopConfigException {
        Assert.notNull(config, "AdvisedSupport must not be null");
        if (config.getAdvisors().length == 0 && config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
            throw new AopConfigException("No advisors and no TargetSource specified");
        }
        this.advised = config;
        this.advisedDispatcher = new AdvisedDispatcher(this.advised);
    }
    
    public void setConstructorArguments(final Object[] constructorArgs, final Class<?>[] constructorArgTypes) {
        if (constructorArgs == null || constructorArgTypes == null) {
            throw new IllegalArgumentException("Both 'constructorArgs' and 'constructorArgTypes' need to be specified");
        }
        if (constructorArgs.length != constructorArgTypes.length) {
            throw new IllegalArgumentException("Number of 'constructorArgs' (" + constructorArgs.length + ") must match number of 'constructorArgTypes' (" + constructorArgTypes.length + ")");
        }
        this.constructorArgs = constructorArgs;
        this.constructorArgTypes = constructorArgTypes;
    }
    
    @Override
    public Object getProxy() {
        return this.getProxy(null);
    }
    
    @Override
    public Object getProxy(final ClassLoader classLoader) {
        if (CglibAopProxy.logger.isDebugEnabled()) {
            CglibAopProxy.logger.debug("Creating CGLIB proxy: target source is " + this.advised.getTargetSource());
        }
        try {
            final Class<?> rootClass = this.advised.getTargetClass();
            Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");
            Class<?> proxySuperClass = rootClass;
            if (ClassUtils.isCglibProxyClass(rootClass)) {
                proxySuperClass = rootClass.getSuperclass();
                final Class<?>[] interfaces;
                final Class<?>[] additionalInterfaces = interfaces = rootClass.getInterfaces();
                for (final Class<?> additionalInterface : interfaces) {
                    this.advised.addInterface(additionalInterface);
                }
            }
            this.validateClassIfNecessary(proxySuperClass);
            final Enhancer enhancer = this.createEnhancer();
            if (classLoader != null) {
                enhancer.setClassLoader(classLoader);
                if (classLoader instanceof SmartClassLoader && ((SmartClassLoader)classLoader).isClassReloadable(proxySuperClass)) {
                    enhancer.setUseCache(false);
                }
            }
            enhancer.setSuperclass(proxySuperClass);
            enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setStrategy(new UndeclaredThrowableStrategy(UndeclaredThrowableException.class));
            final Callback[] callbacks = this.getCallbacks(rootClass);
            final Class<?>[] types = (Class<?>[])new Class[callbacks.length];
            for (int x = 0; x < types.length; ++x) {
                types[x] = callbacks[x].getClass();
            }
            enhancer.setCallbackFilter(new ProxyCallbackFilter(this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
            enhancer.setCallbackTypes(types);
            return this.createProxyClassAndInstance(enhancer, callbacks);
        }
        catch (CodeGenerationException ex) {
            throw new AopConfigException("Could not generate CGLIB subclass of class [" + this.advised.getTargetClass() + "]: " + "Common causes of this problem include using a final class or a non-visible class", ex);
        }
        catch (IllegalArgumentException ex2) {
            throw new AopConfigException("Could not generate CGLIB subclass of class [" + this.advised.getTargetClass() + "]: " + "Common causes of this problem include using a final class or a non-visible class", ex2);
        }
        catch (Exception ex3) {
            throw new AopConfigException("Unexpected AOP exception", ex3);
        }
    }
    
    protected Object createProxyClassAndInstance(final Enhancer enhancer, final Callback[] callbacks) {
        enhancer.setInterceptDuringConstruction(false);
        enhancer.setCallbacks(callbacks);
        return (this.constructorArgs != null) ? enhancer.create(this.constructorArgTypes, this.constructorArgs) : enhancer.create();
    }
    
    protected Enhancer createEnhancer() {
        return new Enhancer();
    }
    
    private void validateClassIfNecessary(final Class<?> proxySuperClass) {
        if (CglibAopProxy.logger.isWarnEnabled()) {
            synchronized (CglibAopProxy.validatedClasses) {
                if (!CglibAopProxy.validatedClasses.containsKey(proxySuperClass)) {
                    this.doValidateClass(proxySuperClass);
                    CglibAopProxy.validatedClasses.put(proxySuperClass, Boolean.TRUE);
                }
            }
        }
    }
    
    private void doValidateClass(final Class<?> proxySuperClass) {
        if (CglibAopProxy.logger.isWarnEnabled()) {
            final Method[] methods2;
            final Method[] methods = methods2 = proxySuperClass.getMethods();
            for (final Method method : methods2) {
                if (!Object.class.equals(method.getDeclaringClass()) && !Modifier.isStatic(method.getModifiers()) && Modifier.isFinal(method.getModifiers())) {
                    CglibAopProxy.logger.warn("Unable to proxy method [" + method + "] because it is final: " + "All calls to this method via a proxy will be routed directly to the proxy.");
                }
            }
        }
    }
    
    private Callback[] getCallbacks(final Class<?> rootClass) throws Exception {
        final boolean exposeProxy = this.advised.isExposeProxy();
        final boolean isFrozen = this.advised.isFrozen();
        final boolean isStatic = this.advised.getTargetSource().isStatic();
        final Callback aopInterceptor = new DynamicAdvisedInterceptor(this.advised);
        Callback targetInterceptor;
        if (exposeProxy) {
            targetInterceptor = (isStatic ? new StaticUnadvisedExposedInterceptor(this.advised.getTargetSource().getTarget()) : new DynamicUnadvisedExposedInterceptor(this.advised.getTargetSource()));
        }
        else {
            targetInterceptor = (isStatic ? new StaticUnadvisedInterceptor(this.advised.getTargetSource().getTarget()) : new DynamicUnadvisedInterceptor(this.advised.getTargetSource()));
        }
        final Callback targetDispatcher = (Callback)(isStatic ? new StaticDispatcher(this.advised.getTargetSource().getTarget()) : new SerializableNoOp());
        final Callback[] mainCallbacks = { aopInterceptor, targetInterceptor, new SerializableNoOp(), targetDispatcher, this.advisedDispatcher, new EqualsInterceptor(this.advised), new HashCodeInterceptor(this.advised) };
        Callback[] callbacks;
        if (isStatic && isFrozen) {
            final Method[] methods = rootClass.getMethods();
            final Callback[] fixedCallbacks = new Callback[methods.length];
            this.fixedInterceptorMap = new HashMap<String, Integer>(methods.length);
            for (int x = 0; x < methods.length; ++x) {
                final List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(methods[x], rootClass);
                fixedCallbacks[x] = new FixedChainStaticTargetInterceptor(chain, this.advised.getTargetSource().getTarget(), this.advised.getTargetClass());
                this.fixedInterceptorMap.put(methods[x].toString(), x);
            }
            callbacks = new Callback[mainCallbacks.length + fixedCallbacks.length];
            System.arraycopy(mainCallbacks, 0, callbacks, 0, mainCallbacks.length);
            System.arraycopy(fixedCallbacks, 0, callbacks, mainCallbacks.length, fixedCallbacks.length);
            this.fixedInterceptorOffset = mainCallbacks.length;
        }
        else {
            callbacks = mainCallbacks;
        }
        return callbacks;
    }
    
    private static Object processReturnType(final Object proxy, final Object target, final Method method, Object retVal) {
        if (retVal != null && retVal == target && !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
            retVal = proxy;
        }
        final Class<?> returnType = method.getReturnType();
        if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
            throw new AopInvocationException("Null return value from advice does not match primitive return type for: " + method);
        }
        return retVal;
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof CglibAopProxy && AopProxyUtils.equalsInProxy(this.advised, ((CglibAopProxy)other).advised));
    }
    
    @Override
    public int hashCode() {
        return CglibAopProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
    }
    
    static {
        logger = LogFactory.getLog(CglibAopProxy.class);
        validatedClasses = new WeakHashMap<Class<?>, Boolean>();
    }
    
    public static class SerializableNoOp implements NoOp, Serializable
    {
    }
    
    private static class StaticUnadvisedInterceptor implements MethodInterceptor, Serializable
    {
        private final Object target;
        
        public StaticUnadvisedInterceptor(final Object target) {
            this.target = target;
        }
        
        @Override
        public Object intercept(final Object proxy, final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
            final Object retVal = methodProxy.invoke(this.target, args);
            return processReturnType(proxy, this.target, method, retVal);
        }
    }
    
    private static class StaticUnadvisedExposedInterceptor implements MethodInterceptor, Serializable
    {
        private final Object target;
        
        public StaticUnadvisedExposedInterceptor(final Object target) {
            this.target = target;
        }
        
        @Override
        public Object intercept(final Object proxy, final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;
            try {
                oldProxy = AopContext.setCurrentProxy(proxy);
                final Object retVal = methodProxy.invoke(this.target, args);
                return processReturnType(proxy, this.target, method, retVal);
            }
            finally {
                AopContext.setCurrentProxy(oldProxy);
            }
        }
    }
    
    private static class DynamicUnadvisedInterceptor implements MethodInterceptor, Serializable
    {
        private final TargetSource targetSource;
        
        public DynamicUnadvisedInterceptor(final TargetSource targetSource) {
            this.targetSource = targetSource;
        }
        
        @Override
        public Object intercept(final Object proxy, final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
            final Object target = this.targetSource.getTarget();
            try {
                final Object retVal = methodProxy.invoke(target, args);
                return processReturnType(proxy, target, method, retVal);
            }
            finally {
                this.targetSource.releaseTarget(target);
            }
        }
    }
    
    private static class DynamicUnadvisedExposedInterceptor implements MethodInterceptor, Serializable
    {
        private final TargetSource targetSource;
        
        public DynamicUnadvisedExposedInterceptor(final TargetSource targetSource) {
            this.targetSource = targetSource;
        }
        
        @Override
        public Object intercept(final Object proxy, final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;
            final Object target = this.targetSource.getTarget();
            try {
                oldProxy = AopContext.setCurrentProxy(proxy);
                final Object retVal = methodProxy.invoke(target, args);
                return processReturnType(proxy, target, method, retVal);
            }
            finally {
                AopContext.setCurrentProxy(oldProxy);
                this.targetSource.releaseTarget(target);
            }
        }
    }
    
    private static class StaticDispatcher implements Dispatcher, Serializable
    {
        private Object target;
        
        public StaticDispatcher(final Object target) {
            this.target = target;
        }
        
        @Override
        public Object loadObject() {
            return this.target;
        }
    }
    
    private static class AdvisedDispatcher implements Dispatcher, Serializable
    {
        private final AdvisedSupport advised;
        
        public AdvisedDispatcher(final AdvisedSupport advised) {
            this.advised = advised;
        }
        
        @Override
        public Object loadObject() throws Exception {
            return this.advised;
        }
    }
    
    private static class EqualsInterceptor implements MethodInterceptor, Serializable
    {
        private final AdvisedSupport advised;
        
        public EqualsInterceptor(final AdvisedSupport advised) {
            this.advised = advised;
        }
        
        @Override
        public Object intercept(final Object proxy, final Method method, final Object[] args, final MethodProxy methodProxy) {
            final Object other = args[0];
            if (proxy == other) {
                return true;
            }
            if (!(other instanceof Factory)) {
                return false;
            }
            final Callback callback = ((Factory)other).getCallback(5);
            if (!(callback instanceof EqualsInterceptor)) {
                return false;
            }
            final AdvisedSupport otherAdvised = ((EqualsInterceptor)callback).advised;
            return AopProxyUtils.equalsInProxy(this.advised, otherAdvised);
        }
    }
    
    private static class HashCodeInterceptor implements MethodInterceptor, Serializable
    {
        private final AdvisedSupport advised;
        
        public HashCodeInterceptor(final AdvisedSupport advised) {
            this.advised = advised;
        }
        
        @Override
        public Object intercept(final Object proxy, final Method method, final Object[] args, final MethodProxy methodProxy) {
            return CglibAopProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
        }
    }
    
    private static class FixedChainStaticTargetInterceptor implements MethodInterceptor, Serializable
    {
        private final List<Object> adviceChain;
        private final Object target;
        private final Class<?> targetClass;
        
        public FixedChainStaticTargetInterceptor(final List<Object> adviceChain, final Object target, final Class<?> targetClass) {
            this.adviceChain = adviceChain;
            this.target = target;
            this.targetClass = targetClass;
        }
        
        @Override
        public Object intercept(final Object proxy, final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
            final MethodInvocation invocation = new CglibMethodInvocation(proxy, this.target, method, args, this.targetClass, this.adviceChain, methodProxy);
            Object retVal = invocation.proceed();
            retVal = processReturnType(proxy, this.target, method, retVal);
            return retVal;
        }
    }
    
    private static class DynamicAdvisedInterceptor implements MethodInterceptor, Serializable
    {
        private AdvisedSupport advised;
        
        public DynamicAdvisedInterceptor(final AdvisedSupport advised) {
            this.advised = advised;
        }
        
        @Override
        public Object intercept(final Object proxy, final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;
            boolean setProxyContext = false;
            Class<?> targetClass = null;
            Object target = null;
            try {
                if (this.advised.exposeProxy) {
                    oldProxy = AopContext.setCurrentProxy(proxy);
                    setProxyContext = true;
                }
                target = this.getTarget();
                if (target != null) {
                    targetClass = target.getClass();
                }
                final List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
                Object retVal;
                if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
                    retVal = methodProxy.invoke(target, args);
                }
                else {
                    retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();
                }
                retVal = processReturnType(proxy, target, method, retVal);
                return retVal;
            }
            finally {
                if (target != null) {
                    this.releaseTarget(target);
                }
                if (setProxyContext) {
                    AopContext.setCurrentProxy(oldProxy);
                }
            }
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || (other instanceof DynamicAdvisedInterceptor && this.advised.equals(((DynamicAdvisedInterceptor)other).advised));
        }
        
        @Override
        public int hashCode() {
            return this.advised.hashCode();
        }
        
        protected Object getTarget() throws Exception {
            return this.advised.getTargetSource().getTarget();
        }
        
        protected void releaseTarget(final Object target) throws Exception {
            this.advised.getTargetSource().releaseTarget(target);
        }
    }
    
    private static class CglibMethodInvocation extends ReflectiveMethodInvocation
    {
        private final MethodProxy methodProxy;
        private boolean protectedMethod;
        
        public CglibMethodInvocation(final Object proxy, final Object target, final Method method, final Object[] arguments, final Class<?> targetClass, final List<Object> interceptorsAndDynamicMethodMatchers, final MethodProxy methodProxy) {
            super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
            this.methodProxy = methodProxy;
            this.protectedMethod = Modifier.isProtected(method.getModifiers());
        }
        
        @Override
        protected Object invokeJoinpoint() throws Throwable {
            if (this.protectedMethod) {
                return super.invokeJoinpoint();
            }
            return this.methodProxy.invoke(this.target, this.arguments);
        }
    }
    
    private static class ProxyCallbackFilter implements CallbackFilter
    {
        private final AdvisedSupport advised;
        private final Map<String, Integer> fixedInterceptorMap;
        private final int fixedInterceptorOffset;
        
        public ProxyCallbackFilter(final AdvisedSupport advised, final Map<String, Integer> fixedInterceptorMap, final int fixedInterceptorOffset) {
            this.advised = advised;
            this.fixedInterceptorMap = fixedInterceptorMap;
            this.fixedInterceptorOffset = fixedInterceptorOffset;
        }
        
        @Override
        public int accept(final Method method) {
            if (AopUtils.isFinalizeMethod(method)) {
                CglibAopProxy.logger.debug("Found finalize() method - using NO_OVERRIDE");
                return 2;
            }
            if (!this.advised.isOpaque() && method.getDeclaringClass().isInterface() && method.getDeclaringClass().isAssignableFrom(Advised.class)) {
                if (CglibAopProxy.logger.isDebugEnabled()) {
                    CglibAopProxy.logger.debug("Method is declared on Advised interface: " + method);
                }
                return 4;
            }
            if (AopUtils.isEqualsMethod(method)) {
                CglibAopProxy.logger.debug("Found 'equals' method: " + method);
                return 5;
            }
            if (AopUtils.isHashCodeMethod(method)) {
                CglibAopProxy.logger.debug("Found 'hashCode' method: " + method);
                return 6;
            }
            final Class<?> targetClass = this.advised.getTargetClass();
            final List<?> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
            final boolean haveAdvice = !chain.isEmpty();
            final boolean exposeProxy = this.advised.isExposeProxy();
            final boolean isStatic = this.advised.getTargetSource().isStatic();
            final boolean isFrozen = this.advised.isFrozen();
            if (haveAdvice || !isFrozen) {
                if (exposeProxy) {
                    if (CglibAopProxy.logger.isDebugEnabled()) {
                        CglibAopProxy.logger.debug("Must expose proxy on advised method: " + method);
                    }
                    return 0;
                }
                final String key = method.toString();
                if (isStatic && isFrozen && this.fixedInterceptorMap.containsKey(key)) {
                    if (CglibAopProxy.logger.isDebugEnabled()) {
                        CglibAopProxy.logger.debug("Method has advice and optimisations are enabled: " + method);
                    }
                    final int index = this.fixedInterceptorMap.get(key);
                    return index + this.fixedInterceptorOffset;
                }
                if (CglibAopProxy.logger.isDebugEnabled()) {
                    CglibAopProxy.logger.debug("Unable to apply any optimisations to advised method: " + method);
                }
                return 0;
            }
            else {
                if (exposeProxy || !isStatic) {
                    return 1;
                }
                final Class<?> returnType = method.getReturnType();
                if (targetClass == returnType) {
                    if (CglibAopProxy.logger.isDebugEnabled()) {
                        CglibAopProxy.logger.debug("Method " + method + "has return type same as target type (may return this) - using INVOKE_TARGET");
                    }
                    return 1;
                }
                if (returnType.isPrimitive() || !returnType.isAssignableFrom(targetClass)) {
                    if (CglibAopProxy.logger.isDebugEnabled()) {
                        CglibAopProxy.logger.debug("Method " + method + " has return type that ensures this cannot be returned- using DISPATCH_TARGET");
                    }
                    return 3;
                }
                if (CglibAopProxy.logger.isDebugEnabled()) {
                    CglibAopProxy.logger.debug("Method " + method + "has return type that is assignable from the target type (may return this) - " + "using INVOKE_TARGET");
                }
                return 1;
            }
        }
        
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ProxyCallbackFilter)) {
                return false;
            }
            final ProxyCallbackFilter otherCallbackFilter = (ProxyCallbackFilter)other;
            final AdvisedSupport otherAdvised = otherCallbackFilter.advised;
            if (this.advised == null || otherAdvised == null) {
                return false;
            }
            if (this.advised.isFrozen() != otherAdvised.isFrozen()) {
                return false;
            }
            if (this.advised.isExposeProxy() != otherAdvised.isExposeProxy()) {
                return false;
            }
            if (this.advised.getTargetSource().isStatic() != otherAdvised.getTargetSource().isStatic()) {
                return false;
            }
            if (!AopProxyUtils.equalsProxiedInterfaces(this.advised, otherAdvised)) {
                return false;
            }
            final Advisor[] thisAdvisors = this.advised.getAdvisors();
            final Advisor[] thatAdvisors = otherAdvised.getAdvisors();
            if (thisAdvisors.length != thatAdvisors.length) {
                return false;
            }
            for (int i = 0; i < thisAdvisors.length; ++i) {
                final Advisor thisAdvisor = thisAdvisors[i];
                final Advisor thatAdvisor = thatAdvisors[i];
                if (!this.equalsAdviceClasses(thisAdvisor, thatAdvisor)) {
                    return false;
                }
                if (!this.equalsPointcuts(thisAdvisor, thatAdvisor)) {
                    return false;
                }
            }
            return true;
        }
        
        private boolean equalsAdviceClasses(final Advisor a, final Advisor b) {
            final Advice aa = a.getAdvice();
            final Advice ba = b.getAdvice();
            if (aa == null || ba == null) {
                return aa == ba;
            }
            return aa.getClass().equals(ba.getClass());
        }
        
        private boolean equalsPointcuts(final Advisor a, final Advisor b) {
            return !(a instanceof PointcutAdvisor) || (b instanceof PointcutAdvisor && ObjectUtils.nullSafeEquals(((PointcutAdvisor)a).getPointcut(), ((PointcutAdvisor)b).getPointcut()));
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            final Advisor[] advisors2;
            final Advisor[] advisors = advisors2 = this.advised.getAdvisors();
            for (final Advisor advisor : advisors2) {
                final Advice advice = advisor.getAdvice();
                if (advice != null) {
                    hashCode = 13 * hashCode + advice.getClass().hashCode();
                }
            }
            hashCode = 13 * hashCode + (this.advised.isFrozen() ? 1 : 0);
            hashCode = 13 * hashCode + (this.advised.isExposeProxy() ? 1 : 0);
            hashCode = 13 * hashCode + (this.advised.isOptimize() ? 1 : 0);
            hashCode = 13 * hashCode + (this.advised.isOpaque() ? 1 : 0);
            return hashCode;
        }
    }
}
