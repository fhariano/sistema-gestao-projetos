package service;

import model.Alocacao;
import model.Equipe;
import model.Projeto;
import repository.AlocacaoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AlocacaoService {
    private final AlocacaoRepository repository;
    private final EquipeService equipeService;
    private final ProjetoService projetoService;

    public AlocacaoService(AlocacaoRepository repository, EquipeService equipeService,
                            ProjetoService projetoService) {
        this.repository = repository;
        this.equipeService = equipeService;
        this.projetoService = projetoService;
    }

    public String alocar(String equipeId, String projetoId, String observacao) {
        Optional<Equipe> optEquipe = equipeService.buscarPorId(equipeId);
        if (optEquipe.isEmpty()) return "Equipe não encontrada.";

        Optional<Projeto> optProjeto = projetoService.buscarPorId(projetoId);
        if (optProjeto.isEmpty()) return "Projeto não encontrado.";

        if (repository.alocacaoAtivExiste(equipeId, projetoId))
            return "Esta equipe já está alocada neste projeto.";

        Alocacao alocacao = new Alocacao(equipeId, projetoId, observacao);
        repository.salvar(alocacao);
        return null;
    }

    public String desalocar(String alocacaoId) {
        Optional<Alocacao> opt = repository.buscarPorId(alocacaoId);
        if (opt.isEmpty()) return "Alocação não encontrada.";
        Alocacao alocacao = opt.get();
        if (!alocacao.isAtiva()) return "Alocação já está encerrada.";
        alocacao.setAtiva(false);
        alocacao.setDataDesalocacao(LocalDate.now());
        repository.atualizar(alocacao);
        return null;
    }

    public List<Alocacao> listarPorProjeto(String projetoId) {
        return repository.listarAtivasPorProjeto(projetoId);
    }

    public List<Alocacao> listarPorEquipe(String equipeId) {
        return repository.listarAtivasPorEquipe(equipeId);
    }

    public List<Alocacao> listarTodas() {
        return repository.listarTodas();
    }

    public Optional<Alocacao> buscarPorId(String id) {
        return repository.buscarPorId(id);
    }
}
