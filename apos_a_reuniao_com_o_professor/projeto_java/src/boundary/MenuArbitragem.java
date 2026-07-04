package boundary;

import domain.*;
import manager.ArbitragemManager;
import manager.CampeonatoManager;
import util.LeitorInput;
import util.ValidadorDados;

import java.util.List;

/**
 * Menu de consola para utilizadores com cargo de Gestor de Arbitragem.
 */
public class MenuArbitragem {
    private final ArbitragemManager arbManager;
    private final CampeonatoManager campManager;

    public MenuArbitragem() {
        this.arbManager = ArbitragemManager.getInstance();
        this.campManager = CampeonatoManager.getInstance();
    }

    public void exibir() {
        while (true) {
            System.out.println("\n==================================");
            System.out.println("   MENU GESTAO ARBITRAGEM - WC    ");
            System.out.println("==================================");
            System.out.println("1. Registar Arbitro");
            System.out.println("2. Escalar Arbitro para Jogo");
            System.out.println("3. Avaliar Desempenho de Arbitros");
            System.out.println("4. Listar Arbitros e Scores FIFA");
            System.out.println("0. Voltar");
            System.out.println("==================================");
            int opcao = LeitorInput.lerInteiro("Opcao: ");

            switch (opcao) {
                case 1: registarArbitro(); break;
                case 2: escalarArbitrosParaJogo(); break;
                case 3: avaliarArbitros(); break;
                case 4: listarArbitros(); break;
                case 0: return;
                default: System.out.println("Opcao invalida.");
            }
        }
    }

    private void registarArbitro() {
        System.out.println("\n--- REGISTAR ARBITRO ---");
        int id = LeitorInput.lerInteiro("ID do Arbitro: ");
        if (arbManager.procurarArbitroPorId(id) != null) {
            System.out.println("Arbitro com esse ID ja existe.");
            return;
        }

        String email = LeitorInput.lerLinha("Email do Arbitro: ");
        if (!ValidadorDados.validarEmail(email)) {
            System.out.println("Email invalido.");
            return;
        }

        String nome = LeitorInput.lerLinha("Nome do Arbitro: ");
        String nacionalidade = LeitorInput.lerLinha("Nacionalidade do Arbitro: ");

        System.out.println("Escolha a Funcao do Arbitro:");
        System.out.println("1. PRINCIPAL");
        System.out.println("2. ASSISTENTE");
        System.out.println("3. VAR");
        System.out.println("4. QUARTO");
        int funcaoOp = LeitorInput.lerInteiro("Funcao: ");
        TipoArbitro tipo;
        switch (funcaoOp) {
            case 1: tipo = TipoArbitro.PRINCIPAL; break;
            case 2: tipo = TipoArbitro.ASSISTENTE; break;
            case 3: tipo = TipoArbitro.VAR; break;
            case 4: tipo = TipoArbitro.QUARTO; break;
            default:
                System.out.println("Opcao invalida. Registado como PRINCIPAL.");
                tipo = TipoArbitro.PRINCIPAL;
        }

        Arbitro ref = new Arbitro(id, email, nome, nacionalidade, tipo);
        arbManager.registarArbitro(ref);
        System.out.println("Arbitro " + nome + " registado com sucesso!");
    }

    private void escalarArbitrosParaJogo() {
        System.out.println("\n--- ESCALAR ARBITROS PARA JOGO ---");
        List<Jogo> jogos = campManager.getJogos();
        if (jogos.isEmpty()) {
            System.out.println("Nao existem jogos agendados.");
            return;
        }

        for (Jogo j : jogos) {
            System.out.println("  ID " + j.getId() + ": " + j.getHomeTeam().getNome() + " vs " + j.getAwayTeam().getNome() + " [" + j.getPhase() + "] | Estado: " + j.getStatus());
        }

        int jogoId = LeitorInput.lerInteiro("ID do Jogo a escalar: ");
        Jogo jogo = campManager.procurarJogoPorId(jogoId);
        if (jogo == null) {
            System.out.println("Jogo nao encontrado.");
            return;
        }

        if (StatusJogo.FINALIZADO.equals(jogo.getStatus())) {
            System.out.println("Nao e possivel escalar arbitros para um jogo ja finalizado.");
            return;
        }

        System.out.println("Arbitros Disponiveis:");
        List<Arbitro> refs = arbManager.getArbitros();
        for (Arbitro r : refs) {
            System.out.println("  - ID " + r.getId() + ": " + r.getNome() + " (" + r.getNacionalidade() + ") - Funcao: " + r.getTipo());
        }

        int refId = LeitorInput.lerInteiro("ID do Arbitro a escalar: ");
        Arbitro ref = arbManager.procurarArbitroPorId(refId);
        if (ref == null) {
            System.out.println("Arbitro nao encontrado.");
            return;
        }

        System.out.println("Funcao a atribuir no jogo:");
        System.out.println("1. PRINCIPAL");
        System.out.println("2. ASSISTENTE (1 ou 2)");
        System.out.println("3. VAR");
        System.out.println("4. QUARTO");
        int funcOp = LeitorInput.lerInteiro("Opcao: ");
        TipoArbitro tipo;
        switch (funcOp) {
            case 1: tipo = TipoArbitro.PRINCIPAL; break;
            case 2: tipo = TipoArbitro.ASSISTENTE; break;
            case 3: tipo = TipoArbitro.VAR; break;
            case 4: tipo = TipoArbitro.QUARTO; break;
            default:
                System.out.println("Funcao invalida.");
                return;
        }

        try {
            boolean success = arbManager.escalarArbitro(jogo, ref, tipo);
            if (success) {
                System.out.println("Arbitro " + ref.getNome() + " escalado com sucesso para o jogo ID " + jogoId + "!");
            } else {
                System.out.println("Falha ao escalar. Arbitro incompativel com as regras do jogo.");
            }
        } catch (IllegalStateException e) {
            System.out.println("Erro Critico: " + e.getMessage());
        }
    }

