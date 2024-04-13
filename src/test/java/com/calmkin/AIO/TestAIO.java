package com.calmkin.AIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Semaphore;

import static com.calmkin.util.ByteBufferUtil.debugAll;

public class TestAIO {
    public static void main(String[] args) throws InterruptedException {

        Semaphore semaphore = new Semaphore(0);

        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("test.txt"), StandardOpenOption.READ)) {

            ByteBuffer buffer = ByteBuffer.allocate(16);
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                // read 成功之后会调用这个方法
                // result表示读取成功的字节数
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                     attachment.flip();
                     debugAll(attachment);
                     semaphore.release();
                }
                // read失败会调用这个方法
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

        semaphore.acquire();

    }
}
