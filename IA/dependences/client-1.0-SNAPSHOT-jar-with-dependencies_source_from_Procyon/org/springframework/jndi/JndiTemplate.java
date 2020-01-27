// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi;

import javax.naming.NameNotFoundException;
import javax.naming.InitialContext;
import java.util.Map;
import org.springframework.util.CollectionUtils;
import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.Context;
import org.apache.commons.logging.LogFactory;
import java.util.Properties;
import org.apache.commons.logging.Log;

public class JndiTemplate
{
    protected final Log logger;
    private Properties environment;
    
    public JndiTemplate() {
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    public JndiTemplate(final Properties environment) {
        this.logger = LogFactory.getLog(this.getClass());
        this.environment = environment;
    }
    
    public void setEnvironment(final Properties environment) {
        this.environment = environment;
    }
    
    public Properties getEnvironment() {
        return this.environment;
    }
    
    public <T> T execute(final JndiCallback<T> contextCallback) throws NamingException {
        final Context ctx = this.getContext();
        try {
            return contextCallback.doInContext(ctx);
        }
        finally {
            this.releaseContext(ctx);
        }
    }
    
    public Context getContext() throws NamingException {
        return this.createInitialContext();
    }
    
    public void releaseContext(final Context ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            }
            catch (NamingException ex) {
                this.logger.debug("Could not close JNDI InitialContext", ex);
            }
        }
    }
    
    protected Context createInitialContext() throws NamingException {
        Hashtable<?, ?> icEnv = null;
        final Properties env = this.getEnvironment();
        if (env != null) {
            icEnv = new Hashtable<Object, Object>(env.size());
            CollectionUtils.mergePropertiesIntoMap(env, icEnv);
        }
        return new InitialContext(icEnv);
    }
    
    public Object lookup(final String name) throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Looking up JNDI object with name [" + name + "]");
        }
        return this.execute((JndiCallback<Object>)new JndiCallback<Object>() {
            @Override
            public Object doInContext(final Context ctx) throws NamingException {
                final Object located = ctx.lookup(name);
                if (located == null) {
                    throw new NameNotFoundException("JNDI object with [" + name + "] not found: JNDI implementation returned null");
                }
                return located;
            }
        });
    }
    
    public <T> T lookup(final String name, final Class<T> requiredType) throws NamingException {
        final Object jndiObject = this.lookup(name);
        if (requiredType != null && !requiredType.isInstance(jndiObject)) {
            throw new TypeMismatchNamingException(name, requiredType, (jndiObject != null) ? jndiObject.getClass() : null);
        }
        return (T)jndiObject;
    }
    
    public void bind(final String name, final Object object) throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Binding JNDI object with name [" + name + "]");
        }
        this.execute((JndiCallback<Object>)new JndiCallback<Object>() {
            @Override
            public Object doInContext(final Context ctx) throws NamingException {
                ctx.bind(name, object);
                return null;
            }
        });
    }
    
    public void rebind(final String name, final Object object) throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Rebinding JNDI object with name [" + name + "]");
        }
        this.execute((JndiCallback<Object>)new JndiCallback<Object>() {
            @Override
            public Object doInContext(final Context ctx) throws NamingException {
                ctx.rebind(name, object);
                return null;
            }
        });
    }
    
    public void unbind(final String name) throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Unbinding JNDI object with name [" + name + "]");
        }
        this.execute((JndiCallback<Object>)new JndiCallback<Object>() {
            @Override
            public Object doInContext(final Context ctx) throws NamingException {
                ctx.unbind(name);
                return null;
            }
        });
    }
}
