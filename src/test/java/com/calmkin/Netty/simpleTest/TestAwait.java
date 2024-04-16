package com.calmkin.Netty.simpleTest;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.util.logging.Logger;

public class TestAwait {
    public static void main(String[] args) {
        DefaultEventLoop eventExecutors = new DefaultEventLoop();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

        eventExecutors.submit(()->{
            System.out.println("1");
            try {
                promise.await();
                // 注意不能仅捕获 InterruptedException 异常
                // 否则 死锁检查抛出的 BlockingOperationException 会继续向上传播
                // 而提交的任务会被包装为 PromiseTask，它的 run 方法中会 catch 所有异常然后设置为 Promise 的失败结果而不会抛出
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("2");
            promise.setSuccess(1);
        });
        eventExecutors.submit(()->{
            System.out.println("3");
            try {
                promise.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("4");
            Integer res = promise.getNow();
            System.out.println(res);
        });
    }
}
