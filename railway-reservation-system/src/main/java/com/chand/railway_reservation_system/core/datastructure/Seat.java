package com.chand.railway_reservation_system.core.datastructure;

import java.util.*;

public class Seat<T> implements Collection<T> {

    private Node<T> root;
    private Comparator<T> comparator;
    private int size;
    private boolean insertionFlag;
    private boolean removalFlag;
    private int id;

    private static final class Node<T> {
        Node<T> left;
        Node<T> right;
        int height;
        T value;

        public Node(T value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.height = 1;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    ", height=" + height +
                    '}';
        }
    }

    public Seat() {
    }

    public Seat(int seatNumber) {
        this.id = seatNumber;
    }

    public Seat(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public int getId() {
        return id;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return contains(this.comparator, this.root, ((T)o));
    }

    private static <T> boolean contains (Comparator<T> comparator, Node<T> root, T element) {
        Node<T> tempRoot = root;
        while (tempRoot != null) {
            if (element.equals(root.value))
                return true;
            int comparedValue = compareElements(comparator, element, root.value);

            if (comparedValue == 0)
                break;

            tempRoot = comparedValue < 0 ? tempRoot.left : tempRoot.right;
        }

        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    @Override
    public boolean add(T element) {
        Objects.requireNonNull(element);
        // if root is null
        this.root = add(this.root, element);

        if (this.insertionFlag) {
            this.insertionFlag = false;
            this.size++;
            return true;
        }
        return false;
    }

    private Node<T> add(Node<T> root, T value) {

        // if root is null we reach the end so we need to create the node
        if (root == null) {
            this.insertionFlag = true;
            return new Node<>(value);
        }

        int comparedValue = compareElements(comparator, value, root.value);

        if (comparedValue == 0)
            return root;
        // move left
        else if (comparedValue < 0)
            root.left = add(root.left, value);
        // move right
        else
            root.right = add(root.right, value);

        if (this.insertionFlag) {
            root.height = 1 + getMaxHeight(root);
            return makeRotation(root);
        }

        return root;
    }

    private static <T> int getMaxHeight(Node<T> node) {
        return Math.max(height(node.left), height(node.right));
    }

    private static <T> int height(Node<T> node) {
        return node == null ? 0 : node.height;
    }

    private static <T> int compareElements (Comparator<T> comparator, T obj1, T obj2) {
        if (comparator != null)
            return comparator.compare(obj1, obj2);
        return ((Comparable<T>) obj1).compareTo(obj2);
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

    private static <T> int balanceFactor(Node<T> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private static <T> Node<T> makeRotation (Node<T> root) {
        // calculate the balance level
        // balancing thing is only happen when the balance >= 1 || <= -1
        // after the update of the height we need to check the balance of the current root
        int balanceLevel = balanceFactor(root);

        // left
        if (balanceLevel > 1) {
            int leftBalanceLevel = balanceFactor(root.left);
            // left
            if (leftBalanceLevel >= 0)
                return rightRotate(root);
            // right
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }
        // right
        else if (balanceLevel < -1) {
            int rightBalanceLevel = balanceFactor(root.right);
            // right
            if (rightBalanceLevel <= 0)
                return leftRotate(root);
            // left
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    public static <T> boolean preAddCheck(Collection<Integer> t, T i) {
        return preAddCheck((Seat<T>)t, i);
    }

    public static <T> boolean preAddCheck (Seat<T> node, T element) {

        Node<T> root = node.root;

        while (root != null) {
            int comparedValue = compareElements(node.comparator, element, root.value);
            if (comparedValue == 0)
                break;
            root = comparedValue > 0 ? root.right : root.left;
        }

        return root == null;
    }

    @Override
    public boolean remove(Object o) {

        this.root = remove(this.root, (T) o);

        if (this.removalFlag) {
            this.removalFlag = false;
            this.size--;
            return true;
        }
        return false;
    }

    private Node<T> remove (Node<T> root, T element) {

        if (root == null) {
            return null;
        }

        int comparedValue = compareElements(this.comparator, element, root.value);

        if (comparedValue < 0) {
            root.left = this.remove (root.left, element);
        }

        else if (comparedValue > 0) {
            root.right = this.remove (root.right, element);
        }

        // in common case the else condition is triggered when the element is matched
        else {
            if (element.equals(root.value)) {
                this.removalFlag = true;
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
                // element is removed
                root.right = this.remove(root.right, tempNode.value);
            }
        }

        // we know from the removalFlag that the node has been deleted
        if (this.removalFlag) {
            root.height = 1 + getMaxHeight(root);
            return makeRotation(root);
        }

        return root;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    private void inorder (Node<T> node, StringBuilder sb) {

        if (node == null)
            return;

        inorder(node.left, sb);
        sb.append(node.value).append(" - ");
        inorder(node.right, sb);
    }

    // --- TEST ---
    public String levelOrder() {

        StringBuilder sb = new StringBuilder();

        for (List<Node<T>> ele : levelOrder(this.root))
            sb.append(ele).append("\n");

        return sb.toString();
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

    public String toString () {
//        StringBuilder sb = new StringBuilder();
//        inorder(this.root, sb);
//        return sb.toString();

        return levelOrder();
    }
}
