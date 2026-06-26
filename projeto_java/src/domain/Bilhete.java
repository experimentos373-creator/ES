package domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa um Bilhete vendido para um jogo específico.
 */
public class Bilhete implements Serializable {
    private static final long serialVersionUID = 1L;

    private int jogoId;
    private String setor;
    private double preco;

    public Bilhete(int jogoId, String setor, double preco) {
        this.jogoId = jogoId;
        this.setor = setor;
        this.preco = preco;
    }

    public int getJogoId() {
        return jogoId;
    }

    public String getSetor() {
        return setor;
    }

    public double getPreco() {
        return preco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bilhete bilhete = (Bilhete) o;
        return jogoId == bilhete.jogoId && Objects.equals(setor, bilhete.setor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jogoId, setor);
    }
}
