// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

public interface ProblemReporter
{
    void fatal(final Problem p0);
    
    void error(final Problem p0);
    
    void warning(final Problem p0);
}
