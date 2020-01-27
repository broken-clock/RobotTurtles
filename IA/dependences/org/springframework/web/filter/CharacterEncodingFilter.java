// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class CharacterEncodingFilter extends OncePerRequestFilter
{
    private String encoding;
    private boolean forceEncoding;
    
    public CharacterEncodingFilter() {
        this.forceEncoding = false;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public void setForceEncoding(final boolean forceEncoding) {
        this.forceEncoding = forceEncoding;
    }
    
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (this.encoding != null && (this.forceEncoding || request.getCharacterEncoding() == null)) {
            request.setCharacterEncoding(this.encoding);
            if (this.forceEncoding) {
                response.setCharacterEncoding(this.encoding);
            }
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
}
