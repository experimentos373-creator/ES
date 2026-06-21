# 8. Diagrama de Classes do Domínio

Este diagrama foca-se no domínio do problema e nas relações de negócio puras entre as entidades do campeonato, alinhado com o estilo académico da disciplina.

![Diagrama de Classes do Domínio](imagens/diagrama_classes_dominio.png)

### Código PlantUML do Domínio

```plantuml
@startuml
skinparam classAttributeIconSize 0
skinparam packageStyle rectangle
skinparam linetype ortho
skinparam nodesep 100
skinparam ranksep 80

package "Partida" #EBF3F9 {
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
        + finalizar(vencedor: Equipa, golosCasa: int, golosFora: int, penaltisCasa: int, penaltisFora: int, estatisticas: EstatisticaJogo): void
    }

    class Equipa {
        - nome: String
        - treinador: String
        - jogadores: List<Jogador>
    }

    class EscalaoArbitral {
        - principal: Arbitro
        - assistente1: Arbitro
        - assistente2: Arbitro
        - quarto: Arbitro
        - var: Arbitro
    }

    class EventoJogo {
        - minuto: int
        - tipo: TipoEvento
        - jogador: Jogador
        - equipa: Equipa
    }

    class EstatisticaJogo {
        - posseBolaCasa: int
        - posseBolaFora: int
        - rematesCasa: int
        - rematesFora: int
        - cantosCasa: int
        - cantosFora: int
    }
}

package "Jogadores" #EDF7ED {
    class Jogador {
        - id: int
        - numeroCamisola: int
        - nome: String
        - posicao: String
        - estado: EstadoJogador
        - golos: int
        - assistencias: int
    }
}

package "Árbitros" #FFF4E5 {
    class Arbitro {
        - id: int
        - email: String
        - nome: String
        - nacionalidade: String
        - tipo: TipoArbitro
        - estado: EstadoArbitro
        - pontuacaoFIFA: int
        - totalAvaliacoes: int
    }
}

package "Estádio" #F6EDF9 {
    class Estadio {
        - nome: String
        - localizacao: String
        - setores: List<SetorEstadio>
    }

    class SetorEstadio {
        - nome: String
        - capacidadeTotal: int
        - bilhetesVendidos: int
        - precoBase: double
    }
}

package "Bilheteira" #FDEDED {
    class Bilhete {
        - jogoId: int
        - setor: String
        - preco: double
    }
}

package "Logística" #FFFDE7 {
    class Hotel {
        - id: int
        - nome: String
        - localizacao: String
        - capacidadeQuartos: int
        - checkInDate: String
        - checkOutDate: String
        - equipaHospedada: Equipa
        + checkIn(equipa: Equipa, checkInDate: String, checkOutDate: String): boolean
    }

    class Viagem {
        - origem: String
        - destino: String
        - dataPartida: String
        - dataChegada: String
        - meioTransporte: String
    }
}

package "Utilizadores" #E3F2FD {
    class Utilizador {
        - email: String
        - nome: String
        - cargo: TipoUtilizador
        - equipaAssociada: String
    }
}

package "Classificação" #F9FBE7 {
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
    }
}

package "Enums" #ECEFF1 {
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
}

' Relacionamentos limpos
Jogo "0..*" o-- "0..1" Equipa : equipaCasa
Jogo "0..*" o-- "0..1" Equipa : equipaFora
Jogo "0..*" o-- "1" Estadio : estadio
Jogo "1" o-- "0..1" EscalaoArbitral : escalaArbitros
Jogo "1" *-- "0..*" EventoJogo : eventos
Jogo "1" o-- "0..1" EstatisticaJogo : estatisticas
Jogo "0..1" --> "0..1" Jogo : proximoJogo

Equipa "1" *-- "0..26" Jogador : jogadores
Equipa "0..*" --> "0..1" Estadio : estadioCasa

EscalaoArbitral "1" o-- "1" Arbitro : principal
EscalaoArbitral "1" o-- "0..1" Arbitro : assistente1
EscalaoArbitral "1" o-- "0..1" Arbitro : assistente2
EscalaoArbitral "1" o-- "0..1" Arbitro : quarto
EscalaoArbitral "1" o-- "0..1" Arbitro : var

Estadio "1" *-- "1..*" SetorEstadio : setores

Bilhete "0..*" ..> Jogo : refere-se a

Hotel "0..*" o-- "0..1" Equipa : equipaHospedada
Viagem "0..*" ..> Equipa : transporte de
Viagem "0..*" ..> Jogo : para

Utilizador "0..*" ..> Equipa : associado a

EventoJogo "0..*" o-- "1" Jogador : jogador
EventoJogo "0..*" o-- "1" Equipa : equipa

ClassificacaoLinha "0..*" o-- "1" Equipa : equipa
@enduml
```
