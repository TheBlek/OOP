package ru.nsu.kuklin.graph;

/**
 * Tests for AdjacencyMatrix graph implementation.
 */
public class AdjacencyMatrixGraphTest extends GraphTest<AdjacencyMatrixGraph> {
    public <V> Graph<V, ?> getInstance() {
        return new AdjacencyMatrixGraph<V>();
    }
}


