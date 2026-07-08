package manager;

import domain.*;
import util.PersistenceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador central para a gestao do Campeonato do Mundo de Futebol (Singleton DCL).
 * Coordena equipas, jogos, estadios, classificacoes e bracket.
 */
public class CampeonatoManager {
    private static volatile CampeonatoManager instance = null;

    private List<Jogo> jogos;
    private List<Equipa> equipas;
    private List<Estadio> estadios;
    private Map<String, List<String>> grupos;

    private static final String JOGOS_FILE = "jogos.ser";
    private static final String EQUIPAS_FILE = "equipas.ser";
    private static final String ESTADIOS_FILE = "estadios.ser";
    private static final String GRUPOS_FILE = "grupos.ser";

    private CampeonatoManager() {
        this.jogos = PersistenceUtil.carregar(JOGOS_FILE);
        this.equipas = PersistenceUtil.carregar(EQUIPAS_FILE);
        this.estadios = PersistenceUtil.carregar(ESTADIOS_FILE);
        carregarGrupos();
    }

    public static CampeonatoManager getInstance() {
        if (instance == null) {
            synchronized (CampeonatoManager.class) {
                if (instance == null) {
                    instance = new CampeonatoManager();
                }
            }
        }
        return instance;
    }

    /**
     * Guarda todas as colecoes de dados persistentes.
     */
    public void saveAll() {
        PersistenceUtil.guardar(JOGOS_FILE, this.jogos);
        PersistenceUtil.guardar(EQUIPAS_FILE, this.equipas);
        PersistenceUtil.guardar(ESTADIOS_FILE, this.estadios);
        guardarGrupos();
    }

    /**
     * Limpa o estado atual (util para testes JUnit).
     */
    public void reset() {
        this.jogos = new ArrayList<>();
        this.equipas = new ArrayList<>();
        this.estadios = new ArrayList<>();
        inicializarGruposPadrao();
        saveAll();
    }

    // --- Gestao de Equipas, Jogos e Estadios ---

    public List<Jogo> getJogos() {
        return new ArrayList<>(this.jogos);
    }

    public List<Equipa> getEquipas() {
        return new ArrayList<>(this.equipas);
    }

    public List<Estadio> getEstadios() {
        return new ArrayList<>(this.estadios);
    }

    public void registarJogo(Jogo jogo) {
        if (jogo == null) return;

        // Validar formato de data
        if (jogo.getData() == null || !jogo.getData().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("Formato de data invalido. Deve ser YYYY-MM-DD.");
        }

        // Impedir agendar jogos para equipas que ja jogam na mesma data
        for (Jogo j : this.jogos) {
            if (j.getId() != jogo.getId() && j.getData().equals(jogo.getData())) {
                if (jogo.getHomeTeam() != null) {
                    boolean homeBusy = (j.getHomeTeam() != null && j.getHomeTeam().equals(jogo.getHomeTeam())) || 
                                       (j.getAwayTeam() != null && j.getAwayTeam().equals(jogo.getHomeTeam()));
                    if (homeBusy) {
                        throw new IllegalArgumentException("A equipa da casa ja tem jogo agendado na data: " + jogo.getData());
                    }
                }
                if (jogo.getAwayTeam() != null) {
                    boolean awayBusy = (j.getHomeTeam() != null && j.getHomeTeam().equals(jogo.getAwayTeam())) || 
                                       (j.getAwayTeam() != null && j.getAwayTeam().equals(jogo.getAwayTeam()));
                    if (awayBusy) {
                        throw new IllegalArgumentException("A equipa de fora ja tem jogo agendado na data: " + jogo.getData());
                    }
                }
            }
        }

        this.jogos.removeIf(j -> j.getId() == jogo.getId());
        this.jogos.add(jogo);
        saveAll();
    }

    public void registarEquipa(Equipa equipa) {
        if (equipa == null) return;
        this.equipas.removeIf(e -> e.getNome().equalsIgnoreCase(equipa.getNome()));
        this.equipas.add(equipa);
        saveAll();
    }

    public void registarEstadio(Estadio estadio) {
        if (estadio == null) return;
        this.estadios.removeIf(e -> e.getNome().equalsIgnoreCase(estadio.getNome()));
        this.estadios.add(estadio);
        saveAll();
    }

