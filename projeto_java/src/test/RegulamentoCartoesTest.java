package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import domain.*;
import manager.CampeonatoManager;

public class RegulamentoCartoesTest {
    private CampeonatoManager manager;

    @BeforeEach
    public void setUp() {
        manager = CampeonatoManager.getInstance();
        manager.reset();
    }

    @Test
    public void testAcumulacaoCartoesESuspensao() {
        // Arrange
        Equipa p = new Equipa("Portugal", "Martínez");
        Jogador jog = new Jogador(1, 7, "Cristiano", "Avancado", EstadoJogador.APTO);
        p.adicionarJogador(jog);
        manager.registarEquipa(p);

        Equipa c = new Equipa("Cuba", "Castillo");
        Jogador jogC = new Jogador(2, 10, "Diaz", "Defesa", EstadoJogador.APTO);
        c.adicionarJogador(jogC);
        manager.registarEquipa(c);

        Estadio est = new Estadio("Santiago Bernabéu", "Madrid");
        Jogo jogo1 = new Jogo(1, "2026-06-25", "15:00", est, p, c, "Grupos", null, null);
        manager.registarJogo(jogo1);

        // Act 1: Adicionar 1 amarelo no jogo 1
        jogo1.adicionarEvento(new EventoJogo(15, TipoEvento.CARTAO_AMARELO, jog, p));
        manager.finalizarJogoECorrerBracket(1, p, 1, 0, -1, -1, null);

        // Assert 1: Deve ter 1 amarelo e estar APTO
        Jogador realCristiano = manager.procurarEquipaPorNome("Portugal").getJogadores().get(0);
        assertEquals(1, realCristiano.getYellowCards());
        assertEquals(EstadoJogador.APTO, realCristiano.getEstado());

        // Act 2: Novo jogo
        Jogo jogo2 = new Jogo(2, "2026-06-28", "18:00", est, p, c, "Grupos", null, null);
        manager.registarJogo(jogo2);
        
        // Adicionar 2º amarelo no jogo 2
        jogo2.adicionarEvento(new EventoJogo(40, TipoEvento.CARTAO_AMARELO, realCristiano, p));
        manager.finalizarJogoECorrerBracket(2, p, 1, 0, -1, -1, null);

        // Assert 2: Cristiano deve ter 2 amarelos acumulados e estar SUSPENSO para o próximo jogo
        Jogador realCristianoF = manager.procurarEquipaPorNome("Portugal").getJogadores().get(0);
        assertEquals(2, realCristianoF.getYellowCards());
        assertEquals(EstadoJogador.SUSPENSO, realCristianoF.getEstado());

        // Act 3: Novo jogo (Cristiano cumpre a suspensão)
        Jogo jogo3 = new Jogo(3, "2026-07-02", "18:00", est, p, c, "Oitavos", null, null);
        manager.registarJogo(jogo3);
        manager.finalizarJogoECorrerBracket(3, p, 1, 0, -1, -1, null);

        // Assert 3: Após o jogo 3, Cristiano deve estar APTO novamente e com amarelos a zero
        Jogador realCristianoApto = manager.procurarEquipaPorNome("Portugal").getJogadores().get(0);
        assertEquals(EstadoJogador.APTO, realCristianoApto.getEstado());
        assertEquals(0, realCristianoApto.getYellowCards());
    }

