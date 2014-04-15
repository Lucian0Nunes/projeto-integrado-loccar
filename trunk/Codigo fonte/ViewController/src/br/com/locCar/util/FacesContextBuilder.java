package br.com.locCar.util;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


public class FacesContextBuilder {
    public FacesContext getFacesContext(final ServletRequest request, final ServletResponse response) {
        FacesContext facesContext;
        
        facesContext = FacesContext.getCurrentInstance();
        
        if (facesContext == null) {
            ServletContext servletContext;
            FacesContextFactory contextFactory;
            LifecycleFactory lifecycleFactory;
            Lifecycle lifecycle;
            
            contextFactory = (FacesContextFactory)FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            lifecycleFactory = (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

            servletContext = ((HttpServletRequest)request).getSession().getServletContext();
            facesContext = contextFactory.getFacesContext(servletContext, request, response, lifecycle);
            InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);
        }

        return facesContext;
    }

    public void removeFacesContext() {
        InnerFacesContext.setFacesContextAsCurrentInstance(null);
    }

    private abstract static class InnerFacesContext extends FacesContext {
        protected static void setFacesContextAsCurrentInstance(final FacesContext facesContext) {
            FacesContext.setCurrentInstance(facesContext);
        }
    }

}
