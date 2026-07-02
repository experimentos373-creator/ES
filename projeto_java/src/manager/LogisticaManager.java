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

    public synchronized List<Hotel> getHoteis() {
        return new ArrayList<>(this.hoteis);
    }

    public synchronized List<Viagem> getViagens() {
        return new ArrayList<>(this.viagens);
    }

    public synchronized void registarHotel(Hotel hotel) {
        if (hotel == null) return;
        this.hoteis.removeIf(h -> h.getId() == hotel.getId());
        this.hoteis.add(hotel);
        saveAll();
    }

    public synchronized void removerHotel(int id) {
        this.hoteis.removeIf(h -> h.getId() == id);
        saveAll();
    }


    public Hotel procurarHotelPorId(int id) {
        for (Hotel h : this.hoteis) {
            if (h.getId() == id) return h;
        }
        return null;
    }

    /**
     * Aloca uma equipa a um hotel, validando capacidade acumulada e unicidade.
     * Capacidade = numero de jogadores inscritos (squadSize).
     */
    public synchronized boolean alocarHotel(Equipa equipa, Hotel hotel, String checkIn, String checkOut) {
        if (equipa == null || hotel == null) return false;

        // Regra de Unicidade: Equipa não pode estar alojada em nenhum outro hotel
        if (isEquipaHospedada(equipa)) {
            return false;
        }

        // Regra de Lotação: Calcular ocupação atual
        int currentOccupancy = 0;
        for (Hotel.AlojamentoInfo info : hotel.getAlojamentos()) {
            currentOccupancy += info.getEquipa().getJogadores().size();
        }

        int squadSize = equipa.getJogadores().size();
        if (currentOccupancy + squadSize > hotel.getCapacidadePessoas()) {
            return false; // Capacidade excedida
        }

        boolean success = hotel.checkIn(equipa, checkIn, checkOut);
        if (success) {
            registarHotel(hotel);
        }
        return success;
    }

    /**
     * Regista o checkout de uma equipa específica do hotel, libertando-a de forma persistente.
     */
    public synchronized boolean registarCheckoutEquipa(Hotel hotel, Equipa equipa) {
        if (hotel == null || equipa == null) return false;
        boolean success = hotel.checkOutEquipa(equipa);
        if (success) {
            registarHotel(hotel);
        }
        return success;
    }

    /**
     * Regista o checkout de todas as equipas do hotel, libertando-o totalmente.
     */
    public void registarCheckout(Hotel hotel) {
        if (hotel == null) return;
        hotel.checkOut();
        registarHotel(hotel);
    }

    /**
     * Verifica se uma equipa já se encontra hospedada em qualquer hotel do torneio.
     */
    public boolean isEquipaHospedada(Equipa equipa) {
        if (equipa == null) return false;
        for (Hotel h : this.hoteis) {
            for (Hotel.AlojamentoInfo info : h.getAlojamentos()) {
                if (info.getEquipa().equals(equipa)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Viagem planearViagem(Jogo jogo, Equipa equipa, String origem, String destino, String dataPartida, String dataChegada, String meio) {
        Viagem v = new Viagem(origem, destino, dataPartida, dataChegada, meio);
        v.setJogo(jogo);
        v.setEquipa(equipa);
        this.viagens.add(v);
        saveAll();
        return v;
    }
}
