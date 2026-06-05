package model;

import java.io.Serializable;
import java.util.UUID;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String cargo;
    private String login;
    private String senha;
    private Perfil perfil;
    private boolean ativo;

    public Usuario() {
        this.id = UUID.randomUUID().toString().substring(0, 5);
        this.ativo = true;
    }

    public Usuario(String nomeCompleto, String cpf, String email, String cargo,
                   String login, String senha, Perfil perfil) {
        this();
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.email = email;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return String.format("[%s] %s | CPF: %s | Login: %s | Perfil: %s | %s",
                id.substring(0, 5), nomeCompleto, cpf, login, perfil.getDescricao(),
                ativo ? "Ativo" : "Inativo");
    }
}
