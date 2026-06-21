# Texto dos Casos de Uso — Metodologia ICONIX
## Sistema de Gestão do Campeonato do Mundo de Futebol 2026

**Disciplina:** Engenharia de Software  
**Grupo de Trabalho / Alunos:**  
- Paulo Gomes (2024134892) — Responsável pelo Administrador (Geral), Gestor de Equipa, Gestor de Bilheteira, Bracket/Tabelas, Integração, Público (Adepto) e Bilheteira (Desenvolvimento Partilhado) (50% do trabalho)
- Leonardo Mendes (2241009) — Responsável pelo Gestor de Arbitragem e parte do Público (Adepto) (25% do trabalho)
- Arthur (2024107339) — Responsável pelo Gestor de Logística (25% do trabalho)  
**Fase:** 2  

---

## Módulo 1 — Administrador (Responsável: Paulo Gomes - 2024134892)

### CU01: Consultar Visão Geral do Torneio

1. O Administrador seleciona a opção "Visão Geral" no `"Menu de Navegação"`.
2. O sistema lê os dados de `Equipa`, `Jogo`, `Estadio` e `Arbitro` na base de dados.
3. O sistema calcula o total de `Bilhete` vendidos e a `Receita` acumulada.
4. O sistema apresenta o `"Ecrã de Visão Geral"` exibindo a `Estatistica` com o total de `Equipa`, `Jogo`, `Arbitro`, `Bilhete` vendidos e `Receita`.

**Caminhos Alternativos:**
4.1 – Sem dados registados na base de dados: O sistema apresenta a `Estatistica` a zeros no `"Ecrã de Visão Geral"` com a `"Mensagem: Sem dados registados"`.

---

### CU02: Agendar Novo Jogo

1. O Administrador seleciona a opção "Agendar Novo Jogo" no `"Menu de Navegação"`.
2. O sistema apresenta o `"Formulário de Agendamento"` com os campos de `Jogo`: ID, data, hora, `Estadio`, `Equipa` da casa, `Equipa` visitante e fase.
3. O Administrador preenche os campos do `"Formulário de Agendamento"` e clica no botão "Guardar".
4. O sistema valida que o ID do `Jogo` não está duplicado, que a data e hora respeitam o formato e que as duas `Equipa` selecionadas não têm `Jogo` agendado no mesmo dia.
5. O sistema regista o `Jogo` na base de dados, apresenta a `"Mensagem de Confirmação: Jogo agendado com sucesso"` e o caso de uso termina.

**Caminhos Alternativos:**
4.1 – ID do `Jogo` já existe: O sistema apresenta a `"Mensagem de Erro: ID do jogo já existe!"` no `"Formulário de Agendamento"` e o caso de uso termina.
4.2 – Conflito de calendário: O sistema apresenta a `"Mensagem de Erro: Uma ou ambas as equipas já têm jogo agendado na data!"` no `"Formulário de Agendamento"` e o caso de uso termina.
4.3 – Formato de data ou hora inválido: O sistema apresenta a `"Mensagem de Erro: Formato de data ou hora inválido!"` no `"Formulário de Agendamento"` e o caso de uso termina.

---

### CU03: Finalizar Jogo

1. O Administrador seleciona o `Jogo` pretendido na `"Lista de Jogos"`.
2. O Administrador seleciona o botão "Finalizar Jogo".
3. O sistema apresenta o `"Formulário de Resultados"`.
4. O Administrador introduz os golos marcados pelas duas `Equipa` e clica no botão "Confirmar".
5. O sistema valida se o resultado é válido.
6. O sistema altera o estado do `Jogo` para finalizado, atribui a vitória ou empate, e atualiza a `Classificacao` e as `Pontuacao`.
7. O sistema calcula a progressão da `Equipa` no `"Bracket de Eliminatórias"`.
8. O sistema apresenta a `"Mensagem de Confirmação: Jogo finalizado"` contendo o vencedor e o caso de uso termina.

**Caminhos Alternativos:**
5.1 – Empate em Fase Eliminatória (Sem Penalties): O sistema impede a gravação, apresenta a `"Mensagem de Erro: Jogos das eliminatórias empatados exigem penalties!"` no `"Formulário de Resultados"` e o caso de uso termina.
5.2 – Empate com Penalties (Eliminatória): O sistema aceita a `Pontuacao` de penalties inserida pelo Administrador e o fluxo prossegue no passo 6.

---

### CU04: Consultar e Gerir Utilizadores do Sistema

