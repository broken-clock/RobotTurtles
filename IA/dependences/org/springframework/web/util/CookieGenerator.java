// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import javax.servlet.http.Cookie;
import org.springframework.util.Assert;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class CookieGenerator
{
    public static final String DEFAULT_COOKIE_PATH = "/";
    @Deprecated
    public static final int DEFAULT_COOKIE_MAX_AGE = Integer.MAX_VALUE;
    protected final Log logger;
    private String cookieName;
    private String cookieDomain;
    private String cookiePath;
    private Integer cookieMaxAge;
    private boolean cookieSecure;
    private boolean cookieHttpOnly;
    
    public CookieGenerator() {
        this.logger = LogFactory.getLog(this.getClass());
        this.cookiePath = "/";
        this.cookieMaxAge = null;
        this.cookieSecure = false;
        this.cookieHttpOnly = false;
    }
    
    public void setCookieName(final String cookieName) {
        this.cookieName = cookieName;
    }
    
    public String getCookieName() {
        return this.cookieName;
    }
    
    public void setCookieDomain(final String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }
    
    public String getCookieDomain() {
        return this.cookieDomain;
    }
    
    public void setCookiePath(final String cookiePath) {
        this.cookiePath = cookiePath;
    }
    
    public String getCookiePath() {
        return this.cookiePath;
    }
    
    public void setCookieMaxAge(final Integer cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }
    
    public Integer getCookieMaxAge() {
        return this.cookieMaxAge;
    }
    
    public void setCookieSecure(final boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }
    
    public boolean isCookieSecure() {
        return this.cookieSecure;
    }
    
    public void setCookieHttpOnly(final boolean cookieHttpOnly) {
        this.cookieHttpOnly = cookieHttpOnly;
    }
    
    public boolean isCookieHttpOnly() {
        return this.cookieHttpOnly;
    }
    
    public void addCookie(final HttpServletResponse response, final String cookieValue) {
        Assert.notNull(response, "HttpServletResponse must not be null");
        final Cookie cookie = this.createCookie(cookieValue);
        final Integer maxAge = this.getCookieMaxAge();
        if (maxAge != null) {
            cookie.setMaxAge((int)maxAge);
        }
        if (this.isCookieSecure()) {
            cookie.setSecure(true);
        }
        if (this.isCookieHttpOnly()) {
            cookie.setHttpOnly(true);
        }
        response.addCookie(cookie);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Added cookie with name [" + this.getCookieName() + "] and value [" + cookieValue + "]");
        }
    }
    
    public void removeCookie(final HttpServletResponse response) {
        Assert.notNull(response, "HttpServletResponse must not be null");
        final Cookie cookie = this.createCookie("");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Removed cookie with name [" + this.getCookieName() + "]");
        }
    }
    
    protected Cookie createCookie(final String cookieValue) {
        final Cookie cookie = new Cookie(this.getCookieName(), cookieValue);
        if (this.getCookieDomain() != null) {
            cookie.setDomain(this.getCookieDomain());
        }
        cookie.setPath(this.getCookiePath());
        return cookie;
    }
}
