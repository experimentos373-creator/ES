package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa um Estádio onde decorrem as partidas do Campeonato.
 */
public class Estadio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String localizacao;
    private List<SetorEstadio> setores;

    public Estadio(String nome, String localizacao) {
        this.nome = nome;
        this.localizacao = localizacao;
        this.setores = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public List<SetorEstadio> getSetores() {
        return new ArrayList<>(setores);
    }

    public void adicionarSetor(SetorEstadio setor) {
        this.setores.add(setor);
    }

    /**
     * Procura um setor pelo nome.
     */
    public SetorEstadio getSetorPorNome(String nomeSetor) {
        for (SetorEstadio s : setores) {
            if (s.getNome().equalsIgnoreCase(nomeSetor)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estadio estadio = (Estadio) o;
        return Objects.equals(nome, estadio.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}
