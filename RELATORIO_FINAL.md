# Relatório de Engenharia de Software — Fase 1

**Projeto:** Sistema de Gestão do Campeonato do Mundo de Futebol 2026  
**Instituição:** Politécnico de Leiria — ESTG  
**Curso:** Licenciatura em Engenharia Informática  
**Disciplina:** Engenharia de Software  
**Grupo:** [preencher manualmente]  
**Data:** [preencher manualmente]  

---

## 1. Introdução

O presente relatório documenta a primeira fase do projeto de Engenharia de Software, cujo objetivo é a conceção e desenvolvimento de um **Sistema de Gestão do Campeonato do Mundo de Futebol 2026**. Este sistema destina-se à gestão interna do torneio, abrangendo áreas como o calendário de jogos, a arbitragem, a bilheteira, o alojamento das seleções e a gestão de jogadores.

O enunciado oficial fornecido pelo professor (`Projeto_ES_2526.pdf`) é propositadamente incompleto, conforme indicado pelo próprio docente. Compete ao grupo realizar o levantamento de requisitos junto do professor (que desempenha o papel de cliente) e propor requisitos adicionais que complementem a especificação. O grupo realizou este trabalho de análise e produziu dois artefactos principais:

1. **Especificação completa de requisitos** — documentada internamente e refletida na estrutura do protótipo
2. **Protótipo funcional web** — implementado em HTML/CSS/JavaScript vanilla, servindo como prova de conceito para validação com o cliente antes da implementação final em Java

A metodologia seguida é o **ICONIX**, conforme exigido pelo enunciado, que prescreve uma abordagem iterativa com foco em casos de uso, diagramas de robustez e diagramas de sequência.

---

## 2. Levantamento de Requisitos

### 2.1 Metodologia de Levantamento

O levantamento de requisitos foi efetuado através de reuniões com o professor, que desempenha o papel de cliente do sistema. As decisões do professor têm prioridade máxima e são vinculativas para o desenvolvimento. O grupo complementou estas decisões com propostas próprias de requisitos adicionais, devidamente sinalizadas.

O processo seguiu as seguintes etapas:
- Análise do enunciado oficial (incompleto por natureza)
- Reuniões com o professor/cliente para esclarecimento de requisitos
- Documentação das decisões do professor com citações diretas
- Proposta de requisitos complementares pelo grupo
- Validação através do protótipo funcional

### 2.2 Decisões do Cliente (Professor)

As seguintes decisões foram tomadas diretamente pelo professor durante as reuniões e têm carácter vinculativo:

| # | Decisão | Impacto |
|---|---------|---------|
| D01 | O sistema deve gerir o calendário completo do torneio, incluindo todas as fases | Define o módulo de Calendário como central *(Fonte: professor)* |
| D02 | A arbitragem deve incluir regras de validação (48h entre jogos, nacionalidade) | Regras de negócio críticas para o módulo de Arbitragem *(Fonte: professor)* |
| D03 | A bilheteira opera por transação externa (telefone/link), sem venda direta no sistema | Define o modelo de negócio da bilheteira *(Fonte: professor)* |
| D04 | Cada equipa tem um limite máximo de 26 jogadores no plantel | Regra FIFA implementada no módulo de Jogadores *(Fonte: professor)* |
| D05 | O alojamento deve permitir atribuição de hotéis a equipas com check-in/check-out | Define o módulo de Logística *(Fonte: professor)* |

> ⚠ *Nota: As citações diretas exatas do professor encontram-se no documento `projeto_worldcup_spec.md` do grupo. Recomenda-se a sua consulta para referência completa.*

---

## 3. Atores do Sistema

O sistema identifica **5 atores** com diferentes níveis de acesso, conforme validado tanto na especificação como no protótipo funcional:

| Ator | Descrição | Nível de Acesso | Validado no Protótipo |
|------|-----------|-----------------|----------------------|
| **Administrador** | Supervisão global do torneio. Acesso total a todos os módulos, incluindo gestão financeira e dashboard de supervisão. | Total | ✅ `admin.html` |
| **Gestor de Arbitragem** | Responsável pela atribuição de árbitros a jogos, validação de regras (48h, nacionalidade) e avaliação de desempenho. | Módulo de Arbitragem | ✅ `gestor_arbitragem.html` |
| **Gestor de Equipa** | Gere o plantel de uma seleção específica (máx. 26 jogadores), define titulares/suplentes e monitoriza estatísticas. | Módulo de Equipa (restrito à sua seleção) | ✅ `gestor_equipa.html` |
| **Gestor de Logística** | Gere alojamento (hotéis), transportes e inventário de material FIFA. | Módulo de Logística | ✅ `gestor_logistica.html` |
| **Cliente (Público)** | Consulta o calendário de jogos, classificações dos grupos, bracket do torneio e informação de bilhetes (sem compra direta). | Apenas leitura (portal público) | ✅ `publico.html` |

