
package com.mycompany.projetotocadordemusica;

import javax.swing.*;
import java.awt.BorderLayout;

class JanelaPlayIt extends JFrame {

    private String musicaTocandoAtual = "Nenhuma música";

    private JLabel textoMusicaTocando;
    private JButton botaoPlayPause;

    private JPanel painelCentral;
    private JPanel painelProximas;

    private boolean tocando = false;

    public JanelaPlayIt() {

        setTitle("PlayIt");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        textoMusicaTocando =
                new JLabel("Música: " + musicaTocandoAtual);

        botaoPlayPause = new JButton("Play");

        painelCentral = new JPanel();
        painelProximas = new JPanel();

        botaoPlayPause.addActionListener(evento -> {

            if (tocando) {
                tocando = false;
                botaoPlayPause.setText("Play");
            } else {
                tocando = true;
                botaoPlayPause.setText("Pause");
            }

        });

        painelCentral.add(textoMusicaTocando);
        painelCentral.add(botaoPlayPause);

        painelProximas.add(
                new JLabel("Próximas músicas")
        );

        add(painelCentral, BorderLayout.CENTER);
        add(painelProximas, BorderLayout.EAST);
    }

    public static void main(String[] args) {

        JanelaPlayIt janela = new JanelaPlayIt();

        janela.setVisible(true);
    }
}
