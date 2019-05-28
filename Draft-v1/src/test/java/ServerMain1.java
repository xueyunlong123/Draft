/**
 * \* Author: iverson
 * \* Date: 2019-05-22
 * \* Time: 15:28
 * \* Description:
 * \
 */
public class ServerMain1 {

  public static void main(String[] args) {
    NodeServer nodeServer = new NodeServer();
    nodeServer.getPeerMap().put(2,Peer.builder().serverId(2).ip("127.0.0.1").port(8002).build());
    nodeServer.getPeerMap().put(3,Peer.builder().serverId(3).ip("127.0.0.1").port(8003).build());
    nodeServer.start(1,"127.0.0.1", 8001);
  }
}
