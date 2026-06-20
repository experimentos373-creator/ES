# 8. Diagramas de Classes

Para maximizar a nota e garantir tanto o rigor académico quanto a fidelidade técnica do sistema, a especificação dos Diagramas de Classes foi dividida em duas versões distintas:

1. **Diagrama de Classes do Domínio (Simplificado - Académico)**: Focado estritamente nas entidades de negócio (`domain`), omitindo interfaces de serialização, atributos técnicos (`serialVersionUID`), Managers e utilitários de persistência. Está alinhado com o estilo simplificado dos exercícios práticos apresentados nas aulas teóricas.
2. **Diagrama de Classes de Arquitetura (Completo - Técnico)**: Representa o sistema na sua totalidade, incluindo a infraestrutura de Managers (Singletons), utilitários (`PersistenceUtil`), enums e correspondência 1:1 com o código fonte Java, justificando a arquitetura BCE (Boundary-Control-Entity) / ICONIX.

---

## 8.1 Diagrama de Classes do Domínio (Simplificado)

Este diagrama é o ideal para a entrega principal ao professor, focando-se no domínio do problema e nas relações de negócio pura entre as entidades.

![Diagrama de Classes do Domínio](imagens/diagrama_classes_dominio.png)

### Código PlantUML do Domínio

```plantuml
@startuml
skinparam classAttributeIconSize 0
skinparam packageStyle rectangle
skinparam linetype ortho
skinparam nodesep 70
skinparam ranksep 60

class Jogador {
    - id: int
    - numeroCamisola: int
    - nome: String
    - posicao: String
    - estado: EstadoJogador
    - golos: int
    - assistencias: int
    - titular: boolean
    - cartoesAmarelos: int
    - cartoesVermelhos: int
    - energia: int
    - historicoLesoes: List<String>
    + Jogador(id: int, numeroCamisola: int, nome: String, posicao: String, estado: EstadoJogador)
    + adicionarLesao(lesao: String): void
    + incrementarGolos(): void
    + incrementarAssistencias(): void
}

class Arbitro {
    - id: int
    - email: String
    - nome: String
    - nacionalidade: String
    - tipo: TipoArbitro
    - estado: EstadoArbitro
    - pontuacaoFIFA: int
    - totalAvaliacoes: int
    + Arbitro(id: int, email: String, nome: String, nacionalidade: String, tipo: TipoArbitro, estado: EstadoArbitro)
    + registarAvaliacao(pontuacao: int): void
    + reporPontuacao(): void
}

class Jogo {
    - id: int
    - data: String
    - hora: String
    - estadio: Estadio
    - equipaCasa: Equipa
    - equipaFora: Equipa
    - estado: EstadoJogo
    - fase: String
    - vencedor: Equipa
    - golosCasa: int
    - golosFora: int
    - penaltisCasa: int
    - penaltisFora: int
    - escalaArbitros: EscalaoArbitral
    - eventos: List<EventoJogo>
    - estatisticas: EstatisticaJogo
    - proximoJogo: Jogo
    - posicaoNoProximoJogo: PosicaoBracket
    + Jogo(id: int, data: String, hora: String, estadio: Estadio, equipaCasa: Equipa, equipaFora: Equipa, fase: String, estado: EstadoJogo)
    + obterEscalaArbitrosPublica(): EscalaoArbitral
    + associarEscalaArbitros(escala: EscalaoArbitral): void
    + adicionarEvento(evento: EventoJogo): void
    + finalizar(vencedor: Equipa, golosCasa: int, golosFora: int, penaltisCasa: int, penaltisFora: int, estatisticas: EstatisticaJogo): void
}

class Equipa {
    - nome: String
    - treinador: String
    - jogadores: List<Jogador>
    + Equipa(nome: String, treinador: String)
    + adicionarJogador(jogador: Jogador): boolean
    + removerJogador(jogadorId: int): void
}

class Estadio {
    - nome: String
    - localizacao: String
    - setores: List<SetorEstadio>
    + Estadio(nome: String, localizacao: String)
    + adicionarSetor(setor: SetorEstadio): void
    + obterSetorPorNome(nome: String): SetorEstadio
}

class SetorEstadio {
    - nome: String
    - capacidadeTotal: int
    - bilhetesVendidos: int
    - precoBase: double
    + SetorEstadio(nome: String, capacidadeTotal: int, precoBase: double)
    + venderBilhete(quantidade: int): boolean
}

class Bilhete {
    - jogoId: int
    - setor: String
    - preco: double
    + Bilhete(jogoId: int, setor: String, preco: double)
}

class EventoJogo {
    - minuto: int
    - tipo: TipoEvento
    - jogador: Jogador
    - equipa: Equipa
    + EventoJogo(minuto: int, tipo: TipoEvento, jogador: Jogador, equipa: Equipa)
}

class EstatisticaJogo {
    - posseBolaCasa: int
    - posseBolaFora: int
    - rematesCasa: int
    - rematesFora: int
    - cantosCasa: int
    - cantosFora: int
    + EstatisticaJogo(posseBolaCasa: int, posseBolaFora: int, rematesCasa: int, rematesFora: int, cantosCasa: int, cantosFora: int)
}

class EscalaoArbitral {
    - principal: Arbitro
    - assistente1: Arbitro
    - assistente2: Arbitro
    - quarto: Arbitro
    - var: Arbitro
    + EscalaoArbitral(principal: Arbitro, assistente1: Arbitro, assistente2: Arbitro, quarto: Arbitro, var: Arbitro)
}

class ClassificacaoLinha {
    - equipa: Equipa
    - pontos: int
    - jogados: int
    - vitorias: int
    - empates: int
    - derrotas: int
    - golosMarcados: int
    - golosSofridos: int
    - saldoGolos: int
    + ClassificacaoLinha(equipa: Equipa)
    + adicionarResultado(golosMarcados: int, golosSofridos: int): void
}

class Viagem {
    - origem: String
    - destino: String
    - dataPartida: String
    - dataChegada: String
    - meioTransporte: String
    + Viagem(origem: String, destino: String, dataPartida: String, dataChegada: String, meioTransporte: String)
}

class Hotel {
    - id: int
    - nome: String
    - localizacao: String
    - capacidadeQuartos: int
    - checkInDate: String
    - checkOutDate: String
    - equipaHospedada: Equipa
    + Hotel(id: int, nome: String, localizacao: String, capacidadeQuartos: int)
    + checkIn(equipa: Equipa, checkInDate: String, checkOutDate: String): boolean
    + checkOut(): void
}

class Utilizador {
    - email: String
    - nome: String
    - cargo: TipoUtilizador
    - equipaAssociada: String
    + Utilizador(email: String, nome: String, cargo: TipoUtilizador, equipaAssociada: String)
}

enum TipoUtilizador {
    ADMIN
    GESTOR_ARBITRAGEM
    GESTOR_EQUIPA
    GESTOR_LOGISTICA
    GESTOR_BILHETEIRA
    PUBLICO
}

enum EstadoJogo {
    AGENDADO
    EM_CURSO
    FINALIZADO
}

enum EstadoJogador {
    APTO
    LESIONADO
    SUSPENSO
}

enum EstadoArbitro {
    ATIVO
    DESCANSO
    INATIVO
}

enum TipoArbitro {
    PRINCIPAL
    ASSISTENTE
    VAR
    QUARTO
}

enum TipoEvento {
    GOLO
    AUTO_GOLO
    CARTAO_AMARELO
    CARTAO_VERMELHO
    SUBSTITUICAO
}

enum PosicaoBracket {
    HOME
    AWAY
}

' === Relacionamentos ===
Equipa "1" *-- "0..26" Jogador : jogadores
Estadio "1" *-- "1..*" SetorEstadio : setores
Jogo "0..*" o-- "0..1" Equipa : equipaCasa
Jogo "0..*" o-- "0..1" Equipa : equipaFora
Jogo "0..*" o-- "1" Estadio : estadio
Jogo "1" *-- "0..*" EventoJogo : eventos
Jogo "1" o-- "0..1" EstatisticaJogo : estatisticas
Jogo "1" o-- "0..1" EscalaoArbitral : escalaArbitros
Jogo "0..1" --> "0..1" Jogo : proximoJogo
EscalaoArbitral "1" o-- "1" Arbitro : principal
EscalaoArbitral "1" o-- "0..1" Arbitro : assistente1
EscalaoArbitral "1" o-- "0..1" Arbitro : assistente2
EscalaoArbitral "1" o-- "0..1" Arbitro : quarto
EscalaoArbitral "1" o-- "0..1" Arbitro : var
EventoJogo "0..*" o-- "1" Jogador : jogador
EventoJogo "0..*" o-- "1" Equipa : equipa
Hotel "0..*" o-- "0..1" Equipa : equipaHospedada
ClassificacaoLinha "0..*" o-- "1" Equipa : equipa

' Associacoes adicionais para classes anteriormente isoladas
Bilhete "0..*" ..> Jogo : refere-se a (jogoId)
Utilizador "0..*" ..> Equipa : associado a
Viagem "0..*" ..> Equipa : transporte de
Viagem "0..*" ..> Jogo : para

Utilizador ..> TipoUtilizador
Jogador ..> EstadoJogador
Arbitro ..> EstadoArbitro
Arbitro ..> TipoArbitro
Jogo ..> EstadoJogo
Jogo ..> PosicaoBracket
EventoJogo ..> TipoEvento
@enduml
```

