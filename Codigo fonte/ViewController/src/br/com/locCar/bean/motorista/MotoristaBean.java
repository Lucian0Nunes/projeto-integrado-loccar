package br.com.locCar.bean.motorista;

import br.com.locCar.util.ADFUtils;
import br.com.locCar.util.GenericTableSelectionHandler;
import br.com.locCar.util.JSFUtils;

import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;

public class MotoristaBean {
    
    private static final String EL_EXP_CATEGORIA_CURR_ROW = "#{bindings.TbCatHabView1.currentRow}";
    
    private String vlNome;
    private String vlCpf;
    private String vlTelefone;
    private String vlEmail;
    private String vlEndereco;
    private RichSelectOneChoice comboCategoria;
    private RichPopup popupInserir;

    public MotoristaBean() {
    }
    
    public void chamadaPopupInclusao(PopupFetchEvent popupFetchEvent) {
        this.setVlNome("");
        this.setVlCpf("");
        this.setVlTelefone("");
        this.setVlEmail("");
    }

    public void inserirNovoMotorista(ActionEvent actionEvent) {
        
        RowSetIterator iteratorMotorista=
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

       // refreshTable();

       /*  GenericTableSelectionHandler.setFocusOnLine("VeiculoMMView1Iterator",
                                                    gridVeiculo, "IdVeiculo",
                                                    criarRow.getAttribute("IdVeiculo").toString(),
                                                    null); */
        popupInserir.cancel();
        JSFUtils.addFacesConfirmationMessage("Dados gravados com sucesso!");
        
    }

    public void cancelarRegistro(ActionEvent actionEvent) {
        popupInserir.cancel();
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

    public void setComboCategoria(RichSelectOneChoice comboCategoria) {
        this.comboCategoria = comboCategoria;
    }

    public RichSelectOneChoice getComboCategoria() {
        return comboCategoria;
    }

    public void setPopupInserir(RichPopup popupInserir) {
        this.popupInserir = popupInserir;
    }

    public RichPopup getPopupInserir() {
        return popupInserir;
    }
}
