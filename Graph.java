package graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

/**
 * Граф со взвешенными рёбрами. Режим задаётся при создании:
 * directed = true  - тип 1 (ориентированный граф),
 * directed = false - тип 2 (каждая связь добавляется в обоих направлениях).
 * Отсутствие ребра НЕ кодируется нулевым весом: ребра просто нет в списке смежности.
 * Сам граф не хранит состояния алгоритмов, поэтому повторные запуски независимы.
 */
public class Graph {
    private final boolean directed;
    private final Map<String, Vertex> vertices = new LinkedHashMap<>();

    public Graph(boolean directed) { this.directed = directed; }

    public boolean isDirected() { return directed; }

    /** Добавляет вершину; если она уже есть, возвращает существующую. */
    public Vertex addVertex(String id) {
        Vertex v = vertices.get(id);
        if (v == null) {
            v = new Vertex(id);
            vertices.put(id, v);
        }
        return v;
    }

    /** Добавляет дугу from -> to (для неориентированного графа ещё и to -> from). */
    public void addEdge(String from, String to, long weight) {
        Vertex a = vertices.get(from);
        Vertex b = vertices.get(to);
        if (a == null || b == null) {
            throw new IllegalArgumentException("Вершина не найдена: " + (a == null ? from : to));
        }
        a.addOutgoing(new Edge(a, b, weight));
        if (!directed && !a.equals(b)) {
            b.addOutgoing(new Edge(b, a, weight));
        }
    }

    public boolean hasVertex(String id) { return vertices.containsKey(id); }

    public Vertex getVertex(String id) { return vertices.get(id); }

    public int size() { return vertices.size(); }

    /** Идентификаторы вершин в алфавитном порядке (порядок строк и столбцов матриц). */
    public List<String> vertexIds() {
        List<String> ids = new ArrayList<>(vertices.keySet());
        Collections.sort(ids);
        return ids;
    }

    /** Все хранимые дуги (для неориентированного графа - обе направленные копии). */
    public List<Edge> getEdges() {
        List<Edge> result = new ArrayList<>();
        for (String id : vertexIds()) {
            result.addAll(vertices.get(id).getOutgoing());
        }
        return result;
    }

    /** Минимальный вес дуги from -> to; пусто, если дуги нет. */
    public OptionalLong getEdgeWeight(String from, String to) {
        Vertex a = vertices.get(from);
        if (a == null) return OptionalLong.empty();
        long best = 0;
        boolean found = false;
        for (Edge e : a.getOutgoing()) {
            if (e.getTo().getId().equals(to) && (!found || e.getWeight() < best)) {
                best = e.getWeight();
                found = true;
            }
        }
        return found ? OptionalLong.of(best) : OptionalLong.empty();
    }
}
