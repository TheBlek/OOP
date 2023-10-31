package ru.nsu.kuklin.graph;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;

/** 
 * Graph base class. 
 * Defines a bunch of useful functions using API as slim as possible
 *
 */
public abstract class Graph<V, E extends Edge<VertexIndex>> {
    /** 
     * Load graph from file. By default in adjacency matrix format.
     *
     * @param filename  filename.
     * @param standin  standin vertex object to populate graph with. 
     * @throws  ParseException - if adjecency matrix is broken
     * @throws  FileNotFoundException  - if no file :)
     */
    void loadFromFile(String filename, V standin) throws ParseException, FileNotFoundException {
        clear();

        var classLoader = getClass().getClassLoader();
        var resource = classLoader.getResourceAsStream(filename);
        var reader = new Scanner(resource);
        var vertexCount = Integer.parseInt(reader.nextLine());
        var indices = new VertexIndex[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            indices[i] = addVertex(standin);
        }

        for (int i = 0; i < vertexCount; i++) {
            String line;
            try {
                line = reader.nextLine(); 
            } catch (NoSuchElementException e) {
                throw new ParseException(
                    "File does not have enough vertices data. Expected: " 
                    + vertexCount 
                    + ". Found: " 
                    + i,
                    i
                );
            }
            var atoms = line.split("\\s+");
            if (atoms.length != vertexCount) {
                throw new ParseException(
                    "Vertex #" 
                    + i 
                    + " have a different number of vertices. Found: " 
                    + atoms.length 
                    + " Expected: " 
                    + vertexCount,
                    i
                );
            }
            for (int j = 0; j < vertexCount; j++) {
                // If this parses into float - it's a weight :)
                try {
                    float weight = Float.parseFloat(atoms[j]);
                    setEdge(indices[i], indices[j], weight);
                } catch (Exception e) {
                    continue; 
                }
            }
        }
        reader.close();
    }
    
    /**
     * Adds vertex to the graph and returns a handle to it.
     *
     * @parameter vertex Vertex value
     * @return Handle
     */
    abstract VertexIndex addVertex(V vertex);

    /**
     * Set a vertex with given handle to the given value and returns previous value if it existed.
     * Does not create a new vertex on invalid handle.
     *
     * @parameter id Vertex handle
     * @parameter value New vertex value
     */
    abstract Optional<V> setVertex(VertexIndex id, V value);

    /**
     * Get a vertex value with given handle.
     *
     * @parameter id Vertex handle
     *
     * @return Vertex value
     */
    abstract Optional<V> getVertex(VertexIndex id);

    /**
     * Removes a vertex with given handle and returns previous value if it existed.
     * Does nothing on invalid handle.
     *
     * @parameter id Vertex handle
     *
     * @return Previous vertex value
     */
    abstract Optional<V> removeVertex(VertexIndex id);

    /**
     * Get vertex count in graph.
     *
     * 
     * @return Vertex count.
     */
    abstract int getVertexCount();

    /**
     * Returns dense index from handle.
     * Dense indecies are guaranteed to stay in range [0; getVertexCount()).
     * However, the order is not guaranteed.
     *
     * 
     * @return Dense index, if handle is valid.
     */
    abstract Optional<Integer> getDenseIndex(VertexIndex id);
    
    /**
     * Returns a handle from a dense index.
     * Dense indecies are guaranteed to stay in range [0; getVertexCount()).
     * However, the order is not guaranteed.
     *
     * 
     * @return Handle, if dense index is valid.
     */
    abstract Optional<VertexIndex> fromDenseIndex(int id);

    /**
     * Tests whether there's an edge with given ends.
     *
     * 
     * @return Existence of edge
     */
    abstract boolean hasEdge(VertexIndex from, VertexIndex to);

    /**
     * Set the weight of edge between from and to to weight.
     * Creates an edge, if it didn't exist.
     *
     * 
     * @return Previous edge, if it existed
     */
    abstract Optional<E> setEdge(VertexIndex from, VertexIndex to, float weight);

    /**
     * Remove an edge with given ends.
     * 
     * @return Previous edge, if it existed
     */
    abstract Optional<E> removeEdge(VertexIndex from, VertexIndex to);

    /**
     * Get an edge with given ends.
     * 
     * @return Previous edge, if it exists
     */
    abstract Optional<E> getEdge(VertexIndex from, VertexIndex to);

    /**
     * Get a number of outgoing edges.
     * 
     * @return Number of outgoing edges
     */
    abstract int getOutNeighborCount(VertexIndex id);

    /**
     * Get a number of incoming edges.
     * 
     * @return Number of incoming edges
     */
    abstract int getInNeighborCount(VertexIndex id);

    /**
     * Iterable over all outgoing neighbors.
     * 
     * @return Iterable over all outgoing neighbors.
     */
    abstract Iterable<VertexIndex> getOutNeighbors(VertexIndex id);

