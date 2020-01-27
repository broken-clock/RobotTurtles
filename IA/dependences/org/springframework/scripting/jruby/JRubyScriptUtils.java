// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.jruby;

import org.jruby.RubyException;
import org.springframework.core.NestedRuntimeException;
import java.lang.reflect.Array;
import org.jruby.RubyArray;
import org.jruby.exceptions.RaiseException;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.Colon2Node;
import org.jruby.javasupport.JavaEmbedUtils;
import java.util.Collections;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.ast.Node;
import org.jruby.Ruby;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.jruby.RubyNil;
import org.jruby.runtime.DynamicScope;
import org.jruby.exceptions.JumpException;
import org.springframework.util.ClassUtils;

public abstract class JRubyScriptUtils
{
    public static Object createJRubyObject(final String scriptSource, final Class<?>... interfaces) throws JumpException {
        return createJRubyObject(scriptSource, interfaces, ClassUtils.getDefaultClassLoader());
    }
    
    public static Object createJRubyObject(final String scriptSource, final Class<?>[] interfaces, final ClassLoader classLoader) {
        final Ruby ruby = initializeRuntime();
        final Node scriptRootNode = ruby.parseEval(scriptSource, "", (DynamicScope)null, 0);
        IRubyObject rubyObject = ruby.runNormally(scriptRootNode);
        if (rubyObject instanceof RubyNil) {
            final String className = findClassName(scriptRootNode);
            rubyObject = ruby.evalScriptlet("\n" + className + ".new");
        }
        if (rubyObject instanceof RubyNil) {
            throw new IllegalStateException("Compilation of JRuby script returned RubyNil: " + rubyObject);
        }
        return Proxy.newProxyInstance(classLoader, interfaces, new RubyObjectInvocationHandler(rubyObject, ruby));
    }
    
    private static Ruby initializeRuntime() {
        return JavaEmbedUtils.initialize(Collections.EMPTY_LIST);
    }
    
    private static String findClassName(final Node rootNode) {
        final ClassNode classNode = findClassNode(rootNode);
        if (classNode == null) {
            throw new IllegalArgumentException("Unable to determine class name for root node '" + rootNode + "'");
        }
        final Colon2Node node = (Colon2Node)classNode.getCPath();
        return node.getName();
    }
    
    private static ClassNode findClassNode(final Node node) {
        if (node instanceof ClassNode) {
            return (ClassNode)node;
        }
        final List<Node> children = (List<Node>)node.childNodes();
        for (final Node child : children) {
            if (child instanceof ClassNode) {
                return (ClassNode)child;
            }
            if (!(child instanceof NewlineNode)) {
                continue;
            }
            final NewlineNode nn = (NewlineNode)child;
            final ClassNode found = findClassNode(nn.getNextNode());
            if (found != null) {
                return found;
            }
        }
        for (final Node child : children) {
            final ClassNode found2 = findClassNode(child);
            if (found2 != null) {
                return found2;
            }
        }
        return null;
    }
    
    private static class RubyObjectInvocationHandler implements InvocationHandler
    {
        private final IRubyObject rubyObject;
        private final Ruby ruby;
        
        public RubyObjectInvocationHandler(final IRubyObject rubyObject, final Ruby ruby) {
            this.rubyObject = rubyObject;
            this.ruby = ruby;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (ReflectionUtils.isEqualsMethod(method)) {
                return this.isProxyForSameRubyObject(args[0]);
            }
            if (ReflectionUtils.isHashCodeMethod(method)) {
                return this.rubyObject.hashCode();
            }
            if (ReflectionUtils.isToStringMethod(method)) {
                String toStringResult = this.rubyObject.toString();
                if (!StringUtils.hasText(toStringResult)) {
                    toStringResult = ObjectUtils.identityToString(this.rubyObject);
                }
                return "JRuby object [" + toStringResult + "]";
            }
            try {
                final IRubyObject[] rubyArgs = this.convertToRuby(args);
                final IRubyObject rubyResult = this.rubyObject.callMethod(this.ruby.getCurrentContext(), method.getName(), rubyArgs);
                return this.convertFromRuby(rubyResult, method.getReturnType());
            }
            catch (RaiseException ex) {
                throw new JRubyExecutionException(ex);
            }
        }
        
        private boolean isProxyForSameRubyObject(final Object other) {
            if (!Proxy.isProxyClass(other.getClass())) {
                return false;
            }
            final InvocationHandler ih = Proxy.getInvocationHandler(other);
            return ih instanceof RubyObjectInvocationHandler && this.rubyObject.equals(((RubyObjectInvocationHandler)ih).rubyObject);
        }
        
        private IRubyObject[] convertToRuby(final Object[] javaArgs) {
            if (javaArgs == null || javaArgs.length == 0) {
                return new IRubyObject[0];
            }
            final IRubyObject[] rubyArgs = new IRubyObject[javaArgs.length];
            for (int i = 0; i < javaArgs.length; ++i) {
                rubyArgs[i] = JavaEmbedUtils.javaToRuby(this.ruby, javaArgs[i]);
            }
            return rubyArgs;
        }
        
        private Object convertFromRuby(final IRubyObject rubyResult, final Class<?> returnType) {
            Object result = JavaEmbedUtils.rubyToJava(this.ruby, rubyResult, (Class)returnType);
            if (result instanceof RubyArray && returnType.isArray()) {
                result = this.convertFromRubyArray(((RubyArray)result).toJavaArray(), returnType);
            }
            return result;
        }
        
        private Object convertFromRubyArray(final IRubyObject[] rubyArray, final Class<?> returnType) {
            final Class<?> targetType = returnType.getComponentType();
            final Object javaArray = Array.newInstance(targetType, rubyArray.length);
            for (int i = 0; i < rubyArray.length; ++i) {
                final IRubyObject rubyObject = rubyArray[i];
                Array.set(javaArray, i, this.convertFromRuby(rubyObject, targetType));
            }
            return javaArray;
        }
    }
    
    public static class JRubyExecutionException extends NestedRuntimeException
    {
        public JRubyExecutionException(final RaiseException ex) {
            super(buildMessage(ex), (Throwable)ex);
        }
        
        private static String buildMessage(final RaiseException ex) {
            final RubyException rubyEx = ex.getException();
            return (rubyEx != null && rubyEx.message != null) ? rubyEx.message.toString() : "Unexpected JRuby error";
        }
    }
}
