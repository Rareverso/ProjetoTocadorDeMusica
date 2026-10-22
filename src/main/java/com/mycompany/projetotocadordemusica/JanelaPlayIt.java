package com.mycompany.projetotocadordemusica;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JanelaPlayIt extends JFrame {

    private static final Color FUNDO = new Color(18, 19, 23);
    private static final Color PAINEL = new Color(30, 31, 37);
    private static final Color VINHO = new Color(94, 38, 51);
    private static final Color TEXTO = new Color(242, 242, 245);

    private final Path pastaMusicas = Path.of(
            System.getProperty("user.home"), "MusicaPlayIt");
    private final DefaultListModel<File> modeloMusicas =
            new DefaultListModel<>();
    private final JList<File> listaMusicas = new JList<>(modeloMusicas);
    private final Map<String, File> capasEscolhidas = new HashMap<>();

    private JLabel textoMusicaTocando;
    private JLabel textoEstado;
    private JLabel imagemCapa;
    private JButton botaoPlayPause;
    private JButton botaoAnterior;
    private JButton botaoProxima;
    private boolean tocando = false;

    public JanelaPlayIt() {
        super("PlayIt");

        setSize(1050, 640);
        setMinimumSize(new Dimension(760, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        prepararPastaDeMusicas();
        montarInterface();
        carregarMusicasDaPasta();
    }

    private void prepararPastaDeMusicas() {
        try {
            Files.createDirectories(pastaMusicas);
        } catch (IOException erro) {
            JOptionPane.showMessageDialog(
                    this, "Erro ao criar MusicaPlayIt: " + erro.getMessage());
        }
    }

    private void montarInterface() {
        JPanel raiz = new JPanel(new BorderLayout(14, 14));
        raiz.setBackground(FUNDO);
        raiz.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(raiz);

        raiz.add(criarCabecalho(), BorderLayout.NORTH);
        raiz.add(criarPainelPrincipal(), BorderLayout.CENTER);
        raiz.add(criarPainelDeBotoes(), BorderLayout.SOUTH);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);

        JLabel nome = new JLabel("♫  PLAYIT");
        nome.setForeground(TEXTO);
        nome.setFont(new Font("Segoe UI", Font.BOLD, 27));

        JButton adicionar = criarBotao("＋  Adicionar músicas");
        adicionar.addActionListener(evento -> adicionarMusicas());

        cabecalho.add(nome, BorderLayout.WEST);
        cabecalho.add(adicionar, BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel criarPainelPrincipal() {
        JPanel principal = new JPanel(new GridLayout(1, 2, 16, 0));
        principal.setOpaque(false);
        principal.add(criarPainelDaCapa());
        principal.add(criarPainelDaLista());
        return principal;
    }

    private JPanel criarPainelDaCapa() {
        JPanel painel = criarCartao();
        painel.setLayout(new BorderLayout(10, 12));

        JLabel titulo = new JLabel("TOCANDO AGORA");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        painel.add(titulo, BorderLayout.NORTH);

        imagemCapa = new JLabel("♫", SwingConstants.CENTER);
        imagemCapa.setOpaque(true);
        imagemCapa.setBackground(new Color(40, 41, 48));
        imagemCapa.setForeground(VINHO);
        imagemCapa.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 100));
        imagemCapa.setBorder(BorderFactory.createLineBorder(new Color(55, 56, 64)));
        painel.add(imagemCapa, BorderLayout.CENTER);

        textoMusicaTocando = new JLabel("Nenhuma música selecionada");
        textoMusicaTocando.setForeground(TEXTO);
        textoMusicaTocando.setFont(new Font("Segoe UI", Font.BOLD, 17));

        textoEstado = new JLabel("Adicione uma faixa à sua biblioteca");
        textoEstado.setForeground(new Color(173, 174, 183));
        textoEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel informacoes = new JPanel(new GridLayout(2, 1, 0, 5));
        informacoes.setOpaque(false);
        informacoes.add(textoMusicaTocando);
        informacoes.add(textoEstado);

        JButton escolherCapa = criarBotao("▧  Escolher capa");
        escolherCapa.addActionListener(evento -> escolherCapa());

        JPanel rodape = new JPanel(new BorderLayout(8, 0));
        rodape.setOpaque(false);
        rodape.add(informacoes, BorderLayout.CENTER);
        rodape.add(escolherCapa, BorderLayout.SOUTH);
        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelDaLista() {
        JPanel painel = criarCartao();
        painel.setLayout(new BorderLayout(8, 10));

        JLabel titulo = new JLabel("☷  MINHA BIBLIOTECA");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        painel.add(titulo, BorderLayout.NORTH);

        listaMusicas.setBackground(PAINEL);
        listaMusicas.setForeground(TEXTO);
        listaMusicas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaMusicas.setSelectionBackground(new Color(77, 39, 50));
        listaMusicas.setFixedCellHeight(42);
        listaMusicas.setCellRenderer(new RenderizadorDeMusicas());
        listaMusicas.addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) {
                atualizarFaixaSelecionada();
            }
        });
        listaMusicas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evento) {
                if (evento.getClickCount() == 2) {
                    alternarPlayPause();
                }
            }
        });

        JScrollPane rolagem = new JScrollPane(listaMusicas);
        rolagem.setBorder(BorderFactory.createLineBorder(new Color(50, 51, 58)));
        rolagem.getViewport().setBackground(PAINEL);
        painel.add(rolagem, BorderLayout.CENTER);

        JLabel dica = new JLabel("A lista começa vazia; adicione seus arquivos.");
        dica.setForeground(new Color(165, 166, 174));
        dica.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        painel.add(dica, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelDeBotoes() {
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botoes.setOpaque(false);

        botaoAnterior = criarBotao("|◀  Anterior");
        botaoPlayPause = criarBotao("▶  Play");
        botaoProxima = criarBotao("Próxima  ▶|");

        botaoAnterior.addActionListener(evento -> mudarFaixa(-1));
        botaoPlayPause.addActionListener(evento -> alternarPlayPause());
        botaoProxima.addActionListener(evento -> mudarFaixa(1));

        botoes.add(botaoAnterior);
        botoes.add(botaoPlayPause);
        botoes.add(botaoProxima);
        return botoes;
    }

    private JPanel criarCartao() {
        JPanel painel = new JPanel();
        painel.setBackground(PAINEL);
        painel.setBorder(new EmptyBorder(16, 16, 16, 16));
        return painel;
    }

    private JButton criarBotao(String nome) {
        JButton botao = new JButton(nome);
        botao.setForeground(TEXTO);
        botao.setBackground(VINHO);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 13, 10, 13));
        return botao;
    }

    private void adicionarMusicas() {
        JFileChooser seletor = new JFileChooser();
        seletor.setMultiSelectionEnabled(true);
        seletor.setFileFilter(new FileNameExtensionFilter(
                "Arquivos WAV ou MP3", "wav", "mp3"));

        int resultado = seletor.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        for (File arquivo : seletor.getSelectedFiles()) {
            copiarMusicaParaBiblioteca(arquivo);
        }
        carregarMusicasDaPasta();
    }

    private void copiarMusicaParaBiblioteca(File arquivo) {
        Path origem = arquivo.toPath().toAbsolutePath().normalize();
        Path destino = pastaMusicas.resolve(arquivo.getName());

        if (!ehArquivoDeAudio(origem)) {
            return;
        }

        try {
            if (!origem.equals(destino) && !Files.exists(destino)) {
                Files.copy(origem, destino, StandardCopyOption.COPY_ATTRIBUTES);
            }
        } catch (IOException erro) {
            JOptionPane.showMessageDialog(this,
                    "Não consegui copiar o arquivo: " + erro.getMessage());
        }
    }

    private void carregarMusicasDaPasta() {
        modeloMusicas.clear();

        try (Stream<Path> arquivos = Files.list(pastaMusicas)) {
            arquivos.filter(Files::isRegularFile)
                    .filter(this::ehArquivoDeAudio)
                    .sorted(Comparator.comparing(caminho ->
                            caminho.getFileName().toString().toLowerCase(Locale.ROOT)))
                    .forEach(caminho -> modeloMusicas.addElement(caminho.toFile()));
        } catch (IOException erro) {
            textoEstado.setText("Não foi possível ler MusicaPlayIt");
        }

        atualizarBotoes();
        if (modeloMusicas.isEmpty()) {
            textoMusicaTocando.setText("Nenhuma música selecionada");
            textoEstado.setText("Adicione uma faixa à sua biblioteca");
            mostrarCapaPadrao();
        }
    }

    private boolean ehArquivoDeAudio(Path caminho) {
        String nome = caminho.getFileName().toString().toLowerCase(Locale.ROOT);
        return nome.endsWith(".wav") || nome.endsWith(".mp3");
    }

    private void atualizarFaixaSelecionada() {
        File selecionada = listaMusicas.getSelectedValue();
        if (selecionada == null) {
            atualizarBotoes();
            return;
        }

        tocando = false;
        botaoPlayPause.setText("▶  Play");
        textoMusicaTocando.setText(selecionada.getName());
        textoEstado.setText("Faixa selecionada");
        mostrarCapaDaFaixa(selecionada);
        atualizarBotoes();
    }

    private void alternarPlayPause() {
        if (listaMusicas.getSelectedValue() == null) {
            textoEstado.setText("Selecione uma faixa primeiro");
            return;
        }

        tocando = !tocando;
        botaoPlayPause.setText(tocando ? "Ⅱ  Pause" : "▶  Play");
        textoEstado.setText(tocando
                ? "Estado de exemplo: tocando"
                : "Estado de exemplo: pausado");

        // Próximo passo: conectar este botão a um Clip de áudio WAV.
    }

    private void mudarFaixa(int direcao) {
        if (modeloMusicas.isEmpty()) {
            return;
        }

        int atual = listaMusicas.getSelectedIndex();
        int proxima = atual < 0
                ? 0
                : Math.floorMod(atual + direcao, modeloMusicas.size());

        listaMusicas.setSelectedIndex(proxima);
        listaMusicas.ensureIndexIsVisible(proxima);
    }

    private void escolherCapa() {
        File musica = listaMusicas.getSelectedValue();
        if (musica == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma faixa antes de escolher a capa.");
            return;
        }

        JFileChooser seletor = new JFileChooser();
        seletor.setFileFilter(new FileNameExtensionFilter(
                "Imagens PNG, JPG ou GIF", "png", "jpg", "jpeg", "gif"));

        if (seletor.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            capasEscolhidas.put(musica.getAbsolutePath(), seletor.getSelectedFile());
            mostrarCapaDaFaixa(musica);
        }
    }

    private void mostrarCapaDaFaixa(File musica) {
        File capa = capasEscolhidas.get(musica.getAbsolutePath());
        if (capa == null || !capa.isFile()) {
            mostrarCapaPadrao();
            return;
        }

        ImageIcon original = new ImageIcon(capa.getAbsolutePath());
        Image imagem = original.getImage().getScaledInstance(
                300, 300, Image.SCALE_SMOOTH);
        imagemCapa.setText("");
        imagemCapa.setIcon(new ImageIcon(imagem));
    }

    private void mostrarCapaPadrao() {
        imagemCapa.setIcon(null);
        imagemCapa.setText("♫");
    }

    private void atualizarBotoes() {
        boolean temMusicas = !modeloMusicas.isEmpty();
        botaoAnterior.setEnabled(temMusicas);
        botaoProxima.setEnabled(temMusicas);
        botaoPlayPause.setEnabled(listaMusicas.getSelectedValue() != null);
    }

    private static class RenderizadorDeMusicas extends DefaultListCellRenderer {
        @Override
        public java.awt.Component getListCellRendererComponent(
                JList<?> lista, Object valor, int indice,
                boolean selecionada, boolean foco) {
            super.getListCellRendererComponent(
                    lista, valor, indice, selecionada, foco);

            if (valor instanceof File arquivo) {
                setText("  ♫    " + arquivo.getName());
            }
            setBorder(BorderFactory.createEmptyBorder(7, 8, 7, 8));
            return this;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception erro) {
            // O tema padrão do Swing continua disponível.
        }

        SwingUtilities.invokeLater(() -> {
            JanelaPlayIt janela = new JanelaPlayIt();
            janela.setVisible(true);
        });
    }
}
