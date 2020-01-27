// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.beans.ConstructorProperties;
import org.springframework.util.MethodInvoker;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.core.GenericTypeResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.util.ObjectUtils;
import org.springframework.core.MethodParameter;
import java.util.HashSet;
import org.springframework.beans.TypeConverter;
import java.util.Map;
import org.springframework.beans.factory.config.BeanDefinition;
import java.util.Collection;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.lang.reflect.Modifier;
import org.springframework.util.ClassUtils;
import java.util.Iterator;
import org.springframework.core.ParameterNameDiscoverer;
import java.util.List;
import java.util.Set;
import java.security.AccessController;
import org.springframework.beans.factory.BeanFactory;
import java.security.PrivilegedAction;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import java.util.LinkedList;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import java.lang.reflect.Member;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeanWrapper;
import java.lang.reflect.Constructor;

class ConstructorResolver
{
    private final AbstractAutowireCapableBeanFactory beanFactory;
    
    public ConstructorResolver(final AbstractAutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    public BeanWrapper autowireConstructor(final String beanName, final RootBeanDefinition mbd, final Constructor<?>[] chosenCtors, final Object[] explicitArgs) {
        final BeanWrapperImpl bw = new BeanWrapperImpl();
        this.beanFactory.initBeanWrapper(bw);
        Constructor<?> constructorToUse = null;
        ArgumentsHolder argsHolderToUse = null;
        Object[] argsToUse = null;
        if (explicitArgs != null) {
            argsToUse = explicitArgs;
        }
        else {
            Object[] argsToResolve = null;
            synchronized (mbd.constructorArgumentLock) {
                constructorToUse = (Constructor<?>)mbd.resolvedConstructorOrFactoryMethod;
                if (constructorToUse != null && mbd.constructorArgumentsResolved) {
                    argsToUse = mbd.resolvedConstructorArguments;
                    if (argsToUse == null) {
                        argsToResolve = mbd.preparedConstructorArguments;
                    }
                }
            }
            if (argsToResolve != null) {
                argsToUse = this.resolvePreparedArguments(beanName, mbd, bw, constructorToUse, argsToResolve);
            }
        }
        if (constructorToUse == null) {
            final boolean autowiring = chosenCtors != null || mbd.getResolvedAutowireMode() == 3;
            ConstructorArgumentValues resolvedValues = null;
            int minNrOfArgs;
            if (explicitArgs != null) {
                minNrOfArgs = explicitArgs.length;
            }
            else {
                final ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
                resolvedValues = new ConstructorArgumentValues();
                minNrOfArgs = this.resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
            }
            Constructor<?>[] candidates = chosenCtors;
            if (candidates == null) {
                final Class<?> beanClass = mbd.getBeanClass();
                try {
                    candidates = (mbd.isNonPublicAccessAllowed() ? beanClass.getDeclaredConstructors() : beanClass.getConstructors());
                }
                catch (Throwable ex) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Resolution of declared constructors on bean Class [" + beanClass.getName() + "] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
                }
            }
            AutowireUtils.sortConstructors(candidates);
            int minTypeDiffWeight = Integer.MAX_VALUE;
            Set<Constructor<?>> ambiguousConstructors = null;
            List<Exception> causes = null;
            for (int i = 0; i < candidates.length; ++i) {
                final Constructor<?> candidate = candidates[i];
                final Class<?>[] paramTypes = candidate.getParameterTypes();
                if (constructorToUse != null && argsToUse.length > paramTypes.length) {
                    break;
                }
                if (paramTypes.length >= minNrOfArgs) {
                    ArgumentsHolder argsHolder = null;
                    Label_0607: {
                        if (resolvedValues != null) {
                            try {
                                String[] paramNames = ConstructorPropertiesChecker.evaluate(candidate, paramTypes.length);
                                if (paramNames == null) {
                                    final ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
                                    if (pnd != null) {
                                        paramNames = pnd.getParameterNames(candidate);
                                    }
                                }
                                argsHolder = this.createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames, candidate, autowiring);
                                break Label_0607;
                            }
                            catch (UnsatisfiedDependencyException ex2) {
                                if (this.beanFactory.logger.isTraceEnabled()) {
                                    this.beanFactory.logger.trace("Ignoring constructor [" + candidate + "] of bean '" + beanName + "': " + ex2);
                                }
                                if (i == candidates.length - 1 && constructorToUse == null) {
                                    if (causes != null) {
                                        for (final Exception cause : causes) {
                                            this.beanFactory.onSuppressedException(cause);
                                        }
                                    }
                                    throw ex2;
                                }
                                if (causes == null) {
                                    causes = new LinkedList<Exception>();
                                }
                                causes.add(ex2);
                                continue;
                            }
                        }
                        if (paramTypes.length != explicitArgs.length) {
                            continue;
                        }
                        argsHolder = new ArgumentsHolder(explicitArgs);
                    }
                    final int typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes);
                    if (typeDiffWeight < minTypeDiffWeight) {
                        constructorToUse = candidate;
                        argsHolderToUse = argsHolder;
                        argsToUse = argsHolder.arguments;
                        minTypeDiffWeight = typeDiffWeight;
                        ambiguousConstructors = null;
                    }
                    else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
                        if (ambiguousConstructors == null) {
                            ambiguousConstructors = new LinkedHashSet<Constructor<?>>();
                            ambiguousConstructors.add(constructorToUse);
                        }
                        ambiguousConstructors.add(candidate);
                    }
                }
            }
            if (constructorToUse == null) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Could not resolve matching constructor (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities)");
            }
            if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous constructor matches found in bean '" + beanName + "' " + "(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousConstructors);
            }
            if (explicitArgs == null) {
                argsHolderToUse.storeCache(mbd, constructorToUse);
            }
        }
        try {
            Object beanInstance;
            if (System.getSecurityManager() != null) {
                final Constructor<?> ctorToUse = constructorToUse;
                final Object[] argumentsToUse = argsToUse;
                beanInstance = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        return ConstructorResolver.this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, ConstructorResolver.this.beanFactory, ctorToUse, argumentsToUse);
                    }
                }, this.beanFactory.getAccessControlContext());
            }
            else {
                beanInstance = this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, this.beanFactory, constructorToUse, argsToUse);
            }
            bw.setWrappedInstance(beanInstance);
            return bw;
        }
        catch (Throwable ex3) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex3);
        }
    }
    
    public void resolveFactoryMethodIfPossible(final RootBeanDefinition mbd) {
        Class<?> factoryClass;
        boolean isStatic;
        if (mbd.getFactoryBeanName() != null) {
            factoryClass = this.beanFactory.getType(mbd.getFactoryBeanName());
            isStatic = false;
        }
        else {
            factoryClass = mbd.getBeanClass();
            isStatic = true;
        }
        factoryClass = ClassUtils.getUserClass(factoryClass);
        final Method[] candidates = this.getCandidateMethods(factoryClass, mbd);
        Method uniqueCandidate = null;
        for (final Method candidate : candidates) {
            if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
                if (uniqueCandidate == null) {
                    uniqueCandidate = candidate;
                }
                else if (!Arrays.equals(uniqueCandidate.getParameterTypes(), candidate.getParameterTypes())) {
                    uniqueCandidate = null;
                    break;
                }
            }
        }
        synchronized (mbd.constructorArgumentLock) {
            mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
        }
    }
    
    private Method[] getCandidateMethods(final Class<?> factoryClass, final RootBeanDefinition mbd) {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged((PrivilegedAction<Method[]>)new PrivilegedAction<Method[]>() {
                @Override
                public Method[] run() {
                    return mbd.isNonPublicAccessAllowed() ? ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods();
                }
            });
        }
        return mbd.isNonPublicAccessAllowed() ? ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods();
    }
    
    public BeanWrapper instantiateUsingFactoryMethod(final String beanName, final RootBeanDefinition mbd, final Object[] explicitArgs) {
        final BeanWrapperImpl bw = new BeanWrapperImpl();
        this.beanFactory.initBeanWrapper(bw);
        final String factoryBeanName = mbd.getFactoryBeanName();
        Object factoryBean;
        Class<?> factoryClass;
        boolean isStatic;
        if (factoryBeanName != null) {
            if (factoryBeanName.equals(beanName)) {
                throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "factory-bean reference points back to the same bean definition");
            }
            factoryBean = this.beanFactory.getBean(factoryBeanName);
            if (factoryBean == null) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "factory-bean '" + factoryBeanName + "' returned null");
            }
            factoryClass = factoryBean.getClass();
            isStatic = false;
        }
        else {
            if (!mbd.hasBeanClass()) {
                throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "bean definition declares neither a bean class nor a factory-bean reference");
            }
            factoryBean = null;
            factoryClass = mbd.getBeanClass();
            isStatic = true;
        }
        Method factoryMethodToUse = null;
        ArgumentsHolder argsHolderToUse = null;
        Object[] argsToUse = null;
        if (explicitArgs != null) {
            argsToUse = explicitArgs;
        }
        else {
            Object[] argsToResolve = null;
            synchronized (mbd.constructorArgumentLock) {
                factoryMethodToUse = (Method)mbd.resolvedConstructorOrFactoryMethod;
                if (factoryMethodToUse != null && mbd.constructorArgumentsResolved) {
                    argsToUse = mbd.resolvedConstructorArguments;
                    if (argsToUse == null) {
                        argsToResolve = mbd.preparedConstructorArguments;
                    }
                }
            }
            if (argsToResolve != null) {
                argsToUse = this.resolvePreparedArguments(beanName, mbd, bw, factoryMethodToUse, argsToResolve);
            }
        }
        if (factoryMethodToUse == null || argsToUse == null) {
            factoryClass = ClassUtils.getUserClass(factoryClass);
            final Method[] rawCandidates = this.getCandidateMethods(factoryClass, mbd);
            final List<Method> candidateSet = new ArrayList<Method>();
            for (final Method candidate : rawCandidates) {
                if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
                    candidateSet.add(candidate);
                }
            }
            final Method[] candidates = candidateSet.toArray(new Method[candidateSet.size()]);
            AutowireUtils.sortFactoryMethods(candidates);
            ConstructorArgumentValues resolvedValues = null;
            final boolean autowiring = mbd.getResolvedAutowireMode() == 3;
            int minTypeDiffWeight = Integer.MAX_VALUE;
            Set<Method> ambiguousFactoryMethods = null;
            int minNrOfArgs;
            if (explicitArgs != null) {
                minNrOfArgs = explicitArgs.length;
            }
            else {
                final ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
                resolvedValues = new ConstructorArgumentValues();
                minNrOfArgs = this.resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
            }
            List<Exception> causes = null;
            for (int i = 0; i < candidates.length; ++i) {
                final Method candidate2 = candidates[i];
                final Class<?>[] paramTypes = candidate2.getParameterTypes();
                if (paramTypes.length >= minNrOfArgs) {
                    ArgumentsHolder argsHolder = null;
                    Label_0720: {
                        if (resolvedValues != null) {
                            try {
                                String[] paramNames = null;
                                final ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
                                if (pnd != null) {
                                    paramNames = pnd.getParameterNames(candidate2);
                                }
                                argsHolder = this.createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames, candidate2, autowiring);
                                break Label_0720;
                            }
                            catch (UnsatisfiedDependencyException ex) {
                                if (this.beanFactory.logger.isTraceEnabled()) {
                                    this.beanFactory.logger.trace("Ignoring factory method [" + candidate2 + "] of bean '" + beanName + "': " + ex);
                                }
                                if (i == candidates.length - 1 && argsHolderToUse == null) {
                                    if (causes != null) {
                                        for (final Exception cause : causes) {
                                            this.beanFactory.onSuppressedException(cause);
                                        }
                                    }
                                    throw ex;
                                }
                                if (causes == null) {
                                    causes = new LinkedList<Exception>();
                                }
                                causes.add(ex);
                                continue;
                            }
                        }
                        if (paramTypes.length != explicitArgs.length) {
                            continue;
                        }
                        argsHolder = new ArgumentsHolder(explicitArgs);
                    }
                    final int typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes);
                    if (typeDiffWeight < minTypeDiffWeight) {
                        factoryMethodToUse = candidate2;
                        argsHolderToUse = argsHolder;
                        argsToUse = argsHolder.arguments;
                        minTypeDiffWeight = typeDiffWeight;
                        ambiguousFactoryMethods = null;
                    }
                    else if (factoryMethodToUse != null && typeDiffWeight == minTypeDiffWeight && !mbd.isLenientConstructorResolution() && paramTypes.length == factoryMethodToUse.getParameterTypes().length && !Arrays.equals(paramTypes, factoryMethodToUse.getParameterTypes())) {
                        if (ambiguousFactoryMethods == null) {
                            ambiguousFactoryMethods = new LinkedHashSet<Method>();
                            ambiguousFactoryMethods.add(factoryMethodToUse);
                        }
                        ambiguousFactoryMethods.add(candidate2);
                    }
                }
            }
            if (factoryMethodToUse == null) {
                final boolean hasArgs = resolvedValues.getArgumentCount() > 0;
                String argDesc = "";
                if (hasArgs) {
                    final List<String> argTypes = new ArrayList<String>();
                    for (final ConstructorArgumentValues.ValueHolder value : resolvedValues.getIndexedArgumentValues().values()) {
                        final String argType = (value.getType() != null) ? ClassUtils.getShortName(value.getType()) : value.getValue().getClass().getSimpleName();
                        argTypes.add(argType);
                    }
                    argDesc = StringUtils.collectionToCommaDelimitedString(argTypes);
                }
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "No matching factory method found: " + ((mbd.getFactoryBeanName() != null) ? ("factory bean '" + mbd.getFactoryBeanName() + "'; ") : "") + "factory method '" + mbd.getFactoryMethodName() + "(" + argDesc + ")'. " + "Check that a method with the specified name " + (hasArgs ? "and arguments " : "") + "exists and that it is " + (isStatic ? "static" : "non-static") + ".");
            }
            if (Void.TYPE.equals(factoryMethodToUse.getReturnType())) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid factory method '" + mbd.getFactoryMethodName() + "': needs to have a non-void return type!");
            }
            if (ambiguousFactoryMethods != null) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous factory method matches found in bean '" + beanName + "' " + "(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousFactoryMethods);
            }
            if (explicitArgs == null && argsHolderToUse != null) {
                argsHolderToUse.storeCache(mbd, factoryMethodToUse);
            }
        }
        try {
            Object beanInstance;
            if (System.getSecurityManager() != null) {
                final Object fb = factoryBean;
                final Method factoryMethod = factoryMethodToUse;
                final Object[] args = argsToUse;
                beanInstance = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        return ConstructorResolver.this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, ConstructorResolver.this.beanFactory, fb, factoryMethod, args);
                    }
                }, this.beanFactory.getAccessControlContext());
            }
            else {
                beanInstance = this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, this.beanFactory, factoryBean, factoryMethodToUse, argsToUse);
            }
            if (beanInstance == null) {
                return null;
            }
            bw.setWrappedInstance(beanInstance);
            return bw;
        }
        catch (Throwable ex2) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex2);
        }
    }
    
    private int resolveConstructorArguments(final String beanName, final RootBeanDefinition mbd, final BeanWrapper bw, final ConstructorArgumentValues cargs, final ConstructorArgumentValues resolvedValues) {
        final TypeConverter converter = (this.beanFactory.getCustomTypeConverter() != null) ? this.beanFactory.getCustomTypeConverter() : bw;
        final BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);
        int minNrOfArgs = cargs.getArgumentCount();
        for (final Map.Entry<Integer, ConstructorArgumentValues.ValueHolder> entry : cargs.getIndexedArgumentValues().entrySet()) {
            final int index = entry.getKey();
            if (index < 0) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid constructor argument index: " + index);
            }
            if (index > minNrOfArgs) {
                minNrOfArgs = index + 1;
            }
            final ConstructorArgumentValues.ValueHolder valueHolder = entry.getValue();
            if (valueHolder.isConverted()) {
                resolvedValues.addIndexedArgumentValue(index, valueHolder);
            }
            else {
                final Object resolvedValue = valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
                final ConstructorArgumentValues.ValueHolder resolvedValueHolder = new ConstructorArgumentValues.ValueHolder(resolvedValue, valueHolder.getType(), valueHolder.getName());
                resolvedValueHolder.setSource(valueHolder);
                resolvedValues.addIndexedArgumentValue(index, resolvedValueHolder);
            }
        }
        for (final ConstructorArgumentValues.ValueHolder valueHolder2 : cargs.getGenericArgumentValues()) {
            if (valueHolder2.isConverted()) {
                resolvedValues.addGenericArgumentValue(valueHolder2);
            }
            else {
                final Object resolvedValue2 = valueResolver.resolveValueIfNecessary("constructor argument", valueHolder2.getValue());
                final ConstructorArgumentValues.ValueHolder resolvedValueHolder2 = new ConstructorArgumentValues.ValueHolder(resolvedValue2, valueHolder2.getType(), valueHolder2.getName());
                resolvedValueHolder2.setSource(valueHolder2);
                resolvedValues.addGenericArgumentValue(resolvedValueHolder2);
            }
        }
        return minNrOfArgs;
    }
    
    private ArgumentsHolder createArgumentArray(final String beanName, final RootBeanDefinition mbd, final ConstructorArgumentValues resolvedValues, final BeanWrapper bw, final Class<?>[] paramTypes, final String[] paramNames, final Object methodOrCtor, final boolean autowiring) throws UnsatisfiedDependencyException {
        final String methodType = (methodOrCtor instanceof Constructor) ? "constructor" : "factory method";
        final TypeConverter converter = (this.beanFactory.getCustomTypeConverter() != null) ? this.beanFactory.getCustomTypeConverter() : bw;
        final ArgumentsHolder args = new ArgumentsHolder(paramTypes.length);
        final Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<ConstructorArgumentValues.ValueHolder>(paramTypes.length);
        final Set<String> autowiredBeanNames = new LinkedHashSet<String>(4);
        for (int paramIndex = 0; paramIndex < paramTypes.length; ++paramIndex) {
            final Class<?> paramType = paramTypes[paramIndex];
            final String paramName = (paramNames != null) ? paramNames[paramIndex] : null;
            ConstructorArgumentValues.ValueHolder valueHolder = resolvedValues.getArgumentValue(paramIndex, paramType, paramName, usedValueHolders);
            if (valueHolder == null && !autowiring) {
                valueHolder = resolvedValues.getGenericArgumentValue(null, null, usedValueHolders);
            }
            if (valueHolder != null) {
                usedValueHolders.add(valueHolder);
                final Object originalValue = valueHolder.getValue();
                Object convertedValue;
                if (valueHolder.isConverted()) {
                    convertedValue = valueHolder.getConvertedValue();
                    args.preparedArguments[paramIndex] = convertedValue;
                }
                else {
                    final ConstructorArgumentValues.ValueHolder sourceHolder = (ConstructorArgumentValues.ValueHolder)valueHolder.getSource();
                    final Object sourceValue = sourceHolder.getValue();
                    try {
                        convertedValue = converter.convertIfNecessary(originalValue, paramType, MethodParameter.forMethodOrConstructor(methodOrCtor, paramIndex));
                        args.resolveNecessary = true;
                        args.preparedArguments[paramIndex] = sourceValue;
                    }
                    catch (TypeMismatchException ex) {
                        throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, paramIndex, paramType, "Could not convert " + methodType + " argument value of type [" + ObjectUtils.nullSafeClassName(valueHolder.getValue()) + "] to required type [" + paramType.getName() + "]: " + ex.getMessage());
                    }
                }
                args.arguments[paramIndex] = convertedValue;
                args.rawArguments[paramIndex] = originalValue;
            }
            else {
                if (!autowiring) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, paramIndex, paramType, "Ambiguous " + methodType + " argument types - " + "did you specify the correct bean references as " + methodType + " arguments?");
                }
                try {
                    final MethodParameter param = MethodParameter.forMethodOrConstructor(methodOrCtor, paramIndex);
                    final Object autowiredArgument = this.resolveAutowiredArgument(param, beanName, autowiredBeanNames, converter);
                    args.rawArguments[paramIndex] = autowiredArgument;
                    args.arguments[paramIndex] = autowiredArgument;
                    args.preparedArguments[paramIndex] = new AutowiredArgumentMarker();
                    args.resolveNecessary = true;
                }
                catch (BeansException ex2) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, paramIndex, paramType, ex2);
                }
            }
        }
        for (final String autowiredBeanName : autowiredBeanNames) {
            this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
            if (this.beanFactory.logger.isDebugEnabled()) {
                this.beanFactory.logger.debug("Autowiring by type from bean name '" + beanName + "' via " + methodType + " to bean named '" + autowiredBeanName + "'");
            }
        }
        return args;
    }
    
    private Object[] resolvePreparedArguments(final String beanName, final RootBeanDefinition mbd, final BeanWrapper bw, final Member methodOrCtor, final Object[] argsToResolve) {
        final Class<?>[] paramTypes = (methodOrCtor instanceof Method) ? ((Method)methodOrCtor).getParameterTypes() : ((Constructor)methodOrCtor).getParameterTypes();
        final TypeConverter converter = (this.beanFactory.getCustomTypeConverter() != null) ? this.beanFactory.getCustomTypeConverter() : bw;
        final BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);
        final Object[] resolvedArgs = new Object[argsToResolve.length];
        for (int argIndex = 0; argIndex < argsToResolve.length; ++argIndex) {
            Object argValue = argsToResolve[argIndex];
            final MethodParameter methodParam = MethodParameter.forMethodOrConstructor(methodOrCtor, argIndex);
            GenericTypeResolver.resolveParameterType(methodParam, methodOrCtor.getDeclaringClass());
            if (argValue instanceof AutowiredArgumentMarker) {
                argValue = this.resolveAutowiredArgument(methodParam, beanName, null, converter);
            }
            else if (argValue instanceof BeanMetadataElement) {
                argValue = valueResolver.resolveValueIfNecessary("constructor argument", argValue);
            }
            else if (argValue instanceof String) {
                argValue = this.beanFactory.evaluateBeanDefinitionString((String)argValue, mbd);
            }
            final Class<?> paramType = paramTypes[argIndex];
            try {
                resolvedArgs[argIndex] = converter.convertIfNecessary(argValue, paramType, methodParam);
            }
            catch (TypeMismatchException ex) {
                final String methodType = (methodOrCtor instanceof Constructor) ? "constructor" : "factory method";
                throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, argIndex, paramType, "Could not convert " + methodType + " argument value of type [" + ObjectUtils.nullSafeClassName(argValue) + "] to required type [" + paramType.getName() + "]: " + ex.getMessage());
            }
        }
        return resolvedArgs;
    }
    
    protected Object resolveAutowiredArgument(final MethodParameter param, final String beanName, final Set<String> autowiredBeanNames, final TypeConverter typeConverter) {
        return this.beanFactory.resolveDependency(new DependencyDescriptor(param, true), beanName, autowiredBeanNames, typeConverter);
    }
    
    private static class ArgumentsHolder
    {
        public final Object[] rawArguments;
        public final Object[] arguments;
        public final Object[] preparedArguments;
        public boolean resolveNecessary;
        
        public ArgumentsHolder(final int size) {
            this.resolveNecessary = false;
            this.rawArguments = new Object[size];
            this.arguments = new Object[size];
            this.preparedArguments = new Object[size];
        }
        
        public ArgumentsHolder(final Object[] args) {
            this.resolveNecessary = false;
            this.rawArguments = args;
            this.arguments = args;
            this.preparedArguments = args;
        }
        
        public int getTypeDifferenceWeight(final Class<?>[] paramTypes) {
            final int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.arguments);
            final int rawTypeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.rawArguments) - 1024;
            return (rawTypeDiffWeight < typeDiffWeight) ? rawTypeDiffWeight : typeDiffWeight;
        }
        
        public int getAssignabilityWeight(final Class<?>[] paramTypes) {
            for (int i = 0; i < paramTypes.length; ++i) {
                if (!ClassUtils.isAssignableValue(paramTypes[i], this.arguments[i])) {
                    return Integer.MAX_VALUE;
                }
            }
            for (int i = 0; i < paramTypes.length; ++i) {
                if (!ClassUtils.isAssignableValue(paramTypes[i], this.rawArguments[i])) {
                    return 2147483135;
                }
            }
            return 2147482623;
        }
        
        public void storeCache(final RootBeanDefinition mbd, final Object constructorOrFactoryMethod) {
            synchronized (mbd.constructorArgumentLock) {
                mbd.resolvedConstructorOrFactoryMethod = constructorOrFactoryMethod;
                mbd.constructorArgumentsResolved = true;
                if (this.resolveNecessary) {
                    mbd.preparedConstructorArguments = this.preparedArguments;
                }
                else {
                    mbd.resolvedConstructorArguments = this.arguments;
                }
            }
        }
    }
    
    private static class AutowiredArgumentMarker
    {
    }
    
    private static class ConstructorPropertiesChecker
    {
        public static String[] evaluate(final Constructor<?> candidate, final int paramCount) {
            final ConstructorProperties cp = candidate.getAnnotation(ConstructorProperties.class);
            if (cp == null) {
                return null;
            }
            final String[] names = cp.value();
            if (names.length != paramCount) {
                throw new IllegalStateException("Constructor annotated with @ConstructorProperties but not corresponding to actual number of parameters (" + paramCount + "): " + candidate);
            }
            return names;
        }
    }
}
