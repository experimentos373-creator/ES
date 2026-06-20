# Estrutura do Projeto & Checklist Final
## Engenharia de Software – Projeto (Fase 2)

Este documento descreve a estrutura de pacotes recomendada para o código Java e fornece a checklist final oficial e atualizada para a entrega da Fase 2.

---

## 📁 Estrutura de Pacotes Recomendada

Organiza o teu código fonte (`src/`) de acordo com as convenções da arquitetura ICONIX:

```
src/
├── Main.java         ← Ponto de entrada da aplicação (contém o public static void main)
├── domain/           ← Classes de Domínio (Entidades de dados persistentes)
│   ├── Jogo.java
│   ├── Equipa.java
│   ├── Arbitro.java
│   ├── Bilhete.java
│   ├── Estadio.java
│   ├── Hotel.java
│   └── Utilizador.java   ← Representa os Atores (Administrador, Adepto, etc.)
├── manager/          ← Controladores principais / Serviços centrais (Singletons)
│   ├── CampeonatoManager.java   ← Singleton (gere calendário de jogos e seleções)
│   ├── ArbitragemManager.java    ← Singleton (gere árbitros e as restrições de neutralidade)
│   ├── BilheteiraManager.java    ← Singleton (gere a venda de bilhetes)
│   └── LogisticaManager.java    ← Singleton (gere alojamento e deslocações)
├── boundary/         ← Interfaces do Utilizador (Menus de Consola, ecrãs CLI)
│   ├── MenuPrincipal.java        ← Tela inicial para login / escolha de papel
│   ├── MenuAdmin.java            ← Menu restrito com operações administrativas
│   ├── MenuAdepto.java           ← Menu público (compra de bilhetes, consulta)
│   ├── MenuArbitragem.java
│   ├── MenuBilheteira.java
│   └── MenuLogistica.java
└── util/             ← Classes utilitárias auxiliares
    ├── ValidadorDados.java
    └── LeitorInput.java
```

---

## 🔍 Checklist de Entrega Oficial — Fase 2

### 1. Modelação e Casos de Uso (Visual Paradigm)
- [ ] **Casos de Uso Expandidos:** Descrição detalhada de todos os casos de uso principais dos 4 módulos (Arbitragem, Bilhética, Alojamento e Transportes).
- [ ] **Fluxos Alternativos:** Identificação clara dos caminhos normais e dos caminhos alternativos/exceções em cada caso de uso.
- [ ] **Diagrama de Casos de Uso:** Atualizado no Visual Paradigm com todos os atores e associações.

### 2. Análise de Robustez (BCE)
- [ ] **Diagramas BCE:** Um diagrama de robustez desenhado para cada caso de uso relevante.
- [ ] **Identificação de Objetos:** Presença clara de objetos do tipo Boundary (`T`), Control (seta circular) e Entity (círculo com base plana).
- [ ] **Regras de Ligação Respeitadas:**
  - [ ] Atores apenas comunicam com Boundary.
  - [ ] Boundary apenas comunica com Control.
  - [ ] Control comunica com Boundary, Entity e Control.
  - [ ] Entity **nunca** comunica diretamente com Boundary.

### 3. Modelação Dinâmica (Diagramas de Sequência)
- [ ] **Linhas de Vida (Lifelines):** Presentes em todos os elementos.
- [ ] **Focos de Controlo (Focus of Control):** Retângulos de ativação de métodos bem delimitados.
- [ ] **Nota Descritiva Lateral:** Colocada no lado esquerdo do diagrama detalhando o fluxo representado.
- [ ] **Ordem dos Elementos (Esquerda para a Direita):**
  - [ ] `Descrição/Nota` -> `Ator` -> `Objetos Fronteira (Boundary)` -> `Objetos Entidade (Entity)`.

### 4. Modelação Estática (Diagrama de Classes Final)
- [ ] **Correspondência de Métodos:** 100% de sintonia entre as mensagens enviadas nos diagramas de sequência e os métodos expostos nas classes.
- [ ] **Sintaxe Rigorosa dos Atributos:**
  - [ ] `visibilidade nome [cardinalidade]: tipo = valor_por_omissão {restrições}`
- [ ] **Visibilidades UML Indicadas:**
  - [ ] Público (`+`), Privado (`-`), Protegido (`#`) ou Módulo/Package (`~`).
- [ ] **Encapsulamento:** Sem declarações de getters/setters repetitivos sem lógica.
- [ ] **Acoplamento e Coesão:** Avaliar se as classes têm forte coesão interna e baixa dependência mútua.
- [ ] **Singleton:**
  - [ ] Atributo `instance` estático privado.
  - [ ] Construtor privado (`-`).
  - [ ] Método `getInstance()` estático sublinhado no diagrama.

### 5. Código Java & Testes Unitários
- [ ] **Compilação Sem Erros:** Código compila totalmente na versão de Java especificada.
- [ ] **Coerência com Diagramas:** Classes e assinaturas de métodos no código correspondem exatamente ao Diagrama de Classes.
- [ ] **Testes Unitários:** JUnit implementados para a lógica de negócio (sem testar menus ou UI).
- [ ] **Quota Individual:** Mínimo de **três testes de requisitos por cada elemento do grupo**.

### 6. Ética Profissional (ACM/IEEE)
- [ ] **Demonstração Prática:** O projeto demonstra preocupação ética aplicada nos três níveis:
  - [ ] *Público:* Segurança de transações e integridade de dados (Bilhética).
  - [ ] *Cliente/Empregador:* Confidencialidade e integridade nas atribuições (Arbitragem).
  - [ ] *Produto:* Código profissional, coeso e limpo.
- [ ] **Documentação:** Presença de notas ou relatório sobre a aplicação dos princípios de ética profissional no sistema.
