package com.armin.thread.pool;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewFixedThreadPool {

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(() -> {
            System.out.println("任务1：" + Thread.currentThread().getName() + System.currentTimeMillis());
        });
        executor.execute(() -> {
            System.out.println("任务2：" + Thread.currentThread().getName() + System.currentTimeMillis());
        });
        executor.execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务3：" + Thread.currentThread().getName() + System.currentTimeMillis());
        });
        executor.shutdown();
    }

}
