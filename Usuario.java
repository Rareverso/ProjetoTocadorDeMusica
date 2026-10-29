import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Usuario {
    String Perfil;
    String Senha;
    
    List<Usuario> BancoDeDadosUsuarios = new ArrayList<>();
    
    public Usuario(String Perfil, String Senha){
    this.Perfil = Perfil;
    this.Senha = Senha;
            }
    
    public String getPerfil() { return this.Perfil; }
    public String getSenha() { return this.Senha; }

    
       public void logIn(){
        if (BancoDeDadosUsuarios.stream().anyMatch(u -> u.Perfil.equals(this.Perfil))) {
            System.out.println("ERRO: Usuário já cadastrado com este nome!");
        } else {
            System.out.println("Perfil Criado com sucesso.");
        }
    }
}