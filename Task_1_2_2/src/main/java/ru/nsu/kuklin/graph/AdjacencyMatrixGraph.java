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
  *  Implementation of Graph interface using adjacency matrix
 */ 
public class AdjacencyMatrixGraph<V> extends Graph<V, Edge<VertexIndex>> {
    /** 
     *
     * Adjacency graph
     *
     * @return public
     */
    public AdjacencyMatrixGraph() { 
        modCount = 0;
        adjecencyMatrix = new ArrayList<>();
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
        indexToDense.set(id, adjecencyMatrix.size());
        denseToIndex.add(id);
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
        adjecencyMatrix.remove(deleted);
        denseToIndex.remove(deleted);
        for (var row : adjecencyMatrix) {
            row.remove(deleted);
        }
        for (int i = 0; i < indexToDense.size(); i++) {
            if (indexToDense.get(i) > deleted) {
                indexToDense.set(i, indexToDense.get(i)-1);
            }
        }
        return vertices.set(id.id(), Optional.empty());
    }

    @Override
    public int getVertexCount() {
        return vertices.size();
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
        return adjecencyMatrix.get(from.id()).get(to.id()).isPresent();
    }

    @Override
    public Optional<Edge<VertexIndex>> setEdge(VertexIndex from, VertexIndex to, float weight) { 
        if (!exists(from) || !exists(to)) {
            return Optional.empty();
        }
        modCount += 1;
        return adjecencyMatrix
            .get(getDenseIndex(from).get())
            .set(getDenseIndex(to).get(), Optional.of(weight))
            .map(w -> new Edge<>(from, to, w));
    }

    @Override
    public Optional<Edge<VertexIndex>> removeEdge(VertexIndex from, VertexIndex to) { 
        if (!exists(from) || !exists(to)) {
            return Optional.empty();
        }
        modCount += 1;
        return adjecencyMatrix
            .get(getDenseIndex(from).get())
            .set(getDenseIndex(to).get(), Optional.empty())
            .map(w -> new Edge<>(from, to, w));
    }

    @Override
    public Optional<Edge<VertexIndex>> getEdge(VertexIndex from, VertexIndex to) { 
        if (!exists(from) || !exists(to)) {
            return Optional.empty();
        }
        return adjecencyMatrix
            .get(getDenseIndex(from).get())
            .get(getDenseIndex(to).get())
            .map(w -> new Edge<>(from, to, w));
    }

    @Override
    public void clear() { 
        vertices.clear();
        adjecencyMatrix.clear();
        modCount += 1;
    }

    @Override
    public int getOutNeighborCount(VertexIndex id) {
        int from = getDenseIndex(id).get();
        int neighborCnt = 0;
        for (int i = 0; i < getVertexCount(); i++) {
            if (adjecencyMatrix.get(from).get(i).isPresent()) {
                neighborCnt++;
            }
        }
        return neighborCnt;
    }

    @Override
    public int getInNeighborCount(VertexIndex id) {
        int to = getDenseIndex(id).get();
        int neighborCnt = 0;
        for (int i = 0; i < getVertexCount(); i++) {
            if (adjecencyMatrix.get(i).get(to).isPresent()) {
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
            this.from = from.id();
            modStart = modCount;
        }

        @Override
        public boolean hasNext() {
            while (next < vertices.size() && !adjecencyMatrix.get(from).get(next).isPresent()) {
               next++;
            }
            return next < vertices.size();
        }

        @Override
        public VertexIndex next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (modStart != modCount) {
                throw new ConcurrentModificationException();
            }
            var res = fromDenseIndex(next).get();
            next++;
            return res;
        }
        private int modStart;
        private int next;
        private int from;
    }

    private class InNeighborIterator implements Iterator<VertexIndex> {
        public InNeighborIterator(VertexIndex from) {
            next = 0;
            this.from = from.id();
            modStart = modCount;
        }

        @Override
        public boolean hasNext() {
            while (next < vertices.size() && !adjecencyMatrix.get(next).get(from).isPresent()) {
               next++;
            }
            return next < vertices.size();
        }

        @Override
        public VertexIndex next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (modStart != modCount) {
                throw new ConcurrentModificationException();
            }
            var res = fromDenseIndex(next).get();
            next++;
            return res;
        }

        private int modStart;
        private int next;
        private int from;
    }

    private boolean exists(VertexIndex id) { 
        return id.id() < vertices.size() && vertices.get(id.id()).isPresent();
    }

    private ArrayList<ArrayList<Optional<Float>>> adjecencyMatrix;
    private ArrayList<Integer> indexToDense;
    private ArrayList<Integer> denseToIndex;
    private ArrayList<Optional<V>> vertices;
    private int modCount;
}
