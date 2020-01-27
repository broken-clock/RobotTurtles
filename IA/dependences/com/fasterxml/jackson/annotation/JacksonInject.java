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

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JacksonInject {
    String value() default "";
    
    OptBoolean useInput() default OptBoolean.DEFAULT;
    
    public static class Value implements JacksonAnnotationValue<JacksonInject>, Serializable
    {
        private static final long serialVersionUID = 1L;
        protected static final Value EMPTY;
        protected final Object _id;
        protected final Boolean _useInput;
        
        protected Value(final Object id, final Boolean useInput) {
            this._id = id;
            this._useInput = useInput;
        }
        
        @Override
        public Class<JacksonInject> valueFor() {
            return JacksonInject.class;
        }
        
        public static Value empty() {
            return Value.EMPTY;
        }
        
        public static Value construct(Object id, final Boolean useInput) {
            if ("".equals(id)) {
                id = null;
            }
            if (_empty(id, useInput)) {
                return Value.EMPTY;
            }
            return new Value(id, useInput);
        }
        
        public static Value from(final JacksonInject src) {
            if (src == null) {
                return Value.EMPTY;
            }
            return construct(src.value(), src.useInput().asBoolean());
        }
        
        public static Value forId(final Object id) {
            return construct(id, null);
        }
        
        public Value withId(final Object id) {
            if (id == null) {
                if (this._id == null) {
                    return this;
                }
            }
            else if (id.equals(this._id)) {
                return this;
            }
            return new Value(id, this._useInput);
        }
        
        public Value withUseInput(final Boolean useInput) {
            if (useInput == null) {
                if (this._useInput == null) {
                    return this;
                }
            }
            else if (useInput.equals(this._useInput)) {
                return this;
            }
            return new Value(this._id, useInput);
        }
        
        public Object getId() {
            return this._id;
        }
        
        public Boolean getUseInput() {
            return this._useInput;
        }
        
        public boolean hasId() {
            return this._id != null;
        }
        
        public boolean willUseInput(final boolean defaultSetting) {
            return (this._useInput == null) ? defaultSetting : this._useInput;
        }
        
        @Override
        public String toString() {
            return String.format("JacksonInject.Value(id=%s,useInput=%s)", this._id, this._useInput);
        }
        
        @Override
        public int hashCode() {
            int h = 1;
            if (this._id != null) {
                h += this._id.hashCode();
            }
            if (this._useInput != null) {
                h += this._useInput.hashCode();
            }
            return h;
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
                if (OptBoolean.equals(this._useInput, other._useInput)) {
                    if (this._id == null) {
                        return other._id == null;
                    }
                    return this._id.equals(other._id);
                }
            }
            return false;
        }
        
        private static boolean _empty(final Object id, final Boolean useInput) {
            return id == null && useInput == null;
        }
        
        static {
            EMPTY = new Value(null, null);
        }
    }
}
