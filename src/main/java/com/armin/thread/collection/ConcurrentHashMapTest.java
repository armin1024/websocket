package com.armin.thread.collection;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapTest {

    public static void main(String[] args) {
        ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();
        String put = concurrentHashMap.put("a", "b");
        String put1 = concurrentHashMap.put("a", "c");
        System.out.println(put);
        System.out.println(put1);
        System.out.println(concurrentHashMap.get("a"));

        System.out.println("-------------");

        String s = concurrentHashMap.putIfAbsent("k", "n");
        String s1 = concurrentHashMap.putIfAbsent("k", "m");
        System.out.println(s);
        System.out.println(s1);
        System.out.println(concurrentHashMap.get("k"));
    }

}
