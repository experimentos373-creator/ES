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

        // Finaliza o jogo
        jogo.finalizar(winnerToSet, goalsHome, goalsAway, penaltiesHome, penaltiesAway, stats);

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
