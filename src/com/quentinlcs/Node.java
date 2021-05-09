package com.quentinlcs;

class Node implements Comparable<Node> {
    private char symbol;
    private String code = "";
    private final int frequency;
    private Node left, right;

    public Node(char symbol, int frequency) {
        this.symbol = symbol;
        this.frequency = frequency;
    }

    public Node(char symbol, int frequency, Node left, Node right) {
        this.symbol = symbol;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public int getFrequency() {
        return frequency;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int compareTo(Node other) {
        if (this.frequency < other.frequency) return -1;
        else if (this.frequency > other.frequency) return 1;

        return (this.symbol > other.symbol) ? 1 : -1;
    }

    public boolean isLeaf() {
        return this.getLeft() == null && this.getRight() == null;
    }

    @Override
    public String toString() {
        return "\nNode{" +
                "symbol=" + symbol +
                ", frequency=" + frequency +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}

