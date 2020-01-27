// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.io.Serializable;

public abstract class NameTransformer
{
    public static final NameTransformer NOP;
    
    protected NameTransformer() {
    }
    
    public static NameTransformer simpleTransformer(final String prefix, final String suffix) {
        final boolean hasPrefix = prefix != null && prefix.length() > 0;
        final boolean hasSuffix = suffix != null && suffix.length() > 0;
        if (hasPrefix) {
            if (hasSuffix) {
                return new NameTransformer() {
                    @Override
                    public String transform(final String name) {
                        return prefix + name + suffix;
                    }
                    
                    @Override
                    public String reverse(final String transformed) {
                        if (transformed.startsWith(prefix)) {
                            final String str = transformed.substring(prefix.length());
                            if (str.endsWith(suffix)) {
                                return str.substring(0, str.length() - suffix.length());
                            }
                        }
                        return null;
                    }
                    
                    @Override
                    public String toString() {
                        return "[PreAndSuffixTransformer('" + prefix + "','" + suffix + "')]";
                    }
                };
            }
            return new NameTransformer() {
                @Override
                public String transform(final String name) {
                    return prefix + name;
                }
                
                @Override
                public String reverse(final String transformed) {
                    if (transformed.startsWith(prefix)) {
                        return transformed.substring(prefix.length());
                    }
                    return null;
                }
                
                @Override
                public String toString() {
                    return "[PrefixTransformer('" + prefix + "')]";
                }
            };
        }
        else {
            if (hasSuffix) {
                return new NameTransformer() {
                    @Override
                    public String transform(final String name) {
                        return name + suffix;
                    }
                    
                    @Override
                    public String reverse(final String transformed) {
                        if (transformed.endsWith(suffix)) {
                            return transformed.substring(0, transformed.length() - suffix.length());
                        }
                        return null;
                    }
                    
                    @Override
                    public String toString() {
                        return "[SuffixTransformer('" + suffix + "')]";
                    }
                };
            }
            return NameTransformer.NOP;
        }
    }
    
    public static NameTransformer chainedTransformer(final NameTransformer t1, final NameTransformer t2) {
        return new Chained(t1, t2);
    }
    
    public abstract String transform(final String p0);
    
    public abstract String reverse(final String p0);
    
    static {
        NOP = new NopTransformer();
    }
    
    protected static final class NopTransformer extends NameTransformer implements Serializable
    {
        private static final long serialVersionUID = 1L;
        
        @Override
        public String transform(final String name) {
            return name;
        }
        
        @Override
        public String reverse(final String transformed) {
            return transformed;
        }
    }
    
    public static class Chained extends NameTransformer implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected final NameTransformer _t1;
        protected final NameTransformer _t2;
        
        public Chained(final NameTransformer t1, final NameTransformer t2) {
            this._t1 = t1;
            this._t2 = t2;
        }
        
        @Override
        public String transform(final String name) {
            return this._t1.transform(this._t2.transform(name));
        }
        
        @Override
        public String reverse(String transformed) {
            transformed = this._t1.reverse(transformed);
            if (transformed != null) {
                transformed = this._t2.reverse(transformed);
            }
            return transformed;
        }
        
        @Override
        public String toString() {
            return "[ChainedTransformer(" + this._t1 + ", " + this._t2 + ")]";
        }
    }
}
