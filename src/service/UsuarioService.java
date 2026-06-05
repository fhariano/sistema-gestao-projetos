package service;

import model.Perfil;
import model.Usuario;
import repository.UsuarioRepository;
import util.Validador;

import java.util.List;
import java.util.Optional;

public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public String cadastrar(String nomeCompleto, String cpf, String email, String cargo,
                             String login, String senha, Perfil perfil) {
        // Validações
        if (!Validador.naoVazio(nomeCompleto)) return "Nome completo é obrigatório.";
        if (!Validador.cpfValido(cpf)) return "CPF inválido.";
        if (!Validador.emailValido(email)) return "E-mail inválido.";
        if (!Validador.naoVazio(cargo)) return "Cargo é obrigatório.";
        if (!Validador.loginValido(login)) return "Login inválido (4-20 caracteres, letras/números/._).";
        if (!Validador.senhaValida(senha)) return "Senha deve ter pelo menos 6 caracteres.";
        if (perfil == null) return "Perfil é obrigatório.";

        String cpfFormatado = Validador.formatarCpf(cpf);
        if (repository.cpfExiste(cpfFormatado)) return "CPF já cadastrado.";
        if (repository.loginExiste(login)) return "Login já em uso.";
        if (repository.emailExiste(email)) return "E-mail já cadastrado.";

        Usuario usuario = new Usuario(nomeCompleto, cpfFormatado, email, cargo, login, senha, perfil);
        repository.salvar(usuario);
        return null; // sem erro
    }

    public String atualizar(String id, String nomeCompleto, String email, String cargo,
                             String senha, Perfil perfil) {
        Optional<Usuario> opt = repository.buscarPorId(id);
        if (opt.isEmpty()) return "Usuário não encontrado.";

        Usuario usuario = opt.get();
        if (!Validador.naoVazio(nomeCompleto)) return "Nome completo é obrigatório.";
        if (!Validador.emailValido(email)) return "E-mail inválido.";
        if (!Validador.naoVazio(cargo)) return "Cargo é obrigatório.";
        if (senha != null && !senha.isBlank() && !Validador.senhaValida(senha))
            return "Senha deve ter pelo menos 6 caracteres.";

        // Verificar e-mail duplicado em outro usuário
        Optional<Usuario> comMesmoEmail = repository.buscarPorEmail(email);
        if (comMesmoEmail.isPresent() && !comMesmoEmail.get().getId().equals(id))
            return "E-mail já cadastrado para outro usuário.";

        usuario.setNomeCompleto(nomeCompleto);
        usuario.setEmail(email);
        usuario.setCargo(cargo);
        if (perfil != null) usuario.setPerfil(perfil);
        if (senha != null && !senha.isBlank()) usuario.setSenha(senha);

        repository.atualizar(usuario);
        return null;
    }

    public String desativar(String id) {
        Optional<Usuario> opt = repository.buscarPorId(id);
        if (opt.isEmpty()) return "Usuário não encontrado.";
        Usuario usuario = opt.get();
        if (!usuario.isAtivo()) return "Usuário já está inativo.";
        usuario.setAtivo(false);
        repository.atualizar(usuario);
        return null;
    }

    public String reativar(String id) {
        Optional<Usuario> opt = repository.buscarPorId(id);
        if (opt.isEmpty()) return "Usuário não encontrado.";
        Usuario usuario = opt.get();
        if (usuario.isAtivo()) return "Usuário já está ativo.";
        usuario.setAtivo(true);
        repository.atualizar(usuario);
        return null;
    }

    public Optional<Usuario> autenticar(String login, String senha) {
        Optional<Usuario> opt = repository.buscarPorLogin(login);
        if (opt.isEmpty()) return Optional.empty();
        Usuario usuario = opt.get();
        if (!usuario.isAtivo()) return Optional.empty();
        if (!usuario.getSenha().equals(senha)) return Optional.empty();
        return Optional.of(usuario);
    }

    public List<Usuario> listarTodos() {
        return repository.listarTodos();
    }

    public List<Usuario> listarAtivos() {
        return repository.listarAtivos();
    }

    public Optional<Usuario> buscarPorId(String id) {
        return repository.buscarPorId(id);
    }

    public List<Usuario> listarGerentes() {
        return repository.listarAtivos().stream()
                .filter(u -> u.getPerfil() == Perfil.GERENTE || u.getPerfil() == Perfil.ADMINISTRADOR)
                .collect(java.util.stream.Collectors.toList());
    }
}
