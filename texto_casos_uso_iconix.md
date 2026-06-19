# Texto dos Casos de Uso — Metodologia ICONIX
## Sistema de Gestão do Campeonato do Mundo de Futebol 2026

**Disciplina:** Engenharia de Software  
**Aluno:** Paulo Gomes (2024134892)  
**Fase:** 2  

---

## Módulo 1 — Administrador

### CU01: Consultar Visão Geral do Torneio

**Objetivo Principal:** Permitir que o Administrador consulte as estatísticas agregadas do campeonato.  
**Ator Principal:** Administrador  
**Pré-condições:** O utilizador está autenticado no sistema com o perfil ADMIN.  
**Pós-condições:** O sistema apresenta o resumo estatístico contendo o total de jogos, equipas, estádios, árbitros e receitas de bilheteira.

**Cenário Principal:**
1. O Administrador seleciona a opção "Visão Geral" no menu de navegação.
2. O sistema lê as informações consolidadas sobre equipas, jogos, estádios e árbitros registados.
3. O sistema calcula o total de bilhetes vendidos e a receita acumulada das vendas.
4. O sistema apresenta o ecrã de visão geral exibindo o resumo estatístico com o total de equipas, total de jogos, total de árbitros, bilhetes vendidos e receita.

**Caminhos Alternativos:**
* **4.1 — Sem Dados Registados:** Se não existirem dados correspondentes no sistema, o sistema apresenta o resumo estatístico a zeros com a mensagem "Sem dados registados" no ecrã de visão geral.

---

### CU02: Agendar Novo Jogo

**Objetivo Principal:** Permitir que o Administrador crie e agende uma nova partida no calendário do campeonato.  
**Ator Principal:** Administrador  
**Pré-condições:** O utilizador está autenticado como ADMIN. Existem pelo menos 2 equipas e 1 estádio registados.  
**Pós-condições:** O novo jogo é guardado e exibido na lista de jogos.

**Cenário Principal:**
1. O Administrador escolhe a opção "Agendar Novo Jogo" no menu de navegação.
2. O sistema apresenta o formulário de agendamento.
3. O Administrador introduz o ID do jogo, a data (formato YYYY-MM-DD), a hora (formato HH:MM), seleciona o estádio, a equipa da casa, a equipa visitante e a fase correspondente.
4. O Administrador seleciona a opção "Guardar".
5. O sistema valida que o ID não está duplicado, que a data e a hora respeitam o formato correto e que as equipas não têm jogos agendados no mesmo dia.
6. O sistema regista o jogo na base de dados.
7. O sistema apresenta uma mensagem de confirmação: "Jogo agendado com sucesso!".
8. O sistema atualiza a lista de jogos no ecrã principal.

**Caminhos Alternativos:**
* **5.1 — ID Duplicado:** Se o ID inserido já existir, o sistema apresenta a mensagem de erro "ID do jogo já existe!" e o caso de uso termina.
* **5.2 — Conflito de Calendário:** Se uma das equipas selecionadas já tiver um jogo agendado na mesma data, o sistema apresenta a mensagem de erro "Uma ou ambas as equipas já têm jogo agendado na data!" e o caso de uso termina.
* **5.3 — Formato de Data/Hora Inválido:** Se a data ou a hora inseridas não respeitarem o formato regulamentar, o sistema apresenta a mensagem de erro "Formato de data ou hora inválido!" e o caso de uso termina.

---

### CU03: Finalizar Jogo

**Objetivo Principal:** Permitir ao Administrador introduzir o resultado final de um jogo e deparar a progressão das fases do campeonato.  
**Ator Principal:** Administrador  
**Pré-condições:** O utilizador está autenticado como ADMIN. O jogo selecionado está no estado AGENDADO.  
**Pós-condições:** O jogo é marcado como FINALIZADO, as pontuações e o bracket de eliminatórias são atualizados.

