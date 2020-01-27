// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.groovy;

import groovy.lang.GroovyObjectSupport;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import groovy.lang.GString;
import java.util.Arrays;
import org.springframework.beans.factory.config.BeanDefinition;
import java.util.List;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import java.io.IOException;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.beans.factory.xml.NamespaceHandler;
import java.util.Iterator;
import org.springframework.core.io.DescriptiveResource;
import java.util.Collection;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.Location;
import groovy.lang.GroovyShell;
import groovy.lang.Closure;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.Resource;
import java.util.HashMap;
import groovy.lang.GroovySystem;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import java.util.Map;
import groovy.lang.Binding;
import groovy.lang.MetaClass;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import groovy.lang.GroovyObject;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;

public class GroovyBeanDefinitionReader extends AbstractBeanDefinitionReader implements GroovyObject
{
    private final XmlBeanDefinitionReader xmlBeanDefinitionReader;
    private MetaClass metaClass;
    private Binding binding;
    private GroovyBeanDefinitionWrapper currentBeanDefinition;
    private final Map<String, String> namespaces;
    private final Map<String, DeferredProperty> deferredProperties;
    
    public GroovyBeanDefinitionReader(final BeanDefinitionRegistry registry) {
        super(registry);
        this.metaClass = GroovySystem.getMetaClassRegistry().getMetaClass((Class)this.getClass());
        this.namespaces = new HashMap<String, String>();
        this.deferredProperties = new HashMap<String, DeferredProperty>();
        (this.xmlBeanDefinitionReader = new XmlBeanDefinitionReader(registry)).setValidating(false);
    }
    
    public GroovyBeanDefinitionReader(final XmlBeanDefinitionReader xmlBeanDefinitionReader) {
        super(xmlBeanDefinitionReader.getRegistry());
        this.metaClass = GroovySystem.getMetaClassRegistry().getMetaClass((Class)this.getClass());
        this.namespaces = new HashMap<String, String>();
        this.deferredProperties = new HashMap<String, DeferredProperty>();
        this.xmlBeanDefinitionReader = xmlBeanDefinitionReader;
    }
    
    public void setMetaClass(final MetaClass metaClass) {
        this.metaClass = metaClass;
    }
    
    public MetaClass getMetaClass() {
        return this.metaClass;
    }
    
    public void setBinding(final Binding binding) {
        this.binding = binding;
    }
    
    public Binding getBinding() {
        return this.binding;
    }
    
    public int loadBeanDefinitions(final Resource resource) throws BeanDefinitionStoreException {
        return this.loadBeanDefinitions(new EncodedResource(resource));
    }
    
    public int loadBeanDefinitions(final EncodedResource encodedResource) throws BeanDefinitionStoreException {
        final Closure beans = new Closure(this) {
            public Object call(final Object[] args) {
                GroovyBeanDefinitionReader.this.invokeBeanDefiningClosure((Closure)args[0]);
                return null;
            }
        };
        final Binding binding = new Binding() {
            public void setVariable(final String name, final Object value) {
                if (GroovyBeanDefinitionReader.this.currentBeanDefinition != null) {
                    GroovyBeanDefinitionReader.this.applyPropertyToBeanDefinition(name, value);
                }
                else {
                    super.setVariable(name, value);
                }
            }
        };
        binding.setVariable("beans", (Object)beans);
        final int countBefore = this.getRegistry().getBeanDefinitionCount();
        try {
            final GroovyShell shell = new GroovyShell(this.getResourceLoader().getClassLoader(), binding);
            shell.evaluate(encodedResource.getReader(), encodedResource.getResource().getFilename());
        }
        catch (Throwable ex) {
            throw new BeanDefinitionParsingException(new Problem("Error evaluating Groovy script: " + ex.getMessage(), new Location(encodedResource.getResource()), null, ex));
        }
        return this.getRegistry().getBeanDefinitionCount() - countBefore;
    }
    
    public GroovyBeanDefinitionReader beans(final Closure closure) {
        return this.invokeBeanDefiningClosure(closure);
    }
    
    public GenericBeanDefinition bean(final Class<?> type) {
        final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(type);
        return beanDefinition;
    }
    
