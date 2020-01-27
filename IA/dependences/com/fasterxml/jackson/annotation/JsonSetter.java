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

@Target({ ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSetter {
    String value() default "";
    
    Nulls nulls() default Nulls.DEFAULT;
    
    Nulls contentNulls() default Nulls.DEFAULT;
    
    public static class Value implements JacksonAnnotationValue<JsonSetter>, Serializable
    {
        private static final long serialVersionUID = 1L;
        private final Nulls _nulls;
        private final Nulls _contentNulls;
        protected static final Value EMPTY;
        
        protected Value(final Nulls nulls, final Nulls contentNulls) {
            this._nulls = nulls;
            this._contentNulls = contentNulls;
        }
        
        @Override
        public Class<JsonSetter> valueFor() {
            return JsonSetter.class;
        }
        
        protected Object readResolve() {
            if (_empty(this._nulls, this._contentNulls)) {
                return Value.EMPTY;
            }
            return this;
        }
        
        public static Value from(final JsonSetter src) {
            if (src == null) {
                return Value.EMPTY;
            }
            return construct(src.nulls(), src.contentNulls());
        }
        
        public static Value construct(Nulls nulls, Nulls contentNulls) {
            if (nulls == null) {
                nulls = Nulls.DEFAULT;
            }
            if (contentNulls == null) {
                contentNulls = Nulls.DEFAULT;
            }
            if (_empty(nulls, contentNulls)) {
                return Value.EMPTY;
            }
            return new Value(nulls, contentNulls);
        }
        
        public static Value empty() {
            return Value.EMPTY;
        }
        
        public static Value merge(final Value base, final Value overrides) {
            return (base == null) ? overrides : base.withOverrides(overrides);
        }
        
        public static Value forValueNulls(final Nulls nulls) {
            return construct(nulls, Nulls.DEFAULT);
        }
        
        public static Value forValueNulls(final Nulls nulls, final Nulls contentNulls) {
            return construct(nulls, contentNulls);
        }
        
        public static Value forContentNulls(final Nulls nulls) {
            return construct(Nulls.DEFAULT, nulls);
        }
        
        public Value withOverrides(final Value overrides) {
            if (overrides == null || overrides == Value.EMPTY) {
                return this;
            }
            Nulls nulls = overrides._nulls;
            Nulls contentNulls = overrides._contentNulls;
            if (nulls == Nulls.DEFAULT) {
                nulls = this._nulls;
            }
            if (contentNulls == Nulls.DEFAULT) {
                contentNulls = this._contentNulls;
            }
            if (nulls == this._nulls && contentNulls == this._contentNulls) {
                return this;
            }
            return construct(nulls, contentNulls);
        }
        
        public Value withValueNulls(Nulls nulls) {
            if (nulls == null) {
                nulls = Nulls.DEFAULT;
            }
            if (nulls == this._nulls) {
                return this;
            }
            return construct(nulls, this._contentNulls);
        }
        
        public Value withValueNulls(Nulls valueNulls, Nulls contentNulls) {
            if (valueNulls == null) {
                valueNulls = Nulls.DEFAULT;
            }
            if (contentNulls == null) {
                contentNulls = Nulls.DEFAULT;
            }
            if (valueNulls == this._nulls && contentNulls == this._contentNulls) {
                return this;
            }
            return construct(valueNulls, contentNulls);
        }
        
        public Value withContentNulls(Nulls nulls) {
            if (nulls == null) {
                nulls = Nulls.DEFAULT;
            }
            if (nulls == this._contentNulls) {
                return this;
            }
            return construct(this._nulls, nulls);
        }
        
        public Nulls getValueNulls() {
            return this._nulls;
        }
        
        public Nulls getContentNulls() {
            return this._contentNulls;
        }
        
        public Nulls nonDefaultValueNulls() {
            return (this._nulls == Nulls.DEFAULT) ? null : this._nulls;
        }
        
        public Nulls nonDefaultContentNulls() {
            return (this._contentNulls == Nulls.DEFAULT) ? null : this._contentNulls;
        }
        
        @Override
        public String toString() {
            return String.format("JsonSetter.Value(valueNulls=%s,contentNulls=%s)", this._nulls, this._contentNulls);
        }
        
        @Override
        public int hashCode() {
            return this._nulls.ordinal() + (this._contentNulls.ordinal() << 2);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o.getClass() == this.getClass()) {
                final Value other = (Value)o;
                return other._nulls == this._nulls && other._contentNulls == this._contentNulls;
            }
            return false;
        }
        
        private static boolean _empty(final Nulls nulls, final Nulls contentNulls) {
            return nulls == Nulls.DEFAULT && contentNulls == Nulls.DEFAULT;
        }
        
        static {
            EMPTY = new Value(Nulls.DEFAULT, Nulls.DEFAULT);
        }
    }
}
