package graphs;

import java.util.List;
import java.util.OptionalLong;

import static graphs.FloydWarshallResult.INF;

/**
 * ДЗ1, вариант 9 (D11): алгоритм Флойда - Уоршелла, ориентированный граф рисунка 1
 * (исходные веса, включая отрицательные). Консольный вывод - только здесь.
 * Запуск: без аргументов (A -> F) или с двумя аргументами: <откуда> <куда>.
 */
public class Main {

    /** Граф рисунка 1 из задания. */
    public static Graph buildFigure1() {
        Graph g = new Graph(true);
        for (String id : new String[]{"A", "B", "C", "D", "E", "F"}) g.addVertex(id);
        g.addEdge("A", "B", 7);
        g.addEdge("A", "C", 9);
        g.addEdge("C", "B", -3);
        g.addEdge("B", "D", -1);
        g.addEdge("B", "E", -2);
        g.addEdge("C", "E", 2);
        g.addEdge("D", "E", 1);
        g.addEdge("D", "F", 3);
        g.addEdge("E", "F", 3);
        return g;
    }

    public static void main(String[] args) {
        String from = args.length >= 2 ? args[0] : "A";
        String to = args.length >= 2 ? args[1] : "F";

        Graph graph = buildFigure1();
        FloydWarshallResult result = FloydWarshall.run(graph);

        System.out.println("Вариант 9 (D11): Флойд - Уоршелл, ориентированный граф, рисунок 1");
        System.out.println();
        System.out.println("Дуги графа:");
        for (Edge e : graph.getEdges()) System.out.println("  " + e);

        System.out.println();
        System.out.println("Исходная матрица расстояний:");
        printMatrix(result.ids(), result.initialMatrix());

        System.out.println();
        System.out.println("Итоговая матрица кратчайших расстояний:");
        printMatrix(result.ids(), result.distanceMatrix());

        System.out.println();
        if (result.hasNegativeCycle()) {
            System.out.println("Обнаружен отрицательный цикл, вершины на цикле: " + result.negativeCycleVertices());
        } else {
            System.out.println("Отрицательных циклов нет (диагональ матрицы неотрицательна).");
        }

        PathResult pr = result.path(from, to);
        System.out.println();
        switch (pr.status()) {
            case OK -> {
                System.out.println("Путь " + from + " -> " + to + ": " + String.join(" -> ", pr.path()));
                System.out.println("Вес пути (из матрицы): " + pr.weight());
                System.out.println("Вес пути (пересчёт по дугам графа): " + recomputeWeight(graph, pr.path()));
            }
            case NO_PATH -> System.out.println("Пути из " + from + " в " + to + " нет.");
            case NEGATIVE_CYCLE -> System.out.println(
                    "Путь " + from + " -> " + to + " затронут отрицательным циклом: кратчайшего пути не существует.");
        }
    }

    /** Независимый пересчёт веса пути по дугам графа; Long.MIN_VALUE, если какой-то дуги нет. */
    public static long recomputeWeight(Graph graph, List<String> path) {
        long sum = 0;
        for (int i = 0; i + 1 < path.size(); i++) {
            OptionalLong w = graph.getEdgeWeight(path.get(i), path.get(i + 1));
            if (w.isEmpty()) return Long.MIN_VALUE;
            sum += w.getAsLong();
        }
        return sum;
    }

    private static void printMatrix(List<String> ids, long[][] m) {
        StringBuilder sb = new StringBuilder(String.format("%4s", ""));
        for (String id : ids) sb.append(String.format("%6s", id));
        System.out.println(sb);
        for (int i = 0; i < ids.size(); i++) {
            sb = new StringBuilder(String.format("%4s", ids.get(i)));
            for (int j = 0; j < ids.size(); j++) {
                sb.append(String.format("%6s", m[i][j] == INF ? "INF" : Long.toString(m[i][j])));
            }
            System.out.println(sb);
        }
    }
}
