/**
 * \* Author: iverson
 * \* Date: 2019-05-22
 * \* Time: 15:28
 * \* Description:
 * \
 */
public class ServerMain3 {

  public static void main(String[] args) {
    NodeServer nodeServer = new NodeServer();
    nodeServer.start(3,"127.0.0.1", 8003);
  }
}
