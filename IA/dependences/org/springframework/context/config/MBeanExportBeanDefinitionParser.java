// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.config;

import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;

class MBeanExportBeanDefinitionParser extends AbstractBeanDefinitionParser
{
    private static final String MBEAN_EXPORTER_BEAN_NAME = "mbeanExporter";
    private static final String DEFAULT_DOMAIN_ATTRIBUTE = "default-domain";
    private static final String SERVER_ATTRIBUTE = "server";
    private static final String REGISTRATION_ATTRIBUTE = "registration";
    private static final String REGISTRATION_IGNORE_EXISTING = "ignoreExisting";
    private static final String REGISTRATION_REPLACE_EXISTING = "replaceExisting";
    
    @Override
    protected String resolveId(final Element element, final AbstractBeanDefinition definition, final ParserContext parserContext) {
        return "mbeanExporter";
    }
    
    @Override
    protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(AnnotationMBeanExporter.class);
        builder.setRole(2);
        builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
        final String defaultDomain = element.getAttribute("default-domain");
        if (StringUtils.hasText(defaultDomain)) {
            builder.addPropertyValue("defaultDomain", defaultDomain);
        }
        final String serverBeanName = element.getAttribute("server");
        if (StringUtils.hasText(serverBeanName)) {
            builder.addPropertyReference("server", serverBeanName);
        }
        else {
            final AbstractBeanDefinition specialServer = MBeanServerBeanDefinitionParser.findServerForSpecialEnvironment();
            if (specialServer != null) {
                builder.addPropertyValue("server", specialServer);
            }
        }
        final String registration = element.getAttribute("registration");
        RegistrationPolicy registrationPolicy = RegistrationPolicy.FAIL_ON_EXISTING;
        if ("ignoreExisting".equals(registration)) {
            registrationPolicy = RegistrationPolicy.IGNORE_EXISTING;
        }
        else if ("replaceExisting".equals(registration)) {
            registrationPolicy = RegistrationPolicy.REPLACE_EXISTING;
        }
        builder.addPropertyValue("registrationPolicy", registrationPolicy);
        return builder.getBeanDefinition();
    }
}
