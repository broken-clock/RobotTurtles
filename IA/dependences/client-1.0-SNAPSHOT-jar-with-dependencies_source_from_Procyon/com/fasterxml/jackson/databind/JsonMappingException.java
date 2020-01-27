// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import java.io.Serializable;
import java.util.Iterator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonLocation;
import java.io.Closeable;
import java.util.LinkedList;
import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonMappingException extends JsonProcessingException
{
    private static final long serialVersionUID = 1L;
    static final int MAX_REFS_TO_LIST = 1000;
    protected LinkedList<Reference> _path;
    protected transient Closeable _processor;
    
    @Deprecated
    public JsonMappingException(final String msg) {
        super(msg);
    }
    
    @Deprecated
    public JsonMappingException(final String msg, final Throwable rootCause) {
        super(msg, rootCause);
    }
    
    @Deprecated
    public JsonMappingException(final String msg, final JsonLocation loc) {
        super(msg, loc);
    }
    
    @Deprecated
    public JsonMappingException(final String msg, final JsonLocation loc, final Throwable rootCause) {
        super(msg, loc, rootCause);
    }
    
    public JsonMappingException(final Closeable processor, final String msg) {
        super(msg);
        this._processor = processor;
        if (processor instanceof JsonParser) {
            this._location = ((JsonParser)processor).getTokenLocation();
        }
    }
    
    public JsonMappingException(final Closeable processor, final String msg, final Throwable problem) {
        super(msg, problem);
        this._processor = processor;
        if (processor instanceof JsonParser) {
            this._location = ((JsonParser)processor).getTokenLocation();
        }
    }
    
    public JsonMappingException(final Closeable processor, final String msg, final JsonLocation loc) {
        super(msg, loc);
        this._processor = processor;
    }
    
    public static JsonMappingException from(final JsonParser p, final String msg) {
        return new JsonMappingException(p, msg);
    }
    
    public static JsonMappingException from(final JsonParser p, final String msg, final Throwable problem) {
        return new JsonMappingException(p, msg, problem);
    }
    
    public static JsonMappingException from(final JsonGenerator g, final String msg) {
        return new JsonMappingException(g, msg, (Throwable)null);
    }
    
    public static JsonMappingException from(final JsonGenerator g, final String msg, final Throwable problem) {
        return new JsonMappingException(g, msg, problem);
    }
    
    public static JsonMappingException from(final DeserializationContext ctxt, final String msg) {
        return new JsonMappingException(ctxt.getParser(), msg);
    }
    
    public static JsonMappingException from(final DeserializationContext ctxt, final String msg, final Throwable t) {
        return new JsonMappingException(ctxt.getParser(), msg, t);
    }
    
    public static JsonMappingException from(final SerializerProvider ctxt, final String msg) {
        return new JsonMappingException(ctxt.getGenerator(), msg);
    }
    
    public static JsonMappingException from(final SerializerProvider ctxt, final String msg, final Throwable problem) {
        return new JsonMappingException(ctxt.getGenerator(), msg, problem);
    }
    
    public static JsonMappingException fromUnexpectedIOE(final IOException src) {
        return new JsonMappingException(null, String.format("Unexpected IOException (of type %s): %s", src.getClass().getName(), ClassUtil.exceptionMessage(src)));
    }
    
    public static JsonMappingException wrapWithPath(final Throwable src, final Object refFrom, final String refFieldName) {
        return wrapWithPath(src, new Reference(refFrom, refFieldName));
    }
    
    public static JsonMappingException wrapWithPath(final Throwable src, final Object refFrom, final int index) {
        return wrapWithPath(src, new Reference(refFrom, index));
    }
    
    public static JsonMappingException wrapWithPath(final Throwable src, final Reference ref) {
        JsonMappingException jme;
        if (src instanceof JsonMappingException) {
            jme = (JsonMappingException)src;
        }
        else {
            String msg = ClassUtil.exceptionMessage(src);
            if (msg == null || msg.length() == 0) {
                msg = "(was " + src.getClass().getName() + ")";
            }
            Closeable proc = null;
            if (src instanceof JsonProcessingException) {
                final Object proc2 = ((JsonProcessingException)src).getProcessor();
                if (proc2 instanceof Closeable) {
                    proc = (Closeable)proc2;
                }
            }
            jme = new JsonMappingException(proc, msg, src);
        }
        jme.prependPath(ref);
        return jme;
    }
    
    public List<Reference> getPath() {
        if (this._path == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends Reference>)this._path);
    }
    
    public String getPathReference() {
        return this.getPathReference(new StringBuilder()).toString();
    }
    
    public StringBuilder getPathReference(final StringBuilder sb) {
        this._appendPathDesc(sb);
        return sb;
    }
    
    public void prependPath(final Object referrer, final String fieldName) {
        final Reference ref = new Reference(referrer, fieldName);
        this.prependPath(ref);
    }
    
    public void prependPath(final Object referrer, final int index) {
        final Reference ref = new Reference(referrer, index);
        this.prependPath(ref);
    }
    
    public void prependPath(final Reference r) {
        if (this._path == null) {
            this._path = new LinkedList<Reference>();
        }
        if (this._path.size() < 1000) {
            this._path.addFirst(r);
        }
    }
    
    @JsonIgnore
    @Override
    public Object getProcessor() {
        return this._processor;
    }
    
    @Override
    public String getLocalizedMessage() {
        return this._buildMessage();
    }
    
    @Override
    public String getMessage() {
        return this._buildMessage();
    }
    
    protected String _buildMessage() {
        final String msg = super.getMessage();
        if (this._path == null) {
            return msg;
        }
        StringBuilder sb = (msg == null) ? new StringBuilder() : new StringBuilder(msg);
        sb.append(" (through reference chain: ");
        sb = this.getPathReference(sb);
        sb.append(')');
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": " + this.getMessage();
    }
    
    protected void _appendPathDesc(final StringBuilder sb) {
        if (this._path == null) {
            return;
        }
        final Iterator<Reference> it = this._path.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            if (it.hasNext()) {
                sb.append("->");
            }
        }
    }
    
    public static class Reference implements Serializable
    {
        private static final long serialVersionUID = 2L;
        protected transient Object _from;
        protected String _fieldName;
        protected int _index;
        protected String _desc;
        
        protected Reference() {
            this._index = -1;
        }
        
        public Reference(final Object from) {
            this._index = -1;
            this._from = from;
        }
        
        public Reference(final Object from, final String fieldName) {
            this._index = -1;
            this._from = from;
            if (fieldName == null) {
                throw new NullPointerException("Cannot pass null fieldName");
            }
            this._fieldName = fieldName;
        }
        
        public Reference(final Object from, final int index) {
            this._index = -1;
            this._from = from;
            this._index = index;
        }
        
        void setFieldName(final String n) {
            this._fieldName = n;
        }
        
        void setIndex(final int ix) {
            this._index = ix;
        }
        
        void setDescription(final String d) {
            this._desc = d;
        }
        
        @JsonIgnore
        public Object getFrom() {
            return this._from;
        }
        
        public String getFieldName() {
            return this._fieldName;
        }
        
        public int getIndex() {
            return this._index;
        }
        
        public String getDescription() {
            if (this._desc == null) {
                final StringBuilder sb = new StringBuilder();
                if (this._from == null) {
                    sb.append("UNKNOWN");
                }
                else {
                    Class<?> cls;
                    int arrays;
                    for (cls = (Class<?>)((this._from instanceof Class) ? ((Class)this._from) : this._from.getClass()), arrays = 0; cls.isArray(); cls = cls.getComponentType(), ++arrays) {}
                    sb.append(cls.getName());
                    while (--arrays >= 0) {
                        sb.append("[]");
                    }
                }
                sb.append('[');
                if (this._fieldName != null) {
                    sb.append('\"');
                    sb.append(this._fieldName);
                    sb.append('\"');
                }
                else if (this._index >= 0) {
                    sb.append(this._index);
                }
                else {
                    sb.append('?');
                }
                sb.append(']');
                this._desc = sb.toString();
            }
            return this._desc;
        }
        
        @Override
        public String toString() {
            return this.getDescription();
        }
        
        Object writeReplace() {
            this.getDescription();
            return this;
        }
    }
}
