package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Equipe implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private String descricao;
    private List<String> membroIds; // IDs dos usuários membros
    private boolean ativa;

    public Equipe() {
        this.id = UUID.randomUUID().toString().substring(0, 5);
        this.membroIds = new ArrayList<>();
        this.ativa = true;
    }

    public Equipe(String nome, String descricao) {
        this();
        this.nome = nome;
        this.descricao = descricao;
    }

    public void adicionarMembro(String usuarioId) {
        if (!membroIds.contains(usuarioId)) {
            membroIds.add(usuarioId);
        }
    }

    public void removerMembro(String usuarioId) {
        membroIds.remove(usuarioId);
    }

    public boolean contemMembro(String usuarioId) {
        return membroIds.contains(usuarioId);
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<String> getMembroIds() { return membroIds; }
    public void setMembroIds(List<String> membroIds) { this.membroIds = membroIds; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Membros: %d | %s",
                id.substring(0, 5), nome, membroIds.size(),
                ativa ? "Ativa" : "Inativa");
    }
}
