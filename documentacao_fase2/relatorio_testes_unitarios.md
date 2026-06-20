# Relatório de Testes Unitários Realizados
## Sistema de Gestão do Campeonato do Mundo de Futebol 2026

**Disciplina:** Engenharia de Software  
**Grupo de Trabalho / Alunos:**  
- Paulo Gomes (2024134892)
- Leonardo Mendes
- Arthur  
**Fase:** 2  

Este documento apresenta os testes unitários realizados pelo grupo para garantir o rigor das regras de negócio do campeonato, divididos estritamente em **3 testes por elemento do grupo**, sem qualquer dependência de interfaces gráficas.

---

## 📊 Distribuição de Testes por Elemento

| Elemento | Testes Atribuídos | Módulos Relacionados | Justificação / Objetivo |
| :--- | :--- | :--- | :--- |
| **Leonardo** | 1. [JogadorStateTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/JogadorStateTest.java)<br>2. [NeutralidadeArbitroTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/NeutralidadeArbitroTest.java)<br>3. [IntervaloArbitroTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/IntervaloArbitroTest.java) | Gestor de Equipa + Arbitragem | Gestão e transição de estados dos atletas, elegibilidade ética por nacionalidade e repouso regulamentar de árbitros. |
| **Arthur** | 1. [AlojamentoCapacidadeTest (Capacidade)](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/AlojamentoCapacidadeTest.java)<br>2. [AlojamentoCapacidadeTest (Exclusividade)](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/AlojamentoCapacidadeTest.java)<br>3. [AntiBotBilheteiraTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/AntiBotBilheteiraTest.java) | Gestor de Logística + Bilheteira | Lotação máxima de quartos por hotel, regra de exclusividade (1 equipa por hotel) e limites anti-bot na compra de bilhetes. |
| **Paulo** | 1. [GrupoClassificacaoTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/GrupoClassificacaoTest.java)<br>2. [CalendarioJogoTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/CalendarioJogoTest.java)<br>3. [AvancoBracketTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/AvancoBracketTest.java) | Admin + Calendário + Bracket | Cálculo de pontos de grupos com critérios de desempate, agendamento sem conflitos e avanço automático no bracket. |

---

## 🧪 Detalhe Técnico dos Casos de Teste (AAA - Arrange, Act, Assert)

### 👤 1. Leonardo Mendes (Gestor de Equipa + Arbitragem)

#### 1.1 [JogadorStateTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/JogadorStateTest.java)
* **Objetivo:** Garantir a correta transição de estados disciplinares/físicos dos jogadores do plantel.
* **Cenário de Teste:**
  - **Arrange:** Criação de um `Jogador` com estado inicial `APTO` e energia 100%.
  - **Act:** Modificação do estado para `LESIONADO`, decréscimo de energia e registo de uma nova ocorrência médica.
  - **Assert:** Verifica se o estado mudou para `LESIONADO`, se a energia reduziu e se a ocorrência foi registada no histórico clínico do jogador.

#### 1.2 [NeutralidadeArbitroTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/NeutralidadeArbitroTest.java)
* **Objetivo:** Validar a regra de neutralidade nacional da FIFA para árbitros escalados.
* **Cenário de Teste:**
  - **Arrange:** Jogo entre Portugal e Brasil. Criação de um `Arbitro` principal de nacionalidade portuguesa.
  - **Act:** Tentar escalar o `Arbitro` português para o jogo.
  - **Assert:** Confirma que a escala foi rejeitada (retorna `false`), impedindo potenciais conflitos de interesse éticos.

#### 1.3 [IntervaloArbitroTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/IntervaloArbitroTest.java)
* **Objetivo:** Assegurar o tempo regulamentar mínimo de descanso de 48 horas para os árbitros entre partidas.
* **Cenário de Teste:**
  - **Arrange:** Jogo A no dia 25-06 e Jogo B no dia 26-06. Escalar o árbitro no Jogo A.
  - **Act:** Tentar escalar o mesmo árbitro para o Jogo B.
  - **Assert:** O sistema rejeita o agendamento (retorna `false`), salvaguardando a integridade física do árbitro.

---

### 👤 2. Arthur (Gestor de Logística + Bilheteira)

