# Diagramas de Sequência em Mermaid — Gestão WC 2026

Estes diagramas replicam a estrutura **BCE (Boundary-Control-Entity)** / padrão **ICONIX** exigido para o projeto, em formato **Mermaid** para visualização interativa diretamente no GitHub ou editores compatíveis com Markdown.

> [!NOTE]
> **Convenção de Retornos em Sequência**: Para as partes de **Paulo Gomes** (CU02, CU03, CU23), as setas tracejadas (`-->>`) são suprimidas em retornos normais implícitos de sucesso, sendo desenhadas apenas em caso de exceções/erros ou retornos condicionais. As partes dos restantes membros do grupo mantêm-se inalteradas.

---

### 1. Login & RBAC (Roteamento Dinâmico) - Responsável: Paulo Gomes
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
        B1->>B2: new DashboardController(stage)
        activate B2
        B2->>C: getUtilizadorAtual()
        B2->>E: getCargo()
        E-->>B2: Cargo (ex: ADMIN) (Retorno Condicional)
        B2->>B2: Desenhar Sidebar com permissões do Cargo
        B2-->>U: Exibe Dashboard Customizado
        deactivate B2
    else Email Não Encontrado
        C-->>B1: false (Exceção/Erro)
        B1-->>U: Exibe mensagem "Email não encontrado!" (Erro)
    end
```

---

### 2. CU02 — Agendar Jogo - Responsável: Paulo Gomes
```mermaid
sequenceDiagram
    actor A as Administrador
    participant B as DashboardController (Boundary)
    participant C as CampeonatoManager (Control)
    participant J as Jogo (Entity)

    A->>B: Insere dados e clica "Guardar"
    B->>C: registarJogo(novoJogo)
    activate C
    C->>C: procurarJogoPorId(id)
    C->>C: verificar disponibilidade do estádio
    C->>C: verificar conflito de calendário (itera jogos)
    alt Sem Conflito
        create J
        C->>J: Jogo(...)
        C->>C: saveAll()
        C->>B: exibeSucesso()
        B-->>A: "Jogo agendado com sucesso!"
    else Conflito Detetado
        C-->>B: throw IllegalArgumentException (Erro)
        B-->>A: Exibe mensagem de erro correspondente
    end
    deactivate C
```

---

### 3. CU03 — Finalizar Jogo & Brackets - Responsável: Paulo Gomes
```mermaid
sequenceDiagram
    actor A as Administrador
    participant B as DashboardController (Boundary)
    participant C as CampeonatoManager (Control)
    participant J as Jogo (Entity)

    A->>B: Introduz golos e clica "Confirmar"
    B->>C: finalizarJogoECorrerBracket(jogoId, vencedor, goalsHome, goalsAway, penHome, penAway, stats)
    activate C
    C->>C: procurarJogoPorId(jogoId)
    C->>J: finalizar(winner, goalsHome, goalsAway, penHome, penAway, stats)
    activate J
    J->>J: setStatus(FINALIZADO)
    deactivate J
    
    alt Fase de Grupos
        C->>C: checkAndAdvanceGroupsToOitavos()
    else Fase Eliminatória
        C->>J: getProximoJogo()
        activate J
        J-->>C: proximoJogo (Retorno Condicional)
        deactivate J
        alt proximoJogo != null
            C->>J: setHomeTeam(winner) ou setAwayTeam(winner)
        end
    end
    C->>C: saveAll()
    C->>B: exibeSucesso()
    deactivate C
    B-->>A: Mensagem: "Jogo finalizado! Vencedor: X"
```

---

### 4. CU06 — Escalar Árbitro (Neutralidade e Repouso) - Responsável: Leonardo Mendes
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

### 5. CU19 — Alocar Hotel (Logística) - Responsável: Arthur
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
    C->>H: getCapacidadePessoas()
    activate H
    H-->>C: capacidadePessoas
    deactivate H
    C->>H: getAlojamentos()
    activate H
    H-->>C: alojamentos
    deactivate H
    C->>C: Calcular ocupação atual
    
    alt ocupacao + squadSize > capacidadePessoas
        C-->>B: false (Capacidade Insuficiente)
    else Disponível
        C->>C: isEquipaHospedada(equipa) (Regra de Unicidade)
        alt Já Hospedada
            C-->>B: false (Equipa já alojada)
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

### 6. CU23 — Compra de Bilhete com Regra Anti-Bot - Responsável: Paulo Gomes / Co-Autor: Arthur
```mermaid
sequenceDiagram
    actor P as Público / Adepto
    participant B as DashboardController (Boundary)
    participant C as BilheteiraManager (Control)
    participant J as Jogo (Entity)
    participant E as Estadio (Entity)
    participant S as SetorEstadio (Entity)
    participant Bi as Bilhete (Entity)

    P->>B: Seleciona Jogo, Setor, Quantidade (Q) e clica "Comprar"
    alt Q <= 0 ou Q > 4
        B-->>P: Erro: "Limite máximo de compra de 4 bilhetes..."
    else Q Válido (1 a 4)
        B->>C: venderBilhete(jogo, nomeSetor, Q)
        activate C
        C->>C: validarQtdAntiBot(Q)
        C->>J: getEstadio()
        activate J
        J-->>C: estadio (Retorno Condicional)
        deactivate J
        C->>E: getSetorPorNome(nomeSetor)
        activate E
        E-->>C: setor (Retorno Condicional)
        deactivate E
        C->>S: venderLugares(Q)
        activate S
        alt Lugares < Q
            S-->>C: false (Retorno Condicional/Erro)
            C-->>B: throw IllegalArgumentException (Erro)
            B-->>P: Erro: "Setor esgotado ou lugares insuficientes!"
        else Lugares OK
            deactivate S
            create Bi
            C->>Bi: Bilhete(...)
            C->>C: saveAll()
            C->>B: exibeSucesso()
            B-->>P: Mensagem: "Compra efetuada com sucesso!"
        end
    end
    deactivate C
```
