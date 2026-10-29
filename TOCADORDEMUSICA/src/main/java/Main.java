
public class Main {
      public static void main(String[] args) {

        BibliotecaMusical biblioteca = new BibliotecaMusical();

        biblioteca.carregarPasta("C:/Musicas");

        biblioteca.listarMusicas();

        System.out.println(
            "Total de músicas: "
            + biblioteca.quantidadeMusicas()
        );
    }  
} 

