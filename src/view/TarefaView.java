package view;

import java.util.List;
import java.util.Optional;
import model.Projeto;
import model.StatusTarefa;
import model.Tarefa;
import model.Usuario;
import service.ProjetoService;
import service.TarefaService;
import service.UsuarioService;
import util.Console;

public class TarefaView {
    private final TarefaService service;
    private final ProjetoService projetoService;
    private final UsuarioService usuarioService;
    private final Usuario usuarioLogado;

    public TarefaView(TarefaService service, ProjetoService projetoService,
                       UsuarioService usuarioService, Usuario usuarioLogado) {
        this.service = service;
        this.projetoService = projetoService;
        this.usuarioService = usuarioService;
        this.usuarioLogado = usuarioLogado;
    }

    public void exibirMenu() {
        int opcao;
        do {
            Console.limpar();
            Console.titulo("GESTÃO DE TAREFAS");
            Console.menu("Cadastrar Tarefa", "Listar Tarefas por Projeto",
                    "Editar Tarefa", "Excluir Tarefa", "Minhas Tarefas");
            opcao = Console.lerInteiro("  Opção: ", 0, 5);
            switch (opcao) {
                case 1 -> cadastrar();
                case 2 -> listarPorProjeto();
                case 3 -> editar();
                case 4 -> excluir();
                case 5 -> minhasTarefas();
            }
        } while (opcao != 0);
    }

