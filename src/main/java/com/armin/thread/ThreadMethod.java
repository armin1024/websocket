package com.armin.thread;

/**
 * 线程常用方法
 */
public class ThreadMethod {

    public static void main(String[] args) throws InterruptedException {
//        getCurrentThread();
//        setThreadName();
//        setPriority();
//        executeYield();
//        executeSleep();
//        executeJoin();
//        setDaemon();
//        executeWaitAndNotify();
//        executeStop();
//        executeVolatile();
        executeInterrupt();
    }

    /**
     * 获取当前线程信息
     */
    public static void getCurrentThread() {
        Thread currentThread = Thread.currentThread();
        System.out.println(currentThread);
        //return "Thread[" + getName() + "," + getPriority() + "," + group.getName() + "]";
        //Thread[main,5,main]
    }

    /**
     * 设置线程名称：当出现异常时方便追溯到问题线程
     */
    public static void setThreadName() {
        Thread thread = new Thread(() -> {
            System.out.println(Thread.currentThread().getName()); //默认：Thread-0，设置名称后：模块-功能-计数器
        });
        thread.setName("模块-功能-计数器");
        thread.start();
    }

    /**
     * 线程的优先级：取值范围（1~10）数值越高优先级越高，默认5
     */
    public static void setPriority() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("t1:" + i);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("t2:" + i);
            }
        });
        t2.start();
        t1.start();
        t1.setPriority(10); //虽然t2先执行，但t1的优先级最高，所以t1会由cpu优先调度
        t2.setPriority(1);
    }

    /**
     * 线程的让步：从运行状态到就绪状态
     */
    public static void executeYield() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                if (i == 50) {
                    Thread.yield(); //当t1的i循环到5让t1让出cpu调度（让出后可能cpu马上又分配给了t1）
                }
                System.out.println("t1:" + i);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("t2:" + i);
            }
        });
        t1.start();
        t2.start();
    }

    /**
     * 线程的休眠
     */
    public static void executeSleep() throws InterruptedException {
        System.out.println(System.currentTimeMillis());
        Thread.sleep(1000L);
        System.out.println(System.currentTimeMillis());
    }

    /**
     * 线程的抢占
     * Thread的非静态方法join()
     * 需要在某个线程下去调用这个方法
     * - 如果在main线程中调用了t1.join()，那么main线程会进入等待状态，需要等待t1线程全部执行完毕，再恢复到就绪状态等待cpu调度
     * - 如果在main线程中调用了t1.join(2000)，那么main线程会进入等待状态，需要等待t1执行2s后，再恢复到就绪状态等待cpu调度
     * 如果在等待期间，t1已经结束了，那么main线程自动变为就绪状态等待cpu调度
     */
    public static void executeJoin() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("t1:" + i);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("t2:" + i);
            }
        });
        t1.start();
        t2.start();
        for (int i = 0; i < 10; i++) {
            System.out.println("main");
            if (i == 1) {
                t1.join(2000);
            }
        }
    }

    /**
     * 守护线程
     * 默认情况下，线程都是非守护线程
     * JVM会在程序中没有非守护线程时，结束掉当前JVM
     * 主线程默认是非守护线程，如果主线程执行结束，需要查看当前JVM内是否还有非守护线程，如果没有JVM直接停止
     */
    public static void setDaemon() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t1:" + i);
            }
        });
        t1.setDaemon(true); //设置t1为守护线程，主线程结束t1不管有没有执行完毕也随之结束
        t1.start();
    }

    /**
     * 线程的等待和唤醒
     * 可以让获取synchronized锁资源的线程通过wait()进入到锁的等待池，并且会释放锁资源
     * 可以让获取synchronized锁资源的线程，通过notify()或notifyAll()，将等待池中的线程唤醒，添加到锁池中
     * notify()随机的唤醒等待池中的一个线程到锁池
     * notifyAll()将等待池中的全部线程都唤醒，并且添加到锁池
     * - 等待池：WAITING
     * - 锁池：BLOCKED
     * 在调用wait()和notify()以及notifyAll()时，必须在synchronized修饰的代码快或者方法内部才可以，因为要操作基于某个对象的锁的信息维护
     */
    public static void executeWaitAndNotify() throws InterruptedException {
        Thread t1 = new Thread(ThreadMethod::sync, "t1");
        Thread t2 = new Thread(ThreadMethod::sync, "t2");
        t1.start();
        t2.start();
        Thread.sleep(12000);
        synchronized (ThreadMethod.class) {
            ThreadMethod.class.notifyAll();
        }
    }

    private static synchronized void sync() {
        try {
            for (int i = 0; i < 10; i++) {
                if (i == 5) {
                    ThreadMethod.class.wait();
                }
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 线程的结束方式
     * 线程结束方式很多，最常用的就是让线程的run方法结束，无论是return结束，还是抛出异常结束，都可以
     */
    //方式1：（不建议）强制线程结束，无论你在干嘛，不推荐使用，但是他确实可以把线程干掉
    public static void executeStop() throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        System.out.println(thread.getState()); //RUNNABLE
        thread.stop();
        Thread.sleep(500); //stop()可能存在延迟，需要小小的睡一会儿方可看到结果
        System.out.println(thread.getState()); //TERMINATED
    }

    //方式2：使用共享变量（用的不多）
    //这种方式用的也不多，有的线程可能会通过死循环来保证一直运行
    //我们可以通过修改共享变量破坏死循环，让线程推出循环，结束run方法
    static volatile boolean flag = true; //共享变量需要使用volatile修饰，否则共享不生效
    public static void executeVolatile() throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (flag) {
                //do something
            }
            System.out.println("任务结束");
        });
        thread.start();
        Thread.sleep(500);
        flag = false;
    }

    //方式3：interrupt
    //通过打断WAITING或者TIMED_WAITING状态的线程，从而抛出异常自行处理
    //这种停止线程方式是最常用的一种，在框架和JUC中也是最常见的
    public static void executeInterrupt() throws InterruptedException {
        //线程默认情况下， interrupt标记位：false
        System.out.println(Thread.currentThread().isInterrupted());
        //执行interrupt之后，再次查看打断信息
        Thread.currentThread().interrupt();
        //interrupt标记位：true
        System.out.println(Thread.currentThread().isInterrupted());
        //返回当前线程，并归位为false interrupt标记位：true
        System.out.println(Thread.interrupted());
        //已经归位了
        System.out.println(Thread.interrupted());

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("基于打断形式结束当前线程");
                    return;
                }
            }
        });
        thread.start();
        Thread.sleep(500);
        thread.interrupt(); //无法处理BLOCKED
    }
}
