# Diagramas UML para Visual Paradigm (Parte 5)
## Engenharia de Software – Projeto (Fase 2)

Este documento fornece a especificação textual (PlantUML / Sereia e Fluxogramas) dos diagramas de estados e atividades exigidos na Parte 5, prontos para serem modelados no **Visual Paradigm**.

---

## 📌 1. Diagramas de Estados (State Machine Diagrams)

### A. Diagrama de Estados para Jogo
Representa o ciclo de vida de uma partida no campeonato.

```plantuml
@startuml
[*] --> AGENDADO : Criar Jogo (agendarJogo)

state AGENDADO {
    note left of AGENDADO : Escala de árbitros é confidencial\npara o público (Retorna null)
}

AGENDADO --> FINALIZADO : finalizarJogoECorrerBracket()
note on link : Valida resultado + penalties\n(se eliminatória) + avança brackets

state FINALIZADO {
    note right of FINALIZADO : Escala de árbitros e resultado\ntornam-se públicos
}

FINALIZADO --> [*]
@endum
```

* **Transições e Eventos:**
  - `Criar Jogo` -> Transita do estado inicial para `AGENDADO`.
  - `finalizarJogoECorrerBracket()` -> Transita de `AGENDADO` para `FINALIZADO`. As regras de desempate e progressão são aplicadas neste evento.

---

### B. Diagrama de Estados para Bilhete (Setor do Estádio)
Representa a disponibilidade de bilhetes por setor para um determinado jogo.

```plantuml
@startuml
[*] --> DISPONIVEL : Inicializar Setor (capacidadeTotal > 0)

state DISPONIVEL {
    note left of DISPONIVEL : bilhetesVendidos < capacidadeTotal
}

DISPONIVEL --> DISPONIVEL : venderBilhete(qtd)\n[bilhetesVendidos + qtd < capacidadeTotal]

DISPONIVEL --> ESGOTADO : venderBilhete(qtd)\n[bilhetesVendidos + qtd == capacidadeTotal]

state ESGOTADO {
    note right of ESGOTADO : bilhetesVendidos == capacidadeTotal\nNovas vendas são rejeitadas
}

ESGOTADO --> DISPONIVEL : reset() / libertarBilhetes()\n[bilhetesVendidos < capacidadeTotal]
@endum
```

---

### C. Diagrama de Estados para Árbitro
Representa o estado de disponibilidade física e regulamentar do árbitro.

```plantuml
@startuml
[*] --> ATIVO : Registar Árbitro (Estado: ATIVO)

state ATIVO {
    note left of ATIVO : Elegível para escalação\n(se cumprir neutralidade e repouso)
}

ATIVO --> DESCANSO : Jogo Concluído / Escala Concluída\n[refereed match]
note on link : Entra em repouso obrigatório de 48h

state DESCANSO {
    note right of DESCANSO : Inelegível para novos jogos\n(diferença de tempo < 48 horas)
}

DESCANSO --> ATIVO : Tempo Decorrido\n[diferença de tempo >= 48 horas]

ATIVO --> INATIVO : Alterar Estado (Estado: INATIVO)
DESCANSO --> INATIVO : Alterar Estado (Estado: INATIVO)
@endum
```

---

## ⚡ 2. Diagramas de Atividades (Activity Diagrams)

### A. Diagrama de Atividades para Compra de Bilhetes
Modela o fluxo com a validação da regra de negócio de lotação e gate de ética (Anti-Bot).

```mermaid
graph TD
    A([Início: Solicitar Bilhetes]) --> B[Introduzir JogoID, Setor e Quantidade]
    B --> C{Quantidade <= 0 OR Quantidade > 4?}
    C -- Sim --> D[Rejeitar Compra: Mensagem de Limite Anti-Bot]
    C -- Não --> E{Setor existe e tem Lotação Disponível?}
    E -- Não --> F[Rejeitar Compra: Capacidade Excedida]
    E -- Sim --> G[Efetuar Venda: Incrementar bilhetesVendidos]
    G --> H[Registrar Instância de Bilhete]
    H --> I[Persistir Coleções de Bilhetes e Jogo]
    I --> J[Exibir Recibo de Sucesso com Total Pago]
    D --> K([Fim])
    F --> K
    J --> K
```

