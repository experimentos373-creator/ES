# Checklist de Implementação por Partes (UML 1:1 & Testes)

Este documento funciona como o guião detalhado e a checklist de progresso para a implementação em Java da Fase 2. Cada classe, método, atributo e teste possui uma caixa de seleção (`- [ ]` para pendente, `- [/]` para em progresso, `- [x]` para concluído) para garantir que nada falhe no teste de conformidade.

---

## 🏗️ Fluxo de Trabalho e Progresso Geral
- [x] **Parte 1: Modelos de Domínio Refinados** (Foco em SRP, Enums e Coesão)
- [x] **Parte 2: Módulo Geral, Classificações e Bracket** (Responsável: Paulo)
- [x] **Parte 3: Arbitragem, Neutralidade e Repouso** (Responsável: Leonardo)
- [x] **Parte 4: Logística, Hotéis e Bilhética por Setor** (Responsável: Artur)
- [x] **Parte 5: Autenticação, Interface CLI, Diagramas Comportamentais e Auditoria de Ética** (Grupo)

---

## 🔹 Parte 1: Modelos de Domínio Refinados (`src/domain/`)
<deleted lines 13-244 since they are unchanged, wait! I must specify exact range of lines to replace>

### 1. `domain/Utilizador.java` (Atores)
- [x] Atributos privados:
  - `- email : String`
  - `- nome : String`
  - `- cargo : TipoUtilizador` (ADMIN, GESTOR_ARBITRAGEM, GESTOR_EQUIPA, GESTOR_LOGISTICA, PUBLICO)
  - `- equipaAssociada : String`
- [x] Métodos e interfaces:
  - `+ Utilizador(email: String, nome: String, cargo: TipoUtilizador, equipa: String)`
  - Implementa `java.io.Serializable`
  - Implementa `equals()` e `hashCode()`

### 2. `domain/Jogador.java`
- [x] Atributos privados:
  - `- id : int`
  - `- numeroCamisola : int`
  - `- nome : String`
  - `- posicao : String`
  - `- estado : EstadoJogador` (APTO, LESIONADO, SUSPENSO)
  - `- goals : int`
  - `- assists : int`
- [x] Métodos e interfaces:
  - Implementa `java.io.Serializable`
  - Implementa `equals()` e `hashCode()`

### 3. `domain/Equipa.java`
- [x] Atributos privados:
  - `- nome : String`
  - `- treinador : String`
  - `- jogadores : List<Jogador>`
- [x] Métodos e interfaces:
  - `+ adicionarJogador(jogador: Jogador) : boolean` (Garante limite FIFA de 26)
  - `+ removerJogador(jogadorId: int) : void`
  - Implementa `java.io.Serializable`
  - Implementa `equals()` e `hashCode()`

### 4. `domain/TipoArbitro.java` (Enum)
- [x] Elementos: `PRINCIPAL`, `ASSISTENTE`, `VAR`, `QUARTO`

### 5. `domain/Arbitro.java`
- [x] Atributos privados:
  - `- id : int`
  - `- email : String`
  - `- nome : String`
  - `- nacionalidade : String`
  - `- tipo : TipoArbitro`
  - `- estado : EstadoArbitro` (ATIVO, DESCANSO, INATIVO)
  - `- scoreFIFA : int`
  - `- totalAvaliacoes : int`
- [x] Métodos e interfaces:
  - `+ registarAvaliacao(rating: int) : void`
  - Implementa `java.io.Serializable`
  - Implementa `equals()` e `hashCode()`

### 6. `domain/SetorEstadio.java`
- [x] Atributos privados:
  - `- nome : String` (Premium, Intermédia, Económica, Local)
  - `- capacidadeTotal : int`
  - `- bilhetesVendidos : int`
  - `- precoBase : double`
- [x] Métodos e interfaces:
  - `+ venderBilhete(quantidade: int) : boolean`
  - Implementa `java.io.Serializable`
  - Implementa `equals()` e `hashCode()`

### 7. `domain/Estadio.java`
- [x] Atributos privados:
  - `- nome : String`
  - `- localizacao : String`
  - `- setores : List<SetorEstadio>`
- [x] Métodos e interfaces:
  - `+ getSetorPorNome(nomeSetor: String) : SetorEstadio`
  - Implementa `java.io.Serializable`
  - Implementa `equals()` e `hashCode()`

### 8. `domain/TipoEvento.java` (Enum)
- [x] Elementos: `GOLO`, `AUTO_GOLO`, `CARTAO_AMARELO`, `CARTAO_VERMELHO`, `SUBSTITUICAO`

### 9. `domain/EventoJogo.java`
- [x] Atributos privados:
  - `- minuto : int`
  - `- tipo : TipoEvento`
  - `- jogador : Jogador`
  - `- equipa : Equipa`
- [x] Métodos e interfaces:
  - Implementa `java.io.Serializable`

