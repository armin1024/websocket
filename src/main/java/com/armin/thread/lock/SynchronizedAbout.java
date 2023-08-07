package com.armin.thread.lock;

public class SynchronizedAbout {

    public static void main(String[] args) {
        //锁的是当前Test.class
        Test.a();
        Test test = new Test();
        //锁的是new出来的test对象
        test.b();
    }

}

class Test {

    public static synchronized void a() {
        System.out.println("method a()");
    }

    public synchronized void b() {
        System.out.println("method b()");
    }

}
