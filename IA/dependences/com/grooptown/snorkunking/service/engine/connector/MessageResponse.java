// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.connector;

public class MessageResponse
{
    private String error;
    private String message;
    
    public MessageResponse() {
    }
    
    public MessageResponse(final String error, final String message) {
        this.setError(error);
        this.setMessage(message);
    }
    
    public String getError() {
        return this.error;
    }
    
    public void setError(final String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
}
