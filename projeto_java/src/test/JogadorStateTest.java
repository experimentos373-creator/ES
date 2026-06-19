package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.Jogador;
import domain.EstadoJogador;

public class JogadorStateTest {
    private Jogador jogador;

    @BeforeEach
    public void setUp() {
        jogador = new Jogador(1, 7, "Cristiano Ronaldo", "Avançado", EstadoJogador.APTO);
    }

    @Test
    public void testAlterarEstadoFisico() {
        // Assert estado inicial
        assertEquals(EstadoJogador.APTO, jogador.getEstado());

        // Alterar para Lesionado
        jogador.setEstado(EstadoJogador.LESIONADO);
        assertEquals(EstadoJogador.LESIONADO, jogador.getEstado());

        // Alterar para Suspenso
        jogador.setEstado(EstadoJogador.SUSPENSO);
        assertEquals(EstadoJogador.SUSPENSO, jogador.getEstado());
    }

    @Test
    public void testAlterarEnergia() {
        // Assert energia inicial
        assertEquals(100, jogador.getEnergy());

        // Definir nova energia
        jogador.setEnergy(75);
        assertEquals(75, jogador.getEnergy());

        jogador.setEnergy(0);
        assertEquals(0, jogador.getEnergy());
    }

    @Test
    public void testAdicionarHistoricoLesoes() {
        // Assert histórico inicial vazio
        assertTrue(jogador.getInjuryHistory().isEmpty());

        // Adicionar primeira lesão
        jogador.addInjury("Rotura de ligamentos no joelho esquerdo");
        assertEquals(1, jogador.getInjuryHistory().size());
        assertEquals("Rotura de ligamentos no joelho esquerdo", jogador.getInjuryHistory().get(0));

        // Adicionar segunda lesão
        jogador.addInjury("Entorse do tornozelo");
        assertEquals(2, jogador.getInjuryHistory().size());
        assertEquals("Entorse do tornozelo", jogador.getInjuryHistory().get(1));
    }

    @Test
    public void testToggleStarter() {
        // Assert inicial reserva
        assertFalse(jogador.isStarter());

        // Definir como titular
        jogador.setStarter(true);
        assertTrue(jogador.isStarter());

        // Voltar a reserva
        jogador.setStarter(false);
        assertFalse(jogador.isStarter());
    }
}
