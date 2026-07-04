package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.CampeonatoManager;
import java.util.List;

public class SorteioPotesTest {
    private CampeonatoManager manager;

    @BeforeEach
    public void setUp() {
        manager = CampeonatoManager.getInstance();
        manager.reset();
    }

    @Test
    public void testSorteioPotesValido() {
        // Arrange: Registar 16 equipas com rankings diferentes
        // Pote 1: Pontos de 1600 a 1900
        // Pote 2: Pontos de 1200 a 1500
        // Pote 3: Pontos de 800 a 1100
        // Pote 4: Pontos de 400 a 700
        for (int i = 1; i <= 16; i++) {
            Equipa eq = new Equipa("Seleção " + i, "Treinador " + i);
            eq.setRankingPontos(2000 - (i * 100)); // Seleção 1 terá 1900, Seleção 16 terá 400
            manager.registarEquipa(eq);
        }

        // Act: Executar o sorteio
        boolean ok = manager.realizarSorteioGrupos();

        // Assert
        assertTrue(ok, "Sorteio deve ser bem-sucedido com 16 equipas");

        // Verificar que os grupos A, B, C, D têm exatamente 4 equipas cada
        List<Equipa> equipasA = manager.getEquipas();
        assertEquals(16, equipasA.size());

        // Verificar as atribuições dos grupos
        // Nota: O CampeonatoManager expõe os grupos direta ou indiretamente.
        // Vamos verificar as chaves
        java.util.Map<String, List<String>> gruposMap = null;
        try {
            java.lang.reflect.Field field = CampeonatoManager.class.getDeclaredField("grupos");
            field.setAccessible(true);
            gruposMap = (java.util.Map<String, List<String>>) field.get(manager);
        } catch (Exception e) {
            fail("Não foi possível aceder ao campo grupos refletivamente");
        }

        assertNotNull(gruposMap);
        assertEquals(4, gruposMap.size(), "Devem existir exatamente 4 grupos");
        assertTrue(gruposMap.containsKey("Grupo A"));
        assertTrue(gruposMap.containsKey("Grupo B"));
        assertTrue(gruposMap.containsKey("Grupo C"));
        assertTrue(gruposMap.containsKey("Grupo D"));

        for (String key : gruposMap.keySet()) {
            List<String> eqNames = gruposMap.get(key);
            assertEquals(4, eqNames.size(), "Cada grupo deve conter exatamente 4 seleções");
            
            // Garantir que não há equipas repetidas no mesmo grupo
            long uniqueCount = eqNames.stream().distinct().count();
            assertEquals(4, uniqueCount);
        }
    }

    @Test
    public void testSorteioPotesInsuficientesEquipasFalha() {
        // Registar apenas 10 equipas
        for (int i = 1; i <= 10; i++) {
            Equipa eq = new Equipa("Seleção " + i, "Treinador " + i);
            eq.setRankingPontos(1000);
            manager.registarEquipa(eq);
        }

        // Act & Assert: Sorteio deve falhar
        boolean ok = manager.realizarSorteioGrupos();
        assertFalse(ok, "Sorteio deve falhar com menos de 16 equipas");
    }
}
