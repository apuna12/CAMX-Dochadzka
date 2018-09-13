package com.example.tiborkocik.camx_dochadzka;

public class ZOZNAM_ZAMESTNANCOV {
    private String meno;
    private String priezvisko;
    private String tel_cislo;

    public ZOZNAM_ZAMESTNANCOV(String meno, String tel_cislo)
    {

        this.setMeno(meno);
        this.setCislo(tel_cislo);
    }

    public String getMeno() { return meno; }

    public void setMeno(String meno) {
        this.meno = meno;
    }

    public String getCislo() {
        return tel_cislo;
    }

    public void setCislo(String cislo) {
        this.tel_cislo = cislo;
    }

}
