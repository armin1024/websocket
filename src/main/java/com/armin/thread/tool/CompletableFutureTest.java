package com.armin.thread.tool;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureTest {

    public static void main(String[] args) throws IOException {

//        testSupplyAsync();
//        testRunAsync();
//        testThenApply();
        testThenAccept();
    }

    //thenAccept拿到上一个任务的返回值为入参做操作，不做返回。thenAcceptAsync可接收线程池
    private static void testThenAccept() {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("任务A");
            return "abc";
        }).thenAccept(result -> {
            System.out.println("任务B输出任务A返回：" + result);
        });
    }

    //第一个线程执行后第二个线程拿到第一个线程的结果后继续执行
    private static void testThenApply() {
        //way 1
        /*CompletableFuture<String> taskA = CompletableFuture.supplyAsync(() -> {
            String id = UUID.randomUUID().toString();
            System.out.println("线程A执行, id：" + id + ", " + Thread.currentThread().getName());
            return id;
        });
        CompletableFuture<String> taskB = taskA.thenApply(result -> {
            System.out.println("线程B执行, 拿到线程A id:" + result + ", " + Thread.currentThread().getName());
            return result.replaceAll("-", "");
        });
        String result = taskB.join();
        System.out.println("main get B result:" + result);
        */
        //another way
        /*CompletableFuture<String> task = CompletableFuture.supplyAsync(() -> {
            String id = UUID.randomUUID().toString();
            System.out.println("线程A执行, id：" + id + ", " + Thread.currentThread().getName());
            return id;
        }).thenApply(result -> {
            System.out.println("线程B执行, 拿到线程A id:" + result + ", " + Thread.currentThread().getName());
            return result.replaceAll("-", "");
        });
        String result = task.join();
        System.out.println("main get B result:" + result);*/

        //another way 可以使用线程池异步执行
        CompletableFuture<String> task = CompletableFuture.supplyAsync(() -> {
            String id = UUID.randomUUID().toString();
            System.out.println("线程A执行, id：" + id + ", " + Thread.currentThread().getName());
            return id;
        }).thenApplyAsync(result -> {
            System.out.println("线程B执行, 拿到线程A id:" + result + ", " + Thread.currentThread().getName());
            return result.replaceAll("-", "");
        });
        String result = task.join();
        System.out.println("main get B result:" + result);
    }

    //线程无返回值，与普通线程无异
    private static void testRunAsync() throws IOException {
        CompletableFuture.runAsync(() -> {
            System.out.println("任务go");
            System.out.println("任务done");
        });

        System.in.read();
    }

    //线程有返回值
    private static void testSupplyAsync() {
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
    }

}
