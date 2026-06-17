package boundary.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import domain.*;
import manager.*;

public class MainGUI extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Seeding database if empty (identical to CLI startup)
        if (isBaseVazia()) {
            semearDados();
        }

        // Initialize UI
        showLoginScreen();

        primaryStage.setTitle("Gestão WC 2026 - Premium GUI");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(750);
        primaryStage.show();
    }

    private boolean isBaseVazia() {
        return AutenticacaoManager.getInstance().getUtilizadores().isEmpty();
    }

    /**
     * DADOS DE DEMONSTRAÇÃO (SEED) — Idempotente, só corre se a base estiver vazia.
     * Todos os dados criados aqui servem exclusivamente para testes visuais e funcionais.
     * Para produção, substituir por dados reais ou remover este método.
     */
    private void semearDados() {
        // [SEED] 1. Utilizadores (Atores do Sistema)
        AutenticacaoManager auth = AutenticacaoManager.getInstance();
        auth.registarUtilizador(new Utilizador("admin@fifa.com", "Administrador FIFA", TipoUtilizador.ADMIN, null));
        auth.registarUtilizador(new Utilizador("arbitragem@fifa.com", "Gestor Arbitragem", TipoUtilizador.GESTOR_ARBITRAGEM, null));
        auth.registarUtilizador(new Utilizador("logistica@fifa.com", "Gestor Logistica", TipoUtilizador.GESTOR_LOGISTICA, null));
        auth.registarUtilizador(new Utilizador("equipa@fifa.com", "Gestor Equipa", TipoUtilizador.GESTOR_EQUIPA, "Portugal"));
        auth.registarUtilizador(new Utilizador("adepto@wc2026.com", "Adepto Geral", TipoUtilizador.PUBLICO, null));

        // [SEED] 2. Equipas e Jogadores (semente mínima e detalhada para Portugal)
        CampeonatoManager camp = CampeonatoManager.getInstance();
        
        Equipa portugal = new Equipa("Portugal", "Roberto Martinez");
        Equipa brasil = new Equipa("Brasil", "Dorival Junior");
        Equipa franca = new Equipa("França", "Didier Deschamps");
        Equipa espanha = new Equipa("Espanha", "Luis de la Fuente");
        Equipa argentina = new Equipa("Argentina", "Lionel Scaloni");
        Equipa alemanha = new Equipa("Alemanha", "Julian Nagelsmann");
        Equipa inglaterra = new Equipa("Inglaterra", "Gareth Southgate");
        Equipa cuba = new Equipa("Cuba", "Yunielys Castillo");

        // [SEED] Jogadores de Portugal (23 Jogadores: 11 Titulares, 12 Suplentes)
        // Titulares
        Jogador j1 = new Jogador(1, 1, "Diogo Costa", "Guarda-Redes", EstadoJogador.APTO); j1.setStarter(true); j1.setEnergy(100); portugal.adicionarJogador(j1);
        Jogador j2 = new Jogador(2, 2, "Nélson Semedo", "Defesa", EstadoJogador.APTO); j2.setStarter(true); j2.setEnergy(90); j2.setYellowCards(1); portugal.adicionarJogador(j2);
        Jogador j3 = new Jogador(3, 3, "Pepe", "Defesa", EstadoJogador.APTO); j3.setStarter(true); j3.setEnergy(82); j3.addInjury("Lesão Muscular na Coxa (Outubro 2025)"); portugal.adicionarJogador(j3);
        Jogador j4 = new Jogador(4, 4, "Rúben Dias", "Defesa", EstadoJogador.APTO); j4.setStarter(true); j4.setEnergy(98); portugal.adicionarJogador(j4);
        Jogador j5 = new Jogador(5, 19, "Nuno Mendes", "Defesa", EstadoJogador.APTO); j5.setStarter(true); j5.setEnergy(88); j5.addInjury("Entorse do Tornozelo (Janeiro 2026)"); portugal.adicionarJogador(j5);
        Jogador j6 = new Jogador(6, 6, "João Palhinha", "Médio", EstadoJogador.APTO); j6.setStarter(true); j6.setEnergy(95); j6.setYellowCards(2); portugal.adicionarJogador(j6);
        Jogador j7 = new Jogador(7, 8, "Bruno Fernandes", "Médio", EstadoJogador.APTO); j7.setStarter(true); j7.setEnergy(97); j7.setGoals(2); j7.setAssists(3); portugal.adicionarJogador(j7);
        Jogador j8 = new Jogador(8, 10, "Bernardo Silva", "Médio", EstadoJogador.APTO); j8.setStarter(true); j8.setEnergy(94); j8.setGoals(1); j8.setAssists(1); portugal.adicionarJogador(j8);
        Jogador j9 = new Jogador(9, 7, "Cristiano Ronaldo", "Avançado", EstadoJogador.APTO); j9.setStarter(true); j9.setEnergy(92); j9.setGoals(4); j9.setAssists(1); portugal.adicionarJogador(j9);
        Jogador j10 = new Jogador(10, 17, "Rafael Leão", "Avançado", EstadoJogador.APTO); j10.setStarter(true); j10.setEnergy(89); j10.setGoals(1); j10.addInjury("Mialgia de esforço (Fevereiro 2026)"); portugal.adicionarJogador(j10);
        Jogador j11 = new Jogador(11, 11, "João Félix", "Avançado", EstadoJogador.APTO); j11.setStarter(true); j11.setEnergy(91); j11.setGoals(1); portugal.adicionarJogador(j11);

        // Suplentes
        Jogador j12 = new Jogador(12, 12, "Rui Patrício", "Guarda-Redes", EstadoJogador.APTO); portugal.adicionarJogador(j12);
        Jogador j13 = new Jogador(13, 22, "José Sá", "Guarda-Redes", EstadoJogador.APTO); portugal.adicionarJogador(j13);
        Jogador j14 = new Jogador(14, 5, "Diogo Dalot", "Defesa", EstadoJogador.APTO); portugal.adicionarJogador(j14);
        Jogador j15 = new Jogador(15, 14, "Gonçalo Inácio", "Defesa", EstadoJogador.APTO); portugal.adicionarJogador(j15);
        Jogador j16 = new Jogador(16, 24, "António Silva", "Defesa", EstadoJogador.APTO); portugal.adicionarJogador(j16);
        Jogador j17 = new Jogador(17, 13, "Danilo Pereira", "Defesa", EstadoJogador.APTO); portugal.adicionarJogador(j17);
        Jogador j18 = new Jogador(18, 15, "João Neves", "Médio", EstadoJogador.APTO); portugal.adicionarJogador(j18);
        Jogador j19 = new Jogador(19, 16, "Vitinha", "Médio", EstadoJogador.APTO); portugal.adicionarJogador(j19);
        Jogador j20 = new Jogador(20, 23, "Otávio", "Médio", EstadoJogador.APTO); portugal.adicionarJogador(j20);
        Jogador j21 = new Jogador(21, 21, "Diogo Jota", "Avançado", EstadoJogador.APTO); j21.setGoals(2); portugal.adicionarJogador(j21);
        Jogador j22 = new Jogador(22, 9, "Gonçalo Ramos", "Avançado", EstadoJogador.LESIONADO); j22.setEnergy(45); j22.setEstado(EstadoJogador.LESIONADO); j22.addInjury("Estiramento Ligamentar (Junho 2026)"); portugal.adicionarJogador(j22);
        Jogador j23 = new Jogador(23, 18, "Rúben Neves", "Médio", EstadoJogador.SUSPENSO); j23.setEstado(EstadoJogador.SUSPENSO); portugal.adicionarJogador(j23);

        // [SEED] Jogadores genéricos para outras equipas (dados de demonstração)
        for (int i = 1; i <= 5; i++) {
            brasil.adicionarJogador(new Jogador(100 + i, i, "Brasileiro " + i, "Avançado", EstadoJogador.APTO));
            franca.adicionarJogador(new Jogador(200 + i, i, "Francês " + i, "Defesa", EstadoJogador.APTO));
            espanha.adicionarJogador(new Jogador(300 + i, i, "Espanhol " + i, "Médio", EstadoJogador.APTO));
            argentina.adicionarJogador(new Jogador(400 + i, i, "Argentino " + i, "Avançado", EstadoJogador.APTO));
            alemanha.adicionarJogador(new Jogador(500 + i, i, "Alemão " + i, "Defesa", EstadoJogador.APTO));
            inglaterra.adicionarJogador(new Jogador(600 + i, i, "Inglês " + i, "Médio", EstadoJogador.APTO));
            cuba.adicionarJogador(new Jogador(700 + i, i, "Cubano " + i, "Avançado", EstadoJogador.APTO));
        }

        camp.registarEquipa(portugal);
        camp.registarEquipa(brasil);
        camp.registarEquipa(franca);
        camp.registarEquipa(espanha);
        camp.registarEquipa(argentina);
        camp.registarEquipa(alemanha);
        camp.registarEquipa(inglaterra);
        camp.registarEquipa(cuba);

        // Registar nos Grupos (para tabela dinâmica de grupos)
        camp.registarEquipaNoGrupo("Grupo A", "Portugal");
        camp.registarEquipaNoGrupo("Grupo A", "Cuba");
        camp.registarEquipaNoGrupo("Grupo A", "Espanha");
        camp.registarEquipaNoGrupo("Grupo A", "Alemanha");
        
        camp.registarEquipaNoGrupo("Grupo B", "Brasil");
        camp.registarEquipaNoGrupo("Grupo B", "França");
        camp.registarEquipaNoGrupo("Grupo B", "Argentina");
        camp.registarEquipaNoGrupo("Grupo B", "Inglaterra");

        // [SEED] 3. Estádios e Setores
        Estadio luz = new Estadio("Estádio da Luz", "Lisboa");
        luz.adicionarSetor(new SetorEstadio("Premium", 5000, 200.0));
        luz.adicionarSetor(new SetorEstadio("Intermedia", 15000, 100.0));
        luz.adicionarSetor(new SetorEstadio("Economica", 35000, 50.0));
        luz.adicionarSetor(new SetorEstadio("Local", 10000, 25.0));
        camp.registarEstadio(luz);

        Estadio alvalade = new Estadio("Estádio Alvalade", "Lisboa");
        alvalade.adicionarSetor(new SetorEstadio("Premium", 4000, 250.0));
        alvalade.adicionarSetor(new SetorEstadio("Intermedia", 10000, 120.0));
        alvalade.adicionarSetor(new SetorEstadio("Economica", 25000, 60.0));
        alvalade.adicionarSetor(new SetorEstadio("Local", 11000, 30.0));
        camp.registarEstadio(alvalade);

        Estadio dragao = new Estadio("Estádio do Dragão", "Porto");
        dragao.adicionarSetor(new SetorEstadio("Premium", 4000, 180.0));
        dragao.adicionarSetor(new SetorEstadio("Intermedia", 10000, 90.0));
        dragao.adicionarSetor(new SetorEstadio("Economica", 25000, 45.0));
        dragao.adicionarSetor(new SetorEstadio("Local", 11000, 20.0));
        camp.registarEstadio(dragao);

        // [SEED] 4. Árbitros
        ArbitragemManager arb = ArbitragemManager.getInstance();
        arb.registarArbitro(new Arbitro(1, "artur@fifa.com", "Artur Soares Dias", "Portugal", TipoArbitro.PRINCIPAL));
        arb.registarArbitro(new Arbitro(2, "szymon@fifa.com", "Szymon Marciniak", "Polónia", TipoArbitro.PRINCIPAL));
        arb.registarArbitro(new Arbitro(3, "clement@fifa.com", "Clement Turpin", "França", TipoArbitro.PRINCIPAL));
        arb.registarArbitro(new Arbitro(4, "wilton@fifa.com", "Wilton Sampaio", "Brasil", TipoArbitro.PRINCIPAL));
        arb.registarArbitro(new Arbitro(5, "danos@fifa.com", "Nicolas Danos", "França", TipoArbitro.ASSISTENTE));
        arb.registarArbitro(new Arbitro(6, "devis@fifa.com", "Pau Cebrian Devis", "Espanha", TipoArbitro.ASSISTENTE));
        arb.registarArbitro(new Arbitro(7, "ref7@fifa.com", "Roberto Alonso", "Espanha", TipoArbitro.ASSISTENTE));
        arb.registarArbitro(new Arbitro(8, "ref8@fifa.com", "Michael Oliver", "Inglaterra", TipoArbitro.QUARTO));
        arb.registarArbitro(new Arbitro(9, "ref9@fifa.com", "Hernan Maidana", "Argentina", TipoArbitro.VAR));

        // [SEED] 5. Hotéis e Alojamento (Logística)
        LogisticaManager log = LogisticaManager.getInstance();
        Hotel h1 = new Hotel(1, "Vila Galé Collection", "Faro, Portugal", 100);
        Hotel h2 = new Hotel(2, "Pestana Palace", "Lisboa, Portugal", 150);
        Hotel h3 = new Hotel(3, "Hotel Marriott", "Lisboa, Portugal", 200);
        Hotel h4 = new Hotel(4, "Tivoli Avenida", "Lisboa, Portugal", 120);
        Hotel h5 = new Hotel(5, "Intercontinental Porto", "Porto, Portugal", 180);
        Hotel h6 = new Hotel(6, "Sheraton Porto", "Porto, Portugal", 160);

        log.registarHotel(h1);
        log.registarHotel(h2);
        log.registarHotel(h3);
        log.registarHotel(h4);
        log.registarHotel(h5);
        log.registarHotel(h6);

        // [SEED] Check-in inicial para popular dados de logística
        log.alocarHotel(portugal, h2, "2026-06-08", "2026-07-20");
        log.alocarHotel(espanha, h4, "2026-06-09", "2026-07-15");
        log.alocarHotel(brasil, h6, "2026-06-07", "2026-07-18");

        // [SEED] 6. Jogos e Construção do Bracket Simétrico
        // Final
        Jogo jFinal = new Jogo(40, "2026-07-19", "20:00", luz, null, null, "Final", null, null);
        
        // Meias-Finais
        Jogo jMeia1 = new Jogo(30, "2026-07-15", "20:00", luz, null, null, "Meias-Finais", jFinal, PosicaoBracket.HOME);
        Jogo jMeia2 = new Jogo(31, "2026-07-16", "20:00", alvalade, null, null, "Meias-Finais", jFinal, PosicaoBracket.AWAY);
        
        // Quartos
        Jogo jQuartos1 = new Jogo(20, "2026-07-10", "18:00", dragao, null, null, "Quartos", jMeia1, PosicaoBracket.HOME);
        Jogo jQuartos2 = new Jogo(21, "2026-07-10", "21:00", luz, null, null, "Quartos", jMeia1, PosicaoBracket.AWAY);
        Jogo jQuartos3 = new Jogo(22, "2026-07-11", "18:00", alvalade, null, null, "Quartos", jMeia2, PosicaoBracket.HOME);
        Jogo jQuartos4 = new Jogo(23, "2026-07-11", "21:00", dragao, null, null, "Quartos", jMeia2, PosicaoBracket.AWAY);

        // Oitavos
        Jogo jOitavos1 = new Jogo(10, "2026-07-04", "18:00", luz, portugal, cuba, "Oitavos", jQuartos1, PosicaoBracket.HOME);
        Jogo jOitavos2 = new Jogo(11, "2026-07-04", "21:00", dragao, espanha, argentina, "Oitavos", jQuartos1, PosicaoBracket.AWAY);
        Jogo jOitavos3 = new Jogo(12, "2026-07-05", "18:00", alvalade, brasil, alemanha, "Oitavos", jQuartos2, PosicaoBracket.HOME);
        Jogo jOitavos4 = new Jogo(13, "2026-07-05", "21:00", luz, franca, inglaterra, "Oitavos", jQuartos2, PosicaoBracket.AWAY);
        
        Jogo jOitavos5 = new Jogo(14, "2026-07-06", "18:00", dragao, null, null, "Oitavos", jQuartos3, PosicaoBracket.HOME);
        Jogo jOitavos6 = new Jogo(15, "2026-07-06", "21:00", alvalade, null, null, "Oitavos", jQuartos3, PosicaoBracket.AWAY);
        Jogo jOitavos7 = new Jogo(16, "2026-07-07", "18:00", luz, null, null, "Oitavos", jQuartos4, PosicaoBracket.HOME);
        Jogo jOitavos8 = new Jogo(17, "2026-07-07", "21:00", dragao, null, null, "Oitavos", jQuartos4, PosicaoBracket.AWAY);

        // Jogos de Grupos para testes de calendários e standings
        Jogo jG1 = new Jogo(1, "2026-06-25", "15:00", luz, portugal, cuba, "Grupos", null, null);
        Jogo jG2 = new Jogo(2, "2026-06-25", "18:00", luz, espanha, alemanha, "Grupos", null, null);
        Jogo jG3 = new Jogo(3, "2026-06-26", "15:00", alvalade, brasil, franca, "Grupos", null, null);
        Jogo jG4 = new Jogo(4, "2026-06-26", "18:00", alvalade, argentina, inglaterra, "Grupos", null, null);

        // Registar todos no manager
        camp.registarJogo(jFinal);
        camp.registarJogo(jMeia1);
        camp.registarJogo(jMeia2);
        camp.registarJogo(jQuartos1);
        camp.registarJogo(jQuartos2);
        camp.registarJogo(jQuartos3);
        camp.registarJogo(jQuartos4);
        camp.registarJogo(jOitavos1);
        camp.registarJogo(jOitavos2);
        camp.registarJogo(jOitavos3);
        camp.registarJogo(jOitavos4);
        camp.registarJogo(jOitavos5);
        camp.registarJogo(jOitavos6);
        camp.registarJogo(jOitavos7);
        camp.registarJogo(jOitavos8);
        camp.registarJogo(jG1);
        camp.registarJogo(jG2);
        camp.registarJogo(jG3);
        camp.registarJogo(jG4);

        // [SEED] 7. Simular compras de bilhetes iniciais para Bilheteira
        // NOTA: venderBilhete() limita a 4 bilhetes por transação (regra anti-bot),
        //       por isso fazemos múltiplas chamadas para simular volume realista.
        BilheteiraManager bil = BilheteiraManager.getInstance();
        for (int i = 0; i < 4; i++) bil.venderBilhete(jG1, "Premium", 4);      // 16 bilhetes Premium
        for (int i = 0; i < 30; i++) bil.venderBilhete(jG1, "Economica", 4);   // 120 bilhetes Económica
        for (int i = 0; i < 10; i++) bil.venderBilhete(jG2, "Intermedia", 4);  // 40 bilhetes Intermédia
        for (int i = 0; i < 8; i++) bil.venderBilhete(jOitavos1, "Premium", 4);// 32 bilhetes Premium
    }

    public static void showLoginScreen() {
        LoginController loginController = new LoginController(primaryStage);
        Scene scene = loginController.getScene();
        primaryStage.setScene(scene);
    }

    public static void showDashboard() {
        DashboardController dashboardController = new DashboardController(primaryStage);
        Scene scene = dashboardController.getScene();
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
