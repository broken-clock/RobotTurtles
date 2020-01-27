// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.bind;

import org.springframework.validation.BindException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import org.springframework.web.util.WebUtils;
import org.springframework.web.multipart.MultipartRequest;
import javax.servlet.ServletRequest;

public class ServletRequestDataBinder extends WebDataBinder
{
    public ServletRequestDataBinder(final Object target) {
        super(target);
    }
    
    public ServletRequestDataBinder(final Object target, final String objectName) {
        super(target, objectName);
    }
    
    public void bind(final ServletRequest request) {
        final MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request);
        final MultipartRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartRequest.class);
        if (multipartRequest != null) {
            this.bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
        }
        this.addBindValues(mpvs, request);
        this.doBind(mpvs);
    }
    
    protected void addBindValues(final MutablePropertyValues mpvs, final ServletRequest request) {
    }
    
    public void closeNoCatch() throws ServletRequestBindingException {
        if (this.getBindingResult().hasErrors()) {
            throw new ServletRequestBindingException("Errors binding onto object '" + this.getBindingResult().getObjectName() + "'", new BindException(this.getBindingResult()));
        }
    }
}
