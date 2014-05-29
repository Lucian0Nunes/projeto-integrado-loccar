package br.com.locCar.bean.ordemDeServico;

import br.com.locCar.util.ADFUtils;

import br.com.locCar.util.GenericTableSelectionHandler;
import br.com.locCar.util.JSFUtils;

import br.com.locCar.util.ValidaCampos;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Date;
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

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import javax.faces.validator.ValidatorException;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;


import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.adf.view.rich.event.QueryEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewObject;

import oracle.jbo.domain.Number;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;

public class OrdemDeServico extends ValidaCampos {

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

    private List<SelectItem> listaKmFranquia;
    private Number idHrFranquia;
    private Number idKmFranquia;
   // private String hrFranquia;
   // private String kmFranquia;

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


    public OrdemDeServico() {
    }

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
    }

    //Gravar os dados da nova OS.

    public void gravarOs(ActionEvent actionEvent) {
        RowSetIterator it =
            ADFUtils.findIterator(IT_TB_OS).getRowSetIterator();
        try {
            oracle.jbo.domain.Date dataAtual =
                new oracle.jbo.domain.Date(oracle.jbo.domain.Date.getCurrentDate());
            pegaIdCliente();
            pegaIdVeiculo();
            pegaIdMotorista();

            if (getIdCliente() == null || getIdVeiculo() == null) {
                JSFUtils.addFacesErrorMessage("Cliente ou Veiculo nao informado");
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
                popupInserir.cancel();
                JSFUtils.addFacesConfirmationMessage("Dados gravados com sucesso");
            } //end if else

        } catch (Exception e) {
            e.printStackTrace();
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
            RowKeySet selectedRowKeySet =
                bindGridClientes.getSelectedRowKeys();
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
            RowKeySet selectedRowKeySet =
                bindGridVeiculos.getSelectedRowKeys();
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
            RowKeySet selectedRowKeySet =
                bindGridMotoristas.getSelectedRowKeys();
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
        view.setWhereClause("( (UPPER(NOME) LIKE UPPER('%' || '" +
                            nomeCliPesq + "'|| '%') ) )");
        view.executeQuery();

        RowSetIterator iteratorFranquia =
            ADFUtils.findIterator(IT_TB_CLIENTE).getRowSetIterator();

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
        view.setWhereClause("( (UPPER(NOME) LIKE UPPER('%' || '" +
                            nomeMotPesq + "'|| '%') ) )");
        view.executeQuery();

        RowSetIterator iteratorFranquia =
            ADFUtils.findIterator(IT_TB_MOTORISTA).getRowSetIterator();

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
        view.setWhereClause("( (UPPER(MODELO) LIKE UPPER('%' || '" +
                            modeloPesq + "'|| '%') ) )");
        view.executeQuery();

        RowSetIterator iteratorFranquia =
            ADFUtils.findIterator(IT_TB_VEICULO).getRowSetIterator();

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

                RowSetIterator iteratorFranquia =
                    ADFUtils.findIterator(IT_TB_CLIENTE).getRowSetIterator();

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
                e.printStackTrace();
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

                RowSetIterator iteratorFranquia =
                    ADFUtils.findIterator(IT_TB_VEICULO).getRowSetIterator();

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
                e.printStackTrace();
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

                RowSetIterator iteratorFranquia =
                    ADFUtils.findIterator(IT_TB_MOTORISTA).getRowSetIterator();

                Row[] rows = iteratorFranquia.getAllRowsInRange();

                if (rows != null) {
                    for (Row rw : rows) {
                        Number id = (Number)rw.getAttribute("IdMotorista");
                        String nome = (String)rw.getAttribute("Nome");
                        String categoria =
                            (String)rw.getAttribute("Categoria");
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
                e.printStackTrace();
            }
        } //end if
        return motorista;
    } //end

    //Método utilizado para atualizar a grid de retorno e buscar as diárias referente a OS selecionada.

    public void selectionListenerGridPrincipal(SelectionEvent selectionEvent) {
        makeCurrent(selectionEvent, "#{bindings.DadosOsView1.collectionModel.makeCurrent}");
        Row rw = (Row)ADFUtils.evaluateEL("#{bindings.DadosOsView1.currentRow}");
        Number idMotorista = null;
        Number idModelo = null;
       // Number idOs = null;
        if (rw != null) {
            idMotorista = (Number)rw.getAttribute("FkMotorista");
            idModelo = (Number)rw.getAttribute("FkModelo");
            idOs = (Number)rw.getAttribute("IdOs");
        } //end if
        if (idOs != null) {
            final Map<String, Object> parametros =
                new HashMap<String, Object>();
            parametros.put("idOs", idOs);
            ADFUtils.executeOperationBinding("buscarDiaria", parametros);

            RowSetIterator it =
                ADFUtils.findIterator(IT_DIARIA).getRowSetIterator();

            JSFUtils.addPartialTriggerWithIdFromUiRoot("t11");
        } //end

        if (idMotorista != null) {
            DCIteratorBinding it = ADFUtils.findIterator(IT_TB_MOTORISTA);
            ViewObject view = it.getViewObject();
            view.reset();
            view.clearCache();
            view.setWhereClause("ID_MOTORISTA = " + idMotorista);
            view.executeQuery();
            RowSetIterator iteratorMotorista =
                ADFUtils.findIterator(IT_TB_MOTORISTA).getRowSetIterator();
            Row row = iteratorMotorista.getCurrentRow();
            if (row != null) {
                String nome = (String)row.getAttribute("Nome");
                String telefone = (String)row.getAttribute("Telefone");
                setNomeMotorista(nome);
                setTelefoneMotorista(telefone);
            } //end if
        } //end if
        if (idModelo != null) {
            DCIteratorBinding it = ADFUtils.findIterator(IT_TB_VEICULO);
            ViewObject view = it.getViewObject();
            view.reset();
            view.clearCache();
            view.setWhereClause("ID_MODELO = " + idModelo);
            view.executeQuery();
            RowSetIterator iteratorVeiculo =
                ADFUtils.findIterator(IT_TB_VEICULO).getRowSetIterator();
            Row row = iteratorVeiculo.getCurrentRow();
            if (row != null) {
                String modelo = (String)row.getAttribute("Modelo");
                setModeloVeiculo(modelo);
            } //end if
        } //end if
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

    //TODO
    //Codigo da diaria

    public void chamadaPopupInserirDiaria(PopupFetchEvent popupFetchEvent) {
        setDtHrChegada(null);
        setDtHrSaida(null);
        setKmChegada(null);
        setKmsaida(null);
    }

    public void gravarDiaria(ActionEvent actionEvent) {
        RowSetIterator iteratorDiaria = ADFUtils.findIterator(IT_TB_DIARIA).getRowSetIterator();
        RowSetIterator iteratorHrs = ADFUtils.findIterator(IT_TB_FRANQUIA_HR).getRowSetIterator();
        RowSetIterator iteratorKm = ADFUtils.findIterator(IT_TB_FRANQUIA_KM).getRowSetIterator();
        
        Row rowHoras = iteratorHrs.getCurrentRow();
        Row rowKm = iteratorKm.getCurrentRow();
              
        try {

            Row criarRow = iteratorDiaria.createRow();
            criarRow.setNewRowState(Row.STATUS_INITIALIZED);
            criarRow.setAttribute("HrChegada", this.getDtHrChegada());
            criarRow.setAttribute("HrSaida", this.getDtHrSaida());
            criarRow.setAttribute("FkOrdemDeServico", this.getIdOs());
            criarRow.setAttribute("KmChegada", this.getKmChegada());
            criarRow.setAttribute("KmSaida", this.getKmsaida());
            
            if(this.getTotalHrsDia()!= null){
                criarRow.setAttribute("TotalHrExtDia", this.getTotalHrsDia() +"Hrs");
            }
            if(this.getTotalKmDia()!= null){
                criarRow.setAttribute("TotalKmRodado", this.getTotalKmDia() +"Hrs");
            }
            criarRow.setAttribute("FkFranquiaHrs", rowHoras.getAttribute("IdFranquia"));
            criarRow.setAttribute("FkFranquiaKm", rowKm.getAttribute("IdFranquiaKm"));

            ADFUtils.executeBindingOperation("CommitTbDiaria");

            popupInserirDiaria.cancel();
            refreshTableDiaria();
            JSFUtils.addFacesConfirmationMessage("Dados gravados com sucesso");
        }catch (Exception e) {
            e.printStackTrace();
        } //end try... catch
    }//end
    
    public void refreshTableDiaria() {
        DCIteratorBinding dcIter = ADFUtils.findIterator(IT_DIARIA);
        dcIter.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getBindGridDiaria());
        this.getBindGridDiaria().processUpdates(FacesContext.getCurrentInstance());
    } //end
    
    

    public void cancelPopupInserirDiaria(ActionEvent actionEvent) {
        popupInserirDiaria.cancel();
    }
    
    
    public void validarHoraChegada(FacesContext facesContext,UIComponent uIComponent, Object object) {
        Timestamp dataChegada = (Timestamp)object;
        Timestamp dataSaida = getDtHrSaida();
        if (dataSaida != null && dataChegada.compareTo(dataSaida) < 0) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data inv�lida","A data de chegada nao pode ser menor que a saida");
            throw new ValidatorException(msg);
        }//end if 
    }//end
    
    public void validarKmChegada(FacesContext facesContext, UIComponent uIComponent, Object object) {
        Integer chegada = Integer.parseInt(object.toString());
        if (getKmsaida() != null && getKmsaida().intValue() > chegada) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Quilometragem inv�lida","A Quilometragem de chegada nao pode ser menor que a saida");
            throw new ValidatorException(msg);
        }//end if 
    }
    
    //TODO

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
}

/*     public List<SelectItem> getListKmFranquia() {
        //#{bindings.TbFranquiaKmView1.items}        
      if (listaKmFranquia == null) {
          listaKmFranquia = new ArrayList<SelectItem>();
          try {
              DCIteratorBinding it = ADFUtils.findIterator(IT_TB_FRANQUIA_KM);
              ViewObject view = it.getViewObject();
              view.reset();
              view.clearCache();
              view.executeQuery();

              RowSetIterator iteratorFranquia =
                  ADFUtils.findIterator(IT_TB_FRANQUIA_KM).getRowSetIterator();

              Row[] rows = iteratorFranquia.getAllRowsInRange();

              if (rows != null) {
                  for (Row rw : rows) {
                      listaKmFranquia.add(new SelectItem(rw.getAttribute("IdFranquiaKm"),
                                                         rw.getAttribute("Km").toString()));
                  } //end for
              } //end if
          } catch (Exception e) {
              e.printStackTrace();
          }
      } //end if
      return listaKmFranquia;
    } //end */
