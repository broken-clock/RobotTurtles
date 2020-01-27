// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.config;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.BeanDefinitionParser;

public class ScriptingDefaultsParser implements BeanDefinitionParser
{
    private static final String REFRESH_CHECK_DELAY_ATTRIBUTE = "refresh-check-delay";
    private static final String PROXY_TARGET_CLASS_ATTRIBUTE = "proxy-target-class";
    
    @Override
    public BeanDefinition parse(final Element element, final ParserContext parserContext) {
        final BeanDefinition bd = LangNamespaceUtils.registerScriptFactoryPostProcessorIfNecessary(parserContext.getRegistry());
        final String refreshCheckDelay = element.getAttribute("refresh-check-delay");
        if (StringUtils.hasText(refreshCheckDelay)) {
            bd.getPropertyValues().add("defaultRefreshCheckDelay", new Long(refreshCheckDelay));
        }
        final String proxyTargetClass = element.getAttribute("proxy-target-class");
        if (StringUtils.hasText(proxyTargetClass)) {
            bd.getPropertyValues().add("defaultProxyTargetClass", new TypedStringValue(proxyTargetClass, Boolean.class));
        }
        return null;
    }
}
