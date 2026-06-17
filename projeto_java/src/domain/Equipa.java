package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa uma Equipa/Seleção do Campeonato.
 */
public class Equipa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String treinador;
    private List<Jogador> jogadores;

    public Equipa(String nome, String treinador) {
        this.nome = nome;
        this.treinador = treinador;
        this.jogadores = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public String getTreinador() {
        return treinador;
    }

    public List<Jogador> getJogadores() {
        return new ArrayList<>(jogadores); // Retorna cópia de segurança
    }

    /**
     * Adiciona um jogador à equipa respeitando o limite máximo de 26 jogadores da FIFA.
     */
    public boolean adicionarJogador(Jogador jogador) {
        if (this.jogadores.size() >= 26) {
            return false; // Limite máximo atingido
        }
        
        // Verifica se o número da camisola já existe na equipa
        for (Jogador j : jogadores) {
            if (j.getNumeroCamisola() == jogador.getNumeroCamisola()) {
                return false; // Número duplicado
            }
        }
        
        this.jogadores.add(jogador);
        return true;
    }

    public void removerJogador(int jogadorId) {
        this.jogadores.removeIf(j -> j.getId() == jogadorId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipa equipa = (Equipa) o;
        return Objects.equals(nome, equipa.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    @Override
    public String toString() {
        return nome;
    }
}
