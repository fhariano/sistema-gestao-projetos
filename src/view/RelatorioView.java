package view;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import model.*;
import service.*;
import util.Console;

public class RelatorioView {
    private final ProjetoService projetoService;
    private final TarefaService tarefaService;
    private final EquipeService equipeService;
    private final AlocacaoService alocacaoService;
    private final UsuarioService usuarioService;
    private final Usuario usuarioLogado;

    public RelatorioView(ProjetoService projetoService, TarefaService tarefaService,
                          EquipeService equipeService, AlocacaoService alocacaoService,
                          UsuarioService usuarioService, Usuario usuarioLogado) {
        this.projetoService = projetoService;
        this.tarefaService = tarefaService;
        this.equipeService = equipeService;
        this.alocacaoService = alocacaoService;
        this.usuarioService = usuarioService;
        this.usuarioLogado = usuarioLogado;
    }

    public void exibirMenu() {
        int opcao;
        do {
            Console.limpar();
            Console.titulo("RELATÓRIOS");
            Console.menu("Visão Geral de Projetos",
                    "Acompanhamento de Tarefas por Projeto",
                    "Projetos com Atraso",
                    "Relatório de Equipes",
                    "Resumo por Status");
            opcao = Console.lerInteiro("  Opção: ", 0, 5);
            switch (opcao) {
                case 1 -> visaoGeralProjetos();
                case 2 -> acompanhamentoTarefas();
                case 3 -> projetosComAtraso();
                case 4 -> relatorioEquipes();
                case 5 -> resumoPorStatus();
            }
        } while (opcao != 0);
    }

    private void visaoGeralProjetos() {
        Console.limpar();
        Console.titulo("VISÃO GERAL DE PROJETOS");

        List<Projeto> projetos = projetoService.listarAtivos();

        if (usuarioLogado.getPerfil() == Perfil.GERENTE) {
            projetos = projetos.stream()
                    .filter(p -> p.getGerenteId().equals(usuarioLogado.getId()))
                    .collect(Collectors.toList());
        }

        Console.separador();
        Console.info(String.format("  %-30s %-15s %-12s %-12s %-10s",
                "PROJETO", "STATUS", "INÍCIO", "TÉRMINO", "EQUIPES"));
        Console.separador();

        for (Projeto p : projetos) {
            long qtdEquipes = alocacaoService.listarPorProjeto(p.getId()).size();
            List<Tarefa> tarefas = tarefaService.listarPorProjeto(p.getId());
            long concluidas = tarefas.stream()
                    .filter(t -> t.getStatus() == StatusTarefa.CONCLUIDA).count();

            Console.info(String.format("  %-30s %-15s %-12s %-12s %-3d eq / %d/%d tarefas",
                    truncar(p.getNome(), 28),
                    p.getStatus().getDescricao(),
                    p.getDataInicio().toString(),
                    p.getDataTerminoPrevista().toString(),
                    qtdEquipes, concluidas, tarefas.size()));
        }
        Console.separador();
        Console.info("  Total de projetos: " + projetos.size());
        Console.pausar();
    }

    private void acompanhamentoTarefas() {
        Console.limpar();
        Console.titulo("ACOMPANHAMENTO DE TAREFAS");

        List<Projeto> projetos = projetoService.listarAtivos();
        if (projetos.isEmpty()) { Console.aviso("Nenhum projeto ativo."); Console.pausar(); return; }

        projetos.forEach(p -> Console.info("  [" + p.getId().substring(0, 5) + "] " + p.getNome()));
        Console.separador();

        String projetoId = Console.lerStringObrigatorio("  ID do projeto: ");
        Optional<Projeto> optP = projetos.stream()
                .filter(p -> p.getId().startsWith(projetoId) || p.getId().equals(projetoId))
                .findFirst();
        if (optP.isEmpty()) { Console.erro("Projeto não encontrado."); Console.pausar(); return; }

        Projeto projeto = optP.get();
        List<Tarefa> tarefas = tarefaService.listarPorProjeto(projeto.getId());

        Console.limpar();
        Console.titulo("TAREFAS: " + projeto.getNome());

        if (tarefas.isEmpty()) {
            Console.aviso("  Nenhuma tarefa cadastrada para este projeto.");
            Console.pausar(); return;
        }

        // Agrupar por status
        Map<StatusTarefa, List<Tarefa>> porStatus = tarefas.stream()
                .collect(Collectors.groupingBy(Tarefa::getStatus));

        for (StatusTarefa status : StatusTarefa.values()) {
            List<Tarefa> grupo = porStatus.getOrDefault(status, List.of());
            if (!grupo.isEmpty()) {
                Console.subtitulo(status.getDescricao() + " (" + grupo.size() + ")");
                for (Tarefa t : grupo) {
                    Console.info("  » " + t.getTitulo() + " | Prazo: " + t.getDataPrevistaConclusao()
                            + " | Prioridade: " + t.getPrioridadeDescricao());
                    if (t.getResponsavelId() != null) {
                        Optional<Usuario> resp = usuarioService.buscarPorId(t.getResponsavelId());
                        resp.ifPresent(u -> Console.info("     Responsável: " + u.getNomeCompleto()));
                    }
                    boolean atrasada = !t.getStatus().equals(StatusTarefa.CONCLUIDA) &&
                            t.getDataPrevistaConclusao().isBefore(LocalDate.now());
                    if (atrasada) Console.info(Console.RED + "     ⚠ ATRASADA!" + Console.RESET);
                }
            }
        }

        // Percentual de conclusão
        long concluidas = tarefas.stream().filter(t -> t.getStatus() == StatusTarefa.CONCLUIDA).count();
        double perc = tarefas.isEmpty() ? 0 : (double) concluidas / tarefas.size() * 100;

        Console.separador();
        Console.info(String.format("  Progresso: %d/%d tarefas concluídas (%.1f%%)",
                concluidas, tarefas.size(), perc));
        barraProgresso(perc);
        Console.pausar();
    }

