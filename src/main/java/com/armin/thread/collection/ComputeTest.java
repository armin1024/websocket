package com.armin.thread.collection;

import java.util.concurrent.ConcurrentHashMap;

public class ComputeTest {

    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap();
        map.put("key", 1);
        // 修改key对应的value，追加上1

        // 之前的操作方式
        Integer oldValue = map.get("key");
        Integer newValue = oldValue + 1;
        map.put("key", newValue);
        System.out.println(map);

        // 现在的操作方式
        map.compute("key", (k, v) -> {
            if (v == null) {
                return 0;
            }
            return v+1;
        });
        System.out.println(map);
    }

}
