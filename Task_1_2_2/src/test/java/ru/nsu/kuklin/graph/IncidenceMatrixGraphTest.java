package ru.nsu.kuklin.graph;

public class IncidenceMatrixGraphTest extends GraphTest<IncidenceMatrixGraph> {
    public <V> Graph<V, ?> getInstance() {
        return new IncidenceMatrixGraph<V>();
    }
}


