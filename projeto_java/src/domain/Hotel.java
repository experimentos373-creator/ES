package domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa um Hotel associado ao alojamento de uma equipa.
 */
public class Hotel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String localizacao;
    private int capacidadeQuartos;
    private String checkInDate;
    private String checkOutDate;
    private Equipa equipaHospedada;

    public Hotel(int id, String nome, String localizacao, int capacidadeQuartos) {
        this.id = id;
        this.nome = nome;
        this.localizacao = localizacao;
        this.capacidadeQuartos = capacidadeQuartos;
        this.checkInDate = null;
        this.checkOutDate = null;
        this.equipaHospedada = null;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public int getCapacidadeQuartos() {
        return capacidadeQuartos;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public Equipa getEquipaHospedada() {
        return equipaHospedada;
    }

    /**
     * Efetua check-in de uma equipa no hotel.
     */
    public boolean checkIn(Equipa equipa, String checkInDate, String checkOutDate) {
        // Regra de Negocio: Se o hotel ja estiver ocupado por outra equipa, rejeita
        if (this.equipaHospedada != null && !this.equipaHospedada.equals(equipa)) {
            return false; 
        }
        this.equipaHospedada = equipa;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        return true;
    }

    /**
     * Efetua check-out, libertando o hotel.
     */
    public void checkOut() {
        this.equipaHospedada = null;
        this.checkInDate = null;
        this.checkOutDate = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return id == hotel.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
