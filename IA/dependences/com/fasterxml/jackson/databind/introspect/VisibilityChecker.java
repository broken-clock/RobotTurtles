// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

public interface VisibilityChecker<T extends VisibilityChecker<T>>
{
    T with(final JsonAutoDetect p0);
    
    T withOverrides(final JsonAutoDetect.Value p0);
    
    T with(final JsonAutoDetect.Visibility p0);
    
    T withVisibility(final PropertyAccessor p0, final JsonAutoDetect.Visibility p1);
    
    T withGetterVisibility(final JsonAutoDetect.Visibility p0);
    
    T withIsGetterVisibility(final JsonAutoDetect.Visibility p0);
    
    T withSetterVisibility(final JsonAutoDetect.Visibility p0);
    
    T withCreatorVisibility(final JsonAutoDetect.Visibility p0);
    
    T withFieldVisibility(final JsonAutoDetect.Visibility p0);
    
    boolean isGetterVisible(final Method p0);
    
    boolean isGetterVisible(final AnnotatedMethod p0);
    
    boolean isIsGetterVisible(final Method p0);
    
    boolean isIsGetterVisible(final AnnotatedMethod p0);
    
    boolean isSetterVisible(final Method p0);
    
    boolean isSetterVisible(final AnnotatedMethod p0);
    
    boolean isCreatorVisible(final Member p0);
    
    boolean isCreatorVisible(final AnnotatedMember p0);
    
    boolean isFieldVisible(final Field p0);
    
    boolean isFieldVisible(final AnnotatedField p0);
    
    public static class Std implements VisibilityChecker<Std>, Serializable
    {
        private static final long serialVersionUID = 1L;
        protected static final Std DEFAULT;
        protected final JsonAutoDetect.Visibility _getterMinLevel;
        protected final JsonAutoDetect.Visibility _isGetterMinLevel;
        protected final JsonAutoDetect.Visibility _setterMinLevel;
        protected final JsonAutoDetect.Visibility _creatorMinLevel;
        protected final JsonAutoDetect.Visibility _fieldMinLevel;
        
        public static Std defaultInstance() {
            return Std.DEFAULT;
        }
        
        public Std(final JsonAutoDetect ann) {
            this._getterMinLevel = ann.getterVisibility();
            this._isGetterMinLevel = ann.isGetterVisibility();
            this._setterMinLevel = ann.setterVisibility();
            this._creatorMinLevel = ann.creatorVisibility();
            this._fieldMinLevel = ann.fieldVisibility();
        }
        
        public Std(final JsonAutoDetect.Visibility getter, final JsonAutoDetect.Visibility isGetter, final JsonAutoDetect.Visibility setter, final JsonAutoDetect.Visibility creator, final JsonAutoDetect.Visibility field) {
            this._getterMinLevel = getter;
            this._isGetterMinLevel = isGetter;
            this._setterMinLevel = setter;
            this._creatorMinLevel = creator;
            this._fieldMinLevel = field;
        }
        
        public Std(final JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                this._getterMinLevel = Std.DEFAULT._getterMinLevel;
                this._isGetterMinLevel = Std.DEFAULT._isGetterMinLevel;
                this._setterMinLevel = Std.DEFAULT._setterMinLevel;
                this._creatorMinLevel = Std.DEFAULT._creatorMinLevel;
                this._fieldMinLevel = Std.DEFAULT._fieldMinLevel;
            }
            else {
                this._getterMinLevel = v;
                this._isGetterMinLevel = v;
                this._setterMinLevel = v;
                this._creatorMinLevel = v;
                this._fieldMinLevel = v;
            }
        }
        
        public static Std construct(final JsonAutoDetect.Value vis) {
            return Std.DEFAULT.withOverrides(vis);
        }
        
        protected Std _with(final JsonAutoDetect.Visibility g, final JsonAutoDetect.Visibility isG, final JsonAutoDetect.Visibility s, final JsonAutoDetect.Visibility cr, final JsonAutoDetect.Visibility f) {
            if (g == this._getterMinLevel && isG == this._isGetterMinLevel && s == this._setterMinLevel && cr == this._creatorMinLevel && f == this._fieldMinLevel) {
                return this;
            }
            return new Std(g, isG, s, cr, f);
        }
        
        @Override
        public Std with(final JsonAutoDetect ann) {
            final Std curr = this;
            if (ann != null) {
                return this._with(this._defaultOrOverride(this._getterMinLevel, ann.getterVisibility()), this._defaultOrOverride(this._isGetterMinLevel, ann.isGetterVisibility()), this._defaultOrOverride(this._setterMinLevel, ann.setterVisibility()), this._defaultOrOverride(this._creatorMinLevel, ann.creatorVisibility()), this._defaultOrOverride(this._fieldMinLevel, ann.fieldVisibility()));
            }
            return curr;
        }
        
