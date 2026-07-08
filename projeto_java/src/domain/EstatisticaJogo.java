package domain;

import java.io.Serializable;

/**
 * Representa as estatísticas detalhadas de um Jogo.
 */
public class EstatisticaJogo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int posseBolaHome;
    private int posseBolaAway;
    private int rematesHome;
    private int rematesAway;
    private int cantosHome;
    private int cantosAway;

    // Novos campos adicionados para estatísticas detalhadas pós-jogo
    private int rematesBalizaHome;
    private int rematesBalizaAway;
    private int forasJogoHome;
    private int forasJogoAway;
    private int faltasHome;
    private int faltasAway;
    private int amarelosHome;
    private int amarelosAway;
    private int vermelhosHome;
    private int vermelhosAway;
    private int defesasHome;
    private int defesasAway;
    private int passesHome;
    private int passesAway;
    private int precisaoPasseHome;
    private int precisaoPasseAway;

    // Construtor completo
    public EstatisticaJogo(int posseBolaHome, int posseBolaAway, int rematesHome, int rematesAway, int cantosHome, int cantosAway,
                           int rematesBalizaHome, int rematesBalizaAway, int forasJogoHome, int forasJogoAway,
                           int faltasHome, int faltasAway, int amarelosHome, int amarelosAway,
                           int vermelhosHome, int vermelhosAway, int defesasHome, int defesasAway,
                           int passesHome, int passesAway, int precisaoPasseHome, int precisaoPasseAway) {
        this.posseBolaHome = posseBolaHome;
        this.posseBolaAway = posseBolaAway;
        this.rematesHome = rematesHome;
        this.rematesAway = rematesAway;
        this.cantosHome = cantosHome;
        this.cantosAway = cantosAway;
        this.rematesBalizaHome = rematesBalizaHome;
        this.rematesBalizaAway = rematesBalizaAway;
        this.forasJogoHome = forasJogoHome;
        this.forasJogoAway = forasJogoAway;
        this.faltasHome = faltasHome;
        this.faltasAway = faltasAway;
        this.amarelosHome = amarelosHome;
        this.amarelosAway = amarelosAway;
        this.vermelhosHome = vermelhosHome;
        this.vermelhosAway = vermelhosAway;
        this.defesasHome = defesasHome;
        this.defesasAway = defesasAway;
        this.passesHome = passesHome;
        this.passesAway = passesAway;
        this.precisaoPasseHome = precisaoPasseHome;
        this.precisaoPasseAway = precisaoPasseAway;
    }

    // Construtor alternativo para retrocompatibilidade
    public EstatisticaJogo(int posseBolaHome, int posseBolaAway, int rematesHome, int rematesAway, int cantosHome, int cantosAway) {
        this(
            posseBolaHome, 
            posseBolaAway, 
            rematesHome, 
            rematesAway, 
            cantosHome, 
            cantosAway,
            Math.max(1, rematesHome / 3 + (int)(Math.random() * 3)), // rematesBalizaHome
            Math.max(1, rematesAway / 3 + (int)(Math.random() * 3)), // rematesBalizaAway
            (int)(Math.random() * 4), // forasJogoHome
            (int)(Math.random() * 4), // forasJogoAway
            8 + (int)(Math.random() * 10), // faltasHome
            8 + (int)(Math.random() * 10), // faltasAway
            (int)(Math.random() * 3), // amarelosHome
            (int)(Math.random() * 3), // amarelosAway
            (Math.random() < 0.05 ? 1 : 0), // vermelhosHome
            (Math.random() < 0.05 ? 1 : 0), // vermelhosAway
            Math.max(0, (rematesAway / 3) + (int)(Math.random() * 2)), // defesasHome
            Math.max(0, (rematesHome / 3) + (int)(Math.random() * 2)), // defesasAway
            posseBolaHome * 7 + (int)(Math.random() * 50), // passesHome
            posseBolaAway * 7 + (int)(Math.random() * 50), // passesAway
            70 + (int)(Math.random() * 20), // precisaoPasseHome
            70 + (int)(Math.random() * 20)  // precisaoPasseAway
        );
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

    public int getRematesBalizaHome() {
        return rematesBalizaHome;
    }

    public int getRematesBalizaAway() {
        return rematesBalizaAway;
    }

    public int getForasJogoHome() {
        return forasJogoHome;
    }

    public int getForasJogoAway() {
        return forasJogoAway;
    }

    public int getFaltasHome() {
        return faltasHome;
    }

    public int getFaltasAway() {
        return faltasAway;
    }

    public int getAmarelosHome() {
        return amarelosHome;
    }

    public int getAmarelosAway() {
        return amarelosAway;
    }

    public int getVermelhosHome() {
        return vermelhosHome;
    }

    public int getVermelhosAway() {
        return vermelhosAway;
    }

    public int getDefesasHome() {
        return defesasHome;
    }

    public int getDefesasAway() {
        return defesasAway;
    }

    public int getPassesHome() {
        return passesHome;
    }

    public int getPassesAway() {
        return passesAway;
    }

    public int getPrecisaoPasseHome() {
        return precisaoPasseHome;
    }

    public int getPrecisaoPasseAway() {
        return precisaoPasseAway;
    }

    public static EstatisticaJogo gerarEstatisticasAleatorias(int goalsHome, int goalsAway) {
        java.util.Random rand = new java.util.Random();
        int posseHome;
        int posseAway;
        int remHome;
        int remAway;
        int remBalHome;
        int remBalAway;
        int passHome;
        int passAway;

        boolean homeWon = goalsHome > goalsAway;
        boolean awayWon = goalsAway > goalsHome;
        boolean statsFavorHome = false;

        if (homeWon) {
            statsFavorHome = (rand.nextInt(100) < 70); // 70% chance to favor winner
        } else if (awayWon) {
            statsFavorHome = (rand.nextInt(100) >= 70); // 30% chance to favor home (i.e. 70% to favor away)
        } else {
            statsFavorHome = rand.nextBoolean(); // 50/50 for draws
        }

        if (statsFavorHome) {
            posseHome = 53 + rand.nextInt(12); // 53% to 65%
            posseAway = 100 - posseHome;
            remHome = 12 + rand.nextInt(8); // 12 to 20 shots
            remAway = 6 + rand.nextInt(6);  // 6 to 12 shots
            remBalHome = Math.max(goalsHome, remHome / 3 + rand.nextInt(3));
            remBalAway = Math.max(goalsAway, remAway / 4 + rand.nextInt(2));
            passHome = posseHome * 9 + rand.nextInt(30);
            passAway = posseAway * 8 + rand.nextInt(30);
        } else {
            posseAway = 53 + rand.nextInt(12); // 53% to 65%
            posseHome = 100 - posseAway;
            remAway = 12 + rand.nextInt(8); // 12 to 20 shots
            remHome = 6 + rand.nextInt(6);  // 6 to 12 shots
            remBalAway = Math.max(goalsAway, remAway / 3 + rand.nextInt(3));
            remBalHome = Math.max(goalsHome, remHome / 4 + rand.nextInt(2));
            passAway = posseAway * 9 + rand.nextInt(30);
            passHome = posseHome * 8 + rand.nextInt(30);
        }

        int forasHome = rand.nextInt(4);
        int forasAway = rand.nextInt(4);
        int faltasHome = 8 + rand.nextInt(10);
        int faltasAway = 8 + rand.nextInt(10);
        int savesHome = Math.max(0, remBalAway - goalsAway);
        int savesAway = Math.max(0, remBalHome - goalsHome);
        int accHome = 75 + rand.nextInt(15);
        int accAway = 75 + rand.nextInt(15);
        int cantHome = 3 + rand.nextInt(6);
        int cantAway = 3 + rand.nextInt(6);
        int yHome = rand.nextInt(3);
        int yAway = rand.nextInt(3);
        int rHome = rand.nextInt(100) < 5 ? 1 : 0;
        int rAway = rand.nextInt(100) < 5 ? 1 : 0;

        return new EstatisticaJogo(
            posseHome, posseAway, remHome, remAway, cantHome, cantAway,
            remBalHome, remBalAway, forasHome, forasAway,
            faltasHome, faltasAway, yHome, yAway,
            rHome, rAway, savesHome, savesAway,
            passHome, passAway, accHome, accAway
        );
    }
}
