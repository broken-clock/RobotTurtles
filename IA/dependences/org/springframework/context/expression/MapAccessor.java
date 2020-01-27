// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.expression;

import org.springframework.expression.TypedValue;
import org.springframework.expression.AccessException;
import java.util.Map;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;

public class MapAccessor implements PropertyAccessor
{
    @Override
    public boolean canRead(final EvaluationContext context, final Object target, final String name) throws AccessException {
        final Map<?, ?> map = (Map<?, ?>)target;
        return map.containsKey(name);
    }
    
    @Override
    public TypedValue read(final EvaluationContext context, final Object target, final String name) throws AccessException {
        final Map<?, ?> map = (Map<?, ?>)target;
        final Object value = map.get(name);
        if (value == null && !map.containsKey(name)) {
            throw new MapAccessException(name);
        }
        return new TypedValue(value);
    }
    
    @Override
    public boolean canWrite(final EvaluationContext context, final Object target, final String name) throws AccessException {
        return true;
    }
    
    @Override
    public void write(final EvaluationContext context, final Object target, final String name, final Object newValue) throws AccessException {
        final Map<Object, Object> map = (Map<Object, Object>)target;
        map.put(name, newValue);
    }
    
    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return (Class<?>[])new Class[] { Map.class };
    }
    
    private static class MapAccessException extends AccessException
    {
        private final String key;
        
        public MapAccessException(final String key) {
            super((String)null);
            this.key = key;
        }
        
        @Override
        public String getMessage() {
            return "Map does not contain a value for key '" + this.key + "'";
        }
    }
}
