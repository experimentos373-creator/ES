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
**Pós-condições:** O sistema apresenta o painel com as estatísticas de jogos, equipas, estádios, árbitros e receitas de bilheteira.

**Cenário Principal:**
1. O Administrador seleciona a opção "Visão Geral" na barra lateral de navegação da interface gráfica.
2. O sistema lê as informações consolidadas sobre equipas, jogos, estádios e árbitros registados.
3. O sistema calcula o total de bilhetes vendidos e a receita acumulada das vendas.
4. O sistema apresenta o "Painel de Visão Geral" com cartões estatísticos exibindo: total de equipas, total de jogos, total de árbitros, bilhetes vendidos e receita.

**Cenário Alternativo 1 — Sem Dados Registados:**
- No passo 4, se não existirem dados correspondentes no sistema, o sistema apresenta os cartões estatísticos a zeros com a mensagem "Sem dados registados" no "Painel de Visão Geral".

---

### CU02: Agendar Novo Jogo

**Objetivo Principal:** Permitir que o Administrador crie e agende uma nova partida no calendário do campeonato.  
**Ator Principal:** Administrador  
**Pré-condições:** O utilizador está autenticado como ADMIN. Existem pelo menos 2 equipas e 1 estádio registados.  
**Pós-condições:** O novo jogo é guardado e exibido no calendário de jogos.

**Cenário Principal:**
1. O Administrador acede ao "Painel de Gestão Geral" e clica no botão "Agendar Novo Jogo".
2. O sistema apresenta o "Formulário de Agendamento de Jogo".
3. O Administrador introduz o ID do jogo, a data (formato YYYY-MM-DD), a hora (formato HH:MM), seleciona o estádio, a equipa da casa, a equipa visitante e a fase correspondente.
4. O Administrador clica em "Guardar Jogo".
5. O sistema valida que o ID não está duplicado, que as datas/horas respeitam o formato correto e que as equipas não têm jogos agendados no mesmo dia.
6. O sistema regista o jogo na base de dados.
7. O sistema apresenta uma caixa de confirmação de sucesso com a mensagem "Jogo agendado com sucesso!".
8. O sistema atualiza a "Tabela de Jogos" no painel principal.

**Cenário Alternativo 1 — ID Duplicado:**
- No passo 5, se o ID inserido já existir, o sistema exibe uma janela de aviso com o erro "ID do jogo já existe!" e o caso de uso termina.

**Cenário Alternativo 2 — Conflito de Calendário:**
- No passo 5, se uma das equipas selecionadas já tiver um jogo agendado na mesma data, o sistema exibe uma janela de aviso com o erro "Uma ou ambas as equipas já têm jogo agendado na data!" e o caso de uso termina.

**Cenário Alternativo 3 — Formato de Data/Hora Inválido:**
- No passo 5, se a data ou a hora inseridas não respeitarem o formato regulamentar, o sistema exibe uma mensagem de erro na interface "Formato de data ou hora inválido!" e o caso de uso termina.

---

### CU03: Finalizar Jogo

**Objetivo Principal:** Permitir ao Administrador introduzir o resultado final de um jogo e desencadear a progressão das fases do campeonato.  
**Ator Principal:** Administrador  
**Pré-condições:** O utilizador está autenticado como ADMIN. O jogo selecionado está no estado AGENDADO.  
**Pós-condições:** O jogo é marcado como FINALIZADO, as pontuações e o bracket de eliminatórias são atualizados.

**Cenário Principal:**
1. O Administrador seleciona o jogo pretendido na "Tabela de Jogos".
2. O Administrador clica em "Finalizar Jogo" no "Painel de Ações".
3. O sistema apresenta o "Formulário de Resultados do Jogo".
4. O Administrador introduz os golos da equipa da casa e da equipa visitante.
5. O Administrador clica em "Confirmar Resultado".
6. O sistema altera o estado do jogo para finalizado e atribui a vitória/empate com base nos golos.
7. Se for um jogo da fase de grupos e todos os jogos do grupo correspondente estiverem concluídos, o sistema calcula os dois primeiros classificados e preenche automaticamente o slot correspondente nos Oitavos-de-Final no "Bracket Eliminatório".
8. Se for um jogo da fase eliminatória, o sistema coloca a equipa vencedora no slot correto do jogo subsequente do "Bracket Eliminatório".
9. O sistema exibe um alerta de sucesso confirmando o encerramento do jogo e o vencedor.
10. O sistema atualiza as pontuações na "Tabela Classificativa" e a visualização do "Bracket".

