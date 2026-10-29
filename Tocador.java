import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Tocador {
    private ListaReproducao lista;
    private Clip clip;

    public Tocador(ListaReproducao lista) {
        this.lista = lista;
    }

    public void carregarMusicasDaPasta(String caminho) {
        File pasta = new File(caminho);
        if (pasta.exists() && pasta.isDirectory()) {
            for (File arquivo : pasta.listFiles()) {
                if (arquivo.getName().toLowerCase().endsWith(".wav")) {
                    Musica m = new Musica(arquivo.getName(), "Artista", arquivo.getAbsolutePath());
                    this.lista.adicionarMusica(m);
                }
            }
        }
    }

    public void play() {
        Musica atual = lista.getMusicaAtual();
        if (atual == null) return;
        try {
            if (clip == null) {
                clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(atual.getArquivoAudio()));
            }
            clip.start();
        } catch (Exception e) {
            System.out.println("Erro ao tocar.");
        }
    }

    public void pause() {
        if (clip != null) clip.stop();
    }
    
    private void fechar() {
        if (clip != null) {
            clip.close();
            clip = null;
        }
    }
}
