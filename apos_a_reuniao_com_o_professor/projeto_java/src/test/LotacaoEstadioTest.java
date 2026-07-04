package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.*;

public class LotacaoEstadioTest {
    private BilheteiraManager bilManager;
    private CampeonatoManager campManager;

    @BeforeEach
    public void setUp() {
        bilManager = BilheteiraManager.getInstance();
        campManager = CampeonatoManager.getInstance();
        bilManager.reset();
        campManager.reset();
    }

    @Test
    public void testVendaExcedendoLotacaoSetor() {
        // Arrange: Estadio com setor de capacidade 10
        Estadio est = new Estadio("Estádio da Luz", "Lisboa");
        SetorEstadio setor = new SetorEstadio("Premium", 10, 150.0);
        est.adicionarSetor(setor);

        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa c = new Equipa("Cuba", "Castillo");
        campManager.registarEquipa(p);
        campManager.registarEquipa(c);

        Jogo jogo = new Jogo(1, "2026-06-25", "15:00", est, p, c, "Grupos", null, null);
        campManager.registarJogo(jogo);

        // Act 1: Vender 10 bilhetes respeitando o limite anti-bot (máximo 4 por transação)
        boolean ok1a = bilManager.venderBilhete(jogo, "Premium", 4);
        boolean ok1b = bilManager.venderBilhete(jogo, "Premium", 4);
        boolean ok1c = bilManager.venderBilhete(jogo, "Premium", 2);
        assertTrue(ok1a && ok1b && ok1c, "Vendas sucessivas acumulando 10 bilhetes devem ter sucesso");

        // Act 2: Tentar vender 1 bilhete a mais (capacidade esgotada)
        // Como o estadio é partilhado e setor já vendeu 10, deve falhar
        boolean ok2 = bilManager.venderBilhete(jogo, "Premium", 1);
        assertFalse(ok2, "Venda excedendo lotacao do setor deve falhar");
    }
}
