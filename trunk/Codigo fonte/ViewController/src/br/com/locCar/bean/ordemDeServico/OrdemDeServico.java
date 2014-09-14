package br.com.locCar.bean.ordemDeServico;


import br.com.locCar.util.ADFUtils;
import br.com.locCar.util.GenericTableSelectionHandler;
import br.com.locCar.util.JSFUtils;
import br.com.locCar.util.ValidarUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;
import oracle.jbo.domain.Timestamp;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;


public class OrdemDeServico extends ValidarUtil {

    private static ADFLogger logger = ADFLogger.createADFLogger(OrdemDeServico.class);

    private final String IT_TB_OS = "TbOrdemDeServicoView1Iterator";
    private final String IT_OS = "DadosOsView1Iterator";
    private final String IT_TB_CLIENTE = "TbClienteView1Iterator";
    private final String IT_TB_VEICULO = "VeiculoMMView1Iterator";
    private final String IT_TB_MOTORISTA = "MotoristaCategoriaView1Iterator";

    private final String IT_DIARIA = "DadosDiariaView1Iterator";
    private final String IT_TB_DIARIA = "TbDiariaView1Iterator";
    private final String IT_TB_FRANQUIA_KM = "TbFranquiaKmView1Iterator";
    private final String IT_TB_FRANQUIA_HR = "TbFranquiaHrsView1Iterator";

    protected List<OsTO> cliente;
    protected List<OsTO> veiculo;
    protected List<OsTO> motorista;
    private Number idOs = null;
    private Integer idCliente;
    private Integer idMotorista;
    private Integer idVeiculo;
    private Number idHrFranquia;
    private Number idKmFranquia;

    private String nomeCliPesq;
    private String nomeMotPesq;
    private String modeloPesq;
    private String nomeUsuario;
    private String telUsuario;
    private String nomeMotorista;
    private String telefoneMotorista;
    private String modeloVeiculo;
    protected boolean statusVeiculo;
    protected boolean statusMotorista;
    protected boolean statusCliente;
    private RichTable bindGridClientes;
    private RichTable bindGridVeiculos;
    private RichTable bindGridMotoristas;
    private RowKeySet selectedKeys;
    private RichPopup popupInserir;
    private RichTable bindGridPrincipal;

    //variaveis para diaria

    private Timestamp dtHrSaida;
    private Timestamp dtHrChegada;
    private Number kmsaida;
    private Number kmChegada;
    private String totalHrsDia;
    private Number totalKmDia;
    private RichPopup popupInserirDiaria;
    private RichTable bindGridDiaria;
    private RichPopup popupEditarDiaria;
    private Number kmChegadaDiariaAnterior;
    private Timestamp dataChegadaDiariaAnterior;
    private Boolean botaoEditarDiaria;
    private Boolean botaoInserirDiaria;
    private Row linhaDiariaSelecionada;

    public OrdemDeServico() {
        iniciar();
    }

