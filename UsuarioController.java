package com.exemplo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private Cadastro cadastro;

    @Autowired
    private LogIn login;

    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrar(@RequestBody Usuario usuario) {
        cadastro.executar(usuario);
        return ResponseEntity.ok("Processo de cadastro executado. Verifique o console.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> logar(@RequestBody Usuario usuario) {
        login.executar(usuario.getPerfil(), usuario.getSenha());
        return ResponseEntity.ok("Processo de login executado. Verifique o console.");
    }
}
