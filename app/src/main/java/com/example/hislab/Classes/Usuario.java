package com.example.hislab.Classes;

import com.google.firebase.database.Exclude;

public class Usuario {

    private String dsEmail;
    private String dsSenha;
    private String dsNome;

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
}
