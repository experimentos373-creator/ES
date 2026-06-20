# Especificacao Modular de Casos de Uso - ICONIX (Fase 1)

Este documento apresenta a modelacao completa e exaustiva de Casos de Uso do **Sistema de Gestao do Campeonato do Mundo 2026**.
Em estrita conformidade com as diretrizes da disciplina (Slide 23) e com 100% de fidelidade aos prototipos e imagens de referencia fornecidas, a modelacao esta dividida em **6 modulos independentes**.

---

## 1. Modulo Administrador (Gestao de Jogos e Sistema)

**Ator:** Administrador  
**Foco:** Agendamento de partidas, geracao de bilhetes padrao, finalizacao de jogos com calculo automatico de classificacoes e gestao de utilizadores.

### Diagrama Mermaid
```mermaid
flowchart LR
  Admin["🧍 Administrador"]

  subgraph Sistema["Sistema de Gestao - World Cup 2026"]
    direction TB
    Login(["Autenticar Administrador"])
    CU01(["Consultar visao geral do torneio"])
    CU02(["Agendar novo jogo"])
    CU02_sub1(["Validar disponibilidade de estadio e datas"])
    CU02_sub2(["Gerar lote de bilhetes padrao"])
    CU03(["Finalizar jogo"])
    CU03_sub1(["Registar eventos (Golos, Cartoes, Substituicoes)"])
    CU03_sub2(["Atualizar classificacoes e estatisticas do grupo"])
    CU04(["Consultar e gerir utilizadores do sistema"])
  end

  Admin --- CU01
  Admin --- CU02
  Admin --- CU03
  Admin --- CU04

  CU01 -.->|Include| Login
  CU02 -.->|Include| Login
  CU03 -.->|Include| Login
  CU04 -.->|Include| Login

  CU02 -.->|Include| CU02_sub1
  CU02 -.->|Include| CU02_sub2
  CU03 -.->|Include| CU03_sub1
  CU03 -.->|Include| CU03_sub2

  style Sistema fill:#E0F2FE,stroke:#0284C7,stroke-width:2px,color:#0F172A
  style Login fill:#E11D48,stroke:#9F1239,color:#FFFFFF,stroke-width:2px
  style CU02_sub1 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU02_sub2 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
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

actor "Administrador" as Admin

rectangle "Modulo 1 - Administrador" {
  usecase "Autenticar Administrador" as Login #Pink
  usecase "Consultar visao geral do torneio" as CU01
  usecase "Agendar novo jogo" as CU02
  usecase "Validar disponibilidade de estadio e datas" as CU02_sub1 #Yellow
  usecase "Gerar lote de bilhetes padrao" as CU02_sub2 #Yellow
  usecase "Finalizar jogo" as CU03
  usecase "Registar eventos (Golos, Cartoes)" as CU03_sub1 #Yellow
  usecase "Atualizar classificacoes do grupo" as CU03_sub2 #Yellow
  usecase "Consultar e gerir utilizadores" as CU04
}

Admin -- CU01
Admin -- CU02
Admin -- CU03
Admin -- CU04

CU01 ..> Login : <<include>>
CU02 ..> Login : <<include>>
CU03 ..> Login : <<include>>
CU04 ..> Login : <<include>>

CU02 ..> CU02_sub1 : <<include>>
CU02 ..> CU02_sub2 : <<include>>
CU03 ..> CU03_sub1 : <<include>>
CU03 ..> CU03_sub2 : <<include>>
@enduml
```

---

## 2. Modulo Gestor de Arbitragem

**Ator:** Gestor de Arbitragem  
**Foco:** Atribuicao de arbitros com validacao estrita de regras FIFA (descanso de 48h e neutralidade de nacionalidade) e avaliacao de desempenho pos-jogo.

