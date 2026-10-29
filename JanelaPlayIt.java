package com.mycompany.projetotocadordemusica;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/*
 * PLAYIT - central de comando musical.
 * O JFrame é o prédio; os botões são a equipe tentando manter tudo aberto.
 */
public class JanelaPlayIt extends JFrame {

    private JLabel musicaAtual;
    private JLabel capaMusica;
    private JButton botaoPlayPause;
    private JButton botaoAnterior;
    private JButton botaoProxima;
    private JButton botaoAdicionar;

    private DefaultListModel<File> modeloMusicas;
    private JList<File> listaMusicas;

    private boolean tocando = false;

    /*
     * A ordem importa: primeiro criamos os componentes,
     * depois organizamos a janela e, por último, ligamos os eventos.
     * Nada de tentar usar uma lista fantasma!
     */
    public JanelaPlayIt() {
        setTitle("PlayIt");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        criarComponentes();
        organizarJanela();
        configurarEventos();
    }

    // Campos adicionados para a busca, as capas e os ícones.
    private File pastaMusicas;
    private File pastaCapas;
    private File pastaIcones;
    private File arquivoPreferenciasCapas;
    private javax.swing.JTextField campoBusca;
    private JButton botaoEscolherCapa;
    private JButton botaoLimparBusca;
    private JLabel contadorMusicas;
    private JLabel mensagemListaVazia;
    private JPanel painelConteudoLista;
    private CardLayout layoutConteudoLista;
    private java.util.ArrayList<File> todasAsMusicas = new java.util.ArrayList<>();
    private java.util.Properties capasPorMusica = new java.util.Properties();
    private ImageIcon capaPadrao;
    private ImageIcon iconePlay;
    private ImageIcon iconePause;
    private ImageIcon imagemCapaAtual;
    private JPanel painelCapa;

    // FASE 1: criar as peças antes de tentar usar qualquer uma.
    private void criarComponentes() {
        musicaAtual = new JLabel("Nenhuma música selecionada");
        capaMusica = new JLabel("Capa da música", SwingConstants.CENTER);

        botaoPlayPause = new JButton("Play");
        botaoAnterior = new JButton("Anterior");
        botaoProxima = new JButton("Próxima");
        botaoAdicionar = new JButton("Adicionar música");
        botaoEscolherCapa = new JButton("Escolher capa");
        botaoLimparBusca = new JButton("Limpar busca");
        campoBusca = new javax.swing.JTextField();
        contadorMusicas = new JLabel("0 músicas");

        pastaMusicas = new File(System.getProperty("user.home"), "MusicaPlayIt");
        File pastaOneDrive = obterPastaOneDrive();
        pastaCapas = new File(pastaOneDrive, "CapaMusicaPlayIt");
        pastaIcones = new File(pastaOneDrive, "PlayPausePlayIt");
        arquivoPreferenciasCapas = new File(pastaCapas, "capas.properties");
        pastaMusicas.mkdirs();
        pastaCapas.mkdirs();
        copiarCapaPadraoParaPasta();

        modeloMusicas = new DefaultListModel<>();
        listaMusicas = new JList<>(modeloMusicas);
        listaMusicas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        capaPadrao = carregarCapaPadrao();
        imagemCapaAtual = capaPadrao;
        capaMusica.setIcon(capaPadrao);
        capaMusica.setText("");

        carregarCapasSalvas();
        carregarMusicasDaPasta();
        carregarIconesPlayPause();
        atualizarBotaoPlayPause();
    }

    private File obterPastaOneDrive() {
        String caminho = System.getenv("OneDrive");
        if (caminho == null || caminho.trim().isEmpty()) {
            caminho = System.getenv("OneDriveConsumer");
        }
        if (caminho == null || caminho.trim().isEmpty()) {
            caminho = System.getenv("OneDriveCommercial");
        }
        if (caminho != null && !caminho.trim().isEmpty()) {
            File raizEncontrada = new File(caminho);
            if (new File(raizEncontrada, "CapaMusicaPlayIt").isDirectory()
                    || new File(raizEncontrada, "PlayPausePlayIt").isDirectory()) {
                return raizEncontrada;
            }
        }
        return new File(System.getProperty("user.home"), "OneDrive");
    }

