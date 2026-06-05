package service;

import model.Projeto;
import model.StatusTarefa;
import model.Tarefa;
import model.Usuario;
import repository.TarefaRepository;
import util.Validador;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TarefaService {
    private final TarefaRepository repository;
    private final ProjetoService projetoService;
    private final UsuarioService usuarioService;

    public TarefaService(TarefaRepository repository, ProjetoService projetoService,
                          UsuarioService usuarioService) {
        this.repository = repository;
        this.projetoService = projetoService;
        this.usuarioService = usuarioService;
    }

    public String cadastrar(String titulo, String descricao, String dataInicioStr,
                             String dataConclusaoStr, String projetoId,
                             String responsavelId, int prioridade) {
        if (!Validador.naoVazio(titulo)) return "Título é obrigatório.";

        LocalDate dataInicio = Validador.parseData(dataInicioStr);
        if (dataInicio == null) return "Data de início inválida (use dd/MM/yyyy).";

        LocalDate dataConclusao = Validador.parseData(dataConclusaoStr);
        if (dataConclusao == null) return "Data de conclusão inválida (use dd/MM/yyyy).";

        if (!dataConclusao.isAfter(dataInicio)) return "Data de conclusão deve ser após a data de início.";

        Optional<Projeto> projeto = projetoService.buscarPorId(projetoId);
        if (projeto.isEmpty()) return "Projeto não encontrado.";

        if (responsavelId != null && !responsavelId.isBlank()) {
            Optional<Usuario> responsavel = usuarioService.buscarPorId(responsavelId);
            if (responsavel.isEmpty()) return "Responsável não encontrado.";
        }

        if (prioridade < 1 || prioridade > 3) return "Prioridade inválida (1=Baixa, 2=Média, 3=Alta).";

        Tarefa tarefa = new Tarefa(titulo, descricao, dataInicio, dataConclusao,
                projetoId, responsavelId, prioridade);
        repository.salvar(tarefa);
        return null;
    }

    public String atualizar(String id, String titulo, String descricao, String dataConclusaoStr,
                             StatusTarefa status, String responsavelId, int prioridade) {
        Optional<Tarefa> opt = repository.buscarPorId(id);
        if (opt.isEmpty()) return "Tarefa não encontrada.";

        Tarefa tarefa = opt.get();
        if (!Validador.naoVazio(titulo)) return "Título é obrigatório.";

        if (dataConclusaoStr != null && !dataConclusaoStr.isBlank()) {
            LocalDate dataConclusao = Validador.parseData(dataConclusaoStr);
            if (dataConclusao == null) return "Data de conclusão inválida.";
            tarefa.setDataPrevistaConclusao(dataConclusao);
        }

        if (responsavelId != null && !responsavelId.isBlank()) {
            Optional<Usuario> responsavel = usuarioService.buscarPorId(responsavelId);
            if (responsavel.isEmpty()) return "Responsável não encontrado.";
            tarefa.setResponsavelId(responsavelId);
        }

        tarefa.setTitulo(titulo);
        tarefa.setDescricao(descricao);
        if (status != null) tarefa.setStatus(status);
        if (prioridade >= 1 && prioridade <= 3) tarefa.setPrioridade(prioridade);

        repository.atualizar(tarefa);
        return null;
    }

    public String excluir(String id) {
        Optional<Tarefa> opt = repository.buscarPorId(id);
        if (opt.isEmpty()) return "Tarefa não encontrada.";
        Tarefa tarefa = opt.get();
        tarefa.setAtiva(false);
        repository.atualizar(tarefa);
        return null;
    }

    public List<Tarefa> listarPorProjeto(String projetoId) {
        return repository.listarPorProjeto(projetoId);
    }

    public List<Tarefa> listarPorResponsavel(String responsavelId) {
        return repository.listarPorResponsavel(responsavelId);
    }

    public List<Tarefa> listarTodas() { return repository.listarTodas(); }

    public Optional<Tarefa> buscarPorId(String id) { return repository.buscarPorId(id); }
}