### Diagrama Mermaid
```mermaid
flowchart LR
  GArb["🧍 Gestor de Arbitragem"]

  subgraph Sistema["Sistema de Gestao - World Cup 2026"]
    direction TB
    Login(["Autenticar Gestor de Arbitragem"])
    CU05(["Consultar visao geral dos arbitros"])
    CU06(["Atribuir arbitros a um jogo"])
    CU06_sub1(["Validar regra de descanso de 48h"])
    CU06_sub2(["Validar neutralidade de nacionalidade"])
    CU07(["Avaliar desempenho da equipa de arbitragem pos-jogo"])
    CU07_sub(["Calcular e atualizar rating global do arbitro"])
    CU08(["Consultar base de dados dos arbitros"])
  end

  GArb --- CU05
  GArb --- CU06
  GArb --- CU07
  GArb --- CU08

  CU05 -.->|Include| Login
  CU06 -.->|Include| Login
  CU07 -.->|Include| Login
  CU08 -.->|Include| Login

  CU06 -.->|Include| CU06_sub1
  CU06 -.->|Include| CU06_sub2
  CU07 -.->|Include| CU07_sub

  style Sistema fill:#E0F2FE,stroke:#0284C7,stroke-width:2px,color:#0F172A
  style Login fill:#E11D48,stroke:#9F1239,color:#FFFFFF,stroke-width:2px
  style CU06_sub1 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU06_sub2 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU07_sub fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
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
  usecase "Consultar visao geral dos arbitros" as CU05
  usecase "Atribuir arbitros a um jogo" as CU06
  usecase "Validar regra de descanso de 48h" as CU06_sub1 #Yellow
  usecase "Validar neutralidade de nacionalidade" as CU06_sub2 #Yellow
  usecase "Avaliar desempenho pos-jogo" as CU07
  usecase "Calcular e atualizar rating global" as CU07_sub #Yellow
  usecase "Consultar base de dados dos arbitros" as CU08
}

GArb -- CU05
GArb -- CU06
GArb -- CU07
GArb -- CU08

CU05 ..> Login : <<include>>
CU06 ..> Login : <<include>>
CU07 ..> Login : <<include>>
CU08 ..> Login : <<include>>

CU06 ..> CU06_sub1 : <<include>>
CU06 ..> CU06_sub2 : <<include>>
CU07 ..> CU07_sub : <<include>>
@enduml
```

---

## 3. Modulo Gestor de Equipa (Selecionador Nacional)

**Ator:** Gestor de Equipa  
**Foco:** Convocatorias de jogadores com validacao do limite de 26 atletas, escolha dos 11 titulares e consulta de calendario.

### Diagrama Mermaid
```mermaid
flowchart LR
  GEqui["🧍 Gestor de Equipa"]

  subgraph Sistema["Sistema de Gestao - World Cup 2026"]
    direction TB
    Login(["Autenticar Gestor de Equipa"])
    CU09(["Consultar visao geral da equipa"])
    CU10(["Consultar calendario de jogos da equipa"])
    CU11(["Adicionar/remover jogadores a equipa (Convocatoria)"])
    CU11_sub1(["Verificar limite estrito de 26 jogadores"])
    CU11_sub2(["Validar selecao obrigatoria de 11 titulares"])
    CU12(["Confirmar e exportar convocatoria"])
    CU13(["Consultar e exportar ficha tecnica de jogadores"])
  end

  GEqui --- CU09
  GEqui --- CU10
  GEqui --- CU11
  GEqui --- CU12
  GEqui --- CU13

  CU09 -.->|Include| Login
  CU10 -.->|Include| Login
  CU11 -.->|Include| Login
  CU12 -.->|Include| Login
  CU13 -.->|Include| Login

  CU11 -.->|Include| CU11_sub1
  CU11 -.->|Include| CU11_sub2

  style Sistema fill:#E0F2FE,stroke:#0284C7,stroke-width:2px,color:#0F172A
  style Login fill:#E11D48,stroke:#9F1239,color:#FFFFFF,stroke-width:2px
  style CU11_sub1 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU11_sub2 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
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
  usecase "Consultar visao geral da equipa" as CU09
  usecase "Consultar calendario de jogos" as CU10
  usecase "Adicionar/remover jogadores (Convocatoria)" as CU11
  usecase "Verificar limite estrito de 26 jogadores" as CU11_sub1 #Yellow
  usecase "Validar selecao de 11 titulares" as CU11_sub2 #Yellow
  usecase "Confirmar e exportar convocatoria" as CU12
  usecase "Consultar e exportar ficha tecnica" as CU13
}

GEqui -- CU09
GEqui -- CU10
GEqui -- CU11
GEqui -- CU12
GEqui -- CU13

CU09 ..> Login : <<include>>
CU10 ..> Login : <<include>>
CU11 ..> Login : <<include>>
CU12 ..> Login : <<include>>
CU13 ..> Login : <<include>>

CU11 ..> CU11_sub1 : <<include>>
CU11 ..> CU11_sub2 : <<include>>
@enduml
```

