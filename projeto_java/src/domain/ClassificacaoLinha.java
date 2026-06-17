package domain;

import java.io.Serializable;

/**
 * Representa uma linha de classificacao de uma equipa no grupo (Value Object).
 */
public class ClassificacaoLinha implements Serializable {
    private Equipa equipa;
    private int pontos;
    private int jogados;
    private int vitorias;
    private int empates;
    private int derrotas;
    private int golosMarcados;
    private int golosSofridos;
    private int saldoGolos;

    public ClassificacaoLinha(Equipa equipa) {
        this.equipa = equipa;
        this.pontos = 0;
        this.jogados = 0;
        this.vitorias = 0;
        this.empates = 0;
        this.derrotas = 0;
        this.golosMarcados = 0;
        this.golosSofridos = 0;
        this.saldoGolos = 0;
    }

    public Equipa getEquipa() {
        return equipa;
    }

    public int getPontos() {
        return pontos;
    }

    public int getJogados() {
        return jogados;
    }

    public int getVitorias() {
        return vitorias;
    }

    public int getEmpates() {
        return empates;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public int getGolosMarcados() {
        return golosMarcados;
    }

    public int getGolosSofridos() {
        return golosSofridos;
    }

    public int getSaldoGolos() {
        return saldoGolos;
    }

    public void adicionarResultado(int golosMarcados, int golosSofridos) {
        this.jogados++;
        this.golosMarcados += golosMarcados;
        this.golosSofridos += golosSofridos;
        this.saldoGolos = this.golosMarcados - this.golosSofridos;

        if (golosMarcados > golosSofridos) {
            this.vitorias++;
            this.pontos += 3;
        } else if (golosMarcados == golosSofridos) {
            this.empates++;
            this.pontos += 1;
        } else {
            this.derrotas++;
        }
    }
}
