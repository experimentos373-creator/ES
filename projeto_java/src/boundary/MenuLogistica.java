package boundary;

import domain.*;
import manager.LogisticaManager;
import manager.CampeonatoManager;
import util.LeitorInput;

import java.util.List;

/**
 * Menu de consola para Gestores de Logistica.
 */
public class MenuLogistica {
    private final LogisticaManager logManager;
    private final CampeonatoManager campManager;

    public MenuLogistica() {
        this.logManager = LogisticaManager.getInstance();
        this.campManager = CampeonatoManager.getInstance();
    }

    public void exibir() {
        while (true) {
            System.out.println("\n==================================");
            System.out.println("   MENU LOGISTICA - WC 2026       ");
            System.out.println("==================================");
            System.out.println("1. Registar Hotel");
            System.out.println("2. Alocar Hotel a Comitiva");
            System.out.println("3. Registar Checkout de Hotel");
            System.out.println("4. Planear Viagem de Jogo");
            System.out.println("5. Listar Hoteis e Comitivas");
            System.out.println("0. Voltar");
            System.out.println("==================================");
            int opcao = LeitorInput.lerInteiro("Opcao: ");

            switch (opcao) {
                case 1: registarHotel(); break;
                case 2: alocarHotel(); break;
                case 3: checkoutHotel(); break;
                case 4: planearViagem(); break;
                case 5: listarHoteis(); break;
                case 0: return;
                default: System.out.println("Opcao invalida.");
            }
        }
    }

    private void registarHotel() {
        System.out.println("\n--- REGISTAR HOTEL ---");
        int id = LeitorInput.lerInteiro("ID do Hotel: ");
        String nome = LeitorInput.lerLinha("Nome: ");
        String local = LeitorInput.lerLinha("Localizacao: ");
        int cap = LeitorInput.lerInteiro("Capacidade de Quartos: ");

        Hotel h = new Hotel(id, nome, local, cap);
        logManager.registarHotel(h);
        System.out.println("Hotel registado com sucesso!");
    }

    private void alocarHotel() {
        System.out.println("\n--- ALOCAR HOTEL A COMITIVA ---");
        List<Hotel> hoteis = logManager.getHoteis();
        if (hoteis.isEmpty()) {
            System.out.println("Nao existem hoteis registados.");
            return;
        }
        for (Hotel h : hoteis) {
            System.out.println("  ID " + h.getId() + ": " + h.getNome() + " | Quartos: " + h.getCapacidadeQuartos() + " | Ocupado: " + (h.getEquipaHospedada() != null ? h.getEquipaHospedada().getNome() : "Livre"));
        }

        int hotelId = LeitorInput.lerInteiro("ID do Hotel: ");
        Hotel hotel = logManager.procurarHotelPorId(hotelId);
        if (hotel == null) {
            System.out.println("Hotel nao encontrado.");
            return;
        }

        String eqNome = LeitorInput.lerLinha("Nome da Equipa/Comitiva: ");
        Equipa eq = campManager.procurarEquipaPorNome(eqNome);
        if (eq == null) {
            System.out.println("Equipa nao encontrada.");
            return;
        }

        String checkIn = LeitorInput.lerLinha("Data Check-in (YYYY-MM-DD): ");
        String checkOut = LeitorInput.lerLinha("Data Check-out (YYYY-MM-DD): ");

        boolean ok = logManager.alocarHotel(eq, hotel, checkIn, checkOut);
        if (ok) {
            System.out.println("Comitiva alocada com sucesso!");
        } else {
            System.out.println("Falha na alocacao. Capacidade insuficiente ou hotel ja ocupado.");
        }
    }

    private void checkoutHotel() {
        System.out.println("\n--- CHECKOUT DE HOTEL ---");
        List<Hotel> hoteis = logManager.getHoteis();
        for (Hotel h : hoteis) {
            if (h.getEquipaHospedada() != null) {
                System.out.println("  ID " + h.getId() + ": " + h.getNome() + " | Equipa: " + h.getEquipaHospedada().getNome());
            }
        }

        int hotelId = LeitorInput.lerInteiro("ID do Hotel para checkout: ");
        Hotel hotel = logManager.procurarHotelPorId(hotelId);
        if (hotel == null) {
            System.out.println("Hotel nao encontrado.");
            return;
        }

        logManager.registarCheckout(hotel);
        System.out.println("Checkout registado. Hotel agora livre.");
    }

    private void planearViagem() {
        System.out.println("\n--- PLANEAR VIAGEM DE JOGO ---");
        List<Jogo> jogos = campManager.getJogos();
        if (jogos.isEmpty()) {
            System.out.println("Sem jogos registados.");
            return;
        }
        for (Jogo j : jogos) {
            System.out.println("  ID " + j.getId() + ": " + j.getHomeTeam().getNome() + " vs " + j.getAwayTeam().getNome() + " em " + j.getEstadio().getNome());
        }

        int jogoId = LeitorInput.lerInteiro("ID do Jogo: ");
        Jogo jogo = campManager.procurarJogoPorId(jogoId);
        if (jogo == null) {
            System.out.println("Jogo nao encontrado.");
            return;
        }

        String origem = LeitorInput.lerLinha("Origem (Hotel/Cidade): ");
        String destino = LeitorInput.lerLinha("Destino (Estadio/Cidade): ");
        String dataPartida = LeitorInput.lerLinha("Data/Hora Partida: ");
        String dataChegada = LeitorInput.lerLinha("Data/Hora Chegada Prevista: ");
        String meio = LeitorInput.lerLinha("Meio de Transporte (Autocarro/Aviao): ");

        Viagem v = logManager.planearViagem(jogo, origem, destino, dataPartida, dataChegada, meio);
        System.out.println("Viagem planificada! ID da viagem: " + v.hashCode());
    }

    private void listarHoteis() {
        System.out.println("\n--- LISTA DE HOTEIS ---");
        List<Hotel> hoteis = logManager.getHoteis();
        if (hoteis.isEmpty()) {
            System.out.println("Sem hoteis registados.");
            return;
        }
        for (Hotel h : hoteis) {
            System.out.println("ID " + h.getId() + ": " + h.getNome() + " | " + h.getLocalizacao() + " | Quartos: " + h.getCapacidadeQuartos());
            if (h.getEquipaHospedada() != null) {
                System.out.println("  -> Comitiva: " + h.getEquipaHospedada().getNome() + " (Check-in: " + h.getCheckInDate() + ", Check-out: " + h.getCheckOutDate() + ")");
            } else {
                System.out.println("  -> Livre");
            }
        }
    }
}