    // FASE 2: cada componente ganha seu lugar na janela.
    private void organizarJanela() {
        setLayout(new BorderLayout(8, 8));

        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.add(musicaAtual, BorderLayout.CENTER);
        painelSuperior.add(contadorMusicas, BorderLayout.EAST);

        painelCapa = new JPanel(new BorderLayout());
        capaMusica.setHorizontalAlignment(SwingConstants.CENTER);
        painelCapa.add(capaMusica, BorderLayout.CENTER);
        painelCapa.add(botaoEscolherCapa, BorderLayout.SOUTH);
        painelCapa.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                ajustarCapaAoPainel();
            }
        });

        JPanel painelBusca = new JPanel(new BorderLayout(6, 0));
        painelBusca.add(new JLabel("Pesquisar música:"), BorderLayout.WEST);
        painelBusca.add(campoBusca, BorderLayout.CENTER);
        painelBusca.add(botaoLimparBusca, BorderLayout.EAST);

        JPanel painelLista = new JPanel(new BorderLayout(0, 6));
        painelLista.add(painelBusca, BorderLayout.NORTH);
        layoutConteudoLista = new CardLayout();
        painelConteudoLista = new JPanel(layoutConteudoLista);
        painelConteudoLista.add(new JScrollPane(listaMusicas), "lista");

        JPanel painelVazio = new JPanel(new GridBagLayout());
        JPanel mensagemVazia = new JPanel(new GridLayout(0, 1, 0, 10));
        mensagemListaVazia = new JLabel("Sua lista de músicas está vazia.", SwingConstants.CENTER);
        JButton botaoAdicionarVazio = new JButton("Adicionar a primeira música");
        botaoAdicionarVazio.addActionListener(e -> adicionarMusica());
        mensagemVazia.add(mensagemListaVazia);
        mensagemVazia.add(new JLabel("Escolha um arquivo de áudio para começar.",
                SwingConstants.CENTER));
        mensagemVazia.add(botaoAdicionarVazio);
        painelVazio.add(mensagemVazia);
        painelConteudoLista.add(painelVazio, "vazio");
        painelLista.add(painelConteudoLista, BorderLayout.CENTER);

        JSplitPane painelCentral = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, painelCapa, painelLista);
        painelCentral.setResizeWeight(0.38);
        painelCentral.setContinuousLayout(true);
        painelCentral.setOneTouchExpandable(true);

        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(botaoAnterior);
        painelBotoes.add(botaoPlayPause);
        painelBotoes.add(botaoProxima);
        painelBotoes.add(botaoAdicionar);

        add(painelSuperior, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
        atualizarEstadoLista();
    }

    /*
     * FASE 3: os cliques começam a fazer alguma coisa.
     * Até aqui, os botões eram praticamente enfeites de escritório.
     *
     * ◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢
     * ◤   AVISO: NÚCLEO DA JVM QUENTE   ◢
     * ◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢
     *
     *       (o_O)       [ MONITORANDO CLIQUES ]
     *      /|___|\      [ CPU: "EU NÃO PEDI ISSO" ]
     *       /   \       [ SWING NA FILA DE EVENTOS ]
     *
     * Diagnóstico: o botão foi clicado 47 vezes.
     * O usuário afirma que foi "só uma vez".
     */
    private void configurarEventos() {
        botaoPlayPause.addActionListener(e -> {
            if (listaMusicas.isSelectionEmpty()) {
                musicaAtual.setText("Selecione uma música primeiro");
                return;
            }

            tocando = !tocando;
            atualizarBotaoPlayPause();

            // TODO: conectar os métodos de áudio.
            // Por enquanto o botão só finge que é DJ. O som ainda não chegou.
            // TODO: conecte aqui os métodos de tocar e pausar do código de áudio.
        });

        botaoAdicionar.addActionListener(e -> adicionarMusica());
        botaoEscolherCapa.addActionListener(e -> escolherCapa());
        botaoLimparBusca.addActionListener(e -> {
            campoBusca.setText("");
            filtrarMusicas();
        });

        listaMusicas.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor,
                    int indice, boolean selecionado, boolean foco) {
                JLabel rotulo = (JLabel) super.getListCellRendererComponent(
                        lista, valor, indice, selecionado, foco);
                if (valor instanceof File) {
                    rotulo.setText(((File) valor).getName());
                }
                return rotulo;
            }
        });

        listaMusicas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                File selecionada = listaMusicas.getSelectedValue();
                if (selecionada != null) {
                    musicaAtual.setText(selecionada.getName());
                    mostrarCapaDaMusica(selecionada);
                }
            }
        });

        listaMusicas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && listaMusicas.getSelectedValue() != null) {
                    tocando = true;
                    atualizarBotaoPlayPause();
                    // TODO: inicie aqui a reprodução ao dar dois cliques na música.
                }
            }
        });

        capaMusica.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                escolherCapa();
            }
        });

        campoBusca.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filtrarMusicas();
            }
        });

        botaoProxima.addActionListener(e -> selecionarMusica(-1));
        botaoAnterior.addActionListener(e -> selecionarMusica(1));
    }

    // O seletor pergunta qual arquivo merece entrar na festa.
    private void adicionarMusica() {
        JFileChooser seletor = new JFileChooser(pastaMusicas);
        seletor.setDialogTitle("Escolha uma música");
        seletor.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Arquivos de música", "mp3", "wav", "ogg"));

        if (seletor.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File escolhida = seletor.getSelectedFile();
        if (!ehArquivoDeMusica(escolhida)) {
            JOptionPane.showMessageDialog(this,
                    "Escolha um arquivo MP3, WAV ou OGG.");
            return;
        }

        try {
            File musicaSalva = copiarMusicaParaPasta(escolhida);
            if (!todasAsMusicas.contains(musicaSalva)) {
                todasAsMusicas.add(musicaSalva);
            }
            filtrarMusicas();
            listaMusicas.setSelectedValue(musicaSalva, true);
        } catch (java.io.IOException erro) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível adicionar a música: " + erro.getMessage());
        }
    }

    private File copiarMusicaParaPasta(File escolhida) throws java.io.IOException {
        java.nio.file.Path origem = escolhida.toPath().toAbsolutePath().normalize();
        java.nio.file.Path destinoPasta = pastaMusicas.toPath().toAbsolutePath().normalize();

        if (origem.getParent().equals(destinoPasta)) {
            return escolhida;
        }

        String nome = escolhida.getName();
        int ponto = nome.lastIndexOf('.');
        String base = ponto > 0 ? nome.substring(0, ponto) : nome;
        String extensao = ponto > 0 ? nome.substring(ponto) : "";
        java.nio.file.Path destino = destinoPasta.resolve(nome);
        int numero = 2;

        while (java.nio.file.Files.exists(destino)) {
            destino = destinoPasta.resolve(base + " (" + numero + ")" + extensao);
            numero++;
        }

        java.nio.file.Files.copy(origem, destino);
        return destino.toFile();
    }

    private void carregarMusicasDaPasta() {
        File[] arquivos = pastaMusicas.listFiles();
        if (arquivos == null) {
            return;
        }

        for (File arquivo : arquivos) {
            if (arquivo.isFile() && ehArquivoDeMusica(arquivo)) {
                todasAsMusicas.add(arquivo);
                modeloMusicas.addElement(arquivo);
            }
        }
    }

    private boolean ehArquivoDeMusica(File arquivo) {
        String nome = arquivo.getName().toLowerCase();
        return nome.endsWith(".mp3")
                || nome.endsWith(".wav")
                || nome.endsWith(".ogg");
    }

    // Busca em tempo real: achou, fica; não achou, sai do radar.
    private void filtrarMusicas() {
        String busca = campoBusca.getText().trim();
        modeloMusicas.clear();

        for (File musica : todasAsMusicas) {
            if (musica.getName().contains(busca)) {
                modeloMusicas.addElement(musica);
            }
        }
        atualizarEstadoLista();
    }

    private void atualizarEstadoLista() {
        if (contadorMusicas != null && modeloMusicas != null) {
            contadorMusicas.setText(modeloMusicas.size() + " de "
                    + todasAsMusicas.size() + " músicas");
        }

        if (painelConteudoLista == null || modeloMusicas == null) {
            return;
        }

        if (modeloMusicas.isEmpty()) {
            if (mensagemListaVazia != null) {
                if (todasAsMusicas.isEmpty()) {
                    mensagemListaVazia.setText("Sua lista de músicas está vazia.");
                } else {
                    mensagemListaVazia.setText("Nenhuma música corresponde à busca.");
                }
            }
            layoutConteudoLista.show(painelConteudoLista, "vazio");
        } else {
            layoutConteudoLista.show(painelConteudoLista, "lista");
        }
    }

    private void selecionarMusica(int direcao) {
        if (modeloMusicas.isEmpty()) {
            return;
        }

        int indice = listaMusicas.getSelectedIndex();
        if (indice == -1) {
            indice = 0;
        } else {
            indice = (indice + direcao + modeloMusicas.size())
                    % modeloMusicas.size();
        }

        listaMusicas.setSelectedIndex(indice);
        listaMusicas.ensureIndexIsVisible(indice);
    }

    // Cada música pode ter sua identidade visual. RG musical, basicamente.
    private void escolherCapa() {
        File musicaSelecionada = listaMusicas.getSelectedValue();
        if (musicaSelecionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma música antes de escolher a capa.");
            return;
        }

        JFileChooser seletor = new JFileChooser(pastaCapas);
        seletor.setDialogTitle("Escolha uma capa para esta música");
        seletor.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imagens", "png", "jpg", "jpeg", "gif"));

        if (seletor.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File capaEscolhida = seletor.getSelectedFile();
        capasPorMusica.setProperty(
                musicaSelecionada.getAbsolutePath(), capaEscolhida.getAbsolutePath());
        salvarCapas();
        mostrarCapaDaMusica(musicaSelecionada);
    }

    private void carregarCapasSalvas() {
        if (!arquivoPreferenciasCapas.isFile()) {
            return;
        }

        try (java.io.FileInputStream entrada =
                     new java.io.FileInputStream(arquivoPreferenciasCapas)) {
            capasPorMusica.load(entrada);
        } catch (java.io.IOException erro) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível carregar as capas salvas.");
        }
    }

    // Salvando as capas para o próximo início. Memória de peixe? Aqui não.
    private void salvarCapas() {
        java.util.Properties arquivoParaSalvar = new java.util.Properties();

        for (String musica : capasPorMusica.stringPropertyNames()) {
            arquivoParaSalvar.setProperty(musica, capasPorMusica.getProperty(musica));
        }

        try (java.io.FileOutputStream saida =
                     new java.io.FileOutputStream(arquivoPreferenciasCapas)) {
            arquivoParaSalvar.store(saida, "Capas personalizadas do PlayIt");
        } catch (java.io.IOException erro) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível salvar a capa escolhida.");
        }
    }

    /*
     *          (o___o)     [ ENCONTREI 47 BUGS ]
     *         (  - -  )    [ RELATÓRIO EM ANDAMENTO ]
     *         /|  _  |\
     *        / | (_) | \
     *
     * Bug 1: a capa não carregou.
     * Bug 2: era só o caminho errado.
     * Bugs 3 a 47: a equipe ainda está investigando.
     */
    private void mostrarCapaDaMusica(File musica) {
        String caminho = capasPorMusica.getProperty(musica.getAbsolutePath());
        if (caminho == null) {
            imagemCapaAtual = capaPadrao;
            ajustarCapaAoPainel();
            return;
        }

        File arquivoCapa = new File(caminho);
        if (arquivoCapa.isFile()) {
            imagemCapaAtual = new ImageIcon(arquivoCapa.getAbsolutePath());
        } else {
            imagemCapaAtual = capaPadrao;
        }
        ajustarCapaAoPainel();
    }

    private void ajustarCapaAoPainel() {
        if (painelCapa == null || imagemCapaAtual == null) {
            return;
        }

        int largura = Math.max(1, painelCapa.getWidth() - 24);
        int altura = Math.max(1, painelCapa.getHeight() - 70);
        int lado = Math.max(1, Math.min(largura, altura));
        Image imagem = imagemCapaAtual.getImage().getScaledInstance(
                lado, lado, Image.SCALE_SMOOTH);
        capaMusica.setIcon(new ImageIcon(imagem));
    }

    /*
     *          (o_o)       [ DOCKER DESTRUÍDO ]
     *         /     \      [ NÃO ERA PRA TER SUBIDO ]
     *        //| | |\\
     *       /  | | |  \
     *
     * O ícone Play foi localizado.
     * O ícone Pause está em reunião com a infraestrutura.
     */
    private void carregarIconesPlayPause() {
        // Seus arquivos se chamam play.png.png e pause.png.png.
        File arquivoPlay = procurarImagem(pastaIcones, "play.png");
        File arquivoPause = procurarImagem(pastaIcones, "pause.png");

        // Também aceita nomes mais descritivos, caso você os renomeie.
        if (arquivoPlay == null) {
            arquivoPlay = procurarImagem(pastaIcones, "PlayPausePlayItPlay");
        }
        if (arquivoPause == null) {
            arquivoPause = procurarImagem(pastaIcones, "PlayPausePlayItPause");
        }

        iconePlay = carregarIconePlayPause(arquivoPlay, "/Play.png", true);
        iconePause = carregarIconePlayPause(arquivoPause, "/Pause.png", false);
    }

    private void atualizarBotaoPlayPause() {
        if (tocando) {
            botaoPlayPause.setText("Pause");
            botaoPlayPause.setIcon(iconePause);
        } else {
            botaoPlayPause.setText("Play");
            botaoPlayPause.setIcon(iconePlay);
        }
    }

    private ImageIcon carregarIconePlayPause(File arquivo, String recurso,
            boolean iconeDePlay) {
        java.net.URL imagemDoProjeto = getClass().getResource(recurso);
        if (imagemDoProjeto != null) {
            Image imagem = new ImageIcon(imagemDoProjeto).getImage()
                    .getScaledInstance(42, 42, Image.SCALE_SMOOTH);
            return new ImageIcon(imagem);
        }

        if (arquivo != null) {
            return criarIconeRedimensionado(arquivo, 42, 42);
        }

        return criarIconePlayPause(iconeDePlay);
    }

    private File procurarImagem(File pasta, String nomeSemExtensao) {
        String[] extensoes = {"png", "jpg", "jpeg", "gif"};
        for (String extensao : extensoes) {
            File imagem = new File(pasta,
                    nomeSemExtensao + "." + extensao);
            if (imagem.isFile()) {
                return imagem;
            }
        }
        return null;
    }

    private ImageIcon criarIconeRedimensionado(File arquivo, int largura, int altura) {
        Image imagem = new ImageIcon(arquivo.getAbsolutePath())
                .getImage().getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
        return new ImageIcon(imagem);
    }

    /*
     *       ,___,
     *      [ O.O ]      [ REVISÃO DE CAPAS ]
     *      /)___(\      [ OLHANDO O CAMINHO DO ARQUIVO ]
     *
     * A coruja perguntou se a imagem existe.
     * O arquivo respondeu: "depende de onde você me procura".
     */
    private void copiarCapaPadraoParaPasta() {
        File destino = new File(pastaCapas, "CapaPadraoPlayIt.png");
        if (destino.isFile()) {
            return;
        }

        try (java.io.InputStream imagem =
                     getClass().getResourceAsStream("/CapaPadraoPlayIt.png")) {
            if (imagem != null) {
                java.nio.file.Files.copy(imagem, destino.toPath());
            }
        } catch (java.io.IOException erro) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível copiar a capa padrão para a pasta de capas.");
        }
    }

    private ImageIcon carregarCapaPadrao() {
        File imagemNaPasta = new File(pastaCapas, "CapaPadraoPlayIt.png");
        if (imagemNaPasta.isFile()) {
            return new ImageIcon(imagemNaPasta.getAbsolutePath());
        }

        java.net.URL imagemDoProjeto = getClass().getResource("/CapaPadraoPlayIt.png");
        if (imagemDoProjeto != null) {
            return new ImageIcon(imagemDoProjeto);
        }

        return new ImageIcon();
    }

    private ImageIcon criarIconePlayPause(boolean iconeDePlay) {
        java.awt.image.BufferedImage imagem = new java.awt.image.BufferedImage(
                36, 36, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D desenho = imagem.createGraphics();
        desenho.setColor(Color.BLACK);

        if (iconeDePlay) {
            int[] x = {10, 28, 10};
            int[] y = {6, 18, 30};
            desenho.fillPolygon(x, y, 3);
        } else {
            desenho.fillRect(9, 7, 7, 22);
            desenho.fillRect(20, 7, 7, 22);
        }

        desenho.dispose();
        return new ImageIcon(imagem);
    }

    /*
     * PONTO DE PARTIDA:
     * A JVM chama o main e a interface nasce na fila de eventos do Swing.
     * Sem atropelar a EventQueue, por favor.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JanelaPlayIt janela = new JanelaPlayIt();
            janela.setExtendedState(JFrame.MAXIMIZED_BOTH);
            janela.setVisible(true);
        });
    }
}
