// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.lang.reflect.WildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeUtils
{
    public static boolean isAssignable(final Type lhsType, final Type rhsType) {
        Assert.notNull(lhsType, "Left-hand side type must not be null");
        Assert.notNull(rhsType, "Right-hand side type must not be null");
        if (lhsType.equals(rhsType) || lhsType.equals(Object.class)) {
            return true;
        }
        if (lhsType instanceof Class) {
            final Class<?> lhsClass = (Class<?>)lhsType;
            if (rhsType instanceof Class) {
                return ClassUtils.isAssignable(lhsClass, (Class<?>)rhsType);
            }
            if (rhsType instanceof ParameterizedType) {
                final Type rhsRaw = ((ParameterizedType)rhsType).getRawType();
                if (rhsRaw instanceof Class) {
                    return ClassUtils.isAssignable(lhsClass, (Class<?>)rhsRaw);
                }
            }
            else if (lhsClass.isArray() && rhsType instanceof GenericArrayType) {
                final Type rhsComponent = ((GenericArrayType)rhsType).getGenericComponentType();
                return isAssignable(lhsClass.getComponentType(), rhsComponent);
            }
        }
        if (lhsType instanceof ParameterizedType) {
            if (rhsType instanceof Class) {
                final Type lhsRaw = ((ParameterizedType)lhsType).getRawType();
                if (lhsRaw instanceof Class) {
                    return ClassUtils.isAssignable((Class<?>)lhsRaw, (Class<?>)rhsType);
                }
            }
            else if (rhsType instanceof ParameterizedType) {
                return isAssignable((ParameterizedType)lhsType, (ParameterizedType)rhsType);
            }
        }
        if (lhsType instanceof GenericArrayType) {
            final Type lhsComponent = ((GenericArrayType)lhsType).getGenericComponentType();
            if (rhsType instanceof Class) {
                final Class<?> rhsClass = (Class<?>)rhsType;
                if (rhsClass.isArray()) {
                    return isAssignable(lhsComponent, rhsClass.getComponentType());
                }
            }
            else if (rhsType instanceof GenericArrayType) {
                final Type rhsComponent = ((GenericArrayType)rhsType).getGenericComponentType();
                return isAssignable(lhsComponent, rhsComponent);
            }
        }
        return lhsType instanceof WildcardType && isAssignable((WildcardType)lhsType, rhsType);
    }
    
    private static boolean isAssignable(final ParameterizedType lhsType, final ParameterizedType rhsType) {
        if (lhsType.equals(rhsType)) {
            return true;
        }
        final Type[] lhsTypeArguments = lhsType.getActualTypeArguments();
        final Type[] rhsTypeArguments = rhsType.getActualTypeArguments();
        if (lhsTypeArguments.length != rhsTypeArguments.length) {
            return false;
        }
        for (int size = lhsTypeArguments.length, i = 0; i < size; ++i) {
            final Type lhsArg = lhsTypeArguments[i];
            final Type rhsArg = rhsTypeArguments[i];
            if (!lhsArg.equals(rhsArg) && (!(lhsArg instanceof WildcardType) || !isAssignable((WildcardType)lhsArg, rhsArg))) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isAssignable(final WildcardType lhsType, final Type rhsType) {
        Type[] lUpperBounds = lhsType.getUpperBounds();
        if (lUpperBounds.length == 0) {
            lUpperBounds = new Type[] { Object.class };
        }
        Type[] lLowerBounds = lhsType.getLowerBounds();
        if (lLowerBounds.length == 0) {
            lLowerBounds = new Type[] { null };
        }
        if (rhsType instanceof WildcardType) {
            final WildcardType rhsWcType = (WildcardType)rhsType;
            Type[] rUpperBounds = rhsWcType.getUpperBounds();
            if (rUpperBounds.length == 0) {
                rUpperBounds = new Type[] { Object.class };
            }
            Type[] rLowerBounds = rhsWcType.getLowerBounds();
            if (rLowerBounds.length == 0) {
                rLowerBounds = new Type[] { null };
            }
            for (final Type lBound : lUpperBounds) {
                for (final Type rBound : rUpperBounds) {
                    if (!isAssignableBound(lBound, rBound)) {
                        return false;
                    }
                }
                for (final Type rBound : rLowerBounds) {
                    if (!isAssignableBound(lBound, rBound)) {
                        return false;
                    }
                }
            }
            for (final Type lBound : lLowerBounds) {
                for (final Type rBound : rUpperBounds) {
                    if (!isAssignableBound(rBound, lBound)) {
                        return false;
                    }
                }
                for (final Type rBound : rLowerBounds) {
                    if (!isAssignableBound(rBound, lBound)) {
                        return false;
                    }
                }
            }
        }
        else {
            for (final Type lBound2 : lUpperBounds) {
                if (!isAssignableBound(lBound2, rhsType)) {
                    return false;
                }
            }
            for (final Type lBound2 : lLowerBounds) {
                if (!isAssignableBound(rhsType, lBound2)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isAssignableBound(final Type lhsType, final Type rhsType) {
        return rhsType == null || (lhsType != null && isAssignable(lhsType, rhsType));
    }
}
