package com.armin.thread.blockingqueue.delay;

import java.util.concurrent.DelayQueue;

public class DelayQueueTest {

    public static void main(String[] args) throws InterruptedException {
        // 声明元素
        Task task1 = new Task("A",1000L);
        Task task2 = new Task("B",5000L);
        Task task3 = new Task("C",3000L);
        Task task4 = new Task("D",2000L);
        // 声明阻塞队列
        DelayQueue<Task> queue = new DelayQueue<>();
        // 将元素添加到延迟队列中
        queue.put(task1);
        queue.put(task2);
        queue.put(task3);
        queue.put(task4);
        // 获取元素
        System.out.println(queue.take());
        System.out.println(queue.take());
        System.out.println(queue.take());
//        System.out.println(queue.take());
        // A,D,C,B
    }

}
