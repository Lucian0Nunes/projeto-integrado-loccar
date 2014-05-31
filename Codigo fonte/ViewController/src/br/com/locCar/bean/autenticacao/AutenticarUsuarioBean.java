package br.com.locCar.bean.autenticacao;


import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;

import oracle.adf.model.BindingContext;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;


public class AutenticarUsuarioBean {
    public AutenticarUsuarioBean() {
        super();
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public String cmdLogin_action() {
        BindingContainer bindings = getBindings();
        OperationBinding operationBinding =
            bindings.getOperationBinding("autenticarUsuario");
        Object result = operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            return null;

        }

        int ret =
            ((Integer)result).intValue(); // convert the result of checkUser into int type
        if (ret == 0) {
            String message = "Login invalido!";
            FacesContext.getCurrentInstance().addMessage(null,
                                                         new FacesMessage(message)); // add a message in the faces context to be displayed in the messages component of the page

            HttpServletRequest req =
                (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            req.getSession().setAttribute("fromPath", req.getRequestURI());
            return "erro";

        } else {
            String userId =
                bindings.getControlBinding("userid").toString(); // read the value from the bindings for the userid textItem component
            System.out.println("userId = " + userId);
            HttpServletRequest req =
                (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            req.getSession().setAttribute("login",
                                          userId); // write the value of the Userid in a session attribute named �login� so that it can be used by others pages
            return "inicio"; // navigation to the page pointed by the Navigation Case �success�
        }


    }

   /*  public String autenticar() {

        String operacao = "login";
        DCIteratorBinding itMail =
            ADFUtils.findIterator("TbFuncionarioView1Iterator");
        ViewObject view = itMail.getViewObject();
        view.reset();
        view.clearCache();
        view.setWhereClause("EMAIL='" + getUsername() + "'");
        view.executeQuery();

        Row rowMail = itMail.getCurrentRow();

        DCIteratorBinding itPw =
            ADFUtils.findIterator("TbFuncionarioView1Iterator");
        ViewObject viewPw = itPw.getViewObject();
        viewPw.reset();
        viewPw.clearCache();
        viewPw.setWhereClause("SENHA='" + getSecret() + "'");
        viewPw.executeQuery();

        Row rowPw = itPw.getCurrentRow();

        if (rowMail == null) {
            System.out.println("Usuario errado");
            operacao = "erro";
            JSFUtils.addFacesErrorMessage("Usuário inválido!");
            setUsername("");
            setSecret("");
        }
        if (rowPw == null) {
            System.out.println("Senha errada");
            JSFUtils.addFacesErrorMessage("Senha inválida!");
            setSecret("");
            operacao = "erro";
        }
        if (rowMail != null && rowPw != null) {
            operacao = "inicio";
        }
        return operacao;
    } //end method */
} //end class
