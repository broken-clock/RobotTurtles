// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.jsf;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import java.util.Collection;
import javax.faces.context.FacesContext;
import java.util.Iterator;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class DelegatingPhaseListenerMulticaster implements PhaseListener
{
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
    
    public void beforePhase(final PhaseEvent event) {
        for (final PhaseListener listener : this.getDelegates(event.getFacesContext())) {
            listener.beforePhase(event);
        }
    }
    
    public void afterPhase(final PhaseEvent event) {
        for (final PhaseListener listener : this.getDelegates(event.getFacesContext())) {
            listener.afterPhase(event);
        }
    }
    
    protected Collection<PhaseListener> getDelegates(final FacesContext facesContext) {
        final ListableBeanFactory bf = this.getBeanFactory(facesContext);
        return BeanFactoryUtils.beansOfTypeIncludingAncestors(bf, PhaseListener.class, true, false).values();
    }
    
    protected ListableBeanFactory getBeanFactory(final FacesContext facesContext) {
        return this.getWebApplicationContext(facesContext);
    }
    
    protected WebApplicationContext getWebApplicationContext(final FacesContext facesContext) {
        return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
    }
}
