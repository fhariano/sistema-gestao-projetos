package view;

import java.util.List;
import java.util.Optional;
import model.Perfil;
import model.Usuario;
import service.UsuarioService;
import util.Console;

public class UsuarioView {
    private final UsuarioService service;
    private final Usuario usuarioLogado;

    public UsuarioView(UsuarioService service, Usuario usuarioLogado) {
        this.service = service;
        this.usuarioLogado = usuarioLogado;
    }

    public void exibirMenu() {
        int opcao;
        do {
            Console.limpar();
            Console.titulo("GESTÃO DE USUÁRIOS");
            Console.menu("Cadastrar Usuário", "Listar Usuários", "Editar Usuário",
                    "Desativar Usuário", "Reativar Usuário", "Visualizar Detalhes");
            opcao = Console.lerInteiro("  Opção: ", 0, 6);
            switch (opcao) {
                case 1 -> cadastrar();
                case 2 -> listar();
                case 3 -> editar();
                case 4 -> desativar();
                case 5 -> reativar();
                case 6 -> detalhes();
            }
        } while (opcao != 0);
    }

    private void cadastrar() {
        Console.limpar();
        Console.titulo("CADASTRAR USUÁRIO");

        String nome = Console.lerStringObrigatorio("  Nome completo: ");
        String cpf = Console.lerStringObrigatorio("  CPF (somente números): ");
        String email = Console.lerStringObrigatorio("  E-mail: ");
        String cargo = Console.lerStringObrigatorio("  Cargo: ");
        String login = Console.lerStringObrigatorio("  Login (4-20 chars, letras/números/._): ");
        String senha = Console.lerStringObrigatorio("  Senha (mín. 6 chars): ");

        Console.subtitulo("Perfil de Acesso");
        Console.info("  [1] Administrador  [2] Gerente  [3] Colaborador");
        int perfOpt = Console.lerInteiro("  Perfil: ", 1, 3);
        Perfil perfil = switch (perfOpt) {
            case 1 -> Perfil.ADMINISTRADOR;
            case 2 -> Perfil.GERENTE;
            default -> Perfil.COLABORADOR;
        };

        String erro = service.cadastrar(nome, cpf, email, cargo, login, senha, perfil);
        if (erro == null) {
            Console.sucesso("Usuário cadastrado com sucesso!");
        } else {
            Console.erro(erro);
        }
        Console.pausar();
    }

    private void listar() {
        Console.limpar();
        Console.titulo("LISTA DE USUÁRIOS");
        List<Usuario> usuarios = service.listarTodos();
        if (usuarios.isEmpty()) {
            Console.aviso("Nenhum usuário cadastrado.");
        } else {
            Console.separador();
            for (Usuario u : usuarios) {
                Console.info(u.toString());
            }
            Console.separador();
            Console.info("Total: " + usuarios.size() + " usuário(s)");
        }
        Console.pausar();
    }

    private void editar() {
        Console.limpar();
        Console.titulo("EDITAR USUÁRIO");
        listarResumido();

        String id = Console.lerStringObrigatorio("  ID do usuário (8 primeiros chars): ");
        id = resolverIdParcial(id);
        if (id == null) { Console.pausar(); return; }

        Optional<Usuario> opt = service.buscarPorId(id);
        if (opt.isEmpty()) { Console.erro("Usuário não encontrado."); Console.pausar(); return; }

        Usuario u = opt.get();
        Console.subtitulo("Editando: " + u.getNomeCompleto());
        Console.info("  (ENTER para manter valor atual)");

        String nome = Console.lerStringObrigatorio("  Nome completo [" + u.getNomeCompleto() + "]: ");
        String email = Console.lerStringObrigatorio("  E-mail [" + u.getEmail() + "]: ");
        String cargo = Console.lerStringObrigatorio("  Cargo [" + u.getCargo() + "]: ");

        System.out.print(Console.CYAN + "  Nova senha (ENTER para manter): " + Console.RESET);
        String senha = Console.getScanner().nextLine().trim();

        Console.info("  Perfil atual: " + u.getPerfil().getDescricao());
        Console.info("  [1] Administrador  [2] Gerente  [3] Colaborador  [0] Manter");
        int perfOpt = Console.lerInteiro("  Novo perfil: ", 0, 3);
        Perfil perfil = switch (perfOpt) {
            case 1 -> Perfil.ADMINISTRADOR;
            case 2 -> Perfil.GERENTE;
            case 3 -> Perfil.COLABORADOR;
            default -> u.getPerfil();
        };

        String erro = service.atualizar(id, nome, email, cargo,
                senha.isBlank() ? null : senha, perfil);
        if (erro == null) Console.sucesso("Usuário atualizado com sucesso!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void desativar() {
        Console.limpar();
        Console.titulo("DESATIVAR USUÁRIO");
        listarResumido();

        String id = Console.lerStringObrigatorio("  ID do usuário: ");
        id = resolverIdParcial(id);
        if (id == null) { Console.pausar(); return; }

        if (!Console.confirmar("Confirma a desativação?")) {
            Console.aviso("Operação cancelada.");
            Console.pausar(); return;
        }
        String erro = service.desativar(id);
        if (erro == null) Console.sucesso("Usuário desativado!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void reativar() {
        Console.limpar();
        Console.titulo("REATIVAR USUÁRIO");
        List<Usuario> inativos = service.listarTodos().stream()
                .filter(u -> !u.isAtivo()).collect(java.util.stream.Collectors.toList());
        if (inativos.isEmpty()) { Console.aviso("Nenhum usuário inativo."); Console.pausar(); return; }
        Console.separador();
        inativos.forEach(u -> Console.info(u.toString()));
        Console.separador();

        String id = Console.lerStringObrigatorio("  ID do usuário: ");
        id = resolverIdParcial(id);
        if (id == null) { Console.pausar(); return; }

        String erro = service.reativar(id);
        if (erro == null) Console.sucesso("Usuário reativado!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void detalhes() {
        Console.limpar();
        Console.titulo("DETALHES DO USUÁRIO");
        listarResumido();

        String id = Console.lerStringObrigatorio("  ID do usuário: ");
        id = resolverIdParcial(id);
        if (id == null) { Console.pausar(); return; }

        Optional<Usuario> opt = service.buscarPorId(id);
        if (opt.isEmpty()) { Console.erro("Usuário não encontrado."); Console.pausar(); return; }

        Usuario u = opt.get();
        Console.separador();
        Console.info("  ID:           " + u.getId());
        Console.info("  Nome:         " + u.getNomeCompleto());
        Console.info("  CPF:          " + u.getCpf());
        Console.info("  E-mail:       " + u.getEmail());
        Console.info("  Cargo:        " + u.getCargo());
        Console.info("  Login:        " + u.getLogin());
        Console.info("  Perfil:       " + u.getPerfil().getDescricao());
        Console.info("  Status:       " + (u.isAtivo() ? "Ativo" : "Inativo"));
        Console.separador();
        Console.pausar();
    }

    private void listarResumido() {
        Console.separador();
        service.listarAtivos().forEach(u ->
                Console.info("  [" + u.getId().substring(0, 5) + "] " +
                        u.getNomeCompleto() + " - " + u.getPerfil().getDescricao()));
        Console.separador();
    }

    private String resolverIdParcial(String parcial) {
        if (parcial.length() == 36) return parcial;
        Optional<Usuario> match = service.listarTodos().stream()
                .filter(u -> u.getId().startsWith(parcial))
                .findFirst();
        if (match.isEmpty()) {
            Console.erro("Nenhum usuário encontrado com esse ID.");
            return null;
        }
        return match.get().getId();
    }
}