    public Equipa procurarEquipaPorNome(String nome) {
        for (Equipa eq : this.equipas) {
            if (eq.getNome().equalsIgnoreCase(nome)) {
                return eq;
            }
        }
        return null;
    }

    public Jogo procurarJogoPorId(int id) {
        for (Jogo j : this.jogos) {
            if (j.getId() == id) {
                return j;
            }
        }
        return null;
    }

    // --- Lógica de Grupos e Classificações ---

    public Map<String, List<String>> getGrupos() {
        return new HashMap<>(this.grupos);
    }

    public void registarEquipaNoGrupo(String grupoNome, String equipaNome) {
        String nomeCompleto = grupoNome.startsWith("Grupo ") ? grupoNome : "Grupo " + grupoNome;
        List<String> list = this.grupos.computeIfAbsent(nomeCompleto, k -> new ArrayList<>());
        if (!list.contains(equipaNome)) {
            list = new ArrayList<>(list);
            list.add(equipaNome);
            this.grupos.put(nomeCompleto, list);
            saveAll();
        }
    }

    public List<ClassificacaoLinha> calcularClassificacaoGrupo(String grupoNome) {
        String nomeCompleto = grupoNome.startsWith("Grupo ") ? grupoNome : "Grupo " + grupoNome;
        List<String> equipasNoGrupo = this.grupos.get(nomeCompleto);
        if (equipasNoGrupo == null) {
            return new ArrayList<>();
        }

        Map<String, ClassificacaoLinha> standings = new HashMap<>();
        for (String teamName : equipasNoGrupo) {
            Equipa eq = procurarEquipaPorNome(teamName);
            if (eq == null) {
                eq = new Equipa(teamName, "Desconhecido");
            }
            standings.put(teamName, new ClassificacaoLinha(eq));
        }

        for (Jogo j : this.jogos) {
            if ("Grupos".equalsIgnoreCase(j.getPhase()) && StatusJogo.FINALIZADO.equals(j.getStatus())) {
                String home = j.getHomeTeam().getNome();
                String away = j.getAwayTeam().getNome();

                if (equipasNoGrupo.contains(home) && equipasNoGrupo.contains(away)) {
                    ClassificacaoLinha homeLine = standings.get(home);
                    ClassificacaoLinha awayLine = standings.get(away);

                    if (homeLine != null && awayLine != null) {
                        homeLine.adicionarResultado(j.getGoalsHome(), j.getGoalsAway());
                        awayLine.adicionarResultado(j.getGoalsAway(), j.getGoalsHome());
                    }
                }
            }
        }

        List<ClassificacaoLinha> result = new ArrayList<>(standings.values());
        
        // Criterios de Desempate (FIFA):
        // 1. Pontos (descendente)
        // 2. Saldo de golos (descendente)
        // 3. Golos marcados (descendente)
        // 4. Nome da equipa (alfabetico para estabilidade de ordenacao)
        result.sort((a, b) -> {
            if (b.getPontos() != a.getPontos()) {
                return Integer.compare(b.getPontos(), a.getPontos());
            }
            if (b.getSaldoGolos() != a.getSaldoGolos()) {
                return Integer.compare(b.getSaldoGolos(), a.getSaldoGolos());
            }
            if (b.getGolosMarcados() != a.getGolosMarcados()) {
                return Integer.compare(b.getGolosMarcados(), a.getGolosMarcados());
            }
            return a.getEquipa().getNome().compareTo(b.getEquipa().getNome());
        });

        return result;
    }

    // --- Lógica de Bracket e Finalização ---

