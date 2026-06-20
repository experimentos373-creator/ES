# Relatório de Testes Unitários Realizados
## Sistema de Gestão do Campeonato do Mundo de Futebol 2026

**Disciplina:** Engenharia de Software  
**Fase:** 2  

Este documento apresenta a especificação, cobertura e os resultados de execução dos **Testes Unitários** implementados para garantir a robustez das regras de negócio do sistema. Todos os testes seguem o padrão **AAA (Arrange, Act, Assert)** e utilizam a framework **JUnit 5**.

---

## 📊 Resumo de Execução
* **Total de Testes Executados:** 22
* **Sucessos:** 22
* **Falhas:** 0
* **Erros:** 0
* **Tempo de Execução:** ~8.6 segundos
* **Status do Build:** **BUILD SUCCESS** (JUnit / Maven)

---

## 🧪 1. Detalhe dos Casos de Teste por Módulo

### 🔹 Módulo 2 — Gestor de Arbitragem (Leonardo Mendes)

#### A. [NeutralidadeArbitroTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/NeutralidadeArbitroTest.java)
Garante as regras éticas e regulamentares da FIFA na escala de equipas de arbitragem.
* **`testEscalarArbitroNacionalidadeDiferenteSucesso`**
  - **Descrição:** Verifica que um `Arbitro` polaco pode ser escalado com sucesso para um `Jogo` entre Portugal e Brasil (nacionalidade neutra).
* **`testEscalarArbitroMesmaNacionalidadeFalha`**
  - **Descrição:** Garante que um `Arbitro` português é rejeitado ao tentar ser escalado para um `Jogo` onde joga Portugal.
* **`testEscalarArbitroSemNenhumElegivelNoPoolFalha`**
  - **Descrição:** Verifica que o sistema lança `IllegalStateException` se o único `Arbitro` registado pertencer à mesma nacionalidade de uma das `Equipa` no `Jogo`.
* **`testEscalarArbitroConflitoTotalDeRestricoesFalha`**
  - **Descrição:** Testa um cenário extremo onde um `Arbitro` português, além de pertencer ao mesmo país de uma `Equipa`, apitou um `Jogo` há menos de 48 horas, resultando na falha da escala.

#### B. [IntervaloArbitroTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/IntervaloArbitroTest.java)
Garante o tempo regulamentar de descanso dos árbitros.
* **`testArbitroMenosDe48HorasFalha`**
  - **Descrição:** Verifica que um `Arbitro` que apitou uma partida no dia 25-06 não pode ser escalado para outra no dia 26-06 (tempo de descanso inferior a 48 horas).

#### C. [ScoreFIFATest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/ScoreFIFATest.java)
Garante a integridade do cálculo do Score FIFA dos árbitros.
* **`testRecalculoScoreFIFA`**
  - **Descrição:** Valida que, após a atribuição de uma `Pontuacao` (1 a 5 estrelas), o score FIFA do `Arbitro` é atualizado corretamente baseado na média ponderada das avaliações do histórico.

#### D. [JogadorStateTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/JogadorStateTest.java)
Valida a alteração e controlo do estado de saúde e disciplina dos atletas.
* **`testEstadoJogadorEAvaliacaoFisica`**
  - **Descrição:** Garante que a transição de estados do `Jogador` (`APTO`, `LESIONADO`, `SUSPENSO`), percentagem de energia e histórico de `Ocorrencia` (lesões) funcionam corretamente.

---

### 🔹 Módulo 1 & 3 — Administrador, Classificações e Bracket (Paulo Gomes)

#### E. [GrupoClassificacaoTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/GrupoClassificacaoTest.java)
Valida a ordenação das classificações nos grupos da fase inicial.
* **`testCalculoPontosSucesso`**
  - **Descrição:** Garante que os pontos de vitória (3), empate (1) e derrota (0) são atribuídos e ordenados corretamente.
* **`testCriteriosDesempate`**
  - **Descrição:** Garante o cumprimento dos critérios oficiais de desempate da FIFA (Pontos -> Saldo de Golos -> Golos Marcados).

#### F. [CalendarioJogoTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/CalendarioJogoTest.java)
Garante a validação de datas e horas no calendário geral do torneio.
* **`testAgendarJogoFaseCorreta`**
  - **Descrição:** Valida que o agendamento do `Jogo` é recusado se violar as datas limite das fases ou se introduzir datas inválidas.

