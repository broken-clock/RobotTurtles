// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.config;

import java.util.List;
import org.springframework.util.xml.DomUtils;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.StringUtils;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;

class ScriptBeanDefinitionParser extends AbstractBeanDefinitionParser
{
    private static final String SCRIPT_SOURCE_ATTRIBUTE = "script-source";
    private static final String INLINE_SCRIPT_ELEMENT = "inline-script";
    private static final String SCOPE_ATTRIBUTE = "scope";
    private static final String AUTOWIRE_ATTRIBUTE = "autowire";
    private static final String DEPENDENCY_CHECK_ATTRIBUTE = "dependency-check";
    private static final String DEPENDS_ON_ATTRIBUTE = "depends-on";
    private static final String INIT_METHOD_ATTRIBUTE = "init-method";
    private static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
    private static final String SCRIPT_INTERFACES_ATTRIBUTE = "script-interfaces";
    private static final String REFRESH_CHECK_DELAY_ATTRIBUTE = "refresh-check-delay";
    private static final String PROXY_TARGET_CLASS_ATTRIBUTE = "proxy-target-class";
    private static final String CUSTOMIZER_REF_ATTRIBUTE = "customizer-ref";
    private final String scriptFactoryClassName;
    
    public ScriptBeanDefinitionParser(final String scriptFactoryClassName) {
        this.scriptFactoryClassName = scriptFactoryClassName;
    }
    
    @Override
    protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
        final String value = this.resolveScriptSource(element, parserContext.getReaderContext());
        if (value == null) {
            return null;
        }
        LangNamespaceUtils.registerScriptFactoryPostProcessorIfNecessary(parserContext.getRegistry());
        final GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClassName(this.scriptFactoryClassName);
        bd.setSource(parserContext.extractSource(element));
        bd.setAttribute(ScriptFactoryPostProcessor.LANGUAGE_ATTRIBUTE, element.getLocalName());
        final String scope = element.getAttribute("scope");
        if (StringUtils.hasLength(scope)) {
            bd.setScope(scope);
        }
        final String autowire = element.getAttribute("autowire");
        int autowireMode = parserContext.getDelegate().getAutowireMode(autowire);
        if (autowireMode == 4) {
            autowireMode = 2;
        }
        else if (autowireMode == 3) {
            autowireMode = 0;
        }
        bd.setAutowireMode(autowireMode);
        final String dependencyCheck = element.getAttribute("dependency-check");
        bd.setDependencyCheck(parserContext.getDelegate().getDependencyCheck(dependencyCheck));
        final String dependsOn = element.getAttribute("depends-on");
        if (StringUtils.hasLength(dependsOn)) {
            bd.setDependsOn(StringUtils.tokenizeToStringArray(dependsOn, ",; "));
        }
        final BeanDefinitionDefaults beanDefinitionDefaults = parserContext.getDelegate().getBeanDefinitionDefaults();
        final String initMethod = element.getAttribute("init-method");
        if (StringUtils.hasLength(initMethod)) {
            bd.setInitMethodName(initMethod);
        }
        else if (beanDefinitionDefaults.getInitMethodName() != null) {
            bd.setInitMethodName(beanDefinitionDefaults.getInitMethodName());
        }
        final String destroyMethod = element.getAttribute("destroy-method");
        if (StringUtils.hasLength(destroyMethod)) {
            bd.setDestroyMethodName(destroyMethod);
        }
        else if (beanDefinitionDefaults.getDestroyMethodName() != null) {
            bd.setDestroyMethodName(beanDefinitionDefaults.getDestroyMethodName());
        }
        final String refreshCheckDelay = element.getAttribute("refresh-check-delay");
        if (StringUtils.hasText(refreshCheckDelay)) {
            bd.setAttribute(ScriptFactoryPostProcessor.REFRESH_CHECK_DELAY_ATTRIBUTE, new Long(refreshCheckDelay));
        }
        final String proxyTargetClass = element.getAttribute("proxy-target-class");
        if (StringUtils.hasText(proxyTargetClass)) {
            final Boolean flag = new Boolean(proxyTargetClass);
            bd.setAttribute(ScriptFactoryPostProcessor.PROXY_TARGET_CLASS_ATTRIBUTE, flag);
        }
        final ConstructorArgumentValues cav = bd.getConstructorArgumentValues();
        int constructorArgNum = 0;
        cav.addIndexedArgumentValue(constructorArgNum++, value);
        if (element.hasAttribute("script-interfaces")) {
            cav.addIndexedArgumentValue(constructorArgNum++, element.getAttribute("script-interfaces"));
        }
        if (element.hasAttribute("customizer-ref")) {
            final String customizerBeanName = element.getAttribute("customizer-ref");
            if (!StringUtils.hasText(customizerBeanName)) {
                parserContext.getReaderContext().error("Attribute 'customizer-ref' has empty value", element);
            }
            else {
                cav.addIndexedArgumentValue(constructorArgNum++, new RuntimeBeanReference(customizerBeanName));
            }
        }
        parserContext.getDelegate().parsePropertyElements(element, bd);
        return bd;
    }
    
    private String resolveScriptSource(final Element element, final XmlReaderContext readerContext) {
        final boolean hasScriptSource = element.hasAttribute("script-source");
        final List<Element> elements = DomUtils.getChildElementsByTagName(element, "inline-script");
        if (hasScriptSource && !elements.isEmpty()) {
            readerContext.error("Only one of 'script-source' and 'inline-script' should be specified.", element);
            return null;
        }
        if (hasScriptSource) {
            return element.getAttribute("script-source");
        }
        if (!elements.isEmpty()) {
            final Element inlineElement = elements.get(0);
            return "inline:" + DomUtils.getTextValue(inlineElement);
        }
        readerContext.error("Must specify either 'script-source' or 'inline-script'.", element);
        return null;
    }
    
    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }
}
