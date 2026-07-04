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
        // Arrange: Equipa com 26 jogadores, Hotel com 25 lugares
        Equipa eq = new Equipa("Portugal", "Martínez");
        for (int i = 1; i <= 26; i++) {
            eq.adicionarJogador(new Jogador(i, i, "Jogador " + i, "Avancado", EstadoJogador.APTO));
        }
        campManager.registarEquipa(eq);

        Hotel hotel = new Hotel(1, "Hotel Marquês", "Lisboa", 25);
        logManager.registarHotel(hotel);

        // Act & Assert: Capacidade 25 < Squad 26, deve falhar
        boolean result = logManager.alocarHotel(eq, hotel, "2026-06-20", "2026-07-15");
        assertFalse(result, "Deve falhar quando o plantel excede a capacidade de pessoas");
    }

    @Test
    public void testAlocacaoHotelEquipasDiferentesFalhaPorCapacidade() {
        // Arrange: Equipa A com 10 jogadores, Equipa B com 8 jogadores, Hotel com 15 lugares
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

        // Assert: Tentar alocar equipa B no mesmo hotel deve falhar pois 10 + 8 = 18 > 15
        boolean okB = logManager.alocarHotel(eqB, hotel, "2026-06-20", "2026-07-15");
        assertFalse(okB, "Segunda alocacao deve falhar devido a capacidade total excedida");
    }

    @Test
    public void testAlocacaoMultiplasEquipasSucesso() {
        // Arrange: Equipa A com 5 jogadores, Equipa B com 4 jogadores, Hotel com 10 lugares
        Equipa eqA = new Equipa("Portugal", "Martínez");
        for (int i = 1; i <= 5; i++) {
            eqA.adicionarJogador(new Jogador(i, i, "Jogador " + i, "Avancado", EstadoJogador.APTO));
        }
        Equipa eqB = new Equipa("Cuba", "Castillo");
        for (int i = 6; i <= 9; i++) {
            eqB.adicionarJogador(new Jogador(i, i - 5, "Jogador " + i, "Defesa", EstadoJogador.APTO));
        }
        campManager.registarEquipa(eqA);
        campManager.registarEquipa(eqB);

        Hotel hotel = new Hotel(1, "Hotel Marquês", "Lisboa", 10);
        logManager.registarHotel(hotel);

        // Act: Alocar ambas as equipas
        boolean okA = logManager.alocarHotel(eqA, hotel, "2026-06-20", "2026-07-15");
        boolean okB = logManager.alocarHotel(eqB, hotel, "2026-06-20", "2026-07-15");

        // Assert
        assertTrue(okA, "Alocação de Portugal deve ter sucesso");
        assertTrue(okB, "Alocação de Cuba no mesmo hotel deve ter sucesso");
        assertEquals(2, hotel.getAlojamentos().size(), "O hotel deve conter exatamente 2 alojamentos ativos");
    }

    @Test
    public void testAlocacaoLimiteExato() {
        // Arrange: Equipa A com 5 jogadores, Equipa B com 5 jogadores, Hotel com 10 lugares (limite exato)
        Equipa eqA = new Equipa("Portugal", "Martínez");
        for (int i = 1; i <= 5; i++) {
            eqA.adicionarJogador(new Jogador(i, i, "Jogador " + i, "Avancado", EstadoJogador.APTO));
        }
        Equipa eqB = new Equipa("Cuba", "Castillo");
        for (int i = 6; i <= 10; i++) {
            eqB.adicionarJogador(new Jogador(i, i - 5, "Jogador " + i, "Defesa", EstadoJogador.APTO));
        }
        campManager.registarEquipa(eqA);
        campManager.registarEquipa(eqB);

        Hotel hotel = new Hotel(1, "Hotel Marquês", "Lisboa", 10);
        logManager.registarHotel(hotel);

        // Act
        boolean okA = logManager.alocarHotel(eqA, hotel, "2026-06-20", "2026-07-15");
        boolean okB = logManager.alocarHotel(eqB, hotel, "2026-06-20", "2026-07-15");

        // Assert
        assertTrue(okA);
        assertTrue(okB, "Alocação no limite exato de capacidade deve passar");
    }

    @Test
    public void testCheckoutParcialRecalculaCapacidade() {
        // Arrange: Equipa A com 5 jogadores, Equipa B com 6 jogadores, Hotel com 10 lugares
        Equipa eqA = new Equipa("Portugal", "Martínez");
        for (int i = 1; i <= 5; i++) {
            eqA.adicionarJogador(new Jogador(i, i, "Jogador " + i, "Avancado", EstadoJogador.APTO));
        }
        Equipa eqB = new Equipa("Cuba", "Castillo");
        for (int i = 6; i <= 11; i++) {
            eqB.adicionarJogador(new Jogador(i, i - 5, "Jogador " + i, "Defesa", EstadoJogador.APTO));
        }
        campManager.registarEquipa(eqA);
        campManager.registarEquipa(eqB);

        Hotel hotel = new Hotel(1, "Hotel Marquês", "Lisboa", 10);
        logManager.registarHotel(hotel);

        // Alocar Portugal (ocupa 5)
        assertTrue(logManager.alocarHotel(eqA, hotel, "2026-06-20", "2026-07-15"));

        // Cuba necessita de 6. Sobram apenas 5 no hotel. Deve falhar.
        assertFalse(logManager.alocarHotel(eqB, hotel, "2026-06-20", "2026-07-15"));

        // Efetuar check-out de Portugal (liberta 5)
        assertTrue(logManager.registarCheckoutEquipa(hotel, eqA));

        // Tentar novamente alocar Cuba (ocupa 6). Agora há 10 livres. Deve passar.
        assertTrue(logManager.alocarHotel(eqB, hotel, "2026-06-20", "2026-07-15"));
    }

    @Test
    public void testDoubleCheckInEmHoteisDiferentesRejeitado() {
        // Arrange: Equipa com 5 jogadores, dois hotéis com capacidade de 10
        Equipa eq = new Equipa("Portugal", "Martínez");
        for (int i = 1; i <= 5; i++) {
            eq.adicionarJogador(new Jogador(i, i, "Jogador " + i, "Avancado", EstadoJogador.APTO));
        }
        campManager.registarEquipa(eq);

        Hotel hotel1 = new Hotel(1, "Hotel Um", "Lisboa", 10);
        Hotel hotel2 = new Hotel(2, "Hotel Dois", "Lisboa", 10);
        logManager.registarHotel(hotel1);
        logManager.registarHotel(hotel2);

        // Act: Alojar no Hotel 1 (ok)
        assertTrue(logManager.alocarHotel(eq, hotel1, "2026-06-20", "2026-07-15"));

        // Assert: Alojar a mesma equipa no Hotel 2 (deve falhar por regra de unicidade)
        assertFalse(logManager.alocarHotel(eq, hotel2, "2026-06-20", "2026-07-15"), "Equipa não pode ser hospedada em dois hotéis diferentes ao mesmo tempo");
    }

    @Test
    public void testDoubleCheckInNoMesmoHotelRejeitado() {
        // Arrange: Equipa com 5 jogadores, hotel com capacidade de 10
        Equipa eq = new Equipa("Portugal", "Martínez");
        for (int i = 1; i <= 5; i++) {
            eq.adicionarJogador(new Jogador(i, i, "Jogador " + i, "Avancado", EstadoJogador.APTO));
        }
        campManager.registarEquipa(eq);

        Hotel hotel = new Hotel(1, "Hotel Marquês", "Lisboa", 10);
        logManager.registarHotel(hotel);

        // Act: Alojar no Hotel (ok)
        assertTrue(logManager.alocarHotel(eq, hotel, "2026-06-20", "2026-07-15"));

        // Assert: Alojar a mesma equipa no mesmo Hotel outra vez (deve falhar por unicidade)
        assertFalse(logManager.alocarHotel(eq, hotel, "2026-06-20", "2026-07-15"));
    }

    @Test
    public void testCheckOutInvalido() {
        // Arrange: Equipa não hospedada
        Equipa eq = new Equipa("Portugal", "Martínez");
        campManager.registarEquipa(eq);

        Hotel hotel = new Hotel(1, "Hotel Marquês", "Lisboa", 10);
        logManager.registarHotel(hotel);

        // Act & Assert: Checkout de equipa não hospedada deve retornar false
        assertFalse(logManager.registarCheckoutEquipa(hotel, eq), "Checkout de equipa não hospedada deve retornar false");
    }
}
