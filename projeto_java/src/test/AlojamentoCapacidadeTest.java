package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.*;

public class AlojamentoCapacidadeTest {
    private LogisticaManager logManager;
    private CampeonatoManager campManager;

    @BeforeEach
    public void setUp() {
        logManager = LogisticaManager.getInstance();
        campManager = CampeonatoManager.getInstance();
        logManager.reset();
        campManager.reset();
    }

    @Test
    public void testAlocacaoHotelCapacidadeExcedida() {
        // Arrange: Equipa com 26 jogadores, Hotel com 25 quartos
        Equipa eq = new Equipa("Portugal", "Martínez");
        for (int i = 1; i <= 26; i++) {
            eq.adicionarJogador(new Jogador(i, i, "Jogador " + i, "Avancado", EstadoJogador.APTO));
        }
        campManager.registarEquipa(eq);

        Hotel hotel = new Hotel(1, "Hotel Marquês", "Lisboa", 25);
        logManager.registarHotel(hotel);

        // Act & Assert: Capacidade 25 < Squad 26, deve falhar
        boolean result = logManager.alocarHotel(eq, hotel, "2026-06-20", "2026-07-15");
        assertFalse(result, "Deve falhar quando o plantel excede a capacidade de quartos");
    }

    @Test
    public void testAlocacaoHotelEquipasDiferentesFalha() {
        // Arrange: Equipa A com 10 jogadores, Equipa B com 8 jogadores, Hotel com 15 quartos
        Equipa eqA = new Equipa("Portugal", "Martínez");
        for (int i = 1; i <= 10; i++) {
            eqA.adicionarJogador(new Jogador(i, i, "Jogador " + i, "Avancado", EstadoJogador.APTO));
        }
        Equipa eqB = new Equipa("Cuba", "Castillo");
        for (int i = 11; i <= 18; i++) {
            eqB.adicionarJogador(new Jogador(i, i - 10, "Jogador " + i, "Defesa", EstadoJogador.APTO));
        }
        campManager.registarEquipa(eqA);
        campManager.registarEquipa(eqB);

        Hotel hotel = new Hotel(1, "Hotel Marquês", "Lisboa", 15);
        logManager.registarHotel(hotel);

        // Act: Alocar equipa A
        boolean okA = logManager.alocarHotel(eqA, hotel, "2026-06-20", "2026-07-15");
        assertTrue(okA, "Primeira alocacao deve ter sucesso");

        // Assert: Tentar alocar equipa B no mesmo hotel deve falhar (exclusividade)
        boolean okB = logManager.alocarHotel(eqB, hotel, "2026-06-20", "2026-07-15");
        assertFalse(okB, "Segunda alocacao deve falhar - hotel ja ocupado por outra equipa");
    }
}
