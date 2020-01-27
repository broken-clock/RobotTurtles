// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.apache.commons.logging.LogFactory;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.objenesis.ObjenesisStd;
import org.apache.commons.logging.Log;

class ObjenesisCglibAopProxy extends CglibAopProxy
{
    private static final Log logger;
    private final ObjenesisStd objenesis;
    
    public ObjenesisCglibAopProxy(final AdvisedSupport config) {
        super(config);
        this.objenesis = new ObjenesisStd(true);
    }
    
    @Override
    protected Object createProxyClassAndInstance(final Enhancer enhancer, final Callback[] callbacks) {
        try {
            final Factory factory = this.objenesis.newInstance((Class<Factory>)enhancer.createClass());
            factory.setCallbacks(callbacks);
            return factory;
        }
        catch (ObjenesisException ex) {
            if (ObjenesisCglibAopProxy.logger.isDebugEnabled()) {
                ObjenesisCglibAopProxy.logger.debug("Unable to instantiate proxy using Objenesis, falling back to regular proxy construction", ex);
            }
            return super.createProxyClassAndInstance(enhancer, callbacks);
        }
    }
    
    static {
        logger = LogFactory.getLog(ObjenesisCglibAopProxy.class);
    }
}
