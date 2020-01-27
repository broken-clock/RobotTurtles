// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.TypedValue;

public interface ValueRef
{
    TypedValue getValue();
    
    void setValue(final Object p0);
    
    boolean isWritable();
    
    public static class NullValueRef implements ValueRef
    {
        static NullValueRef instance;
        
        @Override
        public TypedValue getValue() {
            return TypedValue.NULL;
        }
        
        @Override
        public void setValue(final Object newValue) {
            throw new SpelEvaluationException(0, SpelMessage.NOT_ASSIGNABLE, new Object[] { "null" });
        }
        
        @Override
        public boolean isWritable() {
            return false;
        }
        
        static {
            NullValueRef.instance = new NullValueRef();
        }
    }
    
    public static class TypedValueHolderValueRef implements ValueRef
    {
        private final TypedValue typedValue;
        private final SpelNodeImpl node;
        
        public TypedValueHolderValueRef(final TypedValue typedValue, final SpelNodeImpl node) {
            this.typedValue = typedValue;
            this.node = node;
        }
        
        @Override
        public TypedValue getValue() {
            return this.typedValue;
        }
        
        @Override
        public void setValue(final Object newValue) {
            throw new SpelEvaluationException(this.node.pos, SpelMessage.NOT_ASSIGNABLE, new Object[] { this.node.toStringAST() });
        }
        
        @Override
        public boolean isWritable() {
            return false;
        }
    }
}
