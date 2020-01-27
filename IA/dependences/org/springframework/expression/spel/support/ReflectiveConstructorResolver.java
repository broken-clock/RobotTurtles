// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.support;

import org.springframework.expression.TypeConverter;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.AccessException;
import org.springframework.core.MethodParameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.Constructor;
import java.util.Comparator;
import org.springframework.expression.ConstructorExecutor;
import org.springframework.core.convert.TypeDescriptor;
import java.util.List;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ConstructorResolver;

public class ReflectiveConstructorResolver implements ConstructorResolver
{
    @Override
    public ConstructorExecutor resolve(final EvaluationContext context, final String typename, final List<TypeDescriptor> argumentTypes) throws AccessException {
        try {
            final TypeConverter typeConverter = context.getTypeConverter();
            final Class<?> type = context.getTypeLocator().findType(typename);
            final Constructor<?>[] ctors = type.getConstructors();
            Arrays.sort(ctors, new Comparator<Constructor<?>>() {
                @Override
                public int compare(final Constructor<?> c1, final Constructor<?> c2) {
                    final int c1pl = c1.getParameterTypes().length;
                    final int c2pl = c2.getParameterTypes().length;
                    return new Integer(c1pl).compareTo(c2pl);
                }
            });
            Constructor<?> closeMatch = null;
            Constructor<?> matchRequiringConversion = null;
            for (final Constructor<?> ctor : ctors) {
                final Class<?>[] paramTypes = ctor.getParameterTypes();
                final List<TypeDescriptor> paramDescriptors = new ArrayList<TypeDescriptor>(paramTypes.length);
                for (int i = 0; i < paramTypes.length; ++i) {
                    paramDescriptors.add(new TypeDescriptor(new MethodParameter(ctor, i)));
                }
                ReflectionHelper.ArgumentsMatchInfo matchInfo = null;
                if (ctor.isVarArgs() && argumentTypes.size() >= paramTypes.length - 1) {
                    matchInfo = ReflectionHelper.compareArgumentsVarargs(paramDescriptors, argumentTypes, typeConverter);
                }
                else if (paramTypes.length == argumentTypes.size()) {
                    matchInfo = ReflectionHelper.compareArguments(paramDescriptors, argumentTypes, typeConverter);
                }
                if (matchInfo != null) {
                    if (matchInfo.isExactMatch()) {
                        return new ReflectiveConstructorExecutor(ctor);
                    }
                    if (matchInfo.isCloseMatch()) {
                        closeMatch = ctor;
                    }
                    else if (matchInfo.isMatchRequiringConversion()) {
                        matchRequiringConversion = ctor;
                    }
                }
            }
            if (closeMatch != null) {
                return new ReflectiveConstructorExecutor(closeMatch);
            }
            if (matchRequiringConversion != null) {
                return new ReflectiveConstructorExecutor(matchRequiringConversion);
            }
            return null;
        }
        catch (EvaluationException ex) {
            throw new AccessException("Failed to resolve constructor", ex);
        }
    }
}
