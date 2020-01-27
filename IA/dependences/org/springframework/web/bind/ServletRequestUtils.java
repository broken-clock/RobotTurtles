// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.bind;

import javax.servlet.ServletRequest;

public abstract class ServletRequestUtils
{
    private static final IntParser INT_PARSER;
    private static final LongParser LONG_PARSER;
    private static final FloatParser FLOAT_PARSER;
    private static final DoubleParser DOUBLE_PARSER;
    private static final BooleanParser BOOLEAN_PARSER;
    private static final StringParser STRING_PARSER;
    
    public static Integer getIntParameter(final ServletRequest request, final String name) throws ServletRequestBindingException {
        if (request.getParameter(name) == null) {
            return null;
        }
        return getRequiredIntParameter(request, name);
    }
    
    public static int getIntParameter(final ServletRequest request, final String name, final int defaultVal) {
        if (request.getParameter(name) == null) {
            return defaultVal;
        }
        try {
            return getRequiredIntParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return defaultVal;
        }
    }
    
    public static int[] getIntParameters(final ServletRequest request, final String name) {
        try {
            return getRequiredIntParameters(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return new int[0];
        }
    }
    
    public static int getRequiredIntParameter(final ServletRequest request, final String name) throws ServletRequestBindingException {
        return ServletRequestUtils.INT_PARSER.parseInt(name, request.getParameter(name));
    }
    
    public static int[] getRequiredIntParameters(final ServletRequest request, final String name) throws ServletRequestBindingException {
        return ServletRequestUtils.INT_PARSER.parseInts(name, request.getParameterValues(name));
    }
    
    public static Long getLongParameter(final ServletRequest request, final String name) throws ServletRequestBindingException {
        if (request.getParameter(name) == null) {
            return null;
        }
        return getRequiredLongParameter(request, name);
    }
    
    public static long getLongParameter(final ServletRequest request, final String name, final long defaultVal) {
        if (request.getParameter(name) == null) {
            return defaultVal;
        }
        try {
            return getRequiredLongParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return defaultVal;
        }
    }
    
    public static long[] getLongParameters(final ServletRequest request, final String name) {
        try {
            return getRequiredLongParameters(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return new long[0];
        }
    }
    
    public static long getRequiredLongParameter(final ServletRequest request, final String name) throws ServletRequestBindingException {
        return ServletRequestUtils.LONG_PARSER.parseLong(name, request.getParameter(name));
    }
    
    public static long[] getRequiredLongParameters(final ServletRequest request, final String name) throws ServletRequestBindingException {
        return ServletRequestUtils.LONG_PARSER.parseLongs(name, request.getParameterValues(name));
    }
    
    public static Float getFloatParameter(final ServletRequest request, final String name) throws ServletRequestBindingException {
        if (request.getParameter(name) == null) {
            return null;
        }
        return getRequiredFloatParameter(request, name);
    }
    
    public static float getFloatParameter(final ServletRequest request, final String name, final float defaultVal) {
        if (request.getParameter(name) == null) {
            return defaultVal;
        }
        try {
            return getRequiredFloatParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return defaultVal;
        }
    }
    
    public static float[] getFloatParameters(final ServletRequest request, final String name) {
        try {
            return getRequiredFloatParameters(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return new float[0];
        }
    }
    
    public static float getRequiredFloatParameter(final ServletRequest request, final String name) throws ServletRequestBindingException {
        return ServletRequestUtils.FLOAT_PARSER.parseFloat(name, request.getParameter(name));
    }
    
    public static float[] getRequiredFloatParameters(final ServletRequest request, final String name) throws ServletRequestBindingException {
        return ServletRequestUtils.FLOAT_PARSER.parseFloats(name, request.getParameterValues(name));
    }
    
    public static Double getDoubleParameter(final ServletRequest request, final String name) throws ServletRequestBindingException {
        if (request.getParameter(name) == null) {
            return null;
        }
        return getRequiredDoubleParameter(request, name);
    }
    
    public static double getDoubleParameter(final ServletRequest request, final String name, final double defaultVal) {
        if (request.getParameter(name) == null) {
            return defaultVal;
        }
        try {
            return getRequiredDoubleParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return defaultVal;
        }
    }
    
    public static double[] getDoubleParameters(final ServletRequest request, final String name) {
        try {
            return getRequiredDoubleParameters(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return new double[0];
        }
    }
    
    public static double getRequiredDoubleParameter(final ServletRequest request, final String name) throws ServletRequestBindingException {
        return ServletRequestUtils.DOUBLE_PARSER.parseDouble(name, request.getParameter(name));
    }
    
    public static double[] getRequiredDoubleParameters(final ServletRequest request, final String name) throws ServletRequestBindingException {
        return ServletRequestUtils.DOUBLE_PARSER.parseDoubles(name, request.getParameterValues(name));
    }
    
    public static Boolean getBooleanParameter(final ServletRequest request, final String name) throws ServletRequestBindingException {
        if (request.getParameter(name) == null) {
            return null;
        }
        return getRequiredBooleanParameter(request, name);
    }
    
    public static boolean getBooleanParameter(final ServletRequest request, final String name, final boolean defaultVal) {
        if (request.getParameter(name) == null) {
            return defaultVal;
        }
        try {
            return getRequiredBooleanParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return defaultVal;
        }
    }
    
    public static boolean[] getBooleanParameters(final ServletRequest request, final String name) {
        try {
            return getRequiredBooleanParameters(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return new boolean[0];
        }
    }
    
    public static boolean getRequiredBooleanParameter(final ServletRequest request, final String name) throws ServletRequestBindingException {
        return ServletRequestUtils.BOOLEAN_PARSER.parseBoolean(name, request.getParameter(name));
    }
    
    public static boolean[] getRequiredBooleanParameters(final ServletRequest request, final String name) throws ServletRequestBindingException {
        return ServletRequestUtils.BOOLEAN_PARSER.parseBooleans(name, request.getParameterValues(name));
    }
    
    public static String getStringParameter(final ServletRequest request, final String name) throws ServletRequestBindingException {
        if (request.getParameter(name) == null) {
            return null;
        }
        return getRequiredStringParameter(request, name);
    }
    
    public static String getStringParameter(final ServletRequest request, final String name, final String defaultVal) {
        final String val = request.getParameter(name);
        return (val != null) ? val : defaultVal;
    }
    
    public static String[] getStringParameters(final ServletRequest request, final String name) {
        try {
            return getRequiredStringParameters(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return new String[0];
        }
    }
    
    public static String getRequiredStringParameter(final ServletRequest request, final String name) throws ServletRequestBindingException {
        return ServletRequestUtils.STRING_PARSER.validateRequiredString(name, request.getParameter(name));
    }
    
    public static String[] getRequiredStringParameters(final ServletRequest request, final String name) throws ServletRequestBindingException {
        return ServletRequestUtils.STRING_PARSER.validateRequiredStrings(name, request.getParameterValues(name));
    }
    
    static {
        INT_PARSER = new IntParser();
        LONG_PARSER = new LongParser();
        FLOAT_PARSER = new FloatParser();
        DOUBLE_PARSER = new DoubleParser();
        BOOLEAN_PARSER = new BooleanParser();
        STRING_PARSER = new StringParser();
    }
    
    private abstract static class ParameterParser<T>
    {
        protected final T parse(final String name, final String parameter) throws ServletRequestBindingException {
            this.validateRequiredParameter(name, parameter);
            try {
                return this.doParse(parameter);
            }
            catch (NumberFormatException ex) {
                throw new ServletRequestBindingException("Required " + this.getType() + " parameter '" + name + "' with value of '" + parameter + "' is not a valid number", ex);
            }
        }
        
        protected final void validateRequiredParameter(final String name, final Object parameter) throws ServletRequestBindingException {
            if (parameter == null) {
                throw new MissingServletRequestParameterException(name, this.getType());
            }
        }
        
        protected abstract String getType();
        
        protected abstract T doParse(final String p0) throws NumberFormatException;
    }
    
    private static class IntParser extends ParameterParser<Integer>
    {
        @Override
        protected String getType() {
            return "int";
        }
        
        @Override
        protected Integer doParse(final String s) throws NumberFormatException {
            return Integer.valueOf(s);
        }
        
        public int parseInt(final String name, final String parameter) throws ServletRequestBindingException {
            return this.parse(name, parameter);
        }
        
        public int[] parseInts(final String name, final String[] values) throws ServletRequestBindingException {
            this.validateRequiredParameter(name, values);
            final int[] parameters = new int[values.length];
            for (int i = 0; i < values.length; ++i) {
                parameters[i] = this.parseInt(name, values[i]);
            }
            return parameters;
        }
    }
    
    private static class LongParser extends ParameterParser<Long>
    {
        @Override
        protected String getType() {
            return "long";
        }
        
        @Override
        protected Long doParse(final String parameter) throws NumberFormatException {
            return Long.valueOf(parameter);
        }
        
        public long parseLong(final String name, final String parameter) throws ServletRequestBindingException {
            return this.parse(name, parameter);
        }
        
        public long[] parseLongs(final String name, final String[] values) throws ServletRequestBindingException {
            this.validateRequiredParameter(name, values);
            final long[] parameters = new long[values.length];
            for (int i = 0; i < values.length; ++i) {
                parameters[i] = this.parseLong(name, values[i]);
            }
            return parameters;
        }
    }
    
    private static class FloatParser extends ParameterParser<Float>
    {
        @Override
        protected String getType() {
            return "float";
        }
        
        @Override
        protected Float doParse(final String parameter) throws NumberFormatException {
            return Float.valueOf(parameter);
        }
        
        public float parseFloat(final String name, final String parameter) throws ServletRequestBindingException {
            return this.parse(name, parameter);
        }
        
        public float[] parseFloats(final String name, final String[] values) throws ServletRequestBindingException {
            this.validateRequiredParameter(name, values);
            final float[] parameters = new float[values.length];
            for (int i = 0; i < values.length; ++i) {
                parameters[i] = this.parseFloat(name, values[i]);
            }
            return parameters;
        }
    }
    
    private static class DoubleParser extends ParameterParser<Double>
    {
        @Override
        protected String getType() {
            return "double";
        }
        
        @Override
        protected Double doParse(final String parameter) throws NumberFormatException {
            return Double.valueOf(parameter);
        }
        
        public double parseDouble(final String name, final String parameter) throws ServletRequestBindingException {
            return this.parse(name, parameter);
        }
        
        public double[] parseDoubles(final String name, final String[] values) throws ServletRequestBindingException {
            this.validateRequiredParameter(name, values);
            final double[] parameters = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                parameters[i] = this.parseDouble(name, values[i]);
            }
            return parameters;
        }
    }
    
    private static class BooleanParser extends ParameterParser<Boolean>
    {
        @Override
        protected String getType() {
            return "boolean";
        }
        
        @Override
        protected Boolean doParse(final String parameter) throws NumberFormatException {
            return parameter.equalsIgnoreCase("true") || parameter.equalsIgnoreCase("on") || parameter.equalsIgnoreCase("yes") || parameter.equals("1");
        }
        
        public boolean parseBoolean(final String name, final String parameter) throws ServletRequestBindingException {
            return this.parse(name, parameter);
        }
        
        public boolean[] parseBooleans(final String name, final String[] values) throws ServletRequestBindingException {
            this.validateRequiredParameter(name, values);
            final boolean[] parameters = new boolean[values.length];
            for (int i = 0; i < values.length; ++i) {
                parameters[i] = this.parseBoolean(name, values[i]);
            }
            return parameters;
        }
    }
    
    private static class StringParser extends ParameterParser<String>
    {
        @Override
        protected String getType() {
            return "string";
        }
        
        @Override
        protected String doParse(final String parameter) throws NumberFormatException {
            return parameter;
        }
        
        public String validateRequiredString(final String name, final String value) throws ServletRequestBindingException {
            this.validateRequiredParameter(name, value);
            return value;
        }
        
        public String[] validateRequiredStrings(final String name, final String[] values) throws ServletRequestBindingException {
            this.validateRequiredParameter(name, values);
            for (final String value : values) {
                this.validateRequiredParameter(name, value);
            }
            return values;
        }
    }
}
