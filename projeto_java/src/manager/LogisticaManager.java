package manager;

import domain.*;
import util.PersistenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador central para Logistica (Alojamento e Transporte) - Singleton DCL.
 */
public class LogisticaManager {
    private static volatile LogisticaManager instance = null;

    private List<Hotel> hoteis;
    private List<Viagem> viagens;

    private static final String HOTEIS_FILE = "hoteis.ser";
    private static final String VIAGENS_FILE = "viagens.ser";

    private LogisticaManager() {
        this.hoteis = PersistenceUtil.carregar(HOTEIS_FILE);
        this.viagens = PersistenceUtil.carregar(VIAGENS_FILE);
    }

    public static LogisticaManager getInstance() {
        if (instance == null) {
            synchronized (LogisticaManager.class) {
                if (instance == null) {
                    instance = new LogisticaManager();
                }
            }
        }
        return instance;
    }

    public void saveAll() {
        PersistenceUtil.guardar(HOTEIS_FILE, this.hoteis);
        PersistenceUtil.guardar(VIAGENS_FILE, this.viagens);
    }

    public void reset() {
        this.hoteis = new ArrayList<>();
        this.viagens = new ArrayList<>();
        saveAll();
    }

    public List<Hotel> getHoteis() {
        return new ArrayList<>(this.hoteis);
    }

    public List<Viagem> getViagens() {
        return new ArrayList<>(this.viagens);
    }

    public void registarHotel(Hotel hotel) {
        if (hotel == null) return;
        this.hoteis.removeIf(h -> h.getId() == hotel.getId());
        this.hoteis.add(hotel);
        saveAll();
    }

    public Hotel procurarHotelPorId(int id) {
        for (Hotel h : this.hoteis) {
            if (h.getId() == id) return h;
        }
        return null;
    }

    /**
     * Aloca uma equipa a um hotel, validando capacidade e exclusividade.
     * Capacidade = numero de jogadores inscritos (squadSize).
     */
    public boolean alocarHotel(Equipa equipa, Hotel hotel, String checkIn, String checkOut) {
        if (equipa == null || hotel == null) return false;

        int squadSize = equipa.getJogadores().size();
        if (squadSize > hotel.getCapacidadeQuartos()) {
            return false; // Capacidade excedida
        }

        // Exclusividade: verificar se hotel ja tem outra equipa hospedada
        if (hotel.getEquipaHospedada() != null && !hotel.getEquipaHospedada().equals(equipa)) {
            return false; // Hotel ja ocupado por outra equipa
        }

        boolean success = hotel.checkIn(equipa, checkIn, checkOut);
        if (success) {
            registarHotel(hotel);
        }
        return success;
    }

    /**
     * Regista o checkout de uma equipa do hotel, libertando-o.
     */
    public void registarCheckout(Hotel hotel) {
        if (hotel == null) return;
        hotel.checkOut();
        registarHotel(hotel);
    }

    /**
     * Planeia uma viagem para um jogo e persiste.
     */
    public Viagem planearViagem(Jogo jogo, String origem, String destino, String dataPartida, String dataChegada, String meio) {
        Viagem v = new Viagem(origem, destino, dataPartida, dataChegada, meio);
        this.viagens.add(v);
        saveAll();
        return v;
    }
}
