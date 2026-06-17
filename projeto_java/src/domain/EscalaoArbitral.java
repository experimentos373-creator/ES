package domain;

import java.io.Serializable;

/**
 * Representa a equipa de arbitragem escalada para um Jogo (BCE Entity/Value Object).
 */
public class EscalaoArbitral implements Serializable {
    private static final long serialVersionUID = 1L;

    private Arbitro principal;
    private Arbitro assistente1;
    private Arbitro assistente2;
    private Arbitro quarto;
    private Arbitro var;

    public EscalaoArbitral(Arbitro principal, Arbitro assistente1, Arbitro assistente2, Arbitro quarto, Arbitro var) {
        this.principal = principal;
        this.assistente1 = assistente1;
        this.assistente2 = assistente2;
        this.quarto = quarto;
        this.var = var;
    }

    public Arbitro getPrincipal() {
        return principal;
    }

    public Arbitro getAssistente1() {
        return assistente1;
    }

    public Arbitro getAssistente2() {
        return assistente2;
    }

    public Arbitro getQuarto() {
        return quarto;
    }

    public Arbitro getVar() {
        return var;
    }
}
