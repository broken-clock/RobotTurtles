// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import java.util.StringTokenizer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;

public class TypeParser implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final TypeFactory _factory;
    
    public TypeParser(final TypeFactory f) {
        this._factory = f;
    }
    
    public TypeParser withFactory(final TypeFactory f) {
        return (f == this._factory) ? this : new TypeParser(f);
    }
    
    public JavaType parse(final String canonical) throws IllegalArgumentException {
        final MyTokenizer tokens = new MyTokenizer(canonical.trim());
        final JavaType type = this.parseType(tokens);
        if (tokens.hasMoreTokens()) {
            throw this._problem(tokens, "Unexpected tokens after complete type");
        }
        return type;
    }
    
    protected JavaType parseType(final MyTokenizer tokens) throws IllegalArgumentException {
        if (!tokens.hasMoreTokens()) {
            throw this._problem(tokens, "Unexpected end-of-string");
        }
        final Class<?> base = this.findClass(tokens.nextToken(), tokens);
        if (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken();
            if ("<".equals(token)) {
                final List<JavaType> parameterTypes = this.parseTypes(tokens);
                final TypeBindings b = TypeBindings.create(base, parameterTypes);
                return this._factory._fromClass(null, base, b);
            }
            tokens.pushBack(token);
        }
        return this._factory._fromClass(null, base, TypeBindings.emptyBindings());
    }
    
    protected List<JavaType> parseTypes(final MyTokenizer tokens) throws IllegalArgumentException {
        final ArrayList<JavaType> types = new ArrayList<JavaType>();
        while (tokens.hasMoreTokens()) {
            types.add(this.parseType(tokens));
            if (!tokens.hasMoreTokens()) {
                break;
            }
            final String token = tokens.nextToken();
            if (">".equals(token)) {
                return types;
            }
            if (!",".equals(token)) {
                throw this._problem(tokens, "Unexpected token '" + token + "', expected ',' or '>')");
            }
        }
        throw this._problem(tokens, "Unexpected end-of-string");
    }
    
    protected Class<?> findClass(final String className, final MyTokenizer tokens) {
        try {
            return this._factory.findClass(className);
        }
        catch (Exception e) {
            ClassUtil.throwIfRTE(e);
            throw this._problem(tokens, "Cannot locate class '" + className + "', problem: " + e.getMessage());
        }
    }
    
    protected IllegalArgumentException _problem(final MyTokenizer tokens, final String msg) {
        return new IllegalArgumentException(String.format("Failed to parse type '%s' (remaining: '%s'): %s", tokens.getAllInput(), tokens.getRemainingInput(), msg));
    }
    
    static final class MyTokenizer extends StringTokenizer
    {
        protected final String _input;
        protected int _index;
        protected String _pushbackToken;
        
        public MyTokenizer(final String str) {
            super(str, "<,>", true);
            this._input = str;
        }
        
        @Override
        public boolean hasMoreTokens() {
            return this._pushbackToken != null || super.hasMoreTokens();
        }
        
        @Override
        public String nextToken() {
            String token;
            if (this._pushbackToken != null) {
                token = this._pushbackToken;
                this._pushbackToken = null;
            }
            else {
                token = super.nextToken();
                this._index += token.length();
                token = token.trim();
            }
            return token;
        }
        
        public void pushBack(final String token) {
            this._pushbackToken = token;
        }
        
        public String getAllInput() {
            return this._input;
        }
        
        public String getRemainingInput() {
            return this._input.substring(this._index);
        }
    }
}
