package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.CampeonatoManager;

public class SimulacaoJogoTest {
    private CampeonatoManager manager;

    @BeforeEach
    public void setUp() {
        manager = CampeonatoManager.getInstance();
        manager.reset();
    }

    @Test
    public void testSimularJogoInexistenteRetornaFalso() {
        boolean ok = manager.simularJogoMinutoAMinuto(9999);
        assertFalse(ok, "Simular jogo com ID inexistente deve retornar falso");
    }

    @Test
    public void testSimulacaoJogoValida() {
        // Arrange: Criar equipas e registar
        Equipa p = new Equipa("Portugal", "Martínez");
        for (int i = 1; i <= 11; i++) {
            p.adicionarJogador(new Jogador(i, i, "Jogador P" + i, "Médio", EstadoJogador.APTO));
        }
        manager.registarEquipa(p);

        Equipa c = new Equipa("Cuba", "Castillo");
        for (int i = 1; i <= 11; i++) {
            c.adicionarJogador(new Jogador(i + 20, i, "Jogador C" + i, "Defesa", EstadoJogador.APTO));
        }
        manager.registarEquipa(c);

        Estadio est = new Estadio("Santiago Bernabéu", "Madrid");
        Jogo jogo = new Jogo(1, "2026-06-25", "15:00", est, p, c, "Grupos", null, null);
        manager.registarJogo(jogo);

        // Act: Simular o jogo minuto a minuto
        boolean ok = manager.simularJogoMinutoAMinuto(1);

        // Assert
        assertTrue(ok, "A simulação deve ser bem-sucedida");

        // Recuperar jogo atualizado
        Jogo jogoFinal = manager.procurarJogoPorId(1);
        assertNotNull(jogoFinal);
        assertEquals(StatusJogo.FINALIZADO, jogoFinal.getStatus(), "O jogo deve estar no estado FINALIZADO após a simulação");
        if (jogoFinal.getGoalsHome() > jogoFinal.getGoalsAway()) {
            assertEquals("Portugal", jogoFinal.getWinner().getNome());
        } else if (jogoFinal.getGoalsAway() > jogoFinal.getGoalsHome()) {
            assertEquals("Cuba", jogoFinal.getWinner().getNome());
        } else {
            assertNull(jogoFinal.getWinner(), "Empates na fase de grupos devem ter vencedor nulo");
        }

        // Verificar que eventos foram populados
        assertNotNull(jogoFinal.getEventos(), "A lista de eventos do jogo não deve ser nula");
        
        // Verificar que estatísticas do jogo foram geradas
        assertNotNull(jogoFinal.getEstatisticas(), "As estatísticas do jogo devem ser populadas");
        assertTrue(jogoFinal.getEstatisticas().getPosseBolaHome() > 0, "Posse de bola da casa deve ser maior que 0");
        assertTrue(jogoFinal.getEstatisticas().getPosseBolaAway() > 0, "Posse de bola de fora deve ser maior que 0");

        // O score final deve bater com o número de golos nos eventos
        int golosEventosHome = 0;
        int golosEventosAway = 0;
        for (EventoJogo ev : jogoFinal.getEventos()) {
            if (ev.getTipo() == TipoEvento.GOLO) {
                if (ev.getEquipa().getNome().equals("Portugal")) {
                    golosEventosHome++;
                } else {
                    golosEventosAway++;
                }
            }
        }
        assertEquals(golosEventosHome, jogoFinal.getGoalsHome(), "Golos de casa no placar devem coincidir com eventos de GOLO");
        assertEquals(golosEventosAway, jogoFinal.getGoalsAway(), "Golos de fora no placar devem coincidir com eventos de GOLO");
    }
}
