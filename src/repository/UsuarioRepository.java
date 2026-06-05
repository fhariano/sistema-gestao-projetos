package repository;

import model.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {
    private static final String ARQUIVO = "data/usuarios.dat";
    private ArrayList<Usuario> usuarios;

    public UsuarioRepository() {
        this.usuarios = carregarDados();
    }

    public void salvar(Usuario usuario) {
        usuarios.add(usuario);
        persistir();
    }

    public void atualizar(Usuario usuario) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(usuario.getId())) {
                usuarios.set(i, usuario);
                persistir();
                return;
            }
        }
    }

    public void deletar(String id) {
        usuarios.removeIf(u -> u.getId().equals(id));
        persistir();
    }

    public Optional<Usuario> buscarPorId(String id) {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarios.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(login))
                .findFirst();
    }

    public Optional<Usuario> buscarPorCpf(String cpf) {
        return usuarios.stream()
                .filter(u -> u.getCpf().equals(cpf))
                .findFirst();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }

    public List<Usuario> listarAtivos() {
        return usuarios.stream()
                .filter(Usuario::isAtivo)
                .collect(java.util.stream.Collectors.toList());
    }

    private void persistir() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            System.err.println("Erro ao salvar usuários: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Usuario> carregarDados() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (ArrayList<Usuario>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public boolean loginExiste(String login) {
        return usuarios.stream().anyMatch(u -> u.getLogin().equalsIgnoreCase(login));
    }

    public boolean cpfExiste(String cpf) {
        return usuarios.stream().anyMatch(u -> u.getCpf().equals(cpf));
    }

    public boolean emailExiste(String email) {
        return usuarios.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }
}
