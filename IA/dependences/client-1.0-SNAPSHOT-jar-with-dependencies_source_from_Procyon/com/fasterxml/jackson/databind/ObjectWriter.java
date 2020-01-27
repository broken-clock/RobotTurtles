// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
import com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.core.util.Instantiatable;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import java.util.concurrent.atomic.AtomicReference;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Closeable;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.core.JsonParser;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.Writer;
import com.fasterxml.jackson.core.JsonEncoding;
import java.io.File;
import java.util.Map;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.core.Base64Variant;
import java.util.TimeZone;
import java.util.Locale;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.text.DateFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.FormatFeature;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.core.PrettyPrinter;
import java.io.Serializable;
import com.fasterxml.jackson.core.Versioned;

public class ObjectWriter implements Versioned, Serializable
{
    private static final long serialVersionUID = 1L;
    protected static final PrettyPrinter NULL_PRETTY_PRINTER;
    protected final SerializationConfig _config;
    protected final DefaultSerializerProvider _serializerProvider;
    protected final SerializerFactory _serializerFactory;
    protected final JsonFactory _generatorFactory;
    protected final GeneratorSettings _generatorSettings;
    protected final Prefetch _prefetch;
    
    protected ObjectWriter(final ObjectMapper mapper, final SerializationConfig config, JavaType rootType, final PrettyPrinter pp) {
        this._config = config;
        this._serializerProvider = mapper._serializerProvider;
        this._serializerFactory = mapper._serializerFactory;
        this._generatorFactory = mapper._jsonFactory;
        this._generatorSettings = ((pp == null) ? GeneratorSettings.empty : new GeneratorSettings(pp, null, null, null));
        if (rootType == null || rootType.hasRawClass(Object.class)) {
            this._prefetch = Prefetch.empty;
        }
        else {
            rootType = rootType.withStaticTyping();
            this._prefetch = Prefetch.empty.forRootType(this, rootType);
        }
    }
    
    protected ObjectWriter(final ObjectMapper mapper, final SerializationConfig config) {
        this._config = config;
        this._serializerProvider = mapper._serializerProvider;
        this._serializerFactory = mapper._serializerFactory;
        this._generatorFactory = mapper._jsonFactory;
        this._generatorSettings = GeneratorSettings.empty;
        this._prefetch = Prefetch.empty;
    }
    
    protected ObjectWriter(final ObjectMapper mapper, final SerializationConfig config, final FormatSchema s) {
        this._config = config;
        this._serializerProvider = mapper._serializerProvider;
        this._serializerFactory = mapper._serializerFactory;
        this._generatorFactory = mapper._jsonFactory;
        this._generatorSettings = ((s == null) ? GeneratorSettings.empty : new GeneratorSettings(null, s, null, null));
        this._prefetch = Prefetch.empty;
    }
    
    protected ObjectWriter(final ObjectWriter base, final SerializationConfig config, final GeneratorSettings genSettings, final Prefetch prefetch) {
        this._config = config;
        this._serializerProvider = base._serializerProvider;
        this._serializerFactory = base._serializerFactory;
        this._generatorFactory = base._generatorFactory;
        this._generatorSettings = genSettings;
        this._prefetch = prefetch;
    }
    
    protected ObjectWriter(final ObjectWriter base, final SerializationConfig config) {
        this._config = config;
        this._serializerProvider = base._serializerProvider;
        this._serializerFactory = base._serializerFactory;
        this._generatorFactory = base._generatorFactory;
        this._generatorSettings = base._generatorSettings;
        this._prefetch = base._prefetch;
    }
    
    protected ObjectWriter(final ObjectWriter base, final JsonFactory f) {
        this._config = ((MapperConfigBase<CFG, SerializationConfig>)base._config).with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
        this._serializerProvider = base._serializerProvider;
        this._serializerFactory = base._serializerFactory;
        this._generatorFactory = f;
        this._generatorSettings = base._generatorSettings;
        this._prefetch = base._prefetch;
    }
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    protected ObjectWriter _new(final ObjectWriter base, final JsonFactory f) {
        return new ObjectWriter(base, f);
    }
    
