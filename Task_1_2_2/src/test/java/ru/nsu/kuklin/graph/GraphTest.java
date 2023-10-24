package ru.nsu.kuklin.graph;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.io.FileNotFoundException;
import java.text.ParseException;
/**
 * Class for tests.
 */
public class GraphTest {
    @Test
    @DisplayName("Vertex addition")
    public void testVertexAddition() {
        Graph<Integer, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex(90);
        var id2 = graph.addVertex(70);
        assertEquals(Optional.of(90), graph.getVertex(id1));
        assertEquals(Optional.of(70), graph.getVertex(id2));
        assertEquals(Optional.empty(), graph.getEdge(id1, id2));
    }
    
    @Test
    @DisplayName("Vertex removal")
    public void testVertexRemoval() {
        Graph<Integer, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex(90);
        var id2 = graph.addVertex(70);
        graph.removeVertex(id1);
        assertEquals(Optional.empty(), graph.getVertex(id1));
        assertEquals(Optional.of(70), graph.getVertex(id2));
        assertEquals(Optional.empty(), graph.getEdge(id1, id2));
    }

    @Test
    @DisplayName("Edge addition")
    public void testEdgeAddition() {
        Graph<Integer, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex(90);
        var id2 = graph.addVertex(70);
        assertEquals(Optional.empty(), graph.setEdge(id1, id2, 9.6f));
        assertEquals(Optional.of(new Edge(id1, id2, 9.6f)), graph.getEdge(id1, id2));
        assertEquals(Optional.empty(), graph.getEdge(id2, id1));
    }

    @Test
    @DisplayName("Edge addition with vertex removal")
    public void testEdgeAdditionWithVertexRemoval() {
        Graph<String, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex("one");
        var id2 = graph.addVertex("two");
        var id3 = graph.addVertex("three");
        graph.setEdge(id1, id2, 8.7f);
        graph.setEdge(id2, id3, 1.3f);
        graph.removeVertex(id1);
        assertEquals(Optional.empty(), graph.getEdge(id1, id2));
        assertEquals(Optional.of(new Edge(id2, id3, 1.3f)), graph.getEdge(id2, id3));
    }

    @Test
    @DisplayName("Clear")
    public void testClear() {
        Graph<String, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex("one");
        var id2 = graph.addVertex("two");
        var id3 = graph.addVertex("three");
        graph.setEdge(id1, id2, 8.7f);
        var e1 = graph.getEdge(id1, id2);
        var e2 = graph.getEdge(id2, id3);
        graph.clear();

        assertEquals(Optional.empty(), graph.getVertex(id1));
        assertEquals(Optional.empty(), graph.getVertex(id2));
        assertEquals(Optional.empty(), graph.getVertex(id3));
        assertEquals(Optional.empty(), graph.getEdge(id1, id2));
        assertEquals(Optional.empty(), graph.getEdge(id2, id3));
    }

    @Test
    @DisplayName("Edge removal")
    public void testEdgeRemoval() {
        Graph<Double, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex(1.5);
        var id2 = graph.addVertex(3.);
        var id3 = graph.addVertex(9.);
        graph.setEdge(id1, id2, 0f);

        assertTrue(graph.hasEdge(id1, id2));
        assertEquals(Optional.of(new Edge(id1, id2, 0f)), graph.removeEdge(id1, id2));
        assertEquals(Optional.empty(), graph.getEdge(id1, id2));
        assertFalse(graph.hasEdge(id1, id2));
    }

    @Test
    @DisplayName("Not possible vertex removals")
    public void testNotPossibleVertexRemovals() {
        Graph<Double, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex(1.5);
        var id2 = graph.addVertex(3.);
        var id3 = graph.addVertex(9.);
        graph.setEdge(id1, id2, 0f);
        var e1 = graph.getEdge(id1, id2);
        
        assertEquals(Optional.empty(), graph.removeVertex(new VertexIndex(9)));
        assertEquals(Optional.of(1.5), graph.getVertex(id1));
        assertEquals(Optional.of(3.), graph.getVertex(id2));
        assertEquals(Optional.of(9.), graph.getVertex(id3));
        assertEquals(Optional.of(new Edge(id1, id2, 0f)), graph.getEdge(id1, id2));
    }

    @Test
    @DisplayName("Not possible edge removals")
    public void testNotPossibleEdgeRemovals() {
        Graph<Double, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex(1.5);
        var id2 = graph.addVertex(3.);
        var id3 = graph.addVertex(9.);
        graph.setEdge(id1, id2, 0f);
        var e1 = graph.getEdge(id1, id2);
        
        assertEquals(Optional.empty(), graph.removeEdge(id1, new VertexIndex(9)));
        assertEquals(Optional.empty(), graph.removeEdge(id2, id1));
        assertEquals(Optional.of(1.5), graph.getVertex(id1));
        assertEquals(Optional.of(3.), graph.getVertex(id2));
        assertEquals(Optional.of(9.), graph.getVertex(id3));
        assertEquals(Optional.of(new Edge(id1, id2, 0f)), graph.getEdge(id1, id2));
    }