**Cenário Alternativo 1 — Empate em Fase Eliminatória (Sem Penalties):**
- No passo 5, se for um jogo de eliminatórias (onde deve haver obrigatoriamente um vencedor) e o utilizador introduzir um resultado de empate sem golos de penalties válidos, o sistema impede a gravação e apresenta a mensagem "Jogos das eliminatórias empatados exigem penalties válidos e com vencedor decidido.".

**Cenário Alternativo 2 — Empate com Penalties (Eliminatória):**
- No passo 4, o Administrador preenche também os campos de "Golos de Penalties" de cada equipa.
- No passo 5, o sistema valida que uma das equipas marcou mais penalties e avança com a equipa vencedora. O fluxo prossegue a partir do passo 6.

---

### CU04: Consultar e Gerir Utilizadores do Sistema

**Objetivo Principal:** Permitir ao Administrador auditar e consultar a lista de utilizadores e cargos registados na aplicação.  
**Ator Principal:** Administrador  
**Pré-condições:** O utilizador está autenticado como ADMIN.  
**Pós-condições:** O sistema apresenta a listagem de utilizadores.

**Cenário Principal:**
1. O Administrador clica na opção "Gestão Geral" na barra de navegação lateral.
2. O Administrador seleciona a aba "Utilizadores Registados".
3. O sistema lê a base de utilizadores ativos e apresenta a "Tabela de Utilizadores" com: nome, email, cargo associado e equipa (se aplicável).

**Cenário Alternativo 1 — Sem Utilizadores Registados:**
- No passo 3, se não existirem utilizadores registados além do atual, o sistema apresenta a tabela vazia com a indicação "Nenhum utilizador registado.".

---

### CU04-B: Auditoria de Fraude e IPs Bloqueados

**Objetivo Principal:** Permitir ao Administrador monitorizar e bloquear preventivamente transações suspeitas e IPs potencialmente fraudulentos.  
**Ator Principal:** Administrador  
**Pré-condições:** O utilizador está autenticado como ADMIN.  
**Pós-condições:** O log de fraude é retirado da lista ativa e a segurança do IP/transação é atualizada.

**Cenário Principal:**
1. O Administrador acede ao menu "Gestão Geral" e seleciona a aba "Auditoria de Fraude & Segurança".
2. O sistema exibe a "Tabela de Auditoria de Fraude" com os alertas contendo: Timestamp, Tipo de Infração, Descrição e o botão de ação "Bloquear".
3. O Administrador localiza o IP/Transação em risco e clica no botão "Bloquear".
4. O sistema processa o bloqueio preventivo e remove a linha correspondente da "Tabela de Auditoria".
5. O sistema exibe uma caixa de mensagem informativa: "Ação tomada: Transação/IP bloqueado preventivamente.".
6. O sistema atualiza todas as visualizações reativas que dependem deste estado.

---

## Módulo 2 — Gestor de Arbitragem

### CU05: Consultar Visão Geral dos Árbitros

**Objetivo Principal:** Permitir ao Gestor de Arbitragem analisar estatísticas e alertas éticos/técnicos sobre a equipa de árbitros.  
**Ator Principal:** Gestor de Arbitragem  
**Pré-condições:** O utilizador está autenticado com o perfil GESTOR_ARBITRAGEM.  
**Pós-condições:** O sistema exibe a contagem de árbitros por estado de disponibilidade e avisos éticos.

