# Домашнее задание 1 — Алгоритмы на графах в Java

- **Номер в списке:** 9 → **вариант D11**
- **Алгоритм:** Флойд — Уоршелл (кратчайшие пути между всеми парами вершин)
- **Граф:** ориентированный, рисунок 1, исходные веса (в том числе отрицательные; замена весов на модули относится только к C11 и здесь **не** применяется)
- **JDK:** 17 или новее (проверено на OpenJDK 21)

## Граф рисунка 1

Вершины: A, B, C, D, E, F.
Дуги: A→B: 7; A→C: 9; C→B: −3; B→D: −1; B→E: −2; C→E: 2; D→E: 1; D→F: 3; E→F: 3.
Старт — A, цель — F; порядок строк и столбцов матриц — A, B, C, D, E, F; INF — «пути нет».

## Структура проекта

```
homework1/
├── README.md
├── example_output.txt        вывод основной программы
├── tests_output.txt          вывод набора проверок (ожидаемое / фактическое)
├── report/                   отчёт DOCX
└── src/
    ├── main/java/graphs/
    │   ├── Vertex.java               вершина: id + исходящие дуги
    │   ├── Edge.java                 дуга: from, to, weight
    │   ├── Graph.java                граф (ориентированный / неориентированный)
    │   ├── FloydWarshall.java        сам алгоритм (без печати)
    │   ├── FloydWarshallResult.java  матрицы, восстановление пути, отрицательный цикл
    │   ├── PathResult.java           ответ на запрос пути (OK / NO_PATH / NEGATIVE_CYCLE)
    │   └── Main.java                 точка запуска: строит граф, печатает результат
    └── test/java/graphs/Tests.java   29 проверок с выводом «ожидалось / получено»
```

## Сборка и запуск

Из корня проекта (папка `homework1`).

**Linux / macOS:**
```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out graphs.Main            # основной результат (путь A -> F)
java -cp out graphs.Main C F        # необязательно: путь между другими вершинами
java -cp out graphs.Tests           # проверки; код возврата 1, если хоть одна не пройдена
```

**Windows (PowerShell):**
```powershell
mkdir out -Force
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
chcp 65001
java "-Dstdout.encoding=UTF-8" -cp out graphs.Main
java "-Dstdout.encoding=UTF-8" -cp out graphs.Tests
```

Если русские буквы в консоли отображаются как `???`, добавьте ключ `-Dstdout.encoding=UTF-8`.

## Пример вывода (`java -cp out graphs.Main`)

```
Исходная матрица расстояний:
         A     B     C     D     E     F
   A     0     7     9   INF   INF   INF
   B   INF     0   INF    -1    -2   INF
   C   INF    -3     0   INF     2   INF
   D   INF   INF   INF     0     1     3
   E   INF   INF   INF   INF     0     3
   F   INF   INF   INF   INF   INF     0

Итоговая матрица кратчайших расстояний:
         A     B     C     D     E     F
   A     0     6     9     5     4     7
   B   INF     0   INF    -1    -2     1
   C   INF    -3     0    -4    -5    -2
   D   INF   INF   INF     0     1     3
   E   INF   INF   INF   INF     0     3
   F   INF   INF   INF   INF   INF     0

Отрицательных циклов нет (диагональ матрицы неотрицательна).

Путь A -> F: A -> C -> B -> E -> F
Вес пути (из матрицы): 7
Вес пути (пересчёт по дугам графа): 7
```

## Ключевые решения

- Отсутствие дуги — значение `INF = Long.MAX_VALUE / 4`, а не 0; сложение выполняется только для двух конечных расстояний.
- Путь восстанавливается по матрице `next` (следующая вершина на пути); `next[i][j]` меняется только при строго меньшем расстоянии.
- После расчёта проверяется диагональ: `dist[i][i] < 0` — отрицательный цикл. Для пар (i, j), чей путь может пройти через такой цикл, конечный путь не выдаётся (статус `NEGATIVE_CYCLE`).
- Алгоритм не печатает и не хранит состояние в графе, поэтому повторный запуск на том же объекте `Graph` даёт тот же ответ.
- Для неориентированного графа (тип 2) `Graph(false).addEdge` добавляет связь в обоих направлениях.
