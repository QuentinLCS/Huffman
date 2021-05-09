package com.quentinlcs;

import java.util.ArrayList;
import java.util.Map;

public class Sorter {

    private final ArrayList<Integer> values = new ArrayList<>();
    private final ArrayList<Character> keys = new ArrayList<>();
    private final int size;

    public Sorter(Map<Character, Integer> map) {
        map.forEach((key, value) -> {
            this.values.add(value);
            this.keys.add(key);
        });
        this.size = this.values.size()-1;
    }

    public ArrayList<Integer> getValues() {
        return values;
    }

    public ArrayList<Character> getKeys() {
        return keys;
    }

    private void exchange(int index1, int index2){

        int tempValue = this.values.get(index1);
        this.values.set(index1, this.values.get(index2));
        this.values.set(index2, tempValue);

        char tempKey = this.keys.get(index1);
        this.keys.set(index1, this.keys.get(index2));
        this.keys.set(index2, tempKey);

    }

    public boolean isSorted(){

        for (int i = 0; i < this.values.size()-1; i++)
            if ( this.values.get(i) > this.values.get(i+1) )
                return false;

        return true;

    }

    public void sort() {

        for ( int i = this.size/2; i >= 0; i-- ) this.reorganize(i, this.size);
        for (int i = this.size; i >= 1; i--) {
            this.exchange(i, 0);
            this.reorganize(0, i - 1);
        }

    }

    private void reorganize(int node, int size) {
        int i = 2 * node;

        while (i <= size) {
            if (i < size && (this.values.get(i) < this.values.get(i+1) || (this.values.get(i).equals(this.values.get(i + 1)) && this.keys.get(i) < this.keys.get(i+1))))
                i++;
            if (this.values.get(node) < this.values.get(i) || (this.values.get(node).equals(this.values.get(i)) && this.keys.get(node) < this.keys.get(i))) {
                this.exchange(node, i);
                node = i;
                i = 2 * node;
            } else i = size+1;
        }
    }
}
