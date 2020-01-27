// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.apache.commons.logging.LogFactory;
import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.apache.commons.logging.Log;
import org.aspectj.bridge.IMessageHandler;

public class AspectJWeaverMessageHandler implements IMessageHandler
{
    private static final String AJ_ID = "[AspectJ] ";
    private static final Log logger;
    
    public boolean handleMessage(final IMessage message) throws AbortException {
        final IMessage.Kind messageKind = message.getKind();
        if (messageKind == IMessage.DEBUG) {
            if (AspectJWeaverMessageHandler.logger.isDebugEnabled()) {
                AspectJWeaverMessageHandler.logger.debug(this.makeMessageFor(message));
                return true;
            }
        }
        else if (messageKind == IMessage.INFO || messageKind == IMessage.WEAVEINFO) {
            if (AspectJWeaverMessageHandler.logger.isInfoEnabled()) {
                AspectJWeaverMessageHandler.logger.info(this.makeMessageFor(message));
                return true;
            }
        }
        else if (messageKind == IMessage.WARNING) {
            if (AspectJWeaverMessageHandler.logger.isWarnEnabled()) {
                AspectJWeaverMessageHandler.logger.warn(this.makeMessageFor(message));
                return true;
            }
        }
        else if (messageKind == IMessage.ERROR) {
            if (AspectJWeaverMessageHandler.logger.isErrorEnabled()) {
                AspectJWeaverMessageHandler.logger.error(this.makeMessageFor(message));
                return true;
            }
        }
        else if (messageKind == IMessage.ABORT && AspectJWeaverMessageHandler.logger.isFatalEnabled()) {
            AspectJWeaverMessageHandler.logger.fatal(this.makeMessageFor(message));
            return true;
        }
        return false;
    }
    
    private String makeMessageFor(final IMessage aMessage) {
        return "[AspectJ] " + aMessage.getMessage();
    }
    
    public boolean isIgnoring(final IMessage.Kind messageKind) {
        return false;
    }
    
    public void dontIgnore(final IMessage.Kind messageKind) {
    }
    
    public void ignore(final IMessage.Kind kind) {
    }
    
    static {
        logger = LogFactory.getLog("AspectJ Weaver");
    }
}
