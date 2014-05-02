package br.com.locCar.bean.ordemDeServico;

import br.com.locCar.util.ADFUtils;

import br.com.locCar.util.GenericTableSelectionHandler;
import br.com.locCar.util.JSFUtils;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;


import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;


import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import oracle.jbo.domain.Number;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;

public class OrdemDeServico {

    private final String IT_TB_FRANQUIA = "TbFranquiaView1Iterator";
    private final String IT_TB_OS = "TbOrdemDeServicoView1Iterator";
    private final String IT_OS = "OsDadosView1Iterator";

    private final String IT_TB_CLIENTE = "TbClienteView1Iterator";
    private final String IT_TB_VEICULO = "VeiculoMMView1Iterator";
    private final String IT_TB_MOTORISTA = "MotoristaCategoriaView1Iterator";

    protected List<OsTO> cliente;
    protected List<OsTO> veiculo;
    protected List<OsTO> motorista;

    private Integer idCliente;
    private Integer idMotorista;
    private Integer idVeiculo;

    private List<SelectItem> listaHrsFranquias;
    private List<SelectItem> listaKmFranquia;
    private Number idHrFranquia;
    private Number idKmFranquia;
    private String hrFranquia;
    private String kmFranquia;

    private String nomeCliPesq;
    private String nomeMotPesq;
    private String modeloPesq;

    private String nomeUsuario;
    private String telUsuario;

    protected boolean statusVeiculo;
    protected boolean statusMotorista;
    protected boolean statusCliente;

    private RichTable bindGridClientes;
    private RichTable bindGridVeiculos;
    private RichTable bindGridMotoristas;

    private RowKeySet selectedKeys;
    private RichPopup popupInserir;
    private RichTable bindGridPrincipal;


    public OrdemDeServico() {
    }

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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void cancelarPopupInserir(ActionEvent actionEvent) {
        popupInserir.cancel();
    }

