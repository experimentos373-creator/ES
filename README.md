# World Cup Management System (ES)

Projeto em JavaFX para gestão do Campeonato do Mundo 2026.

## 📂 Entregas (Fase 2)

Os documentos e diagramas desta fase encontram-se estruturados nas seguintes pastas:

### 📍 Versão Pós-Reunião com o Professor (Mais Recente e Corrigida)
* 📂 Pasta: [`apos_a_reuniao_com_o_professor`](apos_a_reuniao_com_o_professor)
* ⚠️ **Nota de Autoria:** Todas as novas alterações, diagramas de robustez BCE, correção do modelo de domínio conceptual, acertos de capitalização de texto e a revisão e alinhamento completo do relatório em PDF pós-reunião foram realizados **exclusivamente pelo aluno Paulo Gomes (2024134892)**.
* 📄 **Relatório Técnico Completo (Atualizado):** [Relatorio_Fase2_versão_correta!.pdf](apos_a_reuniao_com_o_professor/documentacao_fase2/Relatorio_Fase2_versão_correta!.pdf) (Casos de Uso, Implementação e Testes)
* 🎨 **Diagrama de Classes:** [Diagrama de classes_versão_correta!.png](apos_a_reuniao_com_o_professor/documentacao_fase2/Diagrama%20de%20classes_versão_correta!.png)

### 📍 Versão de Entrega Inicial
* 📂 Pasta: [`documentacao_fase2`](documentacao_fase2)
* 📄 **Relatório Técnico Completo:** [Relatorio_Fase2_versão_correta!.pdf](documentacao_fase2/Relatorio_Fase2_versão_correta!.pdf)
* 🎨 **Diagrama de Classes:** [Diagrama de classes_versão_correta!.png](documentacao_fase2/Diagrama%20de%20classes_versão_correta!.png)

## Requisitos

- **JDK 21 ou superior** (testado com JDK 21 e 26)
- **Maven** (incluído no IntelliJ ou instalável via `sudo apt install maven` / `choco install maven`)

## Como Importar e Executar

Existem duas versões do projeto disponíveis no repositório. Para executar a versão mais recente e corrigida (pós-reunião), deve utilizar os caminhos da pasta `apos_a_reuniao_com_o_professor`.

### Opção A — IntelliJ IDEA (recomendado)

1. Clonar o repositório:
   ```bash
   git clone https://github.com/experimentos373-creator/ES.git
   cd ES
   ```

2. Abrir no IntelliJ:
   * **Para a Versão Pós-Reunião (Recomendada e Corrigida):** `File` → `Open` → selecionar a pasta **`apos_a_reuniao_com_o_professor/projeto_java`** (contém o `pom.xml`).
   * **Para a Versão Original:** `File` → `Open` → selecionar a pasta **`projeto_java`** (contém o `pom.xml`).
   * Escolher **"Open as Project"** (Maven).

3. Configurar o JDK:
   * `File` → `Project Structure` → `Project` → definir SDK para **JDK 21+**.

4. Executar:
   * Abrir `src/boundary/gui/Launcher.java`.
   * Clicar com o botão direito → **`Run 'Launcher.main()'`**.
   * Se der erro de módulos JavaFX, adicionar nas VM options da Run Configuration:
     ```
     --add-modules javafx.controls,javafx.fxml
     ```

### Opção B — Terminal com Maven

* **Para a Versão Pós-Reunião (Recomendada e Corrigida):**
  ```bash
  cd apos_a_reuniao_com_o_professor/projeto_java
  mvn clean javafx:run
  ```
  Se `javafx:run` não funcionar, usar:
  ```bash
  mvn clean compile exec:java
  ```

* **Para a Versão Original:**
  ```bash
  cd projeto_java
  mvn clean javafx:run
  ```
  Se `javafx:run` não funcionar, usar:
  ```bash
  mvn clean compile exec:java
  ```

## Credenciais de Login (Dados de Demonstração)

Os dados são auto-gerados no primeiro arranque. Usar o **email** no campo de login:

| Perfil | Email | Nome |
|--------|-------|------|
| Administrador | `admin@fifa.com` | Administrador FIFA |
| Gestor de Arbitragem | `arbitragem@fifa.com` | Gestor Arbitragem |
| Gestor de Equipa | `equipa@fifa.com` | Gestor Equipa (Portugal) |
| Gestor de Logística | `logistica@fifa.com` | Gestor Logística |
| Público (Adepto) | Botão **"Entrar como Público"** | Sem credenciais |

Senhas:123456

> **Nota:** Se já existirem ficheiros `.ser` na pasta raiz do projeto, apagar todos (`rm *.ser` ou `del *.ser`) para forçar a regeneração dos dados de demonstração.

---

## 🛠️ Novas Atualizações e Melhorias (Fase de Consolidação)

Todas as seguintes atualizações e melhorias críticas foram desenvolvidas e consolidadas **exclusivamente pelo aluno Paulo Gomes (2024134892)**:

1. **Sincronização Fidedigna de Cartões e Estatísticas:**
   - Correção do algoritmo que gerava estatísticas aleatórias sobrepostas a jogos com posse de bola standard de 50-50, garantindo que os cartões amarelos e vermelhos definidos pelo utilizador são guardados e apresentados com precisão absoluta.
2. **Finalização Manual Completa (Incluindo Empates a 0-0):**
   - Garantia de que todos os jogos, incluindo empates sem golos (0-0), acionam o diálogo completo de finalização para permitir a introdução manual de cartões e substituições.
3. **Cronologia Real de Substituições:**
   - Remoção de substituições fictícias automáticas na finalização manual, respeitando exclusivamente os dados fornecidos pelo utilizador.
4. **Detalhes das Grandes Penalidades na Cronologia:**
   - Geração e exibição de um log ronda a ronda detalhado para decisões por grandes penalidades (penaltis), identificando os marcadores e guarda-redes envolvidos com os seus nomes reais.
5. **Redesenho do Campo Táctico e Formações:**
   - Redesenho da aba "Pontuações" de jogo para exibir ambas as equipas num único campo de futebol vertical com badges de jogador de 11.5px e as formações táticas dinâmicas em string (ex: "4-3-3") ao lado dos suplentes.
