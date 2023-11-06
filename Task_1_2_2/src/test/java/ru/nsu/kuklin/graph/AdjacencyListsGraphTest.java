package ru.nsu.kuklin.graph;

/**
 * Tests for AdjacencyList graph implementation.
 */
public class AdjacencyListsGraphTest extends GraphTest<AdjacencyListGraph> {
    public <V> Graph<V, ?> getInstance() {
        return new AdjacencyListGraph<V>();
    }
}


