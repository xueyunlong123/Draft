/**
 * \* Author: iverson
 * \* Date: 2019-05-22
 * \* Time: 15:28
 * \* Description:
 * \
 */
public class ServerMain2 {

  public static void main(String[] args) {
    NodeServer nodeServer = new NodeServer();
    nodeServer.start(2,"127.0.0.1", 8002);
  }
}
