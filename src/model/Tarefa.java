package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class Tarefa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String titulo;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataPrevistaConclusao;
    private StatusTarefa status;
    private String projetoId;
    private String responsavelId; // ID do usuário responsável
    private int prioridade; // 1-Baixa, 2-Média, 3-Alta
    private boolean ativa;

    public Tarefa() {
        this.id = UUID.randomUUID().toString().substring(0, 5);
        this.status = StatusTarefa.PENDENTE;
        this.ativa = true;
    }

    public Tarefa(String titulo, String descricao, LocalDate dataInicio,
                  LocalDate dataPrevistaConclusao, String projetoId,
                  String responsavelId, int prioridade) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataPrevistaConclusao = dataPrevistaConclusao;
        this.projetoId = projetoId;
        this.responsavelId = responsavelId;
        this.prioridade = prioridade;
    }

    public String getPrioridadeDescricao() {
        return switch (prioridade) {
            case 1 -> "Baixa";
            case 2 -> "Média";
            case 3 -> "Alta";
            default -> "Indefinida";
        };
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataPrevistaConclusao() { return dataPrevistaConclusao; }
    public void setDataPrevistaConclusao(LocalDate dataPrevistaConclusao) { this.dataPrevistaConclusao = dataPrevistaConclusao; }

    public StatusTarefa getStatus() { return status; }
    public void setStatus(StatusTarefa status) { this.status = status; }

    public String getProjetoId() { return projetoId; }
    public void setProjetoId(String projetoId) { this.projetoId = projetoId; }

    public String getResponsavelId() { return responsavelId; }
    public void setResponsavelId(String responsavelId) { this.responsavelId = responsavelId; }

    public int getPrioridade() { return prioridade; }
    public void setPrioridade(int prioridade) { this.prioridade = prioridade; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Status: %s | Prioridade: %s | Prazo: %s",
                id.substring(0, 5), titulo, status.getDescricao(),
                getPrioridadeDescricao(), dataPrevistaConclusao);
    }
}
