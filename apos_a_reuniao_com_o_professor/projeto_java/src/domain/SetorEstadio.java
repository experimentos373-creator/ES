package domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa um Setor específico de um Estádio com a sua capacidade e preços.
 */
public class SetorEstadio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome; // Premium, Intermédia, Económica, Local
    private int capacidadeTotal;
    private int bilhetesVendidos;
    private double precoBase;

    public SetorEstadio(String nome, int capacidadeTotal, double precoBase) {
        this.nome = nome;
        this.capacidadeTotal = capacidadeTotal;
        this.bilhetesVendidos = 0;
        this.precoBase = precoBase;
    }

    public String getNome() {
        return nome;
    }

    public int getCapacidadeTotal() {
        return capacidadeTotal;
    }

    public int getBilhetesVendidos() {
        return bilhetesVendidos;
    }

    public double getPrecoBase() {
        return precoBase;
    }

    public void setCapacidadeTotal(int capacidadeTotal) {
        this.capacidadeTotal = capacidadeTotal;
    }

    public void setPrecoBase(double precoBase) {
        this.precoBase = precoBase;
    }

    /**
     * Tenta vender bilhetes para este setor, verificando a capacidade restante.
     */
    public synchronized boolean venderBilhete(int quantidade) {
        if (this.bilhetesVendidos + quantidade > this.capacidadeTotal) {
            return false; // Capacidade máxima excedida
        }
        this.bilhetesVendidos += quantidade;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetorEstadio that = (SetorEstadio) o;
        return Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}
