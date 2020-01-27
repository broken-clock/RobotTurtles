// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Constructor;

public class FastConstructor extends FastMember
{
    FastConstructor(final FastClass fc, final Constructor constructor) {
        super(fc, constructor, fc.getIndex(constructor.getParameterTypes()));
    }
    
    public Class[] getParameterTypes() {
        return ((Constructor)this.member).getParameterTypes();
    }
    
    public Class[] getExceptionTypes() {
        return ((Constructor)this.member).getExceptionTypes();
    }
    
    public Object newInstance() throws InvocationTargetException {
        return this.fc.newInstance(this.index, null);
    }
    
    public Object newInstance(final Object[] args) throws InvocationTargetException {
        return this.fc.newInstance(this.index, args);
    }
    
    public Constructor getJavaConstructor() {
        return (Constructor)this.member;
    }
}
