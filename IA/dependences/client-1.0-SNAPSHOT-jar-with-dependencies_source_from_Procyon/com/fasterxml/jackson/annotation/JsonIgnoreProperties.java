// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.annotation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set;
import java.io.Serializable;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIgnoreProperties {
    String[] value() default {};
    
    boolean ignoreUnknown() default false;
    
    boolean allowGetters() default false;
    
    boolean allowSetters() default false;
    
    public static class Value implements JacksonAnnotationValue<JsonIgnoreProperties>, Serializable
    {
        private static final long serialVersionUID = 1L;
        protected static final Value EMPTY;
        protected final Set<String> _ignored;
        protected final boolean _ignoreUnknown;
        protected final boolean _allowGetters;
        protected final boolean _allowSetters;
        protected final boolean _merge;
        
        protected Value(final Set<String> ignored, final boolean ignoreUnknown, final boolean allowGetters, final boolean allowSetters, final boolean merge) {
            if (ignored == null) {
                this._ignored = Collections.emptySet();
            }
            else {
                this._ignored = ignored;
            }
            this._ignoreUnknown = ignoreUnknown;
            this._allowGetters = allowGetters;
            this._allowSetters = allowSetters;
            this._merge = merge;
        }
        
        public static Value from(final JsonIgnoreProperties src) {
            if (src == null) {
                return Value.EMPTY;
            }
            return construct(_asSet(src.value()), src.ignoreUnknown(), src.allowGetters(), src.allowSetters(), false);
        }
        
        public static Value construct(final Set<String> ignored, final boolean ignoreUnknown, final boolean allowGetters, final boolean allowSetters, final boolean merge) {
            if (_empty(ignored, ignoreUnknown, allowGetters, allowSetters, merge)) {
                return Value.EMPTY;
            }
            return new Value(ignored, ignoreUnknown, allowGetters, allowSetters, merge);
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
        
        public static Value forIgnoredProperties(final Set<String> propNames) {
            return Value.EMPTY.withIgnored(propNames);
        }
        
        public static Value forIgnoredProperties(final String... propNames) {
            if (propNames.length == 0) {
                return Value.EMPTY;
            }
            return Value.EMPTY.withIgnored(_asSet(propNames));
        }
        
        public static Value forIgnoreUnknown(final boolean state) {
            return state ? Value.EMPTY.withIgnoreUnknown() : Value.EMPTY.withoutIgnoreUnknown();
        }
        
        public Value withOverrides(final Value overrides) {
            if (overrides == null || overrides == Value.EMPTY) {
                return this;
            }
            if (!overrides._merge) {
                return overrides;
            }
            if (_equals(this, overrides)) {
                return this;
            }
            final Set<String> ignored = _merge(this._ignored, overrides._ignored);
            final boolean ignoreUnknown = this._ignoreUnknown || overrides._ignoreUnknown;
            final boolean allowGetters = this._allowGetters || overrides._allowGetters;
            final boolean allowSetters = this._allowSetters || overrides._allowSetters;
            return construct(ignored, ignoreUnknown, allowGetters, allowSetters, true);
        }
        
        public Value withIgnored(final Set<String> ignored) {
            return construct(ignored, this._ignoreUnknown, this._allowGetters, this._allowSetters, this._merge);
        }
        
        public Value withIgnored(final String... ignored) {
            return construct(_asSet(ignored), this._ignoreUnknown, this._allowGetters, this._allowSetters, this._merge);
        }
        
        public Value withoutIgnored() {
            return construct(null, this._ignoreUnknown, this._allowGetters, this._allowSetters, this._merge);
        }
        
        public Value withIgnoreUnknown() {
            return this._ignoreUnknown ? this : construct(this._ignored, true, this._allowGetters, this._allowSetters, this._merge);
        }
        
        public Value withoutIgnoreUnknown() {
            return this._ignoreUnknown ? construct(this._ignored, false, this._allowGetters, this._allowSetters, this._merge) : this;
        }
        
        public Value withAllowGetters() {
            return this._allowGetters ? this : construct(this._ignored, this._ignoreUnknown, true, this._allowSetters, this._merge);
        }
        
        public Value withoutAllowGetters() {
            return this._allowGetters ? construct(this._ignored, this._ignoreUnknown, false, this._allowSetters, this._merge) : this;
        }
        
        public Value withAllowSetters() {
            return this._allowSetters ? this : construct(this._ignored, this._ignoreUnknown, this._allowGetters, true, this._merge);
        }
        
        public Value withoutAllowSetters() {
            return this._allowSetters ? construct(this._ignored, this._ignoreUnknown, this._allowGetters, false, this._merge) : this;
        }
        
        public Value withMerge() {
            return this._merge ? this : construct(this._ignored, this._ignoreUnknown, this._allowGetters, this._allowSetters, true);
        }
        
        public Value withoutMerge() {
            return this._merge ? construct(this._ignored, this._ignoreUnknown, this._allowGetters, this._allowSetters, false) : this;
        }
        
        @Override
        public Class<JsonIgnoreProperties> valueFor() {
            return JsonIgnoreProperties.class;
        }
        
        protected Object readResolve() {
            if (_empty(this._ignored, this._ignoreUnknown, this._allowGetters, this._allowSetters, this._merge)) {
                return Value.EMPTY;
            }
            return this;
        }
        
        public Set<String> getIgnored() {
            return this._ignored;
        }
        
        public Set<String> findIgnoredForSerialization() {
            if (this._allowGetters) {
                return Collections.emptySet();
            }
            return this._ignored;
        }
        
        public Set<String> findIgnoredForDeserialization() {
            if (this._allowSetters) {
                return Collections.emptySet();
            }
            return this._ignored;
        }
        
        public boolean getIgnoreUnknown() {
            return this._ignoreUnknown;
        }
        
        public boolean getAllowGetters() {
            return this._allowGetters;
        }
        
        public boolean getAllowSetters() {
            return this._allowSetters;
        }
        
        public boolean getMerge() {
            return this._merge;
        }
        
        @Override
        public String toString() {
            return String.format("JsonIgnoreProperties.Value(ignored=%s,ignoreUnknown=%s,allowGetters=%s,allowSetters=%s,merge=%s)", this._ignored, this._ignoreUnknown, this._allowGetters, this._allowSetters, this._merge);
        }
        
        @Override
        public int hashCode() {
            return this._ignored.size() + (this._ignoreUnknown ? 1 : -3) + (this._allowGetters ? 3 : -7) + (this._allowSetters ? 7 : -11) + (this._merge ? 11 : -13);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o == this || (o != null && o.getClass() == this.getClass() && _equals(this, (Value)o));
        }
        
        private static boolean _equals(final Value a, final Value b) {
            return a._ignoreUnknown == b._ignoreUnknown && a._merge == b._merge && a._allowGetters == b._allowGetters && a._allowSetters == b._allowSetters && a._ignored.equals(b._ignored);
        }
        
        private static Set<String> _asSet(final String[] v) {
            if (v == null || v.length == 0) {
                return Collections.emptySet();
            }
            final Set<String> s = new HashSet<String>(v.length);
            for (final String str : v) {
                s.add(str);
            }
            return s;
        }
        
        private static Set<String> _merge(final Set<String> s1, final Set<String> s2) {
            if (s1.isEmpty()) {
                return s2;
            }
            if (s2.isEmpty()) {
                return s1;
            }
            final HashSet<String> result = new HashSet<String>(s1.size() + s2.size());
            result.addAll((Collection<?>)s1);
            result.addAll((Collection<?>)s2);
            return result;
        }
        
        private static boolean _empty(final Set<String> ignored, final boolean ignoreUnknown, final boolean allowGetters, final boolean allowSetters, final boolean merge) {
            return ignoreUnknown == Value.EMPTY._ignoreUnknown && allowGetters == Value.EMPTY._allowGetters && allowSetters == Value.EMPTY._allowSetters && merge == Value.EMPTY._merge && (ignored == null || ignored.size() == 0);
        }
        
        static {
            EMPTY = new Value(Collections.emptySet(), false, false, false, true);
        }
    }
}
