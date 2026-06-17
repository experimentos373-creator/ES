package boundary;

import domain.*;
import manager.CampeonatoManager;
import util.LeitorInput;
import util.ValidadorDados;

import java.util.List;

/**
 * Menu de consola para os utilizadores com cargo de Administrador.
 */
public class MenuAdmin {
    private final CampeonatoManager manager;

    public MenuAdmin() {
        this.manager = CampeonatoManager.getInstance();
    }

    public void exibir() {
        while (true) {
            System.out.println("\n==================================");
            System.out.println("   MENU ADMINISTRADOR - WC 2026   ");
            System.out.println("==================================");
            System.out.println("1. Registar Selecao/Equipa");
            System.out.println("2. Adicionar Jogador a Equipa");
            System.out.println("3. Registar Estadio");
            System.out.println("4. Adicionar Setor a Estadio");
            System.out.println("5. Agendar Jogo");
            System.out.println("6. Finalizar Jogo (Introduzir Resultado)");
            System.out.println("7. Listar Equipas e Jogadores");
            System.out.println("8. Listar Jogos e Detalhes");
            System.out.println("0. Voltar");
            System.out.println("==================================");
            int opcao = LeitorInput.lerInteiro("Opcao: ");

            switch (opcao) {
                case 1: registarEquipa(); break;
                case 2: adicionarJogador(); break;
                case 3: registarEstadio(); break;
                case 4: adicionarSetor(); break;
                case 5: agendarJogo(); break;
                case 6: finalizarJogo(); break;
                case 7: listarEquipas(); break;
                case 8: listarJogos(); break;
                case 0: return;
                default: System.out.println("Opcao invalida.");
            }
        }
    }

    private void registarEquipa() {
        System.out.println("\n--- REGISTAR EQUIPA ---");
        String nome = LeitorInput.lerLinha("Nome da Selecao: ");
        if (nome.isEmpty()) {
            System.out.println("Nome invalido.");
            return;
        }
        if (manager.procurarEquipaPorNome(nome) != null) {
            System.out.println("Esta equipa ja esta registada.");
            return;
        }
        String treinador = LeitorInput.lerLinha("Nome do Selecionador: ");
        Equipa eq = new Equipa(nome, treinador);
        manager.registarEquipa(eq);
        System.out.println("Equipa " + nome + " registada com sucesso!");
        
        String grupo = LeitorInput.lerLinha("Nome do Grupo (Ex: Grupo A) [opcional]: ");
        if (!grupo.isEmpty()) {
            manager.registarEquipaNoGrupo(grupo, nome);
            System.out.println("Equipa associada ao " + grupo + ".");
        }
    }

    private void adicionarJogador() {
        System.out.println("\n--- ADICIONAR JOGADOR ---");
        String equipaNome = LeitorInput.lerLinha("Nome da Equipa: ");
        Equipa eq = manager.procurarEquipaPorNome(equipaNome);
        if (eq == null) {
            System.out.println("Equipa nao encontrada.");
            return;
        }

        int id = LeitorInput.lerInteiro("ID do Jogador: ");
        String nome = LeitorInput.lerLinha("Nome do Jogador: ");
        int numero = LeitorInput.lerInteiro("Numero da Camisola: ");
        String posicao = LeitorInput.lerLinha("Posicao (Guarda-Redes/Defesa/Medio/Avancado): ");

        Jogador jog = new Jogador(id, numero, nome, posicao, EstadoJogador.APTO);
        
        if (eq.adicionarJogador(jog)) {
            manager.registarEquipa(eq); 
            System.out.println("Jogador " + nome + " adicionado a selecao " + equipaNome + "!");
        } else {
            System.out.println("Falha ao adicionar jogador. Verifique o limite de 26 jogadores ou se o numero de camisola ja existe.");
        }
    }

    private void registarEstadio() {
        System.out.println("\n--- REGISTAR ESTADIO ---");
        String nome = LeitorInput.lerLinha("Nome do Estadio: ");
        if (nome.isEmpty()) return;
        String localizacao = LeitorInput.lerLinha("Localizacao: ");
        Estadio est = new Estadio(nome, localizacao);
        manager.registarEstadio(est);
        System.out.println("Estadio " + nome + " registado com sucesso!");
    }

    private void adicionarSetor() {
        System.out.println("\n--- ADICIONAR SETOR AO ESTADIO ---");
        String estNome = LeitorInput.lerLinha("Nome do Estadio: ");
        Estadio est = null;
        for (Estadio e : manager.getEstadios()) {
            if (e.getNome().equalsIgnoreCase(estNome)) {
                est = e;
                break;
            }
        }
        if (est == null) {
            System.out.println("Estadio nao encontrado.");
            return;
        }

        String nomeSetor = LeitorInput.lerLinha("Nome do Setor (Premium, Intermedia, Economica, Local): ");
        int capacidade = LeitorInput.lerInteiro("Capacidade do Setor: ");
        double precoBase = LeitorInput.lerDecimal("Preco Base (EUR): ");

        SetorEstadio setor = new SetorEstadio(nomeSetor, capacidade, precoBase);
        est.adicionarSetor(setor);
        manager.registarEstadio(est); 
        System.out.println("Setor " + nomeSetor + " adicionado ao estadio " + estNome + "!");
    }

