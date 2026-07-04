package domain;

import java.io.Serializable;

/**
 * Representa as estatísticas detalhadas de um Jogo (Value Object).
 */
public class EstatisticaJogo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int posseBolaHome;
    private int posseBolaAway;
    private int rematesHome;
    private int rematesAway;
    private int cantosHome;
    private int cantosAway;

    public EstatisticaJogo(int posseBolaHome, int posseBolaAway, int rematesHome, int rematesAway, int cantosHome, int cantosAway) {
        this.posseBolaHome = posseBolaHome;
        this.posseBolaAway = posseBolaAway;
        this.rematesHome = rematesHome;
        this.rematesAway = rematesAway;
        this.cantosHome = cantosHome;
        this.cantosAway = cantosAway;
    }

    public int getPosseBolaHome() {
        return posseBolaHome;
    }

    public int getPosseBolaAway() {
        return posseBolaAway;
    }

    public int getRematesHome() {
        return rematesHome;
    }

    public int getRematesAway() {
        return rematesAway;
    }

    public int getCantosHome() {
        return cantosHome;
    }

    public int getCantosAway() {
        return cantosAway;
    }
}