    private void avaliarArbitros() {
        System.out.println("\n--- AVALIAR DESEMPENHO DOS ARBITROS ---");
        List<Jogo> jogos = campManager.getJogos();
        boolean temFinalizados = false;
        for (Jogo j : jogos) {
            if (StatusJogo.FINALIZADO.equals(j.getStatus())) {
                temFinalizados = true;
                System.out.println("  ID " + j.getId() + ": " + j.getHomeTeam().getNome() + " vs " + j.getAwayTeam().getNome() + " (Resultado: " + j.getGoalsHome() + "-" + j.getGoalsAway() + ")");
            }
        }

        if (!temFinalizados) {
            System.out.println("Nao existem jogos finalizados para avaliar.");
            return;
        }

        int jogoId = LeitorInput.lerInteiro("ID do Jogo finalizado a avaliar: ");
        Jogo jogo = campManager.procurarJogoPorId(jogoId);
        if (jogo == null) {
            System.out.println("Jogo nao encontrado.");
            return;
        }

        if (!StatusJogo.FINALIZADO.equals(jogo.getStatus())) {
            System.out.println("Este jogo ainda nao terminou. Nao pode ser avaliado.");
            return;
        }

        EscalaoArbitral escala = jogo.getEscalaArbitros();
        if (escala == null) {
            System.out.println("Este jogo nao possui escala de arbitros para avaliar.");
            return;
        }

        int pRating = -1, a1Rating = -1, a2Rating = -1, qRating = -1, vRating = -1;

        if (escala.getPrincipal() != null) {
            pRating = LeitorInput.lerInteiro("Avaliacao para Principal " + escala.getPrincipal().getNome() + " (1-5 estrelas): ");
        }
        if (escala.getAssistente1() != null) {
            a1Rating = LeitorInput.lerInteiro("Avaliacao para Assistente 1 " + escala.getAssistente1().getNome() + " (1-5 estrelas): ");
        }
        if (escala.getAssistente2() != null) {
            a2Rating = LeitorInput.lerInteiro("Avaliacao para Assistente 2 " + escala.getAssistente2().getNome() + " (1-5 estrelas): ");
        }
        if (escala.getQuarto() != null) {
            qRating = LeitorInput.lerInteiro("Avaliacao para Quarto Arbitro " + escala.getQuarto().getNome() + " (1-5 estrelas): ");
        }
        if (escala.getVar() != null) {
            vRating = LeitorInput.lerInteiro("Avaliacao para VAR " + escala.getVar().getNome() + " (1-5 estrelas): ");
        }

        try {
            arbManager.avaliarDesempenho(jogo, pRating, a1Rating, a2Rating, qRating, vRating);
            System.out.println("Avaliacoes de desempenho registadas! Scores FIFA recalculados.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void listarArbitros() {
        System.out.println("\n--- LISTA DE ARBITROS E CLASSIFICACAO ---");
        List<Arbitro> refs = arbManager.getArbitros();
        if (refs.isEmpty()) {
            System.out.println("Sem arbitros registados.");
            return;
        }
        for (Arbitro r : refs) {
            System.out.println("ID " + r.getId() + ": " + r.getNome() + " (" + r.getNacionalidade() + ") | Funcao: " + r.getTipo() + " | Score FIFA: " + r.getScoreFIFA() + "/100 (Avaliacoes: " + r.getTotalAvaliacoes() + ") | Estado: " + r.getEstado());
        }
    }
}
