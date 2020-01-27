// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import java.util.Iterator;
import org.springframework.util.Assert;
import java.util.Locale;
import java.util.HashMap;
import java.text.MessageFormat;
import java.util.Map;

public class StaticMessageSource extends AbstractMessageSource
{
    private final Map<String, String> messages;
    private final Map<String, MessageFormat> cachedMessageFormats;
    
    public StaticMessageSource() {
        this.messages = new HashMap<String, String>();
        this.cachedMessageFormats = new HashMap<String, MessageFormat>();
    }
    
    @Override
    protected String resolveCodeWithoutArguments(final String code, final Locale locale) {
        return this.messages.get(code + "_" + locale.toString());
    }
    
    @Override
    protected MessageFormat resolveCode(final String code, final Locale locale) {
        final String key = code + "_" + locale.toString();
        final String msg = this.messages.get(key);
        if (msg == null) {
            return null;
        }
        synchronized (this.cachedMessageFormats) {
            MessageFormat messageFormat = this.cachedMessageFormats.get(key);
            if (messageFormat == null) {
                messageFormat = this.createMessageFormat(msg, locale);
                this.cachedMessageFormats.put(key, messageFormat);
            }
            return messageFormat;
        }
    }
    
    public void addMessage(final String code, final Locale locale, final String msg) {
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(locale, "Locale must not be null");
        Assert.notNull(msg, "Message must not be null");
        this.messages.put(code + "_" + locale.toString(), msg);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Added message [" + msg + "] for code [" + code + "] and Locale [" + locale + "]");
        }
    }
    
    public void addMessages(final Map<String, String> messages, final Locale locale) {
        Assert.notNull(messages, "Messages Map must not be null");
        for (final Map.Entry<String, String> entry : messages.entrySet()) {
            this.addMessage(entry.getKey(), locale, entry.getValue());
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": " + this.messages;
    }
}
