package netty.client;

import org.junit.Test;

public class NettyClientTest {


  @Test
  public void connect() throws Exception {
    NettyClient nettyClient = new NettyClient();
    nettyClient.connect("127.0.0.1", 8080, "this is a test");
  }

}