// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.proxy;

import org.springframework.asm.MethodVisitor;
import org.springframework.cglib.core.Signature;
import java.util.Iterator;
import java.io.IOException;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.ClassReader;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

class BridgeMethodResolver
{
    private final Map declToBridge;
    
    public BridgeMethodResolver(final Map declToBridge) {
        this.declToBridge = declToBridge;
    }
    
    public Map resolveAll() {
        final Map resolved = new HashMap();
        for (final Map.Entry entry : this.declToBridge.entrySet()) {
            final Class owner = entry.getKey();
            final Set bridges = entry.getValue();
            try {
                new ClassReader(owner.getName()).accept(new BridgedFinder(bridges, resolved), 6);
            }
            catch (IOException ex) {}
        }
        return resolved;
    }
    
    private static class BridgedFinder extends ClassVisitor
    {
        private Map resolved;
        private Set eligableMethods;
        private Signature currentMethod;
        
        BridgedFinder(final Set eligableMethods, final Map resolved) {
            super(262144);
            this.currentMethod = null;
            this.resolved = resolved;
            this.eligableMethods = eligableMethods;
        }
        
        public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        }
        
        public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
            final Signature sig = new Signature(name, desc);
            if (this.eligableMethods.remove(sig)) {
                this.currentMethod = sig;
                return new MethodVisitor(262144) {
                    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
                        if (opcode == 183 && BridgedFinder.this.currentMethod != null) {
                            final Signature target = new Signature(name, desc);
                            if (!target.equals(BridgedFinder.this.currentMethod)) {
                                BridgedFinder.this.resolved.put(BridgedFinder.this.currentMethod, target);
                            }
                            BridgedFinder.this.currentMethod = null;
                        }
                    }
                };
            }
            return null;
        }
    }
}