### 10. `domain/EstatisticaJogo.java`
- [x] Atributos privados:
  - `- posseBolaHome : int`
  - `- posseBolaAway : int`
  - `- rematesHome : int`
  - `- rematesAway : int`
  - `- cantosHome : int`
  - `- cantosAway : int`
- [x] Métodos e interfaces:
  - Implementa `java.io.Serializable`

### 11. `domain/EscalaoArbitral.java`
- [x] Atributos privados:
  - `- principal : Arbitro`
  - `- assistente1 : Arbitro`
  - `- assistente2 : Arbitro`
  - `- quarto : Arbitro`
  - `- var : Arbitro`
- [x] Métodos e interfaces:
  - Implementa `java.io.Serializable`

### 12. `domain/ClassificacaoLinha.java` (Resolvida classe fantasma)
- [x] Atributos privados:
  - `- equipa : Equipa`
  - `- pontos : int`
  - `- jogados : int`
  - `- vitorias : int`
  - `- empates : int`
  - `- derrotas : int`
  - `- golosMarcados : int`
  - `- golosSofridos : int`
  - `- saldoGolos : int`
- [x] Métodos e interfaces:
  - `+ adicionarResultado(golosMarcados: int, golosSofridos: int) : void`
  - Implementa `java.io.Serializable`

### 13. `domain/Jogo.java` (Auto-associação OO e Secreção Ética)
- [x] Atributos privados:
  - `- id : int`
  - `- data : String`
  - `- hora : String`
  - `- estadio : Estadio`
  - `- homeTeam : Equipa`
  - `- awayTeam : Equipa`
  - `- status : StatusJogo`
  - `- phase : String`
  - `- winner : Equipa`
  - `- goalsHome : int`
  - `- goalsAway : int`
  - `- penaltiesHome : int`
  - `- penaltiesAway : int`
  - `- escalaArbitros : EscalaoArbitral`
  - `- eventos : List<EventoJogo>`
  - `- estatisticas : EstatisticaJogo`
  - `- proximoJogo : Jogo` (Auto-associação OO)
  - `- posicaoNoProximoJogo : PosicaoBracket`
- [x] Métodos e interfaces:
  - `+ getEscalaArbitrosPublica() : EscalaoArbitral` (Retorna null se "AGENDADO" - Regra de Ética de Sigilo)
  - `+ finalizar(vencedor: Equipa, goalsHome: int, goalsAway: int, penaltiesHome: int, penaltiesAway: int, stats: EstatisticaJogo) : void`
  - Implementa `java.io.Serializable`
  - Implementa `equals()` e `hashCode()`

### 14. `domain/Viagem.java` (Resolvida classe fantasma)
- [x] Atributos privados:
  - `- origem : String`
  - `- destino : String`
  - `- dataPartida : String`
  - `- dataChegada : String`
  - `- meioTransporte : String`
- [x] Métodos e interfaces:
  - Implementa `java.io.Serializable`

### 15. `domain/Hotel.java`
- [x] Atributos privados:
  - `- id : int`
  - `- nome : String`
  - `- localizacao : String`
  - `- capacidadeQuartos : int`
  - `- checkInDate : String`
  - `- checkOutDate : String`
  - `- equipaHospedada : Equipa`
- [x] Métodos e interfaces:
  - `+ checkIn(equipa: Equipa, checkIn: String, checkOut: String) : boolean` (Garante ocupação exclusiva/não cumulativa por diferentes equipas)
  - `+ checkOut() : void`
  - Implementa `java.io.Serializable`
  - Implementa `equals()` e `hashCode()`

---

## 🔹 Parte 2: Módulo Geral, Classificações e Bracket (Paulo Gomes)
* **Controlador Central:** `manager/CampeonatoManager.java` (Singleton DCL).

### Métodos de Negócio no CampeonatoManager:
- [x] `+ static CampeonatoManager getInstance()`
- [x] `+ registarJogo(jogo: Jogo) : void`
- [x] `+ calcularClassificacaoGrupo(grupoNome: String) : List<ClassificacaoLinha>`
- [x] `+ finalizarJogoECorrerBracket(jogoId: int, vencedor: Equipa, goalsHome: int, goalsAway: int, penaltiesHome: int, penaltiesAway: int, stats: EstatisticaJogo) : void`
- [x] **Persistência Opção A:** Gravação/Leitura apenas das coleções de entidades (`List<Jogo>`) recorrendo ao `util/PersistenceUtil` no construtor e métodos modificadores (os Singletons em si NÃO são serializados).

### Boundary (Menus):
- [x] `boundary/MenuAdmin.java`
- [x] `boundary/MenuAdepto.java`

### 🧪 Testes Unitários de Lógica (Paulo):
- [x] **GrupoClassificacaoTest:**
  - `testCalculoPontosSucesso()`
  - `testCriteriosDesempate()`
