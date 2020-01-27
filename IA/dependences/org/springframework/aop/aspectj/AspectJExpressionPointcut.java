// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.aspectj.weaver.tools.MatchingContext;
import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.tools.ContextBasedMatcher;
import org.apache.commons.logging.LogFactory;
import java.util.HashSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.springframework.util.ObjectUtils;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.reflect.ShadowMatchImpl;
import org.aspectj.util.FuzzyBoolean;
import org.springframework.aop.framework.autoproxy.ProxyCreationContext;
import org.aspectj.weaver.tools.JoinPointMatch;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.AopUtils;
import org.aspectj.weaver.reflect.ReflectionWorld;
import org.aspectj.weaver.BCException;
import org.springframework.util.StringUtils;
import org.aspectj.weaver.tools.PointcutDesignatorHandler;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutParameter;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.aop.MethodMatcher;
import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.weaver.tools.ShadowMatch;
import java.lang.reflect.Method;
import java.util.Map;
import org.aspectj.weaver.tools.PointcutExpression;
import org.springframework.beans.factory.BeanFactory;
import org.apache.commons.logging.Log;
import org.aspectj.weaver.tools.PointcutPrimitive;
import java.util.Set;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.AbstractExpressionPointcut;

public class AspectJExpressionPointcut extends AbstractExpressionPointcut implements ClassFilter, IntroductionAwareMethodMatcher, BeanFactoryAware
{
    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES;
    private static final Log logger;
    private Class<?> pointcutDeclarationScope;
    private String[] pointcutParameterNames;
    private Class<?>[] pointcutParameterTypes;
    private BeanFactory beanFactory;
    private transient PointcutExpression pointcutExpression;
    private transient Map<Method, ShadowMatch> shadowMatchCache;
    
    public AspectJExpressionPointcut() {
        this.pointcutParameterNames = new String[0];
        this.pointcutParameterTypes = (Class<?>[])new Class[0];
        this.shadowMatchCache = new ConcurrentHashMap<Method, ShadowMatch>(32);
    }
    
    public AspectJExpressionPointcut(final Class<?> declarationScope, final String[] paramNames, final Class<?>[] paramTypes) {
        this.pointcutParameterNames = new String[0];
        this.pointcutParameterTypes = (Class<?>[])new Class[0];
        this.shadowMatchCache = new ConcurrentHashMap<Method, ShadowMatch>(32);
        this.pointcutDeclarationScope = declarationScope;
        if (paramNames.length != paramTypes.length) {
            throw new IllegalStateException("Number of pointcut parameter names must match number of pointcut parameter types");
        }
        this.pointcutParameterNames = paramNames;
        this.pointcutParameterTypes = paramTypes;
    }
    
    public void setPointcutDeclarationScope(final Class<?> pointcutDeclarationScope) {
        this.pointcutDeclarationScope = pointcutDeclarationScope;
    }
    
    public void setParameterNames(final String... names) {
        this.pointcutParameterNames = names;
    }
    
