// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.core.async;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ByteBufferFeeder extends NonBlockingInputFeeder
{
    void feedInput(final ByteBuffer p0) throws IOException;
}
