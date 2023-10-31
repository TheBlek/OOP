package ru.nsu.kuklin.graph;


public class AdjacencyMatrixGraphTest extends GraphTest<AdjacencyMatrixGraph> {
    public <V> Graph<V, ?> getInstance() {
        return new AdjacencyMatrixGraph<V>();
    }
}


