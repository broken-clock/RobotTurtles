// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.util.LinkedMultiValueMap;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.MediaType;
import javax.servlet.ServletException;
import org.springframework.http.HttpInputMessage;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.springframework.util.MultiValueMap;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.server.ServletServerHttpRequest;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;

public class HttpPutFormContentFilter extends OncePerRequestFilter
{
    private final FormHttpMessageConverter formConverter;
    
    public HttpPutFormContentFilter() {
        this.formConverter = new AllEncompassingFormHttpMessageConverter();
    }
    
    public void setCharset(final Charset charset) {
        this.formConverter.setCharset(charset);
    }
    
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (("PUT".equals(request.getMethod()) || "PATCH".equals(request.getMethod())) && this.isFormContentType(request)) {
            final HttpInputMessage inputMessage = new ServletServerHttpRequest(request) {
                @Override
                public InputStream getBody() throws IOException {
                    return (InputStream)request.getInputStream();
                }
            };
            final MultiValueMap<String, String> formParameters = this.formConverter.read((Class<? extends MultiValueMap<String, ?>>)null, inputMessage);
            final HttpServletRequest wrapper = (HttpServletRequest)new HttpPutFormContentRequestWrapper(request, formParameters);
            filterChain.doFilter((ServletRequest)wrapper, (ServletResponse)response);
        }
        else {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }
    
    private boolean isFormContentType(final HttpServletRequest request) {
        final String contentType = request.getContentType();
        if (contentType != null) {
            try {
                final MediaType mediaType = MediaType.parseMediaType(contentType);
                return MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType);
            }
            catch (IllegalArgumentException ex) {
                return false;
            }
        }
        return false;
    }
    
    private static class HttpPutFormContentRequestWrapper extends HttpServletRequestWrapper
    {
        private MultiValueMap<String, String> formParameters;
        
        public HttpPutFormContentRequestWrapper(final HttpServletRequest request, final MultiValueMap<String, String> parameters) {
            super(request);
            this.formParameters = ((parameters != null) ? parameters : new LinkedMultiValueMap<String, String>());
        }
        
        public String getParameter(final String name) {
            final String queryStringValue = super.getParameter(name);
            final String formValue = this.formParameters.getFirst(name);
            return (queryStringValue != null) ? queryStringValue : formValue;
        }
        
        public Map<String, String[]> getParameterMap() {
            final Map<String, String[]> result = new LinkedHashMap<String, String[]>();
            final Enumeration<String> names = this.getParameterNames();
            while (names.hasMoreElements()) {
                final String name = names.nextElement();
                result.put(name, this.getParameterValues(name));
            }
            return result;
        }
        
        public Enumeration<String> getParameterNames() {
            final Set<String> names = new LinkedHashSet<String>();
            names.addAll((Collection<? extends String>)Collections.list((Enumeration<Object>)super.getParameterNames()));
            names.addAll((Collection<? extends String>)this.formParameters.keySet());
            return Collections.enumeration(names);
        }
        
        public String[] getParameterValues(final String name) {
            final String[] queryStringValues = super.getParameterValues(name);
            final List<String> formValues = this.formParameters.get(name);
            if (formValues == null) {
                return queryStringValues;
            }
            if (queryStringValues == null) {
                return formValues.toArray(new String[formValues.size()]);
            }
            final List<String> result = new ArrayList<String>();
            result.addAll(Arrays.asList(queryStringValues));
            result.addAll(formValues);
            return result.toArray(new String[result.size()]);
        }
    }
}
