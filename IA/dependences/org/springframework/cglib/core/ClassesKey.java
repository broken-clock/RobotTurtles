// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

public class ClassesKey
{
    private static final Key FACTORY;
    
    private ClassesKey() {
    }
    
    public static Object create(final Object[] array) {
        return ClassesKey.FACTORY.newInstance(array);
    }
    
    static {
        FACTORY = (Key)KeyFactory.create(Key.class, KeyFactory.OBJECT_BY_CLASS);
    }
    
    interface Key
    {
        Object newInstance(final Object[] p0);
    }
}
