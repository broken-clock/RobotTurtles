// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.support;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.AccessException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.core.MethodParameter;
import java.util.LinkedHashSet;
import org.springframework.core.BridgeMethodResolver;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.expression.MethodExecutor;
import org.springframework.core.convert.TypeDescriptor;
import java.util.List;
import org.springframework.expression.EvaluationContext;
import java.util.HashMap;
import org.springframework.expression.MethodFilter;
import java.util.Map;
import org.springframework.expression.MethodResolver;

public class ReflectiveMethodResolver implements MethodResolver
{
    private final boolean useDistance;
    private Map<Class<?>, MethodFilter> filters;
    
    public ReflectiveMethodResolver() {
        this.useDistance = false;
    }
    
    public ReflectiveMethodResolver(final boolean useDistance) {
        this.useDistance = useDistance;
    }
    
    public void registerMethodFilter(final Class<?> type, final MethodFilter filter) {
        if (this.filters == null) {
            this.filters = new HashMap<Class<?>, MethodFilter>();
        }
        if (filter != null) {
            this.filters.put(type, filter);
        }
        else {
            this.filters.remove(type);
        }
    }
    
    @Override
    public MethodExecutor resolve(final EvaluationContext context, final Object targetObject, final String name, final List<TypeDescriptor> argumentTypes) throws AccessException {
        try {
            final TypeConverter typeConverter = context.getTypeConverter();
            final Class<?> type = (Class<?>)((targetObject instanceof Class) ? ((Class)targetObject) : targetObject.getClass());
            List<Method> methods = new ArrayList<Method>(Arrays.asList(this.getMethods(type, targetObject)));
            final MethodFilter filter = (this.filters != null) ? this.filters.get(type) : null;
            if (filter != null) {
                final List<Method> filtered = filter.filter(methods);
                methods = ((filtered instanceof ArrayList) ? filtered : new ArrayList<Method>(filtered));
            }
            if (methods.size() > 1) {
                Collections.sort(methods, new Comparator<Method>() {
                    @Override
                    public int compare(final Method m1, final Method m2) {
                        final int m1pl = m1.getParameterTypes().length;
                        final int m2pl = m2.getParameterTypes().length;
                        return new Integer(m1pl).compareTo(m2pl);
                    }
                });
            }
            for (int i = 0; i < methods.size(); ++i) {
                methods.set(i, BridgeMethodResolver.findBridgedMethod(methods.get(i)));
            }
            final Set<Method> methodsToIterate = new LinkedHashSet<Method>(methods);
            Method closeMatch = null;
            int closeMatchDistance = Integer.MAX_VALUE;
            Method matchRequiringConversion = null;
            boolean multipleOptions = false;
            for (final Method method : methodsToIterate) {
                if (method.getName().equals(name)) {
                    final Class<?>[] paramTypes = method.getParameterTypes();
                    final List<TypeDescriptor> paramDescriptors = new ArrayList<TypeDescriptor>(paramTypes.length);
                    for (int j = 0; j < paramTypes.length; ++j) {
                        paramDescriptors.add(new TypeDescriptor(new MethodParameter(method, j)));
                    }
                    ReflectionHelper.ArgumentsMatchInfo matchInfo = null;
                    if (method.isVarArgs() && argumentTypes.size() >= paramTypes.length - 1) {
                        matchInfo = ReflectionHelper.compareArgumentsVarargs(paramDescriptors, argumentTypes, typeConverter);
                    }
                    else if (paramTypes.length == argumentTypes.size()) {
                        matchInfo = ReflectionHelper.compareArguments(paramDescriptors, argumentTypes, typeConverter);
                    }
                    if (matchInfo == null) {
                        continue;
                    }
                    if (matchInfo.isExactMatch()) {
                        return new ReflectiveMethodExecutor(method);
                    }
                    if (matchInfo.isCloseMatch()) {
                        if (!this.useDistance) {
                            closeMatch = method;
                        }
                        else {
                            final int matchDistance = ReflectionHelper.getTypeDifferenceWeight(paramDescriptors, argumentTypes);
                            if (matchDistance >= closeMatchDistance) {
                                continue;
                            }
                            closeMatchDistance = matchDistance;
                            closeMatch = method;
                        }
                    }
                    else {
                        if (!matchInfo.isMatchRequiringConversion()) {
                            continue;
                        }
                        if (matchRequiringConversion != null) {
                            multipleOptions = true;
                        }
                        matchRequiringConversion = method;
                    }
                }
            }
            if (closeMatch != null) {
                return new ReflectiveMethodExecutor(closeMatch);
            }
            if (matchRequiringConversion == null) {
                return null;
            }
            if (multipleOptions) {
                throw new SpelEvaluationException(SpelMessage.MULTIPLE_POSSIBLE_METHODS, new Object[] { name });
            }
            return new ReflectiveMethodExecutor(matchRequiringConversion);
        }
        catch (EvaluationException ex) {
            throw new AccessException("Failed to resolve method", ex);
        }
    }
    
    private Method[] getMethods(final Class<?> type, final Object targetObject) {
        if (targetObject instanceof Class) {
            final Set<Method> methods = new HashSet<Method>();
            methods.addAll(Arrays.asList(this.getMethods(type)));
            methods.addAll(Arrays.asList(this.getMethods(targetObject.getClass())));
            return methods.toArray(new Method[methods.size()]);
        }
        return this.getMethods(type);
    }
    
    protected Method[] getMethods(final Class<?> type) {
        return type.getMethods();
    }
}
