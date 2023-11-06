package ru.nsu.kuklin.graph;

/**
 * Tests for IncidenceMatrix graph implementation.
 */
public class IncidenceMatrixGraphTest extends GraphTest<IncidenceMatrixGraph> {
    public <V> Graph<V, ?> getInstance() {
        return new IncidenceMatrixGraph<V>();
    }
}


