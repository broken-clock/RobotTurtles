// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import java.util.Map;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonStreamContext;

abstract class NodeCursor extends JsonStreamContext
{
    protected final NodeCursor _parent;
    protected String _currentName;
    protected Object _currentValue;
    
    public NodeCursor(final int contextType, final NodeCursor p) {
        this._type = contextType;
        this._index = -1;
        this._parent = p;
    }
    
    @Override
    public final NodeCursor getParent() {
        return this._parent;
    }
    
    @Override
    public final String getCurrentName() {
        return this._currentName;
    }
    
    public void overrideCurrentName(final String name) {
        this._currentName = name;
    }
    
    @Override
    public Object getCurrentValue() {
        return this._currentValue;
    }
    
    @Override
    public void setCurrentValue(final Object v) {
        this._currentValue = v;
    }
    
    public abstract JsonToken nextToken();
    
    public abstract JsonToken nextValue();
    
    public abstract JsonToken endToken();
    
    public abstract JsonNode currentNode();
    
    public abstract boolean currentHasChildren();
    
    public final NodeCursor iterateChildren() {
        final JsonNode n = this.currentNode();
        if (n == null) {
            throw new IllegalStateException("No current node");
        }
        if (n.isArray()) {
            return new ArrayCursor(n, this);
        }
        if (n.isObject()) {
            return new ObjectCursor(n, this);
        }
        throw new IllegalStateException("Current node of type " + n.getClass().getName());
    }
    
    protected static final class RootCursor extends NodeCursor
    {
        protected JsonNode _node;
        protected boolean _done;
        
        public RootCursor(final JsonNode n, final NodeCursor p) {
            super(0, p);
            this._done = false;
            this._node = n;
        }
        
        @Override
        public void overrideCurrentName(final String name) {
        }
        
        @Override
        public JsonToken nextToken() {
            if (!this._done) {
                this._done = true;
                return this._node.asToken();
            }
            this._node = null;
            return null;
        }
        
        @Override
        public JsonToken nextValue() {
            return this.nextToken();
        }
        
        @Override
        public JsonToken endToken() {
            return null;
        }
        
        @Override
        public JsonNode currentNode() {
            return this._node;
        }
        
        @Override
        public boolean currentHasChildren() {
            return false;
        }
    }
    
    protected static final class ArrayCursor extends NodeCursor
    {
        protected Iterator<JsonNode> _contents;
        protected JsonNode _currentNode;
        
        public ArrayCursor(final JsonNode n, final NodeCursor p) {
            super(1, p);
            this._contents = n.elements();
        }
        
        @Override
        public JsonToken nextToken() {
            if (!this._contents.hasNext()) {
                this._currentNode = null;
                return null;
            }
            this._currentNode = this._contents.next();
            return this._currentNode.asToken();
        }
        
        @Override
        public JsonToken nextValue() {
            return this.nextToken();
        }
        
        @Override
        public JsonToken endToken() {
            return JsonToken.END_ARRAY;
        }
        
        @Override
        public JsonNode currentNode() {
            return this._currentNode;
        }
        
        @Override
        public boolean currentHasChildren() {
            return ((ContainerNode)this.currentNode()).size() > 0;
        }
    }
    
    protected static final class ObjectCursor extends NodeCursor
    {
        protected Iterator<Map.Entry<String, JsonNode>> _contents;
        protected Map.Entry<String, JsonNode> _current;
        protected boolean _needEntry;
        
        public ObjectCursor(final JsonNode n, final NodeCursor p) {
            super(2, p);
            this._contents = ((ObjectNode)n).fields();
            this._needEntry = true;
        }
        
        @Override
        public JsonToken nextToken() {
            if (!this._needEntry) {
                this._needEntry = true;
                return this._current.getValue().asToken();
            }
            if (!this._contents.hasNext()) {
                this._currentName = null;
                this._current = null;
                return null;
            }
            this._needEntry = false;
            this._current = this._contents.next();
            this._currentName = ((this._current == null) ? null : this._current.getKey());
            return JsonToken.FIELD_NAME;
        }
        
        @Override
        public JsonToken nextValue() {
            JsonToken t = this.nextToken();
            if (t == JsonToken.FIELD_NAME) {
                t = this.nextToken();
            }
            return t;
        }
        
        @Override
        public JsonToken endToken() {
            return JsonToken.END_OBJECT;
        }
        
        @Override
        public JsonNode currentNode() {
            return (this._current == null) ? null : this._current.getValue();
        }
        
        @Override
        public boolean currentHasChildren() {
            return ((ContainerNode)this.currentNode()).size() > 0;
        }
    }
}
