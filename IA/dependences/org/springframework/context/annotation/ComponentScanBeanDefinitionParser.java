// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.beans.FatalBeanException;
import org.springframework.beans.BeanUtils;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import java.util.regex.Pattern;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import java.lang.annotation.Annotation;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.springframework.beans.factory.support.BeanNameGenerator;
import java.util.Iterator;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import java.util.Set;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.BeanDefinitionParser;

public class ComponentScanBeanDefinitionParser implements BeanDefinitionParser
{
    private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";
    private static final String RESOURCE_PATTERN_ATTRIBUTE = "resource-pattern";
    private static final String USE_DEFAULT_FILTERS_ATTRIBUTE = "use-default-filters";
    private static final String ANNOTATION_CONFIG_ATTRIBUTE = "annotation-config";
    private static final String NAME_GENERATOR_ATTRIBUTE = "name-generator";
    private static final String SCOPE_RESOLVER_ATTRIBUTE = "scope-resolver";
    private static final String SCOPED_PROXY_ATTRIBUTE = "scoped-proxy";
    private static final String EXCLUDE_FILTER_ELEMENT = "exclude-filter";
    private static final String INCLUDE_FILTER_ELEMENT = "include-filter";
    private static final String FILTER_TYPE_ATTRIBUTE = "type";
    private static final String FILTER_EXPRESSION_ATTRIBUTE = "expression";
    
    @Override
    public BeanDefinition parse(final Element element, final ParserContext parserContext) {
        final String[] basePackages = StringUtils.tokenizeToStringArray(element.getAttribute("base-package"), ",; \t\n");
        final ClassPathBeanDefinitionScanner scanner = this.configureScanner(parserContext, element);
        final Set<BeanDefinitionHolder> beanDefinitions = scanner.doScan(basePackages);
        this.registerComponents(parserContext.getReaderContext(), beanDefinitions, element);
        return null;
    }
    
    protected ClassPathBeanDefinitionScanner configureScanner(final ParserContext parserContext, final Element element) {
        final XmlReaderContext readerContext = parserContext.getReaderContext();
        boolean useDefaultFilters = true;
        if (element.hasAttribute("use-default-filters")) {
            useDefaultFilters = Boolean.valueOf(element.getAttribute("use-default-filters"));
        }
        final ClassPathBeanDefinitionScanner scanner = this.createScanner(readerContext, useDefaultFilters);
        scanner.setResourceLoader(readerContext.getResourceLoader());
        scanner.setEnvironment(parserContext.getDelegate().getEnvironment());
        scanner.setBeanDefinitionDefaults(parserContext.getDelegate().getBeanDefinitionDefaults());
        scanner.setAutowireCandidatePatterns(parserContext.getDelegate().getAutowireCandidatePatterns());
        if (element.hasAttribute("resource-pattern")) {
            scanner.setResourcePattern(element.getAttribute("resource-pattern"));
        }
        try {
            this.parseBeanNameGenerator(element, scanner);
        }
        catch (Exception ex) {
            readerContext.error(ex.getMessage(), readerContext.extractSource(element), ex.getCause());
        }
        try {
            this.parseScope(element, scanner);
        }
        catch (Exception ex) {
            readerContext.error(ex.getMessage(), readerContext.extractSource(element), ex.getCause());
        }
        this.parseTypeFilters(element, scanner, readerContext, parserContext);
        return scanner;
    }
    
    protected ClassPathBeanDefinitionScanner createScanner(final XmlReaderContext readerContext, final boolean useDefaultFilters) {
        return new ClassPathBeanDefinitionScanner(readerContext.getRegistry(), useDefaultFilters);
    }
    