**Cenário Principal:**
1. O Administrador seleciona o jogo pretendido na lista de jogos.
2. O Administrador escolhe a opção "Finalizar Jogo".
3. O sistema apresenta o formulário de resultados.
4. O Administrador introduz os golos da equipa da casa e da equipa visitante.
5. O Administrador seleciona a opção "Confirmar".
6. O sistema altera o estado do jogo para finalizado e atribui a vitória ou empate com base nos golos.
7. Se for um jogo da fase de grupos e todos os jogos do grupo correspondente estiverem concluídos, o sistema calcula os dois primeiros classificados e preenche a fase seguinte no bracket de eliminatórias.
8. Se for um jogo da fase eliminatória, o sistema coloca a equipa vencedora no jogo subsequente do bracket de eliminatórias.
9. O sistema apresenta uma mensagem de confirmação contendo o vencedor.
10. O sistema atualiza as pontuações na tabela classificativa e no bracket de eliminatórias.

**Caminhos Alternativos:**
* **5.1 — Empate em Fase Eliminatória (Sem Penalties):** Se for um jogo de eliminatórias e o resultado for de empate sem golos de penalties válidos, o sistema impede a gravação e apresenta a mensagem de erro "Jogos das eliminatórias empatados exigem penalties válidos e com vencedor decidido.".
* **5.2 — Empate com Penalties (Eliminatória):** Se forem fornecidos golos de penalties, o sistema valida que uma das equipas marcou mais penalties e avança com a equipa vencedora. O fluxo prossegue a partir do passo 6.

---

### CU04: Consultar e Gerir Utilizadores do Sistema

**Objetivo Principal:** Permitir ao Administrador consultar a lista de utilizadores e cargos registados na aplicação.  
**Ator Principal:** Administrador  
**Pré-condições:** O utilizador está autenticado como ADMIN.  
**Pós-condições:** O sistema apresenta a listagem de utilizadores.

**Cenário Principal:**
1. O Administrador seleciona a opção "Utilizadores" no menu de navegação.
2. O sistema lê os utilizadores registados na base de dados.
3. O sistema apresenta a lista de utilizadores com: nome, email, cargo associado e equipa (se aplicável).

**Caminhos Alternativos:**
* **3.1 — Sem Utilizadores Registados:** Se não existirem utilizadores registados além do atual, o sistema apresenta a lista vazia com a indicação "Nenhum utilizador registado.".

---

### CU04-B: Auditoria de Fraude e IPs Bloqueados

**Objetivo Principal:** Permitir ao Administrador monitorizar e bloquear preventivamente transações e IPs suspeitos.  
**Ator Principal:** Administrador  
**Pré-condições:** O utilizador está autenticado como ADMIN.  
**Pós-condições:** O log de fraude selecionado é removido da lista ativa.

**Cenário Principal:**
1. O Administrador seleciona a opção "Segurança" no menu de navegação.
2. O sistema apresenta a lista de logs de fraude contendo: data/hora, tipo de infração, descrição e a opção "Bloquear".
3. O Administrador seleciona o IP/transação correspondente e escolhe a opção "Bloquear".
4. O sistema processa o bloqueio preventivo e remove a ocorrência da lista de logs de fraude.
5. O sistema apresenta uma mensagem de confirmação: "Ação tomada: Transação/IP bloqueado preventivamente.".
6. O sistema atualiza a lista de logs de fraude no ecrã.

---

## Módulo 2 — Gestor de Arbitragem

### CU05: Consultar Visão Geral dos Árbitros

**Objetivo Principal:** Permitir ao Gestor de Arbitragem analisar as estatísticas de disponibilidade e alertas éticos/técnicos sobre a equipa de árbitros.  
**Ator Principal:** Gestor de Arbitragem  
**Pré-condições:** O utilizador está autenticado com o perfil GESTOR_ARBITRAGEM.  
**Pós-condições:** O sistema apresenta a contagem de árbitros por estado e os alertas de integridade no ecrã.

**Cenário Principal:**
1. O Gestor de Arbitragem seleciona a opção "Arbitragem" no menu de navegação.
2. O sistema lê os árbitros registados na base de dados.
3. O sistema calcula a quantidade de árbitros nos estados ativo, em descanso e inativo.
4. O sistema analisa os jogos agendados para verificar se existem violações de regras éticas (nacionalidade de árbitro igual à de uma das equipas, ou descanso regulamentar de 48h não respeitado).
5. O sistema apresenta o resumo estatístico e os alertas de integridade no ecrã de arbitragem.

