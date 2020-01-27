// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyBatchUpdateException;
import java.util.Map;
import java.util.HashMap;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import java.lang.reflect.Field;
import org.springframework.core.MethodParameter;
import org.springframework.beans.TypeMismatchException;
import java.beans.PropertyEditor;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import org.springframework.util.StringUtils;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.util.Assert;
import java.util.ArrayList;
import org.springframework.core.convert.ConversionService;
import java.util.List;
import org.springframework.beans.SimpleTypeConverter;
import org.apache.commons.logging.Log;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.PropertyEditorRegistry;

public class DataBinder implements PropertyEditorRegistry, TypeConverter
{
    public static final String DEFAULT_OBJECT_NAME = "target";
    public static final int DEFAULT_AUTO_GROW_COLLECTION_LIMIT = 256;
    protected static final Log logger;
    private final Object target;
    private final String objectName;
    private AbstractPropertyBindingResult bindingResult;
    private SimpleTypeConverter typeConverter;
    private boolean ignoreUnknownFields;
    private boolean ignoreInvalidFields;
    private boolean autoGrowNestedPaths;
    private int autoGrowCollectionLimit;
    private String[] allowedFields;
    private String[] disallowedFields;
    private String[] requiredFields;
    private BindingErrorProcessor bindingErrorProcessor;
    private final List<Validator> validators;
    private ConversionService conversionService;
    
    public DataBinder(final Object target) {
        this(target, "target");
    }
    
    public DataBinder(final Object target, final String objectName) {
        this.ignoreUnknownFields = true;
        this.ignoreInvalidFields = false;
        this.autoGrowNestedPaths = true;
        this.autoGrowCollectionLimit = 256;
        this.bindingErrorProcessor = new DefaultBindingErrorProcessor();
        this.validators = new ArrayList<Validator>();
        this.target = target;
        this.objectName = objectName;
    }
    
    public Object getTarget() {
        return this.target;
    }
    
    public String getObjectName() {
        return this.objectName;
    }
    
    public void setAutoGrowNestedPaths(final boolean autoGrowNestedPaths) {
        Assert.state(this.bindingResult == null, "DataBinder is already initialized - call setAutoGrowNestedPaths before other configuration methods");
        this.autoGrowNestedPaths = autoGrowNestedPaths;
    }
    
    public boolean isAutoGrowNestedPaths() {
        return this.autoGrowNestedPaths;
    }
    
    public void setAutoGrowCollectionLimit(final int autoGrowCollectionLimit) {
        this.autoGrowCollectionLimit = autoGrowCollectionLimit;
    }
    
    public int getAutoGrowCollectionLimit() {
        return this.autoGrowCollectionLimit;
    }
    
    public void initBeanPropertyAccess() {
        Assert.state(this.bindingResult == null, "DataBinder is already initialized - call initBeanPropertyAccess before other configuration methods");
        this.bindingResult = new BeanPropertyBindingResult(this.getTarget(), this.getObjectName(), this.isAutoGrowNestedPaths(), this.getAutoGrowCollectionLimit());
        if (this.conversionService != null) {
            this.bindingResult.initConversion(this.conversionService);
        }
    }
    
    public void initDirectFieldAccess() {
        Assert.state(this.bindingResult == null, "DataBinder is already initialized - call initDirectFieldAccess before other configuration methods");
        this.bindingResult = new DirectFieldBindingResult(this.getTarget(), this.getObjectName());
        if (this.conversionService != null) {
            this.bindingResult.initConversion(this.conversionService);
        }
    }
    
    protected AbstractPropertyBindingResult getInternalBindingResult() {
        if (this.bindingResult == null) {
            this.initBeanPropertyAccess();
        }
        return this.bindingResult;
    }
    
    protected ConfigurablePropertyAccessor getPropertyAccessor() {
        return this.getInternalBindingResult().getPropertyAccessor();
    }
    
    protected SimpleTypeConverter getSimpleTypeConverter() {
        if (this.typeConverter == null) {
            this.typeConverter = new SimpleTypeConverter();
            if (this.conversionService != null) {
                this.typeConverter.setConversionService(this.conversionService);
            }
        }
        return this.typeConverter;
    }
    
    protected PropertyEditorRegistry getPropertyEditorRegistry() {
        if (this.getTarget() != null) {
            return this.getInternalBindingResult().getPropertyAccessor();
        }
        return this.getSimpleTypeConverter();
    }
    
    protected TypeConverter getTypeConverter() {
        if (this.getTarget() != null) {
            return this.getInternalBindingResult().getPropertyAccessor();
        }
        return this.getSimpleTypeConverter();
    }
    
    public BindingResult getBindingResult() {
        return this.getInternalBindingResult();
    }
    
