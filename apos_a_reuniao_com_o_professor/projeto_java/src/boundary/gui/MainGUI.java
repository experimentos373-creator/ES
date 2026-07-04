package boundary.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.*;

import domain.*;
import manager.*;

public class MainGUI extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Forçar sempre a nova sementeira rica de demonstração
        semearDados();

        // Iniciar como PUBLICO por defeito para ir direto ao portal do adepto
        AutenticacaoManager.getInstance().autenticar("adepto@wc2026.com");
        showDashboard();

        primaryStage.setTitle("Gestão WC 2026 - Premium GUI");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(750);
        primaryStage.show();
    }



    /**
     * DADOS DE DEMONSTRAÇÃO (SEED) — Idempotente, só corre se a base estiver vazia.
     * Todos os dados criados aqui servem exclusivamente para testes visuais e funcionais.
     * Para produção, substituir por dados reais ou remover este método.
     */
    private void semearDados() {
        // Garantir limpeza total de todos os managers para sementeira fresca
        AutenticacaoManager.getInstance().reset();
        CampeonatoManager.getInstance().reset();
        ArbitragemManager.getInstance().reset();
        LogisticaManager.getInstance().reset();
        BilheteiraManager.getInstance().reset();

        // [SEED] 1. Utilizadores (Atores do Sistema)
        AutenticacaoManager auth = AutenticacaoManager.getInstance();
        auth.registarUtilizador(new Utilizador("admin@fifa.com", "Administrador FIFA", TipoUtilizador.ADMIN, null));
        auth.registarUtilizador(new Utilizador("arbitragem@fifa.com", "Gestor Arbitragem", TipoUtilizador.GESTOR_ARBITRAGEM, null));
        auth.registarUtilizador(new Utilizador("logistica@fifa.com", "Gestor Logistica", TipoUtilizador.GESTOR_LOGISTICA, null));
        auth.registarUtilizador(new Utilizador("equipa@fifa.com", "Gestor Equipa", TipoUtilizador.GESTOR_EQUIPA, "Portugal"));
        auth.registarUtilizador(new Utilizador("bilheteira@fifa.com", "Gestor Bilheteira", TipoUtilizador.GESTOR_BILHETEIRA, null));
        auth.registarUtilizador(new Utilizador("adepto@wc2026.com", "Adepto Geral", TipoUtilizador.PUBLICO, null));

        CampeonatoManager camp = CampeonatoManager.getInstance();
        camp.reset(); // Garantir base limpa para a nova sementeira rica

        // [SEED] 2. Estádios e Setores
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

        // [SEED] 3. Árbitros
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

        // [SEED] 4. Hotéis e Alojamento (Logística)
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

        // Mapeamento de Treinadores e Seleções do Mundial
        Map<String, String> treinadores = new HashMap<>();
        treinadores.put("Portugal", "Roberto Martinez");
        treinadores.put("Brasil", "Dorival Junior");
        treinadores.put("França", "Didier Deschamps");
        treinadores.put("Espanha", "Luis de la Fuente");
        treinadores.put("Argentina", "Lionel Scaloni");
        treinadores.put("Alemanha", "Julian Nagelsmann");
        treinadores.put("Inglaterra", "Gareth Southgate");
        treinadores.put("Cuba", "Yunielys Castillo");
        treinadores.put("Japão", "Hajime Moriyasu");
        treinadores.put("Senegal", "Aliou Cissé");
        treinadores.put("Marrocos", "Walid Regragui");
        treinadores.put("EUA", "Gregg Berhalter");
        treinadores.put("Holanda", "Ronald Koeman");
        treinadores.put("Itália", "Luciano Spalletti");
        treinadores.put("Colômbia", "Néstor Lorenzo");
        treinadores.put("Suíça", "Murat Yakin");
        treinadores.put("Bélgica", "Domenico Tedesco");
        treinadores.put("Coreia do Sul", "Kim Do-hoon");
        treinadores.put("Uruguai", "Marcelo Bielsa");
        treinadores.put("Gana", "Otto Addo");
        treinadores.put("Croácia", "Zlatko Dalić");
        treinadores.put("Canadá", "Jesse Marsch");
        treinadores.put("Dinamarca", "Kasper Hjulmand");
        treinadores.put("Sérvia", "Dragan Stojković");
        treinadores.put("Polónia", "Michał Probierz");
        treinadores.put("Austrália", "Graham Arnold");
        treinadores.put("Áustria", "Ralf Rangnick");
        treinadores.put("Nigéria", "Finidi George");
        treinadores.put("Turquia", "Vincenzo Montella");
        treinadores.put("Chile", "Ricardo Gareca");
        treinadores.put("Ucrânia", "Serhiy Rebrov");
        treinadores.put("Irão", "Amir Ghalenoei");

        Map<String, Integer> rankings = new HashMap<>();
        rankings.put("Argentina", 1860);
        rankings.put("França", 1840);
        rankings.put("Bélgica", 1795);
        rankings.put("Brasil", 1790);
        rankings.put("Inglaterra", 1785);
        rankings.put("Portugal", 1745);
        rankings.put("Holanda", 1740);
        rankings.put("Espanha", 1730);
        rankings.put("Itália", 1725);
        rankings.put("Croácia", 1720);
        rankings.put("EUA", 1660);
        rankings.put("Marrocos", 1661);
        rankings.put("Senegal", 1620);
        rankings.put("Alemanha", 1644);
        rankings.put("Colômbia", 1600);
        rankings.put("Suíça", 1610);
        rankings.put("Coreia do Sul", 1560);
        rankings.put("Uruguai", 1658);
        rankings.put("Gana", 1500);
        rankings.put("Canadá", 1495);
        rankings.put("Dinamarca", 1602);
        rankings.put("Sérvia", 1515);
        rankings.put("Polónia", 1530);
        rankings.put("Austrália", 1570);
        rankings.put("Áustria", 1555);
        rankings.put("Nigéria", 1520);
        rankings.put("Turquia", 1490);
        rankings.put("Chile", 1485);
        rankings.put("Ucrânia", 1565);
        rankings.put("Irão", 1612);
        rankings.put("Cuba", 1050);
        rankings.put("Japão", 1622);

        Map<String, Equipa> equipasCriadas = new HashMap<>();

        // Registrar as equipas de forma dinâmica com plantéis ricos
        for (Map.Entry<String, List<String>> entry : camp.getGrupos().entrySet()) {
            for (String nomeEq : entry.getValue()) {
                String coach = treinadores.getOrDefault(nomeEq, "Selecionador");
                Equipa eq = new Equipa(nomeEq, coach);
                eq.setRankingPontos(rankings.getOrDefault(nomeEq, 1200));

                if ("Portugal".equals(nomeEq)) {
                    // Mantemos a sementeira manual rica de Portugal para demonstrar o plantel fiel
                    // Titulares
                    Jogador pj1 = new Jogador(1, 1, "Diogo Costa", "Guarda-Redes", EstadoJogador.APTO); pj1.setStarter(true); pj1.setEnergy(100); eq.adicionarJogador(pj1);
                    Jogador pj2 = new Jogador(2, 2, "Nélson Semedo", "Defesa", EstadoJogador.APTO); pj2.setStarter(true); pj2.setEnergy(90); pj2.setYellowCards(1); eq.adicionarJogador(pj2);
                    Jogador pj3 = new Jogador(3, 3, "Pepe", "Defesa", EstadoJogador.APTO); pj3.setStarter(true); pj3.setEnergy(82); pj3.addInjury("Lesão Muscular na Coxa (Outubro 2025)"); eq.adicionarJogador(pj3);
                    Jogador pj4 = new Jogador(4, 4, "Rúben Dias", "Defesa", EstadoJogador.APTO); pj4.setStarter(true); pj4.setEnergy(98); eq.adicionarJogador(pj4);
                    Jogador pj5 = new Jogador(5, 19, "Nuno Mendes", "Defesa", EstadoJogador.APTO); pj5.setStarter(true); pj5.setEnergy(88); pj5.addInjury("Entorse do Tornozelo (Janeiro 2026)"); eq.adicionarJogador(pj5);
                    Jogador pj6 = new Jogador(6, 6, "João Palhinha", "Médio", EstadoJogador.APTO); pj6.setStarter(true); pj6.setEnergy(95); pj6.setYellowCards(2); eq.adicionarJogador(pj6);
                    Jogador pj7 = new Jogador(7, 8, "Bruno Fernandes", "Médio", EstadoJogador.APTO); pj7.setStarter(true); pj7.setEnergy(97); pj7.setGoals(2); pj7.setAssists(3); eq.adicionarJogador(pj7);
                    Jogador pj8 = new Jogador(8, 10, "Bernardo Silva", "Médio", EstadoJogador.APTO); pj8.setStarter(true); pj8.setEnergy(94); pj8.setGoals(1); pj8.setAssists(1); eq.adicionarJogador(pj8);
                    Jogador pj9 = new Jogador(9, 7, "Cristiano Ronaldo", "Avançado", EstadoJogador.APTO); pj9.setStarter(true); pj9.setEnergy(92); pj9.setGoals(4); pj9.setAssists(1); eq.adicionarJogador(pj9);
                    Jogador pj10 = new Jogador(10, 17, "Rafael Leão", "Avançado", EstadoJogador.APTO); pj10.setStarter(true); pj10.setEnergy(89); pj10.setGoals(1); pj10.addInjury("Mialgia de esforço (Fevereiro 2026)"); eq.adicionarJogador(pj10);
                    Jogador pj11 = new Jogador(11, 11, "João Félix", "Avançado", EstadoJogador.APTO); pj11.setStarter(true); pj11.setEnergy(91); pj11.setGoals(1); eq.adicionarJogador(pj11);

                    // Suplentes
                    Jogador pj12 = new Jogador(12, 12, "Rui Patrício", "Guarda-Redes", EstadoJogador.APTO); eq.adicionarJogador(pj12);
                    Jogador pj13 = new Jogador(13, 22, "José Sá", "Guarda-Redes", EstadoJogador.APTO); eq.adicionarJogador(pj13);
                    Jogador pj14 = new Jogador(14, 5, "Diogo Dalot", "Defesa", EstadoJogador.APTO); eq.adicionarJogador(pj14);
                    Jogador pj15 = new Jogador(15, 14, "Gonçalo Inácio", "Defesa", EstadoJogador.APTO); eq.adicionarJogador(pj15);
                    Jogador pj16 = new Jogador(16, 24, "António Silva", "Defesa", EstadoJogador.APTO); eq.adicionarJogador(pj16);
                    Jogador pj17 = new Jogador(17, 13, "Danilo Pereira", "Defesa", EstadoJogador.APTO); eq.adicionarJogador(pj17);
                    Jogador pj18 = new Jogador(18, 15, "João Neves", "Médio", EstadoJogador.APTO); eq.adicionarJogador(pj18);
                    Jogador pj19 = new Jogador(19, 16, "Vitinha", "Médio", EstadoJogador.APTO); eq.adicionarJogador(pj19);
                    Jogador pj20 = new Jogador(20, 23, "Otávio", "Médio", EstadoJogador.APTO); eq.adicionarJogador(pj20);
                    Jogador pj21 = new Jogador(21, 21, "Diogo Jota", "Avançado", EstadoJogador.APTO); pj21.setGoals(2); eq.adicionarJogador(pj21);
                    Jogador pj22 = new Jogador(22, 9, "Gonçalo Ramos", "Avançado", EstadoJogador.LESIONADO); pj22.setEnergy(45); pj22.setEstado(EstadoJogador.LESIONADO); pj22.addInjury("Estiramento Ligamentar (Junho 2026)"); eq.adicionarJogador(pj22);
                    Jogador pj23 = new Jogador(23, 18, "Rúben Neves", "Médio", EstadoJogador.SUSPENSO); pj23.setEstado(EstadoJogador.SUSPENSO); eq.adicionarJogador(pj23);
                } else {
                    // Criar plantel automático de 23 jogadores para as outras seleções
                    for (int i = 1; i <= 23; i++) {
                        int id = nomeEq.hashCode() * 31 + i;
                        if (id < 0) id = -id;

                        String posicao = "Avançado";
                        if (i <= 3) posicao = "Guarda-Redes";
                        else if (i <= 10) posicao = "Defesa";
                        else if (i <= 17) posicao = "Médio";

                        EstadoJogador estado = EstadoJogador.APTO;
                        int energy = 80 + (i % 19);

                        if (i == 22) {
                            estado = EstadoJogador.LESIONADO;
                            energy = 40;
                        } else if (i == 23) {
                            estado = EstadoJogador.SUSPENSO;
                        }

                        Jogador jog = new Jogador(id, i, nomeEq + " Player " + i, posicao, estado);
                        jog.setEnergy(energy);
                        jog.setStarter(i <= 11);
                        if (i == 22) {
                            jog.addInjury("Mialgia na perna (Junho 2026)");
                        }
                        eq.adicionarJogador(jog);
                    }
                }
                camp.registarEquipa(eq);
                equipasCriadas.put(nomeEq, eq);
            }
        }

        // [SEED] 5. Construir os Jogos de Grupos e Simular Resultados
        int gGameId = 100;
        List<String> datasGrupo = Arrays.asList(
            "2026-06-11", "2026-06-12", "2026-06-16", "2026-06-17", "2026-06-21", "2026-06-22"
        );
        List<String> horasGrupo = Arrays.asList("15:00", "18:00", "21:00");
        List<Estadio> estadiosGrupo = Arrays.asList(luz, alvalade, dragao);

        for (Map.Entry<String, List<String>> entry : camp.getGrupos().entrySet()) {
            List<String> eqs = entry.getValue();
            if (eqs.size() < 4) continue;

            List<int[]> combinacoes = Arrays.asList(
                new int[]{0, 1}, new int[]{2, 3},
                new int[]{0, 2}, new int[]{1, 3},
                new int[]{0, 3}, new int[]{1, 2}
            );

            for (int idx = 0; idx < combinacoes.size(); idx++) {
                int[] comb = combinacoes.get(idx);
                Equipa home = equipasCriadas.get(eqs.get(comb[0]));
                Equipa away = equipasCriadas.get(eqs.get(comb[1]));

                String data = datasGrupo.get(idx);
                String hora = horasGrupo.get(idx % 3);
                Estadio est = estadiosGrupo.get((idx + entry.getKey().hashCode()) % 3);

                Jogo j = new Jogo(gGameId++, data, hora, est, home, away, "Grupos", null, null);
                camp.registarJogo(j);

                // Determinar vencedor determinístico para gerar a classificação estável
                int gHome = (home.getNome().hashCode() & 3);
                int gAway = (away.getNome().hashCode() & 3);
                if (gHome == gAway) {
                    gHome = (gHome + 1) % 4; // Desempatar para termos vencedores
                }

                Equipa vencedor = gHome > gAway ? home : away;
                EstatisticaJogo stats = new EstatisticaJogo(55, 45, 12, 8, 4, 2);
                camp.finalizarJogoECorrerBracket(j.getId(), vencedor, gHome, gAway, -1, -1, stats);
            }
        }

        // [SEED] 6. Construir e Popular o Bracket Simétrico das Eliminatórias (Oitavos -> Final)
        // Final
        Jogo jFinal = new Jogo(40, "2026-07-19", "20:00", luz, null, null, "Final", null, null);
        camp.registarJogo(jFinal);

        // Meias-Finais
        Jogo jMeia1 = new Jogo(30, "2026-07-15", "20:00", luz, null, null, "Meias-Finais", jFinal, PosicaoBracket.HOME);
        Jogo jMeia2 = new Jogo(31, "2026-07-16", "20:00", alvalade, null, null, "Meias-Finais", jFinal, PosicaoBracket.AWAY);
        camp.registarJogo(jMeia1);
        camp.registarJogo(jMeia2);

        // Quartos
        Jogo jQuartos1 = new Jogo(20, "2026-07-10", "18:00", dragao, null, null, "Quartos", jMeia1, PosicaoBracket.HOME);
        Jogo jQuartos2 = new Jogo(21, "2026-07-10", "21:00", luz, null, null, "Quartos", jMeia1, PosicaoBracket.AWAY);
        Jogo jQuartos3 = new Jogo(22, "2026-07-11", "18:00", alvalade, null, null, "Quartos", jMeia2, PosicaoBracket.HOME);
        Jogo jQuartos4 = new Jogo(23, "2026-07-11", "21:00", dragao, null, null, "Quartos", jMeia2, PosicaoBracket.AWAY);
        camp.registarJogo(jQuartos1);
        camp.registarJogo(jQuartos2);
        camp.registarJogo(jQuartos3);
        camp.registarJogo(jQuartos4);

        // Oitavos (Preencher com os vencedores reais dos grupos!)
        List<ClassificacaoLinha> classA = camp.calcularClassificacaoGrupo("Grupo A");
        List<ClassificacaoLinha> classB = camp.calcularClassificacaoGrupo("Grupo B");
        List<ClassificacaoLinha> classC = camp.calcularClassificacaoGrupo("Grupo C");
        List<ClassificacaoLinha> classD = camp.calcularClassificacaoGrupo("Grupo D");
        List<ClassificacaoLinha> classE = camp.calcularClassificacaoGrupo("Grupo E");
        List<ClassificacaoLinha> classF = camp.calcularClassificacaoGrupo("Grupo F");
        List<ClassificacaoLinha> classG = camp.calcularClassificacaoGrupo("Grupo G");
        List<ClassificacaoLinha> classH = camp.calcularClassificacaoGrupo("Grupo H");

        Jogo jOitavos1 = new Jogo(10, "2026-07-04", "18:00", luz, classA.get(0).getEquipa(), classB.get(1).getEquipa(), "Oitavos", jQuartos1, PosicaoBracket.HOME);
        Jogo jOitavos2 = new Jogo(11, "2026-07-04", "21:00", dragao, classC.get(0).getEquipa(), classD.get(1).getEquipa(), "Oitavos", jQuartos1, PosicaoBracket.AWAY);
        Jogo jOitavos3 = new Jogo(12, "2026-07-05", "18:00", alvalade, classE.get(0).getEquipa(), classF.get(1).getEquipa(), "Oitavos", jQuartos2, PosicaoBracket.HOME);
        Jogo jOitavos4 = new Jogo(13, "2026-07-05", "21:00", luz, classG.get(0).getEquipa(), classH.get(1).getEquipa(), "Oitavos", jQuartos2, PosicaoBracket.AWAY);
        Jogo jOitavos5 = new Jogo(14, "2026-07-06", "18:00", dragao, classB.get(0).getEquipa(), classA.get(1).getEquipa(), "Oitavos", jQuartos3, PosicaoBracket.HOME);
        Jogo jOitavos6 = new Jogo(15, "2026-07-06", "21:00", alvalade, classD.get(0).getEquipa(), classC.get(1).getEquipa(), "Oitavos", jQuartos3, PosicaoBracket.AWAY);
        Jogo jOitavos7 = new Jogo(16, "2026-07-07", "18:00", luz, classF.get(0).getEquipa(), classE.get(1).getEquipa(), "Oitavos", jQuartos4, PosicaoBracket.HOME);
        Jogo jOitavos8 = new Jogo(17, "2026-07-07", "21:00", dragao, classH.get(0).getEquipa(), classG.get(1).getEquipa(), "Oitavos", jQuartos4, PosicaoBracket.AWAY);

        camp.registarJogo(jOitavos1);
        camp.registarJogo(jOitavos2);
        camp.registarJogo(jOitavos3);
        camp.registarJogo(jOitavos4);
        camp.registarJogo(jOitavos5);
        camp.registarJogo(jOitavos6);
        camp.registarJogo(jOitavos7);
        camp.registarJogo(jOitavos8);

        // [SEED] 7. Alocar Hotéis para as 16 Seleções Qualificadas
        List<Hotel> hoteis = Arrays.asList(h1, h2, h3, h4, h5, h6);
        List<Equipa> qualificadas = Arrays.asList(
            classA.get(0).getEquipa(), classA.get(1).getEquipa(),
            classB.get(0).getEquipa(), classB.get(1).getEquipa(),
            classC.get(0).getEquipa(), classC.get(1).getEquipa(),
            classD.get(0).getEquipa(), classD.get(1).getEquipa(),
            classE.get(0).getEquipa(), classE.get(1).getEquipa(),
            classF.get(0).getEquipa(), classF.get(1).getEquipa(),
            classG.get(0).getEquipa(), classG.get(1).getEquipa(),
            classH.get(0).getEquipa(), classH.get(1).getEquipa()
        );

        for (int i = 0; i < qualificadas.size(); i++) {
            Hotel hotel = hoteis.get(i % hoteis.size());
            log.alocarHotel(qualificadas.get(i), hotel, "2026-07-01", "2026-07-15");
        }

        // [SEED] 8. Simular Venda de Bilhetes de Forma Volumosa
        BilheteiraManager bil = BilheteiraManager.getInstance();
        for (Jogo j : camp.getJogos()) {
            for (int k = 0; k < 3; k++) {
                bil.venderBilhete(j, "Premium", 4);
                bil.venderBilhete(j, "Intermedia", 4);
                bil.venderBilhete(j, "Economica", 4);
            }
        }

        // [SEED] 9. Associar Escala de Árbitros aos Jogos Finalizados
        List<Arbitro> refs = arb.getArbitros();
        List<Arbitro> prs = new ArrayList<>();
        List<Arbitro> asts = new ArrayList<>();
        List<Arbitro> fths = new ArrayList<>();
        List<Arbitro> vrs = new ArrayList<>();
        for (Arbitro r : refs) {
            if (r.getTipo() == TipoArbitro.PRINCIPAL) prs.add(r);
            else if (r.getTipo() == TipoArbitro.ASSISTENTE) asts.add(r);
            else if (r.getTipo() == TipoArbitro.QUARTO) fths.add(r);
            else if (r.getTipo() == TipoArbitro.VAR) vrs.add(r);
        }

        int gameCount = 0;
        for (Jogo j : camp.getJogos()) {
            if (domain.StatusJogo.FINALIZADO.equals(j.getStatus())) {
                Arbitro p = prs.isEmpty() ? null : prs.get(gameCount % prs.size());
                Arbitro a1 = asts.isEmpty() ? null : asts.get((gameCount) % asts.size());
                Arbitro a2 = asts.size() < 2 ? a1 : asts.get((gameCount + 1) % asts.size());
                Arbitro q = fths.isEmpty() ? null : fths.get(gameCount % fths.size());
                Arbitro v = vrs.isEmpty() ? null : vrs.get(gameCount % vrs.size());
                j.associarEscalaArbitros(new domain.EscalaoArbitral(p, a1, a2, q, v));
                gameCount++;
            }
        }
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
