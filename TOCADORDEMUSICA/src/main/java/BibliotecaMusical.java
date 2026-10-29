import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.Desktop;
import java.io.IOException;

public class BibliotecaMusical {

    private List<Musica> musicas;

    public BibliotecaMusical() {
        musicas = new ArrayList<>();
    }

    public List<Musica> carregarPasta(String pasta) {
        musicas.clear();

        File diretorio = new File(pasta);

        if (!diretorio.isDirectory()) {
            System.out.println("Erro: pasta não encontrada.");
            return musicas;
        }

        procurarMusicas(diretorio);

        return musicas;
    }

    private void procurarMusicas(File pasta) {
        File[] arquivos = pasta.listFiles();

        if (arquivos == null) {
            return;
        }

        for (File arquivo : arquivos) {

            if (arquivo.isDirectory()) {
                procurarMusicas(arquivo);
            } else {
                String nome = arquivo.getName().toLowerCase();

                if (nome.endsWith(".mp3")
                        || nome.endsWith(".wav")) {

                    Musica musica = new Musica(
                        arquivo.getName(),
                        arquivo.getAbsolutePath(),
                        nome.substring(nome.lastIndexOf('.') + 1)
                    );

                    musicas.add(musica);
                }
            }
        }
    }

    public void listarMusicas() {
        if (musicas.isEmpty()) {
            System.out.println("Nenhuma música encontrada.");
            return;
        }

        for (Musica musica : musicas) {
            System.out.println("Nome: " + musica.getNome());
            System.out.println("Caminho: " + musica.getCaminho());
            System.out.println("Formato: " + musica.getFormato());
            System.out.println("------------------------------");
        }
    }

    public int quantidadeMusicas() {
        return musicas.size();
    }

    public List<Musica> getMusicas() {
        return new ArrayList<>(musicas);
    }
    

public void reproduzirMusica(Musica musica) {
    if (musica == null) return;
    try {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(musica.getCaminho()));
        } else {
            System.out.println("Erro: Sistema não suporta a abertura automática de arquivos.");
        }
    } catch (IOException e) {
        System.out.println("Erro ao tentar tocar a música: " + e.getMessage());
    }
}
public List<Musica> buscarPorNome(String termoBusca) {
    List<Musica> resultados = new ArrayList<>();
    for (Musica musica : musicas) {
        if (musica.getNome().toLowerCase().contains(termoBusca.toLowerCase())) {
            resultados.add(musica);
        }
    }
    return resultados;
}

}
