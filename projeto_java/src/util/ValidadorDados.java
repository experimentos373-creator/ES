package util;

/**
 * Utilitario para validacoes de formato de dados basicos.
 */
public class ValidadorDados {

    public static boolean validarEmail(String email) {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    public static boolean validarData(String data) {
        if (data == null) return false;
        return data.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    public static boolean validarHora(String hora) {
        if (hora == null) return false;
        return hora.matches("^\\d{2}:\\d{2}$");
    }
}
