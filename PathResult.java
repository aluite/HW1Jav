package graphs;

import java.util.List;

/** Ответ на запрос "путь from -> to": статус, вершины пути и вес (если путь есть). */
public record PathResult(Status status, List<String> path, long weight) {

    public enum Status {
        /** Конечный кратчайший путь найден. */
        OK,
        /** Пути нет: to недостижима из from. */
        NO_PATH,
        /** Путь затронут отрицательным циклом: кратчайшего пути не существует. */
        NEGATIVE_CYCLE
    }

    public boolean found() { return status == Status.OK; }
}
