package br.com.locCar.bean.cliente;


import br.com.locCar.util.ADFUtils;
import br.com.locCar.util.GenericTableSelectionHandler;
import br.com.locCar.util.JSFUtils;
import br.com.locCar.util.ValidarUtil;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.logging.ADFLogger;
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


public class ClienteBean extends ValidarUtil {
    
    private static ADFLogger logger = ADFLogger.createADFLogger(ClienteBean.class);
    
    private static final String EL_EXP_CLIENTE_CURR_ROW = "#{bindings.TbClienteView1.currentRow}";
    private static final String EMPTY = "";

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
        super();
    }

    public void chamadaPopupInclusao(PopupFetchEvent popupFetchEvent) {
        labelEMaskCpf();
        this.setVlCpfCnpj(EMPTY);
        this.setVlNome(EMPTY);
        this.setVlTelefone(EMPTY);
        this.setVlEndereco(EMPTY);
        this.setVlEmail(EMPTY);
    }


    public void incluirNovo(ActionEvent actionEvent){
        try {
            final RowSetIterator iteratorCliente = ADFUtils.findIterator("TbClienteView1Iterator").getRowSetIterator();
            final Row criarRow = iteratorCliente.createRow();
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
            logger.warning("Erro ao incluir ==>> "+ e.getMessage());
        }//end try... catch
    }//end method

    public void cancelarNovo(ActionEvent actionEvent) {
        popupInserir.cancel();
    }//end method

    public void chamadaPopupEditar(PopupFetchEvent popupFetchEvent) {
        final Row row = (Row)ADFUtils.evaluateEL(EL_EXP_CLIENTE_CURR_ROW);
        String valor = (String)row.getAttribute("CpfCnpj");
        valor = valor.replaceAll("[^0-9]", "");
        if (valor.length() == 11) {
            labelEMaskCpf();
        } else {
            labelEMaskCnpj();
        }//end if
    }//end method

    public void gravarEdicao(ActionEvent actionEvent){
        try {        
            final Row rw = ADFUtils.findIterator("TbClienteView1Iterator").getCurrentRow();            

            ADFUtils.executeBindingOperation("CommitTbCliente");    
            refreshTable();
            GenericTableSelectionHandler.setFocusOnLine("TbClienteView1Iterator",
                                                            gridCliente,
                                                            "IdCliente",
                                                            rw.getAttribute("IdCliente").toString(),
                                                            null);
                              
            JSFUtils.addPartialTriggerWithIdFromUiRoot("t1","pfl3");
            popupEditar.cancel();
            JSFUtils.addFacesConfirmationMessage("Dados alterados com sucesso!");
        } catch (Exception e) {
            JSFUtils.addFacesErrorMessage("Não foi possível gravar os dados alterados!");
            logger.warning("Erro ao gravar ==>> "+ e.getMessage());
        }//end try... catch
    }//end method

    public void cancelarEdicao(ActionEvent actionEvent) {
        popupEditar.cancel();
    }//end method


    public void excluirCliente(DialogEvent dialogEvent){
        try {
            Row rw = ADFUtils.findIterator("TbClienteView1Iterator").getCurrentRow();            
            if(rw != null){
                final Number id = (Number)rw.getAttribute("IdCliente");                
                if(podeExcluir(id)){
                    ADFUtils.executeBindingOperation("Delete");
                    ADFUtils.executeBindingOperation("CommitTbCliente");
                    refreshTable();
                    JSFUtils.addFacesInformationMessage("Exclu\u00EDdo com sucesso!");        
                } else {
                    JSFUtils.addFacesWarningMessage("Cliente vinculado a uma OS. Proibida a exclus\u00E3o!");
                }//end if        
            }//end if
        } catch (Exception e) {
            logger.severe("Erro ao excluir ==>> "+ e.getMessage());
        }//end try... catch
    }//end  method
    
    public Boolean podeExcluir(final Number id) {
        final DCIteratorBinding it = ADFUtils.findIterator("TbOrdemDeServicoView1Iterator");
        final ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setWhereClause("FK_CLIENTE = " + id);
        view.executeQuery();
        final RowSetIterator iterator = ADFUtils.findIterator("TbOrdemDeServicoView1Iterator").getRowSetIterator();
        final Row rows = iterator.getCurrentRow();
        if (rows == null) {
            return true;            
        } else {
            return false;    
        }//end if
    } //end
    
    public void refreshTable() {
        final DCIteratorBinding dcIter = ADFUtils.findIterator("TbClienteView1Iterator");
        dcIter.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getGridCliente());
        this.getGridCliente().processUpdates(FacesContext.getCurrentInstance());
        //Este comando atualiza o toolbar que contem os bot�es, (T2) � o id do toolbar
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t2","pfl3");
    }

    public Boolean getHabilitaBotoes() {
        Row rw = (Row)ADFUtils.evaluateEL(EL_EXP_CLIENTE_CURR_ROW);
        if (rw == null) {
            return false;
        }//end if
        return true;
    }//end method


    public void setaMaskCpfCnpj(ValueChangeEvent vce) {
        final String valor = (String)vce.getNewValue();
        if ("CPF".equals(valor)) {
            labelEMaskCpf();
        } else {
            labelEMaskCnpj();
        }//end if
    }//end method


    public void validaCpfECnpj(FacesContext facesContext,
                               UIComponent uIComponent, Object object) {
        String opcao = (String)object;

        DCIteratorBinding it = ADFUtils.findIterator("ExisteCpfOuCnjp1Iterator");
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setNamedWhereClauseParam("cpf", opcao);
        view.executeQuery();

        final RowIterator rsi = ADFUtils.findIterator("ExisteCpfOuCnjp1Iterator").getRowSetIterator();

        Row row = null;

        opcao = opcao.replaceAll("[^0-9]", "");

        if (this.getEscolha().equals("CPF")) {
            if ((opcao.length() == 11) && (this.validaCPF(opcao) == true)) {
                row = rsi.getCurrentRow();
                if (row != null) {
                    final FacesMessage msg =  new FacesMessage("CPF inv\u00E1lido", "CPF informado ja consta na base");
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(msg);
                }
            } else {
                FacesMessage msg = new FacesMessage("CPF inv\u00E1lido", "Informe um CPF \nv\u00E1lido");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }//end if

        } else {
            if ((opcao.length() == 14) && (this.validaCnpj(opcao) == true)) {
                row = rsi.getCurrentRow();
                if (row != null) {
                    FacesMessage msg = new FacesMessage("CNPJ inv\u00E1lido", "CNPJ informado ja consta na base");
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(msg);
                }//end if
            } else {
                FacesMessage msg = new FacesMessage("CNPJ inv\u00E1lido", "Informe um CNPJ \nv\u00E1lido");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }//end if
        }//end if
    }//end method


    public void labelEMaskCpf() {
        this.setNome("Nome completo");
        this.setEscolha("CPF");
        this.setMascara("fixed.cpf");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it1","it2");
    }//end method

    public void labelEMaskCnpj() {
        this.setNome("Raz\u00E3o Social");
        this.setEscolha("CNPJ");
        this.setMascara("fixed.cnpj");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it1","it2");
    }//end mehtod

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
