package br.com.locCar.bean.motorista;

import br.com.locCar.util.ADFUtils;
import br.com.locCar.util.GenericTableSelectionHandler;
import br.com.locCar.util.JSFUtils;

import br.com.locCar.util.ValidaCampos;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.jbo.Row;
import oracle.jbo.RowIterator;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;

public class MotoristaBean extends ValidaCampos {
    
    private static final String EL_EXP_MOTORISTA_CURR_ROW = "#{bindings.MotoristaCategoriaView1.currentRow}";
    private static final String EL_EXP_CATEGORIA_CURR_ROW = "#{bindings.TbCatHabView1.currentRow}";

    private String vlNome;
    private String vlCpf;
    private String vlTelefone;
    private String vlEmail;
    private String vlEndereco;
    private RichPopup popupInserir;
    private RichTable gridMotorista;
    private RichSelectOneChoice comboCategoria;
    private RichPopup popupEditar;

    public MotoristaBean() {
    }

    public void chamadaPopupInclusao(PopupFetchEvent popupFetchEvent) {
        refreshComboCategoria();
        this.setVlNome("");
        this.setVlCpf("");
        this.setVlTelefone("");
        this.setVlEmail("");
        this.setVlEndereco("");
    }

    public void inserirNovoMotorista(ActionEvent actionEvent) {

        RowSetIterator iteratorMotorista =
            ADFUtils.findIterator("TbMotoristaView1Iterator").getRowSetIterator();

        Row rw = (Row)ADFUtils.evaluateEL(EL_EXP_CATEGORIA_CURR_ROW);

        Row criarRow = iteratorMotorista.createRow();
        criarRow.setNewRowState(Row.STATUS_INITIALIZED);

        criarRow.setAttribute("Nome", this.getVlNome());
        criarRow.setAttribute("Cpf", this.getVlCpf());
        criarRow.setAttribute("Telefone", this.getVlTelefone());
        criarRow.setAttribute("Email", this.getVlEmail());
        criarRow.setAttribute("Endereco", this.getVlEndereco());
        criarRow.setAttribute("FkCatHab", rw.getAttribute("IdCat"));

        ADFUtils.executeBindingOperation("CommitTbMotorista");

        refreshTable();

        GenericTableSelectionHandler.setFocusOnLine("MotoristaCategoriaView1Iterator",
                                                    gridMotorista,
                                                    "IdMotorista",
                                                    criarRow.getAttribute("IdMotorista").toString(),
                                                    null);
        popupInserir.cancel();
        JSFUtils.addFacesConfirmationMessage("Dados gravados com sucesso!");

    }

    public void cancelarRegistro(ActionEvent actionEvent) {
        popupInserir.cancel();
    }
    

