package view;

import java.util.List;
import java.util.Optional;
import model.Alocacao;
import model.Equipe;
import model.Projeto;
import model.Usuario;
import service.AlocacaoService;
import service.EquipeService;
import service.ProjetoService;
import service.UsuarioService;
import util.Console;

public class AlocacaoView {
    private final AlocacaoService service;
    private final EquipeService equipeService;
    private final ProjetoService projetoService;
    private final UsuarioService usuarioService;
    private final Usuario usuarioLogado;

    public AlocacaoView(AlocacaoService service, EquipeService equipeService,
                         ProjetoService projetoService, UsuarioService usuarioService,
                         Usuario usuarioLogado) {
        this.service = service;
        this.equipeService = equipeService;
        this.projetoService = projetoService;
        this.usuarioService = usuarioService;
        this.usuarioLogado = usuarioLogado;
    }

    public void exibirMenu() {
        int opcao;
        do {
            Console.limpar();
            Console.titulo("ALOCAÇÃO DE EQUIPES");
            Console.menu("Alocar Equipe em Projeto", "Desalocar Equipe",
                    "Equipes por Projeto", "Projetos por Equipe", "Todas as Alocações");
            opcao = Console.lerInteiro("  Opção: ", 0, 5);
            switch (opcao) {
                case 1 -> alocar();
                case 2 -> desalocar();
                case 3 -> equipesPorProjeto();
                case 4 -> projetosPorEquipe();
                case 5 -> listarTodas();
            }
        } while (opcao != 0);
    }

