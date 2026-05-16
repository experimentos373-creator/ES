# Especificacao Modular de Casos de Uso - ICONIX (Fase 1)

Este documento apresenta a modelacao de Casos de Uso do **Sistema de Gestao do Campeonato do Mundo 2026**. 
Para garantir a maxima clareza, simplicidade e facilidade de leitura (conforme as boas praticas da metodologia ICONIX e os slides da disciplina), os casos de uso foram divididos em **4 modulos funcionais separados**.

---

## 1. Modulo de Gestao de Jogos e Calendario (Administrador)

**Ator:** Administrador  
**Foco:** Agendamento de novas partidas, geracao de bilhetes padrao e finalizacao de jogos com calculo automatico de classificacoes.

### Diagrama Mermaid
```mermaid
flowchart LR
  Admin["🧍 Administrador"]

  Login(["Autenticar Administrador"])
  CU01(["CU01 - Agendar Novo Jogo"])
  CU01_sub(["Gerar Bilhetes Padrao"])
  CU02(["CU02 - Finalizar Jogo"])
  CU02_sub1(["Registar Eventos (Golos/Cartoes)"])
  CU02_sub2(["Atualizar Classificacoes do Grupo"])

  Admin --- CU01
  Admin --- CU02

  CU01 -.->|Include| Login
  CU02 -.->|Include| Login
  CU01 -.->|Include| CU01_sub
  CU02 -.->|Include| CU02_sub1
  CU02 -.->|Include| CU02_sub2

  style Login fill:#E11D48,stroke:#9F1239,color:#ffffff,stroke-width:2px
  style CU01_sub fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU02_sub1 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU02_sub2 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
```

### Codigo PlantUML (Visual Paradigm)
```plantuml
@startuml
left to right direction
skinparam packageStyle rectangle
skinparam usecase {
  BackgroundColor LightBlue
  BorderColor DarkBlue
  ArrowColor DarkBlue
}

actor "Administrador" as Admin

rectangle "Modulo 1 - Gestao de Jogos" {
  usecase "Autenticar Administrador" as Login #Pink
  usecase "CU01 - Agendar Novo Jogo" as CU01
  usecase "Gerar Bilhetes Padrao" as CU01_sub #Yellow
  usecase "CU02 - Finalizar Jogo" as CU02
  usecase "Registar Eventos (Golos/Cartoes)" as CU02_sub1 #Yellow
  usecase "Atualizar Classificacoes do Grupo" as CU02_sub2 #Yellow
}

Admin -- CU01
Admin -- CU02

CU01 ..> Login : <<include>>
CU02 ..> Login : <<include>>
CU01 ..> CU01_sub : <<include>>
CU02 ..> CU02_sub1 : <<include>>
CU02 ..> CU02_sub2 : <<include>>
@enduml
```

---

## 2. Modulo de Gestao de Arbitragem (Gestor de Arbitragem)

**Ator:** Gestor de Arbitragem  
**Foco:** Atribuicao de equipas de arbitragem com validacao estrita de regras FIFA (descanso de 48h e neutralidade de nacionalidade) e avaliacao de desempenho pos-jogo.

### Diagrama Mermaid
```mermaid
flowchart LR
  GArb["🧍 Gestor de Arbitragem"]

  Login(["Autenticar Gestor"])
  CU03(["CU03 - Atribuir Arbitros a Jogo"])
  CU03_sub1(["Validar Regra 48h"])
  CU03_sub2(["Validar Nacionalidade"])
  CU04(["CU04 - Avaliar Arbitros Pos-Jogo"])

  GArb --- CU03
  GArb --- CU04

  CU03 -.->|Include| Login
  CU04 -.->|Include| Login
  CU03 -.->|Include| CU03_sub1
  CU03 -.->|Include| CU03_sub2

  style Login fill:#E11D48,stroke:#9F1239,color:#ffffff,stroke-width:2px
  style CU03_sub1 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU03_sub2 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
```

### Codigo PlantUML (Visual Paradigm)
```plantuml
@startuml
left to right direction
skinparam packageStyle rectangle
skinparam usecase {
  BackgroundColor LightBlue
  BorderColor DarkBlue
  ArrowColor DarkBlue
}

actor "Gestor de Arbitragem" as GArb

rectangle "Modulo 2 - Arbitragem" {
  usecase "Autenticar Gestor" as Login #Pink
  usecase "CU03 - Atribuir Arbitros a Jogo" as CU03
  usecase "Validar Regra 48h" as CU03_sub1 #Yellow
  usecase "Validar Nacionalidade" as CU03_sub2 #Yellow
  usecase "CU04 - Avaliar Arbitros Pos-Jogo" as CU04
}

GArb -- CU03
GArb -- CU04

CU03 ..> Login : <<include>>
CU04 ..> Login : <<include>>
CU03 ..> CU03_sub1 : <<include>>
CU03 ..> CU03_sub2 : <<include>>
@enduml
```

