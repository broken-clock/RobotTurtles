// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.groovy;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.w3c.dom.Element;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import java.util.Iterator;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.GeneratedClosure;
import groovy.xml.StreamingMarkupBuilder;
import groovy.lang.GroovyObject;
import groovy.lang.Reference;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import java.util.Map;
import groovy.lang.GroovyObjectSupport;

class GroovyDynamicElementReader extends GroovyObjectSupport
{
    private final String rootNamespace;
    private final Map<String, String> xmlNamespaces;
    private final BeanDefinitionParserDelegate delegate;
    private final GroovyBeanDefinitionWrapper beanDefinition;
    protected final boolean decorating;
    private boolean callAfterInvocation;
    public static /* synthetic */ long __timeStamp;
    public static /* synthetic */ long __timeStamp__239_neverHappen1392770109699;
    private static /* synthetic */ SoftReference $callSiteArray;
    private static /* synthetic */ Class $class$groovy$xml$StreamingMarkupBuilder;
    private static /* synthetic */ Class $class$org$springframework$beans$factory$xml$BeanDefinitionParserDelegate;
    private static /* synthetic */ Class $class$org$w3c$dom$Element;
    private static /* synthetic */ Class $class$java$lang$String;
    private static /* synthetic */ Class $class$java$io$StringWriter;
    private static /* synthetic */ Class $class$java$util$Map;
    private static /* synthetic */ Class $class$org$springframework$beans$factory$config$BeanDefinitionHolder;
    private static /* synthetic */ Class $class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper;
    private static /* synthetic */ Class $class$groovy$lang$Closure;
    
    public GroovyDynamicElementReader(final String namespace, final Map<String, String> namespaceMap, final BeanDefinitionParserDelegate delegate, final GroovyBeanDefinitionWrapper beanDefinition, final boolean decorating) {
        $getCallSiteArray();
        this.callAfterInvocation = true;
        this.rootNamespace = (String)ScriptBytecodeAdapter.castToType((Object)namespace, $get$$class$java$lang$String());
        this.xmlNamespaces = (Map<String, String>)ScriptBytecodeAdapter.castToType((Object)namespaceMap, $get$$class$java$util$Map());
        this.delegate = (BeanDefinitionParserDelegate)ScriptBytecodeAdapter.castToType((Object)delegate, $get$$class$org$springframework$beans$factory$xml$BeanDefinitionParserDelegate());
        this.beanDefinition = (GroovyBeanDefinitionWrapper)ScriptBytecodeAdapter.castToType((Object)beanDefinition, $get$$class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper());
        this.decorating = DefaultTypeTransformation.booleanUnbox((Object)DefaultTypeTransformation.box(decorating));
    }
    
