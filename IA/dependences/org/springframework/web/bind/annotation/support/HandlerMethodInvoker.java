// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.bind.annotation.support;

import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpOutputMessage;
import org.springframework.validation.BindException;
import org.springframework.web.bind.support.WebRequestDataBinder;
import java.lang.reflect.Type;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import org.springframework.util.Assert;
import java.util.Collection;
import org.springframework.http.MediaType;
import java.util.ArrayList;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.util.ClassUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpHeaders;
import java.util.LinkedHashMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import org.springframework.web.multipart.MultipartRequest;
import java.util.Set;
import java.util.Arrays;
import org.springframework.web.bind.annotation.InitBinder;
import java.lang.annotation.Annotation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.Errors;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;
import java.util.Map;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import org.springframework.core.Conventions;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.ReflectionUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.WebRequest;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.context.request.NativeWebRequest;
import java.lang.reflect.Method;
import org.springframework.web.bind.support.DefaultSessionAttributeStore;
import org.springframework.web.bind.support.SimpleSessionStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.apache.commons.logging.Log;

public class HandlerMethodInvoker
{
    private static final String MODEL_KEY_PREFIX_STALE;
    private static final Log logger;
    private final HandlerMethodResolver methodResolver;
    private final WebBindingInitializer bindingInitializer;
    private final SessionAttributeStore sessionAttributeStore;
    private final ParameterNameDiscoverer parameterNameDiscoverer;
    private final WebArgumentResolver[] customArgumentResolvers;
    private final HttpMessageConverter<?>[] messageConverters;
    private final SimpleSessionStatus sessionStatus;
    
    public HandlerMethodInvoker(final HandlerMethodResolver methodResolver) {
        this(methodResolver, null);
    }
    
    public HandlerMethodInvoker(final HandlerMethodResolver methodResolver, final WebBindingInitializer bindingInitializer) {
        this(methodResolver, bindingInitializer, new DefaultSessionAttributeStore(), null, null, null);
    }
    
    public HandlerMethodInvoker(final HandlerMethodResolver methodResolver, final WebBindingInitializer bindingInitializer, final SessionAttributeStore sessionAttributeStore, final ParameterNameDiscoverer parameterNameDiscoverer, final WebArgumentResolver[] customArgumentResolvers, final HttpMessageConverter<?>[] messageConverters) {
        this.sessionStatus = new SimpleSessionStatus();
        this.methodResolver = methodResolver;
        this.bindingInitializer = bindingInitializer;
        this.sessionAttributeStore = sessionAttributeStore;
        this.parameterNameDiscoverer = parameterNameDiscoverer;
        this.customArgumentResolvers = customArgumentResolvers;
        this.messageConverters = messageConverters;
    }
    
    public final Object invokeHandlerMethod(final Method handlerMethod, final Object handler, final NativeWebRequest webRequest, final ExtendedModelMap implicitModel) throws Exception {
        final Method handlerMethodToInvoke = BridgeMethodResolver.findBridgedMethod(handlerMethod);
        try {
            final boolean debug = HandlerMethodInvoker.logger.isDebugEnabled();
            for (final String attrName : this.methodResolver.getActualSessionAttributeNames()) {
                final Object attrValue = this.sessionAttributeStore.retrieveAttribute(webRequest, attrName);
                if (attrValue != null) {
                    implicitModel.addAttribute(attrName, attrValue);
                }
            }
            for (final Method attributeMethod : this.methodResolver.getModelAttributeMethods()) {
                final Method attributeMethodToInvoke = BridgeMethodResolver.findBridgedMethod(attributeMethod);
                final Object[] args = this.resolveHandlerArguments(attributeMethodToInvoke, handler, webRequest, implicitModel);
                if (debug) {
                    HandlerMethodInvoker.logger.debug("Invoking model attribute method: " + attributeMethodToInvoke);
                }
                String attrName2 = AnnotationUtils.findAnnotation(attributeMethod, ModelAttribute.class).value();
                if (!"".equals(attrName2) && implicitModel.containsAttribute(attrName2)) {
                    continue;
                }
                ReflectionUtils.makeAccessible(attributeMethodToInvoke);
                final Object attrValue2 = attributeMethodToInvoke.invoke(handler, args);
                if ("".equals(attrName2)) {
                    final Class<?> resolvedType = GenericTypeResolver.resolveReturnType(attributeMethodToInvoke, handler.getClass());
                    attrName2 = Conventions.getVariableNameForReturnType(attributeMethodToInvoke, resolvedType, attrValue2);
                }
                if (implicitModel.containsAttribute(attrName2)) {
                    continue;
                }
                implicitModel.addAttribute(attrName2, attrValue2);
            }
            final Object[] args2 = this.resolveHandlerArguments(handlerMethodToInvoke, handler, webRequest, implicitModel);
            if (debug) {
                HandlerMethodInvoker.logger.debug("Invoking request handler method: " + handlerMethodToInvoke);
            }
            ReflectionUtils.makeAccessible(handlerMethodToInvoke);
            return handlerMethodToInvoke.invoke(handler, args2);
        }
        catch (IllegalStateException ex) {
            throw new HandlerMethodInvocationException(handlerMethodToInvoke, ex);
        }
        catch (InvocationTargetException ex2) {
            ReflectionUtils.rethrowException(ex2.getTargetException());
            return null;
        }
    }
    
