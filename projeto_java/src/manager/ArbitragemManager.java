package manager;

import domain.*;
import util.PersistenceUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador central para a gestao de Arbitragem (Singleton DCL).
 * Valida regras de neutralidade (nacionalidade) e repouso (48 horas).
 */
public class ArbitragemManager {
    private static volatile ArbitragemManager instance = null;

    private List<Arbitro> arbitros;
    private static final String ARBITROS_FILE = "arbitros.ser";

    private ArbitragemManager() {
        this.arbitros = PersistenceUtil.carregar(ARBITROS_FILE);
    }

    public static ArbitragemManager getInstance() {
        if (instance == null) {
            synchronized (ArbitragemManager.class) {
                if (instance == null) {
                    instance = new ArbitragemManager();
                }
            }
        }
        return instance;
    }

    public void saveAll() {
        PersistenceUtil.guardar(ARBITROS_FILE, this.arbitros);
    }

    public void reset() {
        this.arbitros = new ArrayList<>();
        saveAll();
    }

    public List<Arbitro> getArbitros() {
        return new ArrayList<>(this.arbitros);
    }

    public void registarArbitro(Arbitro arbitro) {
        if (arbitro == null) return;
        this.arbitros.removeIf(a -> a.getId() == arbitro.getId());
        this.arbitros.add(arbitro);
        saveAll();
    }

    public Arbitro procurarArbitroPorId(int id) {
        for (Arbitro a : this.arbitros) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    /**
     * Escala um arbitro para um jogo especifico, verificando a elegibilidade global
     * e o descanso regulamentar.
     */
    public boolean escalarArbitro(Jogo jogo, Arbitro arbitro, TipoArbitro tipo) {
        if (jogo == null || arbitro == null || tipo == null) return false;

        // 1. Verificar se existe pelo menos um arbitro do tipo solicitado elegivel no pool.
        // Se nao existir ninguem, lança IllegalStateException conforme os requisitos de ética.
        boolean algumElegivel = false;
        for (Arbitro r : this.arbitros) {
            if (r.getTipo() == tipo && isArbitroElegivel(jogo, r)) {
                algumElegivel = true;
                break;
            }
        }

        if (!algumElegivel) {
            throw new IllegalStateException("Não existem árbitros elegíveis para este jogo: conflito de neutralidade ou repouso insuficiente.");
        }

        // 2. Se o arbitro especifico fornecido nao for elegivel, retorna false.
        if (!isArbitroElegivel(jogo, arbitro)) {
            return false;
        }

        // 3. Associar o arbitro ao EscalaoArbitral
        EscalaoArbitral atual = jogo.getEscalaArbitros();
        Arbitro principal = (tipo == TipoArbitro.PRINCIPAL) ? arbitro : (atual != null ? atual.getPrincipal() : null);
        Arbitro assist1 = (atual != null ? atual.getAssistente1() : null);
        Arbitro assist2 = (atual != null ? atual.getAssistente2() : null);
        Arbitro quarto = (tipo == TipoArbitro.QUARTO) ? arbitro : (atual != null ? atual.getQuarto() : null);
        Arbitro var = (tipo == TipoArbitro.VAR) ? arbitro : (atual != null ? atual.getVar() : null);

        if (tipo == TipoArbitro.ASSISTENTE) {
            if (assist1 == null) {
                assist1 = arbitro;
            } else if (assist2 == null || assist2.equals(arbitro)) {
                assist2 = arbitro;
            } else {
                assist1 = arbitro;
            }
        }

        EscalaoArbitral novoEscalao = new EscalaoArbitral(principal, assist1, assist2, quarto, var);
        jogo.associarEscalaArbitros(novoEscalao);

        // Guarda o jogo atualizado no CampeonatoManager
        CampeonatoManager.getInstance().registarJogo(jogo);

        return true;
    }

    /**
     * Valida se o arbitro cumpre as restricoes de nacionalidade (neutralidade)
     * e o intervalo de repouso minimo de 48 horas.
     */
    public boolean isArbitroElegivel(Jogo jogo, Arbitro ref) {
        // Regra de neutralidade (Opcao B): nacionalidade do arbitro coincide com o pais da equipa
        if (jogo.getHomeTeam() != null && ref.getNacionalidade().equalsIgnoreCase(jogo.getHomeTeam().getNome())) {
            return false;
        }
        if (jogo.getAwayTeam() != null && ref.getNacionalidade().equalsIgnoreCase(jogo.getAwayTeam().getNome())) {
            return false;
        }

        // Regra de repouso: 48 horas de intervalo minimo
        List<Jogo> todosJogos = CampeonatoManager.getInstance().getJogos();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime novoJogoTime = LocalDateTime.parse(jogo.getData() + " " + jogo.getHora(), formatter);

        for (Jogo j : todosJogos) {
            if (j.getId() != jogo.getId() && temArbitroEscalado(j, ref)) {
                LocalDateTime outroJogoTime = LocalDateTime.parse(j.getData() + " " + j.getHora(), formatter);
                long diffHours = ChronoUnit.HOURS.between(outroJogoTime, novoJogoTime);
                if (Math.abs(diffHours) < 48) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean temArbitroEscalado(Jogo j, Arbitro ref) {
        EscalaoArbitral escala = j.getEscalaArbitros();
        if (escala == null) return false;
        return ref.equals(escala.getPrincipal()) ||
               ref.equals(escala.getAssistente1()) ||
               ref.equals(escala.getAssistente2()) ||
               ref.equals(escala.getQuarto()) ||
               ref.equals(escala.getVar());
    }

    /**
     * Regista avaliacoes de desempenho para todos os arbitros escalados no jogo
     * que nao sejam nulos.
     */
    public void avaliarDesempenho(Jogo jogo, int principalEstrelas, int assistente1Estrelas, int assistente2Estrelas, int quartoEstrelas, int varEstrelas) {
        Jogo j = CampeonatoManager.getInstance().procurarJogoPorId(jogo.getId());
        if (j == null) {
            throw new IllegalArgumentException("Jogo nao encontrado.");
        }

        EscalaoArbitral escala = j.getEscalaArbitros();
        if (escala == null) {
            throw new IllegalArgumentException("Este jogo nao possui escala de arbitros para avaliar.");
        }

        if (escala.getPrincipal() != null) {
            escala.getPrincipal().registarAvaliacao(principalEstrelas);
            registarArbitro(escala.getPrincipal());
        }
        if (escala.getAssistente1() != null) {
            escala.getAssistente1().registarAvaliacao(assistente1Estrelas);
            registarArbitro(escala.getAssistente1());
        }
        if (escala.getAssistente2() != null) {
            escala.getAssistente2().registarAvaliacao(assistente2Estrelas);
            registarArbitro(escala.getAssistente2());
        }
        if (escala.getQuarto() != null) {
            escala.getQuarto().registarAvaliacao(quartoEstrelas);
            registarArbitro(escala.getQuarto());
        }
        if (escala.getVar() != null) {
            escala.getVar().registarAvaliacao(varEstrelas);
            registarArbitro(escala.getVar());
        }
    }
}