    public Object invokeMethod(final String name, final Object args) {
        final Reference name2 = new Reference((Object)name);
        final Reference args2 = new Reference(args);
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox($getCallSiteArray[0].call((Object)name2.get(), (Object)"doCall"))) {
            final Object callable = $getCallSiteArray[1].call((Object)args2.get(), (Object)DefaultTypeTransformation.box(0));
            ScriptBytecodeAdapter.setProperty($getCallSiteArray[2].callGetProperty((Object)$get$$class$groovy$lang$Closure()), (Class)null, callable, "resolveStrategy");
            ScriptBytecodeAdapter.setProperty((Object)this, (Class)null, callable, "delegate");
            final Object result = $getCallSiteArray[3].call(callable);
            if (this.callAfterInvocation) {
                $getCallSiteArray[4].callCurrent((GroovyObject)this);
                this.callAfterInvocation = DefaultTypeTransformation.booleanUnbox((Object)DefaultTypeTransformation.box(false));
            }
            return result;
        }
        final Reference builder = new Reference((Object)ScriptBytecodeAdapter.castToType($getCallSiteArray[5].callConstructor((Object)$get$$class$groovy$xml$StreamingMarkupBuilder()), $get$$class$groovy$xml$StreamingMarkupBuilder()));
        final Reference myNamespace = new Reference((Object)this.rootNamespace);
        final Reference myNamespaces = new Reference((Object)this.xmlNamespaces);
        final Object callable2 = new GeneratedClosure((Object)this, (Object)this) {
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;
            private static /* synthetic */ Class $class$groovy$xml$StreamingMarkupBuilder;
            private static /* synthetic */ Class $class$java$lang$Object;
            private static /* synthetic */ Class $class$java$lang$String;
            private static /* synthetic */ Class $class$org$springframework$beans$factory$groovy$GroovyDynamicElementReader$_invokeMethod_closure1;
            private static /* synthetic */ Class $class$java$util$Iterator;
            private static /* synthetic */ Class $class$groovy$lang$Closure;
            
            public Object doCall(final Object it) {
                final CallSite[] $getCallSiteArray = $getCallSiteArray();
                Object namespace = null;
                final Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType($getCallSiteArray[0].call(myNamespaces.get()), $get$$class$java$util$Iterator());
                while (iterator.hasNext()) {
                    namespace = iterator.next();
                    $getCallSiteArray[1].call($getCallSiteArray[2].callGroovyObjectGetProperty((Object)this), (Object)ScriptBytecodeAdapter.createMap(new Object[] { $getCallSiteArray[3].callGetProperty(namespace), $getCallSiteArray[4].callGetProperty(namespace) }));
                }
                if (BytecodeInterface8.isOrigZ() && !GroovyDynamicElementReader$_invokeMethod_closure1.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
                    if (DefaultTypeTransformation.booleanUnbox(args2.get()) && $getCallSiteArray[9].call(args2.get(), (Object)DefaultTypeTransformation.box(-1)) instanceof Closure) {
                        ScriptBytecodeAdapter.setProperty($getCallSiteArray[10].callGetProperty((Object)$get$$class$groovy$lang$Closure()), (Class)null, $getCallSiteArray[11].call(args2.get(), (Object)DefaultTypeTransformation.box(-1)), "resolveStrategy");
                        ScriptBytecodeAdapter.setProperty(builder.get(), (Class)null, $getCallSiteArray[12].call(args2.get(), (Object)DefaultTypeTransformation.box(-1)), "delegate");
                    }
                }
                else if (DefaultTypeTransformation.booleanUnbox(args2.get()) && $getCallSiteArray[5].call(args2.get(), (Object)DefaultTypeTransformation.box(-1)) instanceof Closure) {
                    ScriptBytecodeAdapter.setProperty($getCallSiteArray[6].callGetProperty((Object)$get$$class$groovy$lang$Closure()), (Class)null, $getCallSiteArray[7].call(args2.get(), (Object)DefaultTypeTransformation.box(-1)), "resolveStrategy");
                    ScriptBytecodeAdapter.setProperty(builder.get(), (Class)null, $getCallSiteArray[8].call(args2.get(), (Object)DefaultTypeTransformation.box(-1)), "delegate");
                }
                return ScriptBytecodeAdapter.invokeMethodN($get$$class$org$springframework$beans$factory$groovy$GroovyDynamicElementReader$_invokeMethod_closure1(), ScriptBytecodeAdapter.getProperty($get$$class$org$springframework$beans$factory$groovy$GroovyDynamicElementReader$_invokeMethod_closure1(), $getCallSiteArray[13].callGroovyObjectGetProperty((Object)this), (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { myNamespace.get() }, new String[] { "", "" }), $get$$class$java$lang$String())), (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { name2.get() }, new String[] { "", "" }), $get$$class$java$lang$String()), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[] { args2.get() }, new int[] { 0 }));
            }
            
            public Object getArgs() {
                $getCallSiteArray();
                return args2.get();
            }
            
            public Object getMyNamespaces() {
                $getCallSiteArray();
                return myNamespaces.get();
            }
            
            public Object getMyNamespace() {
                $getCallSiteArray();
                return myNamespace.get();
            }
            
            public StreamingMarkupBuilder getBuilder() {
                $getCallSiteArray();
                return (StreamingMarkupBuilder)ScriptBytecodeAdapter.castToType(builder.get(), $get$$class$groovy$xml$StreamingMarkupBuilder());
            }
            
            public String getName() {
                $getCallSiteArray();
                return (String)ScriptBytecodeAdapter.castToType(name2.get(), $get$$class$java$lang$String());
            }
            
            public Object doCall() {
                return $getCallSiteArray()[14].callCurrent((GroovyObject)this, (Object)ScriptBytecodeAdapter.createPojoWrapper((Object)null, $get$$class$java$lang$Object()));
            }
            
            public static /* synthetic */ void __$swapInit() {
                $getCallSiteArray();
                GroovyDynamicElementReader$_invokeMethod_closure1.$callSiteArray = null;
            }
            
            static {
                __$swapInit();
            }
            
            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                final String[] array = new String[15];
                $createCallSiteArray_1(array);
                return new CallSiteArray($get$$class$org$springframework$beans$factory$groovy$GroovyDynamicElementReader$_invokeMethod_closure1(), array);
            }
            
            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray $createCallSiteArray;
                if (GroovyDynamicElementReader$_invokeMethod_closure1.$callSiteArray == null || ($createCallSiteArray = GroovyDynamicElementReader$_invokeMethod_closure1.$callSiteArray.get()) == null) {
                    $createCallSiteArray = $createCallSiteArray();
                    GroovyDynamicElementReader$_invokeMethod_closure1.$callSiteArray = new SoftReference($createCallSiteArray);
                }
                return $createCallSiteArray.array;
            }
            
            private static /* synthetic */ Class $get$$class$groovy$xml$StreamingMarkupBuilder() {
                Class $class$groovy$xml$StreamingMarkupBuilder;
                if (($class$groovy$xml$StreamingMarkupBuilder = GroovyDynamicElementReader$_invokeMethod_closure1.$class$groovy$xml$StreamingMarkupBuilder) == null) {
                    $class$groovy$xml$StreamingMarkupBuilder = (GroovyDynamicElementReader$_invokeMethod_closure1.$class$groovy$xml$StreamingMarkupBuilder = class$("groovy.xml.StreamingMarkupBuilder"));
                }
                return $class$groovy$xml$StreamingMarkupBuilder;
            }
            
            private static /* synthetic */ Class $get$$class$java$lang$Object() {
                Class $class$java$lang$Object;
                if (($class$java$lang$Object = GroovyDynamicElementReader$_invokeMethod_closure1.$class$java$lang$Object) == null) {
                    $class$java$lang$Object = (GroovyDynamicElementReader$_invokeMethod_closure1.$class$java$lang$Object = class$("java.lang.Object"));
                }
                return $class$java$lang$Object;
            }
            
            private static /* synthetic */ Class $get$$class$java$lang$String() {
                Class $class$java$lang$String;
                if (($class$java$lang$String = GroovyDynamicElementReader$_invokeMethod_closure1.$class$java$lang$String) == null) {
                    $class$java$lang$String = (GroovyDynamicElementReader$_invokeMethod_closure1.$class$java$lang$String = class$("java.lang.String"));
                }
                return $class$java$lang$String;
            }
            
            private static /* synthetic */ Class $get$$class$org$springframework$beans$factory$groovy$GroovyDynamicElementReader$_invokeMethod_closure1() {
                Class $class$org$springframework$beans$factory$groovy$GroovyDynamicElementReader$_invokeMethod_closure1;
                if (($class$org$springframework$beans$factory$groovy$GroovyDynamicElementReader$_invokeMethod_closure1 = GroovyDynamicElementReader$_invokeMethod_closure1.$class$org$springframework$beans$factory$groovy$GroovyDynamicElementReader$_invokeMethod_closure1) == null) {
                    $class$org$springframework$beans$factory$groovy$GroovyDynamicElementReader$_invokeMethod_closure1 = (GroovyDynamicElementReader$_invokeMethod_closure1.$class$org$springframework$beans$factory$groovy$GroovyDynamicElementReader$_invokeMethod_closure1 = class$("org.springframework.beans.factory.groovy.GroovyDynamicElementReader$_invokeMethod_closure1"));
                }
                return $class$org$springframework$beans$factory$groovy$GroovyDynamicElementReader$_invokeMethod_closure1;
            }
            
            private static /* synthetic */ Class $get$$class$java$util$Iterator() {
                Class $class$java$util$Iterator;
                if (($class$java$util$Iterator = GroovyDynamicElementReader$_invokeMethod_closure1.$class$java$util$Iterator) == null) {
                    $class$java$util$Iterator = (GroovyDynamicElementReader$_invokeMethod_closure1.$class$java$util$Iterator = class$("java.util.Iterator"));
                }
                return $class$java$util$Iterator;
            }
            
            private static /* synthetic */ Class $get$$class$groovy$lang$Closure() {
                Class $class$groovy$lang$Closure;
                if (($class$groovy$lang$Closure = GroovyDynamicElementReader$_invokeMethod_closure1.$class$groovy$lang$Closure) == null) {
                    $class$groovy$lang$Closure = (GroovyDynamicElementReader$_invokeMethod_closure1.$class$groovy$lang$Closure = class$("groovy.lang.Closure"));
                }
                return $class$groovy$lang$Closure;
            }
            
            static /* synthetic */ Class class$(final String className) {
                try {
                    return Class.forName(className);
                }
                catch (ClassNotFoundException ex) {
                    throw new NoClassDefFoundError(ex.getMessage());
                }
            }
        };
        ScriptBytecodeAdapter.setProperty($getCallSiteArray[6].callGetProperty((Object)$get$$class$groovy$lang$Closure()), (Class)null, callable2, "resolveStrategy");
        ScriptBytecodeAdapter.setProperty((Object)builder.get(), (Class)null, callable2, "delegate");
        final Object writable = $getCallSiteArray[7].call((Object)builder.get(), callable2);
        final Object sw = $getCallSiteArray[8].callConstructor((Object)$get$$class$java$io$StringWriter());
        $getCallSiteArray[9].call(writable, sw);
        final Element element = (Element)ScriptBytecodeAdapter.castToType($getCallSiteArray[10].callGetProperty($getCallSiteArray[11].call($getCallSiteArray[12].callGetProperty((Object)this.delegate), $getCallSiteArray[13].call(sw))), $get$$class$org$w3c$dom$Element());
        $getCallSiteArray[14].call((Object)this.delegate, (Object)element);
        if (this.decorating) {
            BeanDefinitionHolder holder = (BeanDefinitionHolder)ScriptBytecodeAdapter.castToType($getCallSiteArray[15].callGetProperty((Object)this.beanDefinition), $get$$class$org$springframework$beans$factory$config$BeanDefinitionHolder());
            holder = (BeanDefinitionHolder)ScriptBytecodeAdapter.castToType($getCallSiteArray[16].call((Object)this.delegate, (Object)element, (Object)holder, (Object)null), $get$$class$org$springframework$beans$factory$config$BeanDefinitionHolder());
            $getCallSiteArray[17].call((Object)this.beanDefinition, (Object)holder);
        }
        else {
            final Object beanDefinition = $getCallSiteArray[18].call((Object)this.delegate, (Object)element);
            if (DefaultTypeTransformation.booleanUnbox(beanDefinition)) {
                $getCallSiteArray[19].call((Object)this.beanDefinition, beanDefinition);
            }
        }
        if (this.callAfterInvocation) {
            $getCallSiteArray[20].callCurrent((GroovyObject)this);
            this.callAfterInvocation = DefaultTypeTransformation.booleanUnbox((Object)DefaultTypeTransformation.box(false));
        }
        return element;
    }
    
    protected void afterInvocation() {
        $getCallSiteArray();
    }
    
    public static /* synthetic */ void __$swapInit() {
        $getCallSiteArray();
        GroovyDynamicElementReader.$callSiteArray = null;
    }
    
    static {
        __$swapInit();
        GroovyDynamicElementReader.__timeStamp__239_neverHappen1392770109699 = (long)DefaultTypeTransformation.box(0L);
        GroovyDynamicElementReader.__timeStamp = (long)DefaultTypeTransformation.box(1392770109699L);
    }
    
    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        final String[] array = new String[21];
        $createCallSiteArray_1(array);
        return new CallSiteArray($get$$class$org$springframework$beans$factory$groovy$GroovyDynamicElementReader(), array);
    }
    
    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray $createCallSiteArray;
        if (GroovyDynamicElementReader.$callSiteArray == null || ($createCallSiteArray = GroovyDynamicElementReader.$callSiteArray.get()) == null) {
            $createCallSiteArray = $createCallSiteArray();
            GroovyDynamicElementReader.$callSiteArray = new SoftReference($createCallSiteArray);
        }
        return $createCallSiteArray.array;
    }
    
    private static /* synthetic */ Class $get$$class$groovy$xml$StreamingMarkupBuilder() {
        Class $class$groovy$xml$StreamingMarkupBuilder;
        if (($class$groovy$xml$StreamingMarkupBuilder = GroovyDynamicElementReader.$class$groovy$xml$StreamingMarkupBuilder) == null) {
            $class$groovy$xml$StreamingMarkupBuilder = (GroovyDynamicElementReader.$class$groovy$xml$StreamingMarkupBuilder = class$("groovy.xml.StreamingMarkupBuilder"));
        }
        return $class$groovy$xml$StreamingMarkupBuilder;
    }
    
    private static /* synthetic */ Class $get$$class$org$springframework$beans$factory$xml$BeanDefinitionParserDelegate() {
        Class $class$org$springframework$beans$factory$xml$BeanDefinitionParserDelegate;
        if (($class$org$springframework$beans$factory$xml$BeanDefinitionParserDelegate = GroovyDynamicElementReader.$class$org$springframework$beans$factory$xml$BeanDefinitionParserDelegate) == null) {
            $class$org$springframework$beans$factory$xml$BeanDefinitionParserDelegate = (GroovyDynamicElementReader.$class$org$springframework$beans$factory$xml$BeanDefinitionParserDelegate = class$("org.springframework.beans.factory.xml.BeanDefinitionParserDelegate"));
        }
        return $class$org$springframework$beans$factory$xml$BeanDefinitionParserDelegate;
    }
    
    private static /* synthetic */ Class $get$$class$org$w3c$dom$Element() {
        Class $class$org$w3c$dom$Element;
        if (($class$org$w3c$dom$Element = GroovyDynamicElementReader.$class$org$w3c$dom$Element) == null) {
            $class$org$w3c$dom$Element = (GroovyDynamicElementReader.$class$org$w3c$dom$Element = class$("org.w3c.dom.Element"));
        }
        return $class$org$w3c$dom$Element;
    }
    
    private static /* synthetic */ Class $get$$class$java$lang$String() {
        Class $class$java$lang$String;
        if (($class$java$lang$String = GroovyDynamicElementReader.$class$java$lang$String) == null) {
            $class$java$lang$String = (GroovyDynamicElementReader.$class$java$lang$String = class$("java.lang.String"));
        }
        return $class$java$lang$String;
    }
    
    private static /* synthetic */ Class $get$$class$java$io$StringWriter() {
        Class $class$java$io$StringWriter;
        if (($class$java$io$StringWriter = GroovyDynamicElementReader.$class$java$io$StringWriter) == null) {
            $class$java$io$StringWriter = (GroovyDynamicElementReader.$class$java$io$StringWriter = class$("java.io.StringWriter"));
        }
        return $class$java$io$StringWriter;
    }
    
    private static /* synthetic */ Class $get$$class$java$util$Map() {
        Class $class$java$util$Map;
        if (($class$java$util$Map = GroovyDynamicElementReader.$class$java$util$Map) == null) {
            $class$java$util$Map = (GroovyDynamicElementReader.$class$java$util$Map = class$("java.util.Map"));
        }
        return $class$java$util$Map;
    }
    
    private static /* synthetic */ Class $get$$class$org$springframework$beans$factory$config$BeanDefinitionHolder() {
        Class $class$org$springframework$beans$factory$config$BeanDefinitionHolder;
        if (($class$org$springframework$beans$factory$config$BeanDefinitionHolder = GroovyDynamicElementReader.$class$org$springframework$beans$factory$config$BeanDefinitionHolder) == null) {
            $class$org$springframework$beans$factory$config$BeanDefinitionHolder = (GroovyDynamicElementReader.$class$org$springframework$beans$factory$config$BeanDefinitionHolder = class$("org.springframework.beans.factory.config.BeanDefinitionHolder"));
        }
        return $class$org$springframework$beans$factory$config$BeanDefinitionHolder;
    }
    
    private static /* synthetic */ Class $get$$class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper() {
        Class $class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper;
        if (($class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper = GroovyDynamicElementReader.$class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper) == null) {
            $class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper = (GroovyDynamicElementReader.$class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper = class$("org.springframework.beans.factory.groovy.GroovyBeanDefinitionWrapper"));
        }
        return $class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper;
    }
    
    private static /* synthetic */ Class $get$$class$groovy$lang$Closure() {
        Class $class$groovy$lang$Closure;
        if (($class$groovy$lang$Closure = GroovyDynamicElementReader.$class$groovy$lang$Closure) == null) {
            $class$groovy$lang$Closure = (GroovyDynamicElementReader.$class$groovy$lang$Closure = class$("groovy.lang.Closure"));
        }
        return $class$groovy$lang$Closure;
    }
    
    static /* synthetic */ Class class$(final String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
}