1. O Administrador seleciona a opção "Utilizadores" no `"Menu de Navegação"`.
2. O sistema lê os dados de `Utilizador` na base de dados.
3. O sistema apresenta a `"Lista de Utilizadores"` exibindo nome, email, cargo e `Equipa` associada de cada `Utilizador`.

**Caminhos Alternativos:**
3.1 – Sem outros utilizadores registados na base de dados: O sistema apresenta a `"Lista de Utilizadores"` vazia com a `"Mensagem: Nenhum utilizador registado"`.

---

### CU04-B: Auditoria de Fraude e IPs Bloqueados

1. O Administrador seleciona a opção "Segurança" no `"Menu de Navegação"`.
2. O sistema apresenta o `"Ecrã de Segurança"` contendo a `"Lista de Ocorrências de Fraude"`.
3. O Administrador seleciona uma ocorrência da `"Lista de Ocorrências de Fraude"` e clica no botão "Bloquear".
4. O sistema regista o bloqueio da transação e do IP do `Utilizador` na base de dados e remove a ocorrência da `"Lista de Ocorrências de Fraude"`.
5. O sistema apresenta a `"Mensagem de Confirmação: Bloqueio Efetuado"` e atualiza o `"Ecrã de Segurança"`.

**Caminhos Alternativos:**
2.1 – Sem ocorrências de fraude registadas: O sistema apresenta o `"Ecrã de Segurança"` com a `"Lista de Ocorrências de Fraude"` vazia e a `"Mensagem: Sem incidentes de fraude registados"`.

---

## Módulo 2 — Gestor de Arbitragem (Responsável: Leonardo Mendes - 2241009)

### CU05: Consultar Visão Geral dos Árbitros

1. O Gestor de Arbitragem seleciona a opção "Arbitragem" no `"Menu de Navegação"`.
2. O sistema lê os dados de `Arbitro` e `Jogo` na base de dados.
3. O sistema calcula a `Estatistica` de disponibilidade por estado físico e analisa se existem conflitos éticos nos agendamentos de `Jogo`.
4. O sistema apresenta o `"Ecrã de Arbitragem"` exibindo a `Estatistica` e a `"Lista de Alertas Éticos"`.

**Caminhos Alternativos:**
4.1 – Sem conflitos ou violações detetadas: O sistema exibe a `"Mensagem: Sem violações éticas detetadas"` no `"Ecrã de Arbitragem"`.
4.2 – Sem árbitros registados na base de dados: O sistema apresenta a `Estatistica` a zeros no `"Ecrã de Arbitragem"`.

---

### CU06: Atribuir Árbitros a um Jogo

1. O Gestor de Arbitragem seleciona a opção "Escalar Árbitro" no `"Menu de Navegação"`.
2. O Gestor de Arbitragem seleciona o `Jogo` pretendido no dropdown da `"Lista de Jogos Agendados"`.
3. O Gestor de Arbitragem escolhe cinco `Arbitro` nos dropdowns das respetivas funções do `"Formulário de Escala"` e clica no botão "Confirmar Escala".
4. O sistema valida que nenhum `Arbitro` tem nacionalidade igual à de uma das `Equipa` do `Jogo`, nem tem outro `Jogo` no período de 48 horas.
5. O sistema regista o `EscalaoArbitral` na base de dados associado ao `Jogo`.
6. O sistema apresenta a `"Mensagem de Confirmação: Escala Confirmada"` e o caso de uso termina.

**Caminhos Alternativos:**
4.1 – Conflito de nacionalidade ou tempo de repouso insuficiente: O sistema impede a escala, apresenta a `"Mensagem de Erro: Árbitro inelegível"` no `"Formulário de Escala"` detalhando a causa, e o caso de uso termina.

---

### CU07: Avaliar Desempenho da Equipa de Arbitragem

1. O Gestor de Arbitragem seleciona a opção "Avaliar Árbitros" no `"Menu de Navegação"`.
2. O Gestor de Arbitragem seleciona o `Jogo` finalizado pretendido na `"Lista de Jogos"`.
3. O sistema apresenta o `"Formulário de Avaliação"` com os nomes e funções dos `Arbitro` associados ao `Jogo`.
4. O Gestor de Arbitragem seleciona a `Pontuacao` (1 a 5 estrelas) de cada `Arbitro` e clica no botão "Submeter".
5. O sistema valida e converte a avaliação em nota numérica, recalculando o score de cada `Arbitro`.
6. O sistema grava o score atualizado dos `Arbitro` na base de dados, apresenta a `"Mensagem de Confirmação: Avaliação Submetida"` e o caso de uso termina.

