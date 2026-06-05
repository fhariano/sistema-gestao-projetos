package service;

import model.Equipe;
import model.Usuario;
import repository.EquipeRepository;
import util.Validador;

import java.util.List;
import java.util.Optional;

public class EquipeService {
    private final EquipeRepository repository;
    private final UsuarioService usuarioService;

    public EquipeService(EquipeRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    public String cadastrar(String nome, String descricao) {
        if (!Validador.naoVazio(nome)) return "Nome da equipe é obrigatório.";
        Equipe equipe = new Equipe(nome, descricao);
        repository.salvar(equipe);
        return null;
    }

    public String atualizar(String id, String nome, String descricao) {
        Optional<Equipe> opt = repository.buscarPorId(id);
        if (opt.isEmpty()) return "Equipe não encontrada.";
        if (!Validador.naoVazio(nome)) return "Nome da equipe é obrigatório.";
        Equipe equipe = opt.get();
        equipe.setNome(nome);
        equipe.setDescricao(descricao);
        repository.atualizar(equipe);
        return null;
    }

    public String desativar(String id) {
        Optional<Equipe> opt = repository.buscarPorId(id);
        if (opt.isEmpty()) return "Equipe não encontrada.";
        Equipe equipe = opt.get();
        equipe.setAtiva(false);
        repository.atualizar(equipe);
        return null;
    }

    public String adicionarMembro(String equipeId, String usuarioId) {
        Optional<Equipe> optEquipe = repository.buscarPorId(equipeId);
        if (optEquipe.isEmpty()) return "Equipe não encontrada.";

        Optional<Usuario> optUsuario = usuarioService.buscarPorId(usuarioId);
        if (optUsuario.isEmpty()) return "Usuário não encontrado.";

        Equipe equipe = optEquipe.get();
        if (equipe.contemMembro(usuarioId)) return "Usuário já é membro desta equipe.";

        equipe.adicionarMembro(usuarioId);
        repository.atualizar(equipe);
        return null;
    }

    public String removerMembro(String equipeId, String usuarioId) {
        Optional<Equipe> optEquipe = repository.buscarPorId(equipeId);
        if (optEquipe.isEmpty()) return "Equipe não encontrada.";

        Equipe equipe = optEquipe.get();
        if (!equipe.contemMembro(usuarioId)) return "Usuário não é membro desta equipe.";

        equipe.removerMembro(usuarioId);
        repository.atualizar(equipe);
        return null;
    }

    public List<Equipe> listarTodas() { return repository.listarTodas(); }
    public List<Equipe> listarAtivas() { return repository.listarAtivas(); }
    public Optional<Equipe> buscarPorId(String id) { return repository.buscarPorId(id); }

    public List<Equipe> listarEquipesComMembro(String usuarioId) {
        return repository.listarEquipesComMembro(usuarioId);
    }
}