    protected ObjectWriter _new(final ObjectWriter base, final SerializationConfig config) {
        if (config == this._config) {
            return this;
        }
        return new ObjectWriter(base, config);
    }
    
    protected ObjectWriter _new(final GeneratorSettings genSettings, final Prefetch prefetch) {
        if (this._generatorSettings == genSettings && this._prefetch == prefetch) {
            return this;
        }
        return new ObjectWriter(this, this._config, genSettings, prefetch);
    }
    
    protected SequenceWriter _newSequenceWriter(final boolean wrapInArray, final JsonGenerator gen, final boolean managedInput) throws IOException {
        this._configureGenerator(gen);
        return new SequenceWriter(this._serializerProvider(), gen, managedInput, this._prefetch).init(wrapInArray);
    }
    
    public ObjectWriter with(final SerializationFeature feature) {
        return this._new(this, this._config.with(feature));
    }
    
    public ObjectWriter with(final SerializationFeature first, final SerializationFeature... other) {
        return this._new(this, this._config.with(first, other));
    }
    
    public ObjectWriter withFeatures(final SerializationFeature... features) {
        return this._new(this, this._config.withFeatures(features));
    }
    
    public ObjectWriter without(final SerializationFeature feature) {
        return this._new(this, this._config.without(feature));
    }
    
    public ObjectWriter without(final SerializationFeature first, final SerializationFeature... other) {
        return this._new(this, this._config.without(first, other));
    }
    
    public ObjectWriter withoutFeatures(final SerializationFeature... features) {
        return this._new(this, this._config.withoutFeatures(features));
    }
    
    public ObjectWriter with(final JsonGenerator.Feature feature) {
        return this._new(this, this._config.with(feature));
    }
    
    public ObjectWriter withFeatures(final JsonGenerator.Feature... features) {
        return this._new(this, this._config.withFeatures(features));
    }
    
    public ObjectWriter without(final JsonGenerator.Feature feature) {
        return this._new(this, this._config.without(feature));
    }
    
    public ObjectWriter withoutFeatures(final JsonGenerator.Feature... features) {
        return this._new(this, this._config.withoutFeatures(features));
    }
    
    public ObjectWriter with(final FormatFeature feature) {
        return this._new(this, this._config.with(feature));
    }
    
    public ObjectWriter withFeatures(final FormatFeature... features) {
        return this._new(this, this._config.withFeatures(features));
    }
    
    public ObjectWriter without(final FormatFeature feature) {
        return this._new(this, this._config.without(feature));
    }
    
    public ObjectWriter withoutFeatures(final FormatFeature... features) {
        return this._new(this, this._config.withoutFeatures(features));
    }
    
    public ObjectWriter forType(final JavaType rootType) {
        return this._new(this._generatorSettings, this._prefetch.forRootType(this, rootType));
    }
    
    public ObjectWriter forType(final Class<?> rootType) {
        if (rootType == Object.class) {
            return this.forType((JavaType)null);
        }
        return this.forType(this._config.constructType(rootType));
    }
    
    public ObjectWriter forType(final TypeReference<?> rootType) {
        return this.forType(this._config.getTypeFactory().constructType(rootType.getType()));
    }
    
    @Deprecated
    public ObjectWriter withType(final JavaType rootType) {
        return this.forType(rootType);
    }
    
    @Deprecated
    public ObjectWriter withType(final Class<?> rootType) {
        return this.forType(rootType);
    }
    
    @Deprecated
    public ObjectWriter withType(final TypeReference<?> rootType) {
        return this.forType(rootType);
    }
    
    public ObjectWriter with(final DateFormat df) {
        return this._new(this, this._config.with(df));
    }
    
    public ObjectWriter withDefaultPrettyPrinter() {
        return this.with(this._config.getDefaultPrettyPrinter());
    }
    
