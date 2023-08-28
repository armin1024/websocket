package com.armin.thread.blockingqueue.priority;

import java.util.concurrent.PriorityBlockingQueue;

public class PriorityBlockingQueueTest {

    public static void main(String[] args) {
        PriorityBlockingQueue<Integer> intQueue = new PriorityBlockingQueue<>();
        PriorityBlockingQueue<String> strQueue = new PriorityBlockingQueue<>();
        intQueue.add(1);
        intQueue.add(10);
        intQueue.add(5);
        intQueue.add(3);
        strQueue.add("456");
        strQueue.add("123");
        strQueue.add("345");
        System.out.println(intQueue.poll());
        System.out.println(intQueue.poll());
        System.out.println(intQueue.poll());
        System.out.println("=============");
        System.out.println(strQueue.poll());
        System.out.println(strQueue.poll());
        System.out.println(strQueue.poll());
    }

}
