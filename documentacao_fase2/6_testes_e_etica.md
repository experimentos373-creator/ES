# Testes Unitários e Ética Profissional
## Engenharia de Software – Projeto (Fase 2)

Este documento descreve os critérios formais relativos aos **Testes Unitários** e à integração dos princípios de **Ética Profissional** na Fase 2 do projeto.

---

## 🧪 1. Testes Unitários (JUnit)

Diferente de testes manuais, a Fase 2 exige a implementação de testes unitários automatizados para assegurar o correto funcionamento da lógica do campeonato.

### Regras dos Testes:
1. **Foco Exclusivo na Lógica de Negócio:**
   - Devem ser testados apenas os componentes lógicos (ex: regras de neutralidade na arbitragem, cálculo de lotação na bilheteira, validações de disponibilidade de hotéis).
   - **Não** se devem fazer testes para classes de interface com o utilizador (`boundary` / menus da consola).
2. **Quota Mínima Individual:**
   - Cada elemento do grupo deve implementar testes para, pelo menos, **três requisitos específicos** da sua autoria.
   - Devem ser testados tanto os caminhos felizes (fluxos básicos) como as condições de erro e exceções (caminhos alternativos).
3. **Padrão AAA (Arrange, Act, Assert):**
   - **Arrange:** Configurar os dados e o estado inicial (ex: criar o árbitro e o jogo).
   - **Act:** Executar a ação a testar (ex: tentar associar o árbitro ao jogo).
   - **Assert:** Verificar se o resultado obtido é o esperado (ex: verificar se retornou falso ou se lançou uma exceção devido à nacionalidade coincidente).

---

## 📜 2. Ética Profissional (ACM/IEEE)

Como futuros Engenheiros de Informática, a vossa conduta profissional e o próprio software desenvolvido devem refletir os códigos de ética da **ACM** (Association for Computing Machinery) e do **IEEE** (Institute of Electrical and Electronics Engineers). No contexto do campeonato, estes princípios aplicam-se da seguinte forma:

### 1. Público (Public)
* **Princípio:** O engenheiro de software deve agir de forma coerente com o interesse público.
* **Aplicação no Projeto:**
  - **Segurança da Bilheteira:** Garantir que o sistema de venda de bilhetes é seguro e justo, evitando que bots comprem todos os ingressos para revenda (proteção contra especulação).
  - **Privacidade dos Dados:** Proteger dados pessoais dos adeptos (nome, métodos de pagamento) e das comitivas.

### 2. Cliente e Empregador (Client and Employer)
* **Princípio:** Agir de forma a promover os interesses do cliente e empregador, mantendo a confidencialidade e integridade.
* **Aplicação no Projeto:**
  - **Integridade da Arbitragem:** Garantir o sigilo absoluto na atribuição das equipas de arbitragem antes de serem publicadas oficialmente, para mitigar o risco de manipulação de resultados ou suborno de árbitros.
  - **Transparência Logística:** Fornecer relatórios de custos e reservas corretos à FIFA (o cliente fictício) sem adulteração de dados.

### 3. Produto (Product)
* **Princípio:** Garantir que o produto atinge os padrões profissionais mais elevados possíveis.
* **Aplicação no Projeto:**
  - **Qualidade do Software:** Escrever código limpo, coeso, com baixo acoplamento e devidamente documentado.
  - **Testabilidade:** Garantir que o sistema é testável (daí a importância dos testes unitários) e que as exceções são tratadas de forma a que o programa nunca falhe de forma inesperada.
