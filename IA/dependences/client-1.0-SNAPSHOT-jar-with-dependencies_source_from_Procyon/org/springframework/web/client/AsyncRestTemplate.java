// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import java.io.OutputStream;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.util.concurrent.ListenableFutureAdapter;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.AsyncClientHttpRequest;
import java.io.IOException;
import org.springframework.web.util.UriTemplate;
import org.springframework.core.ParameterizedTypeReference;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import java.net.URI;
import java.util.Map;
import org.springframework.http.HttpMethod;
import java.lang.reflect.Type;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.http.converter.HttpMessageConverter;
import java.util.List;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.client.support.AsyncHttpAccessor;

public class AsyncRestTemplate extends AsyncHttpAccessor implements AsyncRestOperations
{
    private final RestTemplate syncTemplate;
    
    public AsyncRestTemplate() {
        this(new SimpleAsyncTaskExecutor());
    }
    
    public AsyncRestTemplate(final AsyncListenableTaskExecutor taskExecutor) {
        Assert.notNull(taskExecutor, "AsyncTaskExecutor must not be null");
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setTaskExecutor(taskExecutor);
        this.syncTemplate = new RestTemplate(requestFactory);
        this.setAsyncRequestFactory(requestFactory);
    }
    
    public AsyncRestTemplate(final AsyncClientHttpRequestFactory asyncRequestFactory) {
        this(asyncRequestFactory, (ClientHttpRequestFactory)asyncRequestFactory);
    }
    
    public AsyncRestTemplate(final AsyncClientHttpRequestFactory asyncRequestFactory, final ClientHttpRequestFactory syncRequestFactory) {
        this(asyncRequestFactory, new RestTemplate(syncRequestFactory));
    }
    
    public AsyncRestTemplate(final AsyncClientHttpRequestFactory requestFactory, final RestTemplate restTemplate) {
        Assert.notNull(restTemplate, "'restTemplate' must not be null");
        this.syncTemplate = restTemplate;
        this.setAsyncRequestFactory(requestFactory);
    }
    
    public void setErrorHandler(final ResponseErrorHandler errorHandler) {
        this.syncTemplate.setErrorHandler(errorHandler);
    }
    
    public ResponseErrorHandler getErrorHandler() {
        return this.syncTemplate.getErrorHandler();
    }
    
    @Override
    public RestOperations getRestOperations() {
        return this.syncTemplate;
    }
    
