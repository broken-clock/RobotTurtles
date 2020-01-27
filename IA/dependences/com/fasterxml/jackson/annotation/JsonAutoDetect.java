// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Member;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAutoDetect {
    Visibility getterVisibility() default Visibility.DEFAULT;
    
    Visibility isGetterVisibility() default Visibility.DEFAULT;
    
    Visibility setterVisibility() default Visibility.DEFAULT;
    
    Visibility creatorVisibility() default Visibility.DEFAULT;
    
    Visibility fieldVisibility() default Visibility.DEFAULT;
    
    public enum Visibility
    {
        ANY, 
        NON_PRIVATE, 
        PROTECTED_AND_PUBLIC, 
        PUBLIC_ONLY, 
        NONE, 
        DEFAULT;
        
        public boolean isVisible(final Member m) {
            switch (this) {
                case ANY: {
                    return true;
                }
                case NONE: {
                    return false;
                }
                case NON_PRIVATE: {
                    return !Modifier.isPrivate(m.getModifiers());
                }
                case PROTECTED_AND_PUBLIC: {
                    if (Modifier.isProtected(m.getModifiers())) {
                        return true;
                    }
                    return Modifier.isPublic(m.getModifiers());
                }
                case PUBLIC_ONLY: {
                    return Modifier.isPublic(m.getModifiers());
                }
                default: {
                    return false;
                }
            }
        }
    }
    
    public static class Value implements JacksonAnnotationValue<JsonAutoDetect>, Serializable
    {
        private static final long serialVersionUID = 1L;
        private static final Visibility DEFAULT_FIELD_VISIBILITY;
        protected static final Value DEFAULT;
        protected static final Value NO_OVERRIDES;
        protected final Visibility _fieldVisibility;
        protected final Visibility _getterVisibility;
        protected final Visibility _isGetterVisibility;
        protected final Visibility _setterVisibility;
        protected final Visibility _creatorVisibility;
        
        private Value(final Visibility fields, final Visibility getters, final Visibility isGetters, final Visibility setters, final Visibility creators) {
            this._fieldVisibility = fields;
            this._getterVisibility = getters;
            this._isGetterVisibility = isGetters;
            this._setterVisibility = setters;
            this._creatorVisibility = creators;
        }
        
        public static Value defaultVisibility() {
            return Value.DEFAULT;
        }
        
        public static Value noOverrides() {
            return Value.NO_OVERRIDES;
        }
        
        public static Value from(final JsonAutoDetect src) {
            return construct(src.fieldVisibility(), src.getterVisibility(), src.isGetterVisibility(), src.setterVisibility(), src.creatorVisibility());
        }
        
        public static Value construct(final PropertyAccessor acc, final Visibility visibility) {
            Visibility fields = Visibility.DEFAULT;
            Visibility getters = Visibility.DEFAULT;
            Visibility isGetters = Visibility.DEFAULT;
            Visibility setters = Visibility.DEFAULT;
            Visibility creators = Visibility.DEFAULT;
            switch (acc) {
                case CREATOR: {
                    creators = visibility;
                    break;
                }
                case FIELD: {
                    fields = visibility;
                    break;
                }
                case GETTER: {
                    getters = visibility;
                    break;
                }
                case IS_GETTER: {
                    isGetters = visibility;
                }
                case SETTER: {
                    setters = visibility;
                    break;
                }
                case ALL: {
                    creators = visibility;
                    setters = visibility;
                    isGetters = visibility;
                    getters = visibility;
                    fields = visibility;
                    break;
                }
            }
            return construct(fields, getters, isGetters, setters, creators);
        }
        
        public static Value construct(final Visibility fields, final Visibility getters, final Visibility isGetters, final Visibility setters, final Visibility creators) {
            Value v = _predefined(fields, getters, isGetters, setters, creators);
            if (v == null) {
                v = new Value(fields, getters, isGetters, setters, creators);
            }
            return v;
        }
        
        public Value withFieldVisibility(final Visibility v) {
            return construct(v, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility);
        }
        
        public Value withGetterVisibility(final Visibility v) {
            return construct(this._fieldVisibility, v, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility);
        }
        
        public Value withIsGetterVisibility(final Visibility v) {
            return construct(this._fieldVisibility, this._getterVisibility, v, this._setterVisibility, this._creatorVisibility);
        }
        
        public Value withSetterVisibility(final Visibility v) {
            return construct(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, v, this._creatorVisibility);
        }
        
        public Value withCreatorVisibility(final Visibility v) {
            return construct(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, v);
        }
        
        public static Value merge(final Value base, final Value overrides) {
            return (base == null) ? overrides : base.withOverrides(overrides);
        }
        
        public Value withOverrides(final Value overrides) {
            if (overrides == null || overrides == Value.NO_OVERRIDES || overrides == this) {
                return this;
            }
            if (_equals(this, overrides)) {
                return this;
            }
            Visibility fields = overrides._fieldVisibility;
            if (fields == Visibility.DEFAULT) {
                fields = this._fieldVisibility;
            }
            Visibility getters = overrides._getterVisibility;
            if (getters == Visibility.DEFAULT) {
                getters = this._getterVisibility;
            }
            Visibility isGetters = overrides._isGetterVisibility;
            if (isGetters == Visibility.DEFAULT) {
                isGetters = this._isGetterVisibility;
            }
            Visibility setters = overrides._setterVisibility;
            if (setters == Visibility.DEFAULT) {
                setters = this._setterVisibility;
            }
            Visibility creators = overrides._creatorVisibility;
            if (creators == Visibility.DEFAULT) {
                creators = this._creatorVisibility;
            }
            return construct(fields, getters, isGetters, setters, creators);
        }
        
        @Override
        public Class<JsonAutoDetect> valueFor() {
            return JsonAutoDetect.class;
        }
        
        public Visibility getFieldVisibility() {
            return this._fieldVisibility;
        }
        
        public Visibility getGetterVisibility() {
            return this._getterVisibility;
        }
        
        public Visibility getIsGetterVisibility() {
            return this._isGetterVisibility;
        }
        
        public Visibility getSetterVisibility() {
            return this._setterVisibility;
        }
        
        public Visibility getCreatorVisibility() {
            return this._creatorVisibility;
        }
        
        protected Object readResolve() {
            final Value v = _predefined(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility);
            return (v == null) ? this : v;
        }
        
        @Override
        public String toString() {
            return String.format("JsonAutoDetect.Value(fields=%s,getters=%s,isGetters=%s,setters=%s,creators=%s)", this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility);
        }
        
        @Override
        public int hashCode() {
            return 1 + this._fieldVisibility.ordinal() ^ 3 * this._getterVisibility.ordinal() - 7 * this._isGetterVisibility.ordinal() + 11 * this._setterVisibility.ordinal() ^ 13 * this._creatorVisibility.ordinal();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o == this || (o != null && o.getClass() == this.getClass() && _equals(this, (Value)o));
        }
        
        private static Value _predefined(final Visibility fields, final Visibility getters, final Visibility isGetters, final Visibility setters, final Visibility creators) {
            if (fields == Value.DEFAULT_FIELD_VISIBILITY) {
                if (getters == Value.DEFAULT._getterVisibility && isGetters == Value.DEFAULT._isGetterVisibility && setters == Value.DEFAULT._setterVisibility && creators == Value.DEFAULT._creatorVisibility) {
                    return Value.DEFAULT;
                }
            }
            else if (fields == Visibility.DEFAULT && getters == Visibility.DEFAULT && isGetters == Visibility.DEFAULT && setters == Visibility.DEFAULT && creators == Visibility.DEFAULT) {
                return Value.NO_OVERRIDES;
            }
            return null;
        }
        
        private static boolean _equals(final Value a, final Value b) {
            return a._fieldVisibility == b._fieldVisibility && a._getterVisibility == b._getterVisibility && a._isGetterVisibility == b._isGetterVisibility && a._setterVisibility == b._setterVisibility && a._creatorVisibility == b._creatorVisibility;
        }
        
        static {
            DEFAULT_FIELD_VISIBILITY = Visibility.PUBLIC_ONLY;
            DEFAULT = new Value(Value.DEFAULT_FIELD_VISIBILITY, Visibility.PUBLIC_ONLY, Visibility.PUBLIC_ONLY, Visibility.ANY, Visibility.PUBLIC_ONLY);
            NO_OVERRIDES = new Value(Visibility.DEFAULT, Visibility.DEFAULT, Visibility.DEFAULT, Visibility.DEFAULT, Visibility.DEFAULT);
        }
    }
}
