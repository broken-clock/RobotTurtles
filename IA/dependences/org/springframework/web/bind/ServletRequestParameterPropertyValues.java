// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.bind;

import java.util.Map;
import org.springframework.web.util.WebUtils;
import javax.servlet.ServletRequest;
import org.springframework.beans.MutablePropertyValues;

public class ServletRequestParameterPropertyValues extends MutablePropertyValues
{
    public static final String DEFAULT_PREFIX_SEPARATOR = "_";
    
    public ServletRequestParameterPropertyValues(final ServletRequest request) {
        this(request, null, null);
    }
    
    public ServletRequestParameterPropertyValues(final ServletRequest request, final String prefix) {
        this(request, prefix, "_");
    }
    
    public ServletRequestParameterPropertyValues(final ServletRequest request, final String prefix, final String prefixSeparator) {
        super(WebUtils.getParametersStartingWith(request, (prefix != null) ? (prefix + prefixSeparator) : null));
    }
}
