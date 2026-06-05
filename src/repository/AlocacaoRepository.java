package repository;

import model.Alocacao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AlocacaoRepository {
    private static final String ARQUIVO = "data/alocacoes.dat";
    private ArrayList<Alocacao> alocacoes;

    public AlocacaoRepository() {
        this.alocacoes = carregarDados();
    }

    public void salvar(Alocacao alocacao) {
        alocacoes.add(alocacao);
        persistir();
    }

    public void atualizar(Alocacao alocacao) {
        for (int i = 0; i < alocacoes.size(); i++) {
            if (alocacoes.get(i).getId().equals(alocacao.getId())) {
                alocacoes.set(i, alocacao);
                persistir();
                return;
            }
        }
    }

    public void deletar(String id) {
        alocacoes.removeIf(a -> a.getId().equals(id));
        persistir();
    }

    public Optional<Alocacao> buscarPorId(String id) {
        return alocacoes.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }

    public List<Alocacao> listarTodas() {
        return new ArrayList<>(alocacoes);
    }

    public List<Alocacao> listarAtivasPorProjeto(String projetoId) {
        return alocacoes.stream()
                .filter(a -> a.getProjetoId().equals(projetoId) && a.isAtiva())
                .collect(Collectors.toList());
    }

    public List<Alocacao> listarAtivasPorEquipe(String equipeId) {
        return alocacoes.stream()
                .filter(a -> a.getEquipeId().equals(equipeId) && a.isAtiva())
                .collect(Collectors.toList());
    }

    public boolean alocacaoAtivExiste(String equipeId, String projetoId) {
        return alocacoes.stream()
                .anyMatch(a -> a.getEquipeId().equals(equipeId) &&
                               a.getProjetoId().equals(projetoId) && a.isAtiva());
    }

    private void persistir() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            oos.writeObject(alocacoes);
        } catch (IOException e) {
            System.err.println("Erro ao salvar alocações: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Alocacao> carregarDados() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (ArrayList<Alocacao>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
