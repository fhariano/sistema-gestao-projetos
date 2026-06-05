package util;

import java.util.Scanner;

public class Console {
    private static final Scanner scanner = new Scanner(System.in);

    public static final String RESET  = "\u001B[0m";
    public static final String BOLD   = "\u001B[1m";
    public static final String RED    = "\u001B[31m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE   = "\u001B[34m";
    public static final String CYAN   = "\u001B[36m";
    public static final String WHITE  = "\u001B[37m";

    public static String lerString(String label) {
        System.out.print(CYAN + label + RESET);
        return scanner.nextLine().trim();
    }

    public static String lerStringObrigatorio(String label) {
        String valor;
        do {
            System.out.print(CYAN + label + RESET);
            valor = scanner.nextLine().trim();
            if (valor.isBlank()) {
                System.out.println(RED + "  Campo obrigatório! Por favor, informe um valor." + RESET);
            }
        } while (valor.isBlank());
        return valor;
    }

    public static int lerInteiro(String label) {
        while (true) {
            System.out.print(CYAN + label + RESET);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println(RED + "  Valor inválido! Informe um número inteiro." + RESET);
            }
        }
    }

    public static int lerInteiro(String label, int min, int max) {
        while (true) {
            int valor = lerInteiro(label);
            if (valor >= min && valor <= max) return valor;
            System.out.println(RED + "  Valor deve estar entre " + min + " e " + max + "." + RESET);
        }
    }

    public static void pausar() {
        System.out.print(YELLOW + "\n  Pressione ENTER para continuar..." + RESET);
        scanner.nextLine();
    }

    public static void limpar() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void titulo(String texto) {
        System.out.println();
        System.out.println(BOLD + BLUE + "  ╔══════════════════════════════════════════════════╗" + RESET);
        String linha = "  ║  " + texto;
        int padding = 54 - linha.length() - 1;
        if (padding < 0) padding = 0;
        System.out.println(BOLD + BLUE + linha + " ".repeat(padding) + "║" + RESET);
        System.out.println(BOLD + BLUE + "  ╚══════════════════════════════════════════════════╝" + RESET);
    }

    public static void subtitulo(String texto) {
        System.out.println(BOLD + CYAN + "\n  ─── " + texto + " ───" + RESET);
    }

    public static void sucesso(String msg) {
        System.out.println(GREEN + "\n  Ok -  " + msg + RESET);
    }

    public static void erro(String msg) {
        System.out.println(RED + "\n  ✘ " + msg + RESET);
    }

    public static void aviso(String msg) {
        System.out.println(YELLOW + "\n  ⚠ " + msg + RESET);
    }

    public static void info(String msg) {
        System.out.println(WHITE + "  " + msg + RESET);
    }

    public static void separador() {
        System.out.println(BLUE + "  ──────────────────────────────────────────────────" + RESET);
    }

    public static void menu(String... opcoes) {
        separador();
        for (int i = 0; i < opcoes.length; i++) {
            System.out.printf(CYAN + "  [%d] " + WHITE + "%s" + RESET + "%n", i + 1, opcoes[i]);
        }
        System.out.printf(CYAN + "  [0] " + RED + "Voltar / Sair" + RESET + "%n");
        separador();
    }

    public static boolean confirmar(String mensagem) {
        System.out.print(YELLOW + "  " + mensagem + " (s/n): " + RESET);
        String resp = scanner.nextLine().trim().toLowerCase();
        return resp.equals("s") || resp.equals("sim");
    }

    public static Scanner getScanner() {
        return scanner;
    }
}
