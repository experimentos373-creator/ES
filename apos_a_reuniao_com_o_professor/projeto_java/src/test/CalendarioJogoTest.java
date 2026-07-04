package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.CampeonatoManager;

public class CalendarioJogoTest {
    private CampeonatoManager manager;

    @BeforeEach
    public void setUp() {
        manager = CampeonatoManager.getInstance();
        manager.reset();
    }

    @Test
    public void testAgendarJogoFaseCorreta() {
        // Arrange
        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa c = new Equipa("Cuba", "Castillo");
        Estadio est = new Estadio("Estádio da Luz", "Lisboa");
        
        manager.registarEquipa(p);
        manager.registarEquipa(c);
        manager.registarEstadio(est);

        Jogo j = new Jogo(10, "2026-06-25", "15:00", est, p, c, "Oitavos", null, null);

        // Act
        manager.registarJogo(j);

        // Assert
        Jogo recuperado = manager.procurarJogoPorId(10);
        assertNotNull(recuperado);
        assertEquals("Oitavos", recuperado.getPhase());
        assertEquals("Portugal", recuperado.getHomeTeam().getNome());
        assertEquals("Cuba", recuperado.getAwayTeam().getNome());
        assertEquals(StatusJogo.AGENDADO, recuperado.getStatus());
    }

    @Test
    public void testEquipasRepetidasMesmaDataFalha() {
        // Arrange
        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa c = new Equipa("Cuba", "Castillo");
        Equipa f = new Equipa("França", "Deschamps");
        Estadio est = new Estadio("Estádio da Luz", "Lisboa");

        manager.registarEquipa(p);
        manager.registarEquipa(c);
        manager.registarEquipa(f);
        manager.registarEstadio(est);

        // Portugal joga contra Cuba a 2026-06-25
        Jogo j1 = new Jogo(1, "2026-06-25", "15:00", est, p, c, "Grupos", null, null);
        manager.registarJogo(j1);

        // Act & Assert: Tentar agendar outro jogo com Portugal na mesma data deve falhar
        Jogo j2 = new Jogo(2, "2026-06-25", "18:00", est, p, f, "Grupos", null, null);
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.registarJogo(j2);
        });
    }
}
