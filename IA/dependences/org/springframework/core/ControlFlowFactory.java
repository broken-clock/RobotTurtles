// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.springframework.util.Assert;

public abstract class ControlFlowFactory
{
    public static ControlFlow createControlFlow() {
        return new Jdk14ControlFlow();
    }
    
    static class Jdk14ControlFlow implements ControlFlow
    {
        private StackTraceElement[] stack;
        
        public Jdk14ControlFlow() {
            this.stack = new Throwable().getStackTrace();
        }
        
        @Override
        public boolean under(final Class<?> clazz) {
            Assert.notNull(clazz, "Class must not be null");
            final String className = clazz.getName();
            for (int i = 0; i < this.stack.length; ++i) {
                if (this.stack[i].getClassName().equals(className)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean under(final Class<?> clazz, final String methodName) {
            Assert.notNull(clazz, "Class must not be null");
            Assert.notNull(methodName, "Method name must not be null");
            final String className = clazz.getName();
            for (int i = 0; i < this.stack.length; ++i) {
                if (this.stack[i].getClassName().equals(className) && this.stack[i].getMethodName().equals(methodName)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean underToken(final String token) {
            if (token == null) {
                return false;
            }
            final StringWriter sw = new StringWriter();
            new Throwable().printStackTrace(new PrintWriter(sw));
            final String stackTrace = sw.toString();
            return stackTrace.indexOf(token) != -1;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Jdk14ControlFlow: ");
            for (int i = 0; i < this.stack.length; ++i) {
                if (i > 0) {
                    sb.append("\n\t@");
                }
                sb.append(this.stack[i]);
            }
            return sb.toString();
        }
    }
}
