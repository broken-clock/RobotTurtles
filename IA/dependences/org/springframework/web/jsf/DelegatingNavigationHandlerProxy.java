// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.jsf;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.beans.factory.BeanFactory;
import javax.faces.context.FacesContext;
import javax.faces.application.NavigationHandler;

public class DelegatingNavigationHandlerProxy extends NavigationHandler
{
    public static final String DEFAULT_TARGET_BEAN_NAME = "jsfNavigationHandler";
    private NavigationHandler originalNavigationHandler;
    
    public DelegatingNavigationHandlerProxy() {
    }
    
    public DelegatingNavigationHandlerProxy(final NavigationHandler originalNavigationHandler) {
        this.originalNavigationHandler = originalNavigationHandler;
    }
    
    public void handleNavigation(final FacesContext facesContext, final String fromAction, final String outcome) {
        final NavigationHandler handler = this.getDelegate(facesContext);
        if (handler instanceof DecoratingNavigationHandler) {
            ((DecoratingNavigationHandler)handler).handleNavigation(facesContext, fromAction, outcome, this.originalNavigationHandler);
        }
        else {
            handler.handleNavigation(facesContext, fromAction, outcome);
        }
    }
    
    protected NavigationHandler getDelegate(final FacesContext facesContext) {
        final String targetBeanName = this.getTargetBeanName(facesContext);
        return this.getBeanFactory(facesContext).getBean(targetBeanName, NavigationHandler.class);
    }
    
    protected String getTargetBeanName(final FacesContext facesContext) {
        return "jsfNavigationHandler";
    }
    
    protected BeanFactory getBeanFactory(final FacesContext facesContext) {
        return this.getWebApplicationContext(facesContext);
    }
    
    protected WebApplicationContext getWebApplicationContext(final FacesContext facesContext) {
        return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
    }
}
