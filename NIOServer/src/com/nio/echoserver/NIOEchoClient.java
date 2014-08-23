package com.nio.echoserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import naga.NIOService;
import naga.NIOSocket;
import naga.SocketObserver;
import naga.packetreader.RegularPacketReader;
import naga.packetwriter.RegularPacketWriter;


public class NIOEchoClient {
	NIOEchoClient()
    {}

    public static void main(String... args)
    {
            try
            {

                    // Prepare the login packet, packing two UTF strings together
                    // using a data output stream.
	            	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	                DataOutputStream dataStream = new DataOutputStream(stream);
	                dataStream.writeUTF("connecting to server");
	                dataStream.flush();
	                final byte[] content = stream.toByteArray();
	                dataStream.close();

                    // Start up the service.
                    NIOService service = new NIOService();

                    // Open our socket.
                    NIOSocket socket = service.openSocket("localhost", 5218);

                    // Use regular 1 byte header reader/writer
                    socket.setPacketReader(new RegularPacketReader(1, true));
                    socket.setPacketWriter(new RegularPacketWriter(1, true));
                    
                    Thread t = new Thread() {

						@Override
						public void run() {
							BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
							try {
								String temp = "";
								while(!temp.equalsIgnoreCase("quit")) {
									ByteArrayOutputStream stream = new ByteArrayOutputStream();
					                DataOutputStream dataStream = new DataOutputStream(stream);
					                temp = reader.readLine();
					                dataStream.writeUTF(temp);
					                final byte[] content = stream.toByteArray();
									socket.write(content);
									dataStream.close();
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
                                socket.write(content);
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
                                        // Read the UTF-reply and print it.
                                        String reply = new DataInputStream(new ByteArrayInputStream(packet)).readUTF();
                                        System.out.println("Reply was: " + reply);
                                        System.exit(0);
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
                            service.selectBlocking();
                    }
            }
            catch (Exception e)
            {
                    e.printStackTrace();
            }
    }

}
