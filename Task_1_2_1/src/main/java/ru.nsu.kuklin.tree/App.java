
package ru.nsu.kuklin.tree;

/**
 * Stub description. FUN.
 */
public class App {
    /**
     * Stub description. FUN.
     * @param args Stub. FUN.
     */
    public static void main(String[] args) {
        System.out.println("Hello world");
        System.out.flush();
        var root = new Tree<>("Root");
        var child = root.addChild("Child");
        root.addChild("Child2");
        child.addChild("GrandChild1"); 
        child.addChild("GrandChild2"); 
        for (String value : root) {
            System.out.println(value);
        }
    }
}