    /**
     * Iterable over all incoming neighbors.
     * 
     * @return Iterable over all incoming neighbors.
     */
    abstract Iterable<VertexIndex> getInNeighbors(VertexIndex id);

    /**
     * Remove every object from the graph.
     */
    abstract void clear();

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Graph)) {
            return false;
        }

        var otherGraph = (Graph) other;

        if (otherGraph.getVertexCount() != getVertexCount()) {
            return false;
        }

        var vertexCount = getVertexCount();
        var used = new boolean[vertexCount];
        var mapping = new int[vertexCount];
        
        return tryMapping(0, used, mapping, otherGraph);
    }

    @Override
    public int hashCode() {
        int vertexCount = getVertexCount();
        int hash = 0;

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            VertexIndex id = fromDenseIndex(vertex).get();
            hash += getVertex(id).get().hashCode();
            for (var neighbor : getOutNeighbors(id)) {
                hash += getVertex(neighbor).get().hashCode() * neighbor.id(); 
            }

            for (var neighbor : getInNeighbors(id)) {
                hash += getVertex(neighbor).get().hashCode() * neighbor.id(); 
            }
        }

        return hash;
    }

    /**
     * Calculates distances from a vertex with given index.
     * Uses dijkstra algorithm to do so.
     * Hence BEHAVIOUR IS UNDEFINED ON GRAPHS WITH NEGATIVE WEIGHTS.
     *
     * @return A sorted (from near to far) array of vertex handles and their distances
     */
    public ArrayList<DistanceData> calculateDistances(VertexIndex from) {
        var vertexCount = getVertexCount();
        var result = new ArrayList<DistanceData>(vertexCount);
        var distance = new float[vertexCount];
        var visited = new boolean[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            distance[i] = Float.MAX_VALUE;
            visited[i] = false;
        }
        var start = getDenseIndex(from).get();
        distance[start] = 0.f;

        while (result.size() < vertexCount) {
            var closest = -1;
            for (int i = 0; i < vertexCount; i++) {
                if (visited[i]) {
                    continue;
                }
                if (closest == -1 || distance[closest] > distance[i]) {
                    closest = i;
                }
            }

            var closestIndex = fromDenseIndex(closest).get();
            for (var vertexIndex : getOutNeighbors(closestIndex)) {
                var vertex = getDenseIndex(vertexIndex).get();
                var weight = getEdge(closestIndex, vertexIndex).get().weight();
                if (distance[vertex] > distance[closest] + weight) {
                    distance[vertex] = distance[closest] + weight;
                }
            }

            result.add(new DistanceData(closestIndex, distance[closest]));
            visited[closest] = true;
        }

        return result;
    }

    private boolean tryMapping(int current, boolean[] used, int[] mapping, Graph<V, E> other) {
        if (current == used.length) {
            return true;
        }
        NEXT_MAPPING: 
        for (int d2 = 0; d2 < used.length; d2++) {
            if (used[d2]) {
                continue NEXT_MAPPING;
            }

            var d2Id = fromDenseIndex(d2).get();
            var currentId = fromDenseIndex(current).get();
            if (!other.getVertex(d2Id).get().equals(getVertex(currentId).get())) {
                continue NEXT_MAPPING;
            }

            mapping[current] = d2;

            // Local check: check that neighbors of newly mapped vertex match with previous mappings
            if (getOutNeighborCount(currentId) != other.getOutNeighborCount(d2Id)) {
                continue NEXT_MAPPING;
            }

            if (getInNeighborCount(currentId) != other.getInNeighborCount(d2Id)) {
                continue NEXT_MAPPING;
            }

            // Check that out neighbors match
            for (var neighbor : getOutNeighbors(currentId)) {
                var nd = getDenseIndex(neighbor).get();
                // This neighbor has not been mapped yet
                if (nd > current) {
                    continue;
                }
                var nd2 = mapping[nd];
                var edge = getEdge(currentId, neighbor).get();
                var edge2 = other.getEdge(d2Id, other.fromDenseIndex(nd2).get());
                if (!edge2.isPresent() || edge2.get().weight() != edge.weight()) {
                    continue NEXT_MAPPING;
                }
            }

            // Check that in neighbors match
            for (var neighbor : getInNeighbors(currentId)) {
                var nd = getDenseIndex(neighbor).get();
                // This neighbor has not been mapped yet
                if (nd > current) {
                    continue;
                }
                var nd2 = mapping[nd];
                var edge = getEdge(neighbor, currentId).get();
                var edge2 = other.getEdge(other.fromDenseIndex(nd2).get(), d2Id);
                if (!edge2.isPresent() || edge2.get().weight() != edge.weight()) {
                    break NEXT_MAPPING;
                }
            }

            used[d2] = true;
            if (tryMapping(current + 1, used, mapping, other)) {
                return true;
            }
            used[d2] = false;
        }
        return false;
    }
}

