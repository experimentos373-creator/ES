package domain;

import java.io.Serializable;

/**
 * Representa um evento ocorrido durante um jogo (Golo, Cartão, Substituição).
 */
public class EventoJogo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int minuto;
    private TipoEvento tipo; // GOLO, AUTO_GOLO, CARTAO_AMARELO, CARTAO_VERMELHO, SUBSTITUICAO
    private Jogador jogador;
    private Equipa equipa;

    public EventoJogo(int minuto, TipoEvento tipo, Jogador jogador, Equipa equipa) {
        this.minuto = minuto;
        this.tipo = tipo;
        this.jogador = jogador;
        this.equipa = equipa;
    }

    public int getMinuto() {
        return minuto;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public Jogador getJogador() {
        return jogador;
    }

    public Equipa getEquipa() {
        return equipa;
    }
}
