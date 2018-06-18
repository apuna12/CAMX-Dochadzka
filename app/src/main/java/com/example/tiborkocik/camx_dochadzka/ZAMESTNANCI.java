package com.example.tiborkocik.camx_dochadzka;

public class ZAMESTNANCI {
    private String meno;
    private String prichod;
    private String odchod_na_obed;
    private String prichod_z_obeda;
    private String odchod;
    private String poznamka;

    public ZAMESTNANCI(String meno, String prichod, String odchod_na_obed, String prichod_z_obeda, String odchod, String poznamka)
    {

        this.setMeno(meno);
        this.setPrichod(prichod);
        this.setOdchod_na_obed(odchod_na_obed);
        this.setPrichod_z_obeda(prichod_z_obeda);
        this.setOdchod(odchod);
        this.setPoznamka(poznamka);
    }

    public String getMeno() {
        return meno;
    }

    public void setMeno(String meno) {
        this.meno = meno;
    }

    public String getPrichod() {
        return prichod;
    }

    public void setPrichod(String prichod) {
        this.prichod = prichod;
    }

    public String getOdchod_na_obed() {
        return odchod_na_obed;
    }

    public void setOdchod_na_obed(String odchod_na_obed) {
        this.odchod_na_obed = odchod_na_obed;
    }

    public String getPrichod_z_obeda() {
        return prichod_z_obeda;
    }

    public void setPrichod_z_obeda(String prichod_z_obeda) {
        this.prichod_z_obeda = prichod_z_obeda;
    }

    public String getOdchod() {
        return odchod;
    }

    public void setOdchod(String odchod) {
        this.odchod = odchod;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }
}
