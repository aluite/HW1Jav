package graphs;

import java.util.ArrayList;
import java.util.List;

/**
 * Результат алгоритма Флойда - Уоршелла. Только данные, без печати.
 * Матрицы индексируются порядком ids (алфавитным).
 */
public final class FloydWarshallResult {
    /** Значение "пути нет". Достаточно мало, чтобы сумма двух конечных значений не переполняла long. */
    public static final long INF = Long.MAX_VALUE / 4;

    private final List<String> ids;
    private final long[][] initial;
    private final long[][] dist;
    private final int[][] next;
    private final boolean[][] affected;
    private final boolean negativeCycle;

    FloydWarshallResult(List<String> ids, long[][] initial, long[][] dist, int[][] next) {
        this.ids = List.copyOf(ids);
        this.initial = initial;
        this.dist = dist;
        this.next = next;
        int n = ids.size();
        boolean neg = false;
        this.affected = new boolean[n][n];
        for (int k = 0; k < n; k++) {
            if (dist[k][k] < 0) {
                neg = true;
                // Пара (i, j) затронута, если i достигает вершины k цикла, а k достигает j.
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (dist[i][k] != INF && dist[k][j] != INF) {
                            affected[i][j] = true;
                        }
                    }
                }
            }
        }
        this.negativeCycle = neg;
    }

    public List<String> ids() { return ids; }
    public int indexOf(String id) { return ids.indexOf(id); }
    public long[][] initialMatrix() { return copy(initial); }
    public long[][] distanceMatrix() { return copy(dist); }
    public boolean hasNegativeCycle() { return negativeCycle; }

    /** Есть ли отрицательная сумма на диагонали (вершины, лежащие на отрицательном цикле). */
    public List<String> negativeCycleVertices() {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            if (dist[i][i] < 0) res.add(ids.get(i));
        }
        return res;
    }

    /** Кратчайшее расстояние или INF; для затронутых отрицательным циклом пар значение недостоверно. */
    public long distance(String from, String to) {
        return dist[index(from)][index(to)];
    }

    /** Восстанавливает путь from -> to по матрице next. */
    public PathResult path(String from, String to) {
        int i = index(from);
        int j = index(to);
        if (affected[i][j]) {
            return new PathResult(PathResult.Status.NEGATIVE_CYCLE, List.of(), 0);
        }
        if (dist[i][j] == INF) {
            return new PathResult(PathResult.Status.NO_PATH, List.of(), 0);
        }
        List<String> path = new ArrayList<>();
        path.add(ids.get(i));
        int cur = i;
        // Не более n-1 шагов: простой путь без повторов вершин.
        for (int steps = 0; cur != j && steps < ids.size(); steps++) {
            cur = next[cur][j];
            path.add(ids.get(cur));
        }
        return new PathResult(PathResult.Status.OK, path, dist[i][j]);
    }

    private int index(String id) {
        int idx = ids.indexOf(id);
        if (idx < 0) throw new IllegalArgumentException("Вершина не найдена: " + id);
        return idx;
    }

    private static long[][] copy(long[][] m) {
        long[][] c = new long[m.length][];
        for (int i = 0; i < m.length; i++) c[i] = m[i].clone();
        return c;
    }
}