    @Test
    public void testLimpezaCartoesNasMeiasFinais() {
        // Arrange
        Equipa p = new Equipa("Portugal", "Martínez");
        Jogador jogApto = new Jogador(1, 7, "Cristiano", "Avancado", EstadoJogador.APTO);
        Jogador jogSuspenso = new Jogador(2, 10, "Bernardo", "Médio", EstadoJogador.APTO);
        p.adicionarJogador(jogApto);
        p.adicionarJogador(jogSuspenso);
        manager.registarEquipa(p);

        Equipa c = new Equipa("Cuba", "Castillo");
        manager.registarEquipa(c);

        Estadio est = new Estadio("Santiago Bernabéu", "Madrid");
        Jogo jogoQuartos = new Jogo(1, "2026-07-05", "18:00", est, p, c, "Quartos", null, null);
        manager.registarJogo(jogoQuartos);

        // jogApto tem 1 amarelo vindo das fases anteriores
        jogApto.setYellowCards(1);
        // Vamos simular Bernardo (jogSuspenso) a receber o segundo amarelo e a ficar suspenso
        jogSuspenso.setYellowCards(1);
        jogoQuartos.adicionarEvento(new EventoJogo(30, TipoEvento.CARTAO_AMARELO, jogSuspenso, p));

        // Act: Finalizar o jogo de Quartos
        manager.finalizarJogoECorrerBracket(1, p, 2, 1, -1, -1, null);

        // Assert:
        // Cristiano (jogApto) que tinha 1 amarelo antes e não acumulou suspensão deve ter cartões limpos (yellowCards = 0)
        Jogador realCristiano = manager.procurarEquipaPorNome("Portugal").getJogadores().stream().filter(j -> j.getId() == 1).findFirst().orElse(null);
        assertNotNull(realCristiano);
        assertEquals(0, realCristiano.getYellowCards(), "O cartão amarelo acumulado deve ser limpo para as Meias-Finais");
        assertEquals(EstadoJogador.APTO, realCristiano.getEstado());

        // Bernardo (jogSuspenso) que acumulou 2 amarelos no jogo de Quartos deve continuar SUSPENSO para a Meia-Final
        Jogador realBernardo = manager.procurarEquipaPorNome("Portugal").getJogadores().stream().filter(j -> j.getId() == 2).findFirst().orElse(null);
        assertNotNull(realBernardo);
        assertEquals(EstadoJogador.SUSPENSO, realBernardo.getEstado(), "Jogador suspenso nos Quartos deve cumprir castigo na Meia-Final");
    }

    @Test
    public void testOrdemProcessamentoEventosELimpezaNosQuartos() {
        // Arrange
        Equipa p = new Equipa("Portugal", "Martínez");
        Jogador jog1AmareloNoJogo = new Jogador(3, 8, "Bruno", "Médio", EstadoJogador.APTO);
        Jogador jogAcumulouNoJogo = new Jogador(4, 11, "João", "Médio", EstadoJogador.APTO);
        p.adicionarJogador(jog1AmareloNoJogo);
        p.adicionarJogador(jogAcumulouNoJogo);
        manager.registarEquipa(p);

        Equipa c = new Equipa("Cuba", "Castillo");
        manager.registarEquipa(c);

        // Bruno começa com 0 amarelos. João começa com 1 amarelo.
        jogAcumulouNoJogo.setYellowCards(1);

        Estadio est = new Estadio("Santiago Bernabéu", "Madrid");
        Jogo jogoQuartos = new Jogo(1, "2026-07-05", "18:00", est, p, c, "Quartos", null, null);
        manager.registarJogo(jogoQuartos);

        // Durante o jogo de Quartos, ambos recebem amarelo
        jogoQuartos.adicionarEvento(new EventoJogo(15, TipoEvento.CARTAO_AMARELO, jog1AmareloNoJogo, p));
        jogoQuartos.adicionarEvento(new EventoJogo(35, TipoEvento.CARTAO_AMARELO, jogAcumulouNoJogo, p));

        // Act: Finalizar jogo dos Quartos
        manager.finalizarJogoECorrerBracket(1, p, 1, 0, -1, -1, null);

        // Assert
        Jogador realBruno = manager.procurarEquipaPorNome("Portugal").getJogadores().stream().filter(j -> j.getId() == 3).findFirst().orElse(null);
        assertNotNull(realBruno);
        assertEquals(0, realBruno.getYellowCards(), "Amarelo de Bruno deve ser limpo");
        assertEquals(EstadoJogador.APTO, realBruno.getEstado());

        Jogador realJoao = manager.procurarEquipaPorNome("Portugal").getJogadores().stream().filter(j -> j.getId() == 4).findFirst().orElse(null);
        assertNotNull(realJoao);
        assertEquals(EstadoJogador.SUSPENSO, realJoao.getEstado(), "João deve ficar suspenso");
        assertEquals(2, realJoao.getYellowCards(), "João deve manter os 2 cartões para a suspensão");
    }
}
