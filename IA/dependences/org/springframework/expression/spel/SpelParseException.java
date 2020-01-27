// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel;

import org.springframework.expression.ParseException;

public class SpelParseException extends ParseException
{
    private final SpelMessage message;
    private final Object[] inserts;
    
    public SpelParseException(final String expressionString, final int position, final SpelMessage message, final Object... inserts) {
        super(expressionString, position, message.formatMessage(position, inserts));
        this.position = position;
        this.message = message;
        this.inserts = inserts;
    }
    
    public SpelParseException(final int position, final SpelMessage message, final Object... inserts) {
        super(position, message.formatMessage(position, inserts));
        this.position = position;
        this.message = message;
        this.inserts = inserts;
    }
    
    public SpelParseException(final int position, final Throwable cause, final SpelMessage message, final Object... inserts) {
        super(position, message.formatMessage(position, inserts), cause);
        this.position = position;
        this.message = message;
        this.inserts = inserts;
    }
    
    @Override
    public String getMessage() {
        return (this.message != null) ? this.message.formatMessage(this.position, this.inserts) : super.getMessage();
    }
    
    public SpelMessage getMessageCode() {
        return this.message;
    }
    
    public Object[] getInserts() {
        return this.inserts;
    }
}
