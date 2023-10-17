package ru.nsu.kuklin.tree;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import org.junit.jupiter.api.Test;

class AppTest {
    @Test
    void testCycleAdd() {
        var root = new Tree<>("Root");
        var child = root.addChild("Child");
        assertFalse(child.addChild(root).isPresent());
    }

    @Test
    void testShareGrandChildAdd() {
        var root = new Tree<>("Root");
        var child = new Tree<>("Child");
        var grandchild = new Tree<>("Grandchild");
        assertTrue(child.addChild(grandchild).isPresent());
        assertTrue(root.addChild(child).isPresent());
        assertFalse(root.addChild(grandchild).isPresent());
    }

    @Test
    void testRemoveBasic() {
        var root = new Tree<>("Root");
        var child = root.addChild("Child");
        assertTrue(root.removeChild(child));
        assertEquals(0, root.childrenCount());
    }

    @Test
    void testRemoveNonExistent() {
        var root = new Tree<>("Root");
        var child = new Tree<>("Child");
        root.addChild("Child");
        root.addChild("Not a child");
        root.addChild(child);
        assertEquals(3, root.childrenCount());
        assertTrue(root.removeChildren("Child"));
        assertEquals(1, root.childrenCount());
    }

    @Test
    void testRemoveMultiple() {
        var root = new Tree<>("Root");
        var child = new Tree<>("Child");
        assertFalse(root.removeChild(child));
        assertEquals(0, root.childrenCount());
    }

    @Test
    void testBfsIteration() {
        var root = new Tree<>("Root");
        var child = root.addChild("Child");
        root.addChild("Child2");
        child.addChild("GrandChild1"); 
        child.addChild("GrandChild2"); 

        var actual = new ArrayList<String>();
        for (var value : root) {
            actual.add(value);
        }
        var expected = new String[] {"Root", "Child", "Child2", "GrandChild1", "GrandChild2"};
        assertArrayEquals(expected, actual.toArray());
    }

    @Test
    void testBfsConcurrentModification() {
        var root = new Tree<>("Root");
        var child = root.addChild("Child");
        root.addChild("Child2");
        child.addChild("GrandChild1"); 
        child.addChild("GrandChild2"); 

        assertThrows(
            new ConcurrentModificationException().getClass(),
            () -> {
                var actual = new ArrayList<String>();
                for (var value : root) {
                    root.removeChildren(value);
                }
            }
        );
    }

    @Test
    void testBfsDoubleIteration() {
        var root = new Tree<>("Root");
        var child = root.addChild("Child");
        root.addChild("Child2");
        child.addChild("GrandChild1"); 
        child.addChild("GrandChild2"); 

        assertThrows(
            new ConcurrentModificationException().getClass(),
            () -> {
                for (var value : root) {
                    root.removeChildren(value);
                    for (var var : root) {}
                }
            }
        );
    }

    @Test
    void testDfsIteration() {
        var root = new Tree<>("Root");
        var child = root.addChild("Child");
        root.addChild("Child2");
        child.addChild("GrandChild1"); 
        child.addChild("GrandChild2"); 

        var actual = new ArrayList<String>();
        root.setIterationOrder(Tree.IterationOrder.Dfs);
        for (var value : root) {
            actual.add(value);
        }
        var expected = new String[] {"GrandChild1", "GrandChild2", "Child", "Child2", "Root"};
        assertArrayEquals(expected, actual.toArray());
    }

    @Test
    void testDfsConcurrentModification() {
        var root = new Tree<>("Root");
        var child = root.addChild("Child");
        root.addChild("Child2");
        child.addChild("GrandChild1"); 
        child.addChild("GrandChild2"); 

        assertThrows(
            new ConcurrentModificationException().getClass(),
            () -> {
                root.setIterationOrder(Tree.IterationOrder.Dfs);
                for (var value : root) {
                    root.removeChildren(value);
                }
            }
        );
    }

    @Test
    void testDfsDoubleIteration() {
        var root = new Tree<>("Root");
        var child = root.addChild("Child");
        root.addChild("Child2");
        child.addChild("GrandChild1"); 
        child.addChild("GrandChild2"); 

        root.setIterationOrder(Tree.IterationOrder.Dfs);
        assertThrows(
            new ConcurrentModificationException().getClass(),
            () -> {
                for (var value : root) {
                    root.removeChildren(value);
                    for (var another : root) {
                    }
                }
            }
        );
    }