        @Override
        public Std withOverrides(final JsonAutoDetect.Value vis) {
            final Std curr = this;
            if (vis != null) {
                return this._with(this._defaultOrOverride(this._getterMinLevel, vis.getGetterVisibility()), this._defaultOrOverride(this._isGetterMinLevel, vis.getIsGetterVisibility()), this._defaultOrOverride(this._setterMinLevel, vis.getSetterVisibility()), this._defaultOrOverride(this._creatorMinLevel, vis.getCreatorVisibility()), this._defaultOrOverride(this._fieldMinLevel, vis.getFieldVisibility()));
            }
            return curr;
        }
        
        private JsonAutoDetect.Visibility _defaultOrOverride(final JsonAutoDetect.Visibility defaults, final JsonAutoDetect.Visibility override) {
            if (override == JsonAutoDetect.Visibility.DEFAULT) {
                return defaults;
            }
            return override;
        }
        
        @Override
        public Std with(final JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                return Std.DEFAULT;
            }
            return new Std(v);
        }
        
        @Override
        public Std withVisibility(final PropertyAccessor method, final JsonAutoDetect.Visibility v) {
            switch (method) {
                case GETTER: {
                    return this.withGetterVisibility(v);
                }
                case SETTER: {
                    return this.withSetterVisibility(v);
                }
                case CREATOR: {
                    return this.withCreatorVisibility(v);
                }
                case FIELD: {
                    return this.withFieldVisibility(v);
                }
                case IS_GETTER: {
                    return this.withIsGetterVisibility(v);
                }
                case ALL: {
                    return this.with(v);
                }
                default: {
                    return this;
                }
            }
        }
        
        @Override
        public Std withGetterVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = Std.DEFAULT._getterMinLevel;
            }
            if (this._getterMinLevel == v) {
                return this;
            }
            return new Std(v, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
        }
        
        @Override
        public Std withIsGetterVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = Std.DEFAULT._isGetterMinLevel;
            }
            if (this._isGetterMinLevel == v) {
                return this;
            }
            return new Std(this._getterMinLevel, v, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
        }
        
        @Override
        public Std withSetterVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = Std.DEFAULT._setterMinLevel;
            }
            if (this._setterMinLevel == v) {
                return this;
            }
            return new Std(this._getterMinLevel, this._isGetterMinLevel, v, this._creatorMinLevel, this._fieldMinLevel);
        }
        
        @Override
        public Std withCreatorVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = Std.DEFAULT._creatorMinLevel;
            }
            if (this._creatorMinLevel == v) {
                return this;
            }
            return new Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, v, this._fieldMinLevel);
        }
        
        @Override
        public Std withFieldVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = Std.DEFAULT._fieldMinLevel;
            }
            if (this._fieldMinLevel == v) {
                return this;
            }
            return new Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, v);
        }
        
        @Override
        public boolean isCreatorVisible(final Member m) {
            return this._creatorMinLevel.isVisible(m);
        }
        
        @Override
        public boolean isCreatorVisible(final AnnotatedMember m) {
            return this.isCreatorVisible(m.getMember());
        }
        
        @Override
        public boolean isFieldVisible(final Field f) {
            return this._fieldMinLevel.isVisible(f);
        }
        
        @Override
        public boolean isFieldVisible(final AnnotatedField f) {
            return this.isFieldVisible(f.getAnnotated());
        }
        
        @Override
        public boolean isGetterVisible(final Method m) {
            return this._getterMinLevel.isVisible(m);
        }
        
        @Override
        public boolean isGetterVisible(final AnnotatedMethod m) {
            return this.isGetterVisible(m.getAnnotated());
        }
        
        @Override
        public boolean isIsGetterVisible(final Method m) {
            return this._isGetterMinLevel.isVisible(m);
        }
        
        @Override
        public boolean isIsGetterVisible(final AnnotatedMethod m) {
            return this.isIsGetterVisible(m.getAnnotated());
        }
        
        @Override
        public boolean isSetterVisible(final Method m) {
            return this._setterMinLevel.isVisible(m);
        }
        
        @Override
        public boolean isSetterVisible(final AnnotatedMethod m) {
            return this.isSetterVisible(m.getAnnotated());
        }
        
        @Override
        public String toString() {
            return String.format("[Visibility: getter=%s,isGetter=%s,setter=%s,creator=%s,field=%s]", this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
        }
        
        static {
            DEFAULT = new Std(JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        }
    }
}
