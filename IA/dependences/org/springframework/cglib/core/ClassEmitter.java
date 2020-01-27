// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

import org.springframework.asm.FieldVisitor;
import org.springframework.asm.Type;
import java.util.HashMap;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import java.util.Map;
import org.springframework.cglib.transform.ClassTransformer;

public class ClassEmitter extends ClassTransformer
{
    private ClassInfo classInfo;
    private Map fieldInfo;
    private static int hookCounter;
    private MethodVisitor rawStaticInit;
    private CodeEmitter staticInit;
    private CodeEmitter staticHook;
    private Signature staticHookSig;
    
    public ClassEmitter(final ClassVisitor cv) {
        this.setTarget(cv);
    }
    
    public ClassEmitter() {
        super(262144);
    }
    
    public void setTarget(final ClassVisitor cv) {
        this.cv = cv;
        this.fieldInfo = new HashMap();
        final CodeEmitter codeEmitter = null;
        this.staticHook = codeEmitter;
        this.staticInit = codeEmitter;
        this.staticHookSig = null;
    }
    
    private static synchronized int getNextHook() {
        return ++ClassEmitter.hookCounter;
    }
    
    public ClassInfo getClassInfo() {
        return this.classInfo;
    }
    
    public void begin_class(final int version, final int access, final String className, final Type superType, final Type[] interfaces, final String source) {
        final Type classType = Type.getType("L" + className.replace('.', '/') + ";");
        this.classInfo = new ClassInfo() {
            public Type getType() {
                return classType;
            }
            
            public Type getSuperType() {
                return (superType != null) ? superType : Constants.TYPE_OBJECT;
            }
            
            public Type[] getInterfaces() {
                return interfaces;
            }
            
            public int getModifiers() {
                return access;
            }
        };
        this.cv.visit(version, access, this.classInfo.getType().getInternalName(), null, this.classInfo.getSuperType().getInternalName(), TypeUtils.toInternalNames(interfaces));
        if (source != null) {
            this.cv.visitSource(source, null);
        }
        this.init();
    }
    
    public CodeEmitter getStaticHook() {
        if (TypeUtils.isInterface(this.getAccess())) {
            throw new IllegalStateException("static hook is invalid for this class");
        }
        if (this.staticHook == null) {
            this.staticHookSig = new Signature("CGLIB$STATICHOOK" + getNextHook(), "()V");
            this.staticHook = this.begin_method(8, this.staticHookSig, null);
            if (this.staticInit != null) {
                this.staticInit.invoke_static_this(this.staticHookSig);
            }
        }
        return this.staticHook;
    }
    
    protected void init() {
    }
    
    public int getAccess() {
        return this.classInfo.getModifiers();
    }
    
    public Type getClassType() {
        return this.classInfo.getType();
    }
    
    public Type getSuperType() {
        return this.classInfo.getSuperType();
    }
    
    public void end_class() {
        if (this.staticHook != null && this.staticInit == null) {
            this.begin_static();
        }
        if (this.staticInit != null) {
            this.staticHook.return_value();
            this.staticHook.end_method();
            this.rawStaticInit.visitInsn(177);
            this.rawStaticInit.visitMaxs(0, 0);
            final CodeEmitter codeEmitter = null;
            this.staticHook = codeEmitter;
            this.staticInit = codeEmitter;
            this.staticHookSig = null;
        }
        this.cv.visitEnd();
    }
    
    public CodeEmitter begin_method(final int access, final Signature sig, final Type[] exceptions) {
        if (this.classInfo == null) {
            throw new IllegalStateException("classInfo is null! " + this);
        }
        final MethodVisitor v = this.cv.visitMethod(access, sig.getName(), sig.getDescriptor(), null, TypeUtils.toInternalNames(exceptions));
        if (sig.equals(Constants.SIG_STATIC) && !TypeUtils.isInterface(this.getAccess())) {
            this.rawStaticInit = v;
            final MethodVisitor wrapped = new MethodVisitor(262144, v) {
                public void visitMaxs(final int maxStack, final int maxLocals) {
                }
                
                public void visitInsn(final int insn) {
                    if (insn != 177) {
                        super.visitInsn(insn);
                    }
                }
            };
            this.staticInit = new CodeEmitter(this, wrapped, access, sig, exceptions);
            if (this.staticHook == null) {
                this.getStaticHook();
            }
            else {
                this.staticInit.invoke_static_this(this.staticHookSig);
            }
            return this.staticInit;
        }
        if (sig.equals(this.staticHookSig)) {
            return new CodeEmitter(this, v, access, sig, exceptions) {
                public boolean isStaticHook() {
                    return true;
                }
            };
        }
        return new CodeEmitter(this, v, access, sig, exceptions);
    }
    
    public CodeEmitter begin_static() {
        return this.begin_method(8, Constants.SIG_STATIC, null);
    }
    
    public void declare_field(final int access, final String name, final Type type, final Object value) {
        final FieldInfo existing = this.fieldInfo.get(name);
        final FieldInfo info = new FieldInfo(access, name, type, value);
        if (existing != null) {
            if (!info.equals(existing)) {
                throw new IllegalArgumentException("Field \"" + name + "\" has been declared differently");
            }
        }
        else {
            this.fieldInfo.put(name, info);
            this.cv.visitField(access, name, type.getDescriptor(), null, value);
        }
    }
    
    boolean isFieldDeclared(final String name) {
        return this.fieldInfo.get(name) != null;
    }
    
    FieldInfo getFieldInfo(final String name) {
        final FieldInfo field = this.fieldInfo.get(name);
        if (field == null) {
            throw new IllegalArgumentException("Field " + name + " is not declared in " + this.getClassType().getClassName());
        }
        return field;
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.begin_class(version, access, name.replace('/', '.'), TypeUtils.fromInternalName(superName), TypeUtils.fromInternalNames(interfaces), null);
    }
    
    public void visitEnd() {
        this.end_class();
    }
    
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        this.declare_field(access, name, Type.getType(desc), value);
        return null;
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        return this.begin_method(access, new Signature(name, desc), TypeUtils.fromInternalNames(exceptions));
    }
    
    static class FieldInfo
    {
        int access;
        String name;
        Type type;
        Object value;
        
        public FieldInfo(final int access, final String name, final Type type, final Object value) {
            this.access = access;
            this.name = name;
            this.type = type;
            this.value = value;
        }
        
        public boolean equals(final Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof FieldInfo)) {
                return false;
            }
            final FieldInfo other = (FieldInfo)o;
            return this.access == other.access && this.name.equals(other.name) && this.type.equals(other.type) && !(this.value == null ^ other.value == null) && (this.value == null || this.value.equals(other.value));
        }
        
        public int hashCode() {
            return this.access ^ this.name.hashCode() ^ this.type.hashCode() ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
    }
}