---

## 4. Modulo Gestor de Bilheteira

**Ator:** Gestor de Bilheteira  
**Foco:** Controlo financeiro e comercial dos ingressos, definicao de precos por categoria, inventario e monitorizacao de seguranca contra fraudes.

### Diagrama Mermaid
```mermaid
flowchart LR
  GBilh["🧍 Gestor de Bilheteira"]

  subgraph Sistema["Sistema de Gestao - World Cup 2026"]
    direction TB
    Login(["Autenticar Gestor de Bilheteira"])
    CU14(["Consultar visao geral de performance de vendas"])
    CU15(["Gerir precos dos bilhetes por categoria"])
    CU15_sub(["Validar regras de preco minimo/maximo"])
    CU16(["Consultar inventario e lotacao de estadios"])
    CU17(["Consultar alertas de seguranca e suspeitas de fraude"])
    CU17_sub(["Bloquear transacoes suspeitas"])
    CU18(["Consultar e exportar relatorio financeiro de vendas"])
  end

  GBilh --- CU14
  GBilh --- CU15
  GBilh --- CU16
  GBilh --- CU17
  GBilh --- CU18

  CU14 -.->|Include| Login
  CU15 -.->|Include| Login
  CU16 -.->|Include| Login
  CU17 -.->|Include| Login
  CU18 -.->|Include| Login

  CU15 -.->|Include| CU15_sub
  CU17 -.->|Include| CU17_sub

  style Sistema fill:#E0F2FE,stroke:#0284C7,stroke-width:2px,color:#0F172A
  style Login fill:#E11D48,stroke:#9F1239,color:#FFFFFF,stroke-width:2px
  style CU15_sub fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU17_sub fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
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

rectangle "Modulo 4 - Bilheteira" {
  usecase "Autenticar Gestor" as Login #Pink
  usecase "Consultar visao geral de performance" as CU14
  usecase "Gerir precos dos bilhetes" as CU15
  usecase "Validar regras de preco min/max" as CU15_sub #Yellow
  usecase "Consultar inventario e lotacao" as CU16
  usecase "Consultar alertas de seguranca/fraude" as CU17
  usecase "Bloquear transacoes suspeitas" as CU17_sub #Yellow
  usecase "Consultar e exportar relatorio financeiro" as CU18
}

GBilh -- CU14
GBilh -- CU15
GBilh -- CU16
GBilh -- CU17
GBilh -- CU18

CU14 ..> Login : <<include>>
CU15 ..> Login : <<include>>
CU16 ..> Login : <<include>>
CU17 ..> Login : <<include>>
CU18 ..> Login : <<include>>

CU15 ..> CU15_sub : <<include>>
CU17 ..> CU17_sub : <<include>>
@enduml
```

---

## 5. Modulo Gestor de Logistica (Alojamento e Transportes)

**Ator:** Gestor de Logistica  
**Foco:** Alocacao de centros de estagio e hoteis para as selecoes, gestao da frota de autocarros e requisicao de material.

