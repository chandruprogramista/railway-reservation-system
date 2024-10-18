package com.chand.railway_reservation_system.core.datastructure;

import com.chand.railway_reservation_system.core.entity.PNRPair;
import com.chand.railway_reservation_system.core.templates.IntervalTree;

import java.util.*;

// Store the objects in the AVL-Tree format
public class Seat<T> implements IntervalTree<T> {

    private Node<T> root;
    private Comparator<PNRPair> comparator;
    private int seatNumber;
    private int size;

    private static class Node<T> {
        Node<T> left;
        Node<T> right;
        int height;
        T value;

        @Override
        public String toString() {
            return "Node{" +
                    "height=" + height +
                    ", value=" + value +
                    '}';
        }

        public Node(T value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.height = 1;
        }
    }

    public Seat() {}

    public Seat(int seatNumber) { this.seatNumber = seatNumber; }

    public Seat(int seatNumber, T initialNode) {
        this(seatNumber, initialNode, null);
    }

    public Seat(int seatNumber, T initialNode, Comparator<PNRPair> comparator) {
        // checks it is the interleavable instance
        isInterleaveIns(initialNode);
        this.seatNumber = seatNumber;
        this.comparator = comparator;
        this.root = createNode(initialNode);
    }

    public Seat(Comparator<PNRPair> comparator) {
        this.comparator = comparator;
    }

    public boolean add(T value) {
        // if root is null
        if (this.root == null) {
            this.root = createNode(value);
            ++size;
            return true;
        }
        // pre-check is used to check if there is a space to insert the "new value"
        // if it not then there is no room, so return false
        if (!preAddCheck(this, (PNRPair)value))
            return false;

        // add the "new value"
        this.root = add(this.root, value);
        ++size;
        return true;
    }

