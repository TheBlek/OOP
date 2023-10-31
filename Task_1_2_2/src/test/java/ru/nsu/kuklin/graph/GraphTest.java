package ru.nsu.kuklin.graph;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.io.FileNotFoundException;
import java.text.ParseException;

import java.lang.reflect.ParameterizedType;

/**
 * Class for tests.
 */
public abstract class GraphTest<T extends Graph> {
    public abstract <V> Graph<V, ?> getInstance(); 

    @Test
    @DisplayName("Vertex addition")
    public void testVertexAddition() {
        Graph<Integer, ?> graph = getInstance();
        var id1 = graph.addVertex(90);
        var id2 = graph.addVertex(70);
        assertEquals(Optional.of(90), graph.getVertex(id1));
        assertEquals(Optional.of(70), graph.getVertex(id2));
        assertEquals(Optional.empty(), graph.getEdge(id1, id2));
    }
    
    @Test
    @DisplayName("Vertex removal")
    public void testVertexRemoval() {
        Graph<Integer, ?> graph = getInstance();
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
        Graph<Integer, ?> graph = getInstance();
        var id1 = graph.addVertex(90);
        var id2 = graph.addVertex(70);
        assertEquals(Optional.empty(), graph.setEdge(id1, id2, 9.6f));
        assertEquals(Optional.of(new Edge<>(id1, id2, 9.6f)), graph.getEdge(id1, id2));
        assertEquals(Optional.empty(), graph.getEdge(id2, id1));
    }

    @Test
    @DisplayName("Edge addition with vertex removal")
    public void testEdgeAdditionWithVertexRemoval() {
        Graph<String, ?> graph = getInstance();
        var id1 = graph.addVertex("one");
        var id2 = graph.addVertex("two");
        var id3 = graph.addVertex("three");
        graph.setEdge(id1, id2, 8.7f);
        graph.setEdge(id2, id3, 1.3f);
        graph.removeVertex(id1);
        assertEquals(Optional.empty(), graph.getEdge(id1, id2));
        assertEquals(Optional.of(new Edge<>(id2, id3, 1.3f)), graph.getEdge(id2, id3));
    }

    @Test
    @DisplayName("Clear")
    public void testClear() {
        Graph<String, ?> graph = getInstance();
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
        assertEquals(0, graph.getVertexCount());
    }

    @Test
    @DisplayName("Edge removal")
    public void testEdgeRemoval() {
        Graph<Double, ?> graph = getInstance();
        var id1 = graph.addVertex(1.5);
        var id2 = graph.addVertex(3.);
        var id3 = graph.addVertex(9.);
        graph.setEdge(id1, id2, 0f);

        assertTrue(graph.hasEdge(id1, id2));
        assertEquals(Optional.of(new Edge<>(id1, id2, 0f)), graph.removeEdge(id1, id2));
        assertEquals(Optional.empty(), graph.getEdge(id1, id2));
        assertFalse(graph.hasEdge(id1, id2));
    }

    @Test
    @DisplayName("Not possible vertex removals")
    public void testNotPossibleVertexRemovals() {
        Graph<Double, ?> graph = getInstance();
        var id1 = graph.addVertex(1.5);
        var id2 = graph.addVertex(3.);
        var id3 = graph.addVertex(9.);
        graph.setEdge(id1, id2, 0f);
        var e1 = graph.getEdge(id1, id2);
        
        assertEquals(Optional.empty(), graph.removeVertex(new VertexIndex(9)));
        assertEquals(Optional.of(1.5), graph.getVertex(id1));
        assertEquals(Optional.of(3.), graph.getVertex(id2));
        assertEquals(Optional.of(9.), graph.getVertex(id3));
        assertEquals(Optional.of(new Edge<>(id1, id2, 0f)), graph.getEdge(id1, id2));
    }

    @Test
    @DisplayName("Not possible edge removals")
    public void testNotPossibleEdgeRemovals() {
        Graph<Double, ?> graph = getInstance();
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
        assertEquals(Optional.of(new Edge<>(id1, id2, 0f)), graph.getEdge(id1, id2));
    }

    @Test
    @DisplayName("Not possible edge additions")
    public void testNotPossibleEdgeAdditions() {
        Graph<Double, ?> graph = getInstance();
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
        assertEquals(Optional.of(new Edge<>(id1, id2, 0f)), graph.getEdge(id1, id2));
    }

