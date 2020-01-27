// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.aop.support.StaticMethodMatcher;
import java.lang.reflect.InvocationTargetException;
import org.springframework.aop.AopInvocationException;
import org.springframework.util.ReflectionUtils;
import org.aspectj.weaver.tools.PointcutParameter;
import org.springframework.util.CollectionUtils;
import org.aspectj.weaver.tools.JoinPointMatch;
import java.util.HashMap;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ClassUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StringUtils;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.support.MethodMatchers;
import org.springframework.aop.Pointcut;
import org.springframework.util.Assert;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.aspectj.lang.JoinPoint;
import java.lang.reflect.Type;
import java.util.Map;
import java.lang.reflect.Method;
import org.aopalliance.aop.Advice;

public abstract class AbstractAspectJAdvice implements Advice, AspectJPrecedenceInformation
{
    protected static final String JOIN_POINT_KEY;
    protected final Method aspectJAdviceMethod;
    private final int adviceInvocationArgumentCount;
    private final AspectJExpressionPointcut pointcut;
    private final AspectInstanceFactory aspectInstanceFactory;
    private String aspectName;
    private int declarationOrder;
    private String[] argumentNames;
    private String throwingName;
    private String returningName;
    private Class<?> discoveredReturningType;
    private Class<?> discoveredThrowingType;
    private int joinPointArgumentIndex;
    private int joinPointStaticPartArgumentIndex;
    private Map<String, Integer> argumentBindings;
    private boolean argumentsIntrospected;
    private Type discoveredReturningGenericType;
    
    public static JoinPoint currentJoinPoint() {
        final MethodInvocation mi = ExposeInvocationInterceptor.currentInvocation();
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        final ProxyMethodInvocation pmi = (ProxyMethodInvocation)mi;
        JoinPoint jp = (JoinPoint)pmi.getUserAttribute(AbstractAspectJAdvice.JOIN_POINT_KEY);
        if (jp == null) {
            jp = (JoinPoint)new MethodInvocationProceedingJoinPoint(pmi);
            pmi.setUserAttribute(AbstractAspectJAdvice.JOIN_POINT_KEY, jp);
        }
        return jp;
    }
    
    public AbstractAspectJAdvice(final Method aspectJAdviceMethod, final AspectJExpressionPointcut pointcut, final AspectInstanceFactory aspectInstanceFactory) {
        this.argumentNames = null;
        this.throwingName = null;
        this.returningName = null;
        this.discoveredReturningType = Object.class;
        this.discoveredThrowingType = Object.class;
        this.joinPointArgumentIndex = -1;
        this.joinPointStaticPartArgumentIndex = -1;
        this.argumentBindings = null;
        this.argumentsIntrospected = false;
        Assert.notNull(aspectJAdviceMethod, "Advice method must not be null");
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.adviceInvocationArgumentCount = this.aspectJAdviceMethod.getParameterTypes().length;
        this.pointcut = pointcut;
        this.aspectInstanceFactory = aspectInstanceFactory;
    }
    
    public final Method getAspectJAdviceMethod() {
        return this.aspectJAdviceMethod;
    }
    
    public final AspectJExpressionPointcut getPointcut() {
        this.calculateArgumentBindings();
        return this.pointcut;
    }
    
    public final Pointcut buildSafePointcut() {
        final Pointcut pc = this.getPointcut();
        final MethodMatcher safeMethodMatcher = MethodMatchers.intersection(new AdviceExcludingMethodMatcher(this.aspectJAdviceMethod), pc.getMethodMatcher());
        return new ComposablePointcut(pc.getClassFilter(), safeMethodMatcher);
    }
    
    public final AspectInstanceFactory getAspectInstanceFactory() {
        return this.aspectInstanceFactory;
    }
    
    public final ClassLoader getAspectClassLoader() {
        return this.aspectInstanceFactory.getAspectClassLoader();
    }
    
    @Override
    public int getOrder() {
        return this.aspectInstanceFactory.getOrder();
    }
    
    public void setAspectName(final String name) {
        this.aspectName = name;
    }
    
    @Override
    public String getAspectName() {
        return this.aspectName;
    }
    
    public void setDeclarationOrder(final int order) {
        this.declarationOrder = order;
    }
    
    @Override
    public int getDeclarationOrder() {
        return this.declarationOrder;
    }
    
    public void setArgumentNames(final String argNames) {
        final String[] tokens = StringUtils.commaDelimitedListToStringArray(argNames);
        this.setArgumentNamesFromStringArray(tokens);
    }
    
