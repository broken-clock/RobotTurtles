// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

import java.util.List;

public interface EvaluationContext
{
    TypedValue getRootObject();
    
    List<ConstructorResolver> getConstructorResolvers();
    
    List<MethodResolver> getMethodResolvers();
    
    List<PropertyAccessor> getPropertyAccessors();
    
    TypeLocator getTypeLocator();
    
    TypeConverter getTypeConverter();
    
    TypeComparator getTypeComparator();
    
    OperatorOverloader getOperatorOverloader();
    
    BeanResolver getBeanResolver();
    
    void setVariable(final String p0, final Object p1);
    
    Object lookupVariable(final String p0);
}
