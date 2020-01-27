// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform;

public class ClassFilterTransformer extends AbstractClassFilterTransformer
{
    private ClassFilter filter;
    
    public ClassFilterTransformer(final ClassFilter filter, final ClassTransformer pass) {
        super(pass);
        this.filter = filter;
    }
    
    protected boolean accept(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        return this.filter.accept(name.replace('/', '.'));
    }
}
