# World Cup Management System (ES)

Projeto em JavaFX para gestão do Campeonato do Mundo.

## Como Importar e Executar no IntelliJ IDEA

1. **Clonar o Repositório**:
   ```bash
   git clone https://github.com/experimentos373-creator/ES.git
   cd ES
   ```

2. **Abrir no IntelliJ**:
   - Vá a `File` -> `Open`.
   - Selecione a pasta **`projeto_java`** (que contém o `pom.xml`).
   - Escolha **"Open as Project"** (projeto Maven).

3. **Configurar o JDK**:
   - Vá a `File` -> `Project Structure` -> `Project`.
   - Defina o SDK para **JDK 17 ou superior** (ex: JDK 21).

4. **Executar a Aplicação**:
   - Localize o arquivo `boundary.gui.Launcher` em `src/main/java` (ou na estrutura de pacotes `src/boundary/gui/Launcher.java`).
   - Clique com o botão direito em `Launcher` e selecione **`Run 'Launcher.main()'`**.
   - *Nota*: Usamos a classe `Launcher` como ponto de entrada para contornar restrições de inicialização do JavaFX sem requerer argumentos VM complexos.

## Credenciais de Acesso (Login)
Os dados são auto-semeados no primeiro arranque:
- **Administrador**: `admin` / `admin`
- **Gestor de Equipa**: `equipa` / `equipa`
- **Gestor de Bilheteira**: `bilheteira` / `bilheteira`
- **Gestor de Logística**: `logistica` / `logistica`
- **Gestor de Arbitragem**: `arbitragem` / `arbitragem`
- **Público Geral**: Não requer login (basta usar a interface principal/pública).