    @Test
    void testEquality() {
        int size = 10000;
        var original = new Tree<>(0);
        var diffRoot = new Tree<>(20);
        var diffChildren = new Tree<>(0);
        Random rng = new Random();
        for (int i = 0; i < size; i++) {
            original.addChild(rng.nextInt()); 
            diffRoot.addChild(rng.nextInt()); 
            if (i % 2 == 0) {
                diffChildren.addChild(rng.nextInt()); 
            }
        }
        assertNotEquals(original, diffRoot); 
        assertNotEquals(original, diffChildren);
    } 

    @Test
    void testEqualityUnorderedness() {
        var left = new Tree<>(0);
        var right = new Tree<>(0);
        left.addChild(10);
        left.addChild(20);
        right.addChild(20);
        right.addChild(10);
        assertEquals(left, right);
    } 

    @Test
    void testEqualityDifferentStructure() {
        var left = new Tree<>(0);
        left.addChild(10);
        var lchild2 = left.addChild(20);
        lchild2.addChild(1);
        lchild2.addChild(2);

        var right = new Tree<>(0);
        var rchild1 = right.addChild(10);
        right.addChild(20);

        rchild1.addChild(1);
        rchild1.addChild(2);
        assertNotEquals(left, right);
    } 

    @Test
    void testEqualityMultipleChildren() {
        var left = new Tree<>(0);
        left.addChild(10);
        left.addChild(10);
        left.addChild(20);

        var right = new Tree<>(0);
        right.addChild(20);
        right.addChild(10);
        right.addChild(20);

        assertNotEquals(left, right);
    } 

    @Test
    void testEqualityReflexivity() {
        int size = 10000;
        var tree = new Tree<>(0);
        Random rng = new Random();
        for (int i = 0; i < size; i++) {
            tree.addChild(rng.nextInt()); 
        }
        assertEquals(tree, tree); 
    } 

    @Test 
    void testEqualityTransitivity() { 
        // This IS a really dumb test but that's the smartest I've got 
        // that tests the property for real and not just three equal trees
        int size = 3;
        int maxNum = 5;
        int positiveTestCount = 1;
        var t1 = new Tree<>(0);
        var t2 = new Tree<>(0);
        var t3 = new Tree<>(0);
        Random rng = new Random();

        for (int j = 0; j < positiveTestCount;) {
            t1.clearChildren();
            t2.clearChildren();
            t3.clearChildren();
            for (int i = 0; i < size; i++) {
                t1.addChild(rng.nextInt() % maxNum);
                t2.addChild(rng.nextInt() % maxNum);
                t3.addChild(rng.nextInt() % maxNum);
            }
            assertEquals(t1.childrenCount(), size);
            assertEquals(t2.childrenCount(), size);
            assertEquals(t3.childrenCount(), size);

            // (t1 == t2 && t2 == t3) -> t1 == t3
            assertTrue(!t1.equals(t2) || !t2.equals(t3) || t1.equals(t3));
            if (t1.equals(t2) && t2.equals(t3)) {
                j++;
            }
        }
    }

    @Test
    void testEqualitySymmetry() {
        int size = 10000;
        var t1 = new Tree<>(0);
        var t2 = new Tree<>(0);
        Random rng = new Random();

        for (int i = 0; i < size; i++) {
            t1.addChild(rng.nextInt());
            t2.addChild(rng.nextInt());
        }

        assertEquals(t1.equals(t2), t2.equals(t1));
    }

    @Test
    void testEqualityHashcode() {
        int size = 10000;
        var t1 = new Tree<>(0);
        var t2 = new Tree<>(0);
        Random rng = new Random();

        for (int j = 0; j < 1000; j++) {
            t1.clearChildren();
            t2.clearChildren();
            for (int i = 0; i < size; i++) {
                t1.addChild(rng.nextInt());
                t2.addChild(rng.nextInt());
            }

            // (t1.hashcode() != t2.hashcode()) -> t1 != t2
            assertTrue(t1.hashCode() == t2.hashCode() || !t2.equals(t1));
        }
    }

    @Test
    void testEqualitySaneness() {
        int size = 10000;
        var t1 = new Tree<>(0);
        Random rng = new Random();

        for (int i = 0; i < size; i++) {
            t1.addChild(rng.nextInt());
        }

        assertNotEquals(t1, rng);
        assertNotEquals(t1, size);
        assertNotEquals(t1, null);
    }
}
