// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.Map;
import java.util.Collection;

public abstract class Assert
{
    public static void isTrue(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isTrue(final boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }
    
    public static void isNull(final Object object, final String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isNull(final Object object) {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }
    
    public static void notNull(final Object object, final String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notNull(final Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }
    
    public static void hasLength(final String text, final String message) {
        if (!StringUtils.hasLength(text)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void hasLength(final String text) {
        hasLength(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }
    
    public static void hasText(final String text, final String message) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void hasText(final String text) {
        hasText(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }
    
    public static void doesNotContain(final String textToSearch, final String substring, final String message) {
        if (StringUtils.hasLength(textToSearch) && StringUtils.hasLength(substring) && textToSearch.contains(substring)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void doesNotContain(final String textToSearch, final String substring) {
        doesNotContain(textToSearch, substring, "[Assertion failed] - this String argument must not contain the substring [" + substring + "]");
    }
    
    public static void notEmpty(final Object[] array, final String message) {
        if (ObjectUtils.isEmpty(array)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(final Object[] array) {
        notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
    }
    
    public static void noNullElements(final Object[] array, final String message) {
        if (array != null) {
            for (final Object element : array) {
                if (element == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }
    
    public static void noNullElements(final Object[] array) {
        noNullElements(array, "[Assertion failed] - this array must not contain any null elements");
    }
    
    public static void notEmpty(final Collection<?> collection, final String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(final Collection<?> collection) {
        notEmpty(collection, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }
    
    public static void notEmpty(final Map<?, ?> map, final String message) {
        if (CollectionUtils.isEmpty(map)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(final Map<?, ?> map) {
        notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
    }
    
    public static void isInstanceOf(final Class<?> clazz, final Object obj) {
        isInstanceOf(clazz, obj, "");
    }
    
    public static void isInstanceOf(final Class<?> type, final Object obj, final String message) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException((StringUtils.hasLength(message) ? (message + " ") : "") + "Object of class [" + ((obj != null) ? obj.getClass().getName() : "null") + "] must be an instance of " + type);
        }
    }
    
    public static void isAssignable(final Class<?> superType, final Class<?> subType) {
        isAssignable(superType, subType, "");
    }
    
    public static void isAssignable(final Class<?> superType, final Class<?> subType, final String message) {
        notNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(message + subType + " is not assignable to " + superType);
        }
    }
    
    public static void state(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }
    
    public static void state(final boolean expression) {
        state(expression, "[Assertion failed] - this state invariant must be true");
    }
}
