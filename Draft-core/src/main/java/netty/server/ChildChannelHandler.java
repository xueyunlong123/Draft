package netty.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * \* Author: iverson
 * \* Date: 2019-05-21
 * \* Time: 15:24
 * \* Description:
 * \
 */
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

  private ChannelHandler handler;

  public ChildChannelHandler(ChannelHandler handler) {
    this.handler = handler;
  }

  protected void initChannel(SocketChannel socketChannel) {
    socketChannel.pipeline().addLast(handler);
  }
}

