package ru.nsu.kuklin.graph;

/**
 * Representation of an Edge with given ends and a weight.
 */
public record Edge<V>(V from, V to, float weight) {}

