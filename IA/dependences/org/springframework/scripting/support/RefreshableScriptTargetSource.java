// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.support;

import org.springframework.util.Assert;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.ScriptFactory;
import org.springframework.aop.target.dynamic.BeanFactoryRefreshableTargetSource;

public class RefreshableScriptTargetSource extends BeanFactoryRefreshableTargetSource
{
    private final ScriptFactory scriptFactory;
    private final ScriptSource scriptSource;
    private final boolean isFactoryBean;
    
    public RefreshableScriptTargetSource(final BeanFactory beanFactory, final String beanName, final ScriptFactory scriptFactory, final ScriptSource scriptSource, final boolean isFactoryBean) {
        super(beanFactory, beanName);
        Assert.notNull(scriptFactory, "ScriptFactory must not be null");
        Assert.notNull(scriptSource, "ScriptSource must not be null");
        this.scriptFactory = scriptFactory;
        this.scriptSource = scriptSource;
        this.isFactoryBean = isFactoryBean;
    }
    
    @Override
    protected boolean requiresRefresh() {
        return this.scriptFactory.requiresScriptedObjectRefresh(this.scriptSource);
    }
    
    @Override
    protected Object obtainFreshBean(final BeanFactory beanFactory, final String beanName) {
        return super.obtainFreshBean(beanFactory, this.isFactoryBean ? ("&" + beanName) : beanName);
    }
}
