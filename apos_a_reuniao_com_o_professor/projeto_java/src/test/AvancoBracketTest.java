package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.CampeonatoManager;

public class AvancoBracketTest {
    private CampeonatoManager manager;

    @BeforeEach
    public void setUp() {
        manager = CampeonatoManager.getInstance();
        manager.reset();
    }

    @Test
    public void testAvancoAutomaticoEliminatorias() {
        // Arrange
        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa c = new Equipa("Cuba", "Castillo");
        Estadio est = new Estadio("Estádio da Luz", "Lisboa");

        manager.registarEquipa(p);
        manager.registarEquipa(c);

        // Criamos o jogo seguinte (Quartos)
        Jogo jQuartos = new Jogo(2, "2026-07-08", "18:00", est, null, null, "Quartos", null, null);
        manager.registarJogo(jQuartos);

        // Criamos o jogo atual (Oitavos) que avança para os Quartos como HOME
        Jogo jOitavos = new Jogo(1, "2026-07-02", "18:00", est, p, c, "Oitavos", jQuartos, PosicaoBracket.HOME);
        manager.registarJogo(jOitavos);

        // Act - Finaliza jogo de Oitavos (Portugal vence 1-0)
        manager.finalizarJogoECorrerBracket(1, p, 1, 0, -1, -1, null);

        // Assert - O vencedor (Portugal) deve estar no slot HOME do jogo de Quartos
        Jogo proxRecuperado = manager.procurarJogoPorId(2);
        assertNotNull(proxRecuperado);
        assertNotNull(proxRecuperado.getHomeTeam());
        assertEquals("Portugal", proxRecuperado.getHomeTeam().getNome());
    }

    @Test
    public void testAvancoComPenaltiesObrigatorios() {
        // Arrange
        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa c = new Equipa("Cuba", "Castillo");
        Estadio est = new Estadio("Estádio da Luz", "Lisboa");

        manager.registarEquipa(p);
        manager.registarEquipa(c);

        Jogo j = new Jogo(1, "2026-07-02", "18:00", est, p, c, "Oitavos", null, null);
        manager.registarJogo(j);

        // Act & Assert: Empate sem penalties deve falhar nas eliminatórias
        assertThrows(IllegalArgumentException.class, () -> {
            manager.finalizarJogoECorrerBracket(1, null, 1, 1, -1, -1, null);
        });

        // Act & Assert: Empate com penalties empatados deve falhar
        assertThrows(IllegalArgumentException.class, () -> {
            manager.finalizarJogoECorrerBracket(1, null, 1, 1, 4, 4, null);
        });

        // Act & Assert: Empate com penalties válidos deve passar (Portugal vence nos penaltis por 4-3)
        manager.finalizarJogoECorrerBracket(1, p, 1, 1, 4, 3, null);

        Jogo recuperado = manager.procurarJogoPorId(1);
        assertEquals(StatusJogo.FINALIZADO, recuperado.getStatus());
        assertEquals("Portugal", recuperado.getWinner().getNome());
    }
}