    public void refreshTable() {
        DCIteratorBinding dcIter = ADFUtils.findIterator(IT_OS);
        dcIter.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.getBindGridPrincipal());
        this.getBindGridPrincipal().processUpdates(FacesContext.getCurrentInstance());
        JSFUtils.addPartialTriggerWithIdFromUiRoot("t1");
    }


    public void limparCliente(ActionEvent actionEvent) {
        removeFocoGridCliente();
        setStatusCliente(true);
        setIdCliente(null);
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ctb8");
        cliente.clear();
        JSFUtils.addPartialTrigger(bindGridClientes);
    }

    public void removeFocoGridCliente() {
        selectedKeys = bindGridClientes.getSelectedRowKeys();
        if (selectedKeys != null) {
            selectedKeys.clear();
        }
    }

    public void limparVeiculo(ActionEvent actionEvent) {
        removeFocoGridVeiculo();
        selectedKeys.clear();
        setIdVeiculo(null);
        setStatusVeiculo(true);
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ctb9");
        veiculo.clear();
        JSFUtils.addPartialTrigger(bindGridVeiculos);

    }

    public void removeFocoGridVeiculo() {
        selectedKeys = bindGridVeiculos.getSelectedRowKeys();
        if (selectedKeys != null) {
            selectedKeys.clear();
        }
    }

    public void limparMotorista(ActionEvent actionEvent) {
        removeFocoGridMotorista();
        setStatusMotorista(true);
        setIdMotorista(null);
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ctb10");
        motorista.clear();
        JSFUtils.addPartialTrigger(bindGridMotoristas);

    }

    public void removeFocoGridMotorista() {
        selectedKeys = bindGridMotoristas.getSelectedRowKeys();
        if (selectedKeys != null) {
            selectedKeys.clear();
        }
    }

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
        }
    }

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
        }
    }

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
        }
    }

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
    }

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

                OsTO dadosCliente = new OsTO();
                dadosCliente.setIdMot(id.intValue());
                dadosCliente.setNomeMot(nome);
                dadosCliente.setCatHab(categoria);
                dadosCliente.setTelefoneMot(telefone);
                motorista.add(dadosCliente);
            } //end for
        } //end if
        JSFUtils.addPartialTrigger(bindGridMotoristas);
        setNomeMotPesq("");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it4");
    }

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

                OsTO dadosCliente = new OsTO();
                dadosCliente.setIdCar(id.intValue());
                dadosCliente.setModeloCar(modelo);
                dadosCliente.setPlacaCar(placa);
                dadosCliente.setAnoCar(ano);
                veiculo.add(dadosCliente);
            } //end for
        } //end if
        JSFUtils.addPartialTrigger(bindGridVeiculos);
        setModeloPesq("");
        JSFUtils.addPartialTriggerWithIdFromUiRoot("it5");
    }

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
            }
        } //end if
        return cliente;
    }

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
            }
        } //end if
        return veiculo;
    }

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
    }

    public void selectionListenerCliente(SelectionEvent selectionEvent) {
        if (bindGridClientes.getSelectedRowKeys() != null) {
            setStatusCliente(false);
        }
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ctb8");
    }

    public void selectionListenerVeiculo(SelectionEvent selectionEvent) {
        if (bindGridVeiculos.getSelectedRowKeys() != null) {
            setStatusVeiculo(false);
        }
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ctb9");
    }

    public void selectionListenerMotorista(SelectionEvent selectionEvent) {
        if (bindGridMotoristas.getSelectedRowKeys() != null) {
            setStatusMotorista(false);
        }
        JSFUtils.addPartialTriggerWithIdFromUiRoot("ctb10");
    }

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

    public void setHrFranquia(String hrFranquia) {
        this.hrFranquia = hrFranquia;
    }

    public String getHrFranquia() {
        return hrFranquia;
    }

    public void setKmFranquia(String kmFranquia) {
        this.kmFranquia = kmFranquia;
    }

    public String getKmFranquia() {
        return kmFranquia;
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

    public void hrFranquiaChangeListener(ValueChangeEvent valueChangeEvent) {
        Number value = (Number)valueChangeEvent.getNewValue();
        if (value != null) {

            try {
                DCIteratorBinding it = ADFUtils.findIterator(IT_TB_FRANQUIA);
                ViewObject view = it.getViewObject();
                view.reset();
                view.clearCache();
                view.setWhereClause("ID_FRANQUIA = " + value);
                view.executeQuery();

                RowSetIterator iteratorFranquia =
                    ADFUtils.findIterator(IT_TB_FRANQUIA).getRowSetIterator();

                Row row = iteratorFranquia.getCurrentRow();

                if (row != null) {
                    String hr = (String)row.getAttribute("HrFranquia");
                    hr = hr + "Hrs";
                    setHrFranquia(hr);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void kmFranquiaChangeListener(ValueChangeEvent valueChangeEvent) {
        Number value = (Number)valueChangeEvent.getNewValue();
        if (value != null) {

            try {
                DCIteratorBinding it = ADFUtils.findIterator(IT_TB_FRANQUIA);
                ViewObject view = it.getViewObject();
                view.reset();
                view.clearCache();
                view.setWhereClause("ID_FRANQUIA = " + value);
                view.executeQuery();

                RowSetIterator iteratorFranquia =
                    ADFUtils.findIterator(IT_TB_FRANQUIA).getRowSetIterator();

                Row row = iteratorFranquia.getCurrentRow();

                if (row != null) {
                    String km = row.getAttribute("KmFranquia").toString();
                    km = km + "Km";
                    setKmFranquia(km);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public List<SelectItem> getListKmFranquia() {
        if (listaKmFranquia == null) {
            listaKmFranquia = new ArrayList<SelectItem>();
            try {
                DCIteratorBinding it = ADFUtils.findIterator(IT_TB_FRANQUIA);
                ViewObject view = it.getViewObject();
                view.reset();
                view.clearCache();
                view.executeQuery();

                RowSetIterator iteratorFranquia =
                    ADFUtils.findIterator(IT_TB_FRANQUIA).getRowSetIterator();

                Row[] rows = iteratorFranquia.getAllRowsInRange();

                if (rows != null) {
                    for (Row rw : rows) {
                        listaKmFranquia.add(new SelectItem(rw.getAttribute("IdFranquia"),
                                                           rw.getAttribute("KmFranquia").toString()));
                    } //end for
                } //end if
            } catch (Exception e) {
                e.printStackTrace();
            }
        } //end if
        return listaKmFranquia;
    } //end

    public List<SelectItem> getListHrsFranquia() {
        if (listaHrsFranquias == null) {
            listaHrsFranquias = new ArrayList<SelectItem>();
            try {
                DCIteratorBinding it = ADFUtils.findIterator(IT_TB_FRANQUIA);
                ViewObject view = it.getViewObject();
                view.reset();
                view.clearCache();
                view.executeQuery();

                RowSetIterator iteratorFranquia =
                    ADFUtils.findIterator(IT_TB_FRANQUIA).getRowSetIterator();

                Row[] rows = iteratorFranquia.getAllRowsInRange();

                if (rows != null) {
                    for (Row rw : rows) {
                        if (rw.getAttribute("HrFranquia") != null) {
                            listaHrsFranquias.add(new SelectItem(rw.getAttribute("IdFranquia"),
                                                                 (String)rw.getAttribute("HrFranquia")));
                        } //end if
                    } //end for
                } //end if

            } catch (Exception e) {
                e.printStackTrace();
            }
        } //end if
        return listaHrsFranquias;
    } //end

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
}
