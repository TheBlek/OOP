package ru.nsu.kuklin.graph;

// Does not support multiple edges between vertices
public interface Graph<V, E extends Edge<VertexIndex>> {
    abstract void LoadFromFile(String filename);

    abstract VertexIndex addVertex(V vertex);
    abstract boolean removeVertex(VertexIndex id);

    abstract E addEdge(VertexIndex from, VertexIndex to, float weight);
    // Check edge equality by value
    abstract E removeEdge(VertexIndex from, VertexIndex to);
     
    // Return previous value
    abstract E setEdge(VertexIndex from, VertexIndex to, E edge);
    abstract E getEdge(VertexIndex from, VertexIndex to);

    abstract V setVertex(VertexIndex id, V value);
    abstract V getVertex(VertexIndex id);
}


