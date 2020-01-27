// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class TaskNamespaceHandler extends NamespaceHandlerSupport
{
    @Override
    public void init() {
        this.registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBeanDefinitionParser());
        this.registerBeanDefinitionParser("executor", new ExecutorBeanDefinitionParser());
        this.registerBeanDefinitionParser("scheduled-tasks", new ScheduledTasksBeanDefinitionParser());
        this.registerBeanDefinitionParser("scheduler", new SchedulerBeanDefinitionParser());
    }
}