---

### B. Diagrama de Atividades para Escalação de Árbitros
Modela o fluxo de associação de árbitros com verificação de neutralidade e descanso de 48 horas.

```mermaid
graph TD
    A([Início: Escalar Árbitro]) --> B[Especificar Jogo, Árbitro e Tipo de Função]
    B --> C{Existe algum árbitro do tipo elegível no pool?}
    C -- Não --> D[Lançar IllegalStateException]
    C -- Sim --> E{Árbitro tem mesma nacionalidade que Equipa Casa ou Fora?}
    E -- Sim --> F[Retornar Falso: Violação de Neutralidade]
    E -- Não --> G{Árbitro tem outro jogo agendado num intervalo < 48 horas?}
    G -- Sim --> H[Retornar Falso: Violação de Repouso de 48h]
    G -- Não --> I[Adicionar Árbitro ao EscalaoArbitral do Jogo]
    I --> J[Gravar Jogo no CampeonatoManager]
    J --> K[Persistir Coleção de Árbitros e Jogos]
    K --> L[Retornar Verdadeiro: Escala Efetuada]
    D --> M([Fim])
    F --> M
    H --> M
    L --> M
```

---

## 📌 3. Diagramas de Robustez (Robustness Diagrams - BCE/ICONIX) - Paulo Gomes (50%)

Esta secção contém os diagramas de robustez correspondentes aos Casos de Uso sob responsabilidade de **Paulo Gomes**, respeitando as regras estritas da análise BCE (sem conexões diretas Boundary-Boundary ou Entity-Entity):

### A. CU02 — Agendar Jogo (Responsável: Paulo Gomes)
```plantuml
@startuml
skinparam handwritten false
skinparam packageStyle rect
skinparam shadowing false

actor Administrador

boundary "Menu de Navegação" as NavMenu <<Boundary>>
boundary "Formulário de Agendamento" as Form <<Boundary>>
boundary "Mensagem de Confirmação" as MsgConf <<Boundary>>
boundary "Mensagem de Erro" as MsgErro <<Boundary>>

control "CampeonatoManager" as CtrlCamp <<Control>>

entity Jogo <<Entity>>
entity Estadio <<Entity>>
entity Equipa <<Entity>>

Administrador -> NavMenu : 1. Seleciona "Agendar Novo Jogo"
Administrador -> Form : 2. Abre formulário e insere dados
Administrador -> Form : 3. Clicar no botão "Guardar"
Form -> CtrlCamp : 4. Solicita registo de jogo

CtrlCamp -> CtrlCamp : 4.1. procurarJogoPorId(id)
CtrlCamp -> CtrlCamp : 4.2. verificar disponibilidade do estádio (itera jogos)
CtrlCamp -> CtrlCamp : 4.3. verificar conflito de calendário (itera jogos)

alt 5. [Sem Conflitos]
    CtrlCamp -> Jogo : 5.1. Cria e regista novo Jogo
    CtrlCamp -> MsgConf : 5.2. Apresenta mensagem de sucesso
else 5. [Conflito de ID ou Calendário]
    CtrlCamp -> MsgErro : 5.1. Apresenta mensagem de erro
end
@enduml
```

