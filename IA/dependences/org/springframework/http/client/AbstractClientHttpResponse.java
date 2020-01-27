// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpStatus;

public abstract class AbstractClientHttpResponse implements ClientHttpResponse
{
    @Override
    public HttpStatus getStatusCode() throws IOException {
        return HttpStatus.valueOf(this.getRawStatusCode());
    }
}
