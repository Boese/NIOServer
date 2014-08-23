package com.nio.echoserver;

import naga.*;

import java.io.IOException;

public class NIOEchoServer
{
  public static void main(String... args)
  {
    //int port = Integer.parseInt(args[0]);
	  int port = 5218;
    try
    {
      // Create the NIO service.
      NIOService service = new NIOService();

      // Open a server socket.
      NIOServerSocket socket = service.openServerSocket(port);
      
      // Set our server socket observer to listen to the server socket.
      socket.listen(new ServerSocketObserverAdapter()
      { 
        public void newConnection(NIOSocket nioSocket)
        {
          // Set our socket observer to listen to the new socket.
          nioSocket.listen(new SocketObserverAdapter()
          {
        	  @Override
            public void packetReceived(NIOSocket socket, byte[] packet)
            {
              // Write the bytes back to the client.
              socket.write(packet);
            }
          });
        }
      });

      // Set server socket accept policy to always accept new clients.
      socket.setConnectionAcceptor(ConnectionAcceptor.ALLOW);

      // Handle IO until we quit the program.
      while (true)
      {
        service.selectNonBlocking();
      }
    }
    catch (IOException e)
    {
      // Ignore any IOExceptions in this simple implementation.
    }
  }
}