**Caminhos Alternativos:**
* **5.1 — Sem Árbitros Cadastrados:** Se não existirem árbitros na base de dados, o sistema apresenta o resumo estatístico com valores a zero.
* **5.2 — Sem Alertas Éticos Detetados:** Se todos os agendamentos respeitarem as regras da FIFA, o sistema apresenta a mensagem de integridade positiva: "Sem violações de regras éticas ou de repouso detetadas.".

---

### CU06: Atribuir Árbitros a um Jogo

**Objetivo Principal:** Permitir ao Gestor de Arbitragem escalar uma equipa de arbitragem regulamentar para uma partida específica.  
**Ator Principal:** Gestor de Arbitragem  
**Pré-condições:** O utilizador está autenticado como GESTOR_ARBITRAGEM. Existem árbitros e jogos disponíveis.  
**Pós-condições:** O jogo passa a ter um escalão oficial de árbitros associado.

**Cenário Principal:**
1. O Gestor de Arbitragem seleciona a opção "Escalar Árbitro" no menu de navegação.
2. O Gestor seleciona o jogo agendado a partir de uma lista de seleção.
3. O Gestor seleciona os árbitros para as 5 funções: Principal, Assistente 1, Assistente 2, Quarto Árbitro e VAR.
4. O Gestor escolhe a opção "Confirmar Escala".
5. O sistema valida as restrições regulamentares de neutralidade (nacionalidade) e descanso (mínimo de 48h).
6. O sistema regista o escalão no jogo selecionado.
7. O sistema apresenta uma mensagem de confirmação: "Escala oficial de árbitros confirmada com sucesso!".

**Caminhos Alternativos:**
* **5.1 — Violação das Regras de Elegibilidade (Neutralidade ou Descanso):** Se algum dos árbitros selecionados violar a regra de neutralidade ou tiver menos de 48 horas de descanso, o sistema rejeita a escala e apresenta uma mensagem de erro indicando o árbitro inelegível e a causa detalhada. O caso de uso termina.

---

### CU07: Avaliar Desempenho da Equipa de Arbitragem

**Objetivo Principal:** Permitir ao Gestor de Arbitragem classificar o trabalho dos árbitros após o fim de um jogo, recalculando a sua classificação.  
**Ator Principal:** Gestor de Arbitragem  
**Pré-condições:** O utilizador está autenticado como GESTOR_ARBITRAGEM. O jogo selecionado está no estado FINALIZADO e possui uma escala de árbitros atribuída.  
**Pós-condições:** O score FIFA do árbitro é atualizado através do cálculo da nova média ponderada.

**Cenário Principal:**
1. O Gestor de Arbitragem seleciona a opção "Avaliar Árbitros" no menu de navegação.
2. O Gestor seleciona o jogo finalizado na lista de jogos.
3. O sistema apresenta o formulário de avaliação, exibindo os nomes e funções da equipa que arbitrou o jogo.
4. O Gestor escolhe uma classificação de 1 a 5 estrelas para cada um dos árbitros.
5. O Gestor seleciona a opção "Submeter".
6. O sistema converte as estrelas em notas numéricas, soma ao histórico de avaliações de cada árbitro e recalcula a sua média ponderada (score FIFA).
7. O sistema guarda os novos dados na base de dados.
8. O sistema apresenta a mensagem de confirmação: "Notas submetidas. FIFA Scores recalculados!".
9. O sistema atualiza o score FIFA na lista de árbitros.

**Caminhos Alternativos:**
* **3.1 — Partida Sem Escala Oficial:** Se o jogo selecionado não possuir um escalão de árbitros atribuído, o sistema apresenta a mensagem de erro "Este jogo não possui uma escala de árbitros para avaliar." e o caso de uso termina.

---

### CU08: Consultar e Gerir Base de Dados de Árbitros

**Objetivo Principal:** Permitir ao Gestor de Arbitragem auditar a lista de árbitros credenciados, alterar a sua disponibilidade ou repor pontuações.  
**Ator Principal:** Gestor de Arbitragem  
**Pré-condições:** O utilizador está autenticado como GESTOR_ARBITRAGEM.  
**Pós-condições:** Os estados e scores da lista de árbitros são modificados e atualizados na vista.

