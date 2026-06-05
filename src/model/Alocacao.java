package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class Alocacao implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String equipeId;
    private String projetoId;
    private LocalDate dataAlocacao;
    private LocalDate dataDesalocacao;
    private boolean ativa;
    private String observacao;

    public Alocacao() {
        this.id = UUID.randomUUID().toString().substring(0, 5);
        this.dataAlocacao = LocalDate.now();
        this.ativa = true;
    }

    public Alocacao(String equipeId, String projetoId, String observacao) {
        this();
        this.equipeId = equipeId;
        this.projetoId = projetoId;
        this.observacao = observacao;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEquipeId() { return equipeId; }
    public void setEquipeId(String equipeId) { this.equipeId = equipeId; }

    public String getProjetoId() { return projetoId; }
    public void setProjetoId(String projetoId) { this.projetoId = projetoId; }

    public LocalDate getDataAlocacao() { return dataAlocacao; }
    public void setDataAlocacao(LocalDate dataAlocacao) { this.dataAlocacao = dataAlocacao; }

    public LocalDate getDataDesalocacao() { return dataDesalocacao; }
    public void setDataDesalocacao(LocalDate dataDesalocacao) { this.dataDesalocacao = dataDesalocacao; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    @Override
    public String toString() {
        return String.format("[%s] Equipe: %s | Projeto: %s | Alocado em: %s | %s",
                id.substring(0, 5), equipeId.substring(0, 5),
                projetoId.substring(0, 5), dataAlocacao,
                ativa ? "Ativa" : "Encerrada");
    }
}
