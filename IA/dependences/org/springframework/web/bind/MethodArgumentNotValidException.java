// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.bind;

import java.util.Iterator;
import org.springframework.validation.ObjectError;
import org.springframework.validation.BindingResult;
import org.springframework.core.MethodParameter;

public class MethodArgumentNotValidException extends Exception
{
    private final MethodParameter parameter;
    private final BindingResult bindingResult;
    
    public MethodArgumentNotValidException(final MethodParameter parameter, final BindingResult bindingResult) {
        this.parameter = parameter;
        this.bindingResult = bindingResult;
    }
    
    public MethodParameter getParameter() {
        return this.parameter;
    }
    
    public BindingResult getBindingResult() {
        return this.bindingResult;
    }
    
    @Override
    public String getMessage() {
        final StringBuilder sb = new StringBuilder("Validation failed for argument at index ").append(this.parameter.getParameterIndex()).append(" in method: ").append(this.parameter.getMethod().toGenericString()).append(", with ").append(this.bindingResult.getErrorCount()).append(" error(s): ");
        for (final ObjectError error : this.bindingResult.getAllErrors()) {
            sb.append("[").append(error).append("] ");
        }
        return sb.toString();
    }
}
