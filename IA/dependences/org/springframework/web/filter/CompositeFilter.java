// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.Iterator;
import javax.servlet.FilterConfig;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;

public class CompositeFilter implements Filter
{
    private List<? extends Filter> filters;
    
    public CompositeFilter() {
        this.filters = new ArrayList<Filter>();
    }
    
    public void setFilters(final List<? extends Filter> filters) {
        this.filters = new ArrayList<Filter>(filters);
    }
    
    public void destroy() {
        int i = this.filters.size();
        while (i-- > 0) {
            final Filter filter = (Filter)this.filters.get(i);
            filter.destroy();
        }
    }
    
    public void init(final FilterConfig config) throws ServletException {
        for (final Filter filter : this.filters) {
            filter.init(config);
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        new VirtualFilterChain(chain, (List)this.filters).doFilter(request, response);
    }
    
    private static class VirtualFilterChain implements FilterChain
    {
        private final FilterChain originalChain;
        private final List<? extends Filter> additionalFilters;
        private int currentPosition;
        
        private VirtualFilterChain(final FilterChain chain, final List<? extends Filter> additionalFilters) {
            this.currentPosition = 0;
            this.originalChain = chain;
            this.additionalFilters = additionalFilters;
        }
        
        public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
            if (this.currentPosition == this.additionalFilters.size()) {
                this.originalChain.doFilter(request, response);
            }
            else {
                ++this.currentPosition;
                final Filter nextFilter = (Filter)this.additionalFilters.get(this.currentPosition - 1);
                nextFilter.doFilter(request, response, (FilterChain)this);
            }
        }
    }
}
