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

        //compute嵌套compute获取相同key的数据（且该key未设置过值时）会造成死锁问题（JDK1.9已修复）
//        map.compute("k", (k, v) -> {
//            return map.compute("k", (key, value) -> {
//                return 0;
//            });
//        });
//        System.out.println(map);

        // 要求`key`在`map`中不能存在，必须为`null`，才会基于函数得到`value`存储进去，否则该函数调用无效
        map.computeIfAbsent("key", (k) -> {
            return 2;
        });
        System.out.println(map);

        // 要求`key`在`map`中必须存在，需要基于`oldValue`计算`newValue`，否则该函数调用无效
        map.computeIfPresent("key", (k, v) -> {
            return v + 1;
        });
        System.out.println(map);

        // replace 如果key的值等于3则替换为0，否则什么也不做
        map.replace("key", 3, 0);
        System.out.println(map);

        /**
         * 如果`key`不存在，就跟`put(key,value);`
         * 如果`key`存在，就可以基于`Function`计算，得到最终结果
         * 结果不为`null`，将`key`对应的`value`，替换为`Function`的结果
         * 结果为`null`，删除当前`key`
         */
        //merge
        map.merge("key", 3, (oldValue1, value) -> {
            return null;
        });
        System.out.println(map);
    }

}
