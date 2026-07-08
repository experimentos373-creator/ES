package domain;

import java.io.Serializable;

/**
 * Representa o histórico de estatísticas e rating de um jogador num determinado jogo.
 */
public class JogadorJogoStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private int jogadorId;
    private String jogadorNome;
    private int jogoId;
    private double rating;
    private int minutesPlayed;
    private int goals;
    private int assists;
    private int yellowCards;
    private int redCards;

    // Construtor completo
    public JogadorJogoStats(int jogadorId, String jogadorNome, int jogoId, double rating, int minutesPlayed, int goals, int assists, int yellowCards, int redCards) {
        this.jogadorId = jogadorId;
        this.jogadorNome = jogadorNome;
        this.jogoId = jogoId;
        this.rating = rating;
        this.minutesPlayed = minutesPlayed;
        this.goals = goals;
        this.assists = assists;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
    }

    // Construtor alternativo para retrocompatibilidade
    public JogadorJogoStats(int jogoId, double rating, int minutesPlayed, int goals, int assists, int yellowCards, int redCards) {
        this(0, "Jogador", jogoId, rating, minutesPlayed, goals, assists, yellowCards, redCards);
    }

    public int getJogadorId() {
        return jogadorId;
    }

    public void setJogadorId(int jogadorId) {
        this.jogadorId = jogadorId;
    }

    public String getJogadorNome() {
        return jogadorNome;
    }

    public void setJogadorNome(String jogadorNome) {
        this.jogadorNome = jogadorNome;
    }

    public int getJogoId() {
        return jogoId;
    }

    public void setJogoId(int jogoId) {
        this.jogoId = jogoId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getMinutesPlayed() {
        return minutesPlayed;
    }

    public void setMinutesPlayed(int minutesPlayed) {
        this.minutesPlayed = minutesPlayed;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
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
}
