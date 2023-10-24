package ru.nsu.kuklin.graph;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;


 /**
 * The class Adjacency graph< v> implements graph< v, edge< vertex index>>
 */ 
public class AdjacencyGraph<V> implements Graph<V, Edge<VertexIndex>> {

/** 
 *
 * Adjacency graph
 *
 * @return public
 */
    public AdjacencyGraph() { 

        adjecencyMatrix = new ArrayList<>();
        vertices = new ArrayList<>();
        indices = new ArrayList<>();
    }


/** 
 *
 * Add vertex
 *
 * @param vertex  the vertex. 
 * @return VertexIndex
 */
    public VertexIndex addVertex(V vertex) { 

        int id = vertices.indexOf(Optional.empty());
        if (id == -1) {
            id = vertices.size();
            vertices.add(Optional.empty());
            indices.add(0);
        }
        vertices.set(id, Optional.of(vertex));
        indices.set(id, adjecencyMatrix.size());
        for (var row : adjecencyMatrix) {
            row.add(Optional.empty());
        }
        var newRow = new ArrayList<Optional<Float>>(vertices.size());
        for (int i = 0; i < vertices.size(); i++) {
            newRow.add(Optional.empty());
        }
        adjecencyMatrix.add(newRow);
        return new VertexIndex(id);
    }


/** 
 *
 * Sets the vertex
 *
 * @param id  the id. 
 * @param value  the value. 
 * @return Optional<V>
 */
    public Optional<V> setVertex(VertexIndex id, V value) { 

        if (!exists(id)) {
            return Optional.empty();
        }
        return vertices.set(id.id(), Optional.of(value));
    }


/** 
 *
 * Gets the vertex
 *
 * @param id  the id. 
 * @return the vertex
 */
    public Optional<V> getVertex(VertexIndex id) { 

        if (!exists(id)) {
            return Optional.empty();
        }
        return vertices.get(id.id());
    }


/** 
 *
 * Remove vertex
 *
 * @param id  the id. 
 * @return Optional<V>
 */
    public Optional<V> removeVertex(VertexIndex id) { 

        if (!exists(id)) {
            return Optional.empty();
        }
        int deleted = indices.get(id.id());
        adjecencyMatrix.remove(deleted);
        for (var row : adjecencyMatrix) {
            row.remove(deleted);
        }
        for (int i = 0; i < indices.size(); i++) {
            if (indices.get(i) > deleted) {
                indices.set(i, indices.get(i)-1);
            }
        }
        return vertices.set(id.id(), Optional.empty());
    }


/** 
 *
 * Has edge
 *
 * @param from  the from. 
 * @param to  the to. 
 * @return boolean
 */
    public boolean hasEdge(VertexIndex from, VertexIndex to) { 

        return adjecencyMatrix.get(from.id()).get(to.id()).isPresent();
    }


/** 
 *
 * Sets the edge
 *
 * @param from  the from. 
 * @param to  the to. 
 * @param weight  the weight. 
 * @return Optional<Edge<VertexIndex>>
 */
    public Optional<Edge<VertexIndex>> setEdge(VertexIndex from, VertexIndex to, float weight) { 

        if (!exists(from) || !exists(to)) {
            return Optional.empty();
        }
        return adjecencyMatrix
            .get(indices.get(from.id()))
            .set(indices.get(to.id()), Optional.of(weight))
            .map(w -> new Edge<>(from, to, w));
    }


/** 
 *
 * Remove edge
 *
 * @param from  the from. 
 * @param to  the to. 
 * @return Optional<Edge<VertexIndex>>
 */
    public Optional<Edge<VertexIndex>> removeEdge(VertexIndex from, VertexIndex to) { 

        if (!exists(from) || !exists(to)) {
            return Optional.empty();
        }
        return adjecencyMatrix
            .get(indices.get(from.id()))
            .set(indices.get(to.id()), Optional.empty())
            .map(w -> new Edge<>(from, to, w));
    }


/** 
 *
 * Gets the edge
 *
 * @param from  the from. 
 * @param to  the to. 
 * @return the edge
 */
    public Optional<Edge<VertexIndex>> getEdge(VertexIndex from, VertexIndex to) { 

        if (!exists(from) || !exists(to)) {
            return Optional.empty();
        }
        return adjecencyMatrix
            .get(indices.get(from.id()))
            .get(indices.get(to.id()))
            .map(w -> new Edge<>(from, to, w));
    }


/** 
 *
 * Clear
 *
 */
    public void clear() { 

        vertices.clear();
        adjecencyMatrix.clear();
    }


    /** 
     *
     * Load from file
     *
     * @param filename  the filename. 
     * @param standin  the standin. 
     * @throws  ParseException - if adjecency matrix is broken
     * @throws  FileNotFoundException 
     */
    public void LoadFromFile(String filename, V standin) throws ParseException, FileNotFoundException { 

        clear();

        var file = new File(filename);
        var reader = new Scanner(file);
        var vertexCount = Integer.parseInt(reader.nextLine());
        var indices = new VertexIndex[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            indices[i] = addVertex(standin);
        }

        for (int i = 0; i < vertexCount; i++) {
            var line = reader.nextLine(); 
            var atoms = line.split("\\s+");
            if (atoms.length != vertexCount) {
                throw new ParseException(
                    "Vertex #" + i + " have a different number of vertices. Found: " + atoms.length + " Expected: " + vertexCount,
                    i
                );
            }
            for (int j = 0; j < vertexCount; j++) {
                // If this parses into float - it's a weight :)
                try {
                    float weight = Float.parseFloat(atoms[j]);
                    setEdge(indices[i], indices[j], weight);
                    System.out.println("Set edge from " + i + " to " + j);
                }
                catch (Exception e) {}
            }
        }
    }

    private boolean exists(VertexIndex id) { 

        return id.id() < vertices.size() && vertices.get(id.id()).isPresent();
    }

    private ArrayList<ArrayList<Optional<Float>>> adjecencyMatrix;
    private ArrayList<Integer> indices;
    private ArrayList<Optional<V>> vertices;
}
