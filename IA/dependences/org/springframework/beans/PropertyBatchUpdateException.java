// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.io.PrintWriter;
import java.io.PrintStream;
import org.springframework.util.ObjectUtils;
import org.springframework.util.Assert;

public class PropertyBatchUpdateException extends BeansException
{
    private PropertyAccessException[] propertyAccessExceptions;
    
    public PropertyBatchUpdateException(final PropertyAccessException[] propertyAccessExceptions) {
        super((String)null);
        Assert.notEmpty(propertyAccessExceptions, "At least 1 PropertyAccessException required");
        this.propertyAccessExceptions = propertyAccessExceptions;
    }
    
    public final int getExceptionCount() {
        return this.propertyAccessExceptions.length;
    }
    
    public final PropertyAccessException[] getPropertyAccessExceptions() {
        return this.propertyAccessExceptions;
    }
    
    public PropertyAccessException getPropertyAccessException(final String propertyName) {
        for (final PropertyAccessException pae : this.propertyAccessExceptions) {
            if (ObjectUtils.nullSafeEquals(propertyName, pae.getPropertyName())) {
                return pae;
            }
        }
        return null;
    }
    
    @Override
    public String getMessage() {
        final StringBuilder sb = new StringBuilder("Failed properties: ");
        for (int i = 0; i < this.propertyAccessExceptions.length; ++i) {
            sb.append(this.propertyAccessExceptions[i].getMessage());
            if (i < this.propertyAccessExceptions.length - 1) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append("; nested PropertyAccessExceptions (");
        sb.append(this.getExceptionCount()).append(") are:");
        for (int i = 0; i < this.propertyAccessExceptions.length; ++i) {
            sb.append('\n').append("PropertyAccessException ").append(i + 1).append(": ");
            sb.append(this.propertyAccessExceptions[i]);
        }
        return sb.toString();
    }
    
    @Override
    public void printStackTrace(final PrintStream ps) {
        synchronized (ps) {
            ps.println(this.getClass().getName() + "; nested PropertyAccessException details (" + this.getExceptionCount() + ") are:");
            for (int i = 0; i < this.propertyAccessExceptions.length; ++i) {
                ps.println("PropertyAccessException " + (i + 1) + ":");
                this.propertyAccessExceptions[i].printStackTrace(ps);
            }
        }
    }
    
    @Override
    public void printStackTrace(final PrintWriter pw) {
        synchronized (pw) {
            pw.println(this.getClass().getName() + "; nested PropertyAccessException details (" + this.getExceptionCount() + ") are:");
            for (int i = 0; i < this.propertyAccessExceptions.length; ++i) {
                pw.println("PropertyAccessException " + (i + 1) + ":");
                this.propertyAccessExceptions[i].printStackTrace(pw);
            }
        }
    }
    
    @Override
    public boolean contains(final Class<?> exType) {
        if (exType == null) {
            return false;
        }
        if (exType.isInstance(this)) {
            return true;
        }
        for (final PropertyAccessException pae : this.propertyAccessExceptions) {
            if (pae.contains(exType)) {
                return true;
            }
        }
        return false;
    }
}
