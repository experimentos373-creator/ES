# Diagramas de Sequência em Mermaid — Gestão WC 2026

Estes diagramas replicam a estrutura **BCE (Boundary-Control-Entity)** / padrão **ICONIX** exigido para o projeto, em formato **Mermaid** para visualização interativa diretamente no GitHub ou editores compatíveis com Markdown.

---

### 1. Login & RBAC (Roteamento Dinâmico)
```mermaid
sequenceDiagram
    actor U as Utilizador
    participant B1 as LoginController (Boundary)
    participant B2 as DashboardController (Boundary)
    participant C as AutenticacaoManager (Control)
    participant E as Utilizador (Entity)

    U->>B1: Insere Email e clica em "Entrar"
    B1->>C: autenticar(email)
    loop Procurar Utilizador
        C->>E: getEmail()
    end
    alt Email Encontrado
        C->>C: Definir utilizadorAtual
        C-->>B1: true
        B1->>B2: new DashboardController(stage)
        activate B2
        B2->>C: getUtilizadorAtual()
        C-->>B2: utilizadorLogado
        B2->>E: getCargo()
        E-->>B2: Cargo (ex: ADMIN)
        B2->>B2: Desenhar Sidebar com permissões do Cargo
        B2-->>U: Exibe Dashboard Customizado
        deactivate B2
    else Email Não Encontrado
        C-->>B1: false
        B1-->>U: Exibe mensagem "Email não encontrado!" (Erro)
    end
```

---

### 2. CU02 — Agendar Jogo
```mermaid
sequenceDiagram
    actor A as Administrador
    participant B as DashboardController (Boundary)
    participant C as CampeonatoManager (Control)
    participant E as Jogo (Entity)

    A->>B: Preenche ID, Data, Hora, Estádio, Equipas, Fase e clica em "Agendar Jogo"
    B->>C: procurarJogoPorId(id)
    alt ID Já Existe
        C-->>B: jogoExistente
        B-->>A: Erro: "ID do jogo já existe!"
    else ID Livre
        C-->>B: null
        B->>B: Validar formato da Hora (HH:MM)
        B->>C: registarJogo(novoJogo)
        loop Verificar Conflito de Data
            C->>C: Compara datas e equipas
        end
        alt Conflito Detetado (Equipa já joga no mesmo dia)
            C-->>B: throw IllegalArgumentException
            B-->>A: Erro: "Uma ou ambas as equipas já têm jogo..."
        else Sem Conflito
            C->>C: Adiciona jogo à lista
            C->>C: saveAll() (Persistência)
            C-->>B: Sucesso
            B-->>A: Mensagem: "Jogo agendado com sucesso!"
        end
    end
```

---

### 3. CU03 — Finalizar Jogo & Progresso do Bracket (Corrigido)
```mermaid
sequenceDiagram
    actor A as Administrador
    participant B as DashboardController (Boundary)
    participant C as CampeonatoManager (Control)
    participant J as Jogo (Entity)

    A->>B: Introduz golos (e penalties se empate eliminatório) e clica "Finalizar Jogo"
    B->>C: finalizarJogoECorrerBracket(jogoId, vencedor, goalsHome, goalsAway, penHome, penAway, stats)
    activate C
    C->>C: procurarJogoPorId(jogoId)
    C->>J: finalizar(winner, goalsHome, goalsAway, penHome, penAway, stats)
    activate J
    J->>J: setStatus(FINALIZADO)
    deactivate J
    
    alt Fase de Grupos
        C->>C: checkAndAdvanceGroupsToOitavos()
        activate C
        C->>C: calcularClassificacaoGrupo(grupoNome)
        deactivate C
    else Fase Eliminatória
        C->>J: getProximoJogo()
        activate J
        J-->>C: proximoJogo
        deactivate J
        alt proximoJogo != null
            C->>J: setHomeTeam(winner) ou setAwayTeam(winner)
        end
    end
    C->>C: saveAll() (Persistência)
    C-->>B: Sucesso
    deactivate C
    B-->>A: Mensagem: "Jogo finalizado! Vencedor: X"
```

---

