package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa um Hotel associado ao alojamento de comitivas.
 */
public class Hotel implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class AlojamentoInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private Equipa equipa;
        private String checkInDate;
        private String checkOutDate;

        public AlojamentoInfo(Equipa equipa, String checkInDate, String checkOutDate) {
            this.equipa = equipa;
            this.checkInDate = checkInDate;
            this.checkOutDate = checkOutDate;
        }

        public Equipa getEquipa() {
            return equipa;
        }

        public String getCheckInDate() {
            return checkInDate;
        }

        public String getCheckOutDate() {
            return checkOutDate;
        }
    }

    private int id;
    private String nome;
    private String localizacao;
    private int capacidadePessoas;
    private List<AlojamentoInfo> alojamentos = new ArrayList<>();

    public Hotel(int id, String nome, String localizacao, int capacidadePessoas) {
        this.id = id;
        this.nome = nome;
        this.localizacao = localizacao;
        this.capacidadePessoas = capacidadePessoas;
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

    public int getCapacidadePessoas() {
        return capacidadePessoas;
    }

    public List<AlojamentoInfo> getAlojamentos() {
        if (this.alojamentos == null) {
            this.alojamentos = new ArrayList<>();
        }
        return new ArrayList<>(this.alojamentos);
    }

    /**
     * Efetua check-in de uma equipa no hotel.
     */
    public boolean checkIn(Equipa equipa, String checkInDate, String checkOutDate) {
        if (equipa == null) return false;
        
        if (this.alojamentos == null) {
            this.alojamentos = new ArrayList<>();
        }
        
        // Evitar check-in duplicado da mesma equipa neste hotel
        for (AlojamentoInfo info : this.alojamentos) {
            if (info.getEquipa().equals(equipa)) {
                return true;
            }
        }
        
        this.alojamentos.add(new AlojamentoInfo(equipa, checkInDate, checkOutDate));
        return true;
    }

    /**
     * Efetua check-out de uma equipa específica.
     */
    public boolean checkOutEquipa(Equipa equipa) {
        if (equipa == null || this.alojamentos == null) return false;
        return this.alojamentos.removeIf(info -> info.getEquipa().equals(equipa));
    }

    /**
     * Efetua check-out de todas as equipas.
     */
    public void checkOut() {
        if (this.alojamentos != null) {
            this.alojamentos.clear();
        } else {
            this.alojamentos = new ArrayList<>();
        }
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
