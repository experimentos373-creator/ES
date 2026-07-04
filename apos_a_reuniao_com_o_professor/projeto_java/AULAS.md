# Resumo de Aulas - Engenharia de Software 2025/2026
Este documento reúne a síntese detalhada e estruturada de todos os conteúdos teóricos e práticos lecionados nas aulas da unidade curricular de Engenharia de Software (ESTG - Politécnico de Leiria).

---

## 📚 Aula 1: Introdução à Engenharia de Software e Ciclos de Vida
### 1. A Crise do Software e a Need de Engenharia
- **Crise do Software (anos 60/70):** O surgimento de computadores mais potentes expôs a incapacidade de produzir software fiável, no prazo e dentro do orçamento.
- **Engenharia de Software (ES):** Disciplina de engenharia focada em todos os aspetos da produção de software de forma sistemática, disciplinada e quantificável.

### 2. Modelos de Ciclo de Vida
* **Modelo em Cascata (Waterfall):** Abordagem linear e sequencial (Requisitos -> Desenho -> Implementação -> Testes -> Manutenção). Dificuldade de lidar com alterações de requisitos após o início.
* **Modelo Espiral:** Foco na análise de risco repetitiva em torno de ciclos incrementais.
* **Rational Unified Process (RUP):** Processo iterativo centrado na arquitetura e guiado por casos de uso. Dividido em 4 fases: Iniciação, Elaboração, Construção e Transição.
* **Processo ICONIX:** Um processo UML simplificado (agile-like) que faz a ponte entre os casos de uso e o código Java em 4 passos práticos, focando-se no desenho preliminar (BCE).

---

## 📚 Aula 2: Engenharia de Requisitos
### 1. O Processo de Engenharia de Requisitos
- **Estudo de Viabilidade:** Avaliar se o sistema atende às necessidades de negócio com custos e prazos viáveis.
- **Elicitação e Análise:** Descobrir os requisitos junto dos stakeholders (entrevistas, brainstorming, prototipagem).
- **Especificação:** Documentar os requisitos num formato legível e formal.
- **Validação:** Confirmar que os requisitos especificam exatamente o que o cliente pretende.

### 2. Classificação de Requisitos
* **Requisitos Funcionais (RF):** Descrevem as funções ou serviços que o sistema deve fornecer (o que o sistema deve fazer). Ex: *"O sistema deve permitir a venda de bilhetes por setor."*
* **Requisitos Não Funcionais (RNF):** Propriedades ou restrições de qualidade do sistema (desempenho, segurança, usabilidade, fiabilidade). Ex: *"A autenticação deve durar menos de 2 segundos."*

---

## 📚 Aula 3: Especificação de Requisitos e Casos de Uso
### 1. Documento de Requisitos (IEEE 830)
- Norma clássica para a elaboração do documento de Especificação de Requisitos de Software (SRS).
- **Histórias de Utilizador (User Stories):** Técnica ágil com o formato: *"Como [ator], eu quero [funcionalidade] para que possa [benefício]."*

### 2. Casos de Uso
- Descrevem a interação entre o utilizador (ator) e o sistema para alcançar um objetivo.
- **Atores:** Entidades externas que interagem com o sistema (humanos ou outros sistemas).
- **Include:** Relação de dependência obrigatória (um caso de uso inclui sempre o outro).
- **Extend:** Relação opcional que ocorre sob condições específicas.

---

## 📚 Aula 4: Modelo de Domínio (Passo 1 ICONIX)
### 1. Conceito e Importância
- Representa as entidades reais do negócio e as suas relações.
- Funciona como um **glossário do projeto** para garantir que a equipa e o cliente usam a mesma terminologia.

### 2. Regras de Construção
- **Fase 1 - Descobrir Classes:** Inspeção gramatical de substantivos no enunciado ou especificação de requisitos.
- **Fase 2 - Definir Relações:** Associações (ligações), Heranças (is-a / é-um), Agregações (has-a / tem-um) e Composições (agregação forte com ciclo de vida dependente).
- **🔴 Regra de Ouro:** **Sem métodos/operações** nesta fase inicial. Apenas nomes de classes, atributos e multiplicidades/cardinalidades.

---

## 📚 Aula 5: Diagramas de Casos de Uso e Classes
### 1. Diagramas de Casos de Uso
- Representação gráfica de atores, casos de uso e os seus relacionamentos.
- **Fronteira do Sistema (Subject):** Caixa retangular que delimita o que está dentro do sistema.

