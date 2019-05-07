package com.example.hislab.Classes;

import com.google.firebase.database.Exclude;

import java.util.Date;

public class Usuario {

    private String dsEmail;
    private String dsSenha;
    private String dsNome;
    private String tpSexo;
    private String dtNascimento;

    public String getDsEmail() {
        return dsEmail;
    }

    public void setDsEmail(String dsEmail) {
        this.dsEmail = dsEmail;
    }

    @Exclude
    public String getDsSenha() {
        return dsSenha;
    }

    @Exclude
    public void setDsSenha(String dsSenha) {
        this.dsSenha = dsSenha;
    }

    public String getDsNome() {
        return dsNome;
    }

    public void setDsNome(String dsNome) {
        this.dsNome = dsNome;
    }

    public String getTpSexo() {
        return tpSexo;
    }

    public void setTpSexo(String tpSexo) {
        this.tpSexo = tpSexo;
    }

    public String getDtNascimento() {
        return dtNascimento;
    }

    public void setDtNascimento(String dtNascimento) {
        this.dtNascimento = dtNascimento;
    }
}
