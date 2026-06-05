package repository;

import model.StatusTarefa;
import model.Tarefa;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TarefaRepository {
    private static final String ARQUIVO = "data/tarefas.dat";
    private ArrayList<Tarefa> tarefas;

    public TarefaRepository() {
        this.tarefas = carregarDados();
    }

    public void salvar(Tarefa tarefa) {
        tarefas.add(tarefa);
        persistir();
    }

    public void atualizar(Tarefa tarefa) {
        for (int i = 0; i < tarefas.size(); i++) {
            if (tarefas.get(i).getId().equals(tarefa.getId())) {
                tarefas.set(i, tarefa);
                persistir();
                return;
            }
        }
    }

    public void deletar(String id) {
        tarefas.removeIf(t -> t.getId().equals(id));
        persistir();
    }

    public Optional<Tarefa> buscarPorId(String id) {
        return tarefas.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    public List<Tarefa> listarTodas() {
        return new ArrayList<>(tarefas);
    }

    public List<Tarefa> listarPorProjeto(String projetoId) {
        return tarefas.stream()
                .filter(t -> t.getProjetoId().equals(projetoId) && t.isAtiva())
                .collect(Collectors.toList());
    }

    public List<Tarefa> listarPorResponsavel(String responsavelId) {
        return tarefas.stream()
                .filter(t -> t.getResponsavelId() != null &&
                             t.getResponsavelId().equals(responsavelId) && t.isAtiva())
                .collect(Collectors.toList());
    }

    public List<Tarefa> listarPorStatus(StatusTarefa status) {
        return tarefas.stream()
                .filter(t -> t.getStatus() == status && t.isAtiva())
                .collect(Collectors.toList());
    }

    public List<Tarefa> listarAtivasPorProjeto(String projetoId) {
        return tarefas.stream()
                .filter(t -> t.getProjetoId().equals(projetoId) && t.isAtiva())
                .collect(Collectors.toList());
    }

    private void persistir() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            oos.writeObject(tarefas);
        } catch (IOException e) {
            System.err.println("Erro ao salvar tarefas: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Tarefa> carregarDados() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (ArrayList<Tarefa>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
