// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.config;

import org.springframework.util.ClassUtils;
import org.springframework.jmx.support.WebSphereMBeanServerFactoryBean;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;

class MBeanServerBeanDefinitionParser extends AbstractBeanDefinitionParser
{
    private static final String MBEAN_SERVER_BEAN_NAME = "mbeanServer";
    private static final String AGENT_ID_ATTRIBUTE = "agent-id";
    private static final boolean weblogicPresent;
    private static final boolean webspherePresent;
    
    @Override
    protected String resolveId(final Element element, final AbstractBeanDefinition definition, final ParserContext parserContext) {
        final String id = element.getAttribute("id");
        return StringUtils.hasText(id) ? id : "mbeanServer";
    }
    
    @Override
    protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
        final String agentId = element.getAttribute("agent-id");
        if (StringUtils.hasText(agentId)) {
            final RootBeanDefinition bd = new RootBeanDefinition(MBeanServerFactoryBean.class);
            bd.getPropertyValues().add("agentId", agentId);
            return bd;
        }
        final AbstractBeanDefinition specialServer = findServerForSpecialEnvironment();
        if (specialServer != null) {
            return specialServer;
        }
        final RootBeanDefinition bd2 = new RootBeanDefinition(MBeanServerFactoryBean.class);
        bd2.getPropertyValues().add("locateExistingServerIfPossible", Boolean.TRUE);
        bd2.setRole(2);
        bd2.setSource(parserContext.extractSource(element));
        return bd2;
    }
    
    static AbstractBeanDefinition findServerForSpecialEnvironment() {
        if (MBeanServerBeanDefinitionParser.weblogicPresent) {
            final RootBeanDefinition bd = new RootBeanDefinition(JndiObjectFactoryBean.class);
            bd.getPropertyValues().add("jndiName", "java:comp/env/jmx/runtime");
            return bd;
        }
        if (MBeanServerBeanDefinitionParser.webspherePresent) {
            return new RootBeanDefinition(WebSphereMBeanServerFactoryBean.class);
        }
        return null;
    }
    
    static {
        weblogicPresent = ClassUtils.isPresent("weblogic.management.Helper", MBeanServerBeanDefinitionParser.class.getClassLoader());
        webspherePresent = ClassUtils.isPresent("com.ibm.websphere.management.AdminServiceFactory", MBeanServerBeanDefinitionParser.class.getClassLoader());
    }
}