    protected void registerComponents(final XmlReaderContext readerContext, final Set<BeanDefinitionHolder> beanDefinitions, final Element element) {
        final Object source = readerContext.extractSource(element);
        final CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), source);
        for (final BeanDefinitionHolder beanDefHolder : beanDefinitions) {
            compositeDef.addNestedComponent(new BeanComponentDefinition(beanDefHolder));
        }
        boolean annotationConfig = true;
        if (element.hasAttribute("annotation-config")) {
            annotationConfig = Boolean.valueOf(element.getAttribute("annotation-config"));
        }
        if (annotationConfig) {
            final Set<BeanDefinitionHolder> processorDefinitions = AnnotationConfigUtils.registerAnnotationConfigProcessors(readerContext.getRegistry(), source);
            for (final BeanDefinitionHolder processorDefinition : processorDefinitions) {
                compositeDef.addNestedComponent(new BeanComponentDefinition(processorDefinition));
            }
        }
        readerContext.fireComponentRegistered(compositeDef);
    }
    
    protected void parseBeanNameGenerator(final Element element, final ClassPathBeanDefinitionScanner scanner) {
        if (element.hasAttribute("name-generator")) {
            final BeanNameGenerator beanNameGenerator = (BeanNameGenerator)this.instantiateUserDefinedStrategy(element.getAttribute("name-generator"), BeanNameGenerator.class, scanner.getResourceLoader().getClassLoader());
            scanner.setBeanNameGenerator(beanNameGenerator);
        }
    }
    
    protected void parseScope(final Element element, final ClassPathBeanDefinitionScanner scanner) {
        if (element.hasAttribute("scope-resolver")) {
            if (element.hasAttribute("scoped-proxy")) {
                throw new IllegalArgumentException("Cannot define both 'scope-resolver' and 'scoped-proxy' on <component-scan> tag");
            }
            final ScopeMetadataResolver scopeMetadataResolver = (ScopeMetadataResolver)this.instantiateUserDefinedStrategy(element.getAttribute("scope-resolver"), ScopeMetadataResolver.class, scanner.getResourceLoader().getClassLoader());
            scanner.setScopeMetadataResolver(scopeMetadataResolver);
        }
        if (element.hasAttribute("scoped-proxy")) {
            final String mode = element.getAttribute("scoped-proxy");
            if ("targetClass".equals(mode)) {
                scanner.setScopedProxyMode(ScopedProxyMode.TARGET_CLASS);
            }
            else if ("interfaces".equals(mode)) {
                scanner.setScopedProxyMode(ScopedProxyMode.INTERFACES);
            }
            else {
                if (!"no".equals(mode)) {
                    throw new IllegalArgumentException("scoped-proxy only supports 'no', 'interfaces' and 'targetClass'");
                }
                scanner.setScopedProxyMode(ScopedProxyMode.NO);
            }
        }
    }
    
    protected void parseTypeFilters(final Element element, final ClassPathBeanDefinitionScanner scanner, final XmlReaderContext readerContext, final ParserContext parserContext) {
        final ClassLoader classLoader = scanner.getResourceLoader().getClassLoader();
        final NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node node = nodeList.item(i);
            if (node.getNodeType() == 1) {
                final String localName = parserContext.getDelegate().getLocalName(node);
                try {
                    if ("include-filter".equals(localName)) {
                        final TypeFilter typeFilter = this.createTypeFilter((Element)node, classLoader);
                        scanner.addIncludeFilter(typeFilter);
                    }
                    else if ("exclude-filter".equals(localName)) {
                        final TypeFilter typeFilter = this.createTypeFilter((Element)node, classLoader);
                        scanner.addExcludeFilter(typeFilter);
                    }
                }
                catch (Exception ex) {
                    readerContext.error(ex.getMessage(), readerContext.extractSource(element), ex.getCause());
                }
            }
        }
    }
    
    protected TypeFilter createTypeFilter(final Element element, final ClassLoader classLoader) {
        final String filterType = element.getAttribute("type");
        final String expression = element.getAttribute("expression");
        try {
            if ("annotation".equals(filterType)) {
                return new AnnotationTypeFilter((Class<? extends Annotation>)classLoader.loadClass(expression));
            }
            if ("assignable".equals(filterType)) {
                return new AssignableTypeFilter(classLoader.loadClass(expression));
            }
            if ("aspectj".equals(filterType)) {
                return new AspectJTypeFilter(expression, classLoader);
            }
            if ("regex".equals(filterType)) {
                return new RegexPatternTypeFilter(Pattern.compile(expression));
            }
            if (!"custom".equals(filterType)) {
                throw new IllegalArgumentException("Unsupported filter type: " + filterType);
            }
            final Class<?> filterClass = classLoader.loadClass(expression);
            if (!TypeFilter.class.isAssignableFrom(filterClass)) {
                throw new IllegalArgumentException("Class is not assignable to [" + TypeFilter.class.getName() + "]: " + expression);
            }
            return BeanUtils.instantiateClass(filterClass);
        }
        catch (ClassNotFoundException ex) {
            throw new FatalBeanException("Type filter class not found: " + expression, ex);
        }
    }
    
    private Object instantiateUserDefinedStrategy(final String className, final Class<?> strategyType, final ClassLoader classLoader) {
        Object result = null;
        try {
            result = classLoader.loadClass(className).newInstance();
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Class [" + className + "] for strategy [" + strategyType.getName() + "] not found", ex);
        }
        catch (Exception ex2) {
            throw new IllegalArgumentException("Unable to instantiate class [" + className + "] for strategy [" + strategyType.getName() + "]. A zero-argument constructor is required", ex2);
        }
        if (!strategyType.isAssignableFrom(result.getClass())) {
            throw new IllegalArgumentException("Provided class name must be an implementation of " + strategyType);
        }
        return result;
    }
}
