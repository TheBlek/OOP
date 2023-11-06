package ru.nsu.kuklin.graph;

/**
 * Representation of an vertex that is distance far from another vertex.
 * Used to return information from alorithms like dijkstra.
 */
public record DistanceData(VertexIndex id, float distance) {}

