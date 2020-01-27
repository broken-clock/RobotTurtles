// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpOutputMessage;
import java.util.Iterator;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.util.ClassUtils;
import org.apache.commons.logging.Log;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;
import org.springframework.web.util.UriTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.Map;
import org.springframework.http.HttpMethod;
import java.lang.reflect.Type;
import java.util.Collection;
import org.springframework.util.Assert;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import java.util.ArrayList;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;
import java.util.List;
import org.springframework.http.client.support.InterceptingHttpAccessor;

public class RestTemplate extends InterceptingHttpAccessor implements RestOperations
{
    private static boolean romePresent;
    private static final boolean jaxb2Present;
    private static final boolean jackson2Present;
    private static final boolean jacksonPresent;
    private final List<HttpMessageConverter<?>> messageConverters;
    private ResponseErrorHandler errorHandler;
    private final ResponseExtractor<HttpHeaders> headersExtractor;
    
    public RestTemplate() {
        this.messageConverters = new ArrayList<HttpMessageConverter<?>>();
        this.errorHandler = new DefaultResponseErrorHandler();
        this.headersExtractor = new HeadersExtractor();
        this.messageConverters.add(new ByteArrayHttpMessageConverter());
        this.messageConverters.add(new StringHttpMessageConverter());
        this.messageConverters.add(new ResourceHttpMessageConverter());
        this.messageConverters.add(new SourceHttpMessageConverter<Object>());
        this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        if (RestTemplate.romePresent) {
            this.messageConverters.add(new AtomFeedHttpMessageConverter());
            this.messageConverters.add(new RssChannelHttpMessageConverter());
        }
        if (RestTemplate.jaxb2Present) {
            this.messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
        }
        if (RestTemplate.jackson2Present) {
            this.messageConverters.add(new MappingJackson2HttpMessageConverter());
        }
        else if (RestTemplate.jacksonPresent) {
            this.messageConverters.add(new MappingJacksonHttpMessageConverter());
        }
    }
    
    public RestTemplate(final ClientHttpRequestFactory requestFactory) {
        this();
        this.setRequestFactory(requestFactory);
    }
    
    public RestTemplate(final List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = new ArrayList<HttpMessageConverter<?>>();
        this.errorHandler = new DefaultResponseErrorHandler();
        this.headersExtractor = new HeadersExtractor();
        Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
        this.messageConverters.addAll(messageConverters);
    }
    
