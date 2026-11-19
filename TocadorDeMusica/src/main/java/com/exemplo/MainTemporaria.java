package com.exemplo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Scanner;

@SpringBootApplication
public class MainTemporaria {

    public static void main(String[] args) {
        SpringApplication.run(MainTemporaria.class, args);
    }

    @Bean
public CommandLineRunner menuConsole(Cadastro cadastro, LogIn login) {
    return args -> {
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n=== MENU DE TESTE ===");
            System.out.println("1 - Cadastrar Usuário");
            System.out.println("2 - Fazer Login");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o Enter da memória

            // O switch analisa diretamente o valor de 'opcao'
            switch (opcao) {
                case 1 -> {
                    System.out.print("Digite o novo perfil: ");
                    String perfil = scanner.nextLine();
                    System.out.print("Digite a nova senha: ");
                    String senha = scanner.nextLine();

                    cadastro.executar(new Usuario(perfil, senha));
                }
                case 2 -> {
                    System.out.print("Digite o perfil: ");
                    String perfil = scanner.nextLine();
                    System.out.print("Digite a senha: ");
                    String senha = scanner.nextLine();

                    login.executar(perfil, senha);
                }
                case 0 -> System.out.println("Encerrando testes...");
                default -> System.out.println("Opção inválida! Digite 1, 2 ou 0.");
            }
        }
        
        scanner.close();
    };
}
}