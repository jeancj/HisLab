package com.example.hislab.Classes;

import java.util.Date;

public class Perfil {

    private int idPerfil;
    private String dsNomePerfil;
    private Date dtNascimento;
    private String tpSexo;

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getDsNomePerfil() {
        return dsNomePerfil;
    }

    public void setDsNomePerfil(String dsNomePerfil) {
        this.dsNomePerfil = dsNomePerfil;
    }

    public Date getDtNascimento() {
        return dtNascimento;
    }

    public void setDtNascimento(Date dtNascimento) {
        this.dtNascimento = dtNascimento;
    }

    public String getTpSexo() {
        return tpSexo;
    }

    public void setTpSexo(String tpSexo) {
        this.tpSexo = tpSexo;
    }
}
