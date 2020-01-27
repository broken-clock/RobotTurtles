// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonInclude {
    Include value() default Include.ALWAYS;
    
    Include content() default Include.ALWAYS;
    
    Class<?> valueFilter() default Void.class;
    
    Class<?> contentFilter() default Void.class;
    
    public enum Include
    {
        ALWAYS, 
        NON_NULL, 
        NON_ABSENT, 
        NON_EMPTY, 
        NON_DEFAULT, 
        CUSTOM, 
        USE_DEFAULTS;
    }
    
    public static class Value implements JacksonAnnotationValue<JsonInclude>, Serializable
    {
        private static final long serialVersionUID = 1L;
        protected static final Value EMPTY;
        protected final Include _valueInclusion;
        protected final Include _contentInclusion;
        protected final Class<?> _valueFilter;
        protected final Class<?> _contentFilter;
        
        public Value(final JsonInclude src) {
            this(src.value(), src.content(), src.valueFilter(), src.contentFilter());
        }
        
        protected Value(final Include vi, final Include ci, final Class<?> valueFilter, final Class<?> contentFilter) {
            this._valueInclusion = ((vi == null) ? Include.USE_DEFAULTS : vi);
            this._contentInclusion = ((ci == null) ? Include.USE_DEFAULTS : ci);
            this._valueFilter = ((valueFilter == Void.class) ? null : valueFilter);
            this._contentFilter = ((contentFilter == Void.class) ? null : contentFilter);
        }
        
        public static Value empty() {
            return Value.EMPTY;
        }
        
        public static Value merge(final Value base, final Value overrides) {
            return (base == null) ? overrides : base.withOverrides(overrides);
        }
        
        public static Value mergeAll(final Value... values) {
            Value result = null;
            for (final Value curr : values) {
                if (curr != null) {
                    result = ((result == null) ? curr : result.withOverrides(curr));
                }
            }
            return result;
        }
        
        protected Object readResolve() {
            if (this._valueInclusion == Include.USE_DEFAULTS && this._contentInclusion == Include.USE_DEFAULTS && this._valueFilter == null && this._contentFilter == null) {
                return Value.EMPTY;
            }
            return this;
        }
        
        public Value withOverrides(final Value overrides) {
            if (overrides == null || overrides == Value.EMPTY) {
                return this;
            }
            final Include vi = overrides._valueInclusion;
            final Include ci = overrides._contentInclusion;
            final Class<?> vf = overrides._valueFilter;
            final Class<?> cf = overrides._contentFilter;
            final boolean viDiff = vi != this._valueInclusion && vi != Include.USE_DEFAULTS;
            final boolean ciDiff = ci != this._contentInclusion && ci != Include.USE_DEFAULTS;
            final boolean filterDiff = vf != this._valueFilter || cf != this._valueFilter;
            if (viDiff) {
                if (ciDiff) {
                    return new Value(vi, ci, vf, cf);
                }
                return new Value(vi, this._contentInclusion, vf, cf);
            }
            else {
                if (ciDiff) {
                    return new Value(this._valueInclusion, ci, vf, cf);
                }
                if (filterDiff) {
                    return new Value(this._valueInclusion, this._contentInclusion, vf, cf);
                }
                return this;
            }
        }
        
        public static Value construct(final Include valueIncl, final Include contentIncl) {
            if ((valueIncl == Include.USE_DEFAULTS || valueIncl == null) && (contentIncl == Include.USE_DEFAULTS || contentIncl == null)) {
                return Value.EMPTY;
            }
            return new Value(valueIncl, contentIncl, null, null);
        }
        
        public static Value construct(final Include valueIncl, final Include contentIncl, Class<?> valueFilter, Class<?> contentFilter) {
            if (valueFilter == Void.class) {
                valueFilter = null;
            }
            if (contentFilter == Void.class) {
                contentFilter = null;
            }
            if ((valueIncl == Include.USE_DEFAULTS || valueIncl == null) && (contentIncl == Include.USE_DEFAULTS || contentIncl == null) && valueFilter == null && contentFilter == null) {
                return Value.EMPTY;
            }
            return new Value(valueIncl, contentIncl, valueFilter, contentFilter);
        }
        
        public static Value from(final JsonInclude src) {
            if (src == null) {
                return Value.EMPTY;
            }
            final Include vi = src.value();
            final Include ci = src.content();
            if (vi == Include.USE_DEFAULTS && ci == Include.USE_DEFAULTS) {
                return Value.EMPTY;
            }
            Class<?> vf = src.valueFilter();
            if (vf == Void.class) {
                vf = null;
            }
            Class<?> cf = src.contentFilter();
            if (cf == Void.class) {
                cf = null;
            }
            return new Value(vi, ci, vf, cf);
        }
        
        public Value withValueInclusion(final Include incl) {
            return (incl == this._valueInclusion) ? this : new Value(incl, this._contentInclusion, this._valueFilter, this._contentFilter);
        }
        
        public Value withValueFilter(Class<?> filter) {
            Include incl;
            if (filter == null || filter == Void.class) {
                incl = Include.USE_DEFAULTS;
                filter = null;
            }
            else {
                incl = Include.CUSTOM;
            }
            return construct(incl, this._contentInclusion, filter, this._contentFilter);
        }
        
        public Value withContentFilter(Class<?> filter) {
            Include incl;
            if (filter == null || filter == Void.class) {
                incl = Include.USE_DEFAULTS;
                filter = null;
            }
            else {
                incl = Include.CUSTOM;
            }
            return construct(this._valueInclusion, incl, this._valueFilter, filter);
        }
        
        public Value withContentInclusion(final Include incl) {
            return (incl == this._contentInclusion) ? this : new Value(this._valueInclusion, incl, this._valueFilter, this._contentFilter);
        }
        
        @Override
        public Class<JsonInclude> valueFor() {
            return JsonInclude.class;
        }
        
        public Include getValueInclusion() {
            return this._valueInclusion;
        }
        
        public Include getContentInclusion() {
            return this._contentInclusion;
        }
        
        public Class<?> getValueFilter() {
            return this._valueFilter;
        }
        
        public Class<?> getContentFilter() {
            return this._contentFilter;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(80);
            sb.append("JsonInclude.Value(value=").append(this._valueInclusion).append(",content=").append(this._contentInclusion);
            if (this._valueFilter != null) {
                sb.append(",valueFilter=").append(this._valueFilter.getName()).append(".class");
            }
            if (this._contentFilter != null) {
                sb.append(",contentFilter=").append(this._contentFilter.getName()).append(".class");
            }
            return sb.append(')').toString();
        }
        
        @Override
        public int hashCode() {
            return (this._valueInclusion.hashCode() << 2) + this._contentInclusion.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o.getClass() != this.getClass()) {
                return false;
            }
            final Value other = (Value)o;
            return other._valueInclusion == this._valueInclusion && other._contentInclusion == this._contentInclusion && other._valueFilter == this._valueFilter && other._contentFilter == this._contentFilter;
        }
        
        static {
            EMPTY = new Value(Include.USE_DEFAULTS, Include.USE_DEFAULTS, null, null);
        }
    }
}