**Cenário Principal:**
1. O Gestor de Arbitragem clica no botão "Arbitragem" na barra de navegação lateral.
2. O sistema lê todos os árbitros registados na base de dados.
3. O sistema calcula a quantidade de árbitros nos estados `ATIVO`, `DESCANSO` e `INATIVO`.
4. O sistema analisa todos os jogos agendados e verifica se existem violações de regras éticas (ex: nacionalidade do árbitro igual à de uma das equipas em jogo, ou período de descanso regulamentar de 48h não respeitado).
5. O sistema apresenta os cartões de contagem no painel e exibe os alertas éticos no "Painel Lateral de Alertas Críticos".

**Cenário Alternativo 1 — Sem Árbitros Cadastrados:**
- No passo 5, se não existirem árbitros na base de dados, o sistema apresenta os cartões estatísticos com valores a zero.

**Cenário Alternativo 2 — Sem Alertas Éticos Detetados:**
- No passo 5, se todos os agendamentos respeitarem as regras de descanso e neutralidade da FIFA, o painel lateral exibe a mensagem de integridade positiva: "Sem violações de regras éticas ou de repouso detetadas.".

---

### CU06: Atribuir Árbitros a um Jogo

**Objetivo Principal:** Permitir ao Gestor de Arbitragem escalar uma equipa de arbitragem regulamentar para uma partida específica.  
**Ator Principal:** Gestor de Arbitragem  
**Pré-condições:** O utilizador está autenticado como GESTOR_ARBITRAGEM. Existem árbitros e jogos disponíveis.  
**Pós-condições:** O jogo passa a ter um escalão oficial de árbitros associado.

**Cenário Principal:**
1. O Gestor de Arbitragem acede à aba "Escalar Árbitro para Jogo".
2. O Gestor seleciona o jogo agendado a partir de uma lista suspensa.
3. O Gestor preenche os campos do "Formulário de Escala" selecionando árbitros para as 5 funções: Principal, Assistente 1, Assistente 2, Quarto Árbitro e VAR.
4. O Gestor clica no botão "Confirmar Escala Oficial".
5. O sistema valida as restrições regulamentares de neutralidade (nenhum árbitro pode partilhar nacionalidade com as equipas do jogo) e descanso (mínimo de 48h desde o último jogo oficial do árbitro).
6. O sistema regista o escalão no jogo selecionado.
7. O sistema exibe um alerta de sucesso: "Escala oficial de árbitros confirmada com sucesso!".

**Cenário Alternativo 1 — Violação das Regras de Elegibilidade (Neutralidade ou Descanso):**
- No passo 5, se algum dos árbitros selecionados violar a regra de neutralidade nacional ou tiver menos de 48 horas de descanso, o sistema rejeita o escalamento, destaca a linha do árbitro inelegível a vermelho no formulário e exibe o aviso com a causa detalhada (ex: "Árbitro X não é neutro para este jogo" ou "Árbitro Y necessita de repouso"). O caso de uso termina.

---

### CU07: Avaliar Desempenho da Equipa de Arbitragem

**Objetivo Principal:** Permitir ao Gestor de Arbitragem classificar o trabalho dos árbitros após o fim de um jogo, recalculando a sua classificação.  
**Ator Principal:** Gestor de Arbitragem  
**Pré-condições:** O utilizador está autenticado como GESTOR_ARBITRAGEM. O jogo selecionado está no estado FINALIZADO e possui uma escala de árbitros atribuída.  
**Pós-condições:** O Score FIFA do árbitro é atualizado através do cálculo da nova média ponderada.

**Cenário Principal:**
1. O Gestor de Arbitragem acede à aba "Avaliar Árbitros".
2. O Gestor seleciona a partida concluída no painel.
3. O sistema apresenta o "Formulário de Avaliação de Árbitros", exibindo os nomes e funções da equipa que arbitrou o jogo.
4. O Gestor escolhe uma classificação de 1 a 5 estrelas para cada um dos árbitros.
5. O Gestor clica em "Submeter Avaliações".
6. O sistema converte as estrelas atribuídas para notas de 0 a 100, soma as novas pontuações ao histórico de avaliações de cada árbitro e recalcula a sua média ponderada (Score FIFA).
7. O sistema guarda os novos dados na base de dados.
8. O sistema exibe a mensagem de confirmação: "Notas submetidas. FIFA Scores recalculados!".
9. O sistema atualiza o Score FIFA exibido nas tabelas de listagem.

