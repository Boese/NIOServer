package com.nio.niochatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import naga.NIOService;
import naga.NIOSocket;
import naga.SocketObserver;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;

public class ChatClient {
	ChatClient(){}
	
	public static void main(String... args)
    {
            try
            {
                    // Start up the service.
                    NIOService service = new NIOService();

                    // Open our socket.
                    NIOSocket socket = service.openSocket("localhost", 5217);

                    // Use regular 1 byte header reader/writer
                    socket.setPacketReader(new AsciiLinePacketReader());
                    socket.setPacketWriter(new AsciiLinePacketWriter());
                    
                    //Start new thread to capture input. Write to NIOSocket
                    Thread t = new Thread() {

						@Override
						public void run() {
							BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
							try {
								String temp = "";
								while(!temp.equalsIgnoreCase("quit")) {
					                temp = reader.readLine();
					                socket.write(temp.getBytes());
								}
								System.exit(0);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							super.run();
						}
                    	
                    };
                    t.start();
                    

                    // Start listening to the socket.
                    socket.listen(new SocketObserver()
                    {
                    	@Override
                        public void connectionOpened(NIOSocket nioSocket)
                        {
                                System.out.println("connection opened");
                        }
                    	@Override
			            public void packetSent(NIOSocket socket, Object tag)
			            {
			                System.out.println("Packet sent");
			            }
                    	@Override
		            	public void packetReceived(NIOSocket socket, byte[] packet)
                        {
                                try
                                {
                                	// Create the string. For real life scenarios, you'd handle exceptions here.
                                    String message = new String(packet).trim();
                                    System.out.println(message);
                                    // Ignore empty lines
                                    if (message.length() == 0) return;
                                }
                                catch (Exception e)
                                {
                                        e.printStackTrace();
                                }
                        }
                    	@Override
                        public void connectionBroken(NIOSocket nioSocket, Exception exception)
                        {
                                System.out.println("Connection failed.");
                                // Exit the program.
                                System.exit(-1);
                        }
                });
                    // Read IO until process exits.
                    while (true)
                    {
                            service.selectNonBlocking();
                    }
            }
            catch (Exception e)
            {
                    e.printStackTrace();
            }
    }

}