**Implementação no Protótipo:**  
O ficheiro `app.js` contém um **Role Switcher** (função `injectRoleSwitcher()` e `changeRole()`) que permite alternar entre vistas de cada ator. A constante de utilizadores em `DEFAULT_DATA.users` define os 4 utilizadores internos com as respetivas roles e emails.

---

## 4. Requisitos Funcionais

Os requisitos funcionais foram organizados por módulo. Para cada um, é indicado o estado de implementação no protótipo:
- ✅ = Implementado e funcional no protótipo
- 📋 = Especificado mas não implementado (planeado para Fase 2)

### 4.1 Módulo: Calendário e Jogos

| ID | Descrição | Ator | Prioridade | Estado |
|----|-----------|------|------------|--------|
| RF01 | Agendar jogo com data, hora, estádio, equipas e fase do torneio | Administrador | Alta | ✅ |
| RF02 | Listar todos os jogos com filtros por fase | Administrador / Público | Alta | ✅ |
| RF03 | Finalizar jogo com registo de resultado (golos casa/fora) | Administrador | Alta | ✅ |
| RF04 | Registar eventos durante o jogo (golos, cartões, faltas, substituições, auto-golos) | Administrador | Alta | ✅ |
| RF05 | Registar estatísticas detalhadas por jogo (11 métricas FIFA: posse, remates, cantos, foras-de-jogo, faltas, cartões amarelos/vermelhos, defesas GR, passes totais, precisão de passe, remates à baliza) | Administrador | Média | ✅ |
| RF06 | Eliminar jogo do calendário | Administrador | Média | ✅ |
| RF07 | Suporte para desempate por pênaltis em fases eliminatórias | Administrador | Média | ✅ |
| RF08 | Avanço automático de equipas nas fases eliminatórias após finalização | Administrador | Alta | ✅ |
| RF09 | Calcular e exibir classificação dos grupos em tempo real (pontos, golos, saldo) | Sistema | Alta | ✅ |
| RF10 | Avanço automático dos 2 primeiros de cada grupo para os Oitavos-de-Final | Sistema | Alta | ✅ |
| RF11 | Filtrar jogos por país, data e horário no portal público | Público | Média | ✅ |
| RF12 | Visualizar bracket completo do torneio (Oitavos → Final) | Público | Média | ✅ |

### 4.2 Módulo: Arbitragem

| ID | Descrição | Ator | Prioridade | Estado |
|----|-----------|------|------------|--------|
| RF13 | Atribuir árbitros a jogos em 4 posições (Principal, Assistente, VAR, Quarto rbitro) | Gestor Arbitragem | Alta | ✅ |
| RF14 | Validar regra das 48h entre jogos do mesmo árbitro | Sistema | Alta | ✅ |
| RF15 | Alertar conflito de nacionalidade (árbitro da mesma nacionalidade de uma equipa) | Sistema | Alta | ✅ |
| RF16 | Avaliar desempenho dos árbitros após jogo finalizado (sistema de estrelas 1-5) | Gestor Arbitragem | Média | ✅ |
| RF17 | Calcular score FIFA automático dos árbitros com base nas avaliações | Sistema | Média | ✅ |
| RF18 | Alterar estado do árbitro (Ativo, Descansar, Inativo) | Gestor Arbitragem | Média | ✅ |
| RF19 | Listar árbitros por categoria (Campo, VAR, Assistente) com ranking | Gestor Arbitragem | Baixa | ✅ |
| RF20 | Resetar avaliações e pontuações dos árbitros | Gestor Arbitragem | Baixa | ✅ |

### 4.3 Módulo: Bilheteira

| ID | Descrição | Ator | Prioridade | Estado |
|----|-----------|------|------------|--------|
| RF21 | Gerir lotação e preços por categoria de bilhete (Premium, Intermediária, Económica, Local) por jogo | Gestor Bilheteira | Alta | ✅ |
| RF22 | Visualizar dashboard de vendas com faturação total e ocupação global | Gestor Bilheteira | Alta | ✅ |
| RF23 | Gráfico circular (pie chart) de resumo de lotação (vendidos vs disponíveis) | Gestor Bilheteira | Média | ✅ |
| RF24 | Gráfico de barras de vendas por dia da semana | Gestor Bilheteira | Média | ✅ |
| RF25 | Monitorizar alertas de fraude (bilhetes duplicados, IPs suspeitos) | Gestor Bilheteira | Alta | ✅ |
| RF26 | Bloquear/resolver alertas de fraude | Gestor Bilheteira | Alta | ✅ |
| RF27 | Relatório de receita bruta por categoria de bilhete | Gestor Bilheteira | Média | ✅ |
| RF28 | Controlo de lotação máxima por estádio e por categoria | Gestor Bilheteira | Alta | ✅ |
| RF29 | Exibir informação de compra externa (telefone + link TicketMaster) no portal público | Público | Alta | ✅ |