**Cenário Alternativo 1 — Partida Sem Escala Oficial:**
- No passo 3, se o jogo selecionado não possuir um escalão de árbitros previamente associado, o sistema impede a edição e exibe um erro: "Este jogo não possui uma escala de árbitros para avaliar.". O caso de uso termina.

---

### CU08: Consultar e Gerir Base de Dados de Árbitros

**Objetivo Principal:** Permitir ao Gestor de Arbitragem auditar a lista de árbitros credenciados, alterar a sua disponibilidade ou repor pontuações.  
**Ator Principal:** Gestor de Arbitragem  
**Pré-condições:** O utilizador está autenticado como GESTOR_ARBITRAGEM.  
**Pós-condições:** Os estados e scores da lista de árbitros são modificados e atualizados na vista.

**Cenário Principal:**
1. O Gestor acede à aba "Árbitros Credenciados".
2. O sistema apresenta a "Tabela de Árbitros" contendo: ID, Nome, Nacionalidade, Função Preferencial, Score FIFA e Estado de Disponibilidade.
3. O Gestor seleciona um árbitro da tabela.
4. O sistema mostra o "Painel de Edição de Estado" na lateral.
5. O Gestor escolhe um novo estado (ex: `DESCANSO` ou `INATIVO`) na caixa de opções e clica em "Atualizar Estado".
6. O sistema grava a alteração do estado na base de dados.
7. O sistema atualiza o estado do árbitro na tabela e recalcula os cards estatísticos superiores.

**Cenário Alternativo 1 — Tabela Vazia:**
- No passo 2, se não existirem árbitros registados na base de dados, a tabela é exibida sem registos com a indicação "Nenhum árbitro credenciado.".

**Cenário Alternativo 2 — Limpar Todas as Pontuações:**
- No passo 5, o Gestor de Arbitragem clica no botão "Limpar Todas as Pontuações" no rodapé.
- O sistema exibe um aviso de confirmação. O Gestor confirma.
- O sistema redefine as notas e o total de avaliações de todos os árbitros da base de dados para zero e recarrega a tabela de árbitros com os valores atualizados.

---

## Módulo 3 — Gestor de Equipa

### CU09: Consultar Visão Geral da Equipa

**Objetivo Principal:** Permitir ao Gestor de Equipa (Selecionador) aceder às métricas agregadas da seleção que lidera.  
**Ator Principal:** Gestor de Equipa  
**Pré-condições:** O utilizador está autenticado como GESTOR_EQUIPA e está associado a uma seleção nacional.  
**Pós-condições:** O sistema apresenta o painel com as estatísticas do plantel.

**Cenário Principal:**
1. O Gestor de Equipa clica no menu "Visão Geral" na barra lateral de navegação.
2. O sistema identifica qual a equipa atribuída à conta do Gestor.
3. O sistema calcula o número de jogadores registados no plantel, pesquisa a data do próximo jogo agendado, soma o total de golos marcados pelos seus jogadores e determina a classificação atual no respetivo grupo.
4. O sistema apresenta o "Dashboard da Seleção" exibindo cartões com o total de convocados, golos marcados, classificação e o adversário no próximo jogo.

**Cenário Alternativo 1 — Sem Equipa Associada:**
- No passo 2, se a conta do utilizador não possuir nenhuma equipa nacional associada na base de dados, o sistema apresenta um painel de aviso no centro do ecrã com a mensagem "Nenhuma equipa atribuída a este utilizador.".

---

### CU10: Consultar Calendário de Jogos da Equipa