    public AbstractBeanDefinition bean(final Class<?> type, final Object... args) {
        final GroovyBeanDefinitionWrapper current = this.currentBeanDefinition;
        try {
            Closure callable = null;
            Collection constructorArgs = null;
            if (args != null && args.length > 0) {
                int index = args.length;
                final Object lastArg = args[index - 1];
                if (lastArg instanceof Closure) {
                    callable = (Closure)lastArg;
                    --index;
                }
                if (index > -1) {
                    constructorArgs = this.resolveConstructorArguments(args, 0, index);
                }
            }
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(null, type, constructorArgs);
            if (callable != null) {
                callable.call((Object)this.currentBeanDefinition);
            }
            return this.currentBeanDefinition.getBeanDefinition();
        }
        finally {
            this.currentBeanDefinition = current;
        }
    }
    
    public void xmlns(final Map<String, String> definition) {
        if (!definition.isEmpty()) {
            for (final Map.Entry<String, String> entry : definition.entrySet()) {
                final String namespace = entry.getKey();
                final String uri = entry.getValue();
                if (uri == null) {
                    throw new IllegalArgumentException("Namespace definition must supply a non-null URI");
                }
                final NamespaceHandler namespaceHandler = this.xmlBeanDefinitionReader.getNamespaceHandlerResolver().resolve(uri);
                if (namespaceHandler == null) {
                    throw new BeanDefinitionParsingException(new Problem("No namespace handler found for URI: " + uri, new Location(new DescriptiveResource("Groovy"))));
                }
                this.namespaces.put(namespace, uri);
            }
        }
    }
    
    public void importBeans(final String resourcePattern) throws IOException {
        final Resource[] resources2;
        final Resource[] resources = resources2 = ResourcePatternUtils.getResourcePatternResolver(this.getResourceLoader()).getResources(resourcePattern);
        for (final Resource resource : resources2) {
            final String filename = resource.getFilename();
            if (filename.endsWith(".groovy")) {
                this.loadBeanDefinitions(resource);
            }
            else if (filename.endsWith(".xml")) {
                this.xmlBeanDefinitionReader.loadBeanDefinitions(resource);
            }
        }
    }
    
    public Object invokeMethod(final String name, final Object arg) {
        final Object[] args = (Object[])arg;
        if ("beans".equals(name) && args.length == 1 && args[0] instanceof Closure) {
            return this.beans((Closure)args[0]);
        }
        if ("ref".equals(name)) {
            if (args[0] == null) {
                throw new IllegalArgumentException("Argument to ref() is not a valid bean or was not found");
            }
            String refName;
            if (args[0] instanceof RuntimeBeanReference) {
                refName = ((RuntimeBeanReference)args[0]).getBeanName();
            }
            else {
                refName = args[0].toString();
            }
            boolean parentRef = false;
            if (args.length > 1 && args[1] instanceof Boolean) {
                parentRef = (boolean)args[1];
            }
            return new RuntimeBeanReference(refName, parentRef);
        }
        else {
            if (this.namespaces.containsKey(name) && args.length > 0 && args[0] instanceof Closure) {
                final GroovyDynamicElementReader reader = this.createDynamicElementReader(name);
                reader.invokeMethod("doCall", args);
            }
            else {
                if (args.length > 0 && args[0] instanceof Closure) {
                    return this.invokeBeanDefiningMethod(name, args);
                }
                if (args.length > 0 && (args[0] instanceof Class || args[0] instanceof RuntimeBeanReference || args[0] instanceof Map)) {
                    return this.invokeBeanDefiningMethod(name, args);
                }
                if (args.length > 1 && args[args.length - 1] instanceof Closure) {
                    return this.invokeBeanDefiningMethod(name, args);
                }
            }
            final MetaClass mc = DefaultGroovyMethods.getMetaClass((Object)this.getRegistry());
            if (!mc.respondsTo((Object)this.getRegistry(), name, args).isEmpty()) {
                return mc.invokeMethod((Object)this.getRegistry(), name, args);
            }
            return this;
        }
    }
    
