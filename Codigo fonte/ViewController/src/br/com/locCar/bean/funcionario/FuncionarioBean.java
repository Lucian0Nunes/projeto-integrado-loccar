package br.com.locCar.bean.funcionario;


import br.com.locCar.util.ADFUtils;
import br.com.locCar.util.GenericTableSelectionHandler;
import br.com.locCar.util.JSFUtils;
import br.com.locCar.util.ValidaCampos;

import java.security.NoSuchAlgorithmException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.jbo.Row;
import oracle.jbo.RowIterator;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;


public class FuncionarioBean extends ValidaCampos{
    private static final String EL_EXP_FUNCIONARIO_CURR_ROW = "#{bindings.TbFuncionarioView1.currentRow}";
    private String vlNome;
    private String vlCpf;
    private String vlTelefone;
    private String vlEmail;
    private String vlEndereco;
    private String vlSna;
    private String vlPerfil;
    private RichPopup popupInserir;
    private RichTable gridFuncionarios;
    private RichPopup popupEditar;


    public FuncionarioBean() {
    }

    public void chamadaPopupInclusao(PopupFetchEvent popupFetchEvent) {
        this.setVlPerfil("2");
        this.setVlNome("");
        this.setVlCpf("");
        this.setVlTelefone("");
        this.setVlEmail("");
        this.setVlEndereco("");
        this.setVlSna("");
    }