    private void agendarJogo() {
        System.out.println("\n--- AGENDAR JOGO ---");
        int id = LeitorInput.lerInteiro("ID do Jogo: ");
        if (manager.procurarJogoPorId(id) != null) {
            System.out.println("Jogo com esse ID ja existe.");
            return;
        }

        String data = LeitorInput.lerLinha("Data (YYYY-MM-DD): ");
        if (!ValidadorDados.validarData(data)) {
            System.out.println("Data no formato incorreto.");
            return;
        }

        String hora = LeitorInput.lerLinha("Hora (HH:MM): ");
        if (!ValidadorDados.validarHora(hora)) {
            System.out.println("Hora no formato incorreto.");
            return;
        }

        String estNome = LeitorInput.lerLinha("Nome do Estadio: ");
        Estadio est = null;
        for (Estadio e : manager.getEstadios()) {
            if (e.getNome().equalsIgnoreCase(estNome)) {
                est = e;
                break;
            }
        }
        if (est == null) {
            System.out.println("Estadio nao encontrado.");
            return;
        }

        String homeName = LeitorInput.lerLinha("Equipa Casa (Home Team): ");
        Equipa home = manager.procurarEquipaPorNome(homeName);
        if (home == null) {
            System.out.println("Equipa Casa nao encontrada.");
            return;
        }

        String awayName = LeitorInput.lerLinha("Equipa Fora (Away Team): ");
        Equipa away = manager.procurarEquipaPorNome(awayName);
        if (away == null) {
            System.out.println("Equipa Fora nao encontrada.");
            return;
        }

        if (home.equals(away)) {
            System.out.println("As equipas do jogo devem ser diferentes.");
            return;
        }

        String fase = LeitorInput.lerLinha("Fase (Grupos, Dezasseis-avos, Oitavos, Quartos, Meias-Finais, Final): ");

        Jogo jogo = new Jogo(id, data, hora, est, home, away, fase, null, null);
        manager.registarJogo(jogo);
        System.out.println("Jogo agendado com sucesso!");
    }

    private void finalizarJogo() {
        System.out.println("\n--- FINALIZAR JOGO ---");
        int id = LeitorInput.lerInteiro("ID do Jogo a finalizar: ");
        Jogo jogo = manager.procurarJogoPorId(id);
        if (jogo == null) {
            System.out.println("Jogo nao encontrado.");
            return;
        }
        if (StatusJogo.FINALIZADO.equals(jogo.getStatus())) {
            System.out.println("Este jogo ja esta finalizado.");
            return;
        }

        System.out.println("Jogo: " + jogo.getHomeTeam().getNome() + " vs " + jogo.getAwayTeam().getNome());
        int goalsHome = LeitorInput.lerInteiro("Golos de " + jogo.getHomeTeam().getNome() + ": ");
        int goalsAway = LeitorInput.lerInteiro("Golos de " + jogo.getAwayTeam().getNome() + ": ");

        int penaltiesHome = -1;
        int penaltiesAway = -1;
        if (!"Grupos".equalsIgnoreCase(jogo.getPhase()) && goalsHome == goalsAway) {
            System.out.println("Jogo das eliminatorias empatado! Decisao por penalties.");
            penaltiesHome = LeitorInput.lerInteiro("Penalties marcados por " + jogo.getHomeTeam().getNome() + ": ");
            penaltiesAway = LeitorInput.lerInteiro("Penalties marcados por " + jogo.getAwayTeam().getNome() + ": ");
        }

        EstatisticaJogo stats = new EstatisticaJogo(50, 50, 8, 8, 4, 4);

        try {
            manager.finalizarJogoECorrerBracket(id, null, goalsHome, goalsAway, penaltiesHome, penaltiesAway, stats);
            Jogo finalizado = manager.procurarJogoPorId(id);
            System.out.println("Jogo finalizado! Vencedor: " + (finalizado.getWinner() != null ? finalizado.getWinner().getNome() : "Empate"));
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao finalizar jogo: " + e.getMessage());
        }
    }

    private void listarEquipas() {
        System.out.println("\n--- EQUIPAS E JOGADORES ---");
        List<Equipa> equipas = manager.getEquipas();
        if (equipas.isEmpty()) {
            System.out.println("Sem equipas registadas.");
            return;
        }
        for (Equipa eq : equipas) {
            System.out.println("Selecao: " + eq.getNome() + " | Treinador: " + eq.getTreinador());
            List<Jogador> plantel = eq.getJogadores();
            if (plantel.isEmpty()) {
                System.out.println("  (Sem jogadores)");
            } else {
                for (Jogador jog : plantel) {
                    System.out.println("  - Camisola " + jog.getNumeroCamisola() + ": " + jog.getNome() + " (" + jog.getPosicao() + ") | Estado: " + jog.getEstado());
                }
            }
        }
    }

    private void listarJogos() {
        System.out.println("\n--- CALENDARIO E DETALHES ---");
        List<Jogo> jogos = manager.getJogos();
        if (jogos.isEmpty()) {
            System.out.println("Sem jogos registados.");
            return;
        }
        for (Jogo j : jogos) {
            System.out.print("ID: " + j.getId() + " | " + j.getData() + " " + j.getHora() + " | [" + j.getPhase() + "] ");
            System.out.print(j.getHomeTeam().getNome() + " vs " + j.getAwayTeam().getNome());
            System.out.print(" | Estado: " + j.getStatus());
            if (StatusJogo.FINALIZADO.equals(j.getStatus())) {
                System.out.print(" | Resultado: " + j.getGoalsHome() + "-" + j.getGoalsAway());
                if (j.getPenaltiesHome() >= 0) {
                    System.out.print(" (Pen: " + j.getPenaltiesHome() + "-" + j.getPenaltiesAway() + ")");
                }
                System.out.print(" | Vencedor: " + (j.getWinner() != null ? j.getWinner().getNome() : "Empate"));
            }
            System.out.println();
        }
    }
}