### 4.4 Módulo: Jogadores e Equipas

| ID | Descrição | Ator | Prioridade | Estado |
|----|-----------|------|------------|--------|
| RF30 | Registar nova equipa/seleção com nome e treinador | Administrador | Alta | ✅ |
| RF31 | Adicionar jogador ao plantel (nome, número, posição) com limite de 26 | Gestor Equipa | Alta | ✅ |
| RF32 | Remover jogador do plantel | Gestor Equipa | Média | ✅ |
| RF33 | Definir titulares vs suplentes (máx. 11 titulares) | Gestor Equipa | Alta | ✅ |
| RF34 | Confirmar convocatória do jogo (validação de 11 titulares) | Gestor Equipa | Alta | ✅ |
| RF35 | Visualizar estatísticas individuais (golos, assistências, cartões, minutos, condição física) | Gestor Equipa | Média | ✅ |
| RF36 | Visualizar histórico de lesões por jogador | Gestor Equipa | Média | ✅ |
| RF37 | Exportar ficha técnica do jogador e convocatória | Gestor Equipa | Baixa | ✅ |
| RF38 | Acesso restrito por equipa (gestor só vê a sua seleção) | Sistema | Alta | ✅ |

### 4.5 Módulo: Logística e Alojamento

| ID | Descrição | Ator | Prioridade | Estado |
|----|-----------|------|------------|--------|
| RF39 | Listar hotéis com estado (Disponível/Ocupado), localização, estrelas e capacidade | Gestor Logística | Alta | ✅ |
| RF40 | Atribuir equipa a hotel com datas de check-in/check-out | Gestor Logística | Alta | ✅ |
| RF41 | Realizar check-out de equipa e libertar hotel | Gestor Logística | Média | ✅ |
| RF42 | Monitorizar frota de autocarros e rotas ativas | Gestor Logística | Média | ✅ |
| RF43 | Gerir inventário de material FIFA (equipamento, médico, consumíveis, tecnologia) | Gestor Logística | Média | ✅ |

### 4.6 Módulo: Sistema e Autenticação

| ID | Descrição | Ator | Prioridade | Estado |
|----|-----------|------|------------|--------|
| RF44 | Login por email com redirecionamento automático por role | Todos (internos) | Alta | ✅ |
| RF45 | Portal de entrada com acesso público e acesso por role | Todos | Alta | ✅ |
| RF46 | Role Switcher para prototipagem (alternância rápida entre vistas) | Sistema (dev) | Alta | ✅ |
| RF47 | Persistência de dados em LocalStorage com versionamento | Sistema | Alta | ✅ |
| RF48 | Dashboard administrativo com supervisão financeira e links rápidos aos módulos | Administrador | Alta | ✅ |

---

## 5. Requisitos Não Funcionais

