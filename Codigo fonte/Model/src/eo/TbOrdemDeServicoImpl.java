package eo;

import oracle.jbo.ApplicationModule;
import oracle.jbo.AttributeList;
import oracle.jbo.Key;
import oracle.jbo.RowIterator;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.EntityImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Wed Apr 23 21:35:32 BRT 2014
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class TbOrdemDeServicoImpl extends EntityImpl {
    private static EntityDefImpl mDefinitionObject;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        IdOs {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getIdOs();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setIdOs((Number)value);
            }
        }
        ,
        DataDaOs {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getDataDaOs();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setDataDaOs((Date)value);
            }
        }
        ,
        TotalHrsExtra {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getTotalHrsExtra();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setTotalHrsExtra((String)value);
            }
        }
        ,
        TotalKmExtra {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getTotalKmExtra();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setTotalKmExtra((Number)value);
            }
        }
        ,
        Observacao {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getObservacao();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setObservacao((String)value);
            }
        }
        ,
        FkVeiculo {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getFkVeiculo();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setFkVeiculo((Number)value);
            }
        }
        ,
        FkCliente {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getFkCliente();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setFkCliente((Number)value);
            }
        }
        ,
        FkMotorista {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getFkMotorista();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setFkMotorista((Number)value);
            }
        }
        ,
        UsuarioServ {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getUsuarioServ();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setUsuarioServ((String)value);
            }
        }
        ,
        UsuarioTel {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getUsuarioTel();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setUsuarioTel((String)value);
            }
        }
        ,
        FkFuncionario {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getFkFuncionario();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setFkFuncionario((Number)value);
            }
        }
        ,
        TbDiaria {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getTbDiaria();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        TbCliente {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getTbCliente();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setTbCliente((TbClienteImpl)value);
            }
        }
        ,
        TbFuncionario {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getTbFuncionario();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setTbFuncionario((TbFuncionarioImpl)value);
            }
        }
        ,
        TbMotorista {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getTbMotorista();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setTbMotorista((TbMotoristaImpl)value);
            }
        }
        ,
        TbVeiculo {
            public Object get(TbOrdemDeServicoImpl obj) {
                return obj.getTbVeiculo();
            }

            public void put(TbOrdemDeServicoImpl obj, Object value) {
                obj.setTbVeiculo((TbVeiculoImpl)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(TbOrdemDeServicoImpl object);

        public abstract void put(TbOrdemDeServicoImpl object, Object value);

        public int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDOS = AttributesEnum.IdOs.index();
    public static final int DATADAOS = AttributesEnum.DataDaOs.index();
    public static final int TOTALHRSEXTRA = AttributesEnum.TotalHrsExtra.index();
    public static final int TOTALKMEXTRA = AttributesEnum.TotalKmExtra.index();
    public static final int OBSERVACAO = AttributesEnum.Observacao.index();
    public static final int FKVEICULO = AttributesEnum.FkVeiculo.index();
    public static final int FKCLIENTE = AttributesEnum.FkCliente.index();
    public static final int FKMOTORISTA = AttributesEnum.FkMotorista.index();
    public static final int USUARIOSERV = AttributesEnum.UsuarioServ.index();
    public static final int USUARIOTEL = AttributesEnum.UsuarioTel.index();
    public static final int FKFUNCIONARIO = AttributesEnum.FkFuncionario.index();
    public static final int TBDIARIA = AttributesEnum.TbDiaria.index();
    public static final int TBCLIENTE = AttributesEnum.TbCliente.index();
    public static final int TBFUNCIONARIO = AttributesEnum.TbFuncionario.index();
    public static final int TBMOTORISTA = AttributesEnum.TbMotorista.index();
    public static final int TBVEICULO = AttributesEnum.TbVeiculo.index();

    /**
     * This is the default constructor (do not remove).
     */
    public TbOrdemDeServicoImpl() {
    }

    /**
     * Gets the attribute value for IdOs, using the alias name IdOs.
     * @return the IdOs
     */
    public Number getIdOs() {
        return (Number)getAttributeInternal(IDOS);
    }

    /**
     * Sets <code>value</code> as the attribute value for IdOs.
     * @param value value to set the IdOs
     */
    public void setIdOs(Number value) {
        setAttributeInternal(IDOS, value);
    }

    /**
     * Gets the attribute value for DataDaOs, using the alias name DataDaOs.
     * @return the DataDaOs
     */
    public Date getDataDaOs() {
        return (Date)getAttributeInternal(DATADAOS);
    }

    /**
     * Sets <code>value</code> as the attribute value for DataDaOs.
     * @param value value to set the DataDaOs
     */
    public void setDataDaOs(Date value) {
        setAttributeInternal(DATADAOS, value);
    }

    /**
     * Gets the attribute value for TotalHrsExtra, using the alias name TotalHrsExtra.
     * @return the TotalHrsExtra
     */
    public String getTotalHrsExtra() {
        return (String)getAttributeInternal(TOTALHRSEXTRA);
    }

    /**
     * Sets <code>value</code> as the attribute value for TotalHrsExtra.
     * @param value value to set the TotalHrsExtra
     */
    public void setTotalHrsExtra(String value) {
        setAttributeInternal(TOTALHRSEXTRA, value);
    }

    /**
     * Gets the attribute value for TotalKmExtra, using the alias name TotalKmExtra.
     * @return the TotalKmExtra
     */
    public Number getTotalKmExtra() {
        return (Number)getAttributeInternal(TOTALKMEXTRA);
    }

    /**
     * Sets <code>value</code> as the attribute value for TotalKmExtra.
     * @param value value to set the TotalKmExtra
     */
    public void setTotalKmExtra(Number value) {
        setAttributeInternal(TOTALKMEXTRA, value);
    }

    /**
     * Gets the attribute value for Observacao, using the alias name Observacao.
     * @return the Observacao
     */
    public String getObservacao() {
        return (String)getAttributeInternal(OBSERVACAO);
    }

    /**
     * Sets <code>value</code> as the attribute value for Observacao.
     * @param value value to set the Observacao
     */
    public void setObservacao(String value) {
        setAttributeInternal(OBSERVACAO, value);
    }

    /**
     * Gets the attribute value for FkVeiculo, using the alias name FkVeiculo.
     * @return the FkVeiculo
     */
    public Number getFkVeiculo() {
        return (Number)getAttributeInternal(FKVEICULO);
    }

    /**
     * Sets <code>value</code> as the attribute value for FkVeiculo.
     * @param value value to set the FkVeiculo
     */
    public void setFkVeiculo(Number value) {
        setAttributeInternal(FKVEICULO, value);
    }

    /**
     * Gets the attribute value for FkCliente, using the alias name FkCliente.
     * @return the FkCliente
     */
    public Number getFkCliente() {
        return (Number)getAttributeInternal(FKCLIENTE);
    }

    /**
     * Sets <code>value</code> as the attribute value for FkCliente.
     * @param value value to set the FkCliente
     */
    public void setFkCliente(Number value) {
        setAttributeInternal(FKCLIENTE, value);
    }

    /**
     * Gets the attribute value for FkMotorista, using the alias name FkMotorista.
     * @return the FkMotorista
     */
    public Number getFkMotorista() {
        return (Number)getAttributeInternal(FKMOTORISTA);
    }

    /**
     * Sets <code>value</code> as the attribute value for FkMotorista.
     * @param value value to set the FkMotorista
     */
    public void setFkMotorista(Number value) {
        setAttributeInternal(FKMOTORISTA, value);
    }

    /**
     * Gets the attribute value for UsuarioServ, using the alias name UsuarioServ.
     * @return the UsuarioServ
     */
    public String getUsuarioServ() {
        return (String)getAttributeInternal(USUARIOSERV);
    }

    /**
     * Sets <code>value</code> as the attribute value for UsuarioServ.
     * @param value value to set the UsuarioServ
     */
    public void setUsuarioServ(String value) {
        setAttributeInternal(USUARIOSERV, value);
    }

    /**
     * Gets the attribute value for UsuarioTel, using the alias name UsuarioTel.
     * @return the UsuarioTel
     */
    public String getUsuarioTel() {
        return (String)getAttributeInternal(USUARIOTEL);
    }

    /**
     * Sets <code>value</code> as the attribute value for UsuarioTel.
     * @param value value to set the UsuarioTel
     */
    public void setUsuarioTel(String value) {
        setAttributeInternal(USUARIOTEL, value);
    }

    /**
     * Gets the attribute value for FkFuncionario, using the alias name FkFuncionario.
     * @return the FkFuncionario
     */
    public Number getFkFuncionario() {
        return (Number)getAttributeInternal(FKFUNCIONARIO);
    }

    /**
     * Sets <code>value</code> as the attribute value for FkFuncionario.
     * @param value value to set the FkFuncionario
     */
    public void setFkFuncionario(Number value) {
        setAttributeInternal(FKFUNCIONARIO, value);
    }

    /**
     * getAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param attrDef the attribute

     * @return the attribute value
     * @throws Exception
     */
    protected Object getAttrInvokeAccessor(int index,
                                           AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            return AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].get(this);
        }
        return super.getAttrInvokeAccessor(index, attrDef);
    }

    /**
     * setAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param value the value to assign to the attribute
     * @param attrDef the attribute

     * @throws Exception
     */
    protected void setAttrInvokeAccessor(int index, Object value,
                                         AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].put(this, value);
            return;
        }
        super.setAttrInvokeAccessor(index, value, attrDef);
    }

    /**
     * @return the associated entity oracle.jbo.RowIterator.
     */
    public RowIterator getTbDiaria() {
        return (RowIterator)getAttributeInternal(TBDIARIA);
    }

    /**
     * @return the associated entity TbClienteImpl.
     */
    public TbClienteImpl getTbCliente() {
        return (TbClienteImpl)getAttributeInternal(TBCLIENTE);
    }

    /**
     * Sets <code>value</code> as the associated entity TbClienteImpl.
     */
    public void setTbCliente(TbClienteImpl value) {
        setAttributeInternal(TBCLIENTE, value);
    }

    /**
     * @return the associated entity TbFuncionarioImpl.
     */
    public TbFuncionarioImpl getTbFuncionario() {
        return (TbFuncionarioImpl)getAttributeInternal(TBFUNCIONARIO);
    }

    /**
     * Sets <code>value</code> as the associated entity TbFuncionarioImpl.
     */
    public void setTbFuncionario(TbFuncionarioImpl value) {
        setAttributeInternal(TBFUNCIONARIO, value);
    }

    /**
     * @return the associated entity TbMotoristaImpl.
     */
    public TbMotoristaImpl getTbMotorista() {
        return (TbMotoristaImpl)getAttributeInternal(TBMOTORISTA);
    }

    /**
     * Sets <code>value</code> as the associated entity TbMotoristaImpl.
     */
    public void setTbMotorista(TbMotoristaImpl value) {
        setAttributeInternal(TBMOTORISTA, value);
    }

    /**
     * @return the associated entity TbVeiculoImpl.
     */
    public TbVeiculoImpl getTbVeiculo() {
        return (TbVeiculoImpl)getAttributeInternal(TBVEICULO);
    }

    /**
     * Sets <code>value</code> as the associated entity TbVeiculoImpl.
     */
    public void setTbVeiculo(TbVeiculoImpl value) {
        setAttributeInternal(TBVEICULO, value);
    }

    /**
     * @param idOs key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Number idOs) {
        return new Key(new Object[]{idOs});
    }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        if (mDefinitionObject == null) {
            mDefinitionObject = EntityDefImpl.findDefObject("eo.TbOrdemDeServico");
        }
        return mDefinitionObject;
    }
    protected void create(AttributeList attributeList) {
        super.create(attributeList);
        //Sequence
        ApplicationModule root = getDBTransaction().getRootApplicationModule();
        DBSequence seqid = new DBSequence("SEQ_TB_OS", root);
        setIdOs(seqid.getSequenceNumber());
    }
}