**Cenário Principal:**
1. O Gestor seleciona a opção "Árbitros Credenciados" no menu de navegação.
2. O sistema apresenta a lista de árbitros contendo: ID, Nome, Nacionalidade, Função, Score FIFA e Estado.
3. O Gestor seleciona um árbitro da lista.
4. O sistema mostra as opções de edição no ecrã.
5. O Gestor seleciona um novo estado (ex: ativo, descanso ou inativo) e escolhe a opção "Atualizar".
6. O sistema grava a alteração do estado na base de dados.
7. O sistema atualiza o estado do árbitro na lista e recalcula as estatísticas do ecrã.

**Caminhos Alternativos:**
* **2.1 — Lista Vazia:** Se não existirem árbitros registados na base de dados, o sistema apresenta a lista com a indicação "Nenhum árbitro credenciado.".
* **5.1 — Limpar Todas as Pontuações:** Se o Gestor selecionar a opção "Limpar Pontuações", o sistema apresenta uma mensagem de confirmação. Após a confirmação, redefine as notas e o total de avaliações de todos os árbitros para zero e atualiza a lista de árbitros.

---

## Módulo 3 — Gestor de Equipa

### CU09: Consultar Visão Geral da Equipa

**Objetivo Principal:** Permitir ao Gestor de Equipa (Selecionador) aceder às métricas agregadas da seleção que lidera.  
**Ator Principal:** Gestor de Equipa  
**Pré-condições:** O utilizador está autenticado como GESTOR_EQUIPA e está associado a uma seleção nacional.  
**Pós-condições:** O sistema apresenta o resumo estatístico do plantel.

**Cenário Principal:**
1. O Gestor de Equipa seleciona a opção "Visão Geral" no menu de navegação.
2. O sistema identifica a equipa atribuída à conta do Gestor.
3. O sistema calcula o número de jogadores registados no plantel, pesquisa a data do próximo jogo, soma o total de golos marcados e determina a classificação atual no grupo.
4. O sistema apresenta o ecrã com o resumo estatístico contendo o total de convocados, golos marcados, classificação e o adversário no próximo jogo.

**Caminhos Alternativos:**
* **2.1 — Sem Equipa Associada:** Se a conta do utilizador não possuir nenhuma equipa associada, o sistema apresenta a mensagem de aviso "Nenhuma equipa atribuída a este utilizador.".

---

### CU10: Consultar Calendário de Jogos da Equipa

**Objetivo Principal:** Permitir ao Gestor de Equipa consultar o calendário e os resultados dos confrontos da sua seleção nacional.  
**Ator Principal:** Gestor de Equipa  
**Pré-condições:** O utilizador está autenticado como GESTOR_EQUIPA.  
**Pós-condições:** O sistema apresenta a lista de jogos onde a seleção participa.

**Cenário Principal:**
1. O Gestor de Equipa seleciona a opção "Calendário" no menu de navegação.
2. O sistema obtém a lista global de jogos e filtra apenas os confrontos em que a sua seleção participa (como equipa da casa ou visitante).
3. O sistema apresenta a lista de jogos com: data, hora, estádio, adversário, fase e resultado (caso o jogo esteja concluído).

**Caminhos Alternativos:**
* **3.1 — Sem Jogos Agendados:** Se não existirem partidas agendadas para a seleção no campeonato, o sistema apresenta a mensagem "Nenhum jogo agendado.".

---

### CU11: Adicionar/Remover Jogadores à Equipa (Convocatória)

**Objetivo Principal:** Permitir ao Gestor de Equipa gerir a lista oficial de convocados da sua seleção nacional para o campeonato.  
**Ator Principal:** Gestor de Equipa  
**Pré-condições:** O utilizador está autenticado como GESTOR_EQUIPA e possui uma equipa nacional associada.  
**Pós-condições:** O jogador é inserido ou retirado do plantel oficial, sendo a alteração guardada.

**Cenário Principal (Adicionar Jogador):**
1. O Gestor de Equipa seleciona a opção "Plantel" no menu de navegação.
2. O Gestor introduz o nome, número de camisola e posição (Guarda-Redes, Defesa, Médio, Avançado) no formulário de jogador.
3. O Gestor seleciona a opção "Guardar".
4. O sistema valida que a comitiva não excede o limite de 26 jogadores da FIFA e que o número de camisola não está ocupado por outro jogador da mesma equipa.
5. O sistema insere o novo jogador na base de dados.
6. O sistema apresenta uma mensagem de confirmação: "Jogador adicionado com sucesso.".
7. O sistema atualiza a lista de convocados e as estatísticas de jogadores do ecrã.