### B. CU03 — Finalizar Jogo (Responsável: Paulo Gomes)
```plantuml
@startuml
skinparam handwritten false
skinparam packageStyle rect
skinparam shadowing false

actor Administrador

boundary "Lista de Jogos" as JogoList <<Boundary>>
boundary "Formulário de Resultados" as FormRes <<Boundary>>
boundary "Mensagem de Confirmação" as MsgConf <<Boundary>>
boundary "Mensagem de Erro" as MsgErro <<Boundary>>

control "CampeonatoManager" as CtrlCamp <<Control>>

entity Jogo <<Entity>>

Administrador -> JogoList : 1. Seleciona o jogo pretendido
Administrador -> JogoList : 2. Clica no botão "Finalizar Jogo"
Administrador -> FormRes : 3. Abre formulário de resultados e insere dados
Administrador -> FormRes : 4. Clica no botão "Confirmar"
FormRes -> CtrlCamp : 5. Solicita finalização do jogo

CtrlCamp -> CtrlCamp : 5.1. procurarJogoPorId(id)
CtrlCamp -> Jogo : 5.2. finalizar(vencedor, golosCasa, golosFora, ...)
CtrlCamp -> CtrlCamp : 5.3. atualizarClassificacoesEBrackets()

alt 6. [Resultado Válido]
    CtrlCamp -> MsgConf : 6.1. Apresenta mensagem de sucesso
else 6. [Resultado Inválido/Empate Eliminatório sem Penalties]
    CtrlCamp -> MsgErro : 6.1. Apresenta mensagem de erro
end
@enduml
```

### C. CU23 — Vender Bilhetes (Responsável: Paulo Gomes / Co-Autor: Arthur)
```plantuml
@startuml
skinparam handwritten false
skinparam packageStyle rect
skinparam shadowing false

actor Adepto

boundary "Portal do Adepto" as Portal <<Boundary>>
boundary "Ecrã de Compra de Bilhetes" as EcrãCompra <<Boundary>>
boundary "Mensagem de Confirmação" as MsgConf <<Boundary>>
boundary "Mensagem de Erro" as MsgErro <<Boundary>>

control "BilheteiraManager" as CtrlBilheteira <<Control>>

entity Jogo <<Entity>>
entity Estadio <<Entity>>
entity SetorEstadio <<Entity>>
entity Bilhete <<Entity>>

Adepto -> Portal : 1. Seleciona jogo pretendido na lista
Portal -> CtrlBilheteira : 2. Solicita carregamento do ecrã de compra
CtrlBilheteira -> EcrãCompra : 3. Apresenta ecrã de compra com dados do jogo
Adepto -> EcrãCompra : 4. Escolhe SetorEstadio, Qtd e clica "Comprar"
EcrãCompra -> CtrlBilheteira : 5. Solicita venda de bilhete(s)

CtrlBilheteira -> CtrlBilheteira : 5.1. validarQtdAntiBot(qtd) (verifica limite de 4)
CtrlBilheteira -> Jogo : 5.2. getEstadio()
CtrlBilheteira -> Estadio : 5.3. getSetorPorNome(nomeSetor)
CtrlBilheteira -> SetorEstadio : 5.4. venderLugares(qtd)

alt 6. [Sucesso]
    CtrlBilheteira -> Bilhete : 6.1. Cria instância(s) de Bilhete
    CtrlBilheteira -> MsgConf : 6.2. Apresenta compra efetuada com sucesso
else 6. [Esgotado ou Qtd Inválida]
    CtrlBilheteira -> MsgErro : 6.2. Apresenta falha na compra
end
@enduml
```

---

## 🎨 4. Diagramas de Sequência (Sequence Diagrams - BCE/ICONIX)

Estes diagramas seguem a arquitetura **BCE (Boundary-Control-Entity)** / padrão **ICONIX** exigido para a documentação académica. Os diagramas associados ao trabalho de **Paulo Gomes** (CU02, CU03 e CU23) foram corrigidos para alinhar as setas de retorno tracejadas apenas a exceções e incluir notas descritivas. Os diagramas de **Leonardo Mendes** (CU06) e **Arthur** (CU19) mantêm-se inalterados para respeitar a divisão de autoria.