---

## 8.2 Diagrama de Classes de Arquitetura (Completo)

Este diagrama representa fielmente a implementação física do sistema, contendo todas as assinaturas de métodos, atributos privados, enums completos e a arquitetura DCL (Double-Checked Locking) dos Singletons Managers.

![Diagrama de Classes de Arquitetura](imagens/diagrama_classes_arquitetura.png)

### Código PlantUML de Arquitetura
```plantuml
@startuml
set namespaceSeparator ::
skinparam classAttributeIconSize 0
skinparam packageStyle rectangle
skinparam linetype ortho

package "domain" {

    class Jogador implements Serializable {
        - serialVersionUID: long
        - id: int
        - numeroCamisola: int
        - nome: String
        - posicao: String
        - estado: EstadoJogador
        - goals: int
        - assists: int
        - starter: boolean
        - yellowCards: int
        - redCards: int
        - energy: int
        - injuryHistory: List<String>
        + Jogador(int, int, String, String, EstadoJogador)
        + getId(): int
        + getNumeroCamisola(): int
        + getNome(): String
        + getPosicao(): String
        + getEstado(): EstadoJogador
        + setEstado(EstadoJogador): void
        + getGoals(): int
        + getAssists(): int
        + setGoals(int): void
        + setAssists(int): void
        + isStarter(): boolean
        + setStarter(boolean): void
        + getYellowCards(): int
        + setYellowCards(int): void
        + getRedCards(): int
        + setRedCards(int): void
        + getEnergy(): int
        + setEnergy(int): void
        + getInjuryHistory(): List<String>
        + addInjury(String): void
        + incrementGoals(): void
        + incrementAssists(): void
        + equals(Object): boolean
        + hashCode(): int
    }

    class Arbitro implements Serializable {
        - serialVersionUID: long
        - id: int
        - email: String
        - nome: String
        - nacionalidade: String
        - tipo: TipoArbitro
        - estado: EstadoArbitro
        - scoreFIFA: int
        - totalAvaliacoes: int
        + Arbitro(int, String, String, String, TipoArbitro, EstadoArbitro)
        + getId(): int
        + getEmail(): String
        + getNome(): String
        + getNacionalidade(): String
        + getTipo(): TipoArbitro
        + getEstado(): EstadoArbitro
        + setEstado(EstadoArbitro): void
        + getScoreFIFA(): int
        + getTotalAvaliacoes(): int
        + registarAvaliacao(int): void
        + resetScore(): void
        + equals(Object): boolean
        + hashCode(): int
    }

    class Jogo implements Serializable {
        - serialVersionUID: long
        - id: int
        - data: String
        - hora: String
        - estadio: Estadio
        - homeTeam: Equipa
        - awayTeam: Equipa
        - status: StatusJogo
        - phase: String
        - winner: Equipa
        - goalsHome: int
        - goalsAway: int
        - penaltiesHome: int
        - penaltiesAway: int
        - escalaArbitros: EscalaoArbitral
        - eventos: List<EventoJogo>
        - estatisticas: EstatisticaJogo
        - proximoJogo: Jogo
        - posicaoNoProximoJogo: PosicaoBracket
        + Jogo(int, String, String, Estadio, Equipa, Equipa, String, StatusJogo)
        + getId(): int
        + getData(): String
        + getHora(): String
        + getEstadio(): Estadio
        + getHomeTeam(): Equipa
        + setHomeTeam(Equipa): void
        + getAwayTeam(): Equipa
        + setAwayTeam(Equipa): void
        + getStatus(): StatusJogo
        + setStatus(StatusJogo): void
        + getPhase(): String
        + setPhase(String): void
        + getWinner(): Equipa
        + setWinner(Equipa): void
        + getGoalsHome(): int
        + getGoalsAway(): int
        + getPenaltiesHome(): int
        + getPenaltiesAway(): int
        + getEscalaArbitros(): EscalaoArbitral
        + getEscalaArbitrosPublica(): EscalaoArbitral
        + associarEscalaArbitros(EscalaoArbitral): void
        + getEventos(): List<EventoJogo>
        + adicionarEvento(EventoJogo): void
        + getEstatisticas(): EstatisticaJogo
        + setEstatisticas(EstatisticaJogo): void
        + getProximoJogo(): Jogo
        + setProximoJogo(Jogo): void
        + getPosicaoNoProximoJogo(): PosicaoBracket
        + setPosicaoNoProximoJogo(PosicaoBracket): void
        + finalizar(Equipa, int, int, int, int, EstatisticaJogo): void
        + equals(Object): boolean
        + hashCode(): int
    }

    class Equipa implements Serializable {
        - serialVersionUID: long
        - nome: String
        - treinador: String
        - jogadores: List<Jogador>
        + Equipa(String, String)
        + getNome(): String
        + getTreinador(): String
        + getJogadores(): List<Jogador>
        + adicionarJogador(Jogador): boolean
        + removerJogador(int): void
        + equals(Object): boolean
        + hashCode(): int
        + toString(): String
    }

    class Estadio implements Serializable {
        - serialVersionUID: long
        - nome: String
        - localizacao: String
        - setores: List<SetorEstadio>
        + Estadio(String, String)
        + getNome(): String
        + getLocalizacao(): String
        + getSetores(): List<SetorEstadio>
        + adicionarSetor(SetorEstadio): void
        + getSetorPorNome(String): SetorEstadio
        + equals(Object): boolean
        + hashCode(): int
    }

    class SetorEstadio implements Serializable {
        - serialVersionUID: long
        - nome: String
        - capacidadeTotal: int
        - bilhetesVendidos: int
        - precoBase: double
        + SetorEstadio(String, int, double)
        + getNome(): String
        + getCapacidadeTotal(): int
        + getBilhetesVendidos(): int
        + getPrecoBase(): double
        + setCapacidadeTotal(int): void
        + setPrecoBase(double): void
        + venderBilhete(int): boolean
        + equals(Object): boolean
        + hashCode(): int
    }

    class Bilhete implements Serializable {
        - serialVersionUID: long
        - jogoId: int
        - setor: String
        - preco: double
        + Bilhete(int, String, double)
        + getJogoId(): int
        + getSetor(): String
        + getPreco(): double
        + equals(Object): boolean
        + hashCode(): int
    }

    class EventoJogo implements Serializable {
        - serialVersionUID: long
        - minuto: int
        - tipo: TipoEvento
        - jogador: Jogador
        - equipa: Equipa
        + EventoJogo(int, TipoEvento, Jogador, Equipa)
        + getMinuto(): int
        + getTipo(): TipoEvento
        + getJogador(): Jogador
        + getEquipa(): Equipa
    }

    class EstatisticaJogo implements Serializable {
        - serialVersionUID: long
        - posseBolaHome: int
        - posseBolaAway: int
        - rematesHome: int
        - rematesAway: int
        - cantosHome: int
        - cantosAway: int
        + EstatisticaJogo(int, int, int, int, int, int)
        + getPosseBolaHome(): int
        + getPosseBolaAway(): int
        + getRematesHome(): int
        + getRematesAway(): int
        + getCantosHome(): int
        + getCantosAway(): int
    }

    class EscalaoArbitral implements Serializable {
        - serialVersionUID: long
        - principal: Arbitro
        - assistente1: Arbitro
        - assistente2: Arbitro
        - quarto: Arbitro
        - var: Arbitro
        + EscalaoArbitral(Arbitro, Arbitro, Arbitro, Arbitro, Arbitro)
        + getPrincipal(): Arbitro
        + getAssistente1(): Arbitro
        + getAssistente2(): Arbitro
        + getQuarto(): Arbitro
        + getVar(): Arbitro
    }

    class ClassificacaoLinha implements Serializable {
        - equipa: Equipa
        - pontos: int
        - jogados: int
        - vitorias: int
        - empates: int
        - derrotas: int
        - golosMarcados: int
        - golosSofridos: int
        - saldoGolos: int
        + ClassificacaoLinha(Equipa)
        + getEquipa(): Equipa
        + getPontos(): int
        + getJogados(): int
        + getVitorias(): int
        + getEmpates(): int
        + getDerrotas(): int
        + getGolosMarcados(): int
        + getGolosSofridos(): int
        + getSaldoGolos(): int
        + adicionarResultado(int, int): void
    }

    class Viagem implements Serializable {
        - origem: String
        - destino: String
        - dataPartida: String
        - dataChegada: String
        - meioTransporte: String
        + Viagem(String, String, String, String, String)
        + getOrigem(): String
        + getDestino(): String
        + getDataPartida(): String
        + getDataChegada(): String
        + getMeioTransporte(): String
    }

    class Hotel implements Serializable {
        - serialVersionUID: long
        - id: int
        - nome: String
        - localizacao: String
        - capacidadeQuartos: int
        - checkInDate: String
        - checkOutDate: String
        - equipaHospedada: Equipa
        + Hotel(int, String, String, int)
        + getId(): int
        + getNome(): String
        + getLocalizacao(): String
        + getCapacidadeQuartos(): int
        + getCheckInDate(): String
        + getCheckOutDate(): String
        + getEquipaHospedada(): Equipa
        + checkIn(Equipa, String, String): boolean
        + checkOut(): void
        + equals(Object): boolean
        + hashCode(): int
    }

    class Utilizador implements Serializable {
        - serialVersionUID: long
        - email: String
        - nome: String
        - cargo: TipoUtilizador
        - equipaAssociada: String
        + Utilizador(String, String, TipoUtilizador, String)
        + getEmail(): String
        + getNome(): String
        + getCargo(): TipoUtilizador
        + getEquipaAssociada(): String
        + equals(Object): boolean
        + hashCode(): int
    }

    enum TipoUtilizador {
        ADMIN
        GESTOR_ARBITRAGEM
        GESTOR_EQUIPA
        GESTOR_LOGISTICA
        GESTOR_BILHETEIRA
        PUBLICO
    }

    enum StatusJogo {
        AGENDADO
        EM_CURSO
        FINALIZADO
    }

    enum EstadoJogador {
        APTO
        LESIONADO
        SUSPENSO
    }

    enum EstadoArbitro {
        ATIVO
        DESCANSO
        INATIVO
    }

    enum TipoArbitro {
        PRINCIPAL
        ASSISTENTE
        VAR
        QUARTO
    }

    enum TipoEvento {
        GOLO
        AUTO_GOLO
        CARTAO_AMARELO
        CARTAO_VERMELHO
        SUBSTITUICAO
    }

    enum PosicaoBracket {
        HOME
        AWAY
    }
}

package "manager" {

    class CampeonatoManager {
        - instance: volatile CampeonatoManager
        - jogos: List<Jogo>
        - equipas: List<Equipa>
        - estadios: List<Estadio>
        - grupos: Map<String, List<String>>
        - JOGOS_FILE: String
        - EQUIPAS_FILE: String
        - ESTADIOS_FILE: String
        - GRUPOS_FILE: String
        - CampeonatoManager()
        + getInstance(): CampeonatoManager
        + saveAll(): void
        + reset(): void
        + getJogos(): List<Jogo>
        + getEquipas(): List<Equipa>
        + getEstadios(): List<Estadio>
        + registarJogo(Jogo): void
        + registarEquipa(Equipa): void
        + registarEstadio(Estadio): void
        + procurarEquipaPorNome(String): Equipa
        + procurarJogoPorId(int): Jogo
        + getGrupos(): Map<String, List<String>>
        + registarEquipaNoGrupo(String, String): void
        + calcularClassificacaoGrupo(String): List<ClassificacaoLinha>
        + finalizarJogoECorrerBracket(int, Equipa, int, int, int, int, EstatisticaJogo): void
        - carregarGrupos(): void
        - guardarGrupos(): void
        - inicializarGruposPadrao(): void
        - checkAndAdvanceGroupsToOitavos(): void
    }

    class ArbitragemManager {
        - instance: volatile ArbitragemManager
        - arbitros: List<Arbitro>
        - ARBITROS_FILE: String
        - ArbitragemManager()
        + getInstance(): ArbitragemManager
        + saveAll(): void
        + reset(): void
        + getArbitros(): List<Arbitro>
        + registarArbitro(Arbitro): void
        + procurarArbitroPorId(int): Arbitro
        + escalarArbitro(Jogo, Arbitro, TipoArbitro): boolean
        + isArbitroElegivel(Jogo, Arbitro): boolean
        + avaliarDesempenho(Jogo, int, int, int, int, int): void
        - temArbitroEscalado(Jogo, Arbitro): boolean
    }

    class BilheteiraManager {
        - instance: volatile BilheteiraManager
        - bilhetes: List<Bilhete>
        - BILHETES_FILE: String
        - BilheteiraManager()
        + getInstance(): BilheteiraManager
        + saveAll(): void
        + reset(): void
        + getBilhetes(): List<Bilhete>
        + venderBilhete(Jogo, String, int): boolean
    }

    class LogisticaManager {
        - instance: volatile LogisticaManager
        - hoteis: List<Hotel>
        - viagens: List<Viagem>
        - HOTEIS_FILE: String
        - VIAGENS_FILE: String
        - LogisticaManager()
        + getInstance(): LogisticaManager
        + saveAll(): void
        + reset(): void
        + getHoteis(): List<Hotel>
        + getViagens(): List<Viagem>
        + registarHotel(Hotel): void
        + procurarHotelPorId(int): Hotel
        + alocarHotel(Equipa, Hotel, String, String): boolean
        + registarCheckout(Hotel): void
        + planearViagem(Jogo, String, String, String, String, String): Viagem
    }

    class AutenticacaoManager {
        - instance: volatile AutenticacaoManager
        - utilizadores: List<Utilizador>
        - utilizadorAtual: Utilizador
        - UTILIZADORES_FILE: String
        - AutenticacaoManager()
        + getInstance(): AutenticacaoManager
        + saveAll(): void
        + reset(): void
        + getUtilizadores(): List<Utilizador>
        + getUtilizadorAtual(): Utilizador
        + isAutenticado(): boolean
        + autenticar(String): boolean
        + logout(): void
        + registarUtilizador(Utilizador): void
        + procurarUtilizadorPorEmail(String): Utilizador
    }
}

package "util" {
    class PersistenceUtil {
        + guardar<T extends Serializable>(String, List<T>): boolean
        + carregar<T extends Serializable>(String): List<T>
    }
}

' === Associações e Relacionamentos ===
CampeonatoManager "1" *-- "0..*" Jogo : gere
CampeonatoManager "1" *-- "0..*" Equipa : gere
CampeonatoManager "1" *-- "0..*" Estadio : gere
CampeonatoManager "1" o-- "0..*" ClassificacaoLinha : calcula

Estadio "1" *-- "1..*" SetorEstadio : contém
Equipa "1" *-- "0..26" Jogador : squad FIFA
Jogo "0..*" o-- "0..1" Equipa : homeTeam
Jogo "0..*" o-- "0..1" Equipa : awayTeam
Jogo "0..*" o-- "1" Estadio : realiza-se em
Jogo "1" *-- "0..*" EventoJogo : contém
Jogo "1" o-- "0..1" EstatisticaJogo : estatísticas
Jogo "1" o-- "0..1" EscalaoArbitral : escalado

EscalaoArbitral "1" o-- "1" Arbitro : principal
EscalaoArbitral "1" o-- "0..1" Arbitro : assistente1
EscalaoArbitral "1" o-- "0..1" Arbitro : assistente2
EscalaoArbitral "1" o-- "0..1" Arbitro : quarto
EscalaoArbitral "1" o-- "0..1" Arbitro : var

Jogo "0..1" --> "0..1" Jogo : proximoJogo
EventoJogo "0..*" o-- "1" Jogador : envolve
EventoJogo "0..*" o-- "1" Equipa : envolve
Hotel "0..*" o-- "0..1" Equipa : hospeda
ClassificacaoLinha "0..*" o-- "1" Equipa : classifica

BilheteiraManager "1" *-- "0..*" Bilhete : gere
ArbitragemManager "1" *-- "0..*" Arbitro : gere
LogisticaManager "1" *-- "0..*" Hotel : gere
LogisticaManager "1" *-- "0..*" Viagem : gere
AutenticacaoManager "1" *-- "0..*" Utilizador : gere

CampeonatoManager ..> PersistenceUtil : utiliza
ArbitragemManager ..> PersistenceUtil : utiliza
BilheteiraManager ..> PersistenceUtil : utiliza
LogisticaManager ..> PersistenceUtil : utiliza
AutenticacaoManager ..> PersistenceUtil : utiliza

ArbitragemManager ..> CampeonatoManager : getInstance()
BilheteiraManager ..> CampeonatoManager : getInstance()
LogisticaManager ..> CampeonatoManager : getInstance()

Utilizador ..> TipoUtilizador : cargo
Jogador ..> EstadoJogador : estado
Arbitro ..> EstadoArbitro : estado
Arbitro ..> TipoArbitro : tipo
Jogo ..> StatusJogo : status
Jogo ..> PosicaoBracket : posicaoNoProximoJogo
EventoJogo ..> TipoEvento : tipo

@enduml
```