### Diagrama Mermaid
```mermaid
flowchart LR
  GLog["🧍 Gestor de Logistica"]

  subgraph Sistema["Sistema de Gestao - World Cup 2026"]
    direction TB
    Login(["Autenticar Gestor de Logistica"])
    CU19(["Visao geral de alojamento e transportes"])
    CU19_sub1(["Realizar checkout das equipas"])
    CU19_sub2(["Adicionar hotel ao catalogo"])
    CU19_sub3(["Atribuir hotel a equipas"])
    CU19_sub4(["Adicionar autocarros a frota"])
    CU20(["Consultar inventario de material"])
    CU20_sub(["Requisitar material de treino/jogo"])
  end

  GLog --- CU19
  GLog --- CU20

  CU19 -.->|Include| Login
  CU20 -.->|Include| Login

  CU19 -.->|Include| CU19_sub1
  CU19 -.->|Include| CU19_sub2
  CU19 -.->|Include| CU19_sub3
  CU19 -.->|Include| CU19_sub4
  CU20 -.->|Include| CU20_sub

  style Sistema fill:#E0F2FE,stroke:#0284C7,stroke-width:2px,color:#0F172A
  style Login fill:#E11D48,stroke:#9F1239,color:#FFFFFF,stroke-width:2px
  style CU19_sub1 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU19_sub2 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU19_sub3 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU19_sub4 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU20_sub fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
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

actor "Gestor de Logistica" as GLog

rectangle "Modulo 5 - Logistica" {
  usecase "Autenticar Gestor" as Login #Pink
  usecase "Visao geral de alojamento e transportes" as CU19
  usecase "Realizar checkout das equipas" as CU19_sub1 #Yellow
  usecase "Adicionar hotel ao catalogo" as CU19_sub2 #Yellow
  usecase "Atribuir hotel a equipas" as CU19_sub3 #Yellow
  usecase "Adicionar autocarros a frota" as CU19_sub4 #Yellow
  usecase "Consultar inventario de material" as CU20
  usecase "Requisitar material de treino/jogo" as CU20_sub #Yellow
}

GLog -- CU19
GLog -- CU20

CU19 ..> Login : <<include>>
CU20 ..> Login : <<include>>

CU19 ..> CU19_sub1 : <<include>>
CU19 ..> CU19_sub2 : <<include>>
CU19 ..> CU19_sub3 : <<include>>
CU19 ..> CU19_sub4 : <<include>>
CU20 ..> CU20_sub : <<include>>
@enduml
```

---

## 6. Modulo Cliente Publico (Portal do Adepto)

**Ator:** Cliente Publico  
**Foco:** Consulta de calendarios, visualizacao de tabelas classificativas e aquisicao segura de bilhetes.

### Diagrama Mermaid
```mermaid
flowchart LR
  Pub["🧍 Cliente Publico"]

  subgraph Sistema["Sistema de Gestao - World Cup 2026"]
    direction TB
    CU21(["Consultar calendario de jogos e resultados"])
    CU22(["Consultar tabelas classificativas dos grupos"])
    CU23(["Comprar bilhetes para jogos"])
    CU23_sub1(["Autenticar cliente / registar dados"])
    CU23_sub2(["Processar pagamento seguro"])
  end

  Pub --- CU21
  Pub --- CU22
  Pub --- CU23

  CU23 -.->|Include| CU23_sub1
  CU23 -.->|Include| CU23_sub2

  style Sistema fill:#E0F2FE,stroke:#0284C7,stroke-width:2px,color:#0F172A
  style CU23_sub1 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
  style CU23_sub2 fill:#FEF08A,stroke:#CA8A04,color:#854D0E,stroke-width:2px
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

actor "Cliente Publico" as Pub

rectangle "Modulo 6 - Publico" {
  usecase "Consultar calendario de jogos e resultados" as CU21
  usecase "Consultar tabelas classificativas" as CU22
  usecase "Comprar bilhetes para jogos" as CU23
  usecase "Autenticar cliente / registar dados" as CU23_sub1 #Yellow
  usecase "Processar pagamento seguro" as CU23_sub2 #Yellow
}

Pub -- CU21
Pub -- CU22
Pub -- CU23

CU23 ..> CU23_sub1 : <<include>>
CU23 ..> CU23_sub2 : <<include>>
@enduml
```

---

## Resumo da Auditoria e Conformidade ICONIX

1. **Fatoracao de Rotinas Comuns (`<<Include>>`):** Todos os modulos garantem que funcionalidades dependentes (como validacao de limites, regras de 48h, seguranca de transacoes e checkout) sao explicitamente modeladas com o relacionamento de inclusao, respeitando a semantica do UML 2.5 e do Slide 23.
2. **Coesao Modular:** A divisao em 6 diagramas isolados permite analisar cada subsistema de forma limpa, direta e profissional.
