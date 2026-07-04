package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa um Jogo/Partida de futebol no Campeonato.
 */
public class Jogo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String data; // YYYY-MM-DD
    private String hora; // HH:MM
    private Estadio estadio;
    private Equipa homeTeam;
    private Equipa awayTeam;
    private StatusJogo status; // AGENDADO, EM_CURSO, FINALIZADO
    private String phase;      // Grupos, Dezasseis-avos, Oitavos, Quartos, Meias-Finais, Final
    private Equipa winner;
    private int goalsHome;
    private int goalsAway;
    private int penaltiesHome;
    private int penaltiesAway;

    // Associações extraídas para SRP
    private EscalaoArbitral escalaArbitros;
    private List<EventoJogo> eventos;
    private EstatisticaJogo estatisticas;

    // Atributos de navegação de bracket (Auto-associação OO)
    private Jogo proximoJogo; // null se for a Final
    private PosicaoBracket posicaoNoProximoJogo; // HOME ou AWAY

    public Jogo(int id, String data, String hora, Estadio estadio, Equipa homeTeam, Equipa awayTeam, String phase, Jogo proximoJogo, PosicaoBracket posicaoNoProximoJogo) {
        this.id = id;
        this.data = data;
        this.hora = hora;
        this.estadio = estadio;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.status = StatusJogo.AGENDADO;
        this.phase = phase;
        this.winner = null;
        this.goalsHome = 0;
        this.goalsAway = 0;
        this.penaltiesHome = -1;
        this.penaltiesAway = -1;
        this.escalaArbitros = null;
        this.eventos = new ArrayList<>();
        this.estatisticas = null;
        this.proximoJogo = proximoJogo;
        this.posicaoNoProximoJogo = posicaoNoProximoJogo;
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Estadio getEstadio() {
        return estadio;
    }

    public Equipa getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Equipa homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Equipa getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Equipa awayTeam) {
        this.awayTeam = awayTeam;
    }

    public StatusJogo getStatus() {
        return status;
    }

    public void setStatus(StatusJogo status) {
        this.status = status;
    }

    public String getPhase() {
        return phase;
    }

    public Equipa getWinner() {
        return winner;
    }

    public int getGoalsHome() {
        return goalsHome;
    }

    public int getGoalsAway() {
        return goalsAway;
    }

    public int getPenaltiesHome() {
        return penaltiesHome;
    }

    public int getPenaltiesAway() {
        return penaltiesAway;
    }

    public EscalaoArbitral getEscalaArbitros() {
        return escalaArbitros;
    }

    /**
     * Retorna o escalão de árbitros apenas se o jogo estiver Finalizado (Regra de Ética: Sigilo).
     */
    public EscalaoArbitral getEscalaArbitrosPublica() {
        return StatusJogo.AGENDADO.equals(this.status) ? null : this.escalaArbitros;
    }

    public void associarEscalaArbitros(EscalaoArbitral escalaArbitros) {
        this.escalaArbitros = escalaArbitros;
    }

    public List<EventoJogo> getEventos() {
        return new ArrayList<>(eventos);
    }

    public void adicionarEvento(EventoJogo evento) {
        this.eventos.add(evento);
    }

    public EstatisticaJogo getEstatisticas() {
        return estatisticas;
    }

    public Jogo getProximoJogo() {
        return proximoJogo;
    }

    public void setProximoJogo(Jogo proximoJogo) {
        this.proximoJogo = proximoJogo;
    }

    public PosicaoBracket getPosicaoNoProximoJogo() {
        return posicaoNoProximoJogo;
    }

    /**
     * Finaliza o jogo, registando o vencedor, os golos e as estatísticas.
     */
    public void finalizar(Equipa vencedor, int goalsHome, int goalsAway, int penaltiesHome, int penaltiesAway, EstatisticaJogo stats) {
        this.status = StatusJogo.FINALIZADO;
        this.winner = vencedor;
        this.goalsHome = goalsHome;
        this.goalsAway = goalsAway;
        this.penaltiesHome = penaltiesHome;
        this.penaltiesAway = penaltiesAway;
        this.estatisticas = stats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jogo jogo = (Jogo) o;
        return id == jogo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String home = homeTeam != null ? homeTeam.getNome() : "[A definir]";
        String away = awayTeam != null ? awayTeam.getNome() : "[A definir]";
        return home + " vs " + away + " (" + phase + " - " + data + " " + hora + ")";
    }
}
