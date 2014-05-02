package eo;

import oracle.jbo.ApplicationModule;
import oracle.jbo.AttributeList;
import oracle.jbo.Key;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Number;
import oracle.jbo.domain.Timestamp;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.EntityImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Wed Apr 23 21:38:44 BRT 2014
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class TbDiariaImpl extends EntityImpl {
    private static EntityDefImpl mDefinitionObject;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        IdDiaria {
            public Object get(TbDiariaImpl obj) {
                return obj.getIdDiaria();
            }

            public void put(TbDiariaImpl obj, Object value) {
                obj.setIdDiaria((Number)value);
            }
        }
        ,
        HrChegada {
            public Object get(TbDiariaImpl obj) {
                return obj.getHrChegada();
            }

            public void put(TbDiariaImpl obj, Object value) {
                obj.setHrChegada((Timestamp)value);
            }
        }
        ,
        HrSaida {
            public Object get(TbDiariaImpl obj) {
                return obj.getHrSaida();
            }

            public void put(TbDiariaImpl obj, Object value) {
                obj.setHrSaida((Timestamp)value);
            }
        }
        ,
        TotalHrExtDia {
            public Object get(TbDiariaImpl obj) {
                return obj.getTotalHrExtDia();
            }

            public void put(TbDiariaImpl obj, Object value) {
                obj.setTotalHrExtDia((String)value);
            }
        }
        ,
        FkFranquia {
            public Object get(TbDiariaImpl obj) {
                return obj.getFkFranquia();
            }

            public void put(TbDiariaImpl obj, Object value) {
                obj.setFkFranquia((Number)value);
            }
        }
        ,
        FkOrdemDeServico {
            public Object get(TbDiariaImpl obj) {
                return obj.getFkOrdemDeServico();
            }

            public void put(TbDiariaImpl obj, Object value) {
                obj.setFkOrdemDeServico((Number)value);
            }
        }
        ,
        KmChegada {
            public Object get(TbDiariaImpl obj) {
                return obj.getKmChegada();
            }

            public void put(TbDiariaImpl obj, Object value) {
                obj.setKmChegada((Number)value);
            }
        }
        ,
        KmSaida {
            public Object get(TbDiariaImpl obj) {
                return obj.getKmSaida();
            }

            public void put(TbDiariaImpl obj, Object value) {
                obj.setKmSaida((Number)value);
            }
        }
        ,
        TotalKmRodado {
            public Object get(TbDiariaImpl obj) {
                return obj.getTotalKmRodado();
            }

            public void put(TbDiariaImpl obj, Object value) {
                obj.setTotalKmRodado((Number)value);
            }
        }
        ,
        TbFranquia {
            public Object get(TbDiariaImpl obj) {
                return obj.getTbFranquia();
            }

            public void put(TbDiariaImpl obj, Object value) {
                obj.setTbFranquia((EntityImpl)value);
            }
        }
        ,
        TbOrdemDeServico {
            public Object get(TbDiariaImpl obj) {
                return obj.getTbOrdemDeServico();
            }

            public void put(TbDiariaImpl obj, Object value) {
                obj.setTbOrdemDeServico((TbOrdemDeServicoImpl)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(TbDiariaImpl object);

        public abstract void put(TbDiariaImpl object, Object value);

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
    public static final int IDDIARIA = AttributesEnum.IdDiaria.index();
    public static final int HRCHEGADA = AttributesEnum.HrChegada.index();
    public static final int HRSAIDA = AttributesEnum.HrSaida.index();
    public static final int TOTALHREXTDIA = AttributesEnum.TotalHrExtDia.index();
    public static final int FKFRANQUIA = AttributesEnum.FkFranquia.index();
    public static final int FKORDEMDESERVICO = AttributesEnum.FkOrdemDeServico.index();
    public static final int KMCHEGADA = AttributesEnum.KmChegada.index();
    public static final int KMSAIDA = AttributesEnum.KmSaida.index();
    public static final int TOTALKMRODADO = AttributesEnum.TotalKmRodado.index();
    public static final int TBFRANQUIA = AttributesEnum.TbFranquia.index();
    public static final int TBORDEMDESERVICO = AttributesEnum.TbOrdemDeServico.index();

    /**
     * This is the default constructor (do not remove).
     */
    public TbDiariaImpl() {
    }

    /**
     * Gets the attribute value for IdDiaria, using the alias name IdDiaria.
     * @return the IdDiaria
     */
    public Number getIdDiaria() {
        return (Number)getAttributeInternal(IDDIARIA);
    }

    /**
     * Sets <code>value</code> as the attribute value for IdDiaria.
     * @param value value to set the IdDiaria
     */
    public void setIdDiaria(Number value) {
        setAttributeInternal(IDDIARIA, value);
    }

    /**
     * Gets the attribute value for HrChegada, using the alias name HrChegada.
     * @return the HrChegada
     */
    public Timestamp getHrChegada() {
        return (Timestamp)getAttributeInternal(HRCHEGADA);
    }

    /**
     * Sets <code>value</code> as the attribute value for HrChegada.
     * @param value value to set the HrChegada
     */
    public void setHrChegada(Timestamp value) {
        setAttributeInternal(HRCHEGADA, value);
    }

    /**
     * Gets the attribute value for HrSaida, using the alias name HrSaida.
     * @return the HrSaida
     */
    public Timestamp getHrSaida() {
        return (Timestamp)getAttributeInternal(HRSAIDA);
    }

    /**
     * Sets <code>value</code> as the attribute value for HrSaida.
     * @param value value to set the HrSaida
     */
    public void setHrSaida(Timestamp value) {
        setAttributeInternal(HRSAIDA, value);
    }

    /**
     * Gets the attribute value for TotalHrExtDia, using the alias name TotalHrExtDia.
     * @return the TotalHrExtDia
     */
    public String getTotalHrExtDia() {
        return (String)getAttributeInternal(TOTALHREXTDIA);
    }

    /**
     * Sets <code>value</code> as the attribute value for TotalHrExtDia.
     * @param value value to set the TotalHrExtDia
     */
    public void setTotalHrExtDia(String value) {
        setAttributeInternal(TOTALHREXTDIA, value);
    }

    /**
     * Gets the attribute value for FkFranquia, using the alias name FkFranquia.
     * @return the FkFranquia
     */
    public Number getFkFranquia() {
        return (Number)getAttributeInternal(FKFRANQUIA);
    }

    /**
     * Sets <code>value</code> as the attribute value for FkFranquia.
     * @param value value to set the FkFranquia
     */
    public void setFkFranquia(Number value) {
        setAttributeInternal(FKFRANQUIA, value);
    }

    /**
     * Gets the attribute value for FkOrdemDeServico, using the alias name FkOrdemDeServico.
     * @return the FkOrdemDeServico
     */
    public Number getFkOrdemDeServico() {
        return (Number)getAttributeInternal(FKORDEMDESERVICO);
    }

    /**
     * Sets <code>value</code> as the attribute value for FkOrdemDeServico.
     * @param value value to set the FkOrdemDeServico
     */
    public void setFkOrdemDeServico(Number value) {
        setAttributeInternal(FKORDEMDESERVICO, value);
    }

    /**
     * Gets the attribute value for KmChegada, using the alias name KmChegada.
     * @return the KmChegada
     */
    public Number getKmChegada() {
        return (Number)getAttributeInternal(KMCHEGADA);
    }

    /**
     * Sets <code>value</code> as the attribute value for KmChegada.
     * @param value value to set the KmChegada
     */
    public void setKmChegada(Number value) {
        setAttributeInternal(KMCHEGADA, value);
    }

    /**
     * Gets the attribute value for KmSaida, using the alias name KmSaida.
     * @return the KmSaida
     */
    public Number getKmSaida() {
        return (Number)getAttributeInternal(KMSAIDA);
    }

    /**
     * Sets <code>value</code> as the attribute value for KmSaida.
     * @param value value to set the KmSaida
     */
    public void setKmSaida(Number value) {
        setAttributeInternal(KMSAIDA, value);
    }

    /**
     * Gets the attribute value for TotalKmRodado, using the alias name TotalKmRodado.
     * @return the TotalKmRodado
     */
    public Number getTotalKmRodado() {
        return (Number)getAttributeInternal(TOTALKMRODADO);
    }

    /**
     * Sets <code>value</code> as the attribute value for TotalKmRodado.
     * @param value value to set the TotalKmRodado
     */
    public void setTotalKmRodado(Number value) {
        setAttributeInternal(TOTALKMRODADO, value);
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
     * @return the associated entity oracle.jbo.server.EntityImpl.
     */
    public EntityImpl getTbFranquia() {
        return (EntityImpl)getAttributeInternal(TBFRANQUIA);
    }

    /**
     * Sets <code>value</code> as the associated entity oracle.jbo.server.EntityImpl.
     */
    public void setTbFranquia(EntityImpl value) {
        setAttributeInternal(TBFRANQUIA, value);
    }

    /**
     * @return the associated entity TbOrdemDeServicoImpl.
     */
    public TbOrdemDeServicoImpl getTbOrdemDeServico() {
        return (TbOrdemDeServicoImpl)getAttributeInternal(TBORDEMDESERVICO);
    }

    /**
     * Sets <code>value</code> as the associated entity TbOrdemDeServicoImpl.
     */
    public void setTbOrdemDeServico(TbOrdemDeServicoImpl value) {
        setAttributeInternal(TBORDEMDESERVICO, value);
    }

    /**
     * @param idDiaria key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Number idDiaria) {
        return new Key(new Object[]{idDiaria});
    }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        if (mDefinitionObject == null) {
            mDefinitionObject = EntityDefImpl.findDefObject("eo.TbDiaria");
        }
        return mDefinitionObject;
    }
    
    protected void create(AttributeList attributeList) {
        super.create(attributeList);
        //Sequence
        ApplicationModule root = getDBTransaction().getRootApplicationModule();
        DBSequence seqid = new DBSequence("SEQ_TB_DIARIA", root);
        setIdDiaria(seqid.getSequenceNumber());
    }
}
