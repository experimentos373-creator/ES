# Documentação da Fase 2 — Pós-Reunião com o Professor
## Sistema de Gestão do Campeonato do Mundo de Futebol 2026 (Engenharia de Software)

Este documento descreve a estrutura atual do projeto Java, mapeando as classes físicas com os conceitos do Modelo de Domínio e detalhando as correções e refinamentos aplicados à documentação da **Fase 2** após a reunião de feedback com o docente.

---

## 📂 1. Estrutura do Projeto Java (O que o projeto contém)

A arquitetura física da aplicação está implementada de forma modular no pacote `projeto_java/src/` e divide-se em três camadas principais:

### A. Entidades do Domínio (`domain/`)
Representam os dados lógicos puros e a persistência simulada do campeonato:
* **`Jogo`**: Guarda o estado da partida, golos, penalties, escala de árbitros escalada e referências de bracket (`proximoJogo`).
* **`Equipa`**: Representa uma seleção nacional, contendo o plantel de jogadores e o estádio principal.
* **`Jogador`**: Dados do atleta (nome, camisola, golos, assistências) e estados clínicos/disciplinares (`APTO`, `LESIONADO`, `SUSPENSO`).
* **`Arbitro`**: Guarda os dados do árbitro, nacionalidade, tipo (Principal, VAR, Assistente), avaliações e score FIFA acumulado.
* **`Estadio`** & **`SetorEstadio`**: Gerem a capacidade total física, preços base e o registo de bilhetes vendidos por setor.
* **`Bilhete`**: Representa um ingresso emitido associado a um jogo e setor.
* **`Hotel`** & **`AlojamentoInfo`**: Suportam a alocação logística de comitivas e o registo temporal de alojamentos ativos.
* **`Viagem`**: Registo de deslocações e transporte de equipas para jogos.
* **`Utilizador`**: Contas e perfis de segurança do sistema (Admin, Gestores e Adepto).

### B. Gestores de Estado/Controlo (`manager/`)
Implementados sob o padrão **Singleton** para garantir a persistência de coleções simuladas em ficheiros serializados (`.ser`):
* **`CampeonatoManager`**: Gere equipas, estádios, tabelas classificativas de grupos e progressão do bracket de eliminatórias.
* **`ArbitragemManager`**: Controla o pool de árbitros, avaliações éticas, e validação de regras de escala (neutralidade e repouso de 48h).
* **`LogisticaManager`**: Controla as viagens e alocação de hotéis (exclusividade de datas e limites de capacidade).
* **`BilheteiraManager`**: Efetua a venda de ingressos, aplicando a validação anti-bot (máximo 4 bilhetes por transação) e limites físicos dos setores.
* **`AutenticacaoManager`**: Gere sessões de utilizadores, palavras-passe (hash) e autorizações.

### C. Interfaces e Limites (`boundary/gui/`)
Ecrãs e controladores desenvolvidos em **JavaFX** (FXML) que interagem com os `Managers` para expor o sistema aos utilizadores.

---

## 🛠️ 2. Changelog Metodológico (Alterações Pós-Reunião)

Para alinhar a documentação técnica com os critérios estritos exigidos pelo docente (metodologia ICONIX e UML pura), foram efetuadas as seguintes correções:

### I. Modelo de Domínio Conceptual (Passo 1 — Análise)
* **Remoção de Elementos de Desenho**: Limpeza total de cardinalidades, operações/métodos e marcadores de visibilidade (`-`/`+`), os quais são decisões exclusivas de desenho técnico (Passo 4).
* **Limpeza de Redundâncias**: Remoção de atributos internos (ex: `jogadores: List` na classe `Equipa`) que já eram expressos graficamente pelas linhas de associação UML.
* **Nomes Conceptuais**: Substituição de chaves técnicas de base de dados (`id`, `jogoId`) por conceitos de negócio lógicos (`numero`, `numeroInscricao`, `numeroJogo`).

### II. Análise de Robustez (Passo 3 — Fronteira BCE)
* **Introdução de Diagramas**: Criação de diagramas de robustez PlantUML para os casos de uso sob responsabilidade do Paulo (`CU02 — Agendar Jogo`, `CU03 — Finalizar Jogo` e `CU23 — Vender Bilhetes`).
* **Cumprimento do BCE Estrito**:
  * Proibição de ligações diretas *Boundary-to-Boundary* (ex: no `CU23` a navegação de ecrãs é mediada pelo controller: `Portal` ➔ `CtrlBilheteira` ➔ `EcrãCompra`).
  * Proibição de ligações *Entity-to-Entity*.
  * Validações complexas remapeadas como auto-chamadas de controlo (*self-calls* no `CampeonatoManager`).

### III. Diagramas de Sequência de Desenho (Passo 4)
* **Limpeza de Setas de Retorno**: Remoção de setas tracejadas (`-->`) em retornos implícitos de fluxos felizes. As setas tracejadas agora representam apenas saídas de erro real (lançamento de exceções como `IllegalArgumentException`).
* **Nota Explicativa**: Inclusão do texto descritivo do caso de uso do lado esquerdo do diagrama para máxima rastreabilidade.

### IV. Correções de Texto e Consistência (Glossário)
* **Capitalização de Entidades**: Padronização de todas as entidades do domínio com letra maiúscula (ex: `Jogo`, `Equipa`, `Bilhete`, `SetorEstadio`) e termos de linguagem comum em minúsculas (ex: `receita`, `estatísticas`), evitando inconsistências estáticas.
* **Terminologia de Alojamento**: Atualização da documentação do `CU19` (de "Exclusividade" para "Partilha de Alojamento"), visto que a lógica física do código permite que mais do que uma equipa partilhe o mesmo hotel desde que a capacidade de pessoas não seja ultrapassada.