---

## 8.3 Correspondência 1:1 — Verificação por Classe

| Classe | Fonte Java | Validação |
|--------|-----------|-----------|
| **Jogador** | `domain/Jogador.java` | Atributos: `id`, `numeroCamisola`, `nome`, `posicao`, `estado` (enum), `goals`, `assists`, `starter`, `yellowCards`, `redCards`, `energy`, `injuryHistory`. Métodos: todos os getters/setters, `addInjury()`, `incrementGoals()`, `incrementAssists()`. ✅ Sem `minutosJogados`, `recuperarEnergia()`, `updateStats()` — esses não existem no código. |
| **Arbitro** | `domain/Arbitro.java` | `scoreFIFA` é **int** (0-100), não `double`. `totalAvaliacoes` é **int**. Método `registarAvaliacao(int)` converte 1-5 estrelas → 20-100 e faz média ponderada. `resetScore()` zera ambos. ✅ Sem `fifaSum`, `evaluationsCount`, `ultimoJogoTimestamp`, `matchesRefereed` — esses não existem. |
| **Jogo** | `domain/Jogo.java` | `getEscalaArbitrosPublica()` existe e retorna `null` quando `status == AGENDADO`. `finalizar()` aceita `(Equipa vencedor, int gh, int ga, int ph, int pa, EstatisticaJogo stats)`. `proximoJogo` é auto-referência `Jogo`. ✅ Sem `evaluated` — não existe. |
| **Bilhete** | `domain/Bilhete.java` | `jogoId` é **int** (primitivo), não referência a `Jogo`. ✅ |
| **TipoArbitro** | `domain/TipoArbitro.java` | Valor literal: `QUARTO`. ❌ Não é `QUARTO_ARBITRO`. |
| **Estadio** | `domain/Estadio.java` | `localizacao` (String). ❌ Não é `cidade`. |
| **SetorEstadio** | `domain/SetorEstadio.java` | `precoBase` existe. `venderBilhete(int)` verifica capacidade. ✅ |
| **EstatisticaJogo** | `domain/EstatisticaJogo.java` | Campos: `posseBolaHome/Away`, `rematesHome/Away`, `cantosHome/Away`. Apenas getters. ✅ Sem `shots`, `fouls`, `offsides` — não existem. |
| **EscalaoArbitral** | `domain/EscalaoArbitral.java` | 5 árbitros: `principal`, `assistente1`, `assistente2`, `quarto`, `var`. ✅ |
| **Equipa** | `domain/Equipa.java` | `adicionarJogador()` limita a 26 (FIFA). `removerJogador(int jogadorId)`. ✅ |
| **Hotel** | `domain/Hotel.java` | `checkIn(Equipa, String, String)` e `checkOut()`. `equipaHospedada` é `Equipa`. ✅ |
| **CampeonatoManager** | `manager/CampeonatoManager.java` | `saveAll()`, `reset()`, `finalizarJogoECorrerBracket()`, `calcularClassificacaoGrupo()`. ✅ |
| **ArbitragemManager** | `manager/ArbitragemManager.java` | `isArbitroElegivel()`, `escalarArbitro()`, `avaliarDesempenho()`. ✅ |
| **BilheteiraManager** | `manager/BilheteiraManager.java` | `venderBilhete()` com anti-bot [1,4]. ✅ |
| **LogisticaManager** | `manager/LogisticaManager.java` | `alocarHotel()`, `planearViagem()`. ✅ |
| **AutenticacaoManager** | `manager/AutenticacaoManager.java` | `autenticar(String)`, `logout()`, `getUtilizadorAtual()`. ✅ |
| **PersistenceUtil** | `util/PersistenceUtil.java` | `guardar()` e `carregar()` genéricos `<T extends Serializable>`. ✅ |

