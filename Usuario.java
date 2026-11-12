package com.exemplo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Usuario {

    @Id
    private String perfil;
    private String senha;

    public Usuario() {}

    public Usuario(String perfil, String senha) {
        this.perfil = perfil;
        this.senha = senha;
    }
    public void setPerfil(String perfil) { this.perfil = perfil; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getPerfil() { return perfil; }
    public String getSenha() { return senha; }
}