**Objetivo Principal:** Permitir ao Gestor de Equipa consultar o calendário e os resultados dos confrontos da sua seleção nacional.  
**Ator Principal:** Gestor de Equipa  
**Pré-condições:** O utilizador está autenticado como GESTOR_EQUIPA.  
**Pós-condições:** O sistema exibe a lista cronológica de jogos onde a seleção participa.

**Cenário Principal:**
1. O Gestor de Equipa clica na opção "Calendário" na barra lateral de navegação.
2. O sistema obtém a lista global de jogos e filtra apenas os confrontos em que a sua seleção participa (seja como equipa da casa ou visitante).
3. O sistema apresenta a "Lista de Jogos da Equipa" exibindo: Data, Hora, Estádio, Seleção Adversária, Fase e Resultado Final (caso o jogo esteja concluído).

**Cenário Alternativo 1 — Sem Jogos Agendados:**
- No passo 3, se não existirem partidas agendadas para a seleção do Gestor no campeonato, o sistema apresenta a mensagem "Nenhum jogo agendado." no painel central.

---

### CU11: Adicionar/Remover Jogadores à Equipa (Convocatória)

**Objetivo Principal:** Permitir ao Gestor de Equipa gerir a lista oficial de convocados da sua seleção nacional para o campeonato.  
**Ator Principal:** Gestor de Equipa  
**Pré-condições:** O utilizador está autenticado como GESTOR_EQUIPA e possui uma equipa nacional associada.  
**Pós-condições:** O jogador é inserido ou retirado do plantel oficial, sendo a alteração guardada.

**Cenário Principal (Adicionar Jogador):**
1. O Gestor de Equipa acede ao painel "Plantel Oficial".
2. O Gestor preenche o formulário "Adicionar Novo Jogador" com: Nome, Número de Camisola e Posição (Guarda-Redes, Defesa, Médio, Avançado).
3. O Gestor clica no botão "+ Guardar".
4. O sistema valida que a comitiva não excede a regra de limite de 26 jogadores da FIFA e que o número de camisola não está ocupado por outro jogador da mesma equipa.
5. O sistema insere o novo jogador na base de dados do plantel.
6. O sistema exibe um alerta confirmando a inserção: "Jogador adicionado com sucesso.".
7. O sistema atualiza a lista de convocados e as estatísticas de jogadores do ecrã.

**Cenário Alternativo 1 — Limite de 26 Convocados Atingido:**
- No passo 4, se o plantel já contar com 26 jogadores registados, o sistema bloqueia a adição, mantém o formulário preenchido e exibe a mensagem "Limite máximo de 26 jogadores atingido!". O caso de uso termina.

**Cenário Alternativo 2 — Número de Camisola Duplicado:**
- No passo 4, se o número de camisola inserido no formulário já pertencer a outro jogador da seleção, o sistema recusa o registo e exibe o alerta "Número de camisola já atribuído a outro jogador.". O caso de uso termina.

**Cenário Alternativo 3 — Remover Jogador:**
1. O Gestor de Equipa seleciona um jogador na tabela do plantel.
2. O Gestor clica no botão "Remover do Plantel".
3. O sistema exibe uma caixa de confirmação. O Gestor confirma a exclusão.
4. O sistema elimina o jogador do plantel, guarda a alteração e atualiza a tabela e a contagem de convocados do painel.

---

### CU11-B: Editar Estado Físico e Função de Jogadores

**Objetivo Principal:** Permitir ao Gestor de Equipa ou Administrador gerir o estado físico/clínico, a energia e a titularidade dos jogadores do plantel.  
**Ator Principal:** Gestor de Equipa (ou Administrador)  
**Pré-condições:** O utilizador está autenticado com permissões de gestão do plantel da seleção selecionada.  
**Pós-condições:** O estado (Apto, Lesionado, Suspenso), energia, titularidade e histórico médico do jogador são atualizados na base de dados.

