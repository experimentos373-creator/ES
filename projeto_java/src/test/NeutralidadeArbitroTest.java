package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.*;

public class NeutralidadeArbitroTest {
    private ArbitragemManager arbManager;
    private CampeonatoManager campManager;

    @BeforeEach
    public void setUp() {
        arbManager = ArbitragemManager.getInstance();
        campManager = CampeonatoManager.getInstance();
        arbManager.reset();
        campManager.reset();
    }

    @Test
    public void testEscalarArbitroNacionalidadeDiferenteSucesso() {
        // Arrange: Jogo Portugal vs Brasil. Arbitro e Polaco (Polonia).
        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa b = new Equipa("Brasil", "Júnior");
        Estadio est = new Estadio("Santiago Bernabéu", "Madrid");
        Jogo j = new Jogo(1, "2026-06-25", "15:00", est, p, b, "Grupos", null, null);

        Arbitro ref = new Arbitro(1, "ref1@fifa.com", "Szymon Marciniak", "Polonia", TipoArbitro.PRINCIPAL);

        campManager.registarEquipa(p);
        campManager.registarEquipa(b);
        campManager.registarJogo(j);
        arbManager.registarArbitro(ref);

        // Act
        boolean result = arbManager.escalarArbitro(j, ref, TipoArbitro.PRINCIPAL);

        // Assert
        assertTrue(result);
        assertNotNull(j.getEscalaArbitros());
        assertEquals(ref, j.getEscalaArbitros().getPrincipal());
    }

    @Test
    public void testEscalarArbitroMesmaNacionalidadeFalha() {
        // Arrange: Jogo Portugal vs Brasil. Arbitro e Portugues (Portugal).
        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa b = new Equipa("Brasil", "Júnior");
        Estadio est = new Estadio("Santiago Bernabéu", "Madrid");
        Jogo j = new Jogo(1, "2026-06-25", "15:00", est, p, b, "Grupos", null, null);

        Arbitro ref = new Arbitro(1, "ref1@fifa.com", "Artur Soares Dias", "Portugal", TipoArbitro.PRINCIPAL);
        Arbitro ref2 = new Arbitro(2, "ref2@fifa.com", "Szymon Marciniak", "Polonia", TipoArbitro.PRINCIPAL);

        campManager.registarEquipa(p);
        campManager.registarEquipa(b);
        campManager.registarJogo(j);
        arbManager.registarArbitro(ref);
        arbManager.registarArbitro(ref2);

        // Act: escalar arbitro de mesma nacionalidade de uma das equipas deve retornar false
        boolean result = arbManager.escalarArbitro(j, ref, TipoArbitro.PRINCIPAL);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testEscalarArbitroSemNenhumElegivelNoPoolFalha() {
        // Arrange: Jogo Portugal vs Brasil. O unico arbitro no pool e Portugues.
        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa b = new Equipa("Brasil", "Júnior");
        Estadio est = new Estadio("Santiago Bernabéu", "Madrid");
        Jogo j = new Jogo(1, "2026-06-25", "15:00", est, p, b, "Grupos", null, null);

        Arbitro ref = new Arbitro(1, "ref1@fifa.com", "Artur Soares Dias", "Portugal", TipoArbitro.PRINCIPAL);

        campManager.registarEquipa(p);
        campManager.registarEquipa(b);
        campManager.registarJogo(j);
        arbManager.registarArbitro(ref);

        // Act & Assert: Tentar escalar deve lancar IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            arbManager.escalarArbitro(j, ref, TipoArbitro.PRINCIPAL);
        });
    }

    @Test
    public void testEscalarArbitroConflitoTotalDeRestricoesFalha() {
        // Arrange: Arbitro portugues, ja apitou ha menos de 48h.
        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa b = new Equipa("Brasil", "Júnior");
        Equipa f = new Equipa("França", "Deschamps");
        Estadio est = new Estadio("Santiago Bernabéu", "Madrid");

        Jogo j1 = new Jogo(1, "2026-06-25", "15:00", est, f, b, "Grupos", null, null);
        Jogo j2 = new Jogo(2, "2026-06-26", "15:00", est, p, f, "Grupos", null, null);

        Arbitro ref = new Arbitro(1, "ref1@fifa.com", "Artur Soares Dias", "Portugal", TipoArbitro.PRINCIPAL);
        Arbitro refPool = new Arbitro(2, "ref2@fifa.com", "Szymon Marciniak", "Polonia", TipoArbitro.PRINCIPAL);

        campManager.registarEquipa(p);
        campManager.registarEquipa(b);
        campManager.registarEquipa(f);
        campManager.registarJogo(j1);
        campManager.registarJogo(j2);
        
        arbManager.registarArbitro(ref);
        arbManager.registarArbitro(refPool);

        // Escala o arbitro portugues para o Jogo 1 (sucesso pois e neutro para França vs Brasil)
        assertTrue(arbManager.escalarArbitro(j1, ref, TipoArbitro.PRINCIPAL));

        // Act: Tentar escalar o mesmo arbitro para o Jogo 2 (falha por nacionalidade e repouso)
        boolean result = arbManager.escalarArbitro(j2, ref, TipoArbitro.PRINCIPAL);

        // Assert
        assertFalse(result);
    }
}
