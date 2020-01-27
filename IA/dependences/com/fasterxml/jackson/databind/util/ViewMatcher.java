// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.io.Serializable;

public class ViewMatcher implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected static final ViewMatcher EMPTY;
    
    public boolean isVisibleForView(final Class<?> activeView) {
        return false;
    }
    
    public static ViewMatcher construct(final Class<?>[] views) {
        if (views == null) {
            return ViewMatcher.EMPTY;
        }
        switch (views.length) {
            case 0: {
                return ViewMatcher.EMPTY;
            }
            case 1: {
                return new Single(views[0]);
            }
            default: {
                return new Multi(views);
            }
        }
    }
    
    static {
        EMPTY = new ViewMatcher();
    }
    
    private static final class Single extends ViewMatcher
    {
        private static final long serialVersionUID = 1L;
        private final Class<?> _view;
        
        public Single(final Class<?> v) {
            this._view = v;
        }
        
        @Override
        public boolean isVisibleForView(final Class<?> activeView) {
            return activeView == this._view || this._view.isAssignableFrom(activeView);
        }
    }
    
    private static final class Multi extends ViewMatcher implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private final Class<?>[] _views;
        
        public Multi(final Class<?>[] v) {
            this._views = v;
        }
        
        @Override
        public boolean isVisibleForView(final Class<?> activeView) {
            for (int i = 0, len = this._views.length; i < len; ++i) {
                final Class<?> view = this._views[i];
                if (activeView == view || view.isAssignableFrom(activeView)) {
                    return true;
                }
            }
            return false;
        }
    }
}
