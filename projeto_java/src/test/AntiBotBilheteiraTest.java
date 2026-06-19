package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.*;

public class AntiBotBilheteiraTest {
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
    public void testLimiteMaximoPorCompra() {
        // Arrange: Estadio com setor de capacidade 20
        Estadio est = new Estadio("Estádio da Luz", "Lisboa");
        SetorEstadio setor = new SetorEstadio("Premium", 20, 150.0);
        est.adicionarSetor(setor);

        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa c = new Equipa("Cuba", "Castillo");
        campManager.registarEquipa(p);
        campManager.registarEquipa(c);

        Jogo jogo = new Jogo(1, "2026-06-25", "15:00", est, p, c, "Grupos", null, null);
        campManager.registarJogo(jogo);

        // Act & Assert: 0 bilhetes deve falhar
        assertFalse(bilManager.venderBilhete(jogo, "Premium", 0), "Quantidade 0 deve ser rejeitada");

        // Act & Assert: -1 bilhetes deve falhar
        assertFalse(bilManager.venderBilhete(jogo, "Premium", -1), "Quantidade negativa deve ser rejeitada");

        // Act & Assert: 5 bilhetes (acima do limite 4) deve falhar
        assertFalse(bilManager.venderBilhete(jogo, "Premium", 5), "Quantidade 5 deve ser rejeitada (limite anti-bot = 4)");

        // Act & Assert: 4 bilhetes (limite exato) deve passar
        assertTrue(bilManager.venderBilhete(jogo, "Premium", 4), "Quantidade 4 deve ser aceite (limite anti-bot)");

        // Act & Assert: 1 bilhete deve passar
        assertTrue(bilManager.venderBilhete(jogo, "Premium", 1), "Quantidade 1 deve ser aceite");
    }
}
