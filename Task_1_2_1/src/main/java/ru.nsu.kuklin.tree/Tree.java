package ru.nsu.kuklin.tree;

import java.util.*;

/**
 *
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
        this.modified = false;
        this.isBFS = true;
    }

    /**
     * Get current value.
     *
     * @returns value
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
        modified = true;
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
        modified = true;
        return child;
    }

    /**
     * Add a given child.
     * Returns Optional.empty()
     *
     * @param child 
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
        modified = true;
        return Optional.of(child);
    }

    /**
     * Remove all children.
     */
    public void clearChildren() {
        modified = children.isEmpty();
        children.clear();
    }

    /**
     * Remove first occurence of child. Compares are deep
     *
     * @param child Child to remove
     * @return whether the child was present
     */
    public boolean removeChild(Tree<T> child) {
        boolean res = children.remove(child);
        modified = res;
        return res;
    }

    /**
     * Remove all children with given value.
     *
     * @param value Value to delete children with
     * @return whether the any child was present
     */
    public boolean removeChildren(T value) {
        boolean res = children.removeIf(t -> t.value == value);
        modified = res;
        return res;
    }

    /**
     * Set iteration order.
     *
     * @param value Whether to iterate in bfs
     */
    public void setBFSIteration(boolean value) {
        isBFS = value;
    }

    /**
     * Iterator. 
     *
     * @return iterator over a collection
     */
    public Iterator<T> iterator() {
        reset();
        if (isBFS) {
            return new BFSIterator<T>(this);
        } else {
            return new DFSIterator<T>(this);
        }
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
        Tree otherTree = (Tree<?>) other;
        return this.value == otherTree.value && this.children.equals(otherTree.children);
    }

    @Override
    public int hashCode() {
        var values = new int[] {value.hashCode(), children.hashCode()};
        return Arrays.hashCode(values);
    }

    private boolean modified() {
        if (modified) {
            return true;
        }
        for (var child : children) {
            if (child.modified()) {
                return true;
            }
        }
        return false;
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

    private void reset() {
        modified = false;
        for (var child : children) {
            child.reset();
        }
    }

    private class BFSIterator<T> implements Iterator<T> {
        public BFSIterator(Tree<T> tree) {
            root = tree;
            searchQueue = new ArrayDeque<>(2);
            searchQueue.add(tree); 
        }

        public boolean hasNext() {
            if (root.modified()) {
                throw new ConcurrentModificationException();
            }
            return !searchQueue.isEmpty();
        }

        public T next() {
            if (root.modified()) {
                throw new ConcurrentModificationException();
            }
            // Exception 
            assert !searchQueue.isEmpty();
            var current = searchQueue.remove();
            for (int i = 0; i < current.children.size(); i++) {
                searchQueue.add(current.children.get(i));
            }
            return current.value();
        }

        private Tree<T> root;
        private ArrayDeque<Tree<T>> searchQueue;
    }

    private class DFSIterator<T> implements Iterator<T> {
        public DFSIterator(Tree<T> tree) {
            root = tree;
            searchStack = new ArrayList<>(2);
            searchStack.add(new SearchState<>(tree)); 
        }

        public boolean hasNext() {
            if (root.modified()) {
                throw new ConcurrentModificationException();
            }
            return !searchStack.isEmpty();
        }

        public T next() {
            if (root.modified()) {
                throw new ConcurrentModificationException();
            }
            assert(!searchStack.isEmpty());
            var state = searchStack.get(searchStack.size() - 1);
            if (state.CurrentChild < state.Node.children.size()) {
                searchStack.add(
                    new SearchState<>(state.Node.children.get(state.CurrentChild))
                );
                return next();
            }
            var res = searchStack.remove(searchStack.size() - 1).Node.value();
            if (!searchStack.isEmpty()) {
                searchStack.get(searchStack.size() - 1).CurrentChild += 1;
            }
            return res;
        }

        private Tree<T> root;
        private ArrayList<SearchState<T>> searchStack;
        private class SearchState<T> {
            public Tree<T> Node;
            public int CurrentChild;
            
            public SearchState(Tree<T> node) {
                this.Node = node;
                CurrentChild = 0;
            }
        }

    }

    private T value;
    private List<Tree<T>> children;
    private boolean modified;
    private boolean isBFS;
}
