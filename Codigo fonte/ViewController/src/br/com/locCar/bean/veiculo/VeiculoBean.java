package br.com.locCar.bean.veiculo;

import br.com.locCar.util.ADFUtils;
import br.com.locCar.util.GenericTableSelectionHandler;
import br.com.locCar.util.JSFUtils;


import br.com.locCar.util.ValidaCampos;

import java.text.Format;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.jbo.Row;

import oracle.jbo.RowIterator;
import oracle.jbo.RowSetIterator;

import oracle.jbo.ViewObject;

import oracle.jbo.domain.Number;


public class VeiculoBean extends ValidaCampos {
    private static final String EL_EXP_VEICULO_CURR_ROW = "#{bindings.VeiculoMMView1.currentRow}";
    private static final String EL_EXP_MODELO_CURR_ROW = "#{bindings.ListarModeloView1.currentRow}";

    protected Number idMarca;
    
    private RichTable gridVeiculo;
    
    private String vlAno;
    private String vlPlaca;
    private String vlProprietario;
    private String vlTelefone;
    
    private RichSelectOneChoice comboMarca;
    private RichSelectOneChoice comboModelo;
    private List<SelectItem> comboAno;
    
    private RichPopup popupInserir;
    private RichPopup popupEditar;


    public VeiculoBean() {
    }

    public void chamadaPopupInclusao(PopupFetchEvent popupFetchEvent) {
        refreshComboMarca();
        RowSetIterator iteratorMarca =
            ADFUtils.findIterator("ListarMarcaView1Iterator").getRowSetIterator();
        Row row = iteratorMarca.getCurrentRow();
        Number id = (Number)row.getAttribute("IdMarca");

        this.setIdMarca(id);
        ADFUtils.executeBindingOperation("BuscarModelos");
        this.setVlAno(null);
        this.setVlPlaca("");
        this.setVlProprietario("");
        this.setVlTelefone("");
    }

    public void incluirNovoVeiculo(ActionEvent actionEvent) {
        RowSetIterator iteratorVeiculo =
            ADFUtils.findIterator("TbVeiculoView1Iterator").getRowSetIterator();

        Row rw = (Row)ADFUtils.evaluateEL(EL_EXP_MODELO_CURR_ROW);

        Row criarRow = iteratorVeiculo.createRow();
        criarRow.setNewRowState(Row.STATUS_INITIALIZED);
        System.out.println("id modelo: " + rw.getAttribute("IdModelo"));
        criarRow.setAttribute("Ano", this.getVlAno());
        criarRow.setAttribute("Placa", this.getVlPlaca());
        criarRow.setAttribute("Proprietario", this.getVlProprietario());
        criarRow.setAttribute("Telefone", this.getVlTelefone());
        criarRow.setAttribute("FkModelo", rw.getAttribute("IdModelo"));

        ADFUtils.executeBindingOperation("CommitTbVeiculo");

        refreshTable();

        GenericTableSelectionHandler.setFocusOnLine("VeiculoMMView1Iterator",
                                                    gridVeiculo, "IdVeiculo",
                                                    criarRow.getAttribute("IdVeiculo").toString(),
                                                    null);
        popupInserir.cancel();
        JSFUtils.addFacesConfirmationMessage("Dados gravados com sucesso!");
    }

    public void cancelarRegistro(ActionEvent actionEvent) {
        popupInserir.cancel();
    }


