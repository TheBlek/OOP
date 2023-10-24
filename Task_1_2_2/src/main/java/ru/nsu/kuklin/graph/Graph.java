package ru.nsu.kuklin.graph;

import java.util.Optional;
import java.io.FileNotFoundException;
import java.text.ParseException;

// Does not support multiple edges between vertices
public interface Graph<V, E extends Edge<VertexIndex>> {
    void LoadFromFile(String filename, V stadin) throws ParseException, FileNotFoundException;

    VertexIndex addVertex(V vertex);
    // Return previous value, if it existed
    // Set on non-existant does not create one
    Optional<V> setVertex(VertexIndex id, V value);
    Optional<V> getVertex(VertexIndex id);
    // Whether it was in the graph
    Optional<V> removeVertex(VertexIndex id);

    boolean hasEdge(VertexIndex from, VertexIndex to);
    // Create edge if it didn't exist otherwise replace edge with given weight and return the previous one 
    Optional<E> setEdge(VertexIndex from, VertexIndex to, float weight);
    // Return previous one, if it existed
    Optional<E> removeEdge(VertexIndex from, VertexIndex to);
    Optional<E> getEdge(VertexIndex from, VertexIndex to);

    void clear();
}