    @Test
    @DisplayName("Not possible edge additions")
    public void testNotPossibleEdgeAdditions() {
        Graph<Double, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex(1.5);
        var id2 = graph.addVertex(3.);
        var id3 = graph.addVertex(9.);
        graph.setEdge(id1, id2, 0f);
        graph.setEdge(id1, new VertexIndex(9), 0f);
        graph.setEdge(new VertexIndex(123), new VertexIndex(9), 0f);
        graph.setEdge(new VertexIndex(123), id2, 0f);
        
        assertEquals(Optional.of(1.5), graph.getVertex(id1));
        assertEquals(Optional.of(3.), graph.getVertex(id2));
        assertEquals(Optional.of(9.), graph.getVertex(id3));
        assertEquals(Optional.of(new Edge(id1, id2, 0f)), graph.getEdge(id1, id2));
    }

    @Test
    @DisplayName("Reassign vertex")
    public void testReassignVertex() {
        Graph<Double, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex(1.5);
        var id2 = graph.addVertex(3.);
        var id3 = graph.addVertex(9.);
        assertEquals(Optional.of(1.5), graph.setVertex(id1, 123.3));
        
        assertEquals(Optional.of(123.3), graph.getVertex(id1));
        assertEquals(Optional.of(3.), graph.getVertex(id2));
        assertEquals(Optional.of(9.), graph.getVertex(id3));
    }

    @Test
    @DisplayName("Reassign non-existent vertex")
    public void testReassignNonExistentVertex() {
        Graph<Double, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex(1.5);
        var id2 = graph.addVertex(3.);
        var id3 = graph.addVertex(9.);
        graph.removeVertex(id3);
        assertEquals(Optional.empty(), graph.setVertex(id3, 123.3));
        
        assertEquals(Optional.of(1.5), graph.getVertex(id1));
        assertEquals(Optional.of(3.), graph.getVertex(id2));
        assertEquals(Optional.empty(), graph.getVertex(id3));
    }

    @Test
    @DisplayName("Reassign edge") 
    public void testReassignEdge() {
        Graph<Double, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex(1.5);
        var id2 = graph.addVertex(3.);
        var id3 = graph.addVertex(9.);
        graph.setEdge(id1, id2, 5.4f);
        graph.setEdge(id1, id2, 2.4f);
        assertEquals(Optional.of(new Edge(id1, id2, 2.4f)), graph.getEdge(id1, id2));
    }

    @Test
    @DisplayName("Remove non-existant edge") 
    public void testRemoveNonExistantEdge() {
        Graph<Double, ?> graph = new AdjacencyGraph<>();
        var id1 = graph.addVertex(1.5);
        var id2 = graph.addVertex(3.);
        var id3 = graph.addVertex(9.);
        graph.setEdge(id1, id2, 5.4f);
        assertEquals(Optional.empty(), graph.getEdge(id2, id1));
        assertEquals(Optional.empty(), graph.removeEdge(id2, id1));

        assertEquals(Optional.empty(), graph.getEdge(new VertexIndex(9), id1));
        assertEquals(Optional.empty(), graph.removeEdge(new VertexIndex(9), id1));

        assertEquals(Optional.empty(), graph.getEdge(id1, new VertexIndex(9)));
        assertEquals(Optional.empty(), graph.removeEdge(id1, new VertexIndex(9)));

        assertEquals(Optional.empty(), graph.getEdge(new VertexIndex(20), new VertexIndex(9)));
        assertEquals(Optional.empty(), graph.removeEdge(new VertexIndex(123), new VertexIndex(9)));
    }

    @Test
    @DisplayName("Load graph from file")
    public void testLoadFromFile() throws ParseException, FileNotFoundException {
        Graph<Double, ?> graph = new AdjacencyGraph<>();
        graph.LoadFromFile("test_graph.txt", 0.0);

        var id0 = new VertexIndex(0);
        var id1 = new VertexIndex(1);
        var id2 = new VertexIndex(2);
        
        assertEquals(Optional.of(new Edge(id0, id0, 4.3f)), graph.getEdge(id0, id0));
        assertEquals(Optional.of(new Edge(id0, id1, 1.0f)), graph.getEdge(id0, id1));
        assertEquals(Optional.empty(), graph.getEdge(id0, id2));

        assertEquals(Optional.empty(), graph.getEdge(id1, id0));
        assertEquals(Optional.of(new Edge(id1, id1, 13.0f)), graph.getEdge(id1, id1));
        assertEquals(Optional.of(new Edge(id1, id2, 15.0f)), graph.getEdge(id1, id2));

        assertEquals(Optional.of(new Edge(id2, id0, -8.0f)), graph.getEdge(id2, id0));
        assertEquals(Optional.empty(), graph.getEdge(id2, id1));
        assertEquals(Optional.of(new Edge(id2, id2, 15.4f)), graph.getEdge(id2, id2));
    }
}