---

## 8.4 Relacionamentos — Semântica e Multiplicidade

| Relação | Tipo | Multiplicidade | Justificação no Código |
|---------|------|---------------|----------------------|
| `CampeonatoManager` → `Jogo` | **Composição** | 1 → 0..* | Manager é dono; `jogos` criados e persistidos via `registarJogo()`. |
| `CampeonatoManager` → `Equipa` | **Composição** | 1 → 0..* | `equipas` lista interna do Singleton. |
| `CampeonatoManager` → `Estadio` | **Composição** | 1 → 0..* | `estadios` lista interna do Singleton. |
| `Estadio` → `SetorEstadio` | **Composição** | 1 → 1..* | Setor sem estádio não tem identidade própria. `adicionarSetor()` no construtor. |
| `Equipa` → `Jogador` | **Composição** | 1 → 0..26 | Squad FIFA limitado. `adicionarJogador()` valida. |
| `Jogo` → `Equipa` (home/away) | **Agregação** | 0..* → 0..1 | Jogo pode ser criado sem equipas atribuídas; `setHomeTeam()`/`setAwayTeam()`. |
| `Jogo` → `Estadio` | **Agregação** | 0..* → 1 | Estádio obrigatório no construtor. |
| `Jogo` → `EventoJogo` | **Composição** | 1 → 0..* | Evento perde significado fora do jogo. `adicionarEvento()`. |
| `Jogo` → `EstatisticaJogo` | **Agregação** | 1 → 0..1 | Atribuída apenas após finalização. `setEstatisticas()`. |
| `Jogo` → `EscalaoArbitral` | **Agregação** | 1 → 0..1 | Escalação atribuída posteriormente via `associarEscalaArbitros()`. |
| `EscalaoArbitral` → `Arbitro` | **Agregação** | 1 → 0..1/1 | Árbitros existem independentemente; `principal` é obrigatório, restos opcionais. |
| `Jogo` → `Jogo` (proximoJogo) | **Auto-associação** | 0..1 → 0..1 | Bracket: `setProximoJogo()`, `setPosicaoNoProximoJogo()`. |
| `EventoJogo` → `Jogador` / `Equipa` | **Agregação** | 0..* → 1 | Jogador e equipa existem independentemente. |
| `Hotel` → `Equipa` | **Agregação** | 0..* → 0..1 | Hotel pode estar vazio; `checkIn()`/`checkOut()`. |
| `ClassificacaoLinha` → `Equipa` | **Agregação** | 0..4 → 1 | VO calculado em runtime. |
| `BilheteiraManager` → `Bilhete` | **Composição** | 1 → 0..* | Manager é dono da lista. |
| `ArbitragemManager` → `Arbitro` | **Composição** | 1 → 0..* | Manager é dono da lista. |
| `LogisticaManager` → `Hotel` / `Viagem` | **Composição** | 1 → 0..* | Manager é dono das listas. |
| `AutenticacaoManager` → `Utilizador` | **Composição** | 1 → 0..* | Manager é dono da lista. |
| `Utilizador` → `TipoUtilizador` | **Dependência** | 1 → 1 | Enum; atributo `cargo`. |
| `Jogador` → `EstadoJogador` | **Dependência** | 1 → 1 | Enum; atributo `estado`. |
| `Arbitro` → `EstadoArbitro` / `TipoArbitro` | **Dependência** | 1 → 1 | Enums; atributos `estado` e `tipo`. |
| `Jogo` → `StatusJogo` / `PosicaoBracket` | **Dependência** | 1 → 1 | Enums; atributos `status` e `posicaoNoProximoJogo`. |
| `EventoJogo` → `TipoEvento` | **Dependência** | 1 → 1 | Enum; atributo `tipo`. |
| Managers → `PersistenceUtil` | **Dependência** | 1 → 1 | Uso estático de `guardar()`/`carregar()`. |
| `ArbitragemManager` / `BilheteiraManager` / `LogisticaManager` → `CampeonatoManager` | **Dependência** | 1 → 1 | Chamada a `CampeonatoManager.getInstance()` para operações. |