    private void alocar() {
        Console.limpar();
        Console.titulo("ALOCAR EQUIPE EM PROJETO");

        Console.subtitulo("Selecionar Projeto");
        List<Projeto> projetos = projetoService.listarAtivos();
        if (projetos.isEmpty()) { Console.erro("Nenhum projeto ativo."); Console.pausar(); return; }
        projetos.forEach(p -> Console.info("  [" + p.getId().substring(0, 5) + "] " + p.getNome()));
        Console.separador();
        String projetoId = Console.lerStringObrigatorio("  ID do projeto: ");
        projetoId = resolverIdProjeto(projetoId);
        if (projetoId == null) { Console.pausar(); return; }

        Console.subtitulo("Selecionar Equipe");
        List<Equipe> equipes = equipeService.listarAtivas();
        if (equipes.isEmpty()) { Console.erro("Nenhuma equipe ativa."); Console.pausar(); return; }
        equipes.forEach(e -> Console.info("  [" + e.getId().substring(0, 5) + "] " + e.getNome()));
        Console.separador();
        String equipeId = Console.lerStringObrigatorio("  ID da equipe: ");
        equipeId = resolverIdEquipe(equipeId);
        if (equipeId == null) { Console.pausar(); return; }

        String observacao = Console.lerString("  Observação (opcional): ");

        String erro = service.alocar(equipeId, projetoId, observacao);
        if (erro == null) Console.sucesso("Equipe alocada com sucesso!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void desalocar() {
        Console.limpar();
        Console.titulo("DESALOCAR EQUIPE");
        List<Alocacao> ativas = service.listarTodas().stream()
                .filter(Alocacao::isAtiva).collect(java.util.stream.Collectors.toList());

        if (ativas.isEmpty()) { Console.aviso("Nenhuma alocação ativa."); Console.pausar(); return; }

        Console.separador();
        for (Alocacao a : ativas) {
            Optional<Equipe> eq = equipeService.buscarPorId(a.getEquipeId());
            Optional<Projeto> pr = projetoService.buscarPorId(a.getProjetoId());
            Console.info("  [" + a.getId().substring(0, 5) + "] " +
                    eq.map(Equipe::getNome).orElse("?") + " → " +
                    pr.map(Projeto::getNome).orElse("?") +
                    " | Desde: " + a.getDataAlocacao());
        }
        Console.separador();

        String id = Console.lerStringObrigatorio("  ID da alocação: ");
        id = resolverIdAlocacao(id);
        if (id == null) { Console.pausar(); return; }

        if (!Console.confirmar("Confirma a desalocação?")) {
            Console.aviso("Operação cancelada."); Console.pausar(); return;
        }
        String erro = service.desalocar(id);
        if (erro == null) Console.sucesso("Equipe desalocada com sucesso!");
        else Console.erro(erro);
        Console.pausar();
    }

    private void equipesPorProjeto() {
        Console.limpar();
        Console.titulo("EQUIPES POR PROJETO");

        List<Projeto> projetos = projetoService.listarAtivos();
        projetos.forEach(p -> Console.info("  [" + p.getId().substring(0, 5) + "] " + p.getNome()));
        Console.separador();
        String projetoId = Console.lerStringObrigatorio("  ID do projeto: ");
        projetoId = resolverIdProjeto(projetoId);
        if (projetoId == null) { Console.pausar(); return; }

        List<Alocacao> alocacoes = service.listarPorProjeto(projetoId);
        Console.separador();
        if (alocacoes.isEmpty()) {
            Console.aviso("Nenhuma equipe alocada neste projeto.");
        } else {
            Optional<Projeto> proj = projetoService.buscarPorId(projetoId);
            Console.info("  Projeto: " + proj.map(Projeto::getNome).orElse("?"));
            Console.separador();
            for (Alocacao a : alocacoes) {
                Optional<Equipe> eq = equipeService.buscarPorId(a.getEquipeId());
                Console.info("  » " + eq.map(Equipe::getNome).orElse("?") +
                        " | Desde: " + a.getDataAlocacao() +
                        (a.getObservacao() != null && !a.getObservacao().isBlank() ?
                                " | Obs: " + a.getObservacao() : ""));
            }
        }
        Console.pausar();
    }

    private void projetosPorEquipe() {
        Console.limpar();
        Console.titulo("PROJETOS POR EQUIPE");

        List<Equipe> equipes = equipeService.listarAtivas();
        equipes.forEach(e -> Console.info("  [" + e.getId().substring(0, 5) + "] " + e.getNome()));
        Console.separador();
        String equipeId = Console.lerStringObrigatorio("  ID da equipe: ");
        equipeId = resolverIdEquipe(equipeId);
        if (equipeId == null) { Console.pausar(); return; }

        List<Alocacao> alocacoes = service.listarPorEquipe(equipeId);
        Console.separador();
        if (alocacoes.isEmpty()) {
            Console.aviso("Esta equipe não está alocada em nenhum projeto.");
        } else {
            Optional<Equipe> eq = equipeService.buscarPorId(equipeId);
            Console.info("  Equipe: " + eq.map(Equipe::getNome).orElse("?"));
            Console.separador();
            for (Alocacao a : alocacoes) {
                Optional<Projeto> pr = projetoService.buscarPorId(a.getProjetoId());
                Console.info("  » " + pr.map(Projeto::getNome).orElse("?") +
                        " [" + pr.map(p -> p.getStatus().getDescricao()).orElse("?") + "]" +
                        " | Desde: " + a.getDataAlocacao());
            }
        }
        Console.pausar();
    }

    private void listarTodas() {
        Console.limpar();
        Console.titulo("TODAS AS ALOCAÇÕES");
        List<Alocacao> todas = service.listarTodas();
        Console.separador();
        if (todas.isEmpty()) {
            Console.aviso("Nenhuma alocação registrada.");
        } else {
            for (Alocacao a : todas) {
                Optional<Equipe> eq = equipeService.buscarPorId(a.getEquipeId());
                Optional<Projeto> pr = projetoService.buscarPorId(a.getProjetoId());
                Console.info("  [" + a.getId().substring(0, 5) + "] " +
                        eq.map(Equipe::getNome).orElse("?") + " → " +
                        pr.map(Projeto::getNome).orElse("?") +
                        " | " + (a.isAtiva() ? "Ativa" : "Encerrada") +
                        " | " + a.getDataAlocacao());
            }
            Console.separador();
            Console.info("Total: " + todas.size() + " alocação(ões)");
        }
        Console.pausar();
    }

    private String resolverIdProjeto(String parcial) {
        if (parcial.length() == 36) return parcial;
        Optional<Projeto> match = projetoService.listarTodos().stream()
                .filter(p -> p.getId().startsWith(parcial)).findFirst();
        if (match.isEmpty()) { Console.erro("Projeto não encontrado."); return null; }
        return match.get().getId();
    }

    private String resolverIdEquipe(String parcial) {
        if (parcial.length() == 36) return parcial;
        Optional<Equipe> match = equipeService.listarTodas().stream()
                .filter(e -> e.getId().startsWith(parcial)).findFirst();
        if (match.isEmpty()) { Console.erro("Equipe não encontrada."); return null; }
        return match.get().getId();
    }

    private String resolverIdAlocacao(String parcial) {
        if (parcial.length() == 36) return parcial;
        Optional<Alocacao> match = service.listarTodas().stream()
                .filter(a -> a.getId().startsWith(parcial)).findFirst();
        if (match.isEmpty()) { Console.erro("Alocação não encontrada."); return null; }
        return match.get().getId();
    }
}
