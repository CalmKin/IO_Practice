package com.calmkin.Netty.simpleTest;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestNettyFuture {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Future<Integer> future = eventLoopGroup.submit(() -> {
            log.debug("执行计算....");
            return 100;
        });


        // 方式一：同步阻塞获取结果
//        Integer res = future.get();
//        log.debug("获得结果:{}" , res);

        // 方式二：异步回调
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                // 既然回调方法被调用了，说明已经得到结果了
                // 所以没必要阻塞获取结果
                Integer asyncResult = (Integer) future.getNow();
                log.debug("获得结果:{}" , asyncResult);
            }
        });


    }
}