    public void setArgumentNamesFromStringArray(final String[] args) {
        this.argumentNames = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            this.argumentNames[i] = StringUtils.trimWhitespace(args[i]);
            if (!this.isVariableName(this.argumentNames[i])) {
                throw new IllegalArgumentException("'argumentNames' property of AbstractAspectJAdvice contains an argument name '" + this.argumentNames[i] + "' that is not a valid Java identifier");
            }
        }
        if (this.argumentNames != null && this.aspectJAdviceMethod.getParameterTypes().length == this.argumentNames.length + 1) {
            final Class<?> firstArgType = this.aspectJAdviceMethod.getParameterTypes()[0];
            if (firstArgType == JoinPoint.class || firstArgType == ProceedingJoinPoint.class || firstArgType == JoinPoint.StaticPart.class) {
                final String[] oldNames = this.argumentNames;
                (this.argumentNames = new String[oldNames.length + 1])[0] = "THIS_JOIN_POINT";
                System.arraycopy(oldNames, 0, this.argumentNames, 1, oldNames.length);
            }
        }
    }
    
    public void setReturningName(final String name) {
        throw new UnsupportedOperationException("Only afterReturning advice can be used to bind a return value");
    }
    
    protected void setReturningNameNoCheck(final String name) {
        if (this.isVariableName(name)) {
            this.returningName = name;
        }
        else {
            try {
                this.discoveredReturningType = ClassUtils.forName(name, this.getAspectClassLoader());
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Returning name '" + name + "' is neither a valid argument name nor the fully-qualified name of a Java type on the classpath. " + "Root cause: " + ex);
            }
        }
    }
    
    protected Class<?> getDiscoveredReturningType() {
        return this.discoveredReturningType;
    }
    
    protected Type getDiscoveredReturningGenericType() {
        return this.discoveredReturningGenericType;
    }
    
    public void setThrowingName(final String name) {
        throw new UnsupportedOperationException("Only afterThrowing advice can be used to bind a thrown exception");
    }
    
    protected void setThrowingNameNoCheck(final String name) {
        if (this.isVariableName(name)) {
            this.throwingName = name;
        }
        else {
            try {
                this.discoveredThrowingType = ClassUtils.forName(name, this.getAspectClassLoader());
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Throwing name '" + name + "' is neither a valid argument name nor the fully-qualified name of a Java type on the classpath. " + "Root cause: " + ex);
            }
        }
    }
    
    protected Class<?> getDiscoveredThrowingType() {
        return this.discoveredThrowingType;
    }
    
    private boolean isVariableName(final String name) {
        final char[] chars = name.toCharArray();
        if (!Character.isJavaIdentifierStart(chars[0])) {
            return false;
        }
        for (int i = 1; i < chars.length; ++i) {
            if (!Character.isJavaIdentifierPart(chars[i])) {
                return false;
            }
        }
        return true;
    }
    
    public final synchronized void calculateArgumentBindings() {
        if (this.argumentsIntrospected || this.adviceInvocationArgumentCount == 0) {
            return;
        }
        int numUnboundArgs = this.adviceInvocationArgumentCount;
        final Class<?>[] parameterTypes = this.aspectJAdviceMethod.getParameterTypes();
        if (this.maybeBindJoinPoint(parameterTypes[0]) || this.maybeBindProceedingJoinPoint(parameterTypes[0])) {
            --numUnboundArgs;
        }
        else if (this.maybeBindJoinPointStaticPart(parameterTypes[0])) {
            --numUnboundArgs;
        }
        if (numUnboundArgs > 0) {
            this.bindArgumentsByName(numUnboundArgs);
        }
        this.argumentsIntrospected = true;
    }
    
    private boolean maybeBindJoinPoint(final Class<?> candidateParameterType) {
        if (candidateParameterType.equals(JoinPoint.class)) {
            this.joinPointArgumentIndex = 0;
            return true;
        }
        return false;
    }
    
    private boolean maybeBindProceedingJoinPoint(final Class<?> candidateParameterType) {
        if (!candidateParameterType.equals(ProceedingJoinPoint.class)) {
            return false;
        }
        if (!this.supportsProceedingJoinPoint()) {
            throw new IllegalArgumentException("ProceedingJoinPoint is only supported for around advice");
        }
        this.joinPointArgumentIndex = 0;
        return true;
    }
    
    protected boolean supportsProceedingJoinPoint() {
        return false;
    }
    
    private boolean maybeBindJoinPointStaticPart(final Class<?> candidateParameterType) {
        if (candidateParameterType.equals(JoinPoint.StaticPart.class)) {
            this.joinPointStaticPartArgumentIndex = 0;
            return true;
        }
        return false;
    }
    
    private void bindArgumentsByName(final int numArgumentsExpectingToBind) {
        if (this.argumentNames == null) {
            this.argumentNames = this.createParameterNameDiscoverer().getParameterNames(this.aspectJAdviceMethod);
        }
        if (this.argumentNames != null) {
            this.bindExplicitArguments(numArgumentsExpectingToBind);
            return;
        }
        throw new IllegalStateException("Advice method [" + this.aspectJAdviceMethod.getName() + "] " + "requires " + numArgumentsExpectingToBind + " arguments to be bound by name, but " + "the argument names were not specified and could not be discovered.");
    }
    
    protected ParameterNameDiscoverer createParameterNameDiscoverer() {
        final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        final AspectJAdviceParameterNameDiscoverer adviceParameterNameDiscoverer = new AspectJAdviceParameterNameDiscoverer(this.pointcut.getExpression());
        adviceParameterNameDiscoverer.setReturningName(this.returningName);
        adviceParameterNameDiscoverer.setThrowingName(this.throwingName);
        adviceParameterNameDiscoverer.setRaiseExceptions(true);
        discoverer.addDiscoverer(adviceParameterNameDiscoverer);
        return discoverer;
    }
    
    private void bindExplicitArguments(final int numArgumentsLeftToBind) {
        this.argumentBindings = new HashMap<String, Integer>();
        final int numExpectedArgumentNames = this.aspectJAdviceMethod.getParameterTypes().length;
        if (this.argumentNames.length != numExpectedArgumentNames) {
            throw new IllegalStateException("Expecting to find " + numExpectedArgumentNames + " arguments to bind by name in advice, but actually found " + this.argumentNames.length + " arguments.");
        }
        int i;
        int argumentIndexOffset;
        for (argumentIndexOffset = (i = this.adviceInvocationArgumentCount - numArgumentsLeftToBind); i < this.argumentNames.length; ++i) {
            this.argumentBindings.put(this.argumentNames[i], i);
        }
        if (this.returningName != null) {
            if (!this.argumentBindings.containsKey(this.returningName)) {
                throw new IllegalStateException("Returning argument name '" + this.returningName + "' was not bound in advice arguments");
            }
            final Integer index = this.argumentBindings.get(this.returningName);
            this.discoveredReturningType = this.aspectJAdviceMethod.getParameterTypes()[index];
            this.discoveredReturningGenericType = this.aspectJAdviceMethod.getGenericParameterTypes()[index];
        }
        if (this.throwingName != null) {
            if (!this.argumentBindings.containsKey(this.throwingName)) {
                throw new IllegalStateException("Throwing argument name '" + this.throwingName + "' was not bound in advice arguments");
            }
            final Integer index = this.argumentBindings.get(this.throwingName);
            this.discoveredThrowingType = this.aspectJAdviceMethod.getParameterTypes()[index];
        }
        this.configurePointcutParameters(argumentIndexOffset);
    }
    
    private void configurePointcutParameters(final int argumentIndexOffset) {
        int numParametersToRemove = argumentIndexOffset;
        if (this.returningName != null) {
            ++numParametersToRemove;
        }
        if (this.throwingName != null) {
            ++numParametersToRemove;
        }
        final String[] pointcutParameterNames = new String[this.argumentNames.length - numParametersToRemove];
        final Class<?>[] pointcutParameterTypes = (Class<?>[])new Class[pointcutParameterNames.length];
        final Class<?>[] methodParameterTypes = this.aspectJAdviceMethod.getParameterTypes();
        int index = 0;
        for (int i = 0; i < this.argumentNames.length; ++i) {
            if (i >= argumentIndexOffset) {
                if (!this.argumentNames[i].equals(this.returningName)) {
                    if (!this.argumentNames[i].equals(this.throwingName)) {
                        pointcutParameterNames[index] = this.argumentNames[i];
                        pointcutParameterTypes[index] = methodParameterTypes[i];
                        ++index;
                    }
                }
            }
        }
        this.pointcut.setParameterNames(pointcutParameterNames);
        this.pointcut.setParameterTypes(pointcutParameterTypes);
    }
    
    protected Object[] argBinding(final JoinPoint jp, final JoinPointMatch jpMatch, final Object returnValue, final Throwable ex) {
        this.calculateArgumentBindings();
        final Object[] adviceInvocationArgs = new Object[this.adviceInvocationArgumentCount];
        int numBound = 0;
        if (this.joinPointArgumentIndex != -1) {
            adviceInvocationArgs[this.joinPointArgumentIndex] = jp;
            ++numBound;
        }
        else if (this.joinPointStaticPartArgumentIndex != -1) {
            adviceInvocationArgs[this.joinPointStaticPartArgumentIndex] = jp.getStaticPart();
            ++numBound;
        }
        if (!CollectionUtils.isEmpty(this.argumentBindings)) {
            if (jpMatch != null) {
                final PointcutParameter[] parameterBindings2;
                final PointcutParameter[] parameterBindings = parameterBindings2 = jpMatch.getParameterBindings();
                for (final PointcutParameter parameter : parameterBindings2) {
                    final String name = parameter.getName();
                    final Integer index = this.argumentBindings.get(name);
                    adviceInvocationArgs[index] = parameter.getBinding();
                    ++numBound;
                }
            }
            if (this.returningName != null) {
                final Integer index2 = this.argumentBindings.get(this.returningName);
                adviceInvocationArgs[index2] = returnValue;
                ++numBound;
            }
            if (this.throwingName != null) {
                final Integer index2 = this.argumentBindings.get(this.throwingName);
                adviceInvocationArgs[index2] = ex;
                ++numBound;
            }
        }
        if (numBound != this.adviceInvocationArgumentCount) {
            throw new IllegalStateException("Required to bind " + this.adviceInvocationArgumentCount + " arguments, but only bound " + numBound + " (JoinPointMatch " + ((jpMatch == null) ? "was NOT" : "WAS") + " bound in invocation)");
        }
        return adviceInvocationArgs;
    }
    
    protected Object invokeAdviceMethod(final JoinPointMatch jpMatch, final Object returnValue, final Throwable ex) throws Throwable {
        return this.invokeAdviceMethodWithGivenArgs(this.argBinding(this.getJoinPoint(), jpMatch, returnValue, ex));
    }
    
    protected Object invokeAdviceMethod(final JoinPoint jp, final JoinPointMatch jpMatch, final Object returnValue, final Throwable t) throws Throwable {
        return this.invokeAdviceMethodWithGivenArgs(this.argBinding(jp, jpMatch, returnValue, t));
    }
    
    protected Object invokeAdviceMethodWithGivenArgs(final Object[] args) throws Throwable {
        Object[] actualArgs = args;
        if (this.aspectJAdviceMethod.getParameterTypes().length == 0) {
            actualArgs = null;
        }
        try {
            ReflectionUtils.makeAccessible(this.aspectJAdviceMethod);
            return this.aspectJAdviceMethod.invoke(this.aspectInstanceFactory.getAspectInstance(), actualArgs);
        }
        catch (IllegalArgumentException ex) {
            throw new AopInvocationException("Mismatch on arguments to advice method [" + this.aspectJAdviceMethod + "]; pointcut expression [" + this.pointcut.getPointcutExpression() + "]", ex);
        }
        catch (InvocationTargetException ex2) {
            throw ex2.getTargetException();
        }
    }
    
    protected JoinPoint getJoinPoint() {
        return currentJoinPoint();
    }
    
    protected JoinPointMatch getJoinPointMatch() {
        final MethodInvocation mi = ExposeInvocationInterceptor.currentInvocation();
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        return this.getJoinPointMatch((ProxyMethodInvocation)mi);
    }
    
    protected JoinPointMatch getJoinPointMatch(final ProxyMethodInvocation pmi) {
        return (JoinPointMatch)pmi.getUserAttribute(this.pointcut.getExpression());
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": advice method [" + this.aspectJAdviceMethod + "]; " + "aspect name '" + this.aspectName + "'";
    }
    
    static {
        JOIN_POINT_KEY = JoinPoint.class.getName();
    }
    
    private static class AdviceExcludingMethodMatcher extends StaticMethodMatcher
    {
        private final Method adviceMethod;
        
        public AdviceExcludingMethodMatcher(final Method adviceMethod) {
            this.adviceMethod = adviceMethod;
        }
        
        @Override
        public boolean matches(final Method method, final Class<?> targetClass) {
            return !this.adviceMethod.equals(method);
        }
        
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AdviceExcludingMethodMatcher)) {
                return false;
            }
            final AdviceExcludingMethodMatcher otherMm = (AdviceExcludingMethodMatcher)other;
            return this.adviceMethod.equals(otherMm.adviceMethod);
        }
        
        @Override
        public int hashCode() {
            return this.adviceMethod.hashCode();
        }
    }
}
