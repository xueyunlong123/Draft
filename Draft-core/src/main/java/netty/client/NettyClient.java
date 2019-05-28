package netty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * \* Author: iverson
 * \* Date: 2019-05-21
 * \* Time: 15:29
 * \* Description:
 * \
 */
@Slf4j@NoArgsConstructor
public class NettyClient {

  private String host;

  private Integer port;

  public NettyClient(String host, Integer port) {
    this.host = host;
    this.port = port;
  }

  public void send(String data) throws Exception {
    connect(host, port, data);
  }
  void connect(String host, int port, final String data) throws Exception {
    //配置客户端
    log.info("host={}, port={}, data ={}", host, port, data);
    EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(eventLoopGroup)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY, true)
          .handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel){
              socketChannel.pipeline().addLast(new NettyClientHandler(data));
            }
          });

      //绑定端口，同步等待成功
      ChannelFuture f = b.connect(host, port).sync();
      //等待服务监听端口关闭
      f.channel().closeFuture().sync();
    } finally {
      //优雅退出，释放线程资源
      eventLoopGroup.shutdownGracefully();
    }
  }

  public void run() throws IOException {
    EventLoopGroup worker = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(worker);
    bootstrap.channel(NioSocketChannel.class);
    bootstrap.handler(new NettyClientHandler());
    try {
      Channel channel = bootstrap.connect(host, port).sync().channel();
      while (true) {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));
        String input = reader.readLine();
        if (input != null) {
          if ("quit".equals(input)) {
            System.exit(1);
          }
          channel.writeAndFlush(Unpooled.copiedBuffer(input, CharsetUtil.UTF_8));
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }


  public static void main(String[] args) throws Exception {
    new NettyClient("127.0.0.1", 8001).run();
  }
}

