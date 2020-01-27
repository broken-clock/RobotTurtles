// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import org.springframework.asm.MethodVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.util.ClassUtils;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.core.type.ClassMetadata;
import org.springframework.asm.ClassVisitor;

class ClassMetadataReadingVisitor extends ClassVisitor implements ClassMetadata
{
    private String className;
    private boolean isInterface;
    private boolean isAbstract;
    private boolean isFinal;
    private String enclosingClassName;
    private boolean independentInnerClass;
    private String superClassName;
    private String[] interfaces;
    private Set<String> memberClassNames;
    
    public ClassMetadataReadingVisitor() {
        super(262144);
        this.memberClassNames = new LinkedHashSet<String>();
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String supername, final String[] interfaces) {
        this.className = ClassUtils.convertResourcePathToClassName(name);
        this.isInterface = ((access & 0x200) != 0x0);
        this.isAbstract = ((access & 0x400) != 0x0);
        this.isFinal = ((access & 0x10) != 0x0);
        if (supername != null) {
            this.superClassName = ClassUtils.convertResourcePathToClassName(supername);
        }
        this.interfaces = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            this.interfaces[i] = ClassUtils.convertResourcePathToClassName(interfaces[i]);
        }
    }
    
    @Override
    public void visitOuterClass(final String owner, final String name, final String desc) {
        this.enclosingClassName = ClassUtils.convertResourcePathToClassName(owner);
    }
    
    @Override
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        if (outerName != null) {
            final String fqName = ClassUtils.convertResourcePathToClassName(name);
            final String fqOuterName = ClassUtils.convertResourcePathToClassName(outerName);
            if (this.className.equals(fqName)) {
                this.enclosingClassName = fqOuterName;
                this.independentInnerClass = ((access & 0x8) != 0x0);
            }
            else if (this.className.equals(fqOuterName)) {
                this.memberClassNames.add(fqName);
            }
        }
    }
    
    @Override
    public void visitSource(final String source, final String debug) {
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        return new EmptyAnnotationVisitor();
    }
    
    @Override
    public void visitAttribute(final Attribute attr) {
    }
    
    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        return new EmptyFieldVisitor();
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        return new EmptyMethodVisitor();
    }
    
    @Override
    public void visitEnd() {
    }
    
    @Override
    public String getClassName() {
        return this.className;
    }
    
    @Override
    public boolean isInterface() {
        return this.isInterface;
    }
    
    @Override
    public boolean isAbstract() {
        return this.isAbstract;
    }
    
    @Override
    public boolean isConcrete() {
        return !this.isInterface && !this.isAbstract;
    }
    
    @Override
    public boolean isFinal() {
        return this.isFinal;
    }
    
    @Override
    public boolean isIndependent() {
        return this.enclosingClassName == null || this.independentInnerClass;
    }
    
    @Override
    public boolean hasEnclosingClass() {
        return this.enclosingClassName != null;
    }
    
    @Override
    public String getEnclosingClassName() {
        return this.enclosingClassName;
    }
    
    @Override
    public boolean hasSuperClass() {
        return this.superClassName != null;
    }
    
    @Override
    public String getSuperClassName() {
        return this.superClassName;
    }
    
    @Override
    public String[] getInterfaceNames() {
        return this.interfaces;
    }
    
    @Override
    public String[] getMemberClassNames() {
        return this.memberClassNames.toArray(new String[this.memberClassNames.size()]);
    }
}
