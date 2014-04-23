package br.com.locCar.user;


import br.com.locCar.bean.autenticacao.AutenticarUsuarioBean;
import br.com.locCar.bean.funcionario.FuncionarioBean;
import br.com.locCar.util.JSFUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;

import oracle.adf.share.logging.ADFLogger;


public class InformacaoUsuarioBean {

    private static final ADFLogger logger =
        ADFLogger.createADFLogger(InformacaoUsuarioBean.class);

    public InformacaoUsuarioBean() {
        super();
    }

    public static String getHostName() {
        String hostname;
        InetAddress addr;
        String ipAddr;

        try {
            addr = InetAddress.getLocalHost();
            ipAddr = addr.getHostAddress();
            hostname = addr.getHostName();
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
            hostname = "****";
            ipAddr = "****";
        } //ebd try ... catch

        return String.format("%s/%s", hostname, ipAddr);
    } //end method


    public String getContextPath() {
        return JSFUtils.getAppContextPath();
    }

    public String getServerPath() {
        return JSFUtils.getServerPath();
    }
}