    public void setMessageConverters(final List<HttpMessageConverter<?>> messageConverters) {
        Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
        this.messageConverters.clear();
        this.messageConverters.addAll(messageConverters);
    }
    
    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.messageConverters;
    }
    
    public void setErrorHandler(final ResponseErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "'errorHandler' must not be null");
        this.errorHandler = errorHandler;
    }
    
    public ResponseErrorHandler getErrorHandler() {
        return this.errorHandler;
    }
    
    @Override
    public <T> T getForObject(final String url, final Class<T> responseType, final Object... urlVariables) throws RestClientException {
        final RequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        final HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, this.getMessageConverters(), this.logger);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor, urlVariables);
    }
    
    @Override
    public <T> T getForObject(final String url, final Class<T> responseType, final Map<String, ?> urlVariables) throws RestClientException {
        final RequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        final HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, this.getMessageConverters(), this.logger);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor, urlVariables);
    }
    
    @Override
    public <T> T getForObject(final URI url, final Class<T> responseType) throws RestClientException {
        final RequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        final HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, this.getMessageConverters(), this.logger);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }
    
    @Override
    public <T> ResponseEntity<T> getForEntity(final String url, final Class<T> responseType, final Object... urlVariables) throws RestClientException {
        final RequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor, urlVariables);
    }
    
    @Override
    public <T> ResponseEntity<T> getForEntity(final String url, final Class<T> responseType, final Map<String, ?> urlVariables) throws RestClientException {
        final RequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor, urlVariables);
    }
    
    @Override
    public <T> ResponseEntity<T> getForEntity(final URI url, final Class<T> responseType) throws RestClientException {
        final RequestCallback requestCallback = this.acceptHeaderRequestCallback(responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }
    
    @Override
    public HttpHeaders headForHeaders(final String url, final Object... urlVariables) throws RestClientException {
        return this.execute(url, HttpMethod.HEAD, null, this.headersExtractor(), urlVariables);
    }
    
    @Override
    public HttpHeaders headForHeaders(final String url, final Map<String, ?> urlVariables) throws RestClientException {
        return this.execute(url, HttpMethod.HEAD, null, this.headersExtractor(), urlVariables);
    }
    
    @Override
    public HttpHeaders headForHeaders(final URI url) throws RestClientException {
        return this.execute(url, HttpMethod.HEAD, null, this.headersExtractor());
    }
    
    @Override
    public URI postForLocation(final String url, final Object request, final Object... urlVariables) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(request);
        final HttpHeaders headers = this.execute(url, HttpMethod.POST, requestCallback, this.headersExtractor(), urlVariables);
        return headers.getLocation();
    }
    
    @Override
    public URI postForLocation(final String url, final Object request, final Map<String, ?> urlVariables) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(request);
        final HttpHeaders headers = this.execute(url, HttpMethod.POST, requestCallback, this.headersExtractor(), urlVariables);
        return headers.getLocation();
    }
    
    @Override
    public URI postForLocation(final URI url, final Object request) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(request);
        final HttpHeaders headers = this.execute(url, HttpMethod.POST, requestCallback, this.headersExtractor());
        return headers.getLocation();
    }
    
    @Override
    public <T> T postForObject(final String url, final Object request, final Class<T> responseType, final Object... uriVariables) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        final HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, this.getMessageConverters(), this.logger);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> T postForObject(final String url, final Object request, final Class<T> responseType, final Map<String, ?> uriVariables) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        final HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, this.getMessageConverters(), this.logger);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> T postForObject(final URI url, final Object request, final Class<T> responseType) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        final HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, this.getMessageConverters());
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor);
    }
    
    @Override
    public <T> ResponseEntity<T> postForEntity(final String url, final Object request, final Class<T> responseType, final Object... uriVariables) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ResponseEntity<T> postForEntity(final String url, final Object request, final Class<T> responseType, final Map<String, ?> uriVariables) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ResponseEntity<T> postForEntity(final URI url, final Object request, final Class<T> responseType) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(request, responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, HttpMethod.POST, requestCallback, responseExtractor);
    }
    
    @Override
    public void put(final String url, final Object request, final Object... urlVariables) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(request);
        this.execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor<Object>)null, urlVariables);
    }
    
    @Override
    public void put(final String url, final Object request, final Map<String, ?> urlVariables) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(request);
        this.execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor<Object>)null, urlVariables);
    }
    
    @Override
    public void put(final URI url, final Object request) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(request);
        this.execute(url, HttpMethod.PUT, requestCallback, (ResponseExtractor<Object>)null);
    }
    
    @Override
    public void delete(final String url, final Object... urlVariables) throws RestClientException {
        this.execute(url, HttpMethod.DELETE, null, (ResponseExtractor<Object>)null, urlVariables);
    }
    
    @Override
    public void delete(final String url, final Map<String, ?> urlVariables) throws RestClientException {
        this.execute(url, HttpMethod.DELETE, null, (ResponseExtractor<Object>)null, urlVariables);
    }
    
    @Override
    public void delete(final URI url) throws RestClientException {
        this.execute(url, HttpMethod.DELETE, null, (ResponseExtractor<Object>)null);
    }
    
    @Override
    public Set<HttpMethod> optionsForAllow(final String url, final Object... urlVariables) throws RestClientException {
        final ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        final HttpHeaders headers = this.execute(url, HttpMethod.OPTIONS, null, headersExtractor, urlVariables);
        return headers.getAllow();
    }
    
    @Override
    public Set<HttpMethod> optionsForAllow(final String url, final Map<String, ?> urlVariables) throws RestClientException {
        final ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        final HttpHeaders headers = this.execute(url, HttpMethod.OPTIONS, null, headersExtractor, urlVariables);
        return headers.getAllow();
    }
    
    @Override
    public Set<HttpMethod> optionsForAllow(final URI url) throws RestClientException {
        final ResponseExtractor<HttpHeaders> headersExtractor = this.headersExtractor();
        final HttpHeaders headers = this.execute(url, HttpMethod.OPTIONS, null, headersExtractor);
        return headers.getAllow();
    }
    
    @Override
    public <T> ResponseEntity<T> exchange(final String url, final HttpMethod method, final HttpEntity<?> requestEntity, final Class<T> responseType, final Object... uriVariables) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ResponseEntity<T> exchange(final String url, final HttpMethod method, final HttpEntity<?> requestEntity, final Class<T> responseType, final Map<String, ?> uriVariables) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ResponseEntity<T> exchange(final URI url, final HttpMethod method, final HttpEntity<?> requestEntity, final Class<T> responseType) throws RestClientException {
        final RequestCallback requestCallback = this.httpEntityCallback(requestEntity, responseType);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(responseType);
        return this.execute(url, method, requestCallback, responseExtractor);
    }
    
    @Override
    public <T> ResponseEntity<T> exchange(final String url, final HttpMethod method, final HttpEntity<?> requestEntity, final ParameterizedTypeReference<T> responseType, final Object... uriVariables) throws RestClientException {
        final Type type = responseType.getType();
        final RequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return this.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ResponseEntity<T> exchange(final String url, final HttpMethod method, final HttpEntity<?> requestEntity, final ParameterizedTypeReference<T> responseType, final Map<String, ?> uriVariables) throws RestClientException {
        final Type type = responseType.getType();
        final RequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return this.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }
    
    @Override
    public <T> ResponseEntity<T> exchange(final URI url, final HttpMethod method, final HttpEntity<?> requestEntity, final ParameterizedTypeReference<T> responseType) throws RestClientException {
        final Type type = responseType.getType();
        final RequestCallback requestCallback = this.httpEntityCallback(requestEntity, type);
        final ResponseExtractor<ResponseEntity<T>> responseExtractor = this.responseEntityExtractor(type);
        return this.execute(url, method, requestCallback, responseExtractor);
    }
    
    @Override
    public <T> T execute(final String url, final HttpMethod method, final RequestCallback requestCallback, final ResponseExtractor<T> responseExtractor, final Object... urlVariables) throws RestClientException {
        final URI expanded = new UriTemplate(url).expand(urlVariables);
        return this.doExecute(expanded, method, requestCallback, responseExtractor);
    }
    
    @Override
    public <T> T execute(final String url, final HttpMethod method, final RequestCallback requestCallback, final ResponseExtractor<T> responseExtractor, final Map<String, ?> urlVariables) throws RestClientException {
        final URI expanded = new UriTemplate(url).expand(urlVariables);
        return this.doExecute(expanded, method, requestCallback, responseExtractor);
    }
    
    @Override
    public <T> T execute(final URI url, final HttpMethod method, final RequestCallback requestCallback, final ResponseExtractor<T> responseExtractor) throws RestClientException {
        return (T)this.doExecute(url, method, requestCallback, (ResponseExtractor<Object>)responseExtractor);
    }
    
    protected <T> T doExecute(final URI url, final HttpMethod method, final RequestCallback requestCallback, final ResponseExtractor<T> responseExtractor) throws RestClientException {
        Assert.notNull(url, "'url' must not be null");
        Assert.notNull(method, "'method' must not be null");
        ClientHttpResponse response = null;
        try {
            final ClientHttpRequest request = this.createRequest(url, method);
            if (requestCallback != null) {
                requestCallback.doWithRequest(request);
            }
            response = request.execute();
            if (!this.getErrorHandler().hasError(response)) {
                this.logResponseStatus(method, url, response);
            }
            else {
                this.handleResponseError(method, url, response);
            }
            if (responseExtractor != null) {
                return responseExtractor.extractData(response);
            }
            return null;
        }
        catch (IOException ex) {
            throw new ResourceAccessException("I/O error on " + method.name() + " request for \"" + url + "\":" + ex.getMessage(), ex);
        }
        finally {
            if (response != null) {
                response.close();
            }
        }
    }
    
    private void logResponseStatus(final HttpMethod method, final URI url, final ClientHttpResponse response) {
        if (this.logger.isDebugEnabled()) {
            try {
                this.logger.debug(method.name() + " request for \"" + url + "\" resulted in " + response.getStatusCode() + " (" + response.getStatusText() + ")");
            }
            catch (IOException ex) {}
        }
    }
    
    private void handleResponseError(final HttpMethod method, final URI url, final ClientHttpResponse response) throws IOException {
        if (this.logger.isWarnEnabled()) {
            try {
                this.logger.warn(method.name() + " request for \"" + url + "\" resulted in " + response.getStatusCode() + " (" + response.getStatusText() + "); invoking error handler");
            }
            catch (IOException ex) {}
        }
        this.getErrorHandler().handleError(response);
    }
    
    protected <T> RequestCallback acceptHeaderRequestCallback(final Class<T> responseType) {
        return new AcceptHeaderRequestCallback((Type)responseType);
    }
    
    protected <T> RequestCallback httpEntityCallback(final Object requestBody) {
        return new HttpEntityRequestCallback(requestBody);
    }
    
    protected <T> RequestCallback httpEntityCallback(final Object requestBody, final Type responseType) {
        return new HttpEntityRequestCallback(requestBody, responseType);
    }
    
    protected <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(final Type responseType) {
        return new ResponseEntityResponseExtractor<T>(responseType);
    }
    
    protected ResponseExtractor<HttpHeaders> headersExtractor() {
        return this.headersExtractor;
    }
    
    static {
        RestTemplate.romePresent = ClassUtils.isPresent("com.sun.syndication.feed.WireFeed", RestTemplate.class.getClassLoader());
        jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", RestTemplate.class.getClassLoader());
        jackson2Present = (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", RestTemplate.class.getClassLoader()) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", RestTemplate.class.getClassLoader()));
        jacksonPresent = (ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", RestTemplate.class.getClassLoader()) && ClassUtils.isPresent("org.codehaus.jackson.JsonGenerator", RestTemplate.class.getClassLoader()));
    }
    
    private class AcceptHeaderRequestCallback implements RequestCallback
    {
        private final Type responseType;
        
        private AcceptHeaderRequestCallback(final Type responseType) {
            this.responseType = responseType;
        }
        
        @Override
        public void doWithRequest(final ClientHttpRequest request) throws IOException {
            if (this.responseType != null) {
                Class<?> responseClass = null;
                if (this.responseType instanceof Class) {
                    responseClass = (Class<?>)this.responseType;
                }
                final List<MediaType> allSupportedMediaTypes = new ArrayList<MediaType>();
                for (final HttpMessageConverter<?> converter : RestTemplate.this.getMessageConverters()) {
                    if (responseClass != null) {
                        if (!converter.canRead(responseClass, null)) {
                            continue;
                        }
                        allSupportedMediaTypes.addAll(this.getSupportedMediaTypes(converter));
                    }
                    else {
                        if (!(converter instanceof GenericHttpMessageConverter)) {
                            continue;
                        }
                        final GenericHttpMessageConverter<?> genericConverter = (GenericHttpMessageConverter<?>)(GenericHttpMessageConverter)converter;
                        if (!genericConverter.canRead(this.responseType, null, null)) {
                            continue;
                        }
                        allSupportedMediaTypes.addAll(this.getSupportedMediaTypes(converter));
                    }
                }
                if (!allSupportedMediaTypes.isEmpty()) {
                    MediaType.sortBySpecificity(allSupportedMediaTypes);
                    if (RestTemplate.this.logger.isDebugEnabled()) {
                        RestTemplate.this.logger.debug("Setting request Accept header to " + allSupportedMediaTypes);
                    }
                    request.getHeaders().setAccept(allSupportedMediaTypes);
                }
            }
        }
        
        private List<MediaType> getSupportedMediaTypes(final HttpMessageConverter<?> messageConverter) {
            final List<MediaType> supportedMediaTypes = messageConverter.getSupportedMediaTypes();
            final List<MediaType> result = new ArrayList<MediaType>(supportedMediaTypes.size());
            for (MediaType supportedMediaType : supportedMediaTypes) {
                if (supportedMediaType.getCharSet() != null) {
                    supportedMediaType = new MediaType(supportedMediaType.getType(), supportedMediaType.getSubtype());
                }
                result.add(supportedMediaType);
            }
            return result;
        }
    }
    
    private class HttpEntityRequestCallback extends AcceptHeaderRequestCallback
    {
        private final HttpEntity<?> requestEntity;
        
        private HttpEntityRequestCallback(final RestTemplate restTemplate, final Object requestBody) {
            this(restTemplate, requestBody, (Type)null);
        }
        
        private HttpEntityRequestCallback(final Object requestBody, final Type responseType) {
            super(responseType);
            if (requestBody instanceof HttpEntity) {
                this.requestEntity = (HttpEntity<?>)requestBody;
            }
            else if (requestBody != null) {
                this.requestEntity = new HttpEntity<Object>(requestBody);
            }
            else {
                this.requestEntity = HttpEntity.EMPTY;
            }
        }
        
        @Override
        public void doWithRequest(final ClientHttpRequest httpRequest) throws IOException {
            super.doWithRequest(httpRequest);
            if (!this.requestEntity.hasBody()) {
                final HttpHeaders httpHeaders = httpRequest.getHeaders();
                final HttpHeaders requestHeaders = this.requestEntity.getHeaders();
                if (!requestHeaders.isEmpty()) {
                    httpHeaders.putAll(requestHeaders);
                }
                if (httpHeaders.getContentLength() == -1L) {
                    httpHeaders.setContentLength(0L);
                }
                return;
            }
            final Object requestBody = this.requestEntity.getBody();
            final Class<?> requestType = requestBody.getClass();
            final HttpHeaders requestHeaders2 = this.requestEntity.getHeaders();
            final MediaType requestContentType = requestHeaders2.getContentType();
            for (final HttpMessageConverter<?> messageConverter : RestTemplate.this.getMessageConverters()) {
                if (messageConverter.canWrite(requestType, requestContentType)) {
                    if (!requestHeaders2.isEmpty()) {
                        httpRequest.getHeaders().putAll(requestHeaders2);
                    }
                    if (RestTemplate.this.logger.isDebugEnabled()) {
                        if (requestContentType != null) {
                            RestTemplate.this.logger.debug("Writing [" + requestBody + "] as \"" + requestContentType + "\" using [" + messageConverter + "]");
                        }
                        else {
                            RestTemplate.this.logger.debug("Writing [" + requestBody + "] using [" + messageConverter + "]");
                        }
                    }
                    messageConverter.write(requestBody, requestContentType, httpRequest);
                    return;
                }
            }
            String message = "Could not write request: no suitable HttpMessageConverter found for request type [" + requestType.getName() + "]";
            if (requestContentType != null) {
                message = message + " and content type [" + requestContentType + "]";
            }
            throw new RestClientException(message);
        }
    }
    
    private class ResponseEntityResponseExtractor<T> implements ResponseExtractor<ResponseEntity<T>>
    {
        private final HttpMessageConverterExtractor<T> delegate;
        
        public ResponseEntityResponseExtractor(final Type responseType) {
            if (responseType != null && !Void.class.equals(responseType)) {
                this.delegate = new HttpMessageConverterExtractor<T>(responseType, RestTemplate.this.getMessageConverters(), RestTemplate.this.logger);
            }
            else {
                this.delegate = null;
            }
        }
        
        @Override
        public ResponseEntity<T> extractData(final ClientHttpResponse response) throws IOException {
            if (this.delegate != null) {
                final T body = this.delegate.extractData(response);
                return new ResponseEntity<T>(body, response.getHeaders(), response.getStatusCode());
            }
            return new ResponseEntity<T>(response.getHeaders(), response.getStatusCode());
        }
    }
    
    private static class HeadersExtractor implements ResponseExtractor<HttpHeaders>
    {
        @Override
        public HttpHeaders extractData(final ClientHttpResponse response) throws IOException {
            return response.getHeaders();
        }
    }
}
