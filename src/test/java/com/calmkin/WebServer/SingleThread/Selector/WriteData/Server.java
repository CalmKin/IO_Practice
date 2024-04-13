package com.calmkin.WebServer.SingleThread.Selector.WriteData;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author CalmKin
 * @description 服务器写入
 * @version 1.0
 * @date 2024/4/11 11:46
 */
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while(true)
        {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

            while(iter.hasNext())
            {
                SelectionKey key = iter.next();
                iter.remove();

                if(key.isAcceptable())
                {
                    SocketChannel sc = ssc.accept();
                    // 客户端socket设置成非阻塞
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);


                    // 向客户端socket写数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append('a');
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    // 写入数据，write返回值代表实际写入字节数
                    int write = sc.write(buffer);

                    // 如果还有数据没写完，那么先注册一个写事件，等到下一次可写的时候再写
                    if(buffer.hasRemaining())
                    {
                        // 注册客户端的写事件
                        // 不能直接这样写，因为有可能原先还注册了监听别的事件，比如上面的读事件
                        // 这样操作的话，就会把原来感兴趣的事件覆盖掉
                        // scKey.interestOps( SelectionKey.OP_WRITE  );

                        // 这样操作的话，可以同时监听多个事件
                        scKey.interestOps( scKey.interestOps() | SelectionKey.OP_WRITE );

                        // 一次可能写不完，所以还要把没写完的数据先挂到channel上
                        // 后面还可以取出来继续写
                        scKey.attach(buffer);

                    }
                }
                // 触发写事件
                if(key.isWritable())
                {
                    // 取出channel和buffer，继续写
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();

                    // 接着把buffer剩下没写的内容写完
                    int write = sc.write(buffer);

                    // 虽然这一次可能还是写不完
                    // 但是我们已经关注了写事件，所以后续触发之后还可以继续写

                    // 如果已经完成了数据的写入，那么需要做相对应的善后工作
                    if(!buffer.hasRemaining())
                    {
                        // 绑定一个null，就能把原来的buffer释放掉
                        key.attach(null);
                        // 取消关注写事件
                        // 因为原来我们通过 位或的方式添加事件的，所以取消事件的时候，通过异或取消
                        key.interestOps( key.interestOps() ^ SelectionKey.OP_WRITE );
                    }

                }


            }

        }

    }
}
