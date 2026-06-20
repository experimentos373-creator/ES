# Diagramas de Sequência BCE em Mermaid (Visualização Live)
## Engenharia de Software – Projeto (Fase 2)

Este documento contém a representação visual dos diagramas de sequência no formato **Mermaid**. O GitHub e visualizadores de Markdown compatíveis renderizam estes blocos diretamente como diagramas interativos.

Estes diagramas seguem a arquitetura **BCE (Boundary-Control-Entity)** do padrão **ICONIX**:
* **Boundary**: Controladores de interface (`DashboardController`).
* **Control**: Managers de lógica (`CampeonatoManager`, `ArbitragemManager`, `LogisticaManager`, `BilheteiraManager`).
* **Entity**: Entidades de dados (`Jogo`, `Arbitro`, `Equipa`, `Hotel`, `SetorEstadio`, `Bilhete`).

---

### 1. CU02 — Agendar Jogo
```mermaid
sequenceDiagram
    actor A as Administrador
    participant B as DashboardController (Boundary)
    participant C as CampeonatoManager (Control)
    participant E as Jogo (Entity)

    A->>B: Preenche dados e clica "Agendar Jogo"
    B->>C: procurarJogoPorId(id)
    alt ID ja registado
        C-->>B: jogoExistente
        B-->>A: Erro: "ID do jogo ja existe!"
    else ID livre
        C-->>B: null
        B->>B: Validar formato hora (HH:MM)
        B->>C: registarJogo(novoJogo)
        activate C
        loop Verificar Conflitos de Calendario
            C->>C: Compara data/hora e equipas
        end
        alt Conflito Detetado
            C-->>B: throw IllegalArgumentException
            B-->>A: Exibe Erro de Conflito de Data
        else Sem Conflito
            C->>C: Adiciona jogo ao calendario
            C->>C: saveAll() (Gravar ficheiros .ser)
            C-->>B: Sucesso
            deactivate C
            B-->>A: "Jogo agendado com sucesso!"
        end
    end
```

---

### 2. CU03 — Finalizar Jogo (Corrigido)
```mermaid
sequenceDiagram
    actor A as Administrador
    participant B as DashboardController (Boundary)
    participant C as CampeonatoManager (Control)
    participant J as Jogo (Entity)

    A->>B: Introduz golos/penalties e clica "Finalizar Jogo"
    B->>C: finalizarJogoECorrerBracket(jogoId, vencedor, gh, ga, ph, pa, stats)
    activate C
    C->>C: procurarJogoPorId(jogoId)
    C->>J: finalizar(winner, gh, ga, ph, pa, stats)
    activate J
    J->>J: setStatus(FINALIZADO)
    deactivate J
    alt Fase de Grupos
        C->>C: checkAndAdvanceGroupsToOitavos()
        activate C
        C->>C: calcularClassificacaoGrupo(grupoNome)
        deactivate C
    else Fase Eliminatoria
        C->>J: getProximoJogo()
        activate J
        J-->>C: proximoJogo
        deactivate J
        alt proximoJogo != null
            C->>J: setHomeTeam(winner) / setAwayTeam(winner)
        end
    end
    C->>C: saveAll() (Gravar jogos.ser)
    C-->>B: Sucesso
    deactivate C
    B-->>A: "Jogo finalizado! Vencedor: X"
```

---

### 3. CU06 — Escalar Árbitro
```mermaid
sequenceDiagram
    actor GA as Gestor de Arbitragem
    participant B as DashboardController (Boundary)
    participant C as ArbitragemManager (Control)
    participant CM as CampeonatoManager (Control)
    participant E as Arbitro (Entity)

    GA->>B: Seleciona Jogo, Arbitro e Funcao
    B->>C: escalarArbitro(jogo, arbitro, tipo)
    activate C
    C->>C: isArbitroElegivel(jogo, arbitro)
    Note over C: Valida Regra 1: Neutralidade
    C->>E: getNacionalidade()
    alt Nacionalidade coincide com Equipa Casa ou Fora
        C-->>B: false
    else Neutro
        Note over C: Valida Regra 2: Repouso (48 Horas)
        C->>CM: getJogos()
        CM-->>C: listaJogos
        loop Para cada jogo do arbitro
            C->>C: Compara data/hora
        end
        alt Intervalo < 48 horas
            C-->>B: false
        else Elegível
            C->>E: Atualizar Escala
            C->>CM: registarJogo(jogo)
            C->>C: saveAll() (Gravar arbitros.ser)
            C-->>B: true
            deactivate C
            B-->>GA: "Arbitro escalado com sucesso!"
        end
    end
```

---

### 4. CU19 — Alocar Hotel
```mermaid
sequenceDiagram
    actor GL as Gestor de Logistica
    participant B as DashboardController (Boundary)
    participant C as LogisticaManager (Control)
    participant Eq as Equipa (Entity)
    participant H as Hotel (Entity)

    GL->>B: Seleciona Equipa, Hotel e clica em "Confirmar Alojamento"
    B->>C: alocarHotel(equipa, hotel, checkIn, checkOut)
    activate C
    C->>Eq: getJogadores()
    activate Eq
    Eq-->>C: jogadores
    deactivate Eq
    C->>H: getCapacidadeQuartos()
    activate H
    H-->>C: capacidadeQuartos
    deactivate H
    alt squadSize > capacidadeQuartos
        C-->>B: false
    else squadSize <= capacidadeQuartos
        C->>H: getEquipaHospedada()
        activate H
        H-->>C: equipaHospedada
        deactivate H
        alt equipaHospedada != null && !equipaHospedada.equals(equipa)
            C-->>B: false
        else Elegível
            C->>H: checkIn(equipa, checkIn, checkOut)
            activate H
            H-->>C: true
            deactivate H
            C->>C: registarHotel(hotel)
            activate C
            C->>C: saveAll() (Gravar hoteis.ser)
            deactivate C
            C-->>B: true
        end
    end
    deactivate C
    alt Sucesso
        B-->>GL: "Hotel alocado com sucesso."
    else Falha
        B-->>GL: "Falha na alocacao (Capacidade/Ocupacao)."
    end
```

---

### 5. CU23 — Vender Bilhetes
```mermaid
sequenceDiagram
    actor P as Publico / Adepto
    participant B as DashboardController (Boundary)
    participant C as BilheteiraManager (Control)
    participant CM as CampeonatoManager (Control)
    participant J as Jogo (Entity)
    participant E as Estadio (Entity)
    participant S as SetorEstadio (Entity)

    P->>B: Seleciona Jogo, Setor, Qtd (Q) e clica "Comprar"
    alt Q <= 0 ou Q > 4
        B-->>P: Erro: "Limite maximo de compra de 4 bilhetes..."
    else Q Valido
        B->>C: venderBilhete(jogo, nomeSetor, Q)
        activate C
        C->>J: getEstadio()
        J-->>C: estadio
        C->>E: getSetorPorNome(nomeSetor)
        E-->>C: setor
        C->>S: venderBilhete(Q)
        activate S
        alt Lugares Disponiveis >= Q
            S->>S: decrementa lugares
            S-->>C: true
            deactivate S
            C->>CM: registarJogo(jogo)
            C->>C: Adiciona bilhetes a lista bilhetes
            C->>C: saveAll() (Gravar bilhetes.ser)
            C-->>B: true
        else Esgotado
            S-->>C: false
            C-->>B: false
            deactivate C
            B-->>P: Erro: "Capacidade excedida!"
        end
    end
```