    public void iniciar() {
        Row rw = (Row)ADFUtils.evaluateEL("#{bindings.DadosOsView1.currentRow}");
        Number idMotorista = null;
        Number idModelo = null;
        if (rw != null) {
            idMotorista = (Number)rw.getAttribute("FkMotorista");
            idModelo = (Number)rw.getAttribute("FkModelo");
            idOs = (Number)rw.getAttribute("IdOs");
        } //end if
        if (idOs != null) {
            final Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("idOs", idOs);
            ADFUtils.executeOperationBinding("buscarDiaria", parametros);            
        } //end if        

        if (idMotorista != null) {
            indicaMotorista(idMotorista);
        } else {
            setNomeMotorista(null);
            setTelefoneMotorista(null);
        } //end if else
        if (idModelo != null) {
            indicaModelo(idModelo);
        } else {
            setModeloVeiculo(null);
        } //end if else
        
        habitarInserirDiaria();
        habilitarEdicaoDiaria();
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t11");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t2");
    } //end

    public void indicaMotorista(Number idMotorista) {
        DCIteratorBinding it = ADFUtils.findIterator(IT_TB_MOTORISTA);
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setWhereClause("ID_MOTORISTA = " + idMotorista);
        view.executeQuery();
        RowSetIterator iteratorMotorista = ADFUtils.findIterator(IT_TB_MOTORISTA).getRowSetIterator();
        Row row = iteratorMotorista.getCurrentRow();
        if (row != null) {
            String nome = (String)row.getAttribute("Nome");
            String telefone = (String)row.getAttribute("Telefone");
            setNomeMotorista(nome);
            setTelefoneMotorista(telefone);
        } //end if
    } //end

    public void indicaModelo(Number idModelo) {
        DCIteratorBinding it = ADFUtils.findIterator(IT_TB_VEICULO);
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setWhereClause("ID_MODELO = " + idModelo);
        view.executeQuery();
        RowSetIterator iteratorVeiculo =  ADFUtils.findIterator(IT_TB_VEICULO).getRowSetIterator();
        Row row = iteratorVeiculo.getCurrentRow();
        if (row != null) {
            String modelo = (String)row.getAttribute("Modelo");
            setModeloVeiculo(modelo);
        } //end if
    } //end

    public void indicaModeloIdVeiculo(Number idVeiculo) {
        DCIteratorBinding it = ADFUtils.findIterator(IT_TB_VEICULO);
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setWhereClause("ID_VEICULO = " + idVeiculo);
        view.executeQuery();
        RowSetIterator iteratorVeiculo = ADFUtils.findIterator(IT_TB_VEICULO).getRowSetIterator();
        Row row = iteratorVeiculo.getCurrentRow();
        if (row != null) {
            String modelo = (String)row.getAttribute("Modelo");
            setModeloVeiculo(modelo);
        } //end if
    } //end


    //Método executado antes de abrir o popup de inclusão de uma nova OS.

    public void chamadaPopupInclusao(PopupFetchEvent popupFetchEvent) {
        removeFocoGridCliente();
        removeFocoGridMotorista();
        removeFocoGridVeiculo();
        setStatusCliente(true);
        setStatusMotorista(true);
        setStatusVeiculo(true);
        setNomeUsuario("");
        setTelUsuario("");
        setModeloPesq("");
        setNomeCliPesq("");
        setNomeMotPesq("");
        setIdCliente(null);
        setIdMotorista(null);
        setIdVeiculo(null);
    } //end

    //Gravar os dados da nova OS.

    public void gravarOs(ActionEvent actionEvent) {
        RowSetIterator it = ADFUtils.findIterator(IT_TB_OS).getRowSetIterator();
        try {
            oracle.jbo.domain.Date dataAtual = new oracle.jbo.domain.Date(oracle.jbo.domain.Date.getCurrentDate());
            pegaIdCliente();
            pegaIdVeiculo();
            pegaIdMotorista();

            if (getIdCliente() == null || getIdVeiculo() == null) {
                JSFUtils.addFacesErrorMessage("Cliente ou Ve\u00EDculo nao informado");
            } else {

                Row criarRow = it.createRow();
                criarRow.setNewRowState(Row.STATUS_INITIALIZED);
                criarRow.setAttribute("FkVeiculo", this.getIdVeiculo());
                criarRow.setAttribute("FkCliente", this.getIdCliente());
                criarRow.setAttribute("FkMotorista", this.getIdMotorista());
                criarRow.setAttribute("DataDaOs", dataAtual);
                criarRow.setAttribute("UsuarioServ", getNomeUsuario());
                criarRow.setAttribute("UsuarioTel", getTelUsuario());

                ADFUtils.executeBindingOperation("CommitTbOs");

                refreshTable();
                GenericTableSelectionHandler.setFocusOnLine(IT_OS,
                                                            bindGridPrincipal,
                                                            "IdOs",
                                                            criarRow.getAttribute("IdOs").toString(),
                                                            null);

                final Map<String, Object> parametros = new HashMap<String, Object>();
                parametros.put("idOs", (Number)criarRow.getAttribute("IdOs"));
                ADFUtils.executeOperationBinding("buscarDiaria", parametros);
                habilitarEdicaoDiaria();
                habitarInserirDiaria();
                if (this.getIdMotorista() != null) {
                    indicaMotorista(new Number(this.getIdMotorista()));
                } else {
                    setNomeMotorista(null);
                    setTelefoneMotorista(null);
                }
                if (this.getIdVeiculo() != null) {
                    indicaModeloIdVeiculo(new Number(this.getIdVeiculo()));
                } else {
                    setModeloVeiculo(null);
                }
                JSFUtils.addPartialTriggerWithIdFromUiRoot("t11");
                JSFUtils.addPartialTriggerWithIdFromUiRoot("t12");
                JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl2");
                JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl3");
                JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl4");
                JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl5");

                popupInserir.cancel();
                JSFUtils.addFacesConfirmationMessage("Dados gravados com sucesso");
            } //end if else
        } catch (Exception e) {
            logger.severe("Erro ao gravar OS===>>>> " + e.getMessage());
        } //end try... catch
    } //end

    //Fecha o popup

    public void cancelarPopupInserir(ActionEvent actionEvent) {
        popupInserir.cancel();
    } //end

    //Realiza um refresh na grid principal.

    public void refreshTable() {
        DCIteratorBinding dcIter = ADFUtils.findIterator(IT_OS);
        dcIter.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getBindGridPrincipal());
        this.getBindGridPrincipal().processUpdates(FacesContext.getCurrentInstance());
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t1");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t2");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t12");
    } //end

    public void refreshComboKm() {
        DCIteratorBinding dcIter = ADFUtils.findIterator(IT_TB_FRANQUIA_KM);
        dcIter.executeQuery();
    } //end

    public void refreshRadioHrs() {
        DCIteratorBinding dcIter = ADFUtils.findIterator(IT_TB_FRANQUIA_HR);
        dcIter.executeQuery();
    } //end


    public void limparCliente(ActionEvent actionEvent) {
        removeFocoGridCliente();
        setStatusCliente(true);
        setIdCliente(null);
        setNomeCliPesq("");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ctb8");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it3");
        cliente.clear();
        JSFUtils.addPartialTrigger(bindGridClientes);
    } //end

    public void removeFocoGridCliente() {
        selectedKeys = bindGridClientes.getSelectedRowKeys();
        if (selectedKeys != null) {
            selectedKeys.clear();
        } //end if
    } //end

    public void limparVeiculo(ActionEvent actionEvent) {
        removeFocoGridVeiculo();
        selectedKeys.clear();
        setIdVeiculo(null);
        setStatusVeiculo(true);
        setModeloPesq("");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it4");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ctb9");
        veiculo.clear();
        JSFUtils.addPartialTrigger(bindGridVeiculos);
    } //end

    public void removeFocoGridVeiculo() {
        selectedKeys = bindGridVeiculos.getSelectedRowKeys();
        if (selectedKeys != null) {
            selectedKeys.clear();
        } //end if
    } //end

    public void limparMotorista(ActionEvent actionEvent) {
        removeFocoGridMotorista();
        setStatusMotorista(true);
        setIdMotorista(null);
        setNomeMotPesq("");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it5");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ctb10");
        motorista.clear();
        JSFUtils.addPartialTrigger(bindGridMotoristas);
    } //end

    public void removeFocoGridMotorista() {
        selectedKeys = bindGridMotoristas.getSelectedRowKeys();
        if (selectedKeys != null) {
            selectedKeys.clear();
        } //end if
    } //end

    //Método utilizado para recuperar o Id do cliente selecionado.

    public void pegaIdCliente() {
        if (bindGridClientes.getSelectedRowKeys() != null) {
            RowKeySet selectedRowKeySet =  bindGridClientes.getSelectedRowKeys();
            Iterator selectionIt = selectedRowKeySet.iterator();
            Integer rowKey;
            while (selectionIt.hasNext()) {
                rowKey = (Integer)selectionIt.next();
                OsTO os = cliente.get(rowKey.intValue());
                setIdCliente(os.getIdCli().intValue());
            } //end while
        } //end if
    } //end

    //Método utilizado para recuperar o Id do veículo selecionado.

    public void pegaIdVeiculo() {
        if (bindGridVeiculos.getSelectedRowKeys() != null) {
            RowKeySet selectedRowKeySet =  bindGridVeiculos.getSelectedRowKeys();
            Iterator selectionIt = selectedRowKeySet.iterator();
            Integer rowKey;
            while (selectionIt.hasNext()) {
                rowKey = (Integer)selectionIt.next();
                OsTO os = veiculo.get(rowKey.intValue());
                setIdVeiculo(os.getIdCar().intValue());
            } //end while
        } //end if
    } //end

    //Método utilizado para recuperar o Id do motorista selecionado.

    public void pegaIdMotorista() {
        if (bindGridMotoristas.getSelectedRowKeys() != null) {
            RowKeySet selectedRowKeySet = bindGridMotoristas.getSelectedRowKeys();
            Iterator selectionIt = selectedRowKeySet.iterator();
            Integer rowKey;
            while (selectionIt.hasNext()) {
                rowKey = (Integer)selectionIt.next();
                OsTO os = motorista.get(rowKey.intValue());
                setIdMotorista(os.getIdMot().intValue());
            } //end while
        } //end if
    } //end

    public void pesquisarCliente(ActionEvent actionEvent) {
        cliente.clear();
        DCIteratorBinding it = ADFUtils.findIterator(IT_TB_CLIENTE);
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setWhereClause("( (UPPER(NOME) LIKE UPPER('%' || '" + nomeCliPesq + "'|| '%') ) )");
        view.executeQuery();

        RowSetIterator iteratorFranquia = ADFUtils.findIterator(IT_TB_CLIENTE).getRowSetIterator();

        Row[] rows = iteratorFranquia.getAllRowsInRange();

        if (rows != null) {
            for (Row rw : rows) {
                Number id = (Number)rw.getAttribute("IdCliente");
                String nome = (String)rw.getAttribute("Nome");
                String doc = (String)rw.getAttribute("CpfCnpj");

                OsTO dadosCliente = new OsTO();
                dadosCliente.setIdCli(id.intValue());
                dadosCliente.setNomeCli(nome);
                dadosCliente.setDocCli(doc);
                cliente.add(dadosCliente);
            } //end for
        } //end if
        JSFUtils.addPartialTrigger(bindGridClientes);
        setNomeCliPesq("");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it3");
    } //end

    public void pesquisarMotorista(ActionEvent actionEvent) {
        motorista.clear();
        DCIteratorBinding it = ADFUtils.findIterator(IT_TB_MOTORISTA);
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setWhereClause("( (UPPER(NOME) LIKE UPPER('%' || '" + nomeMotPesq + "'|| '%') ) )");
        view.executeQuery();

        RowSetIterator iteratorFranquia = ADFUtils.findIterator(IT_TB_MOTORISTA).getRowSetIterator();

        Row[] rows = iteratorFranquia.getAllRowsInRange();

        if (rows != null) {
            for (Row rw : rows) {
                Number id = (Number)rw.getAttribute("IdMotorista");
                String nome = (String)rw.getAttribute("Nome");
                String categoria = (String)rw.getAttribute("Categoria");
                String telefone = (String)rw.getAttribute("Telefone");

                OsTO dadosMotorista = new OsTO();
                dadosMotorista.setIdMot(id.intValue());
                dadosMotorista.setNomeMot(nome);
                dadosMotorista.setCatHab(categoria);
                dadosMotorista.setTelefoneMot(telefone);
                motorista.add(dadosMotorista);
            } //end for
        } //end if
        JSFUtils.addPartialTrigger(bindGridMotoristas);
        setNomeMotPesq("");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it4");
    } //end

    public void pesquisarVeiculo(ActionEvent actionEvent) {
        veiculo.clear();
        DCIteratorBinding it = ADFUtils.findIterator(IT_TB_VEICULO);
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setWhereClause("( (UPPER(MODELO) LIKE UPPER('%' || '" +  modeloPesq + "'|| '%') ) )");
        view.executeQuery();

        RowSetIterator iteratorFranquia = ADFUtils.findIterator(IT_TB_VEICULO).getRowSetIterator();

        Row[] rows = iteratorFranquia.getAllRowsInRange();

        if (rows != null) {
            for (Row rw : rows) {
                Number id = (Number)rw.getAttribute("IdVeiculo");
                String modelo = (String)rw.getAttribute("Modelo");
                String placa = (String)rw.getAttribute("Placa");
                String ano = (String)rw.getAttribute("Ano");

                OsTO dadosVeiculo = new OsTO();
                dadosVeiculo.setIdCar(id.intValue());
                dadosVeiculo.setModeloCar(modelo);
                dadosVeiculo.setPlacaCar(placa);
                dadosVeiculo.setAnoCar(ano);
                veiculo.add(dadosVeiculo);
            } //end for
        } //end if
        JSFUtils.addPartialTrigger(bindGridVeiculos);
        setModeloPesq("");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it5");
    } //end

    public List<OsTO> getCliente() {
        if (cliente == null) {
            cliente = new ArrayList<OsTO>();
            try {
                DCIteratorBinding it = ADFUtils.findIterator(IT_TB_CLIENTE);
                ViewObject view = it.getViewObject();
                view.reset();
                view.clearCache();
                view.executeQuery();

                RowSetIterator iteratorFranquia = ADFUtils.findIterator(IT_TB_CLIENTE).getRowSetIterator();

                Row[] rows = iteratorFranquia.getAllRowsInRange();

                if (rows != null) {
                    for (Row rw : rows) {
                        Number id = (Number)rw.getAttribute("IdCliente");
                        String nome = (String)rw.getAttribute("Nome");
                        String doc = (String)rw.getAttribute("CpfCnpj");

                        OsTO dadosCliente = new OsTO();
                        dadosCliente.setIdCli(id.intValue());
                        dadosCliente.setNomeCli(nome);
                        dadosCliente.setDocCli(doc);
                        cliente.add(dadosCliente);
                    } //end for
                } //end if

            } catch (Exception e) {
                logger.severe(e.getMessage());
            } //end try... catch
        } //end if
        return cliente;
    } //end

    public List<OsTO> getVeiculo() {
        if (veiculo == null) {
            veiculo = new ArrayList<OsTO>();
            try {
                DCIteratorBinding it = ADFUtils.findIterator(IT_TB_VEICULO);
                ViewObject view = it.getViewObject();
                view.reset();
                view.clearCache();
                view.executeQuery();

                RowSetIterator iteratorFranquia = ADFUtils.findIterator(IT_TB_VEICULO).getRowSetIterator();

                Row[] rows = iteratorFranquia.getAllRowsInRange();

                if (rows != null) {
                    for (Row rw : rows) {
                        Number id = (Number)rw.getAttribute("IdVeiculo");
                        String modelo = (String)rw.getAttribute("Modelo");
                        String placa = (String)rw.getAttribute("Placa");
                        String ano = (String)rw.getAttribute("Ano");

                        OsTO dadosCliente = new OsTO();
                        dadosCliente.setIdCar(id.intValue());
                        dadosCliente.setModeloCar(modelo);
                        dadosCliente.setPlacaCar(placa);
                        dadosCliente.setAnoCar(ano);
                        veiculo.add(dadosCliente);
                    } //end for
                } //end if

            } catch (Exception e) {
                logger.severe(e.getMessage());
            } //end try... catch
        } //end if
        return veiculo;
    } //end

    public List<OsTO> getMotorista() {
        if (motorista == null) {
            motorista = new ArrayList<OsTO>();
            try {
                DCIteratorBinding it = ADFUtils.findIterator(IT_TB_MOTORISTA);
                ViewObject view = it.getViewObject();
                view.reset();
                view.clearCache();
                view.executeQuery();

                RowSetIterator iteratorFranquia = ADFUtils.findIterator(IT_TB_MOTORISTA).getRowSetIterator();

                Row[] rows = iteratorFranquia.getAllRowsInRange();

                if (rows != null) {
                    for (Row rw : rows) {
                        Number id = (Number)rw.getAttribute("IdMotorista");
                        String nome = (String)rw.getAttribute("Nome");
                        String categoria = (String)rw.getAttribute("Categoria");
                        String telefone = (String)rw.getAttribute("Telefone");

                        OsTO dadosCliente = new OsTO();
                        dadosCliente.setIdMot(id.intValue());
                        dadosCliente.setNomeMot(nome);
                        dadosCliente.setCatHab(categoria);
                        dadosCliente.setTelefoneMot(telefone);
                        motorista.add(dadosCliente);
                    } //end for
                } //end if

            } catch (Exception e) {
                logger.severe(e.getMessage());
            } //end try... catch
        } //end if
        return motorista;
    } //end


    public void excluirOrdemDeServico(DialogEvent dialogEvent) throws Exception {
        Row rw = (Row)ADFUtils.evaluateEL("#{bindings.DadosOsView1.currentRow}");

        DCIteratorBinding it = ADFUtils.findIterator(IT_TB_DIARIA);
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setWhereClause("FK_ORDEM_DE_SERVICO = " + rw.getAttribute("IdOs"));
        view.executeQuery();

        buscarOS((Number)rw.getAttribute("IdOs"));
        Row[] rows = it.getAllRowsInRange();
        if (rows != null) {
            for (Row row : rows) {
                row.remove();
            }
            ADFUtils.executeBindingOperation("CommitTbDiaria");
        } //end if

        ADFUtils.executeBindingOperation("DeleteTbOrdemDeServico");
        ADFUtils.executeBindingOperation("CommitTbOs");
        refreshTable();
        iniciar();
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t11");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl2");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl3");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl4");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t12");
        JSFUtils.addFacesInformationMessage("Exclu\u00EDdo com sucesso!");
    } //end

    public Boolean getHabilitaBotoes() {
        Row rw =
            (Row)ADFUtils.evaluateEL("#{bindings.DadosOsView1.currentRow}");
        if (rw == null) {
            return false;
        } //end if
        return true;
    } //end

    private void habitarInserirDiaria() {
        Row rw = (Row)ADFUtils.evaluateEL("#{bindings.DadosOsView1.currentRow}");
        RowSetIterator itDiaria = ADFUtils.findIterator(IT_DIARIA).getRowSetIterator();
        Row[] rows = itDiaria.getAllRowsInRange();
        Row ultimaLinha = itDiaria.last();

        if (rw != null && (rows == null || rows.length == 0)) {
            setBotaoInserirDiaria(false);
        } else if (rw != null && ultimaLinha != null &&
                   ultimaLinha.getAttribute("HrChegada") != null &&
                   ultimaLinha.getAttribute("KmChegada") != null) {
            setBotaoInserirDiaria(false);
        } else {
            setBotaoInserirDiaria(true);
        } //end if else
    }

    //Método utilizado para atualizar a grid de retorno e buscar as diárias referente a OS selecionada.

    public void selectionListenerGridPrincipal(SelectionEvent selectionEvent) {
        makeCurrent(selectionEvent, "#{bindings.DadosOsView1.collectionModel.makeCurrent}");
        iniciar();
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ps2");
    } //end


    public void selectionListenerCliente(SelectionEvent selectionEvent) {
        if (bindGridClientes.getSelectedRowKeys() != null) {
            setStatusCliente(false);
        } //end if
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ctb8");
    } //end

    public void selectionListenerVeiculo(SelectionEvent selectionEvent) {
        if (bindGridVeiculos.getSelectedRowKeys() != null) {
            setStatusVeiculo(false);
        } //end if
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ctb9");
    } //end

    public void selectionListenerMotorista(SelectionEvent selectionEvent) {
        if (bindGridMotoristas.getSelectedRowKeys() != null) {
            setStatusMotorista(false);
        } //end if
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ctb10");
    } //end

    public void selectionListenerGridDiarias(SelectionEvent selectionEvent) {
        makeCurrent(selectionEvent, "#{bindings.DadosDiariaView1.collectionModel.makeCurrent}");
        habilitarEdicaoDiaria();
        habitarInserirDiaria();
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t12");
    } //end

    private void habilitarEdicaoDiaria() {
        Row linhaCorrente = (Row)ADFUtils.evaluateEL("#{bindings.DadosDiariaView1.currentRow}");
        Row ultimaLinha =
            ADFUtils.findIterator(IT_DIARIA).getViewObject().last();

        Number idCorrente = null;
        Number idUltimo = null;

        if (linhaCorrente != null && ultimaLinha != null) {
            idCorrente = (Number)linhaCorrente.getAttribute("IdDiaria");
            idUltimo = (Number)ultimaLinha.getAttribute("IdDiaria");
            if (idCorrente.compareTo(idUltimo) == 0) {
                setBotaoEditarDiaria(false);
            } else {
                setBotaoEditarDiaria(true);
            }
        } else {
            setBotaoEditarDiaria(true);
        }
    }


    public void makeCurrent(SelectionEvent selectionEvent,
                            String adfSelectionListener) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        Application application = fctx.getApplication();
        ELContext elCtx = fctx.getELContext();
        ExpressionFactory exprFactory = application.getExpressionFactory();
        MethodExpression me =
            exprFactory.createMethodExpression(elCtx, adfSelectionListener,
                                               Object.class,
                                               new Class[] { SelectionEvent.class });
        me.invoke(elCtx, new Object[] { selectionEvent });
    } //end


    public void chamadaPopupInserirDiaria(PopupFetchEvent popupFetchEvent) {
        setDtHrChegada(null);
        setDtHrSaida(null);
        setKmChegada(null);
        setKmsaida(null);
        setTotalHrsDia("");
        setTotalKmDia(null);
        refreshComboKm();
        refreshRadioHrs();
        Row lastRow = ADFUtils.findIterator(IT_DIARIA).getViewObject().last();
          
        if(lastRow != null){
            setDataChegadaDiariaAnterior((Timestamp)lastRow.getAttribute("HrChegada"));
            setKmChegadaDiariaAnterior((Number)lastRow.getAttribute("KmChegada"));        
         }else {
            setDataChegadaDiariaAnterior(null);
            setKmChegadaDiariaAnterior(null);
        }
    } //end

    public void gravarDiaria(ActionEvent actionEvent) {
        RowSetIterator iteratorDiaria = ADFUtils.findIterator(IT_TB_DIARIA).getRowSetIterator();
        RowSetIterator iteratorHrs = ADFUtils.findIterator(IT_TB_FRANQUIA_HR).getRowSetIterator();
        RowSetIterator iteratorKm = ADFUtils.findIterator(IT_TB_FRANQUIA_KM).getRowSetIterator();

        Row rowHoras = iteratorHrs.getCurrentRow();
        Row rowKm = iteratorKm.getCurrentRow();

        Row rw = (Row)ADFUtils.evaluateEL("#{bindings.DadosOsView1.currentRow}");
        idOs = (Number)rw.getAttribute("IdOs");

        try {
            Row criarRow = iteratorDiaria.createRow();
            criarRow.setNewRowState(Row.STATUS_INITIALIZED);
            criarRow.setAttribute("HrChegada", this.getDtHrChegada());
            criarRow.setAttribute("HrSaida", this.getDtHrSaida());
            criarRow.setAttribute("FkOrdemDeServico", this.getIdOs());
            criarRow.setAttribute("KmChegada", this.getKmChegada());
            criarRow.setAttribute("KmSaida", this.getKmsaida());

            String horas = (String)rowHoras.getAttribute("HrFranquia");
            addFranquiaHras(horas);
            String km = (String)rowKm.getAttribute("Km");
            addFranquiaKm(km);

            if (this.getTotalHrsDia() != null) {
                criarRow.setAttribute("TotalHrExtDia", this.getTotalHrsDia());
            }
            if (this.getTotalKmDia() != null) {
                criarRow.setAttribute("TotalKmRodado", this.getTotalKmDia());
            }
            criarRow.setAttribute("FkFranquiaHrs",
                                  rowHoras.getAttribute("IdFranquia"));
            criarRow.setAttribute("FkFranquiaKm",
                                  rowKm.getAttribute("IdFranquiaKm"));

            ADFUtils.executeBindingOperation("CommitTbDiaria");

            Row[] rowsDiarias = buscarDiarias(idOs);

            List<String> listaDeHoras = new ArrayList<String>();
            List<Number> listaDeKm = new ArrayList<Number>();

            if (rowsDiarias != null) {
                for (Row row : rowsDiarias) {
                    if (row.getAttribute("TotalHrExtDia") != null) {
                        listaDeHoras.add((String)row.getAttribute("TotalHrExtDia"));
                    } //end if
                    if (row.getAttribute("TotalKmRodado") != null) {
                        listaDeKm.add((Number)row.getAttribute("TotalKmRodado"));
                    } //end if
                } //end for
            } //end if

            Row rowOs = buscarOS(idOs);

            if (rowOs != null) {
                String totalHrs = somarHoras(listaDeHoras);
                Number totalKm = somarTotalKm(listaDeKm);
                rowOs.setAttribute("TotalHrsExtra", totalHrs);
                rowOs.setAttribute("TotalKmExtra", totalKm);
                ADFUtils.executeBindingOperation("CommitTbOs");
            } //end if

            popupInserirDiaria.cancel();
            habitarInserirDiaria();
            habilitarEdicaoDiaria();
            refreshTableDiaria();
            GenericTableSelectionHandler.setFocusOnLine(IT_DIARIA,
                                                        bindGridDiaria,
                                                        "IdDiaria",
                                                        criarRow.getAttribute("IdDiaria").toString(),
                                                        null);
            JSFUtils.addFacesConfirmationMessage("Dados gravados com sucesso");
            JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl5");
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } //end try... catch
    } //end


    public void gravarEdicaoDiaria(ActionEvent actionEvent) throws Exception {
        DCIteratorBinding it = ADFUtils.findIterator(IT_TB_DIARIA);
        ViewObject view = it.getViewObject();
        view.reset();
        view.clearCache();
        view.setWhereClause("ID_DIARIA = " + linhaDiariaSelecionada.getAttribute("IdDiaria"));
        view.executeQuery();

        RowSetIterator iteratorHrs = ADFUtils.findIterator(IT_TB_FRANQUIA_HR).getRowSetIterator();
        RowSetIterator iteratorKm = ADFUtils.findIterator(IT_TB_FRANQUIA_KM).getRowSetIterator();
        Row linhaCorrente = ADFUtils.findIterator(IT_TB_DIARIA).getCurrentRow();

        Row rowHoras = iteratorHrs.getCurrentRow();
        Row rowKm = iteratorKm.getCurrentRow();


        linhaCorrente.setAttribute("HrChegada", getDtHrChegada());
        linhaCorrente.setAttribute("HrSaida", getDtHrSaida());
        linhaCorrente.setAttribute("KmChegada", getKmChegada());
        linhaCorrente.setAttribute("KmSaida", getKmsaida());
        linhaCorrente.setAttribute("FkFranquiaHrs", rowHoras.getAttribute("IdFranquia"));
        linhaCorrente.setAttribute("FkFranquiaKm", rowKm.getAttribute("IdFranquiaKm"));

        String horas = (String)rowHoras.getAttribute("HrFranquia");
        addFranquiaHras(horas);
        String km = (String)rowKm.getAttribute("Km");
        addFranquiaKm(km);

        linhaCorrente.setAttribute("TotalHrExtDia", this.getTotalHrsDia());
        linhaCorrente.setAttribute("TotalKmRodado", this.getTotalKmDia());

        ADFUtils.executeBindingOperation("CommitTbDiaria");

        Row[] rowsDiarias = buscarDiarias(idOs);

        List<String> listaDeHoras = new ArrayList<String>();
        List<Number> listaDeKm = new ArrayList<Number>();

        if (rowsDiarias != null) {
            for (Row row : rowsDiarias) {
                if (row.getAttribute("TotalHrExtDia") != null) {
                    listaDeHoras.add((String)row.getAttribute("TotalHrExtDia"));
                } //end if
                if (row.getAttribute("TotalKmRodado") != null) {
                    listaDeKm.add((Number)row.getAttribute("TotalKmRodado"));
                } //end if
            } //end for
        } //end if

        Row rowOs = buscarOS(idOs);

        if (rowOs != null) {
            String totalHrs = somarHoras(listaDeHoras);
            Number totalKm = somarTotalKm(listaDeKm);
            rowOs.setAttribute("TotalHrsExtra", totalHrs);
            rowOs.setAttribute("TotalKmExtra", totalKm);
            ADFUtils.executeBindingOperation("CommitTbOs");
        } //end if

        popupEditarDiaria.cancel();
        habitarInserirDiaria();
        habilitarEdicaoDiaria();
        refreshTableDiaria();
        GenericTableSelectionHandler.setFocusOnLine(IT_DIARIA, bindGridDiaria,
                                                    "IdDiaria",
                                                    linhaCorrente.getAttribute("IdDiaria").toString(),
                                                    null);
        JSFUtils.addFacesConfirmationMessage("Dados gravados com sucesso");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("pfl5");
    }

    public void chamadaPopupEditarDiaria(PopupFetchEvent popupFetchEvent) {
        linhaDiariaSelecionada = (Row)ADFUtils.evaluateEL("#{bindings.DadosDiariaView1.currentRow}");

        RowSetIterator iteratorHoras = ADFUtils.findIterator(IT_TB_FRANQUIA_HR).getRowSetIterator();
        Row[] rowsHrs = iteratorHoras.getAllRowsInRange();
        for (Row row : rowsHrs) {
            if (linhaDiariaSelecionada.getAttribute("FkFranquiaHrs").equals(row.getAttribute("IdFranquia"))) {
                iteratorHoras.setCurrentRow(row);
            } //end if
        } //end for

        RowSetIterator iteratorKm = ADFUtils.findIterator(IT_TB_FRANQUIA_KM).getRowSetIterator();
        Row[] rowsKm = iteratorKm.getAllRowsInRange();
        for (Row row : rowsKm) {
            if (linhaDiariaSelecionada.getAttribute("FkFranquiaKm").equals(row.getAttribute("IdFranquiaKm"))) {
                iteratorKm.setCurrentRow(row);
            } //end if
        } //end for
        setDtHrChegada((Timestamp)linhaDiariaSelecionada.getAttribute("HrChegada"));
        setDtHrSaida((Timestamp)linhaDiariaSelecionada.getAttribute("HrSaida"));
        setKmChegada((Number)linhaDiariaSelecionada.getAttribute("KmChegada"));
        setKmsaida((Number)linhaDiariaSelecionada.getAttribute("KmSaida"));

        setaVariaveisParaValidacao();

    } //end

    public void setaVariaveisParaValidacao() {       
        Row linhaAnterior = ADFUtils.findIterator(IT_DIARIA).getViewObject().previous();
        Row lastRow = ADFUtils.findIterator(IT_DIARIA).getViewObject().last();
        Row linhaCorrente = ADFUtils.findIterator(IT_DIARIA).getCurrentRow();
        Boolean op = false;
        
        if(linhaAnterior == null && lastRow != null){
            op = true;    
        }       
        if (linhaAnterior != null && linhaCorrente != null) {
            Number idCorrente = (Number)linhaCorrente.getAttribute("IdDiaria");
            Number idAnterior = (Number)linhaAnterior.getAttribute("IdDiaria");
            if(!(idCorrente.compareTo(idAnterior)==0)){            
                setDataChegadaDiariaAnterior((Timestamp)linhaAnterior.getAttribute("HrChegada"));
                setKmChegadaDiariaAnterior((Number)linhaAnterior.getAttribute("KmChegada"));
            }
        } else if(op){
            setDataChegadaDiariaAnterior((Timestamp)lastRow.getAttribute("HrChegada"));
            setKmChegadaDiariaAnterior((Number)lastRow.getAttribute("KmChegada"));        
         } else {
            setDataChegadaDiariaAnterior(null);
            setKmChegadaDiariaAnterior(null);
        }
    }

    public Row buscarOS(Number idOs) throws Exception {
        try {
            DCIteratorBinding iterator = ADFUtils.findIterator(IT_TB_OS);

            ViewObject vo = iterator.getViewObject();
            vo.reset();
            vo.clearCache();
            vo.setWhereClause("ID_OS=" + idOs);
            vo.executeQuery();
            iterator.executeQuery();

            RowSetIterator itOs = ADFUtils.findIterator(IT_TB_OS).getRowSetIterator();

            return itOs.getCurrentRow();
        } catch (Exception e) {
            logger.severe(e);
            throw e;
        } //end try... catch
    } //end

    public Row[] buscarDiarias(Number idOs) throws Exception {
        try {
            final Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("idOs", idOs);
            ADFUtils.executeOperationBinding("buscarDiaria", parametros);

            RowSetIterator itDiaria =
                ADFUtils.findIterator(IT_DIARIA).getRowSetIterator();
            return itDiaria.getAllRowsInRange();
        } catch (Exception e) {
            logger.severe("Erro ao buscar as diarias = " + e.getMessage());
            throw e;
        } //end try... catch
    } //end


    public void refreshTableDiaria() {
        DCIteratorBinding dcIter = ADFUtils.findIterator(IT_DIARIA);
        dcIter.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getBindGridDiaria());
        this.getBindGridDiaria().processUpdates(FacesContext.getCurrentInstance());
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t12");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t12");
    } //end


    public void cancelPopupInserirDiaria(ActionEvent actionEvent) {
        popupInserirDiaria.cancel();
    } //end

    public void cancelPopupEditarDiaria(ActionEvent actionEvent) {
        popupEditarDiaria.cancel();
    } //end

    public void validarHoraSaida(FacesContext facesContext,
                                 UIComponent uIComponent, Object object) {

        Timestamp dataSaida = (Timestamp)object;
        Timestamp dataChegada = getDtHrChegada();

        System.out.println("Data chegada anterior: " +
                           getDataChegadaDiariaAnterior());
        System.out.println("Data chegada: " + dataChegada);
        System.out.println("Data saida: " + dataSaida);

        if (getDataChegadaDiariaAnterior() != null &&
            getDataChegadaDiariaAnterior().compareTo(dataSaida) > 0) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data inv\u00E1lida",
                                 "Data de Sa\u00EDda n\u00E3o pode ser menor que a Data de Chegada da di\u00E1ria anterior");
            throw new ValidatorException(msg);
        } else if (dataChegada != null &&
                   dataSaida.compareTo(dataChegada) > 0) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data inv\u00E1lida",
                                 "Data de Sa\u00EDda n\u00E3o pode ser maior que a Data de Chegada");
            throw new ValidatorException(msg);
        } //end if else
    } //end

    public void validarHoraChegada(FacesContext facesContext,
                                   UIComponent uIComponent, Object object) {
        Timestamp dataChegada = (Timestamp)object;
        Timestamp dataSaida = getDtHrSaida();

        if (dataSaida != null && dataChegada.compareTo(dataSaida) < 0) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data inv\u00E1lida",
                                 "Data de Chegada n\u00E3o pode ser menor que a Data de Sa\u00EDda");
            throw new ValidatorException(msg);
        } //end if else
    } //end

    public void validarKmSaida(FacesContext facesContext,
                               UIComponent uIComponent, Object object) {

        Integer saida = Integer.parseInt(object.toString()); //Recebe o valor digitado


        System.out.println("Quilometragem chegada anterior: " + getKmChegadaDiariaAnterior());
        System.out.println("Quilometragem chegada: " + getKmChegada());
        System.out.println("Quilometragem saida: " + saida);

        if (getKmChegadaDiariaAnterior() != null &&
            getKmChegadaDiariaAnterior().intValue() > saida) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Quilometragem inv\u00E1lida",
                                 "Quilometragem de Sa\u00EDda n\u00E3o pode ser menor que a Quilometragem de Chegada da di\u00E1ria anterior");
            throw new ValidatorException(msg);
        } else if (getKmChegada() != null &&
                   getKmChegada().intValue() < saida) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Quilometragem inv\u00E1lida",
                                 "A Quilometragem de sa\u00EDda n\u00E3o pode ser maior que a Quilometragem de Chegada");
            throw new ValidatorException(msg);
        } //end if
    } //end

    public void validarKmChegada(FacesContext facesContext,
                                 UIComponent uIComponent, Object object) {
        Integer kmDeChegada = Integer.parseInt(object.toString()); //Recebe o valor digitado

        if (getKmsaida() != null && getKmsaida().intValue() > kmDeChegada) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Quilometragem invalida",
                                 "A Quilometragem de chegada n\u00E3o pode ser menor que a Quilometragem de Sa\u00EDda");
            throw new ValidatorException(msg);
        } //end if
    } //end

    private void addFranquiaHras(String horas) {
        horas = horas.replaceAll("[^0-9]", "");

        if (horas.length() > 0) {
            Integer valor = Integer.parseInt(horas);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(getDtHrSaida().getTime());
            cal.add(Calendar.HOUR, valor);
            Timestamp diferenca = new Timestamp(cal.getTime().getTime());

            if (getDtHrChegada() != null &&
                getDtHrChegada().compareTo(diferenca) == 1) {
                String result = subtrairHoras(diferenca, getDtHrChegada());
                setTotalHrsDia(result);
            } else {
                setTotalHrsDia(null); 
            }//end if
        } else {
            setTotalHrsDia(null);
        }
    } //end

    private void addFranquiaKm(String km) {
        km = km.replaceAll("[^0-9]", "");
        if (km.length() > 0) {
            Integer valor = Integer.parseInt(km);
            Integer diferenca = getKmsaida().intValue() + valor;
            if (getKmChegada() != null &&
                getKmChegada().intValue() > diferenca) {
                Number result =
                    new Number(getKmChegada().intValue() - diferenca);
                setTotalKmDia(result);
            } else {
                setTotalKmDia(null);
            }
        } else {
            setTotalKmDia(null);
        }//end if
    } //end

    public String subtrairHoras(Timestamp inicio, Timestamp fim) {
        //Calcula o tempo transcorrido em milissegundos, entre dois horários
        long tempo = fim.getTime() - inicio.getTime();

        tempo = tempo / 60000; //Converte o tempo para minutos
        int minutos = (int)(tempo % 60); //Retira os minutos da hora
        tempo = tempo / 60; //Deixa em tempo apenas as horas

        String total = String.format("%02d:%02d", tempo, minutos);
        if (total.equals("00:00")) {
            return null;
        } //end if
        return String.format("%02d:%02d", tempo, minutos);
    } //end

    private String somarHoras(List<String> lista) {
        int horaTotal = 00;
        int minutoTotal = 00;
        for (String s : lista) {
            int i = s.indexOf(":");
            int hora = Integer.parseInt(s.substring(0, i));
            horaTotal += hora;
            int min = Integer.parseInt(s.replaceAll(":", "").substring(i));
            minutoTotal += min;
        } //end for
        if (minutoTotal > 60) {
            int h = minutoTotal / 60;
            int m = minutoTotal % 60;
            horaTotal += h;
            minutoTotal = m;
        } //end if
        String total = horaTotal + ":" + minutoTotal;
        if (total.equals("00:00")) {
            return null;
        } //end if
        int i = total.indexOf(":");
        if (total.replaceAll(":", "").substring(i).length() < 2) {
            return horaTotal + ":0" + minutoTotal;
        } //end if
        return total;
    } //end

    private Number somarTotalKm(List<Number> lista) {
        int total = 0;
        for (Number t : lista) {
            total += t.intValue();
        } //end for
        if (total > 0) {
            return new Number(total);
        } //end if
        return null;
    } //end

    public void setIdKmFranquia(Number idKmFranquia) {
        this.idKmFranquia = idKmFranquia;
    }

    public Number getIdKmFranquia() {
        return idKmFranquia;
    }

    public void setIdHrFranquia(Number idHrFranquia) {
        this.idHrFranquia = idHrFranquia;
    }

    public Number getIdHrFranquia() {
        return idHrFranquia;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setTelUsuario(String telUsuario) {
        this.telUsuario = telUsuario;
    }

    public String getTelUsuario() {
        return telUsuario;
    }


    public void setBindGridClientes(RichTable bindGridClientes) {
        this.bindGridClientes = bindGridClientes;
    }

    public RichTable getBindGridClientes() {
        return bindGridClientes;
    }

    public void setBindGridVeiculos(RichTable bindGridVeiculos) {
        this.bindGridVeiculos = bindGridVeiculos;
    }

    public RichTable getBindGridVeiculos() {
        return bindGridVeiculos;
    }

    public void setBindGridMotoristas(RichTable bindGridMotoristas) {
        this.bindGridMotoristas = bindGridMotoristas;
    }

    public RichTable getBindGridMotoristas() {
        return bindGridMotoristas;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdMotorista(Integer idMotorista) {
        this.idMotorista = idMotorista;
    }

    public Integer getIdMotorista() {
        return idMotorista;
    }

    public void setIdVeiculo(Integer idVeiculo) {
        this.idVeiculo = idVeiculo;
    }

    public Integer getIdVeiculo() {
        return idVeiculo;
    }

    public void setNomeCliPesq(String nomeCliPesq) {
        this.nomeCliPesq = nomeCliPesq;
    }

    public String getNomeCliPesq() {
        return nomeCliPesq;
    }

    public void setNomeMotPesq(String nomeMotPesq) {
        this.nomeMotPesq = nomeMotPesq;
    }

    public String getNomeMotPesq() {
        return nomeMotPesq;
    }

    public void setModeloPesq(String modeloPesq) {
        this.modeloPesq = modeloPesq;
    }

    public String getModeloPesq() {
        return modeloPesq;
    }

    public void setStatusVeiculo(boolean statusVeiculo) {
        this.statusVeiculo = statusVeiculo;
    }

    public boolean isStatusVeiculo() {
        return statusVeiculo;
    }

    public void setStatusMotorista(boolean statusMotorista) {
        this.statusMotorista = statusMotorista;
    }

    public boolean isStatusMotorista() {
        return statusMotorista;
    }

    public void setStatusCliente(boolean statusCliente) {
        this.statusCliente = statusCliente;
    }

    public boolean isStatusCliente() {
        return statusCliente;
    }

    public void setSelectedKeys(RowKeySet selectedKeys) {
        this.selectedKeys = selectedKeys;
    }

    public RowKeySet getSelectedKeys() {
        return selectedKeys;
    }

    public void setPopupInserir(RichPopup popupInserir) {
        this.popupInserir = popupInserir;
    }

    public RichPopup getPopupInserir() {
        return popupInserir;
    }

    public void setBindGridPrincipal(RichTable bindGridPrincipal) {
        this.bindGridPrincipal = bindGridPrincipal;
    }

    public RichTable getBindGridPrincipal() {
        return bindGridPrincipal;
    }

    public void setNomeMotorista(String nomeMotorista) {
        this.nomeMotorista = nomeMotorista;
    }

    public String getNomeMotorista() {
        return nomeMotorista;
    }

    public void setTelefoneMotorista(String telefoneMotorista) {
        this.telefoneMotorista = telefoneMotorista;
    }

    public String getTelefoneMotorista() {
        return telefoneMotorista;
    }

    public void setModeloVeiculo(String modeloVeiculo) {
        this.modeloVeiculo = modeloVeiculo;
    }

    public String getModeloVeiculo() {
        return modeloVeiculo;
    }

    public void setDtHrSaida(Timestamp dtHrSaida) {
        this.dtHrSaida = dtHrSaida;
    }

    public Timestamp getDtHrSaida() {
        return dtHrSaida;
    }

    public void setDtHrChegada(Timestamp dtHrChegada) {
        this.dtHrChegada = dtHrChegada;
    }

    public Timestamp getDtHrChegada() {
        return dtHrChegada;
    }

    public void setKmsaida(Number kmsaida) {
        this.kmsaida = kmsaida;
    }

    public Number getKmsaida() {
        return kmsaida;
    }

    public void setKmChegada(Number kmChegada) {
        this.kmChegada = kmChegada;
    }

    public Number getKmChegada() {
        return kmChegada;
    }

    public void setTotalHrsDia(String totalHrExtraDia) {
        this.totalHrsDia = totalHrExtraDia;
    }

    public String getTotalHrsDia() {
        return totalHrsDia;
    }

    public void setTotalKmDia(Number totalKmDia) {
        this.totalKmDia = totalKmDia;
    }

    public Number getTotalKmDia() {
        return totalKmDia;
    }

    public void setIdOs(Number idOs) {
        this.idOs = idOs;
    }

    public Number getIdOs() {
        return idOs;
    }

    public void setPopupInserirDiaria(RichPopup popupInserirDiaria) {
        this.popupInserirDiaria = popupInserirDiaria;
    }

    public RichPopup getPopupInserirDiaria() {
        return popupInserirDiaria;
    }

    public void setBindGridDiaria(RichTable bindGridDiaria) {
        this.bindGridDiaria = bindGridDiaria;
    }

    public RichTable getBindGridDiaria() {
        return bindGridDiaria;
    }

    public void setPopupEditarDiaria(RichPopup popupEditarDiaria) {
        this.popupEditarDiaria = popupEditarDiaria;
    }

    public RichPopup getPopupEditarDiaria() {
        return popupEditarDiaria;
    }

    public void setKmChegadaDiariaAnterior(Number kmChegadaDiariaAnterior) {
        this.kmChegadaDiariaAnterior = kmChegadaDiariaAnterior;
    }

    public Number getKmChegadaDiariaAnterior() {
        return kmChegadaDiariaAnterior;
    }

    public void setDataChegadaDiariaAnterior(Timestamp dataChegadaDiariaAnterior) {
        this.dataChegadaDiariaAnterior = dataChegadaDiariaAnterior;
    }

    public Timestamp getDataChegadaDiariaAnterior() {
        return dataChegadaDiariaAnterior;
    }

    public void setBotaoEditarDiaria(Boolean botaoEditarDiaria) {
        this.botaoEditarDiaria = botaoEditarDiaria;
    }

    public Boolean getBotaoEditarDiaria() {
        return botaoEditarDiaria;
    }

    public void setBotaoInserirDiaria(Boolean botaoInserirDiaria) {
        this.botaoInserirDiaria = botaoInserirDiaria;
    }

    public Boolean getBotaoInserirDiaria() {
        return botaoInserirDiaria;
    }
}

