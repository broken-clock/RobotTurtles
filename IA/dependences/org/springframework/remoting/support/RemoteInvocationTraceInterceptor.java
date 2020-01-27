// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import org.apache.commons.logging.LogFactory;
import java.lang.reflect.Method;
import org.springframework.util.ClassUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.aopalliance.intercept.MethodInterceptor;

public class RemoteInvocationTraceInterceptor implements MethodInterceptor
{
    protected static final Log logger;
    private final String exporterNameClause;
    
    public RemoteInvocationTraceInterceptor() {
        this.exporterNameClause = "";
    }
    
    public RemoteInvocationTraceInterceptor(final String exporterName) {
        this.exporterNameClause = exporterName + " ";
    }
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Method method = invocation.getMethod();
        if (RemoteInvocationTraceInterceptor.logger.isDebugEnabled()) {
            RemoteInvocationTraceInterceptor.logger.debug("Incoming " + this.exporterNameClause + "remote call: " + ClassUtils.getQualifiedMethodName(method));
        }
        try {
            final Object retVal = invocation.proceed();
            if (RemoteInvocationTraceInterceptor.logger.isDebugEnabled()) {
                RemoteInvocationTraceInterceptor.logger.debug("Finished processing of " + this.exporterNameClause + "remote call: " + ClassUtils.getQualifiedMethodName(method));
            }
            return retVal;
        }
        catch (Throwable ex) {
            if (ex instanceof RuntimeException || ex instanceof Error) {
                if (RemoteInvocationTraceInterceptor.logger.isWarnEnabled()) {
                    RemoteInvocationTraceInterceptor.logger.warn("Processing of " + this.exporterNameClause + "remote call resulted in fatal exception: " + ClassUtils.getQualifiedMethodName(method), ex);
                }
            }
            else if (RemoteInvocationTraceInterceptor.logger.isInfoEnabled()) {
                RemoteInvocationTraceInterceptor.logger.info("Processing of " + this.exporterNameClause + "remote call resulted in exception: " + ClassUtils.getQualifiedMethodName(method), ex);
            }
            throw ex;
        }
    }
    
    static {
        logger = LogFactory.getLog(RemoteInvocationTraceInterceptor.class);
    }
}
