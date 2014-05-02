package br.com.locCar.bean.ordemDeServico;

public class OsTO {
    public OsTO() {
        super();
    }
    private Number idCli;
    private String nomeCli;
    private String docCli;
    
    private Number idCar;
    private String modeloCar;
    private String placaCar;
    private String anoCar;
    
    private Number idMot;
    private String nomeMot;
    private String catHab;
    private String telefoneMot;


    public void setNomeCli(String nomeCli) {
        this.nomeCli = nomeCli;
    }

    public String getNomeCli() {
        return nomeCli;
    }

    public void setDocCli(String docCli) {
        this.docCli = docCli;
    }

    public String getDocCli() {
        return docCli;
    }

    public void setModeloCar(String modeloCar) {
        this.modeloCar = modeloCar;
    }

    public String getModeloCar() {
        return modeloCar;
    }

    public void setPlacaCar(String placaCar) {
        this.placaCar = placaCar;
    }

    public String getPlacaCar() {
        return placaCar;
    }

    public void setAnoCar(String anoCar) {
        this.anoCar = anoCar;
    }

    public String getAnoCar() {
        return anoCar;
    }

    public void setNomeMot(String nomeMot) {
        this.nomeMot = nomeMot;
    }

    public String getNomeMot() {
        return nomeMot;
    }

    public void setCatHab(String catHab) {
        this.catHab = catHab;
    }

    public String getCatHab() {
        return catHab;
    }

    public void setTelefoneMot(String telefoneMot) {
        this.telefoneMot = telefoneMot;
    }

    public String getTelefoneMot() {
        return telefoneMot;
    }

    public void setIdCli(Number idCli) {
        this.idCli = idCli;
    }

    public Number getIdCli() {
        return idCli;
    }

    public void setIdCar(Number idCar) {
        this.idCar = idCar;
    }

    public Number getIdCar() {
        return idCar;
    }

    public void setIdMot(Number idMot) {
        this.idMot = idMot;
    }

    public Number getIdMot() {
        return idMot;
    }
}