**Cenário Principal:**
1. O utilizador seleciona um jogador na tabela do "Plantel Oficial".
2. O sistema carrega os dados do jogador e apresenta a "Ficha Técnica e Médica" na barra lateral de detalhes.
3. O utilizador altera a sua disponibilidade na caixa de seleção "Estado Físico / Disciplinar" (escolhendo entre Apto, Lesionado ou Suspenso), define se o jogador é "Titular" ou "Reserva" e arrasta o slider para atualizar a percentagem de "Condição Física (Energia)".
4. (Opcional) O utilizador introduz a descrição de uma nova ocorrência no campo de texto e clica no botão "+ Adicionar" para acrescentar a informação à lista de lesões. O sistema atualiza imediatamente a caixa de visualização do histórico clínico na ficha.
5. O utilizador clica em "Guardar Alterações".
6. O sistema valida as entradas e guarda as novas propriedades do jogador na base de dados.
7. O sistema atualiza o estado e a função do jogador na tabela do plantel e atualiza os cartões de estatísticas gerais da equipa (ex: número de jogadores aptos).
8. O sistema exibe a mensagem de sucesso: "Ficha do jogador atualizada com sucesso!".

---

## Módulo 4 — Gestor de Bilheteira

### CU14: Consultar Visão Geral de Performance de Vendas

**Objetivo Principal:** Permitir ao Gestor de Bilheteira consultar o balanço geral de bilhetes vendidos e receitas do campeonato.  
**Ator Principal:** Gestor de Bilheteira  
**Pré-condições:** O utilizador está autenticado como GESTOR_BILHETEIRA.  
**Pós-condições:** O sistema apresenta os totais agregados e métricas de vendas.

**Cenário Principal:**
1. O Gestor de Bilheteira clica na opção "Bilheteira" na barra lateral de navegação.
2. O sistema lê todos os bilhetes emitidos e calcula a receita financeira global acumulada.
3. O sistema calcula a percentagem média de ocupação dos estádios relacionando a capacidade total de cada setor com os bilhetes vendidos.
4. O sistema apresenta o "Painel de Vendas" contendo cartões com: receita total, bilhetes vendidos e taxa de ocupação global do campeonato.

**Cenário Alternativo 1 — Sem Vendas Registadas:**
- No passo 4, se não existirem compras de bilhetes efetuadas na base de dados, o sistema apresenta os indicadores a zero no painel de vendas.

---

### CU16: Consultar Inventário e Lotação de Estádios

**Objetivo Principal:** Permitir ao Gestor de Bilheteira inspecionar a capacidade, bilhetes vendidos e disponibilidade detalhada por setor em cada estádio.  
**Ator Principal:** Gestor de Bilheteira  
**Pré-condições:** O utilizador está autenticado como GESTOR_BILHETEIRA.  
**Pós-condições:** O sistema exibe a tabela detalhada de setores e preços por estádio.

**Cenário Principal:**
1. O Gestor de Bilheteira acede ao menu "Inventário" na barra de navegação lateral.
2. O sistema lê a base de estádios e os seus respetivos setores.
3. O sistema apresenta a "Lista de Inventário de Estádios", exibindo para cada setor: nome (ex: Setor Vip, Setor Económico), capacidade de público, bilhetes já emitidos, lugares que restam disponíveis e o preço base do bilhete.

**Cenário Alternativo 1 — Sem Estádios Registados:**
- No passo 3, se não existirem estádios cadastrados no sistema, o painel exibe a mensagem de erro "Nenhum estádio registado.".

---

## Módulo 5 — Gestor de Logística

### CU19: Gestão de Alojamento e Transportes

**Objetivo Principal:** Permitir ao Gestor de Logística gerir a atribuição e ocupação dos hotéis oficiais das seleções nacionais.  
**Ator Principal:** Gestor de Logística  
**Pré-condições:** O utilizador está autenticado como GESTOR_LOGISTICA.  
**Pós-condições:** A comitiva é alocada ou desvinculada de um hotel oficial, atualizando o inventário.

