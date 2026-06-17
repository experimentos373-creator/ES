package util;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Utilitario generico para gravacao e leitura persistente de colecoes de entidades (.ser).
 * Evita a serializacao dos Managers (Singletons), mantendo a integridade do padrao.
 */
public class PersistenceUtil {

    /**
     * Guarda uma lista de objetos serializaveis num ficheiro.
     */
    public static <T extends Serializable> boolean guardar(String nomeFicheiro, List<T> dados) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeFicheiro))) {
            oos.writeObject(dados);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao gravar no ficheiro " + nomeFicheiro + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Carrega uma lista de objetos serializaveis de um ficheiro.
     * Retorna uma nova lista se o ficheiro nao existir ou ocorrer um erro.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> carregar(String nomeFicheiro) {
        File ficheiro = new File(nomeFicheiro);
        if (!ficheiro.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomeFicheiro))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao ler do ficheiro " + nomeFicheiro + ". A inicializar lista vazia. Detalhes: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
