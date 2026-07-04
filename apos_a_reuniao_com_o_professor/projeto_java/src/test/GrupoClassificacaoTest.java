package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.CampeonatoManager;
import java.util.List;

public class GrupoClassificacaoTest {
    private CampeonatoManager manager;

    @BeforeEach
    public void setUp() {
        manager = CampeonatoManager.getInstance();
        manager.reset();
    }

    @Test
    public void testCalculoPontosSucesso() {
        // Arrange
        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa c = new Equipa("Cuba", "Castillo");
        manager.registarEquipa(p);
        manager.registarEquipa(c);
        
        manager.registarEquipaNoGrupo("Grupo A", "Portugal");
        manager.registarEquipaNoGrupo("Grupo A", "Cuba");

        Estadio est = new Estadio("Estádio da Luz", "Lisboa");
        Jogo j = new Jogo(1, "2026-06-25", "15:00", est, p, c, "Grupos", null, null);
        manager.registarJogo(j);

        // Act - Portugal vence Cuba por 2-0
        manager.finalizarJogoECorrerBracket(1, p, 2, 0, -1, -1, new EstatisticaJogo(60, 40, 10, 2, 5, 1));

        // Assert
        List<ClassificacaoLinha> standings = manager.calcularClassificacaoGrupo("Grupo A");
        assertNotNull(standings);
        assertEquals(4, standings.size());
        
        ClassificacaoLinha first = standings.get(0);
        ClassificacaoLinha cubaLine = standings.stream()
            .filter(line -> "Cuba".equals(line.getEquipa().getNome()))
            .findFirst()
            .orElse(null);

        assertEquals("Portugal", first.getEquipa().getNome());
        assertEquals(3, first.getPontos());
        assertEquals(1, first.getVitorias());
        assertEquals(2, first.getGolosMarcados());
        assertEquals(0, first.getGolosSofridos());

        assertNotNull(cubaLine);
        assertEquals("Cuba", cubaLine.getEquipa().getNome());
        assertEquals(0, cubaLine.getPontos());
        assertEquals(1, cubaLine.getDerrotas());
        assertEquals(0, cubaLine.getGolosMarcados());
        assertEquals(2, cubaLine.getGolosSofridos());
    }

    @Test
    public void testCriteriosDesempate() {
        // Arrange
        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa c = new Equipa("Cuba", "Castillo");
        Equipa f = new Equipa("França", "Deschamps");
        Equipa j = new Equipa("Japão", "Moriyasu");

        manager.registarEquipa(p);
        manager.registarEquipa(c);
        manager.registarEquipa(f);
        manager.registarEquipa(j);

        manager.registarEquipaNoGrupo("Grupo A", "Portugal");
        manager.registarEquipaNoGrupo("Grupo A", "Cuba");
        manager.registarEquipaNoGrupo("Grupo A", "França");
        manager.registarEquipaNoGrupo("Grupo A", "Japão");

        Estadio est = new Estadio("Estádio da Luz", "Lisboa");

        // Jogo 1: Portugal 3 vs 0 Cuba
        Jogo j1 = new Jogo(1, "2026-06-25", "15:00", est, p, c, "Grupos", null, null);
        // Jogo 2: França 2 vs 0 Japão
        Jogo j2 = new Jogo(2, "2026-06-25", "18:00", est, f, j, "Grupos", null, null);

        manager.registarJogo(j1);
        manager.registarJogo(j2);

        manager.finalizarJogoECorrerBracket(1, p, 3, 0, -1, -1, null);
        manager.finalizarJogoECorrerBracket(2, f, 2, 0, -1, -1, null);

        // Act
        List<ClassificacaoLinha> standings = manager.calcularClassificacaoGrupo("Grupo A");

        // Assert
        // Portugal (3 pontos, saldo +3, 3 golos) deve estar em 1º
        // França (3 pontos, saldo +2, 2 golos) deve estar em 2º
        assertEquals("Portugal", standings.get(0).getEquipa().getNome());
        assertEquals("França", standings.get(1).getEquipa().getNome());
    }
}
