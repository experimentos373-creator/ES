package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.CampeonatoManager;

public class SigiloArbitrosTest {
    private CampeonatoManager manager;

    @BeforeEach
    public void setUp() {
        manager = CampeonatoManager.getInstance();
        manager.reset();
    }

    @Test
    public void testOcultarArbitrosAntesDoInicio() {
        // Arrange
        Equipa p = new Equipa("Portugal", "Martínez");
        Equipa c = new Equipa("Cuba", "Castillo");
        Estadio est = new Estadio("Estádio da Luz", "Lisboa");

        Arbitro principal = new Arbitro(1, "ref1@fifa.com", "Artur Soares Dias", "Portugal", TipoArbitro.PRINCIPAL);
        Arbitro assist1 = new Arbitro(2, "ref2@fifa.com", "Pau Cebrián Devís", "Espanha", TipoArbitro.ASSISTENTE);
        Arbitro assist2 = new Arbitro(3, "ref3@fifa.com", "Nicolas Danos", "França", TipoArbitro.ASSISTENTE);
        Arbitro quarto = new Arbitro(4, "ref4@fifa.com", "Clément Turpin", "França", TipoArbitro.QUARTO);
        Arbitro var = new Arbitro(5, "ref5@fifa.com", "Wilton Sampaio", "Brasil", TipoArbitro.VAR);

        EscalaoArbitral escala = new EscalaoArbitral(principal, assist1, assist2, quarto, var);

        Jogo j = new Jogo(1, "2026-06-25", "15:00", est, p, c, "Grupos", null, null);
        j.associarEscalaArbitros(escala);

        manager.registarJogo(j);

        // Act & Assert 1: Como o jogo está AGENDADO, a escala pública deve ser oculta (null)
        Jogo recuperado = manager.procurarJogoPorId(1);
        assertNotNull(recuperado);
        assertEquals(StatusJogo.AGENDADO, recuperado.getStatus());
        assertNull(recuperado.getEscalaArbitrosPublica()); // Deve ser ocultada ao público!
        assertNotNull(recuperado.getEscalaArbitros()); // Mas ainda existe internamente para o sistema

        // Act 2: Alteramos o estado para EM_CURSO ou FINALIZADO
        recuperado.setStatus(StatusJogo.EM_CURSO);
        
        // Assert 2: Agora que o jogo está em curso/finalizado, a escala pública pode ser revelada
        assertNotNull(recuperado.getEscalaArbitrosPublica());
        assertEquals("Artur Soares Dias", recuperado.getEscalaArbitrosPublica().getPrincipal().getNome());
    }
}
