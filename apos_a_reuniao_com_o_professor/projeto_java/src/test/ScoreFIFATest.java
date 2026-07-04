package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.*;

public class ScoreFIFATest {
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
    public void testRecalculoScoreFIFA() {
        // Arrange
        Equipa b = new Equipa("Brasil", "Júnior");
        Equipa f = new Equipa("França", "Deschamps");
        Estadio est = new Estadio("Santiago Bernabéu", "Madrid");
        Jogo jogo = new Jogo(1, "2026-06-25", "15:00", est, f, b, "Grupos", null, null);

        Arbitro ref = new Arbitro(1, "ref1@fifa.com", "Szymon Marciniak", "Polonia", TipoArbitro.PRINCIPAL);

        campManager.registarEquipa(b);
        campManager.registarEquipa(f);
        campManager.registarJogo(jogo);
        arbManager.registarArbitro(ref);

        // Escalar o arbitro para o jogo
        assertTrue(arbManager.escalarArbitro(jogo, ref, TipoArbitro.PRINCIPAL));

        // Act 1: Avaliar com 4 estrelas (score FIFA deve ir para 80)
        arbManager.avaliarDesempenho(jogo, 4, -1, -1, -1, -1);

        // Assert 1
        Arbitro recuperado1 = arbManager.procurarArbitroPorId(1);
        assertEquals(80, recuperado1.getScoreFIFA());
        assertEquals(1, recuperado1.getTotalAvaliacoes());

        // Act 2: Agendar outro jogo e avaliar com 3 estrelas (media de 80 e 60 deve dar 70)
        Jogo jogo2 = new Jogo(2, "2026-06-27", "18:00", est, f, b, "Grupos", null, null);
        campManager.registarJogo(jogo2);
        assertTrue(arbManager.escalarArbitro(jogo2, ref, TipoArbitro.PRINCIPAL));

        arbManager.avaliarDesempenho(jogo2, 3, -1, -1, -1, -1);

        // Assert 2
        Arbitro recuperado2 = arbManager.procurarArbitroPorId(1);
        assertEquals(70, recuperado2.getScoreFIFA());
        assertEquals(2, recuperado2.getTotalAvaliacoes());
    }

    @Test
    public void testResetScore() {
        Arbitro ref = new Arbitro(2, "ref2@fifa.com", "Szymon Marciniak", "Polonia", TipoArbitro.PRINCIPAL);
        ref.registarAvaliacao(4);
        ref.registarAvaliacao(5);
        assertEquals(90, ref.getScoreFIFA());
        assertEquals(2, ref.getTotalAvaliacoes());

        ref.resetScore();
        assertEquals(0, ref.getScoreFIFA());
        assertEquals(0, ref.getTotalAvaliacoes());
    }
}