### 2. Diagramas de Classes
- Representa a estrutura estática do sistema.
- **Sintaxe UML de Atributos:** `visibilidade nome [cardinalidade]: tipo = valor_por_omissão {restrições}`
- **Visibilidades:** Público (`+`), Privado (`-`), Protegido (`#`), Pacote (`~`).

---

## 📚 Aula 6: Texto dos Casos de Uso (Passo 2 ICONIX)
### 1. Refinamento dos Casos de Uso
- Passar de uma frase simples para uma descrição detalhada em texto corrido (cenário de utilização).
- Deve descrever as ações do ator e as respostas respetivas do sistema.

### 2. Estrutura Padrão
* **Caminho Principal (Fluxo Básico):** O percurso feliz em que tudo corre conforme esperado.
* **Caminhos Alternativos:** Tratamento de erros, exceções e fluxos secundários (ex: dados incorretos, recursos esgotados).
- **🔴 Erro Comum:** Escrever requisitos funcionais isolados em vez de descrever o cenário narrativo de utilização.

---

## 📚 Aula 7: Análise de Robustez (BCE) e Diagramas de Sequência
### 1. Análise de Robustez (Passo 3 ICONIX)
Ajuda a identificar falhas nos casos de uso ligando-os ao modelo de domínio através de 3 estereótipos:
- **Boundary (Fronteira):** Interfaces (janelas, CLI, menus).
- **Control (Controlo):** Lógica, regras e validações.
- **Entity (Entidade):** Objetos de dados do domínio.

#### Regras de Comunicação BCE:
- Atores só falam com Boundary.
- Boundary só fala com Control.
- Control fala com Boundary, Entity e Control.
- Entity **nunca** fala diretamente com Boundary.

### 2. Diagramas de Sequência (Passo 4 ICONIX)
- Modelação do comportamento dinâmico e passagem de mensagens no tempo.
- **Estrutura no VP:** Nota descritiva à esquerda -> Linha de Vida (Lifelines) -> Foco de Controlo (Focus of Control) representando o tempo de execução do método.

---

## 📚 Aula 8: Metodologias Ágeis
### 1. O Manifesto Ágil
- Indivíduos e interações mais do que processos e ferramentas.
- Software em funcionamento mais do que documentação abrangente.
- Colaboração com o cliente mais do que negociação de contratos.
- Responder à mudança mais do que seguir um plano.

### 2. Framework Scrum
- Papéis: Product Owner (PO), Scrum Master (SM), Equipa de Desenvolvimento.
- Eventos: Sprint Planning, Daily Scrum, Sprint Review, Sprint Retrospective.
- Artefactos: Product Backlog, Sprint Backlog, Incremento.

---

## 📚 Aula 10: Gestão de Projetos de Software
### 1. Funções de Gestão
- Planeamento, organização, monitorização e controlo do projeto de software.
- Estimativas de esforço baseadas em pontos de função ou linhas de código.

### 2. Gestão de Risco
- Identificar riscos (técnicos, humanos, organizacionais), analisar impacto, planear ações preventivas e mitigadoras.

---

## 📚 Aula 11: Planeamento de Projetos de Software
### 1. Atividades de Planeamento
- Divisão do trabalho em tarefas (WBS - Work Breakdown Structure).
- Identificação de dependências entre tarefas (bloqueadores).

### 2. Diagramas de Planeamento
- **Gantt:** Representação temporal das tarefas em barras horizontais.
- **PERT/CPM:** Diagrama de rede que permite identificar o **Caminho Crítico** (sequência de tarefas que determina a duração mínima do projeto).

---

## 📚 Aula 12: Diagramas de Atividades e de Estados (Comportamentais)
### 1. Diagramas de Atividades
- Descrevem o fluxo de controlo/trabalho passo a passo de forma processual.
- **Ações:** Passos individuais.
- **Decisão e Fusão (Decision/Merge):** Ramificação sob condições e junção de caminhos.
- **Bifurcação e Sincronização (Fork/Join):** Fluxos concorrentes/paralelos.
- **Swimlanes (Partições):** Agrupamento de ações por responsabilidade/ator.

### 2. Diagrama de Máquina de Estados
- Descreve os estados por que passa um objeto durante o seu ciclo de vida.
- **Estado (State):** Condição estável.
- **Transição (Transition):** Mudança de estado desencadeada por um evento, opcionalmente com uma condição guarda (`[guard]`) e uma ação.
- **Estados Compostos:** Estados que contêm sub-estados internamente.
