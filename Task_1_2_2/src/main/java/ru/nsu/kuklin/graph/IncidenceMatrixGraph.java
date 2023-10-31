package ru.nsu.kuklin.graph;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;


/**
 *  Implemetation of Graph interface using incidence matrix
 */ 
public class IncidenceMatrixGraph<V> extends Graph<V, Edge<VertexIndex>> {
    /** 
     * Constructor.
     */
    public IncidenceMatrixGraph() { 
        modCount = 0;
        incidenceMatrix = new ArrayList<>();
        vertices = new ArrayList<>();
        indexToDense = new ArrayList<>();
        denseToIndex = new ArrayList<>();
    }

    @Override
    public VertexIndex addVertex(V vertex) { 
        modCount += 1;
        int id = vertices.indexOf(Optional.empty());
        if (id == -1) {
            id = vertices.size();
            vertices.add(Optional.empty());
            indexToDense.add(0);
        }
        vertices.set(id, Optional.of(vertex));
        indexToDense.set(id, getVertexCount());
        denseToIndex.add(id);

        var edgeCount = incidenceMatrix.size() > 0 ? incidenceMatrix.get(0).size() : 0;
        var newRow = new ArrayList<Optional<Edge<VertexIndex>>>(edgeCount);
        for (int i = 0; i < edgeCount; i++) {
            newRow.add(Optional.empty());
        }
        incidenceMatrix.add(newRow);
        return new VertexIndex(id);
    }


    @Override
    public Optional<V> setVertex(VertexIndex id, V value) { 
        if (!exists(id)) {
            return Optional.empty();
        }
        modCount += 1;
        return vertices.set(id.id(), Optional.of(value));
    }

    @Override
    public Optional<V> getVertex(VertexIndex id) { 
        if (!exists(id)) {
            return Optional.empty();
        }
        return vertices.get(id.id());
    }

    @Override
    public Optional<V> removeVertex(VertexIndex id) { 
        if (!exists(id)) {
            return Optional.empty();
        }
        modCount += 1;
        int deleted = getDenseIndex(id).get();
        var edges = incidenceMatrix.get(deleted);
        for (int i = 0; i < edges.size(); i++) {
            var edge = edges.get(i);
            if (edge.isPresent()) {
                removeEdge(edge.get().from(), edge.get().to());
            }
        }
        incidenceMatrix.remove(deleted);
        denseToIndex.remove(deleted);
        for (int i = 0; i < indexToDense.size(); i++) {
            if (indexToDense.get(i) > deleted) {
                indexToDense.set(i, indexToDense.get(i)-1);
            }
        }
        return vertices.set(id.id(), Optional.empty());
    }

    @Override
    public int getVertexCount() {
        return incidenceMatrix.size();
    }

    @Override
    public Optional<Integer> getDenseIndex(VertexIndex id) {
        if (!exists(id)) {
            return Optional.empty();
        }
        return Optional.of(indexToDense.get(id.id()));
    }

    @Override
    public Optional<VertexIndex> fromDenseIndex(int id) {
        if (id >= getVertexCount() || id < 0) {
            return Optional.empty();
        }
        return Optional.of(new VertexIndex(denseToIndex.get(id)));
    }

    @Override
    public boolean hasEdge(VertexIndex from, VertexIndex to) { 
        if (!exists(from) || !exists(to)) {
            return false;
        }
        return findEdge(from, to).isPresent();
    }

    @Override
    public Optional<Edge<VertexIndex>> setEdge(VertexIndex from, VertexIndex to, float weight) {
        if (!exists(from) || !exists(to)) {
            return Optional.empty();
        }
        modCount += 1;
        var edge = findEdge(from, to);
        var newEdge = new Edge<>(from, to, weight);
        if (edge.isPresent()) {
            // Replace existing edge
            var list = incidenceMatrix.get(getDenseIndex(from).get());
            // Doesn't really matter which to take - they should be equal
            var previous = list.set(edge.get(), Optional.of(newEdge));

            list = incidenceMatrix.get(getDenseIndex(to).get());
            var alsoPrevious = list.set(edge.get(), Optional.of(newEdge));
            assert previous.equals(alsoPrevious) : "Edges are not in sync";

            return previous;
        }
        // Add new edge
        for (int i = 0; i < incidenceMatrix.size(); i++) {
            var edges = incidenceMatrix.get(i);
            edges.add(Optional.empty());
        }

        var edgeCount = incidenceMatrix.get(0).size();
        incidenceMatrix.get(getDenseIndex(from).get()).set(edgeCount - 1, Optional.of(newEdge));
        incidenceMatrix.get(getDenseIndex(to).get()).set(edgeCount - 1, Optional.of(newEdge));
        return Optional.empty();
    }

