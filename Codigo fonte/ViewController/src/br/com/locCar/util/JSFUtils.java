package br.com.locCar.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.util.FacesMessageUtils;

import oracle.jbo.domain.Number;
import oracle.jbo.uicli.binding.JUCtrlHierBinding;
import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.component.UIXComponentBase;
import org.apache.myfaces.trinidad.component.UIXTree;
import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.ModelUtils;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;
import org.apache.myfaces.trinidad.model.RowKeySetTreeImpl;
import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;


/**
 * General useful static utilies for working with JSF.
 * NOTE: Updated to use JSF 1.2 ExpressionFactory.
 *
 * @author Duncan Mills
 * @author Steve Muench
 *
 * $Id: JSFUtils.java 1885 2007-06-26 00:47:41Z ralsmith $
 */
public class JSFUtils {

    private static final String NO_RESOURCE_FOUND = "Recurso inexistente: ";

    /**
     * Method for taking a reference to a JSF binding expression and returning
     * the matching object (or creating it).
     * @param expression EL expression
     * @return Managed object
     */
    public static Object resolveExpression(String expression) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application app = facesContext.getApplication();
        ExpressionFactory elFactory = app.getExpressionFactory();
        ELContext elContext = facesContext.getELContext();
        ValueExpression valueExp = elFactory.createValueExpression(elContext, expression, Object.class);
        return valueExp.getValue(elContext);
    }

    public static ValueExpression createValueExpression(String expression) {
        FacesContext facesContext;
        Application app;
        ExpressionFactory elFactory;
        ELContext elContext;
        ValueExpression valueExp;

        facesContext = getFacesContext();
        app = facesContext.getApplication();
        elFactory = app.getExpressionFactory();
        elContext = facesContext.getELContext();
        valueExp = elFactory.createValueExpression(elContext, expression, String.class);

        return valueExp;
    }

    /**
     * @return
     */
    public static String resolveRemoteUser() {
        FacesContext facesContext = getFacesContext();
        ExternalContext ectx = facesContext.getExternalContext();
        return ectx.getRemoteUser();
    }

    /**
     * @return
     */
    public static String resolveUserPrincipal() {
        FacesContext facesContext;
        ExternalContext ectx;
        HttpServletRequest request;
        String principal;

        facesContext = getFacesContext();
        ectx = facesContext.getExternalContext();
        request = (HttpServletRequest)ectx.getRequest();
        
        if (request == null) {
            principal = null;
        } else if (request.getUserPrincipal() == null) {
            principal = null;
        } else {
            principal = request.getUserPrincipal().getName();
        }
        
        return principal;
    }

    /**
     * @param expression
     * @param returnType
     * @param argTypes
     * @param argValues
     * @return
     */
    public static Object resolveMethodExpression(String expression, Class returnType, Class[] argTypes, Object[] argValues) {
        FacesContext facesContext;
        Application app;
        ExpressionFactory elFactory;
        ELContext elContext;
        MethodExpression methodExpression;

        facesContext = getFacesContext();
        app = facesContext.getApplication();
        elFactory = app.getExpressionFactory();
        elContext = facesContext.getELContext();
        methodExpression = elFactory.createMethodExpression(elContext, expression, returnType, argTypes);

        return methodExpression.invoke(elContext, argValues);
    }

    /**
     * Method for taking a reference to a JSF binding expression and returning
     * the matching Boolean.
     * @param expression EL expression
     * @return Managed object
     */
    public static Boolean resolveExpressionAsBoolean(String expression) {
        return (Boolean)resolveExpression(expression);
    }

    /**
     * Method for taking a reference to a JSF binding expression and returning
     * the matching String (or creating it).
     * @param expression EL expression
     * @return Managed object
     */
    public static String resolveExpressionAsString(String expression) {
        return (String)resolveExpression(expression);
    }

    /**
     * Convenience method for resolving a reference to a managed bean by name
     * rather than by expression.
     * @param beanName name of managed bean
     * @return Managed object
     */
    public static Object getManagedBeanValue(String beanName) {
        StringBuffer buff;
        buff = new StringBuffer("#{");
        buff.append(beanName);
        buff.append("}");
        return resolveExpression(buff.toString());
    }

    /**
     * Method for setting a new object into a JSF managed bean
     * Note: will fail silently if the supplied object does
     * not match the type of the managed bean.
     * @param expression EL expression
     * @param newValue new value to set
     */
    public static void setExpressionValue(String expression, Object newValue) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application app = facesContext.getApplication();
        ExpressionFactory elFactory = app.getExpressionFactory();
        ELContext elContext = facesContext.getELContext();
        ValueExpression valueExp = elFactory.createValueExpression(elContext, expression, Object.class);
        
        Class bindClass = valueExp.getType(elContext);

        //Check that the input newValue can be cast to the property type
        //expected by the managed bean.
        //If the managed Bean expects a primitive we rely on Auto-Unboxing
        bindClass = valueExp.getType(elContext);
        if (bindClass.isPrimitive() || bindClass.isInstance(newValue)) {
            valueExp.setValue(elContext, newValue);
        }
    }

    /**
     * Convenience method for setting the value of a managed bean by name
     * rather than by expression.
     * @param beanName name of managed bean
     * @param newValue new value to set
     */
    public static void setManagedBeanValue(String beanName, Object newValue) {
        StringBuffer buff;
        buff = new StringBuffer("#{");
        buff.append(beanName);
        buff.append("}");
        setExpressionValue(buff.toString(), newValue);
    }


    /**
     * Convenience method for setting Session variables.
     * @param key object key
     * @param object value to store
     */

    public static void storeOnSession(String key, Object object) {
        FacesContext ctx;
        Map sessionState;

        ctx = getFacesContext();
        sessionState = ctx.getExternalContext().getSessionMap();
        sessionState.put(key, object);
    }

    /**
     * Convenience method for getting Session variables.
     * @param key object key
     * @return session object for key
     */
    public static Object getFromSession(String key) {
        FacesContext ctx;
        Map sessionState;

        ctx = getFacesContext();
        sessionState = ctx.getExternalContext().getSessionMap();

        return sessionState.get(key);
    }
    
    public static Object removeFromSession(String key) {
        FacesContext ctx;
        Map sessionState;

        ctx = getFacesContext();
        sessionState = ctx.getExternalContext().getSessionMap();

        return sessionState.remove(key);
    }

    /**
     * @param key
     * @return
     */
    public static String getFromHeader(String key) {
        FacesContext ctx = getFacesContext();
        ExternalContext ectx = ctx.getExternalContext();
        return ectx.getRequestHeaderMap().get(key);
    }

    /**
     * Convenience method for getting Request variables.
     * @param key object key
     * @return session object for key
     */
    public static Object getFromRequest(String key) {
        FacesContext ctx = getFacesContext();
        Map sessionState = ctx.getExternalContext().getRequestMap();
        return sessionState.get(key);
    }

    /**
     * Pulls a String resource from the property bundle that
     * is defined under the application &lt;message-bundle&gt; element in
     * the faces config. Respects Locale
     * @param key string message key
     * @return Resource value or placeholder error String
     */
    public static String getStringFromBundle(String key) {
        ResourceBundle bundle = getBundle();
        return getStringSafely(bundle, key, null);
    }


    /**
     * Convenience method to construct a <code>FacesMesssage</code>
     * from a defined error key and severity
     * This assumes that the error keys follow the convention of
     * using <b>_detail</b> for the detailed part of the
     * message, otherwise the main message is returned for the
     * detail as well.
     * @param key for the error message in the resource bundle
     * @param severity severity of message
     * @return Faces Message object
     */
    public static FacesMessage getMessageFromBundle(String key, FacesMessage.Severity severity) {
        ResourceBundle bundle = getBundle();
        String summary = getStringSafely(bundle, key, null);
        String detail = getStringSafely(bundle, key + "_detail", summary);
        FacesMessage message = new FacesMessage(summary, detail);
        message.setSeverity(severity);
        return message;
    }

    /**
     * Add JSF info message.
     * @param msg info message string
     */
    public static void addFacesInformationMessage(String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, "");
        ctx.addMessage(getRootViewComponentId(), fm);
    }

    /**
     * Add JSF error message.
     * @param fm error message string
     */
    public static void addFacesErrorMessage(FacesMessage fm) {
        FacesContext ctx = getFacesContext();
        ctx.addMessage(getRootViewComponentId(), fm);
    }
    
    /**
     * Add JSF error message.
     * @param msg error message string
     */
    public static void addFacesErrorMessage(String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
        ctx.addMessage(getRootViewComponentId(), fm);
    }

    public static void addFacesErrorMessageByCompId(String componentId, String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
        ctx.addMessage(componentId, fm);
    }
    
    public static void addFacesConfirmationMessageByCompId(String componentId, String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, "");
        ctx.addMessage(componentId, FacesMessageUtils.getConfirmationMessage(fm));
    }
    
    public static void addFacesConfirmationMessage(String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, "");
        ctx.addMessage(getRootViewComponentId(), FacesMessageUtils.getConfirmationMessage(fm));
    }
        
    public static void addFacesWarningMessage(String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, "");
        ctx.addMessage(getRootViewComponentId(), fm);
    }
    
    public static void addFacesFatalMessage(String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_FATAL, msg, "");
        ctx.addMessage(getRootViewComponentId(), fm);
    }
    
    /**
     * Add JSF error message for a specific attribute.
     * @param attrName name of attribute
     * @param msg error message string
     */
    public static void addFacesErrorMessage(String attrName, String msg) {
        FacesContext ctx = getFacesContext();
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, attrName, msg);
        ctx.addMessage(getRootViewComponentId(), fm);
    }

    // Informational getters

    /**
     * Get view id of the view root.
     * @return view id of the view root
     */
    public static String getRootViewId() {
        return getFacesContext().getViewRoot().getViewId();
    }

    /**
     * Get component id of the view root.
     * @return component id of the view root
     */
    public static String getRootViewComponentId() {
        return getFacesContext().getViewRoot().getId();
    }

    /**
     * Get FacesContext.
     * @return FacesContext
     */
    public static FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
    /*
   * Internal method to pull out the correct local
   * message bundle
   */

    private static ResourceBundle getBundle() {
        FacesContext ctx = getFacesContext();
        UIViewRoot uiRoot = ctx.getViewRoot();
        Locale locale = uiRoot.getLocale();
        ClassLoader ldr = Thread.currentThread().getContextClassLoader();
        return ResourceBundle.getBundle(ctx.getApplication().getMessageBundle(), locale, ldr);
    }

    /**
     * Get an HTTP Request attribute.
     * @param name attribute name
     * @return attribute value
     */
    public static Object getRequestAttribute(String name) {
        return getFacesContext().getExternalContext().getRequestMap().get(name);
    }

    /**
     * Set an HTTP Request attribute.
     * @param name attribute name
     * @param value attribute value
     */
    public static void setRequestAttribute(String name, Object value) {
        getFacesContext().getExternalContext().getRequestMap().put(name, value);
    }

    /*
   * Internal method to proxy for resource keys that don't exist
   */

    private static String getStringSafely(ResourceBundle bundle, String key, String defaultValue) {
        String resource = null;
        try {
            resource = bundle.getString(key);
        } catch (MissingResourceException mrex) {
            if (defaultValue != null) {
                resource = defaultValue;
            } else {
                resource = NO_RESOURCE_FOUND + key;
            }
        }
        return resource;
    }

    /**
     * Locate an UIComponent in view root with its component id. Use a recursive way to achieve this.
     * @param id UIComponent id
     * @return UIComponent object
     */
    public static UIComponent findComponentInRoot(String id) {
        UIComponent component = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            UIComponent root = facesContext.getViewRoot();
            if (root != null) {
                component = findComponent(root, id);
            }
        }
        return component;
    }
    
    /**
     * Reinicia um campo edit�vel definindo o valor original informado.<br/>
     * 
     * @param componentId ID do componente na �rvore da p�gina JSF
     * @param originalValue Novo valor do que dever� ser utilizado para  componente.
     * @param ignorePartialTrigger Para impedir que o componente seja atualizado automaticamente.
     */
    public static void resetEditableField(final String componentId, final Object originalValue, final boolean ignorePartialTrigger) {
        UIComponent component;
        component = findComponentInRoot(componentId);
        
        if (component == null) {
            System.err.println(String.format("*********** Componente com id '%s' nao encontrado.", componentId));
        } else {
            resetEditableField(component, originalValue, ignorePartialTrigger);
        } //end if
    } //end method
    
    
    /**
     * Reinicia um campo edit�vel definindo o valor original.<br/>
     * Este m�todo adiciona o componente no "Partial Trigger".
     * 
     * @param componentId
     * @param originalValue
     */
    public static void resetEditableField(final String componentId, final Object originalValue) {
        resetEditableField(componentId, originalValue, false);
    } //end if
    
    /**
     * Reinicia um campo edit�vel definindo o valor original.<br/>
     * Este m�todo adiciona o componente no "Partial Trigger".
     *
     * @param component
     * @param originalValue
     */
    public static void resetEditableField(UIComponent component, final Object originalValue) {
        resetEditableField(component, originalValue, false);
    } //end method
    
    /**
     * Reinicia um campo edit�vel definindo o valor original informado.<br/>
     * 
     * @param component
     * @param originalValue
     * @param ignorePartialTrigger
     */
    public static void resetEditableField(UIComponent component, final Object originalValue, final boolean ignorePartialTrigger) {
        EditableValueHolder editableField;
        
        if (component == null) {
            System.err.println("*********** Argumento 'component' e nulo.");
        } else if (component instanceof EditableValueHolder) {
            editableField = (EditableValueHolder) component;
            editableField.setValue(originalValue);
            editableField.setSubmittedValue(originalValue);
            component.processUpdates(getFacesContext());
            if (!ignorePartialTrigger) {
                addPartialTrigger(component);
            } //end if
        } else {
            System.err.println(String.format("*********** Componente com id '%s' nao e um campo editavel. Tipo: %s", 
                                             component.getId(), component.getClass()));
        } //end if
    } //end method
    
    public static void updateEditableField(final String componentId, final Object newValue) {
        updateEditableField(componentId, newValue, false);
    } //end if
    
    public static void updateEditableField(UIComponent component, final Object newValue) {
        updateEditableField(component, newValue, false);
    } //end method
    
    public static void updateEditableField(final String componentId, final Object newValue, final boolean ignorePartialTrigger) {
        UIComponent component;
        component = findComponentInRoot(componentId);
        
        if (component == null) {
            System.err.println(String.format("*********** Componente com id '%s' nao encontrado.", componentId));
        } else {
            updateEditableField(component, newValue, ignorePartialTrigger);
        } //end if
    } //end method
    
    public static void updateEditableField(UIComponent component, final Object newValue, final boolean ignorePartialTrigger) {
        EditableValueHolder editableField;
        
        if (component == null) {
            System.err.println("*********** Argumento 'component' e nulo.");
        } else if (component instanceof EditableValueHolder) {
            editableField = (EditableValueHolder) component;
            editableField.setValue(newValue);
            if (!ignorePartialTrigger) {
                addPartialTrigger(component);
            } //end if
        } else {
            System.err.println(String.format("*********** Componente com id '%s' nao e um campo editavel. Tipo: %s", 
                                             component.getId(), component.getClass()));
        } //end if
    } //end method
    
    /**
     * Locate an UIComponent from its root component.
     * Taken from http://www.jroller.com/page/mert?entry=how_to_find_a_uicomponent
     * @param base root Component (parent)
     * @param id UIComponent id
     * @return UIComponent object
     */
    public static UIComponent findComponent(UIComponent base, String id) {
        if (id.equals(base.getId()))
            return base;

        UIComponent children = null;
        UIComponent result = null;
        Iterator childrens = base.getFacetsAndChildren();
        while (childrens.hasNext() && (result == null)) {
            children = (UIComponent)childrens.next();
            if (id.equals(children.getId())) {
                result = children;
                break;
            }
            result = findComponent(children, id);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    /**
     * Method to create a redirect URL. The assumption is that the JSF servlet mapping is
     * "faces", which is the default
     *
     * @param view the JSP or JSPX page to redirect to
     * @return a URL to redirect to
     */
    public static String getPageURL(String view) {
        FacesContext facesContext;
        ExternalContext externalContext;
        String url;
        StringBuffer newUrlBuffer;

        facesContext = getFacesContext();
        externalContext = facesContext.getExternalContext();
        url = ((HttpServletRequest)externalContext.getRequest()).getRequestURL().toString();
        newUrlBuffer = new StringBuffer();
        newUrlBuffer.append(url.substring(0, url.lastIndexOf("faces/")));
        newUrlBuffer.append("faces");
        String targetPageUrl = view.startsWith("/") ? view : "/" + view;
        newUrlBuffer.append(targetPageUrl);

        return newUrlBuffer.toString();
    }

    /**
     * Refresh the current page.
     */
    public static void refreshCurrentPage() {
        FacesContext context = FacesContext.getCurrentInstance();
        String currentView = context.getViewRoot().getViewId();
        ViewHandler vh = context.getApplication().getViewHandler();
        UIViewRoot x = vh.createView(context, currentView);
        context.setViewRoot(x);
    }

    /**
     * @return
     */
    public static HttpServletRequest getRequest() {
        FacesContext facesContext;
        ExternalContext ectx;
        HttpServletRequest request;

        facesContext = getFacesContext();
        ectx = facesContext.getExternalContext();
        request = (HttpServletRequest)ectx.getRequest();

        return request;
    }

    /**
     * @return
     */
    public static HttpServletResponse getResponse() {
        FacesContext facesContext;
        ExternalContext ectx;
        HttpServletResponse response;

        facesContext = getFacesContext();
        ectx = facesContext.getExternalContext();
        response = (HttpServletResponse)ectx.getResponse();

        return response;
    }

    /**
     * @return
     */
    public static String getAppContextPath() {
        String cp;
        String scheme;
        String serverName;
        int serverPort;
        String contextPath;
        final String[] HEADER_ATT = new String[]{"Referer", "REFERER", "referer"};
        final boolean PUT_DEFAULT_PORTS = true;
        
        String requestHeaderReferer;
        
        try {
            scheme = getRequest().getScheme().toLowerCase();
            serverName = getRequest().getServerName();
            serverPort = getRequest().getServerPort();
            contextPath = getRequest().getContextPath();
            requestHeaderReferer = null;
            
            for (String hatt : HEADER_ATT) {
                requestHeaderReferer = getFromHeader(hatt);
                if (requestHeaderReferer != null && !requestHeaderReferer.trim().isEmpty()) {
                    break;
                } //end if
            } //end for
            
            if ("http".equalsIgnoreCase(scheme) && requestHeaderReferer != null && requestHeaderReferer.toLowerCase().startsWith("https")) {
                scheme = "https";
                if (serverPort == 80) {
                    serverPort = 443;
                } //end if
            } //end if
            
            if ((serverPort == 80 || serverPort == 443) && !PUT_DEFAULT_PORTS) {
                cp = String.format("%s://%s%s", scheme, serverName, contextPath);
            } else {
                cp = String.format("%s://%s:%s%s", scheme, serverName, serverPort, contextPath);
            } //end if
        } catch (java.lang.NullPointerException npe) {
            cp = "";
        } //end try ... catch
        return cp;
    } //end method

    public static String getServerPath() {
        String cp;
        String scheme;
        String serverName;
        int serverPort;
        final String[] HEADER_ATT = new String[]{"Referer", "REFERER", "referer"};
        final boolean PUT_DEFAULT_PORTS = true;
        
        String requestHeaderReferer;
        
        try {
            scheme = getRequest().getScheme().toLowerCase();
            serverName = getRequest().getServerName();
            serverPort = getRequest().getServerPort();
            requestHeaderReferer = null;
            
            for (String hatt : HEADER_ATT) {
                requestHeaderReferer = getFromHeader(hatt);
                if (requestHeaderReferer != null && !requestHeaderReferer.trim().isEmpty()) {
                    break;
                } //end if
            } //end for
            
            if ("http".equalsIgnoreCase(scheme) && requestHeaderReferer != null && requestHeaderReferer.toLowerCase().startsWith("https")) {
                scheme = "https";
                if (serverPort == 80) {
                    serverPort = 443;
                } //end if
            } //end if
            
            if ((serverPort == 80 || serverPort == 443) && !PUT_DEFAULT_PORTS) {
                cp = String.format("%s://%s/", scheme, serverName);
            } else {
                cp = String.format("%s://%s:%s/", scheme, serverName, serverPort);
            } //end if
        } catch (java.lang.NullPointerException npe) {
            cp = "";
        } //end try ... catch
        return cp;
    } //end method
    
    /**
     * @param ctrlAttribute
     * @return
     */
    public static String getAdfControlAttributeValueFromRequest(final String ctrlAttribute) {
        String value;

        if (JSFUtils.getRequest().getParameterMap().get(ctrlAttribute) == null) {
            value = null;
        } else {
            value = ((String[])JSFUtils.getRequest().getParameterMap().get(ctrlAttribute))[0];
        } //end if

        return value;
    } //end method

    /**
     * @return
     */
    public static String getAdfStateToken() {
        return getAdfControlAttributeValueFromRequest("_adf.ctrl-state");
    } //end method
    
    public static AdfFacesContext getAdfFacesCtx() {
        return AdfFacesContext.getCurrentInstance();
    } //end method
    
    public static void addPartialTrigger(UIComponent... component) {
        if (component != null) {
            for (UIComponent cmp : component) {
                getAdfFacesCtx().addPartialTarget(cmp);
            } //end for
        } //end if
    } //end method
    
    public static void addPartialTriggerWithIdFromUiRoot(String... componentId) {
        UIComponent uiComp;
        if (componentId != null) {
            for (String cmpId : componentId) {
                if (cmpId != null && !cmpId.trim().isEmpty()) {
                    uiComp = findComponentInRoot(cmpId);
                    if (uiComp != null) {
                        addPartialTrigger(uiComp);
                    } //end if
                } //end if
            } //end for
        } //end if
    } //end method
    
    public static void writeJavaScriptToClient(String script) {
        FacesContext fctx;
        ExtendedRenderKitService erks;
        
        fctx = getFacesContext();
        erks = Service.getRenderKitService(fctx, ExtendedRenderKitService.class);
        
        erks.addScript(fctx, script);
    } //end method
    
    public static void setFocusOnComponent(UIXComponentBase component2focus) {
        FacesContext fctx;
        String clientId;
        StringBuilder script;
        
        fctx = getFacesContext();
        clientId = component2focus.getClientId(fctx);
        
        script = new StringBuilder();
        script.append("try {");
        script.append("var textInput = ");
        script.append("AdfPage.PAGE.findComponentByAbsoluteId");
        script.append("('");
        script.append(clientId);
        script.append("');");
        script.append("if (textInput != null) {");
        script.append("textInput.focus();");
        script.append("}");
        script.append("catch (_excecao) {}");
        
        writeJavaScriptToClient(script.toString());
    } //end method
    
    public static void setFocusOnComponent(final String componentHtmlId) {
        String clientId;
        StringBuilder script;
        
        clientId = componentHtmlId;
        
        script = new StringBuilder();
        script.append("var textInput = ");
        script.append("AdfPage.PAGE.findComponent");
        script.append("('");
        script.append(clientId);
        script.append("');\n");
        script.append("if (textInput != null) {\n");
        script.append("    textInput.focus();");
        script.append("}\n");
        
        writeJavaScriptToClient(script.toString());
    } //end method
    
    /**
     * Invokes an expression
     * @param expr
     * @param returnType
     * @param argTypes
     * @param args
     * @return
     */
    public static Object invokeMethodExpression(String expr, Class returnType, Class[] argTypes, Object[] args) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ELContext elctx = fc.getELContext();
        ExpressionFactory elFactory = fc.getApplication().getExpressionFactory();
        MethodExpression methodExpr = elFactory.createMethodExpression(elctx, expr, returnType, argTypes);
        return methodExpr.invoke(elctx, args);
    }

    /**
     * Invoke an expression
     * @param expr
     * @param returnType
     * @param argType
     * @param argument
     * @return
     */
    public static Object invokeMethodExpression(String expr, Class returnType, Class argType, Object argument) {
        return invokeMethodExpression(expr, returnType, new Class[] { argType }, new Object[] { argument });

    }
    
    public static void expandTree(final UIXTree tree) {
        expandTree(tree, true);
    } //end method
    
    public static void expandTree(final UIXTree tree, final boolean addPartialTrigger) {
        RowKeySet _disclosedRowKeys;
        
        if (tree != null) {
            _disclosedRowKeys = new RowKeySetTreeImpl(true);
            _disclosedRowKeys.setCollectionModel(ModelUtils.toTreeModel(tree.getValue()));
            
            tree.setDisclosedRowKeys(_disclosedRowKeys);
            
            if (addPartialTrigger) {
                JSFUtils.addPartialTrigger(tree);
            } //end if
        } //end if
    } //end method
    
    /**
     * Method to set the value of page flow scope created on runtime
     * @param name
     * @param value
     */
    public static void setPageFlowScopeValue(String name, Object value) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map pageFlowScope = adfCtx.getPageFlowScope();
        pageFlowScope.put(name, value);
    }

    public static FacesContext getFacesContextOnServlet(HttpServletRequest request, HttpServletResponse response) {
        FacesContext facesContext;
        
        facesContext = JSFUtils.getFacesContext();
        
        if (Bean.isNull(facesContext)) {
            FacesContextBuilder builder;
            
            builder = new FacesContextBuilder();
            
            facesContext = builder.getFacesContext(request, response);
            
            UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "");
            facesContext.setViewRoot(view); 
            
            builder = null;
        } //end if
        
        return facesContext;
    } //end method
    
    /**
     * Method to get the value of page flow scope created on runtime
     * @param name
     * @return
     */
    public static Object getPageFlowScopeValue(String name) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map pageFlowScope = adfCtx.getPageFlowScope();
        Object val = pageFlowScope.get(name);
        return val;
    }    
    
    public static Object getSessionScopeValue(String name) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sessionScope = adfCtx.getSessionScope();
        Object val = sessionScope.get(name);
        return val;
    }    
    
    /**
     * Pega um valor armazenado em uma variavel do pageFlow do tipo Integer.
     * @author leandro.lima@tarea.com.br
     * @param variable
     * @return
     */
    public static Integer getIntegerPageFlowVar(String variable) {
        Object object = JSFUtils.getPageFlowScopeValue(variable);
        Integer value = null;
        if (object != null) {
            if (object instanceof String) {
                value = new Integer(object.toString());
            }
            else if (object instanceof Integer) {
                value = (Integer)object;
            }
            else if (object instanceof Number) {
                value = ((Number)object).intValue();
            }
        }
        return value;
    }

    public static Integer getIntegerSessionVar(String variable) {
        Object object = JSFUtils.getSessionScopeValue(variable);
        Integer value = null;
        if (object != null) {
            if (object instanceof String) {
                value = new Integer(object.toString());
            }
            else if (object instanceof Integer) {
                value = (Integer)object;
            }
            else if (object instanceof Number) {
                value = ((Number)object).intValue();
            }
        }
        return value;
    }
    
    /**
     * Pega um valor armazenado em uma variavel do pageFlow do tipo Number.
     * @author leandro.lima@tarea.com.br
     * @param variable
     * @return
     */
    public static Number getNumberPageFlowVar(String variable) {
        Object object = JSFUtils.getPageFlowScopeValue(variable);
        Number value = null;
        if (object != null) {
            if (object instanceof String) {
                try {
                    value = new Number(object.toString());
                } catch (Exception e) {
                    value = null;
                }
            }
            else if (object instanceof Integer) {
                value = new Number(((Integer)object).intValue());
            }
            else if (object instanceof Long) {
                value = new Number(((Long)object).intValue());
            }
            else if (object instanceof Number) {
                value = ((Number)object);
            } else if (object instanceof Long) {
                value = new Number(((Long)object).intValue());
            }
        }
        return value;
    }

    public static Number getNumberSessionVar(String variable) {
        Object object = JSFUtils.getSessionScopeValue(variable);
        Number value = null;
        if (object != null) {
            if (object instanceof String) {
                try {
                    value = new Number(object.toString());
                } catch (Exception e) {
                    value = null;
                }
            }
            else if (object instanceof Integer) {
                value = new Number(((Integer)object).intValue());
            }
            else if (object instanceof Number) {
                value = ((Number)object);
            }
        }
        return value;
    }
    
    public static void expandTreeOnLevels(final UIXTree tree, final int levels, final boolean ignorePartialTrigger) {
        if (Bean.isNotNull(tree) && (levels > 0)) {
            RowKeySetImpl discolsedRows;
            CollectionModel model;
            JUCtrlHierBinding treeBinding;
            JUCtrlHierNodeBinding rootNodeBinding;
            DCIteratorBinding iteratorBinding;
            
            discolsedRows = new RowKeySetImpl();
            
            model = (CollectionModel)tree.getValue();
            
            treeBinding = (JUCtrlHierBinding)model.getWrappedData();
            rootNodeBinding = treeBinding.getRootNodeBinding();
            iteratorBinding = treeBinding.getDCIteratorBinding();
            
            expandAllNodes(rootNodeBinding, discolsedRows, 0, (levels - 1));
            
            tree.setDisclosedRowKeys(discolsedRows);
            
            if (!ignorePartialTrigger) {
                JSFUtils.addPartialTrigger(tree);
            } //end if
        } //end if
    }
    
    /**
     * Recursive method to expand nodes to a pre-defined level
     *
     * @param nodeBinding the JUCtrlHierNodeBinding representing the current node
     * @param disclosedKeys the RowKeySetImpl instance that holds the keys to disclose
     * @param currentExpandLevel the current depth of the tree node
     * @param maxExpandLevel the max. number of levels to expand nodes for
     */
    public static void expandAllNodes(JUCtrlHierNodeBinding nodeBinding,
                                RowKeySetImpl disclosedKeys,
                                int currentExpandLevel, int maxExpandLevel) {
        //logger.info("Iniciando expansao de nivel " + currentExpandLevel + "/" + maxExpandLevel);
            
        if (currentExpandLevel <= maxExpandLevel) {
            List<JUCtrlHierNodeBinding> childNodes = (List<JUCtrlHierNodeBinding>)nodeBinding.getChildren();
            ArrayList newKeys = new ArrayList();
            if (childNodes != null) {
                for (JUCtrlHierNodeBinding _node : childNodes) {
                    newKeys.add(_node.getKeyPath());
                    expandAllNodes(_node, disclosedKeys, currentExpandLevel + 1, maxExpandLevel);
                }
            }
            //else logger.info("-> Sem nos filhos...");
            
            //logger.info("Expandindo nivel " + currentExpandLevel + " - No " + newKeys);
            disclosedKeys.addAll(newKeys);
        }
    }

} //end class
