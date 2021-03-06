package eo;

import oracle.jbo.ApplicationModule;
import oracle.jbo.AttributeList;
import oracle.jbo.Key;
import oracle.jbo.RowIterator;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Number;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.EntityImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Apr 01 14:23:01 BRT 2014
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class TbVeiculoImpl extends EntityImpl {
    private static EntityDefImpl mDefinitionObject;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        IdVeiculo {
            public Object get(TbVeiculoImpl obj) {
                return obj.getIdVeiculo();
            }

            public void put(TbVeiculoImpl obj, Object value) {
                obj.setIdVeiculo((Number)value);
            }
        }
        ,
        Ano {
            public Object get(TbVeiculoImpl obj) {
                return obj.getAno();
            }

            public void put(TbVeiculoImpl obj, Object value) {
                obj.setAno((String)value);
            }
        }
        ,
        Placa {
            public Object get(TbVeiculoImpl obj) {
                return obj.getPlaca();
            }

            public void put(TbVeiculoImpl obj, Object value) {
                obj.setPlaca((String)value);
            }
        }
        ,
        Proprietario {
            public Object get(TbVeiculoImpl obj) {
                return obj.getProprietario();
            }

            public void put(TbVeiculoImpl obj, Object value) {
                obj.setProprietario((String)value);
            }
        }
        ,
        Telefone {
            public Object get(TbVeiculoImpl obj) {
                return obj.getTelefone();
            }

            public void put(TbVeiculoImpl obj, Object value) {
                obj.setTelefone((String)value);
            }
        }
        ,
        FkModelo {
            public Object get(TbVeiculoImpl obj) {
                return obj.getFkModelo();
            }

            public void put(TbVeiculoImpl obj, Object value) {
                obj.setFkModelo((Number)value);
            }
        }
        ,
        TbOrdemDeServico {
            public Object get(TbVeiculoImpl obj) {
                return obj.getTbOrdemDeServico();
            }

            public void put(TbVeiculoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(TbVeiculoImpl object);

        public abstract void put(TbVeiculoImpl object, Object value);

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


    public static final int IDVEICULO = AttributesEnum.IdVeiculo.index();
    public static final int ANO = AttributesEnum.Ano.index();
    public static final int PLACA = AttributesEnum.Placa.index();
    public static final int PROPRIETARIO = AttributesEnum.Proprietario.index();
    public static final int TELEFONE = AttributesEnum.Telefone.index();
    public static final int FKMODELO = AttributesEnum.FkModelo.index();
    public static final int TBORDEMDESERVICO = AttributesEnum.TbOrdemDeServico.index();

    /**
     * This is the default constructor (do not remove).
     */
    public TbVeiculoImpl() {
    }


    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        if (mDefinitionObject == null) {
            mDefinitionObject = EntityDefImpl.findDefObject("eo.TbVeiculo");
        }
        return mDefinitionObject;
    }

    /**
     * Gets the attribute value for IdVeiculo, using the alias name IdVeiculo.
     * @return the IdVeiculo
     */
    public Number getIdVeiculo() {
        return (Number)getAttributeInternal(IDVEICULO);
    }

    /**
     * Sets <code>value</code> as the attribute value for IdVeiculo.
     * @param value value to set the IdVeiculo
     */
    public void setIdVeiculo(Number value) {
        setAttributeInternal(IDVEICULO, value);
    }


    /**
     * Gets the attribute value for Ano, using the alias name Ano.
     * @return the Ano
     */
    public String getAno() {
        return (String)getAttributeInternal(ANO);
    }

    /**
     * Sets <code>value</code> as the attribute value for Ano.
     * @param value value to set the Ano
     */
    public void setAno(String value) {
        setAttributeInternal(ANO, value);
    }

    /**
     * Gets the attribute value for Placa, using the alias name Placa.
     * @return the Placa
     */
    public String getPlaca() {
        return (String)getAttributeInternal(PLACA);
    }

    /**
     * Sets <code>value</code> as the attribute value for Placa.
     * @param value value to set the Placa
     */
    public void setPlaca(String value) {
        setAttributeInternal(PLACA, value);
    }

    /**
     * Gets the attribute value for Proprietario, using the alias name Proprietario.
     * @return the Proprietario
     */
    public String getProprietario() {
        return (String)getAttributeInternal(PROPRIETARIO);
    }

    /**
     * Sets <code>value</code> as the attribute value for Proprietario.
     * @param value value to set the Proprietario
     */
    public void setProprietario(String value) {
        setAttributeInternal(PROPRIETARIO, value);
    }

    /**
     * Gets the attribute value for Telefone, using the alias name Telefone.
     * @return the Telefone
     */
    public String getTelefone() {
        return (String)getAttributeInternal(TELEFONE);
    }

    /**
     * Sets <code>value</code> as the attribute value for Telefone.
     * @param value value to set the Telefone
     */
    public void setTelefone(String value) {
        setAttributeInternal(TELEFONE, value);
    }

    /**
     * Gets the attribute value for FkModelo, using the alias name FkModelo.
     * @return the FkModelo
     */
    public Number getFkModelo() {
        return (Number)getAttributeInternal(FKMODELO);
    }

    /**
     * Sets <code>value</code> as the attribute value for FkModelo.
     * @param value value to set the FkModelo
     */
    public void setFkModelo(Number value) {
        setAttributeInternal(FKMODELO, value);
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
    public RowIterator getTbOrdemDeServico() {
        return (RowIterator)getAttributeInternal(TBORDEMDESERVICO);
    }


    /**
     * @param idVeiculo key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Number idVeiculo) {
        return new Key(new Object[]{idVeiculo});
    }

    protected void create(AttributeList attributeList) {
        super.create(attributeList);
        //Sequence
        ApplicationModule root = getDBTransaction().getRootApplicationModule();
        DBSequence seqid = new DBSequence("SEQ_TB_VEICULO", root);
        setIdVeiculo(seqid.getSequenceNumber());
    }
}
