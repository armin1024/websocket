package com.armin.thread.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewSingleThreadExecutor {

    public static void main(String[] args) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            System.out.println("任务1：" + Thread.currentThread().getName());
        });
        executor.execute(() -> {
            System.out.println("任务2：" + Thread.currentThread().getName());
        });
        executor.execute(() -> {
            System.out.println("任务3：" + Thread.currentThread().getName());
        });
        executor.shutdown();

    }

}
