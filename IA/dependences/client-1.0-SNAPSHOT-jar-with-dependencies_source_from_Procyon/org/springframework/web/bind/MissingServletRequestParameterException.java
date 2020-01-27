// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.bind;

public class MissingServletRequestParameterException extends ServletRequestBindingException
{
    private final String parameterName;
    private final String parameterType;
    
    public MissingServletRequestParameterException(final String parameterName, final String parameterType) {
        super("");
        this.parameterName = parameterName;
        this.parameterType = parameterType;
    }
    
    @Override
    public String getMessage() {
        return "Required " + this.parameterType + " parameter '" + this.parameterName + "' is not present";
    }
    
    public final String getParameterName() {
        return this.parameterName;
    }
    
    public final String getParameterType() {
        return this.parameterType;
    }
}
