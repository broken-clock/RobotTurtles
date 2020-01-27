// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import org.springframework.core.NestedRuntimeException;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.FatalBeanException;

public class BeanCreationException extends FatalBeanException
{
    private String beanName;
    private String resourceDescription;
    private List<Throwable> relatedCauses;
    
    public BeanCreationException(final String msg) {
        super(msg);
    }
    
    public BeanCreationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public BeanCreationException(final String beanName, final String msg) {
        super("Error creating bean with name '" + beanName + "': " + msg);
        this.beanName = beanName;
    }
    
    public BeanCreationException(final String beanName, final String msg, final Throwable cause) {
        this(beanName, msg);
        this.initCause(cause);
    }
    
    public BeanCreationException(final String resourceDescription, final String beanName, final String msg) {
        super("Error creating bean with name '" + beanName + "'" + ((resourceDescription != null) ? (" defined in " + resourceDescription) : "") + ": " + msg);
        this.resourceDescription = resourceDescription;
        this.beanName = beanName;
    }
    
    public BeanCreationException(final String resourceDescription, final String beanName, final String msg, final Throwable cause) {
        this(resourceDescription, beanName, msg);
        this.initCause(cause);
    }
    
    public String getBeanName() {
        return this.beanName;
    }
    
    public String getResourceDescription() {
        return this.resourceDescription;
    }
    
    public void addRelatedCause(final Throwable ex) {
        if (this.relatedCauses == null) {
            this.relatedCauses = new LinkedList<Throwable>();
        }
        this.relatedCauses.add(ex);
    }
    
    public Throwable[] getRelatedCauses() {
        if (this.relatedCauses == null) {
            return null;
        }
        return this.relatedCauses.toArray(new Throwable[this.relatedCauses.size()]);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (this.relatedCauses != null) {
            for (final Throwable relatedCause : this.relatedCauses) {
                sb.append("\nRelated cause: ");
                sb.append(relatedCause);
            }
        }
        return sb.toString();
    }
    
    @Override
    public void printStackTrace(final PrintStream ps) {
        synchronized (ps) {
            super.printStackTrace(ps);
            if (this.relatedCauses != null) {
                for (final Throwable relatedCause : this.relatedCauses) {
                    ps.println("Related cause:");
                    relatedCause.printStackTrace(ps);
                }
            }
        }
    }
    
    @Override
    public void printStackTrace(final PrintWriter pw) {
        synchronized (pw) {
            super.printStackTrace(pw);
            if (this.relatedCauses != null) {
                for (final Throwable relatedCause : this.relatedCauses) {
                    pw.println("Related cause:");
                    relatedCause.printStackTrace(pw);
                }
            }
        }
    }
    
    @Override
    public boolean contains(final Class<?> exClass) {
        if (super.contains(exClass)) {
            return true;
        }
        if (this.relatedCauses != null) {
            for (final Throwable relatedCause : this.relatedCauses) {
                if (relatedCause instanceof NestedRuntimeException && ((NestedRuntimeException)relatedCause).contains(exClass)) {
                    return true;
                }
            }
        }
        return false;
    }
}
