package com.armin.thread.tool;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureTest {

    public static void main(String[] args) throws IOException {
        // 生产者，可以指定返回结果
        CompletableFuture<String> firstTask = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务开始执行");
            System.out.println("异步任务执行结束");
            return "返回结果";
        });

        String result1 = firstTask.join();
        String result2 = null;
        try {
            result2 = firstTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println(result1 + "," + result2);

        CompletableFuture.runAsync(() -> {
            System.out.println("任务go");
            System.out.println("任务done");
        });

        System.in.read();
    }

}