    public final void updateModelAttributes(final Object handler, final Map<String, Object> mavModel, final ExtendedModelMap implicitModel, final NativeWebRequest webRequest) throws Exception {
        if (this.methodResolver.hasSessionAttributes() && this.sessionStatus.isComplete()) {
            for (final String attrName : this.methodResolver.getActualSessionAttributeNames()) {
                this.sessionAttributeStore.cleanupAttribute(webRequest, attrName);
            }
        }
        final Map<String, Object> model = (mavModel != null) ? mavModel : implicitModel;
        if (model != null) {
            try {
                final String[] array;
                final String[] originalAttrNames = array = model.keySet().toArray(new String[model.size()]);
                for (final String attrName2 : array) {
                    final Object attrValue = model.get(attrName2);
                    final boolean isSessionAttr = this.methodResolver.isSessionAttribute(attrName2, (attrValue != null) ? attrValue.getClass() : null);
                    if (isSessionAttr) {
                        if (this.sessionStatus.isComplete()) {
                            ((HashMap<String, Boolean>)implicitModel).put(HandlerMethodInvoker.MODEL_KEY_PREFIX_STALE + attrName2, Boolean.TRUE);
                        }
                        else if (!implicitModel.containsKey(HandlerMethodInvoker.MODEL_KEY_PREFIX_STALE + attrName2)) {
                            this.sessionAttributeStore.storeAttribute(webRequest, attrName2, attrValue);
                        }
                    }
                    if (!attrName2.startsWith(BindingResult.MODEL_KEY_PREFIX) && (isSessionAttr || this.isBindingCandidate(attrValue))) {
                        final String bindingResultKey = BindingResult.MODEL_KEY_PREFIX + attrName2;
                        if (mavModel != null && !model.containsKey(bindingResultKey)) {
                            final WebDataBinder binder = this.createBinder(webRequest, attrValue, attrName2);
                            this.initBinder(handler, attrName2, binder, webRequest);
                            mavModel.put(bindingResultKey, binder.getBindingResult());
                        }
                    }
                }
            }
            catch (InvocationTargetException ex) {
                ReflectionUtils.rethrowException(ex.getTargetException());
            }
        }
    }
    
