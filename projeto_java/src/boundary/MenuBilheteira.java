package boundary;

import domain.*;
import manager.BilheteiraManager;
import manager.CampeonatoManager;
import util.LeitorInput;

import java.util.List;

/**
 * Menu de consola para Bilhetica (acessivel ao publico/adepto).
 */
public class MenuBilheteira {
    private final BilheteiraManager bilManager;
    private final CampeonatoManager campManager;

    public MenuBilheteira() {
        this.bilManager = BilheteiraManager.getInstance();
        this.campManager = CampeonatoManager.getInstance();
    }

    public void exibir() {
        while (true) {
            System.out.println("\n==================================");
            System.out.println("   MENU BILHETICA - WC 2026       ");
            System.out.println("==================================");
            System.out.println("1. Listar Jogos Agendados");
            System.out.println("2. Consultar Precos e Setores");
            System.out.println("3. Comprar Bilhetes");
            System.out.println("0. Voltar");
            System.out.println("==================================");
            int opcao = LeitorInput.lerInteiro("Opcao: ");

            switch (opcao) {
                case 1: listarJogos(); break;
                case 2: consultarSetores(); break;
                case 3: comprarBilhetes(); break;
                case 0: return;
                default: System.out.println("Opcao invalida.");
            }
        }
    }

    private void listarJogos() {
        System.out.println("\n--- JOGOS AGENDADOS ---");
        List<Jogo> jogos = campManager.getJogos();
        if (jogos.isEmpty()) {
            System.out.println("Sem jogos disponiveis.");
            return;
        }
        for (Jogo j : jogos) {
            if (!StatusJogo.FINALIZADO.equals(j.getStatus())) {
                System.out.println("  ID " + j.getId() + ": " + j.getData() + " " + j.getHora() + " | " + j.getHomeTeam().getNome() + " vs " + j.getAwayTeam().getNome() + " | " + j.getEstadio().getNome());
            }
        }
    }

    private void consultarSetores() {
        System.out.println("\n--- CONSULTAR SETORES ---");
        int jogoId = LeitorInput.lerInteiro("ID do Jogo: ");
        Jogo jogo = campManager.procurarJogoPorId(jogoId);
        if (jogo == null || jogo.getEstadio() == null) {
            System.out.println("Jogo ou estadio nao encontrado.");
            return;
        }

        Estadio est = jogo.getEstadio();
        System.out.println("Estadio: " + est.getNome());
        List<SetorEstadio> setores = est.getSetores();
        if (setores.isEmpty()) {
            System.out.println("Nao existem setores configurados.");
            return;
        }
        for (SetorEstadio s : setores) {
            int disp = s.getCapacidadeTotal() - s.getBilhetesVendidos();
            System.out.println("  - " + s.getNome() + " | Preco: " + s.getPrecoBase() + " EUR | Disponiveis: " + disp + "/" + s.getCapacidadeTotal());
        }
    }

    private void comprarBilhetes() {
        System.out.println("\n--- COMPRAR BILHETES ---");
        int jogoId = LeitorInput.lerInteiro("ID do Jogo: ");
        Jogo jogo = campManager.procurarJogoPorId(jogoId);
        if (jogo == null || jogo.getEstadio() == null) {
            System.out.println("Jogo ou estadio invalido.");
            return;
        }

        if (StatusJogo.FINALIZADO.equals(jogo.getStatus())) {
            System.out.println("Este jogo ja terminou.");
            return;
        }

        String nomeSetor = LeitorInput.lerLinha("Nome do Setor: ");
        int quantidade = LeitorInput.lerInteiro("Quantidade (max 4 por transacao): ");

        boolean ok = bilManager.venderBilhete(jogo, nomeSetor, quantidade);
        if (ok) {
            SetorEstadio setor = jogo.getEstadio().getSetorPorNome(nomeSetor);
            double total = quantidade * setor.getPrecoBase();
            System.out.println("Compra efetuada! " + quantidade + " bilhete(s) no setor " + nomeSetor + ". Total: " + total + " EUR");
        } else {
            System.out.println("Compra rejeitada. Verifique: limite de 4 bilhetes, quantidade valida, ou lotacao esgotada.");
        }
    }
}