    public void setMessageConverters(final List<HttpMessageConverter<?>> messageConverters) {
        this.syncTemplate.setMessageConverters(messageConverters);
    }
    
    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.syncTemplate.getMessageConverters();
    }
    
    @Override
    public <T> ListenableFuture<ResponseEntity<T>> getForEntity(final String url, final Class<T> responseType, final Object... uriVariables) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ListenableFuture<ResponseEntity<T>> getForEntity(final String url, final Class<T> responseType, final Map<String, ?> urlVariables) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor, urlVariables);
    }
    
    @Override
    public <T> ListenableFuture<ResponseEntity<T>> getForEntity(final URI url, final Class<T> responseType) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }
    
    @Override
    public ListenableFuture<HttpHeaders> headForHeaders(final String url, final Object... uriVariables) throws RestClientException {
        final ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        return this.execute(url, HttpMethod.HEAD, null, headersExtractor, uriVariables);
    }
    
    @Override
    public ListenableFuture<HttpHeaders> headForHeaders(final String url, final Map<String, ?> uriVariables) throws RestClientException {
        final ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        return this.execute(url, HttpMethod.HEAD, null, headersExtractor, uriVariables);
    }
    
    @Override
    public ListenableFuture<HttpHeaders> headForHeaders(final URI url) throws RestClientException {
        final ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        return this.execute(url, HttpMethod.HEAD, null, headersExtractor);
    }
    
    @Override
    public ListenableFuture<URI> postForLocation(final String url, final HttpEntity<?> request, final Object... uriVariables) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(request);
        final ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        final ListenableFuture<HttpHeaders> headersFuture = this.execute(url, HttpMethod.POST, requestCallback, headersExtractor, uriVariables);
        return extractLocationHeader(headersFuture);
    }
    
    @Override
    public ListenableFuture<URI> postForLocation(final String url, final HttpEntity<?> request, final Map<String, ?> uriVariables) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(request);
        final ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        final ListenableFuture<HttpHeaders> headersFuture = this.execute(url, HttpMethod.POST, requestCallback, headersExtractor, uriVariables);
        return extractLocationHeader(headersFuture);
    }
    
    @Override
    public ListenableFuture<URI> postForLocation(final URI url, final HttpEntity<?> request) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(request);
        final ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        final ListenableFuture<HttpHeaders> headersFuture = this.execute(url, HttpMethod.POST, requestCallback, headersExtractor);
        return extractLocationHeader(headersFuture);
    }
    
    private static ListenableFuture<URI> extractLocationHeader(final ListenableFuture<HttpHeaders> headersFuture) {
        return new ListenableFuture<URI>() {
            @Override
            public void addCallback(final ListenableFutureCallback<? super URI> callback) {
                headersFuture.addCallback(new ListenableFutureCallback<HttpHeaders>() {
                    @Override
                    public void onSuccess(final HttpHeaders result) {
                        callback.onSuccess(result.getLocation());
                    }
                    
                    @Override
                    public void onFailure(final Throwable t) {
                        callback.onFailure(t);
                    }
                });
            }
            
            @Override
            public boolean cancel(final boolean mayInterruptIfRunning) {
                return headersFuture.cancel(mayInterruptIfRunning);
            }
            
            @Override
            public boolean isCancelled() {
                return headersFuture.isCancelled();
            }
            
            @Override
            public boolean isDone() {
                return headersFuture.isDone();
            }
            
            @Override
            public URI get() throws InterruptedException, ExecutionException {
                final HttpHeaders headers = (HttpHeaders)headersFuture.get();
                return headers.getLocation();
            }
            
            @Override
            public URI get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                final HttpHeaders headers = (HttpHeaders)headersFuture.get(timeout, unit);
                return headers.getLocation();
            }
        };
    }
    
    @Override
    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(final String url, final HttpEntity<?> request, final Class<T> responseType, final Object... uriVariables) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(final String url, final HttpEntity<?> request, final Class<T> responseType, final Map<String, ?> uriVariables) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(final URI url, final HttpEntity<?> request, final Class<T> responseType) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor);
    }
    
    @Override
    public ListenableFuture<?> put(final String url, final HttpEntity<?> request, final Object... uriVariables) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(request);
        return this.execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor<?>)null, uriVariables);
    }
    
    @Override
    public ListenableFuture<?> put(final String url, final HttpEntity<?> request, final Map<String, ?> uriVariables) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(request);
        return this.execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor<?>)null, uriVariables);
    }
    
    @Override
    public ListenableFuture<?> put(final URI url, final HttpEntity<?> request) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(request);
        return this.execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor<?>)null);
    }
    
    @Override
    public ListenableFuture<?> delete(final String url, final Object... urlVariables) throws RestClientException {
        return this.execute(url, HttpMethod.DELETE, null, (ResponseExtractor<?>)null, urlVariables);
    }
    
    @Override
    public ListenableFuture<?> delete(final String url, final Map<String, ?> urlVariables) throws RestClientException {
        return this.execute(url, HttpMethod.DELETE, null, (ResponseExtractor<?>)null, urlVariables);
    }
    
    @Override
    public ListenableFuture<?> delete(final URI url) throws RestClientException {
        return this.execute(url, HttpMethod.DELETE, null, (ResponseExtractor<?>)null);
    }
    
    @Override
    public ListenableFuture<Set<HttpMethod>> optionsForAllow(final String url, final Object... uriVariables) throws RestClientException {
        final ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        final ListenableFuture<HttpHeaders> headersFuture = this.execute(url, HttpMethod.OPTIONS, null, headersExtractor, uriVariables);
        return extractAllowHeader(headersFuture);
    }
    
    @Override
    public ListenableFuture<Set<HttpMethod>> optionsForAllow(final String url, final Map<String, ?> uriVariables) throws RestClientException {
        final ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        final ListenableFuture<HttpHeaders> headersFuture = this.execute(url, HttpMethod.OPTIONS, null, headersExtractor, uriVariables);
        return extractAllowHeader(headersFuture);
    }
    
    @Override
    public ListenableFuture<Set<HttpMethod>> optionsForAllow(final URI url) throws RestClientException {
        final ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        final ListenableFuture<HttpHeaders> headersFuture = this.execute(url, HttpMethod.OPTIONS, null, headersExtractor);
        return extractAllowHeader(headersFuture);
    }
    
    private static ListenableFuture<Set<HttpMethod>> extractAllowHeader(final ListenableFuture<HttpHeaders> headersFuture) {
        return new ListenableFuture<Set<HttpMethod>>() {
            @Override
            public void addCallback(final ListenableFutureCallback<? super Set<HttpMethod>> callback) {
                headersFuture.addCallback(new ListenableFutureCallback<HttpHeaders>() {
                    @Override
                    public void onSuccess(final HttpHeaders result) {
                        callback.onSuccess(result.getAllow());
                    }
                    
                    @Override
                    public void onFailure(final Throwable t) {
                        callback.onFailure(t);
                    }
                });
            }
            
            @Override
            public boolean cancel(final boolean mayInterruptIfRunning) {
                return headersFuture.cancel(mayInterruptIfRunning);
            }
            
            @Override
            public boolean isCancelled() {
                return headersFuture.isCancelled();
            }
            
            @Override
            public boolean isDone() {
                return headersFuture.isDone();
            }
            
            @Override
            public Set<HttpMethod> get() throws InterruptedException, ExecutionException {
                final HttpHeaders headers = (HttpHeaders)headersFuture.get();
                return headers.getAllow();
            }
            
            @Override
            public Set<HttpMethod> get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                final HttpHeaders headers = (HttpHeaders)headersFuture.get(timeout, unit);
                return headers.getAllow();
            }
        };
    }
    
    @Override
    public <T> ListenableFuture<ResponseEntity<T>> exchange(final String url, final HttpMethod method, final HttpEntity<?> requestEntity, final Class<T> responseType, final Object... uriVariables) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ListenableFuture<ResponseEntity<T>> exchange(final String url, final HttpMethod method, final HttpEntity<?> requestEntity, final Class<T> responseType, final Map<String, ?> uriVariables) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ListenableFuture<ResponseEntity<T>> exchange(final URI url, final HttpMethod method, final HttpEntity<?> requestEntity, final Class<T> responseType) throws RestClientException {
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, method, requestCallback, responseExtractor);
    }
    
    @Override
    public <T> ListenableFuture<ResponseEntity<T>> exchange(final String url, final HttpMethod method, final HttpEntity<?> requestEntity, final ParameterizedTypeReference<T> responseType, final Object... uriVariables) throws RestClientException {
        final Type type = responseType.getType();
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return this.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ListenableFuture<ResponseEntity<T>> exchange(final String url, final HttpMethod method, final HttpEntity<?> requestEntity, final ParameterizedTypeReference<T> responseType, final Map<String, ?> uriVariables) throws RestClientException {
        final Type type = responseType.getType();
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return this.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ListenableFuture<ResponseEntity<T>> exchange(final URI url, final HttpMethod method, final HttpEntity<?> requestEntity, final ParameterizedTypeReference<T> responseType) throws RestClientException {
        final Type type = responseType.getType();
        final AsyncRequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return this.execute(url, method, requestCallback, responseExtractor);
    }
    
    @Override
    public <T> ListenableFuture<T> execute(final String url, final HttpMethod method, final AsyncRequestCallback requestCallback, final ResponseExtractor<T> responseExtractor, final Object... urlVariables) throws RestClientException {
        final URI expanded = new UriTemplate(url).expand(urlVariables);
        return this.doExecute(expanded, method, requestCallback, responseExtractor);
    }
    
    @Override
    public <T> ListenableFuture<T> execute(final String url, final HttpMethod method, final AsyncRequestCallback requestCallback, final ResponseExtractor<T> responseExtractor, final Map<String, ?> urlVariables) throws RestClientException {
        final URI expanded = new UriTemplate(url).expand(urlVariables);
        return this.doExecute(expanded, method, requestCallback, responseExtractor);
    }
    
    @Override
    public <T> ListenableFuture<T> execute(final URI url, final HttpMethod method, final AsyncRequestCallback requestCallback, final ResponseExtractor<T> responseExtractor) throws RestClientException {
        return (ListenableFuture<T>)this.doExecute(url, method, requestCallback, (ResponseExtractor<Object>)responseExtractor);
    }
    
    protected <T> ListenableFuture<T> doExecute(final URI url, final HttpMethod method, final AsyncRequestCallback requestCallback, final ResponseExtractor<T> responseExtractor) throws RestClientException {
        Assert.notNull(url, "'url' must not be null");
        Assert.notNull(method, "'method' must not be null");
        try {
            final AsyncClientHttpRequest request = this.createAsyncRequest(url, method);
            if (requestCallback != null) {
                requestCallback.doWithRequest(request);
            }
            final ListenableFuture<ClientHttpResponse> responseFuture = request.executeAsync();
            return (ListenableFuture<T>)new ResponseExtractorFuture(method, url, responseFuture, (ResponseExtractor<Object>)responseExtractor);
        }
        catch (IOException ex) {
            throw new ResourceAccessException("I/O error on " + method.name() + " request for \"" + url + "\":" + ex.getMessage(), ex);
        }
    }
    
    private void logResponseStatus(final HttpMethod method, final URI url, final ClientHttpResponse response) {
        if (this.logger.isDebugEnabled()) {
            try {
                this.logger.debug("Async " + method.name() + " request for \"" + url + "\" resulted in " + response.getStatusCode() + " (" + response.getStatusText() + ")");
            }
            catch (IOException ex) {}
        }
    }
    
    private void handleResponseError(final HttpMethod method, final URI url, final ClientHttpResponse response) throws IOException {
        if (this.logger.isWarnEnabled()) {
            try {
                this.logger.warn("Async " + method.name() + " request for \"" + url + "\" resulted in " + response.getStatusCode() + " (" + response.getStatusText() + "); invoking error handler");
            }
            catch (IOException ex) {}
        }
        this.getErrorHandler().handleError(response);
    }
    
    protected <T> AsyncRequestCallback acceptHeaderRequestCallback(final Class<T> responseType) {
        return new AsyncRequestCallbackAdapter(this.syncTemplate.acceptHeaderRequestCallback(responseType));
    }
    
    protected <T> AsyncRequestCallback httpEntityCallback(final HttpEntity<T> requestBody) {
        return new AsyncRequestCallbackAdapter(this.syncTemplate.httpEntityCallback(requestBody));
    }
    
    protected <T> AsyncRequestCallback httpEntityCallback(final HttpEntity<T> request, final Type responseType) {
        return new AsyncRequestCallbackAdapter(this.syncTemplate.httpEntityCallback(request, responseType));
    }
    
    protected <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(final Type responseType) {
        return this.syncTemplate.responseEntityExtractor(responseType);
    }
    
    protected ResponseExtractor<HttpHeaders> headersExtractor() {
        return this.syncTemplate.headersExtractor();
    }
    
    private class ResponseExtractorFuture<T> extends ListenableFutureAdapter<T, ClientHttpResponse>
    {
        private final HttpMethod method;
        private final URI url;
        private final ResponseExtractor<T> responseExtractor;
        
        public ResponseExtractorFuture(final HttpMethod method, final URI url, final ListenableFuture<ClientHttpResponse> clientHttpResponseFuture, final ResponseExtractor<T> responseExtractor) {
            super(clientHttpResponseFuture);
            this.method = method;
            this.url = url;
            this.responseExtractor = responseExtractor;
        }
        
        @Override
        protected final T adapt(final ClientHttpResponse response) throws ExecutionException {
            try {
                if (!AsyncRestTemplate.this.getErrorHandler().hasError(response)) {
                    AsyncRestTemplate.this.logResponseStatus(this.method, this.url, response);
                }
                else {
                    AsyncRestTemplate.this.handleResponseError(this.method, this.url, response);
                }
                return this.convertResponse(response);
            }
            catch (IOException ex) {
                throw new ExecutionException(ex);
            }
            finally {
                if (response != null) {
                    response.close();
                }
            }
        }
        
        protected T convertResponse(final ClientHttpResponse response) throws IOException {
            return (this.responseExtractor != null) ? this.responseExtractor.extractData(response) : null;
        }
    }
    
    private static class AsyncRequestCallbackAdapter implements AsyncRequestCallback
    {
        private final RequestCallback adaptee;
        
        public AsyncRequestCallbackAdapter(final RequestCallback requestCallback) {
            this.adaptee = requestCallback;
        }
        
        @Override
        public void doWithRequest(final AsyncClientHttpRequest request) throws IOException {
            if (this.adaptee != null) {
                this.adaptee.doWithRequest(new ClientHttpRequest() {
                    @Override
                    public ClientHttpResponse execute() throws IOException {
                        throw new UnsupportedOperationException("execute not supported");
                    }
                    
                    @Override
                    public OutputStream getBody() throws IOException {
                        return request.getBody();
                    }
                    
                    @Override
                    public HttpMethod getMethod() {
                        return request.getMethod();
                    }
                    
                    @Override
                    public URI getURI() {
                        return request.getURI();
                    }
                    
                    @Override
                    public HttpHeaders getHeaders() {
                        return request.getHeaders();
                    }
                });
            }
        }
    }
}