### A. CU02 — Agendar Jogo (Responsável: Paulo Gomes)
```plantuml
@startuml
actor "Administrador" as A
boundary "DashboardController" as B
control "CampeonatoManager" as C
entity "Jogo" as J

note left of A
  **CU02: Agendar Novo Jogo**
  1. O Administrador seleciona "Agendar Novo Jogo" no Menu.
  2. O sistema apresenta o "Formulário de Agendamento".
  3. O Administrador insere os dados e clica "Guardar".
  4. O sistema valida dados e conflito de calendário.
  5. O sistema regista o Jogo e apresenta mensagem.
end note

A -> B: 1. Seleciona opção no Menu
A -> B: 2. Insere dados e clica "Guardar"
B -> C: 3. registarJogo(novoJogo)
activate C
C -> C: 3.1. procurarJogoPorId(id)
C -> C: 3.2. verificar disponibilidade do estádio
C -> C: 3.3. verificar conflito de calendário (itera jogos)
alt Sem Conflito
    create J
    C -> J: 4. Jogo(...)
    C -> C: 4.1. saveAll()
    C -> B: 4.2. exibeSucesso()
    B -> A: 5. "Jogo agendado com sucesso!"
else Conflito Detetado
    C --> B: throw IllegalArgumentException
    B -> A: 5. Exibe Mensagem de Erro correspondente
end
deactivate C
@enduml
```

### B. CU03 — Finalizar Jogo (Responsável: Paulo Gomes)
```plantuml
@startuml
actor "Administrador" as A
boundary "DashboardController" as B
control "CampeonatoManager" as C
entity "Jogo" as J

note left of A
  **CU03: Finalizar Jogo**
  1. Administrador seleciona o Jogo na Lista.
  2. Administrador clica "Finalizar Jogo".
  3. O sistema apresenta o "Formulário de Resultados".
  4. Administrador introduz golos e clica "Confirmar".
  5. O sistema valida, altera estado e corre brackets.
end note

A -> B: 1. Seleciona Jogo e clica "Finalizar Jogo"
A -> B: 2. Introduz golos e clica "Confirmar"
B -> C: 3. finalizarJogoECorrerBracket(jogoId, vencedor, ...)
activate C
C -> C: 3.1. procurarJogoPorId(jogoId)
C -> J: 3.2. finalizar(vencedor, golosCasa, golosFora, ...)
activate J
J -> J: setStatus(FINALIZADO)
deactivate J
alt Fase de Grupos
    C -> C: 3.3. checkAndAdvanceGroupsToOitavos()
else Fase Eliminatória
    C -> J: 3.4. getProximoJogo()
    activate J
    J --> C: proximoJogo (Retorno Condicional)
    deactivate J
    alt proximoJogo != null
        C -> J: setHomeTeam(winner) / setAwayTeam(winner)
    end
end
C -> C: 3.5. saveAll()
C -> B: 3.6. exibeSucesso()
B -> A: 4. "Jogo finalizado! Vencedor: X"
deactivate C
@enduml
```

### C. CU06 — Escalar Árbitro (Responsável: Leonardo Mendes)
```plantuml
@startuml
actor "Gestor de Arbitragem" as GA
boundary "DashboardController" as B
control "ArbitragemManager" as C
control "CampeonatoManager" as CM
entity "Arbitro" as E

GA -> B: Seleciona Jogo, Arbitro e Funcao
B -> C: escalarArbitro(jogo, arbitro, tipo)
activate C
C -> C: isArbitroElegivel(jogo, arbitro)
Note over C: Valida Regra 1: Neutralidade
C -> E: getNacionalidade()
alt Nacionalidade coincide com Equipa Casa ou Fora
    C --> B: false
else Neutro
    Note over C: Valida Regra 2: Repouso (48 Horas)
    C -> CM: getJogos()
    CM --> C: listaJogos
    loop Para cada jogo do arbitro
        C -> C: Compara data/hora
    end
    alt Intervalo < 48 horas
        C --> B: false
    else Elegível
        C -> E: Atualizar Escala
        C -> CM: registarJogo(jogo)
        C -> C: saveAll() (Gravar arbitros.ser)
        C --> B: true
        deactivate C
        B --> GA: "Arbitro escalado com sucesso!"
    end
end
@enduml
```

