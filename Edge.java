package graphs;

/**
 * Дуга ориентированного графа: from -> to с весом weight.
 * Объект неизменяемый. Для неориентированного графа Graph хранит две такие дуги.
 */
public final class Edge {
    private final Vertex from;
    private final Vertex to;
    private final long weight;

    public Edge(Vertex from, Vertex to, long weight) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Концы дуги не могут быть null");
        }
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Vertex getFrom() { return from; }
    public Vertex getTo() { return to; }
    public long getWeight() { return weight; }

    @Override
    public String toString() {
        return from.getId() + "->" + to.getId() + ": " + weight;
    }
}
