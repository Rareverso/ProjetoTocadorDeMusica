
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GerenciadorPastas {

    private final Path pastaPrincipal;

    // Construtor: define a pasta principal do aplicativo
    public GerenciadorPastas() throws IOException {

        pastaPrincipal = Paths.get(
            System.getProperty("user.home"),
            "MusicasPlayt"
        );

        // Cria a pasta principal caso ela não exista
        Files.createDirectories(pastaPrincipal);
    }

    // Retorna o caminho da pasta principal
    public Path getPastaPrincipal() {
        return pastaPrincipal;
    }

    // Cria uma nova pasta
    public Path criarPasta(String nome) throws IOException {

        Path novaPasta = pastaPrincipal.resolve(nome).normalize();

        if (!novaPasta.startsWith(pastaPrincipal)) {
            throw new IOException("Nome de pasta inválido.");
        }

        if (Files.exists(novaPasta)) {
            throw new IOException("Essa pasta já existe.");
        }

        return Files.createDirectory(novaPasta);
    }

    // Lista as subpastas da pasta principal
    public List<Path> listarPastas() throws IOException {

        List<Path> pastas = new ArrayList<>();

        try (Stream<Path> arquivos = Files.list(pastaPrincipal)) {

            arquivos.filter(Files::isDirectory)
                    .forEach(pastas::add);
        }

        return pastas;
    }

    // Cria uma subpasta dentro de outra pasta
    public Path criarSubpasta(
            Path pasta,
            String nomeSubpasta) throws IOException {

        verificarPasta(pasta);

        Path subpasta = pasta.resolve(nomeSubpasta).normalize();

        if (!subpasta.startsWith(pastaPrincipal)) {
            throw new IOException("Caminho inválido.");
        }

        return Files.createDirectories(subpasta);
    }

    // Verifica se uma pasta existe
    public boolean pastaExiste(String nome) {

        Path pasta = pastaPrincipal.resolve(nome).normalize();

        return pasta.startsWith(pastaPrincipal)
                && Files.isDirectory(pasta);
    }

    // Conta os arquivos dentro de uma pasta e suas subpastas
    public long contarArquivos(Path pasta) throws IOException {

        verificarPasta(pasta);

        try (Stream<Path> arquivos = Files.walk(pasta)) {

            return arquivos.filter(Files::isRegularFile)
                            .count();
        }
    }

    // Move um arquivo para outra pasta
    public void moverArquivo(
            Path arquivo,
            Path destino) throws IOException {

        verificarPasta(destino);

        if (!Files.isRegularFile(arquivo)) {
            throw new IOException("Arquivo não encontrado.");
        }

        Path novoCaminho = destino.resolve(
            arquivo.getFileName()
        );

        if (Files.exists(novoCaminho)) {
            throw new IOException(
                "Já existe um arquivo com esse nome no destino."
            );
        }

        Files.move(arquivo, novoCaminho);
    }

    // Renomeia uma pasta
    public Path renomearPasta(
            Path pasta,
            String novoNome) throws IOException {

        verificarPasta(pasta);

        Path novaPasta = pasta.resolveSibling(novoNome).normalize();

        if (!novaPasta.startsWith(pastaPrincipal)) {
            throw new IOException("Caminho inválido.");
        }

        if (Files.exists(novaPasta)) {
            throw new IOException("Já existe uma pasta com esse nome.");
        }

        return Files.move(pasta, novaPasta);
    }

    // Exclui uma pasta somente se estiver vazia
    public void excluirPastaVazia(Path pasta) throws IOException {

        verificarPasta(pasta);

        try (Stream<Path> arquivos = Files.list(pasta)) {

            if (arquivos.findAny().isPresent()) {
                throw new IOException(
                    "A pasta não está vazia."
                );
            }
        }

        Files.delete(pasta);
    }

    // Verifica se o caminho pertence à pasta principal
    private void verificarPasta(Path pasta) throws IOException {

        Path caminho = pasta.toAbsolutePath().normalize();

        if (!caminho.startsWith(pastaPrincipal)) {
            throw new IOException(
                "A pasta não pertence ao MusicasPlayt."
            );
        }

        if (!Files.isDirectory(caminho)) {
            throw new IOException("Pasta não encontrada.");
        }
    }
}


