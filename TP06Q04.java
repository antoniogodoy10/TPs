import java.io.*;
import java.util.*;

public class TP06Q04 {

    // [0] Configurações gerais
    // - Fila de tamanho fixo (5)
    // - Caminho do CSV: /tmp/games.csv (como no enunciado)
    private static final int TAM_FILA = 5;

    // [1] Classe Game: representa um jogo do CSV
    static class Game {
        public int id;
        public String name;
        public String releaseDate;
        public int estimatedOwners;
        public float price;
        public String[] languages;
        public int metascore;
        public float userScore;
        public int achievements;
        public String[] publishers;
        public String[] developers;
        public String[] categories;
        public String[] genres;
        public String[] tags;

        public Game() {}

        // Lê uma linha do CSV manualmente
        public void ler(String linha) {
            String[] campos = new String[14];
            int i = 0;
            StringBuilder temp = new StringBuilder();
            boolean aspas = false, colchetes = false;

            for (int k = 0; k < linha.length(); k++) {
                char c = linha.charAt(k);
                if (c == '"') aspas = !aspas;
                else if (c == '[') { colchetes = true; temp.append(c); }
                else if (c == ']') { colchetes = false; temp.append(c); }
                else if (c == ',' && !aspas && !colchetes) {
                    campos[i++] = temp.toString();
                    temp.setLength(0);
                } else temp.append(c);
            }
            campos[i] = temp.toString();

            id = parseInt(campos[0]);
            name = campos[1];
            releaseDate = campos[2];
            estimatedOwners = parseInt(campos[3]);
            price = parseFloat(campos[4]);
            languages = parseLista(campos[5]);
            metascore = parseInt(campos[6]);
            userScore = parseFloat(campos[7]);
            achievements = parseInt(campos[8]);
            publishers = parseLista(campos[9]);
            developers = parseLista(campos[10]);
            categories = parseLista(campos[11]);
            genres = parseLista(campos[12]);
            tags = parseLista(campos[13]);
        }

        // Converte lista textual “[a, b, c]” em array de strings
        private String[] parseLista(String campo) {
            if (campo == null || campo.length() < 2) return new String[0];
            String limpa = campo.replaceAll("[\\[\\]']", "");
            return limpa.split(", ");
        }

        // Conversores manuais
        private int parseInt(String s) {
            if (s == null || s.isEmpty()) return 0;
            int n = 0;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c >= '0' && c <= '9') n = n * 10 + (c - '0');
            }
            return n;
        }

        private float parseFloat(String s) {
            if (s == null || s.isEmpty()) return 0;
            float num = 0, div = 1;
            boolean dec = false;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '.') dec = true;
                else if (c >= '0' && c <= '9') {
                    num = num * 10 + (c - '0');
                    if (dec) div *= 10;
                }
            }
            return num / div;
        }

        // Saída formatada
        public String toStringFormatado(int i) {
            return "[" + i + "] => " + id + " ## " + name + " ## " + releaseDate + " ## " +
                    estimatedOwners + " ## " + price + " ## " + Arrays.toString(languages) + " ## " +
                    metascore + " ## " + userScore + " ## " + achievements + " ## " +
                    Arrays.toString(publishers) + " ## " + Arrays.toString(developers) + " ## " +
                    Arrays.toString(categories) + " ## " + Arrays.toString(genres) + " ## " +
                    Arrays.toString(tags) + " ##";
        }
    }

    // [2] Fila circular de tamanho fixo
    static class Fila {
        private Game[] array;
        private int primeiro, ultimo;

        public Fila(int tamanho) {
            array = new Game[tamanho + 1];
            primeiro = ultimo = 0;
        }

        public boolean isVazia() {
            return primeiro == ultimo;
        }

        public boolean isCheia() {
            return ((ultimo + 1) % array.length) == primeiro;
        }

        public void inserir(Game g) throws Exception {
            if (isCheia()) remover(); // fila circular remove o mais antigo
            array[ultimo] = g;
            ultimo = (ultimo + 1) % array.length;
        }

        public Game remover() throws Exception {
            if (isVazia()) throw new Exception("Erro: fila vazia!");
            Game resp = array[primeiro];
            primeiro = (primeiro + 1) % array.length;
            return resp;
        }

        public void mostrar() {
            int i = primeiro, pos = 0;
            while (i != ultimo) {
                System.out.println(array[i].toStringFormatado(pos));
                pos++;
                i = (i + 1) % array.length;
            }
        }
    }

    // [3] Busca manual por ID no vetor de jogos
    public static Game buscarPorId(Game[] jogos, int total, int id) {
        for (int i = 0; i < total; i++) {
            if (jogos[i].id == id) return jogos[i];
        }
        return null;
    }

    // [4] Programa principal
    public static void main(String[] args) throws Exception {
        // 4.1 Ler o CSV
        BufferedReader br = new BufferedReader(new FileReader("/tmp/games.csv"));
        String linha;
        Game[] jogos = new Game[6000];
        int total = 0;
        br.readLine(); // ignora cabeçalho
        while ((linha = br.readLine()) != null) {
            Game g = new Game();
            g.ler(linha);
            jogos[total++] = g;
        }
        br.close();

        // 4.2 Ler IDs iniciais (até FIM)
        Scanner sc = new Scanner(System.in);
        Fila fila = new Fila(TAM_FILA);
        String entrada;
        while (!(entrada = sc.nextLine()).equals("FIM")) {
            int id = Integer.parseInt(entrada);
            Game g = buscarPorId(jogos, total, id);
            if (g != null) {
                System.out.println("(R) " + g.name);
                fila.inserir(g);
            }
        }

        // 4.3 Ler número de comandos e executá-los
        int n = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < n; i++) {
            String comando = sc.next();
            if (comando.equals("I")) {
                int id = sc.nextInt();
                Game g = buscarPorId(jogos, total, id);
                if (g != null) {
                    System.out.println("(R) " + g.name);
                    fila.inserir(g);
                }
            } else if (comando.equals("R")) {
                if (!fila.isVazia()) {
                    Game removido = fila.remover();
                    System.out.println("(R) " + removido.name);
                }
            }
        }

        // 4.4 Mostrar fila final formatada
        fila.mostrar();
        sc.close();
    }
}