    // return the new root if the root is modified or else normally return the this.root
    private Node<T> add(Node<T> root, T value) {

        // if root is null we reach the end so we need to create the node
        if (root == null)
            return createNode(value);

        // compare the two node (comparator : "default", value : "to be inserted", root : "current node in the tree")
        // comparison is come from the "comparator" if it not then "comparable"
        int comparedValue = compareInterleave(comparator, (PNRPair)value, (PNRPair)root.value);

        // move left
        if (comparedValue < 0)
            root.left = add(root.left, value);
            // move right
        else if (comparedValue > 0)
            root.right = add(root.right, value);
        // it is not a valid interval to insert
        // case 1 : the interval is already inserted
        // case 2 : the interval is overlapped
        // we know that the incoming node is going to be successfully insert
        // so that the else condition is not necessary
//        else
//            return root;

        // the every node's height is must be updated in back-tracking
        // update the height of the current root
        root.height = 1 + getMaxHeight(root);
        return makeRotation (root);
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    @Override
    public List<T> getAll() {

        List<T> list = new ArrayList<>();
        Stack<Node<T>> stack = new Stack<>();
        Node<T> tempRoot = this.root;

        while (true) {

            if (tempRoot != null) {
                stack.push(tempRoot);
                tempRoot = tempRoot.left;
            }

            else {
                if (stack.isEmpty())
                    break;

                Node<T> temp = stack.pop();
                list.add(temp.value);
                tempRoot = temp.right;
            }
        }

        return list;
    }

    public boolean contains(T searchObject) {
        Objects.requireNonNull(searchObject, "Search object cannot be empty");
        return contains(this.root, searchObject);
    }

    private boolean contains(Node<T> root, T searchObject) {
        while (root != null) {
            // USE OF "isEqual"
            // Because we need to find a way to equalize the two object
            // We know all the object inside the IntervalAVLTree is the child of Interleavable
            // So I use the interleavable to compare the two objects to give the positive result to caller
            if (isEqual((PNRPair)searchObject, (PNRPair)root.value))
                return true;
            int comparedValue = compareInterleave(comparator, (PNRPair)searchObject, (PNRPair)root.value);
            // go to left
            if (comparedValue < 0)
                root = root.left;
                // go right
            else if (comparedValue > 0)
                root = root.right;
            else
                break;
        }

        return false;
    }

    public boolean remove(T obj) {
        if (this.root == null) {
//            System.out.printf("seat : %s 1", this.seatNumber);
            return false;
        }
        // pre-check of the deletion
        if (!contains(obj)) {
//            System.out.printf("seat : %s 2", this.seatNumber);
            return false;
        }

        this.root = remove(this.root, obj);
        --size;
        return true;
    }

    private Node<T> remove(Node<T> root, T keyObj) {

        if (root == null)
            return null;

        int compareValue = compareInterleave(comparator, (PNRPair)keyObj, (PNRPair)root.value);

        // go left
        if (compareValue < 0)
            root.left = remove(root.left, keyObj);
            // go right
        else if (compareValue > 0)
            root.right = remove(root.right, keyObj);
            // the compareValue = 0 cause in two scenario
            // 1 -> the "keyObj" is "overlap" the current root
            // 2 -> the "keyObj" "match" the current root
        else {
            // we found the node to be deleted
            if (isEqual((PNRPair)keyObj, (PNRPair)root.value)) {

                if (root.left == null)
                    return root.right;

                if (root.right == null)
                    return root.left;

                Node<T> tempNode = root.right;
                // find the left most to replace the node that we want to delete
                while (tempNode.left != null)
                    tempNode = tempNode.left;

                root.value = tempNode.value;
                // from root.right -> we need to delete the leaf node that we replaced with the current root
                root.right = remove(root.right, tempNode.value);
            }
        }

        // from this point we need do update the height the current root
        root.height = 1 + getMaxHeight(root);
        return makeRotation (root);
    }

    @Override
    public int size() {
        return size;
    }

    // --- UTILITIES ---
    // interleave node check
    private static <T> void isInterleaveIns(T node) {
        try {
            @SuppressWarnings("unchecked")
            PNRPair dummy = (PNRPair) node;
        } catch (ClassCastException exception) {
            throw new ClassCastException("it is not a interleave node");
        }
    }

    private static <T> Node<T> createNode(T value) {
        return new Node<>(value);
    }

    private static <T> int compareInterleave(Comparator<PNRPair> comparator, PNRPair toBeInsert, PNRPair current) {
        if (comparator != null)
            return comparator.compare(toBeInsert, current);

        @SuppressWarnings("unchecked")
        Comparable<PNRPair> comparable = (Comparable<PNRPair>) toBeInsert;
        return comparable.compareTo(current);
    }

    // AVL-UTILS
    private static <T> int height(Node<T> node) {
        return node == null ? 0 : node.height;
    }

    private static <T> int balanceLevel(Node<T> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private static <T> int getMaxHeight(Node<T> node) {
        return Math.max(height(node.left), height(node.right));
    }

    private static <T> Node<T> rightRotate(Node<T> node) {
        Node<T> tempRoot = node.left;
        Node<T> tempRootRight = tempRoot.right;

        tempRoot.right = node;
        node.left = tempRootRight;

        node.height = 1 + getMaxHeight(node);
        tempRoot.height = 1 + getMaxHeight(tempRoot);

        return tempRoot;
    }

    private static <T> Node<T> leftRotate(Node<T> node) {
        Node<T> tempRoot = node.right;
        Node<T> tempRootLeft = tempRoot.left;

        tempRoot.left = node;
        node.right = tempRootLeft;

        node.height = 1 + getMaxHeight(node);
        tempRoot.height = 1 + getMaxHeight(tempRoot);

        return tempRoot;
    }

    private static <T> Node<T> makeRotation (Node<T> root) {
        // calculate the balance level
        // balancing thing is only happen when the balance >= 1 || <= -1
        // after the update of the height we need to check the balance of the current root
        int balanceLevel = balanceLevel(root);

        // left
        if (balanceLevel > 1) {
            int leftBalanceLevel = balanceLevel(root.left);
            // left
            if (leftBalanceLevel >= 0)
                return rightRotate(root);
            // right
            root.left = leftRotate(root.left);
            // bug
            return rightRotate(root.right);
        }
        // right
        else if (balanceLevel < -1) {
            int rightBalanceLevel = balanceLevel(root.right);
            // right
            if (rightBalanceLevel <= 0)
                return leftRotate(root);
            // left
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    private static <T> boolean isEqual(PNRPair target, PNRPair current) {
        return target.equals(current);
    }

    // Used to check if there is room for inserting the element
    // because we want pre-check the element has a space to insert into the tree
    public static <T> boolean preAddCheck(Seat<T> currentObject, PNRPair object) {
        // avoid the original root gets modified
        Node<T> node = currentObject.root;
        while (node != null) {
            int compareValue = compareInterleave(currentObject.comparator, (PNRPair)object, (PNRPair)node.value);
            if (compareValue < 0)
                node = node.left;
            else if (compareValue > 0)
                node = node.right;
            else
                return false;
        }

        return true;
    }

    // --- TEST ---
    public void levelOrder() {

        for (List<Node<T>> ele : levelOrder(this.root))
            System.out.println(ele);

        System.out.println();
    }

    private List<List<Node<T>>> levelOrder(Node<T> root) {

        List<List<Node<T>>> ds = new ArrayList<>();

        if (root == null)
            return ds;

        Queue<Node<T>> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {

            int size = queue.size();
            LinkedList<Node<T>> list = new LinkedList<>();

            while (size-- > 0) {
                Node<T> temp = queue.poll();
                list.addLast(temp);

                if (temp.left != null)
                    queue.add(temp.left);

                if (temp.right != null)
                    queue.add(temp.right);
            }

            ds.add(list);
        }

        return ds;
    }

    private void inorder(Node<T> root, StringBuilder sb) {
        if (root == null)
            return;

        inorder(root.left, sb);
        sb.append(root.value).append(" ");
        inorder(root.right, sb);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        inorder (this.root, sb);
        return sb.toString();
    }
}
