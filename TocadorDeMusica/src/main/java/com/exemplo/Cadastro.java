package com.exemplo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Cadastro {

    @Autowired
    private UsuarioRepository repo; // O Spring coloca o repo pronto aqui dentro

    public void executar(Usuario novoUsuario) {
        // 1. Usando um método pronto do Repo para checar se o ID já existe
        if (repo.existsById(novoUsuario.getPerfil())) {
            System.out.println("Erro: Esse perfil já existe!");
        } else {
            // 2. Usando um método pronto do Repo para dar "INSERT" no banco
            repo.save(novoUsuario); 
            System.out.println("Usuário salvo com sucesso!");
        }
    }
}
