package netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * \* Author: iverson
 * \* Date: 2019-05-21
 * \* Time: 15:24
 * \* Description:
 * \
 */
@Slf4j
public class NettyServer {
  public void run(int port, ChannelHandler handler) throws Exception {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    log.info("begin run port : {}", port);
    try {
      ServerBootstrap b = new ServerBootstrap();
      b = b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, 128)
          .childHandler(new ChildChannelHandler(handler));
      //绑定端口，同步等待成功
      ChannelFuture f = b.bind(port).sync();
      //等待服务监听端口关闭
      f.channel().closeFuture().sync();
    } finally {
      //退出，释放线程资源
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }
  public static void main(String[] args) throws Exception {
    new NettyServer().run(8080, new NettyServerHandler());
  }
}
