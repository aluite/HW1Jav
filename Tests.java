package graphs;

import java.util.Arrays;
import java.util.List;

import static graphs.FloydWarshallResult.INF;

/**
 * Проверки ДЗ1 (вариант D11). Запуск: java -cp out graphs.Tests
 * Для каждой проверки печатаются вход, ожидаемый и фактический результат и вердикт.
 * Код возврата 1, если хотя бы одна проверка не пройдена.
 */
public class Tests {
    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, String input, String expected, String actual) {
        boolean ok = expected.equals(actual);
        if (ok) passed++; else failed++;
        System.out.println("[" + (ok ? "OK  " : "FAIL") + "] " + name);
        System.out.println("   Вход:      " + input);
        System.out.println("   Ожидалось: " + expected);
        System.out.println("   Получено:  " + actual);
    }

    private static String matrix(long[][] m) {
        StringBuilder sb = new StringBuilder();
        for (long[] row : m) {
            sb.append('[');
            for (int j = 0; j < row.length; j++) {
                if (j > 0) sb.append(' ');
                sb.append(row[j] == INF ? "INF" : Long.toString(row[j]));
            }
            sb.append(']');
        }
        return sb.toString();
    }

    private static String pathStr(PathResult r) {
        return r.status() == PathResult.Status.OK
                ? String.join("->", r.path()) + " (вес " + r.weight() + ")"
                : r.status().name();
    }

    private static Graph graph(boolean directed, String ids, String... edges) {
        Graph g = new Graph(directed);
        for (String id : ids.split("")) g.addVertex(id);
        for (String e : edges) { // формат "A>B:5"
            String[] p = e.split("[>:]");
            g.addEdge(p[0], p[1], Long.parseLong(p[2]));
        }
        return g;
    }

    public static void main(String[] args) {
        // ---- 1. Основной граф (рисунок 1, исходные веса) ----
        Graph fig1 = Main.buildFigure1();
        FloydWarshallResult r1 = FloydWarshall.run(fig1);
        check("1.1 Основной граф: исходная матрица",
                "рисунок 1",
                "[0 7 9 INF INF INF][INF 0 INF -1 -2 INF][INF -3 0 INF 2 INF][INF INF INF 0 1 3][INF INF INF INF 0 3][INF INF INF INF INF 0]",
                matrix(r1.initialMatrix()));
        check("1.2 Основной граф: итоговая матрица (проверена вручную)",
                "рисунок 1",
                "[0 6 9 5 4 7][INF 0 INF -1 -2 1][INF -3 0 -4 -5 -2][INF INF INF 0 1 3][INF INF INF INF 0 3][INF INF INF INF INF 0]",
                matrix(r1.distanceMatrix()));
        PathResult p1 = r1.path("A", "F");
        check("1.3 Основной граф: путь A->F", "A, F", "A->C->B->E->F (вес 7)", pathStr(p1));
        check("1.4 Независимый пересчёт веса пути по дугам: 9 + (-3) + (-2) + 3",
                "путь " + p1.path(), "7", Long.toString(Main.recomputeWeight(fig1, p1.path())));
        check("1.5 Отрицательных циклов нет (граф ацикличен, цикла нет)", "рисунок 1", "false",
                Boolean.toString(r1.hasNegativeCycle()));
        check("1.6 Альтернативные пути разного веса: A->B (прямая 7 против A->C->B = 6)", "A, B",
                "A->C->B (вес 6)", pathStr(r1.path("A", "B")));
        check("1.7 Нет обратной дуги: F->A недостижима", "F, A", "NO_PATH", pathStr(r1.path("F", "A")));

        // ---- 2. Одна вершина ----
        FloydWarshallResult r2 = FloydWarshall.run(graph(true, "A"));
        check("2.1 Одна вершина: матрица", "вершина A", "[0]", matrix(r2.distanceMatrix()));
        check("2.2 Одна вершина: запрос A->A", "A, A", "A (вес 0)", pathStr(r2.path("A", "A")));

        // ---- 3. Недостижимая F при существующих A и F ----
        Graph g3 = graph(true, "ABF", "A>B:2");
        FloydWarshallResult r3 = FloydWarshall.run(g3);
        check("3.1 Недостижимая F: путь A->F", "A>B:2, вершина F изолирована", "NO_PATH", pathStr(r3.path("A", "F")));
        check("3.2 Недостижимая F: матрица содержит INF и не содержит сумм с INF",
                "тот же граф", "[0 2 INF][INF 0 INF][INF INF 0]", matrix(r3.distanceMatrix()));

        // ---- 4. Граф с (положительным) циклом ----
        Graph g4 = graph(true, "ABCF", "A>B:1", "B>C:1", "C>A:1", "C>F:5");
        FloydWarshallResult r4 = FloydWarshall.run(g4);
        check("4.1 Граф с циклом A->B->C->A (вес 3): путь A->F", "A>B:1 B>C:1 C>A:1 C>F:5",
                "A->B->C->F (вес 7)", pathStr(r4.path("A", "F")));
        check("4.2 Цикл положителен: отрицательного цикла нет, диагональ 0", "тот же граф", "false",
                Boolean.toString(r4.hasNegativeCycle()));
        check("4.3 Путь вокруг цикла: C->B (C->A->B)", "C, B", "C->A->B (вес 2)", pathStr(r4.path("C", "B")));

        // ---- 5. Граф без цикла - основной граф рисунка 1 (DAG) и цепочка ----
        Graph g5 = graph(true, "ABCF", "A>B:4", "B>C:-2", "C>F:3");
        FloydWarshallResult r5 = FloydWarshall.run(g5);
        check("5.1 Граф без цикла (цепочка с отрицательным весом): путь A->F", "A>B:4 B>C:-2 C>F:3",
                "A->B->C->F (вес 5)", pathStr(r5.path("A", "F")));

        // ---- 6. Отрицательный цикл ----
        Graph g6 = graph(true, "ABCDEF", "A>B:1", "B>C:-3", "C>A:1", "C>F:2", "D>E:2");
        FloydWarshallResult r6 = FloydWarshall.run(g6);
        check("6.1 Отрицательный цикл A->B->C->A (вес -1) диагностирован", "A>B:1 B>C:-3 C>A:1 C>F:2 D>E:2",
                "true", Boolean.toString(r6.hasNegativeCycle()));
        check("6.2 Вершины на отрицательном цикле", "тот же граф", "[A, B, C]",
                r6.negativeCycleVertices().toString());
        check("6.3 Затронутая пара A->F: конечный путь не выдаётся", "A, F", "NEGATIVE_CYCLE",
                pathStr(r6.path("A", "F")));
        check("6.4 Незатронутая пара D->E по-прежнему корректна", "D, E", "D->E (вес 2)", pathStr(r6.path("D", "E")));
        check("6.5 Отрицательная петля A->A (-1) - тоже отрицательный цикл", "A>A:-1",
                "true", Boolean.toString(FloydWarshall.run(graph(true, "A", "A>A:-1")).hasNegativeCycle()));

        // ---- 7. Повторный запуск на том же объекте графа ----
        FloydWarshallResult again = FloydWarshall.run(fig1);
        check("7.1 Повторный запуск на том же Graph: матрица та же", "рисунок 1, второй запуск",
                matrix(r1.distanceMatrix()), matrix(again.distanceMatrix()));
        check("7.2 Повторный запуск: путь тот же", "A, F", pathStr(p1), pathStr(again.path("A", "F")));
        long[][] copy = r1.distanceMatrix();
        copy[0][5] = 999; // изменение копии не должно портить результат
        check("7.3 Результат защищён от внешних изменений (копия матрицы)", "правка копии [A][F]=999",
                "7", Long.toString(r1.distance("A", "F")));

        // ---- 8. Модель графа ----
        Graph u = graph(false, "AB", "A>B:1");
        check("8.1 Неориентированный граф: связь в обе стороны", "A>B:1 (directed=false)",
                "true/true", u.getEdgeWeight("A", "B").isPresent() + "/" + u.getEdgeWeight("B", "A").isPresent());
        Graph dd = graph(true, "AB", "A>B:1");
        check("8.2 Ориентированный граф: обратной дуги нет", "A>B:1 (directed=true)",
                "true/false", dd.getEdgeWeight("A", "B").isPresent() + "/" + dd.getEdgeWeight("B", "A").isPresent());
        Graph dup = new Graph(true);
        dup.addVertex("A");
        dup.addVertex("A");
        check("8.3 Повторное добавление вершины не создаёт дубликат", "addVertex(A) x2", "1", Integer.toString(dup.size()));
        check("8.4 Дуга к несуществующей вершине - исключение", "addEdge(A, Z, 1)", "IllegalArgumentException", thrown(
                () -> dup.addEdge("A", "Z", 1)));
        check("8.5 Запрос пути для неизвестной вершины - исключение", "path(A, Z)", "IllegalArgumentException",
                thrown(() -> r1.path("A", "Z")));
        check("8.6 Ребро с нулевым весом отличается от отсутствия ребра", "A>B:0, вершины A,B,C",
                "[0 0 INF][INF 0 INF][INF INF 0]",
                matrix(FloydWarshall.run(graph(true, "ABC", "A>B:0")).distanceMatrix()));

        System.out.println();
        System.out.println("Итого: пройдено " + passed + ", провалено " + failed);
        if (failed > 0) System.exit(1);
    }

    private static String thrown(Runnable r) {
        try {
            r.run();
            return "нет исключения";
        } catch (IllegalArgumentException e) {
            return "IllegalArgumentException";
        } catch (RuntimeException e) {
            return e.getClass().getSimpleName();
        }
    }
}
