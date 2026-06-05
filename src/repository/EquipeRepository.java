package repository;

import model.Equipe;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EquipeRepository {
    private static final String ARQUIVO = "data/equipes.dat";
    private ArrayList<Equipe> equipes;

    public EquipeRepository() {
        this.equipes = carregarDados();
    }

    public void salvar(Equipe equipe) {
        equipes.add(equipe);
        persistir();
    }

    public void atualizar(Equipe equipe) {
        for (int i = 0; i < equipes.size(); i++) {
            if (equipes.get(i).getId().equals(equipe.getId())) {
                equipes.set(i, equipe);
                persistir();
                return;
            }
        }
    }

    public void deletar(String id) {
        equipes.removeIf(e -> e.getId().equals(id));
        persistir();
    }

    public Optional<Equipe> buscarPorId(String id) {
        return equipes.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    public List<Equipe> listarTodas() {
        return new ArrayList<>(equipes);
    }

    public List<Equipe> listarAtivas() {
        return equipes.stream()
                .filter(Equipe::isAtiva)
                .collect(Collectors.toList());
    }

    public List<Equipe> listarEquipesComMembro(String usuarioId) {
        return equipes.stream()
                .filter(e -> e.contemMembro(usuarioId) && e.isAtiva())
                .collect(Collectors.toList());
    }

    private void persistir() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            oos.writeObject(equipes);
        } catch (IOException e) {
            System.err.println("Erro ao salvar equipes: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Equipe> carregarDados() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (ArrayList<Equipe>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
