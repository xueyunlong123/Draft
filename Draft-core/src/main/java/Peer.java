import lombok.Builder;
import lombok.Data;
import netty.client.NettyClient;

/**
 * \* Author: iverson
 * \* Date: 2019-05-22
 * \* Time: 10:05
 * \* Description: 被监视的节点
 * \
 */
@Data@Builder
public class Peer {

  private Integer serverId;

  private String ip;

  private Integer port;

  private NettyClient client;

  public NettyClient getClient() {
    client = new NettyClient(ip, port);
    return client;
  }
}
