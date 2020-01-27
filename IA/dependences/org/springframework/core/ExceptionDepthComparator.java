// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.util.Assert;
import java.util.Comparator;

public class ExceptionDepthComparator implements Comparator<Class<? extends Throwable>>
{
    private final Class<? extends Throwable> targetException;
    
    public ExceptionDepthComparator(final Throwable exception) {
        Assert.notNull(exception, "Target exception must not be null");
        this.targetException = exception.getClass();
    }
    
    public ExceptionDepthComparator(final Class<? extends Throwable> exceptionType) {
        Assert.notNull(exceptionType, "Target exception type must not be null");
        this.targetException = exceptionType;
    }
    
    @Override
    public int compare(final Class<? extends Throwable> o1, final Class<? extends Throwable> o2) {
        final int depth1 = this.getDepth(o1, this.targetException, 0);
        final int depth2 = this.getDepth(o2, this.targetException, 0);
        return depth1 - depth2;
    }
    
    private int getDepth(final Class<?> declaredException, final Class<?> exceptionToMatch, final int depth) {
        if (declaredException.equals(exceptionToMatch)) {
            return depth;
        }
        if (Throwable.class.equals(exceptionToMatch)) {
            return Integer.MAX_VALUE;
        }
        return this.getDepth(declaredException, exceptionToMatch.getSuperclass(), depth + 1);
    }
    
    public static Class<? extends Throwable> findClosestMatch(final Collection<Class<? extends Throwable>> exceptionTypes, final Throwable targetException) {
        Assert.notEmpty(exceptionTypes, "Exception types must not be empty");
        if (exceptionTypes.size() == 1) {
            return exceptionTypes.iterator().next();
        }
        final List<Class<? extends Throwable>> handledExceptions = new ArrayList<Class<? extends Throwable>>(exceptionTypes);
        Collections.sort(handledExceptions, new ExceptionDepthComparator(targetException));
        return handledExceptions.get(0);
    }
}
