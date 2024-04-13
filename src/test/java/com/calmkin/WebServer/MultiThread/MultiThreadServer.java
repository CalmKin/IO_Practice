package com.calmkin.WebServer.MultiThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static com.calmkin.util.ByteBufferUtil.debugRead;

public class MultiThreadServer
{
    public static void main(String[] args) throws IOException {

        // 设置线程名称
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        ssc.bind(new InetSocketAddress(8080));

        Selector boss = Selector.open();

        SelectionKey sscKey = ssc.register(boss, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);


        // 创建固定数量的Worker来处理读写请求
        Worker[] workers = new Worker[2];

        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("Worker-" + i);
        }

        AtomicInteger idx = new AtomicInteger();

        while(true)
        {
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();

            while(iter.hasNext())
            {
                SelectionKey key = iter.next();
                iter.remove();

                if(key.isAcceptable())
                {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);

                    // 将读写请求交给Worker
                    // 这里采用round robin的负载均衡算法进行任务派发
                    workers[idx.getAndIncrement() % workers.length ].register(sc);

                }
            }
        }
    }

    static class Worker implements Runnable{

        private String name;
        private Thread thread;
        private Selector selector;

        // 为了保证多次调用register, 线程和selector只会初始化一次
        private volatile boolean inited = false;



        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel sc) throws IOException {
            // 为了保证多次调用register, 线程和selector只会初始化一次
            // 所以需要一个变量来表示是否初始化
            if (!inited) {
                thread = new Thread(name);
                thread.start();
                selector = Selector.open();
                inited = true;
            }

            // 因为一开始没有事件，所以需要执行wakeup保证继续执行注册事件
            selector.wakeup();
            sc.register(selector, SelectionKey.OP_READ, null);
        }


        // 任务就是接受客户端连接，读取数据
        @Override
        public void run() {
            try {
                selector.select();

                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

                while(iter.hasNext())
                {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if(key.isReadable())
                    {
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
