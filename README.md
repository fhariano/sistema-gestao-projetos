# Sistema de Gestão de Projetos e Equipes - Aluno: Flávio Ariano
### Java 17 — Console MVC — Persistência em .dat

---

## 📁 Estrutura do Projeto

```
sistema_gestao/
├── src/
│   ├── Main.java                        ← Ponto de entrada
│   ├── model/
│   │   ├── Perfil.java                  ← Enum: ADMINISTRADOR, GERENTE, COLABORADOR
│   │   ├── StatusProjeto.java           ← Enum: PLANEJADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO
│   │   ├── StatusTarefa.java            ← Enum: PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA
│   │   ├── Usuario.java                 ← Entidade Usuário (Serializable)
│   │   ├── Projeto.java                 ← Entidade Projeto (Serializable)
│   │   ├── Tarefa.java                  ← Entidade Tarefa (Serializable)
│   │   ├── Equipe.java                  ← Entidade Equipe (Serializable)
│   │   └── Alocacao.java               ← Entidade Alocação (Serializable)
│   ├── repository/
│   │   ├── UsuarioRepository.java       ← CRUD + persistência em data/usuarios.dat
│   │   ├── ProjetoRepository.java       ← CRUD + persistência em data/projetos.dat
│   │   ├── TarefaRepository.java        ← CRUD + persistência em data/tarefas.dat
│   │   ├── EquipeRepository.java        ← CRUD + persistência em data/equipes.dat
│   │   └── AlocacaoRepository.java      ← CRUD + persistência em data/alocacoes.dat
│   ├── service/
│   │   ├── UsuarioService.java          ← Regras de negócio de Usuários
│   │   ├── ProjetoService.java          ← Regras de negócio de Projetos
│   │   ├── TarefaService.java           ← Regras de negócio de Tarefas
│   │   ├── EquipeService.java           ← Regras de negócio de Equipes
│   │   └── AlocacaoService.java         ← Regras de negócio de Alocações
│   ├── util/
│   │   ├── Console.java                 ← Utilitários de UI (cores, menus, inputs)
│   │   └── Validador.java               ← Validações (CPF, e-mail, senha, datas)
│   └── view/
│       ├── MenuPrincipal.java           ← Login + roteamento por perfil
│       ├── UsuarioView.java             ← CRUD de Usuários (menu console)
│       ├── ProjetoView.java             ← CRUD de Projetos (menu console)
│       ├── TarefaView.java              ← CRUD de Tarefas (menu console)
│       ├── EquipeView.java              ← CRUD de Equipes + gerenciar membros
│       ├── AlocacaoView.java            ← Alocar/desalocar equipes em projetos
│       └── RelatorioView.java           ← Relatórios de acompanhamento
└── data/                                ← Criada automaticamente (arquivos .dat)
```

---

## ▶️ Como Compilar e Executar

### Pré-requisito
- Java 17 ou superior instalado (testado também com Java 21)
- Terminal / Prompt de Comando

### Compilação (a partir da pasta `src/`)

```bash
# Windows
cd src
javac -encoding UTF-8 model\*.java repository\*.java service\*.java util\*.java view\*.java Main.java

# Linux / macOS
cd src
javac -encoding UTF-8 model/*.java repository/*.java service/*.java util/*.java view/*.java Main.java
```

### Execução (a partir da pasta `src/`)

```bash
java Main
```

---

## 🔐 Acesso Padrão

Na **primeira execução**, o sistema cria automaticamente um usuário administrador:

| Campo  | Valor      |
|--------|------------|
| Login  | `admin`    |
| Senha  | `admin123` |

> ⚠️ Recomenda-se alterar a senha após o primeiro acesso.

---

## 👤 Perfis de Acesso

| Perfil          | Permissões                                              |
|-----------------|--------------------------------------------------------|
| Administrador   | Acesso total: usuários, projetos, tarefas, equipes, alocações, relatórios |
| Gerente         | Projetos (seus), tarefas, equipes, alocações, relatórios |
| Colaborador     | Minhas tarefas, visualizar projetos, relatórios        |

---

## 🏗️ Arquitetura MVC

| Camada       | Responsabilidade                                         |
|--------------|----------------------------------------------------------|
| **Model**    | Entidades de domínio com Serializable para persistência  |
| **View**     | Interação com usuário via Scanner/Console                |
| **Service**  | Regras de negócio e validações                          |
| **Repository**| Persistência em arquivos `.dat` com ArrayList           |

---

## ✅ Funcionalidades Implementadas

- [x] Login com autenticação (máx. 5 tentativas)
- [x] 3 perfis de acesso com menus diferenciados
- [x] CRUD completo de Usuários
- [x] CRUD completo de Projetos
- [x] CRUD completo de Tarefas
- [x] CRUD completo de Equipes + gerenciar membros
- [x] Alocação/desalocação de Equipes em Projetos
- [x] Relatório: Visão geral de projetos
- [x] Relatório: Acompanhamento de tarefas com barra de progresso
- [x] Relatório: Projetos com atraso
- [x] Relatório: Relatório de equipes
- [x] Relatório: Resumo por status
- [x] IDs automáticos UUID
- [x] Validação de CPF, e-mail, senha, datas
- [x] Persistência automática em arquivos `.dat`
- [x] Interface colorida no console (ANSI)
- [x] Resolução de ID por prefixo (8 primeiros caracteres)

---

## 📋 Regras de Negócio

1. **Usuários**: CPF único, login único, e-mail único; senha mínima 6 chars; perfil obrigatório
2. **Projetos**: Data de término > data de início; gerente obrigatório (GERENTE ou ADMIN)
3. **Tarefas**: Vinculadas a projeto ativo; prioridade 1-3; responsável opcional
4. **Equipes**: Podem ter múltiplos membros; atuam em múltiplos projetos
5. **Alocações**: Uma equipe não pode ser alocada duas vezes no mesmo projeto (ativa)
6. **Perfis**: Gerente só vê/edita seus projetos; Colaborador vê apenas suas tarefas
