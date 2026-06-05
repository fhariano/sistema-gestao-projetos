package service;

import model.Projeto;
import model.StatusProjeto;
import model.Usuario;
import repository.ProjetoRepository;
import util.Validador;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ProjetoService {
    private final ProjetoRepository repository;
    private final UsuarioService usuarioService;

    public ProjetoService(ProjetoRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    public String cadastrar(String nome, String descricao, String dataInicioStr,
                             String dataTerminoStr, String gerenteId) {
        if (!Validador.naoVazio(nome)) return "Nome do projeto é obrigatório.";
        if (!Validador.naoVazio(descricao)) return "Descrição é obrigatória.";

        LocalDate dataInicio = Validador.parseData(dataInicioStr);
        if (dataInicio == null) return "Data de início inválida (use dd/MM/yyyy).";

        LocalDate dataTermino = Validador.parseData(dataTerminoStr);
        if (dataTermino == null) return "Data de término inválida (use dd/MM/yyyy).";

        if (!dataTermino.isAfter(dataInicio)) return "Data de término deve ser após a data de início.";

        Optional<Usuario> gerente = usuarioService.buscarPorId(gerenteId);
        if (gerente.isEmpty()) return "Gerente não encontrado.";

        Projeto projeto = new Projeto(nome, descricao, dataInicio, dataTermino, gerenteId);
        repository.salvar(projeto);
        return null;
    }

    public String atualizar(String id, String nome, String descricao, String dataInicioStr,
                             String dataTerminoStr, StatusProjeto status, String gerenteId) {
        Optional<Projeto> opt = repository.buscarPorId(id);
        if (opt.isEmpty()) return "Projeto não encontrado.";

        Projeto projeto = opt.get();
        if (!Validador.naoVazio(nome)) return "Nome do projeto é obrigatório.";

        LocalDate dataInicio = Validador.parseData(dataInicioStr);
        if (dataInicio == null) return "Data de início inválida.";

        LocalDate dataTermino = Validador.parseData(dataTerminoStr);
        if (dataTermino == null) return "Data de término inválida.";

        if (!dataTermino.isAfter(dataInicio)) return "Data de término deve ser após a data de início.";

        if (gerenteId != null && !gerenteId.isBlank()) {
            Optional<Usuario> gerente = usuarioService.buscarPorId(gerenteId);
            if (gerente.isEmpty()) return "Gerente não encontrado.";
            projeto.setGerenteId(gerenteId);
        }

        projeto.setNome(nome);
        projeto.setDescricao(descricao);
        projeto.setDataInicio(dataInicio);
        projeto.setDataTerminoPrevista(dataTermino);
        if (status != null) projeto.setStatus(status);

        repository.atualizar(projeto);
        return null;
    }

    public String excluir(String id) {
        Optional<Projeto> opt = repository.buscarPorId(id);
        if (opt.isEmpty()) return "Projeto não encontrado.";
        Projeto projeto = opt.get();
        projeto.setAtivo(false);
        repository.atualizar(projeto);
        return null;
    }

    public List<Projeto> listarTodos() { return repository.listarTodos(); }
    public List<Projeto> listarAtivos() { return repository.listarAtivos(); }
    public Optional<Projeto> buscarPorId(String id) { return repository.buscarPorId(id); }

    public List<Projeto> listarPorStatus(StatusProjeto status) {
        return repository.listarPorStatus(status);
    }

    public List<Projeto> listarPorGerente(String gerenteId) {
        return repository.listarPorGerente(gerenteId);
    }
}
