package domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa um Árbitro do Campeonato do Mundo.
 */
public class Arbitro implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String email;
    private String nome;
    private String nacionalidade;
    private TipoArbitro tipo;    // PRINCIPAL, ASSISTENTE, VAR, QUARTO
    private EstadoArbitro estado;  // ATIVO, DESCANSO, INATIVO
    private int scoreFIFA;       // Classificação acumulada de 1 a 100
    private int totalAvaliacoes;

    public Arbitro(int id, String email, String nome, String nacionalidade, TipoArbitro tipo) {
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.nacionalidade = nacionalidade;
        this.tipo = tipo;
        this.estado = EstadoArbitro.ATIVO;
        this.scoreFIFA = 0;
        this.totalAvaliacoes = 0;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public TipoArbitro getTipo() {
        return tipo;
    }

    public EstadoArbitro getEstado() {
        return estado;
    }

    public void setEstado(EstadoArbitro estado) {
        this.estado = estado;
    }

    public int getScoreFIFA() {
        return scoreFIFA;
    }

    public int getTotalAvaliacoes() {
        return totalAvaliacoes;
    }

    /**
     * Regista uma nova avaliação e atualiza a média ponderada do score do árbitro.
     * @param rating Valor da avaliação (1 a 5 estrelas, convertido para escala 1-100)
     */
    public void registarAvaliacao(int rating) {
        int ratingConvertido = rating * 20; // 1-5 estrelas -> 20-100 pontos
        int oldTotal = this.totalAvaliacoes;
        int oldScore = this.scoreFIFA;
        
        double newScore = ((double)(oldScore * oldTotal) + ratingConvertido) / (oldTotal + 1);
        this.scoreFIFA = (int) Math.round(newScore);
        this.totalAvaliacoes = oldTotal + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbitro arbitro = (Arbitro) o;
        return id == arbitro.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nome + " (" + nacionalidade + ")";
    }
}
