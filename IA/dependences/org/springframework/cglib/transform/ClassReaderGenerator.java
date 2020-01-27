// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.ClassReader;
import org.springframework.cglib.core.ClassGenerator;

public class ClassReaderGenerator implements ClassGenerator
{
    private final ClassReader r;
    private final Attribute[] attrs;
    private final int flags;
    
    public ClassReaderGenerator(final ClassReader r, final int flags) {
        this(r, null, flags);
    }
    
    public ClassReaderGenerator(final ClassReader r, final Attribute[] attrs, final int flags) {
        this.r = r;
        this.attrs = ((attrs != null) ? attrs : new Attribute[0]);
        this.flags = flags;
    }
    
    public void generateClass(final ClassVisitor v) {
        this.r.accept(v, this.attrs, this.flags);
    }
}
