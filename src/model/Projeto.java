package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class Projeto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataTerminoPrevista;
    private StatusProjeto status;
    private String gerenteId; // ID do usuário gerente
    private boolean ativo;

    public Projeto() {
        this.id = UUID.randomUUID().toString().substring(0, 5);
        this.status = StatusProjeto.PLANEJADO;
        this.ativo = true;
    }

    public Projeto(String nome, String descricao, LocalDate dataInicio,
                   LocalDate dataTerminoPrevista, String gerenteId) {
        this();
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataTerminoPrevista = dataTerminoPrevista;
        this.gerenteId = gerenteId;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataTerminoPrevista() { return dataTerminoPrevista; }
    public void setDataTerminoPrevista(LocalDate dataTerminoPrevista) { this.dataTerminoPrevista = dataTerminoPrevista; }

    public StatusProjeto getStatus() { return status; }
    public void setStatus(StatusProjeto status) { this.status = status; }

    public String getGerenteId() { return gerenteId; }
    public void setGerenteId(String gerenteId) { this.gerenteId = gerenteId; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Status: %s | Início: %s | Término: %s",
                id.substring(0, 5), nome, status.getDescricao(),
                dataInicio, dataTerminoPrevista);
    }
}
