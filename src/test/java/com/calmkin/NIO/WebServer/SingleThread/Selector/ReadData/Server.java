package com.calmkin.NIO.WebServer.SingleThread.Selector.ReadData;

import com.calmkin.NIO.practice.SplictPacket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {

        // 创建selector
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 将channel注册到selector上, 建立selector和channel之间的联系
        // SelectionKey 就是将来事件发生后，通过它可以知道事件类型和所属channel
        // 第二个参数0表示不关注任何事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // 注册感兴趣的事件, 表明只关注建立连接的事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);


        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            // 没有事件发生的时候，还是会阻塞住线程
            // 有事件，才会继续运行
            // 如果有事件，但是没有处理，也会继续运行
            selector.select();


            // 处理事件，selectedKeys内部包含了所有发生的事件
            // 因为事件处理完之后，需要把事件从集合里面删掉
            // 一边遍历一边删除的话，只能拿到迭代器，不能用增强for
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();

                // 删除key
                iter.remove();

                // 如果是建立连接事件
                if (key.isAcceptable()) {
                    // 拿到触发事件对应的channel
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    // 建立连接
                    SocketChannel sc = channel.accept();

                    // 把客户端channel转换成非阻塞
                    sc.configureBlocking(false);

                    // 给客户端对应channel添加一个buffer附件
                    // 这样不同channel之间的buffer互不干扰，而且还能做到扩容
                    ByteBuffer buffer = ByteBuffer.allocate(16);

                    // 把客户端channel注册到selector
                    SelectionKey scKey = sc.register(selector, 0, buffer);

                    // 注册读事件
                    scKey.interestOps(SelectionKey.OP_READ);
                }
                // 如果发生了读事件
                else if (key.isReadable()) {
                    try {
                        // 取出发生事件对应的channel
                        SocketChannel scChannel = (SocketChannel) key.channel();

                        // 取出channel对应的附件（buffer）
                        ByteBuffer buffer = (ByteBuffer) key.attachment();

                        int read = scChannel.read(buffer);

                        // 客户端正常关闭
                        if (read == -1) {
                            key.cancel();
                        } else {
                            // 通过分隔符的方式，处理消息边界
                            SplictPacket.split(buffer);

                            // 如果没有读取到换行符
                            if(buffer.position() == buffer.limit())
                            {
                                // buffer扩容两倍
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);

                                // compact方法会把buffer变成写模式，所以需要重新切换到读模式
                                buffer.flip();

                                // 把buffer原来的数据放进去
                                newBuffer.put(buffer);

                                // 覆盖掉原来的buffer
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        // 客户端异常断开，也会触发读事件
                        // 需要将key从集合中删除
                        key.cancel();
                    }
                }

            }

        }

    }
}
