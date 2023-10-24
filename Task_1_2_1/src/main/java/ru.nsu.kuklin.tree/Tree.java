package ru.nsu.kuklin.tree;

import java.util.*;

/**
 * Generic tree implementation. 
 * Supports iteration through bfs and dfs,
 * children manipulation and etc.
 */
public class Tree<T> implements Iterable<T> {
    /**
     * Create a node with value and no children.
     *
     * @param value Value to hold in node
     */
    public Tree(T value) {
        this.value = value;
        this.children = new ArrayList<>();
        this.order = IterationOrder.Bfs;
    }

    /**
     * Get current value.
     *
     * @return value
     */
    public T value() {
        return value;
    }
    
    /**
     * Set current value.
     *
     * @param value Value to set
     */
    public void setValue(T value) {
        this.value = value; 
    }

    /**
     * Get current children count.
     *
     * @return children count
     */
    public int childrenCount() {
        return children.size();
    }

    /**
     * Add a child with given value.
     *
     * @param value Value to give a new child
     * @return new child
     */
    public Tree<T> addChild(T value) {
        var child = new Tree<>(value);
        children.add(child);
        return child;
    }

    /**
     * Add a given child.
     * Returns Optional.empty()
     *
     * @param child child to add
     * @return new child, if addition does not cause cycles
     */
    public Optional<Tree<T>> addChild(Tree<T> child) {
        children.add(child); 
        
        var colors = new IdentityHashMap<Tree<T>, NodeColor>(); 
        boolean cycle = hasCycles(colors);
        if (cycle) {
            children.remove(children.size() - 1);
            return Optional.empty();
        }
        return Optional.of(child);
    }

    /**
     * Remove all children.
     */
    public void clearChildren() {
        children.clear();
    }

    /**
     * Remove first occurence of child. Compares are deep
     *
     * @param child Child to remove
     * @return whether the child was present
     */
    public boolean removeChild(Tree<T> child) {
        return children.remove(child);
    }

    /**
     * Remove all children with given value.
     *
     * @param value Value to delete children with
     * @return whether the any child was present
     */
    public boolean removeChildren(T value) {
        return children.removeIf(t -> t.value == value);
    }

    /**
     * Iteration order for tree traversal.
     * Dfs is on exit order.
     */
    public enum IterationOrder {
        Bfs,
        Dfs
    }

    /**
     * Set iteration order.
     *
     * @param order Whether to iterate in bfs
     */
    public void setIterationOrder(IterationOrder order) {
        this.order = order;
    }

    /**
     * Iterator.
     * This is fail-fast iterator. HOWEVER. 
     * It's possible that it would not detect a change because
     * it uses hashcodes to record state. Which can collide, hence not throwing.
     * It is a design decision that allows us to achieve 
     * 1. O(1) vs O(parent count) state change
     * 2. 0bytes and awareness of parents vs 8bytes (pbbly) for parent link
     * 3. Same tree in multiple hierarchies without additional nonsense 
     * or
     * 1. O(1) vs O(tree size) state snapshot memory footprint
     *
     * @return iterator over a collection
     */
    public Iterator<T> iterator() {
        return switch (order) {
            case Bfs -> new BfsIterator<T>(this);
            case Dfs -> new DfsIterator<T>(this);
        };
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (this.getClass() != other.getClass()) {
            return false;
        }
        var otherTree = (Tree<?>) other;
        if (this.value != otherTree.value 
                || this.childrenCount() != otherTree.childrenCount()
        ) {
            return false;
        }
        var childrenMap = new HashMap<Object, Integer>();
        for (var child : this.children) {
            childrenMap.putIfAbsent(child, 0);
            childrenMap.compute(child, (k, ovalue) -> ovalue + 1);
        }
        for (Object child : otherTree.children) {
            if (!childrenMap.containsKey(child)) {
                return false;
            }
            childrenMap.compute(child, (k, ovalue) -> ovalue - 1);
        }
        return childrenMap.values().stream().allMatch(v -> v == 0);
    }

    @Override
    public int hashCode() {
        var values = new int[] {value.hashCode(), new HashSet<>(children).hashCode()};
        return Arrays.hashCode(values);
    }

    private enum NodeColor {
        Visiting,
        Visited,
    }

    private boolean hasCycles(IdentityHashMap<Tree<T>, NodeColor> state) {
        state.put(this, NodeColor.Visiting);
        for (var child : children) {
            if (state.get(child) != null) {
                return true;
            }
            if (child.hasCycles(state)) {
                return true;
            }
        }
        state.put(this, NodeColor.Visited);
        return false;
    }

    private class BfsIterator<T> implements Iterator<T> {
        public BfsIterator(Tree<T> tree) {
            root = tree;
            initHash = root.hashCode();
            searchQueue = new ArrayDeque<>(2);
            searchQueue.add(tree); 
        }

        public boolean hasNext() {
            if (root.hashCode() != initHash) {
                throw new ConcurrentModificationException();
            }
            return !searchQueue.isEmpty();
        }

        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            var current = searchQueue.remove();
            for (int i = 0; i < current.children.size(); i++) {
                searchQueue.add(current.children.get(i));
            }
            return current.value();
        }

        private Tree<T> root;
        private int initHash;
        private ArrayDeque<Tree<T>> searchQueue;
    }

    private class DfsIterator<T> implements Iterator<T> {
        public DfsIterator(Tree<T> tree) {
            initHash = Tree.this.hashCode();
            searchStack = new ArrayList<>(2);
            searchStack.add(new SearchState<>(tree)); 
        }

        public boolean hasNext() {
            if (Tree.this.hashCode() != initHash) {
                throw new ConcurrentModificationException();
            }
            return !searchStack.isEmpty();
        }

        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            var state = searchStack.get(searchStack.size() - 1);
            if (state.currentChild < state.node.children.size()) {
                searchStack.add(
                    new SearchState<>(state.node.children.get(state.currentChild))
                );
                return next();
            }
            var res = searchStack.remove(searchStack.size() - 1).node.value();
            if (!searchStack.isEmpty()) {
                searchStack.get(searchStack.size() - 1).currentChild += 1;
            }
            return res;
        }

        private static class SearchState<T> {
            public Tree<T> node;
            public int currentChild;
            
            public SearchState(Tree<T> node) {
                this.node = node;
                currentChild = 0;
            }
        }

        private Tree<T> root;
        private int initHash;
        private ArrayList<SearchState<T>> searchStack;
    }

    private T value;
    private List<Tree<T>> children;
    private IterationOrder order;
}
