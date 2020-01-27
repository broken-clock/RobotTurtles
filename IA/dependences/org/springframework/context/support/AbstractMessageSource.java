// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import java.util.List;
import java.util.ArrayList;
import java.text.MessageFormat;
import org.springframework.util.ObjectUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import java.util.Locale;
import java.util.Properties;
import org.springframework.context.MessageSource;
import org.springframework.context.HierarchicalMessageSource;

public abstract class AbstractMessageSource extends MessageSourceSupport implements HierarchicalMessageSource
{
    private MessageSource parentMessageSource;
    private Properties commonMessages;
    private boolean useCodeAsDefaultMessage;
    
    public AbstractMessageSource() {
        this.useCodeAsDefaultMessage = false;
    }
    
    @Override
    public void setParentMessageSource(final MessageSource parent) {
        this.parentMessageSource = parent;
    }
    
    @Override
    public MessageSource getParentMessageSource() {
        return this.parentMessageSource;
    }
    
    public void setCommonMessages(final Properties commonMessages) {
        this.commonMessages = commonMessages;
    }
    
    protected Properties getCommonMessages() {
        return this.commonMessages;
    }
    
    public void setUseCodeAsDefaultMessage(final boolean useCodeAsDefaultMessage) {
        this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
    }
    
    protected boolean isUseCodeAsDefaultMessage() {
        return this.useCodeAsDefaultMessage;
    }
    
    @Override
    public final String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
        final String msg = this.getMessageInternal(code, args, locale);
        if (msg != null) {
            return msg;
        }
        if (defaultMessage == null) {
            final String fallback = this.getDefaultMessage(code);
            if (fallback != null) {
                return fallback;
            }
        }
        return this.renderDefaultMessage(defaultMessage, args, locale);
    }
    
    @Override
    public final String getMessage(final String code, final Object[] args, final Locale locale) throws NoSuchMessageException {
        final String msg = this.getMessageInternal(code, args, locale);
        if (msg != null) {
            return msg;
        }
        final String fallback = this.getDefaultMessage(code);
        if (fallback != null) {
            return fallback;
        }
        throw new NoSuchMessageException(code, locale);
    }
    
    @Override
    public final String getMessage(final MessageSourceResolvable resolvable, final Locale locale) throws NoSuchMessageException {
        String[] codes = resolvable.getCodes();
        if (codes == null) {
            codes = new String[0];
        }
        for (final String code : codes) {
            final String msg = this.getMessageInternal(code, resolvable.getArguments(), locale);
            if (msg != null) {
                return msg;
            }
        }
        final String defaultMessage = resolvable.getDefaultMessage();
        if (defaultMessage != null) {
            return this.renderDefaultMessage(defaultMessage, resolvable.getArguments(), locale);
        }
        if (codes.length > 0) {
            final String fallback = this.getDefaultMessage(codes[0]);
            if (fallback != null) {
                return fallback;
            }
        }
        throw new NoSuchMessageException((codes.length > 0) ? codes[codes.length - 1] : null, locale);
    }
    
    protected String getMessageInternal(final String code, final Object[] args, Locale locale) {
        if (code == null) {
            return null;
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Object[] argsToUse = args;
        if (!this.isAlwaysUseMessageFormat() && ObjectUtils.isEmpty(args)) {
            final String message = this.resolveCodeWithoutArguments(code, locale);
            if (message != null) {
                return message;
            }
        }
        else {
            argsToUse = this.resolveArguments(args, locale);
            final MessageFormat messageFormat = this.resolveCode(code, locale);
            if (messageFormat != null) {
                synchronized (messageFormat) {
                    return messageFormat.format(argsToUse);
                }
            }
        }
        final Properties commonMessages = this.getCommonMessages();
        if (commonMessages != null) {
            final String commonMessage = commonMessages.getProperty(code);
            if (commonMessage != null) {
                return this.formatMessage(commonMessage, args, locale);
            }
        }
        return this.getMessageFromParent(code, argsToUse, locale);
    }
    
    protected String getMessageFromParent(final String code, final Object[] args, final Locale locale) {
        final MessageSource parent = this.getParentMessageSource();
        if (parent == null) {
            return null;
        }
        if (parent instanceof AbstractMessageSource) {
            return ((AbstractMessageSource)parent).getMessageInternal(code, args, locale);
        }
        return parent.getMessage(code, args, null, locale);
    }
    
    protected String getDefaultMessage(final String code) {
        if (this.isUseCodeAsDefaultMessage()) {
            return code;
        }
        return null;
    }
    
    @Override
    protected Object[] resolveArguments(final Object[] args, final Locale locale) {
        if (args == null) {
            return new Object[0];
        }
        final List<Object> resolvedArgs = new ArrayList<Object>(args.length);
        for (final Object arg : args) {
            if (arg instanceof MessageSourceResolvable) {
                resolvedArgs.add(this.getMessage((MessageSourceResolvable)arg, locale));
            }
            else {
                resolvedArgs.add(arg);
            }
        }
        return resolvedArgs.toArray(new Object[resolvedArgs.size()]);
    }
    
    protected String resolveCodeWithoutArguments(final String code, final Locale locale) {
        final MessageFormat messageFormat = this.resolveCode(code, locale);
        if (messageFormat != null) {
            synchronized (messageFormat) {
                return messageFormat.format(new Object[0]);
            }
        }
        return null;
    }
    
    protected abstract MessageFormat resolveCode(final String p0, final Locale p1);
}
