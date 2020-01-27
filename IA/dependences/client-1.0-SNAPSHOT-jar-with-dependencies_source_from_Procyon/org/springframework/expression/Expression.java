// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;

public interface Expression
{
    Object getValue() throws EvaluationException;
    
    Object getValue(final Object p0) throws EvaluationException;
    
     <T> T getValue(final Class<T> p0) throws EvaluationException;
    
     <T> T getValue(final Object p0, final Class<T> p1) throws EvaluationException;
    
    Object getValue(final EvaluationContext p0) throws EvaluationException;
    
    Object getValue(final EvaluationContext p0, final Object p1) throws EvaluationException;
    
     <T> T getValue(final EvaluationContext p0, final Class<T> p1) throws EvaluationException;
    
     <T> T getValue(final EvaluationContext p0, final Object p1, final Class<T> p2) throws EvaluationException;
    
    Class<?> getValueType() throws EvaluationException;
    
    Class<?> getValueType(final Object p0) throws EvaluationException;
    
    Class<?> getValueType(final EvaluationContext p0) throws EvaluationException;
    
    Class<?> getValueType(final EvaluationContext p0, final Object p1) throws EvaluationException;
    
    TypeDescriptor getValueTypeDescriptor() throws EvaluationException;
    
    TypeDescriptor getValueTypeDescriptor(final Object p0) throws EvaluationException;
    
    TypeDescriptor getValueTypeDescriptor(final EvaluationContext p0) throws EvaluationException;
    
    TypeDescriptor getValueTypeDescriptor(final EvaluationContext p0, final Object p1) throws EvaluationException;
    
    boolean isWritable(final EvaluationContext p0) throws EvaluationException;
    
    boolean isWritable(final EvaluationContext p0, final Object p1) throws EvaluationException;
    
    boolean isWritable(final Object p0) throws EvaluationException;
    
    void setValue(final EvaluationContext p0, final Object p1) throws EvaluationException;
    
    void setValue(final Object p0, final Object p1) throws EvaluationException;
    
    void setValue(final EvaluationContext p0, final Object p1, final Object p2) throws EvaluationException;
    
    String getExpressionString();
}
