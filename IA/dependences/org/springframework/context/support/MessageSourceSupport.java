// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.util.ObjectUtils;
import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.logging.Log;
import java.text.MessageFormat;

public abstract class MessageSourceSupport
{
    private static final MessageFormat INVALID_MESSAGE_FORMAT;
    protected final Log logger;
    private boolean alwaysUseMessageFormat;
    private final Map<String, Map<Locale, MessageFormat>> messageFormatsPerMessage;
    
    public MessageSourceSupport() {
        this.logger = LogFactory.getLog(this.getClass());
        this.alwaysUseMessageFormat = false;
        this.messageFormatsPerMessage = new HashMap<String, Map<Locale, MessageFormat>>();
    }
    
    public void setAlwaysUseMessageFormat(final boolean alwaysUseMessageFormat) {
        this.alwaysUseMessageFormat = alwaysUseMessageFormat;
    }
    
    protected boolean isAlwaysUseMessageFormat() {
        return this.alwaysUseMessageFormat;
    }
    
    protected String renderDefaultMessage(final String defaultMessage, final Object[] args, final Locale locale) {
        return this.formatMessage(defaultMessage, args, locale);
    }
    
    protected String formatMessage(final String msg, final Object[] args, final Locale locale) {
        if (msg == null || (!this.alwaysUseMessageFormat && ObjectUtils.isEmpty(args))) {
            return msg;
        }
        MessageFormat messageFormat = null;
        synchronized (this.messageFormatsPerMessage) {
            Map<Locale, MessageFormat> messageFormatsPerLocale = this.messageFormatsPerMessage.get(msg);
            if (messageFormatsPerLocale != null) {
                messageFormat = messageFormatsPerLocale.get(locale);
            }
            else {
                messageFormatsPerLocale = new HashMap<Locale, MessageFormat>();
                this.messageFormatsPerMessage.put(msg, messageFormatsPerLocale);
            }
            if (messageFormat == null) {
                try {
                    messageFormat = this.createMessageFormat(msg, locale);
                }
                catch (IllegalArgumentException ex) {
                    if (this.alwaysUseMessageFormat) {
                        throw ex;
                    }
                    messageFormat = MessageSourceSupport.INVALID_MESSAGE_FORMAT;
                }
                messageFormatsPerLocale.put(locale, messageFormat);
            }
        }
        if (messageFormat == MessageSourceSupport.INVALID_MESSAGE_FORMAT) {
            return msg;
        }
        synchronized (messageFormat) {
            return messageFormat.format(this.resolveArguments(args, locale));
        }
    }
    
    protected MessageFormat createMessageFormat(final String msg, final Locale locale) {
        return new MessageFormat((msg != null) ? msg : "", locale);
    }
    
    protected Object[] resolveArguments(final Object[] args, final Locale locale) {
        return args;
    }
    
    static {
        INVALID_MESSAGE_FORMAT = new MessageFormat("");
    }
}