**Caminhos Alternativos:**
* **4.1 — Limite de 26 Convocados Atingido:** Se o plantel já contar com 26 jogadores registados, o sistema bloqueia a adição e apresenta a mensagem de erro "Limite máximo de 26 jogadores atingido!". O caso de uso termina.
* **4.2 — Número de Camisola Duplicado:** Se o número de camisola inserido já pertencer a outro jogador da seleção, o sistema recusa o registo e apresenta a mensagem de erro "Número de camisola já atribuído a outro jogador.". O caso de uso termina.
* **4.3 — Remover Jogador:** Se o Gestor escolher a opção "Remover" num jogador da lista, o sistema apresenta uma mensagem de confirmação. Após a confirmação, elimina o jogador do plantel e atualiza a lista de convocados.

---

### CU11-B: Editar Estado Físico e Função de Jogadores

**Objetivo Principal:** Permitir ao Gestor de Equipa ou Administrador gerir o estado físico/clínico, a energia e a titularidade dos jogadores do plantel.  
**Ator Principal:** Gestor de Equipa (ou Administrador)  
**Pré-condições:** O utilizador está autenticado com permissões de gestão do plantel da seleção selecionada.  
**Pós-condições:** O estado, a energia, a função e o histórico médico do jogador são atualizados e gravados na base de dados.

**Cenário Principal:**
1. O utilizador seleciona um jogador na lista do plantel.
2. O sistema apresenta a ficha de detalhes do jogador no ecrã.
3. O utilizador altera o estado físico/disciplinar (Apto, Lesionado ou Suspenso), seleciona a função (Titular ou Reserva) e define o campo de percentagem correspondente à condição física (energia).
4. (Opcional) O utilizador introduz a descrição de uma nova ocorrência no campo de texto e escolhe a opção "Adicionar" para acrescentar a ocorrência ao histórico de lesões. O sistema atualiza a visualização do histórico.
5. O utilizador escolhe a opção "Guardar".
6. O sistema valida as entradas e grava as novas propriedades do jogador na base de dados.
7. O sistema atualiza a lista de jogadores e apresenta uma mensagem de confirmação: "Ficha do jogador atualizada com sucesso!".

---

## Módulo 4 — Gestor de Bilheteira

### CU14: Consultar Visão Geral de Performance de Vendas

**Objetivo Principal:** Permitir ao Gestor de Bilheteira consultar o balanço geral de bilhetes vendidos e receitas do campeonato.  
**Ator Principal:** Gestor de Bilheteira  
**Pré-condições:** O utilizador está autenticado como GESTOR_BILHETEIRA.  
**Pós-condições:** O sistema apresenta o resumo estatístico com as métricas de vendas do torneio.

**Cenário Principal:**
1. O Gestor de Bilheteira seleciona a opção "Bilheteira" no menu de navegação.
2. O sistema lê todos os bilhetes emitidos e calcula a receita financeira global acumulada.
3. O sistema calcula a percentagem média de ocupação dos estádios relacionando a capacidade total com os bilhetes vendidos.
4. O sistema apresenta o ecrã de vendas com o resumo estatístico contendo a receita total, bilhetes vendidos e taxa de ocupação global.

**Caminhos Alternativos:**
* **4.1 — Sem Vendas Registadas:** Se não existirem compras de bilhetes, o sistema apresenta os indicadores a zero no ecrã de vendas.

---

### CU16: Consultar Inventário e Lotação de Estádios

**Objetivo Principal:** Permitir ao Gestor de Bilheteira inspecionar a capacidade, bilhetes vendidos e disponibilidade detalhada por setor em cada estádio.  
**Ator Principal:** Gestor de Bilheteira  
**Pré-condições:** O utilizador está autenticado como GESTOR_BILHETEIRA.  
**Pós-condições:** O sistema exibe a lista detalhada de setores e preços por estádio.