    private Object[] resolveHandlerArguments(final Method handlerMethod, final Object handler, final NativeWebRequest webRequest, final ExtendedModelMap implicitModel) throws Exception {
        final Class<?>[] paramTypes = handlerMethod.getParameterTypes();
        final Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < args.length; ++i) {
            final MethodParameter methodParam = new MethodParameter(handlerMethod, i);
            methodParam.initParameterNameDiscovery(this.parameterNameDiscoverer);
            GenericTypeResolver.resolveParameterType(methodParam, handler.getClass());
            String paramName = null;
            String headerName = null;
            boolean requestBodyFound = false;
            String cookieName = null;
            String pathVarName = null;
            String attrName = null;
            boolean required = false;
            String defaultValue = null;
            boolean validate = false;
            Object[] validationHints = null;
            int annotationsFound = 0;
            final Annotation[] parameterAnnotations;
            final Annotation[] paramAnns = parameterAnnotations = methodParam.getParameterAnnotations();
            for (final Annotation paramAnn : parameterAnnotations) {
                if (RequestParam.class.isInstance(paramAnn)) {
                    final RequestParam requestParam = (RequestParam)paramAnn;
                    paramName = requestParam.value();
                    required = requestParam.required();
                    defaultValue = this.parseDefaultValueAttribute(requestParam.defaultValue());
                    ++annotationsFound;
                }
                else if (RequestHeader.class.isInstance(paramAnn)) {
                    final RequestHeader requestHeader = (RequestHeader)paramAnn;
                    headerName = requestHeader.value();
                    required = requestHeader.required();
                    defaultValue = this.parseDefaultValueAttribute(requestHeader.defaultValue());
                    ++annotationsFound;
                }
                else if (RequestBody.class.isInstance(paramAnn)) {
                    requestBodyFound = true;
                    ++annotationsFound;
                }
                else if (CookieValue.class.isInstance(paramAnn)) {
                    final CookieValue cookieValue = (CookieValue)paramAnn;
                    cookieName = cookieValue.value();
                    required = cookieValue.required();
                    defaultValue = this.parseDefaultValueAttribute(cookieValue.defaultValue());
                    ++annotationsFound;
                }
                else if (PathVariable.class.isInstance(paramAnn)) {
                    final PathVariable pathVar = (PathVariable)paramAnn;
                    pathVarName = pathVar.value();
                    ++annotationsFound;
                }
                else if (ModelAttribute.class.isInstance(paramAnn)) {
                    final ModelAttribute attr = (ModelAttribute)paramAnn;
                    attrName = attr.value();
                    ++annotationsFound;
                }
                else if (Value.class.isInstance(paramAnn)) {
                    defaultValue = ((Value)paramAnn).value();
                }
                else if (paramAnn.annotationType().getSimpleName().startsWith("Valid")) {
                    validate = true;
                    final Object value = AnnotationUtils.getValue(paramAnn);
                    validationHints = (Object[])((value instanceof Object[]) ? value : new Object[] { value });
                }
            }
            if (annotationsFound > 1) {
                throw new IllegalStateException("Handler parameter annotations are exclusive choices - do not specify more than one such annotation on the same parameter: " + handlerMethod);
            }
            if (annotationsFound == 0) {
                final Object argValue = this.resolveCommonArgument(methodParam, webRequest);
                if (argValue != WebArgumentResolver.UNRESOLVED) {
                    args[i] = argValue;
                }
                else if (defaultValue != null) {
                    args[i] = this.resolveDefaultValue(defaultValue);
                }
                else {
                    final Class<?> paramType = methodParam.getParameterType();
                    if (Model.class.isAssignableFrom(paramType) || Map.class.isAssignableFrom(paramType)) {
                        if (!paramType.isAssignableFrom(implicitModel.getClass())) {
                            throw new IllegalStateException("Argument [" + paramType.getSimpleName() + "] is of type " + "Model or Map but is not assignable from the actual model. You may need to switch " + "newer MVC infrastructure classes to use this argument.");
                        }
                        args[i] = implicitModel;
                    }
                    else if (SessionStatus.class.isAssignableFrom(paramType)) {
                        args[i] = this.sessionStatus;
                    }
                    else if (HttpEntity.class.isAssignableFrom(paramType)) {
                        args[i] = this.resolveHttpEntityRequest(methodParam, webRequest);
                    }
                    else {
                        if (Errors.class.isAssignableFrom(paramType)) {
                            throw new IllegalStateException("Errors/BindingResult argument declared without preceding model attribute. Check your handler method signature!");
                        }
                        if (BeanUtils.isSimpleProperty(paramType)) {
                            paramName = "";
                        }
                        else {
                            attrName = "";
                        }
                    }
                }
            }
            if (paramName != null) {
                args[i] = this.resolveRequestParam(paramName, required, defaultValue, methodParam, webRequest, handler);
            }
            else if (headerName != null) {
                args[i] = this.resolveRequestHeader(headerName, required, defaultValue, methodParam, webRequest, handler);
            }
            else if (requestBodyFound) {
                args[i] = this.resolveRequestBody(methodParam, webRequest, handler);
            }
            else if (cookieName != null) {
                args[i] = this.resolveCookieValue(cookieName, required, defaultValue, methodParam, webRequest, handler);
            }
            else if (pathVarName != null) {
                args[i] = this.resolvePathVariable(pathVarName, methodParam, webRequest, handler);
            }
            else if (attrName != null) {
                final WebDataBinder binder = this.resolveModelAttribute(attrName, methodParam, implicitModel, webRequest, handler);
                final boolean assignBindingResult = args.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]);
                if (binder.getTarget() != null) {
                    this.doBind(binder, webRequest, validate, validationHints, !assignBindingResult);
                }
                args[i] = binder.getTarget();
                if (assignBindingResult) {
                    args[i + 1] = binder.getBindingResult();
                    ++i;
                }
                implicitModel.putAll(binder.getBindingResult().getModel());
            }
        }
        return args;
    }
    
    protected void initBinder(final Object handler, final String attrName, final WebDataBinder binder, final NativeWebRequest webRequest) throws Exception {
        if (this.bindingInitializer != null) {
            this.bindingInitializer.initBinder(binder, webRequest);
        }
        if (handler != null) {
            final Set<Method> initBinderMethods = this.methodResolver.getInitBinderMethods();
            if (!initBinderMethods.isEmpty()) {
                final boolean debug = HandlerMethodInvoker.logger.isDebugEnabled();
                for (final Method initBinderMethod : initBinderMethods) {
                    final Method methodToInvoke = BridgeMethodResolver.findBridgedMethod(initBinderMethod);
                    final String[] targetNames = AnnotationUtils.findAnnotation(initBinderMethod, InitBinder.class).value();
                    if (targetNames.length == 0 || Arrays.asList(targetNames).contains(attrName)) {
                        final Object[] initBinderArgs = this.resolveInitBinderArguments(handler, methodToInvoke, binder, webRequest);
                        if (debug) {
                            HandlerMethodInvoker.logger.debug("Invoking init-binder method: " + methodToInvoke);
                        }
                        ReflectionUtils.makeAccessible(methodToInvoke);
                        final Object returnValue = methodToInvoke.invoke(handler, initBinderArgs);
                        if (returnValue != null) {
                            throw new IllegalStateException("InitBinder methods must not have a return value: " + methodToInvoke);
                        }
                        continue;
                    }
                }
            }
        }
    }
    
    private Object[] resolveInitBinderArguments(final Object handler, final Method initBinderMethod, final WebDataBinder binder, final NativeWebRequest webRequest) throws Exception {
        final Class<?>[] initBinderParams = initBinderMethod.getParameterTypes();
        final Object[] initBinderArgs = new Object[initBinderParams.length];
        for (int i = 0; i < initBinderArgs.length; ++i) {
            final MethodParameter methodParam = new MethodParameter(initBinderMethod, i);
            methodParam.initParameterNameDiscovery(this.parameterNameDiscoverer);
            GenericTypeResolver.resolveParameterType(methodParam, handler.getClass());
            String paramName = null;
            boolean paramRequired = false;
            String paramDefaultValue = null;
            String pathVarName = null;
            final Annotation[] parameterAnnotations;
            final Annotation[] paramAnns = parameterAnnotations = methodParam.getParameterAnnotations();
            for (final Annotation paramAnn : parameterAnnotations) {
                if (RequestParam.class.isInstance(paramAnn)) {
                    final RequestParam requestParam = (RequestParam)paramAnn;
                    paramName = requestParam.value();
                    paramRequired = requestParam.required();
                    paramDefaultValue = this.parseDefaultValueAttribute(requestParam.defaultValue());
                    break;
                }
                if (ModelAttribute.class.isInstance(paramAnn)) {
                    throw new IllegalStateException("@ModelAttribute is not supported on @InitBinder methods: " + initBinderMethod);
                }
                if (PathVariable.class.isInstance(paramAnn)) {
                    final PathVariable pathVar = (PathVariable)paramAnn;
                    pathVarName = pathVar.value();
                }
            }
            if (paramName == null && pathVarName == null) {
                final Object argValue = this.resolveCommonArgument(methodParam, webRequest);
                if (argValue != WebArgumentResolver.UNRESOLVED) {
                    initBinderArgs[i] = argValue;
                }
                else {
                    final Class<?> paramType = initBinderParams[i];
                    if (paramType.isInstance(binder)) {
                        initBinderArgs[i] = binder;
                    }
                    else {
                        if (!BeanUtils.isSimpleProperty(paramType)) {
                            throw new IllegalStateException("Unsupported argument [" + paramType.getName() + "] for @InitBinder method: " + initBinderMethod);
                        }
                        paramName = "";
                    }
                }
            }
            if (paramName != null) {
                initBinderArgs[i] = this.resolveRequestParam(paramName, paramRequired, paramDefaultValue, methodParam, webRequest, null);
            }
            else if (pathVarName != null) {
                initBinderArgs[i] = this.resolvePathVariable(pathVarName, methodParam, webRequest, null);
            }
        }
        return initBinderArgs;
    }
    
    private Object resolveRequestParam(String paramName, final boolean required, final String defaultValue, final MethodParameter methodParam, final NativeWebRequest webRequest, final Object handlerForInitBinderCall) throws Exception {
        final Class<?> paramType = methodParam.getParameterType();
        if (Map.class.isAssignableFrom(paramType) && paramName.length() == 0) {
            return this.resolveRequestParamMap((Class<? extends Map<?, ?>>)paramType, webRequest);
        }
        if (paramName.length() == 0) {
            paramName = this.getRequiredParameterName(methodParam);
        }
        Object paramValue = null;
        final MultipartRequest multipartRequest = webRequest.getNativeRequest(MultipartRequest.class);
        if (multipartRequest != null) {
            final List<MultipartFile> files = multipartRequest.getFiles(paramName);
            if (!files.isEmpty()) {
                paramValue = ((files.size() == 1) ? files.get(0) : files);
            }
        }
        if (paramValue == null) {
            final String[] paramValues = webRequest.getParameterValues(paramName);
            if (paramValues != null) {
                paramValue = ((paramValues.length == 1) ? paramValues[0] : paramValues);
            }
        }
        if (paramValue == null) {
            if (defaultValue != null) {
                paramValue = this.resolveDefaultValue(defaultValue);
            }
            else if (required) {
                this.raiseMissingParameterException(paramName, paramType);
            }
            paramValue = this.checkValue(paramName, paramValue, paramType);
        }
        final WebDataBinder binder = this.createBinder(webRequest, null, paramName);
        this.initBinder(handlerForInitBinderCall, paramName, binder, webRequest);
        return binder.convertIfNecessary(paramValue, paramType, methodParam);
    }
    
    private Map<String, ?> resolveRequestParamMap(final Class<? extends Map<?, ?>> mapType, final NativeWebRequest webRequest) {
        final Map<String, String[]> parameterMap = webRequest.getParameterMap();
        if (MultiValueMap.class.isAssignableFrom(mapType)) {
            final MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>(parameterMap.size());
            for (final Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                for (final String value : entry.getValue()) {
                    result.add(entry.getKey(), value);
                }
            }
            return result;
        }
        final Map<String, String> result2 = new LinkedHashMap<String, String>(parameterMap.size());
        for (final Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getValue().length > 0) {
                result2.put(entry.getKey(), entry.getValue()[0]);
            }
        }
        return result2;
    }
    
    private Object resolveRequestHeader(String headerName, final boolean required, final String defaultValue, final MethodParameter methodParam, final NativeWebRequest webRequest, final Object handlerForInitBinderCall) throws Exception {
        final Class<?> paramType = methodParam.getParameterType();
        if (Map.class.isAssignableFrom(paramType)) {
            return this.resolveRequestHeaderMap((Class<? extends Map<?, ?>>)paramType, webRequest);
        }
        if (headerName.length() == 0) {
            headerName = this.getRequiredParameterName(methodParam);
        }
        Object headerValue = null;
        final String[] headerValues = webRequest.getHeaderValues(headerName);
        if (headerValues != null) {
            headerValue = ((headerValues.length == 1) ? headerValues[0] : headerValues);
        }
        if (headerValue == null) {
            if (defaultValue != null) {
                headerValue = this.resolveDefaultValue(defaultValue);
            }
            else if (required) {
                this.raiseMissingHeaderException(headerName, paramType);
            }
            headerValue = this.checkValue(headerName, headerValue, paramType);
        }
        final WebDataBinder binder = this.createBinder(webRequest, null, headerName);
        this.initBinder(handlerForInitBinderCall, headerName, binder, webRequest);
        return binder.convertIfNecessary(headerValue, paramType, methodParam);
    }
    
    private Map<String, ?> resolveRequestHeaderMap(final Class<? extends Map<?, ?>> mapType, final NativeWebRequest webRequest) {
        if (MultiValueMap.class.isAssignableFrom(mapType)) {
            MultiValueMap<String, String> result;
            if (HttpHeaders.class.isAssignableFrom(mapType)) {
                result = new HttpHeaders();
            }
            else {
                result = new LinkedMultiValueMap<String, String>();
            }
            final Iterator<String> iterator = webRequest.getHeaderNames();
            while (iterator.hasNext()) {
                final String headerName = iterator.next();
                for (final String headerValue : webRequest.getHeaderValues(headerName)) {
                    result.add(headerName, headerValue);
                }
            }
            return result;
        }
        final Map<String, String> result2 = new LinkedHashMap<String, String>();
        final Iterator<String> iterator = webRequest.getHeaderNames();
        while (iterator.hasNext()) {
            final String headerName = iterator.next();
            final String headerValue2 = webRequest.getHeader(headerName);
            result2.put(headerName, headerValue2);
        }
        return result2;
    }
    
    protected Object resolveRequestBody(final MethodParameter methodParam, final NativeWebRequest webRequest, final Object handler) throws Exception {
        return this.readWithMessageConverters(methodParam, this.createHttpInputMessage(webRequest), methodParam.getParameterType());
    }
    
    private HttpEntity<?> resolveHttpEntityRequest(final MethodParameter methodParam, final NativeWebRequest webRequest) throws Exception {
        final HttpInputMessage inputMessage = this.createHttpInputMessage(webRequest);
        final Class<?> paramType = this.getHttpEntityType(methodParam);
        final Object body = this.readWithMessageConverters(methodParam, inputMessage, paramType);
        return new HttpEntity<Object>(body, inputMessage.getHeaders());
    }
    
    private Object readWithMessageConverters(final MethodParameter methodParam, final HttpInputMessage inputMessage, final Class<?> paramType) throws Exception {
        final MediaType contentType = inputMessage.getHeaders().getContentType();
        if (contentType == null) {
            final StringBuilder builder = new StringBuilder(ClassUtils.getShortName(methodParam.getParameterType()));
            final String paramName = methodParam.getParameterName();
            if (paramName != null) {
                builder.append(' ');
                builder.append(paramName);
            }
            throw new HttpMediaTypeNotSupportedException("Cannot extract parameter (" + builder.toString() + "): no Content-Type found");
        }
        final List<MediaType> allSupportedMediaTypes = new ArrayList<MediaType>();
        if (this.messageConverters != null) {
            for (final HttpMessageConverter<?> messageConverter : this.messageConverters) {
                allSupportedMediaTypes.addAll(messageConverter.getSupportedMediaTypes());
                if (messageConverter.canRead(paramType, contentType)) {
                    if (HandlerMethodInvoker.logger.isDebugEnabled()) {
                        HandlerMethodInvoker.logger.debug("Reading [" + paramType.getName() + "] as \"" + contentType + "\" using [" + messageConverter + "]");
                    }
                    return messageConverter.read(paramType, inputMessage);
                }
            }
        }
        throw new HttpMediaTypeNotSupportedException(contentType, allSupportedMediaTypes);
    }
    
    private Class<?> getHttpEntityType(final MethodParameter methodParam) {
        Assert.isAssignable(HttpEntity.class, methodParam.getParameterType());
        final ParameterizedType type = (ParameterizedType)methodParam.getGenericParameterType();
        if (type.getActualTypeArguments().length == 1) {
            final Type typeArgument = type.getActualTypeArguments()[0];
            if (typeArgument instanceof Class) {
                return (Class<?>)typeArgument;
            }
            if (typeArgument instanceof GenericArrayType) {
                final Type componentType = ((GenericArrayType)typeArgument).getGenericComponentType();
                if (componentType instanceof Class) {
                    final Object array = Array.newInstance((Class<?>)componentType, 0);
                    return array.getClass();
                }
            }
        }
        throw new IllegalArgumentException("HttpEntity parameter (" + methodParam.getParameterName() + ") is not parameterized");
    }
    
    private Object resolveCookieValue(String cookieName, final boolean required, final String defaultValue, final MethodParameter methodParam, final NativeWebRequest webRequest, final Object handlerForInitBinderCall) throws Exception {
        final Class<?> paramType = methodParam.getParameterType();
        if (cookieName.length() == 0) {
            cookieName = this.getRequiredParameterName(methodParam);
        }
        Object cookieValue = this.resolveCookieValue(cookieName, paramType, webRequest);
        if (cookieValue == null) {
            if (defaultValue != null) {
                cookieValue = this.resolveDefaultValue(defaultValue);
            }
            else if (required) {
                this.raiseMissingCookieException(cookieName, paramType);
            }
            cookieValue = this.checkValue(cookieName, cookieValue, paramType);
        }
        final WebDataBinder binder = this.createBinder(webRequest, null, cookieName);
        this.initBinder(handlerForInitBinderCall, cookieName, binder, webRequest);
        return binder.convertIfNecessary(cookieValue, paramType, methodParam);
    }
    
    protected Object resolveCookieValue(final String cookieName, final Class<?> paramType, final NativeWebRequest webRequest) throws Exception {
        throw new UnsupportedOperationException("@CookieValue not supported");
    }
    
    private Object resolvePathVariable(String pathVarName, final MethodParameter methodParam, final NativeWebRequest webRequest, final Object handlerForInitBinderCall) throws Exception {
        final Class<?> paramType = methodParam.getParameterType();
        if (pathVarName.length() == 0) {
            pathVarName = this.getRequiredParameterName(methodParam);
        }
        final String pathVarValue = this.resolvePathVariable(pathVarName, paramType, webRequest);
        final WebDataBinder binder = this.createBinder(webRequest, null, pathVarName);
        this.initBinder(handlerForInitBinderCall, pathVarName, binder, webRequest);
        return binder.convertIfNecessary(pathVarValue, paramType, methodParam);
    }
    
    protected String resolvePathVariable(final String pathVarName, final Class<?> paramType, final NativeWebRequest webRequest) throws Exception {
        throw new UnsupportedOperationException("@PathVariable not supported");
    }
    
    private String getRequiredParameterName(final MethodParameter methodParam) {
        final String name = methodParam.getParameterName();
        if (name == null) {
            throw new IllegalStateException("No parameter name specified for argument of type [" + methodParam.getParameterType().getName() + "], and no parameter name information found in class file either.");
        }
        return name;
    }
    
    private Object checkValue(final String name, final Object value, final Class<?> paramType) {
        if (value == null) {
            if (Boolean.TYPE.equals(paramType)) {
                return Boolean.FALSE;
            }
            if (paramType.isPrimitive()) {
                throw new IllegalStateException("Optional " + paramType + " parameter '" + name + "' is not present but cannot be translated into a null value due to being declared as a " + "primitive type. Consider declaring it as object wrapper for the corresponding primitive type.");
            }
        }
        return value;
    }
    
    private WebDataBinder resolveModelAttribute(final String attrName, final MethodParameter methodParam, final ExtendedModelMap implicitModel, final NativeWebRequest webRequest, final Object handler) throws Exception {
        String name = attrName;
        if ("".equals(name)) {
            name = Conventions.getVariableNameForParameter(methodParam);
        }
        final Class<?> paramType = methodParam.getParameterType();
        Object bindObject;
        if (implicitModel.containsKey(name)) {
            bindObject = ((LinkedHashMap<K, Object>)implicitModel).get(name);
        }
        else if (this.methodResolver.isSessionAttribute(name, paramType)) {
            bindObject = this.sessionAttributeStore.retrieveAttribute(webRequest, name);
            if (bindObject == null) {
                this.raiseSessionRequiredException("Session attribute '" + name + "' required - not found in session");
            }
        }
        else {
            bindObject = BeanUtils.instantiateClass(paramType);
        }
        final WebDataBinder binder = this.createBinder(webRequest, bindObject, name);
        this.initBinder(handler, name, binder, webRequest);
        return binder;
    }
    
    protected boolean isBindingCandidate(final Object value) {
        return value != null && !value.getClass().isArray() && !(value instanceof Collection) && !(value instanceof Map) && !BeanUtils.isSimpleValueType(value.getClass());
    }
    
    protected void raiseMissingParameterException(final String paramName, final Class<?> paramType) throws Exception {
        throw new IllegalStateException("Missing parameter '" + paramName + "' of type [" + paramType.getName() + "]");
    }
    
    protected void raiseMissingHeaderException(final String headerName, final Class<?> paramType) throws Exception {
        throw new IllegalStateException("Missing header '" + headerName + "' of type [" + paramType.getName() + "]");
    }
    
    protected void raiseMissingCookieException(final String cookieName, final Class<?> paramType) throws Exception {
        throw new IllegalStateException("Missing cookie value '" + cookieName + "' of type [" + paramType.getName() + "]");
    }
    
    protected void raiseSessionRequiredException(final String message) throws Exception {
        throw new IllegalStateException(message);
    }
    
    protected WebDataBinder createBinder(final NativeWebRequest webRequest, final Object target, final String objectName) throws Exception {
        return new WebRequestDataBinder(target, objectName);
    }
    
    private void doBind(final WebDataBinder binder, final NativeWebRequest webRequest, final boolean validate, final Object[] validationHints, final boolean failOnErrors) throws Exception {
        this.doBind(binder, webRequest);
        if (validate) {
            binder.validate(validationHints);
        }
        if (failOnErrors && binder.getBindingResult().hasErrors()) {
            throw new BindException(binder.getBindingResult());
        }
    }
    
    protected void doBind(final WebDataBinder binder, final NativeWebRequest webRequest) throws Exception {
        ((WebRequestDataBinder)binder).bind(webRequest);
    }
    
    protected HttpInputMessage createHttpInputMessage(final NativeWebRequest webRequest) throws Exception {
        throw new UnsupportedOperationException("@RequestBody not supported");
    }
    
    protected HttpOutputMessage createHttpOutputMessage(final NativeWebRequest webRequest) throws Exception {
        throw new UnsupportedOperationException("@Body not supported");
    }
    
    protected String parseDefaultValueAttribute(final String value) {
        return "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n".equals(value) ? null : value;
    }
    
    protected Object resolveDefaultValue(final String value) {
        return value;
    }
    
    protected Object resolveCommonArgument(final MethodParameter methodParameter, final NativeWebRequest webRequest) throws Exception {
        if (this.customArgumentResolvers != null) {
            for (final WebArgumentResolver argumentResolver : this.customArgumentResolvers) {
                final Object value = argumentResolver.resolveArgument(methodParameter, webRequest);
                if (value != WebArgumentResolver.UNRESOLVED) {
                    return value;
                }
            }
        }
        final Class<?> paramType = methodParameter.getParameterType();
        final Object value2 = this.resolveStandardArgument(paramType, webRequest);
        if (value2 != WebArgumentResolver.UNRESOLVED && !ClassUtils.isAssignableValue(paramType, value2)) {
            throw new IllegalStateException("Standard argument type [" + paramType.getName() + "] resolved to incompatible value of type [" + ((value2 != null) ? value2.getClass() : null) + "]. Consider declaring the argument type in a less specific fashion.");
        }
        return value2;
    }
    
    protected Object resolveStandardArgument(final Class<?> parameterType, final NativeWebRequest webRequest) throws Exception {
        if (WebRequest.class.isAssignableFrom(parameterType)) {
            return webRequest;
        }
        return WebArgumentResolver.UNRESOLVED;
    }
    
    protected final void addReturnValueAsModelAttribute(final Method handlerMethod, final Class<?> handlerType, final Object returnValue, final ExtendedModelMap implicitModel) {
        final ModelAttribute attr = AnnotationUtils.findAnnotation(handlerMethod, ModelAttribute.class);
        String attrName = (attr != null) ? attr.value() : "";
        if ("".equals(attrName)) {
            final Class<?> resolvedType = GenericTypeResolver.resolveReturnType(handlerMethod, handlerType);
            attrName = Conventions.getVariableNameForReturnType(handlerMethod, resolvedType, returnValue);
        }
        implicitModel.addAttribute(attrName, returnValue);
    }
    
    static {
        MODEL_KEY_PREFIX_STALE = SessionAttributeStore.class.getName() + ".STALE.";
        logger = LogFactory.getLog(HandlerMethodInvoker.class);
    }
}