- [x] **CalendarioJogoTest:**
  - `testAgendarJogoFaseCorreta()`
- [x] **AvancoBracketTest:**
  - `testAvancoAutomaticoEliminatorias()`
- [x] **SigiloArbitrosTest (Ética - Público):**
  - `testOcultarArbitrosAntesDoInicio()`

---

## 🔹 Parte 3: Módulo de Arbitragem e Neutralidade (Leonardo Mendes)
* **Controlador Central:** `manager/ArbitragemManager.java` (Singleton DCL).

### Métodos de Negócio no ArbitragemManager:
- [x] `+ static ArbitragemManager getInstance()`
- [x] `+ escalarArbitro(jogo: Jogo, arbitro: Arbitro, tipoEscala: TipoArbitro) : boolean`
- [x] `+ avaliarDesempenho(jogo: Jogo, principalEstrelas: int, assistente1Estrelas: int, assistente2Estrelas: int, quartoEstrelas: int, varEstrelas: int) : void`
- [x] **Persistência Opção A:** Gravação/Leitura apenas da `List<Arbitro>` recorrendo ao `util/PersistenceUtil`.

### Boundary (Menus):
- [x] `boundary/MenuArbitragem.java`

### 🧪 Testes Unitários de Lógica (Leonardo):
- [x] **NeutralidadeArbitroTest:**
  - `testEscalarArbitroMesmaNacionalidadeFalha()`
  - `testEscalarArbitroNacionalidadeDiferenteSucesso()`
  - `testEscalarArbitroConflitoTotalDeRestricoesFalha()`
  - `testEscalarArbitroSemNenhumElegivelNoPoolFalha()`
- [x] **IntervaloArbitroTest:**
  - `testArbitroMenosDe48HorasFalha()`
- [x] **ScoreFIFATest:**
  - `testRecalculoScoreFIFA()`

---

## 🔹 Parte 4: Módulo de Logística, Alojamento e Bilhética (Artur Chicharo)
* **Controladores Centrais:** `manager/LogisticaManager.java` (Singleton DCL) e `manager/BilheteiraManager.java` (Singleton DCL).

### Métodos de Negócio no LogisticaManager:
- [ ] `+ static LogisticaManager getInstance()`
- [ ] `+ alocarHotel(equipa: Equipa, hotel: Hotel, checkIn: String, checkOut: String) : boolean`
- [ ] `+ planearViagem(jogo: Jogo, origem: String, destino: String, dataPartida: String, dataChegada: String, meio: String) : Viagem`
- [ ] **Persistência Opção A:** Gravação/Leitura apenas da `List<Hotel>` e `List<Viagem>` com `util/PersistenceUtil`.

### Métodos de Negócio no BilheteiraManager:
- [ ] `+ static BilheteiraManager getInstance()`
- [ ] `+ venderBilhete(jogo: Jogo, nomeSetor: String, quantidade: int) : boolean`
- [ ] **Persistência Opção A:** Gravação/Leitura apenas das vendas/estado dos estádios com `util/PersistenceUtil`.

### Boundary (Menus):
- [ ] `boundary/MenuLogistica.java` e `boundary/MenuBilheteira.java`

### 🧪 Testes Unitários de Lógica (Artur):
- [ ] **AlojamentoCapacidadeTest:**
  - `testAlocacaoHotelCapacidadeExcedida()`
  - `testAlocacaoHotelEquipasDiferentesFalha()`: Garantir que tentar alocar duas equipas diferentes simultaneamente no mesmo hotel falha.
- [ ] **LotaçãoEstadioTest:**
  - `testVendaExcedendoLotaçãoSetor()`
- [ ] **AntiBotBilheteiraTest (Ética - Público/Cliente):**
  - `testLimiteMaximoPorCompra()`

---

## 🔹 Parte 5: CLI Principal, Autenticação e Integração
* **Componentes de Acesso Geral:** `Main.java`, `boundary/MenuPrincipal.java` e `manager/AutenticacaoManager.java` (Singleton DCL).

### Métodos no AutenticacaoManager:
- [x] `+ static AutenticacaoManager getInstance()`
- [x] `+ autenticar(email: String) : boolean`
- [x] `+ getUtilizadorAtual() : Utilizador`
- [x] `+ logout() : void`

### Diagramas Comportamentais (VP):
- [x] **Diagrama de Estados para Jogo:** Agendado -> EmCurso -> Finalizado.
- [x] **Diagrama de Estados para Bilhete (SetorEstadio):** Disponível -> Esgotado.
- [x] **Diagrama de Estados para Arbitro:** Ativo -> Descanso -> Inativo.
- [x] **Diagrama de Atividades para compra de bilhetes:** Com validação de gate de lotação máxima.
- [x] **Diagrama de Atividades para escalação de árbitros:** Com verificação de neutralidade e repouso.