    /**
     * Finaliza o jogo atual, validando as regras de penaltis nas eliminatorias,
     * e corre a progressão do bracket de forma automatica.
     */
    public void finalizarJogoECorrerBracket(int jogoId, Equipa vencedor, int goalsHome, int goalsAway, int penaltiesHome, int penaltiesAway, EstatisticaJogo stats) {
        Jogo jogo = procurarJogoPorId(jogoId);
        if (jogo == null) {
            throw new IllegalArgumentException("Jogo com ID " + jogoId + " nao encontrado.");
        }

        if (jogo.getHomeTeam() == null || jogo.getAwayTeam() == null) {
            throw new IllegalArgumentException("Nao e possivel finalizar um jogo sem equipas definidas.");
        }

        boolean isEliminatoria = !"Grupos".equalsIgnoreCase(jogo.getPhase());
        
        // Validacao estrita de empates nas eliminatorias
        if (isEliminatoria && goalsHome == goalsAway) {
            if (penaltiesHome < 0 || penaltiesAway < 0 || penaltiesHome == penaltiesAway) {
                throw new IllegalArgumentException("Jogos das eliminatorias empatados exigem penalties validos e com vencedor decidido.");
            }
        }

        // Determinar o vencedor real com base nos golos e penaltis
        Equipa vencedorReal = null;
        if (goalsHome > goalsAway) {
            vencedorReal = jogo.getHomeTeam();
        } else if (goalsAway > goalsHome) {
            vencedorReal = jogo.getAwayTeam();
        } else if (isEliminatoria) {
            if (penaltiesHome > penaltiesAway) {
                vencedorReal = jogo.getHomeTeam();
            } else {
                vencedorReal = jogo.getAwayTeam();
            }
        }

        // Se o utilizador forneceu um vencedor diferente do calculado, lancamos erro
        if (vencedor != null && vencedorReal != null && !vencedor.equals(vencedorReal)) {
            throw new IllegalArgumentException("O vencedor fornecido nao condiz com o resultado do jogo.");
        }

        Equipa winnerToSet = vencedorReal != null ? vencedorReal : vencedor;

        // --- 1. Identificar jogadores das equipas participantes que estavam suspensos antes do início do jogo ---
        List<Jogador> suspensosPreJogo = new ArrayList<>();
        Equipa home = procurarEquipaPorNome(jogo.getHomeTeam().getNome());
        Equipa away = procurarEquipaPorNome(jogo.getAwayTeam().getNome());
        if (home != null) {
            for (Jogador p : home.getJogadores()) {
                if (p.getEstado() == EstadoJogador.SUSPENSO) {
                    suspensosPreJogo.add(p);
                }
            }
        }
        if (away != null) {
            for (Jogador p : away.getJogadores()) {
                if (p.getEstado() == EstadoJogador.SUSPENSO) {
                    suspensosPreJogo.add(p);
                }
            }
        }

        // --- 2. Finaliza o jogo ---
        jogo.finalizar(winnerToSet, goalsHome, goalsAway, penaltiesHome, penaltiesAway, stats);

        // --- 3. Processar eventos do jogo atual para atualizar estatísticas de cartões e golos ---
        for (EventoJogo ev : jogo.getEventos()) {
            Jogador j = ev.getJogador();
            if (j == null) continue;
            
            Jogador jogadorReal = null;
            Equipa eq = ev.getEquipa() != null ? procurarEquipaPorNome(ev.getEquipa().getNome()) : null;
            if (eq != null) {
                for (Jogador p : eq.getJogadores()) {
                    if (p.getId() == j.getId()) {
                        jogadorReal = p;
                        break;
                    }
                }
            }
            if (jogadorReal == null) jogadorReal = j;
            
            if (ev.getTipo() == TipoEvento.GOLO) {
                jogadorReal.incrementGoals();
            } else if (ev.getTipo() == TipoEvento.CARTAO_AMARELO) {
                jogadorReal.setYellowCards(jogadorReal.getYellowCards() + 1);
                // Se acumular 2 amarelos no torneio, fica suspenso para o jogo seguinte
                if (jogadorReal.getYellowCards() >= 2) {
                    jogadorReal.setEstado(EstadoJogador.SUSPENSO);
                }
            } else if (ev.getTipo() == TipoEvento.CARTAO_VERMELHO) {
                jogadorReal.setRedCards(jogadorReal.getRedCards() + 1);
                jogadorReal.setEstado(EstadoJogador.SUSPENSO);
            }
        }

        // --- 4. Libertar os jogadores que já cumpriram a suspensão neste jogo ---
        for (Jogador p : suspensosPreJogo) {
            p.setEstado(EstadoJogador.APTO);
            p.setYellowCards(0); // Reiniciar cartões amarelos após cumprir suspensão
        }

        // --- 5. Limpeza de cartões amarelos antes das meias-finais (Regulamento FIFA - Opção B) ---
        if ("Quartos".equalsIgnoreCase(jogo.getPhase()) && winnerToSet != null) {
            Equipa qualificada = procurarEquipaPorNome(winnerToSet.getNome());
            if (qualificada != null) {
                for (Jogador p : qualificada.getJogadores()) {
                    if (p.getEstado() != EstadoJogador.SUSPENSO) {
                        p.setYellowCards(0);
                    }
                }
            }
        }

        // --- Progressao no Bracket ---
        if (isEliminatoria) {
            // Metodo 1: Auto-associacao OO
            if (jogo.getProximoJogo() != null) {
                Jogo prox = jogo.getProximoJogo();
                // Procura o jogo na colecao do manager para o atualizar
                Jogo proxNoManager = procurarJogoPorId(prox.getId());
                if (proxNoManager != null) {
                    if (jogo.getPosicaoNoProximoJogo() == PosicaoBracket.HOME) {
                        proxNoManager.setHomeTeam(winnerToSet);
                    } else {
                        proxNoManager.setAwayTeam(winnerToSet);
                    }
                }
            } else {
                // Metodo 2: Index-based progression (caso nao estejam associados via proximoJogo)
                List<String> order = java.util.Arrays.asList("Grupos", "Dezasseis-avos", "Oitavos", "Quartos", "Meias-Finais", "Final");
                int idx = order.indexOf(jogo.getPhase());
                if (idx >= 1 && idx < order.size() - 1) {
                    String nextPhase = order.get(idx + 1);
                    
                    List<Jogo> currentPhaseMatches = new ArrayList<>();
                    for (Jogo j : this.jogos) {
                        if (j.getPhase().equalsIgnoreCase(jogo.getPhase())) {
                            currentPhaseMatches.add(j);
                        }
                    }
                    currentPhaseMatches.sort(Comparator.comparingInt(Jogo::getId));
                    
                    int matchIndex = -1;
                    for (int i = 0; i < currentPhaseMatches.size(); i++) {
                        if (currentPhaseMatches.get(i).getId() == jogo.getId()) {
                            matchIndex = i;
                            break;
                        }
                    }
                    
                    if (matchIndex != -1) {
                        int pairIndex = matchIndex / 2;
                        
                        List<Jogo> nextPhaseMatches = new ArrayList<>();
                        for (Jogo j : this.jogos) {
                            if (j.getPhase().equalsIgnoreCase(nextPhase)) {
                                nextPhaseMatches.add(j);
                            }
                        }
                        nextPhaseMatches.sort(Comparator.comparingInt(Jogo::getId));
                        
                        if (pairIndex < nextPhaseMatches.size()) {
                            Jogo nextMatch = nextPhaseMatches.get(pairIndex);
                            if (matchIndex % 2 == 0) {
                                nextMatch.setHomeTeam(winnerToSet);
                            } else {
                                nextMatch.setAwayTeam(winnerToSet);
                            }
                        }
                    }
                }
            }
        } else {
            // Fase de grupos: verifica se todos os jogos de grupo terminaram
            checkAndAdvanceGroupsToOitavos();
        }

        // --- 6. Gerar estatísticas e ratings individuais dos jogadores e guardar no snapshot ---
        List<JogadorJogoStats> snapshot = new ArrayList<>();
        java.util.Random rand = new java.util.Random();
        
        for (Equipa eq : java.util.Arrays.asList(home, away)) {
            if (eq == null) continue;
            boolean isHome = eq.equals(home);
            int saves = isHome ? stats.getDefesasHome() : stats.getDefesasAway();
            
            for (Jogador p : eq.getJogadores()) {
                boolean participou = p.isStarter();
                for (EventoJogo ev : jogo.getEventos()) {
                    if (ev.getJogador() != null && ev.getJogador().getId() == p.getId()) {
                        participou = true;
                        break;
                    }
                }
                
                if (!participou) continue;
                
                // Pontuação base 6.0 + variação aleatória de -1.5 a +1.5
                double rating = 6.0 + (-1.5 + (rand.nextDouble() * 3.0));
                int minutes = 90;
                int pGoals = 0;
                int pAssists = 0;
                int pYellows = 0;
                int pReds = 0;
                
                for (EventoJogo ev : jogo.getEventos()) {
                    if (ev.getJogador() != null && ev.getJogador().getId() == p.getId()) {
                        if (ev.getTipo() == TipoEvento.GOLO) {
                            pGoals++;
                            rating += 1.5;
                        } else if (ev.getTipo() == TipoEvento.CARTAO_AMARELO) {
                            pYellows++;
                            rating -= 0.5;
                        } else if (ev.getTipo() == TipoEvento.CARTAO_VERMELHO) {
                            pReds++;
                            rating -= 1.5;
                        }
                    }
                }
                
                if ("Guarda-Redes".equalsIgnoreCase(p.getPosicao())) {
                    rating += saves * 0.3;
                }
                
                rating = Math.max(3.0, Math.min(10.0, rating));
                rating = Math.round(rating * 10.0) / 10.0;
                
                JogadorJogoStats js = new JogadorJogoStats(p.getId(), p.getNome(), jogo.getId(), rating, minutes, pGoals, pAssists, pYellows, pReds);
                snapshot.add(js);
                
                // Adicionar ao histórico do jogador
                p.getMatchStatsList().add(js);
            }
        }
        jogo.setPlayersSnapshot(snapshot);

        saveAll();
    }

