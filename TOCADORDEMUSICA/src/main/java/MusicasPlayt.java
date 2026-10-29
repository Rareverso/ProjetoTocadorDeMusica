import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat;

public class MusicasPlayt {
   public static File criarPastaMusicas() throws IOException {

    File pasta = new File(
        System.getProperty("user.home"),
        "MusicasPlayt"
    );

    if (!pasta.exists() && !pasta.mkdirs()) {
        System.out.println("Erro ao criar a pasta.");
        return null;
    }

    criarMusicaTeste(pasta, "musica_teste_1.wav", 440);
    criarMusicaTeste(pasta, "musica_teste_2.wav", 660);

    System.out.println("Pasta de músicas: " + pasta.getAbsolutePath());

    return pasta;
}

public static void criarMusicaTeste(
        File pasta, String nome, int frequencia) throws IOException {

    File arquivo = new File(pasta, nome);

    if (arquivo.exists()) {
        return;
    }

    float taxaAmostragem = 44100;
    int duracao = 2;
    int totalAmostras = (int) (taxaAmostragem * duracao);

    byte[] dados = new byte[totalAmostras * 2];

    for (int i = 0; i < totalAmostras; i++) {
        double tempo = i / taxaAmostragem;

        short amostra = (short) (
            Math.sin(2 * Math.PI * frequencia * tempo) * 12000
        );

        dados[i * 2] = (byte) (amostra & 0xff);
        dados[i * 2 + 1] = (byte) ((amostra >> 8) & 0xff);
    }

    AudioFormat formato = new AudioFormat(
        taxaAmostragem, 16, 1, true, false
    );

    try (
        ByteArrayInputStream entrada = new ByteArrayInputStream(dados);
        AudioInputStream audio = new AudioInputStream(
            entrada, formato, totalAmostras
        )
    ) {
        AudioSystem.write(
            audio,
            AudioFileFormat.Type.WAVE,
            arquivo
        );

        System.out.println("Arquivo criado: " + nome);


        }
    }
}