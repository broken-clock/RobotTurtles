// 
// Decompiled by Procyon v0.5.36
// 

package org.aopalliance.aop;

import java.io.Writer;
import java.io.StringWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.PrintStream;

public class AspectException extends RuntimeException
{
    private String message;
    private String stackTrace;
    private Throwable t;
    
    public Throwable getCause() {
        return this.t;
    }
    
    public String toString() {
        return this.getMessage();
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void printStackTrace() {
        System.err.print(this.stackTrace);
    }
    
    public void printStackTrace(final PrintStream out) {
        this.printStackTrace(new PrintWriter(out));
    }
    
    public void printStackTrace(final PrintWriter printWriter) {
        printWriter.print(this.stackTrace);
    }
    
    public AspectException(final String stackTrace) {
        super(stackTrace);
        this.message = stackTrace;
        this.stackTrace = stackTrace;
    }
    
    public AspectException(final String str, final Throwable t) {
        super(str + "; nested exception is " + t.getMessage());
        this.t = t;
        final StringWriter out = new StringWriter();
        t.printStackTrace(new PrintWriter(out));
        this.stackTrace = out.toString();
    }
}
