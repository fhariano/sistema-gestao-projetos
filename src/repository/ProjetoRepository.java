package repository;

import model.Projeto;
import model.StatusProjeto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjetoRepository {
    private static final String ARQUIVO = "data/projetos.dat";
    private ArrayList<Projeto> projetos;

    public ProjetoRepository() {
        this.projetos = carregarDados();
    }

    public void salvar(Projeto projeto) {
        projetos.add(projeto);
        persistir();
    }

    public void atualizar(Projeto projeto) {
        for (int i = 0; i < projetos.size(); i++) {
            if (projetos.get(i).getId().equals(projeto.getId())) {
                projetos.set(i, projeto);
                persistir();
                return;
            }
        }
    }

    public void deletar(String id) {
        projetos.removeIf(p -> p.getId().equals(id));
        persistir();
    }

    public Optional<Projeto> buscarPorId(String id) {
        return projetos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public List<Projeto> listarTodos() {
        return new ArrayList<>(projetos);
    }

    public List<Projeto> listarAtivos() {
        return projetos.stream()
                .filter(Projeto::isAtivo)
                .collect(Collectors.toList());
    }

    public List<Projeto> listarPorStatus(StatusProjeto status) {
        return projetos.stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Projeto> listarPorGerente(String gerenteId) {
        return projetos.stream()
                .filter(p -> p.getGerenteId().equals(gerenteId))
                .collect(Collectors.toList());
    }

    private void persistir() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            oos.writeObject(projetos);
        } catch (IOException e) {
            System.err.println("Erro ao salvar projetos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Projeto> carregarDados() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (ArrayList<Projeto>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
