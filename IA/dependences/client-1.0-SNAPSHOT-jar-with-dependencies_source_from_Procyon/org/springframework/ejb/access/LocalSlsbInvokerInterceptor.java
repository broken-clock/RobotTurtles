// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ejb.access;

import javax.ejb.EJBLocalHome;
import java.lang.reflect.Method;
import javax.ejb.EJBLocalObject;
import javax.naming.NamingException;
import java.lang.reflect.InvocationTargetException;
import javax.ejb.CreateException;
import org.aopalliance.intercept.MethodInvocation;

public class LocalSlsbInvokerInterceptor extends AbstractSlsbInvokerInterceptor
{
    private volatile boolean homeAsComponent;
    
    public LocalSlsbInvokerInterceptor() {
        this.homeAsComponent = false;
    }
    
    public Object invokeInContext(final MethodInvocation invocation) throws Throwable {
        Object ejb = null;
        try {
            ejb = this.getSessionBeanInstance();
            final Method method = invocation.getMethod();
            if (method.getDeclaringClass().isInstance(ejb)) {
                return method.invoke(ejb, invocation.getArguments());
            }
            final Method ejbMethod = ejb.getClass().getMethod(method.getName(), method.getParameterTypes());
            return ejbMethod.invoke(ejb, invocation.getArguments());
        }
        catch (InvocationTargetException ex) {
            final Throwable targetEx = ex.getTargetException();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Method of local EJB [" + this.getJndiName() + "] threw exception", targetEx);
            }
            if (targetEx instanceof CreateException) {
                throw new EjbAccessException("Could not create local EJB [" + this.getJndiName() + "]", targetEx);
            }
            throw targetEx;
        }
        catch (NamingException ex2) {
            throw new EjbAccessException("Failed to locate local EJB [" + this.getJndiName() + "]", ex2);
        }
        catch (IllegalAccessException ex3) {
            throw new EjbAccessException("Could not access method [" + invocation.getMethod().getName() + "] of local EJB [" + this.getJndiName() + "]", ex3);
        }
        finally {
            if (ejb instanceof EJBLocalObject) {
                this.releaseSessionBeanInstance((EJBLocalObject)ejb);
            }
        }
    }
    
    @Override
    protected Method getCreateMethod(final Object home) throws EjbAccessException {
        if (this.homeAsComponent) {
            return null;
        }
        if (!(home instanceof EJBLocalHome)) {
            this.homeAsComponent = true;
            return null;
        }
        return super.getCreateMethod(home);
    }
    
    protected Object getSessionBeanInstance() throws NamingException, InvocationTargetException {
        return this.newSessionBeanInstance();
    }
    
    protected void releaseSessionBeanInstance(final EJBLocalObject ejb) {
        this.removeSessionBeanInstance(ejb);
    }
    
    protected Object newSessionBeanInstance() throws NamingException, InvocationTargetException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Trying to create reference to local EJB");
        }
        final Object ejbInstance = this.create();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Obtained reference to local EJB: " + ejbInstance);
        }
        return ejbInstance;
    }
    
    protected void removeSessionBeanInstance(final EJBLocalObject ejb) {
        if (ejb != null && !this.homeAsComponent) {
            try {
                ejb.remove();
            }
            catch (Throwable ex) {
                this.logger.warn("Could not invoke 'remove' on local EJB proxy", ex);
            }
        }
    }
}
