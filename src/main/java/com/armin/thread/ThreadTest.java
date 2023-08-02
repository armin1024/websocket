package com.armin.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 线程的多种创建方式
 */
public class ThreadTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /*for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }*/
        XssCallable xssCallable = new XssCallable(128F, 120000F);
        FutureTask<Float> task = new FutureTask<>(xssCallable);
        Thread thread = new Thread(task);
        thread.start();
        //do other things
        System.out.println("做的其他计算工作...");
        System.out.println("阻塞获取异步线程数据...");
        System.out.println(task.get());
    }

}

class XssCallable implements Callable<Float> {

    private Float x;
    private Float y;

    public XssCallable(Float x, Float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Float call() throws Exception {
        System.out.println("开始睡觉...");
        Thread.sleep(5000);
        System.out.println("睡醒了！");
        return x / y * 100;
    }
}