import boundary.*;
import domain.*;
import manager.*;

/**
 * Ponto de entrada principal da aplicacao de Gestao do Campeonato do Mundo de Futebol 2026.
 * Responsavel pela inicializacao, sementeira de dados e arranque do Menu Principal.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==================================");
        System.out.println("  SISTEMA GESTAO WC 2026 - v1.0  ");
        System.out.println("==================================");
        System.out.println("Inicializando sistema...");

        // Verificar se ha dados persistentes; caso contrario, semear dados iniciais
        if (isBaseVazia()) {
            System.out.println("Base de dados vazia. A semear dados iniciais...");
            semearDados();
            System.out.println("Sementeira concluida.");
        }

        System.out.println("A arrancar menu principal...");
        new MenuPrincipal().exibir();
    }

    /**
     * Verifica se todos os ficheiros de persistencia estao vazios ou inexistentes.
     */
    private static boolean isBaseVazia() {
        return AutenticacaoManager.getInstance().getUtilizadores().isEmpty();
    }

    /**
     * Semeia dados iniciais consistentes para permitir testes imediatos.
     */
    private static void semearDados() {
        // 1. Utilizadores (Atores do Sistema)
        AutenticacaoManager auth = AutenticacaoManager.getInstance();
        auth.registarUtilizador(new Utilizador("admin@fifa.com", "Administrador FIFA", TipoUtilizador.ADMIN, null));
        auth.registarUtilizador(new Utilizador("arbitragem@fifa.com", "Gestor Arbitragem", TipoUtilizador.GESTOR_ARBITRAGEM, null));
        auth.registarUtilizador(new Utilizador("logistica@fifa.com", "Gestor Logistica", TipoUtilizador.GESTOR_LOGISTICA, null));
        auth.registarUtilizador(new Utilizador("equipa@fifa.com", "Gestor Equipa", TipoUtilizador.GESTOR_EQUIPA, "Portugal"));
        auth.registarUtilizador(new Utilizador("bilheteira@fifa.com", "Gestor Bilheteira", TipoUtilizador.GESTOR_BILHETEIRA, null));
        auth.registarUtilizador(new Utilizador("adepto@wc2026.com", "Adepto Geral", TipoUtilizador.PUBLICO, null));

        // 2. Equipas e Jogadores (semente minima)
        CampeonatoManager camp = CampeonatoManager.getInstance();
        Equipa portugal = new Equipa("Portugal", "Roberto Martinez");
        Equipa brasil = new Equipa("Brasil", "Junior");
        Equipa franca = new Equipa("Franca", "Deschamps");
        Equipa cuba = new Equipa("Cuba", "Castillo");

        for (int i = 1; i <= 5; i++) {
            portugal.adicionarJogador(new Jogador(i, i, "Jogador P" + i, "Avancado", EstadoJogador.APTO));
            brasil.adicionarJogador(new Jogador(i + 10, i, "Jogador B" + i, "Defesa", EstadoJogador.APTO));
            franca.adicionarJogador(new Jogador(i + 20, i, "Jogador F" + i, "Medio", EstadoJogador.APTO));
            cuba.adicionarJogador(new Jogador(i + 30, i, "Jogador C" + i, "Guarda-Redes", EstadoJogador.APTO));
        }
        camp.registarEquipa(portugal);
        camp.registarEquipa(brasil);
        camp.registarEquipa(franca);
        camp.registarEquipa(cuba);

        // 3. Estadios e Setores
        Estadio luz = new Estadio("Estadio da Luz", "Lisboa");
        luz.adicionarSetor(new SetorEstadio("Premium", 100, 200.0));
        luz.adicionarSetor(new SetorEstadio("Intermedia", 200, 100.0));
        luz.adicionarSetor(new SetorEstadio("Economica", 500, 50.0));
        luz.adicionarSetor(new SetorEstadio("Local", 300, 25.0));
        camp.registarEstadio(luz);

        Estadio alvalade = new Estadio("Estadio de Alvalade", "Lisboa");
        alvalade.adicionarSetor(new SetorEstadio("Premium", 50, 250.0));
        alvalade.adicionarSetor(new SetorEstadio("Economica", 300, 40.0));
        camp.registarEstadio(alvalade);

        // 4. Jogos (Grupos e uma Oitavos para bracket)
        Jogo j1 = new Jogo(1, "2026-06-25", "15:00", luz, portugal, cuba, "Grupos", null, null);
        Jogo j2 = new Jogo(2, "2026-06-25", "18:00", luz, franca, brasil, "Grupos", null, null);
        Jogo j3 = new Jogo(10, "2026-07-02", "18:00", alvalade, null, null, "Oitavos", null, null);
        camp.registarJogo(j1);
        camp.registarJogo(j2);
        camp.registarJogo(j3);

        // 5. Arbitros (elegiveis para testes)
        ArbitragemManager arb = ArbitragemManager.getInstance();
        arb.registarArbitro(new Arbitro(1, "ref1@fifa.com", "Szymon Marciniak", "Polonia", TipoArbitro.PRINCIPAL));
        arb.registarArbitro(new Arbitro(2, "ref2@fifa.com", "Pau Cebrian Devis", "Espanha", TipoArbitro.ASSISTENTE));
        arb.registarArbitro(new Arbitro(3, "ref3@fifa.com", "Nicolas Danos", "Franca", TipoArbitro.ASSISTENTE));
        arb.registarArbitro(new Arbitro(4, "ref4@fifa.com", "Clement Turpin", "Franca", TipoArbitro.QUARTO));
        arb.registarArbitro(new Arbitro(5, "ref5@fifa.com", "Wilton Sampaio", "Brasil", TipoArbitro.VAR));

        // 6. Hoteis
        LogisticaManager log = LogisticaManager.getInstance();
        log.registarHotel(new Hotel(1, "Hotel Marques", "Lisboa", 30));
        log.registarHotel(new Hotel(2, "Hotel Estoril", "Estoril", 50));
    }
}
