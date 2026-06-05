package view;

import model.Perfil;
import model.Usuario;
import service.*;
import util.Console;

public class MenuPrincipal {
    private final UsuarioService usuarioService;
    private final ProjetoService projetoService;
    private final TarefaService tarefaService;
    private final EquipeService equipeService;
    private final AlocacaoService alocacaoService;

    public MenuPrincipal(UsuarioService usuarioService, ProjetoService projetoService,
                          TarefaService tarefaService, EquipeService equipeService,
                          AlocacaoService alocacaoService) {
        this.usuarioService = usuarioService;
        this.projetoService = projetoService;
        this.tarefaService = tarefaService;
        this.equipeService = equipeService;
        this.alocacaoService = alocacaoService;
    }

    public void iniciar() {
        while (true) {
            Usuario usuarioLogado = exibirLogin();
            if (usuarioLogado == null) {
                Console.limpar();
                Console.titulo("SISTEMA ENCERRADO");
                Console.info("  Até logo!");
                System.out.println();
                break;
            }
            exibirMenuPrincipal(usuarioLogado);
        }
    }

    private Usuario exibirLogin() {
        int tentativas = 0;
        while (tentativas < 5) {
            Console.limpar();
            exibirBanner();
            Console.separador();
            Console.info("  Acesse com seu login e senha.");
            Console.info("  Digite '0' para sair do sistema.");
            Console.separador();

            String login = Console.lerStringObrigatorio("  Login: ");
            if (login.equals("0")) return null;

            String senha = Console.lerStringObrigatorio("  Senha: ");

            var opt = usuarioService.autenticar(login, senha);
            if (opt.isPresent()) {
                Console.sucesso("Login realizado! Bem-vindo(a), " + opt.get().getNomeCompleto() + "!");
                Console.pausar();
                return opt.get();
            } else {
                tentativas++;
                Console.erro("Login ou senha inválidos. Tentativa " + tentativas + "/5.");
                Console.pausar();
            }
        }
        Console.erro("Número máximo de tentativas atingido. Sistema bloqueado.");
        Console.pausar();
        return null;
    }

    private void exibirMenuPrincipal(Usuario usuario) {
        int opcao;
        do {
            Console.limpar();
            Console.titulo("MENU PRINCIPAL");
            Console.info("  Usuário: " + Console.BOLD + usuario.getNomeCompleto() + Console.RESET);
            Console.info("  Perfil:  " + Console.CYAN + usuario.getPerfil().getDescricao() + Console.RESET);
            Console.separador();

            if (usuario.getPerfil() == Perfil.ADMINISTRADOR) {
                exibirMenuAdmin();
                opcao = Console.lerInteiro("  Opção: ", 0, 6);
            } else if (usuario.getPerfil() == Perfil.GERENTE) {
                exibirMenuGerente();
                opcao = Console.lerInteiro("  Opção: ", 0, 5);
            } else {
                exibirMenuColaborador();
                opcao = Console.lerInteiro("  Opção: ", 0, 3);
            }

            processarOpcao(opcao, usuario);
        } while (opcao != 0);
    }

    private void exibirMenuAdmin() {
        Console.info(Console.YELLOW + "  ╔══ ADMINISTRADOR ══╗" + Console.RESET);
        Console.info("  [1] Usuários");
        Console.info("  [2] Projetos");
        Console.info("  [3] Tarefas");
        Console.info("  [4] Equipes");
        Console.info("  [5] Alocações");
        Console.info("  [6] Relatórios");
        Console.info(Console.RED + "  [0] Sair do Sistema" + Console.RESET);
        Console.separador();
    }

    private void exibirMenuGerente() {
        Console.info(Console.CYAN + "  ╔══ GERENTE ══╗" + Console.RESET);
        Console.info("  [1] Projetos");
        Console.info("  [2] Tarefas");
        Console.info("  [3] Equipes");
        Console.info("  [4] Alocações");
        Console.info("  [5] Relatórios");
        Console.info(Console.RED + "  [0] Sair do Sistema" + Console.RESET);
        Console.separador();
    }

    private void exibirMenuColaborador() {
        Console.info(Console.GREEN + "  ╔══ COLABORADOR ══╗" + Console.RESET);
        Console.info("  [1] Minhas Tarefas");
        Console.info("  [2] Projetos");
        Console.info("  [3] Relatórios");
        Console.info(Console.RED + "  [0] Sair do Sistema" + Console.RESET);
        Console.separador();
    }

    private void processarOpcao(int opcao, Usuario usuario) {
        if (opcao == 0) return;

        if (usuario.getPerfil() == Perfil.ADMINISTRADOR) {
            switch (opcao) {
                case 1 -> new UsuarioView(usuarioService, usuario).exibirMenu();
                case 2 -> new ProjetoView(projetoService, usuarioService, usuario).exibirMenu();
                case 3 -> new TarefaView(tarefaService, projetoService, usuarioService, usuario).exibirMenu();
                case 4 -> new EquipeView(equipeService, usuarioService, usuario).exibirMenu();
                case 5 -> new AlocacaoView(alocacaoService, equipeService, projetoService, usuarioService, usuario).exibirMenu();
                case 6 -> new RelatorioView(projetoService, tarefaService, equipeService, alocacaoService, usuarioService, usuario).exibirMenu();
            }
        } else if (usuario.getPerfil() == Perfil.GERENTE) {
            switch (opcao) {
                case 1 -> new ProjetoView(projetoService, usuarioService, usuario).exibirMenu();
                case 2 -> new TarefaView(tarefaService, projetoService, usuarioService, usuario).exibirMenu();
                case 3 -> new EquipeView(equipeService, usuarioService, usuario).exibirMenu();
                case 4 -> new AlocacaoView(alocacaoService, equipeService, projetoService, usuarioService, usuario).exibirMenu();
                case 5 -> new RelatorioView(projetoService, tarefaService, equipeService, alocacaoService, usuarioService, usuario).exibirMenu();
            }
        } else { // COLABORADOR
            switch (opcao) {
                case 1 -> new TarefaView(tarefaService, projetoService, usuarioService, usuario).exibirMenu();
                case 2 -> new ProjetoView(projetoService, usuarioService, usuario).exibirMenu();
                case 3 -> new RelatorioView(projetoService, tarefaService, equipeService, alocacaoService, usuarioService, usuario).exibirMenu();
            }
        }
    }

    private void exibirBanner() {
        System.out.println(Console.BOLD + Console.BLUE);
        System.out.println("  ╔════════════════════════════════════════════════════╗");
        System.out.println("  ║                                                    ║");
        System.out.println("  ║      SISTEMA DE GESTÃO DE PROJETOS E EQUIPES       ║");
        System.out.println("  ║                                                    ║");
        System.out.println("  ║               Versão 1.0  -  Java 17               ║");
        System.out.println("  ║                                                    ║");
        System.out.println("  ╚════════════════════════════════════════════════════╝");
        System.out.println(Console.RESET);
    }
}
