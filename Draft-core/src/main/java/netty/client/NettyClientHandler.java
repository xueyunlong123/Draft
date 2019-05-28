package netty.client;

import org.apache.commons.lang3.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.NoArgsConstructor;

/**
 * \* Author: iverson
 * \* Date: 2019-05-21
 * \* Time: 15:30
 * \* Description:
 * \
 */
@NoArgsConstructor
public class NettyClientHandler extends ChannelHandlerAdapter {

  private String data;

  NettyClientHandler(String data) {
    this.data = data;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    ByteBuf message;
    if (StringUtils.isNotEmpty(data)){
      byte[] datas = data.getBytes();
      message = Unpooled.buffer(datas.length);
      message.writeBytes(datas);
      ctx.writeAndFlush(message);
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    try {
      ByteBuf in = (ByteBuf) msg;
      System.out.println(in.toString(CharsetUtil.UTF_8));
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    // 出现异常就关闭
    cause.printStackTrace();
    ctx.close();
  }

}