    public void setIgnoreUnknownFields(final boolean ignoreUnknownFields) {
        this.ignoreUnknownFields = ignoreUnknownFields;
    }
    
    public boolean isIgnoreUnknownFields() {
        return this.ignoreUnknownFields;
    }
    
    public void setIgnoreInvalidFields(final boolean ignoreInvalidFields) {
        this.ignoreInvalidFields = ignoreInvalidFields;
    }
    
    public boolean isIgnoreInvalidFields() {
        return this.ignoreInvalidFields;
    }
    
    public void setAllowedFields(final String... allowedFields) {
        this.allowedFields = PropertyAccessorUtils.canonicalPropertyNames(allowedFields);
    }
    
    public String[] getAllowedFields() {
        return this.allowedFields;
    }
    
    public void setDisallowedFields(final String... disallowedFields) {
        this.disallowedFields = PropertyAccessorUtils.canonicalPropertyNames(disallowedFields);
    }
    
    public String[] getDisallowedFields() {
        return this.disallowedFields;
    }
    
    public void setRequiredFields(final String... requiredFields) {
        this.requiredFields = PropertyAccessorUtils.canonicalPropertyNames(requiredFields);
        if (DataBinder.logger.isDebugEnabled()) {
            DataBinder.logger.debug("DataBinder requires binding of required fields [" + StringUtils.arrayToCommaDelimitedString(requiredFields) + "]");
        }
    }
    
    public String[] getRequiredFields() {
        return this.requiredFields;
    }
    
    public void setExtractOldValueForEditor(final boolean extractOldValueForEditor) {
        this.getPropertyAccessor().setExtractOldValueForEditor(extractOldValueForEditor);
    }
    
    public void setMessageCodesResolver(final MessageCodesResolver messageCodesResolver) {
        this.getInternalBindingResult().setMessageCodesResolver(messageCodesResolver);
    }
    
    public void setBindingErrorProcessor(final BindingErrorProcessor bindingErrorProcessor) {
        Assert.notNull(bindingErrorProcessor, "BindingErrorProcessor must not be null");
        this.bindingErrorProcessor = bindingErrorProcessor;
    }
    
    public BindingErrorProcessor getBindingErrorProcessor() {
        return this.bindingErrorProcessor;
    }
    
    public void setValidator(final Validator validator) {
        this.assertValidators(validator);
        this.validators.clear();
        this.validators.add(validator);
    }
    
    private void assertValidators(final Validator... validators) {
        Assert.notNull(validators, "Validators required");
        for (final Validator validator : validators) {
            if (validator != null && this.getTarget() != null && !validator.supports(this.getTarget().getClass())) {
                throw new IllegalStateException("Invalid target for Validator [" + validator + "]: " + this.getTarget());
            }
        }
    }
    
    public void addValidators(final Validator... validators) {
        this.assertValidators(validators);
        this.validators.addAll(Arrays.asList(validators));
    }
    
    public void replaceValidators(final Validator... validators) {
        this.assertValidators(validators);
        this.validators.clear();
        this.validators.addAll(Arrays.asList(validators));
    }
    
    public Validator getValidator() {
        return (this.validators.size() > 0) ? this.validators.get(0) : null;
    }
    
    public List<Validator> getValidators() {
        return Collections.unmodifiableList((List<? extends Validator>)this.validators);
    }
    
    public void setConversionService(final ConversionService conversionService) {
        Assert.state(this.conversionService == null, "DataBinder is already initialized with ConversionService");
        this.conversionService = conversionService;
        if (this.bindingResult != null && conversionService != null) {
            this.bindingResult.initConversion(conversionService);
        }
    }
    
    public ConversionService getConversionService() {
        return this.conversionService;
    }
    
    @Override
    public void registerCustomEditor(final Class<?> requiredType, final PropertyEditor propertyEditor) {
        this.getPropertyEditorRegistry().registerCustomEditor(requiredType, propertyEditor);
    }
    
    @Override
    public void registerCustomEditor(final Class<?> requiredType, final String field, final PropertyEditor propertyEditor) {
        this.getPropertyEditorRegistry().registerCustomEditor(requiredType, field, propertyEditor);
    }
    
    @Override
    public PropertyEditor findCustomEditor(final Class<?> requiredType, final String propertyPath) {
        return this.getPropertyEditorRegistry().findCustomEditor(requiredType, propertyPath);
    }
    
    @Override
    public <T> T convertIfNecessary(final Object value, final Class<T> requiredType) throws TypeMismatchException {
        return this.getTypeConverter().convertIfNecessary(value, requiredType);
    }
    
    @Override
    public <T> T convertIfNecessary(final Object value, final Class<T> requiredType, final MethodParameter methodParam) throws TypeMismatchException {
        return this.getTypeConverter().convertIfNecessary(value, requiredType, methodParam);
    }
    