    public void chamadaPopupEdicao(PopupFetchEvent popupFetchEvent) throws Exception {
        try {
            Number idMarc =
                (Number)veiculoSelecionado().getAttribute("IdMarca");
            Number IdModel =
                (Number)veiculoSelecionado().getAttribute("IdModelo");
            Number idMarc1;
            Number idModel1;

            RowSetIterator iteratorMarca =
                ADFUtils.findIterator("ListarMarcaView1Iterator").getRowSetIterator();

            Row[] linhas = iteratorMarca.getAllRowsInRange();

            for (Row marca : linhas) {
                idMarc1 = (Number)marca.getAttribute("IdMarca");

                if (idMarc.equals(idMarc1)) {
                    iteratorMarca.setCurrentRow(marca);
                    this.setIdMarca(idMarc);
                }
            }

            ADFUtils.executeBindingOperation("BuscarModelos");

            RowSetIterator iteratorModelos =
                ADFUtils.findIterator("ListarModeloView1Iterator").getRowSetIterator();
            Row[] linhasModelos = iteratorModelos.getAllRowsInRange();

            for (Row modelo : linhasModelos) {
                idModel1 = (Number)modelo.getAttribute("IdModelo");

                if (IdModel.equals(idModel1))
                    iteratorModelos.setCurrentRow(modelo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        this.setVlAno(veiculoSelecionado().getAttribute("Ano").toString());
        this.setVlPlaca(veiculoSelecionado().getAttribute("Placa").toString());
        this.setVlProprietario(veiculoSelecionado().getAttribute("Proprietario").toString());
        this.setVlTelefone(veiculoSelecionado().getAttribute("Telefone").toString());
    }

    public void gravarEdicao(ActionEvent actionEvent) throws Exception {
        try {
            DCIteratorBinding it =
                ADFUtils.findIterator("TbVeiculoView1Iterator");
            ViewObject view = it.getViewObject();
            view.reset();
            view.clearCache();
            view.setWhereClause("ID_VEICULO = " +
                                veiculoSelecionado().getAttribute("IdVeiculo"));
            view.executeQuery();

            Row row =
                ADFUtils.findIterator("TbVeiculoView1Iterator").getCurrentRow();

            row.setAttribute("Ano", getVlAno());
            row.setAttribute("Placa", getVlPlaca());
            row.setAttribute("Proprietario", getVlProprietario());
            row.setAttribute("Telefone", getVlTelefone());

            RowSetIterator iteratorModelos =
                ADFUtils.findIterator("ListarModeloView1Iterator").getRowSetIterator();
            Row rowMOdelo = iteratorModelos.getCurrentRow();

            row.setAttribute("FkModelo", rowMOdelo.getAttribute("IdModelo"));


            ADFUtils.executeBindingOperation("CommitTbVeiculo");
            refreshTable();
            GenericTableSelectionHandler.setFocusOnLine("VeiculoMMView1Iterator",
                                                        gridVeiculo,
                                                        "IdVeiculo",
                                                        row.getAttribute("IdVeiculo").toString(),
                                                        null);
            JSFUtils.addPartialTriggerWithIdFromUiRoot("t1");
            JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl1");
            popupEditar.cancel();
            //refreshComboMarca();
            JSFUtils.addFacesConfirmationMessage("Dados alterados com sucesso!");
        } catch (Exception e) {
            JSFUtils.addFacesErrorMessage("Não foi possível gravar!");
            throw e;
        }
    }
    
    public void cancelarEdicao(ActionEvent actionEvent) {
        popupEditar.cancel();
    }

    public void excluirVeiculo(DialogEvent dialogEvent) throws Exception {
        try {
            DCIteratorBinding it =
                ADFUtils.findIterator("TbVeiculoView1Iterator");
            ViewObject view = it.getViewObject();
            view.reset();
            view.clearCache();
            view.setWhereClause("ID_VEICULO = " +
                                veiculoSelecionado().getAttribute("IdVeiculo"));
            view.executeQuery();

            ADFUtils.executeBindingOperation("Delete");
            ADFUtils.executeBindingOperation("CommitTbVeiculo");
            refreshTable();
            JSFUtils.addFacesInformationMessage("Exclu\u00EDdo com sucesso!");
        } catch (Exception e) {
            JSFUtils.addFacesErrorMessage("Não foi possível excluir");
            throw e;
        }

    }

    public Row veiculoSelecionado() {
        Row rw = (Row)ADFUtils.evaluateEL(EL_EXP_VEICULO_CURR_ROW);
        return rw;
    }

    public void refreshTable() {
        DCIteratorBinding dcIter =
            ADFUtils.findIterator("VeiculoMMView1Iterator");
        dcIter.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getGridVeiculo());
        this.getGridVeiculo().processUpdates(FacesContext.getCurrentInstance());
        //Este comando atualiza o toolbar que contem os bot�es, (T2) � o id do toolbar
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t2");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl1");
    }

    public void refreshComboMarca() {
        DCIteratorBinding dcIter =
            ADFUtils.findIterator("ListarMarcaView1Iterator");
        dcIter.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getComboMarca());
        this.getComboMarca().processUpdates(FacesContext.getCurrentInstance());
        JSFUtils.addPartialTriggerWithIdFromUiRoot("soc2");
    }

