# Especificacao Fiel de Casos de Uso - ICONIX / UML (Fase 1)

Este documento apresenta a modelacao de Casos de Uso do **Sistema de Gestao do Campeonato do Mundo 2026**.
Para garantir **100% de conformidade e exatidao** com as imagens de referencia oficiais do projeto, o sistema esta modelado em **5 diagramas independentes**, representando cada perfil de utilizador e as suas respetivas elipses e relacionamentos `<<Include>>`.

---

## 1. Publico

**Ator:** Publico  
**Descricao:** Acesso externo nao autenticado para consulta de informacoes gerais do torneio.

### Diagrama Mermaid
```mermaid
flowchart LR
  Pub["🧍 Publico"]
  subgraph Sistema["Sistema de Gestao - World Cup 2026"]
    direction TB
    CU1(["Consultar proximos jogos"])
    CU2(["Consultar resultados de jogos"])
    CU3(["Consultar contactos para comprar bilhetes"])
  end
  Pub --- CU1
  Pub --- CU2
  Pub --- CU3

  style Sistema fill:#7DD3FC,stroke:#0284C7,stroke-width:2px,color:#0f172a
  style CU1 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU2 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU3 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
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

actor "Publico" as Pub

rectangle "Sistema de Gestao - World Cup 2026" {
  usecase "Consultar proximos jogos" as CU1
  usecase "Consultar resultados de jogos" as CU2
  usecase "Consultar contactos para comprar bilhetes" as CU3
}

Pub -- CU1
Pub -- CU2
Pub -- CU3
@enduml
```

---

## 2. Administrador

**Ator:** Administrador  
**Descricao:** Perfil de gestao global com controlo total sobre o agendamento de jogos, equipas e visao geral do torneio.

### Diagrama Mermaid
```mermaid
flowchart LR
  Admin["🧍 Administrador"]
  subgraph Sistema["Sistema de Gestao - World Cup 2026"]
    direction TB
    CU1(["Adicionar novo jogo ao calendario"])
    CU2(["Registar nova equipa"])
    CU3(["Consultar visao geral do torneio"])
    CU4(["Acesso aos varios modulos de gestao"])
    CU5(["Finalizar jogo"])
    CU5_sub(["Registar eventos e estatisticas"])
  end
  Admin --- CU1
  Admin --- CU2
  Admin --- CU3
  Admin --- CU4
  Admin --- CU5
  CU5 -.->|Include| CU5_sub

  style Sistema fill:#7DD3FC,stroke:#0284C7,stroke-width:2px,color:#0f172a
  style CU1 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU2 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU3 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU4 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU5 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU5_sub fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
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

rectangle "Sistema de Gestao - World Cup 2026" {
  usecase "Adicionar novo jogo ao calendario" as CU1
  usecase "Registar nova equipa" as CU2
  usecase "Consultar visao geral do torneio" as CU3
  usecase "Acesso aos varios modulos de gestao" as CU4
  usecase "Finalizar jogo" as CU5
  usecase "Registar eventos e estatisticas" as CU5_sub
}

Admin -- CU1
Admin -- CU2
Admin -- CU3
Admin -- CU4
Admin -- CU5
CU5 ..> CU5_sub : <<include>>
@enduml
```

---

## 3. Gestor de Arbitros

**Ator:** Gestor de Arbitros  
**Descricao:** Gestao especializada da equipa de arbitragem, incluindo atribuicao a jogos e avaliacao de desempenho.

### Diagrama Mermaid
```mermaid
flowchart LR
  GArb["🧍 Gestor de Arbitros"]
  subgraph Sistema["Sistema de Gestao - World Cup 2026"]
    direction TB
    CU1(["Consultar visao geral dos arbitros"])
    CU2(["Atribuir arbitros a um jogo"])
    CU2_sub(["Validar nacionalidade e periodo de descanso dos arbitros"])
    CU3(["Avaliar o desempenho da equipa de arbitragem no final de um jogo"])
    CU4(["Consultar base de dados dos arbitros"])
  end
  GArb --- CU1
  GArb --- CU2
  GArb --- CU3
  GArb --- CU4
  CU2 -.->|Include| CU2_sub

  style Sistema fill:#7DD3FC,stroke:#0284C7,stroke-width:2px,color:#0f172a
  style CU1 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU2 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU2_sub fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU3 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU4 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
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

actor "Gestor de Arbitros" as GArb

rectangle "Sistema de Gestao - World Cup 2026" {
  usecase "Consultar visao geral dos arbitros" as CU1
  usecase "Atribuir arbitros a um jogo" as CU2
  usecase "Validar nacionalidade e periodo de descanso dos arbitros" as CU2_sub
  usecase "Avaliar o desempenho da equipa de arbitragem no final de um jogo" as CU3
  usecase "Consultar base de dados dos arbitros" as CU4
}

GArb -- CU1
GArb -- CU2
GArb -- CU3
GArb -- CU4
CU2 ..> CU2_sub : <<include>>
@enduml
```

