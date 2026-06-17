package domain;

import java.io.Serializable;

/**
 * Representa o plano de transporte de uma comitiva/selecao (Value Object).
 */
public class Viagem implements Serializable {
    private String origem;
    private String destino;
    private String dataPartida;
    private String dataChegada;
    private String meioTransporte; // Autocarro, Voo Charter, etc.

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
}