    private void checkAndAdvanceGroupsToOitavos() {
        boolean todosFinalizados = true;
        boolean temJogosGrupo = false;
        for (Jogo j : this.jogos) {
            if ("Grupos".equalsIgnoreCase(j.getPhase())) {
                temJogosGrupo = true;
                if (!StatusJogo.FINALIZADO.equals(j.getStatus())) {
                    todosFinalizados = false;
                    break;
                }
            }
        }

        if (temJogosGrupo && todosFinalizados) {
            // Determinar se avançamos para Oitavos ou Dezasseis-avos
            boolean hasDezasseis = false;
            for (Jogo j : this.jogos) {
                if ("Dezasseis-avos".equalsIgnoreCase(j.getPhase())) {
                    hasDezasseis = true;
                    break;
                }
            }
            
            String nextPhase = hasDezasseis ? "Dezasseis-avos" : "Oitavos";
            List<Jogo> nextMatches = new ArrayList<>();
            for (Jogo j : this.jogos) {
                if (nextPhase.equalsIgnoreCase(j.getPhase())) {
                    nextMatches.add(j);
                }
            }
            nextMatches.sort(Comparator.comparingInt(Jogo::getId));
            
            if (nextMatches.isEmpty()) return;

            Map<String, List<ClassificacaoLinha>> groupStandings = new HashMap<>();
            for (String groupName : this.grupos.keySet()) {
                groupStandings.put(groupName.replace("Grupo ", ""), calcularClassificacaoGrupo(groupName));
            }

            if ("Oitavos".equalsIgnoreCase(nextPhase) && nextMatches.size() >= 8) {
                String[][] matchups = {
                    {"A", "B"}, // Jogo 0: 1A vs 2B
                    {"C", "D"}, // Jogo 1: 1C vs 2D
                    {"E", "F"}, // Jogo 2: 1E vs 2F
                    {"G", "H"}, // Jogo 3: 1G vs 2H
                    {"B", "A"}, // Jogo 4: 1B vs 2A
                    {"D", "C"}, // Jogo 5: 1D vs 2C
                    {"F", "E"}, // Jogo 6: 1F vs 2E
                    {"H", "G"}  // Jogo 7: 1H vs 2G
                };

                for (int i = 0; i < Math.min(8, nextMatches.size()); i++) {
                    Jogo match = nextMatches.get(i);
                    String homeGroup = matchups[i][0];
                    String awayGroup = matchups[i][1];

                    List<ClassificacaoLinha> homeStandings = groupStandings.get(homeGroup);
                    List<ClassificacaoLinha> awayStandings = groupStandings.get(awayGroup);

                    if (homeStandings != null && homeStandings.size() >= 2 &&
                        awayStandings != null && awayStandings.size() >= 2) {
                        
                        Equipa homeTeam;
                        Equipa awayTeam;
                        if (i < 4) {
                            homeTeam = homeStandings.get(0).getEquipa(); // 1º do grupo home
                            awayTeam = awayStandings.get(1).getEquipa(); // 2º do grupo away
                        } else {
                            homeTeam = homeStandings.get(0).getEquipa(); // 1º do grupo home
                            awayTeam = awayStandings.get(1).getEquipa(); // 2º do grupo away
                        }
                        match.setHomeTeam(homeTeam);
                        match.setAwayTeam(awayTeam);
                    }
                }
            }
        }
    }