**Cenário Principal:**
1. O Gestor de Bilheteira seleciona a opção "Inventário" no menu de navegação.
2. O sistema lê os estádios e os seus respetivos setores.
3. O sistema apresenta a lista de inventário de estádios, exibindo para cada setor: nome (ex: Setor Vip, Setor Económico), capacidade de público, bilhetes já emitidos, lugares que restam disponíveis e o preço do bilhete.

**Caminhos Alternativos:**
* **3.1 — Sem Estádios Registados:** Se não existirem estádios cadastrados no sistema, o sistema apresenta a mensagem de erro "Nenhum estádio registado.".

---

## Módulo 5 — Gestor de Logística

### CU19: Gestão de Alojamento e Transportes

**Objetivo Principal:** Permitir ao Gestor de Logística gerir a atribuição e ocupação dos hotéis oficiais das seleções nacionais.  
**Ator Principal:** Gestor de Logística  
**Pré-condições:** O utilizador está autenticado como GESTOR_LOGISTICA.  
**Pós-condições:** A comitiva é alocada ou desvinculada de um hotel oficial, atualizando o inventário.

**Cenário Principal (Alocar Hotel):**
1. O Gestor de Logística seleciona a opção "Logística" no menu de navegação.
2. O Gestor seleciona uma seleção nacional e escolhe um hotel disponível no formulário de atribuição.
3. O Gestor preenche as datas de check-in e check-out e escolhe a opção "Confirmar Alojamento".
4. O sistema valida as regras: a capacidade do hotel suporta o plantel e o hotel não está ocupado por outra seleção (exclusividade).
5. O sistema regista a alocação e associa a comitiva ao hotel.
6. O sistema apresenta uma mensagem de confirmação: "Hotel alocado com sucesso.".
7. O sistema atualiza o estado do hotel para ocupado na lista de alojamentos.

**Caminhos Alternativos:**
* **4.1 — Capacidade de Quartos Insuficiente:** Se o número de elementos da equipa for superior aos quartos do hotel selecionado, o sistema rejeita a atribuição e apresenta a mensagem de erro "Capacidade do hotel insuficiente para o plantel." e o caso de uso termina.
* **4.2 — Hotel Ocupado por Outra Seleção:** Se o hotel selecionado já estiver alocado a outra seleção nacional, o sistema recusa a alocação e apresenta a mensagem de erro "Hotel já ocupado por outra equipa." e o caso de uso termina.
* **4.3 — Realizar Check-out:** Se o Gestor escolher a opção "Checkout" num hotel ocupado, o sistema apresenta uma mensagem de confirmação. Após a confirmação, desvincula a seleção do hotel, altera o estado de ocupação para livre e atualiza o estado de alojamento na interface.

---

## Módulo 6 — Público (Portal do Adepto)

### CU21: Consultar Calendário de Jogos e Resultados

**Objetivo Principal:** Permitir ao Adepto (público) visualizar a programação, horários e resultados de todos os jogos do campeonato.  
**Ator Principal:** Cliente Público (Adepto)  
**Pré-condições:** Nenhuma (acesso público geral).  
**Pós-condições:** O sistema apresenta a lista organizada de jogos.

**Cenário Principal:**
1. O Adepto seleciona a opção "Calendário e Resultados" no portal público.
2. O sistema carrega todos os jogos registados no campeonato.
3. O sistema apresenta a lista de jogos organizando-os por fase del campeonato (Fase de Grupos, Oitavos-de-Final, etc.), contendo: data, hora, estádio, equipas concorrentes e o resultado final (se concluído).

**Caminhos Alternativos:**
* **3.1 — Filtrar Jogos por Fase:** Se o Adepto selecionar uma fase específica a partir de uma lista de filtragem, o sistema atualiza a lista exibindo apenas os jogos da fase selecionada.

---

### CU22: Consultar Tabelas Classificativas dos Grupos

**Objetivo Principal:** Permitir ao Adepto consultar as classificações das seleções em cada grupo e a sua posição no torneio.  
**Ator Principal:** Cliente Público (Adepto)  
**Pré-condições:** Nenhuma (acesso público geral).  
**Pós-condições:** O sistema exibe a tabela classificativa atualizada com os critérios regulamentares de desempate.

