package src;
/*
* Alexander Gonzalez Ramirez
*/

import java.io.*;

class Node {
    String data; // holds the key
    Node parent; // pointer to the parent
    Node left; // pointer to left child
    Node right; // pointer to right child
    int color; // 1 . Red, 0 . Black
    int count;
}

public class RedBlackTree {
    Node root;
    Node TNULL;
    final int RED = 1;
    final int BLACK = 0;
    int nodeCount = 0;

    private void postOrder(Node node) {
        if (node == TNULL) return;
        postOrder(node.left);
        postOrder(node.right);
        nodeCount++;
    }

    // Search the tree
    private Node searchTreeHelper(Node node, String key) {
        if (node == TNULL || key.equals(node.data)) {
            return node;
        }

        if (key.compareTo(node.data) < 0) {
            return searchTreeHelper(node.left, key);
        }
        return searchTreeHelper(node.right, key);
    }

    // fix the red-black tree
    private void fixInsert(Node k){
        Node u;
        while (k.parent.color == 1) {
            if (k.parent == k.parent.parent.right) {
                u = k.parent.parent.left; // uncle
                if (u.color == 1) {
                    u.color = 0;
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.left) {
                        k = k.parent;
                        rightRotate(k);
                    }
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    leftRotate(k.parent.parent);
                }
            } else {
                u = k.parent.parent.right; // uncle

                if (u.color == 1) {
                    u.color = 0;
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    k = k.parent.parent; // move x's grandparent up
                } else {
                    if (k == k.parent.right) {
                        k = k.parent;
                        leftRotate(k);
                    }
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    rightRotate(k.parent.parent);
                }
            }
            if (k == root) {
                break;
            }
        }
        root.color = 0;
    }

    private void printHelper(Node root, String indent, boolean last, int currentRow, int maxRow) {
        File rbtree = new File("rbtree.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rbtree))) {
            writer.write("Alexander Gonzalez Ramirez\n");

            // print the tree structure on the screen
            if (root != TNULL && currentRow < maxRow) {
                writer.write(indent);
                if (last) {
                    writer.write("R----");
                    indent = indent + "     ";
                } else {
                    writer.write("L----");
                    indent = indent + "|    ";
                }

                String sColor = root.color == 1?"RED":"BLACK";
                writer.write(root.data + "(" + sColor + ")\n");

                printHelper(root.left, indent, false, currentRow + 1, maxRow);
                printHelper(root.right, indent, true, currentRow + 1, maxRow);
            }
            writer.close();
        }
        catch(IllegalArgumentException | IOException e){
                System.out.println(e.getMessage());
        }


    }

    public RedBlackTree() {
        TNULL = new Node();
        TNULL.color = BLACK;
        TNULL.left = null;
        TNULL.right = null;
        root = TNULL;
    }

    // rotate left at node x
    public void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != TNULL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    // rotate right at node x
    public void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != TNULL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    // insert the key to the tree in its appropriate position
    // and fix the tree
    public void insert(String key) {
        Node existingNode = searchTreeHelper(root, key);

        if (existingNode != TNULL) {
            existingNode.count++;
            return;
        }

        // Ordinary Binary Search Insertion
        Node node = new Node();
        node.count = 1;
        node.parent = null;
        node.data = key;
        node.left = TNULL;
        node.right = TNULL;
        node.color = RED; // new node must be red

        Node y = null;
        Node x = this.root;

        while (x != TNULL) {
            int j = node.data.compareTo(x.data);
            y = x;
            if (j < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        // y is parent of x
        node.parent = y;
        if (y == null) {
            root = node;
        } else if (y != TNULL) {
            int k = node.data.compareTo(y.data);
            if (k < 0) {
                y.left = node;
            } else {
                y.right = node;
            }
        }

        // if new node is a root node, simply return
        if (node.parent == null){
            node.color = BLACK;
            return;
        }

        // if the grandparent is null, simply return
        if (node.parent.parent == null) {
            return;
        }

        // Fix the tree
        fixInsert(node);
    }

    public Node getNode(String key) {
        return searchTreeHelper(this.root, key);
    }

    public boolean searchTree(String key) {
        Node result = searchTreeHelper(this.root, key);
        return result != TNULL;
    }

    // print the tree structure on the screen
    public void prettyPrint() {
        printHelper(this.root, "", true, 0, 3);
    }

    private int treeDepth(Node node) {
        if (node == TNULL) {
            return 0;
        } else {
            int leftHeight = treeDepth(node.left);
            int rightHeight = treeDepth(node.right);

            // Count only the black nodes
            int currentNodeHeight = (node.color == 0) ? 1 : 0;

            return Math.max(leftHeight, rightHeight) + currentNodeHeight;
        }
    }

    public int getTreeDepth(){return treeDepth(root);}

    public int getNodeTotal(){
        postOrder(root);
        return nodeCount;
    }
}
