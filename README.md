# World Cup Management System (ES)

Projeto em JavaFX para gestão do Campeonato do Mundo 2026.

## 📂 Entregas (Fase 2)

Os documentos e diagramas finais desta fase encontram-se na pasta `documentacao_fase2`:
- 📄 **Relatório Técnico Completo:** [Relatorio_Fase2_versão_correta!.pdf](documentacao_fase2/Relatorio_Fase2_versão_correta!.pdf) (Casos de Uso, Implementação e Testes)
- 📄 **Relatório Técnico - Parte 1 (CU01 a CU12):** [Relatorio_Fase2_Parte1_versão_correta!.pdf](documentacao_fase2/Relatorio_Fase2_Parte1_versão_correta!.pdf)
- 📄 **Relatório Técnico - Parte 2 (CU13 a CU24 + AUTH):** [Relatorio_Fase2_Parte2_versão_correta!.pdf](documentacao_fase2/Relatorio_Fase2_Parte2_versão_correta!.pdf)
- 🎨 **Diagrama de Classes:** [Diagrama de classes_versão_correta!.png](documentacao_fase2/Diagrama%20de%20classes_versão_correta!.png)

## Requisitos

- **JDK 21 ou superior** (testado com JDK 21 e 26)
- **Maven** (incluído no IntelliJ ou instalável via `sudo apt install maven` / `choco install maven`)

## Como Importar e Executar

### Opção A — IntelliJ IDEA (recomendado)

1. Clonar o repositório:
   ```bash
   git clone https://github.com/experimentos373-creator/ES.git
   cd ES
   ```

2. Abrir no IntelliJ:
   - `File` → `Open` → selecionar a pasta **`projeto_java`** (contém o `pom.xml`).
   - Escolher **"Open as Project"** (Maven).

3. Configurar o JDK:
   - `File` → `Project Structure` → `Project` → definir SDK para **JDK 21+**.

4. Executar:
   - Abrir `src/boundary/gui/Launcher.java`.
   - Clicar com o botão direito → **`Run 'Launcher.main()'`**.
   - Se der erro de módulos JavaFX, adicionar nas VM options da Run Configuration:
     ```
     --add-modules javafx.controls,javafx.fxml
     ```

### Opção B — Terminal com Maven

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

> **Nota:** Se já existirem ficheiros `.ser` na pasta raiz do projeto, apagar todos (`rm *.ser` ou `del *.ser`) para forçar a regeneração dos dados de demonstração.
