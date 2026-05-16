# SKILL: Gerador de Relatório — Fase 1
**Projeto:** Sistema de Gestão do Campeonato do Mundo de Futebol 2026  
**Disciplina:** Engenharia de Software — Politécnico de Leiria  
**Metodologia:** ICONIX  

---

## ⚠️ Leitura obrigatória antes de começar

O enunciado oficial do professor (`Projeto_ES_2526.pdf`) está **propositadamente incompleto**. O próprio enunciado indica que "a descrição apresentada está muito incompleta" e que compete ao grupo descobrir e propor os requisitos.

**A fonte principal de informação são os ficheiros do grupo**, não o enunciado. Lê TODOS os ficheiros abaixo antes de escrever uma única linha do relatório.

---

## Ficheiros do projeto — lê por esta ordem

### 1. `projeto_worldcup_spec.md` ← **FICHEIRO MAIS IMPORTANTE**

Este é o documento de especificação completo criado pelo grupo. Extrai daqui:

- A secção **"⚠️ O que o Professor disse"** — contém citações diretas do professor com as decisões mais importantes do sistema. Estas têm prioridade máxima.
- A tabela de **Atores do Sistema** com os 5 atores e níveis de acesso
- Os **6 módulos** e todos os casos de uso por módulo (Calendário, Arbitragem, Bilhetes, Alojamento, Jogadores, Sistema/Autenticação)
- A tabela de **Regras de Negócio** com a coluna "Origem" (Professor vs Grupo) — usa esta distinção no relatório
- O **Modelo de Domínio** com todas as entidades e relações
- A secção **Metodologia ICONIX** com a ordem dos entregáveis
- A estrutura do **repositório GitHub**

---

### 2. `mundial2026.jsx` ← **PROTÓTIPO FUNCIONAL**

Este é o código completo do protótipo web desenvolvido em React. Extrai daqui:

- **Todos os módulos implementados** — estão como componentes React individuais: `PaginaLogin`, `Dashboard`, `Calendario`, `Arbitragem`, `LogJogo`, `Jogadores`, `Bilhetes`, `Alojamento`, `Utilizadores`, `PortalPublico`
- **Dados mock** no topo do ficheiro (`JOGOS_INICIAIS`, `ARBITROS_INICIAIS`, `JOGADORES_INICIAIS`, `CATEGORIAS_BILHETES_INICIAIS`, `BILHETES_INICIAIS`, `HOTEIS_INICIAIS`, `UTILIZADORES_SISTEMA_INICIAIS`) — estes revelam as entidades e atributos do domínio
- **Regras de negócio implementadas** — procura funções como `verificar48h()`, `verificarNacionalidade()`, a lógica de estados dos bilhetes, e os alertas de fraude
- **Roles e permissões** — a constante `linksPorRole` na `BarraLateral` mostra exactamente o que cada role pode ver
- **Fluxos de navegação** — o `renderPagina()` no final mostra como as páginas se ligam
- **Casos de uso implementados** — cada botão, modal e função é um caso de uso funcional

---

### 3. `projeto_worldcup_spec.md` (secção de regras de negócio)

Já lido acima, mas presta atenção especial à tabela de regras de negócio. No relatório, documenta cada regra com:
- Se está **implementada no protótipo** (`✅ Implementado`) ou só especificada (`📋 Planeado`)
- A **origem** (Professor / Grupo)

---

### 4. `Projeto_ES_2526.pdf` ← **ENUNCIADO OFICIAL** (incompleto — usa como contexto)

Extrai daqui apenas:
- O **objetivo geral** do projeto (primeira linha — campeonato do mundo)
- Os **requisitos genéricos básicos** mencionados (calendário, árbitros, bilhetes, alojamento)
- A **metodologia obrigatória**: ICONIX, Visual Paradigm, Java, GitHub privado, Moodle
- As **fases de entrega** e critérios de avaliação
- A nota de que o enunciado é incompleto e o grupo deve propor requisitos adicionais

---

## Como gerar o relatório

Com base em tudo o que leste, gera um `.md` com esta estrutura:

---