    private boolean addDeferredProperty(final String property, final Object newValue) {
        if (newValue instanceof List) {
            this.deferredProperties.put(this.currentBeanDefinition.getBeanName() + '.' + property, new DeferredProperty(this.currentBeanDefinition, property, newValue));
            return true;
        }
        if (newValue instanceof Map) {
            this.deferredProperties.put(this.currentBeanDefinition.getBeanName() + '.' + property, new DeferredProperty(this.currentBeanDefinition, property, newValue));
            return true;
        }
        return false;
    }
    
    private void finalizeDeferredProperties() {
        for (final DeferredProperty dp : this.deferredProperties.values()) {
            if (dp.value instanceof List) {
                dp.value = this.manageListIfNecessary((List<?>)dp.value);
            }
            else if (dp.value instanceof Map) {
                dp.value = this.manageMapIfNecessary((Map<?, ?>)dp.value);
            }
            dp.apply();
        }
        this.deferredProperties.clear();
    }
    
    protected GroovyBeanDefinitionReader invokeBeanDefiningClosure(final Closure callable) {
        callable.setDelegate((Object)this);
        callable.call();
        this.finalizeDeferredProperties();
        return this;
    }
    
    private GroovyBeanDefinitionWrapper invokeBeanDefiningMethod(final String beanName, final Object[] args) {
        final boolean hasClosureArgument = args[args.length - 1] instanceof Closure;
        if (args[0] instanceof Class) {
            final Class<?> beanClass = (Class<?>)((args[0] instanceof Class) ? ((Class)args[0]) : args[0].getClass());
            if (args.length >= 1) {
                if (hasClosureArgument) {
                    if (args.length - 1 != 1) {
                        this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, beanClass, this.resolveConstructorArguments(args, 1, args.length - 1));
                    }
                    else {
                        this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, beanClass);
                    }
                }
                else {
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, beanClass, this.resolveConstructorArguments(args, 1, args.length));
                }
            }
        }
        else if (args[0] instanceof RuntimeBeanReference) {
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
            this.currentBeanDefinition.getBeanDefinition().setFactoryBeanName(((RuntimeBeanReference)args[0]).getBeanName());
        }
        else if (args[0] instanceof Map) {
            if (args.length > 1 && args[1] instanceof Class) {
                final List constructorArgs = this.resolveConstructorArguments(args, 2, hasClosureArgument ? (args.length - 1) : args.length);
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, (Class<?>)args[1], constructorArgs);
                final Map namedArgs = (Map)args[0];
                for (final Object o : namedArgs.keySet()) {
                    final String propName = (String)o;
                    this.setProperty(propName, namedArgs.get(propName));
                }
            }
            else {
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
                final Map.Entry factoryBeanEntry = (Map.Entry)((Map)args[0]).entrySet().iterator().next();
                final int constructorArgsTest = hasClosureArgument ? 2 : 1;
                if (args.length > constructorArgsTest) {
                    final int endOfConstructArgs = hasClosureArgument ? (args.length - 1) : args.length;
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, null, this.resolveConstructorArguments(args, 1, endOfConstructArgs));
                }
                else {
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
                }
                this.currentBeanDefinition.getBeanDefinition().setFactoryBeanName(factoryBeanEntry.getKey().toString());
                this.currentBeanDefinition.getBeanDefinition().setFactoryMethodName(factoryBeanEntry.getValue().toString());
            }
        }
        else if (args[0] instanceof Closure) {
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
            this.currentBeanDefinition.getBeanDefinition().setAbstract(true);
        }
        else {
            final List constructorArgs = this.resolveConstructorArguments(args, 0, hasClosureArgument ? (args.length - 1) : args.length);
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, null, constructorArgs);
        }
        if (hasClosureArgument) {
            final Closure callable = (Closure)args[args.length - 1];
            callable.setDelegate((Object)this);
            callable.setResolveStrategy(1);
            callable.call(new Object[] { this.currentBeanDefinition });
        }
        final GroovyBeanDefinitionWrapper beanDefinition = this.currentBeanDefinition;
        this.currentBeanDefinition = null;
        beanDefinition.getBeanDefinition().setAttribute(GroovyBeanDefinitionWrapper.class.getName(), beanDefinition);
        this.getRegistry().registerBeanDefinition(beanName, beanDefinition.getBeanDefinition());
        return beanDefinition;
    }
    
    protected List<Object> resolveConstructorArguments(final Object[] args, final int start, final int end) {
        final Object[] constructorArgs = Arrays.copyOfRange(args, start, end);
        for (int i = 0; i < constructorArgs.length; ++i) {
            if (constructorArgs[i] instanceof GString) {
                constructorArgs[i] = constructorArgs[i].toString();
            }
            else if (constructorArgs[i] instanceof List) {
                constructorArgs[i] = this.manageListIfNecessary((List<?>)constructorArgs[i]);
            }
            else if (constructorArgs[i] instanceof Map) {
                constructorArgs[i] = this.manageMapIfNecessary((Map<?, ?>)constructorArgs[i]);
            }
        }
        return Arrays.asList(constructorArgs);
    }
    
    private Object manageMapIfNecessary(final Map<?, ?> map) {
        boolean containsRuntimeRefs = false;
        for (final Object element : map.values()) {
            if (element instanceof RuntimeBeanReference) {
                containsRuntimeRefs = true;
                break;
            }
        }
        if (containsRuntimeRefs) {
            final Map<Object, Object> managedMap = new ManagedMap<Object, Object>();
            managedMap.putAll(map);
            return managedMap;
        }
        return map;
    }
    
    private Object manageListIfNecessary(final List<?> list) {
        boolean containsRuntimeRefs = false;
        for (final Object element : list) {
            if (element instanceof RuntimeBeanReference) {
                containsRuntimeRefs = true;
                break;
            }
        }
        if (containsRuntimeRefs) {
            final List<Object> managedList = new ManagedList<Object>();
            managedList.addAll(list);
            return managedList;
        }
        return list;
    }
    
    public void setProperty(final String name, final Object value) {
        if (this.currentBeanDefinition != null) {
            this.applyPropertyToBeanDefinition(name, value);
        }
    }
    
    protected void applyPropertyToBeanDefinition(final String name, Object value) {
        if (value instanceof GString) {
            value = value.toString();
        }
        if (this.addDeferredProperty(name, value)) {
            return;
        }
        if (value instanceof Closure) {
            final GroovyBeanDefinitionWrapper current = this.currentBeanDefinition;
            try {
                final Closure callable = (Closure)value;
                final Class<?> parameterType = (Class<?>)callable.getParameterTypes()[0];
                if (parameterType.equals(Object.class)) {
                    callable.call((Object)(this.currentBeanDefinition = new GroovyBeanDefinitionWrapper("")));
                }
                else {
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(null, parameterType);
                    callable.call((Object)null);
                }
                value = this.currentBeanDefinition.getBeanDefinition();
            }
            finally {
                this.currentBeanDefinition = current;
            }
        }
        this.currentBeanDefinition.addProperty(name, value);
    }
    
    public Object getProperty(final String name) {
        final Binding binding = this.getBinding();
        if (binding != null && binding.hasVariable(name)) {
            return binding.getVariable(name);
        }
        if (this.namespaces.containsKey(name)) {
            return this.createDynamicElementReader(name);
        }
        if (this.getRegistry().containsBeanDefinition(name)) {
            final GroovyBeanDefinitionWrapper beanDefinition = (GroovyBeanDefinitionWrapper)this.getRegistry().getBeanDefinition(name).getAttribute(GroovyBeanDefinitionWrapper.class.getName());
            if (beanDefinition != null) {
                return new GroovyRuntimeBeanReference(name, beanDefinition, false);
            }
            return new RuntimeBeanReference(name, false);
        }
        else {
            if (this.currentBeanDefinition == null) {
                return this.getMetaClass().getProperty((Object)this, name);
            }
            final MutablePropertyValues pvs = this.currentBeanDefinition.getBeanDefinition().getPropertyValues();
            if (pvs.contains(name)) {
                return pvs.get(name);
            }
            final DeferredProperty dp = this.deferredProperties.get(this.currentBeanDefinition.getBeanName() + name);
            if (dp != null) {
                return dp.value;
            }
            return this.getMetaClass().getProperty((Object)this, name);
        }
    }
    
    private GroovyDynamicElementReader createDynamicElementReader(final String namespace) {
        final XmlReaderContext readerContext = this.xmlBeanDefinitionReader.createReaderContext(new DescriptiveResource("Groovy"));
        final BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext, this.getEnvironment());
        final boolean decorating = this.currentBeanDefinition != null;
        if (!decorating) {
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(namespace);
        }
        return new GroovyDynamicElementReader(namespace, this.namespaces, delegate, this.currentBeanDefinition, decorating) {
            @Override
            protected void afterInvocation() {
                if (!this.decorating) {
                    GroovyBeanDefinitionReader.this.currentBeanDefinition = null;
                }
            }
        };
    }
    
    private static class DeferredProperty
    {
        private final GroovyBeanDefinitionWrapper beanDefinition;
        private final String name;
        public Object value;
        
        public DeferredProperty(final GroovyBeanDefinitionWrapper beanDefinition, final String name, final Object value) {
            this.beanDefinition = beanDefinition;
            this.name = name;
            this.value = value;
        }
        
        public void apply() {
            this.beanDefinition.addProperty(this.name, this.value);
        }
    }
    
    private class GroovyRuntimeBeanReference extends RuntimeBeanReference implements GroovyObject
    {
        private final GroovyBeanDefinitionWrapper beanDefinition;
        private MetaClass metaClass;
        
        public GroovyRuntimeBeanReference(final String beanName, final GroovyBeanDefinitionWrapper beanDefinition, final boolean toParent) {
            super(beanName, toParent);
            this.beanDefinition = beanDefinition;
            this.metaClass = InvokerHelper.getMetaClass((Object)this);
        }
        
        public MetaClass getMetaClass() {
            return this.metaClass;
        }
        
        public Object getProperty(final String property) {
            if (property.equals("beanName")) {
                return this.getBeanName();
            }
            if (property.equals("source")) {
                return this.getSource();
            }
            if (this.beanDefinition != null) {
                return new GroovyPropertyValue(property, this.beanDefinition.getBeanDefinition().getPropertyValues().get(property));
            }
            return this.metaClass.getProperty((Object)this, property);
        }
        
        public Object invokeMethod(final String name, final Object args) {
            return this.metaClass.invokeMethod((Object)this, name, args);
        }
        
        public void setMetaClass(final MetaClass metaClass) {
            this.metaClass = metaClass;
        }
        
        public void setProperty(final String property, final Object newValue) {
            if (!GroovyBeanDefinitionReader.this.addDeferredProperty(property, newValue)) {
                this.beanDefinition.getBeanDefinition().getPropertyValues().add(property, newValue);
            }
        }
        
        private class GroovyPropertyValue extends GroovyObjectSupport
        {
            private final String propertyName;
            private final Object propertyValue;
            
            public GroovyPropertyValue(final String propertyName, final Object propertyValue) {
                this.propertyName = propertyName;
                this.propertyValue = propertyValue;
            }
            
            public void leftShift(final Object value) {
                InvokerHelper.invokeMethod(this.propertyValue, "leftShift", value);
                this.updateDeferredProperties(value);
            }
            
            public boolean add(final Object value) {
                final boolean retVal = (boolean)InvokerHelper.invokeMethod(this.propertyValue, "add", value);
                this.updateDeferredProperties(value);
                return retVal;
            }
            
            public boolean addAll(final Collection values) {
                final boolean retVal = (boolean)InvokerHelper.invokeMethod(this.propertyValue, "addAll", (Object)values);
                for (final Object value : values) {
                    this.updateDeferredProperties(value);
                }
                return retVal;
            }
            
            public Object invokeMethod(final String name, final Object args) {
                return InvokerHelper.invokeMethod(this.propertyValue, name, args);
            }
            
            public Object getProperty(final String name) {
                return InvokerHelper.getProperty(this.propertyValue, name);
            }
            
            public void setProperty(final String name, final Object value) {
                InvokerHelper.setProperty(this.propertyValue, name, value);
            }
            
            private void updateDeferredProperties(final Object value) {
                if (value instanceof RuntimeBeanReference) {
                    GroovyBeanDefinitionReader.this.deferredProperties.put(GroovyRuntimeBeanReference.this.beanDefinition.getBeanName(), new DeferredProperty(GroovyRuntimeBeanReference.this.beanDefinition, this.propertyName, this.propertyValue));
                }
            }
        }
    }
}
