import java.io.File;
import model.Perfil;
import repository.*;
import service.*;
import view.MenuPrincipal;

public class Main {

    public static void main(String[] args) {
        // Garantir que o diretório de dados exista
        new File("data").mkdirs();

        // Instanciar Repositories
        UsuarioRepository usuarioRepo   = new UsuarioRepository();
        ProjetoRepository projetoRepo   = new ProjetoRepository();
        TarefaRepository  tarefaRepo    = new TarefaRepository();
        EquipeRepository  equipeRepo    = new EquipeRepository();
        AlocacaoRepository alocacaoRepo = new AlocacaoRepository();

        // Instanciar Services
        UsuarioService  usuarioService  = new UsuarioService(usuarioRepo);
        ProjetoService  projetoService  = new ProjetoService(projetoRepo, usuarioService);
        TarefaService   tarefaService   = new TarefaService(tarefaRepo, projetoService, usuarioService);
        EquipeService   equipeService   = new EquipeService(equipeRepo, usuarioService);
        AlocacaoService alocacaoService = new AlocacaoService(alocacaoRepo, equipeService, projetoService);

        // Seed inicial: criar admin padrão se não houver usuários
        if (usuarioService.listarTodos().isEmpty()) {
            System.out.println("\n  -> Primeira execução detectada. Criando usuário administrador padrão...");
            String erro = usuarioService.cadastrar(
                "Administrador do Sistema",
                "000.000.001-91",  // CPF válido para teste
                "admin@sistema.com",
                "Administrador",
                "admin",
                "admin123",
                Perfil.ADMINISTRADOR
            );
            if (erro == null) {
                System.out.println("  Ok -  Admin criado! Login: admin | Senha: admin123");
            } else {
                // Tenta com CPF diferente se já existir
                usuarioService.cadastrar(
                    "Administrador do Sistema",
                    "529.982.247-25",
                    "admin@sistema.com",
                    "Administrador",
                    "admin",
                    "admin123",
                    Perfil.ADMINISTRADOR
                );
                System.out.println("  Ok -  Admin criado! Login: admin | Senha: admin123");
            }

            try { Thread.sleep(2500); } catch (InterruptedException e) { /* ignora */ }
        }

        // Iniciar menu principal
        MenuPrincipal menu = new MenuPrincipal(
                usuarioService, projetoService, tarefaService, equipeService, alocacaoService);
        menu.iniciar();
    }
}
