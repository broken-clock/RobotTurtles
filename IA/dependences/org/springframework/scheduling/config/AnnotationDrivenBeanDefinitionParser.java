// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.config;

import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.BeanDefinitionParser;

public class AnnotationDrivenBeanDefinitionParser implements BeanDefinitionParser
{
    @Deprecated
    public static final String ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalAsyncAnnotationProcessor";
    @Deprecated
    public static final String ASYNC_EXECUTION_ASPECT_BEAN_NAME = "org.springframework.scheduling.config.internalAsyncExecutionAspect";
    @Deprecated
    public static final String SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalScheduledAnnotationProcessor";
    
    @Override
    public BeanDefinition parse(final Element element, final ParserContext parserContext) {
        final Object source = parserContext.extractSource(element);
        final CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
        parserContext.pushContainingComponent(compDefinition);
        final BeanDefinitionRegistry registry = parserContext.getRegistry();
        final String mode = element.getAttribute("mode");
        if ("aspectj".equals(mode)) {
            this.registerAsyncExecutionAspect(element, parserContext);
        }
        else if (registry.containsBeanDefinition("org.springframework.context.annotation.internalAsyncAnnotationProcessor")) {
            parserContext.getReaderContext().error("Only one AsyncAnnotationBeanPostProcessor may exist within the context.", source);
        }
        else {
            final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition("org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor");
            builder.getRawBeanDefinition().setSource(source);
            final String executor = element.getAttribute("executor");
            if (StringUtils.hasText(executor)) {
                builder.addPropertyReference("executor", executor);
            }
            if (Boolean.valueOf(element.getAttribute("proxy-target-class"))) {
                builder.addPropertyValue("proxyTargetClass", true);
            }
            registerPostProcessor(parserContext, builder, "org.springframework.context.annotation.internalAsyncAnnotationProcessor");
        }
        if (registry.containsBeanDefinition("org.springframework.context.annotation.internalScheduledAnnotationProcessor")) {
            parserContext.getReaderContext().error("Only one ScheduledAnnotationBeanPostProcessor may exist within the context.", source);
        }
        else {
            final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition("org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor");
            builder.getRawBeanDefinition().setSource(source);
            final String scheduler = element.getAttribute("scheduler");
            if (StringUtils.hasText(scheduler)) {
                builder.addPropertyReference("scheduler", scheduler);
            }
            registerPostProcessor(parserContext, builder, "org.springframework.context.annotation.internalScheduledAnnotationProcessor");
        }
        parserContext.popAndRegisterContainingComponent();
        return null;
    }
    
    private void registerAsyncExecutionAspect(final Element element, final ParserContext parserContext) {
        if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.scheduling.config.internalAsyncExecutionAspect")) {
            final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition("org.springframework.scheduling.aspectj.AnnotationAsyncExecutionAspect");
            builder.setFactoryMethod("aspectOf");
            final String executor = element.getAttribute("executor");
            if (StringUtils.hasText(executor)) {
                builder.addPropertyReference("executor", executor);
            }
            parserContext.registerBeanComponent(new BeanComponentDefinition(builder.getBeanDefinition(), "org.springframework.scheduling.config.internalAsyncExecutionAspect"));
        }
    }
    
    private static void registerPostProcessor(final ParserContext parserContext, final BeanDefinitionBuilder builder, final String beanName) {
        builder.setRole(2);
        parserContext.getRegistry().registerBeanDefinition(beanName, builder.getBeanDefinition());
        final BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getBeanDefinition(), beanName);
        parserContext.registerComponent(new BeanComponentDefinition(holder));
    }
}
