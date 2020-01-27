// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.config;

import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;

public class ExecutorBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
{
    @Override
    protected String getBeanClassName(final Element element) {
        return "org.springframework.scheduling.config.TaskExecutorFactoryBean";
    }
    
    @Override
    protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
        final String keepAliveSeconds = element.getAttribute("keep-alive");
        if (StringUtils.hasText(keepAliveSeconds)) {
            builder.addPropertyValue("keepAliveSeconds", keepAliveSeconds);
        }
        final String queueCapacity = element.getAttribute("queue-capacity");
        if (StringUtils.hasText(queueCapacity)) {
            builder.addPropertyValue("queueCapacity", queueCapacity);
        }
        this.configureRejectionPolicy(element, builder);
        final String poolSize = element.getAttribute("pool-size");
        if (StringUtils.hasText(poolSize)) {
            builder.addPropertyValue("poolSize", poolSize);
        }
    }
    
    private void configureRejectionPolicy(final Element element, final BeanDefinitionBuilder builder) {
        final String rejectionPolicy = element.getAttribute("rejection-policy");
        if (!StringUtils.hasText(rejectionPolicy)) {
            return;
        }
        String prefix = "java.util.concurrent.ThreadPoolExecutor.";
        if (builder.getRawBeanDefinition().getBeanClassName().contains("backport")) {
            prefix = "edu.emory.mathcs.backport." + prefix;
        }
        String policyClassName;
        if (rejectionPolicy.equals("ABORT")) {
            policyClassName = prefix + "AbortPolicy";
        }
        else if (rejectionPolicy.equals("CALLER_RUNS")) {
            policyClassName = prefix + "CallerRunsPolicy";
        }
        else if (rejectionPolicy.equals("DISCARD")) {
            policyClassName = prefix + "DiscardPolicy";
        }
        else if (rejectionPolicy.equals("DISCARD_OLDEST")) {
            policyClassName = prefix + "DiscardOldestPolicy";
        }
        else {
            policyClassName = rejectionPolicy;
        }
        builder.addPropertyValue("rejectedExecutionHandler", new RootBeanDefinition(policyClassName));
    }
}
