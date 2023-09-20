package com.armin.thread.tool;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreTest {

    public static void main(String[] args) throws InterruptedException {
        // 今天环球影城还有人个人流量
        Semaphore semaphore = new Semaphore(10);

        new Thread(() -> {
            try {
                System.out.println("一家三口要去~~");
                semaphore.acquire(3);
                System.out.println("一家三口进去了~~~");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release(3);
                System.out.println("一家三口走了~~~");
            }
        }).start();

        for (int i = 0; i < 7; i++) {
            int j = i;
            new Thread(() -> {
                try {
                    System.out.println(j + "大哥来了。");
                    semaphore.acquire();
                    System.out.println(j + "大哥进去了~~~");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                    System.out.println(j + "大哥走了~~~");
                }
            }).start();
        }

        Thread.sleep(10);

        System.out.println("main大哥来了。");
        if (semaphore.tryAcquire(10, TimeUnit.SECONDS)) {
            System.out.println("main大哥进来了。");
        } else {
            System.out.println("资源不够，main大哥没进来。");
        }
        Thread.sleep(10000);

        System.out.println("main大哥又来了。");
        if (semaphore.tryAcquire()) {
            System.out.println("main大哥进来了。");
            semaphore.release();
        } else {
            System.out.println("资源不够，main大哥没进来。");
        }
    }

}