    public ObjectWriter with(final FilterProvider filterProvider) {
        if (filterProvider == this._config.getFilterProvider()) {
            return this;
        }
        return this._new(this, this._config.withFilters(filterProvider));
    }
    
    public ObjectWriter with(final PrettyPrinter pp) {
        return this._new(this._generatorSettings.with(pp), this._prefetch);
    }
    
    public ObjectWriter withRootName(final String rootName) {
        return this._new(this, ((MapperConfigBase<CFG, SerializationConfig>)this._config).withRootName(rootName));
    }
    
    public ObjectWriter withRootName(final PropertyName rootName) {
        return this._new(this, this._config.withRootName(rootName));
    }
    
    public ObjectWriter withoutRootName() {
        return this._new(this, this._config.withRootName(PropertyName.NO_NAME));
    }
    
    public ObjectWriter with(final FormatSchema schema) {
        this._verifySchemaType(schema);
        return this._new(this._generatorSettings.with(schema), this._prefetch);
    }
    
    @Deprecated
    public ObjectWriter withSchema(final FormatSchema schema) {
        return this.with(schema);
    }
    
    public ObjectWriter withView(final Class<?> view) {
        return this._new(this, this._config.withView(view));
    }
    
    public ObjectWriter with(final Locale l) {
        return this._new(this, ((MapperConfigBase<CFG, SerializationConfig>)this._config).with(l));
    }
    
    public ObjectWriter with(final TimeZone tz) {
        return this._new(this, ((MapperConfigBase<CFG, SerializationConfig>)this._config).with(tz));
    }
    
    public ObjectWriter with(final Base64Variant b64variant) {
        return this._new(this, ((MapperConfigBase<CFG, SerializationConfig>)this._config).with(b64variant));
    }
    
    public ObjectWriter with(final CharacterEscapes escapes) {
        return this._new(this._generatorSettings.with(escapes), this._prefetch);
    }
    
    public ObjectWriter with(final JsonFactory f) {
        return (f == this._generatorFactory) ? this : this._new(this, f);
    }
    
    public ObjectWriter with(final ContextAttributes attrs) {
        return this._new(this, this._config.with(attrs));
    }
    
    public ObjectWriter withAttributes(final Map<?, ?> attrs) {
        return this._new(this, ((MapperConfigBase<CFG, SerializationConfig>)this._config).withAttributes(attrs));
    }
    
    public ObjectWriter withAttribute(final Object key, final Object value) {
        return this._new(this, ((MapperConfigBase<CFG, SerializationConfig>)this._config).withAttribute(key, value));
    }
    
    public ObjectWriter withoutAttribute(final Object key) {
        return this._new(this, ((MapperConfigBase<CFG, SerializationConfig>)this._config).withoutAttribute(key));
    }
    
    public ObjectWriter withRootValueSeparator(final String sep) {
        return this._new(this._generatorSettings.withRootValueSeparator(sep), this._prefetch);
    }
    
    public ObjectWriter withRootValueSeparator(final SerializableString sep) {
        return this._new(this._generatorSettings.withRootValueSeparator(sep), this._prefetch);
    }
    
    public SequenceWriter writeValues(final File out) throws IOException {
        return this._newSequenceWriter(false, this._generatorFactory.createGenerator(out, JsonEncoding.UTF8), true);
    }
    
    public SequenceWriter writeValues(final JsonGenerator gen) throws IOException {
        this._configureGenerator(gen);
        return this._newSequenceWriter(false, gen, false);
    }
    
    public SequenceWriter writeValues(final Writer out) throws IOException {
        return this._newSequenceWriter(false, this._generatorFactory.createGenerator(out), true);
    }
    
    public SequenceWriter writeValues(final OutputStream out) throws IOException {
        return this._newSequenceWriter(false, this._generatorFactory.createGenerator(out, JsonEncoding.UTF8), true);
    }
    
    public SequenceWriter writeValues(final DataOutput out) throws IOException {
        return this._newSequenceWriter(false, this._generatorFactory.createGenerator(out), true);
    }
    
