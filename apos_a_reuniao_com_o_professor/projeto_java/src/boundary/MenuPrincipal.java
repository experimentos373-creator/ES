package boundary;

import domain.*;
import manager.*;
import util.LeitorInput;

/**
 * Menu principal de consola que centraliza o acesso ao sistema
 * e redireciona para os menus especificos de cada cargo (RBAC).
 */
public class MenuPrincipal {
    private final AutenticacaoManager authManager;

    public MenuPrincipal() {
        this.authManager = AutenticacaoManager.getInstance();
    }

    public void exibir() {
        while (true) {
            System.out.println("\n==================================");
            System.out.println("  SISTEMA GESTAO WC 2026 - LOGIN  ");
            System.out.println("==================================");

            if (!authManager.isAutenticado()) {
                System.out.println("1. Login");
                System.out.println("2. Entrar como Publico (Sem Login)");
                System.out.println("0. Sair");
                System.out.println("==================================");
                int opcao = LeitorInput.lerInteiro("Opcao: ");

                switch (opcao) {
                    case 1: login(); break;
                    case 2: loginPublico(); break;
                    case 0: System.out.println("Adeus!"); return;
                    default: System.out.println("Opcao invalida.");
                }
            } else {
                Utilizador u = authManager.getUtilizadorAtual();
                System.out.println("Bem-vindo, " + u.getNome() + " [" + u.getCargo() + "]");
                System.out.println("==================================");

                // Menu adaptativo conforme o cargo (RBAC)
                switch (u.getCargo()) {
                    case ADMIN:
                        menuAdmin(); break;
                    case GESTOR_ARBITRAGEM:
                        menuArbitragem(); break;
                    case GESTOR_LOGISTICA:
                        menuLogistica(); break;
                    case GESTOR_EQUIPA:
                        menuGestorEquipa(); break;
                    case PUBLICO:
                        menuPublico(); break;
                    default:
                        System.out.println("Cargo desconhecido.");
                        authManager.logout();
                }
            }
        }
    }

    private void login() {
        String email = LeitorInput.lerLinha("Email: ");
        boolean ok = authManager.autenticar(email);
        if (!ok) {
            System.out.println("Utilizador nao encontrado. Tente os dados de sementeira (ex: admin@fifa.com).");
        } else {
            System.out.println("Autenticado com sucesso!");
        }
    }

    private void loginPublico() {
        Utilizador publico = new Utilizador("publico@wc2026.com", "Publico Geral", TipoUtilizador.PUBLICO, null);
        authManager.registarUtilizador(publico);
        authManager.autenticar("publico@wc2026.com");
        System.out.println("Entrou como Publico.");
    }

    private void menuAdmin() {
        while (true) {
            System.out.println("\n--- MENU PRINCIPAL (ADMINISTRADOR) ---");
            System.out.println("1. Gestao Geral e Calendario (Menu Admin)");
            System.out.println("2. Gestao de Arbitragem (Menu Arbitragem)");
            System.out.println("3. Gestao de Logistica (Menu Logistica)");
            System.out.println("4. Logout");
            System.out.println("==================================");
            int opcao = LeitorInput.lerInteiro("Opcao: ");

            switch (opcao) {
                case 1: new MenuAdmin().exibir(); break;
                case 2: new MenuArbitragem().exibir(); break;
                case 3: new MenuLogistica().exibir(); break;
                case 4: authManager.logout(); System.out.println("Logout efetuado."); return;
                default: System.out.println("Opcao invalida.");
            }
        }
    }

    private void menuArbitragem() {
        while (true) {
            System.out.println("\n--- MENU PRINCIPAL (GESTOR ARBITRAGEM) ---");
            System.out.println("1. Gestao de Arbitragem (Menu Arbitragem)");
            System.out.println("2. Logout");
            System.out.println("==================================");
            int opcao = LeitorInput.lerInteiro("Opcao: ");

            switch (opcao) {
                case 1: new MenuArbitragem().exibir(); break;
                case 2: authManager.logout(); System.out.println("Logout efetuado."); return;
                default: System.out.println("Opcao invalida.");
            }
        }
    }

    private void menuLogistica() {
        while (true) {
            System.out.println("\n--- MENU PRINCIPAL (GESTOR LOGISTICA) ---");
            System.out.println("1. Gestao de Logistica (Menu Logistica)");
            System.out.println("2. Logout");
            System.out.println("==================================");
            int opcao = LeitorInput.lerInteiro("Opcao: ");

            switch (opcao) {
                case 1: new MenuLogistica().exibir(); break;
                case 2: authManager.logout(); System.out.println("Logout efetuado."); return;
                default: System.out.println("Opcao invalida.");
            }
        }
    }

    private void menuGestorEquipa() {
        while (true) {
            System.out.println("\n--- MENU PRINCIPAL (GESTOR DE EQUIPA) ---");
            System.out.println("1. Consultar Calendario e Classificacao (Menu Adepto)");
            System.out.println("2. Logout");
            System.out.println("==================================");
            int opcao = LeitorInput.lerInteiro("Opcao: ");

            switch (opcao) {
                case 1: new MenuAdepto().exibir(); break;
                case 2: authManager.logout(); System.out.println("Logout efetuado."); return;
                default: System.out.println("Opcao invalida.");
            }
        }
    }

    private void menuPublico() {
        while (true) {
            System.out.println("\n--- MENU PRINCIPAL (PUBLICO) ---");
            System.out.println("1. Consultar Calendario e Bilhetica (Menu Adepto)");
            System.out.println("2. Logout");
            System.out.println("==================================");
            int opcao = LeitorInput.lerInteiro("Opcao: ");

            switch (opcao) {
                case 1: new MenuAdepto().exibir(); break;
                case 2: authManager.logout(); System.out.println("Logout efetuado."); return;
                default: System.out.println("Opcao invalida.");
            }
        }
    }
}