---

## 3. Modulo de Gestao de Equipas e Planteis (Gestor de Equipa)

**Ator:** Gestor de Equipa (Selecionador)  
**Foco:** Convocatoria de jogadores, garantindo o cumprimento do limite regulamentar de 26 atletas e a selecao dos 11 titulares para cada partida.

### Diagrama Mermaid
```mermaid
flowchart LR
  GEqui["🧍 Gestor de Equipa"]

  Login(["Autenticar Gestor de Equipa"])
  CU05(["CU05 - Gerir Plantel da Selecao"])
  CU05_sub1(["Validar Limite 26 Jogadores"])
  CU05_sub2(["Validar 11 Titulares"])

  GEqui --- CU05

  CU05 -.->|Include| Login
  CU05 -.->|Include| CU05_sub1
  CU05 -.->|Include| CU05_sub2

  style Login fill:#E11D48,stroke:#9F1239,color:#ffffff,stroke-width:2px
  style CU05_sub1 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU05_sub2 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
```

### Codigo PlantUML (Visual Paradigm)
```plantuml
@startuml
left to right direction
skinparam packageStyle rectangle
skinparam usecase {
  BackgroundColor LightBlue
  BorderColor DarkBlue
  ArrowColor DarkBlue
}

actor "Gestor de Equipa" as GEqui

rectangle "Modulo 3 - Gestao de Equipas" {
  usecase "Autenticar Gestor" as Login #Pink
  usecase "CU05 - Gerir Plantel da Selecao" as CU05
  usecase "Validar Limite 26 Jogadores" as CU05_sub1 #Yellow
  usecase "Validar 11 Titulares" as CU05_sub2 #Yellow
}

GEqui -- CU05

CU05 ..> Login : <<include>>
CU05 ..> CU05_sub1 : <<include>>
CU05 ..> CU05_sub2 : <<include>>
@enduml
```

---

## 4. Modulo de Bilheteira, Logistica e Consulta Publica

**Atores:** Gestor de Bilheteira, Gestor de Logistica, Cliente Publico  
**Foco:** Venda e gestao de ingressos, alocacao de centros de estagio/hoteis para as selecoes e acesso publico aos calendarios e tabelas classificativas.

### Diagrama Mermaid
```mermaid
flowchart LR
  GBilh["🧍 Gestor de Bilheteira"]
  GLog["🧍 Gestor de Logistica"]
  Pub["🧍 Cliente Publico"]

  Login(["Autenticar Gestor"])
  CU06(["CU06 - Gerir Lotacao e Precos"])
  CU07(["CU07 - Atribuir Alojamento (Hotel)"])
  CU08(["CU08 - Consultar Calendario e Tabelas"])

  GBilh --- CU06
  GLog --- CU07
  Pub --- CU08

  CU06 -.->|Include| Login
  CU07 -.->|Include| Login

  style Login fill:#E11D48,stroke:#9F1239,color:#ffffff,stroke-width:2px
```

### Codigo PlantUML (Visual Paradigm)
```plantuml
@startuml
left to right direction
skinparam packageStyle rectangle
skinparam usecase {
  BackgroundColor LightBlue
  BorderColor DarkBlue
  ArrowColor DarkBlue
}

actor "Gestor de Bilheteira" as GBilh
actor "Gestor de Logistica" as GLog
actor "Cliente Publico" as Pub

rectangle "Modulo 4 - Bilheteira, Logistica e Publico" {
  usecase "Autenticar Gestor" as Login #Pink
  usecase "CU06 - Gerir Lotacao e Precos" as CU06
  usecase "CU07 - Atribuir Alojamento (Hotel)" as CU07
  usecase "CU08 - Consultar Calendario e Tabelas" as CU08
}

GBilh -- CU06
GLog -- CU07
Pub -- CU08

CU06 ..> Login : <<include>>
CU07 ..> Login : <<include>>
@enduml
```

---

## Resumo Metodologico (Estereotipos)

Conforme ilustrado no slide 23 da disciplina:
1. **`<<Include>>`:** Utilizado para demonstrar que um caso de uso base invoca obrigatoriamente um sub-caso ou validacao de sistema (ex: Autenticacao ou regras de negocio).
2. **Modularidade:** A separacao em 4 diagramas distintos elimina a complexidade visual, permitindo uma analise direta e limpa de cada dominio do sistema.