    public SequenceWriter writeValuesAsArray(final File out) throws IOException {
        return this._newSequenceWriter(true, this._generatorFactory.createGenerator(out, JsonEncoding.UTF8), true);
    }
    
    public SequenceWriter writeValuesAsArray(final JsonGenerator gen) throws IOException {
        return this._newSequenceWriter(true, gen, false);
    }
    
    public SequenceWriter writeValuesAsArray(final Writer out) throws IOException {
        return this._newSequenceWriter(true, this._generatorFactory.createGenerator(out), true);
    }
    
    public SequenceWriter writeValuesAsArray(final OutputStream out) throws IOException {
        return this._newSequenceWriter(true, this._generatorFactory.createGenerator(out, JsonEncoding.UTF8), true);
    }
    
    public SequenceWriter writeValuesAsArray(final DataOutput out) throws IOException {
        return this._newSequenceWriter(true, this._generatorFactory.createGenerator(out), true);
    }
    
    public boolean isEnabled(final SerializationFeature f) {
        return this._config.isEnabled(f);
    }
    
    public boolean isEnabled(final MapperFeature f) {
        return this._config.isEnabled(f);
    }
    
    @Deprecated
    public boolean isEnabled(final JsonParser.Feature f) {
        return this._generatorFactory.isEnabled(f);
    }
    
    public boolean isEnabled(final JsonGenerator.Feature f) {
        return this._generatorFactory.isEnabled(f);
    }
    
    public SerializationConfig getConfig() {
        return this._config;
    }
    
    public JsonFactory getFactory() {
        return this._generatorFactory;
    }
    
    public TypeFactory getTypeFactory() {
        return this._config.getTypeFactory();
    }
    
    public boolean hasPrefetchedSerializer() {
        return this._prefetch.hasSerializer();
    }
    
    public ContextAttributes getAttributes() {
        return this._config.getAttributes();
    }
    
    public void writeValue(final JsonGenerator gen, final Object value) throws IOException {
        this._configureGenerator(gen);
        if (this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            final Closeable toClose = (Closeable)value;
            try {
                this._prefetch.serialize(gen, value, this._serializerProvider());
                if (this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
                    gen.flush();
                }
            }
            catch (Exception e) {
                ClassUtil.closeOnFailAndThrowAsIOE(null, toClose, e);
                return;
            }
            toClose.close();
        }
        else {
            this._prefetch.serialize(gen, value, this._serializerProvider());
            if (this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
                gen.flush();
            }
        }
    }
    
