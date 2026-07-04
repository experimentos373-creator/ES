package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.*;

public class IntervaloArbitroTest {
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
    public void testArbitroMenosDe48HorasFalha() {
        // Arrange
        Equipa b = new Equipa("Brasil", "Júnior");
        Equipa f = new Equipa("França", "Deschamps");
        Equipa esp = new Equipa("Espanha", "De la Fuente");
        Equipa sen = new Equipa("Senegal", "Cissé");
        Equipa ale = new Equipa("Alemanha", "Nagelsmann");
        Equipa ita = new Equipa("Itália", "Spalletti");
        Estadio est = new Estadio("Santiago Bernabéu", "Madrid");

        // Jogo 1: França vs Brasil (25 de Junho às 15:00)
        Jogo j1 = new Jogo(1, "2026-06-25", "15:00", est, f, b, "Grupos", null, null);
        
        // Jogo 2: Espanha vs Senegal (27 de Junho às 14:00) - 47 horas (falha)
        Jogo j2 = new Jogo(2, "2026-06-27", "14:00", est, esp, sen, "Grupos", null, null);

        // Jogo 3: Alemanha vs Itália (27 de Junho às 15:00) - 48 horas exatas (sucesso)
        Jogo j3 = new Jogo(3, "2026-06-27", "15:00", est, ale, ita, "Grupos", null, null);

        Arbitro ref = new Arbitro(1, "ref1@fifa.com", "Szymon Marciniak", "Polonia", TipoArbitro.PRINCIPAL);

        campManager.registarEquipa(b);
        campManager.registarEquipa(f);
        campManager.registarEquipa(esp);
        campManager.registarEquipa(sen);
        campManager.registarEquipa(ale);
        campManager.registarEquipa(ita);
        campManager.registarJogo(j1);
        campManager.registarJogo(j2);
        campManager.registarJogo(j3);
        
        arbManager.registarArbitro(ref);
        Arbitro refPool = new Arbitro(2, "ref2@fifa.com", "Artur Soares Dias", "Portugal", TipoArbitro.PRINCIPAL);
        arbManager.registarArbitro(refPool);

        // Escalar para o Jogo 1
        assertTrue(arbManager.escalarArbitro(j1, ref, TipoArbitro.PRINCIPAL));

        // Act & Assert 1: Tentar escalar para o Jogo 2 (47h depois) deve retornar false
        boolean result47h = arbManager.escalarArbitro(j2, ref, TipoArbitro.PRINCIPAL);
        assertFalse(result47h);

        // Act & Assert 2: Tentar escalar para o Jogo 3 (48h depois) deve retornar true (limite estrito)
        boolean result48h = arbManager.escalarArbitro(j3, ref, TipoArbitro.PRINCIPAL);
        assertTrue(result48h);
    }
}
