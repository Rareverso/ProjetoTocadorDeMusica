package com.exemplo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class LogIn {

    @Autowired
    private UsuarioRepository repo;

    public void executar(String perfilDigitado, String senhaDigitada) {
        Optional<Usuario> resultado = repo.findByPerfil(perfilDigitado);

        if (resultado.isPresent() && resultado.get().getSenha().equals(senhaDigitada)) {
            System.out.println("Log in Executado!");
        } else {
            System.out.println("ERRO: Usuário ou senha incorretos.");
        }
    }
}