---

## 8.5 Notas de Design (BCE + ICONIX)

### 8.5.1 Packages e Separação de Responsabilidades

| Package | Classes | Papel ICONIX |
|---------|---------|-------------|
| **domain** | Entidades + Enums + VOs | **Entities** (Modelo de Domínio) — Estado e comportamento de negócio. |
| **manager** | 5 Singletons DCL | **Controllers** — Coordenação, regras de negócio, persistência. |
| **util** | `PersistenceUtil` | **Infrastructure** — Genérico de serialização; nunca serializa os próprios Managers. |

### 8.5.2 Padrão Singleton — Double-Checked Locking (DCL)

Todos os managers implementam DCL para thread-safety sem overhead de sincronização em leitura:

```java
private static volatile CampeonatoManager instance = null;
private CampeonatoManager() { ... }  // Carrega .ser no construtor
public static CampeonatoManager getInstance() {
    if (instance == null) {
        synchronized (CampeonatoManager.class) {
            if (instance == null) { instance = new CampeonatoManager(); }
        }
    }
    return instance;
}
```

**Nota:** A persistência serializa **apenas** as listas de entidades (`List<Jogo>`, `List<Arbitro>`, etc.). Os **Managers nunca são serializados**, evitando problemas de Singleton com instâncias duplicadas. A ordem de carregamento: `AutenticacaoManager` → `CampeonatoManager` → restantes (evita dependência circular).

