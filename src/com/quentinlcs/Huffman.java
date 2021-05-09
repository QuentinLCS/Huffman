package com.quentinlcs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Huffman {

    private Node tree;
    private final Map<Character, String> encodedLetters = new HashMap<>();
    private byte biggerCode = -1;

    public Huffman(ArrayList<Character> keys, ArrayList<Integer> values) {
        this.createTree(keys, values);
        this.encodeTree(this.tree);
        this.calculateBiggerCode();
    }

    private void createTree(ArrayList<Character> keys, ArrayList<Integer> values) {

        ArrayList<Node> nodes = new ArrayList<>();
        int nodesSize = 0;

        for (int i = 0; i < keys.size(); i++) nodes.add( new Node(keys.get(i), values.get(i)) );

        while (nodesSize != 1) {
            Node[] smallerNodes = new Node[2];

            for (int h = 0; h < 2; h++) {
                nodesSize = nodes.size();
                Node smallerNode = null;
                for (int i = 0; i < nodesSize; i++) {
                    for (int j = i+1; j < nodesSize; j++) {
                        smallerNode = nodes.get(i).compareTo(nodes.get(j)) < 0 ? nodes.get(i) : nodes.get(j);
                        if (smallerNodes[h] == null || smallerNodes[h].compareTo(smallerNode) > 0) smallerNodes[h] = smallerNode;
                    }
                    if (nodesSize == 1) smallerNodes[h] = nodes.get(i);
                }
                nodes.remove(smallerNodes[h]);
            }
            if (nodesSize >= 1) nodes.add(new Node(smallerNodes[1].getSymbol(), smallerNodes[0].getFrequency() + smallerNodes[1].getFrequency(), smallerNodes[0], smallerNodes[1]));

        }

        this.tree = nodes.get(0);
    }

    public void encodeTree(Node node) {
        if (!node.isLeaf()) {
            node.getLeft().setCode(node.getCode() + '0');
            encodeTree(node.getLeft());

            node.getRight().setCode(node.getCode() + '1');
            encodeTree(node.getRight());
        } else
            encodedLetters.put(node.getSymbol(), node.getCode());
    }

    public Map<Character, String> getEncodedLetters() { return encodedLetters; }

    static public char checkIfExist(Map<String, Character> map, String string) {
        int i = 0, length = string.length();
        char lastChar = ' ';

        for (Map.Entry<String, Character> entry : map.entrySet())
            if (entry.getKey().substring(0, length).equals(string)) {
                i++;
                lastChar = entry.getValue();
            }


        return i == 1 ? lastChar : (char)27;
    }

    private void calculateBiggerCode() {
        int actualLength = 0;
        for (Map.Entry<Character, String> entry : this.encodedLetters.entrySet()) {
            actualLength = entry.getValue().length();
            if (this.biggerCode < actualLength) this.biggerCode = (byte) actualLength;
        }
    }

    public byte getBiggerCode() {
        return biggerCode;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Codage d'huffman : \n");
        for(Map.Entry<Character, String> map : this.encodedLetters.entrySet()) {
            result.append(map.getKey()).append(" - ").append(map.getValue()).append("\n");
        }
        return result.append("\n").toString();
    }
}