    @Override
    public Optional<Edge<VertexIndex>> removeEdge(VertexIndex from, VertexIndex to) { 
        if (!exists(from) || !exists(to)) {
            return Optional.empty();
        }
        modCount += 1;
        Optional<Integer> edge = findEdge(from, to);
        if (!edge.isPresent()) {
            return Optional.empty();
        }
        var res = incidenceMatrix.get(getDenseIndex(from).get()).get(edge.get());

        for (int i = 0; i < incidenceMatrix.size(); i++) {
            incidenceMatrix.get(i).remove((int)edge.get());
        }
        return res;
    }

    @Override
    public Optional<Edge<VertexIndex>> getEdge(VertexIndex from, VertexIndex to) { 
        if (!exists(from) || !exists(to)) {
            return Optional.empty();
        }
        var edge = findEdge(from, to);
        if (!edge.isPresent()) {
            return Optional.empty();
        }
        return incidenceMatrix.get(getDenseIndex(from).get()).get(edge.get());
    }

    @Override
    public void clear() { 
        vertices.clear();
        incidenceMatrix.clear();
        modCount += 1;
    }

    @Override
    public int getOutNeighborCount(VertexIndex id) {
        int neighborCnt = 0;
        var list = incidenceMatrix.get(getDenseIndex(id).get());
        for (var edge : list) {
            if (edge.isPresent() && edge.get().from().equals(id)) {
                neighborCnt++;
            }
        }
        return neighborCnt;
    }

    @Override
    public int getInNeighborCount(VertexIndex id) {
        int neighborCnt = 0;
        var list = incidenceMatrix.get(getDenseIndex(id).get());
        for (var edge : list) {
            if (edge.isPresent() && edge.get().to().equals(id)) {
                neighborCnt++;
            }
        }
        return neighborCnt;
    }

    @Override
    public Iterable<VertexIndex> getOutNeighbors(VertexIndex id) {
        return new Iterable<VertexIndex>() {
            public Iterator<VertexIndex> iterator() {
                return new OutNeighborIterator(id);
            }
        };
    }

    @Override
    public Iterable<VertexIndex> getInNeighbors(VertexIndex id) {
        return new Iterable<VertexIndex>() {
            public Iterator<VertexIndex> iterator() {
                return new InNeighborIterator(id);
            }
        };
    }

    private class OutNeighborIterator implements Iterator<VertexIndex> {
        public OutNeighborIterator(VertexIndex from) {
            next = 0;
            list = incidenceMatrix.get(getDenseIndex(from).get());
            this.from = from;
            modStart = modCount;
        }

        @Override
        public boolean hasNext() {
            while (next < list.size()) {
                var edge = list.get(next);
                if (edge.isPresent() && edge.get().from().equals(from)) {
                    break;
                }
                next++;
            }
            return next < list.size();
        }

        @Override
        public VertexIndex next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (modStart != modCount) {
                throw new ConcurrentModificationException();
            }
            var res = list.get(next).get().to();
            next++;
            return res;
        }
        private int modStart;
        private int next;
        private VertexIndex from;
        private ArrayList<Optional<Edge<VertexIndex>>> list;
    }

    private class InNeighborIterator implements Iterator<VertexIndex> {
        public InNeighborIterator(VertexIndex to) {
            next = 0;
            list = incidenceMatrix.get(getDenseIndex(to).get());
            this.to = to;
            modStart = modCount;
        }

        @Override
        public boolean hasNext() {
            while (next < list.size()) {
                var edge = list.get(next);
                if (edge.isPresent() && edge.get().to().equals(to)) {
                    break;
                }
                next++;
            }
            return next < list.size();
        }

        @Override
        public VertexIndex next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (modStart != modCount) {
                throw new ConcurrentModificationException();
            }
            var res = list.get(next).get().from();
            next++;
            return res;
        }

        private int modStart;
        private int next;
        private VertexIndex to;
        private ArrayList<Optional<Edge<VertexIndex>>> list;
    }

    private boolean exists(VertexIndex id) { 
        return id.id() < vertices.size() && vertices.get(id.id()).isPresent();
    }

    private Optional<Integer> findEdge(VertexIndex from, VertexIndex to) {
        var edges = incidenceMatrix.get(getDenseIndex(from).get());
        for (int i = 0; i < edges.size(); i++) {
            var edge = edges.get(i);
            if (!edge.isPresent()) {
                continue;
            }
            if (edge.get().from().equals(from) && edge.get().to().equals(to)) {
                return Optional.of(i); 
            }
        }
        return Optional.empty();
    }

    private ArrayList<ArrayList<Optional<Edge<VertexIndex>>>> incidenceMatrix;
    private ArrayList<Integer> indexToDense;
    private ArrayList<Integer> denseToIndex;
    private ArrayList<Optional<V>> vertices;
    private int modCount;
}
