package br.com.locCar.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.lang.reflect.InvocationTargetException;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;


public final class Bean { // NOPMD by herberson on 08/06/10 21:41

    private Bean() {
    }

    /**
     * <dl>
     * Verifica se um objeto � nulo. O m�todo pode ser usado para inst�ncias das classes:
     * <dd>- <code>java.lang.String</code><dd>
     * <dd>- <code>java.util.Collection</code><dd>
     * <dd>- <code>java.util.Map</code><dd>
     * <dd>- <code>java.util.List</code><dd>
     * <dd>- <code>java.util.Set</code><dd>
     * </dl>
     * @param objeto
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean isNull(final Object objeto) {
        boolean nulo;

        if (objeto == null) {
            nulo = true;
        } else if (objeto instanceof String) {
            nulo = objeto.toString().trim().length() == 0;
        } else if (objeto instanceof Collection) {
            nulo = ((Collection)objeto).isEmpty();
        } else if (objeto instanceof Map) {
            nulo = ((Map)objeto).isEmpty();
        } else if (objeto instanceof List) {
            nulo = ((List)objeto).isEmpty();
        } else if (objeto instanceof Set) {
            nulo = ((Set)objeto).isEmpty();
        } else {
            nulo = false;
        } //end if

        return nulo;
    } //end method

    public static boolean isNull(final Object objeto, final boolean isZeroNull) {
        boolean nulo;

        if (isZeroNull) {
            if (isNull(objeto)) {
                nulo = true;
            } else if (objeto instanceof Integer) {
                nulo = ((Integer)objeto).intValue() == 0;
            } else if (objeto instanceof Long) {
                nulo = ((Long)objeto).longValue() == 0;
            } else if (objeto instanceof Double) {
                nulo = ((Double)objeto).doubleValue() == 0;
            } else {
                nulo = false;
            } //end if
        } else {
            nulo = isNull(objeto);
        }

        return nulo;
    } //end method

    public static boolean isNotNull(final Object objeto) {
        boolean notNull;

        if (isNull(objeto)) {
            notNull = false;
        } else {
            notNull = true;
        }

        return notNull;
    } //end method

    public static boolean isNotNull(final Object objeto, final boolean isZeroNull) {
        boolean notNull;

        if (isNull(objeto, isZeroNull)) {
            notNull = false;
        } else {
            notNull = true;
        }

        return notNull;
    } //end method

    /**
     * Copia as propriedades entre dois beans.
     *
     * @param dest
     * @param orig
     */
    public static void copyBean(final Object dest, final Object orig) { // NOPMD by herberson on 08/06/10 21:41
        try {
            PropertyUtils.copyProperties(dest, orig);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e); // NOPMD by herberson on 08/06/10 21:40
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e); // NOPMD by herberson on 08/06/10 21:40
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e); // NOPMD by herberson on 08/06/10 21:40
        } //end try ... catch
    } //end method

    /**
     * Copia as propriedades definidas entre os beans.
     * @param dest
     * @param orig
     * @param properties
     */
    public static void copyProperties(final Object dest, final Object orig, final String... properties) { // NOPMD by herberson on 08/06/10 21:41
        try {
            for (String property : properties) {
                PropertyUtils.setProperty(dest, property, PropertyUtils.getProperty(orig, property));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e); // NOPMD by herberson on 08/06/10 21:42
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e); // NOPMD by herberson on 08/06/10 21:43
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e); // NOPMD by herberson on 08/06/10 21:43
        } //end try ... catch
    } //end method

    /**
     * Concatena o valor (<code>toString()</code>) das propriedades no array <code>properties</code>.
     * @param instance
     * @param properties
     * @return
     */
    public static String objectToString(final Object instance, final String... properties) {
        return objectToString(instance, true, properties);
    } //end method

    /**
     * Concatena o valor (<code>toString()</code>) das propriedades no array <code>properties</code>.
     * @param instance
     * @param properties
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String objectToString(final Object instance, final boolean putPropertyName,
                                        final String... properties) { // NOPMD by herberson on 17/06/10 10:42
        final StringBuffer stringTo = new StringBuffer();
        final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("pt-BR")); // NOPMD by herberson on 17/06/10 10:44
        Object value;
        String sizeCollection;
        boolean putSeparator = false; // NOPMD by herberson on 08/06/10 21:47
        if (!isNull(instance) && !isNull(properties)) { // NOPMD by herberson on 08/06/10 21:43
            stringTo.append(instance.getClass().getSimpleName());
            stringTo.append(" [");
            for (String property : properties) {
                sizeCollection = null; // NOPMD by herberson on 11/07/10 10:33
                try {
                    value = PropertyUtils.getProperty(instance, property); // NOPMD by herberson on 08/06/10 21:47
                    if (isNull(value)) {
                        value = "NULL"; // NOPMD by herberson on 17/06/10 10:44
                    } else if (value instanceof java.util.Date) {
                        value = DATE_FORMAT.format((java.util.Date)value);
                    } else if (value instanceof java.util.Calendar) {
                        value = DATE_FORMAT.format(((java.util.Calendar)value).getTime());
                    } else if (value instanceof javax.xml.datatype.XMLGregorianCalendar) {
                        value = DATE_FORMAT.format(((javax.xml.datatype.XMLGregorianCalendar)value).toGregorianCalendar().getTime());
                    } else if (value instanceof Collection) {
                        sizeCollection = String.format("(size=%d)", ((Collection)value).size()); // NOPMD by herberson on 11/07/10 10:33
                    } else if (value instanceof Map) {
                        sizeCollection = String.format("(size=%d)", ((Map)value).size()); // NOPMD by herberson on 11/07/10 10:34
                    } else if (value instanceof List) {
                        sizeCollection = String.format("(size=%d)", ((List)value).size()); // NOPMD by herberson on 11/07/10 10:34
                    } else if (value instanceof Set) {
                        sizeCollection = String.format("(size=%d)", ((Set)value).size()); // NOPMD by herberson on 11/07/10 10:34
                    }
                } catch (Exception e) {
                    value = property + ": N�o encontrada";
                } //end try ... catch
                if (putSeparator && putPropertyName) {
                    stringTo.append(", ");
                } else if (putSeparator) {
                    stringTo.append(" - ");
                } //end if
                if (putPropertyName) {
                    stringTo.append(property);
                    if (sizeCollection != null) {
                        stringTo.append(sizeCollection);
                    }
                    stringTo.append('=');
                    stringTo.append(value);
                } else {
                    stringTo.append(value);
                } //end if
                putSeparator = true; // NOPMD by herberson on 08/06/10 21:48
            } //end for
            stringTo.append(']');
        } else {
            stringTo.append("NULL");
        } //end if

        return stringTo.toString();
    } //end method

    /**
     * Retorna um StringBuffer herarquizado para log.
     *
     * @param bean
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static StringBuffer getObjectForDebug(final Object bean) {
        final StringBuffer stringBuffer = new StringBuffer();

        if (bean == null) {
            stringBuffer.append("Objeto informado � nulo");
        } else {
            ArrayList lista = null; // NOPMD by herberson on 08/06/10 21:48
            final String linesep = System.getProperty("line.separator"); // NOPMD by herberson on 08/06/10 21:48

            lista = listBeanDebugInfo(bean, "this", 0);

            for (int i = 0; i < lista.size(); i++) {
                stringBuffer.append(lista.get(i));
                stringBuffer.append(linesep);
            } //end for
        } //end if

        return stringBuffer;
    } //end method

    public static Object getAttributeValue(final Object obj, final String attribute) {
        Object value;

        if (isNotNull(obj)) {
            try {
                value = PropertyUtils.getProperty(obj, attribute);
            } catch (Exception e) {
                value = null;
            }
        } else {
            value = null;
        }

        return value;
    }

    public static String getIdAttributeName(Object obj) {
        String result = null;
        if (obj != null) {
            result = getIdAttributeName(obj.getClass());
        }
        return result;
    }
    
    public static String statckTraceToString(Throwable throwble) {
        StringWriter sw;
        PrintWriter pw;
        String stackTrace;
        
        if (isNotNull(throwble)) {
            try {
                sw = new StringWriter();
                pw = new PrintWriter(sw);
                
                throwble.printStackTrace(pw);
                
                stackTrace = sw.toString();
            } catch(Exception e2) {
                stackTrace = "";
            } //end try ... catch
        } else {
            stackTrace = "";
        } //end if
        
        return stackTrace;
    } //end method
    
    public static URL newUrlInstance(final String _url) {
        URL instance;

        try {
            instance = new URL(_url);
        } catch (MalformedURLException e) {
            instance = null;
        }
        
        return instance;
    }

    /**
     * Retorna uma lista com as propriedades que comp�em o objeto e os valores
     * de cada uma dessas propriedades.
     *
     * @param bean
     * @param prefix
     * @param n
     * @return
     */
    @SuppressWarnings( { "rawtypes", "unchecked" })
    private static ArrayList listBeanDebugInfo(final Object bean, final String prefix, int n) { // NOPMD by herberson on 08/06/10 21:45
        final ArrayList lista = new ArrayList();
        Map describe = null; // NOPMD by herberson on 08/06/10 21:48
        Iterator ident = null; // NOPMD by herberson on 08/06/10 21:48
        final StringBuffer stringBuffer = new StringBuffer(); // NOPMD by herberson on 08/06/10 21:48
        String property = null; // NOPMD by herberson on 08/06/10 21:48
        Class propertyType = null; // NOPMD by herberson on 08/06/10 21:48
        final ArrayList tmp = new ArrayList();

        try {
            describe = BeanUtils.describe(bean);
            tmp.addAll(describe.keySet());

            Collections.sort(tmp);

            ident = tmp.iterator();

            while (ident.hasNext()) {
                property = (String)ident.next();
                stringBuffer.setLength(0);
                propertyType = PropertyUtils.getPropertyType(bean, property);

                for (int i = 0; i < n; i++) {
                    stringBuffer.append('\t');
                } //end for

                if (prefix != null) {
                    stringBuffer.append(prefix);
                    stringBuffer.append('.');
                } //end if

                stringBuffer.append(property);
                stringBuffer.append(": [");

                if (propertyType.isArray()) {
                    stringBuffer.append("Array of ");
                    stringBuffer.append(propertyType.getComponentType().getName());
                } else {
                    stringBuffer.append(describe.get(property));
                } //end if

                stringBuffer.append("] ");

                lista.add(stringBuffer.toString());

                if (!isIgnoreProperty(propertyType)) {
                    if (PropertyUtils.getProperty(bean, property) != null) { // NOPMD by herberson on 08/06/10 21:45
                        lista.add("");
                        lista.addAll(listBeanDebugInfo(PropertyUtils.getProperty(bean, property), property, n + 1));
                        lista.add("");
                    } //end if
                } //end if
            } //end while
        } catch (IllegalAccessException e) {
            lista.add("Falha. " + e.getMessage());
        } catch (InvocationTargetException e) {
            lista.add("Falha. " + e.getMessage());
        } catch (NoSuchMethodException e) {
            lista.add("Falha. " + e.getMessage());
        } //end try ... catch

        return lista;
    } //end method

    /**
     * Verifica se o objeto deve ser descrito para depura��o.
     *
     * @param propertyType
     * @return
     */
    @SuppressWarnings("rawtypes")
    private static boolean isIgnoreProperty(final Class propertyType) { // NOPMD by herberson on 08/06/10 21:46
        boolean isIgnore = false; // NOPMD by herberson on 08/06/10 21:48
        
        String[] ignoreList = {
          "java.lang.String"
        , "java.lang.Boolean"
        , "java.lang.Integer"
        , "java.lang.Long"
        , "java.lang.Double"
        , "java.lang.Float"
        , "java.lang.Date"
        , "java.lang.Class"
        , "javax.sql.Date"
        , "java.util.List"
        , "java.util.Map"
        , "java.math.BigInteger"
        , "java.math.BigDecimal"
        , "oracle.jbo.domain.Number"
        , "oracle.jbo.domain.Date"
        , "oracle.jbo.domain.Char"
        , "org.hibernate.proxy.LazyInitializer"
       };
        
        if (propertyType.isPrimitive()) {
            isIgnore = true;
        } else if (propertyType.isArray()) {
            isIgnore = true;
        } else {
            for (int i = 0; i < ignoreList.length; i++) {
                if (propertyType.getName().equals(ignoreList[i])) {
                    isIgnore = true;
                    break;
                } //end if
            } //end for
        } //end if
        
        return isIgnore;
    } //end method

} //end class
