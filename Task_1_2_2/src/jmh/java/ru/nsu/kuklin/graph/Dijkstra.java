package ru.nsu.kuklin.graph;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Benchmarks of dijkstra.
 */
@State(Scope.Benchmark)
public class Dijkstra {
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void adjacencyMatrixDijkstra(Blackhole bh) throws Exception {
        Graph<Double, ?> graph = new AdjacencyMatrixGraph<>();
        graph.LoadFromFile("dijkstra_big.txt", 0.0);

        bh.consume(graph.calculateDistances(new VertexIndex(2)).toArray());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void adjacencyListsDijkstra(Blackhole bh) throws Exception {
        Graph<Double, ?> graph = new AdjacencyListGraph<>();
        graph.LoadFromFile("dijkstra_big.txt", 0.0);

        bh.consume(graph.calculateDistances(new VertexIndex(2)).toArray());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void incidenceMatrixDijkstra(Blackhole bh) throws Exception {
        Graph<Double, ?> graph = new IncidenceMatrixGraph<>();
        graph.LoadFromFile("dijkstra_big.txt", 0.0);

        bh.consume(graph.calculateDistances(new VertexIndex(2)).toArray());
    }
}
    