**Caminhos Alternativos:**
3.1 – Jogo sem equipa de arbitragem escalada: O sistema apresenta a `"Mensagem de Erro: Jogo sem escala"` e o caso de uso termina.

---

### CU08: Consultar e Gerir Base de Dados de Árbitros

1. O Gestor de Arbitragem seleciona a opção "Árbitros Credenciados" no `"Menu de Navegação"`.
2. O sistema apresenta a `"Lista de Árbitros"` com ID, nome, nacionalidade, função, score e estado de cada `Arbitro`.
3. O Gestor de Arbitragem seleciona um `Arbitro` na `"Lista de Árbitros"`.
4. O sistema exibe o `"Painel de Edição de Árbitro"`.
5. O Gestor de Arbitragem altera o estado do `Arbitro` no drop-down de estados e clica no botão "Atualizar".
6. O sistema grava as alterações de estado do `Arbitro` na base de dados e apresenta a `"Mensagem de Confirmação: Estado Atualizado"`.

**Caminhos Alternativos:**
2.1 – Sem árbitros registados na base de dados: O sistema exibe a `"Lista de Árbitros"` vazia com a `"Mensagem: Sem árbitros registados"`.
5.1 – Limpar histórico de pontuações de todos os árbitros: O Gestor seleciona o botão "Limpar Pontuações". O sistema apresenta a `"Mensagem de Confirmação: Reset de Notas"`. O Gestor clica em "Confirmar". O sistema repõe as `Pontuacao` de todos os `Arbitro` na base de dados para zero e atualiza a `"Lista de Árbitros"`.

---

## Módulo 3 — Gestor de Equipa (Responsável: Paulo Gomes - 2024134892)

### CU09: Consultar Visão Geral da Equipa

1. O Gestor de Equipa seleciona a opção "Visão Geral" no `"Menu de Navegação"`.
2. O sistema lê os dados da `Equipa` associada à conta de `Utilizador` do Gestor na base de dados.
3. O sistema calcula a `Estatistica` da `Equipa` contendo o número de `Jogador` do plantel, golos marcados, classificação do grupo e data do próximo `Jogo`.
4. O sistema apresenta o `"Ecrã de Visão Geral da Equipa"` com a `Estatistica` calculada.

**Caminhos Alternativos:**
2.1 – Sem equipa nacional associada ao Gestor: O sistema exibe no `"Ecrã de Visão Geral da Equipa"` a `"Mensagem: Nenhuma equipa associada"` e o caso de uso termina.

---

### CU10: Consultar Calendário de Jogos da Equipa

1. O Gestor de Equipa seleciona a opção "Calendário" no `"Menu de Navegação"`.
2. O sistema obtém os `Jogo` registados na base de dados e filtra apenas aqueles nos quais a sua `Equipa` participa.
3. O sistema apresenta a `"Lista de Jogos da Equipa"` exibindo data, hora, `Estadio`, adversário, fase e resultado de cada `Jogo`.

**Caminhos Alternativos:**
3.1 – Sem jogos calendarizados para a equipa: O sistema apresenta a `"Lista de Jogos da Equipa"` vazia com a `"Mensagem: Nenhum jogo agendado"`.

---

### CU11: Adicionar/Remover Jogadores à Equipa (Convocatória)

1. O Gestor de Equipa seleciona a opção "Plantel" no `"Menu de Navegação"`.
2. O Gestor de Equipa introduz o nome, número da camisola e posição no `"Formulário de Jogador"` e clica no botão "Guardar".
3. O sistema valida que o número de `Jogador` convocados na `Equipa` é inferior ao limite de 26 e que o número da camisola não está em uso por outro `Jogador` da mesma `Equipa`.
4. O sistema insere o novo `Jogador` na base de dados.
5. O sistema apresenta a `"Mensagem de Confirmação: Jogador Adicionado"` e atualiza a `"Lista de Convocados"`.

**Caminhos Alternativos:**
3.1 – Limite de 26 convocados atingido: O sistema impede a inserção, apresenta a `"Mensagem de Erro: Limite máximo de jogadores atingido!"` no `"Formulário de Jogador"` e o caso de uso termina.
3.2 – Número de camisola duplicado na equipa: O sistema impede a inserção, apresenta a `"Mensagem de Erro: Número de camisola já atribuído!"` no `"Formulário de Jogador"` e o caso de uso termina.
3.3 – Remover jogador do plantel: O Gestor clica no botão "Remover" na linha do `Jogador` na `"Lista de Convocados"`. O sistema exibe a `"Mensagem de Confirmação: Remover Jogador"`. O Gestor clica em "Confirmar". O sistema elimina o `Jogador` da base de dados e atualiza a `"Lista de Convocados"`.

