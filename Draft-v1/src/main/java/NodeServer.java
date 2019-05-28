import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import netty.server.NettyServer;
import store.log.DraftLog;
import store.log.DraftLogMetaData;
import store.log.LogEntry;

/**
 * \* Author: iverson
 * \* Date: 2019-05-21
 * \* Time: 15:15
 * \* Description:
 * \
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@ChannelHandler.Sharable
@Data
public class NodeServer extends ChannelHandlerAdapter {

  private Integer serverId;

  private NettyServer rpcServer = new NettyServer();

  private DraftLog draftLog = new DraftLog();

  private AtomicBoolean init = new AtomicBoolean(false);

  private Map<Integer, Peer> peerMap = new HashMap<>();

  private ExecutorService executorService;

  private Lock lock = new ReentrantLock();

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {

    try {
      ByteBuf in = (ByteBuf) msg;
      log.info("传输内容是:{}", in.toString(CharsetUtil.UTF_8));
      long index = add(in.toString(CharsetUtil.UTF_8));
      ByteBuf resp = Unpooled.copiedBuffer(String.valueOf(index).getBytes());
      ctx.writeAndFlush(resp);
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    // 出现异常就关闭
    cause.printStackTrace();
    ctx.close();
  }

  void start(Integer serverId, String host, int port) {
    try {
      this.serverId = serverId;
      peerMap.put(serverId, Peer.builder().ip(host).serverId(serverId).port(port).build());
      draftLog.setMetaData(DraftLogMetaData.builder()
                               .firstIndex(0)
                               .lastIndex(0)
                               .logDir("/Users/didi/IdeaProjects/Draft/".concat(String.valueOf(serverId).concat("/")))
                               .maxSize(10)
                               .build());

      executorService = new ThreadPoolExecutor(
          20,
          20,
          60,
          TimeUnit.SECONDS, new LinkedBlockingQueue<>());
      init.compareAndSet(false, true);
      rpcServer.run(port, this);

    } catch (Exception e) {
      log.info("start rpc error");
    }
  }

  //追加存储数据
  private long add(String data) {
    if (!init.get()) {
      log.info("node server not start");
      return -1;
    }
    long append = draftLog.append(Lists.newArrayList(LogEntry.builder().data(data).build()));
    replicate(data);
    return append;
  }

  //同步数据至其他node 节点
  private void replicate(String data) {
    lock.lock();
    try {
      peerMap.entrySet()
          .stream()
          .filter(integerPeerEntry -> !integerPeerEntry.getKey().equals(serverId))
          .forEach(integerPeerEntry -> executorService.execute(() -> {
            try {
              integerPeerEntry.getValue().getClient().send(data);
            } catch (Exception e) {
              log.error("replicate data to peers error", e);
            }
          }));
    } finally {
      lock.unlock();
    }
  }


}
