// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type;

import java.util.LinkedHashSet;
import java.lang.reflect.Modifier;
import org.springframework.util.Assert;

public class StandardClassMetadata implements ClassMetadata
{
    private final Class<?> introspectedClass;
    
    public StandardClassMetadata(final Class<?> introspectedClass) {
        Assert.notNull(introspectedClass, "Class must not be null");
        this.introspectedClass = introspectedClass;
    }
    
    public final Class<?> getIntrospectedClass() {
        return this.introspectedClass;
    }
    
    @Override
    public String getClassName() {
        return this.introspectedClass.getName();
    }
    
    @Override
    public boolean isInterface() {
        return this.introspectedClass.isInterface();
    }
    
    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.introspectedClass.getModifiers());
    }
    
    @Override
    public boolean isConcrete() {
        return !this.isInterface() && !this.isAbstract();
    }
    
    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedClass.getModifiers());
    }
    
    @Override
    public boolean isIndependent() {
        return !this.hasEnclosingClass() || (this.introspectedClass.getDeclaringClass() != null && Modifier.isStatic(this.introspectedClass.getModifiers()));
    }
    
    @Override
    public boolean hasEnclosingClass() {
        return this.introspectedClass.getEnclosingClass() != null;
    }
    
    @Override
    public String getEnclosingClassName() {
        final Class<?> enclosingClass = this.introspectedClass.getEnclosingClass();
        return (enclosingClass != null) ? enclosingClass.getName() : null;
    }
    
    @Override
    public boolean hasSuperClass() {
        return this.introspectedClass.getSuperclass() != null;
    }
    
    @Override
    public String getSuperClassName() {
        final Class<?> superClass = this.introspectedClass.getSuperclass();
        return (superClass != null) ? superClass.getName() : null;
    }
    
    @Override
    public String[] getInterfaceNames() {
        final Class<?>[] ifcs = this.introspectedClass.getInterfaces();
        final String[] ifcNames = new String[ifcs.length];
        for (int i = 0; i < ifcs.length; ++i) {
            ifcNames[i] = ifcs[i].getName();
        }
        return ifcNames;
    }
    
    @Override
    public String[] getMemberClassNames() {
        final LinkedHashSet<String> memberClassNames = new LinkedHashSet<String>();
        for (final Class<?> nestedClass : this.introspectedClass.getDeclaredClasses()) {
            memberClassNames.add(nestedClass.getName());
        }
        return memberClassNames.toArray(new String[memberClassNames.size()]);
    }
}
