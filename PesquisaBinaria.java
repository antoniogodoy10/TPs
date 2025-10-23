import java.io.*;
import java.util.*;

public class PesquisaBinaria {
    private static List<Game> games = new ArrayList<>();
    private static List<Game> selecionados = new ArrayList<>();
    private static int comparacoes = 0;

    public static void main(String[] args) throws Exception {
        long inicio = System.currentTimeMillis();

        // Caminho do CSV (ajuste se estiver em outro local)
        lerCSV("games.csv");

        Scanner sc = new Scanner(System.in);
        String entrada;

        // --- 1ª parte: ler IDs até "FIM"
        while (!(entrada = sc.nextLine()).equals("FIM")) {
            int id = Integer.parseInt(entrada);
            Game g = buscarPorId(id);
            if (g != null) {
                selecionados.add(g);
            }
        }

        // --- Ordenar jogos por nome
        Collections.sort(selecionados, Comparator.comparing(Game::getName));

        // --- 2ª parte: ler nomes até "FIM" e fazer pesquisa binária
        while (!(entrada = sc.nextLine()).equals("FIM")) {
            boolean achou = pesquisaBinaria(entrada);
            System.out.println(achou ? "SIM" : "NAO");
        }

        sc.close();

        // --- Tempo e log
        long fim = System.currentTimeMillis();
        double tempo = (fim - inicio) / 1000.0;

        try (PrintWriter log = new PrintWriter("123456_binaria.txt")) { // troque pela sua matrícula
            log.printf("123456\t%.4f\t%d", tempo, comparacoes);
        }
    }

    // --- Lê o CSV e carrega os jogos
    private static void lerCSV(String caminho) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(caminho));
        String linha;
        while ((linha = br.readLine()) != null) {
            String[] campos = dividirLinhaCSV(linha);
            try {
                int id = Integer.parseInt(campos[0]);
                String nome = campos[1];
                games.add(new Game(id, nome));
            } catch (Exception e) {
                // ignora linhas inválidas
            }
        }
        br.close();
    }

    // --- Divide linha CSV, respeitando aspas e vírgulas internas
    private static String[] dividirLinhaCSV(String linha) {
        List<String> campos = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean dentroAspas = false;
        for (char c : linha.toCharArray()) {
            if (c == '"') dentroAspas = !dentroAspas;
            else if (c == ',' && !dentroAspas) {
                campos.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        campos.add(sb.toString());
        return campos.toArray(new String[0]);
    }

    // --- Busca linear pelo ID
    private static Game buscarPorId(int id) {
        for (Game g : games) {
            if (g.getAppId() == id)
                return g;
        }
        return null;
    }

    // --- Pesquisa binária pelo nome
    private static boolean pesquisaBinaria(String nome) {
        int esq = 0, dir = selecionados.size() - 1;
        while (esq <= dir) {
            int meio = (esq + dir) / 2;
            comparacoes++;
            int cmp = nome.compareTo(selecionados.get(meio).getName());
            if (cmp == 0) return true;
            if (cmp > 0) esq = meio + 1;
            else dir = meio - 1;
        }
        return false;
    }
}
