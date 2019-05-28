package netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * \* Author: iverson
 * \* Date: 2019-05-21
 * \* Time: 15:26
 * \* Description:
 * \
 */
public class NettyServerHandler extends ChannelHandlerAdapter {
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {

    try {
      ByteBuf in = (ByteBuf) msg;
      System.out.println("传输内容是");
      System.out.println(in.toString(CharsetUtil.UTF_8));
      ByteBuf resp= Unpooled.copiedBuffer("收到信息$".getBytes());
      ctx.writeAndFlush(resp);
    }  finally {
      ReferenceCountUtil.release(msg);
    }
  }
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    // 出现异常就关闭
    cause.printStackTrace();
    ctx.close();
  }

}
