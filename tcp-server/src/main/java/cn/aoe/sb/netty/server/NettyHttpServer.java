package cn.aoe.sb.netty.server;

import cn.aoe.sb.netty.common.handler.FilterLoggingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;

/**
 * Netty 做服务端
 *
 * @author zhaoruiqing
 * @version 1.0
 * @since 2022/8/23
 */
@Slf4j
//@Component
public class NettyHttpServer implements ApplicationListener<ApplicationStartedEvent> {
    @Value("${server.port:8080}")
    private int port;
    //private HttpServerHandler tcpServerHandler;
    //
    //public NettyHttpServer(HttpServerHandler tcpServerHandler) {
    //    this.tcpServerHandler = tcpServerHandler;
    //}

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {

        ServerBootstrap bootstrap = new ServerBootstrap();
        // 使用nio而不是aio，因为linux还不支持aio
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrap.group(bossGroup, workerGroup);

        bootstrap.channel(NioServerSocketChannel.class);

        // 设置SO_REUSEADDR为true,意味着地址可以复用
        // 某个进程占用了80端口,然后重启进程,原来的socket1处于TIME-WAIT状态,进程启动后,使用一个新的socket2,要占用80端口,如果这个时候不设置SO_REUSEADDR=true,那么启动的过程中会报端口已被占用的异常。
        // 注意,这个SO_REUSEADDR是使用serverBootstrap的option方法来设置,而不是使用childOption方法来设置,要知道具体原因,可以先看李林峰关于netty线程模式
        // @see https://www.infoq.cn/article/netty-threading-model/?utm_source=infoq&utm_campaign=user_page&utm_medium=link
        // 简单来说就是option操作是针对parentGroup的,而childOption是针对childGroup的。
        //bootstrap.childOption(NioChannelOption.SO_REUSEADDR, true);
        bootstrap.option(NioChannelOption.SO_REUSEADDR, true);
        // 对应于套接字选项中的TCP_NODELAY，该参数的使用与Nagle算法有关。
        // Nagle算法是将小的数据包组装为更大的帧然后进行发送，而不是输入一次发送一次，因此在数据包不足的时候会等待其他数据的到来，组装成大的数据包进行发送，虽然该算法有效提高了网络的有效负载，但是却造成了延时。
        // true 表示禁用Nagle算法。和TCP_NODELAY相对应的是TCP_CORK，该选项是需要等到发送的数据量最大的时候，一次性发送数据，适用于文件传输
        // SO_KEEPALIVE=true,是利用TCP的SO_KEEPALIVE属性,服务端可以探测客户端的连接是否还存活着,如果客户端因为断电或者网络问题或者客户端挂掉了等,那么服务端的连接可以关闭掉,释放资源。
        bootstrap.childOption(NioChannelOption.TCP_NODELAY, true)
                .childOption(NioChannelOption.SO_KEEPALIVE, false)
                .childOption(NioChannelOption.SO_RCVBUF, 2048) // TCP接受缓冲区的容量上限；
                .childOption(NioChannelOption.SO_SNDBUF, 2048); // TCP发送缓冲区的容量上限；

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                //ch.pipeline().addLast("codec", new ByteT());
                ch.pipeline().addLast("logging", new FilterLoggingHandler());
                //ch.pipeline().addLast("bizHandler", tcpServerHandler);
            }
        });

        ChannelFuture channelFuture = bootstrap.bind(port)
                .syncUninterruptibly()
                .addListener(future -> {
                    String logBanner = "\n\n" +
                            "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
                            "****                                                                                *\n" +
                            "***                                                                                 *\n" +
                            "**                  netty http server started on port {}.                           *\n" +
                            "**                                                                                  *\n" +
                            "*                                                                                   *\n" +
                            "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
                    log.info(logBanner, port);
                });

        channelFuture.channel().closeFuture()
                .addListener(future -> {
                    log.info("Netty Http Server Start Shutdown ............");
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                });
    }
}
