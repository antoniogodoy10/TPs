import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class games {
    public int id;
    public String name;

    public games() {
    }

    public boolean manualEquals(String s1, String s2) {
        if (s1.length() != s2.length())
            return false;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i))
                return false;
        }
        return true;
    }

    public String manualSubstring(String s, int ini, int fim) {
        String resultado = "";
        for (int i = ini; i < fim; i++) {
            resultado += s.charAt(i);
        }
        return resultado;
    }

    public String manualTrim(String s) {
        int inicio = 0;
        int fim = s.length() - 1;

        while (inicio <= fim && s.charAt(inicio) == ' ')
            inicio++;
        while (fim >= inicio && s.charAt(fim) == ' ')
            fim--;

        if (inicio > fim)
            return "";
        return manualSubstring(s, inicio, fim + 1);
    }

    public int ParseInt(String s) {
        int resultado = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                resultado = resultado * 10 + (c - '0');
            }
        }
        return resultado;
    }

    public String[] splitManual(String linha) {
        int conta = 0;
        boolean dentroAspas = false, dentroColchete = false;

        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"')
                dentroAspas = !dentroAspas;
            else if (c == '[')
                dentroColchete = true;
            else if (c == ']')
                dentroColchete = false;
            else if (c == ',' && !dentroAspas && !dentroColchete)
                conta++;
        }

        String[] campos = new String[conta + 1];
        int j = 0;
        String temp = "";
        dentroAspas = false;
        dentroColchete = false;
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"')
                dentroAspas = !dentroAspas;
            else if (c == '[')
                dentroColchete = true;
            else if (c == ']')
                dentroColchete = false;

            if (c == ',' && !dentroAspas && !dentroColchete) {
                campos[j++] = temp;
                temp = "";
            } else
                temp += c;
        }
        campos[j] = temp;
        return campos;
    }

    public String[] ler(Scanner sc) throws IOException {
        String[] tmp = new String[1000];
        tmp[0] = sc.nextLine();
        String verifica = tmp[0];
        int i = 1;

        while (!verifica.equals("FIM")) {
            tmp[i] = sc.nextLine();
            verifica = tmp[i];
            i++;
        }

        String[] id = new String[i];
        for (int j = 0; j < i; j++) {
            id[j] = tmp[j];
        }

        return id;
    }

    public games[] preencheIdComNome(String[] ids) throws IOException {
        int n = ids.length;
        games[] vetor = new games[n];

        for (int i = 0; i < n; i++) {
            games jogo = new games();
            jogo.id = ParseInt(ids[i]);
            vetor[i] = jogo;
        }

        Scanner leitor = new Scanner(new File("/tmp/games.csv"), "UTF-8");
        leitor.nextLine();

        while (leitor.hasNextLine()) {
            String linha = leitor.nextLine();
            String[] campos = splitManual(linha);
            int idArquivo = ParseInt(campos[0]);
            for (int i = 0; i < n; i++) {
                if (vetor[i].id == idArquivo) {
                    vetor[i].name = campos[1];
                }
            }
        }
        leitor.close();

        ordenaPorNome(vetor);
        return vetor;
    }

    public void ordenaPorNome(games[] vetor) {
        for (int i = 0; i < vetor.length - 1; i++) {
            int menor = i;

            for (int j = i + 1; j < vetor.length; j++) {
                if (vetor[j].name != null && vetor[menor].name != null) {

                    String nameJ = vetor[j].name;
                    String nameMenor = vetor[menor].name;

                    nameJ = manualNormalize(nameJ);
                    nameMenor = manualNormalize(nameMenor);

                    nameJ = manualTrim(nameJ);
                    nameMenor = manualTrim(nameMenor);

                    nameJ = nameJ.toLowerCase();
                    nameMenor = nameMenor.toLowerCase();

                    int limite;
                    if (nameJ.length() < nameMenor.length()) {
                        limite = nameJ.length();
                    } else {
                        limite = nameMenor.length();
                    }

                    boolean trocou = false;

                    for (int k = 0; k < limite; k++) {
                        if (nameJ.charAt(k) < nameMenor.charAt(k)) {
                            menor = j;
                            trocou = true;
                            break;
                        } else if (nameJ.charAt(k) > nameMenor.charAt(k)) {
                            trocou = true;
                            break;
                        }
                    }

                    if (!trocou && nameJ.length() < nameMenor.length()) {
                        menor = j;
                    }

                }
            }

            swap(vetor, menor, i);
        }
    }

    public String manualNormalize(String s) {
        if (s == null)
            return null;
        String resultado = "";
        char h = '-';

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f' || c == '\u00A0' || c == '\u3000') {
                resultado += ' ';
            }

            else if (c == '\u2014' || c == '\u2013' || c == '\u2015') {
                resultado += h;
            }

            else if (c == '"' || c == '\'') {
            }

            else {
                resultado += c;
            }
        }
        return resultado;
    }

    public void swap(games[] vetor, int a, int b) {
        games tmp = vetor[a];
        vetor[a] = vetor[b];
        vetor[b] = tmp;
    }

    public boolean buscaBinariaNome(games[] vetor, String nomeProcurado) {
        nomeProcurado = manualNormalize(nomeProcurado);
        nomeProcurado = manualTrim(nomeProcurado).toLowerCase();

        int esq = 0;
        int dir = vetor.length - 1;

        while (esq <= dir) {
            int meio = (esq + dir) / 2;
            String nomeMeio = vetor[meio].name;

            if (nomeMeio != null) {
                nomeMeio = manualNormalize(nomeMeio);
                nomeMeio = manualTrim(nomeMeio).toLowerCase();
            } else {
                return false;
            }

            int limite;
            if (nomeProcurado.length() < nomeMeio.length())
                limite = nomeProcurado.length();
            else
                limite = nomeMeio.length();

            int comp = 0;
            for (int i = 0; i < limite; i++) {
                if (nomeMeio.charAt(i) < nomeProcurado.charAt(i)) {
                    comp = -1;
                    break;
                } else if (nomeMeio.charAt(i) > nomeProcurado.charAt(i)) {
                    comp = 1;
                    break;
                }
            }

            if (comp == 0) {
                if (nomeMeio.length() < nomeProcurado.length())
                    comp = -1;
                else if (nomeMeio.length() > nomeProcurado.length())
                    comp = 1;
            }

            if (comp == 0)
                return true;
            else if (comp < 0)
                esq = meio + 1;
            else
                dir = meio - 1;
        }

        return false;
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        games g = new games();

        String[] ids = g.ler(sc);
        games[] vetor = g.preencheIdComNome(ids);

        String procurarNome = sc.nextLine();
        while (!g.manualEquals(procurarNome, "FIM")) {
            if (g.buscaBinariaNome(vetor, procurarNome))
                System.out.println(" SIM");
            else
                System.out.println(" NAO");
            procurarNome = sc.nextLine();
        }
        sc.close();
    }
}