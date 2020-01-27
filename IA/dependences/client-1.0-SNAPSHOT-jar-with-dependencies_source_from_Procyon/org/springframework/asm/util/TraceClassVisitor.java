// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.asm.util;

import java.io.PrintWriter;
import org.springframework.asm.ClassVisitor;

public class TraceClassVisitor extends ClassVisitor
{
    public TraceClassVisitor(final Object object, final PrintWriter pw) {
        super(262144);
    }
}
