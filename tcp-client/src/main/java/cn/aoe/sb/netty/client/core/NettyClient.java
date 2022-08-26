package cn.aoe.sb.netty.client.core;

import cn.aoe.sb.netty.common.base.PackUtil;
import cn.aoe.sb.netty.common.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaoruiqing
 * @version 1.0
 * @since 2022/8/23
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "netty.remote", name = "host", matchIfMissing = false)
public class NettyClient {
    private final EventLoopGroup group = new NioEventLoopGroup();
    private SocketChannel socketChannel;

    @Value("${netty.remote.host}")
    private String host;
    @Value("${netty.remote.port}")
    private Integer port;


    /**
     * 发送消息
     */
    public void sendMsg(byte[] bytes) {
        if (!socketChannel.isActive()) {
            log.info("socket channel is not active. reconnecting...");
            socketChannel.connect(new InetSocketAddress(host, port));
        }
        if (log.isDebugEnabled()) {
            log.debug("send msg hex string: {}", PackUtil.bytes2HexStr(bytes));
        }
        socketChannel.writeAndFlush(bytes);
    }

    public byte[] sendMsgAndReceive(byte[] bytes) {
        if (!socketChannel.isActive()) {
            log.info("socket channel is not active. reconnecting...");
            socketChannel.connect(new InetSocketAddress(host, port));
        }
        if (log.isDebugEnabled()) {
            log.debug("send msg hex string: {}", PackUtil.bytes2HexStr(bytes));
        }
        socketChannel.writeAndFlush(bytes);
        return null;
    }

    @PostConstruct
    public void start() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                //.remoteAddress(host, port)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ByteArrayDecoder());
                        socketChannel.pipeline().addLast(new ByteArrayEncoder());
                        socketChannel.pipeline().addLast(new NettyClientHandler());
                    }
                });

        ChannelFuture future = bootstrap.connect(host, port);
        //客户端断线重连逻辑
        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                log.info("连接Netty服务端成功");
            } else {
                log.info("连接失败，进行断线重连");
                future1.channel().eventLoop().schedule(this::start, 5, TimeUnit.SECONDS);
            }
        });
        socketChannel = (SocketChannel) future.channel();
        //Channel channel = future.sync().channel();
    }
}
