package view;

import java.util.List;
import java.util.Optional;
import model.Perfil;
import model.Projeto;
import model.StatusProjeto;
import model.Usuario;
import service.ProjetoService;
import service.UsuarioService;
import util.Console;

public class ProjetoView {
    private final ProjetoService service;
    private final UsuarioService usuarioService;
    private final Usuario usuarioLogado;

    public ProjetoView(ProjetoService service, UsuarioService usuarioService, Usuario usuarioLogado) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.usuarioLogado = usuarioLogado;
    }

    public void exibirMenu() {
        int opcao;
        do {
            Console.limpar();
            Console.titulo("GESTÃO DE PROJETOS");
            Console.menu("Cadastrar Projeto", "Listar Projetos", "Editar Projeto",
                    "Encerrar/Cancelar Projeto", "Detalhes do Projeto");
            opcao = Console.lerInteiro("  Opção: ", 0, 5);
            switch (opcao) {
                case 1 -> cadastrar();
                case 2 -> listar();
                case 3 -> editar();
                case 4 -> encerrar();
                case 5 -> detalhes();
            }
        } while (opcao != 0);
    }

    private void cadastrar() {
        Console.limpar();
        Console.titulo("CADASTRAR PROJETO");

        String nome = Console.lerStringObrigatorio("  Nome do projeto: ");
        String descricao = Console.lerStringObrigatorio("  Descrição: ");
        String dataInicio = Console.lerStringObrigatorio("  Data de início (dd/MM/yyyy): ");
        String dataTermino = Console.lerStringObrigatorio("  Data de término prevista (dd/MM/yyyy): ");

        Console.subtitulo("Selecionar Gerente");
        List<Usuario> gerentes = usuarioService.listarGerentes();
        if (gerentes.isEmpty()) {
            Console.erro("Nenhum gerente/administrador cadastrado.");
            Console.pausar(); return;
        }
        gerentes.forEach(g -> Console.info("  [" + g.getId().substring(0, 5) + "] " + g.getNomeCompleto()));
        Console.separador();

        String gerenteId = Console.lerStringObrigatorio("  ID do gerente: ");
        gerenteId = resolverIdUsuario(gerenteId);
        if (gerenteId == null) { Console.pausar(); return; }

        String erro = service.cadastrar(nome, descricao, dataInicio, dataTermino, gerenteId);
        if (erro == null) Console.sucesso("Projeto cadastrado com sucesso!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void listar() {
        Console.limpar();
        Console.titulo("LISTA DE PROJETOS");

        Console.info("  Filtrar por status: [1] Todos  [2] Planejado  [3] Em Andamento  [4] Concluído  [5] Cancelado");
        int filtro = Console.lerInteiro("  Opção: ", 1, 5);

        List<Projeto> projetos = switch (filtro) {
            case 2 -> service.listarPorStatus(StatusProjeto.PLANEJADO);
            case 3 -> service.listarPorStatus(StatusProjeto.EM_ANDAMENTO);
            case 4 -> service.listarPorStatus(StatusProjeto.CONCLUIDO);
            case 5 -> service.listarPorStatus(StatusProjeto.CANCELADO);
            default -> service.listarTodos();
        };

        // Gerente vê apenas seus projetos
        if (usuarioLogado.getPerfil() == Perfil.GERENTE) {
            projetos = projetos.stream()
                    .filter(p -> p.getGerenteId().equals(usuarioLogado.getId()))
                    .collect(java.util.stream.Collectors.toList());
        }

        Console.separador();
        if (projetos.isEmpty()) {
            Console.aviso("Nenhum projeto encontrado.");
        } else {
            for (Projeto p : projetos) {
                Console.info(p.toString());
                Optional<Usuario> gerente = usuarioService.buscarPorId(p.getGerenteId());
                gerente.ifPresent(g -> Console.info("     Gerente: " + g.getNomeCompleto()));
            }
            Console.separador();
            Console.info("Total: " + projetos.size() + " projeto(s)");
        }
        Console.pausar();
    }

    private void editar() {
        Console.limpar();
        Console.titulo("EDITAR PROJETO");
        listarResumido();

        String id = Console.lerStringObrigatorio("  ID do projeto: ");
        id = resolverIdProjeto(id);
        if (id == null) { Console.pausar(); return; }

        Optional<Projeto> opt = service.buscarPorId(id);
        if (opt.isEmpty()) { Console.erro("Projeto não encontrado."); Console.pausar(); return; }

        Projeto p = opt.get();

        // Gerente só pode editar seus projetos
        if (usuarioLogado.getPerfil() == Perfil.GERENTE &&
            !p.getGerenteId().equals(usuarioLogado.getId())) {
            Console.erro("Você não tem permissão para editar este projeto.");
            Console.pausar(); return;
        }

        Console.subtitulo("Editando: " + p.getNome());
        String nome = Console.lerStringObrigatorio("  Nome [" + p.getNome() + "]: ");
        String descricao = Console.lerStringObrigatorio("  Descrição [" + p.getDescricao() + "]: ");
        String dataInicio = Console.lerStringObrigatorio("  Data início [" + p.getDataInicio() + "]: ");
        String dataTermino = Console.lerStringObrigatorio("  Data término [" + p.getDataTerminoPrevista() + "]: ");

        Console.info("  Status atual: " + p.getStatus().getDescricao());
        Console.info("  [1] Planejado  [2] Em Andamento  [3] Concluído  [4] Cancelado  [0] Manter");
        int sOpt = Console.lerInteiro("  Novo status: ", 0, 4);
        StatusProjeto status = switch (sOpt) {
            case 1 -> StatusProjeto.PLANEJADO;
            case 2 -> StatusProjeto.EM_ANDAMENTO;
            case 3 -> StatusProjeto.CONCLUIDO;
            case 4 -> StatusProjeto.CANCELADO;
            default -> p.getStatus();
        };

        String gerenteId = p.getGerenteId();
        if (usuarioLogado.getPerfil() == Perfil.ADMINISTRADOR) {
            Console.info("  Gerente atual: " + gerenteId.substring(0, 5));
            System.out.print(Console.CYAN + "  Novo ID gerente (ENTER para manter): " + Console.RESET);
            String novoGerente = Console.getScanner().nextLine().trim();
            if (!novoGerente.isBlank()) {
                String novoId = resolverIdUsuario(novoGerente);
                if (novoId != null) gerenteId = novoId;
            }
        }

        String erro = service.atualizar(id, nome, descricao, dataInicio, dataTermino, status, gerenteId);
        if (erro == null) Console.sucesso("Projeto atualizado com sucesso!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void encerrar() {
        Console.limpar();
        Console.titulo("ENCERRAR/EXCLUIR PROJETO");
        listarResumido();

        String id = Console.lerStringObrigatorio("  ID do projeto: ");
        id = resolverIdProjeto(id);
        if (id == null) { Console.pausar(); return; }

        if (!Console.confirmar("Confirma a exclusão (inativação)?")) {
            Console.aviso("Operação cancelada."); Console.pausar(); return;
        }
        String erro = service.excluir(id);
        if (erro == null) Console.sucesso("Projeto excluído com sucesso!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void detalhes() {
        Console.limpar();
        Console.titulo("DETALHES DO PROJETO");
        listarResumido();

        String id = Console.lerStringObrigatorio("  ID do projeto: ");
        id = resolverIdProjeto(id);
        if (id == null) { Console.pausar(); return; }

        Optional<Projeto> opt = service.buscarPorId(id);
        if (opt.isEmpty()) { Console.erro("Projeto não encontrado."); Console.pausar(); return; }

        Projeto p = opt.get();
        Optional<Usuario> gerente = usuarioService.buscarPorId(p.getGerenteId());
        Console.separador();
        Console.info("  ID:              " + p.getId());
        Console.info("  Nome:            " + p.getNome());
        Console.info("  Descrição:       " + p.getDescricao());
        Console.info("  Data Início:     " + p.getDataInicio());
        Console.info("  Data Término:    " + p.getDataTerminoPrevista());
        Console.info("  Status:          " + p.getStatus().getDescricao());
        Console.info("  Gerente:         " + gerente.map(Usuario::getNomeCompleto).orElse("N/A"));
        Console.info("  Ativo:           " + (p.isAtivo() ? "Sim" : "Não"));
        Console.separador();
        Console.pausar();
    }

    private void listarResumido() {
        Console.separador();
        service.listarAtivos().forEach(p ->
                Console.info("  [" + p.getId().substring(0, 5) + "] " +
                        p.getNome() + " - " + p.getStatus().getDescricao()));
        Console.separador();
    }

    private String resolverIdProjeto(String parcial) {
        if (parcial.length() == 36) return parcial;
        Optional<Projeto> match = service.listarTodos().stream()
                .filter(p -> p.getId().startsWith(parcial))
                .findFirst();
        if (match.isEmpty()) {
            Console.erro("Projeto não encontrado com esse ID.");
            return null;
        }
        return match.get().getId();
    }

    private String resolverIdUsuario(String parcial) {
        if (parcial.length() == 36) return parcial;
        Optional<Usuario> match = usuarioService.listarTodos().stream()
                .filter(u -> u.getId().startsWith(parcial))
                .findFirst();
        if (match.isEmpty()) {
            Console.erro("Usuário não encontrado com esse ID.");
            return null;
        }
        return match.get().getId();
    }
}
