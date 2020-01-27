// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.jsf;

import javax.faces.context.FacesContext;
import javax.faces.application.NavigationHandler;

public abstract class DecoratingNavigationHandler extends NavigationHandler
{
    private NavigationHandler decoratedNavigationHandler;
    
    protected DecoratingNavigationHandler() {
    }
    
    protected DecoratingNavigationHandler(final NavigationHandler originalNavigationHandler) {
        this.decoratedNavigationHandler = originalNavigationHandler;
    }
    
    public final NavigationHandler getDecoratedNavigationHandler() {
        return this.decoratedNavigationHandler;
    }
    
    public final void handleNavigation(final FacesContext facesContext, final String fromAction, final String outcome) {
        this.handleNavigation(facesContext, fromAction, outcome, this.decoratedNavigationHandler);
    }
    
    public abstract void handleNavigation(final FacesContext p0, final String p1, final String p2, final NavigationHandler p3);
    
    protected final void callNextHandlerInChain(final FacesContext facesContext, final String fromAction, final String outcome, final NavigationHandler originalNavigationHandler) {
        final NavigationHandler decoratedNavigationHandler = this.getDecoratedNavigationHandler();
        if (decoratedNavigationHandler instanceof DecoratingNavigationHandler) {
            final DecoratingNavigationHandler decHandler = (DecoratingNavigationHandler)decoratedNavigationHandler;
            decHandler.handleNavigation(facesContext, fromAction, outcome, originalNavigationHandler);
        }
        else if (decoratedNavigationHandler != null) {
            decoratedNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
        }
        else if (originalNavigationHandler != null) {
            originalNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
        }
    }
}
