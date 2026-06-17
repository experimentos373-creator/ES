package util;

import java.util.Scanner;

/**
 * Utilitario para leitura segura de dados a partir da consola.
 */
public class LeitorInput {
    private static final Scanner scanner = new Scanner(System.in);

    public static String lerLinha(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    public static int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Por favor, introduza um numero inteiro valido.");
            }
        }
    }

    public static double lerDecimal(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input.replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Por favor, introduza um numero decimal valido.");
            }
        }
    }
}
