package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validador {

    public static boolean cpfValido(String cpf) {
        if (cpf == null) return false;
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) return false;
        if (cpf.chars().distinct().count() == 1) return false;

        int soma = 0;
        for (int i = 0; i < 9; i++) soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        int primeiroDigito = (soma * 10) % 11;
        if (primeiroDigito == 10) primeiroDigito = 0;
        if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) return false;

        soma = 0;
        for (int i = 0; i < 10; i++) soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        int segundoDigito = (soma * 10) % 11;
        if (segundoDigito == 10) segundoDigito = 0;
        return segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }

    public static boolean emailValido(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean senhaValida(String senha) {
        if (senha == null) return false;
        return senha.length() >= 6;
    }

    public static boolean naoVazio(String valor) {
        return valor != null && !valor.isBlank();
    }

    public static LocalDate parseData(String dataStr) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dataStr.trim(), fmt);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String formatarCpf(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() == 11) {
            return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return cpf;
    }

    public static boolean loginValido(String login) {
        if (login == null || login.isBlank()) return false;
        return login.matches("^[a-zA-Z0-9._]{4,20}$");
    }
}
