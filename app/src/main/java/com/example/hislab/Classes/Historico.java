package com.example.hislab.Classes;

import java.util.Date;

public class Historico {

    private String dsEmail;
    private String dtExame;
    private String idExame;
    private String idHistorico;
    private Double vlExame;
    private Double vlReferenciaInferior;
    private Double vlReferenciaSuperior;

    public String getDsEmail() {
        return dsEmail;
    }

    public void setDsEmail(String dsEmail) {
        this.dsEmail = dsEmail;
    }

    public String getDtExame() {
        return dtExame;
    }

    public void setDtExame(String dtExame) {
        this.dtExame = dtExame;
    }

    public String getIdExame() {
        return idExame;
    }

    public void setIdExame(String idExame) {
        this.idExame = idExame;
    }

    public String getIdHistorico() {
        return idHistorico;
    }

    public void setIdHistorico(String idHistorico) {
        this.idHistorico = idHistorico;
    }

    public Double getVlExame() {
        return vlExame;
    }

    public void setVlExame(Double vlExame) {
        this.vlExame = vlExame;
    }

    public Double getVlReferenciaInferior() {
        return vlReferenciaInferior;
    }

    public void setVlReferenciaInferior(Double vlReferenciaInferior) {
        this.vlReferenciaInferior = vlReferenciaInferior;
    }

    public Double getVlReferenciaSuperior() {
        return vlReferenciaSuperior;
    }

    public void setVlReferenciaSuperior(Double vlReferenciaSuperior) {
        this.vlReferenciaSuperior = vlReferenciaSuperior;
    }
}
