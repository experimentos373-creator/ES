package manager;

import domain.*;
import util.PersistenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador central de Autenticacao e Sessao (Singleton DCL).
 * Gere o estado de login dos utilizadores e a lista de utilizadores registados.
 */
public class AutenticacaoManager {
    private static volatile AutenticacaoManager instance = null;

    private List<Utilizador> utilizadores;
    private Utilizador utilizadorAtual;

    private static final String UTILIZADORES_FILE = "utilizadores.ser";

    private AutenticacaoManager() {
        this.utilizadores = PersistenceUtil.carregar(UTILIZADORES_FILE);
        this.utilizadorAtual = null;
    }

    public static AutenticacaoManager getInstance() {
        if (instance == null) {
            synchronized (AutenticacaoManager.class) {
                if (instance == null) {
                    instance = new AutenticacaoManager();
                }
            }
        }
        return instance;
    }

    public void saveAll() {
        PersistenceUtil.guardar(UTILIZADORES_FILE, this.utilizadores);
    }

    public void reset() {
        this.utilizadores = new ArrayList<>();
        this.utilizadorAtual = null;
        saveAll();
    }

    public List<Utilizador> getUtilizadores() {
        return new ArrayList<>(this.utilizadores);
    }

    public Utilizador getUtilizadorAtual() {
        return this.utilizadorAtual;
    }

    public boolean isAutenticado() {
        return this.utilizadorAtual != null;
    }

    /**
     * Autentica um utilizador pelo email.
     * @param email Email do utilizador
     * @return true se o utilizador existe e a sessao foi iniciada
     */
    public boolean autenticar(String email) {
        for (Utilizador u : this.utilizadores) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                this.utilizadorAtual = u;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        this.utilizadorAtual = null;
    }

    public void registarUtilizador(Utilizador utilizador) {
        if (utilizador == null) return;
        this.utilizadores.removeIf(u -> u.getEmail().equalsIgnoreCase(utilizador.getEmail()));
        this.utilizadores.add(utilizador);
        saveAll();
    }

    public Utilizador procurarUtilizadorPorEmail(String email) {
        for (Utilizador u : this.utilizadores) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }
}