    /**
     * Realiza o sorteio da fase de grupos com base no ranking FIFA das equipas registadas.
     * Requer pelo menos 16 equipas registadas. Distribuirá as equipas em 4 grupos (A, B, C, D) de 4 equipas.
     */
    public boolean realizarSorteioGrupos() {
        if (this.equipas.size() < 16) {
            return false;
        }

        // Ordenar equipas por ranking decrescente
        List<Equipa> sorted = new ArrayList<>(this.equipas);
        sorted.sort((e1, e2) -> Integer.compare(e2.getRankingPontos(), e1.getRankingPontos()));

        // Dividir em 4 potes
        List<Equipa> pote1 = new ArrayList<>(sorted.subList(0, 4));
        List<Equipa> pote2 = new ArrayList<>(sorted.subList(4, 8));
        List<Equipa> pote3 = new ArrayList<>(sorted.subList(8, 12));
        List<Equipa> pote4 = new ArrayList<>(sorted.subList(12, 16));

        // Embaralhar cada pote
        java.util.Collections.shuffle(pote1);
        java.util.Collections.shuffle(pote2);
        java.util.Collections.shuffle(pote3);
        java.util.Collections.shuffle(pote4);

        // Atribuir equipas aos grupos
        this.grupos = new HashMap<>();
        this.grupos.put("Grupo A", new ArrayList<>(java.util.Arrays.asList(pote1.get(0).getNome(), pote2.get(0).getNome(), pote3.get(0).getNome(), pote4.get(0).getNome())));
        this.grupos.put("Grupo B", new ArrayList<>(java.util.Arrays.asList(pote1.get(1).getNome(), pote2.get(1).getNome(), pote3.get(1).getNome(), pote4.get(1).getNome())));
        this.grupos.put("Grupo C", new ArrayList<>(java.util.Arrays.asList(pote1.get(2).getNome(), pote2.get(2).getNome(), pote3.get(2).getNome(), pote4.get(2).getNome())));
        this.grupos.put("Grupo D", new ArrayList<>(java.util.Arrays.asList(pote1.get(3).getNome(), pote2.get(3).getNome(), pote3.get(3).getNome(), pote4.get(3).getNome())));

        saveAll();
        return true;
    }