    public void refreshComboModelo() {
        DCIteratorBinding dcIter =
            ADFUtils.findIterator("ListarModeloView1Iterator");
        dcIter.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getComboModelo());
        this.getComboModelo().processUpdates(FacesContext.getCurrentInstance());
    }

    public Boolean getHabilitaBotoes() {
        Row rw = (Row)ADFUtils.evaluateEL(EL_EXP_VEICULO_CURR_ROW);
        if (rw == null) {
            return false;
        }
        return true;
    }

    public void buscarModelos(ValueChangeEvent valueChangeEvent) throws Exception {
        try {

            UIComponent component = valueChangeEvent.getComponent();
            component.processUpdates(FacesContext.getCurrentInstance());

            RowSetIterator iteratorMarca =
                ADFUtils.findIterator("ListarMarcaView1Iterator").getRowSetIterator();
            Row linha = iteratorMarca.getCurrentRow();
            Number id = (Number)linha.getAttribute("IdMarca");
            this.setIdMarca(id);

            ADFUtils.executeBindingOperation("BuscarModelos");

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void atualizaComboAno() throws Exception {

        try {
            Format formato = new SimpleDateFormat("yyyy");

            Calendar dataPassada = Calendar.getInstance();
            Calendar dataAtual = Calendar.getInstance();

            dataPassada.add(Calendar.YEAR, -30);

            int passado =
                Integer.parseInt(formato.format(dataPassada.getTime()));
            int atual = Integer.parseInt(formato.format(dataAtual.getTime()));

            comboAno = new ArrayList<SelectItem>();

            for (int i = passado; i <= atual; i++) {
                passado += 1;
                comboAno.add(new SelectItem(String.valueOf(passado),
                                            String.valueOf(passado)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void validarPlaca(FacesContext facesContext,
                             UIComponent uIComponent, Object object) {
        String opcao = (String)object;
        DCIteratorBinding it = ADFUtils.findIterator("ExistePlaca1Iterator");
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setNamedWhereClauseParam("placa", opcao);
        view.executeQuery();

        RowIterator rsi =
            ADFUtils.findIterator("ExistePlaca1Iterator").getRowSetIterator();

        Row row = null;

        row = rsi.getCurrentRow();
        if (opcao.length() < 8) {
            FacesMessage msg =
                new FacesMessage("Placa inv\u00E1lida", "Informe uma placa v\u00E1lida");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);

        }

        if (row != null) {
            FacesMessage msg =
                new FacesMessage("Placa inv\u00E1lida", "Placa informada ja consta na base");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }


    public void setGridVeiculo(RichTable gridVeiculo) {
        this.gridVeiculo = gridVeiculo;
    }

    public RichTable getGridVeiculo() {
        return gridVeiculo;
    }

    public void setVlAno(String ano) {
        this.vlAno = ano;
    }

    public String getVlAno() {
        return vlAno;
    }

    public void setVlPlaca(String placa) {
        this.vlPlaca = placa;
    }

    public String getVlPlaca() {
        return vlPlaca;
    }

    public void setVlProprietario(String proprietario) {
        this.vlProprietario = proprietario;
    }

    public String getVlProprietario() {
        return vlProprietario;
    }

    public void setVlTelefone(String telefone) {
        this.vlTelefone = telefone;
    }

    public String getVlTelefone() {
        return vlTelefone;
    }

    public void setPopupInserir(RichPopup popupVeiculo) {
        this.popupInserir = popupVeiculo;
    }

    public RichPopup getPopupInserir() {
        return popupInserir;
    }

    public void setComboMarca(RichSelectOneChoice comboMarca) {
        this.comboMarca = comboMarca;
    }

    public RichSelectOneChoice getComboMarca() {
        return comboMarca;
    }

    public void setComboModelo(RichSelectOneChoice comboModelo) {
        this.comboModelo = comboModelo;
    }

    public RichSelectOneChoice getComboModelo() {
        return comboModelo;
    }


    public void setIdMarca(Number idMarca) {
        this.idMarca = idMarca;
    }

    public Number getIdMarca() {
        return idMarca;
    }
    
    public void setPopupEditar(RichPopup popupEditar) {
        this.popupEditar = popupEditar;
    }

    public RichPopup getPopupEditar() {
        return popupEditar;
    }


    public void setComboAno(List<SelectItem> comboAno) {
        this.comboAno = comboAno;
    }

    public List<SelectItem> getComboAno() throws Exception {
        if (comboAno == null) {
            atualizaComboAno();
        }
        return comboAno;
    }

    /*     private void atualizaComboMarca() {
            ViewObject view = ADFUtils.findIterator("ListarMarcaView1Iterator").getViewObject();
            view.reset();
            view.setRangeSize(-1);
            view.executeQuery();

            Row[] rows = view.getAllRowsInRange();

            comboMarca1 = new ArrayList<SelectItem>();

            if (rows != null) {
                for (Row rw : rows) {
                    comboMarca1.add(new SelectItem(rw.getAttribute("IdMarca").toString(),
                                                       (String)rw.getAttribute("Marca")));
                }
            }
        } */

    /*     public void setComboMarca1(List<SelectItem> comboMarca1) {
        this.comboMarca1 = comboMarca1;
    }

    public List<SelectItem> getComboMarca1() {
        if (comboMarca1 == null) {
            atualizaComboMarca();
        }
        return comboMarca1;
    } */

}
