package br.com.locCar.bean.cliente;


import br.com.locCar.util.ADFUtils;
import br.com.locCar.util.GenericTableSelectionHandler;
import br.com.locCar.util.JSFUtils;
import br.com.locCar.util.ValidaCampos;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
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


public class ClienteBean extends ValidaCampos {
    private static final String EL_EXP_CLIENTE_CURR_ROW = "#{bindings.TbClienteView1.currentRow}";

    private String escolha;
    private String nome;
    private String mascara;
    private RichTable gridCliente;
    private RichPopup popupInserir;
    private RichPopup popupEditar;
    
    private String vlNome;
    private String vlCpfCnpj;
    private String vlTelefone;
    private String vlEmail;
    private String vlEndereco;
    

    public ClienteBean() {
    }

    public void chamadaPopupInclusao(PopupFetchEvent popupFetchEvent) {
        labelEMaskCpf();
        this.setVlCpfCnpj("");
        this.setVlNome("");
        this.setVlTelefone("");
        this.setVlEndereco("");
        this.setVlEmail("");
    }


    public void incluirNovo(ActionEvent actionEvent) throws Exception {
        try {
            RowSetIterator iteratorCliente =
                ADFUtils.findIterator("TbClienteView1Iterator").getRowSetIterator();
            Row criarRow = iteratorCliente.createRow();
            criarRow.setNewRowState(Row.STATUS_INITIALIZED);

            criarRow.setAttribute("Nome", this.getVlNome());
            criarRow.setAttribute("CpfCnpj", this.getVlCpfCnpj());
            criarRow.setAttribute("Telefone", this.getVlTelefone());
            criarRow.setAttribute("Email", this.getVlEmail());
            criarRow.setAttribute("Endereco", this.getVlEndereco());

            ADFUtils.executeBindingOperation("CommitTbCliente");

            refreshTable();

            GenericTableSelectionHandler.setFocusOnLine("TbClienteView1Iterator",
                                                        gridCliente,
                                                        "IdCliente",
                                                        criarRow.getAttribute("IdCliente").toString(),
                                                        null);

            popupInserir.cancel();
            JSFUtils.addFacesConfirmationMessage("Dados gravados com sucesso!");
        } catch (Exception e) {
            JSFUtils.addFacesErrorMessage("Não foi possível gravar!");
            throw e;
        }

    }

    public void cancelarNovo(ActionEvent actionEvent) {
        popupInserir.cancel();
    }

    public void chamadaPopupEditar(PopupFetchEvent popupFetchEvent) {
        Row row = (Row)ADFUtils.evaluateEL(EL_EXP_CLIENTE_CURR_ROW);
        String valor = (String)row.getAttribute("CpfCnpj");
        valor = valor.replaceAll("[^0-9]", "");
        if (valor.length() == 11) {
            labelEMaskCpf();
        } else {
            labelEMaskCnpj();

        }
    }

    public void gravarEdicao(ActionEvent actionEvent) throws Exception {
        try {
            
            Row rw =
                    ADFUtils.findIterator("TbClienteView1Iterator").getCurrentRow();            

            ADFUtils.executeBindingOperation("CommitTbCliente");    
            
            refreshTable();
            GenericTableSelectionHandler.setFocusOnLine("TbClienteView1Iterator",
                                                            gridCliente,
                                                            "IdCliente",
                                                            rw.getAttribute("IdCliente").toString(),
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


    public void excluirCliente(DialogEvent dialogEvent) throws Exception {
        try {
            ADFUtils.executeBindingOperation("Delete");
            List erros =  ADFUtils.executeBindingOperation("CommitTbCliente");

            if(erros != null && erros.size()>0){
                ADFUtils.executeBindingOperation("Rollback");
                JSFUtils.addFacesErrorMessage("Não foi possível excluir!");
                return;
            }
            refreshTable();
            JSFUtils.addFacesInformationMessage("Exclu\u00EDdo com sucesso!");
        } catch (Exception e) {
            ADFUtils.executeBindingOperation("Rollback");
            JSFUtils.addFacesErrorMessage("Não foi possível excluir!"); 
        }
    }
    
    public void refreshTable() {
        DCIteratorBinding dcIter =
            ADFUtils.findIterator("TbClienteView1Iterator");
        dcIter.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getGridCliente());
        this.getGridCliente().processUpdates(FacesContext.getCurrentInstance());
        //Este comando atualiza o toolbar que contem os bot�es, (T2) � o id do toolbar
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t2");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl3");
    }

    public Boolean getHabilitaBotoes() {
        Row rw = (Row)ADFUtils.evaluateEL(EL_EXP_CLIENTE_CURR_ROW);
        if (rw == null) {
            return false;
        }
        return true;
    }


    public void setaMaskCpfCnpj(ValueChangeEvent vce) {
        String valor = (String)vce.getNewValue();
        if (valor.equals("CPF")) {
            labelEMaskCpf();

        } else {
            labelEMaskCnpj();
        }
    }


    public void validaCpfECnpj(FacesContext facesContext,
                               UIComponent uIComponent, Object object) {
        String opcao = (String)object;

        DCIteratorBinding it =
            ADFUtils.findIterator("ExisteCpfOuCnjp1Iterator");
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setNamedWhereClauseParam("cpf", opcao);
        view.executeQuery();

        RowIterator rsi =
            ADFUtils.findIterator("ExisteCpfOuCnjp1Iterator").getRowSetIterator();

        Row row = null;

        opcao = opcao.replaceAll("[^0-9]", "");

        if (this.getEscolha().equals("CPF")) {
            if ((opcao.length() == 11) && (this.validaCPF(opcao) == true)) {
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

        } else {
            if ((opcao.length() == 14) && (this.validaCnpj(opcao) == true)) {
                row = rsi.getCurrentRow();
                if (row != null) {
                    FacesMessage msg =
                        new FacesMessage("CNPJ inv\u00E1lido", "CNPJ informado ja consta na base");
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(msg);
                }
            } else {
                FacesMessage msg =
                    new FacesMessage("CNPJ inv\u00E1lido", "Informe um CNPJ \nv\u00E1lido");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }


    public void labelEMaskCpf() {
        this.setNome("Nome completo");
        this.setEscolha("CPF");
        this.setMascara("fixed.cpf");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it1");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it2");

    }

    public void labelEMaskCnpj() {
        this.setNome("Raz\u00E3o Social");
        this.setEscolha("CNPJ");
        this.setMascara("fixed.cnpj");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it1");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it2");
    }

    public void setGridCliente(RichTable gridCliente) {
        this.gridCliente = gridCliente;
    }

    public RichTable getGridCliente() {
        return gridCliente;
    }

    public void setEscolha(String escolha) {
        this.escolha = escolha;
    }

    public String getEscolha() {
        return escolha;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setMascara(String mascara) {
        this.mascara = mascara;
    }

    public String getMascara() {
        return mascara;
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

    public void setVlCpfCnpj(String vlCpfCnpj) {
        this.vlCpfCnpj = vlCpfCnpj;
    }

    public String getVlCpfCnpj() {
        return vlCpfCnpj;
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

    public void setVlEndereco(String endereco) {
        this.vlEndereco = endereco;
    }

    public String getVlEndereco() {
        return vlEndereco;
    }

    public void setPopupEditar(RichPopup popupEditar) {
        this.popupEditar = popupEditar;
    }

    public RichPopup getPopupEditar() {
        return popupEditar;
    }
}
