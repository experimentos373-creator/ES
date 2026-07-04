# Padrão de Design: Singleton em Java
## Engenharia de Software – Projeto (Fase 2)

O padrão **Singleton** é um padrão criacional que garante que uma classe tem apenas uma instância ativa e fornece um ponto de acesso global para a mesma. Na Fase 2 do projeto, é **obrigatória** a sua implementação.

---

## ☕ Abordagens de Implementação em Java

Para este projeto, a abordagem recomendada e preferencial é a **Classe Clássica com Double-Checked Locking (DCL)**.

### 🌟 Recomendado: Classe Clássica (Double-Checked Locking com Otimização)

**Por que razão deves preferir esta abordagem no projeto?**
1. **Representação UML Perfeita no Visual Paradigm:** O Enum não tem uma mapeamento direto limpo no diagrama de classes. A abordagem DCL possui exatamente os 3 elementos avaliados pelo docente: o atributo `- instance`, o construtor privado `- NomeClasse()` e o método estático `+ getInstance()`.
2. **Conformidade Académica e Fontes Sólidas:** É a abordagem oficial documentada no **Refactoring.Guru**, que serve de referência bibliográfica sólida perante o docente.

#### Implementação Otimizada (com variável local `result`):
Esta implementação inclui uma otimização de performance recomendada (Effective Java / Refactoring.Guru) que usa uma variável local para reduzir os acessos diretos ao campo `volatile` quando a instância já existe.

```java
package manager;

import domain.Jogo;
import java.util.ArrayList;
import java.util.List;

public class CampeonatoManager {

    // 1. Atributo estático privado que guarda a instância única da classe.
    // 'volatile' assegura que as alterações à variável são visíveis entre threads.
    private static volatile CampeonatoManager instance;

    // Atributos de negócio
    private final List<Jogo> calendario;

    // 2. Construtor privado para impedir instanciação externa com 'new'.
    private CampeonatoManager() {
        this.calendario = new ArrayList<>();
    }

    // 3. Método estático público de acesso à instância única (Double-Checked Locking)
    public static CampeonatoManager getInstance() {
        CampeonatoManager result = instance; // Otimização volatile (leitura única local)
        if (result != null) {
            return result;
        }
        synchronized (CampeonatoManager.class) {
            if (instance == null) {
                instance = new CampeonatoManager();
            }
            return instance;
        }
    }

    // Métodos de negócio
    public void registarJogo(Jogo jogo) {
        this.calendario.add(jogo);
    }

    public List<Jogo> getCalendario() {
        return new ArrayList<>(this.calendario); // Retorna cópia de segurança
    }
}
```

---


### 2. Enum Singleton (Mais Simples & Seguro)
De acordo com Joshua Bloch (autor de *Effective Java*), esta é a melhor forma de implementar o Singleton em Java moderno. É inerentemente *thread-safe*, imune a ataques de *reflection* e serialização sem código adicional.

> [!NOTE]
> **Eager Initialization:** Ao contrário da classe clássica com double-checked locking, o **Enum Singleton não tem lazy initialization**. A sua instância única é criada de forma imediata (eager) pela JVM assim que a classe é carregada pela primeira vez. Esta é uma distinção teórica importante caso o docente pergunte as diferenças entre as duas abordagens.

```java
package manager;

import domain.Jogo;
import java.util.ArrayList;
import java.util.List;

public enum CampeonatoManager {
    // Instância única definida como um elemento do enum
    INSTANCE;

    // Atributos de negócio
    private final List<Jogo> calendario = new ArrayList<>();

    // Métodos de negócio
    public void registarJogo(Jogo jogo) {
        this.calendario.add(jogo);
    }

    public List<Jogo> getCalendario() {
        return new ArrayList<>(this.calendario);
    }
}
```

#### Como utilizar no código:
```java
// Com a classe clássica:
CampeonatoManager.getInstance().registarJogo(novoJogo);

// Com o Enum Singleton:
CampeonatoManager.INSTANCE.registarJogo(novoJogo);
```

---

## 🎨 Representação no Visual Paradigm (UML)

Para que o Singleton seja corretamente avaliado pelo docente no **Diagrama de Classes Final**, deves desenhá-lo com as seguintes características:

```
┌──────────────────────────────────────┐
│          CampeonatoManager           │
├──────────────────────────────────────┤
│ - instance : CampeonatoManager {static}│
│ - calendario : List<Jogo>            │
├──────────────────────────────────────┤
│ - CampeonatoManager()                │
│ + getInstance() : CampeonatoManager {static}│
│ + registarJogo(jogo : Jogo) : void   │
│ + getCalendario() : List<Jogo>       │
└──────────────────────────────────────┘
```

### Regras no Visual Paradigm:
1. **Atributo da Instância:**
   - Deve ser privado (`-`).
   - Nome: `instance` (ou semelhante).
   - Tipo: `CampeonatoManager` (a própria classe).
   - Deve ser marcado como **Static** (no Visual Paradigm, isto adiciona o sufixo `{static}` ou sublinha o atributo).
2. **Construtor:**
   - Deve ser privado (`-`).
   - Nome: `CampeonatoManager()`.
3. **Método de Acesso:**
   - Deve ser público (`+`).
   - Nome: `getInstance()`.
   - Tipo de Retorno: `CampeonatoManager`.
   - Deve ser marcado como **Static** (o nome do método aparecerá sublinhado no diagrama).

> [!WARNING]
> Um erro muito comum é deixar o construtor como público (`+`) ou não sublinhar o método `getInstance()` no diagrama UML. Revê isto antes de exportar os teus diagramas!

---

## 📌 Aplicação aos Restantes Managers
A mesma lógica de Singleton (quer em Classe clássica quer em Enum) deve ser replicada para os restantes managers do sistema para garantir o acesso global unificado aos seus estados:
- **`ArbitragemManager`** (gere árbitros e escalas)
- **`BilheteiraManager`** (gere ingressos e vendas)
- **`LogisticaManager`** (gere hotéis, quartos e deslocações)
