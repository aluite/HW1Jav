package graphs;

import java.util.Arrays;
import java.util.List;

import static graphs.FloydWarshallResult.INF;

/**
 * Алгоритм Флойда - Уоршелла: кратчайшие пути между всеми парами вершин.
 * Время O(V^3), память O(V^2). Допускает отрицательные веса; отрицательные циклы диагностирует.
 * Алгоритм не печатает и не изменяет граф.
 */
public final class FloydWarshall {
    private FloydWarshall() { }

    public static FloydWarshallResult run(Graph graph) {
        List<String> ids = graph.vertexIds();
        int n = ids.size();

        long[][] dist = new long[n][n];
        int[][] next = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], INF);
            Arrays.fill(next[i], -1);
            dist[i][i] = 0;
            next[i][i] = i;
        }

        // Начальная матрица: веса существующих дуг. Параллельные дуги - берём меньшую.
        for (Edge e : graph.getEdges()) {
            int i = ids.indexOf(e.getFrom().getId());
            int j = ids.indexOf(e.getTo().getId());
            if (e.getWeight() < dist[i][j]) {
                dist[i][j] = e.getWeight();
                next[i][j] = j;
            }
        }
        long[][] initial = new long[n][];
        for (int i = 0; i < n; i++) initial[i] = dist[i].clone();

        // Основной цикл. Сложение - только для двух конечных значений.
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == INF) continue;
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == INF) continue;
                    long candidate = dist[i][k] + dist[k][j];
                    // Защита от лавинообразного роста по модулю при отрицательном цикле.
                    if (candidate < -INF) candidate = -INF;
                    // Строго меньше: next меняется только при улучшении.
                    if (candidate < dist[i][j]) {
                        dist[i][j] = candidate;
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
        return new FloydWarshallResult(ids, initial, dist, next);
    }
}