### 4. CU06 — Escalar Árbitro (Neutralidade e Repouso)
```mermaid
sequenceDiagram
    actor GA as Gestor de Arbitragem
    participant B as DashboardController (Boundary)
    participant C as ArbitragemManager (Control)
    participant CM as CampeonatoManager (Control)
    participant E as Arbitro (Entity)

    GA->>B: Seleciona Jogo, Árbitro, Função e clica em "Escalar Árbitro"
    B->>C: escalarArbitro(jogo, arbitro, tipo)
    activate C
    C->>C: isArbitroElegivel(jogo, arbitro)
    
    Note over C: Regra 1: Neutralidade
    C->>E: getNacionalidade()
    alt Nacionalidade coincide com Equipa Casa ou Fora
        C-->>B: false (Inelegível)
    else Neutro
        Note over C: Regra 2: Repouso (48 Horas)
        C->>CM: getJogos()
        CM-->>C: listaJogos
        loop Para cada jogo com este árbitro
            C->>C: Compara datas/horas
        end
        alt Menos de 48h de diferença
            C-->>B: false (Inelegível)
        else Elegível
            C->>E: Atualizar Escala
            C->>CM: registarJogo(jogo) (Atualiza jogo com escala)
            C->>C: saveAll() (Persistência)
            C-->>B: true (Sucesso)
            deactivate C
            B-->>GA: Mensagem: "Árbitro escalado com sucesso!"
        end
    end
```

---

### 5. CU19 — Alocar Hotel (Logística)
```mermaid
sequenceDiagram
    actor GL as Gestor de Logística
    participant B as DashboardController (Boundary)
    participant C as LogisticaManager (Control)
    participant Eq as Equipa (Entity)
    participant H as Hotel (Entity)

    GL->>B: Seleciona Equipa, Hotel e clica em "Confirmar Alojamento"
    B->>C: alocarHotel(equipa, hotel, checkIn, checkOut)
    activate C
    C->>Eq: getJogadores()
    activate Eq
    Eq-->>C: jogadores (squadSize)
    deactivate Eq
    C->>H: getCapacidadeQuartos()
    activate H
    H-->>C: capacidadeQuartos
    deactivate H
    
    alt squadSize > capacidadeQuartos
        C-->>B: false (Capacidade Insuficiente)
    else squadSize <= capacidadeQuartos
        C->>H: getEquipaHospedada()
        activate H
        H-->>C: equipaHospedada
        deactivate H
        alt equipaHospedada != null && !equipaHospedada.equals(equipa)
            C-->>B: false (Hotel Ocupado)
        else Elegível
            C->>H: checkIn(equipa, checkIn, checkOut)
            activate H
            H-->>C: true
            deactivate H
            C->>C: registarHotel(hotel)
            activate C
            C->>C: saveAll() (Gravar hoteis.ser)
            deactivate C
            C-->>B: true (Sucesso)
        end
    end
    deactivate C
    alt Sucesso
        B-->>GL: Mensagem: "Hotel alocado com sucesso."
    else Falha
        B-->>GL: Erro: "Falha na alocação (Capacidade/Ocupação)."
    end
```

---

### 6. CU23 — Compra de Bilhete com Regra Anti-Bot
```mermaid
sequenceDiagram
    actor P as Público / Adepto
    participant B as DashboardController (Boundary)
    participant C as BilheteiraManager (Control)
    participant CM as CampeonatoManager (Control)
    participant J as Jogo (Entity)
    participant E as Estadio (Entity)
    participant S as SetorEstadio (Entity)

    P->>B: Seleciona Jogo, Setor, Quantidade (Q) e clica "Comprar Bilhete"
    
    Note over B,C: Regra de Ética: Limite Anti-Bot (1 a 4)
    alt Q <= 0 ou Q > 4
        B-->>P: Erro: "Limite máximo de compra de 4 bilhetes..."
    else Q Válido (1 a 4)
        B->>C: venderBilhete(jogo, nomeSetor, Q)
        activate C
        C->>J: getEstadio()
        J-->>C: estadio
        C->>E: getSetorPorNome(nomeSetor)
        E-->>C: setor
        
        C->>S: venderBilhete(Q) (Valida capacidade restante)
        activate S
        alt Lugares Disponíveis >= Q
            S->>S: decrementa lugares disponíveis
            S-->>C: true
            deactivate S
            C->>CM: registarJogo(jogo) (Atualiza lotação)
            C->>C: regista novos Bilhete(s) na lista
            C->>C: saveAll() (Persistência)
            C-->>B: true
            B-->>P: Mensagem: "Compra efetuada com sucesso!"
        else Lotação Esgotada
            S-->>C: false
            deactivate S
            C-->>B: false
            deactivate C
            B-->>P: Erro: "Compra falhou. Capacidade excedida."
        end
    end
```
