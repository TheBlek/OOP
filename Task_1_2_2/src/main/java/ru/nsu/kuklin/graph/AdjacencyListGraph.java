package ru.nsu.kuklin.graph;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;


 /**
  * Implementation of Graph interface using adjacency lists.
  */ 
public class AdjacencyListGraph<V> extends Graph<V, Edge<VertexIndex>> {
    /** 
     * Constructor.
     */
    public AdjacencyListGraph() { 
        modCount = 0;
        adjacencyLists = new ArrayList<>();
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
        adjacencyLists.add(new ArrayList<Edge<VertexIndex>>());
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
        adjacencyLists.remove(deleted);
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
        return adjacencyLists.size();
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
        for (var edge : adjacencyLists.get(getDenseIndex(from).get())) {
            if (edge.to() == to) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<Edge<VertexIndex>> setEdge(VertexIndex from, VertexIndex to, float weight) { 
        if (!exists(from) || !exists(to)) {
            return Optional.empty();
        }
        modCount += 1;
        var list = adjacencyLists.get(getDenseIndex(from).get());
        var id = findEdge(from, to);
        if (id.isPresent()) {
            return Optional.of(list.set(id.get(), new Edge<>(from, to, weight)));
        }
        list.add(new Edge<>(from, to, weight));
        return Optional.empty();
    }

    @Override
    public Optional<Edge<VertexIndex>> removeEdge(VertexIndex from, VertexIndex to) { 
        if (!exists(from) || !exists(to)) {
            return Optional.empty();
        }
        modCount += 1;
        var list = adjacencyLists.get(getDenseIndex(from).get());
        return findEdge(from, to).map(i -> list.remove((int)i));
    }

    @Override
    public Optional<Edge<VertexIndex>> getEdge(VertexIndex from, VertexIndex to) { 
        if (!exists(from) || !exists(to)) {
            return Optional.empty();
        }
        var list = adjacencyLists.get(getDenseIndex(from).get());
        return findEdge(from, to).map(i -> list.get(i));
    }

    @Override
    public void clear() { 
        vertices.clear();
        adjacencyLists.clear();
        modCount += 1;
    }

    @Override
    public int getOutNeighborCount(VertexIndex id) {
        int from = getDenseIndex(id).get();
        return adjacencyLists.get(from).size();
    }

    @Override
    public int getInNeighborCount(VertexIndex id) {
        int neighborCnt = 0;
        for (var list : adjacencyLists) {
            if (list.indexOf(id) != -1) {
                neighborCnt++;
            }
        }
        return neighborCnt;
    }

    @Override
    public Iterable<VertexIndex> getOutNeighbors(VertexIndex id) {
        if (!exists(id)) {
            return null;
        }
        return new Iterable<VertexIndex>() {
            public Iterator<VertexIndex> iterator() {
                return new OutNeighborIterator(id);
            }
        };
    }

    @Override
    public Iterable<VertexIndex> getInNeighbors(VertexIndex id) {
        if (!exists(id)) {
            return null;
        }
        return new Iterable<VertexIndex>() {
            public Iterator<VertexIndex> iterator() {
                return new InNeighborIterator(id);
            }
        };
    }

    private class OutNeighborIterator implements Iterator<VertexIndex> {
        public OutNeighborIterator(VertexIndex from) {
            next = 0;
            list = adjacencyLists.get(getDenseIndex(from).get());
            modStart = modCount;
        }

        @Override
        public boolean hasNext() {
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
            var res = list.get(next).to();
            next++;
            return res;
        }
        private int modStart;
        private int next;
        private ArrayList<Edge<VertexIndex>> list;
    }

    private class InNeighborIterator implements Iterator<VertexIndex> {
        public InNeighborIterator(VertexIndex to) {
            next = 0;
            this.to = to;
            modStart = modCount;
        }

        @Override
        public boolean hasNext() {
            while (next < getVertexCount() && 
                    !findEdge(fromDenseIndex(next).get(), to).isPresent()) {
                next++;
            }
            return next < getVertexCount();
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
        private VertexIndex to;
    }

    private boolean exists(VertexIndex id) {
        return id.id() < vertices.size() && vertices.get(id.id()).isPresent();
    }

    private Optional<Integer> findEdge(VertexIndex from, VertexIndex to) {
        var list = adjacencyLists.get(getDenseIndex(from).get());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).to().equals(to)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    private ArrayList<ArrayList<Edge<VertexIndex>>> adjacencyLists;
    private ArrayList<Integer> indexToDense;
    private ArrayList<Integer> denseToIndex;
    private ArrayList<Optional<V>> vertices;
    private int modCount;
}
