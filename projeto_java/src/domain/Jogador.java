package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa um Jogador de uma comitiva/seleção.
 */
public class Jogador implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int numeroCamisola;
    private String nome;
    private String posicao; // Guarda-Redes, Defesa, Médio, Avançado
    private EstadoJogador estado;  // APTO, LESIONADO, SUSPENSO
    private int goals;
    private int assists;

    // Campos adicionais para suporte visual e estatísticas (Gestor Equipa)
    private boolean starter;
    private int yellowCards;
    private int redCards;
    private int energy;
    private List<String> injuryHistory;
    private List<JogadorJogoStats> matchStatsList;

    public Jogador(int id, int numeroCamisola, String nome, String posicao, EstadoJogador estado) {
        this.id = id;
        this.numeroCamisola = numeroCamisola;
        this.nome = nome;
        this.posicao = posicao;
        this.estado = estado;
        this.goals = 0;
        this.assists = 0;
        this.starter = false;
        this.yellowCards = 0;
        this.redCards = 0;
        this.energy = 100;
        this.injuryHistory = new ArrayList<>();
        this.matchStatsList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getNumeroCamisola() {
        return numeroCamisola;
    }

    public String getNome() {
        return nome;
    }

    public String getPosicao() {
        return posicao;
    }

    public EstadoJogador getEstado() {
        return estado;
    }

    public void setEstado(EstadoJogador estado) {
        this.estado = estado;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public void incrementGoals() {
        this.goals++;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public void incrementAssists() {
        this.assists++;
    }

    public boolean isStarter() {
        return starter;
    }

    public void setStarter(boolean starter) {
        this.starter = starter;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(int yellowCards) {
        this.yellowCards = yellowCards;
    }

    public int getRedCards() {
        return redCards;
    }

    public void setRedCards(int redCards) {
        this.redCards = redCards;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public List<String> getInjuryHistory() {
        if (injuryHistory == null) {
            injuryHistory = new ArrayList<>();
        }
        return injuryHistory;
    }

    public void addInjury(String injury) {
        getInjuryHistory().add(injury);
    }

    public List<JogadorJogoStats> getMatchStatsList() {
        if (matchStatsList == null) {
            matchStatsList = new ArrayList<>();
        }
        return matchStatsList;
    }

    public void setMatchStatsList(List<JogadorJogoStats> matchStatsList) {
        this.matchStatsList = matchStatsList;
    }

    public double getAverageRating() {
        List<JogadorJogoStats> stats = getMatchStatsList();
        if (stats.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (JogadorJogoStats s : stats) {
            sum += s.getRating();
        }
        return sum / stats.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jogador jogador = (Jogador) o;
        return id == jogador.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