**Cenário Principal:**
1. O Adepto acede à secção "Classificação" no portal público.
2. O sistema calcula a classificação em tempo real para os grupos (A a H) ordenando as equipas de acordo com as regras FIFA (pontos, saldo de golos e golos marcados).
3. O sistema apresenta as tabelas de grupos no ecrã, exibindo a posição, nome da equipa, jogos disputados, vitórias, empates, derrotas, saldo de golos e pontos.

**Caminhos Alternativos:**
* **3.1 — Sem Jogos Concluídos no Grupo:** Se nenhuma partida do grupo tiver sido jogada, o sistema apresenta a tabela com todas as equipas empatadas em primeiro lugar com 0 pontos e 0 jogos disputados.

---

### CU23: Comprar Bilhetes para Jogos

**Objetivo Principal:** Permitir ao Adepto selecionar um jogo, escolher o setor do estádio e adquirir bilhetes oficiais online.  
**Ator Principal:** Cliente Público (Adepto)  
**Pré-condições:** Nenhuma (permite compras públicas).  
**Pós-condições:** O bilhete é gerado na base de dados e os lugares disponíveis no setor correspondente são atualizados.

**Cenário Principal:**
1. O Adepto seleciona o jogo pretendido no portal de compras.
2. O sistema apresenta os setores disponíveis do estádio associado à partida, a sua capacidade restante e o preço.
3. O Adepto escolhe o setor do estádio, introduz a quantidade pretendida de bilhetes (entre 1 e 4) e escolhe a opção "Comprar".
4. O sistema valida a regra anti-bot (quantidade permitida por transação entre 1 e 4) e verifica se o setor dispõe de lugares vagos suficientes.
5. O sistema desconta a quantidade comprada nos lugares disponíveis do setor.
6. O sistema emite os bilhetes correspondentes.
7. O sistema apresenta a mensagem de confirmação: "Compra efetuada! Total: [Valor] EUR".

**Caminhos Alternativos:**
* **4.1 — Quantidade Superior a 4 (Bloqueio Anti-Bot):** Se o utilizador tentar comprar uma quantidade menor que 1 ou maior que 4 bilhetes, o sistema rejeita a operação e apresenta a mensagem de erro: "Limite máximo de compra de 4 bilhetes por transação (regra anti-bot).". A compra não é registada.
* **4.2 — Setor Esgotado ou Sem Lugares Suficientes:** Se a quantidade solicitada for maior que a capacidade restante do setor, o sistema bloqueia a transação e apresenta a mensagem de erro "Compra falhou. Capacidade do setor excedida." e o caso de uso termina.

---

## Caso de Uso Transversal — Autenticação

### CU-AUTH: Autenticar Utilizador

**Objetivo Principal:** Permitir aos diversos gestores e administradores acederem de forma segura às suas respetivas áreas de trabalho privadas (RBAC).  
**Atores:** Administrador, Gestor de Arbitragem, Gestor de Equipa, Gestor de Bilheteira, Gestor de Logística  
**Pré-condições:** O utilizador tem um email previamente registado e configurado no sistema.  
**Pós-condições:** O utilizador é autenticado, a sua sessão é inicializada e o menu de navegação da interface é desenhado de acordo com as permissões do seu cargo.

**Cenário Principal:**
1. O utilizador introduz o seu endereço de email de trabalho no formulário de login.
2. O utilizador escolhe a opção "Entrar".
3. O sistema valida se o email existe na base de dados de utilizadores registados.
4. O sistema inicia a sessão ativa da conta, identifica o papel (cargo) associado à mesma e apresenta o menu de navegação.
5. O sistema desenha o menu de navegação apresentando exclusivamente as opções de ecrã a que o utilizador tem direito com base nas permissões do seu perfil (RBAC).
6. O sistema apresenta o ecrã correspondente ao perfil autenticado.

**Caminhos Alternativos:**
* **3.1 — Email Não Registado:** Se o email inserido não constar da base de dados, o sistema impede o login e apresenta a mensagem de erro "Email não encontrado!" no ecrã de login.
* **6.1 — Terminar Sessão (Logout):** Se o utilizador escolher a opção "Sair" no menu de navegação, o sistema destrói a sessão ativa da conta, limpando as variáveis de sessão, e redireciona o utilizador de volta para o ecrã de login.
