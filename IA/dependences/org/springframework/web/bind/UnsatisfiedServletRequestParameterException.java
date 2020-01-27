// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.bind;

import java.util.Iterator;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import java.util.Map;

public class UnsatisfiedServletRequestParameterException extends ServletRequestBindingException
{
    private final String[] paramConditions;
    private final Map<String, String[]> actualParams;
    
    public UnsatisfiedServletRequestParameterException(final String[] paramConditions, final Map<String, String[]> actualParams) {
        super("");
        this.paramConditions = paramConditions;
        this.actualParams = actualParams;
    }
    
    @Override
    public String getMessage() {
        return "Parameter conditions \"" + StringUtils.arrayToDelimitedString(this.paramConditions, ", ") + "\" not met for actual request parameters: " + requestParameterMapToString(this.actualParams);
    }
    
    private static String requestParameterMapToString(final Map<String, String[]> actualParams) {
        final StringBuilder result = new StringBuilder();
        final Iterator<Map.Entry<String, String[]>> it = actualParams.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<String, String[]> entry = it.next();
            result.append(entry.getKey()).append('=').append(ObjectUtils.nullSafeToString(entry.getValue()));
            if (it.hasNext()) {
                result.append(", ");
            }
        }
        return result.toString();
    }
    
    public final String[] getParamConditions() {
        return this.paramConditions;
    }
    
    public final Map<String, String[]> getActualParams() {
        return this.actualParams;
    }
}