---

### CU11-B: Editar Estado Físico e Função de Jogadores

1. O Gestor de Equipa seleciona o `Jogador` pretendido na `"Lista de Convocados"`.
2. O sistema apresenta o `"Ecrã de Ficha do Jogador"`.
3. O Gestor de Equipa altera o estado clínico, a função (Titular ou Reserva) e a percentagem de energia nos campos de edição.
4. O Gestor de Equipa preenche a descrição no campo de nova ocorrência e clica no botão "Adicionar". O sistema regista a `Ocorrencia` e atualiza a `"Lista de Histórico de Lesões"`.
5. O Gestor de Equipa clica no botão "Guardar".
6. O sistema guarda os dados atualizados do `Jogador` na base de dados, apresenta a `"Mensagem de Confirmação: Ficha Atualizada"` e o caso de uso termina.

**Caminhos Alternativos:**
2.1 – Jogador não encontrado na base de dados: O sistema apresenta o `"Ecrã de Ficha do Jogador"` vazio com a `"Mensagem: Jogador não encontrado no plantel"`.
5.1 – Dados inválidos (energia negativa ou superior a 100%): O sistema impede a gravação, apresenta a `"Mensagem de Erro: Valor de energia inválido"` no `"Ecrã de Ficha do Jogador"` e o caso de uso termina.

---

## Módulo 4 — Gestor de Bilheteira (Responsável: Paulo Gomes - 2024134892)

### CU14: Consultar Visão Geral de Performance de Vendas

1. O Gestor de Bilheteira seleciona a opção "Bilheteira" no `"Menu de Navegação"`.
2. O sistema lê todos os `Bilhete` emitidos e calcula a `Receita` acumulada na base de dados.
3. O sistema calcula a ocupação dos `Estadio` dividindo os `Bilhete` vendidos pela capacidade máxima.
4. O sistema apresenta o `"Ecrã de Vendas"` exibindo a `Estatistica` de `Receita`, total de `Bilhete` vendidos e taxa de ocupação.

**Caminhos Alternativos:**
4.1 – Sem bilhetes vendidos no sistema: O sistema exibe a `Estatistica` a zeros no `"Ecrã de Vendas"` com a `"Mensagem: Sem vendas registadas"`.

---

### CU16: Consultar Inventário e Lotação de Estádios

1. O Gestor de Bilheteira seleciona a opção "Inventário" no `"Menu de Navegação"`.
2. O sistema lê os `Estadio` e os respetivos `Setor` na base de dados.
3. O sistema apresenta a `"Lista de Inventário"` com nome do `Setor`, capacidade de público, `Bilhete` emitidos, lugares disponíveis e o preço de cada `Bilhete`.

**Caminhos Alternativos:**
3.1 – Sem estádios registados na base de dados: O sistema apresenta a `"Lista de Inventário"` vazia com a `"Mensagem: Nenhum estádio registado"`.

---

## Módulo 5 — Gestor de Logística (Responsável: Arthur - 2024107339)

### CU19: Gestão de Alojamento e Transportes

1. O Gestor de Logística seleciona a opção "Logística" no `"Menu de Navegação"`.
2. O Gestor de Logística seleciona uma `Equipa` e um `Hotel` no `"Formulário de Alojamento"`.
3. O Gestor de Logística introduz as datas de check-in e check-out no `"Formulário de Alojamento"` e clica no botão "Confirmar Alojamento".
4. O sistema valida que a capacidade do `Hotel` suporta o plantel da `Equipa` e que o `Hotel` não está ocupado por outra `Equipa`.
5. O sistema regista o alojamento na base de dados.
6. O sistema apresenta a `"Mensagem de Confirmação: Hotel Alocado"` e atualiza o estado do `Hotel` para ocupado na `"Lista de Alojamentos"`.

**Caminhos Alternativos:**
4.1 – Lotação do hotel insuficiente: O sistema impede o registo, apresenta a `"Mensagem de Erro: Capacidade insuficiente"` no `"Formulário de Alojamento"` e o caso de uso termina.
4.2 – Hotel ocupado por outra equipa: O sistema impede o registo, apresenta a `"Mensagem de Erro: Hotel indisponível"` no `"Formulário de Alojamento"` e o caso de uso termina.
4.3 – Efetuar checkout do hotel: O Gestor de Logística clica no botão "Checkout" correspondente ao `Hotel` ocupado na `"Lista de Alojamentos"`. O sistema exibe a `"Mensagem de Confirmação: Efetuar Checkout"`. O Gestor clica em "Confirmar". O sistema desvincula a `Equipa` do `Hotel` na base de dados e atualiza o estado do `Hotel` para livre na `"Lista de Alojamentos"`.