    public void inserirNovoFuncionario(ActionEvent actionEvent) {
        RowSetIterator iteratorFuncionario =
            ADFUtils.findIterator("TbFuncionarioView1Iterator").getRowSetIterator();

        Row criarRow = iteratorFuncionario.createRow();
        criarRow.setNewRowState(Row.STATUS_INITIALIZED);

        criarRow.setAttribute("Nome", this.getVlNome());
        criarRow.setAttribute("Cpf", this.getVlCpf());
        criarRow.setAttribute("Telefone", this.getVlTelefone());
        criarRow.setAttribute("Email", this.getVlEmail());
        criarRow.setAttribute("Endereco", this.getVlEndereco());

        //Gerando a senha com as tres primeiras letras do nome e 3 primeiros digitos do cpf
        String sn = getVlNome().replaceAll(" ", "");
        sn = sn.substring(0, 3);
        setVlCpf(getVlCpf().replaceAll("[^0-9]", ""));
        sn += getVlCpf().substring(0, 3);

        try {
            System.out.println("Senha: " + gerarMD5(sn));
            criarRow.setAttribute("Senha", gerarMD5(sn));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        criarRow.setAttribute("TbPerfilIdPerfil", this.getVlPerfil().toString());

        ADFUtils.executeBindingOperation("CommitTbFuncionario");
        refreshTable();
        GenericTableSelectionHandler.setFocusOnLine("TbFuncionarioView1Iterator",
                                                    gridFuncionarios,
                                                    "IdFuncionario",
                                                    criarRow.getAttribute("IdFuncionario").toString(),
                                                    null);  
        popupInserir.cancel();
        JSFUtils.addFacesConfirmationMessage("Dados gravados com sucesso!");
    }
    
    
    public void cancelarRegistro(ActionEvent actionEvent) {
        popupInserir.cancel();
    }
    
    public void chamadaPopupEditar(PopupFetchEvent popupFetchEvent) {
        Row row = (Row)ADFUtils.evaluateEL(EL_EXP_FUNCIONARIO_CURR_ROW);
        Number valor = (Number)row.getAttribute("TbPerfilIdPerfil");
        if (valor.equals(1)) {
            this.setVlPerfil("1");
        } else {
            this.setVlPerfil("2");

        }
    }
    
    public void gravarEdicao(ActionEvent actionEvent) throws Exception {
        try {            
            Row rw =
                    ADFUtils.findIterator("TbFuncionarioView1Iterator").getCurrentRow();
            rw.setAttribute("TbPerfilIdPerfil", this.getVlPerfil().toString());

            ADFUtils.executeBindingOperation("CommitTbFuncionario");    
            
            refreshTable();
            GenericTableSelectionHandler.setFocusOnLine("TbFuncionarioView1Iterator",
                                                            gridFuncionarios,
                                                            "IdFuncionario",
                                                            rw.getAttribute("IdFuncionario").toString(),
                                                            null);
                              
            JSFUtils.addPartialTriggerWithIdFromUiRoot("t1");
            JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl3");
            popupEditar.cancel();
            JSFUtils.addFacesConfirmationMessage("Dados alterados com sucesso!");
        } catch (Exception e) {
            JSFUtils.addFacesErrorMessage("Não foi possível gravar os dados alterados!");
            throw e;
        }
    }
    
    public void cancelarEdicao(ActionEvent actionEvent) {
        popupEditar.cancel();
    }
    
    public void excluirFuncionario(DialogEvent dialogEvent) throws Exception {
        try {
            ADFUtils.executeBindingOperation("Delete");
            ADFUtils.executeBindingOperation("CommitTbFuncionario");
            refreshTable();
            JSFUtils.addFacesInformationMessage("Exclu\u00EDdo com sucesso!");
        } catch (Exception e) {
            JSFUtils.addFacesErrorMessage("Não foi possível excluir");
            throw e;
        }

    }

    public void refreshTable() {
        DCIteratorBinding dcIter =
            ADFUtils.findIterator("TbFuncionarioView1Iterator");
        dcIter.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getGridFuncionarios());
        this.getGridFuncionarios().processUpdates(FacesContext.getCurrentInstance());
        //Este comando atualiza o toolbar que contem os bot�es, (T2) � o id do toolbar
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t2");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl3");
    }

    public Boolean getHabilitaBotoes() {
        Row rw = (Row)ADFUtils.evaluateEL(EL_EXP_FUNCIONARIO_CURR_ROW);
        if (rw == null) {
            return false;
        }
        return true;
    }
    
    public void verficarValidarCpf(FacesContext facesContext,
                                   UIComponent uIComponent, Object object) {
        String opcao = (String)object;

        DCIteratorBinding it =
            ADFUtils.findIterator("ExisteCpfTbFuncionario1Iterator");
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setNamedWhereClauseParam("cpf", opcao);
        view.executeQuery();

        RowIterator rsi =
            ADFUtils.findIterator("ExisteCpfTbFuncionario1Iterator").getRowSetIterator();

        Row row = null;

        opcao = opcao.replaceAll("[^0-9]", "");

        if (this.validaCPF(opcao) == true) {
            row = rsi.getCurrentRow();
            if (row != null) {
                FacesMessage msg =
                    new FacesMessage("CPF inv\u00E1lido", "CPF informado ja consta na base");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        } else {
            FacesMessage msg =
                new FacesMessage("CPF inv\u00E1lido", "Informe um CPF \nv\u00E1lido");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }
    
    public void setPopupInserir(RichPopup popupInserir) {
        this.popupInserir = popupInserir;
    }

    public RichPopup getPopupInserir() {
        return popupInserir;
    }

    public void setVlNome(String vlNome) {
        this.vlNome = vlNome;
    }

    public String getVlNome() {
        return vlNome;
    }

    public void setVlCpf(String vlCpfCnpj) {
        this.vlCpf = vlCpfCnpj;
    }

    public String getVlCpf() {
        return vlCpf;
    }

    public void setVlTelefone(String vlTelefone) {
        this.vlTelefone = vlTelefone;
    }

    public String getVlTelefone() {
        return vlTelefone;
    }

    public void setVlEmail(String vlEmail) {
        this.vlEmail = vlEmail;
    }

    public String getVlEmail() {
        return vlEmail;
    }

    public void setVlEndereco(String vlEndereco) {
        this.vlEndereco = vlEndereco;
    }

    public String getVlEndereco() {
        return vlEndereco;
    }

    public void setVlSna(String vlSna) {
        this.vlSna = vlSna;
    }

    public String getVlSna() {
        return vlSna;
    }


    public void setGridFuncionarios(RichTable gridFuncionarios) {
        this.gridFuncionarios = gridFuncionarios;
    }

    public RichTable getGridFuncionarios() {
        return gridFuncionarios;
    }

    public void setVlPerfil(String vlPerfil) {
        this.vlPerfil = vlPerfil;
    }

    public String getVlPerfil() {
        return vlPerfil;
    }

    public void setPopupEditar(RichPopup popupEditar) {
        this.popupEditar = popupEditar;
    }

    public RichPopup getPopupEditar() {
        return popupEditar;
    }
}

