// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import java.io.Serializable;
import org.springframework.context.MessageSourceResolvable;

public class DefaultMessageSourceResolvable implements MessageSourceResolvable, Serializable
{
    private final String[] codes;
    private final Object[] arguments;
    private final String defaultMessage;
    
    public DefaultMessageSourceResolvable(final String code) {
        this(new String[] { code }, null, null);
    }
    
    public DefaultMessageSourceResolvable(final String[] codes) {
        this(codes, null, null);
    }
    
    public DefaultMessageSourceResolvable(final String[] codes, final String defaultMessage) {
        this(codes, null, defaultMessage);
    }
    
    public DefaultMessageSourceResolvable(final String[] codes, final Object[] arguments) {
        this(codes, arguments, null);
    }
    
    public DefaultMessageSourceResolvable(final String[] codes, final Object[] arguments, final String defaultMessage) {
        this.codes = codes;
        this.arguments = arguments;
        this.defaultMessage = defaultMessage;
    }
    
    public DefaultMessageSourceResolvable(final MessageSourceResolvable resolvable) {
        this(resolvable.getCodes(), resolvable.getArguments(), resolvable.getDefaultMessage());
    }
    
    @Override
    public String[] getCodes() {
        return this.codes;
    }
    
    public String getCode() {
        return (this.codes != null && this.codes.length > 0) ? this.codes[this.codes.length - 1] : null;
    }
    
    @Override
    public Object[] getArguments() {
        return this.arguments;
    }
    
    @Override
    public String getDefaultMessage() {
        return this.defaultMessage;
    }
    
    protected final String resolvableToString() {
        final StringBuilder result = new StringBuilder();
        result.append("codes [").append(StringUtils.arrayToDelimitedString(this.codes, ","));
        result.append("]; arguments [" + StringUtils.arrayToDelimitedString(this.arguments, ","));
        result.append("]; default message [").append(this.defaultMessage).append(']');
        return result.toString();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": " + this.resolvableToString();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MessageSourceResolvable)) {
            return false;
        }
        final MessageSourceResolvable otherResolvable = (MessageSourceResolvable)other;
        return ObjectUtils.nullSafeEquals(this.getCodes(), otherResolvable.getCodes()) && ObjectUtils.nullSafeEquals(this.getArguments(), otherResolvable.getArguments()) && ObjectUtils.nullSafeEquals(this.getDefaultMessage(), otherResolvable.getDefaultMessage());
    }
    
    @Override
    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.getCodes());
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.getArguments());
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.getDefaultMessage());
        return hashCode;
    }
}
