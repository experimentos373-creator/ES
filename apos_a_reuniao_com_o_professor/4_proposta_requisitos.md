# Proposta de Requisitos Detalhados
## Engenharia de Software – Projeto (Fase 2)

O enunciado da Fase 2 menciona áreas de domínio propositadamente incompletas. Este documento propõe um levantamento de requisitos detalhados e estruturados nas áreas de **Alojamento**, **Deslocações**, **Arbitragem** e **Bilheteira**, prontos a serem validados com o docente ("cliente").

---

## 🏨 1. Gestão de Alojamento (Hotéis e Quartos)

Este módulo trata da permanência das seleções (comitivas de jogadores, equipa técnica e staff) no país anfitrião.

### Requisitos Propostos (Casos de Uso)
- **RF-AL-01: Registar Hotel Parceiro**
  - O administrador do campeonato regista hotéis parceiros indicando: nome, localização, classificação (estrelas), capacidade total de quartos e instalações desportivas associadas (ex: ginásio, campo de treino privado).
- **RF-AL-02: Reservar Bloco de Quartos para Seleção**
  - Uma seleção (ex: Portugal) reserva um hotel em regime de exclusividade ou reserva um bloco específico de quartos (ex: Ala Norte, 40 quartos individuais/duplos).
  - *Fluxo Alternativo:* Se o hotel já estiver reservado por outra seleção na mesma data, o sistema deve sugerir hotéis na mesma cidade com requisitos semelhantes.
- **RF-AL-03: Atribuição Individual de Quartos**
  - O gestor da comitiva da seleção associa cada membro da comitiva (jogador X, treinador Y) a um quarto específico.
  - *Regra de Negócio:* Garantir que a comitiva não excede a capacidade contratada do hotel.

---

## ✈️ 2. Gestão de Deslocações (Logística de Viagens)

Este módulo gere o transporte das seleções entre o local de alojamento (hotel de estágio) e os vários estádios onde irão decorrer os jogos.

### Requisitos Propostos (Casos de Uso)
- **RF-DE-01: Planear Viagem de Jogo**
  - O sistema gera automaticamente um plano de viagem quando um jogo é agendado, contendo: origem (hotel da equipa), destino (estádio do jogo), data/hora de partida e data/hora prevista de chegada.
- **RF-DE-02: Alocar Meios de Transporte**
  - Atribuição de autocarros oficiais do torneio (para percursos terrestres curtos) ou voos charter internos (para distâncias longas entre cidades sedes).
  - *Regra de Negócio:* O transporte deve ser alocado com uma antecedência mínima recomendada de X horas antes do início do jogo (ex: chegada ao estádio 2 horas antes do apito inicial).
- **RF-DE-03: Gestão de Incidentes de Viagem**
  - Registo de atrasos ou avarias no transporte que afetem o horário de chegada da equipa, enviando um alerta automático ao gestor de segurança do estádio.

---

## 🏁 3. Gestão de Equipas de Arbitragem

Atribuição neutra e qualificada de árbitros aos jogos do campeonato.

### Requisitos Propostos (Casos de Uso)
- **RF-AR-01: Registar Equipa de Arbitragem**
  - Registar árbitros individualmente com atributos: nome, nacionalidade, licença FIFA e função (Árbitro Principal, Árbitro Assistente, Quarto Árbitro, VAR).
- **RF-AR-02: Atribuição Automática/Manual a Jogo**
  - O gestor do campeonato atribui uma equipa de arbitragem (composta por 1 principal, 2 assistentes, 1 quarto árbitro e 2 VAR) a uma partida.
  - *Regras de Negócio (Restrições de Neutralidade):*
    - Nenhum árbitro pode pertencer à mesma nacionalidade de qualquer uma das equipas em jogo.
    - Um árbitro não pode apitar jogos consecutivos do mesmo grupo para evitar suspeitas de favorecimento.
    - Árbitros com histórico de lesões ativas não podem ser escalados.

---

## 🎫 4. Gestão da Venda de Bilhetes (Bilheteira)

Controlo de lotação dos estádios e venda de ingressos aos adeptos.

### Requisitos Propostos (Casos de Uso)
- **RF-BI-01: Configurar Lotação e Setores**
  - Para cada estádio, definir os setores (ex: Superior Norte, Central Leste, Camarote VIP), preços base associados a cada setor e capacidade total.
- **RF-BI-02: Compra de Bilhete por Adepto**
  - O adepto seleciona o jogo, escolhe o setor disponível e efetua a compra.
  - *Fluxo Alternativo (Exceção):* Se o setor estiver esgotado, sugerir setores adjacentes.
  - *Regra de Negócio:* Limitar o número máximo de bilhetes por transação (ex: no máximo 4 bilhetes por pessoa) para evitar a revenda ilegal.
- **RF-BI-03: Validação de Entrada no Estádio**
  - O funcionário da porta (ou torniquete eletrónico) valida o bilhete através de um leitor de código (simulado na consola). O bilhete é marcado como "utilizado" para evitar entradas duplicadas.

---

## 💬 Perguntas para fazer ao vosso Docente (Cliente)

Para a vossa próxima aula prática, levem estas perguntas anotadas para obterem a validação oficial do docente:

1. *"Para a gestão de deslocações e alojamento, pretende que implementemos persistência fictícia em memória ou devemos criar um sistema de ficheiros para guardar os dados das reservas?"*
2. *"A neutralidade da arbitragem (ex: nacionalidade do árbitro diferente da nacionalidade das equipas) deve ser validada automaticamente pelo sistema com uma exceção, ou basta que o utilizador a selecione manualmente?"*
3. *"Na bilheteira, devemos suportar diferentes categorias de preços para o mesmo setor com base na idade do adepto (criança, adulto, sénior)?"*