    /**
     * Simula um jogo minuto a minuto (1-90), gerando eventos aleatórios de golos, cartões e substituições,
     * e finalizando o jogo ao correr a lógica do bracket.
     */
    public boolean simularJogoMinutoAMinuto(int jogoId) {
        Jogo jogo = procurarJogoPorId(jogoId);
        if (jogo == null || StatusJogo.FINALIZADO.equals(jogo.getStatus())) {
            return false;
        }

        if (jogo.getHomeTeam() == null || jogo.getAwayTeam() == null) {
            return false;
        }

        // Carregar as equipas persistentes para obter os seus planteis reais
        Equipa homeEq = procurarEquipaPorNome(jogo.getHomeTeam().getNome());
        Equipa awayEq = procurarEquipaPorNome(jogo.getAwayTeam().getNome());
        if (homeEq == null || awayEq == null) {
            return false;
        }

        int scoreHome = 0;
        int scoreAway = 0;
        java.util.Random rand = new java.util.Random();

        // Limpar eventos anteriores se houver
        jogo.getEventos().clear();

        for (int min = 1; min <= 90; min++) {
            // 5% de probabilidade de ocorrer um evento a cada minuto
            if (rand.nextInt(100) < 5) {
                // Escolher a equipa beneficiada/penalizada (50% home, 50% away)
                boolean isHome = rand.nextBoolean();
                Equipa activeTeam = isHome ? homeEq : awayEq;
                
                // Escolher um jogador apto aleatório desta equipa
                List<Jogador> aptos = new ArrayList<>();
                for (Jogador p : activeTeam.getJogadores()) {
                    if (p.getEstado() == EstadoJogador.APTO) {
                        aptos.add(p);
                    }
                }
                if (aptos.isEmpty()) continue;
                Jogador jogador = aptos.get(rand.nextInt(aptos.size()));

                // Decidir o tipo de evento (Golo ou Cartão)
                int roll = rand.nextInt(100);
                TipoEvento tipo;
                if (roll < 45) {
                    tipo = TipoEvento.GOLO;
                    if (isHome) scoreHome++; else scoreAway++;
                } else if (roll < 85) {
                    tipo = TipoEvento.CARTAO_AMARELO;
                } else {
                    tipo = TipoEvento.CARTAO_VERMELHO;
                }

                EventoJogo ev = new EventoJogo(min, tipo, jogador, activeTeam);
                jogo.adicionarEvento(ev);
            }
        }

        // Simular substituições explicitamente para ambas as equipas (entre 1 e 5)
        int numSubsHome = 1 + rand.nextInt(3); // 1, 2 ou 3
        if (rand.nextInt(100) < 30) numSubsHome += rand.nextInt(3); // chance de chegar a 4 ou 5
        numSubsHome = Math.max(1, Math.min(5, numSubsHome));

        int numSubsAway = 1 + rand.nextInt(3);
        if (rand.nextInt(100) < 30) numSubsAway += rand.nextInt(3);
        numSubsAway = Math.max(1, Math.min(5, numSubsAway));

        simulateTeamSubs(rand, homeEq, numSubsHome, jogo);
        simulateTeamSubs(rand, awayEq, numSubsAway, jogo);

        // Se for jogo de eliminatória e estiver empatado ao minuto 90, simular penaltis
        int penHome = -1;
        int penAway = -1;
        boolean isEliminatoria = !"Grupos".equalsIgnoreCase(jogo.getPhase());
        if (isEliminatoria && scoreHome == scoreAway) {
            penHome = 3 + rand.nextInt(3); // 3 a 5
            penAway = penHome;
            if (rand.nextBoolean()) {
                penHome++;
            } else {
                penAway++;
            }
        }

        int yHome = 0, yAway = 0;
        int rHome = 0, rAway = 0;
        for (EventoJogo ev : jogo.getEventos()) {
            if (ev.getTipo() == TipoEvento.CARTAO_AMARELO) {
                if (ev.getEquipa().getNome().equals(homeEq.getNome())) yHome++; else yAway++;
            } else if (ev.getTipo() == TipoEvento.CARTAO_VERMELHO) {
                if (ev.getEquipa().getNome().equals(homeEq.getNome())) rHome++; else rAway++;
            }
        }

        int posseHome;
        int posseAway;
        int remHome;
        int remAway;
        int remBalHome;
        int remBalAway;
        int passHome;
        int passAway;

        boolean homeWon = scoreHome > scoreAway;
        boolean awayWon = scoreAway > scoreHome;
        boolean statsFavorHome = false;

        if (homeWon) {
            statsFavorHome = (rand.nextInt(100) < 70); // 70% chance to favor winner
        } else if (awayWon) {
            statsFavorHome = (rand.nextInt(100) >= 70); // 30% chance to favor home (i.e. 70% to favor away)
        } else {
            statsFavorHome = rand.nextBoolean(); // 50/50 for draws
        }

        if (statsFavorHome) {
            posseHome = 52 + rand.nextInt(14); // 52% to 65%
            posseAway = 100 - posseHome;
            remHome = 12 + rand.nextInt(10); // 12 to 21 shots
            remAway = 6 + rand.nextInt(7);  // 6 to 12 shots
            remBalHome = Math.max(scoreHome, remHome / 3 + rand.nextInt(3));
            remBalAway = Math.max(scoreAway, remAway / 4 + rand.nextInt(2));
            passHome = posseHome * 9 + rand.nextInt(30);
            passAway = posseAway * 8 + rand.nextInt(30);
        } else {
            posseAway = 52 + rand.nextInt(14); // 52% to 65%
            posseHome = 100 - posseAway;
            remAway = 12 + rand.nextInt(10); // 12 to 21 shots
            remHome = 6 + rand.nextInt(7);  // 6 to 12 shots
            remBalAway = Math.max(scoreAway, remAway / 3 + rand.nextInt(3));
            remBalHome = Math.max(scoreHome, remHome / 4 + rand.nextInt(2));
            passAway = posseAway * 9 + rand.nextInt(30);
            passHome = posseHome * 8 + rand.nextInt(30);
        }

        int cantHome = 3 + rand.nextInt(7);
        int cantAway = 2 + rand.nextInt(7);
        int forasHome = rand.nextInt(4);
        int forasAway = rand.nextInt(4);
        int faltasHome = 8 + rand.nextInt(10);
        int faltasAway = 8 + rand.nextInt(10);
        int savesHome = Math.max(0, remBalAway - scoreAway);
        int savesAway = Math.max(0, remBalHome - scoreHome);
        int accHome = 70 + rand.nextInt(20);
        int accAway = 70 + rand.nextInt(20);

        EstatisticaJogo stats = new EstatisticaJogo(
            posseHome, posseAway, remHome, remAway, cantHome, cantAway,
            remBalHome, remBalAway, forasHome, forasAway,
            faltasHome, faltasAway, yHome, yAway,
            rHome, rAway, savesHome, savesAway,
            passHome, passAway, accHome, accAway
        );

        finalizarJogoECorrerBracket(jogoId, null, scoreHome, scoreAway, penHome, penAway, stats);
        return true;
    }

