// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

public class SimpleBeanTargetSource extends AbstractBeanFactoryBasedTargetSource
{
    @Override
    public Object getTarget() throws Exception {
        return this.getBeanFactory().getBean(this.getTargetBeanName());
    }
}