### D. CU19 — Alocar Hotel (Responsável: Arthur)
```plantuml
@startuml
actor "Gestor de Logistica" as GL
boundary "DashboardController" as B
control "LogisticaManager" as C
entity "Equipa" as Eq
entity "Hotel" as H

GL -> B: Seleciona Equipa, Hotel e clica em "Confirmar Alojamento"
B -> C: alocarHotel(equipa, hotel, checkIn, checkOut)
activate C
C -> Eq: getJogadores()
activate Eq
Eq --> C: jogadores
deactivate Eq
C -> H: getCapacidadePessoas()
activate H
H --> C: capacidadePessoas
deactivate H
C -> H: getAlojamentos()
activate H
H --> C: alojamentos
deactivate H
C -> C: Calcular ocupação atual
alt ocupacao + squadSize > capacidadePessoas
    C --> B: false
else Disponível
    C -> C: isEquipaHospedada(equipa)
    alt Já Hospedada
        C --> B: false
    else Elegível
        C -> H: checkIn(equipa, checkIn, checkOut)
        activate H
        H --> C: true
        deactivate H
        C -> C: registarHotel(hotel)
        activate C
        C -> C: saveAll() (Gravar hoteis.ser)
        deactivate C
        C --> B: true
    end
end
deactivate C
alt Sucesso
    B --> GL: "Hotel alocado com sucesso."
else Falha
    B --> GL: "Falha na alocacao (Capacidade/Ocupacao)."
end
@enduml
```

### E. CU23 — Vender Bilhetes (Responsável: Paulo Gomes / Co-Autor: Arthur)
```plantuml
@startuml
actor Adepto
boundary "DashboardController" as B
control "BilheteiraManager" as C
entity "Jogo" as J
entity "Estadio" as E
entity "SetorEstadio" as S
entity "Bilhete" as Bi

note left of Adepto
  **CU23: Comprar Bilhetes para Jogos**
  1. O Adepto seleciona o Jogo no Portal.
  2. O sistema apresenta o Ecrã de Compra.
  3. O Adepto escolhe SetorEstadio, Qtd e clica "Comprar".
  4. O sistema valida quantidade (1 a 4) e lotação.
  5. O sistema desconta lugares, regista a compra e gera bilhetes.
end note

Adepto -> B: 1. Seleciona Jogo, Setor, Qtd (Q) e clica "Comprar"
alt Q <= 0 ou Q > 4
    B -> Adepto: 2. Erro: "Limite anti-bot violado!"
else Q Valido
    B -> C: 3. venderBilhete(jogo, nomeSetor, Q)
    activate C
    C -> C: 3.1. validarQtdAntiBot(Q)
    C -> J: getEstadio()
    activate J
    J --> C: estadio (Retorno Condicional)
    deactivate J
    C -> E: getSetorPorNome(nomeSetor)
    activate E
    E --> C: setor (Retorno Condicional)
    deactivate E
    C -> S: 3.2. venderLugares(Q)
    activate S
    alt Lugares < Q
        S --> C: false (Retorno Condicional/Erro)
        C --> B: throw IllegalArgumentException
        B -> Adepto: 4. Erro: "Setor esgotado ou lugares insuficientes!"
    else Lugares OK
        deactivate S
        create Bi
        C -> Bi: 3.3. Bilhete(...)
        C -> C: saveAll()
        C -> B: 3.4. exibeSucesso()
        B -> Adepto: 4. "Compra efetuada! Bilhetes gerados."
    end
end
deactivate C
@enduml
```
