// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

public abstract class NestedExceptionUtils
{
    public static String buildMessage(final String message, final Throwable cause) {
        if (cause != null) {
            final StringBuilder sb = new StringBuilder();
            if (message != null) {
                sb.append(message).append("; ");
            }
            sb.append("nested exception is ").append(cause);
            return sb.toString();
        }
        return message;
    }
}
