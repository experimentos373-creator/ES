package boundary;

import domain.*;
import manager.CampeonatoManager;
import util.LeitorInput;

import java.util.List;

/**
 * Menu de consola para utilizadores com cargo de Publico/Adepto.
 */
public class MenuAdepto {
    private final CampeonatoManager manager;

    public MenuAdepto() {
        this.manager = CampeonatoManager.getInstance();
    }

    public void exibir() {
        while (true) {
            System.out.println("\n==================================");
            System.out.println("     MENU ADEPTO - WC 2026        ");
            System.out.println("==================================");
            System.out.println("1. Consultar Calendario de Jogos");
            System.out.println("2. Consultar Classificacao dos Grupos");
            System.out.println("3. Visualizar Bracket Eliminatorio");
            System.out.println("4. Comprar Bilhete para Jogo");
            System.out.println("0. Voltar");
            System.out.println("==================================");
            int opcao = LeitorInput.lerInteiro("Opcao: ");

            switch (opcao) {
                case 1: consultarCalendario(); break;
                case 2: consultarClassificacao(); break;
                case 3: visualizarBracket(); break;
                case 4: comprarBilhete(); break;
                case 0: return;
                default: System.out.println("Opcao invalida.");
            }
        }
    }

    private void consultarCalendario() {
        System.out.println("\n--- CALENDARIO DE JOGOS (PUBLICO) ---");
        List<Jogo> jogos = manager.getJogos();
        if (jogos.isEmpty()) {
            System.out.println("Sem jogos agendados.");
            return;
        }
        for (Jogo j : jogos) {
            System.out.print("ID: " + j.getId() + " | " + j.getData() + " " + j.getHora() + " | [" + j.getPhase() + "] ");
            System.out.print(j.getHomeTeam().getNome() + " vs " + j.getAwayTeam().getNome());
            System.out.print(" | Estado: " + j.getStatus());
            if (StatusJogo.FINALIZADO.equals(j.getStatus())) {
                System.out.print(" | Resultado: " + j.getGoalsHome() + "-" + j.getGoalsAway());
            }
            
            // Regra etica de sigilo: getEscalaArbitrosPublica() retorna null se o jogo for AGENDADO
            EscalaoArbitral escala = j.getEscalaArbitrosPublica();
            if (escala != null) {
                System.out.print(" | Arbitro Principal: " + escala.getPrincipal().getNome());
            } else {
                System.out.print(" | Arbitro: [CONFIDENCIAL (Jogo Agendado)]");
            }
            System.out.println();
        }
    }

    private void consultarClassificacao() {
        System.out.println("\n--- CLASSIFICACAO DOS GRUPOS ---");
        String[] grupos = {"Grupo A", "Grupo B", "Grupo C", "Grupo D", "Grupo E", "Grupo F", "Grupo G", "Grupo H"};
        for (String g : grupos) {
            List<ClassificacaoLinha> linhas = manager.calcularClassificacaoGrupo(g);
            if (linhas.isEmpty()) continue;
            System.out.println("\n" + g + ":");
            System.out.println("---------------------------------------------------------");
            System.out.printf("%-15s | %-3s | %-3s | %-3s | %-3s | %-4s | %-4s | %-3s\n",
                    "Equipa", "PTS", "J", "V", "E", "GM", "GS", "DG");
            System.out.println("---------------------------------------------------------");
            for (ClassificacaoLinha l : linhas) {
                System.out.printf("%-15s | %-3d | %-3d | %-3d | %-3d | %-4d | %-4d | %-3d\n",
                        l.getEquipa().getNome(), l.getPontos(), l.getJogados(),
                        l.getVitorias(), l.getEmpates(), l.getGolosMarcados(),
                        l.getGolosSofridos(), l.getSaldoGolos());
            }
        }
    }

    private void visualizarBracket() {
        System.out.println("\n--- BRACKET ELIMINATORIO ---");
        List<Jogo> jogos = manager.getJogos();
        String[] fases = {"Oitavos", "Quartos", "Meias-Finais", "Final"};
        
        for (String fase : fases) {
            System.out.println("\n--- " + fase.toUpperCase() + " ---");
            boolean temJogos = false;
            for (Jogo j : jogos) {
                if (fase.equalsIgnoreCase(j.getPhase())) {
                    temJogos = true;
                    String home = j.getHomeTeam() != null ? j.getHomeTeam().getNome() : "[A definir]";
                    String away = j.getAwayTeam() != null ? j.getAwayTeam().getNome() : "[A definir]";
                    String resultado = "";
                    if (StatusJogo.FINALIZADO.equals(j.getStatus())) {
                        resultado = " (" + j.getGoalsHome() + "-" + j.getGoalsAway();
                        if (j.getPenaltiesHome() >= 0) {
                            resultado += " Pen: " + j.getPenaltiesHome() + "-" + j.getPenaltiesAway();
                        }
                        resultado += ")";
                    }
                    System.out.println("  Jogo ID " + j.getId() + ": " + home + " vs " + away + resultado);
                }
            }
            if (!temJogos) {
                System.out.println("  (Sem jogos agendados nesta fase)");
            }
        }
    }

    private void comprarBilhete() {
        System.out.println("\n--- COMPRAR BILHETE ---");
        int jogoId = LeitorInput.lerInteiro("ID do Jogo: ");
        Jogo jogo = manager.procurarJogoPorId(jogoId);
        if (jogo == null) {
            System.out.println("Jogo nao encontrado.");
            return;
        }

        if (StatusJogo.FINALIZADO.equals(jogo.getStatus())) {
            System.out.println("Este jogo ja terminou.");
            return;
        }

        Estadio est = jogo.getEstadio();
        if (est == null || est.getSetores().isEmpty()) {
            System.out.println("Nao existem setores configurados para este estadio.");
            return;
        }

        System.out.println("Setores disponiveis no " + est.getNome() + ":");
        for (SetorEstadio s : est.getSetores()) {
            int disponiveis = s.getCapacidadeTotal() - s.getBilhetesVendidos();
            System.out.println("  - " + s.getNome() + " | Preco: " + s.getPrecoBase() + " EUR | Disponiveis: " + disponiveis);
        }

        String nomeSetor = LeitorInput.lerLinha("Escolha o Setor: ");
        SetorEstadio setor = est.getSetorPorNome(nomeSetor);
        if (setor == null) {
            System.out.println("Setor invalido.");
            return;
        }

        int quantidade = LeitorInput.lerInteiro("Quantidade de bilhetes (Limite: max 4): ");
        
        // Validacao da regra de etica anti-bot (Publico)
        if (quantidade <= 0 || quantidade > 4) {
            System.out.println("Compra rejeitada. O limite maximo de compra por transacao e de 4 bilhetes para combater a especulacao de precos.");
            return;
        }

        if (setor.venderBilhete(quantidade)) {
            manager.registarEstadio(est); // Atualiza os dados persistidos
            double total = quantidade * setor.getPrecoBase();
            System.out.println("Compra realizada! Adquiriu " + quantidade + " bilhete(s) para o setor " + setor.getNome() + ".");
            System.out.printf("Total Pago: %.2f EUR\n", total);
        } else {
            System.out.println("Compra falhou. Capacidade do setor excedida.");
        }
    }
}