---

## 4. Gestor de Equipa

**Ator:** Gestor de Equipa  
**Descricao:** Perfil dedicado aos selecionadores nacionais para convocatorias, gestao de planteis e consulta de calendario.

### Diagrama Mermaid
```mermaid
flowchart LR
  GEqui["🧍 Gestor de Equipa"]
  subgraph Sistema["Sistema de Gestao - World Cup 2026"]
    direction TB
    CU1(["Consultar visao geral da equipa"])
    CU2(["Consultar calendario de jogos da equipa"])
    CU3(["Adicionar/remover jogadores a equipa"])
    CU3_sub(["Verificar limite de 26 jogadores e validar 11 titulares"])
    CU4(["Confirmar e exportar convocatoria"])
    CU5(["Consultar e exportar ficha tecnica de jogadores"])
  end
  GEqui --- CU1
  GEqui --- CU2
  GEqui --- CU3
  GEqui --- CU4
  GEqui --- CU5
  CU3 -.->|Include| CU3_sub

  style Sistema fill:#7DD3FC,stroke:#0284C7,stroke-width:2px,color:#0f172a
  style CU1 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU2 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU3 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU3_sub fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU4 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU5 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
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

rectangle "Sistema de Gestao - World Cup 2026" {
  usecase "Consultar visao geral da equipa" as CU1
  usecase "Consultar calendario de jogos da equipa" as CU2
  usecase "Adicionar/remover jogadores a equipa" as CU3
  usecase "Verificar limite de 26 jogadores e validar 11 titulares" as CU3_sub
  usecase "Confirmar e exportar convocatoria" as CU4
  usecase "Consultar e exportar ficha tecnica de jogadores" as CU5
}

GEqui -- CU1
GEqui -- CU2
GEqui -- CU3
GEqui -- CU4
GEqui -- CU5
CU3 ..> CU3_sub : <<include>>
@enduml
```

---

## 5. Gestor de Bilheteira

**Ator:** Gestor de Bilheteira  
**Descricao:** Controlo financeiro, inventario de ingressos, definicao de precos e monitorizacao de seguranca/fraude.

### Diagrama Mermaid
```mermaid
flowchart LR
  GBilh["🧍 Gestor de Bilheteira"]
  subgraph Sistema["Sistema de Gestao - World Cup 2026"]
    direction TB
    CU1(["Consultar visao geral de performance de vendas"])
    CU2(["Gerir precos dos bilhetes"])
    CU3(["Consultar inventario"])
    CU4(["Consultar alertas de seguranca e suspeitas de fraude"])
    CU5(["Consultar relatorio de vendas"])
  end
  GBilh --- CU1
  GBilh --- CU2
  GBilh --- CU3
  GBilh --- CU4
  GBilh --- CU5

  style Sistema fill:#7DD3FC,stroke:#0284C7,stroke-width:2px,color:#0f172a
  style CU1 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU2 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU3 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU4 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
  style CU5 fill:#BAE6FD,stroke:#0369A1,color:#0c4a6e,stroke-width:1px
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

rectangle "Sistema de Gestao - World Cup 2026" {
  usecase "Consultar visao geral de performance de vendas" as CU1
  usecase "Gerir precos dos bilhetes" as CU2
  usecase "Consultar inventario" as CU3
  usecase "Consultar alertas de seguranca e suspeitas de fraude" as CU4
  usecase "Consultar relatorio de vendas" as CU5
}

GBilh -- CU1
GBilh -- CU2
GBilh -- CU3
GBilh -- CU4
GBilh -- CU5
@enduml
```

---

## Confirmacao de Exatidao

Os 5 diagramas documentados acima correspondem com **100% de exatidao e fidelidade** as imagens oficiais do projeto, garantindo o alinhamento total entre a especificacao UML e o prototipo funcional desenvolvido.
