// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.config;

import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;

public class ScheduledTasksBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
{
    private static final String ELEMENT_SCHEDULED = "scheduled";
    private static final long ZERO_INITIAL_DELAY = 0L;
    
    @Override
    protected boolean shouldGenerateId() {
        return true;
    }
    
    @Override
    protected String getBeanClassName(final Element element) {
        return "org.springframework.scheduling.config.ContextLifecycleScheduledTaskRegistrar";
    }
    
    @Override
    protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
        builder.setLazyInit(false);
        final ManagedList<RuntimeBeanReference> cronTaskList = new ManagedList<RuntimeBeanReference>();
        final ManagedList<RuntimeBeanReference> fixedDelayTaskList = new ManagedList<RuntimeBeanReference>();
        final ManagedList<RuntimeBeanReference> fixedRateTaskList = new ManagedList<RuntimeBeanReference>();
        final ManagedList<RuntimeBeanReference> triggerTaskList = new ManagedList<RuntimeBeanReference>();
        final NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node child = childNodes.item(i);
            if (this.isScheduledElement(child, parserContext)) {
                final Element taskElement = (Element)child;
                final String ref = taskElement.getAttribute("ref");
                final String method = taskElement.getAttribute("method");
                if (!StringUtils.hasText(ref) || !StringUtils.hasText(method)) {
                    parserContext.getReaderContext().error("Both 'ref' and 'method' are required", taskElement);
                }
                else {
                    final String cronAttribute = taskElement.getAttribute("cron");
                    final String fixedDelayAttribute = taskElement.getAttribute("fixed-delay");
                    final String fixedRateAttribute = taskElement.getAttribute("fixed-rate");
                    final String triggerAttribute = taskElement.getAttribute("trigger");
                    final String initialDelayAttribute = taskElement.getAttribute("initial-delay");
                    final boolean hasCronAttribute = StringUtils.hasText(cronAttribute);
                    final boolean hasFixedDelayAttribute = StringUtils.hasText(fixedDelayAttribute);
                    final boolean hasFixedRateAttribute = StringUtils.hasText(fixedRateAttribute);
                    final boolean hasTriggerAttribute = StringUtils.hasText(triggerAttribute);
                    final boolean hasInitialDelayAttribute = StringUtils.hasText(initialDelayAttribute);
                    if (!hasCronAttribute && !hasFixedDelayAttribute && !hasFixedRateAttribute && !hasTriggerAttribute) {
                        parserContext.getReaderContext().error("one of the 'cron', 'fixed-delay', 'fixed-rate', or 'trigger' attributes is required", taskElement);
                    }
                    else if (hasInitialDelayAttribute && (hasCronAttribute || hasTriggerAttribute)) {
                        parserContext.getReaderContext().error("the 'initial-delay' attribute may not be used with cron and trigger tasks", taskElement);
                    }
                    else {
                        final String runnableName = this.runnableReference(ref, method, taskElement, parserContext).getBeanName();
                        if (hasFixedDelayAttribute) {
                            fixedDelayTaskList.add(this.intervalTaskReference(runnableName, initialDelayAttribute, fixedDelayAttribute, taskElement, parserContext));
                        }
                        if (hasFixedRateAttribute) {
                            fixedRateTaskList.add(this.intervalTaskReference(runnableName, initialDelayAttribute, fixedRateAttribute, taskElement, parserContext));
                        }
                        if (hasCronAttribute) {
                            cronTaskList.add(this.cronTaskReference(runnableName, cronAttribute, taskElement, parserContext));
                        }
                        if (hasTriggerAttribute) {
                            final String triggerName = new RuntimeBeanReference(triggerAttribute).getBeanName();
                            triggerTaskList.add(this.triggerTaskReference(runnableName, triggerName, taskElement, parserContext));
                        }
                    }
                }
            }
        }
        final String schedulerRef = element.getAttribute("scheduler");
        if (StringUtils.hasText(schedulerRef)) {
            builder.addPropertyReference("taskScheduler", schedulerRef);
        }
        builder.addPropertyValue("cronTasksList", cronTaskList);
        builder.addPropertyValue("fixedDelayTasksList", fixedDelayTaskList);
        builder.addPropertyValue("fixedRateTasksList", fixedRateTaskList);
        builder.addPropertyValue("triggerTasksList", triggerTaskList);
    }
    
    private boolean isScheduledElement(final Node node, final ParserContext parserContext) {
        return node.getNodeType() == 1 && "scheduled".equals(parserContext.getDelegate().getLocalName(node));
    }
    
    private RuntimeBeanReference runnableReference(final String ref, final String method, final Element taskElement, final ParserContext parserContext) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition("org.springframework.scheduling.support.ScheduledMethodRunnable");
        builder.addConstructorArgReference(ref);
        builder.addConstructorArgValue(method);
        return this.beanReference(taskElement, parserContext, builder);
    }
    
    private RuntimeBeanReference intervalTaskReference(final String runnableBeanName, final String initialDelay, final String interval, final Element taskElement, final ParserContext parserContext) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition("org.springframework.scheduling.config.IntervalTask");
        builder.addConstructorArgReference(runnableBeanName);
        builder.addConstructorArgValue(interval);
        builder.addConstructorArgValue(StringUtils.hasLength(initialDelay) ? initialDelay : Long.valueOf(0L));
        return this.beanReference(taskElement, parserContext, builder);
    }
    
    private RuntimeBeanReference cronTaskReference(final String runnableBeanName, final String cronExpression, final Element taskElement, final ParserContext parserContext) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition("org.springframework.scheduling.config.CronTask");
        builder.addConstructorArgReference(runnableBeanName);
        builder.addConstructorArgValue(cronExpression);
        return this.beanReference(taskElement, parserContext, builder);
    }
    
    private RuntimeBeanReference triggerTaskReference(final String runnableBeanName, final String triggerBeanName, final Element taskElement, final ParserContext parserContext) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition("org.springframework.scheduling.config.TriggerTask");
        builder.addConstructorArgReference(runnableBeanName);
        builder.addConstructorArgReference(triggerBeanName);
        return this.beanReference(taskElement, parserContext, builder);
    }
    
    private RuntimeBeanReference beanReference(final Element taskElement, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
        builder.getRawBeanDefinition().setSource(parserContext.extractSource(taskElement));
        final String generatedName = parserContext.getReaderContext().generateBeanName(builder.getRawBeanDefinition());
        parserContext.registerBeanComponent(new BeanComponentDefinition(builder.getBeanDefinition(), generatedName));
        return new RuntimeBeanReference(generatedName);
    }
}
