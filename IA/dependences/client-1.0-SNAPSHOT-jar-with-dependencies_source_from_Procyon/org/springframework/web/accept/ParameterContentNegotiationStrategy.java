// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.accept;

import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.util.Assert;
import org.springframework.http.MediaType;
import java.util.Map;
import org.apache.commons.logging.Log;

public class ParameterContentNegotiationStrategy extends AbstractMappingContentNegotiationStrategy
{
    private static final Log logger;
    private String parameterName;
    
    public ParameterContentNegotiationStrategy(final Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
        this.parameterName = "format";
    }
    
    public void setParameterName(final String parameterName) {
        Assert.notNull(parameterName, "parameterName is required");
        this.parameterName = parameterName;
    }
    
    @Override
    protected String getMediaTypeKey(final NativeWebRequest webRequest) {
        return webRequest.getParameter(this.parameterName);
    }
    
    @Override
    protected void handleMatch(final String mediaTypeKey, final MediaType mediaType) {
        if (ParameterContentNegotiationStrategy.logger.isDebugEnabled()) {
            ParameterContentNegotiationStrategy.logger.debug("Requested media type is '" + mediaType + "' (based on parameter '" + this.parameterName + "'='" + mediaTypeKey + "')");
        }
    }
    
    static {
        logger = LogFactory.getLog(ParameterContentNegotiationStrategy.class);
    }
}
