// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import java.util.Set;
import java.util.HashSet;

public abstract class RemoteInvocationUtils
{
    public static void fillInClientStackTraceIfPossible(final Throwable ex) {
        if (ex != null) {
            final StackTraceElement[] clientStack = new Throwable().getStackTrace();
            final Set<Throwable> visitedExceptions = new HashSet<Throwable>();
            for (Throwable exToUpdate = ex; exToUpdate != null && !visitedExceptions.contains(exToUpdate); exToUpdate = exToUpdate.getCause()) {
                final StackTraceElement[] serverStack = exToUpdate.getStackTrace();
                final StackTraceElement[] combinedStack = new StackTraceElement[serverStack.length + clientStack.length];
                System.arraycopy(serverStack, 0, combinedStack, 0, serverStack.length);
                System.arraycopy(clientStack, 0, combinedStack, serverStack.length, clientStack.length);
                exToUpdate.setStackTrace(combinedStack);
                visitedExceptions.add(exToUpdate);
            }
        }
    }
}