    public void setParameterTypes(final Class<?>... types) {
        this.pointcutParameterTypes = types;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    @Override
    public ClassFilter getClassFilter() {
        this.checkReadyToMatch();
        return this;
    }
    
    @Override
    public MethodMatcher getMethodMatcher() {
        this.checkReadyToMatch();
        return this;
    }
    
    private void checkReadyToMatch() {
        if (this.getExpression() == null) {
            throw new IllegalStateException("Must set property 'expression' before attempting to match");
        }
        if (this.pointcutExpression == null) {
            this.pointcutExpression = this.buildPointcutExpression();
        }
    }
    
    private PointcutExpression buildPointcutExpression() {
        final ClassLoader cl = (this.beanFactory instanceof ConfigurableBeanFactory) ? ((ConfigurableBeanFactory)this.beanFactory).getBeanClassLoader() : ClassUtils.getDefaultClassLoader();
        return this.buildPointcutExpression(cl);
    }
    
    private PointcutExpression buildPointcutExpression(final ClassLoader classLoader) {
        final PointcutParser parser = this.initializePointcutParser(classLoader);
        final PointcutParameter[] pointcutParameters = new PointcutParameter[this.pointcutParameterNames.length];
        for (int i = 0; i < pointcutParameters.length; ++i) {
            pointcutParameters[i] = parser.createPointcutParameter(this.pointcutParameterNames[i], (Class)this.pointcutParameterTypes[i]);
        }
        return parser.parsePointcutExpression(this.replaceBooleanOperators(this.getExpression()), (Class)this.pointcutDeclarationScope, pointcutParameters);
    }
    
    private PointcutParser initializePointcutParser(final ClassLoader cl) {
        final PointcutParser parser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution((Set)AspectJExpressionPointcut.SUPPORTED_PRIMITIVES, cl);
        parser.registerPointcutDesignatorHandler((PointcutDesignatorHandler)new BeanNamePointcutDesignatorHandler());
        return parser;
    }
    
    private String replaceBooleanOperators(final String pcExpr) {
        String result = StringUtils.replace(pcExpr, " and ", " && ");
        result = StringUtils.replace(result, " or ", " || ");
        result = StringUtils.replace(result, " not ", " ! ");
        return result;
    }
    
    public PointcutExpression getPointcutExpression() {
        this.checkReadyToMatch();
        return this.pointcutExpression;
    }
    
    @Override
    public boolean matches(final Class<?> targetClass) {
        this.checkReadyToMatch();
        try {
            return this.pointcutExpression.couldMatchJoinPointsInType((Class)targetClass);
        }
        catch (ReflectionWorld.ReflectionWorldException rwe) {
            AspectJExpressionPointcut.logger.debug("PointcutExpression matching rejected target class", (Throwable)rwe);
            try {
                return this.getFallbackPointcutExpression(targetClass).couldMatchJoinPointsInType((Class)targetClass);
            }
            catch (BCException bce) {
                AspectJExpressionPointcut.logger.debug("Fallback PointcutExpression matching rejected target class", (Throwable)bce);
                return false;
            }
        }
        catch (BCException ex) {
            AspectJExpressionPointcut.logger.debug("PointcutExpression matching rejected target class", (Throwable)ex);
            return false;
        }
    }
    
    @Override
    public boolean matches(final Method method, final Class<?> targetClass, final boolean beanHasIntroductions) {
        this.checkReadyToMatch();
        final Method targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        final ShadowMatch shadowMatch = this.getShadowMatch(targetMethod, method);
        if (shadowMatch.alwaysMatches()) {
            return true;
        }
        if (shadowMatch.neverMatches()) {
            return false;
        }
        if (beanHasIntroductions) {
            return true;
        }
        final RuntimeTestWalker walker = this.getRuntimeTestWalker(shadowMatch);
        return !walker.testsSubtypeSensitiveVars() || walker.testTargetInstanceOfResidue(targetClass);
    }
    
    @Override
    public boolean matches(final Method method, final Class<?> targetClass) {
        return this.matches(method, targetClass, false);
    }
    
    @Override
    public boolean isRuntime() {
        this.checkReadyToMatch();
        return this.pointcutExpression.mayNeedDynamicTest();
    }
    
    @Override
    public boolean matches(final Method method, final Class<?> targetClass, final Object[] args) {
        this.checkReadyToMatch();
        final ShadowMatch shadowMatch = this.getShadowMatch(AopUtils.getMostSpecificMethod(method, targetClass), method);
        final ShadowMatch originalShadowMatch = this.getShadowMatch(method, method);
        ProxyMethodInvocation pmi = null;
        Object targetObject = null;
        Object thisObject = null;
        try {
            final MethodInvocation mi = ExposeInvocationInterceptor.currentInvocation();
            targetObject = mi.getThis();
            if (!(mi instanceof ProxyMethodInvocation)) {
                throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
            }
            pmi = (ProxyMethodInvocation)mi;
            thisObject = pmi.getProxy();
        }
        catch (IllegalStateException ex) {
            AspectJExpressionPointcut.logger.debug("Couldn't access current invocation - matching with limited context: " + ex);
        }
        final JoinPointMatch joinPointMatch = shadowMatch.matchesJoinPoint(thisObject, targetObject, args);
        if (pmi != null) {
            final RuntimeTestWalker originalMethodResidueTest = this.getRuntimeTestWalker(originalShadowMatch);
            if (!originalMethodResidueTest.testThisInstanceOfResidue(thisObject.getClass())) {
                return false;
            }
            if (joinPointMatch.matches()) {
                this.bindParameters(pmi, joinPointMatch);
            }
        }
        return joinPointMatch.matches();
    }
    
    protected String getCurrentProxiedBeanName() {
        return ProxyCreationContext.getCurrentProxiedBeanName();
    }
    
    private PointcutExpression getFallbackPointcutExpression(final Class<?> targetClass) {
        final ClassLoader classLoader = targetClass.getClassLoader();
        return (classLoader != null) ? this.buildPointcutExpression(classLoader) : this.pointcutExpression;
    }
    
    private RuntimeTestWalker getRuntimeTestWalker(final ShadowMatch shadowMatch) {
        if (shadowMatch instanceof DefensiveShadowMatch) {
            return new RuntimeTestWalker(((DefensiveShadowMatch)shadowMatch).primary);
        }
        return new RuntimeTestWalker(shadowMatch);
    }
    
    private void bindParameters(final ProxyMethodInvocation invocation, final JoinPointMatch jpm) {
        invocation.setUserAttribute(this.getExpression(), jpm);
    }
    
    private ShadowMatch getShadowMatch(final Method targetMethod, final Method originalMethod) {
        ShadowMatch shadowMatch = this.shadowMatchCache.get(targetMethod);
        if (shadowMatch == null) {
            synchronized (this.shadowMatchCache) {
                Method methodToMatch = targetMethod;
                PointcutExpression fallbackPointcutExpression = null;
                shadowMatch = this.shadowMatchCache.get(methodToMatch);
                if (shadowMatch == null) {
                    Label_0174: {
                        try {
                            shadowMatch = this.pointcutExpression.matchesMethodExecution(targetMethod);
                        }
                        catch (ReflectionWorld.ReflectionWorldException ex) {
                            try {
                                fallbackPointcutExpression = this.getFallbackPointcutExpression(methodToMatch.getDeclaringClass());
                                shadowMatch = fallbackPointcutExpression.matchesMethodExecution(methodToMatch);
                            }
                            catch (ReflectionWorld.ReflectionWorldException ex2) {
                                if (targetMethod == originalMethod) {
                                    shadowMatch = (ShadowMatch)new ShadowMatchImpl(FuzzyBoolean.NO, (Test)null, (ExposedState)null, (PointcutParameter[])null);
                                    break Label_0174;
                                }
                                try {
                                    shadowMatch = this.pointcutExpression.matchesMethodExecution(originalMethod);
                                }
                                catch (ReflectionWorld.ReflectionWorldException ex3) {
                                    methodToMatch = originalMethod;
                                    fallbackPointcutExpression = this.getFallbackPointcutExpression(methodToMatch.getDeclaringClass());
                                    try {
                                        shadowMatch = fallbackPointcutExpression.matchesMethodExecution(methodToMatch);
                                    }
                                    catch (ReflectionWorld.ReflectionWorldException ex4) {
                                        shadowMatch = (ShadowMatch)new ShadowMatchImpl(FuzzyBoolean.NO, (Test)null, (ExposedState)null, (PointcutParameter[])null);
                                    }
                                }
                            }
                        }
                    }
                    if (shadowMatch.maybeMatches() && fallbackPointcutExpression != null) {
                        shadowMatch = (ShadowMatch)new DefensiveShadowMatch(shadowMatch, fallbackPointcutExpression.matchesMethodExecution(methodToMatch));
                    }
                    this.shadowMatchCache.put(targetMethod, shadowMatch);
                }
            }
        }
        return shadowMatch;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AspectJExpressionPointcut)) {
            return false;
        }
        final AspectJExpressionPointcut otherPc = (AspectJExpressionPointcut)other;
        return ObjectUtils.nullSafeEquals(this.getExpression(), otherPc.getExpression()) && ObjectUtils.nullSafeEquals(this.pointcutDeclarationScope, otherPc.pointcutDeclarationScope) && ObjectUtils.nullSafeEquals(this.pointcutParameterNames, otherPc.pointcutParameterNames) && ObjectUtils.nullSafeEquals(this.pointcutParameterTypes, otherPc.pointcutParameterTypes);
    }
    
    @Override
    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.getExpression());
        hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.pointcutDeclarationScope);
        hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.pointcutParameterNames);
        hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.pointcutParameterTypes);
        return hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AspectJExpressionPointcut: ");
        if (this.pointcutParameterNames != null && this.pointcutParameterTypes != null) {
            sb.append("(");
            for (int i = 0; i < this.pointcutParameterTypes.length; ++i) {
                sb.append(this.pointcutParameterTypes[i].getName());
                sb.append(" ");
                sb.append(this.pointcutParameterNames[i]);
                if (i + 1 < this.pointcutParameterTypes.length) {
                    sb.append(", ");
                }
            }
            sb.append(")");
        }
        sb.append(" ");
        if (this.getExpression() != null) {
            sb.append(this.getExpression());
        }
        else {
            sb.append("<pointcut expression not set>");
        }
        return sb.toString();
    }
    
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.shadowMatchCache = new ConcurrentHashMap<Method, ShadowMatch>(32);
    }
    
    static {
        (SUPPORTED_PRIMITIVES = new HashSet<PointcutPrimitive>()).add(PointcutPrimitive.EXECUTION);
        AspectJExpressionPointcut.SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
        AspectJExpressionPointcut.SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
        AspectJExpressionPointcut.SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
        AspectJExpressionPointcut.SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
        AspectJExpressionPointcut.SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
        AspectJExpressionPointcut.SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
        AspectJExpressionPointcut.SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
        AspectJExpressionPointcut.SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
        AspectJExpressionPointcut.SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
        logger = LogFactory.getLog(AspectJExpressionPointcut.class);
    }
    
    private class BeanNamePointcutDesignatorHandler implements PointcutDesignatorHandler
    {
        private static final String BEAN_DESIGNATOR_NAME = "bean";
        
        public String getDesignatorName() {
            return "bean";
        }
        
        public ContextBasedMatcher parse(final String expression) {
            return (ContextBasedMatcher)new BeanNameContextMatcher(expression);
        }
    }
    
    private class BeanNameContextMatcher implements ContextBasedMatcher
    {
        private final NamePattern expressionPattern;
        
        public BeanNameContextMatcher(final String expression) {
            this.expressionPattern = new NamePattern(expression);
        }
        
        @Deprecated
        public boolean couldMatchJoinPointsInType(final Class someClass) {
            return this.contextMatch(someClass) == org.aspectj.weaver.tools.FuzzyBoolean.YES;
        }
        
        @Deprecated
        public boolean couldMatchJoinPointsInType(final Class someClass, final MatchingContext context) {
            return this.contextMatch(someClass) == org.aspectj.weaver.tools.FuzzyBoolean.YES;
        }
        
        public boolean matchesDynamically(final MatchingContext context) {
            return true;
        }
        
        public org.aspectj.weaver.tools.FuzzyBoolean matchesStatically(final MatchingContext context) {
            return this.contextMatch(null);
        }
        
        public boolean mayNeedDynamicTest() {
            return false;
        }
        
        private org.aspectj.weaver.tools.FuzzyBoolean contextMatch(final Class<?> targetType) {
            final String advisedBeanName = AspectJExpressionPointcut.this.getCurrentProxiedBeanName();
            if (advisedBeanName == null) {
                return org.aspectj.weaver.tools.FuzzyBoolean.MAYBE;
            }
            if (BeanFactoryUtils.isGeneratedBeanName(advisedBeanName)) {
                return org.aspectj.weaver.tools.FuzzyBoolean.NO;
            }
            if (targetType != null) {
                final boolean isFactory = FactoryBean.class.isAssignableFrom(targetType);
                return org.aspectj.weaver.tools.FuzzyBoolean.fromBoolean(this.matchesBeanName(isFactory ? ("&" + advisedBeanName) : advisedBeanName));
            }
            return org.aspectj.weaver.tools.FuzzyBoolean.fromBoolean(this.matchesBeanName(advisedBeanName) || this.matchesBeanName("&" + advisedBeanName));
        }
        
        private boolean matchesBeanName(final String advisedBeanName) {
            if (this.expressionPattern.matches(advisedBeanName)) {
                return true;
            }
            if (AspectJExpressionPointcut.this.beanFactory != null) {
                final String[] aliases2;
                final String[] aliases = aliases2 = AspectJExpressionPointcut.this.beanFactory.getAliases(advisedBeanName);
                for (final String alias : aliases2) {
                    if (this.expressionPattern.matches(alias)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    private static class DefensiveShadowMatch implements ShadowMatch
    {
        private final ShadowMatch primary;
        private final ShadowMatch other;
        
        public DefensiveShadowMatch(final ShadowMatch primary, final ShadowMatch other) {
            this.primary = primary;
            this.other = other;
        }
        
        public boolean alwaysMatches() {
            return this.primary.alwaysMatches();
        }
        
        public boolean maybeMatches() {
            return this.primary.maybeMatches();
        }
        
        public boolean neverMatches() {
            return this.primary.neverMatches();
        }
        
        public JoinPointMatch matchesJoinPoint(final Object thisObject, final Object targetObject, final Object[] args) {
            try {
                return this.primary.matchesJoinPoint(thisObject, targetObject, args);
            }
            catch (ReflectionWorld.ReflectionWorldException ex) {
                return this.other.matchesJoinPoint(thisObject, targetObject, args);
            }
        }
        
        public void setMatchingContext(final MatchingContext aMatchContext) {
            this.primary.setMatchingContext(aMatchContext);
            this.other.setMatchingContext(aMatchContext);
        }
    }
}
