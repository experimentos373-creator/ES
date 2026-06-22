package domain;

import java.io.Serializable;

/**
 * Representa o plano de transporte de uma comitiva/selecao (Value Object).
 */
public class Viagem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String origem;
    private String destino;
    private String dataPartida;
    private String dataChegada;
    private String meioTransporte; // Autocarro, Voo Charter, etc.
    private Jogo jogo;             // Jogo associado
    private Equipa equipa;         // Equipa associada

    public Viagem(String origem, String destino, String dataPartida, String dataChegada, String meioTransporte) {
        this.origem = origem;
        this.destino = destino;
        this.dataPartida = dataPartida;
        this.dataChegada = dataChegada;
        this.meioTransporte = meioTransporte;
    }

    public String getOrigem() {
        return origem;
    }

    public String getDestino() {
        return destino;
    }

    public String getDataPartida() {
        return dataPartida;
    }

    public String getDataChegada() {
        return dataChegada;
    }

    public String getMeioTransporte() {
        return meioTransporte;
    }

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    public Equipa getEquipa() {
        return equipa;
    }

    public void setEquipa(Equipa equipa) {
        this.equipa = equipa;
    }
}
