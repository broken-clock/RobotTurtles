// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel;

import org.springframework.expression.EvaluationException;

public class SpelEvaluationException extends EvaluationException
{
    private final SpelMessage message;
    private final Object[] inserts;
    
    public SpelEvaluationException(final SpelMessage message, final Object... inserts) {
        super(message.formatMessage(0, inserts));
        this.message = message;
        this.inserts = inserts;
    }
    
    public SpelEvaluationException(final int position, final SpelMessage message, final Object... inserts) {
        super(position, message.formatMessage(position, inserts));
        this.message = message;
        this.inserts = inserts;
    }
    
    public SpelEvaluationException(final int position, final Throwable cause, final SpelMessage message, final Object... inserts) {
        super(position, message.formatMessage(position, inserts), cause);
        this.message = message;
        this.inserts = inserts;
    }
    
    public SpelEvaluationException(final Throwable cause, final SpelMessage message, final Object... inserts) {
        super(message.formatMessage(0, inserts), cause);
        this.message = message;
        this.inserts = inserts;
    }
    
    @Override
    public String getMessage() {
        if (this.message != null) {
            return this.message.formatMessage(this.position, this.inserts);
        }
        return super.getMessage();
    }
    
    public SpelMessage getMessageCode() {
        return this.message;
    }
    
    public void setPosition(final int position) {
        this.position = position;
    }
    
    public Object[] getInserts() {
        return this.inserts;
    }
}