    public void chamadaPopupEdicao(PopupFetchEvent popupFetchEvent) throws Exception {
        try {
            
            DCIteratorBinding it =
                ADFUtils.findIterator("TbMotoristaView1Iterator");
            ViewObject view = it.getViewObject();
            view.reset();
            view.clearCache();
            view.setWhereClause("ID_MOTORISTA = " +
                                motoristaSelecionado().getAttribute("IdMotorista"));
            view.executeQuery();
            
            Number idCategoria =
                (Number)motoristaSelecionado().getAttribute("IdCat");
         
            Number idCat;

            RowSetIterator iteratorCategoria =
                ADFUtils.findIterator("TbCatHabView1Iterator").getRowSetIterator();

            Row[] linhas = iteratorCategoria.getAllRowsInRange();

            for (Row rw : linhas) {
                idCat = (Number)rw.getAttribute("IdCat");
                
                if (idCategoria.equals(idCat)) {
                    iteratorCategoria.setCurrentRow(rw);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void gravarEdicao(ActionEvent actionEvent) throws Exception {
        try {
              Row row =
                ADFUtils.findIterator("TbMotoristaView1Iterator").getCurrentRow();

            RowSetIterator iteratorCategoria =
                ADFUtils.findIterator("TbCatHabView1Iterator").getRowSetIterator();
            Row rowCategoria = iteratorCategoria.getCurrentRow();

            row.setAttribute("FkCatHab", rowCategoria.getAttribute("IdCat"));


            ADFUtils.executeBindingOperation("CommitTbMotorista");
            refreshTable();
            GenericTableSelectionHandler.setFocusOnLine("MotoristaCategoriaView1Iterator",
                                                        gridMotorista,
                                                        "IdMotorista",
                                                        row.getAttribute("IdMotorista").toString(),
                                                        null);
            popupEditar.cancel();
            JSFUtils.addFacesConfirmationMessage("Dados alterados com sucesso!");
        } catch (Exception e) {
            JSFUtils.addFacesErrorMessage("Não foi possível gravar!");
            throw e;
        }
    }
    
    public void cancelarEdicao(ActionEvent actionEvent) {
        popupEditar.cancel();
    }
    
    public void excluirMotorista(DialogEvent dialogEvent) throws Exception {
        try {
            DCIteratorBinding it =
                ADFUtils.findIterator("TbMotoristaView1Iterator");
            ViewObject view = it.getViewObject();
            view.reset();
            view.clearCache();
            view.setWhereClause("ID_MOTORISTA = " +
                                motoristaSelecionado().getAttribute("IdMotorista"));
            view.executeQuery();

            ADFUtils.executeBindingOperation("Delete");
            ADFUtils.executeBindingOperation("CommitTbMotorista");
            refreshTable();
            JSFUtils.addFacesInformationMessage("Exclu\u00EDdo com sucesso!");
        } catch (Exception e) {
            JSFUtils.addFacesErrorMessage("Não foi possível excluir");
            throw e;
        }

    }
    
    public Boolean getHabilitaBotoes() {
        Row rw = (Row)ADFUtils.evaluateEL(EL_EXP_MOTORISTA_CURR_ROW);
        if (rw == null) {
            return false;
        }
        return true;
    }

    public void refreshTable() {
        DCIteratorBinding dcIter =
            ADFUtils.findIterator("MotoristaCategoriaView1Iterator");
        dcIter.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getGridMotorista());
        this.getGridMotorista().processUpdates(FacesContext.getCurrentInstance());
        //Este comando atualiza o toolbar que contem os bot�es, (T2) � o id do toolbar
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t2");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl3");
    }
    
    public void refreshComboCategoria() {
        DCIteratorBinding dcIter =
            ADFUtils.findIterator("TbCatHabView1Iterator");
        dcIter.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getComboCategoria());
        this.getComboCategoria().processUpdates(FacesContext.getCurrentInstance());
    }

    public void verficarValidarCpf(FacesContext facesContext,
                                   UIComponent uIComponent, Object object) {
        String opcao = (String)object;

        DCIteratorBinding it =
            ADFUtils.findIterator("ExisteCpfTbMot1Iterator");
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setNamedWhereClauseParam("cpf", opcao);
        view.executeQuery();

        RowIterator rsi =
            ADFUtils.findIterator("ExisteCpfTbMot1Iterator").getRowSetIterator();

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
    public Row motoristaSelecionado() {
        Row rw = (Row)ADFUtils.evaluateEL(EL_EXP_MOTORISTA_CURR_ROW);
        return rw;
    }

    public void setVlNome(String vlNome) {
        this.vlNome = vlNome;
    }

    public String getVlNome() {
        return vlNome;
    }

    public void setVlCpf(String vlCpf) {
        this.vlCpf = vlCpf;
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

    public void setPopupInserir(RichPopup popupInserir) {
        this.popupInserir = popupInserir;
    }

    public RichPopup getPopupInserir() {
        return popupInserir;
    }

    public void setGridMotorista(RichTable gridMotorista) {
        this.gridMotorista = gridMotorista;
    }

    public RichTable getGridMotorista() {
        return gridMotorista;
    }

    public void setComboCategoria(RichSelectOneChoice comboCategoria) {
        this.comboCategoria = comboCategoria;
    }

    public RichSelectOneChoice getComboCategoria() {
        return comboCategoria;
    }

    public void setPopupEditar(RichPopup popupEditar) {
        this.popupEditar = popupEditar;
    }

    public RichPopup getPopupEditar() {
        return popupEditar;
    }
}
