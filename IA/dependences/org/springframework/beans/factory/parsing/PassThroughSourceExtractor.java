// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;

public class PassThroughSourceExtractor implements SourceExtractor
{
    @Override
    public Object extractSource(final Object sourceCandidate, final Resource definingResource) {
        return sourceCandidate;
    }
}
