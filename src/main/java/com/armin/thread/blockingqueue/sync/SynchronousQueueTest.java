package com.armin.thread.blockingqueue.sync;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousQueueTest {

    public static void main(String[] args) throws InterruptedException {
        // 因为当前队列不存在数据，没有长度的概念。
        SynchronousQueue queue = new SynchronousQueue();

        String msg = "消息！";
        /*new Thread(() -> {
            // b = false：代表没有消费者来拿
            boolean b = false;
            try {
                b = queue.offer(msg,1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(b);
        }).start();

        Thread.sleep(100);

        new Thread(() -> {
            System.out.println(queue.poll());
        }).start();*/
        new Thread(() -> {
            try {
                System.out.println(queue.poll(1, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(100);

        new Thread(() -> {
            queue.offer(msg);
        }).start();
    }

}
