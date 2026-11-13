package graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Вершина графа. Идентифицируется строкой id (например, "A").
 * Хранит список исходящих дуг (список смежности).
 */
public final class Vertex {
    private final String id;
    private final List<Edge> outgoing = new ArrayList<>();

    public Vertex(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Идентификатор вершины не может быть пустым");
        }
        this.id = id;
    }

    public String getId() { return id; }

    /** Исходящие дуги (только для чтения). */
    public List<Edge> getOutgoing() { return Collections.unmodifiableList(outgoing); }

    void addOutgoing(Edge edge) { outgoing.add(edge); }

    // Две вершины равны, если равны их идентификаторы.
    @Override
    public boolean equals(Object o) {
        return o instanceof Vertex && ((Vertex) o).id.equals(id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() { return id; }
}