    @Override
    public <T> T convertIfNecessary(final Object value, final Class<T> requiredType, final Field field) throws TypeMismatchException {
        return this.getTypeConverter().convertIfNecessary(value, requiredType, field);
    }
    
    public void bind(final PropertyValues pvs) {
        final MutablePropertyValues mpvs = (MutablePropertyValues)((pvs instanceof MutablePropertyValues) ? pvs : new MutablePropertyValues(pvs));
        this.doBind(mpvs);
    }
    
    protected void doBind(final MutablePropertyValues mpvs) {
        this.checkAllowedFields(mpvs);
        this.checkRequiredFields(mpvs);
        this.applyPropertyValues(mpvs);
    }
    
    protected void checkAllowedFields(final MutablePropertyValues mpvs) {
        final PropertyValue[] propertyValues;
        final PropertyValue[] pvs = propertyValues = mpvs.getPropertyValues();
        for (final PropertyValue pv : propertyValues) {
            final String field = PropertyAccessorUtils.canonicalPropertyName(pv.getName());
            if (!this.isAllowed(field)) {
                mpvs.removePropertyValue(pv);
                this.getBindingResult().recordSuppressedField(field);
                if (DataBinder.logger.isDebugEnabled()) {
                    DataBinder.logger.debug("Field [" + field + "] has been removed from PropertyValues " + "and will not be bound, because it has not been found in the list of allowed fields");
                }
            }
        }
    }
    
    protected boolean isAllowed(final String field) {
        final String[] allowed = this.getAllowedFields();
        final String[] disallowed = this.getDisallowedFields();
        return (ObjectUtils.isEmpty(allowed) || PatternMatchUtils.simpleMatch(allowed, field)) && (ObjectUtils.isEmpty(disallowed) || !PatternMatchUtils.simpleMatch(disallowed, field));
    }
    
    protected void checkRequiredFields(final MutablePropertyValues mpvs) {
        final String[] requiredFields = this.getRequiredFields();
        if (!ObjectUtils.isEmpty(requiredFields)) {
            final Map<String, PropertyValue> propertyValues = new HashMap<String, PropertyValue>();
            final PropertyValue[] propertyValues2;
            final PropertyValue[] pvs = propertyValues2 = mpvs.getPropertyValues();
            for (final PropertyValue pv : propertyValues2) {
                final String canonicalName = PropertyAccessorUtils.canonicalPropertyName(pv.getName());
                propertyValues.put(canonicalName, pv);
            }
            for (final String field : requiredFields) {
                final PropertyValue pv2 = propertyValues.get(field);
                boolean empty = pv2 == null || pv2.getValue() == null;
                if (!empty) {
                    if (pv2.getValue() instanceof String) {
                        empty = !StringUtils.hasText((String)pv2.getValue());
                    }
                    else if (pv2.getValue() instanceof String[]) {
                        final String[] values = (String[])pv2.getValue();
                        empty = (values.length == 0 || !StringUtils.hasText(values[0]));
                    }
                }
                if (empty) {
                    this.getBindingErrorProcessor().processMissingFieldError(field, this.getInternalBindingResult());
                    if (pv2 != null) {
                        mpvs.removePropertyValue(pv2);
                        propertyValues.remove(field);
                    }
                }
            }
        }
    }
    
    protected void applyPropertyValues(final MutablePropertyValues mpvs) {
        try {
            this.getPropertyAccessor().setPropertyValues(mpvs, this.isIgnoreUnknownFields(), this.isIgnoreInvalidFields());
        }
        catch (PropertyBatchUpdateException ex) {
            for (final PropertyAccessException pae : ex.getPropertyAccessExceptions()) {
                this.getBindingErrorProcessor().processPropertyAccessException(pae, this.getInternalBindingResult());
            }
        }
    }
    
    public void validate() {
        for (final Validator validator : this.validators) {
            validator.validate(this.getTarget(), this.getBindingResult());
        }
    }
    
    public void validate(final Object... validationHints) {
        for (final Validator validator : this.getValidators()) {
            if (!ObjectUtils.isEmpty(validationHints) && validator instanceof SmartValidator) {
                ((SmartValidator)validator).validate(this.getTarget(), this.getBindingResult(), validationHints);
            }
            else {
                if (validator == null) {
                    continue;
                }
                validator.validate(this.getTarget(), this.getBindingResult());
            }
        }
    }
    
    public Map<?, ?> close() throws BindException {
        if (this.getBindingResult().hasErrors()) {
            throw new BindException(this.getBindingResult());
        }
        return this.getBindingResult().getModel();
    }
    
    static {
        logger = LogFactory.getLog(DataBinder.class);
    }
}
