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
            System.out.println("\n=================================");
            System.out.println("    GESTAO DE LOGISTICA (WC 2026)");
            System.out.println("=================================");
            System.out.println("1. Alocar Hotel a Comitiva");
            System.out.println("2. Registar Checkout de Hotel");
            System.out.println("3. Planear Viagem de Jogo");
            System.out.println("4. Listar Hoteis");
            System.out.println("5. Listar Viagens Planeadas");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.println("=================================");

            int opcao = LeitorInput.lerInteiro("Escolha uma opcao: ");

            switch (opcao) {
                case 1:
                    alocarHotel();
                    break;
                case 2:
                    checkoutHotel();
                    break;
                case 3:
                    planearViagem();
                    break;
                case 4:
                    listarHoteis();
                    break;
                case 5:
                    listarViagens();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida!");
            }
        }
    }

    private void alocarHotel() {
        System.out.println("\n--- ALOCAR HOTEL A COMITIVA ---");
        List<Hotel> hoteis = logManager.getHoteis();
        if (hoteis.isEmpty()) {
            System.out.println("Nao existem hoteis registados.");
            return;
        }
        for (Hotel h : hoteis) {
            int currentOccupancy = h.getAlojamentos().stream().mapToInt(info -> info.getEquipa().getJogadores().size()).sum();
            System.out.print("  ID " + h.getId() + ": " + h.getNome() + " | Capacidade: " + currentOccupancy + "/" + h.getCapacidadePessoas() + " pessoas");
            if (!h.getAlojamentos().isEmpty()) {
                System.out.print(" | Hospeda: ");
                for (int i = 0; i < h.getAlojamentos().size(); i++) {
                    System.out.print(h.getAlojamentos().get(i).getEquipa().getNome() + (i < h.getAlojamentos().size() - 1 ? ", " : ""));
                }
            } else {
                System.out.print(" | Livre");
            }
            System.out.println();
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
            System.out.println("Falha na alocacao. Capacidade insuficiente, comitiva ja hospedada noutro hotel, ou dados invalidos.");
        }
    }

    private void checkoutHotel() {
        System.out.println("\n--- CHECKOUT DE HOTEL ---");
        List<Hotel> hoteis = logManager.getHoteis();
        boolean hasOccupied = false;
        for (Hotel h : hoteis) {
            if (!h.getAlojamentos().isEmpty()) {
                hasOccupied = true;
                System.out.print("  ID " + h.getId() + ": " + h.getNome() + " | Equipas: ");
                for (int i = 0; i < h.getAlojamentos().size(); i++) {
                    System.out.print(h.getAlojamentos().get(i).getEquipa().getNome() + (i < h.getAlojamentos().size() - 1 ? ", " : ""));
                }
                System.out.println();
            }
        }

        if (!hasOccupied) {
            System.out.println("Nao ha hoteis ocupados de momento.");
            return;
        }

        int hotelId = LeitorInput.lerInteiro("ID do Hotel para checkout: ");
        Hotel hotel = logManager.procurarHotelPorId(hotelId);
        if (hotel == null) {
            System.out.println("Hotel nao encontrado.");
            return;
        }

        List<Hotel.AlojamentoInfo> alocs = hotel.getAlojamentos();
        if (alocs.isEmpty()) {
            System.out.println("Este hotel nao tem equipas alojadas.");
            return;
        }

        Equipa selectedTeam = null;
        if (alocs.size() == 1) {
            selectedTeam = alocs.get(0).getEquipa();
        } else {
            System.out.println("Equipas alojadas neste hotel:");
            for (int i = 0; i < alocs.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + alocs.get(i).getEquipa().getNome());
            }
            int opt = LeitorInput.lerInteiro("Selecione a equipa para check-out (1 a " + alocs.size() + "): ");
            if (opt >= 1 && opt <= alocs.size()) {
                selectedTeam = alocs.get(opt - 1).getEquipa();
            } else {
                System.out.println("Opcao invalida.");
                return;
            }
        }

        boolean success = logManager.registarCheckoutEquipa(hotel, selectedTeam);
        if (success) {
            System.out.println("Checkout registado com sucesso para a equipa: " + selectedTeam.getNome());
        } else {
            System.out.println("Nao foi possivel realizar o checkout.");
        }
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

        int jogoId = LeitorInput.lerInteiro("ID do Jogo (ou 0 para Viagem Geral/Sem Jogo): ");
        Jogo jogo = null;
        Equipa equipa = null;

        if (jogoId != 0) {
            jogo = campManager.procurarJogoPorId(jogoId);
            if (jogo == null) {
                System.out.println("Jogo nao encontrado.");
                return;
            }

            System.out.println("Selecione a equipa que viaja:");
            System.out.println("1. " + (jogo.getHomeTeam() != null ? jogo.getHomeTeam().getNome() : "[A definir]"));
            System.out.println("2. " + (jogo.getAwayTeam() != null ? jogo.getAwayTeam().getNome() : "[A definir]"));
            int optEq = LeitorInput.lerInteiro("Opcao (1 ou 2): ");
            equipa = (optEq == 1) ? jogo.getHomeTeam() : jogo.getAwayTeam();
            if (equipa == null) {
                System.out.println("Equipa nao definida para este jogo.");
                return;
            }
        } else {
            String eqNome = LeitorInput.lerLinha("Nome da Equipa/Comitiva que viaja: ");
            equipa = campManager.procurarEquipaPorNome(eqNome);
            if (equipa == null) {
                System.out.println("Equipa nao encontrada.");
                return;
            }
        }

        String origem = LeitorInput.lerLinha("Origem (Hotel/Cidade): ");
        String destino = LeitorInput.lerLinha("Destino (Estadio/Cidade): ");
        String dataPartida = LeitorInput.lerLinha("Data/Hora Partida: ");
        String dataChegada = LeitorInput.lerLinha("Data/Hora Chegada Prevista: ");
        String meio = LeitorInput.lerLinha("Meio de Transporte (Autocarro/Aviao): ");

        Viagem v = logManager.planearViagem(jogo, equipa, origem, destino, dataPartida, dataChegada, meio);
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
            int currentOccupancy = h.getAlojamentos().stream().mapToInt(info -> info.getEquipa().getJogadores().size()).sum();
            System.out.println("ID " + h.getId() + ": " + h.getNome() + " | " + h.getLocalizacao() + " | Lotação: " + currentOccupancy + "/" + h.getCapacidadePessoas() + " pessoas");
            List<Hotel.AlojamentoInfo> alocs = h.getAlojamentos();
            if (!alocs.isEmpty()) {
                for (Hotel.AlojamentoInfo info : alocs) {
                    System.out.println("  -> Comitiva: " + info.getEquipa().getNome() + " (" + info.getEquipa().getJogadores().size() + "p) (Check-in: " + info.getCheckInDate() + ", Check-out: " + info.getCheckOutDate() + ")");
                }
            } else {
                System.out.println("  -> Livre");
            }
        }
    }

    private void listarViagens() {
        System.out.println("\n--- LISTA DE VIAGENS ---");
        List<Viagem> viagens = logManager.getViagens();
        if (viagens.isEmpty()) {
            System.out.println("Sem viagens planeadas.");
            return;
        }
        for (Viagem v : viagens) {
            System.out.println("Equipa: " + v.getEquipa().getNome() + " | De " + v.getOrigem() + " para " + v.getDestino() + " (" + v.getMeioTransporte() + ") em " + v.getDataPartida());
        }
    }
}
