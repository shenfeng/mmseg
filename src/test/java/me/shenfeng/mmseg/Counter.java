package me.shenfeng.mmseg;

import java.util.HashMap;

public class Counter {

    private HashMap<Object, Integer> counter = new HashMap<Object, Integer>();

    public void add(Object key) {
        Integer i = counter.get(key);
        if (i == null) {
            i = 0;
        }
        i++;
        counter.put(key, i);
    }

    public String toString() {
        return counter.toString();
    }
}
