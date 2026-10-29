import java.util.ArrayList;
import java.util.List;
class ListaReproducao {


public class FilaReproducao {
    private List<Musica> musicas = new ArrayList<>();
    
    private int indiceAtual = 0;

    public void definirMusicas(List<Musica> novaLista) {
        this.musicas = novaLista;
        this.indiceAtual = 0; // Começa da primeira música
    }

    public Musica getMusicaAtual() {
        if (musicas == null || musicas.isEmpty()) {
            return null; // Se não tiver música na pasta, não toca nada
        }
        return musicas.get(indiceAtual);
    }

    public void obterProxima() {
        if (!musicas.isEmpty()) {
            indiceAtual = (indiceAtual + 1) % musicas.size();
        }
    }

    public void obterAnterior() {
        if (!musicas.isEmpty()) {
            indiceAtual = (indiceAtual - 1 + musicas.size()) % musicas.size();
        }
    }
}

}
