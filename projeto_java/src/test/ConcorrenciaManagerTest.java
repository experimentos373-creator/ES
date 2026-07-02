package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcorrenciaManagerTest {
    private LogisticaManager logManager;
    private BilheteiraManager bilManager;
    private CampeonatoManager campManager;

    @BeforeEach
    public void setUp() {
        logManager = LogisticaManager.getInstance();
        bilManager = BilheteiraManager.getInstance();
        campManager = CampeonatoManager.getInstance();
        logManager.reset();
        bilManager.reset();
        campManager.reset();
    }

    @Test
    public void testConcorrenciaAlojamentoHotel() throws InterruptedException {
        // Arrange: Hotel com capacidade de 25 pessoas
        Hotel hotel = new Hotel(1, "Hotel Concorrente", "Porto", 25);
        logManager.registarHotel(hotel);

        // Criar 10 equipas com 10 jogadores cada (soma total = 100)
        // O hotel só pode alojar no máximo 2 equipas (20 pessoas). A 3ª excederia (30 > 25).
        int numThreads = 10;
        List<Equipa> equipas = new ArrayList<>();
        for (int i = 1; i <= numThreads; i++) {
            Equipa eq = new Equipa("Seleção " + i, "Treinador " + i);
            for (int k = 1; k <= 10; k++) {
                eq.adicionarJogador(new Jogador((i * 100) + k, k, "Jogador " + k, "Defesa", EstadoJogador.APTO));
            }
            campManager.registarEquipa(eq);
            equipas.add(eq);
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger sucessos = new AtomicInteger(0);
        AtomicInteger falhas = new AtomicInteger(0);

        // Act: Disparar 10 threads concorrentes para alocar o mesmo hotel
        for (final Equipa eq : equipas) {
            executor.submit(() -> {
                try {
                    latch.await(); // Esperar pelo sinal para disparar em simultâneo
                    boolean ok = logManager.alocarHotel(eq, hotel, "2026-06-20", "2026-07-15");
                    if (ok) {
                        sucessos.incrementAndGet();
                    } else {
                        falhas.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        latch.countDown(); // Sinal de disparo
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        // Assert: Exatamente 2 equipas devem ter sucesso, e 8 devem falhar
        assertEquals(2, sucessos.get(), "Exatamente 2 equipas de 10 jogadores devem ser alojadas no hotel de capacidade 25");
        assertEquals(8, falhas.get(), "8 equipas devem falhar na alocação devido a capacidade esgotada");

        // Ocupação final acumulada calculada do hotel deve ser 20
        int finalOccupancy = hotel.getAlojamentos().stream().mapToInt(info -> info.getEquipa().getJogadores().size()).sum();
        assertEquals(20, finalOccupancy, "A ocupação acumulada final deve ser exatamente 20");
    }

    @Test
    public void testConcorrenciaVendaBilhetesSetor() throws InterruptedException {
        // Arrange: Estadio com setor de capacidade 10
        Estadio est = new Estadio("Estádio do Dragão", "Porto");
        SetorEstadio setor = new SetorEstadio("Premium", 10, 100.0);
        est.adicionarSetor(setor);

        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa c = new Equipa("Cuba", "Castillo");
        campManager.registarEquipa(p);
        campManager.registarEquipa(c);

        Jogo jogo = new Jogo(1, "2026-06-25", "15:00", est, p, c, "Grupos", null, null);
        campManager.registarJogo(jogo);

        // 10 threads concorrem para comprar 4 bilhetes cada (total solicitado = 40)
        // Como a capacidade do setor é 10, no máximo 2 threads podem comprar (total 8).
        // A 3ª excederia (12 > 10).
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger comprasSucesso = new AtomicInteger(0);
        AtomicInteger comprasFalha = new AtomicInteger(0);

        // Act
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); // Disparo em simultâneo
                    boolean ok = bilManager.venderBilhete(jogo, "Premium", 4);
                    if (ok) {
                        comprasSucesso.incrementAndGet();
                    } else {
                        comprasFalha.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        latch.countDown();
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        // Assert: Exatamente 2 compras de 4 bilhetes devem passar (total 8). As outras 8 devem falhar.
        assertEquals(2, comprasSucesso.get(), "Exatamente 2 transações de 4 bilhetes devem passar");
        assertEquals(8, comprasFalha.get(), "8 transações devem falhar por falta de lugares no setor");

        // Capacidade restante do setor deve ser 2 (10 - 8 vendidos)
        SetorEstadio setorRecuperado = jogo.getEstadio().getSetorPorNome("Premium");
        assertNotNull(setorRecuperado);
        assertEquals(2, setorRecuperado.getCapacidadeTotal() - setorRecuperado.getBilhetesVendidos(), "Devem sobrar exatamente 2 lugares no setor");
    }
}
