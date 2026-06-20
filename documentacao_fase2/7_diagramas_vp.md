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

## 🎨 3. Diagramas de Sequência (Sequence Diagrams - BCE/ICONIX)

Estes diagramas seguem a arquitetura **BCE (Boundary-Control-Entity)** / padrão **ICONIX** exigido para a documentação académica e de modelação (VP):
- **Boundary**: Ecrãs e Controladores JavaFX (`DashboardController`).
- **Control**: Controladores de Lógica e Managers (`CampeonatoManager`, `ArbitragemManager`, `LogisticaManager`, `BilheteiraManager`).
- **Entity**: Classes de Domínio e Dados (`Jogo`, `Arbitro`, `Equipa`, `Hotel`, `SetorEstadio`, `Bilhete`).

### A. CU02 — Agendar Jogo
```plantuml
@startuml
actor "Administrador" as A
boundary "DashboardController" as B
control "CampeonatoManager" as C
entity "Jogo" as E

A -> B: Preenche dados e clica "Agendar Jogo"
B -> C: procurarJogoPorId(id)
alt ID ja registado
    C --> B: jogoExistente
    B --> A: Erro: "ID do jogo ja existe!"
else ID livre
    C --> B: null
    B -> B: Validar formato hora (HH:MM)
    B -> C: registarJogo(novoJogo)
    activate C
    loop Verificar Conflitos de Calendario
        C -> C: Compara data/hora e equipas
    end
    alt Conflito Detetado
        C --> B: throw IllegalArgumentException
        B --> A: Exibe Erro de Conflito de Data
    else Sem Conflito
        C -> C: Adiciona jogo ao calendario
        C -> C: saveAll() (Gravar ficheiros .ser)
        C --> B: Sucesso
        deactivate C
        B --> A: "Jogo agendado com sucesso!"
    end
end
@endum
```

### B. CU03 — Finalizar Jogo (Corrigido)
```plantuml
@startuml
actor "Administrador" as A
boundary "DashboardController" as B
control "CampeonatoManager" as C
entity "Jogo" as J

A -> B: Introduz golos/penalties e clica "Finalizar Jogo"
B -> C: finalizarJogoECorrerBracket(jogoId, vencedor, gh, ga, ph, pa, stats)
activate C
C -> C: procurarJogoPorId(jogoId)
C -> J: finalizar(winner, gh, ga, ph, pa, stats)
activate J
J -> J: setStatus(FINALIZADO)
deactivate J
alt Fase de Grupos
    C -> C: checkAndAdvanceGroupsToOitavos()
    activate C
    C -> C: calcularClassificacaoGrupo(grupoNome)
    deactivate C
else Fase Eliminatoria
    C -> J: getProximoJogo()
    activate J
    J --> C: proximoJogo
    deactivate J
    alt proximoJogo != null
        C -> J: setHomeTeam(winner) / setAwayTeam(winner)
    end
end
C -> C: saveAll() (Gravar jogos.ser)
C --> B: Sucesso
deactivate C
B --> A: "Jogo finalizado! Vencedor: X"
@endum
```

### C. CU06 — Escalar Árbitro
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
@endum
```

### D. CU19 — Alocar Hotel
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
C -> H: getCapacidadeQuartos()
activate H
H --> C: capacidadeQuartos
deactivate H
alt squadSize > capacidadeQuartos
    C --> B: false
else squadSize <= capacidadeQuartos
    C -> H: getEquipaHospedada()
    activate H
    H --> C: equipaHospedada
    deactivate H
    alt equipaHospedada != null && !equipaHospedada.equals(equipa)
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
@endum
```

### E. CU23 — Vender Bilhetes
```plantuml
@startuml
actor "Publico / Adepto" as P
boundary "DashboardController" as B
control "BilheteiraManager" as C
control "CampeonatoManager" as CM
entity "Jogo" as J
entity "Estadio" as E
entity "SetorEstadio" as S

P -> B: Seleciona Jogo, Setor, Qtd (Q) e clica "Comprar"
alt Q <= 0 ou Q > 4
    B --> P: Erro: "Limite maximo de compra de 4 bilhetes..."
else Q Valido
    B -> C: venderBilhete(jogo, nomeSetor, Q)
    activate C
    C -> J: getEstadio()
    J --> C: estadio
    C -> E: getSetorPorNome(nomeSetor)
    E --> C: setor
    C -> S: venderBilhete(Q)
    activate S
    alt Lugares Disponiveis >= Q
        S -> S: decrementa lugares
        S --> C: true
        deactivate S
        C -> CM: registarJogo(jogo)
        C -> C: Adiciona bilhetes a lista bilhetes
        C -> C: saveAll() (Gravar bilhetes.ser)
        C --> B: true
    else Esgotado
        S --> C: false
        C --> B: false
        deactivate C
        B --> P: Erro: "Capacidade excedida!"
    end
end
@endum
```