    @Test
    @DisplayName("Reassign vertex")
    public void testReassignVertex() {
        Graph<Double, ?> graph = getInstance();
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
        Graph<Double, ?> graph = getInstance();
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
        Graph<Double, ?> graph = getInstance();
        var id1 = graph.addVertex(1.5);
        var id2 = graph.addVertex(3.);
        var id3 = graph.addVertex(9.);
        graph.setEdge(id1, id2, 5.4f);
        assertEquals(Optional.of(new Edge<>(id1, id2, 5.4f)), graph.getEdge(id1, id2));
        graph.setEdge(id1, id2, 2.4f);
        assertEquals(Optional.of(new Edge<>(id1, id2, 2.4f)), graph.getEdge(id1, id2));
    }

    @Test
    @DisplayName("Remove non-existant edge") 
    public void testRemoveNonExistantEdge() {
        Graph<Double, ?> graph = getInstance();
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
    @DisplayName("Neighbor out neighbor iterator")
    public void testNeighborOutIterator() {
        Graph<Integer, ?> graph = getInstance();
        var id1 = graph.addVertex(90);
        var id2 = graph.addVertex(70);
        var id3 = graph.addVertex(60);
        graph.setEdge(id1, id2, 9.6f);
        graph.setEdge(id1, id3, -0.6f);
        
        var expected = new ArrayList<VertexIndex>();
        expected.add(id2);
        expected.add(id3);

        var actual = new ArrayList<VertexIndex>();
        graph.getOutNeighbors(id1).iterator().forEachRemaining(actual::add);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Out neighbor iterator concurrent modification")
    public void testNeighborOutIteratorThrow() {
        Graph<Integer, ?> graph = getInstance();
        var id1 = graph.addVertex(90);
        var id2 = graph.addVertex(70);
        var id3 = graph.addVertex(60);
        graph.setEdge(id1, id2, 9.6f);
        graph.setEdge(id1, id3, -0.6f);
        
        assertThrows(
            new ConcurrentModificationException().getClass(),
            () -> {
                for (var neighbor : graph.getOutNeighbors(id1)) {
                    graph.addVertex(neighbor.id());
                }
            }
        );
    }

    @Test
    @DisplayName("Neighbor in neighbor iterator")
    public void testNeighborInIterator() {
        Graph<Integer, ?> graph = getInstance();
        var id1 = graph.addVertex(90);
        var id2 = graph.addVertex(70);
        var id3 = graph.addVertex(60);
        graph.setEdge(id2, id1, 9.6f);
        graph.setEdge(id3, id1, -0.6f);
        
        var expected = new ArrayList<VertexIndex>();
        expected.add(id2);
        expected.add(id3);

        var actual = new ArrayList<VertexIndex>();
        graph.getInNeighbors(id1).iterator().forEachRemaining(actual::add);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("In neighbor iterator concurrent modification")
    public void testNeighborInIteratorThrow() {
        Graph<Integer, ?> graph = getInstance();
        var id1 = graph.addVertex(90);
        var id2 = graph.addVertex(70);
        var id3 = graph.addVertex(60);
        graph.setEdge(id2, id1, 9.6f);
        graph.setEdge(id3, id1, -0.6f);
        
        assertThrows(
            new ConcurrentModificationException().getClass(),
            () -> {
                for (var neighbor : graph.getInNeighbors(id1)) {
                    graph.addVertex(neighbor.id());
                }
            }
        );
    }

    @Test
    @DisplayName("Load graph from file")
    public void testLoadFromFile() throws ParseException, FileNotFoundException {
        Graph<Double, ?> graph = getInstance();
        graph.LoadFromFile("test_graph.txt", 0.0);

        var id0 = new VertexIndex(0);
        var id1 = new VertexIndex(1);
        var id2 = new VertexIndex(2);
        
        assertEquals(Optional.of(new Edge<>(id0, id0, 4.3f)), graph.getEdge(id0, id0));
        assertEquals(Optional.of(new Edge<>(id0, id1, 1.0f)), graph.getEdge(id0, id1));
        assertEquals(Optional.empty(), graph.getEdge(id0, id2));

        assertEquals(Optional.empty(), graph.getEdge(id1, id0));
        assertEquals(Optional.of(new Edge<>(id1, id1, 13.0f)), graph.getEdge(id1, id1));
        assertEquals(Optional.of(new Edge<>(id1, id2, 15.0f)), graph.getEdge(id1, id2));

        assertEquals(Optional.of(new Edge<>(id2, id0, -8.0f)), graph.getEdge(id2, id0));
        assertEquals(Optional.empty(), graph.getEdge(id2, id1));
        assertEquals(Optional.of(new Edge<>(id2, id2, 15.4f)), graph.getEdge(id2, id2));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-graph-1.txt", "invalid-graph-2.txt"})
    public void testInvalidFormat(String file) {
        assertThrows(
            new ParseException("", 0).getClass(),
            () -> {
                var graph = getInstance();
                graph.LoadFromFile(file, "sample");
            }
        );
    }

    @Test
    @DisplayName("Test dijkstra from the problem statement")
    public void testDijkstra() throws ParseException, FileNotFoundException {
        Graph<Double, ?> graph = getInstance();
        graph.LoadFromFile("dijkstra_test.txt", 0.0);

        var actual = graph.calculateDistances(new VertexIndex(2)).toArray();
        var expected = new DistanceData[7];
        expected[0] = new DistanceData(new VertexIndex(2), 0.f);
        expected[1] = new DistanceData(new VertexIndex(3), 2.f);
        expected[2] = new DistanceData(new VertexIndex(4), 4.f);
        expected[3] = new DistanceData(new VertexIndex(5), 5.f);
        expected[4] = new DistanceData(new VertexIndex(6), 9.f);
        expected[5] = new DistanceData(new VertexIndex(1), 10.f);
        expected[6] = new DistanceData(new VertexIndex(0), 14.f);
        assertTrue(Arrays.equals(expected, actual));
    }
    
    @Test
    @DisplayName("Test dijkstra with zero edge")
    public void testDijkstraZeroEdge() throws ParseException, FileNotFoundException {
        Graph<Double, ?> graph = getInstance();
        graph.LoadFromFile("dijkstra_test2.txt", 0.0);

        var actual = graph.calculateDistances(new VertexIndex(3)).toArray();
        var expected = new DistanceData[4];
        expected[0] = new DistanceData(new VertexIndex(3), 0.f);
        expected[1] = new DistanceData(new VertexIndex(0), 0.f);
        expected[2] = new DistanceData(new VertexIndex(1), 1.f);
        expected[3] = new DistanceData(new VertexIndex(2), 2.f);
        assertTrue(Arrays.equals(expected, actual));
    }

    @ParameterizedTest
    @DisplayName("Equals reflexivity")
    @ValueSource(strings = {"test_graph.txt", "dijkstra_test.txt", "dijkstra_test2.txt"})
    public void testEqualsReflexivity(String file) throws ParseException, FileNotFoundException {
        var graph = getInstance();
        graph.LoadFromFile(file, 8);
        assertEquals(graph, graph);
    }

    @ParameterizedTest
    @DisplayName("Equals value check")
    @ValueSource(strings = {"test_graph.txt", "dijkstra_test.txt", "dijkstra_test2.txt"})
    public void testEqualsValueCheck(String file) throws ParseException, FileNotFoundException {
        var graph1 = getInstance();
        graph1.LoadFromFile(file, 8);

        var graph2 = getInstance();
        graph2.LoadFromFile(file, 9);
        assertNotEquals(graph1, graph2);
    }

    @ParameterizedTest
    @DisplayName("Isomorfic equals")
    @CsvSource(
        value = {
            "iso-1-1.txt:iso-1-2.txt:true",
            "iso-2-1.txt:iso-2-2.txt:false",
            "iso-3-1.txt:iso-3-2.txt:false"
        }, 
        delimiter = ':'
    )
    public void testIsomorficGraph(String one, String two, boolean shouldEqual) throws ParseException, FileNotFoundException {
        var graph = getInstance();
        graph.LoadFromFile(one, 1.0);

        var graph2 = getInstance();
        graph2.LoadFromFile(two, 1.0);
        assertEquals(shouldEqual, graph.equals(graph2));
    }

    @ParameterizedTest
    @DisplayName("Hashcode contract")
    @CsvSource(
        value = {
            "test_graph.txt:test_graph.txt:true",
            "iso-1-1.txt:iso-1-2.txt:true",
            "iso-2-1.txt:iso-2-2.txt:false",
            "iso-3-1.txt:iso-3-2.txt:false"
        }, 
        delimiter = ':'
    )
    public void testHashCodeReflexivity(String file1, String file2, boolean equal) throws ParseException, FileNotFoundException {
        var graph1 = getInstance();
        graph1.LoadFromFile(file1, 8);

        var graph2 = getInstance();
        graph2.LoadFromFile(file2, 8);

        if (equal) {
            assertEquals(graph1.hashCode(), graph2.hashCode());
        }
    }
}