### 8.5.3 Value Objects vs. Entities

| Classe | Tipo | Identidade | Mutabilidade |
|--------|------|-----------|-------------|
| `EstatisticaJogo` | VO | Não tem identidade | Imutável (apenas construtor + getters) |
| `ClassificacaoLinha` | VO | Derivada de `Equipa` | Parcialmente mutável via `adicionarResultado()` |
| `EscalaoArbitral` | VO | Derivada de `Jogo` | Sem setters (construtor define tudo) |
| `Viagem` | VO | Não tem identidade | Imutável |
| `SetorEstadio` | VO | Identidade por `nome` | Mutável (`capacidade`, `preco`) |
| `Jogador`, `Arbitro`, `Equipa`, `Jogo`, `Estadio`, `Hotel`, `Utilizador` | **Entities** | `id`, `email`, `nome` | Com estado mutável |

### 8.5.4 Regras de Negócio Críticas no Diagrama

1. **FIFA Squad Limit**: `Equipa` → `Jogador` multiplicidade `0..26` (regra em `adicionarJogador()`).
2. **Neutralidade de Árbitros**: `ArbitragemManager.isArbitroElegivel()` verifica `nacionalidade` vs. `Equipa.nome`.
3. **Repouso 48h**: Cálculo via `ChronoUnit.HOURS` entre datas de jogos escalados.
4. **Sigilo de Escalação**: `Jogo.getEscalaArbitrosPublica()` retorna `null` se `status == AGENDADO`.
5. **Anti-Bot Bilheteira**: `BilheteiraManager.venderBilhete()` rejeita `quantidade <= 0 || quantidade > 4`.
6. **Auto-Associação Bracket**: `Jogo.proximoJogo` + `posicaoNoProximoJogo` (enum `HOME`/`AWAY`) para progressão automática.
7. **Exclusividade de Hotel**: `Hotel.checkIn()` rejeita se `equipaHospedada != null` e diferente.

