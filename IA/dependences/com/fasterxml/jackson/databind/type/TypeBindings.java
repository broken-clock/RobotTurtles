// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Collections;
import java.util.Arrays;
import java.lang.reflect.TypeVariable;
import java.util.List;
import com.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;

public class TypeBindings implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String[] NO_STRINGS;
    private static final JavaType[] NO_TYPES;
    private static final TypeBindings EMPTY;
    private final String[] _names;
    private final JavaType[] _types;
    private final String[] _unboundVariables;
    private final int _hashCode;
    
    private TypeBindings(final String[] names, final JavaType[] types, final String[] uvars) {
        this._names = ((names == null) ? TypeBindings.NO_STRINGS : names);
        this._types = ((types == null) ? TypeBindings.NO_TYPES : types);
        if (this._names.length != this._types.length) {
            throw new IllegalArgumentException("Mismatching names (" + this._names.length + "), types (" + this._types.length + ")");
        }
        int h = 1;
        for (int i = 0, len = this._types.length; i < len; ++i) {
            h += this._types[i].hashCode();
        }
        this._unboundVariables = uvars;
        this._hashCode = h;
    }
    
    public static TypeBindings emptyBindings() {
        return TypeBindings.EMPTY;
    }
    
    protected Object readResolve() {
        if (this._names == null || this._names.length == 0) {
            return TypeBindings.EMPTY;
        }
        return this;
    }
    
    public static TypeBindings create(final Class<?> erasedType, final List<JavaType> typeList) {
        final JavaType[] types = (typeList == null || typeList.isEmpty()) ? TypeBindings.NO_TYPES : typeList.toArray(new JavaType[typeList.size()]);
        return create(erasedType, types);
    }
    
    public static TypeBindings create(final Class<?> erasedType, JavaType[] types) {
        if (types == null) {
            types = TypeBindings.NO_TYPES;
        }
        else {
            switch (types.length) {
                case 1: {
                    return create(erasedType, types[0]);
                }
                case 2: {
                    return create(erasedType, types[0], types[1]);
                }
            }
        }
        final TypeVariable<?>[] vars = erasedType.getTypeParameters();
        String[] names;
        if (vars == null || vars.length == 0) {
            names = TypeBindings.NO_STRINGS;
        }
        else {
            final int len = vars.length;
            names = new String[len];
            for (int i = 0; i < len; ++i) {
                names[i] = vars[i].getName();
            }
        }
        if (names.length != types.length) {
            throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with " + types.length + " type parameter" + ((types.length == 1) ? "" : "s") + ": class expects " + names.length);
        }
        return new TypeBindings(names, types, null);
    }
    
    public static TypeBindings create(final Class<?> erasedType, final JavaType typeArg1) {
        final TypeVariable<?>[] vars = TypeParamStash.paramsFor1(erasedType);
        final int varLen = (vars == null) ? 0 : vars.length;
        if (varLen != 1) {
            throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with 1 type parameter: class expects " + varLen);
        }
        return new TypeBindings(new String[] { vars[0].getName() }, new JavaType[] { typeArg1 }, null);
    }
    
    public static TypeBindings create(final Class<?> erasedType, final JavaType typeArg1, final JavaType typeArg2) {
        final TypeVariable<?>[] vars = TypeParamStash.paramsFor2(erasedType);
        final int varLen = (vars == null) ? 0 : vars.length;
        if (varLen != 2) {
            throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with 2 type parameters: class expects " + varLen);
        }
        return new TypeBindings(new String[] { vars[0].getName(), vars[1].getName() }, new JavaType[] { typeArg1, typeArg2 }, null);
    }
    
    public static TypeBindings createIfNeeded(final Class<?> erasedType, final JavaType typeArg1) {
        final TypeVariable<?>[] vars = erasedType.getTypeParameters();
        final int varLen = (vars == null) ? 0 : vars.length;
        if (varLen == 0) {
            return TypeBindings.EMPTY;
        }
        if (varLen != 1) {
            throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with 1 type parameter: class expects " + varLen);
        }
        return new TypeBindings(new String[] { vars[0].getName() }, new JavaType[] { typeArg1 }, null);
    }
    
    public static TypeBindings createIfNeeded(final Class<?> erasedType, JavaType[] types) {
        final TypeVariable<?>[] vars = erasedType.getTypeParameters();
        if (vars == null || vars.length == 0) {
            return TypeBindings.EMPTY;
        }
        if (types == null) {
            types = TypeBindings.NO_TYPES;
        }
        final int len = vars.length;
        final String[] names = new String[len];
        for (int i = 0; i < len; ++i) {
            names[i] = vars[i].getName();
        }
        if (names.length != types.length) {
            throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with " + types.length + " type parameter" + ((types.length == 1) ? "" : "s") + ": class expects " + names.length);
        }
        return new TypeBindings(names, types, null);
    }
    
    public TypeBindings withUnboundVariable(final String name) {
        final int len = (this._unboundVariables == null) ? 0 : this._unboundVariables.length;
        final String[] names = (len == 0) ? new String[1] : Arrays.copyOf(this._unboundVariables, len + 1);
        names[len] = name;
        return new TypeBindings(this._names, this._types, names);
    }
    
    public JavaType findBoundType(final String name) {
        for (int i = 0, len = this._names.length; i < len; ++i) {
            if (name.equals(this._names[i])) {
                JavaType t = this._types[i];
                if (t instanceof ResolvedRecursiveType) {
                    final ResolvedRecursiveType rrt = (ResolvedRecursiveType)t;
                    final JavaType t2 = rrt.getSelfReferencedType();
                    if (t2 != null) {
                        t = t2;
                    }
                }
                return t;
            }
        }
        return null;
    }
    
    public boolean isEmpty() {
        return this._types.length == 0;
    }
    
    public int size() {
        return this._types.length;
    }
    
    public String getBoundName(final int index) {
        if (index < 0 || index >= this._names.length) {
            return null;
        }
        return this._names[index];
    }
    
    public JavaType getBoundType(final int index) {
        if (index < 0 || index >= this._types.length) {
            return null;
        }
        return this._types[index];
    }
    
    public List<JavaType> getTypeParameters() {
        if (this._types.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._types);
    }
    
    public boolean hasUnbound(final String name) {
        if (this._unboundVariables != null) {
            int i = this._unboundVariables.length;
            while (--i >= 0) {
                if (name.equals(this._unboundVariables[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Object asKey(final Class<?> rawBase) {
        return new AsKey(rawBase, this._types, this._hashCode);
    }
    
    @Override
    public String toString() {
        if (this._types.length == 0) {
            return "<>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append('<');
        for (int i = 0, len = this._types.length; i < len; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            final String sig = this._types[i].getGenericSignature();
            sb.append(sig);
        }
        sb.append('>');
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        return this._hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!ClassUtil.hasClass(o, this.getClass())) {
            return false;
        }
        final TypeBindings other = (TypeBindings)o;
        final int len = this._types.length;
        if (len != other.size()) {
            return false;
        }
        final JavaType[] otherTypes = other._types;
        for (int i = 0; i < len; ++i) {
            if (!otherTypes[i].equals(this._types[i])) {
                return false;
            }
        }
        return true;
    }
    
    protected JavaType[] typeParameterArray() {
        return this._types;
    }
    
    static {
        NO_STRINGS = new String[0];
        NO_TYPES = new JavaType[0];
        EMPTY = new TypeBindings(TypeBindings.NO_STRINGS, TypeBindings.NO_TYPES, null);
    }
    
    static class TypeParamStash
    {
        private static final TypeVariable<?>[] VARS_ABSTRACT_LIST;
        private static final TypeVariable<?>[] VARS_COLLECTION;
        private static final TypeVariable<?>[] VARS_ITERABLE;
        private static final TypeVariable<?>[] VARS_LIST;
        private static final TypeVariable<?>[] VARS_ARRAY_LIST;
        private static final TypeVariable<?>[] VARS_MAP;
        private static final TypeVariable<?>[] VARS_HASH_MAP;
        private static final TypeVariable<?>[] VARS_LINKED_HASH_MAP;
        
        public static TypeVariable<?>[] paramsFor1(final Class<?> erasedType) {
            if (erasedType == Collection.class) {
                return TypeParamStash.VARS_COLLECTION;
            }
            if (erasedType == List.class) {
                return TypeParamStash.VARS_LIST;
            }
            if (erasedType == ArrayList.class) {
                return TypeParamStash.VARS_ARRAY_LIST;
            }
            if (erasedType == AbstractList.class) {
                return TypeParamStash.VARS_ABSTRACT_LIST;
            }
            if (erasedType == Iterable.class) {
                return TypeParamStash.VARS_ITERABLE;
            }
            return erasedType.getTypeParameters();
        }
        
        public static TypeVariable<?>[] paramsFor2(final Class<?> erasedType) {
            if (erasedType == Map.class) {
                return TypeParamStash.VARS_MAP;
            }
            if (erasedType == HashMap.class) {
                return TypeParamStash.VARS_HASH_MAP;
            }
            if (erasedType == LinkedHashMap.class) {
                return TypeParamStash.VARS_LINKED_HASH_MAP;
            }
            return erasedType.getTypeParameters();
        }
        
        static {
            VARS_ABSTRACT_LIST = AbstractList.class.getTypeParameters();
            VARS_COLLECTION = Collection.class.getTypeParameters();
            VARS_ITERABLE = Iterable.class.getTypeParameters();
            VARS_LIST = List.class.getTypeParameters();
            VARS_ARRAY_LIST = ArrayList.class.getTypeParameters();
            VARS_MAP = Map.class.getTypeParameters();
            VARS_HASH_MAP = HashMap.class.getTypeParameters();
            VARS_LINKED_HASH_MAP = LinkedHashMap.class.getTypeParameters();
        }
    }
    
    static final class AsKey
    {
        private final Class<?> _raw;
        private final JavaType[] _params;
        private final int _hash;
        
        public AsKey(final Class<?> raw, final JavaType[] params, final int hash) {
            this._raw = raw;
            this._params = params;
            this._hash = hash;
        }
        
        @Override
        public int hashCode() {
            return this._hash;
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
            final AsKey other = (AsKey)o;
            if (this._hash == other._hash && this._raw == other._raw) {
                final JavaType[] otherParams = other._params;
                final int len = this._params.length;
                if (len == otherParams.length) {
                    for (int i = 0; i < len; ++i) {
                        if (!this._params[i].equals(otherParams[i])) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String toString() {
            return this._raw.getName() + "<>";
        }
    }
}
