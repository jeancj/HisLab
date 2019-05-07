package com.example.hislab.Classes;

import java.util.Date;

public class HistoricoExame {

    private Exame exame;
    private Date dtExame;
    private Double vlExame;

    public Exame getExame() {
        return exame;
    }

    public void setExame(Exame exame) {
        this.exame = exame;
    }

    public Date getDtExame() {
        return dtExame;
    }

    public void setDtExame(Date dtExame) {
        this.dtExame = dtExame;
    }

    public Double getVlExame() {
        return vlExame;
    }

    public void setVlExame(Double vlExame) {
        this.vlExame = vlExame;
    }
}
