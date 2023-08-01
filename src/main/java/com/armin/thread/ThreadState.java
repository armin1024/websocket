package com.armin.thread;

/**
 * Java线程的6种状态
 */
public class ThreadState {

    public static void main(String[] args) throws Exception {
        testTerminated();
    }

    public static void testNewAndRunnable() {
        Thread thread = new Thread(() -> {});
        System.out.println(thread.getState()); //NEW
        thread.start();
        System.out.println(thread.getState()); //RUNNABLE
    }

    public static void testBlocked() throws InterruptedException {
        Object obj = new Object();
        Thread thread = new Thread(() -> {
            //thread未获取到obj锁资源，所以状态为阻塞
            synchronized (obj) {
                System.out.println("execute");
            }
        });
        //主线程获取到了锁资源
        synchronized (obj) {
            thread.start();
            Thread.sleep(1);
            System.out.println(thread.getState()); //BLOCKED
        }
    }

    public static void testWaiting() throws Exception {
        Object obj = new Object();
        Thread thread = new Thread(() -> {
            synchronized (obj) {
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
        Thread.sleep(500);
        System.out.println(thread.getState()); //WAITING
    }

    public static void testTimedWaiting() throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        //thread线程启动后，主线程休眠了0.5秒再获取thread的状态，此时thread线程还在休眠中
        thread.start();
        Thread.sleep(500);
        System.out.println(thread.getState()); //TIMED_WAITING
    }

    public static void testTerminated() throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        //thread线程启动后，主线程休眠了1秒再获取thread的状态，此时thread线程已经执行完毕
        thread.start();
        Thread.sleep(1000);
        System.out.println(thread.getState()); //TERMINATED
    }

}