    public void writeValue(final File resultFile, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._generatorFactory.createGenerator(resultFile, JsonEncoding.UTF8), value);
    }
    
    public void writeValue(final OutputStream out, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._generatorFactory.createGenerator(out, JsonEncoding.UTF8), value);
    }
    
    public void writeValue(final Writer w, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._generatorFactory.createGenerator(w), value);
    }
    
    public void writeValue(final DataOutput out, final Object value) throws IOException {
        this._configAndWriteValue(this._generatorFactory.createGenerator(out), value);
    }
    
    public String writeValueAsString(final Object value) throws JsonProcessingException {
        final SegmentedStringWriter sw = new SegmentedStringWriter(this._generatorFactory._getBufferRecycler());
        try {
            this._configAndWriteValue(this._generatorFactory.createGenerator(sw), value);
        }
        catch (JsonProcessingException e) {
            throw e;
        }
        catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
        return sw.getAndClear();
    }
    
    public byte[] writeValueAsBytes(final Object value) throws JsonProcessingException {
        final ByteArrayBuilder bb = new ByteArrayBuilder(this._generatorFactory._getBufferRecycler());
        try {
            this._configAndWriteValue(this._generatorFactory.createGenerator(bb, JsonEncoding.UTF8), value);
        }
        catch (JsonProcessingException e) {
            throw e;
        }
        catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
        final byte[] result = bb.toByteArray();
        bb.release();
        return result;
    }
    
    public void acceptJsonFormatVisitor(final JavaType type, final JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        if (type == null) {
            throw new IllegalArgumentException("type must be provided");
        }
        this._serializerProvider().acceptJsonFormatVisitor(type, visitor);
    }
    
    public void acceptJsonFormatVisitor(final Class<?> rawType, final JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        this.acceptJsonFormatVisitor(this._config.constructType(rawType), visitor);
    }
    
    public boolean canSerialize(final Class<?> type) {
        return this._serializerProvider().hasSerializerFor(type, null);
    }
    
    public boolean canSerialize(final Class<?> type, final AtomicReference<Throwable> cause) {
        return this._serializerProvider().hasSerializerFor(type, cause);
    }
    
    protected DefaultSerializerProvider _serializerProvider() {
        return this._serializerProvider.createInstance(this._config, this._serializerFactory);
    }
    
    protected void _verifySchemaType(final FormatSchema schema) {
        if (schema != null && !this._generatorFactory.canUseSchema(schema)) {
            throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._generatorFactory.getFormatName());
        }
    }
    
    protected final void _configAndWriteValue(final JsonGenerator gen, final Object value) throws IOException {
        this._configureGenerator(gen);
        if (this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            this._writeCloseable(gen, value);
            return;
        }
        try {
            this._prefetch.serialize(gen, value, this._serializerProvider());
        }
        catch (Exception e) {
            ClassUtil.closeOnFailAndThrowAsIOE(gen, e);
            return;
        }
        gen.close();
    }
    
    private final void _writeCloseable(final JsonGenerator gen, final Object value) throws IOException {
        Closeable toClose = (Closeable)value;
        try {
            this._prefetch.serialize(gen, value, this._serializerProvider());
            final Closeable tmpToClose = toClose;
            toClose = null;
            tmpToClose.close();
        }
        catch (Exception e) {
            ClassUtil.closeOnFailAndThrowAsIOE(gen, toClose, e);
            return;
        }
        gen.close();
    }
    
    protected final void _configureGenerator(final JsonGenerator gen) {
        this._config.initialize(gen);
        this._generatorSettings.initialize(gen);
    }
    
    static {
        NULL_PRETTY_PRINTER = new MinimalPrettyPrinter();
    }
    
    public static final class GeneratorSettings implements Serializable
    {
        private static final long serialVersionUID = 1L;
        public static final GeneratorSettings empty;
        public final PrettyPrinter prettyPrinter;
        public final FormatSchema schema;
        public final CharacterEscapes characterEscapes;
        public final SerializableString rootValueSeparator;
        
        public GeneratorSettings(final PrettyPrinter pp, final FormatSchema sch, final CharacterEscapes esc, final SerializableString rootSep) {
            this.prettyPrinter = pp;
            this.schema = sch;
            this.characterEscapes = esc;
            this.rootValueSeparator = rootSep;
        }
        
        public GeneratorSettings with(PrettyPrinter pp) {
            if (pp == null) {
                pp = ObjectWriter.NULL_PRETTY_PRINTER;
            }
            return (pp == this.prettyPrinter) ? this : new GeneratorSettings(pp, this.schema, this.characterEscapes, this.rootValueSeparator);
        }
        
        public GeneratorSettings with(final FormatSchema sch) {
            return (this.schema == sch) ? this : new GeneratorSettings(this.prettyPrinter, sch, this.characterEscapes, this.rootValueSeparator);
        }
        
        public GeneratorSettings with(final CharacterEscapes esc) {
            return (this.characterEscapes == esc) ? this : new GeneratorSettings(this.prettyPrinter, this.schema, esc, this.rootValueSeparator);
        }
        
        public GeneratorSettings withRootValueSeparator(final String sep) {
            if (sep == null) {
                if (this.rootValueSeparator == null) {
                    return this;
                }
                return new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, null);
            }
            else {
                if (sep.equals(this._rootValueSeparatorAsString())) {
                    return this;
                }
                return new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, new SerializedString(sep));
            }
        }
        
        public GeneratorSettings withRootValueSeparator(final SerializableString sep) {
            if (sep == null) {
                if (this.rootValueSeparator == null) {
                    return this;
                }
                return new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, null);
            }
            else {
                if (sep.equals(this.rootValueSeparator)) {
                    return this;
                }
                return new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, sep);
            }
        }
        
        private final String _rootValueSeparatorAsString() {
            return (this.rootValueSeparator == null) ? null : this.rootValueSeparator.getValue();
        }
        
        public void initialize(final JsonGenerator gen) {
            PrettyPrinter pp = this.prettyPrinter;
            if (this.prettyPrinter != null) {
                if (pp == ObjectWriter.NULL_PRETTY_PRINTER) {
                    gen.setPrettyPrinter(null);
                }
                else {
                    if (pp instanceof Instantiatable) {
                        pp = ((Instantiatable)pp).createInstance();
                    }
                    gen.setPrettyPrinter(pp);
                }
            }
            if (this.characterEscapes != null) {
                gen.setCharacterEscapes(this.characterEscapes);
            }
            if (this.schema != null) {
                gen.setSchema(this.schema);
            }
            if (this.rootValueSeparator != null) {
                gen.setRootValueSeparator(this.rootValueSeparator);
            }
        }
        
        static {
            empty = new GeneratorSettings(null, null, null, null);
        }
    }
    
    public static final class Prefetch implements Serializable
    {
        private static final long serialVersionUID = 1L;
        public static final Prefetch empty;
        private final JavaType rootType;
        private final JsonSerializer<Object> valueSerializer;
        private final TypeSerializer typeSerializer;
        
        private Prefetch(final JavaType rootT, final JsonSerializer<Object> ser, final TypeSerializer typeSer) {
            this.rootType = rootT;
            this.valueSerializer = ser;
            this.typeSerializer = typeSer;
        }
        
        public Prefetch forRootType(final ObjectWriter parent, final JavaType newType) {
            final boolean noType = newType == null || newType.isJavaLangObject();
            if (noType) {
                if (this.rootType == null || this.valueSerializer == null) {
                    return this;
                }
                return new Prefetch(null, null, this.typeSerializer);
            }
            else {
                if (newType.equals(this.rootType)) {
                    return this;
                }
                if (parent.isEnabled(SerializationFeature.EAGER_SERIALIZER_FETCH)) {
                    final DefaultSerializerProvider prov = parent._serializerProvider();
                    try {
                        final JsonSerializer<Object> ser = prov.findTypedValueSerializer(newType, true, null);
                        if (ser instanceof TypeWrappedSerializer) {
                            return new Prefetch(newType, null, ((TypeWrappedSerializer)ser).typeSerializer());
                        }
                        return new Prefetch(newType, ser, null);
                    }
                    catch (JsonProcessingException ex) {}
                }
                return new Prefetch(newType, null, this.typeSerializer);
            }
        }
        
        public final JsonSerializer<Object> getValueSerializer() {
            return this.valueSerializer;
        }
        
        public final TypeSerializer getTypeSerializer() {
            return this.typeSerializer;
        }
        
        public boolean hasSerializer() {
            return this.valueSerializer != null || this.typeSerializer != null;
        }
        
        public void serialize(final JsonGenerator gen, final Object value, final DefaultSerializerProvider prov) throws IOException {
            if (this.typeSerializer != null) {
                prov.serializePolymorphic(gen, value, this.rootType, this.valueSerializer, this.typeSerializer);
            }
            else if (this.valueSerializer != null) {
                prov.serializeValue(gen, value, this.rootType, this.valueSerializer);
            }
            else if (this.rootType != null) {
                prov.serializeValue(gen, value, this.rootType);
            }
            else {
                prov.serializeValue(gen, value);
            }
        }
        
        static {
            empty = new Prefetch(null, null, null);
        }
    }
}
