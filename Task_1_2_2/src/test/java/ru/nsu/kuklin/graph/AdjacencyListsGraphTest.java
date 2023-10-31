package ru.nsu.kuklin.graph;

public class AdjacencyListsGraphTest extends GraphTest<AdjacencyListGraph> {
    public <V> Graph<V, ?> getInstance() {
        return new AdjacencyListGraph<V>();
    }
}


