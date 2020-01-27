// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.aspectj.runtime.internal.AroundClosure;
import org.springframework.util.Assert;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.lang.Signature;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.core.ParameterNameDiscoverer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

public class MethodInvocationProceedingJoinPoint implements ProceedingJoinPoint, JoinPoint.StaticPart
{
    private static final ParameterNameDiscoverer parameterNameDiscoverer;
    private final ProxyMethodInvocation methodInvocation;
    private Object[] defensiveCopyOfArgs;
    private Signature signature;
    private SourceLocation sourceLocation;
    
    public MethodInvocationProceedingJoinPoint(final ProxyMethodInvocation methodInvocation) {
        Assert.notNull(methodInvocation, "MethodInvocation must not be null");
        this.methodInvocation = methodInvocation;
    }
    
    public void set$AroundClosure(final AroundClosure aroundClosure) {
        throw new UnsupportedOperationException();
    }
    
    public Object proceed() throws Throwable {
        return this.methodInvocation.invocableClone().proceed();
    }
    
    public Object proceed(final Object[] arguments) throws Throwable {
        Assert.notNull(arguments, "Argument array passed to proceed cannot be null");
        if (arguments.length != this.methodInvocation.getArguments().length) {
            throw new IllegalArgumentException("Expecting " + this.methodInvocation.getArguments().length + " arguments to proceed, " + "but was passed " + arguments.length + " arguments");
        }
        this.methodInvocation.setArguments(arguments);
        return this.methodInvocation.invocableClone(arguments).proceed();
    }
    
    public Object getThis() {
        return this.methodInvocation.getProxy();
    }
    
    public Object getTarget() {
        return this.methodInvocation.getThis();
    }
    
    public Object[] getArgs() {
        if (this.defensiveCopyOfArgs == null) {
            final Object[] argsSource = this.methodInvocation.getArguments();
            System.arraycopy(argsSource, 0, this.defensiveCopyOfArgs = new Object[argsSource.length], 0, argsSource.length);
        }
        return this.defensiveCopyOfArgs;
    }
    
    public Signature getSignature() {
        if (this.signature == null) {
            this.signature = (Signature)new MethodSignatureImpl();
        }
        return this.signature;
    }
    
    public SourceLocation getSourceLocation() {
        if (this.sourceLocation == null) {
            this.sourceLocation = (SourceLocation)new SourceLocationImpl();
        }
        return this.sourceLocation;
    }
    
    public String getKind() {
        return "method-execution";
    }
    
    public int getId() {
        return 0;
    }
    
    public JoinPoint.StaticPart getStaticPart() {
        return (JoinPoint.StaticPart)this;
    }
    
    public String toShortString() {
        return "execution(" + this.getSignature().toShortString() + ")";
    }
    
    public String toLongString() {
        return "execution(" + this.getSignature().toLongString() + ")";
    }
    
    @Override
    public String toString() {
        return "execution(" + this.getSignature().toString() + ")";
    }
    
    static {
        parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }
    
    private class MethodSignatureImpl implements MethodSignature
    {
        private volatile String[] parameterNames;
        
        public String getName() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getName();
        }
        
        public int getModifiers() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getModifiers();
        }
        
        public Class<?> getDeclaringType() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getDeclaringClass();
        }
        
        public String getDeclaringTypeName() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getDeclaringClass().getName();
        }
        
        public Class<?> getReturnType() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getReturnType();
        }
        
        public Method getMethod() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod();
        }
        
        public Class<?>[] getParameterTypes() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getParameterTypes();
        }
        
        public String[] getParameterNames() {
            if (this.parameterNames == null) {
                this.parameterNames = MethodInvocationProceedingJoinPoint.parameterNameDiscoverer.getParameterNames(this.getMethod());
            }
            return this.parameterNames;
        }
        
        public Class<?>[] getExceptionTypes() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getExceptionTypes();
        }
        
        public String toShortString() {
            return this.toString(false, false, false, false);
        }
        
        public String toLongString() {
            return this.toString(true, true, true, true);
        }
        
        @Override
        public String toString() {
            return this.toString(false, true, false, true);
        }
        
        private String toString(final boolean includeModifier, final boolean includeReturnTypeAndArgs, final boolean useLongReturnAndArgumentTypeName, final boolean useLongTypeName) {
            final StringBuilder sb = new StringBuilder();
            if (includeModifier) {
                sb.append(Modifier.toString(this.getModifiers()));
                sb.append(" ");
            }
            if (includeReturnTypeAndArgs) {
                this.appendType(sb, this.getReturnType(), useLongReturnAndArgumentTypeName);
                sb.append(" ");
            }
            this.appendType(sb, this.getDeclaringType(), useLongTypeName);
            sb.append(".");
            sb.append(this.getMethod().getName());
            sb.append("(");
            final Class<?>[] parametersTypes = this.getParameterTypes();
            this.appendTypes(sb, parametersTypes, includeReturnTypeAndArgs, useLongReturnAndArgumentTypeName);
            sb.append(")");
            return sb.toString();
        }
        
        private void appendTypes(final StringBuilder sb, final Class<?>[] types, final boolean includeArgs, final boolean useLongReturnAndArgumentTypeName) {
            if (includeArgs) {
                for (int size = types.length, i = 0; i < size; ++i) {
                    this.appendType(sb, types[i], useLongReturnAndArgumentTypeName);
                    if (i < size - 1) {
                        sb.append(",");
                    }
                }
            }
            else if (types.length != 0) {
                sb.append("..");
            }
        }
        
        private void appendType(final StringBuilder sb, final Class<?> type, final boolean useLongTypeName) {
            if (type.isArray()) {
                this.appendType(sb, type.getComponentType(), useLongTypeName);
                sb.append("[]");
            }
            else {
                sb.append(useLongTypeName ? type.getName() : type.getSimpleName());
            }
        }
    }
    
    private class SourceLocationImpl implements SourceLocation
    {
        public Class<?> getWithinType() {
            if (MethodInvocationProceedingJoinPoint.this.methodInvocation.getThis() == null) {
                throw new UnsupportedOperationException("No source location joinpoint available: target is null");
            }
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getThis().getClass();
        }
        
        public String getFileName() {
            throw new UnsupportedOperationException();
        }
        
        public int getLine() {
            throw new UnsupportedOperationException();
        }
        
        @Deprecated
        public int getColumn() {
            throw new UnsupportedOperationException();
        }
    }
}
