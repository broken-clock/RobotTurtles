// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.util.concurrent.ExecutionException;
import org.springframework.util.concurrent.FutureAdapter;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.ListenableFutureCallbackRegistry;
import java.io.IOException;
import org.apache.http.HttpResponse;
import java.util.concurrent.Future;
import org.apache.http.HttpEntity;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.http.HttpHeaders;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.apache.http.protocol.HttpContext;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.nio.client.HttpAsyncClient;

final class HttpComponentsAsyncClientHttpRequest extends AbstractBufferingAsyncClientHttpRequest
{
    private final HttpAsyncClient httpClient;
    private final HttpUriRequest httpRequest;
    private final HttpContext httpContext;
    
    HttpComponentsAsyncClientHttpRequest(final HttpAsyncClient httpClient, final HttpUriRequest httpRequest, final HttpContext httpContext) {
        this.httpClient = httpClient;
        this.httpRequest = httpRequest;
        this.httpContext = httpContext;
    }
    
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.httpRequest.getMethod());
    }
    
    @Override
    public URI getURI() {
        return this.httpRequest.getURI();
    }
    
    @Override
    protected ListenableFuture<ClientHttpResponse> executeInternal(final HttpHeaders headers, final byte[] bufferedOutput) throws IOException {
        HttpComponentsClientHttpRequest.addHeaders(this.httpRequest, headers);
        if (this.httpRequest instanceof HttpEntityEnclosingRequest) {
            final HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest)this.httpRequest;
            final HttpEntity requestEntity = (HttpEntity)new NByteArrayEntity(bufferedOutput);
            entityEnclosingRequest.setEntity(requestEntity);
        }
        final HttpResponseFutureCallback callback = new HttpResponseFutureCallback();
        final Future<HttpResponse> futureResponse = (Future<HttpResponse>)this.httpClient.execute(this.httpRequest, this.httpContext, (FutureCallback)callback);
        return new ClientHttpResponseFuture(futureResponse, callback);
    }
    
    private static class HttpResponseFutureCallback implements FutureCallback<HttpResponse>
    {
        private final ListenableFutureCallbackRegistry<ClientHttpResponse> callbacks;
        
        private HttpResponseFutureCallback() {
            this.callbacks = new ListenableFutureCallbackRegistry<ClientHttpResponse>();
        }
        
        public void addCallback(final ListenableFutureCallback<? super ClientHttpResponse> callback) {
            this.callbacks.addCallback(callback);
        }
        
        @Override
        public void completed(final HttpResponse result) {
            this.callbacks.success(new HttpComponentsAsyncClientHttpResponse(result));
        }
        
        @Override
        public void failed(final Exception ex) {
            this.callbacks.failure(ex);
        }
        
        @Override
        public void cancelled() {
        }
    }
    
    private static class ClientHttpResponseFuture extends FutureAdapter<ClientHttpResponse, HttpResponse> implements ListenableFuture<ClientHttpResponse>
    {
        private final HttpResponseFutureCallback callback;
        
        public ClientHttpResponseFuture(final Future<HttpResponse> futureResponse, final HttpResponseFutureCallback callback) {
            super(futureResponse);
            this.callback = callback;
        }
        
        @Override
        protected ClientHttpResponse adapt(final HttpResponse response) {
            return new HttpComponentsAsyncClientHttpResponse(response);
        }
        
        @Override
        public void addCallback(final ListenableFutureCallback<? super ClientHttpResponse> callback) {
            this.callback.addCallback(callback);
        }
    }
}
