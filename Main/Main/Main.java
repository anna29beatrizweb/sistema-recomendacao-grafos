import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Scanner;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static class Livro {
        String nome;

        public Livro(String nome) {
            this.nome = nome;
        }

        @Override
        public String toString() {
            return nome;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Livro outro = (Livro) obj;
            return nome.equals(outro.nome);
        }

        @Override
        public int hashCode() {
            return nome.hashCode();
        }
    }

    public static void main(String[] args) {

        HashMap<Livro, Set<Livro>> grafo = new HashMap<>();

        // Criando livros
        Livro l1  = new Livro("Além do Bem e do Mal");
        Livro l2  = new Livro("Jane de Lantern Hill");
        Livro l3  = new Livro("Alice no País da Mentira");
        Livro l4  = new Livro("A Parte que Falta");
        Livro l5  = new Livro("Verity");
        Livro l6  = new Livro("A Revolução dos Bichos");
        Livro l7  = new Livro("Heartstopper");
        Livro l8  = new Livro("1984");
        Livro l9  = new Livro("A Biblioteca da Meia-Noite");
        Livro l10 = new Livro("A Metamorfose");

        // Inicializando o grafo
        grafo.put(l1,  new HashSet<>());
        grafo.put(l2,  new HashSet<>());
        grafo.put(l3,  new HashSet<>());
        grafo.put(l4,  new HashSet<>());
        grafo.put(l5,  new HashSet<>());
        grafo.put(l6,  new HashSet<>());
        grafo.put(l7,  new HashSet<>());
        grafo.put(l8,  new HashSet<>());
        grafo.put(l9,  new HashSet<>());
        grafo.put(l10, new HashSet<>());

        // Recomendações (arestas do grafo)
        grafo.get(l1).add(l2);
        grafo.get(l1).add(l3);

        grafo.get(l2).add(l1);
        grafo.get(l2).add(l10);

        grafo.get(l3).add(l1);
        grafo.get(l3).add(l4);

        grafo.get(l4).add(l5);
        grafo.get(l4).add(l7);

        grafo.get(l5).add(l4);
        grafo.get(l5).add(l7);

        grafo.get(l6).add(l1);
        grafo.get(l6).add(l5);

        grafo.get(l7).add(l4);
        grafo.get(l7).add(l5);

        grafo.get(l8).add(l9);
        grafo.get(l8).add(l10);

        grafo.get(l9).add(l8);
        grafo.get(l9).add(l10);

        grafo.get(l10).add(l2);
        grafo.get(l10).add(l8);

        // Entrada do usuário
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Sistema de Recomendação de Livros ===");
        System.out.println("Livros disponíveis:");
        for (Livro l : grafo.keySet()) {
            System.out.println("  - " + l.nome);
        }
        System.out.println();
        System.out.println("Digite o nome do livro:");
        String nomeDigitado = sc.nextLine();

        Livro livroEscolhido = null;
        for (Livro l : grafo.keySet()) {
            if (l.nome.equalsIgnoreCase(nomeDigitado)) {
                livroEscolhido = l;
                break;
            }
        }

        if (livroEscolhido == null) {
            System.out.println("Livro não encontrado.");
            return;
        }

        // Recomendações diretas (vizinhos imediatos)
        recomendar(grafo, livroEscolhido);

        System.out.println();

        // Dijkstra: recomendações por distância
        recomendarPorDistancia(grafo, livroEscolhido);
    }

    // Recomendações diretas (semana 4)
    public static void recomendar(HashMap<Livro, Set<Livro>> grafo, Livro livro) {
        if (livro == null || !grafo.containsKey(livro)) {
            System.out.println("Livro não encontrado.");
            return;
        }

        System.out.println("Se você gostou de \"" + livro + "\", leia:");
        for (Livro l : grafo.get(livro)) {
            System.out.println("  - " + l);
        }
    }

    // Recomendações por distância usando Dijkstra (semana 5)
    public static void recomendarPorDistancia(HashMap<Livro, Set<Livro>> grafo, Livro origem) {
        Map<Livro, Integer> distancias = djikstraSimples(grafo, origem);

        System.out.println("Recomendações por distância a partir de \"" + origem + "\":");
        System.out.println("(Quanto menor a distância, maior a chance de ser uma boa recomendação)");
        System.out.println();

        // Agrupa livros por distância, excluindo a origem (distância 0)
        Map<Integer, List<Livro>> porDistancia = new HashMap<>();
        int maxDist = 0;

        for (Map.Entry<Livro, Integer> entry : distancias.entrySet()) {
            if (entry.getKey().equals(origem)) continue;
            int dist = entry.getValue();
            if (dist > maxDist) maxDist = dist;
            porDistancia.computeIfAbsent(dist, k -> new ArrayList<>()).add(entry.getKey());
        }

        for (int d = 1; d <= maxDist; d++) {
            List<Livro> livros = porDistancia.get(d);
            if (livros != null && !livros.isEmpty()) {
                System.out.println("Distância " + d + ":");
                for (Livro l : livros) {
                    System.out.println("  - " + l);
                }
            }
        }

        // Livros não alcançáveis
        List<Livro> inacessiveis = new ArrayList<>();
        for (Livro l : grafo.keySet()) {
            if (!l.equals(origem) && !distancias.containsKey(l)) {
                inacessiveis.add(l);
            }
        }
        if (!inacessiveis.isEmpty()) {
            System.out.println("\nLivros sem conexão (não alcançáveis):");
            for (Livro l : inacessiveis) {
                System.out.println("  - " + l);
            }
        }
    }

    // Algoritmo de Dijkstra (BFS sem pesos)
    public static Map<Livro, Integer> djikstraSimples(HashMap<Livro, Set<Livro>> grafo, Livro origem) {
        Map<Livro, Integer> distancias = new HashMap<>();
        Queue<Livro> fila = new LinkedList<>();

        distancias.put(origem, 0); // peso padrão 0 para a origem
        fila.add(origem);

        while (!fila.isEmpty()) {
            Livro atual = fila.poll();
            int distanciaAtual = distancias.get(atual);

            for (Livro vizinho : grafo.getOrDefault(atual, new HashSet<>())) {
                if (!distancias.containsKey(vizinho)) {
                    distancias.put(vizinho, distanciaAtual + 1);
                    fila.add(vizinho);
                }
            }
        }

        return distancias;
    }
}