**Cenário Principal (Alocar Hotel):**
1. O Gestor de Logística acede à aba "Logística" na barra lateral de navegação.
2. O Gestor seleciona uma seleção nacional e escolhe um hotel disponível no "Formulário de Atribuição".
3. O Gestor preenche as datas de check-in e check-out e clica no botão "Confirmar Alojamento".
4. O sistema valida as regras de negócio: a capacidade do hotel em termos de quartos tem de suportar o plantel da equipa selecionada, e o hotel não pode estar ocupado por outra seleção (regra de exclusividade de base).
5. O sistema regista a alocação e associa a comitiva ao hotel.
6. O sistema exibe a mensagem de sucesso: "Hotel alocado com sucesso.".
7. O sistema atualiza o estado do hotel para ocupado na "Lista de Alojamentos das Seleções".

**Cenário Alternativo 1 — Capacidade de Quartos Insuficiente:**
- No passo 4, se o número de elementos da equipa for superior ao número de quartos disponíveis no hotel selecionado, o sistema rejeita a atribuição e exibe a mensagem: "Capacidade do hotel insuficiente para o plantel.". O caso de uso termina.

**Cenário Alternativo 2 — Hotel Ocupado por Outra Seleção:**
- No passo 4, se o hotel selecionado já estiver alocado a outra seleção nacional, o sistema recusa a alocação e exibe o alerta: "Hotel já ocupado por outra equipa.". O caso de uso termina.

**Cenário Alternativo 3 — Realizar Check-out:**
1. O Gestor de Logística seleciona um hotel que possui uma comitiva hospedada na "Lista de Alojamentos".
2. O Gestor clica no botão "Realizar Check-out".
3. O sistema exibe um aviso de confirmação. O Gestor confirma a ação.
4. O sistema desvincula a seleção do hotel, altera a sua ocupação para livre e atualiza o estado de alojamento na interface.
5. O sistema exibe a mensagem: "Checkout realizado com sucesso.".

---

## Módulo 6 — Público (Portal do Adepto)

### CU21: Consultar Calendário de Jogos e Resultados

**Objetivo Principal:** Permitir ao Adepto (público) visualizar a programação, horários e resultados de todos os jogos do campeonato.  
**Ator Principal:** Cliente Público (Adepto)  
**Pré-condições:** Nenhuma (acesso público geral).  
**Pós-condições:** O sistema apresenta a lista organizada de jogos.

**Cenário Principal:**
1. O Adepto acede ao portal público da aplicação e seleciona a opção "Calendário e Resultados".
2. O sistema carrega todos os jogos registados no campeonato.
3. O sistema apresenta a "Grelha de Jogos" organizando-os por fase do campeonato (Fase de Grupos, Oitavos-de-Final, etc.), contendo: data, hora, estádio, equipas concorrentes e o resultado final (se o jogo já terminou).

**Cenário Alternativo 1 — Filtrar Jogos por Fase:**
- No passo 3, o Adepto pode selecionar uma fase específica (ex: "Fase de Grupos") a partir de uma caixa de filtragem.
- O sistema atualiza a grelha exibindo apenas os jogos da fase selecionada.

---

### CU22: Consultar Tabelas Classificativas dos Grupos

**Objetivo Principal:** Permitir ao Adepto consultar as classificações das seleções em cada grupo e a sua posição no torneio.  
**Ator Principal:** Cliente Público (Adepto)  
**Pré-condições:** Nenhuma (acesso público geral).  
**Pós-condições:** O sistema exibe a tabela classificativa atualizada com os critérios regulamentares de desempate.

**Cenário Principal:**
1. O Adepto acede à secção "Classificação" no portal público.
2. O sistema calcula a classificação em tempo real para os grupos (A a H) ordenando as equipas de cada grupo de acordo com as regras FIFA (pontos, saldo de golos e golos marcados).
3. O sistema apresenta as 8 tabelas de grupos no ecrã principal, exibindo a posição, nome da equipa, jogos disputados, vitórias, empates, derrotas, saldo de golos e pontos.