    private void cadastrar() {
        Console.limpar();
        Console.titulo("CADASTRAR TAREFA");

        Console.subtitulo("Selecionar Projeto");
        List<Projeto> projetos = projetoService.listarAtivos();
        if (projetos.isEmpty()) { Console.erro("Nenhum projeto ativo."); Console.pausar(); return; }
        projetos.forEach(p -> Console.info("  [" + p.getId().substring(0, 5) + "] " + p.getNome()));
        Console.separador();

        String projetoId = Console.lerStringObrigatorio("  ID do projeto: ");
        projetoId = resolverIdProjeto(projetoId);
        if (projetoId == null) { Console.pausar(); return; }

        String titulo = Console.lerStringObrigatorio("  Título da tarefa: ");
        String descricao = Console.lerString("  Descrição (opcional): ");
        String dataInicio = Console.lerStringObrigatorio("  Data de início (dd/MM/yyyy): ");
        String dataConclusao = Console.lerStringObrigatorio("  Data prevista de conclusão (dd/MM/yyyy): ");

        Console.subtitulo("Selecionar Responsável (opcional)");
        usuarioService.listarAtivos().forEach(u ->
                Console.info("  [" + u.getId().substring(0, 5) + "] " + u.getNomeCompleto()));
        Console.separador();
        System.out.print(Console.CYAN + "  ID responsável (ENTER para pular): " + Console.RESET);
        String responsavelId = Console.getScanner().nextLine().trim();
        if (!responsavelId.isBlank()) {
            responsavelId = resolverIdUsuario(responsavelId);
            if (responsavelId == null) { Console.pausar(); return; }
        }

        Console.info("  Prioridade: [1] Baixa  [2] Média  [3] Alta");
        int prioridade = Console.lerInteiro("  Prioridade: ", 1, 3);

        String erro = service.cadastrar(titulo, descricao, dataInicio, dataConclusao,
                projetoId, responsavelId.isBlank() ? null : responsavelId, prioridade);
        if (erro == null) Console.sucesso("Tarefa cadastrada com sucesso!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void listarPorProjeto() {
        Console.limpar();
        Console.titulo("TAREFAS POR PROJETO");

        List<Projeto> projetos = projetoService.listarAtivos();
        if (projetos.isEmpty()) { Console.aviso("Nenhum projeto ativo."); Console.pausar(); return; }
        projetos.forEach(p -> Console.info("  [" + p.getId().substring(0, 5) + "] " + p.getNome()));
        Console.separador();

        String projetoId = Console.lerStringObrigatorio("  ID do projeto: ");
        projetoId = resolverIdProjeto(projetoId);
        if (projetoId == null) { Console.pausar(); return; }

        List<Tarefa> tarefas = service.listarPorProjeto(projetoId);
        Console.separador();
        if (tarefas.isEmpty()) {
            Console.aviso("Nenhuma tarefa para este projeto.");
        } else {
            for (Tarefa t : tarefas) {
                Console.info(t.toString());
                if (t.getResponsavelId() != null) {
                    Optional<Usuario> resp = usuarioService.buscarPorId(t.getResponsavelId());
                    resp.ifPresent(u -> Console.info("     Responsável: " + u.getNomeCompleto()));
                }
            }
            Console.separador();
            Console.info("Total: " + tarefas.size() + " tarefa(s)");
        }
        Console.pausar();
    }

    private void editar() {
        Console.limpar();
        Console.titulo("EDITAR TAREFA");
        listarResumido();

        String id = Console.lerStringObrigatorio("  ID da tarefa: ");
        id = resolverIdTarefa(id);
        if (id == null) { Console.pausar(); return; }

        Optional<Tarefa> opt = service.buscarPorId(id);
        if (opt.isEmpty()) { Console.erro("Tarefa não encontrada."); Console.pausar(); return; }

        Tarefa t = opt.get();
        Console.subtitulo("Editando: " + t.getTitulo());

        String titulo = Console.lerStringObrigatorio("  Título [" + t.getTitulo() + "]: ");
        String descricao = Console.lerString("  Descrição [" + t.getDescricao() + "]: ");
        String dataConclusao = Console.lerStringObrigatorio("  Data conclusão [" + t.getDataPrevistaConclusao() + "]: ");

        Console.info("  Status atual: " + t.getStatus().getDescricao());
        Console.info("  [1] Pendente  [2] Em Andamento  [3] Concluída  [4] Cancelada  [0] Manter");
        int sOpt = Console.lerInteiro("  Novo status: ", 0, 4);
        StatusTarefa status = switch (sOpt) {
            case 1 -> StatusTarefa.PENDENTE;
            case 2 -> StatusTarefa.EM_ANDAMENTO;
            case 3 -> StatusTarefa.CONCLUIDA;
            case 4 -> StatusTarefa.CANCELADA;
            default -> t.getStatus();
        };

        Console.info("  Prioridade atual: " + t.getPrioridadeDescricao());
        Console.info("  [1] Baixa  [2] Média  [3] Alta  [0] Manter");
        int pOpt = Console.lerInteiro("  Nova prioridade: ", 0, 3);
        int prioridade = pOpt == 0 ? t.getPrioridade() : pOpt;

        String responsavelId = t.getResponsavelId();
        System.out.print(Console.CYAN + "  Novo responsável ID (ENTER para manter): " + Console.RESET);
        String novoResp = Console.getScanner().nextLine().trim();
        if (!novoResp.isBlank()) {
            String rId = resolverIdUsuario(novoResp);
            if (rId != null) responsavelId = rId;
        }

        String erro = service.atualizar(id, titulo, descricao, dataConclusao, status, responsavelId, prioridade);
        if (erro == null) Console.sucesso("Tarefa atualizada com sucesso!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void excluir() {
        Console.limpar();
        Console.titulo("EXCLUIR TAREFA");
        listarResumido();

        String id = Console.lerStringObrigatorio("  ID da tarefa: ");
        id = resolverIdTarefa(id);
        if (id == null) { Console.pausar(); return; }

        if (!Console.confirmar("Confirma a exclusão?")) {
            Console.aviso("Operação cancelada."); Console.pausar(); return;
        }
        String erro = service.excluir(id);
        if (erro == null) Console.sucesso("Tarefa excluída!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void minhasTarefas() {
        Console.limpar();
        Console.titulo("MINHAS TAREFAS");
        List<Tarefa> tarefas = service.listarPorResponsavel(usuarioLogado.getId());
        Console.separador();
        if (tarefas.isEmpty()) {
            Console.aviso("Você não tem tarefas atribuídas.");
        } else {
            for (Tarefa t : tarefas) {
                Console.info(t.toString());
                Optional<Projeto> proj = projetoService.buscarPorId(t.getProjetoId());
                proj.ifPresent(p -> Console.info("     Projeto: " + p.getNome()));
            }
            Console.separador();
            Console.info("Total: " + tarefas.size() + " tarefa(s)");
        }
        Console.pausar();
    }

    private void listarResumido() {
        Console.separador();
        service.listarTodas().stream()
                .filter(Tarefa::isAtiva)
                .forEach(t -> Console.info("  [" + t.getId().substring(0, 5) + "] " +
                        t.getTitulo() + " - " + t.getStatus().getDescricao()));
        Console.separador();
    }

    private String resolverIdProjeto(String parcial) {
        if (parcial.length() == 36) return parcial;
        Optional<Projeto> match = projetoService.listarTodos().stream()
                .filter(p -> p.getId().startsWith(parcial)).findFirst();
        if (match.isEmpty()) { Console.erro("Projeto não encontrado."); return null; }
        return match.get().getId();
    }

    private String resolverIdTarefa(String parcial) {
        if (parcial.length() == 36) return parcial;
        Optional<Tarefa> match = service.listarTodas().stream()
                .filter(t -> t.getId().startsWith(parcial)).findFirst();
        if (match.isEmpty()) { Console.erro("Tarefa não encontrada."); return null; }
        return match.get().getId();
    }

    private String resolverIdUsuario(String parcial) {
        if (parcial.length() == 36) return parcial;
        Optional<Usuario> match = usuarioService.listarTodos().stream()
                .filter(u -> u.getId().startsWith(parcial)).findFirst();
        if (match.isEmpty()) { Console.erro("Usuário não encontrado."); return null; }
        return match.get().getId();
    }
}
