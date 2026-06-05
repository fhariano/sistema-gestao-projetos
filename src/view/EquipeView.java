package view;

import java.util.List;
import java.util.Optional;
import model.Equipe;
import model.Usuario;
import service.EquipeService;
import service.UsuarioService;
import util.Console;

public class EquipeView {
    private final EquipeService service;
    private final UsuarioService usuarioService;
    private final Usuario usuarioLogado;

    public EquipeView(EquipeService service, UsuarioService usuarioService, Usuario usuarioLogado) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.usuarioLogado = usuarioLogado;
    }

    public void exibirMenu() {
        int opcao;
        do {
            Console.limpar();
            Console.titulo("GESTÃO DE EQUIPES");
            Console.menu("Cadastrar Equipe", "Listar Equipes", "Editar Equipe",
                    "Gerenciar Membros", "Desativar Equipe", "Detalhes da Equipe");
            opcao = Console.lerInteiro("  Opção: ", 0, 6);
            switch (opcao) {
                case 1 -> cadastrar();
                case 2 -> listar();
                case 3 -> editar();
                case 4 -> gerenciarMembros();
                case 5 -> desativar();
                case 6 -> detalhes();
            }
        } while (opcao != 0);
    }

    private void cadastrar() {
        Console.limpar();
        Console.titulo("CADASTRAR EQUIPE");

        String nome = Console.lerStringObrigatorio("  Nome da equipe: ");
        String descricao = Console.lerString("  Descrição (opcional): ");

        String erro = service.cadastrar(nome, descricao);
        if (erro == null) Console.sucesso("Equipe cadastrada com sucesso!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void listar() {
        Console.limpar();
        Console.titulo("LISTA DE EQUIPES");
        List<Equipe> equipes = service.listarTodas();
        Console.separador();
        if (equipes.isEmpty()) {
            Console.aviso("Nenhuma equipe cadastrada.");
        } else {
            equipes.forEach(e -> Console.info(e.toString()));
            Console.separador();
            Console.info("Total: " + equipes.size() + " equipe(s)");
        }
        Console.pausar();
    }

    private void editar() {
        Console.limpar();
        Console.titulo("EDITAR EQUIPE");
        listarResumido();

        String id = Console.lerStringObrigatorio("  ID da equipe: ");
        id = resolverIdEquipe(id);
        if (id == null) { Console.pausar(); return; }

        Optional<Equipe> opt = service.buscarPorId(id);
        if (opt.isEmpty()) { Console.erro("Equipe não encontrada."); Console.pausar(); return; }

        Equipe e = opt.get();
        Console.subtitulo("Editando: " + e.getNome());

        String nome = Console.lerStringObrigatorio("  Nome [" + e.getNome() + "]: ");
        String descricao = Console.lerString("  Descrição [" + e.getDescricao() + "]: ");

        String erro = service.atualizar(id, nome, descricao);
        if (erro == null) Console.sucesso("Equipe atualizada com sucesso!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void gerenciarMembros() {
        Console.limpar();
        Console.titulo("GERENCIAR MEMBROS");
        listarResumido();

        String id = Console.lerStringObrigatorio("  ID da equipe: ");
        id = resolverIdEquipe(id);
        if (id == null) { Console.pausar(); return; }

        Optional<Equipe> opt = service.buscarPorId(id);
        if (opt.isEmpty()) { Console.erro("Equipe não encontrada."); Console.pausar(); return; }

        Equipe equipe = opt.get();

        int opcao;
        do {
            Console.limpar();
            Console.titulo("MEMBROS: " + equipe.getNome());

            // Mostrar membros atuais
            Console.subtitulo("Membros Atuais");
            if (equipe.getMembroIds().isEmpty()) {
                Console.aviso("  Equipe sem membros.");
            } else {
                for (String membroId : equipe.getMembroIds()) {
                    Optional<Usuario> u = usuarioService.buscarPorId(membroId);
                    u.ifPresent(user -> Console.info("  [" + user.getId().substring(0, 5) + "] " +
                            user.getNomeCompleto() + " - " + user.getCargo()));
                }
            }

            Console.separador();
            Console.info("  [1] Adicionar membro  [2] Remover membro  [0] Voltar");
            Console.separador();
            opcao = Console.lerInteiro("  Opção: ", 0, 2);

            if (opcao == 1) adicionarMembro(equipe.getId());
            else if (opcao == 2) removerMembro(equipe.getId());

            // Recarregar equipe
            opt = service.buscarPorId(equipe.getId());
            if (opt.isPresent()) equipe = opt.get();

        } while (opcao != 0);
    }

    private void adicionarMembro(String equipeId) {
        Console.subtitulo("Adicionar Membro");
        List<Usuario> usuarios = usuarioService.listarAtivos();
        usuarios.forEach(u -> Console.info("  [" + u.getId().substring(0, 5) + "] " +
                u.getNomeCompleto() + " - " + u.getPerfil().getDescricao()));
        Console.separador();

        String usuarioId = Console.lerStringObrigatorio("  ID do usuário: ");
        usuarioId = resolverIdUsuario(usuarioId);
        if (usuarioId == null) { Console.pausar(); return; }

        String erro = service.adicionarMembro(equipeId, usuarioId);
        if (erro == null) Console.sucesso("Membro adicionado!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void removerMembro(String equipeId) {
        Console.subtitulo("Remover Membro");
        Optional<Equipe> opt = service.buscarPorId(equipeId);
        if (opt.isEmpty()) return;

        Equipe equipe = opt.get();
        if (equipe.getMembroIds().isEmpty()) { Console.aviso("Equipe sem membros."); Console.pausar(); return; }

        equipe.getMembroIds().forEach(mid -> {
            Optional<Usuario> u = usuarioService.buscarPorId(mid);
            u.ifPresent(user -> Console.info("  [" + user.getId().substring(0, 5) + "] " + user.getNomeCompleto()));
        });
        Console.separador();

        String usuarioId = Console.lerStringObrigatorio("  ID do usuário a remover: ");
        usuarioId = resolverIdUsuario(usuarioId);
        if (usuarioId == null) { Console.pausar(); return; }

        String erro = service.removerMembro(equipeId, usuarioId);
        if (erro == null) Console.sucesso("Membro removido!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void desativar() {
        Console.limpar();
        Console.titulo("DESATIVAR EQUIPE");
        listarResumido();

        String id = Console.lerStringObrigatorio("  ID da equipe: ");
        id = resolverIdEquipe(id);
        if (id == null) { Console.pausar(); return; }

        if (!Console.confirmar("Confirma a desativação?")) {
            Console.aviso("Operação cancelada."); Console.pausar(); return;
        }
        String erro = service.desativar(id);
        if (erro == null) Console.sucesso("Equipe desativada!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void detalhes() {
        Console.limpar();
        Console.titulo("DETALHES DA EQUIPE");
        listarResumido();

        String id = Console.lerStringObrigatorio("  ID da equipe: ");
        id = resolverIdEquipe(id);
        if (id == null) { Console.pausar(); return; }

        Optional<Equipe> opt = service.buscarPorId(id);
        if (opt.isEmpty()) { Console.erro("Equipe não encontrada."); Console.pausar(); return; }

        Equipe e = opt.get();
        Console.separador();
        Console.info("  ID:         " + e.getId());
        Console.info("  Nome:       " + e.getNome());
        Console.info("  Descrição:  " + e.getDescricao());
        Console.info("  Ativa:      " + (e.isAtiva() ? "Sim" : "Não"));
        Console.subtitulo("Membros (" + e.getMembroIds().size() + ")");
        e.getMembroIds().forEach(mid -> {
            Optional<Usuario> u = usuarioService.buscarPorId(mid);
            u.ifPresent(user -> Console.info("  » " + user.getNomeCompleto() +
                    " [" + user.getPerfil().getDescricao() + "] - " + user.getCargo()));
        });
        Console.separador();
        Console.pausar();
    }

    private void listarResumido() {
        Console.separador();
        service.listarAtivas().forEach(e ->
                Console.info("  [" + e.getId().substring(0, 5) + "] " +
                        e.getNome() + " | " + e.getMembroIds().size() + " membro(s)"));
        Console.separador();
    }

    private String resolverIdEquipe(String parcial) {
        if (parcial.length() == 36) return parcial;
        Optional<Equipe> match = service.listarTodas().stream()
                .filter(e -> e.getId().startsWith(parcial)).findFirst();
        if (match.isEmpty()) { Console.erro("Equipe não encontrada."); return null; }
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