#### 2.1 [AlojamentoCapacidadeTest (Capacidade)](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/AlojamentoCapacidadeTest.java)
* **Objetivo:** Evitar a alocação de uma comitiva em hotéis que não comportem a sua dimensão.
* **Cenário de Teste:**
  - **Arrange:** Equipa com 26 convocados registados e um `Hotel` com capacidade máxima de 25 quartos.
  - **Act:** Realizar a tentativa de alocação (check-in) da equipa no hotel.
  - **Assert:** Confirma que a atribuição foi negada por falta de espaço suficiente para o plantel.

#### 2.2 [AlojamentoCapacidadeTest (Exclusividade)](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/AlojamentoCapacidadeTest.java)
* **Objetivo:** Validar o princípio ético e logístico de exclusividade (apenas uma seleção por hotel).
* **Cenário de Teste:**
  - **Arrange:** Duas seleções nacionais distintas (Equipa A e Equipa B) e um `Hotel`.
  - **Act:** Alocar a Equipa A com sucesso e tentar alocar a Equipa B no mesmo estabelecimento.
  - **Assert:** O sistema impede a segunda alocação, garantindo a privacidade das seleções.

#### 2.3 [AntiBotBilheteiraTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/AntiBotBilheteiraTest.java)
* **Objetivo:** Impedir a especulação de bilhetes limitando a compra online entre 1 e 4 ingressos.
* **Cenário de Teste:**
  - **Arrange:** Um `Jogo` agendado e um setor de estádio com lugares vagos.
  - **Act:** Simular tentativas de compra com quantidades inválidas (0, -1, 5) e quantidades válidas (1, 4).
  - **Assert:** Verifica que as compras inválidas são bloqueadas e as válidas são processadas com sucesso.

---

### 👤 3. Paulo Gomes (Admin + Calendário + Bracket)

#### 3.1 [GrupoClassificacaoTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/GrupoClassificacaoTest.java)
* **Objetivo:** Validar a tabela de grupos e a correta aplicação dos critérios de desempate da FIFA.
* **Cenário de Teste:**
  - **Arrange:** Registo de equipas e resultados de jogos no mesmo grupo, com igualdade de pontos.
  - **Act:** Solicitar a ordenação classificativa do grupo.
  - **Assert:** Verifica que a tabela aplica corretamente os critérios de desempate (Pontos -> Saldo de Golos -> Golos Marcados).

#### 3.2 [CalendarioJogoTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/CalendarioJogoTest.java)
* **Objetivo:** Impedir o agendamento de jogos em datas de fases inválidas ou com conflito de calendário para as equipas.
* **Cenário de Teste:**
  - **Arrange:** Criação de dois jogos em conflito de calendário para a mesma equipa ou fora do intervalo de datas.
  - **Act:** Tentar registar os jogos no sistema.
  - **Assert:** Garante que o agendamento é recusado em caso de sobreposição ou formato inválido.

#### 3.3 [AvancoBracketTest](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/projeto_java/src/test/AvancoBracketTest.java)
* **Objetivo:** Garantir a progressão automática do bracket de eliminatórias (Oitavos, Quartos, etc.) na base de dados.
* **Cenário de Teste:**
  - **Arrange:** Criação de um jogo de oitavos-de-final ligado a um jogo posterior (quartos-de-final) no bracket.
  - **Act:** Finalizar o jogo introduzindo golos e definindo uma equipa vencedora.
  - **Assert:** Verifica se a equipa vencedora foi automaticamente transportada para o slot correto no jogo dos quartos-de-final.

---

## 💻 Resultados de Execução do Test Runner (Maven)

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

---

## 🎯 Entregáveis da Fase 2 — Estado Final

| # | Entregável | Caminho do Ficheiro | Estado |
|---|---|---|---|
| 1 | **Diagrama de Classes** | [8_diagrama_classes.md](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/documentacao_fase2/8_diagrama_classes.md) / imagens em [imagens/](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/documentacao_fase2/imagens/) | ✅ Concluído e Renderizado |
| 2 | **Texto dos Casos de Uso** | [texto_casos_uso_iconix.md](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/texto_casos_uso_iconix.md) | ✅ Corrigido (Estilo ICONIX exato) |
| 3 | **Relatório de Testes Unitários** | [relatorio_testes_unitarios.md](file:///e:/Projeto_ES_Paulo_Gomes_2024134892/documentacao_fase2/relatorio_testes_unitarios.md) | ✅ Concluído |
