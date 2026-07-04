# Aplicação de Aprendizados ao Projeto - Fase 2
Este documento demonstra de forma clara e rigorosa como os conceitos teóricos lecionados ao longo do semestre foram aplicados no desenvolvimento prático do **Sistema de Gestão do Campeonato do Mundo de Futebol 2026** (Fase 2).

---

## 🏗️ 1. Aplicação da Metodologia ICONIX
A arquitetura e modelação do nosso sistema Java foram estruturadas em estrita conformidade com o ciclo ICONIX:

### Passo 1: Modelo de Domínio (Classes de Análise)
- **Aplicação:** Identificámos as entidades físicas do nosso campeonato. Na transposição para Java, estas constituem o nosso pacote `domain/`.
- **Classes Principais:**
  - `Jogo` (Match): Gere id, data, hora, equipas, estádio, árbitros, eventos e bilhetes.
  - `Equipa` (Team) e `Jogador` (Player): Onde limitamos o plantel a um máximo de 26 elementos (Regra FIFA).
  - `Arbitro` (Referee): Com atributos de email, nacionalidade, tipo e avaliação acumulada.
- **Evitar Classes Fantasmas:** Incluímos expressamente no domínio as classes `ClassificacaoLinha` (para as linhas de pontuação dos grupos) e `Viagem` (para o planeamento de deslocações das equipas), evitando inconsistências com o diagrama de classes.
- **Relação Fiel:** Nenhuma destas classes contém lógica de interface (GUI/CLI) ou métodos de orquestração complexos, focando-se estritamente no estado e atributos.

### Passo 2: Casos de Uso (Texto Expandido)
- **Aplicação:** Cada uma das 8 funcionalidades críticas (CU01 a CU08) foi descrita com o seu **Caminho Principal** (tudo corre bem) e **Caminhos Alternativos** (exceções).
- **Exemplo Prático (Arbitragem):**
  - *Caminho Principal:* Gestor atribui árbitros válidos a um jogo.
  - *Caminho Alternativo (Exceção 4.1):* Se o árbitro for da mesma nacionalidade de uma das equipas, o sistema lança um erro e rejeita a escala (Regra de Neutralidade).

### Passo 3: Análise de Robustez (BCE)
Dividimos a nossa estrutura de pacotes Java em 3 camadas BCE estritas, respeitando as regras de comunicação:
1. **Boundary (Fronteira):** Pacote `boundary/` (menus de consola como `MenuAdmin.java`, `MenuAdepto.java`). Tratam apenas da interação direta com o utilizador (CLI).
2. **Control (Controlo):** Pacote `manager/` (os Singletons como `CampeonatoManager.java`, `ArbitragemManager.java`). Contêm as regras de negócio, cálculos de classificação e validações.
3. **Entity (Entidade):** Pacote `domain/` (classes puras de dados).
- **Regra BCE Satisfeita:** Os menus (`Boundary`) nunca manipulam diretamente as listas de dados das entidades (`Entity`) sem passar pelas regras de validação dos controladores (`Control`).

### Passo 4: Diagrama de Classes Final e Sequência
- **Mensagem -> Método:** Cada mensagem síncrona enviada nos diagramas de sequência corresponde exatamente a um método público implementado nos nossos Managers e classes de domínio.

---

## ☕ 2. Aplicação de Padrões de Desenho: Singleton
Conforme especificado na Aula 12 e nas diretrizes do projeto, implementámos o padrão **Singleton** nos nossos controladores de serviço para garantir acesso global unificado ao estado do campeonato:

- **Implementação Escolhida:** Classe clássica com **Double-Checked Locking (DCL)** e atributo `volatile`, permitindo lazy initialization e thread-safety.
- **Managers Implementados:**
  - `CampeonatoManager`: Instância única que gere o calendário centralizado de jogos e equipas.
  - `ArbitragemManager`: Instância única que valida escalas de árbitros e guarda o seu histórico de avaliações.
  - `BilheteiraManager`: Instância única que controla a lotação máxima de cada estádio e as vendas por categoria.
  - `LogisticaManager`: Instância única que gere reservas de hotéis e viagens.
  - `AutenticacaoManager`: Instância única que gere a sessão atual do utilizador e autenticação por e-mail.

---

## 🧪 3. Estratégia de Testes Unitários (JUnit)
Seguindo o processo de validação da lógica de negócio exposto na aula prática:
- **Isolamento de UI:** Os testes JUnit focam-se exclusivamente nas classes do pacote `manager/` e `domain/`. Nenhum teste interage com a consola ou menus.
- **Padrão AAA (Arrange, Act, Assert):**
  - *Arrange:* Criar um jogo em fase de oitavos e uma equipa de arbitragem.
  - *Act:* Chamar `ArbitragemManager.getInstance().escalarArbitro()`.
  - *Assert:* AssertTrue ou AssertThrows para validar que o estado do sistema se alterou conforme esperado.
- **Testes de Erro:** Implementámos testes específicos para validar caminhos alternativos, tais como tentar escalar um árbitro para dois jogos em menos de 48 horas.

---

## 📜 4. Aplicação Prática da Ética Profissional (ACM/IEEE)
A nossa conduta no desenvolvimento do software reflete responsabilidade profissional:
1. **Público (Segurança na Bilheteira):** Implementação de regras restritas contra fraude (limite de 4 bilhetes por transação no `BilheteiraManager` para evitar a revenda ilegal e a especulação de preços).
2. **Cliente/Empregador (Integridade na Arbitragem):** Mecanismo de confidencialidade que impede que utilizadores sem privilégios de gestor visualizem a escala de árbitros antes do jogo estar finalizado ou anunciado oficialmente, mitigando riscos de suborno ou manipulação externa.
3. **Produto (Código Profissional):** Garantia de alta coesão e baixo acoplamento através da separação BCE em pacotes Java organizados, acompanhado de documentação clara e testes unitários robustos.
