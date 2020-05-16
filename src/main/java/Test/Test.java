package Test;

import io.grpc.stub.StreamObserver;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;

public class Test {

    public static void main(String[] args) {
//        System.out.println( Math.max(1, SystemPropertyUtil.getInt(
//                "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2)));
//        int[] arr = {4,3,6,5,7,8};
//        AVLTree avlTree = new AVLTree();
//        for (int i = 0; i < arr.length; i++) {
//            avlTree.add(new Node(arr[i]));
//        }
//
//        avlTree.infixOrder();
//
//        System.out.println(avlTree.getRoot().height());
//        System.out.println(avlTree.getRoot().rightHeight());

        String str1 = new StringBuilder("计算机").append("软件").toString();
        System.out.println(str1.intern() == str1);

        String str2 = new StringBuilder("ja").append("va").toString();
        System.out.println(str2.intern() == str2);


    }
}

class AVLTree {
    private Node root;

    public Node getRoot () {
        return this.root;
    }

    public void add (Node node) {
        if (root == null) {
            root = node;
        } else {
            root.add(node);
        }
    }

    public void infixOrder() {
        if (root != null) {
            root.infixOrder();
        }
    }
}



class Node {
    int value;
    Node left;
    Node right;

    public Node (int value) {
        this.value = value;
    }

//    public int leftHeight () {
//        if (left == null) {
//            return 0;
//        }
//        return left.height();
//    }

//    public int rightHeight () {
//        if (right == null) {
//            return 0;
//        }
//        return right.height();
//    }


    public int height () {
        return Math.max(left == null ? 0 : left.height(), right == null ? 0  : right.height()) + 1;
}

    public void add (Node node) {
        if (node == null) {
            return;
        }

        if (node.value < this.value) {
            if (this.left == null) {
                this.left = node;
            } else {
                this.left.add(node);
            }
        } else {
            if (this.right == null) {
                this.right = node;
            } else {
                this.right.add(node);
            }
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "value=" + value +
                '}';
    }

    public void infixOrder () {
        if (this.left != null) {
            this.left.infixOrder();
        }
        System.out.println(this);
        if (this.right != null) {
            this.right.infixOrder();
        }
    }
}