**Cenário Alternativo 1 — Sem Jogos Concluídos no Grupo:**
- No passo 3, se nenhuma partida do grupo tiver sido jogada, o sistema apresenta a tabela com todas as equipas empatadas em primeiro lugar com 0 pontos e 0 jogos disputados.

---

### CU23: Comprar Bilhetes para Jogos

**Objetivo Principal:** Permitir ao Adepto selecionar um jogo, escolher o setor do estádio e adquirir bilhetes oficiais online.  
**Ator Principal:** Cliente Público (Adepto)  
**Pré-condições:** Nenhuma (permite compras públicas).  
**Pós-condições:** O bilhete é gerado na base de dados e os lugares disponíveis no setor correspondente são atualizados.

**Cenário Principal:**
1. O Adepto seleciona o jogo pretendido no portal de compras.
2. O sistema apresenta os setores disponíveis do estádio associado à partida, a sua capacidade restante e o preço unitário.
3. O Adepto escolhe o setor do estádio, introduz a quantidade pretendida de bilhetes (entre 1 e 4) e clica no botão "Comprar Bilhete".
4. O sistema valida a regra anti-bot (quantidade permitida por transação entre 1 e 4) e verifica se o setor dispõe de lugares vagos suficientes.
5. O sistema desconta a quantidade comprada nos lugares disponíveis do setor.
6. O sistema emite os bilhetes correspondentes.
7. O sistema exibe o ecrã de confirmação e a mensagem de sucesso: "Compra efetuada! Total: [Valor] EUR".

**Cenário Alternativo 1 — Quantidade Superior a 4 (Bloqueio Anti-Bot):**
- No passo 4, se o utilizador tentar comprar uma quantidade menor que 1 ou maior que 4 bilhetes numa única transação, o sistema rejeita a operação e exibe o alerta: "Limite máximo de compra de 4 bilhetes por transação (regra anti-bot).". A compra não é registada.

**Cenário Alternativo 2 — Setor Esgotado ou Sem Lugares Suficientes:**
- No passo 4, se a quantidade solicitada for maior que a capacidade restante do setor do estádio, o sistema bloqueia a transação e exibe a mensagem de aviso: "Compra falhou. Capacidade do setor excedida.". O caso de uso termina.

---

## Caso de Uso Transversal — Autenticação

### CU-AUTH: Autenticar Utilizador

**Objetivo Principal:** Permitir aos diversos gestores e administradores acederem de forma segura às suas respetivas áreas de trabalho privadas (RBAC).  
**Atores:** Administrador, Gestor de Arbitragem, Gestor de Equipa, Gestor de Bilheteira, Gestor de Logística  
**Pré-condições:** O utilizador tem um email previamente registado e configurado no sistema.  
**Pós-condições:** O utilizador é autenticado, a sua sessão é inicializada e o menu de navegação da interface é desenhado de acordo com as permissões do seu cargo.

**Cenário Principal:**
1. O utilizador introduz o seu endereço de email de trabalho no "Ecrã de Login".
2. O utilizador clica no botão "Entrar".
3. O sistema valida se o email existe na base de dados de utilizadores registados.
4. O sistema inicia a sessão ativa da conta, identifica o papel (cargo) associado à mesma e redireciona para a página principal.
5. O sistema desenha a barra de navegação lateral (sidebar) apresentando exclusivamente as opções de ecrã a que o utilizador tem direito com base nas permissões do seu perfil (RBAC).
6. O sistema exibe o dashboard correspondente ao perfil autenticado.

**Cenário Alternativo 1 — Email Não Registado:**
- No passo 3, se o email inserido não constar da base de dados, o sistema impede o login, mantém o cursor no campo de texto e exibe a mensagem de aviso "Email não encontrado!". O caso de uso termina na mesma página.

**Cenário Alternativo 2 — Terminar Sessão (Logout):**
1. O utilizador clica na opção "Sair" presente no rodapé da barra de navegação lateral.
2. O sistema destrói a sessão ativa da conta, limpando as variáveis de sessão.
3. O sistema redireciona o utilizador de volta para o "Ecrã de Login".
