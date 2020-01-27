// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.config;

import org.springframework.util.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;

public class SchedulerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
{
    @Override
    protected String getBeanClassName(final Element element) {
        return "org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler";
    }
    
    @Override
    protected void doParse(final Element element, final BeanDefinitionBuilder builder) {
        final String poolSize = element.getAttribute("pool-size");
        if (StringUtils.hasText(poolSize)) {
            builder.addPropertyValue("poolSize", poolSize);
        }
    }
}
