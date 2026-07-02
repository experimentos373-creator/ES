package manager;

import domain.*;
import util.PersistenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador central para Bilhetica (Singleton DCL).
 * Garante isolamento de vendas por jogo e regras anti-bot.
 */
public class BilheteiraManager {
    private static volatile BilheteiraManager instance = null;

    private List<Bilhete> bilhetes;
    private static final String BILHETES_FILE = "bilhetes.ser";

    private BilheteiraManager() {
        this.bilhetes = PersistenceUtil.carregar(BILHETES_FILE);
    }

    public static BilheteiraManager getInstance() {
        if (instance == null) {
            synchronized (BilheteiraManager.class) {
                if (instance == null) {
                    instance = new BilheteiraManager();
                }
            }
        }
        return instance;
    }

    public void saveAll() {
        PersistenceUtil.guardar(BILHETES_FILE, this.bilhetes);
    }

    public void reset() {
        this.bilhetes = new ArrayList<>();
        saveAll();
    }

    public List<Bilhete> getBilhetes() {
        return new ArrayList<>(this.bilhetes);
    }

    public synchronized boolean venderBilhete(Jogo jogo, String nomeSetor, int quantidade) {
        if (jogo == null || nomeSetor == null || nomeSetor.isEmpty()) return false;

        // Regra anti-bot: limite de 1 a 4 bilhetes por transacao
        if (quantidade <= 0 || quantidade > 4) {
            return false;
        }

        Estadio estadio = jogo.getEstadio();
        if (estadio == null) return false;

        SetorEstadio setor = estadio.getSetorPorNome(nomeSetor);
        if (setor == null) return false;

        // Tentar vender no setor
        boolean sucesso = setor.venderBilhete(quantidade);
        if (sucesso) {
            // Persistir o estado atualizado do estadio atraves do jogo no CampeonatoManager
            CampeonatoManager.getInstance().registarJogo(jogo);
            // Registrar a venda
            for (int i = 0; i < quantidade; i++) {
                this.bilhetes.add(new Bilhete(jogo.getId(), nomeSetor, setor.getPrecoBase()));
            }
            saveAll();
        }
        return sucesso;
    }
}