    private void projetosComAtraso() {
        Console.limpar();
        Console.titulo("PROJETOS COM ATRASO");
        LocalDate hoje = LocalDate.now();

        List<Projeto> atrasados = projetoService.listarAtivos().stream()
                .filter(p -> p.getStatus() != StatusProjeto.CONCLUIDO &&
                             p.getStatus() != StatusProjeto.CANCELADO &&
                             p.getDataTerminoPrevista().isBefore(hoje))
                .collect(Collectors.toList());

        Console.separador();
        if (atrasados.isEmpty()) {
            Console.sucesso("Nenhum projeto em atraso!");
        } else {
            Console.aviso("  " + atrasados.size() + " projeto(s) com prazo vencido:");
            Console.separador();
            for (Projeto p : atrasados) {
                long diasAtraso = hoje.toEpochDay() - p.getDataTerminoPrevista().toEpochDay();
                Optional<Usuario> gerente = usuarioService.buscarPorId(p.getGerenteId());
                Console.info(Console.RED + "  ✘ " + p.getNome() + Console.RESET);
                Console.info("     Status: " + p.getStatus().getDescricao() +
                        " | Prazo: " + p.getDataTerminoPrevista() +
                        " | Atraso: " + diasAtraso + " dia(s)");
                Console.info("     Gerente: " + gerente.map(Usuario::getNomeCompleto).orElse("N/A"));
            }
        }
        Console.pausar();
    }

    private void relatorioEquipes() {
        Console.limpar();
        Console.titulo("RELATÓRIO DE EQUIPES");

        List<Equipe> equipes = equipeService.listarAtivas();
        Console.separador();

        if (equipes.isEmpty()) {
            Console.aviso("Nenhuma equipe ativa.");
        } else {
            for (Equipe e : equipes) {
                Console.info(Console.BOLD + "  -> " + e.getNome() + Console.RESET);
                Console.info("     Descrição: " + e.getDescricao());
                Console.info("     Membros: " + e.getMembroIds().size());

                List<Alocacao> alocacoes = alocacaoService.listarPorEquipe(e.getId());
                Console.info("     Projetos ativos: " + alocacoes.size());
                alocacoes.forEach(a -> {
                    Optional<Projeto> p = projetoService.buscarPorId(a.getProjetoId());
                    p.ifPresent(proj -> Console.info("       - " + proj.getNome() +
                            " [" + proj.getStatus().getDescricao() + "]"));
                });
                Console.separador();
            }
        }
        Console.pausar();
    }

    private void resumoPorStatus() {
        Console.limpar();
        Console.titulo("RESUMO POR STATUS");

        List<Projeto> todos = projetoService.listarTodos();
        Console.separador();
        for (StatusProjeto status : StatusProjeto.values()) {
            long count = todos.stream().filter(p -> p.getStatus() == status).count();
            Console.info(String.format("  %-15s: %d projeto(s)", status.getDescricao(), count));
        }
        Console.separador();

        List<Tarefa> tarefas = tarefaService.listarTodas().stream()
                .filter(Tarefa::isAtiva).collect(Collectors.toList());
        Console.subtitulo("Tarefas");
        for (StatusTarefa status : StatusTarefa.values()) {
            long count = tarefas.stream().filter(t -> t.getStatus() == status).count();
            Console.info(String.format("  %-15s: %d tarefa(s)", status.getDescricao(), count));
        }
        Console.separador();

        long atrasadas = tarefas.stream()
                .filter(t -> t.getStatus() != StatusTarefa.CONCLUIDA &&
                             t.getDataPrevistaConclusao().isBefore(LocalDate.now()))
                .count();
        Console.info(Console.RED + "  Tarefas atrasadas: " + atrasadas + Console.RESET);
        Console.pausar();
    }

    private void barraProgresso(double percent) {
        int total = 40;
        int preenchido = (int) (percent / 100 * total);
        String barra = "  [" + "█".repeat(preenchido) + "░".repeat(total - preenchido) + "] " +
                String.format("%.1f%%", percent);
        Console.info(Console.GREEN + barra + Console.RESET);
    }

    private String truncar(String texto, int max) {
        if (texto == null) return "";
        return texto.length() > max ? texto.substring(0, max - 1) + "…" : texto;
    }
}
