// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.config;

import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;

class LoadTimeWeaverBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
{
    private static final String WEAVER_CLASS_ATTRIBUTE = "weaver-class";
    private static final String ASPECTJ_WEAVING_ATTRIBUTE = "aspectj-weaving";
    private static final String DEFAULT_LOAD_TIME_WEAVER_CLASS_NAME = "org.springframework.context.weaving.DefaultContextLoadTimeWeaver";
    private static final String ASPECTJ_WEAVING_ENABLER_CLASS_NAME = "org.springframework.context.weaving.AspectJWeavingEnabler";
    
    @Override
    protected String getBeanClassName(final Element element) {
        if (element.hasAttribute("weaver-class")) {
            return element.getAttribute("weaver-class");
        }
        return "org.springframework.context.weaving.DefaultContextLoadTimeWeaver";
    }
    
    @Override
    protected String resolveId(final Element element, final AbstractBeanDefinition definition, final ParserContext parserContext) {
        return "loadTimeWeaver";
    }
    
    @Override
    protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
        builder.setRole(2);
        if (this.isAspectJWeavingEnabled(element.getAttribute("aspectj-weaving"), parserContext)) {
            final RootBeanDefinition weavingEnablerDef = new RootBeanDefinition();
            weavingEnablerDef.setBeanClassName("org.springframework.context.weaving.AspectJWeavingEnabler");
            parserContext.getReaderContext().registerWithGeneratedName(weavingEnablerDef);
            if (this.isBeanConfigurerAspectEnabled(parserContext.getReaderContext().getBeanClassLoader())) {
                new SpringConfiguredBeanDefinitionParser().parse(element, parserContext);
            }
        }
    }
    
    protected boolean isAspectJWeavingEnabled(final String value, final ParserContext parserContext) {
        if ("on".equals(value)) {
            return true;
        }
        if ("off".equals(value)) {
            return false;
        }
        final ClassLoader cl = parserContext.getReaderContext().getResourceLoader().getClassLoader();
        return cl.getResource("META-INF/aop.xml") != null;
    }
    
    protected boolean isBeanConfigurerAspectEnabled(final ClassLoader beanClassLoader) {
        return ClassUtils.isPresent("org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect", beanClassLoader);
    }
}
