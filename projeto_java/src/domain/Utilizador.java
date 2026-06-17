package domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa um Utilizador do sistema (Atores do diagrama de Casos de Uso).
 */
public class Utilizador implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String nome;
    private TipoUtilizador cargo; // ADMIN, GESTOR_ARBITRAGEM, GESTOR_EQUIPA, GESTOR_LOGISTICA, PUBLICO
    private String equipaAssociada; // Opcional, apenas para Gestores de Equipa

    public Utilizador(String email, String nome, TipoUtilizador cargo, String equipaAssociada) {
        this.email = email;
        this.nome = nome;
        this.cargo = cargo;
        this.equipaAssociada = equipaAssociada;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public TipoUtilizador getCargo() {
        return cargo;
    }

    public String getEquipaAssociada() {
        return equipaAssociada;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilizador that = (Utilizador) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
