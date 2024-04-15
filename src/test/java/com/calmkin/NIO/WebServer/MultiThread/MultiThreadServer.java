package com.calmkin.NIO.WebServer.MultiThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.calmkin.util.ByteBufferUtil.debugRead;

public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        new BossEventLoop().register();
    }

    static class BossEventLoop implements Runnable {

        private Selector boss;
        private WorkerEventLoop[] wokers;

        private volatile boolean started = false;

        AtomicInteger index = new AtomicInteger();

        public void register() throws IOException {
            if (!started) {
                // 初始化服务器Socket
                ServerSocketChannel ssc = ServerSocketChannel.open();
                ssc.bind(new InetSocketAddress(8080));
                ssc.configureBlocking(false);

                // 初始化Selector
                boss = Selector.open();

                // 注册事件
                SelectionKey sscKey = ssc.register(boss, 0, null);
                sscKey.interestOps(SelectionKey.OP_ACCEPT);

                // 初始化Worker
                wokers = initEventLoops();

                // 启动Boss线程
                new Thread(this, "boss").start();

                started = true;
            }
        }

        private WorkerEventLoop[] initEventLoops() {
            // 创建和核数相同个数的Worker
            WorkerEventLoop[] eventLoops = new WorkerEventLoop[Runtime.getRuntime().availableProcessors()];

            for (int i = 0; i < eventLoops.length; i++) {
                eventLoops[i] = new WorkerEventLoop("Worker-" + i);
            }

            return eventLoops;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    boss.select();
                    Iterator<SelectionKey> iter = boss.selectedKeys().iterator();

                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();

                        // 建立连接事件发生之后
                        if (key.isAcceptable()) {
                            SocketChannel sc = (SocketChannel) key.channel();
                            sc.configureBlocking(false);

                            // 将读写请求交给Worker
                            // 这里采用round robin的负载均衡算法进行任务派发
                            wokers[index.getAndIncrement() % wokers.length].register(sc);

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class WorkerEventLoop implements Runnable {

        private String name;
        private Thread thread;
        private Selector worker;

        // 为了保证多次调用register, 线程和selector只会初始化一次
        private volatile boolean inited = false;

        // 用于保存注册事件的任务
        private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

        public WorkerEventLoop(String name) {
            this.name = name;
        }

        public void register(SocketChannel sc) throws IOException {
            // 为了保证多次调用register, 线程和selector只会初始化一次
            // 所以需要一个变量来表示是否初始化
            if (!inited) {
                thread = new Thread(name);
                thread.start();
                worker = Selector.open();
                inited = true;
            }

            // 因为一开始没有事件，所以需要执行wakeup保证继续执行注册事件
            tasks.add(()->{
                try {
                    SelectionKey scKey = sc.register(worker, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }

            });

            worker.wakeup();

        }


        // 任务就是接受客户端连接，读取数据
        @Override
        public void run() {
            try {
                worker.select();

                // 如果有注册任务
                Runnable task = tasks.poll();
                if(task != null)
                {
                    task.run();
                }

                Iterator<SelectionKey> iter = worker.selectedKeys().iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        buffer.flip();
                        sc.read(buffer);
                        debugRead(buffer);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }


}