#### G. [AvancoBracketTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/AvancoBracketTest.java)
Garante a progressão automática do torneio em formato de eliminatórias.
* **`testAvancoAutomaticoEliminatorias`**
  - **Descrição:** Garante que quando um `Jogo` das eliminatórias é finalizado, a `Equipa` vencedora avança de forma totalmente automática para o `Jogo` seguinte da árvore do campeonato (Bracket).

#### H. [SigiloArbitrosTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/SigiloArbitrosTest.java)
Valida a proteção ética de sigilo da equipa de arbitragem.
* **`testOcultarArbitrosAntesDoInicio`**
  - **Descrição:** Verifica se o escalão arbitral é ocultado do público (retorna `null` através do método público de visualização) enquanto o estado do `Jogo` for `AGENDADO`.

---

### 🔹 Módulo 4 & 5 — Bilheteira e Logística (Arthur)

#### I. [AlojamentoCapacidadeTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/AlojamentoCapacidadeTest.java)
Valida as regras de atribuição de hotéis e alojamentos às comitivas.
* **`testAlocacaoHotelCapacidadeExcedida`**
  - **Descrição:** Garante que uma `Equipa` com 26 convocados não pode ser alojada num `Hotel` com capacidade máxima de 25 quartos.
* **`testAlocacaoHotelEquipasDiferentesFalha`**
  - **Descrição:** Valida a exclusividade do alojamento, garantindo que duas `Equipa` diferentes não podem habitar o mesmo `Hotel` em simultâneo.

#### J. [LotacaoEstadioTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/LotacaoEstadioTest.java)
Evita a sobrelotação dos estádios nas vendas presenciais/online.
* **`testVendaExcedendoLotaçãoSetor`**
  - **Descrição:** Verifica que a compra de um `Bilhete` é negada se a quantidade solicitada exceder o número de lugares vagos num `Setor` específico do `Estadio`.

#### K. [AntiBotBilheteiraTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/AntiBotBilheteiraTest.java)
Implementa o controlo de segurança no fluxo de bilheteira (Ética de Integridade e Equidade de Acesso).
* **`testLimiteMaximoPorCompra`**
  - **Descrição:** Valida que a quantidade por transação é bloqueada caso seja inferior a 1 ou superior a 4 `Bilhete` (regra anti-bot).

---

## 💻 2. Resultado da Consola (Maven Test Runner)

```text
[INFO] Scanning for projects...
[INFO] --------------------< com.wc2026:wc2026-management >--------------------
[INFO] Building wc2026-management 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] --- resources:3.3.1:resources (default-resources) @ wc2026-management ---
[INFO] Copying 2 resources from src to target\classes
[INFO] --- compiler:3.11.0:testCompile (default-testCompile) @ wc2026-management ---
[INFO] Changes detected - recompiling the module! :source
[INFO] Compiling 51 source files with javac [debug target 21] to target\test-classes
[INFO] --- surefire:3.1.2:test (default-test) @ wc2026-management ---
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running test.AntiBotBilheteiraTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.476 s -- in test.AntiBotBilheteiraTest
[INFO] Running test.SigiloArbitrosTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.084 s -- in test.SigiloArbitrosTest
[INFO] Running test.AvancoBracketTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.430 s -- in test.AvancoBracketTest
[INFO] Running test.LotacaoEstadioTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.273 s -- in test.LotacaoEstadioTest
[INFO] Running test.GrupoClassificacaoTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.834 s -- in test.GrupoClassificacaoTest
[INFO] Running test.IntervaloArbitroTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.456 s -- in test.IntervaloArbitroTest
[INFO] Running test.AlojamentoCapacidadeTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.275 s -- in test.AlojamentoCapacidadeTest
[INFO] Running test.CalendarioJogoTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.376 s -- in test.CalendarioJogoTest
[INFO] Running test.ScoreFIFATest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.352 s -- in test.ScoreFIFATest
[INFO] Running test.NeutralidadeArbitroTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.073 s -- in test.NeutralidadeArbitroTest
[INFO] Running test.JogadorStateTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s -- in test.JogadorStateTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```