---

## 8.6 Como Gerar e Importar os Diagramas em VP Online

1. Copiar o bloco PlantUML respetivo (de Domínio ou Arquitetura) entre `@startuml` e `@enduml`.
2. No Visual Paradigm Online:
   - **Page 6**: Importar o PlantUML do **Domínio** (Simplified).
   - **Page 7**: Criar nova página e importar o PlantUML de **Arquitetura** (Complete).
3. Selecionar **Import PlantUML** → colar o código.
4. Ajustar layout manualmente:
   - No Domínio: Organizar as classes centrais (`Jogo`, `Equipa`, `Jogador`, `Estadio`) no meio, ligando as restantes radialmente.
   - Na Arquitetura: Agrupar `manager` no topo (Controllers), `domain` no centro (Entities), e `util` em baixo (Infrastructure).
5. Exportar cada página respetiva como imagem PNG e guardar na pasta `documentacao_fase2/imagens/`.

---

## 8.7 Checklist de Validação 1:1

- [x] Todos os atributos de `Jogador` correspondem ao `Jogador.java` real (incluindo GUI extras `starter`, `energy`, `yellowCards`, `redCards`, `injuryHistory`).
- [x] `Arbitro.scoreFIFA` é `int` (0-100), não `double`.
- [x] `Arbitro.totalAvaliacoes` é `int`, não `long`.
- [x] `Bilhete.jogoId` é `int` (primitivo), não referência `Jogo`.
- [x] `TipoArbitro.QUARTO` está correto (não `QUARTO_ARBITRO`).
- [x] `Estadio.localizacao` está correto (não `cidade`).
- [x] `SetorEstadio.precoBase` está incluído.
- [x] `EstatisticaJogo` tem apenas `posseBola`, `remates`, `cantos` (não `shots`, `fouls`, `offsides`).
- [x] `EscalaoArbitral` tem 5 árbitros (principal, assistente1, assistente2, quarto, var).
- [x] `Jogo` tem `getEscalaArbitrosPublica()` e `finalizar()` com parâmetros corretos.
- [x] `Jogo` tem auto-associação `proximoJogo: Jogo` e `posicaoNoProximoJogo: PosicaoBracket`.
- [x] Todos os 5 Managers têm `saveAll()` and `reset()`.
- [x] `PersistenceUtil` tem métodos genéricos estáticos.
- [x] Não existem classes, atributos ou métodos inventados que não existam no código.