```markdown
# Relatório de Engenharia de Software — Fase 1
**Projeto:** Sistema de Gestão do Campeonato do Mundo de Futebol 2026  
**Instituição:** Politécnico de Leiria — ESTG  
**Curso:** Licenciatura em Engenharia Informática  
**Disciplina:** Engenharia de Software  
**Grupo:** [preencher]  
**Data:** [preencher]  

---

## 1. Introdução

[Descreve o contexto com base no enunciado + spec do grupo. O sistema é uma 
aplicação de gestão interna para o Mundial 2026. Explica que o enunciado era 
incompleto e que o grupo realizou análise de requisitos com o professor (cliente).]

---

## 2. Levantamento de Requisitos

### 2.1 Metodologia de Levantamento

[Explica que o grupo realizou reuniões com o professor (que desempenha o papel de 
cliente) e foi refinando os requisitos. Menciona as decisões chave que o professor 
tomou — extrai da secção "O que o Professor disse" do spec.md]

### 2.2 Decisões do Cliente (Professor)

[Lista as decisões diretas do professor com as citações presentes no spec.md. 
Estas são as mais importantes para o relatório.]

---

## 3. Atores do Sistema

[Tabela com os 5 atores: Administrador, Gestor de Arbitragem, Gestor de Equipa, 
Árbitro, Cliente (público). Extrai do spec.md e valida com o código do protótipo.]

---

## 4. Requisitos Funcionais

[Extrai TODOS os casos de uso do spec.md organizados por módulo. 
Para cada um: ID (RF01, RF02...), descrição, ator, prioridade, e se está 
implementado no protótipo (✅) ou não (📋).]

| ID | Módulo | Descrição | Ator | Prioridade | Estado |
|----|--------|-----------|------|------------|--------|
| RF01 | Calendário | Criar jogo com data, hora e local | Admin | Alta | ✅ |
...

---

## 5. Requisitos Não Funcionais

[Extrai do código e do spec. Exemplos: roles de acesso, sem notificações de preço, 
transação externa de bilhetes, isolamento de equipa, etc.]

---

## 6. Regras de Negócio

[Extrai a tabela de regras do spec.md. Acrescenta as que encontrares no código 
(verificar48h, verificarNacionalidade, estados do bilhete, etc.). 
Usa o formato: ID | Regra | Módulo | Origem | Estado no protótipo]

---

## 7. Modelo de Domínio

### 7.1 Entidades Identificadas

[Extrai todas as entidades dos dados mock do protótipo E do modelo de domínio 
do spec.md. Para cada entidade lista os atributos encontrados no código.]

### 7.2 Relações entre Entidades

[Descreve as relações com base no spec.md e no código.]

---

## 8. Casos de Uso — Descrição Detalhada

[Para os casos de uso mais importantes (pelo menos 6-8), faz a descrição completa:
- Nome, Ator, Pré-condições, Fluxo principal, Fluxos alternativos, Pós-condições
Extrai os fluxos do código do protótipo onde estão implementados.]

---

## 9. Protótipo

### 9.1 Tecnologia Utilizada

[React (protótipo web). Explica que é um protótipo funcional para validação 
com o cliente antes da implementação final em Java.]

### 9.2 Módulos Implementados

[Lista todos os componentes do protótipo com o que cada um faz. 
Extrai dos nomes dos componentes em mundial2026.jsx]

### 9.3 Funcionalidades Demonstradas

[Lista as funcionalidades que o protótipo já demonstra a funcionar:
validação 48h, alerta de nacionalidade, estados de bilhete, fraude, 
roles e permissões, etc. Extrai do código.]

### 9.4 Decisões de Design

[Documenta as principais decisões: sidebar com roles, modais para formulários, 
portal público separado, dados mock, etc.]

---

## 10. Conclusão e Fase 2

### 10.1 Estado atual — Fase 1

[Resume o que foi entregue: especificação completa de requisitos + protótipo 
funcional web para validação com o cliente.]

### 10.2 Trabalho futuro — Fase 2

[O que falta: implementação em Java, diagramas ICONIX no Visual Paradigm 
(domínio, casos de uso, robustez, sequência, classes), testes.]

---

## Anexos

### Anexo A — Glossário

[Define termos: ICONIX, caso de uso, ator, regra de negócio, UUID, role, etc.]

### Anexo B — Referências

- Enunciado oficial: Projeto_ES_2526.pdf
- Especificação do grupo: projeto_worldcup_spec.md
- Protótipo funcional: mundial2026.jsx
```

---

## Regras de qualidade obrigatórias

1. **Nunca inventar** — só usa o que está nos ficheiros. Se algo não estiver, escreve `[informação em falta — preencher manualmente]`
2. **Português de Portugal** — pt-PT sempre. "Utilizador" não "você". "Efetuar" não "realizar".
3. **IDs em tudo** — RF01, RNF01, RN01, CU01
4. **Citar a origem** — `(Fonte: professor)` ou `(Fonte: grupo)`
5. **✅ / 📋 / ⚠️** — indica o estado de cada requisito/caso de uso no protótipo
6. **Ser exaustivo** — o relatório deve ser longo. Não resumir. Cada módulo merece a sua secção.
7. **O protótipo é a prova** — quando um caso de uso está implementado, menciona o componente React que o implementa

---

## No final, diz ao utilizador

- Quais as secções completas com base nos ficheiros
- Quais precisam de preenchimento manual (nomes, datas, número de grupo)
- Se algum ficheiro estava em falta ou ilegível