---

## Módulo 6 — Público (Portal do Adepto) (Desenvolvimento Partilhado)

### CU21: Consultar Calendário de Jogos e Resultados (Responsáveis: Leonardo Mendes - 2241009 / Paulo Gomes - 2024134892)

1. O Adepto seleciona a opção "Calendário e Resultados" no `"Portal do Adepto"`.
2. O sistema lê todos os `Jogo` registados na base de dados.
3. O sistema apresenta a `"Tabela de Jogos"` agrupando por fase do campeonato e exibindo data, hora, `Estadio`, as duas `Equipa` concorrentes e a `Pontuacao` final de cada `Jogo`.

**Caminhos Alternativos:**
3.1 – Filtrar por fase do torneio: O Adepto seleciona uma fase no dropdown da `"Tabela de Jogos"`. O sistema filtra os dados e apresenta apenas os `Jogo` da fase selecionada.

---

### CU22: Consultar Tabelas Classificativas dos Grupos (Responsável: Paulo Gomes - 2024134892)

1. O Adepto seleciona a opção "Classificações" no `"Portal do Adepto"`.
2. O sistema calcula a `Classificacao` em tempo real para cada grupo ordenando as `Equipa` por pontos, saldo de golos e golos marcados.
3. O sistema apresenta a `"Tabela Classificativa"` exibindo a posição, nome da `Equipa`, vitórias, empates, derrotas, saldo de golos e pontos.

**Caminhos Alternativos:**
3.1 – Grupo sem jogos concluídos: O sistema apresenta a `"Tabela Classificativa"` exibindo todas as `Equipa` com zero pontos e zero `Jogo` disputados.

---

### CU23: Comprar Bilhetes para Jogos (Responsáveis: Arthur - 2024107339 / Paulo Gomes - 2024134892)

1. O Adepto seleciona o `Jogo` pretendido na `"Lista de Jogos para Venda"` no `"Portal do Adepto"`.
2. O sistema apresenta o `"Ecrã de Compra de Bilhetes"`.
3. O Adepto escolhe o `Setor` do `Estadio`, introduz a quantidade de `Bilhete` e clica no botão "Comprar".
4. O sistema valida que a quantidade está entre 1 e 4 (regra anti-bot) e que o `Setor` possui capacidade restante na base de dados.
5. O sistema desconta a quantidade de lugares disponíveis do `Setor` e regista a compra na base de dados.
6. O sistema gera os `Bilhete` e apresenta a `"Mensagem de Confirmação: Compra Efetuada"` com o valor total da transação.

**Caminhos Alternativos:**
4.1 – Quantidade inválida de bilhetes (menor que 1 ou maior que 4): O sistema impede a transação, apresenta a `"Mensagem de Erro: Limite anti-bot violado"` no `"Ecrã de Compra de Bilhetes"` e o caso de uso termina.
4.2 – Lugares insuficientes no setor do estádio: O sistema impede a transação, apresenta a `"Mensagem de Erro: Setor esgotado"` no `"Ecrã de Compra de Bilhetes"` e o caso de uso termina.

---

## Caso de Uso Transversal — Autenticação (Responsável: Paulo Gomes - 2024134892)

### CU-AUTH: Autenticar Utilizador (Responsável: Paulo Gomes - 2024134892)

1. O `Utilizador` introduz o endereço de email no `"Formulário de Login"` e clica no botão "Entrar".
2. O sistema verifica se o email do `Utilizador` existe na base de dados.
3. O sistema inicia a sessão ativa do `Utilizador` e identifica as suas permissões de cargo.
4. O sistema carrega o `"Menu de Navegação"` personalizado ocultando opções de ecrã restritas.
5. O sistema redireciona o `Utilizador` para o `"Ecrã do Dashboard"` correspondente ao seu cargo.

**Caminhos Alternativos:**
2.1 – Email do utilizador não registado: O sistema impede a entrada, apresenta a `"Mensagem de Erro: Utilizador não registado"` no `"Formulário de Login"` e o caso de uso termina.
5.1 – Efetuar logout do sistema: O `Utilizador` clica no botão "Sair" no `"Menu de Navegação"`. O sistema destrói a sessão ativa do `Utilizador` na base de dados, apresenta a `"Mensagem: Sessão Terminada"` e redireciona para o `"Formulário de Login"`.