| ID | Descrição | Origem | Estado |
|----|-----------|--------|--------|
| RNF01 | **Controlo de acesso por roles** — Cada utilizador só acede às funcionalidades autorizadas pela sua role. O sistema impede o acesso a módulos não autorizados. | Professor | ✅ |
| RNF02 | **Sem venda direta de bilhetes** — O sistema não efetua transações financeiras. A compra é redirecionada para entidades externas (TicketMaster) via telefone ou link. Não são exibidos preços ao público. | Professor | ✅ |
| RNF03 | **Isolamento de equipa** — Cada Gestor de Equipa só tem acesso ao plantel da sua seleção, sem visibilidade sobre outras equipas. | Professor | ✅ |
| RNF04 | **Interface responsiva e premium** — A interface utiliza design moderno com tipografia Outfit (Google Fonts), glassmorphism, animações suaves e paleta de cores coerente (verde primário #00D26A, dark #1A202C). | Grupo | ✅ |
| RNF05 | **Persistência local** — Os dados são armazenados em LocalStorage do navegador com controlo de versão (atualmente v14) para garantir consistência entre atualizações. | Grupo | ✅ |
| RNF06 | **Navegação por sidebar** — Todos os módulos internos utilizam uma barra lateral fixa (280px) com navegação por separadores e animações de transição (fadeIn). | Grupo | ✅ |
| RNF07 | **Internacionalização** — A interface está integralmente em Português de Portugal (pt-PT), incluindo labels, mensagens de erro e formatação numérica. | Grupo | ✅ |
| RNF08 | **SEO básico** — Todas as páginas incluem meta tags (title, description, og:title, og:description) e estrutura semântica HTML5. | Grupo | ✅ |
## 6. Regras de Negócio

As regras de negócio foram extraídas das decisões do professor e da análise do código do protótipo. Cada regra indica a sua origem e estado de implementação.

| ID | Regra | Módulo | Origem | Estado |
|----|-------|--------|--------|--------|
| RN01 | Um árbitro não pode ser atribuído a dois jogos com menos de **48 horas** de intervalo | Arbitragem | Professor | ✅ Implementado — alerta no dashboard (`gestor_arbitragem.html`) |
| RN02 | Um árbitro não pode oficiar um jogo onde participe uma equipa da sua **nacionalidade** | Arbitragem | Professor | ✅ Implementado — alerta de conflito de nacionalidade |
| RN03 | Cada equipa tem um **máximo de 26 jogadores** no plantel | Jogadores | Professor | ✅ Implementado — `addPlayerToTeam()` em `app.js` (linha 78) |
| RN04 | O onze inicial deve ter **exatamente 11 titulares** | Jogadores | Professor | ✅ Implementado — `confirmSquad()` em `gestor_equipa.html` |
| RN05 | A **venda de bilhetes** é efetuada por entidade externa (telefone/TicketMaster), o sistema apenas gere a lotação e preços | Bilheteira | Professor | ✅ Implementado — portal público mostra telefone e botão "Comprar ↗" |
| RN06 | Não são exibidos **preços de bilhetes** ao público | Bilheteira | Professor | ✅ Implementado — preços apenas visíveis no módulo de gestão |
| RN07 | A classificação dos grupos segue a regra: **3 pts vitória, 1 pt empate, 0 pts derrota** | Calendário | Professor | ✅ Implementado — `calculateGroupStandings()` em `app.js` |
| RN08 | Desempate nos grupos por: 1º Saldo de Golos, 2º Golos Marcados, 3º Confronto Direto | Calendário | Grupo | ✅ Implementado — ordenação em `calculateGroupStandings()` |
| RN09 | Os **2 primeiros** de cada grupo avançam automaticamente para os Oitavos-de-Final | Calendário | Professor | ✅ Implementado — `checkAndAdvanceGroupsToOitavos()` |
| RN10 | Nas fases eliminatórias, o vencedor **avança automaticamente** para a fase seguinte | Calendário | Grupo | ✅ Implementado — `finalizeMatch()` (linhas 44-54) |
| RN11 | **Pênaltis** apenas em fases eliminatórias (não na fase de grupos) | Calendário | Grupo | ✅ Implementado — área de pênaltis no modal de finalização |
| RN12 | O Gestor de Equipa só pode gerir o plantel da **sua própria seleção** | Jogadores | Professor | ✅ Implementado — `initTeam()` verifica `user.team` |
| RN13 | Cada jogo possui **4 categorias de bilhetes** com lotação e preço independentes | Bilheteira | Grupo | ✅ Implementado — `DEFAULT_TICKETS` em `app.js` |
| RN14 | A lotação atribuída **não pode exceder** a capacidade máxima do estádio por categoria | Bilheteira | Grupo | ✅ Implementado — `STADIUM_CAPACITIES` em `gestor_bilheteira.html` |
| RN15 | O score FIFA de cada árbitro é calculado como **média ponderada** das avaliações recebidas | Arbitragem | Grupo | ✅ Implementado — `evaluateMatch()` em `app.js` |
| RN16 | Um número de camisola **não pode ser repetido** dentro do mesmo plantel | Jogadores | Grupo | ✅ Implementado — validação em `gestor_equipa.html` |

---

## 7. Modelo de Domínio

### 7.1 Entidades Identificadas

As entidades foram extraídas dos dados mock do protótipo (`DEFAULT_DATA` em `app.js`) e da estrutura dos componentes HTML.

| Entidade | Atributos (extraídos do código) |
|----------|--------------------------------|
| **Match (Jogo)** | `id`, `date`, `time`, `stadium`, `homeTeam`, `awayTeam`, `phase`, `status` (Agendado/Finalizado), `winner`, `evaluated`, `refPrincipal`, `refAssistente1`, `refVAR`, `refQuarto`, `events[]`, `tickets[]`, `goalsHome`, `goalsAway`, `penaltiesHome`, `penaltiesAway`, `stats`, `generalRating` |
| **Team (Equipa)** | `name`, `coach`, `flag`, `players[]` |
| **Player (Jogador)** | `id`, `number`, `name`, `position` (GR/Defesa/Médio/Avançado), `status` (Apto/Lesionado/Suspenso), `goals`, `assists`, `injuries`, `yellow`, `red`, `isStarter`, `minutes`, `energy`, `injuryHistory[]` |
| **Referee (rbitro)** | `id`, `email`, `name`, `nationality`, `type` (Principal/VAR/Assistente), `status` (Ativo/Descansar/Inativo), `matchesCount`, `score`, `evaluationsCount` |
| **Ticket (Bilhete)** | `category` (Premium/Intermediária/Económica/Local), `price`, `sold`, `total` |
| **Event (Evento de Jogo)** | `type` (Golo/Auto-Golo/Falta/Cartão Amarelo/Cartão Vermelho/Substituição), `minute`, `player`, `team`, `timestamp` |
| **Hotel (Alojamento)** | `id`, `name`, `location`, `stars`, `capacity`, `status` (Disponível/Ocupado), `team`, `checkin`, `checkout` |
| **User (Utilizador)** | `email`, `role`, `name`, `team` (opcional) |
| **Group (Grupo)** | `name` (Grupo A-H), `teams[]` |
| **FraudLog (Registo Fraude)** | `id`, `timestamp`, `type`, `description` |
| **MatchStats (Estatísticas)** | `possession`, `totalShots`, `shotsOnTarget`, `corners`, `offsides`, `fouls`, `yellowCards`, `redCards`, `saves`, `totalPasses`, `passAccuracy` (Casa vs Fora) |

### 7.2 Relações entre Entidades

```
Match *---* Team          (homeTeam, awayTeam)
Match 1---* Event         (eventos do jogo)
Match 1---4 Ticket        (4 categorias por jogo)
Match 1---4 Referee       (4 posições de arbitragem)
Match 1---1 MatchStats    (estatísticas opcionais)
Team  1---* Player        (plantel, máx. 26)
Group 1---4 Team          (4 equipas por grupo)
Hotel *---1 Team          (equipa alojada, opcional)
User  *---1 Team          (gestor associado a equipa, opcional)
```

---

## 8. Casos de Uso — Descrição Detalhada

Na modelação do sistema segundo a metodologia **ICONIX**, os Casos de Uso não são elementos isolados. Conforme ilustrado no diagrama de casos de uso (`diagrama_casos_uso.html` e `DIAGRAMA_CASOS_USO.md`), foram estabelecidos dois tipos de relacionamentos fundamentais entre as funcionalidades:
- **`<<Precedes>>` (Precede):** Relação de sequência obrigatória, onde um caso de uso é pré-requisito de outro. Exemplo: `Login no Sistema` precede as operações de gestão; `Agendar Novo Jogo` precede a `Atribuição de rbitros` e a `Gestão de Bilhetes`.
- **`<<Invokes>>` (Invoca):** Relação de inclusão/chamada automática de sub-funções ou regras de negócio. Exemplo: `Agendar Novo Jogo` invoca automaticamente a `Geração de Bilhetes Padrão`; `Finalizar Jogo` invoca o `Registo de Eventos` e a `Atualização de Classificações`.

Abaixo apresenta-se a especificação detalhada de cada caso de uso principal.

### CU01 — Agendar Novo Jogo

| Campo | Descrição |
|-------|-----------|
| **Nome** | Agendar Novo Jogo |
| **Ator** | Administrador |
| **Pré-condições** | Administrador autenticado; equipas registadas no sistema |
| **Fluxo Principal** | 1. Admin acede ao separador "Gestão de Jogos" em `admin.html` → 2. Preenche formulário (fase, estádio, equipa casa, equipa fora, data, hora) → 3. Clica "Adicionar Jogo ao Calendário" → 4. Sistema executa `addMatch()` em `app.js` → 5. Jogo é adicionado com status "Agendado" e bilhetes padrão |
| **Fluxos Alternativos** | 4a. Se campos obrigatórios em falta → formulário HTML5 impede submissão |
| **Pós-condições** | Novo jogo visível no calendário admin e no portal público |
| **Componente** | `admin.html` → `handleAddMatch()` + `app.js` → `addMatch()` |

### CU02 — Finalizar Jogo com Eventos e Estatísticas

| Campo | Descrição |
|-------|-----------|
| **Nome** | Finalizar Jogo |
| **Ator** | Administrador |
| **Pré-condições** | Jogo com status "Agendado" |
| **Fluxo Principal** | 1. Admin clica "Finalizar" num jogo → 2. Modal abre com campos para eventos (golos, cartões, faltas, substituições) → 3. Admin adiciona eventos com minuto, jogador e equipa → 4. Score atualiza automaticamente (golos casa vs fora) → 5. Admin preenche 11 estatísticas FIFA → 6. Vencedor é determinado automaticamente → 7. Admin confirma → 8. Sistema executa `finalizeMatch()` → 9. Se eliminatória, vencedor avança automaticamente |
| **Fluxos Alternativos** | 7a. Se empate em eliminatória → área de pênaltis ativa para desempate |
| **Pós-condições** | Jogo marcado como "Finalizado"; vencedor registado; classificação atualizada |
| **Componente** | `admin.html` → `openFinalizeModal()`, `confirmFinalize()` + `app.js` → `finalizeMatch()` |

### CU03 — Atribuir rbitros a Jogo

| Campo | Descrição |
|-------|-----------|
| **Nome** | Atribuir Equipa de Arbitragem |
| **Ator** | Gestor de Arbitragem |
| **Pré-condições** | rbitros registados; jogo agendado |
| **Fluxo Principal** | 1. Gestor acede "Atribuição de rbitros" → 2. Seleciona slot de um jogo (Principal/Assistente/VAR/Quarto) → 3. Modal exibe lista de árbitros disponíveis → 4. Sistema valida regra 48h e nacionalidade → 5. Gestor seleciona árbitro → 6. rbitro atribuído ao jogo |
| **Fluxos Alternativos** | 4a. Alerta se conflito de nacionalidade → 4b. Alerta se violação da regra 48h |
| **Pós-condições** | rbitro associado ao jogo no slot selecionado |
| **Componente** | `gestor_arbitragem.html` → `openRefPicker()`, `assignRef()` |

### CU04 — Avaliar Desempenho de rbitros

| Campo | Descrição |
|-------|-----------|
| **Nome** | Avaliar Equipa de Arbitragem pós-jogo |
| **Ator** | Gestor de Arbitragem |
| **Pré-condições** | Jogo finalizado; árbitros atribuídos; jogo não avaliado |
| **Fluxo Principal** | 1. Gestor acede "Avaliar Jogos" → 2. Seleciona jogo finalizado → 3. Atribui avaliação geral (1-5 estrelas) → 4. Avalia cada árbitro individualmente (1-5 estrelas) → 5. Confirma → 6. Sistema calcula novo score FIFA |
| **Pós-condições** | Jogo marcado como avaliado; scores dos árbitros atualizados |
| **Componente** | `gestor_arbitragem.html` → `openEvalModal()`, `submitEvaluation()` + `app.js` → `evaluateMatch()` |

### CU05 — Gerir Plantel de Equipa

| Campo | Descrição |
|-------|-----------|
| **Nome** | Gerir Plantel da Seleção |
| **Ator** | Gestor de Equipa |
| **Pré-condições** | Gestor autenticado; associado a uma equipa específica |
| **Fluxo Principal** | 1. Gestor acede ao módulo de equipa → 2. Sistema filtra automaticamente pela equipa do gestor → 3. Gestor pode: adicionar jogador (modal), remover jogador, alternar titular/suplente, consultar ficha técnica → 4. Ao confirmar convocatória, sistema valida 11 titulares |
| **Fluxos Alternativos** | 3a. Se plantel ≥ 26 → erro ao adicionar → 3b. Se nº camisola duplicado → erro |
| **Pós-condições** | Plantel atualizado; estatísticas do dashboard recalculadas |
| **Componente** | `gestor_equipa.html` → `initTeam()`, `renderPlayers()`, `toggleStarter()`, `confirmSquad()` |

### CU06 — Gerir Bilheteira de um Jogo

| Campo | Descrição |
|-------|-----------|
| **Nome** | Gerir Lotação e Preços de Bilhetes |
| **Ator** | Gestor de Bilheteira (via Administrador) |
| **Pré-condições** | Jogo registado com bilhetes padrão |
| **Fluxo Principal** | 1. Gestor acede "Performance de Vendas" → 2. Clica "Gerir" num jogo → 3. Modal mostra 4 categorias com preço e lotação editáveis → 4. Sistema exibe lotação máxima do estádio por categoria → 5. Gestor ajusta valores → 6. Confirma → 7. Sistema valida que lotação ≥ bilhetes já vendidos |
| **Fluxos Alternativos** | 7a. Se lotação < vendidos → erro com mensagem explicativa |
| **Pós-condições** | Preços e lotação atualizados; dashboard recalculado |
| **Componente** | `gestor_bilheteira.html` → `openEdit()`, `savePrices()`, `updateCapacityStatus()` |

### CU07 — Atribuir Hotel a Equipa

| Campo | Descrição |
|-------|-----------|
| **Nome** | Atribuir Alojamento |
| **Ator** | Gestor de Logística |
| **Pré-condições** | Hotel disponível; equipas registadas |
| **Fluxo Principal** | 1. Gestor acede "Alojamento" → 2. Clica "Atribuir Equipa" num hotel disponível → 3. Modal pede seleção de equipa e datas check-in/check-out → 4. Confirma → 5. Hotel muda para "Ocupado" |
| **Fluxos Alternativos** | 4a. Se datas não preenchidas → alerta |
| **Pós-condições** | Hotel ocupado; equipa associada com datas |
| **Componente** | `gestor_logistica.html` → `openAssignModal()`, `confirmAssignment()` |

### CU08 — Consultar Portal Público

| Campo | Descrição |
|-------|-----------|
| **Nome** | Consultar Calendário e Classificações |
| **Ator** | Cliente (Público) |
| **Pré-condições** | Nenhuma (acesso livre) |
| **Fluxo Principal** | 1. Utilizador acede `publico.html` → 2. Visualiza bracket do torneio → 3. Filtra jogos por fase, país, data ou hora → 4. Consulta tabela de jogos com informação de bilhetes (telefone + link externo) → 5. Abre sidebar de classificação dos grupos |
| **Pós-condições** | Nenhuma (apenas consulta) |
| **Componente** | `publico.html` → `renderBracket()`, `renderPublicMatches()`, `renderPublicGroupStandings()` |

---

## 9. Protótipo

### 9.1 Tecnologia Utilizada

O protótipo foi desenvolvido como uma **aplicação web estática** (sem servidor backend) utilizando:

| Tecnologia | Utilização |
|------------|-----------|
| **HTML5** | Estrutura semântica de todas as páginas |
| **CSS3** (Vanilla) | Design system com variáveis CSS, glassmorphism, animações, gradientes |
| **JavaScript** (ES6+) | Lógica de negócio, manipulação DOM, persistência LocalStorage |
| **Google Fonts** (Outfit) | Tipografia premium e moderna |
| **LocalStorage** | Persistência de dados no navegador com versionamento (v14) |

> **Nota:** Este é um protótipo funcional para validação com o cliente antes da implementação final em Java (Fase 2). Não utiliza framework (React/Vue) nem backend.

### 9.2 Módulos Implementados

| Ficheiro | Módulo | Descrição |
|----------|--------|-----------|
| `index.html` | Portal de Entrada | Página inicial com acesso ao portal público e gestão de arbitragem |
| `publico.html` | Portal Público | Calendário, bracket do torneio, filtros por fase/país/data/hora, classificação dos grupos |
| `admin.html` | Painel Administrativo | Dashboard financeiro, gestão de equipas, calendário de jogos, fase de grupos, bracket, finalização de jogos |
| `gestor_arbitragem.html` | Gestão de Arbitragem | Dashboard de alertas, atribuição de árbitros, avaliação de jogos, lista/ranking de árbitros |
| `gestor_equipa.html` | Gestão de Equipa | Dashboard com estatísticas, calendário filtrado, plantel com titulares/suplentes, ficha técnica |
| `gestor_bilheteira.html` | Gestão de Bilheteira | Dashboard de vendas, inventário com gráficos, segurança anti-fraude, relatórios por categoria |
| `gestor_logistica.html` | Gestão de Logística | Alojamento (hotéis), transportes (frota/rotas), inventário de material FIFA |
| `app.js` | Motor de Dados | Funções CRUD, dados mock, autenticação, role switcher, lógica de grupos e torneio |
| `styles.css` | Design System | Variáveis CSS, botões, cards, glassmorphism, utilities globais |

### 9.3 Funcionalidades Demonstradas

O protótipo demonstra as seguintes funcionalidades **funcionais e testáveis**:

1. **Validação de regra 48h** — alertas automáticos no dashboard de arbitragem
2. **Alerta de nacionalidade** — árbitro da mesma nacionalidade de uma equipa em jogo
3. **Estados de bilhete** — gestão de lotação por categoria com validação de limites por estádio
4. **Sistema anti-fraude** — monitorização de bilhetes duplicados e IPs suspeitos
5. **Roles e permissões** — Role Switcher com redirecionamento automático por role
6. **Avanço automático no torneio** — vencedor de eliminatória preenche slot na fase seguinte
7. **Classificação de grupos em tempo real** — cálculo automático de pontos e desempate
8. **Avaliação de árbitros** — sistema de estrelas com cálculo de score FIFA
9. **Gestão de plantel restrita** — gestor só acede à sua equipa
10. **Bracket visual do torneio** — representação gráfica com SVG (Oitavos → Final)
11. **Estatísticas detalhadas** — 11 métricas FIFA por jogo (posse, remates, cantos, etc.)
12. **Filtros avançados** — por fase, país, data e horário no portal público

### 9.4 Decisões de Design

| Decisão | Justificação |
|---------|-------------|
| **Sidebar fixa (280px) em todos os módulos** | Navegação consistente e previsível entre funcionalidades |
| **Modais para formulários e detalhes** | Mantém o contexto da página atual, com scroll vertical para conteúdo extenso |
| **Portal público separado** | Simula a separação entre interface interna (gestão) e externa (público) |
| **Dados mock no `app.js`** | 32 equipas, 31 jogos, 10 árbitros, 4 utilizadores — permite demonstração realista |
| **LocalStorage com versionamento** | Reset automático dos dados quando a versão do protótipo é atualizada |
| **Design system com variáveis CSS** | Paleta coerente (#00D26A verde primário, #1A202C dark) em todos os módulos |
| **Role Switcher flutuante** | Permite ao cliente/professor testar todas as vistas sem múltiplos logins |
| **Gráficos nativos (CSS/SVG)** | Pie charts e bar charts sem dependências externas |

---

## 10. Conclusão e Fase 2

### 10.1 Estado Atual — Fase 1

A Fase 1 foi concluída com a entrega de:

- ✅ **Especificação completa de requisitos** — 48 requisitos funcionais, 8 não funcionais, 16 regras de negócio
- ✅ **Protótipo funcional web** — 8 módulos implementados com dados mock realistas
- ✅ **Modelo de domínio** — 11 entidades identificadas com atributos e relações
- ✅ **Casos de uso detalhados** — 8 casos de uso com fluxos completos
- ✅ **Validação de regras de negócio** — todas as regras críticas implementadas no protótipo

O protótipo serve como **prova de conceito** para validação com o professor/cliente, demonstrando que os requisitos levantados são viáveis e que a interface proposta cumpre os objetivos do projeto.

### 10.2 Trabalho Futuro — Fase 2

| Entregável | Descrição |
|-----------|-----------|
| **Implementação em Java** | Migração do protótipo web para aplicação Java conforme exigido pelo enunciado |
| **Diagramas ICONIX no Visual Paradigm** | Modelo de Domínio, Diagramas de Casos de Uso, Diagramas de Robustez, Diagramas de Sequência, Diagrama de Classes |
| **Base de Dados** | Modelação e implementação da base de dados relacional |
| **Testes** | Testes unitários e de integração da implementação Java |
| **Repositório GitHub** | Organização do código no repositório privado com branches e commits estruturados |
| **Entrega final no Moodle** | Empacotamento do projeto para submissão na plataforma institucional |

---

## Anexos

### Anexo A — Glossário

| Termo | Definição |
|-------|-----------|
| **ICONIX** | Metodologia de desenvolvimento de software que combina elementos de RUP e XP, focada em casos de uso e diagramas de robustez |
| **Caso de Uso** | Descrição de uma interação entre um ator e o sistema para atingir um objetivo |
| **Ator** | Entidade externa (pessoa ou sistema) que interage com o sistema |
| **Regra de Negócio** | Restrição ou política que o sistema deve impor para manter a integridade dos dados |
| **Role** | Papel/função atribuído a um utilizador que define as suas permissões no sistema |
| **LocalStorage** | API do navegador para armazenamento persistente de dados no lado do cliente |
| **Mock Data** | Dados fictícios utilizados no protótipo para simular cenários reais |
| **Bracket** | Representação visual das chaves de um torneio eliminatório |
| **VAR** | Video Assistant Referee — árbitro de vídeo que assiste o árbitro principal |
| **FIFA** | Fédération Internationale de Football Association — entidade reguladora do futebol mundial |
| **Glassmorphism** | Tendência de design UI que utiliza transparência, desfoque e bordas subtis |
| **Sidebar** | Barra lateral de navegação fixa presente em todas as interfaces de gestão |
| **Score FIFA** | Pontuação de desempenho dos árbitros calculada com base nas avaliações |

### Anexo B — Estrutura do Repositório

```
ES/
├── index.html               Portal de entrada
├── publico.html             Portal público (calendário, bracket, grupos)
├── admin.html               Painel administrativo
├── gestor_arbitragem.html   Módulo de arbitragem
├── gestor_equipa.html       Módulo de gestão de equipa
├── gestor_bilheteira.html   Módulo de bilheteira
├── gestor_logistica.html    Módulo de logística
├── app.js                   Motor de dados e lógica partilhada
├── styles.css               Design system global
├── update_app.js            Script de migração de dados
├── PLANO_EXPANSAO.md        Documento de visão futura
├── SKILL.md                 Template do relatório
└── RELATORIO_ES_FASE1.md    Este relatório
```

### Anexo C — Referências

- **Enunciado oficial:** `Projeto_ES_2526.pdf` (disponível no Moodle)
- **Protótipo funcional:** Ficheiros HTML/JS/CSS no repositório GitHub do grupo
- **Plano de Expansão:** `PLANO_EXPANSAO.md` — visão para funcionalidades futuras

### Anexo D — Dados Mock do Protótipo

O protótipo inclui os seguintes dados de demonstração:

- **32 equipas** com treinadores (8 grupos de 4)
- **31 jogos** pré-agendados (16 Dezasseis-avos + 8 Oitavos + 4 Quartos + 2 Meias-Finais + 1 Final)
- **10 árbitros** FIFA (5 Principais, 2 VAR, 2 Assistentes, 1 adicional)
- **4 utilizadores** do sistema (Admin, Gestor Arbitragem, Gestor Equipa, Gestor Logística)
- **8 grupos** (A a H) com mapeamento FIFA para Oitavos-de-Final
- **8 estádios** com capacidades por categoria de bilhete
- **6 hotéis** com diferentes estados e atribuições
- **4 equipas com plantel completo** (26 jogadores cada): Portugal, França, Brasil, Argentina
- **6 fases do torneio**: Grupos → Dezasseis-avos → Oitavos → Quartos → Meias-Finais → Final