    private void simulateTeamSubs(java.util.Random rand, Equipa team, int count, Jogo jogo) {
        List<Jogador> starters = new ArrayList<>();
        List<Jogador> reserves = new ArrayList<>();
        for (Jogador p : team.getJogadores()) {
            if (p.isStarter()) starters.add(p);
            else reserves.add(p);
        }

        if (starters.isEmpty() || reserves.isEmpty()) return;

        List<Jogador> alreadyOut = new ArrayList<>();
        List<Jogador> alreadyIn = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            if (alreadyOut.size() >= starters.size() || alreadyIn.size() >= reserves.size()) break;
            
            Jogador outPlayer = starters.get(rand.nextInt(starters.size()));
            while (alreadyOut.contains(outPlayer)) {
                outPlayer = starters.get(rand.nextInt(starters.size()));
            }

            Jogador inPlayer = reserves.get(rand.nextInt(reserves.size()));
            while (alreadyIn.contains(inPlayer)) {
                inPlayer = reserves.get(rand.nextInt(reserves.size()));
            }

            alreadyOut.add(outPlayer);
            alreadyIn.add(inPlayer);

            int min = 46 + rand.nextInt(45); // Substitutions in 2nd half
            Jogador dummySub = new Jogador(0, outPlayer.getNumeroCamisola(), "[Entra] " + inPlayer.getNome() + " / [Sai] " + outPlayer.getNome(), outPlayer.getPosicao(), EstadoJogador.APTO);
            EventoJogo subEvent = new EventoJogo(min, TipoEvento.SUBSTITUICAO, dummySub, team);
            jogo.adicionarEvento(subEvent);
        }
    }

    // --- Persistencia Auxiliar ---

    @SuppressWarnings("unchecked")
    private void carregarGrupos() {
        List<HashMap<String, List<String>>> list = (List<HashMap<String, List<String>>>) (List<?>) PersistenceUtil.carregar(GRUPOS_FILE);
        if (list == null || list.isEmpty()) {
            inicializarGruposPadrao();
        } else {
            this.grupos = list.get(0);
        }
    }

    private void guardarGrupos() {
        List<HashMap<String, List<String>>> list = new ArrayList<>();
        list.add(new HashMap<>(this.grupos));
        PersistenceUtil.guardar(GRUPOS_FILE, list);
    }

    private void inicializarGruposPadrao() {
        this.grupos = new HashMap<>();
        this.grupos.put("Grupo A", new ArrayList<>(java.util.Arrays.asList("Portugal", "Cuba", "França", "Japão")));
        this.grupos.put("Grupo B", new ArrayList<>(java.util.Arrays.asList("Espanha", "Senegal", "Brasil", "Marrocos")));
        this.grupos.put("Grupo C", new ArrayList<>(java.util.Arrays.asList("Argentina", "EUA", "Inglaterra", "Holanda")));
        this.grupos.put("Grupo D", new ArrayList<>(java.util.Arrays.asList("Alemanha", "Itália", "Colômbia", "Suíça")));
        this.grupos.put("Grupo E", new ArrayList<>(java.util.Arrays.asList("Bélgica", "Coreia do Sul", "Uruguai", "Gana")));
        this.grupos.put("Grupo F", new ArrayList<>(java.util.Arrays.asList("Croácia", "Canadá", "Dinamarca", "Sérvia")));
        this.grupos.put("Grupo G", new ArrayList<>(java.util.Arrays.asList("Polónia", "Austrália", "Áustria", "Nigéria")));
        this.grupos.put("Grupo H", new ArrayList<>(java.util.Arrays.asList("Turquia", "Chile", "Ucrânia", "Irão")));
    }
}
